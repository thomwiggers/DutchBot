package nl.thomwiggers.DutchBot.Events;

public interface IInviteEvent {

	void notifyInviteEvent(String targetNick, String sourceNick,
			String sourceLogin, String sourceHostname, String channel);

}
