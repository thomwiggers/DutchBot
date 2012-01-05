/**
 * Manages channels
 */
package org.Thom.DutchBot;

import java.lang.reflect.InvocationTargetException;

import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.User;

/**
 * @author Thom
 * 
 */
public class Channel {

    private final String _channelName;
    private final String _key;
    private final boolean _chanservInvite;
    private final DutchBot _bot;

    /**
     * Module manager:
     */
    private final ModuleManager modulemanager;

    /**
     * Delegate to modulemanager
     * 
     * @param channel
     * @param sender
     * @param login
     * @param hostname
     * @param message
     */
    public void notifyChannelMessageEvent(String channel, String sender,
	    String login, String hostname, String message) {
	this.modulemanager.notifyChannelMessageEvent(channel, sender, login,
		hostname, message);
    }

    /**
     * Delegate to modulemanager
     * 
     * @param module
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    public void loadModule(String module) throws ClassNotFoundException,
	    NoSuchMethodException, SecurityException, InstantiationException,
	    IllegalAccessException, IllegalArgumentException,
	    InvocationTargetException {
	modulemanager.loadModule(module);
    }

    private boolean joined = false;

    public Channel(DutchBot bot, String name) {
	this._bot = bot;
	modulemanager = new ModuleManager(bot);
	this._key = "";
	this._channelName = name;
	this._chanservInvite = false;
    }

    public Channel(DutchBot bot, String name, String key) {
	this._bot = bot;
	modulemanager = new ModuleManager(bot);
	this._key = key;
	this._channelName = name;
	this._chanservInvite = false;
    }

    public Channel(DutchBot bot, String name, String key, boolean chanservInvite) {
	modulemanager = new ModuleManager(bot);
	this._bot = bot;
	this._key = key;
	this._channelName = name;
	this._chanservInvite = chanservInvite;
    }

    public void join() {

	if (_key != null) {
	    if (!_key.trim().isEmpty()) {
		this._bot.joinChannel(this._channelName, _key);
	    }
	}
	if (_chanservInvite) {
	    System.out.println("Trying to join " + _channelName);
	    this._bot.sendRawLine("CS invite " + this._channelName);
	    this._bot.joinChannel(_channelName);
	} else
	    this._bot.joinChannel(_channelName);

    }

    public void processMessage(String sender, String login, String host,
	    String message) {

    }

    public void hasJoined() {
	this.joined = true;
    }

    public User[] getUsers() {
	return this._bot.getUsers(this._channelName);
    }

    public User getUser(String nick) throws IrcException {
	if (!this.joined)
	    throw new IrcException("Not joined to channel");
	for (User user : this.getUsers()) {
	    System.out.println(user.getNick());
	    if (user.getNick().equalsIgnoreCase(nick))
		return user;
	}
	return null;
    }

    @Override
    public String toString() {
	return this._channelName;
    }

}
