package com.elasticpath.domain.search.impl;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.apache.openjpa.persistence.DataCache;

import com.elasticpath.commons.constants.GlobalConstants;
import com.elasticpath.domain.search.Synonym;
import com.elasticpath.persistence.api.AbstractPersistableImpl;

/**
 * Default implementation of {@link Synonym}.
 */
@Entity
@Table(name = SynonymImpl.TABLE_NAME)
@DataCache(enabled = false)
public class SynonymImpl extends AbstractPersistableImpl implements Synonym {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	/** The name of the table & generator to use for persistence.*/
	public static final String TABLE_NAME = "TSYNONYM";

	private String synonym;

	private long uidPk;

	/**
	 * Gets the synonym.
	 * 
	 * @return the synonym
	 */
	@Basic(optional = false)
	@Column(name = "SYNONYM_WORD", length = GlobalConstants.SHORT_TEXT_MAX_LENGTH)
	public String getSynonym() {
		return synonym;
	}

	/**
	 * Sets the synonym.
	 * 
	 * @param synonym the synonym
	 */
	public void setSynonym(final String synonym) {
		if (synonym == null) {
			this.synonym = synonym;
		} else {
			// don't care about whitespace
			this.synonym = synonym.trim();
		}
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
	 * Compares a synonym with another. Sorts nulls lower.
	 * 
	 * @param other the other synonym to compare to
	 * @return an integer specified whether the other synonym is higher, lower or equal to this
	 *         synonym
	 */
	public int compareTo(final Synonym other) {
		if (getSynonym() == null && other.getSynonym() == null) {
			return 0;
		} else if (getSynonym() == null) {
			return Integer.MIN_VALUE;
		} else if (other.getSynonym() == null) {
			return Integer.MAX_VALUE;
		}
		return getSynonym().compareTo(other.getSynonym());
	}

	@Override
	public String toString() {
		return getSynonym();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		final int shift = 32;
		int result = 1;

		if (synonym == null) {
			if (isPersisted()) {
				return prime * result + (int) (uidPk ^ (uidPk >>> shift));
			}
			return prime;
		}
		return synonym.hashCode();
	}

	@Override
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}
		if (other == null) {
			return false;
		}

		if (other instanceof String) {
			return synonym.equals(other);
		}

		if (!(other instanceof SynonymImpl)) {
			return false;
		}
		final SynonymImpl otherSynonym = (SynonymImpl) other;

		if (synonym == null) {
			return otherSynonym.synonym == null;
		}
		return synonym.equals(otherSynonym.synonym);
	}
}
