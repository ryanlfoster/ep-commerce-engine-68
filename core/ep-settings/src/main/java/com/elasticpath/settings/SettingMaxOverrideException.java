/*
 * Copyright (c) Elastic Path Software Inc., 2005
 */
package com.elasticpath.settings;

import com.elasticpath.base.exception.EpSystemException;


/**
 * The exception for trying to create a setting value when the maximum
 * number of setting overrides have been created.
 */
public class SettingMaxOverrideException extends EpSystemException {
	
	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	/**
	 * Creates a new <code>SettingMaxOverrideException</code> object with the given message.
	 * 
	 * @param message the reason for this <code>SettingMaxOverrideException</code>.
	 */
	public SettingMaxOverrideException(final String message) {
		super(message);
	}

	/**
	 * Creates a new <code>SettingMaxOverrideException</code> object using the given message and cause exception.
	 * 
	 * @param msg the reason for this <code>SettingMaxOverrideException</code>.
	 * @param cause the <code>Throwable</code> that caused this <code>SettingMaxOverrideException</code>.
	 */
	public SettingMaxOverrideException(final String msg, final Throwable cause) {
		super(msg, cause);
	}
}
