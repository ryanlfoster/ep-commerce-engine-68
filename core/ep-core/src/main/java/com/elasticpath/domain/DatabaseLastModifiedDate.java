/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.domain;

import java.util.Date;

/**
 * An object that has a last modified date that will be set to database time.
 */
public interface DatabaseLastModifiedDate {
	
	/**
	 * Returns the date when the object was last modified.
	 * 
	 * @return the date when the object was last modified
	 */
	Date getLastModifiedDate();
	
	/**
	 * Set the date when the object was last modified.
	 * 
	 * @param lastModifiedDate the date when the category was last modified
	 */
	void setLastModifiedDate(final Date lastModifiedDate);

}
