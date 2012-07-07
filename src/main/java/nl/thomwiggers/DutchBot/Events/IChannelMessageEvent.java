package nl.thomwiggers.DutchBot.Events;

public interface IChannelMessageEvent {
	public void notifyChannelMessageEvent(String channel, String sender,
			String login, String hostname, String message);
}
