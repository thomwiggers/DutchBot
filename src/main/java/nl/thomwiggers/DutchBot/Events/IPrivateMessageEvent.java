package nl.thomwiggers.DutchBot.Events;

public interface IPrivateMessageEvent {

	void notifyPrivateMessageEvent(String sender, String login,
			String hostname, String message);

}
