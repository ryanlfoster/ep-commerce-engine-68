package com.elasticpath.tags.domain;

import com.elasticpath.persistence.api.Entity;

/**
 * A ConditionalExpression is a boolean expression consisting of one or more operators and operands,
 * and it is built using operands contained only within a particular Tag Dictionary.
 */
public interface ConditionalExpression extends Entity {

	/** A condition max length. */
	int CONDITION_STRING_MAX_LENGTH = 4000;

	/**
     * @return the name of the 'Condition'
     */
    String getName();

    /**
     * Sets a name for the 'Condition'.
     *
     * @param name a name to be set
     */
    void setName(String name);

    /**
     * @return the description of the 'Condition'
     */
    String getDescription();

    /**
     * Sets the description of the 'Condition'.
     *
     * @param description a description to be set
     */
    void setDescription(String description);

    /**
     * @return the condition string.
     */
    String getConditionString();

    /**
     * Sets the condition string of the 'Condition'.
     *
     * @param conditionString a condition to be set
     */
    void setConditionString(String conditionString);

    /**
     * @return the tag dictionary guid
     */
    String getTagDictionaryGuid();

    /**
     * Sets a {@link TagDictionary} guid.
     *
     * @param tagDictionaryGuid a tag dictionary guid to be set
     */
    void setTagDictionaryGuid(String tagDictionaryGuid);

    /**
     * @return the guid.
     */
    String getGuid();

    /**
     * Set the guid.
     *
     * @param guid the guid to set.
     */
    void setGuid(String guid);
    
    /**
     * Is condition created explicitly.
     * @return true if condition was created explicitly. 
     */
    boolean isNamed();

    /**
     * Set explicit flag to condition. I.e. is condition created as named (true) or from ad-hoc (false). 
     * @param named explicit flag.
     */
	void setNamed(boolean named);
    
}
