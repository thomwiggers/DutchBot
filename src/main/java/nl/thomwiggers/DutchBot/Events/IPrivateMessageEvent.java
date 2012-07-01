package nl.thomwiggers.DutchBot.Modules;

public interface IPrivateMessageEvent {

    void notifyPrivateMessageEvent(String sender, String login,
	    String hostname, String message);

}
