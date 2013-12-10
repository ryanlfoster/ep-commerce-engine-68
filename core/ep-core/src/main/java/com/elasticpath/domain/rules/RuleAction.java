/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.rules;

/**
 * Represents an action that can be executed by a rule.
 */
public interface RuleAction extends RuleElement {

	/** Identifies the <code>RuleElement</code> as an action to a rule. */
	String ACTION_KIND = "Action";

	/**
	 * Agenda group that identifies actions that must be fired separately because they depend on the post-rule value of the cart subtotal.
	 */
	String SUBTOTAL_DEPENDENT_AGENDA_GROUP = "SubtotalDependent";

	/**
	 * Default agenda group.
	 */
	String DEFAULT_AGENDA_GROUP = "DefaultGroup";

	/**
	 * Get the salience value for this rule. The higher the salience, the earlier the actions of the rule will be executed relative to other rules.
	 * 
	 * @return the salience value
	 */
	int getSalience();

	/**
	 * Set the salience value.
	 * 
	 * @see getSalience
	 * @param salience the new salience value
	 */
	void setSalience(final int salience);

	/**
	 * Get the agenda group for this action. The agenda group is used to determine which rules will be fired together. <br>
	 * <b>Only one action in a given rule can specify an agenda group because the group will become the agenda group of the rule</b>
	 * 
	 * @return the agenda group or <code>null</code> if no agenda group is required
	 */
	String getAgendaGroup();

	/**
	 * Set the agenda group.
	 * 
	 * @see getAgendaGroup
	 * @param agendaGroup the agenda group name
	 */
	void setAgendaGroup(final String agendaGroup);

	/**
	 * Must be implemented by subclasses to return their type. Get the <code>DiscountType</code> associated with this RuleAction.
	 * 
	 * @return the <code>DiscountType</code> associated with this RuleAction
	 */
	DiscountType getDiscountType();
	
	
	/**
	 * 
	 * @return The number of items that a single coupon can be used for.
	 */
	int getDiscountQuantityPerCoupon();
}
