/*
 * Copyright (c) Elastic Path Software Inc., 2012
 */
package com.elasticpath.domain.search.query.impl;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import org.apache.openjpa.persistence.DataCache;

import com.elasticpath.domain.search.query.SearchTermsMemento;
import com.elasticpath.persistence.api.AbstractEntityImpl;

/**
 * Default implementation of {@link SearchTermsMemento}.
 */
@Entity
@Table(name = SearchTermsMementoImpl.TABLE_NAME)
@DataCache(enabled = true)
public class SearchTermsMementoImpl extends AbstractEntityImpl implements SearchTermsMemento {

	private static final long serialVersionUID = 1L;

	/** Allocation size for JPA_GENERATED_KEYS id. */
	private static final int ALLOCATION_SIZE = 1000;

	/** The table name. */
	public static final String TABLE_NAME = "TSEARCHTERMS";

	private String representation;

	private long uidPk;

	private String guid;

	/**
	 * @return The SearchTermsId (uses the GUID field).
	 */
	@Override
	@Transient
	public SearchTermsId getId() {
		return new SearchTermsId(getGuid());
	}

	/**
	 * Sets the id (uses the GUID field).
	 * 
	 * @param searchTermsId the new id
	 */
	@Override
	public void setId(final SearchTermsId searchTermsId) {
		setGuid(searchTermsId.getValue());
	}

	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(name = "SEARCH_TERMS")
	@Override
	public String getSearchTermsRepresentation() {
		return representation;
	}

	@Override
	public void setSearchTermsRepresentation(final String representation) {
		this.representation = representation;
	}

	@Override
	@Id
	@Column(name = "UIDPK")
	@GeneratedValue(
			strategy = GenerationType.TABLE,
			generator = TABLE_NAME)
	@TableGenerator(
			name = TABLE_NAME,
			table = "JPA_GENERATED_KEYS",
			pkColumnName = "ID",
			valueColumnName = "LAST_VALUE",
			pkColumnValue = TABLE_NAME,
			allocationSize = ALLOCATION_SIZE)
	public long getUidPk() {
		return uidPk;
	}

	@Override
	public void setUidPk(final long uidPk) {
		this.uidPk = uidPk;
	}

	@Basic
	@Column(name = "GUID")
	@Override
	public String getGuid() {
		return guid;
	}

	/**
	 * Necessary for OpenJPA.
	 * 
	 * @param guid The guid.
	 */
	@Override
	public void setGuid(final String guid) {
		this.guid = guid;
	}

}
