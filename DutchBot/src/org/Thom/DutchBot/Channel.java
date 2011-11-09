/**
 * Manages channels
 */
package org.Thom.DutchBot;

import org.jibble.pircbot.User;

/**
 * @author Thom
 * 
 */
public class Channel {

    private final String _channelName;
    private final String _key;
    private final DutchBot _bot;
    private boolean joined = false;

    public Channel(DutchBot bot, String name) {
	this._bot = bot;
	this._key = null;
	this._channelName = name;
    }

    public Channel(DutchBot bot, String name, String key) {
	this._bot = bot;
	this._key = key;
	this._channelName = name;
    }

    public void join() {
	if (_key == null)
	    this._bot.joinChannel(this._channelName);
	else
	    this._bot.joinChannel(_channelName, _key);
    }

    public void hasJoined() {
	this.joined = true;
    }

    public User[] getUsers() {
	return this._bot.getUsers(this._channelName);
    }

    public User getUser(String nick) throws Exception {
	if (!this.joined)
	    throw new Exception("Not joined to channel");
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
