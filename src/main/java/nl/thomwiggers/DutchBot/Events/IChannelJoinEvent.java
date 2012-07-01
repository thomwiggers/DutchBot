package nl.thomwiggers.DutchBot.Events;

public interface IChannelJoinEvent {

    void notifyChannelJoinEvent(String channel, String sender, String login,
	    String hostname);
}
