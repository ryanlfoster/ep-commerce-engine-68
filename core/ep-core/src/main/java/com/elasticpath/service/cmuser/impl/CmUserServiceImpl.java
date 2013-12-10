/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.cmuser.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.exception.EmailExistException;
import com.elasticpath.commons.exception.EmailNonExistException;
import com.elasticpath.commons.exception.EmailSendException;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.misc.CmUserEmailPropertyHelper;
import com.elasticpath.domain.misc.EmailProperties;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.cmuser.CmUserService;
import com.elasticpath.service.cmuser.UserNameExistException;
import com.elasticpath.service.impl.AbstractEpPersistenceServiceImpl;
import com.elasticpath.service.misc.EmailService;
import com.elasticpath.service.misc.FetchPlanHelper;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.service.search.IndexNotificationService;
import com.elasticpath.service.search.IndexType;
import com.elasticpath.settings.SettingsService;

/**
 * The default implementation of <code>CmUserService</code>.
 */
public class CmUserServiceImpl extends AbstractEpPersistenceServiceImpl implements CmUserService {

	private EmailService emailService;

	private FetchPlanHelper fetchPlanHelper;

	private TimeService timeService;

	private SettingsService settingsService;

	private static final String USER_FIELD_STORES = "stores"; //$NON-NLS-1$

	private static final String USER_FIELD_WAREHOUSES = "warehouses"; //$NON-NLS-1$

	private static final String USER_FIELD_CATALOGS = "catalogs"; //$NON-NLS-1$

	private IndexNotificationService indexNotificationService;

	/**
	 * List of fields for eager fetching.
	 */
	public static final List<String> USER_ACCESS_INFO_FIELDS = new ArrayList<String>();

	static {
		USER_ACCESS_INFO_FIELDS.add(USER_FIELD_STORES);
		USER_ACCESS_INFO_FIELDS.add(USER_FIELD_CATALOGS);
		USER_ACCESS_INFO_FIELDS.add(USER_FIELD_WAREHOUSES);
	}

	@Override
	public CmUser add(final CmUser cmUser) throws UserNameExistException, EmailExistException, EmailSendException {
		sanityCheck();
		if (userNameExists(cmUser.getUserName())) {
			throw new UserNameExistException("CmUser with the given user name already exists");
		}
		if (emailExists(cmUser.getEmail())) {
			throw new EmailExistException("CmUser with the given email address already exists");
		}

		final String password = cmUser.resetPassword();
		final Date currentTime = timeService.getCurrentTime();
		cmUser.setCreationDate(currentTime);
		cmUser.setLastModifiedDate(currentTime);
		getPersistenceEngine().save(cmUser);
		sendChangePasswordEmail(getCreateEmailProperties(cmUser, password));
		removeExtraPasswordItems(cmUser);
		indexNotificationService.addNotificationForEntityIndexUpdate(IndexType.CMUSER, cmUser.getUidPk());
		return cmUser;
	}

	@Override
	public CmUser update(final CmUser cmUser) throws UserNameExistException, EmailExistException {
		sanityCheck();

		if (userNameExists(cmUser)) {
			throw new UserNameExistException("CmUser with the given user name already exists");
		}
		if (emailExists(cmUser)) {
			throw new EmailExistException("CmUser with the given email address already exists");
		}

		removeExtraPasswordItems(cmUser);
		cmUser.setLastModifiedDate(timeService.getCurrentTime());
		CmUser persistedUser = getPersistenceEngine().merge(cmUser);
		indexNotificationService.addNotificationForEntityIndexUpdate(IndexType.CMUSER, persistedUser.getUidPk());
		return persistedUser;
	}

	private void removeExtraPasswordItems(final CmUser cmUser) {
		int surplus = cmUser.getPasswordHistoryItems().size()
				- Integer.valueOf(settingsService.getSettingValue("COMMERCE/APPSPECIFIC/RCP/passwordHistoryLength").getValue());
		while (surplus > 0) {
			cmUser.getPasswordHistoryItems().remove(0);
			surplus--;
		}
	}

	@Override
	public void remove(final CmUser cmUser) throws EpServiceException {
		sanityCheck();
		getPersistenceEngine().delete(cmUser);
	}

	@Override
	public boolean emailExists(final String email) throws EpServiceException {
		List<Long> listOfCounts = getPersistenceEngine().retrieveByNamedQuery("CMUSER_COUNT_BY_EMAIL", email);
		return listOfCounts.iterator().next() > 0;
	}

	@Override
	public boolean emailExists(final CmUser cmUser) throws EpServiceException {
		if (cmUser.getEmail() == null) {
			return false;
		}
		final CmUser existingCmUser = this.findByEmail(cmUser.getEmail());
		return (existingCmUser != null && existingCmUser.getUidPk() != cmUser.getUidPk());
	}

	@Override
	public boolean userNameExists(final String userName) throws EpServiceException {
		List<Long> listOfCounts = getPersistenceEngine().retrieveByNamedQuery("CMUSER_COUNT_BY_USERNAME", userName);
		return listOfCounts.iterator().next() > 0;
	}

