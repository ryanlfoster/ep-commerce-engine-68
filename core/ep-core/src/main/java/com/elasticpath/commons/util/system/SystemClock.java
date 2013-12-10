package com.elasticpath.commons.util.system;

/**
 * Default implementation of Clock that uses System. 
 */
public final class SystemClock implements Clock {
    
    /** Singleton instance. */
    public static final Clock INSTANCE = new SystemClock();
    
    private SystemClock() {
        // private singleton class
    }
    
    /**
     * Return singleton instance.
     * @return a singleton instance
     */
    public static Clock getInstance() {
        return INSTANCE;
    }

    @Override
    public long currentTimeMillis() {
        return System.currentTimeMillis();
    }

}
