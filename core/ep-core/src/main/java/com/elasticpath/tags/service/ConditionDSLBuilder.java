package com.elasticpath.tags.service;

import com.elasticpath.tags.domain.LogicalOperator;

/**
 * Builds either logical operator that contains all conditions, or conditional expression DSL string.
 */
public interface ConditionDSLBuilder {
    /**
     * Builds logical operator tree. A {@link com.elasticpath.tags.domain.LogicalOperator} can contain other logical operators or conditions.
     *
     * @param dslString a DSL representation of logical operator
     * @return a logical operator which represents the given dslString
     */
    LogicalOperator getLogicalOperationTree(String dslString);

    /**
     * Builds DSL string from given logical operator. Please refer to user documentation about DSL string syntax.
     *
     * @param rootNode a root node of logical operator tree to be represented as DSL string
     * @return a DSL string that represents the logical operator tree
     * @throws InvalidConditionTreeException is thrown when the condition tree syntax or values are invalid 
     *                                (exception message to contain a message for UI) 
     */
    String getConditionalDSLString(LogicalOperator rootNode) throws InvalidConditionTreeException;
}
