/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.rules;

import java.util.Date;
import java.util.Locale;
import java.util.Set;

import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.misc.LocalizedProperties;
import com.elasticpath.domain.sellingcontext.SellingContext;
import com.elasticpath.domain.store.Store;
import com.elasticpath.persistence.api.Entity;

/**
 * Represents a rule that can be applied by the rules engine. For example,
 * "Give a 10% discount for all products in category X."
 * 
 */
public interface Rule extends Entity {
	/** "AND" operator. */
	boolean AND_OPERATOR = true;
	
	/** "OR" operator.  */
	boolean OR_OPERATOR = false;

	/**
	 * The name of localized property -- displayName.
	 */
	String LOCALIZED_PROPERTY_DISPLAY_NAME = "promotionDisplayName";

	/**
	 * Get the starting date that this rule can be applied.
	 * @return the start date
	 */
	Date getStartDate();
	
	/**
	 * Set the starting date that this rule can be applied.
	 * @param startDate the start date
	 */
	void setStartDate(Date startDate);
	
	/**
	 * Get the end date. After the end date, the rule will no longer be applied.
	 * @return the end date
	 */
	Date getEndDate();
	
	/**
	 * Set the end date.
	 * @param endDate the end date
	 */
	void setEndDate(Date endDate);
	
		
	/**
	 * Gets the operator (AND/OR) if there are multiple eligibility conditions.
	 * @return the eligibility condition operator (ANR/OR)
	 */
	@SuppressWarnings("PMD.BooleanGetMethodName")
	boolean getEligibilityOperator();
	
	/**
	 * Sets the operator (AND/OR) if there are multiple eligibility conditions.
	 * @param eligibilityOperator - the eligibility condition operator (ANR/OR).
	 */
	void setEligibilityOperator(final boolean eligibilityOperator);
	
	/**
	 * Get the conditions associated with this rule.
	 * 
	 * @return the conditions
	 */
	Set<RuleCondition> getConditions();
	
	/**
	 * Gets the operator (AND/OR) if there are multiple conditions.
	 * @return the eligibility condition operator (ANR/OR)
	 */
	@SuppressWarnings("PMD.BooleanGetMethodName")
	boolean getConditionOperator();
	
	/**
	 * Sets the opecom.elasticpath.core/WEB-INF/src/main/java/com/elasticpath/domain/rules/Rule.javarator (AND/OR) 
	 * if there are multiple eligibility conditions.
	 * @param conditionOperator - the condition operator (ANR/OR).
	 */
	void setConditionOperator(final boolean conditionOperator);
	
	/**
	 * Returns the Drools code corresponding to this rule.
	 * @return the rule code.
	 * @throws EpDomainException if the rule is not well formed
	 */
	String getRuleCode() throws EpDomainException;
	
	/**com.elasticpath.core/WEB-INF/src/main/java/com/elasticpath/domain/rules/Rule.java
	 * Adds a condition to the set of conditions.
	 * @param condition the condition to add.
	 * @throws EpDomainException if anything goes wrong.
	 */
	void addCondition(final RuleCondition condition);
	
	/**
	 * Removes the given <code>RuleCondition</code> from the set of rule conditions.
	 * @param ruleCondition the <code>RuleCondition</code> to remove.
	 * @throws EpDomainException if anything goes wrong.
	 */
	void removeCondition(final RuleCondition ruleCondition);
	
	/**
	 * Get the rule elements associated with this rule.
	 * 
	 * @return the rule elements
	 */
	Set<RuleElement> getRuleElements();

	/**
	 * Set the rule elements of this rule.
	 * 
	 * @param ruleElements  a set of <code>RuleElement</code> objects
	 */
	void setRuleElements(final Set<RuleElement> ruleElements);
	
	/**
	 * Adds a rule element to the set of rule elements.
	 * 
	 * @param ruleElement the <code>RuleElement</code> to add
	 * @throws EpDomainException if anything goes wrong.
	 */
	void addRuleElement(final RuleElement ruleElement);

	/**
	 * Get the actions associated with this rule.
	 * @return the actions
	 */
	Set<RuleAction> getActions();
	
	/**
	 * Add an action to the rule.
	 * @param ruleAction the action to add.
	 * @throws EpDomainException if anything goes wrong.
	 */
	void addAction(final RuleAction ruleAction);
	
	/**
	 * Removes the given <code>RuleAction</code> from the set of rule actions.
	 * @param ruleAction the <code>RuleAction</code> to remove.
	 * @throws EpDomainException if anything goes wrong.
	 */
	void removeAction(final RuleAction ruleAction);
	
	/**
	 * Get the name of this rule.
	 * @return the name of the rule
	 */
	String getName();

	/**
	 * Set the name of the rule.
	 * @param name com.elasticpath.core/WEB-INF/src/main/java/com/elasticpath/domain/rules/Rule.javathe name of the rule
	 */
	void setName(final String name);
	
	/**
	 * Get the description of this rule.
	 * @return the description of the rule
	 */
	String getDescription();
	
	/**
	 * Set the description of the rule.
	 * @param description the description of the rule
	 */
	void setDescription(final String description);
	
	/**
	 * Gets the <code>Store</code> associated with this rule.
	 * @return the <code>Store</code> associated with this rule
	 */
	Store getStore();
	
