package org.Thom.DutchBot.Events;

import org.Thom.DutchBot.DutchBot;

public abstract class MessageEventHandler extends Event {

    public abstract boolean run(DutchBot bot, String channel, String sender,
	    String login, String hostname, String message);

}
