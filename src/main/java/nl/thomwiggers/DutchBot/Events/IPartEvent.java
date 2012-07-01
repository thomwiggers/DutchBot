/**
 * 
 * @author Thom
 */
package nl.thomwiggers.DutchBot.Modules;

/**
 * @author Thom
 * 
 */
public interface IPartEvent {
    /**
     * @param channel
     * @param sender
     * @param login
     * @param hostname
     */
    public void notifyPartEvent(String channel, String sender, String login,
	    String hostname);
}
