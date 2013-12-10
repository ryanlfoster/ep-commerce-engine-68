package com.elasticpath.tags.domain;

import java.util.Set;

import com.elasticpath.persistence.api.Persistable;

/**
 * Tag dictionary is a dictionary to group set of tags.
 */
public interface TagDictionary extends Persistable {

	/** Shopper Guid. */
    String DICTIONARY_SHOPPER_GUID = "SHOPPER";
	/** Time Guid. */
    String DICTIONARY_TIME_GUID    = "TIME";
	/** Stores Guid. */
    String DICTIONARY_STORES_GUID  = "STORES";
    /** GUID for the SHOPPER on PLA wizard.*/
	String DICTIONARY_PLA_SHOPPER_GUID = "PLA_SHOPPER";	
    /** GUID for the SHOPPER on promotion wizard.*/
	String DICTIONARY_PROMOTIONS_SHOPPER_GUID = "PROMOTIONS_SHOPPER";
    /** Offer Guid. */
    String DICTIONARY_OFFER_SHOPPER_GUID = "OFFER_SHOPPER";

	/**
     * @return the name
     */
    String getName();

    /**
     * Sets the name.
     *
     * @param name a name to be set
     */
    void setName(String name);

    /**
     * @return the purpose description
     */
    String getPurpose();

    /**
     * Sets the purpose.
     *
     * @param purpose a purpose description to be set
     */
    void setPurpose(String purpose);

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
     * @return a set of {@link TagDefinition}
     */
     Set<TagDefinition> getTagDefinitions();

    /**
     * Adds a {@link TagDefinition}.
     *
     * @param tagDefinition a tag definition to be added
     */
     void addTagDefinition(TagDefinition tagDefinition);

    /**
     * Removes the {@link TagDefinition}.
     *
     * @param tagDefinition a tag definition to be removed
     */
    void removeTagDefinition(TagDefinition tagDefinition);

}
