package com.elasticpath.ql.custom.setting;

import java.io.Serializable;


/**
 * The SettingResult.
 */
public class SettingResult implements Serializable {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 6000000001L;
	
	private final String path;
	
	private final String context;
	
	/**
	 * The constructor.
	 *
	 * @param path the path
	 * @param context the context
	 */
	public SettingResult(final String path, final String context) {
		this.path = path;
		this.context = context;		
	}

	/**
	 * Checks if context is null.
	 * 
	 * @return true or false
	 */
	public boolean isValueExist() {
		return (context != null);
	}
	
	/**
	 * Gets the value of path.
	 *
	 * @return the value of path.
	 */
	public final String getPath() {
		return path;
	}

	/**
	 * Gets the value of context.
	 *
	 * @return the value of context.
	 */
	public final String getContext() {
		return context;
	}	
}
