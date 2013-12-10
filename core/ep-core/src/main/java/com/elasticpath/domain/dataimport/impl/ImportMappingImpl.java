package com.elasticpath.domain.dataimport.impl;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.apache.openjpa.persistence.DataCache;

import com.elasticpath.domain.dataimport.ImportMapping;
import com.elasticpath.persistence.api.AbstractPersistableImpl;

/**
 * A default implementation of <code>ImportMapping</code>.
 */
@Entity
@Table(name = ImportMappingImpl.TABLE_NAME)
@DataCache(enabled = false)
public class ImportMappingImpl extends AbstractPersistableImpl implements ImportMapping {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TIMPORTMAPPINGS";

	private Integer colNumer;

	private String name;

	private long uidPk;

	/**
	 * Returns the import column number.
	 *
	 * @return the import column number
	 */
	@Basic
	@Column(name = "COL_NUMBER")
	public Integer getColNumber() {
		return this.colNumer;
	}

	/**
	 * Returns the name of import field.
	 *
	 * @return the name of the import field.
	 */
	@Basic
	@Column(name = "IMPORT_FIELD_NAME", nullable = false)
	public String getName() {
		return this.name;
	}

	/**
	 * Sets the import column number.
	 *
	 * @param colNumber the import column number
	 */
	public void setColNumber(final Integer colNumber) {
		this.colNumer = colNumber;
	}

	/**
	 * Set the name of import field.
	 *
	 * @param name the name to set
	 */
	public void setName(final String name) {
		this.name = name;
	}

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

	/**
	 * Sets the unique identifier for this domain model object.
	 *
	 * @param uidPk the new unique identifier.
	 */
	public void setUidPk(final long uidPk) {
		this.uidPk = uidPk;
	}
}
