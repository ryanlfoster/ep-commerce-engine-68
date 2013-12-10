/*
 * Copyright (c) Elastic Path Software Inc., 2012
 */
package com.elasticpath.domain.search.query;

import org.apache.commons.lang.ObjectUtils;

import com.elasticpath.persistence.api.Entity;

/**
 * This Class represents a memento for a SearchTerms object.
 * It has an id and a representation of the SearchTerms object so that it can be persisted.
 */
public interface SearchTermsMemento extends Entity {

	/**
	 * @return The SearchTerms representation.
	 */
	String getSearchTermsRepresentation();

	/**
	 * @param representation The SearchTerms representation.
	 */
	void setSearchTermsRepresentation(String representation);

	/**
	 * @return The identifier for the SearchTerms.
	 */
	SearchTermsId getId();

	/**
	 * @param mementoId The identifier for the SearchTerms.
	 */
	void setId(SearchTermsId mementoId);

	/**
	 * Represents the identifier of a SearchTerms.
	 */
	class SearchTermsId {
		
		private final String value;

		/**
		 * Instantiates a new SearchTerms ID.
		 *
		 * @param value The identifier.
		 */
		public SearchTermsId(final String value) {
			this.value = value;
		}

		/**
		 * @return The identifier value.
		 */
		public String getValue() {
			return value;
		}

		@Override
		public boolean equals(final Object obj) {
			if (obj instanceof SearchTermsId) {
				return ObjectUtils.equals(value, ((SearchTermsId) obj).value);
			}
			return false;
		}
		
		@Override
		public int hashCode() {
			return value.hashCode();
		}

	}

}
