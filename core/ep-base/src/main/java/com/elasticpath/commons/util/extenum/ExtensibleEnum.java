package com.elasticpath.commons.util.extenum;

/**
 * Interface for extensible enums.
 */
public interface ExtensibleEnum {

    /**
     * Get the ordinal value of the enum value.
     * @return the ordinal value
     */
    int getOrdinal();
    
    /**
     * Return the name of this enum value.
     * @return the name
     */
    String getName();

}
