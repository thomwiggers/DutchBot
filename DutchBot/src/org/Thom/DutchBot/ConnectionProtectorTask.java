/**
 * 
 */
package org.Thom.DutchBot;

import java.io.IOException;
import java.util.TimerTask;

import org.jibble.pircbot.IrcException;

/**
 * @author Thom
 *
 */
public class ConnectionProtectorTask extends TimerTask {

	private DutchBot bot;
	private boolean wasConnected;
	
	/**
	 * 
	 */
	public ConnectionProtectorTask(DutchBot bot) {
		this.bot = bot;
	}

	/* (non-Javadoc)
	 * @see java.util.TimerTask#run()
	 */
	@Override
	public void run() {
		if(!this.bot.isConnected() && this.wasConnected)
		{
			try {
				bot.reconnect();
			} catch (IrcException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (this.bot.isConnected())
		{
			this.wasConnected = true;
		}

	}

}
