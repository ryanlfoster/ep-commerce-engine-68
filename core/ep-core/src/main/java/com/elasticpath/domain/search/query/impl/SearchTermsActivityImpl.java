package com.elasticpath.domain.search.query.impl;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.openjpa.persistence.jdbc.ForeignKey;

import com.elasticpath.domain.search.query.SearchTermsActivity;
import com.elasticpath.domain.search.query.SearchTermsMemento;
import com.elasticpath.persistence.api.AbstractPersistableImpl;

/**
 * Describes activity for {@link SearchTermsMemento}.
 */
@Entity
@Table(name = SearchTermsActivityImpl.TABLE_NAME)
public class SearchTermsActivityImpl extends AbstractPersistableImpl implements SearchTermsActivity {

	/** Table name. */
	public static final String TABLE_NAME = "TSEARCHTERMSACTIVITY";
	private static final long serialVersionUID = 1;
	private long uidPk;
	private SearchTermsMemento searchTerms;
	private Date lastAccessDate;

	@Override
	@Id
	@Column(name = "UIDPK")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
	@TableGenerator(name = TABLE_NAME, table = "JPA_GENERATED_KEYS", pkColumnName = "ID", valueColumnName = "LAST_VALUE",
			pkColumnValue = TABLE_NAME)
	public long getUidPk() {
		return uidPk;
	}

	@Override
	public void setUidPk(final long uidPk) {
		this.uidPk = uidPk;
	}

	@Override
	@ManyToOne(targetEntity = SearchTermsMementoImpl.class, fetch = FetchType.EAGER, cascade = { })
	@JoinColumn(name = "SEARCH_TERM_UID", nullable = false)
	@ForeignKey(name = "SEARCHACTIVITY_TERM_FK")
	public SearchTermsMemento getSearchTerms() {
		return searchTerms;
	}

	@Override
	public void setSearchTerms(final SearchTermsMemento searchTerms) {
		this.searchTerms = searchTerms;
	}

	@Override
	@Basic
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LAST_ACCESSED", nullable = false)
	public Date getLastAccessDate() {
		return lastAccessDate;
	}

	@Override
	public void setLastAccessDate(final Date lastAccessDate) {
		this.lastAccessDate = lastAccessDate;
	}
}