	@Override
	public boolean userNameExists(final CmUser cmUser) throws EpServiceException {
		if (cmUser.getUserName() == null) {
			return false;
		}
		final CmUser existingCmUser = this.findByUserName(cmUser.getUserName());
		return (existingCmUser != null && existingCmUser.getUidPk() != cmUser.getUidPk());
	}

	@Override
	public List<CmUser> list() throws EpServiceException {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("CMUSER_SELECT_ALL");
	}

	/**
	 * Finds all user uids.
	 *
	 * @return a list of uids
	 * @throws EpServiceException in case of any errors
	 */
	public List<Long> findAllUids() throws EpServiceException {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("CMUSER_FIND_ALL_UIDS");
	}

	@Override
	public List<CmUser> list(final int startIndex, final int maxResults) throws EpServiceException {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("CMUSER_SELECT_ALL", startIndex, maxResults);
	}

	@Override
	public List<CmUser> findByRoleId(final long roleUidPk) throws EpServiceException {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("CMUSER_FIND_BY_USERROLE_ID", roleUidPk);
	}

	@Override
	public long count() throws EpServiceException {
		sanityCheck();
		List<Long> countList = getPersistenceEngine().retrieveByNamedQuery("CMUSER_COUNT");
		if (!countList.isEmpty()) {
			return countList.get(0);
		}
		return 0;
	}

	@Override
	public CmUser load(final long cmUserUid) throws EpServiceException {
		sanityCheck();
		CmUser cmUser = null;
		if (cmUserUid <= 0) {
			cmUser = getBean(ContextIdNames.CMUSER);
		} else {
			cmUser = getPersistentBeanFinder().load(ContextIdNames.CMUSER, cmUserUid);
		}
		return cmUser;
	}

	@Override
	public CmUser get(final long cmUserUid) throws EpServiceException {
		sanityCheck();
		CmUser cmUser = null;
		if (cmUserUid <= 0) {
			cmUser = getBean(ContextIdNames.CMUSER);
		} else {
			cmUser = getPersistentBeanFinder().get(ContextIdNames.CMUSER, cmUserUid);
		}
		return cmUser;
	}

	/**
	 * Retrieves list of <code>CmUser</code> uids where the last modified date is later than
	 * the specified date.
	 *
	 * @param date date to compare with the last modified date
	 * @return list of <code>CmUser</code>s whose last modified date is later than the
	 *         specified date
	 */
	public List<Long> findUidsByModifiedDate(final Date date) {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("CMUSER_UIDS_SELECT_BY_MODIFIED_DATE", date);
	}

	@Override
	public Object getObject(final long uid) throws EpServiceException {
		return get(uid);
	}

	@Override
	public Object getObject(final long uid, final Collection<String> fieldsToLoad) throws EpServiceException {
		fetchPlanHelper.addFields(getElasticPath().getBeanImplClass(ContextIdNames.CMUSER), fieldsToLoad);
		Object object = getObject(uid);
		fetchPlanHelper.clearFetchPlan();
		return object;
	}

	@Override
	public CmUser findByEmail(final String email) throws EpServiceException {
		sanityCheck();
		if (email == null) {
			throw new EpServiceException("Cannot retrieve null email.");
		}

		final List<CmUser> results = getPersistenceEngine().retrieveByNamedQuery("CMUSER_FIND_BY_EMAIL", email);
		CmUser cmUser = null;
		if (results.size() == 1) {
			cmUser = results.get(0);
		} else if (results.size() > 1) {
			throw new EpServiceException("Inconsistent data -- duplicate email address exist -- " + email);
		}
		return cmUser;
	}

	@Override
	public CmUser findByUserName(final String userName) throws EpServiceException {
		sanityCheck();
		if (userName == null) {
			throw new EpServiceException("Cannot retrieve null userName.");
		}

		final List<CmUser> results = getPersistenceEngine().retrieveByNamedQuery("CMUSER_FIND_BY_USERNAME", userName);
		CmUser cmUser = null;
		if (results.size() == 1) {
			cmUser = results.get(0);
		} else if (results.size() > 1) {
			throw new EpServiceException("Inconsistent data -- duplicate email address exist -- " + userName);
		}
		return cmUser;
	}

	@Override
	public CmUser findByUserNameWithAccessInfo(final String userName) throws EpServiceException {
		CmUser lazyInitializedUser = this.findByUserName(userName);
		return (CmUser) this.getObject(lazyInitializedUser.getUidPk(), USER_ACCESS_INFO_FIELDS);
	}

	@Override
	public CmUser updateUserAfterLogin(final CmUser cmUser) {
		cmUser.setLastLoginDate(timeService.getCurrentTime());
		cmUser.resetFailedLoginAttempts();
		return update(cmUser);
	}

