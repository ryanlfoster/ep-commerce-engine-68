package com.elasticpath.tags.service;

import com.elasticpath.tags.TagSet;
import com.elasticpath.tags.domain.ConditionalExpression;


/**
 * Evaluation service for conditions.
 */
public interface ConditionEvaluatorService {
	
	/**
	 * Evaluate a given condition on the given map of tags.
	 * 
	 * @param tags collection of tags
	 * @param condition containing a rule for evaluating against a set of tags
	 * @return true if condition evaluates to true
	 */
	boolean evaluateConditionOnTags(TagSet tags, ConditionalExpression condition);
}
