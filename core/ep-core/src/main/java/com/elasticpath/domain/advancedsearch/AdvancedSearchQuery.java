package com.elasticpath.domain.advancedsearch;

import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.persistence.api.Persistable;

/**
 * Interface that represents methods for advanced search query object.
 */
public interface AdvancedSearchQuery extends Persistable {

	/**
	 * Gets the query id.
	 * 
	 * @return the queryId
	 */
	long getQueryId();

	/**
	 * Sets the query id.
	 * 
	 * @param queryId the queryId to set
	 */
	void setQueryId(final long queryId);

	/**
	 * Gets the query name.
	 * 
	 * @return the name
	 */
	String getName();

	/**
	 * Sets the query name.
	 * 
	 * @param name the name to set
	 */
	void setName(final String name);

	/**
	 * Gets the query description.
	 * 
	 * @return the description
	 */
	String getDescription();

	/**
	 * Sets the query description.
	 * 
	 * @param description the description to set
	 */
	void setDescription(final String description);

	/**
	 * Gets the visibility of query.
	 * 
	 * @return the queryVisibility
	 */
	QueryVisibility getQueryVisibility();

	/**
	 * Sets the visibility of query.
	 * 
	 * @param queryVisibility the queryVisibility to set
	 */
	void setQueryVisibility(final QueryVisibility queryVisibility);

	/**
	 * Gets the query owner.
	 * 
	 * @return the owner
	 */
	CmUser getOwner();

	/**
	 * Sets the query owner.
	 * 
	 * @param owner the owner to set
	 */
	void setOwner(final CmUser owner);

	/**
	 * Gets the query type.
	 * 
	 * @return the queryType
	 */
	AdvancedQueryType getQueryType();

	/**
	 * Sets the query type.
	 * 
	 * @param queryType the queryType to set
	 */
	void setQueryType(final AdvancedQueryType queryType);

	/**
	 * Gets the query in EPQL format.
	 * 
	 * @return the query
	 */
	String getQueryContent();

	/**
	 * Sets the query in EPQL format.
	 * 
	 * @param query the query to set
	 */
	void setQueryContent(final String query);
	
	/**
	 * Populates this object from another instance.
	 * 
	 * @param query the source query
	 */
	void populateFrom(final AdvancedSearchQuery query);

}