package com.elasticpath.commons.util.system;

/**
 * Clock used to retrieve current time.  Mockable for testing. 
 */
public interface Clock {
   
    /**
     * @see {@link System#currentTimeMillis()}
     * @return the current time in milliseconds
     */
    long currentTimeMillis();

}