	/**
	 * Set the <code>Store</code> associated with this the rule.
	 * @param store the <code>Store</code> associated with this the rule
	 */
	void setStore(final Store store);
	
	/**
	 * Gets the {@link Catalog} associated with this rule. If this promotion is a store promotion,
	 * the catalog of the store is returned.
	 * 
	 * @return the com.elasticpath.core/WEB-INF/src/main/java/com/elasticpath/domain/rules/Rule.java{@link Catalog} associated with this rule
	 */
	Catalog getCatalog();
	
	/**
	 * Set the {@link Catalog} associated with this the rule. This clears the associated rule
	 * store.
	 * 
	 * @param catalog the {@link Catalog} associated with this the rule
	 */
	void setCatalog(final Catalog catalog);
	
	/**
	 * Gets the <code>CmUser</code> that created this rule.
	 * @return the <code>CmUser</code> that created this rule
	 */
	CmUser getCmUser();
	
	/**
	 * Set the <code>CmUser</code> that created of the rule.
	 * @param cmUser the <code>CmUser</code> that created of the rule
	 */
	void setCmUser(final CmUser cmUser);
	
	/**
	 * Get the starting date from selling context 
	 * that this rule can be applied.
	 * @return the start date
	 */
	Date getStartDateFromSellingContext();
	
	/**
	 * Get the end date from selling context. After the end date, the rule will no longer be applied.
	 * @return the end date
	 */
	Date getEndDateFromSellingContext();
	
	
	/**
	 * Returns <code>true</code> if this rule is enabled, <code>false</code> if it is disabled.
	 * @return <code>true</code> if this rule is enabled, 
	 * <code>false</code> if it is disabled
	 */
	boolean isEnabled();
	
	/**
	 * Set the state of the rule. <code>true</code> sets the state to enabled, <code>false</code> sets the state to disabled.
	 * @param enabledState the state to set
	 */
	void setEnabled(final boolean enabledState);
	
	/**
	 * Get the ruleSet this rule belongs to.
	 * @return the ruleSet it belongs to
	 */
	RuleSet getRuleSet();
	
	/**
	 * Set the ruleSet this rule belongs to.
	 * @param ruleSet the ruleSet it belongs to
	 */
	void setRuleSet(RuleSet ruleSet);
	
	/**
	 * Checks that the rule set domain model is well formed. For example,
	 * rule conditions must have all required parameters specified.
	 * @throws EpDomainException if the structure is not correct.
	 */
	void validate() throws EpDomainException;
	
	/**
	 * Get the date that the rule was last modified on.
	 * 
	 * @return the last modified date
	 * @domainmodel.property
	 */
	Date getLastModifiedDate();

	/**
	 * Set the date that the rule was last modified on.
	 * 
	 * @param lastModifiedDate the date that the order was last modified
	 */
	void setLastModifiedDate(Date lastModifiedDate);

	/**
	 *
	 * @return the currentLupNumber
	 */
	long getCurrentLupNumber();

	/**
	 *
	 * @param currentLupNumber the currentLupNumber to set
	 */
	void setCurrentLupNumber(final long currentLupNumber);
	
	/**
	 * Gets rule code.
	 * 
	 * @return rule code
	 */
	String getCode();
	
	/**
	 * Set the rule code.
	 *
	 * @param code the rule code.
	 */
	void setCode(final String code);
	
	/** 
	 * Get the selling context. Rule can have no SC to be backward 
	 * compatible so this column is optional and nullable.
	 * 
	 * @return Selling context
	 */		
	SellingContext getSellingContext();

	/**
	 * Sets the selling context.
	 * 
	 * @param sellingContext selling context to set
	 */	
	void setSellingContext(SellingContext sellingContext);	

	/**
	 * Returns the <code>LocalizedProperties</code>, i.e. promotion display name.
	 * 
	 * @return the <code>LocalizedProperties</code>
	 */
	LocalizedProperties getLocalizedProperties();

	/**
	 * Set the <code>LocalizedProperties</code>, i.e. Promotion Display name.
	 * 
	 * @param localizedProperties - the <code>LocalizedProperties</code>
	 */
	void setLocalizedProperties(final LocalizedProperties localizedProperties);
	
	/**
	 * Returns the display name of the <code>Rule</code> with the given locale.
	 * 
	 * @param locale the locale
	 * @return the display name of the promotion rule
	 */
	String getDisplayName(final Locale locale);
	
	/**
	 * Is this rule using coupons?
	 * 
	 * @return true if the rule has a coupon condition.
	 */
	boolean isCouponEnabled();
	
	/**
	 * Set whether the rule should have a coupon condition.
	 * 
	 * @param couponEnabled true if the rule should have a coupon condition
	 */
	void setCouponEnabled(final boolean couponEnabled);

	/**
	 * Returns {@code true} if the given date is within the start and end dates. If either the start or end date is
	 * {@code null}, then that end is treated as unbounded.
	 *
	 * @param date date to compare with
	 * @return {@code true} if the given date is within the start and end dates
	 */
	boolean isWithinDateRange(Date date);

	/**
	 * @return whether the current date is bounded between the rules start and end dates
	 */
	boolean isWithinDateRange();
}

