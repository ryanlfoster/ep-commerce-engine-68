/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.event;

/**
 * Types of the event originator.
 * 
 */
public enum EventOriginatorType {

	/** event come up from the CommerceManager User. */
	CMUSER,
	
	/** event come up from the Customer. */
	CUSTOMER,
	
	/** event come up from the WebService User. */
	WSUSER,
	
	/** event come up from system. */
	SYSTEM;
	
}
