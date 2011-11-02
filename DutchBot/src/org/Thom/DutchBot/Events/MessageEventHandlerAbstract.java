package org.Thom.DutchBot.Events;

import org.Thom.DutchBot.DutchBot;

public abstract class MessageEventHandlerAbstract extends EventHandlerAbstract {

    protected abstract boolean handle(DutchBot bot, String target,
	    String sender, String login, String hostname, String message);

    public boolean run(DutchBot bot, String channel, String sender,
	    String login, String hostname, String message) {
	return handle(bot, channel, sender, login, hostname, message);
    }

    public boolean run(DutchBot bot, String sender, String login,
	    String hostname, String message) {
	return handle(bot, sender, sender, login, hostname, message);
    }

}
