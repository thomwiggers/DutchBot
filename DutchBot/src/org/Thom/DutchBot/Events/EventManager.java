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
	if (message.startsWith(bot.getCommandPrefix())) {
	    for (MessageEventHandler event : messageEvents) {
		boolean result = event.run(bot, channel, sender, login,
			hostname, message.substring(1));
		if (result)
		    break;
	    }
	}
    }

    public void addMessageEvent(String name) throws ClassNotFoundException,
	    InstantiationException, IllegalAccessException {

	name = name.substring(0, 1).toUpperCase()
		.concat(name.substring(2).toLowerCase());

	Class<?> o = Class.forName("org.Thom.DutchBot.Events." + name
		+ "MessageHandler");
	MessageEventHandler event = (MessageEventHandler) o.newInstance();
	messageEvents.add(event);

    }
}
