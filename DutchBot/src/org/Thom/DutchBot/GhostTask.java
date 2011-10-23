package org.Thom.DutchBot;

import java.util.Timer;
import java.util.TimerTask;

class GhostTask extends TimerTask {
	
	private Timer timer;
	private DutchBot bot;
	private String nick;
	
	
	GhostTask(Timer timer, DutchBot bot, String nick) {
		super();
		this.bot = bot;
		this.nick = nick;
	}


	@Override
	public void run() {
		if (nick != bot.getNick())
		{
			bot.ghost(nick);
			bot.changeNick(nick);
			this.timer.schedule(this, 1000L);
		}

	}

}
