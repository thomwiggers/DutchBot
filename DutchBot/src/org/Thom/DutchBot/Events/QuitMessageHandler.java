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
public class QuitMessageHandler extends MessageEventHandlerAbstract {

    protected boolean handle(DutchBot bot, String target, String sender,
	    String login, String hostname, String message) {
	if (message.startsWith("quit")
		&& AccessList.isAllowed(login, hostname, Privileges.OWNER)) {
	    bot.quitServer("This bot was killed by: " + sender);
	    System.out.println("Shutdown invoked by: " + sender + "!" + login
		    + "@" + hostname + " in " + target);
	    System.exit(0);
	    return true;
	}
	return false;
    }
}
