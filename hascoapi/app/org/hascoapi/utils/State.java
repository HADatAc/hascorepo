package org.hascoapi.utils;

public class State {

    private int current;

    public static final String DESIGN_NAME            = "design";
    public static final String ACTIVE_NAME            = "active";
    public static final String CLOSED_NAME            = "closed";
    public static final String ALL_NAME               = "all";

    public static final int DESIGN  = 1;
    public static final int ACTIVE  = 2;
    public static final int CLOSED  = 3;
    public static final int ALL     = 4;
    public static final int CHANGED = 5;

    public State () {
        current = ACTIVE;
    }

    public State (int current) {
        this.current = current;
    }

    public int getCurrent() {
        return current;
    }

}
