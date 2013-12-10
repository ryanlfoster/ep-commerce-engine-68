/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.sfweb.security.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.elasticpath.domain.store.StoreState;
import com.elasticpath.service.catalogview.StoreConfig;
import com.elasticpath.settings.SettingsReader;
import com.elasticpath.sfweb.security.AccessChecker;

/**
 * Determines whether a restricted store can be accessed.
 */
public class StoreAccessCheckerImpl implements AccessChecker {

	private static final String PASSCODE_PARAMETER_NAME = "passcode";

	private static final String SESSION_PASSCODE_PREFIX = "RestrictedStorePasscode_";

	private SettingsReader settingsReader;
	
	private StoreConfig storeConfig;
	
	/**
	 * Determine whether the store is accessible based on what is provided in the request.
	 * Assumption: This won't be called for incomplete stores.
	 * 
	 * @param request the request which may contain information used to determine the accessibility
	 * @return true if the store is accessible
	 */
	public boolean isAccessible(final HttpServletRequest request) {
		if (isStoreOpen()) {
			return true;
		}
		
		String passcode = getPasscode(request);
		if (!isPassCodeValid(passcode)) {
			return false;
		}
		storePasscodeInSession(request.getSession(), passcode);
		return true;
	}
	
	/**
	 * Determine whether a store is considered "open" (i.e. anyone can view it).
	 * 
	 * @return true if the store is open
	 */
	protected boolean isStoreOpen() {
		return StoreState.OPEN.equals(getStoreState());
	}

	/**
	 * Get the passcode. Looks for a passcode request parameter first, if not found
	 * look for the passcode in the session.
	 * 
	 * @param request the request we expect the passcode in.
	 * @return the passcode or null if none found.
	 */
	protected String getPasscode(final HttpServletRequest request) {
		String passcode = request.getParameter(getPasscodeParameterName());
		if (passcode != null) {
			return passcode;
		}
		HttpSession session = request.getSession(false);
		if (session != null) {
			return (String) session.getAttribute(getPasscodeAttributeName());
		}
		return null;
	}

	/**
	 * Get the attribute name used in the session for storing the passcode.
	 *  
	 * @return the name of the session attribute for the passcode
	 */
	protected String getPasscodeAttributeName() {
		return SESSION_PASSCODE_PREFIX + getStoreConfig().getStoreCode();
	}
	
	/**
	 * Get the parameter name used in the request for passing the passcode.
	 * 
	 * @return the name of the request parameter for the passcode.
	 */
	protected String getPasscodeParameterName() {
		return PASSCODE_PARAMETER_NAME;
	}
	
	/**
	 * Store the passcode in the session for later use.
	 * 
	 * @param session the session to store the passcode in
	 * @param passcode the passcode to store
	 */
	protected void storePasscodeInSession(final HttpSession session, final String passcode) {
		if (passcode != null) {
			session.setAttribute(getPasscodeAttributeName(), passcode);
		}
	}

	/**
	 * Verify whether the given passcode is valid.
	 * 
	 * @param passcode the passcode to check 
	 * @return true if the passcode is valid
	 */
	protected boolean isPassCodeValid(final String passcode) {
		if (passcode == null) {
			return false;
		}
		return passcode.equals(getExpectedPasscode());
	}
	
	/**
	 * Get the expected passcode for the current store.
	 * 
	 * @return the expected passcode.
	 */
	protected String getExpectedPasscode() {
		return getSettingsReader().getSettingValue("COMMERCE/STORE/restrictedAccessPasscode", getStoreConfig().getStoreCode()).getValue();
	}

	/**
	 * Get the store state for the current store.
	 * 
	 * @return the store state
	 */
	protected StoreState getStoreState() {
		return getStoreConfig().getStore().getStoreState();
	}

	/**
	 * Get the settings reader.
	 * 
	 * @return the settingsReader
	 */
	protected SettingsReader getSettingsReader() {
		return settingsReader;
	}

	/**
	 * Set the settings reader.
	 * 
	 * @param settingsReader the settingsReader to set
	 */
	public void setSettingsReader(final SettingsReader settingsReader) {
		this.settingsReader = settingsReader;
	}

	/**
	 * Get the store configuration.
	 * 
	 * @return the storeConfig
	 */
	protected StoreConfig getStoreConfig() {
		return storeConfig;
	}

	/**
	 * Set the store configuration object.
	 * 
	 * @param storeConfig the storeConfig to set
	 */
	public void setStoreConfig(final StoreConfig storeConfig) {
		this.storeConfig = storeConfig;
	}

}
