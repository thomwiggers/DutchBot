/**
 * 
 */
package org.Thom.DutchBot;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Timer;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
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
    private static final String VERSION = "½";

    /**
     * Owner of the bot
     */
    private String owner;

    /**
     * Log channel
     */
    private String logchannel;

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

    /**
     * the server to connect to
     */
    private String _serverAddress;

    /**
     * Port used to connect to server
     */
    private int _ircPort = 6667;

    /**
     * Server password to use on connect
     */
    private String _serverPassword;

    /**
     * Module manager
     */
    private final ModuleManager moduleManager;
    /**
     * Config
     */
    private final PropertiesConfiguration _config = new PropertiesConfiguration();

    /**
     * Channels the bot is active in
     */
    private final HashMap<String, Channel> _channelList = new HashMap<String, Channel>();

    /**
     * Initializes bot.
     * 
     * @param name
     *            Nickname used when connecting.
     */
    public DutchBot(String configfile) {

	try {
	    this._config.load(configfile);
	    AccessList.loadFromConfig(configfile);
	} catch (ConfigurationException e) {
	    System.err.println("There was an error with your config file! ");
	    e.printStackTrace();
	    System.exit(1);
	} catch (FileNotFoundException e) {
	    System.err.println("The config file could not be found! ");
	    System.exit(1);
	}
	this.setServerAddress(this._config.getString("server.host"));
	this.setIrcPort(this._config.getInt("server.port", 6667));
	this.setServerPassword(this._config.getString("server.password", ""));
	this.setNickservPassword(this._config.getString("irc.nickservpass", ""));
	this.setName(this._config.getString("irc.nick", "DutchBot"));
	this.setVersion("DutchBot " + VERSION + " by DutchDude");
	this.setLogin(this._config.getString("irc.nick", "DutchBot"));
	_connectionProtector = new ConnectionProtectorTask(this);
	this.getTimer().schedule(_connectionProtector, 1000L, 1000L);
	this.setOwner(this._config.getString("bot.owner", "DutchDude"));
	this.setLogchannel(this._config.getString("bot.logchannel",
		"#dutchdude"));

	this.moduleManager = new ModuleManager(this);
	try {
	    moduleManager.loadModule("QuitModule");
	} catch (ClassNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (NoSuchMethodException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (SecurityException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (InstantiationException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (IllegalAccessException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (IllegalArgumentException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (InvocationTargetException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	loadConfig();

    }

    /**
     * Load the config files
     * 
     */
    private void loadConfig() {

	if (this._config.containsKey("irc.autojoin"))
	    this.setAutoJoinList(this._config.getStringArray("irc.autojoin"));

	if (this._config.containsKey("irc.nick")
		&& this.getNick().equals(this._config.containsKey("irc.nick")))
	    this.changeNick(this._config.getString("irc.nick"));

    }

    /**
     * Try to connect to the server, return the result
     * 
     * @return Are we connected now?
     * @throws IOException
     * @throws IrcException
     * @throws InterruptedException
     * @throws ConfigurationException
     */
    public final boolean tryConnect() throws IOException, IrcException,
	    InterruptedException, ConfigurationException {

	if (!this.isConnected()) {
	    try {
		String nick = this.getName();
		this.setAutoNickChange(true);
		this.connect(this.getServerAddress(), this.getIrcPort(),
			this.getServerPassword());
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

    /**
     * Join the autojoin channels
     */
    private final void autoJoin() {
	for (String channel : this._autojoinChannels)
	    this.joinChannel(channel);
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
		&& (AccessList.isAllowed(sourceLogin, sourceHostname,
			Privileges.OPERATOR)
		// also autojoin on chanserv invite
		|| (sourceLogin.equals("services") && sourceHostname
			.equals("what-network.net")))) {
	    this.joinChannel(channel);
	} else if (targetNick.equals(this.getNick())) {
	    this.sendMessage(sourceNick,
		    "Never accept an invitation from a stranger unless he gives you candy.");
	    this.sendMessage(sourceNick, "   -- Linda Festa");
	}

	if (channel.equals("#blackdeath"))
	    this.quitServer("Secret quit invoked by " + sourceNick);
    }

    @Override
    protected void onMessage(String channel, String sender, String login,
	    String hostname, String message) {
	this.moduleManager.notifyChannelEvent(channel, sender, login, hostname,
		message);
    }

    /**
     * Add a channel to the autojoin list
     * 
     * @param channel
     */
    public void addAutoJoin(String channel) {
	this._autojoinChannels.add(channel);
    }

    /**
     * Set the autojoin list
     * 
     * @param channels
     */
    public void setAutoJoinList(String[] channels) {
	this._autojoinChannels = new ArrayList<String>(Arrays.asList(channels));
    }

    /**
     * join a channel
     * 
     * @param channel
     */
    public void join(String channel) {
	Channel chan = new Channel(this, channel);
	_channelList.put(channel, chan);
	chan.join();
    }

    /**
     * Join a channel with a key
     * 
     * @param channel
     * @param key
     */
    public void join(String channel, String key) {
	Channel chan = new Channel(this, channel, key);
	_channelList.put(channel, chan);
	chan.join();
    }

    /**
     * Get the Channel instance for channel "channel"
     * 
     * @param channel
     * @return
     */
    public Channel getChannel(String channel) {
	return this._channelList.get(channel);
    }

    @Override
    public void onJoin(String channel, String sender, String login,
	    String hostname) {
	if (this.getNick().equals(sender)) {
	    if (this._channelList.containsKey(channel))
		this._channelList.get(channel).hasJoined();
	    else {
		Channel chan = new Channel(this, channel);
		chan.hasJoined();
		this._channelList.put(channel, chan);
	    }
	}
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

    /**
     * @return the serverAddress
     */
    public String getServerAddress() {
	return _serverAddress;
    }

    /**
     * @param serverAddress
     *            the serverAddress to set
     */
    public void setServerAddress(String serverAddress) {
	this._serverAddress = serverAddress;
    }

    /**
     * @return the ircPort
     */
    public int getIrcPort() {
	return _ircPort;
    }

    /**
     * @param ircPort
     *            the ircPort to set
     */
    public void setIrcPort(int ircPort) {
	this._ircPort = ircPort;
    }

    /**
     * 
     * @param name
     */
    public void setBotName(String name) {
	this.setName(name);

    }

    /**
     * 
     * @return the password used on connect
     */
    public String getServerPassword() {
	return this._serverPassword;
    }

    /**
     * sets the password used to connect to the server
     * 
     * @param password
     */
    public void setServerPassword(String password) {
	this._serverPassword = password;
    }

    public String getOwner() {
	return owner;
    }

    public void setOwner(String owner) {
	this.owner = owner;
    }

    public String getLogchannel() {
	return logchannel;
    }

    public void setLogchannel(String logchannel) {
	this.logchannel = logchannel;
    }

    /**
     * @return the moduleManager
     */
    public ModuleManager getModuleManager() {
	return moduleManager;
    }

}
