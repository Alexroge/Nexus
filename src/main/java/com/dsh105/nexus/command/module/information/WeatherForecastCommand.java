/*
 * This file is part of Nexus.
 *
 * Nexus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Nexus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Nexus.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.dsh105.nexus.command.module.information;

import com.dsh105.nexus.command.Command;
import com.dsh105.nexus.command.CommandModule;
import com.dsh105.nexus.command.CommandPerformEvent;
import com.dsh105.nexus.exception.general.WeatherLookupException;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import org.json.JSONArray;
import org.pircbotx.Colors;

import java.text.SimpleDateFormat;
import java.util.Date;


@Command(command = "forecast",
        aliases = {"wf", "fc"},
        needsChannel = false,
        help = "Get a location's weather forecast",
        extendedHelp = {
                "{b}{p}{c} <location>{/b} - Allows you to get the weather forecast of a location"
        })
public class WeatherForecastCommand extends CommandModule {

    public static final String WEATHER_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?mode=json&q=";

    public static double kToC(double k) {
        k = k - 273.15;
        return k;
    }

    @Override
    public boolean onCommand(CommandPerformEvent event) {
        if (event.getArgs().length >= 1) {
            StringBuilder b = new StringBuilder();
            for (int i = 0; i < event.getArgs().length; i++) {
                if (i != 0) {
                    b.append(" ");
                }
                b.append(event.getArgs()[i]);
            }
            try {
                HttpResponse<JsonNode> jsonResponse = Unirest.get(WEATHER_URL + b.toString())
                        .header("accept", "application/json")
                        .asJson();
                String message = jsonResponse.getBody().getObject().getString("cod");
                if (message.equals(404)) {
                    event.errorWithPing("Location not found");
                } else {
                    long timestamp;
                    double d;
                    Date date;
                    String colour;
                    String response = "";
                    JSONArray days = jsonResponse.getBody().getObject().getJSONArray("list");
                    for (int i = 0; i < days.length(); i++) {
                        d = kToC(days.getJSONObject(i).getJSONObject("temp").getDouble("day"));
                        timestamp = days.getJSONObject(i).getLong("dt");
                        timestamp = timestamp * 1000;
                        date = new Date(timestamp);
                        d = (double) Math.round(d * 10) / 10;
                        SimpleDateFormat ft = new SimpleDateFormat("dd/MM/yy (EEE)");
                        if (d < 20) {
                            colour = Colors.BLUE;
                        } else if (d >= 20 && d < 30) {
                            colour = Colors.OLIVE;
                        } else {
                            colour = Colors.RED;
                        }
                        response = response + " " + ft.format(date) + " " + Colors.BOLD + colour + d + "°C" + Colors.NORMAL + " ";
                    }
                    String name = jsonResponse.getBody().getObject().getJSONObject("city").getString("name");
                    String location;
                    if (!name.equals("")) {
                        location = name + ", " + jsonResponse.getBody().getObject().getJSONObject("city").getString("country");
                    } else {
                        location = jsonResponse.getBody().getObject().getJSONObject("city").getString("country");
                    }
                    response = response.trim();
                    event.respond("7 Day Weather Forecast: " + Colors.BOLD + location + ":");
                    event.respond(response);
                }
            } catch (Exception e) {
                throw new WeatherLookupException("An error occurred in the lookup process", e);
            }
        } else {
            return false;
        }
        return true;
    }
}
