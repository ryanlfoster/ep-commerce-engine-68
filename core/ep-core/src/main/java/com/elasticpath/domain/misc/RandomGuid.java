/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.misc;

import java.io.Serializable;

/**
 * Represents a randomly generated global uid.
 */
public interface RandomGuid extends Serializable {

	/**
	 * Convert to the standard format for GUID (Useful for SQL Server UniqueIdentifiers, etc). Example: C2FEEEAC-CFCD-11D1-8B05-00600806D9B6
	 * 
	 * @return the guid string
	 */
	String toString();

}