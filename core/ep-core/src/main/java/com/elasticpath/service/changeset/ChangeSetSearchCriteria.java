package com.elasticpath.service.changeset;

import java.io.Serializable;

import com.elasticpath.domain.changeset.ChangeSetStateCode;
import com.elasticpath.service.search.AbstractSearchCriteriaImpl;
import com.elasticpath.service.search.IndexType;

/**
 * The search criteria for change set.
 */
public class ChangeSetSearchCriteria extends AbstractSearchCriteriaImpl implements Serializable {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	private String userGuid;

	private ChangeSetStateCode changeSetStateCode;

	private String changeSetName;

	private String assignedUserName;

	private boolean strictName = false;

	/**
	 * @return the strictName
	 */
	public boolean isStrictName() {
		return strictName;
	}

	/**
	 * Set search parameter for name attribute in order to have ability to find change set by strict name or use like query for name attribute. 
	 * @param strictName the set true if name should be strict and false if name should be used in query as "%strictName%"  
	 */
	public void setStrictName(final boolean strictName) {
		this.strictName = strictName;
	}

	/**
	 * Get Change Set State Code.
	 * 
	 * @return the change set state code
	 */
	public ChangeSetStateCode getChangeSetStateCode() {
		return changeSetStateCode;
	}

	/**
	 * Set Change Set State Code.
	 * 
	 * @param changeSetStateCode the change set state code
	 */
	public void setChangeSetStateCode(final ChangeSetStateCode changeSetStateCode) {
		this.changeSetStateCode = changeSetStateCode;
	}

	/**
	 * Get user Guid which the change set was assigned to.
	 * 
	 * @return user guid
	 */
	public String getUserGuid() {
		return userGuid;
	}

	/**
	 * Set user guid.
	 * 
	 * @param userGuid the user guid
	 */
	public void setUserGuid(final String userGuid) {
		this.userGuid = userGuid;
	}

	/**
	 * Get the change set name.
	 * 
	 * @return the change set name
	 */
	public String getChangeSetName() {
		return changeSetName;
	}

	/**
	 * Set the change set name.
	 * 
	 * @param changeSetName the change set name
	 */
	public void setChangeSetName(final String changeSetName) {
		this.changeSetName = changeSetName;
	}

	/**
	 * Get the assigned user name.
	 * 
	 * @return get the assigned user name
	 */
	public String getAssignedUserName() {
		return assignedUserName;
	}

	/**
	 * Set the assigned user name.
	 * 
	 * @param assignedUserName the assigned user name
	 */
	public void setAssignedUserName(final String assignedUserName) {
		this.assignedUserName = assignedUserName;
	}

	/**
	 * Index is not used by this search.
	 * 
	 * @return null
	 */
	public IndexType getIndexType() {
		return null;
	}

	@Override
	public void optimize() {
		// TODO optimize by validating values and setting to null if not valid.
	}

}
