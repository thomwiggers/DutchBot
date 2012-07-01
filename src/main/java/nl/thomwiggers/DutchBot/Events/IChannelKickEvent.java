package nl.thomwiggers.DutchBot.Modules;

public interface IChannelKickEvent {

    void notifyChannelKickEvent(String channel, String kickerNick,
	    String kickerLogin, String kickerHostname, String recipientNick,
	    String reason);

}
