/**
 * 
 */
package org.Thom.DutchBot;

import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.jibble.pircbot.IrcException;

/**
 * @author Thom
 * 
 */
public class Main {

    @SuppressWarnings("static-access")
    public static void main(String[] args) throws IOException, IrcException,
	    InterruptedException, ConfigurationException,
	    InstantiationException, IllegalAccessException {
	String server = "irc.what-network.net";
	int port = 6667;
	String configfile = "irc.properties";
	String nspass = "";
	String password = null;
	String nick = "DutchBot";
	String[] autojoinList = {};
	HashMap<String, String[]> eventHandlers = new HashMap<String, String[]>();

	Options options = new Options();
	options.addOption(OptionBuilder
		.withLongOpt("config")
		.withArgName("configfile")
		.hasArg()
		.withDescription(
			"Load configuration from configfile, or use the default irc.cfg")
		.create("c"));
	options.addOption(OptionBuilder.withLongOpt("server")
		.withArgName("<url>").hasArg()
		.withDescription("Connect to this server").create("s"));
	options.addOption(OptionBuilder.withLongOpt("port").hasArg()
		.withArgName("port")
		.withDescription("Connect to the server with this port")
		.create("p"));
	options.addOption(OptionBuilder.withLongOpt("password").hasArg()
		.withArgName("password")
		.withDescription("Connect to the server with this password")
		.create("pw"));
	options.addOption(OptionBuilder.withLongOpt("nick").hasArg()
		.withArgName("nickname")
		.withDescription("Connect to the server with this nickname")
		.create("n"));
	options.addOption(OptionBuilder.withLongOpt("nspass").hasArg()
		.withArgName("password")
		.withDescription("Sets the password for NickServ").create("ns"));
	options.addOption("h", "help", false, "Displays this menu");

	try {
	    CommandLineParser parser = new GnuParser();
	    CommandLine cli = parser.parse(options, args);
	    if (cli.hasOption("h")) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("DutchBot", options);
		return;
	    }
	    // check for override config file
	    if (cli.hasOption("c"))
		configfile = cli.getOptionValue("c");

	    // config files:
	    PropertiesConfiguration config = new PropertiesConfiguration();
	    config.setAutoSave(true);
	    config.load(configfile);

	    server = config.getString("server.host", server);
	    port = config.getInt("server.port", port);
	    password = config.getString("password", password);
	    nick = config.getString("irc.nick", nick);
	    nspass = config.getString("irc.nickservpass", "");

	    if (config.containsKey("irc.autojoin"))
		autojoinList = config.getStringArray("irc.autojoin");

	    if (config.containsKey("bot.eventhandlers.messages"))
		eventHandlers.put("messages",
			config.getStringArray("bot.eventhandlers.messages"));

	    // Read the cli parameters
	    if (cli.hasOption("pw"))
		password = cli.getOptionValue("pw");
	    if (cli.hasOption("s"))
		server = cli.getOptionValue("s");
	    if (cli.hasOption("p"))
		port = Integer.parseInt(cli.getOptionValue("p"));
	    if (cli.hasOption("n"))
		nick = cli.getOptionValue("n");
	    if (cli.hasOption("ns"))
		nspass = cli.getOptionValue("ns");

	} catch (ParseException e) {
	    System.err.println("Error parsing command line vars "
		    + e.getMessage());
	    HelpFormatter formatter = new HelpFormatter();
	    formatter.printHelp("DutchBot", options);
	    System.exit(1);
	} catch (ConfigurationException e) {
	    System.err.println("Error with the configuration file: "
		    + e.getMessage());
	    System.exit(1);
	}

	DutchBot bot = new DutchBot(nick);
	bot.setNickservPassword(nspass);
	bot.setAutoJoinList(autojoinList);
	bot.addEvents(eventHandlers);
	AccessList.loadFromConfig(configfile);
	boolean result = bot.tryConnect(server, port, nick, password);
	if (result)
	    System.out.println(" Connected\n");
	else
	    System.out.println(" Connecting failed :O");
    }
}