	@Override
	public CmUser resetUserPassword(final String email) throws EmailNonExistException {
		final CmUser cmUser = this.findByEmail(email);
		if (cmUser == null) {
			throw new EmailNonExistException("Invalid email address \"" + email + "\"");
		}
		final String password = cmUser.resetPassword();
		//need proxy call to this to fool Spring into bringing back appropriate txn context
		CmUser cmUserUpdated = getCmUserService().update(cmUser);
		sendChangePasswordEmail(getResetEmailProperties(cmUser, password));
		return cmUserUpdated;
	}

	private CmUserService getCmUserService() {
		return getBean(ContextIdNames.CMUSER_SERVICE);
	}

	/**
	 * Sends ChangePassword Email notification.
	 *
	 * @param emailProperties current email properties for send mail
	 * @throws EmailSendException if mail could not be sent
	 */
	protected void sendChangePasswordEmail(final EmailProperties emailProperties) throws EmailSendException {
		try {
			emailService.sendMail(emailProperties);
		} catch (RuntimeException exception) {
			throw new EmailSendException("Mail could not be sent", exception);
		}
	}

	/**
	 * Gets Email Properties for Create User Password E-mail notification.
	 * @param cmUser the user
	 * @param password password which will be shown opened in e-mail
	 * @return EmailProperties instance
	 */
	EmailProperties getCreateEmailProperties(final CmUser cmUser, final String password) {
		CmUserEmailPropertyHelper cmUserEmailPropHelper = getBean(ContextIdNames.EMAIL_PROPERTY_HELPER_CMUSER);
		return cmUserEmailPropHelper.getCreateEmailProperties(cmUser, password, Locale.getDefault());
	}

	/**
	 * Gets Email Properties for Reset User Password E-mail notification.
	 * @param cmUser the user
	 * @param password password which will be shown opened in e-mail
	 * @return EmailProperties instance
	 */
	EmailProperties getResetEmailProperties(final CmUser cmUser, final String password) {
		CmUserEmailPropertyHelper cmUserEmailPropHelper = getBean(ContextIdNames.EMAIL_PROPERTY_HELPER_CMUSER);
		return cmUserEmailPropHelper.getResetEmailProperties(cmUser, password, Locale.getDefault());
	}

	/**
	 * Set the emailService.
	 *
	 * @param emailService the emailService
	 */
	public void setEmailService(final EmailService emailService) {
		this.emailService = emailService;
	}

	/**
	 * @param fetchPlanHelper the fetchPlanHelper to set
	 */
	public void setFetchPlanHelper(final FetchPlanHelper fetchPlanHelper) {
		this.fetchPlanHelper = fetchPlanHelper;
	}

	/**
	 * Gets the time service.
	 *
	 * @return the time service
	 */
	public TimeService getTimeService() {
		return timeService;
	}

	/**
	 * Sets the time service.
	 *
	 * @param timeService the time service
	 */
	public void setTimeService(final TimeService timeService) {
		this.timeService = timeService;
	}

	/**
	 * Gets settings service.
	 *
	 * @return <code>SettingsService</code>
	 */
	public SettingsService getSettingsService() {
		return settingsService;
	}

	/**
	 * Sets settings service.
	 *
	 * @param settingsService <code>SettingsService</code>
	 */
	public void setSettingsService(final SettingsService settingsService) {
		this.settingsService = settingsService;
	}

	@Override
	public CmUser findByGuid(final String guid) {
		sanityCheck();
		if (guid == null) {
			throw new EpServiceException("Cannot search by null GUID.");
		}

		final List<CmUser> results = getPersistenceEngine().retrieveByNamedQuery("CMUSER_FIND_BY_GUID", guid);

		if (results.size() == 1) {
			return results.get(0);
		} else if (results.size() > 1) {
			throw new EpServiceException("Inconsistent data -- duplicate GUIDs exist -- " + guid);
		}
		return null;
	}

	@Override
	public CmUser findByGuid(final String guid, final FetchGroupLoadTuner loadTuner)  {
		CmUser retrievedCmUser = null;

		if (loadTuner == null) {
			retrievedCmUser = findByGuid(guid);
		} else {
			fetchPlanHelper.clearFetchPlan();
			fetchPlanHelper.configureFetchGroupLoadTuner(loadTuner, false);
			retrievedCmUser = findByGuid(guid);
			fetchPlanHelper.clearFetchPlan();
		}

		return retrievedCmUser;
	}

	@Override
	public List<CmUser> findByUids(final Collection<Long> cmUserUids) {
		sanityCheck();

		if (cmUserUids == null || cmUserUids.isEmpty()) {
			return Collections.emptyList();
		}

		return getPersistenceEngine().retrieveByNamedQueryWithList("CMUSER_BY_UIDS", "list", cmUserUids);
	}

	@Override
	public void removePriceListFromUsers(final String priceListGuid) {
		List<CmUser> cmusers = list();
		for (CmUser cmuser : cmusers) {
			if (cmuser.getPriceLists().contains(priceListGuid)) {
				cmuser.removePriceList(priceListGuid);
				update(cmuser);
			}
		}
	}

	/**
	 * @param indexNotificationService instance to set
	 */
	public void setIndexNotificationService(
			final IndexNotificationService indexNotificationService) {
		this.indexNotificationService = indexNotificationService;
	}

}
