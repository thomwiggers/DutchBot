/**
 * 
 */
package org.Thom.DutchBot.Events;

import org.Thom.DutchBot.AccessList;
import org.Thom.DutchBot.DutchBot;
import org.Thom.DutchBot.Privileges;

/**
 * @author Thom
 * 
 */
public class QuitMessageHandler extends MessageEventHandler {

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.Thom.DutchBot.Events.MessageEventHandler#run(org.Thom.DutchBot.DutchBot
     * , java.lang.String, java.lang.String, java.lang.String, java.lang.String,
     * java.lang.String)
     */
    @Override
    public boolean run(DutchBot bot, String channel, String sender,
	    String login, String hostname, String message) {
	System.out.println("Reached this point. WHOO");
	if (message.startsWith(bot.getCommandPrefix() + "quit")
		&& AccessList.isAllowed(login, hostname, Privileges.OWNER)) {
	    bot.quitServer("This bot was shot dead by: " + sender);
	    System.out.println("Shutdown invoked by: " + sender + "!" + login
		    + "@" + hostname + " in channel " + channel);
	    bot.dispose();
	    System.exit(0);
	    return true;
	}
	return false;
    }

}
