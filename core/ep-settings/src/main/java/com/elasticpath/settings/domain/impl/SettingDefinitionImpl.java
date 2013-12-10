package com.elasticpath.settings.domain.impl;

import com.elasticpath.settings.domain.SettingDefinition;
import com.elasticpath.settings.domain.SettingMetadata;
import org.apache.commons.lang.ObjectUtils;
import org.apache.openjpa.persistence.DataCache;
import org.apache.openjpa.persistence.ElementDependent;
import org.apache.openjpa.persistence.Externalizer;
import org.apache.openjpa.persistence.Factory;
import org.apache.openjpa.persistence.jdbc.ElementForeignKey;
import org.apache.openjpa.persistence.jdbc.ElementJoinColumn;
import org.apache.openjpa.persistence.jdbc.Unique;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import java.util.Date;
import java.util.Map;

/**
 * JPA-persistence-enabled implementation of the <code>SettingsDefinition</code> interface.
 */
@Entity
@Table(name = SettingDefinitionImpl.TABLE_NAME, uniqueConstraints = @UniqueConstraint(columnNames = "PATH"))
@DataCache(enabled = false)
public class SettingDefinitionImpl implements SettingDefinition {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 6000000001L;
	
	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TSETTINGDEFINITION";
	
	private String path;
	
	private String defaultValue;
	
	private String valueType;
	
	private long uidPk;
	
	private int maxOverrideValues;
	
	private String description;
	
	private Map<String, SettingMetadata> metadata;

	private Date lastModifiedDate;
	
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
	 * @return the path
	 */
	@Basic
	@Unique
	@Column(name = "PATH", unique = true, nullable = false)
	public String getPath() {
		return path;
	}
	
	/**
	 * 
	 * @param path the path to set
	 */
	public void setPath(final String path) {
		this.path = path;
	}

	/**
	 * <p>This implementation is annotated with OpenJPA annotations for persistence purposes.
	 * Of particular note are the two annotations:
	 * <ul><li>{@code @Externalizer("com.elasticpath.persistence.api.StringExternalizer.toExternalForm")}</li>
	 * <li>{@code @Factory("com.elasticpath.persistence.api.StringExternalizer.toInternalForm")}</li></ul>
	 * which ensure that even if the underlying database (e.g. Oracle) converts empty strings to null values,
	 * JPA won't complain.</p>
	 * @return the defaultValue
	 */
	@Basic
	@Column(name = "DEFAULT_VALUE")
	@Externalizer("com.elasticpath.persistence.api.StringExternalizer.toExternalForm")
	@Factory("com.elasticpath.persistence.api.StringExternalizer.toInternalForm")
	public String getDefaultValue() {
		return defaultValue;
	}
	
	/**
	 * @param defaultValue the default value to set
	 */
	public void setDefaultValue(final String defaultValue) {
		this.defaultValue = defaultValue;
	}

	/**
	 * @return the value type
	 */
	@Basic
	@Column(name = "VALUE_TYPE")
	public String getValueType() {
		return valueType;
	}
	
	/**
	 * 
	 * @param valueType the value type to set
	 */
	public void setValueType(final String valueType) {
		this.valueType = valueType;
	}

	/**
	 * @param maxOverrideValues maxOverrideValues to an <code>integer</code> value, if negative one then there are
	 * an unbound number of overrides possible
	 * 
	 */
	public void setMaxOverrideValues(final int maxOverrideValues) {
		this.maxOverrideValues = maxOverrideValues;
	}
	
	/**
	 * @return the number of overrides that the setting definition's value may have, if negative one is
	 * returned that is an unbounded number of overrides
	 */
	@Basic
	@Column(name = "MAX_OVERRIDE_VALUES")
	public int getMaxOverrideValues() {
		return this.maxOverrideValues;
	}

	/**
	 * @return the non-localized description of this Setting.
	 */
	@Basic
	@Column(name = "DESCRIPTION")
	public String getDescription() {
		return this.description;
	}
	
	/**
	 * @param description the non-localized description of this Setting.
	 */
	public void setDescription(final String description) {
		this.description = description;
	}
	
	/**
	 * Get the date at which this SettingsValue was last modified.
	 * @return the last modified date
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Version
	@Column(name = "LAST_MODIFIED_DATE", nullable = false)
	public Date getLastModifiedDate() {
		return this.lastModifiedDate;
	}

	/**
	 * Set the date when the SettingsValue was last modified.
	 * This method is only used by JPA.
	 *
	 * @param lastModifiedDate the date when the SettingsValue was last modified
	 */
	protected void setLastModifiedDate(final Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	/**
	 * Returns a hash code value for this SettingsDefinition.
	 * This implementation returns the hashcode of the Path.
	 * @return that hash code
	 */
	@Override
	public int hashCode() {
		return ObjectUtils.hashCode(this.path);
	}

	/**
	 * Indicates whether the given SettingsDefinition is equal to this one.
	 * This implementation considers that two SettingsDefinition objects are 
	 * equal if they have the same Path.
	 * @param other the object to which this SettingsDefinition should be compared
	 * @return true if this SettingsDefinition equals the given object
	 */
	@Override
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}
		
		if (!(other instanceof SettingDefinitionImpl)) {
			return false;
		}
		
		SettingDefinitionImpl setting = (SettingDefinitionImpl) other;
		return ObjectUtils.equals(this.path, setting.path);
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
	 * @return the String representation of the setting definition.
	 */
	@Override
	public String toString() {
		return "Setting Definition:\n"
				+ "uidPk=" + getUidPk() + "\n"
				+ "path=" + getPath() + "\n"
				+ "defaultValue=" + getDefaultValue() + "\n"
				+ "valueType=" + getValueType() + "\n"
				+ "maxOverrideValues" + getMaxOverrideValues() + "\n"
				+ "metadata: " + getMetadata().toString(); 
	}

	/**
	 * Compares the path of this SettingDefinition with the path
	 * of the given settingDefinition. Returns 
	 * a negative integer, zero, or a positive integer as this
	 * SettingDefinition's path is less than, equal to, or greater than the
	 * path of the given SettingDefinition.
	 * @param otherSettingDefinition the SettingDefinition to compare with this one
	 * @return a negative integer, zero, or a positive integer as the path of this SettingDefinition
	 * is less than, equal to, or greater than the specified SettingDefinition.fin 
	 */
	public int compareTo(final SettingDefinition otherSettingDefinition) {
		return this.getPath().compareTo(otherSettingDefinition.getPath());
	}

	
	/**
	 * Get the metadata of this setting definition.
	 * 
	 * @return a map of setting metadata key to setting metadata.
	 */
	@OneToMany(targetEntity = SettingMetadataImpl.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@MapKey(name = "key")	
	@ElementJoinColumn(name = "SETTING_DEFINITION_UID", nullable = false)
	@ElementForeignKey
	@ElementDependent
	public Map <String, SettingMetadata> getMetadata() {
		return metadata;
	}

	/**
	 * Set the collection of metadata for this setting definition.
	 * @param metadata the set of setting metadata
	 */
	public void setMetadata(final Map <String, SettingMetadata> metadata) {
		this.metadata = metadata;
	}
}
