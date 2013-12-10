package com.elasticpath.domain.contentspace.impl;

import java.util.HashMap;
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

import org.apache.openjpa.persistence.DataCache;
import org.apache.openjpa.persistence.ElementDependent;
import org.apache.openjpa.persistence.jdbc.ElementForeignKey;
import org.apache.openjpa.persistence.jdbc.ElementJoinColumn;
import org.apache.openjpa.persistence.jdbc.Unique;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.contentspace.Parameter;
import com.elasticpath.domain.contentspace.ParameterLocaleDependantValue;
import com.elasticpath.domain.contentspace.ParameterValue;
import com.elasticpath.domain.impl.AbstractLegacyEntityImpl;


/**
 * Default implementation of {@link ParameterValue}.
 */
@Entity
@Table(name = ParameterValueImpl.TABLE_NAME)
@DataCache(enabled = false)
public class ParameterValueImpl extends AbstractLegacyEntityImpl implements ParameterValue {
	
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 20090112L;
	
	
	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TCSPARAMETERVALUE";
	
	private static final String FK_COLUMN_NAME = "CSPARAMETERVALUE_UID";
	
	/**
	 * Represent non localizable key for values. 
	 */
	public static final String NULL_VALUE = "null-value";

	private Map<String, ParameterLocaleDependantValue> values = new HashMap<String, ParameterLocaleDependantValue>();
	
	private Parameter parameter;
	
	private long uidPk;
	
	private String parameterName;
	
	private boolean localizable;	
	
	private String description;


	private String guid;


	/**
	 * Gets the unique identifier for this domain model object.
	 * 
	 * @return the unique identifier.
	 */
	@Id
	@Column(name = "UIDPK")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
	@TableGenerator(name = TABLE_NAME, table = "JPA_GENERATED_KEYS", pkColumnName = "ID", valueColumnName = "LAST_VALUE", pkColumnValue = TABLE_NAME)
	public long getUidPk() {
		return this.uidPk;
	}
	
	@Override	
	@Basic
	@Column(name = "PARAMETER_NAME")	
	public String getParameterName() {
		return parameterName;
	}

	/**
	 * Get localizable flag.
	 * @return true if parameter localizable
	 */
	@Basic
	@Column(name = "LOCALIZABLE")
	public boolean isLocalizable() {
		return localizable;
	}
	

	/**
	 * Get the Map of values.
	 * @return Map of String values per language.
	 */
	@OneToMany(targetEntity = ParameterLocaleDependantValueImpl.class, cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	@MapKey(name = "locale")
	@ElementJoinColumn(name = FK_COLUMN_NAME, nullable = false)
	@ElementForeignKey(name = "TCSPARAMETERVALUE_FK")
	@ElementDependent
	public Map<String, ParameterLocaleDependantValue> getValues() {
		return values;
	}
	
	

	/**
	 * Sets the unique identifier for this domain model object.
	 * 
	 * @param uidPk the new unique identifier.
	 */
	public void setUidPk(final long uidPk) {
		this.uidPk = uidPk;
	}
	

	@Override	
	public void setParameterName(final String parameterName) {
		this.parameterName = parameterName;
	}
	
	
	@Override
	@Transient
	public Parameter getParameter() {
		if (parameter == null) {
			parameter = getBean(ContextIdNames.DYNAMIC_CONTENT_WRAPPER_USER_INPUT_PARAMETER); 
			parameter.setParameterId(getParameterName());	
			parameter.setDescription(getDescription());
			parameter.setLocalizable(isLocalizable());
		}
		return parameter;
	}
	
	@Override
	@Transient
	public String getValue(final String locale) {
		String value = null;
		ParameterLocaleDependantValue dependantValue;
		
		if (getParameter().isLocalizable()) {
			dependantValue = getValues().get(locale);
			if (dependantValue != null) {
				value = dependantValue.getValue();
			}
		} else {
			dependantValue = getValues().get(NULL_VALUE);
			if (dependantValue != null) {
				value = dependantValue.getValue();
			}
		}
		
		if (value == null && !getParameter().isRequired()) {
			value = "";				
		}		
		
		return value;
	}
	
	@Override
	public void setParameter(final Parameter parameter) {
		this.parameter = parameter;
	}
	

	/**
	 * Set the localizable flag.
	 * @param localizable flag
	 */
	public void setLocalizable(final boolean localizable) {
		this.localizable = localizable;
	}
	
	
	/**
	 * {@inheritDoc}.
	 * This method used in test only.
	 */
	public void setValue(final String value, final String locale) {
		ParameterLocaleDependantValue localeDependantValue = null;
		final String resolvedLocale;
		// MUST NOT BE NULL - should throw NullPointer
		if (getParameter().isLocalizable()) {
			localeDependantValue = getValues().get(locale);
			resolvedLocale = locale;
		} else {
			localeDependantValue = getValues().get(NULL_VALUE);
			resolvedLocale = NULL_VALUE;
		}
		
		if (null == value || value.trim().equals("")) {
			getValues().remove(resolvedLocale);
			return;
		}
		
		
		if (null == localeDependantValue) {
			localeDependantValue = getNewParameterLocaleDependantValue();
        }
		
		
		localeDependantValue.setValue(value);		
		localeDependantValue.setLocale(resolvedLocale);
		getValues().put(resolvedLocale, localeDependantValue);
		 
	}
	
	/**
	 * get new instance of locale dependent value while doing setValue(value, locale).
	 *
	 * @return new instance
	 */
	@Transient
	protected ParameterLocaleDependantValue getNewParameterLocaleDependantValue() {
		return getBean(ContextIdNames.DYNAMIC_PARAMETER_LDF_VALUE);
	}
	
	@Override
	public String toString() {
		String parameterName = "undefined";
		if (getParameter() != null) {
			parameterName = getParameterName();
		}
		return "ParameterValue: " + parameterName + ", values: " + getValues();
	}
	

	
	
	/**
	 * Set the Map of values.
	 * @param values Map of String values per language.
	 */
	public void setValues(final Map<String, ParameterLocaleDependantValue> values) {
		this.values = values;
	}
	
	@Override	
	@Basic
	@Column(name = "DESCRIPTION")	
	public String getDescription() {
		return description;
	}

	@Override
	public void setDescription(final String description) {
		this.description = description;
	}
	
    /**
     * @return the guid.
     */
    @Override
    @Basic
    @Column(name = "GUID")
    @Unique(name = "TCSPARAMETERVALUE_UNIQUE")
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
	
	
}
