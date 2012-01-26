/**
 * 
 */
package org.Thom.DutchBot;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * @author Thom
 * 
 */

public final class AccessList {

    private static HashMap<String, String> _aliasList = new HashMap<String, String>();
    private static HashMap<String, Privileges> _accessList = new HashMap<String, Privileges>();
    private static PropertiesConfiguration config = new PropertiesConfiguration();
    private static DutchBot bot;

    public static void addUser(String login, String hostname, Privileges level) {

	String user = login + "@" + hostname;
	// make sure to update the alias:
	if (_aliasList.containsKey(user)) {
	    user = _aliasList.get(user);
	}
	_accessList.put(user.toLowerCase(), level);
	System.out.println("Registered " + user);
	// update config
	if (config.containsKey("acl." + user))
	    config.clearProperty("acl." + user);
	config.addProperty("acl." + user, level.getValue());
	try {
	    config.save();
	} catch (ConfigurationException e) {
	    bot.logMessage("Failed to write config " + e.getMessage(), true);
	}

    }

    public static void addAlias(String aliasLogin, String aliasHostname,
	    String originalLogin, String originalHostname)
	    throws AccessListException {

	String originalUser = originalLogin + "@" + originalHostname;
	String aliasUser = aliasLogin + "@" + aliasHostname;

	if (!_accessList.containsValue(originalUser))
	    throw new AccessListException(
		    "Can't add an alias to a nick that does not exist!");

	_aliasList.put(aliasUser, originalUser);

	if (config.containsKey("alias." + aliasUser))
	    config.clearProperty("alias." + aliasUser);
	config.addProperty("alias." + aliasUser, originalUser);

    }

    public static void delUser(String login, String hostname) {
	_accessList.remove(login + "@" + hostname);
    }

    public static void loadFromConfig(String configfile)
	    throws ConfigurationException, FileNotFoundException {
	config.setAutoSave(true);
	config.setThrowExceptionOnMissing(true);
	config.setFileName(configfile);
	config.load();

	@SuppressWarnings("rawtypes")
	Iterator keys = config.getKeys("acl");
	while (keys.hasNext()) {
	    String key = keys.next().toString();
	    String host = String.copyValueOf(key.toCharArray(), 4,
		    key.toString().length() - 4).toLowerCase();
	    Privileges axx = Privileges.lookup((config.getInt(key)));
	    _accessList.put(host, axx);
	}

	keys = config.getKeys("alias");
	while (keys.hasNext()) {
	    String key = keys.next().toString();
	    String host = String.copyValueOf(key.toCharArray(), 6, key
		    .toString().length() - 6);
	    _aliasList.put(host, config.getString(key));
	}

    }

    public static boolean isAllowed(String login, String hostname,
	    Privileges minimumAccess) {
	String user = login + "@" + hostname;
	user = user.toLowerCase();
	if (_aliasList.containsKey(user))
	    hostname = _aliasList.get(user);

	Privileges userAccess = Privileges.USER;
	if (_accessList.containsKey(user)) {
	    userAccess = _accessList.get(user);
	}

	if (userAccess.getValue() >= minimumAccess.getValue()) {
	    bot.logMessage("Authorized user " + user);
	    return true;
	}

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
