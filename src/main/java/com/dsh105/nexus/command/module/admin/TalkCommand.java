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

package com.dsh105.nexus.command.module.admin;

import com.dsh105.nexus.Nexus;
import com.dsh105.nexus.command.Command;
import com.dsh105.nexus.command.CommandGroup;
import com.dsh105.nexus.command.CommandModule;
import com.dsh105.nexus.command.CommandPerformEvent;
import com.dsh105.nexus.util.StringUtil;

@Command(command = "talk",
        needsChannel = false,
        groups = CommandGroup.ADMIN,
        help = "Make Nexus talk",
        extendedHelp = {
                "{b}{p}{c} <chan> <msg>{/b} - Send a message to a channel on behalf of Nexus. Only admins may use this command."
        })
public class TalkCommand extends CommandModule {

    @Override
    public boolean onCommand(CommandPerformEvent event) {
        if (event.getArgs().length < 2) {
            return false;
        } else {
            String message = StringUtil.combineSplit(1, event.getArgs(), " ");
            Nexus.getInstance().sendIRC().message(event.getArgs()[0], "(" + event.getSender().getNick() + ") " + message);
            return true;
        }
    }

    @Override
    public boolean adminOnly() {
        return true;
    }
}