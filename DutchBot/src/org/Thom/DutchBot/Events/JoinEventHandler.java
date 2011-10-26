package org.Thom.DutchBot.Events;

import org.Thom.DutchBot.DutchBot;

public abstract class JoinEventHandler extends Event {
    public abstract boolean run(DutchBot bot, String channel, String nick,
	    String login, String hostname);
}
