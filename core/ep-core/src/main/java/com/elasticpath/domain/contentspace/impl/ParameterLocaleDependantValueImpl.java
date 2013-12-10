/**
 * 
 */
package com.elasticpath.domain.contentspace.impl;


import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.apache.openjpa.persistence.DataCache;
import org.apache.openjpa.persistence.jdbc.Unique;

import com.elasticpath.domain.contentspace.ParameterLocaleDependantValue;
import com.elasticpath.persistence.api.AbstractEntityImpl;

/**
 * 
 * Represents localized value for parameter.
 *
 */
@Entity
@Table(name = ParameterLocaleDependantValueImpl.TABLE_NAME)
@DataCache(enabled = false)
public class ParameterLocaleDependantValueImpl extends AbstractEntityImpl implements ParameterLocaleDependantValue {
	
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 20090112L;
	
	
	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TCSPARAMETERVALUELDF";	
	
	private static final int LOCALE_LENGTH = 20;
	
	private long uidPk;
	
	private String locale;
	
	private String value;


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
	@Column(name = "LOCALE", length = LOCALE_LENGTH, nullable = false)
	@Unique(name = "TCSPARAMETERVALUELDF_CK", columnNames = { "LOCALE", "LDVALUE" })
	public String getLocale() {
		return locale;
	}

	@Override
	@Basic
	@Column(name = "LDVALUE")
	@Unique(name = "TCSPARAMETERVALUELDF_CK", columnNames = { "LOCALE", "LDVALUE" })
	public String getValue() {
		return value;
	}

	@Override
	public void setLocale(final String locale) {
		this.locale = locale;
	}

	@Override
	public void setValue(final String value) {
		this.value = value;
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
     * @return the guid.
     */
    @Override
    @Basic
    @Column(name = "GUID")
    @Unique(name = "TCSPARAMETERVALUELDF_UNIQUE")
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
    public String toString() {
    	StringBuilder string = new StringBuilder();
    	string.append("PLDValue [");
    	string.append(getLocale());
    	string.append(", ");
    	string.append(getValue());
    	string.append(']');
    	return string.toString();
    }
	

}
