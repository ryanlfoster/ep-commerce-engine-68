package com.elasticpath.domain.search.impl;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.openjpa.persistence.DataCache;

import com.elasticpath.domain.search.SfSearchLog;
import com.elasticpath.persistence.api.AbstractPersistableImpl;

/**
 * The default implementation of SfSearchLog.
 */
@Entity
@Table(name = SfSearchLogImpl.TABLE_NAME)
@DataCache(enabled = false)
public class SfSearchLogImpl extends AbstractPersistableImpl implements SfSearchLog {
	
	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TSFSEARCHLOG";

	private long categoryRestriction;

	private String keywords;

	private int resultCount;

	private Date searchTime;

	private boolean suggestionsGenerated;

	private long uidPk;

	/**
	 * Gets the category restriction for this search.
	 * 
	 * @return the category restriction for this search
	 */
	@Basic
	@Column(name = "CATEGORY_RESTRICTION")
	public long getCategoryRestriction() {
		return this.categoryRestriction;
	}

	/**
	 * Gets the keywords that were used to search.
	 * 
	 * @return the keywords that were used to search
	 */
	@Basic
	@Column(name = "KEYWORDS")
	public String getKeywords() {
		return this.keywords;
	}

	/**
	 * Gets the number of results returned by the search.
	 * 
	 * @return the number of results returned by the search
	 */
	@Basic
	@Column(name = "RESULT_COUNT")
	public int getResultCount() {
		return this.resultCount;
	}

	/**
	 * Returns the date that this search was executed.
	 * 
	 * @return the date this search was executed
	 */
	@Basic
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "SEARCH_TIME", nullable = false)
	public Date getSearchTime() {
		return this.searchTime;
	}

	/**
	 * Gets whether or not suggestions were generated for this search.
	 * 
	 * @return whether or not suggestions were generated for this search
	 */
	@Basic
	@Column(name = "SUGGESTIONS_GENERATED")
	@SuppressWarnings("PMD.BooleanGetMethodName")
	public boolean getSuggestionsGenerated() {
		return this.suggestionsGenerated;
	}

	/**
	 * Sets the category restriction for this search.
	 * 
	 * @param categoryRestriction the category restriction for this search
	 */
	public void setCategoryRestriction(final long categoryRestriction) {
		this.categoryRestriction = categoryRestriction;
	}

	/**
	 * Sets the keywords that were used to search.
	 * 
	 * @param keywords the keywords that were used to search
	 */
	public void setKeywords(final String keywords) {
		this.keywords = keywords;
	}

	/**
	 * Sets the number of results returned by the search.
	 * 
	 * @param resultCount the number of results returned by the search
	 */
	public void setResultCount(final int resultCount) {
		this.resultCount = resultCount;
	}

	/**
	 * Sets the date that this search was executed.
	 * 
	 * @param searchTime the date this search was executed
	 */
	public void setSearchTime(final Date searchTime) {
		this.searchTime = searchTime;
	}

	/**
	 * Sets whether or not suggestions were generated for this search.
	 * 
	 * @param suggestionsGenerated whether or not suggestions were generated for this search
	 */
	public void setSuggestionsGenerated(final boolean suggestionsGenerated) {
		this.suggestionsGenerated = suggestionsGenerated;
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
