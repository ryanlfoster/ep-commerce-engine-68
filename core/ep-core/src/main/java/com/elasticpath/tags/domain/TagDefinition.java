package com.elasticpath.tags.domain;

import java.util.Locale;

import com.elasticpath.persistence.api.Persistable;

/**
 * An interface for tag definition domain model. Please see Tag Framework Domain model.
 */
public interface TagDefinition extends Persistable {
	
	/**
	 * The name of localized property -- display name.
	 */
	String LOCALIZED_PROPERTY_DISPLAY_NAME = "tagDefinitionDisplayName";

    /**
     * @return the name of the 'Tag Definition'
     */
    String getName();

    /**
     * Sets a name for the 'Tag Definition'.
     *
     * @param name a name to be set
     */
    void setName(String name);

    /**
     * @return the group of the 'Tag Definition'
     */
    TagGroup getGroup();
    
    /**
     * Sets a group for the 'Tag Definition'.
     *
     * @param group a group to be set
     */
    void setGroup(TagGroup group);
    
    /**
     * @param locale the required locale
     * @return tag definition name in language requested 
     *         (or name using getName if no localized value present)
     */
    String getLocalizedName(Locale locale);
   
    /**
     * @return the description of the 'Tag Definition'
     */
    String getDescription();

    /**
     * Sets a description for the 'Tag Definition'.
     *
     * @param description a description to be set
     */
    void setDescription(String description);

    /**
     * @return the data type of the 'Tag Definition'
     */
    TagValueType getValueType();

    /**
     * Sets the data type of the 'Tag Definition'.
     *
     * @param dataType a data type to be set
     */
    void setValueType(TagValueType dataType);


    /**
     * @return the GUID of the price list
     */
    String getGuid();

    /**
     * Set the guid of this tag definition.
     * @param guid the guid of the tag definition
     */
    void setGuid(String guid);
    
}
