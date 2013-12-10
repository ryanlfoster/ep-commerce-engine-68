package ${groupId}.domain;

import com.elasticpath.persistence.api.Entity;

/**
 * An example entity.  See {@code ExampleImpl} for more information.
 */
public interface Example extends Entity {

	/** 
	 * @return first name (cannot be null)
	 */
	String getFirstName();

	/** 
	 * @return last name (cannot be null)
	 */
	String getLastName();

	/** 
	 * @return username name (can be null)
	 */
	String getUserName();

	/** 
	 * @return test integer 
	 */
	int getTestId();

	/** @param firstName firstName */
	void setFirstName(String firstName);

	/** @param lastName lastName */
	void setLastName(String lastName);

	/** @param userName userName */
	void setUserName(String userName);

	/** @param testId id */
	void setTestId(int testId);
}
