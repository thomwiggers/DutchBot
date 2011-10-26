package org.Thom.DutchBot.Events;

import org.Thom.DutchBot.DutchBot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class EventManager {

    public static final String[] EVENT_TYPES = { "join", "message" };

    private final DutchBot bot;

    private final ArrayList<MessageEventHandler> messageEvents = new ArrayList<MessageEventHandler>();
    private final ArrayList<JoinEventHandler> joinEvents = new ArrayList<JoinEventHandler>();

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

    public void addEvents(HashMap<String, String[]> eventHandlers)
	    throws InstantiationException, IllegalAccessException {
	String currentClass = null;
	try {
	    for (String eventType : EventManager.EVENT_TYPES) {
		ArrayList<String> classes = new ArrayList<String>();
		if (eventHandlers.containsKey(eventType))
		    classes.addAll(Arrays.asList(eventHandlers.get(eventType)));
		for (String handler : classes) {
		    currentClass = handler;
		    this.addEvent(handler, eventType);
		}

		// clear again
		classes = new ArrayList<String>();
	    }

	} catch (ClassNotFoundException e) {
	    System.err.println("Couldn't find Class " + currentClass);
	}

    }

    public void addEvent(String name, String type)
	    throws ClassNotFoundException, InstantiationException,
	    IllegalAccessException {

	name = name.substring(0, 1).toUpperCase()
		.concat(name.substring(1).toLowerCase());
	type = type.substring(0, 1).toUpperCase()
		.concat(type.substring(1).toLowerCase());
	System.out.println("org.Thom.DutchBot.Events." + name + type
		+ "Handler");
	Class<?> o = Class.forName("org.Thom.DutchBot.Events." + name + type
		+ "Handler");
	if (type.equalsIgnoreCase("message"))
	    messageEvents.add((MessageEventHandler) o.newInstance());
	else if (type.equalsIgnoreCase("join"))
	    joinEvents.add((JoinEventHandler) o.newInstance());

    }
}
