package com.elasticpath.settings.domain.impl;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.openjpa.persistence.DataCache;
import org.apache.openjpa.persistence.Externalizer;
import org.apache.openjpa.persistence.Factory;

import com.elasticpath.settings.domain.SettingDefinition;
import com.elasticpath.settings.domain.SettingValue;

/**
 * JPA-persistence-enabled implementation of the <code>SettingsValue</code> interface.
 * This implementation encapsulates a SettingDefinition to provide access to the Path, Type,
 * and DefaultValue for the Setting. The LastModifiedDate is set by the persistence layer during
 * persistence.
 * <p>New SettingValueImpl objects are designed to be created by the 
 * {@link com.elasticpath.settings.impl.SettingValueFactoryWithDefinitionImpl}
 * class, which will set the wrapped SettingDefinition during creation. If you choose to extend or override
 * this SettingValue class then you **MUST** create your own implementation of 
 * {@link com.elasticpath.settings.SettingValueFactory}.</p>
 */
@Entity
@Table(name = SettingValueImpl.TABLE_NAME)
@DataCache(enabled = false)
@SuppressWarnings("PMD.AvoidUsingVolatile")
public class SettingValueImpl implements SettingValue {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 6000000001L;
	
	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TSETTINGVALUE";

	private SettingDefinition settingDefinition;
	private String value = null;
	private String context  = null;
	private Date lastModifiedDate = null;
	private long uidPk;

	private volatile String cachedValue = null;
	
	/**
	 * Sets the {@link SettingDefinition} that will be wrapped by this SettingValue.
	 * This method is not specified on the interface, and will be used by the factory.
	 * @param settingDefinition the SettingDefinition to wrap
	 * @throws IllegalArgumentException if the given SettingDefinition is null
	 */
	public void setSettingDefinition(final SettingDefinition settingDefinition) {
		this.settingDefinition = settingDefinition;
	}	

	/**
	 * Gets the unique identifier for this domain model object.
	 * @return the unique identifier.
	 */
	@Id
	@Column(name = "UIDPK")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
	@TableGenerator(name = TABLE_NAME, table = "JPA_GENERATED_KEYS",
					pkColumnName = "ID", valueColumnName = "LAST_VALUE", pkColumnValue = TABLE_NAME)
	public long getUidPk() {
		return uidPk;
	}

	/**
	 * Sets the unique identifier for this domain model object.
	 *
	 * @param uidPk the new unique identifier.
	 */
	public void setUidPk(final long uidPk) {
		this.uidPk = uidPk;
	}

	/**
	 * @return the settingsDefinition
	 */
	@ManyToOne(optional = false, targetEntity = SettingDefinitionImpl.class, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "SETTING_DEFINITION_UID", nullable = false)
	protected SettingDefinition getSettingDefinition() {
		return settingDefinition;
	}

	/**
	 * Get the context within which the setting value applies.
	 * @return the context
	 */
	@Basic
	@Column(name = "CONTEXT")
	public String getContext() {
		return this.context;
	}
	
	/**
	 * Set the context within which the setting value applies.
	 * @param context the context to set
	 */
	public void setContext(final String context) {
		this.context = context;
	}

