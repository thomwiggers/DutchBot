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
import java.util.Iterator;
import java.util.List;
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
    private static final String VERSION = "�";

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

	// initialize the finals
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
	// this.addAutoJoin("#dutch");
	this.moduleManager = new ModuleManager(this);

    }

    /**
     * Load the config files
     * 
     */
    private void loadConfig() {

	loadChannels();

	if (this._config.containsKey("irc.nick")
		&& !this.getNick().equals(
			this._config.getString("irc.nick", "")))
	    this.changeNick(this._config.getString("irc.nick"));

    }

    private void loadChannels() {
	// join all the channels configured
	@SuppressWarnings("unchecked")
	Iterator<String> channels = this._config.getKeys("irc.channel");
	while (channels.hasNext()) {

	    String channelname = channels.next().toLowerCase();

	    // channelname uit de keys halen
	    int end = channelname.lastIndexOf(".");
	    if (end == -1 || end < "irc.channel.".length())
		end = channelname.length();
	    channelname = channelname.substring("irc.channel.".length(), end);

	    // als we hem al hebben, skippen
	    if (this._channelList.containsKey("#" + channelname))
		continue;

	    // andere keys ophalen voor nieuwe Channel instance
	    String key = _config.getString("irc.channel." + channelname
		    + ".key", null);
	    Boolean channelservInvite = _config.getBoolean("irc.channel."
		    + channelname + ".chanservinvite", false);
	    List<?> modules = null;
	    if (_config.containsKey("irc.channel." + channelname + ".modules")) {
		modules = _config.getList("irc.channel." + channelname
			+ ".modules");
	    }

	    channelname = "#" + channelname;
	    System.out.println("Channelname: " + channelname);
	    Channel chan = new Channel(this, channelname, key,
		    channelservInvite);

	    // add the modules for this channel
	    for (Object mod : modules) {
		String name = (String) mod;
		name = name.substring(0, 1).toUpperCase()
			.concat(name.substring(1).toLowerCase())
			.concat("Module");
		try {
		    chan.loadModule(name);
		} catch (ClassNotFoundException | NoSuchMethodException
			| SecurityException | InstantiationException
			| IllegalAccessException | IllegalArgumentException
			| InvocationTargetException e) {
		    e.printStackTrace();
		    System.err.println("Messed up while loading module " + name
			    + " for channel " + channelname);
		}
	    }

	    this.join(chan);
	}
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
	    // this.sendMessage("DutchDude", "I'm here!");

	}
	loadConfig();
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

	this.moduleManager.notifyChannelMessageEvent(channel, sender, login,
		hostname, message);
	this.getChannel(channel).notifyChannelMessageEvent(channel, sender,
		login, hostname, message);
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
     * Join a channel
     * 
     * @param channel
     */
    public void join(Channel channel) {
	_channelList.put(channel.toString().toLowerCase(), channel);
	channel.join();
    }

    /**
     * join a channel
     * 
     * @param channel
     */
    public void join(String channel) {
	channel = channel.toLowerCase();
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
	channel = channel.toLowerCase();
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
	channel = channel.toLowerCase();
	return this._channelList.get(channel);
    }

    @Override
    public void onJoin(String channel, String sender, String login,
	    String hostname) {
	channel = channel.toLowerCase();
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
