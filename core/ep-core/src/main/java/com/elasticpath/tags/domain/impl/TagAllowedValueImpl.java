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

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.openjpa.persistence.DataCache;
import org.apache.openjpa.persistence.ElementDependent;
import org.apache.openjpa.persistence.jdbc.ElementJoinColumn;
import org.apache.openjpa.persistence.jdbc.ElementJoinColumns;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.impl.AbstractLegacyPersistenceImpl;
import com.elasticpath.domain.misc.LocalizedProperties;
import com.elasticpath.domain.misc.LocalizedPropertyValue;
import com.elasticpath.domain.misc.impl.TagAllowedValueLocalizedPropertyValueImpl;
import com.elasticpath.tags.domain.TagAllowedValue;


/**
 * Implementation of allowed values for Tag Value Type.
 */
@Entity
@Table(name = TagAllowedValueImpl.TABLE_NAME)
@DataCache(enabled = false)
public class TagAllowedValueImpl extends AbstractLegacyPersistenceImpl implements TagAllowedValue {
    /**
     * Database Table.
     */
    public static final String TABLE_NAME = "TTAGALLOWEDVALUE";

    /**
     * Serial version id.
     */
    private static final long serialVersionUID = 5000000002L;

    private long uidPk;
	private int ordering;
    private String value;
    private String description;
    
    /**
     * Localized values for tag allowed value description (used for UI).
     */
    private LocalizedProperties localizedProperties;
    private Map<String, LocalizedPropertyValue> localizedPropertiesMap = new HashMap<String, LocalizedPropertyValue>();
    
    
    /**
     * Default constructor.
     */
    public TagAllowedValueImpl() {  	//NOPMD
    }

    /**
     * Constructor with value attribute.
     * 
     * @param value a value to be set
     */
    public TagAllowedValueImpl(final String value) {
		super();
		this.value = value;
	}
    
    /**
     * Constructor with value attribute.
     * 
     * @param value a value to be set
     * @param ordering a order of allowed value
     */
    public TagAllowedValueImpl(final String value, final int ordering) {
		super();
		this.value = value;
		this.ordering = ordering;
	}
    

	/**
	 * 
	 * Regular expression.
	 * 
     * @return the value of 'Allowed value'
     */
    @Basic
    @Column(name = "VALUE")
    public String getValue() {
        return value;
    }

    /**
     * Sets the value.
     *
     * @param value a value to be set
     */
    public void setValue(final String value) {
        this.value = value;
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
     * 
     * Localized description explain the allowed value.
     * 
     * @param locale the required locale
     * @return allowed value name in language requested 
     *         or default description if no localized value present
     */
    @Transient
	public String getLocalizedDescription(final Locale locale) {
    	if (locale != null) {
    		String displayName = getDisplayNameLocalizedPropertyFromLocalizedProperties(locale);
    		if (StringUtils.isNotBlank(displayName)) {
    			return displayName;
    		}
    	}
		return getDescription();
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
			this.localizedProperties.setLocalizedPropertiesMap(getLocalizedPropertiesMap(), 
					ContextIdNames.TAG_ALLOWED_VALUE_LOCALIZED_PROPERTY_VALUE);
		}
		return this.localizedProperties;
	}

	/**
	 * @return the localized properties map.
	 */
	@OneToMany(targetEntity = TagAllowedValueLocalizedPropertyValueImpl.class, fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
	@MapKey(name = "localizedPropertyKey")
	@ElementJoinColumns({
		@ElementJoinColumn(name = "OBJECT_UID", referencedColumnName = "UIDPK", nullable = false),
		@ElementJoinColumn(name = "TLOCALIZEDPROPERTIES.TYPE", referencedColumnName = "'TagAllowedValue'")
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
     * 
     * Description explain the validation regular expression rule and expectations in human readable form.  
     * 
     * @return the description of 'Allowed value'
     */
    @Basic
    @Column(name = "DESCRIPTION")
    public String getDescription() {
		return description;
	}
    
    /**
     * 
     * Set the description of allowed value.
     * 
     * @param description a description to set.
     */
	public void setDescription(final String description) {
		this.description = description;
	}
	
	/**
	 * Get the order in which this allowed value should appear.
	 * 
	 * @return the ordering
	 */
	@Basic
	@Column(name = "ORDERING")
	public int getOrdering() {
		return this.ordering;
	}

	/**
	 * Set the order in which this allowed value should appear.
	 * 
	 * @param ordering the ordering
	 */
	public void setOrdering(final int ordering) {
		this.ordering = ordering;
	}
	

	/**
     * @return the string representation of the {@link com.elasticpath.tags.domain.TagAllowedValue}
     */
    @Override
    public String toString() {
        return "[TagAllowedValue:"
                + " Value=" + this.getValue()
                + " Description=" + this.getDescription()
                + " Ordering=" + this.getOrdering()
                + "]";
    }
    
    /**
     * Generate the hash code.
     *
     * @return the hash code.
     */
    @Override
    public int hashCode() {
        if (getValue() == null) {
            return 0;
        }
        return getValue().hashCode();
    }

    
    /**
     * Determines whether the given object is equal to this TagAllowedValue.
     *
     * @param obj to be compared for equality
     * @return true if the given object's GUID is equal to this one's GUID
     */
    @Override
    public boolean equals(final Object obj) {
    	if (this == obj) {
    		return true;
    	}
        return obj instanceof TagAllowedValueImpl 
        	&& ObjectUtils.equals(getValue(), ((TagAllowedValueImpl) obj).getValue());
    }
}
