package com.elasticpath.domain.advancedsearch.impl;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.apache.openjpa.persistence.DataCache;

import com.elasticpath.commons.constants.GlobalConstants;
import com.elasticpath.domain.advancedsearch.AdvancedQueryType;
import com.elasticpath.domain.advancedsearch.AdvancedSearchQuery;
import com.elasticpath.domain.advancedsearch.QueryVisibility;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.cmuser.impl.CmUserImpl;
import com.elasticpath.persistence.api.AbstractPersistableImpl;

/**
 * Represents information about advanced search query.
 */
@Entity
@Table(name = AdvancedSearchQueryImpl.TABLE_NAME)
@DataCache(enabled = false)
public class AdvancedSearchQueryImpl extends AbstractPersistableImpl implements AdvancedSearchQuery {
	
	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TADVANCEDSEARCHQUERY";
	
	/**
	 * The name of the generator for query id.
	 */
	public static final String QUERY_ID = "TADVANCEDSEARCHQUERYID";

	private long uidPk;
		
	private long queryId;
	
	private String name;
	
	private String description; 
	
	private QueryVisibility queryVisibility;
	
	private CmUser owner;
	
	private AdvancedQueryType queryType;
	
	private String queryContent;

	/**
	 * Gets the unique identifier for this domain model object.
	 * @return the unique identifier.
	 */
	@Id
	@Column(name = "UIDPK")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
	@TableGenerator(name = TABLE_NAME, table = "JPA_GENERATED_KEYS",
					pkColumnName = "ID", valueColumnName = "LAST_VALUE", pkColumnValue = TABLE_NAME)
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
	 * Gets the query id.
	 * 
	 * @return the queryId
	 */
	@Column(name = "QUERY_ID")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = QUERY_ID)
	@TableGenerator(name = QUERY_ID, table = "JPA_GENERATED_KEYS",
					pkColumnName = "ID", valueColumnName = "LAST_VALUE", pkColumnValue = QUERY_ID)
	public long getQueryId() {
		return queryId;
	}

	/**
	 * Sets the query id.
	 * 
	 * @param queryId the queryId to set
	 */
	public void setQueryId(final long queryId) {
		this.queryId = queryId;
	}

	/**
	 * Gets the query name.
	 * 
	 * @return the name
	 */
	@Basic(optional = false)
	@Column(name = "NAME")
	@OrderBy
	public String getName() {
		return name;
	}
	
	/**
	 * Sets the query name.
	 * 
	 * @param name the name to set
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * Gets the query description.
	 * 
	 * @return the description
	 */
	@Lob
	@Column(name = "DESCRIPTION", length = GlobalConstants.LONG_TEXT_MAX_LENGTH)
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the query description.
	 * 
	 * @param description the description to set
	 */
	public void setDescription(final String description) {
		this.description = description;
	}

	/**
	 * Gets the visibility of query.
	 * 
	 * @return the queryVisibility
	 */
	@Basic(optional = false)
	@Column(name = "QUERY_VISIBILITY")
	@Enumerated(EnumType.STRING)
	public QueryVisibility getQueryVisibility() {
		return queryVisibility;
	}

	/**
	 * Sets the visibility of query.
	 * 
	 * @param queryVisibility the queryVisibility to set
	 */
	public void setQueryVisibility(final QueryVisibility queryVisibility) {
		this.queryVisibility = queryVisibility;
	}

	/**
	 * Gets the query owner.
	 * 
	 * @return the owner
	 */
	@ManyToOne(targetEntity = CmUserImpl.class, cascade = CascadeType.REFRESH)
	@JoinColumn(name = "OWNER_ID")
	public CmUser getOwner() {
		return owner;
	}

	/**
	 * Sets the query owner.
	 * 
	 * @param owner the owner to set
	 */
	public void setOwner(final CmUser owner) {
		this.owner = owner;
	}

	/**
	 * Gets the query type.
	 * 
	 * @return the queryType
	 */
	@Basic(optional = false)
	@Column(name = "QUERY_TYPE")
	@Enumerated(EnumType.STRING)
	public AdvancedQueryType getQueryType() {
		return queryType;
	}

	/**
	 * Sets the query type.
	 * 
	 * @param queryType the queryType to set
	 */
	public void setQueryType(final AdvancedQueryType queryType) {
		this.queryType = queryType;
	}

	/**
	 * Gets the query in EPQL format.
	 * 
	 * @return the query
	 */
	@Lob
	@Column(name = "QUERY_CONTENT", length = GlobalConstants.LONG_TEXT_MAX_LENGTH)
	public String getQueryContent() {
		return queryContent;
	}

	/**
	 * Sets the query in EPQL format.
	 * 
	 * @param queryContent the query to set
	 */
	public void setQueryContent(final String queryContent) {
		this.queryContent = queryContent;
	}
	
	/**
	 * Populates this object from another instance.
	 * 
	 * @param query the source query
	 */
	public void populateFrom(final AdvancedSearchQuery query) {
		setName(query.getName());
		setDescription(query.getDescription());
		setOwner(query.getOwner());
		setQueryContent(query.getQueryContent());
		setQueryType(query.getQueryType());
		setQueryVisibility(query.getQueryVisibility());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		final int shift = 32;
		int result = 1;
		if (isPersisted()) {
			result = prime * result + (int) (queryId ^ (queryId >>> shift));
		}
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof AdvancedSearchQueryImpl)) {
			return false;
		}
		
		final AdvancedSearchQueryImpl other = (AdvancedSearchQueryImpl) obj;
		if (isPersisted() && other.isPersisted() && queryId == other.queryId) {
			return true;
		}
		
		return false;
	}

}
