/**
 * 
 */
package com.elasticpath.domain.changeset;

import java.io.Serializable;

import org.apache.commons.lang.ObjectUtils;

/**
 * @author epdev
 *
 */
public class ChangeSetUserView implements Serializable {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;
	
	private final String firstName;
	private final String lastName;
	private final String userName;
	private final String guid;
	
	/**
	 * Get user view.
	 * @return user name
	 */
	public String getUserName() {
		return userName;
	}
	
	/**
	 * ChangeSetUserView constructor.
	 * 
	 * @param firstName the user's first name
	 * @param lastName the user's last name
	 * @param userName the user's user name
	 * @param guid the user's guid
	 */
	public ChangeSetUserView(final String firstName, final String lastName, final String userName, final String guid) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.userName = userName;
		this.guid = guid;
	}
	
	/**
	 * Gets the first name of the user.
	 * 
	 * @return the user's first name
	 */
	public String getFirstName() {
		return firstName;
	}
	
	/**
	 * Gets the last name of the user.
	 * 
	 * @return the user's last name
	 */
	public String getLastName() {
		return lastName;
	}
	
	/**
	 * Gets the GUID of the user.
	 * 
	 * @return the user's GUID
	 */
	public String getGuid() {
		return guid;
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof ChangeSetUserView)) {
			return false;
		}
		ChangeSetUserView other = (ChangeSetUserView) obj;
		return ObjectUtils.equals(getGuid(), other.getGuid());
	}

	@Override
	public int hashCode() {
		return ObjectUtils.hashCode(getGuid());
	}
	
	
}
