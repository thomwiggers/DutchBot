package org.Thom.DutchBot;

import java.util.TreeMap;

/**
 * 
 * @author Thom
 * 
 */
public enum Privileges {
    OWNER(100), OPERATOR(50), USER(0), IGNORE(-10), KILLONSIGHT(-200);

    private final int Privilegelevel;

    Privileges(int level) {
	this.Privilegelevel = level;
    }

    public int getValue() {
	return this.Privilegelevel;
    }

    private static TreeMap<Integer, Privileges> _map;
    static {
	_map = new TreeMap<Integer, Privileges>();
	for (Privileges num : Privileges.values()) {
	    _map.put(num.getValue(), num);
	}
    }

    public static Privileges lookup(int value) {
	return _map.get(value);
    }

}