/**
 * 
 */
package nl.thomwiggers.DutchBot;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * Class that manages the accesslist
 * 
 * @author Thom
 * 
 */
public final class AccessList {
	/**
	 * List with aliases
	 */
	private static HashMap<String, String> aliasList = new HashMap<String, String>();
	/**
	 * List with hostmasks and their privilege levels
	 */
	private static HashMap<String, Privileges> accessList = new HashMap<String, Privileges>();

	private static HashMap<String, Privileges> channelAccessList = new HashMap<String, Privileges>();
	/**
	 * Config object
	 */
	private static PropertiesConfiguration config = new PropertiesConfiguration();
	/**
	 * The active DutchBot instance
	 */
	private static DutchBot bot;

	/**
	 * Add a user to the access list
	 * 
	 * @param login
	 * @param hostname
	 * @param level
	 */
	public static void addUser(String login, String hostname, Privileges level) {

		String user = login + "@" + hostname;
		// make sure to update the alias:
		if (aliasList.containsKey(user)) {
			user = aliasList.get(user);
		}
		accessList.put(user.toLowerCase(), level);
		System.out.println("Registered " + user);
		// update config
		if (config.containsKey("acl.user." + user))
			config.clearProperty("acl.user." + user);
		config.addProperty("acl.user." + user, level.getValue());
		try {
			config.save();
		} catch (ConfigurationException e) {
			bot.logMessage("Failed to write config " + e.getMessage(), true);
		}

	}

	/**
	 * Add an alias to the alias list
	 * 
	 * @param aliasLogin
	 * @param aliasHostname
	 * @param originalLogin
	 * @param originalHostname
	 * @throws AccessListException
	 */
	public static void addAlias(String aliasLogin, String aliasHostname,
			String originalLogin, String originalHostname)
			throws AccessListException {

		String originalUser = originalLogin + "@" + originalHostname;
		String aliasUser = aliasLogin + "@" + aliasHostname;

		if (!accessList.containsKey(originalUser))
			throw new AccessListException(
					"Can't add an alias to a nick that does not exist!");

		aliasList.put(aliasUser, originalUser);

		if (config.containsKey("alias." + aliasUser))
			config.clearProperty("alias." + aliasUser);
		config.addProperty("alias." + aliasUser, originalUser);

	}

	/**
	 * Delete a user from the access list
	 * 
	 * @param login
	 * @param hostname
	 * @throws AccessListException
	 */
	public static void delUser(String login, String hostname)
			throws AccessListException {
		accessList.remove(login + "@" + hostname);
		config.clearProperty(login + "@" + hostname);
		try {
			config.save();
		} catch (ConfigurationException e) {
			throw new AccessListException(e.getMessage());
		}
	}

	/**
	 * Load the configuration
	 * 
	 * @param configfile
	 * @throws ConfigurationException
	 * @throws FileNotFoundException
	 */
	public static void loadFromConfig(String configfile)
			throws ConfigurationException, FileNotFoundException {
		config.setAutoSave(true);
		config.setThrowExceptionOnMissing(true);
		config.setFileName(configfile);
		config.load();

		@SuppressWarnings("rawtypes")
		Iterator keys = config.getKeys("acl.user");
		while (keys.hasNext()) {
			String key = keys.next().toString();
			String host = key.substring("acl.user.".length());
			Privileges axx = Privileges.lookup((config.getInt(key)));
			accessList.put(host, axx);
		}
		keys = config.getKeys("acl.channel");
		while (keys.hasNext()) {
			String key = keys.next().toString();
			String channel = "#" + key.substring("acl.channel.".length());
			Privileges axx = Privileges.lookup(config.getInt(key));
			channelAccessList.put(channel, axx);
		}

		keys = config.getKeys("alias");
		while (keys.hasNext()) {
			String key = keys.next().toString();
			String host = String.copyValueOf(key.toCharArray(), 6, key
					.toString().length() - 6);
			aliasList.put(host, config.getString(key));
		}

	}

	/**
	 * Validate if a user has the minimumAccess Level
	 * 
	 * @param login
	 * @param hostname
	 * @param minimumAccess
	 * @return returns if is the user allowed the access level
	 */
	public static boolean isAllowed(String login, String hostname,
			Privileges minimumAccess) {
		String user = login + "@" + hostname;
		user = user.toLowerCase();
		if (aliasList.containsKey(user))
			user = aliasList.get(user);

		Privileges userAccess = Privileges.USER;
		if (accessList.containsKey(user)) {
			userAccess = accessList.get(user);
		}

		if (userAccess.getValue() >= minimumAccess.getValue()) {
			bot.logMessage("Authorized user " + user);
			return true;
		}

		return false;
	}

	public static boolean isChannelAllowed(String channel,
			Privileges minimumAccess) {
		Privileges defaultAccess = Privileges.USER;

		if (channelAccessList.containsKey(channel))
			defaultAccess = channelAccessList.get(channel);

		if (defaultAccess.getValue() >= minimumAccess.getValue())
			return true;

		return false;
	}

	public static boolean isKnown(String login, String host) {
		String user = login + "@" + host;
		if (accessList.containsKey(user))
			return true;
		else
			return false;
	}

	/**
	 * @param bot
	 *            the bot to set
	 */
	public static void setBot(DutchBot bot) {
		AccessList.bot = bot;
	}
}
