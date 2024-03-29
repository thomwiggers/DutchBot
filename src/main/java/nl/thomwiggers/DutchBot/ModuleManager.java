package nl.thomwiggers.DutchBot;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import nl.thomwiggers.DutchBot.Events.IChannelJoinEvent;
import nl.thomwiggers.DutchBot.Events.IChannelKickEvent;
import nl.thomwiggers.DutchBot.Events.IChannelMessageEvent;
import nl.thomwiggers.DutchBot.Events.IInviteEvent;
import nl.thomwiggers.DutchBot.Events.IPartEvent;
import nl.thomwiggers.DutchBot.Events.IPrivateMessageEvent;
import nl.thomwiggers.DutchBot.Events.IQuitEvent;

public final class ModuleManager {

	/**
	 * DutchBot instance
	 */
	private final DutchBot bot;

	/**
	 * List with modules
	 */
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
				.forName("nl.thomwiggers.DutchBot.Modules." + module);
		Class<?>[] args = new Class[1];
		args[0] = DutchBot.class;
		ModuleAbstract m = o.getConstructor(args).newInstance(bot);
		m.init();
		this.moduleList.add(m);
	}

	/**
	 * Notify the loaded modules implementing IChannelMessageEvent of an event
	 * 
	 * @param channel
	 * @param sender
	 * @param login
	 * @param hostname
	 * @param message
	 */
	public void notifyChannelMessageEvent(String channel, String sender,
			String login, String hostname, String message) {
		for (ModuleAbstract m : this.moduleList) {
			if (m instanceof IChannelMessageEvent)
				((IChannelMessageEvent) m).notifyChannelMessageEvent(channel,
						sender, login, hostname, message);
		}
	}

	/**
	 * Notify the loaded modules implementing IChannelJoinEvent of an event
	 * 
	 * @param channel
	 * @param sender
	 * @param login
	 * @param hostname
	 */
	public void notifyChannelJoinEvent(String channel, String sender,
			String login, String hostname) {
		for (ModuleAbstract m : this.moduleList) {
			if (m instanceof IChannelJoinEvent)
				((IChannelJoinEvent) m).notifyChannelJoinEvent(channel, sender,
						login, hostname);
		}

	}

	/**
	 * Notify the loaded modules implementing IPrivateMessageEvent of an event
	 * 
	 * @param sender
	 * @param login
	 * @param hostname
	 * @param message
	 */
	public void notifyPrivateMessageEvent(String sender, String login,
			String hostname, String message) {
		this.bot.logMessage("Triggered PrivateMessageEvent from " + sender
				+ ": " + message);
		for (ModuleAbstract m : this.moduleList) {
			if (m instanceof IPrivateMessageEvent)
				((IPrivateMessageEvent) m).notifyPrivateMessageEvent(sender,
						login, hostname, message);
		}

	}

	/**
	 * Notify the loaded modules implementing IInviteEvent of an invite
	 * 
	 * @param targetNick
	 * @param sourceNick
	 * @param sourceLogin
	 * @param sourceHostname
	 * @param channel
	 */
	public void notifyInviteEvent(String targetNick, String sourceNick,
			String sourceLogin, String sourceHostname, String channel) {
		for (ModuleAbstract m : this.moduleList) {
			if (m instanceof IInviteEvent)
				((IInviteEvent) m).notifyInviteEvent(targetNick, sourceNick,
						sourceLogin, sourceHostname, channel);
		}

	}

	/**
	 * Notify the loaded modules implementing IChannelKickEvent of an event
	 * 
	 * @param channel
	 * @param kickerNick
	 * @param kickerLogin
	 * @param kickerHostname
	 * @param recipientNick
	 * @param reason
	 */
	public void notifyChannelKickEvent(String channel, String kickerNick,
			String kickerLogin, String kickerHostname, String recipientNick,
			String reason) {
		for (ModuleAbstract m : this.moduleList) {
			if (m instanceof IChannelKickEvent)
				((IChannelKickEvent) m).notifyChannelKickEvent(channel,
						kickerNick, kickerLogin, kickerHostname, recipientNick,
						reason);
		}

	}

	/**
	 * @param sourceNick
	 * @param sourceLogin
	 * @param sourceHostname
	 * @param reason
	 */
	public void notifyQuitEvent(String sourceNick, String sourceLogin,
			String sourceHostname, String reason) {
		for (ModuleAbstract m : this.moduleList) {
			if (m instanceof IQuitEvent)
				((IQuitEvent) m).notifyQuitEvent(sourceNick, sourceLogin,
						sourceHostname, reason);
		}

	}

	/**
	 * @param channel
	 * @param sender
	 * @param login
	 * @param hostname
	 */
	public void notifyPartEvent(String channel, String sender, String login,
			String hostname) {
		for (ModuleAbstract m : this.moduleList) {
			if (m instanceof IPartEvent)
				((IPartEvent) m).notifyPartEvent(channel, sender, login,
						hostname);
		}
	}
}
