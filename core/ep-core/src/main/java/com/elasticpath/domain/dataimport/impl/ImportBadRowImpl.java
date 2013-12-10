package com.elasticpath.domain.dataimport.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import org.apache.openjpa.persistence.DataCache;
import org.apache.openjpa.persistence.jdbc.ElementForeignKey;
import org.apache.openjpa.persistence.jdbc.ElementJoinColumn;

import com.elasticpath.domain.dataimport.ImportBadRow;
import com.elasticpath.domain.dataimport.ImportFault;
import com.elasticpath.domain.impl.AbstractLegacyPersistenceImpl;

/**
 * Represents an import bad row.
 */
@Entity
@Table(name = ImportBadRowImpl.TABLE_NAME)
@DataCache(enabled = false)
public class ImportBadRowImpl extends AbstractLegacyPersistenceImpl implements ImportBadRow {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	/** Database Table. */
	protected static final String TABLE_NAME = "TIMPORTBADROW";
	
	private int rowNumber;

	private String row;

	private List<ImportFault> importFaults = new ArrayList<ImportFault>();

	private final List<String> importErrors = new ArrayList<String>();

	private long uidPk;
	
	/**
	 * Returns the row number that caused error.
	 * 
	 * @return the row number that caused error
	 */
	@Basic
	@Column(name = "ROW_NUMBER")
	public int getRowNumber() {
		return this.rowNumber;
	}

	/**
	 * Set the row number that caused error.
	 * 
	 * @param rowNumber the row number that caused error
	 */
	public void setRowNumber(final int rowNumber) {
		this.rowNumber = rowNumber;
	}

	/**
	 * Returns the row that caused error.
	 * 
	 * @return the row that caused error
	 */
	@Lob
	@Column(name = "ROW_DATA", nullable = false)
	public String getRow() {
		return this.row;
	}

	/**
	 * Set the row.
	 * 
	 * @param row the row to set
	 */
	public void setRow(final String row) {
		this.row = row;
	}

	/**
	 * Returns a list of error message.
	 * 
	 * @return a list of error message
	 */
	@OneToMany(targetEntity = ImportFaultImpl.class, cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	@ElementJoinColumn(name = "IMPORT_BAD_ROW_UID", nullable = false)
	@ElementForeignKey
	public List<ImportFault> getImportFaults() {
		return this.importFaults;
	}

	/**
	 * Sets a list of error messages.
	 * 
	 * @param importFaults the list of error messages to set
	 */
	public void setImportFaults(final List<ImportFault> importFaults) {
		this.importFaults = importFaults;
	}

	/**
	 * Add the error message to the list.
	 * 
	 * @param importFault the error message
	 */
	public void addImportFault(final ImportFault importFault) {
		this.getImportFaults().add(importFault);
	}

	@Override
	@Transient
	public List<String> getImportErrors() {
		return getImportErrors(Locale.getDefault());
	}
	
	/**
	 * Returns a list of error messages.
	 * 
	 * @param locale of the error messages
	 * @return a list of error messages
	 */
	@Transient
	public List<String> getImportErrors(final Locale locale) {
		if (importErrors.size() != getImportFaults().size()) {
			final List<ImportFault> importFaults = getImportFaults();
			for (final ImportFault importFault : importFaults) {
				final String errorMessage = getElasticPath().getMessage(importFault.getCode(), importFault.getArgs(),
						importFault.getCode(), locale);
				importErrors.add(errorMessage);
			}
		}
		return importErrors;
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
	public void addImportFaults(final List<ImportFault> faults) {
		getImportFaults().addAll(faults);
	}
}
