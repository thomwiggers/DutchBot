package org.Thom.DutchBot.Events;

import org.Thom.DutchBot.DutchBot;

public abstract class JoinEventHandlerAbstract extends EventHandlerAbstract {
    public abstract boolean run(DutchBot bot, String channel, String nick,
	    String login, String hostname);
}