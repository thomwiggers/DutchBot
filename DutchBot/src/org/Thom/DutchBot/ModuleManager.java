package org.Thom.DutchBot;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.Thom.DutchBot.Modules.IChannelMessageEvent;
import org.Thom.DutchBot.Modules.ModuleAbstract;

public final class ModuleManager {

    /**
     * DutchBot instance
     */
    private final DutchBot bot;

    private final ArrayList<ModuleAbstract> moduleList = new ArrayList<ModuleAbstract>();

    /**
     * 
     * @param bot
     */
    public ModuleManager(DutchBot bot) {
	this.bot = bot;
    }

    /**
     * Attempts to load a certain module
     * 
     * @param Modulename
     *            , must be correct Class name
     * 
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * 
     */
    public void loadModule(String module) throws ClassNotFoundException,
	    NoSuchMethodException, SecurityException, InstantiationException,
	    IllegalAccessException, IllegalArgumentException,
	    InvocationTargetException {
	@SuppressWarnings("unchecked")
	Class<ModuleAbstract> o = (Class<ModuleAbstract>) Class
		.forName("org.Thom.DutchBot.Modules." + module);
	Class<?>[] args = new Class[1];
	args[0] = DutchBot.class;
	ModuleAbstract m = o.getConstructor(args).newInstance(bot);
	m.init();
	this.moduleList.add(m);
    }

    public void notifyChannelMessageEvent(String channel, String sender,
	    String login, String hostname, String message) {
	for (ModuleAbstract m : this.moduleList) {
	    System.out.println(m.getClass());
	    if (m instanceof IChannelMessageEvent)
		((IChannelMessageEvent) m).notifyChannelMessageEvent(channel,
			sender, login, hostname, message);
	}
    }
}