/**
 * 
 */
package org.Thom.DutchBot;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.configuration.*;

/**
 * @author Thom
 *
 */

public final class AccessList {
	
	private static HashMap<String, String> _aliasList = new HashMap<String, String>();
	private static HashMap<String, Privileges> _accessList = new HashMap<String, Privileges>();
	private static PropertiesConfiguration config = new PropertiesConfiguration();
	
	public static void addUser(String hostname, Privileges level)
	{
		//make sure to update the alias:
		if(_aliasList.containsKey(hostname)){
			hostname = _aliasList.get(hostname);
		} 
		_accessList.put(hostname.toLowerCase(), level);
		
		//update config.
		if(config.containsKey("acl." + hostname))
			config.clearProperty("acl." + hostname);
		config.addProperty("acl." + hostname, level.getValue());
			
	}
	
	public static void addAlias(String aliasHostname, String originalHostname) throws AccessListException
	{
		if(!_accessList.containsValue(originalHostname))
			throw new AccessListException("Can't add an alias to a nick that does not exist!");
		
		_aliasList.put(aliasHostname, originalHostname);
		
		if(config.containsKey("alias." + aliasHostname))
			config.clearProperty("alias." + aliasHostname);
		config.addProperty("alias." + aliasHostname, originalHostname);
		
	}
	
	public static void delUser(String hostname)
	{
		_accessList.remove(hostname);
	}
	
	public static void loadFromConfig(String configfile) throws ConfigurationException, FileNotFoundException
	{
		config.setAutoSave(true);
		config.setThrowExceptionOnMissing(true);
		FileInputStream ifp = new FileInputStream(configfile);
		config.load(ifp);
		
		@SuppressWarnings("rawtypes")
		Iterator keys = config.getKeys("acl");
		while(keys.hasNext())
		{
			System.out.println("Adding key!");
			String key = keys.next().toString();
			String host = String.copyValueOf(key.toCharArray(), 4, key.toString().length()-4).toLowerCase();
			System.out.println("Key: " + key);
			Privileges axx = Privileges.lookup((config.getInt(key)));
			_accessList.put(host, axx );
		}
		
		keys = config.getKeys("alias");
		while(keys.hasNext())
		{
			String key = keys.next().toString();
			String host = String.copyValueOf(key.toCharArray(), 6, key.toString().length()-6);
			_aliasList.put(host, config.getString(key));
		}
		
	}
	
	public static boolean isAllowed(String hostname, Privileges minimumAccess)
	{
		if(_aliasList.containsKey(hostname))
			hostname = _aliasList.get(hostname);
		
		Privileges userAccess = Privileges.USER;
		if(_accessList.containsKey(hostname)){
			System.out.println("listing found");
			userAccess = _accessList.get(hostname);
		}
				
		if(userAccess.getValue() >= minimumAccess.getValue())
			return true;
		
		return false;
	}
}
