package com.elasticpath.tags.domain.impl;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.apache.openjpa.persistence.DataCache;
import org.apache.openjpa.persistence.ElementDependent;
import org.apache.openjpa.persistence.jdbc.ElementJoinColumn;
import org.apache.openjpa.persistence.jdbc.ElementJoinColumns;
import org.apache.openjpa.persistence.jdbc.ForeignKey;
import org.apache.openjpa.persistence.jdbc.Unique;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.impl.AbstractLegacyEntityImpl;
import com.elasticpath.domain.misc.LocalizedProperties;
import com.elasticpath.domain.misc.LocalizedPropertyValue;
import com.elasticpath.domain.misc.impl.TagDefinitionLocalizedPropertyValueImpl;
import com.elasticpath.tags.domain.TagDefinition;
import com.elasticpath.tags.domain.TagGroup;
import com.elasticpath.tags.domain.TagValueType;

/**
 * {@link TagDefinition} implementation class.
 */
@Entity
@Table(name = TagDefinitionImpl.TABLE_NAME)
@DataCache(enabled = false)
public class TagDefinitionImpl extends AbstractLegacyEntityImpl implements TagDefinition {
    /**
     * Database Table.
     */
    public static final String TABLE_NAME = "TTAGDEFINITION";

    /**
     * Serial version id.
     */
    private static final long serialVersionUID = 5000000001L;

    private long uidPk;
    private String name;
    private TagGroup group;
    private String description;
    private TagValueType tagValueType;

   
    /**
     * Localized values for tag definition (used for UI).
     */
    private LocalizedProperties localizedProperties;
    private Map<String, LocalizedPropertyValue> localizedPropertiesMap = new HashMap<String, LocalizedPropertyValue>();

	private String guid;
	

    /**
     * @return the name of the 'Tag Definition'
     */
    @Basic
    @Column(name = "NAME")
    public String getName() {
        return name;
    }

    /**
     * Sets a name for the 'Tag Definition'.
     *
     * @param name a name to be set
     */
    public void setName(final String name) {
        this.name = name;
    }

    @Override
	@ManyToOne(optional = true, targetEntity = TagGroupImpl.class, cascade = { CascadeType.MERGE, CascadeType.REFRESH })
	@JoinColumn(name = "TAGGROUP_UID", nullable = true)
    public TagGroup getGroup() {
        return group;
    }

    @Override
    public void setGroup(final TagGroup group) {
		this.group = group;
	}

	@Override
    @Transient
    public String getLocalizedName(final Locale locale) {
    	if (locale != null) {
    		String displayName = getDisplayNameLocalizedPropertyFromLocalizedProperties(locale);
    		if (StringUtils.isNotBlank(displayName)) {
    			return displayName;
    		}
    	}
    	return getName();
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
			this.localizedProperties.setLocalizedPropertiesMap(getLocalizedPropertiesMap(), ContextIdNames.TAG_DEFINITION_LOCALIZED_PROPERTY_VALUE);
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
	@OneToMany(targetEntity = TagDefinitionLocalizedPropertyValueImpl.class, fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
	@MapKey(name = "localizedPropertyKey")
	@ElementJoinColumns({
		@ElementJoinColumn(name = "OBJECT_UID", referencedColumnName = "UIDPK", nullable = false),
		@ElementJoinColumn(name = "TLOCALIZEDPROPERTIES.TYPE", referencedColumnName = "'TagDefinition'")
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
    

    /**
     * @return the description of the 'Tag Definition'
     */
    @Basic
    @Column(name = "DESCRIPTION")
    public String getDescription() {
        return description;
    }

    /**
     * Sets a description for the 'Tag Definition'.
     *
     * @param description a description to be set
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * @return the value type of the 'Tag Definition'
     */
    @ManyToOne(targetEntity = TagValueTypeImpl.class, fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "TAGVALUETYPE_GUID", referencedColumnName = "GUID")
    @ForeignKey    
    public TagValueType getValueType() {
        return this.tagValueType;
    }

    /**
     * Sets the tag value type of the 'Tag Definition'.
     *
     * @param tagValueType a data type to be set
     */
    public void setValueType(final TagValueType tagValueType) {
        this.tagValueType = tagValueType;
    }


    /**
     * @return the guid.
     */
    @Override
    @Basic
    @Column(name = "GUID")
    @Unique(name = "TAGDEFINITION_UNIQUE")
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

    @Override
    @Id
    @Column(name = "UIDPK")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
    @TableGenerator(name = TABLE_NAME, table = "JPA_GENERATED_KEYS", pkColumnName = "ID", valueColumnName = "LAST_VALUE", pkColumnValue = TABLE_NAME)
    public long getUidPk() {
        return this.uidPk;
    }

    @Override
    public void setUidPk(final long uidPk) {
        this.uidPk = uidPk;
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
     * Determines whether the given object is equal to this TagDefinition.
     *
     * @param obj to be compared for equality
     * @return true if the given object's GUID is equal to this one's GUID
     */
    @Override
    public boolean equals(final Object obj) {
        return obj instanceof TagDefinition && this.getGuid().equals(((TagDefinition) obj).getGuid());

    }

    /**
     * @return the string representation of the {@link TagDefinition}
     */
    @Override
    public String toString() {
        return "[TagDefinition: "
                + "GUID=" + this.getGuid()
                + "Name=" + this.getName()
                + "Description=" + this.getDescription()
                + "DataType=" + this.getValueType()
                + "GroupUid=" + this.getGroup()
                + "]";
    }
}
