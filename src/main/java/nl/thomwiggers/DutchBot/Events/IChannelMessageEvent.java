package nl.thomwiggers.DutchBot.Modules;

public interface IChannelMessageEvent {
    public void notifyChannelMessageEvent(String channel, String sender,
	    String login, String hostname, String message);
}
