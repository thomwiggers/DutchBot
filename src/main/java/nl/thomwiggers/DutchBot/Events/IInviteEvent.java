package nl.thomwiggers.DutchBot.Modules;

public interface IInviteEvent {

    void notifyInviteEvent(String targetNick, String sourceNick,
	    String sourceLogin, String sourceHostname, String channel);

}
