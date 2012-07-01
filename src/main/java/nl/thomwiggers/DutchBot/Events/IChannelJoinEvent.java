package nl.thomwiggers.DutchBot.Modules;

public interface IChannelJoinEvent {

    void notifyChannelJoinEvent(String channel, String sender, String login,
	    String hostname);
}
