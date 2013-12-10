package com.elasticpath.validation.domain.impl;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.openjpa.persistence.ElementDependent;
import org.apache.openjpa.persistence.jdbc.ElementJoinColumn;
import org.apache.openjpa.persistence.jdbc.ElementJoinColumns;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.impl.AbstractEpDomainImpl;
import com.elasticpath.domain.misc.LocalizedProperties;
import com.elasticpath.domain.misc.LocalizedPropertyValue;
import com.elasticpath.domain.misc.impl.TagValueConstraintLocalizedPropertyValueImpl;
import com.elasticpath.validation.domain.ValidationConstraint;

/**
 * Declarative validator, holds constraints for values in a declarative format.
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TYPE")
@Table(name = DeclarativeValidationConstraintImpl.TABLE_NAME)
public class DeclarativeValidationConstraintImpl extends AbstractEpDomainImpl implements ValidationConstraint {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TVALIDATIONCONSTRAINTS";
	
	private String constraint;
	
	private String errorMessageKey;
	
	private long uidPk; 
	
	/**
     * Localized values for tag definition (used for UI).
     */
    private LocalizedProperties localizedProperties;
    private Map<String, LocalizedPropertyValue> localizedPropertiesMap = new HashMap<String, LocalizedPropertyValue>();
    
	
	@Override
	@Basic
	@Column(name = "VALIDATION_CONSTRAINT", nullable = false)
	public String getConstraint() {
		return constraint;
	}

	@Override
	public void setConstraint(final String constraint) {
		this.constraint = constraint;
	}

	@Override
	@Basic
	@Column(name = "ERROR_MESSAGE_KEY", nullable = false)
	public String getErrorMessageKey() {
		return errorMessageKey;
	}

	@Override
	public void setErrorMessageKey(final String errorMessageKey) {
		this.errorMessageKey = errorMessageKey;
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
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;

		result = prime * result + ObjectUtils.hashCode(this.errorMessageKey);
		result = prime * result + ObjectUtils.hashCode(this.constraint);
		
		return result;
	}
	
	@Override
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}
		
		if (!(other instanceof DeclarativeValidationConstraintImpl)) {
			return false;
		}
		
		DeclarativeValidationConstraintImpl validation = (DeclarativeValidationConstraintImpl) other;
		return ObjectUtils.equals(this.errorMessageKey, validation.errorMessageKey)
				&& ObjectUtils.equals(this.constraint, validation.constraint);
	}
	
    @Override
    @Transient
    public String getLocalizedErrorMessage(final Locale locale) {
    	if (locale != null) {
    		String displayError = getDisplayErrorLocalizedPropertyFromLocalizedProperties(locale);
    		if (StringUtils.isNotBlank(displayError)) {
    			return displayError;
    		}
    	}
    	return getErrorMessageKey();
    }

    /**
     * @param locale the locale for which localised value is returned.
     * @return the localized value
     */
	String getDisplayErrorLocalizedPropertyFromLocalizedProperties(final Locale locale) {
		return this.getLocalizedProperties().getValueWithoutFallBack(getErrorMessageKey(), locale);
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
	@OneToMany(targetEntity = TagValueConstraintLocalizedPropertyValueImpl.class, fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
	@MapKey(name = "localizedPropertyKey")
	@ElementJoinColumns({
		@ElementJoinColumn(name = "OBJECT_UID", referencedColumnName = "UIDPK", nullable = false),
		@ElementJoinColumn(name = "TLOCALIZEDPROPERTIES.TYPE", referencedColumnName = "'TagValueConstraint'")
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