	/**
	 * Calls {@link #getLastModifiedDateInternal()}.
	 * @return the value's last modified date, or the last modified date of the
	 * setting definition if the value's date is null.
	 */
	@Transient
	public Date getLastModifiedDate() {
		if (this.getLastModifiedDateInternal() != null) {
			return getLastModifiedDateInternal();
		}
		return getSettingDefinition().getLastModifiedDate();
	}
	
	
	/**
	 * Get the date at which this SettingsValue was last modified.
	 * @return the last modified date
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Version
	@Column(name = "LAST_MODIFIED_DATE", nullable = false)
	protected Date getLastModifiedDateInternal() {
		return this.lastModifiedDate;
	}

	/**
	 * Set the date when the SettingsValue was last modified.
	 * This method is only used by JPA.
	 *
	 * @param lastModifiedDate the date when the SettingsValue was last modified
	 */
	protected void setLastModifiedDateInternal(final Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
	
	/**
	 * Calls {@link #getValueInternal()}.
	 * @return the value, or the default value if the value is null.
	 */
	@Transient
	public String getValue() {
		if (cachedValue == null) {
			synchronized (this) {
				// CHECKSTYLE:OFF
				if (cachedValue == null) {
				// CHECKSTYLE:ON
					final String valueInternal = this.getValueInternal();
					if (valueInternal == null) {
						cachedValue = getDefaultValue();
					} else {
						cachedValue = valueInternal;
					}
				}
			}
		}
		return cachedValue;
	}
	
	/**
	 * JPA getter for value field.
	 * <p>This implementation is annotated with OpenJPA annotations for persistence purposes.
	 * Of particular note are the two annotations:
	 * <ul><li>{@code @Externalizer("com.elasticpath.persistence.api.StringExternalizer.toExternalForm")}</li>
	 * <li>{@code @Factory("com.elasticpath.persistence.api.StringExternalizer.toInternalForm")}</li></ul>
	 * which ensure that even if the underlying database (e.g. Oracle) converts empty strings to null values,
	 * JPA won't complain.</p>
	 * @return the value
	 */
	@Basic
	@Column(name = "CONTEXT_VALUE")
	@Externalizer("com.elasticpath.persistence.api.StringExternalizer.toExternalForm")
	@Factory("com.elasticpath.persistence.api.StringExternalizer.toInternalForm")
	protected String getValueInternal() {
		return this.value;
	}
	
	/**
	 * JPA setter for value field.
	 * @param value the value
	 */
	protected void setValueInternal(final String value) {
		this.value = value;
	}
	
	/**
	 * Calls {@link #setValueInternal(String)}.
	 * @param value the value to set
	 */
	public void setValue(final String value) {
		synchronized (this) {
			cachedValue = null;
		}
		
		setValueInternal(value);
	}

	/**
	 * Get this setting value's default value, from its associated {@link SettingsDefinition}.
	 * @return the default value
	 */
	@Transient
	public String getDefaultValue() {
		return getSettingDefinition().getDefaultValue();
	}

	/**
	 * Get the path to the definition of this Setting, from its associated {@link SettingsDefinition}.
	 * @return the path
	 */
	@Transient
	public String getPath() {
		return getSettingDefinition().getPath();
	}

	/**
	 * Get the type of this setting value, from its associated {@link SettingsDefinition}.
	 * @return the type of this setting value.
	 */
	@Transient
	public String getValueType() {
		return getSettingDefinition().getValueType();
	}

	/**
	 * Returns the hashcode of this SettingsValue.
	 * This implementation uses the Context and the encapsulated SettingsDefinition
	 * object to calculate the hashcode.
	 * @return the hashcode
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ObjectUtils.hashCode(this.context); 
		result = prime * result + ObjectUtils.hashCode(this.settingDefinition);
		return result;
	}

	/**
	 * Indicates whether the given Object is equals to this SettingsValue.
	 * This implementation considers the two SettingsValues to be equal if
	 * their Contexts are equal and their SettingsDefinitions are equal.
	 * @param other the object to which this SettingsValue should be compared
	 * @return true if this SettingsValue is equal to the given object
	 */
	@Override
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}
		
		if (!(other instanceof SettingValueImpl)) {
			return false;
		}
		
		SettingValueImpl setting = (SettingValueImpl) other;
		return ObjectUtils.equals(this.context, setting.context)
				&& ObjectUtils.equals(this.settingDefinition, setting.settingDefinition);
	}

	/**
	 * Hook to perform tasks before persisting - not used.
	 */
	public void executeBeforePersistAction() {
		// Do Nothing
	}
	
	/**
	 * True if the object has previously been persisted.
	 * @return true if the object has previously been persisted.
	 */
	@Transient
	public boolean isPersisted() {
		return getUidPk() > 0;
	}

	/**
	 * Get the setting's value as a boolean.
	 * @return the boolean value of the setting
	 */
	@Transient
	@SuppressWarnings("PMD.BooleanGetMethodName")
	public boolean getBooleanValue() {
		if ("true".equals(this.getValue())) {
			return true;
		}
		
		return false;
	}

	/**
	 * Set the setting's value with a boolean.
	 * @param value the boolean value to set
	 */
	public void setBooleanValue(final boolean value) {
		if (value) {
			this.setValue("true");
		} else {
			this.setValue("false");
		}
	}
	

	/**
	 * Get the setting's value as a integer.
	 * @return the integer value of the setting.
	 */
	@Transient
	@SuppressWarnings("PMD.IntegerGetMethodName")
	public int getIntegerValue() {		
		return NumberUtils.toInt(this.getValue());
	}

	/**
	 * Set the setting's value with a boolean.
	 * @param value the boolean value to set
	 */
	public void setIntegerValue(final int value) {
		this.setValue(String.valueOf(value));
	}

	
}
