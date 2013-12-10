package com.elasticpath.tags.domain.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.apache.openjpa.persistence.ElementDependent;
import org.apache.openjpa.persistence.jdbc.ElementJoinColumn;
import org.apache.openjpa.persistence.jdbc.ElementJoinColumns;
import org.apache.openjpa.persistence.jdbc.Unique;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.impl.AbstractLegacyEntityImpl;
import com.elasticpath.domain.misc.LocalizedProperties;
import com.elasticpath.domain.misc.LocalizedPropertyValue;
import com.elasticpath.domain.misc.impl.TagGroupLocalizedPropertyValueImpl;
import com.elasticpath.tags.domain.TagDefinition;
import com.elasticpath.tags.domain.TagGroup;

/**
 * Tag group that contains several tag definitions.
 */
@Entity
@Table(name = TagGroupImpl.TABLE_NAME)
public class TagGroupImpl extends AbstractLegacyEntityImpl implements TagGroup {

    /**
     * Database Table.
     */
    public static final String TABLE_NAME = "TTAGGROUP";

    /**
     * Serial version id.
     */
    private static final long serialVersionUID = 5000000001L;

    private long uidPk;
    
    private Set<TagDefinition> tagDefinitions = new HashSet<TagDefinition>();
    
    /**
     * Localized values for tag group (used for UI).
     */
    private LocalizedProperties localizedProperties;
    private Map<String, LocalizedPropertyValue> localizedPropertiesMap = new HashMap<String, LocalizedPropertyValue>();

	private String guid;

    @Override
    @Transient
    public String getLocalizedGroupName(final Locale locale) {
    	if (locale != null) {
    		String displayName = getDisplayNameLocalizedPropertyFromLocalizedProperties(locale);
    		if (StringUtils.isNotBlank(displayName)) {
    			return displayName;
    		}
    	}
    	return getGuid();
    }
    
    /**
     * @param locale the locale for which localised value is returned.
     * @return the localised value
     */
	String getDisplayNameLocalizedPropertyFromLocalizedProperties(final Locale locale) {
		return this.getLocalizedProperties().getValueWithoutFallBack(LOCALIZED_PROPERTY_DISPLAY_NAME, locale);
	}
    
    /**
	 * @return the <code>LocalizedProperties</code>
	 */
	@Transient
	public LocalizedProperties getLocalizedProperties() {
		if (localizedProperties == null) {
			this.localizedProperties = getBean(ContextIdNames.LOCALIZED_PROPERTIES);
			this.localizedProperties.setLocalizedPropertiesMap(getLocalizedPropertiesMap(), ContextIdNames.TAG_GROUP_LOCALIZED_PROPERTY_VALUE);
		}
		return this.localizedProperties;
	}

	/**
	 * @param localizedProperties the <code>LocalizedProperties</code>
	 */
	public void setLocalizedProperties(final LocalizedProperties localizedProperties) {
		this.localizedProperties = localizedProperties;
		if (localizedProperties != null) {
			setLocalizedPropertiesMap(localizedProperties.getLocalizedPropertiesMap());
		}
	}
    
    /**
	 * @return the localized properties map.
	 */
	@OneToMany(targetEntity = TagGroupLocalizedPropertyValueImpl.class, fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
	@MapKey(name = "localizedPropertyKey")
	@ElementJoinColumns({
		@ElementJoinColumn(name = "OBJECT_UID", referencedColumnName = "UIDPK", nullable = false),
		@ElementJoinColumn(name = "TLOCALIZEDPROPERTIES.TYPE", referencedColumnName = "'TagGroup'")
	})	
	@ElementDependent
	public Map<String, LocalizedPropertyValue> getLocalizedPropertiesMap() {
		return localizedPropertiesMap;
	}

	/**
	 * @param localizedPropertiesMap the property map to set
	 */
	public void setLocalizedPropertiesMap(final Map<String, LocalizedPropertyValue> localizedPropertiesMap) {
		this.localizedPropertiesMap = localizedPropertiesMap;
	}

	@Override
    @OneToMany(targetEntity = TagDefinitionImpl.class, fetch = FetchType.EAGER, cascade = { CascadeType.MERGE, CascadeType.REFRESH },
    		mappedBy = "group")
	public Set<TagDefinition> getTagDefinitions() {
        return this.tagDefinitions;
    }

	@Override
    public void setTagDefinitions(final Set<TagDefinition> tagDefinitions) {
        this.tagDefinitions = tagDefinitions;
    }

    @Override
    public void addTagDefinition(final TagDefinition tagDefinition) {
    	tagDefinition.setGroup(this);
        this.tagDefinitions.add(tagDefinition);
    }

    @Override
    public void removeTagDefinition(final TagDefinition tagDefinition) {
        this.tagDefinitions.remove(tagDefinition);
    }
    
	
	@Override
    @Id
    @Column(name = "UIDPK")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
    @TableGenerator(name = TABLE_NAME, table = "JPA_GENERATED_KEYS", pkColumnName = "ID", valueColumnName = "LAST_VALUE", pkColumnValue = TABLE_NAME)
	public long getUidPk() {
		return uidPk;
	}

    @Override
	public void setUidPk(final long uidPk) {
		this.uidPk = uidPk;
	}

	/**
     * @return the guid.
     */
    @Override
    @Basic
    @Column(name = "GUID")
    @Unique(name = "TAGGROUP_UNIQUE")
    public String getGuid() {
        return guid;
    }

    /**
     * Set the guid.
     *
     * @param guid the guid to set.
     */
    @Override
    public void setGuid(final String guid) {
		this.guid = guid;
    }
	
	/**
     * Generate the hash code.
     *
     * @return the hash code.
     */
    @Override
    public int hashCode() {
        if (getGuid() == null) {
            return 0;
        }
        return getGuid().hashCode();
    }
    
    /**
     * Determines whether the given object is equal to this 'TagGroup'.
     *
     * @param obj to be compared for equality
     * @return true if the given object's GUID is equal to this one's GUID
     */
    @Override
    public boolean equals(final Object obj) {
        return obj instanceof TagGroup && this.getGuid().equals(((TagGroup) obj).getGuid());
    }

    /**
     * @return the string representation of the {@link com.elasticpath.tags.domain.TagGroup}
     */
    @Override
    public String toString() {
        return "[TagGroup: "
                + "GUID=" + this.getGuid()
                + "]";
    }
}
