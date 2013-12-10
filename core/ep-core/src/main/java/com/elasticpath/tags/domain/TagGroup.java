package com.elasticpath.tags.domain;

import java.util.Locale;
import java.util.Set;

import com.elasticpath.persistence.api.Persistable;

/**
 * Tag group interface. 
 */
public interface TagGroup extends Persistable {

	/**
	 * The name of localized property -- display name.
	 */
	String LOCALIZED_PROPERTY_DISPLAY_NAME = "tagGroupDisplayName";
	
	/**
     * @param locale the required locale
     * @return tag group name in language requested 
     *         (or name using getGuid if no localized value present)
     */
	String getLocalizedGroupName(Locale locale);
	
    /**
     * @return the GUID of the tag group
     */
    String getGuid();

    /**
     * Set the guid of this tag group.
     * @param guid the guid of the tag group
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
    
    /**
     * Sets a list of {@link TagDefinition}.
     *
     * @param tagDefinitions a set of {@link TagDefinition}
     */
    void setTagDefinitions(Set<TagDefinition> tagDefinitions);
    
}
