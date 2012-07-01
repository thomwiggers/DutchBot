/**
 * 
 */
package nl.thomwiggers.DutchBot.Modules;

import nl.thomwiggers.DutchBot.DutchBot;

/**
 * Abstract modules class
 * 
 * @author Thom
 * 
 */
public abstract class ModuleAbstract {

    /**
     * DutchBot instance
     */
    protected final DutchBot bot;

    /**
     * Initializes a new module
     */
    public ModuleAbstract(DutchBot bot) {
	this.bot = bot;
	init();
    }

    /**
     * Initializer, called by constructor
     */
    public void init() {
    }

    /**
     * Returns the bot instance
     * 
     * @return DutchBot instance
     */
    public DutchBot getBot() {
	return bot;
    }

}
