/**
 * 
 */
package org.Thom.DutchBot;

import java.io.IOException;

import org.apache.commons.cli.*;
import org.apache.commons.configuration.*;
import org.jibble.pircbot.IrcException;

/**
 * @author Thom
 * 
 */
public class Main {
	private static String configfile = "C:\\Users\\Thom\\workspace\\DutchBot\\bin\\org\\Thom\\DutchBot\\irc.properties";
	private static String server = "irc.what-network.net";
	private static int port = 6667;
	private static String password = null;
	private static String nick = "DutchBot";

	@SuppressWarnings("static-access")
	public static void main(String[] args) throws IOException, IrcException,
			InterruptedException, ConfigurationException {
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

			if (cli.hasOption("pw"))
				password = cli.getOptionValue("pw");
			if (cli.hasOption("s"))
				server = cli.getOptionValue("s");
			if (cli.hasOption("p"))
				port = Integer.parseInt(cli.getOptionValue("p"));
			if (cli.hasOption("n"))
				nick = cli.getOptionValue("n");

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

		DutchBot bot = new DutchBot("");
		AccessList.loadFromConfig(configfile);
		boolean result = bot.tryConnect(server, port, nick, password);
		if (result)
			System.out.println(" Connected\n");
		else
			System.out.println(" Connecting failed :O");
	}

}
