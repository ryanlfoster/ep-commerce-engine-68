/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.rules;



/**
 * Represents a condition that must be true for a rule to fire.
 * 
 */
public interface RuleCondition extends RuleElement  { //NOPMD
	
	/** Identifies the <code>RuleElement</code> as a condition to a rule. */
	String CONDITION_KIND = "Condition";
}
