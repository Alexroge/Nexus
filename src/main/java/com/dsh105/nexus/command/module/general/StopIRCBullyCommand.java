package com.dsh105.nexus.command.module.general;

import com.dsh105.nexus.command.Command;
import com.dsh105.nexus.command.CommandModule;
import com.dsh105.nexus.command.CommandPerformEvent;

@Command(command = "sib",
        aliases = {"stopb"},
        needsChannel = false,
        help = "Stops IRC Bullying",
        extendedHelp = {
                "{b}{p}{c} <target>{/b} - Tells the target to stop bullying you."
        })
public class StopIRCBullyCommand extends CommandModule {

    @Override
    public boolean onCommand(CommandPerformEvent event) {
        if (event.getArgs().length == 1) {
            event.respond(event.getArgs()[0] + ": " + event.getSender().getNick() + " feels offended by your recent action(s). Please read http://stop-irc-bullying.eu/stop");
            return true;
        } else {
            return false;
        }
    }
}
