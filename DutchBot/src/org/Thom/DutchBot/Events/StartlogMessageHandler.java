package org.Thom.DutchBot.Events;

import org.Thom.DutchBot.AccessList;
import org.Thom.DutchBot.DutchBot;
import org.Thom.DutchBot.Privileges;

public class StartlogMessageHandler extends MessageEventHandlerAbstract {

    @Override
    protected boolean handle(DutchBot bot, String target, String sender,
	    String login, String hostname, String message) {
	if (!AccessList.isAllowed(login, hostname, Privileges.OPERATOR))
	    return false;

	if (message.toLowerCase().startsWith("startlog"))
	    bot.getChannel(target).startLog(sender);

	if (message.toLowerCase().startsWith("endlog"))
	    bot.getChannel(target).closeLog();

	return false;
    }

    @Override
    public boolean run(DutchBot bot, String sender, String login,
	    String hostname, String message) {
	// not for PMs
	return false;
    }
}
