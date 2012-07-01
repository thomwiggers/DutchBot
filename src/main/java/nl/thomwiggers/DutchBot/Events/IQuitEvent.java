/**
 * 
 * @author Thom
 */
package nl.thomwiggers.DutchBot.Events;

/**
 * @author Thom
 * 
 */
public interface IQuitEvent {
    /**
     * @param sourceNick
     * @param sourceLogin
     * @param sourceHostname
     * @param reason
     */
    public void notifyQuitEvent(String sourceNick, String sourceLogin,
	    String sourceHostname, String reason);
}
