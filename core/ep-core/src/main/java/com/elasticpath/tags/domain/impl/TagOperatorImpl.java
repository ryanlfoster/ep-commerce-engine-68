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
import com.elasticpath.domain.misc.impl.TagOperatorLocalizedPropertyValueImpl;
import com.elasticpath.tags.domain.TagOperator;

/**
 * Implementation of UI Picker object. Hold symbolic key that used to get proper 
 * UI picker on client side. Used by Tag Value Type object. 
 */
@Entity
@Table(name = TagOperatorImpl.TABLE_NAME)
public class TagOperatorImpl extends AbstractLegacyEntityImpl implements TagOperator {

    /**
     * Database Table.
     */
    public static final String TABLE_NAME = "TTAGOPERATOR";
    
	   /**
     * Serial version id.
     */
    private static final long serialVersionUID = 5000000017L;

    private long uidPk;
    
    /**
     * Localized values for tag operator (used for UI).
     */
    private LocalizedProperties localizedProperties;
    private Map<String, LocalizedPropertyValue> localizedPropertiesMap = new HashMap<String, LocalizedPropertyValue>();

	private String guid;
	
    
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
     * @return the guid.
     */
    @Override
    @Basic
    @Column(name = "GUID")
    @Unique(name = "TAGOPERATOR_UNIQUE")    
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
     * Determines whether the given object is equal to this TagOperator.
     *
     * @param obj to be compared for equality
     * @return true if the given object's GUID is equal to this one's GUID
     */
    @Override
    public boolean equals(final Object obj) {
        return obj instanceof TagOperator && this.getGuid().equals(((TagOperator) obj).getGuid());

    }

    /** 
     * @return the string representation of the {@link TagOperator}
     */
    @Override
    public String toString() {
        return "[TagOperator: "
                + "GUID=" + this.getGuid()
                + "]";
    }

    @Override
    @Transient
	public String getName(final Locale locale) {
    	if (locale != null) {
    		String displayName = getDisplayNameLocalizedPropertyFromLocalizedProperties(locale);
    		if (StringUtils.isNotBlank(displayName)) {
    			return displayName;
    		}
    	}
		return this.getGuid();
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
	@OneToMany(targetEntity = TagOperatorLocalizedPropertyValueImpl.class, fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
	@MapKey(name = "localizedPropertyKey")
	@ElementJoinColumns({
		@ElementJoinColumn(name = "OBJECT_UID", referencedColumnName = "UIDPK", nullable = false),
		@ElementJoinColumn(name = "TLOCALIZEDPROPERTIES.TYPE", referencedColumnName = "'TagOperator'")
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
	
}
