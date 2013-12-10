/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.payment;

/**
 * Represents a checkout transaction behaviour, typically either an immediate sale
 * or a pre-authorize followed be a capture.
 */
public final class CheckoutTransactionBehaviour {

	private final String behaviour;
	
	private CheckoutTransactionBehaviour(final String behaviour) {
		this.behaviour = behaviour; 
	}
	
	/**
	 * @return the string representation of the checkout transaction behaviour
	 */
	public String toString() {
		return behaviour;
	}
	
	/**
	 * The checkout transaction behaviour is to make an immediate sale.
	 */
	public static final CheckoutTransactionBehaviour SALE = new CheckoutTransactionBehaviour("sale");
	/**
	 * The checkout transaction behaviour is to make an authorization followed by a capture at shipping time.
	 */
	public static final CheckoutTransactionBehaviour AUTHORIZATION = new CheckoutTransactionBehaviour("authorization");
	/**
	 * The checkout transaction behaviour is to verify sufficient funds followed by an auth and capture at shipping time.
	 */
	public static final CheckoutTransactionBehaviour ORDER = new CheckoutTransactionBehaviour("order");
	/**
	 * The default checkout transaction behaviour (Authorization).
	 */
	public static final CheckoutTransactionBehaviour DEFAULT = AUTHORIZATION;
}
