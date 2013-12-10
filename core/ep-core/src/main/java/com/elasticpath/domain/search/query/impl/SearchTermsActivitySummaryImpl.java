package com.elasticpath.domain.search.query.impl;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.openjpa.persistence.jdbc.ForeignKey;

import com.elasticpath.domain.impl.AbstractEpDomainImpl;
import com.elasticpath.domain.search.query.SearchTermsActivitySummary;
import com.elasticpath.domain.search.query.SearchTermsMemento;
import com.elasticpath.domain.search.query.SearchTermsMemento.SearchTermsId;

/**
 * Default implementation of {@link SearchTermsActivitySummary}.
 */
@Entity
@Table(name = SearchTermsActivitySummaryImpl.TABLE_NAME)
public class SearchTermsActivitySummaryImpl extends AbstractEpDomainImpl implements SearchTermsActivitySummary {

	/** Table name. */
	public static final String TABLE_NAME = "TSEARCHTERMSACTIVITYSUMMARY";
	private static final long serialVersionUID = 1L;
	private long uidPk;
	private SearchTermsMemento searchTerms;
	private Date lastAccessDate;
	private int searchCount;

	@Id
	@Column(name = "SEARCH_TERM_UID")
	protected long getUidPk() {
		return uidPk;
	}

	protected void setUidPk(final long uidPk) {
		this.uidPk = uidPk;
	}

	@Override
	@Transient
	public String getSearchTerms() {
		return searchTerms.getSearchTermsRepresentation();
	}

	@Override
	@Transient
	public SearchTermsId getSearchTermsId() {
		return searchTerms.getId();
	}

	@ManyToOne(targetEntity = SearchTermsMementoImpl.class, fetch = FetchType.EAGER, cascade = { CascadeType.REFRESH, CascadeType.MERGE })
	@JoinColumn(name = "SEARCH_TERM_UID", nullable = false)
	@ForeignKey(name = "SEARCHACTIVITY_TERM_FK")
	protected SearchTermsMemento getSearchTermsInternal() {
		return searchTerms;
	}

	protected void setSearchTermsInternal(final SearchTermsMemento searchTerms) {
		this.searchTerms = searchTerms;
	}

	@Override
	@Basic
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LAST_ACCESS_DATE", nullable = false)
	public Date getLastAccessDate() {
		return lastAccessDate;
	}

	protected void setLastAccessDate(final Date lastAccessDate) {
		this.lastAccessDate = lastAccessDate;
	}

	@Override
	@Basic
	@Column(name = "SEARCH_COUNT", nullable = false)
	public int getSearchCount() {
		return searchCount;
	}

	protected void setSearchCount(final int searchCount) {
		this.searchCount = searchCount;
	}
}
