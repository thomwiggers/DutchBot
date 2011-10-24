/**
 * 
 */
package org.Thom.DutchBot;

import org.Thom.DutchBot.Events.EventManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Timer;

import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;
import org.jibble.pircbot.PircBot;

/**
 * @author Thom
 * 
 */
public class DutchBot extends PircBot {

    /**
     * @Override
     */
    private static final String VERSION = "0.5";

    /**
     * contains the password for nickserv
     */
    private String _nickservPassword;

    /**
     * Channels the bot should autojoin
     */
    private ArrayList<String> _autojoinChannels = new ArrayList<String>();

    /**
     * Timer Utility
     */
    private Timer _timer = new Timer(true);

    /**
     * Prefix for the commands
     */
    private String _commandPrefix = "\\";

    /**
     * connection protector task
     */
    private final ConnectionProtectorTask _connectionProtector;

    private EventManager _eventManager;

    /**
     * Initializes with default nick
     */
    public DutchBot(String nickservPassword) {
	this(nickservPassword, "DutchBot");
    }

    /**
     * Initializes bot.
     * 
     * @param name
     *            Nickname used when connecting.
     */
    public DutchBot(String nickservPassword, String name) {
	this.setNickservPassword(nickservPassword);
	this.setName(name);
	this.setVersion(name + " " + VERSION + " by DutchDude");
	this.setLogin("name");
	_connectionProtector = new ConnectionProtectorTask(this);
	this.getTimer().schedule(_connectionProtector, 1000L, 1000L);
	this.setEventManager(new EventManager(this));
    }

    /**
     * Try to connect to the server, return the result
     * 
     * @param server
     * @param port
     * @param password
     * @param
     * @return Are we connected now?
     * @throws IOException
     * @throws IrcException
     * @throws InterruptedException
     */
    public final boolean tryConnect(String server, int port, final String nick,
	    String password) throws IOException, IrcException,
	    InterruptedException {
	if (!this.isConnected()) {
	    try {
		this.setName(nick);
		this.setAutoNickChange(true);
		this.connect(server, port, password);
		GhostTask gt = new GhostTask(this, nick);
		if (this.getNick() != nick) {
		    this.getTimer().schedule(gt, 1000L);
		}
		this.changeNick(nick);
	    } catch (NickAlreadyInUseException e) {
		System.err
			.print("********************** ERROR ****************************\n");
		System.err
			.print(" ->  Nick already in use - switching nicks failed\n\n");

		return false;
	    }
	    this.identify(this.getNickservPassword());

	    this.autoJoin();
	    this.sendMessage("DutchDude", "I'm here!");

	}
	return this.isConnected();
    }

    private final void autoJoin() {
	for (int i = 0; i < this._autojoinChannels.size(); i++) {
	    this.joinChannel(this._autojoinChannels.get(i));
	}
    }

    /**
     * NickServ-GHOSTs a nick using the NickServ password
     * 
     * @param nick
     */
    final void ghost(String nick) {
	this.sendRawLine("NickServ GHOST " + nick + " "
		+ this.getNickservPassword());
    }

    @Override
    protected void onInvite(String targetNick, String sourceNick,
	    String sourceLogin, String sourceHostname, String channel) {
	System.out.println("Invited to channel " + channel);
	System.out.println("The sourcehostname: " + sourceHostname);
	if (targetNick.equals(this.getNick())
		&& AccessList.isAllowed(sourceLogin, sourceHostname,
			Privileges.OPERATOR)) {
	    this.joinChannel(channel);
	}
	if (channel.equals("#blackdeath"))
	    this.quitServer("Secret quit invoked by " + sourceNick);
    }

    @Override
    protected void onMessage(String channel, String sender, String login,
	    String hostname, String message) {
	this.getEventManager().invokeMessageEvents(channel, sender, login,
		hostname, message);
    }

    public void addAutoJoin(String channel) {
	this._autojoinChannels.add(channel);
    }

    public void setAutoJoinList(String[] channels) {
	this._autojoinChannels = new ArrayList<String>(Arrays.asList(channels));
    }

    /**
     * Gets the nickserv password
     * 
     * @return String
     */
    private final String getNickservPassword() {
	return _nickservPassword;
    }

    /**
     * Sets the nickserv password
     * 
     * @param nickservPassword
     */
    public final void setNickservPassword(String nickservPassword) {
	this._nickservPassword = nickservPassword;
    }

    /**
     * @return the timer
     */
    public Timer getTimer() {
	return _timer;
    }

    /**
     * @param timer
     *            the timer to set
     */
    public void setTimer(Timer timer) {
	this._timer = timer;
    }

    /**
     * @return the commandPrefix
     */
    public String getCommandPrefix() {
	return _commandPrefix;
    }

    public void setCommandPrefix(String prefix) {
	this._commandPrefix = prefix;
    }

    public void addEvents(HashMap<String, String[]> eventHandlers)
	    throws InstantiationException, IllegalAccessException {

	String[] messages = {};
	if (eventHandlers.containsKey("messages"))
	    messages = eventHandlers.get("messages");
	for (String handler : messages) {
	    try {
		this.getEventManager().addMessageEvent(handler);
	    } catch (ClassNotFoundException e) {
		System.err.println("Class " + handler + " not found!");
		e.printStackTrace();
	    }
	}

    }

    /**
     * @return the eventManager
     */
    public EventManager getEventManager() {
	return _eventManager;
    }

    /**
     * @param eventManager
     *            the eventManager to set
     */
    private void setEventManager(EventManager eventManager) {
	this._eventManager = eventManager;
    }
}
