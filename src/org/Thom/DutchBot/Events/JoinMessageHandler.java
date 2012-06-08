package org.Thom.DutchBot.Events;

import org.Thom.DutchBot.AccessList;
import org.Thom.DutchBot.DutchBot;
import org.Thom.DutchBot.Privileges;

public class JoinMessageHandler extends MessageEventHandlerAbstract {

    public boolean handle(DutchBot bot, String target, String sender,
	    String login, String hostname, String message) {
	if (message.startsWith("join ")
		&& AccessList.isAllowed(login, hostname, Privileges.OPERATOR)) {

	    String[] command = message.split("\\s+");

	    if (command[1].startsWith("#") && command.length > 2) {
		bot.joinChannel(command[1], command[2]);
	    } else if (command[1].startsWith("#")) {
		bot.joinChannel(command[1]);
	    } else {
		bot.sendNotice(sender, "Usage: " + bot.getCommandPrefix()
			+ "join #channel <key>");
	    }
	    return true;
	}
	return false;
    }

}
