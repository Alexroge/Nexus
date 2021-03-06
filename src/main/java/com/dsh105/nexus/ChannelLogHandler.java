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

package com.dsh105.nexus;

import com.dsh105.nexus.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class ChannelLogHandler extends Handler {

    private static final String[] EXCLUSIONS = new String[]{"Received notice:", "Received PM from"};

    private List<LogRecord> messageQueue = new ArrayList<>();

    private String channelName;

    public ChannelLogHandler(String channelName) {
        this.channelName = channelName;
    }

    @Override
    public void publish(LogRecord record) {
        if (Nexus.getInstance() == null || this.channelName == null || Nexus.getInstance().getChannel(this.channelName) == null) {
            messageQueue.add(record);
            return;
        }
        if (!isLoggable(record)) {
            return;
        }

        ArrayList<LogRecord> queue = new ArrayList<>();
        Collections.addAll(queue, messageQueue.toArray(new LogRecord[0]));
        messageQueue.clear();
        for (LogRecord queuedRecord : queue) {
            publish(queuedRecord);
        }

        String[] parts = record.getMessage().split(" ");
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            parts[i] = StringUtil.mungeMessage(this.channelName, part);
        }
        String message = StringUtil.join(parts, " ");
        for (String exc : EXCLUSIONS) {
            if (message.toLowerCase().contains(exc.toLowerCase())) {
                return;
            }
        }
        Nexus.getInstance().sendIRC().message(this.channelName, message);
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() throws SecurityException {
        channelName = null;
    }
}