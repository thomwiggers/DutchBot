package org.Thom.DutchBot.Events;

public abstract class Event {
    @Override
    public String toString() {
	return this.getClass().getName();
    }

}
