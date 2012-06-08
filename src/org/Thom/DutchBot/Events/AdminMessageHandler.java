/**
 * 
 */
package org.Thom.DutchBot.Events;

import java.util.Arrays;

import org.Thom.DutchBot.AccessList;
import org.Thom.DutchBot.DutchBot;
import org.Thom.DutchBot.Privileges;

/**
 * @author Thom
 * 
 */
public class AdminMessageHandler extends MessageEventHandlerAbstract {

    /**
     * 
     */
    public AdminMessageHandler() {
	// TODO Auto-generated constructor stub
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.Thom.DutchBot.Events.MessageEventHandlerAbstract#handle(org.Thom.
     * DutchBot.DutchBot, java.lang.String, java.lang.String, java.lang.String,
     * java.lang.String, java.lang.String)
     */
    @Override
    protected boolean handle(DutchBot bot, String target, String sender,
	    String login, String hostname, String message) {

	if (!message.startsWith("admin"))
	    return false;

	if (!AccessList.isAllowed(login, hostname, Privileges.OWNER)) {
	    bot.sendMessage(bot.getLogchannel(), "-- WARNING " + sender
		    + " in channel " + target + " tried admin command "
		    + message);
	    bot.sendNotice(sender, "no.");
	}

	message = message.substring("admin".length()).trim();
	if (message.startsWith("loadmod")) {
	    loadModule(bot, message.substring("loadmod".length()), sender);
	}

	return true;
    }

    protected boolean loadModule(DutchBot bot, String message, String sender) {
	message = message.toLowerCase().trim();

	String[] command = message.split("\\s+", 2);

	if (command.length < 2
		|| Arrays.binarySearch(EventManager.EVENT_TYPES, command[1]) == -1) {
	    bot.sendNotice(sender,
		    "Unknown event type! Usage: " + bot.getCommandPrefix()
			    + "admin loadmod <shortname> type>");
	    return true;
	}

	String result = bot.getEventManager().addEvent(command[0], command[1]);
	if (result == "")
	    bot.sendNotice(sender, "Loaded module " + command[0]);
	else
	    bot.sendNotice(sender, result);

	return true;
    }
}
