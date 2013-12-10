package com.elasticpath.test.persister;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.cmuser.UserRole;
import com.elasticpath.domain.cmuser.impl.CmUserImpl;
import com.elasticpath.service.cmuser.CmUserService;
import com.elasticpath.service.cmuser.UserRoleService;

/**
 * Utility class for testing, to facilitate persisting CmUsers and their associated Roles. 
 * Typically used by Import/Export Fixture code. 
 */
public class CmUserTestPersister {

	private static final String DELIM_COMMA = ",";
	private static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd hh:mm:ss";
	
	private final BeanFactory beanFactory;
	private final CmUserService cmUserService;
	private final UserRoleService userRoleService;
	
	/**
	 * Initialize the UserRoleService and the CmUserService.
	 * @param beanFactory beanFactory
	 */
	public CmUserTestPersister(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
		cmUserService = beanFactory.getBean(
				ContextIdNames.CMUSER_SERVICE);

		userRoleService = beanFactory.getBean(
				ContextIdNames.USER_ROLE_SERVICE);
	}
	
	/**
	 * Deletes all CmUser records from the (in-memory) database.
	 */
	public void deleteAllCmUsers() {
		List<CmUser> all = cmUserService.list();
		for (CmUser cmUser : all) {
			cmUserService.remove(cmUser);
		}
	}
	
	/**
	 * Create a CmUser in the database, with associated UserRoles if appropriate.
	 * 
	 * UserRole(s) are not persisted automatically when persisting a CmUser. So each name in passed in userRoleNames
	 * value (comma separated String of names) will be checked to see if it exists. 
	 * A new UserRole will be created for any that don't exist.
	 * UserRoles are then added to the newly created CmUser.
	 * 
	 * Calls CmUserImpl.setDefaultValues to initialize the following fields: creationDate to current date and 
	 * Collections of Store, Warehouse, UserRole and Catalog to new HashSet.
	 * 
	 * @param guid guid
	 * @param userName userName
	 * @param email email
	 * @param firstName firstName
	 * @param lastName lastName
	 * @param password password
	 * @param creationDate creationDate must be of format of yyyy-MM-dd hh:mm:ss
	 * @param userRoleNames a comma separated String
	 * @return the CmUser with a Collection of any associated UserRole(s)
	 * @throws ParseException if the creationDate can not be parsed
	 * 
	 * @see CmUserImpl
	 */
	public CmUser createCmUser(final String guid, final String userName,
			final String email, final String firstName, final String lastName,
			final String password, final String creationDate, final String userRoleNames)
			throws ParseException {
	
		CmUser cmUser = beanFactory.getBean(ContextIdNames.CMUSER);
		cmUser.initialize();
		cmUser.setGuid(guid);
		cmUser.setEmail(email);
		cmUser.setUserName(userName);
		cmUser.setFirstName(firstName);
		cmUser.setLastName(lastName);
		cmUser.setPassword(password);
		
		//default value for CreationDate is new Date(). so don't allow 'null'
		if (StringUtils.isNotBlank(creationDate)) {
			cmUser.setCreationDate(new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS, Locale.CANADA)
				.parse(creationDate));
		}
		
		//userRoleNames is a comma separated String
		if (userRoleNames != null) {
			String [] userRoleNamesArray = StringUtils.split(userRoleNames, DELIM_COMMA);		
			
			for (String currentUserRoleName : userRoleNamesArray) {
				if (currentUserRoleName != null) {
					//check to see if the currentUserRoleName exists
					//persist it first if not
					//in either case then add it to the current CmUser 
					UserRole existingRole = userRoleService.findByName(currentUserRoleName);
					if (existingRole == null) {
						UserRole userRole = beanFactory.getBean(ContextIdNames.USER_ROLE);
						userRole.initialize();
						userRole.setName(currentUserRoleName);
						userRoleService.add(userRole);
						cmUser.addUserRole(userRole);	
					} else {
						cmUser.addUserRole(existingRole);
					}
				}
			}
			
		}
		return cmUserService.add(cmUser);
	}

}
