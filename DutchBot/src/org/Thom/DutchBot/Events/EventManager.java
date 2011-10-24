package org.Thom.DutchBot.Events;

import org.Thom.DutchBot.DutchBot;

import java.util.ArrayList;

public class EventManager {

    private final DutchBot bot;

    private final ArrayList<MessageEventHandler> messageEvents = new ArrayList<MessageEventHandler>();

    public EventManager(DutchBot bot) {
	this.bot = bot;
    }

    public void invokeMessageEvents(String channel, String sender,
	    String login, String hostname, String message) {
	for (MessageEventHandler event : messageEvents) {
	    boolean result = event.run(bot, channel, sender, login, hostname,
		    message);
	    if (result)
		break;
	}
    }

    public void addMessageEvent(String name) throws ClassNotFoundException,
	    InstantiationException, IllegalAccessException {
	Class<?> o = Class.forName("org.Thom.DutchBot.Events." + name);
	MessageEventHandler event = (MessageEventHandler) o.newInstance();
	messageEvents.add(event);

    }
}
