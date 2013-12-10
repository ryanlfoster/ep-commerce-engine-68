/**
 * 
 */
package com.elasticpath.tags.domain;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

/**
 * 
 */
public class Condition implements Serializable {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 200903032L;

	private TagDefinition tagDefinition;
	private Object tagValue;
	private String operator;
	private LogicalOperator parent;
	
	/**
	 * Constructor.
	 * @param tagDefinition the left operand of the expression which is tag definition
	 * @param operator the operator
	 * @param tagValue the right operand of the expression which is value for the expression
	 * @throws UnsupportedOperationException if the right operand is an instance of Arrays, Maps or Collections.
	 */
	public Condition(final TagDefinition tagDefinition, final String operator, final Object tagValue) throws UnsupportedOperationException {
		if (!isSupportedOperand(tagValue)) {
			throw new UnsupportedOperationException("Operations with null values, Array types, Collections and Maps are not supported!");
		}
		
		this.tagDefinition = tagDefinition;
		this.operator = operator;
		this.tagValue = tagValue;
	}

	private boolean isSupportedOperand(final Object tagValue) {
		return tagValue != null 
			&& !(tagValue.getClass().isArray() || tagValue instanceof Collection || tagValue instanceof Map);			
	}
	
	/**
	 * @return the leftOperand
	 */
	public TagDefinition getTagDefinition() {
		return tagDefinition;
	}
	/**
	 * @param tagDefinition the leftOperand of the expression which is tag definition
	 */
	public void setTagDefinition(final TagDefinition tagDefinition) {
		this.tagDefinition = tagDefinition;
	}
	/**
	 * @return the rightOperand
	 */
	public Object getTagValue() {
		return tagValue;
	}
	/**
	 * @param tagValue the rightOperand  of the expression which is value for the expression
	 * @throws UnsupportedOperationException if the right operand is an instance of Arrays, Maps or Collections.
	 */
	public void setTagValue(final Object tagValue) throws UnsupportedOperationException {
		if (!isSupportedOperand(tagValue)) {
			throw new UnsupportedOperationException("Operations with Array types, Collections and Maps are not supported!");
		}
		
		this.tagValue = tagValue;
	}
	/**
	 * @return the operator
	 */
	public String getOperator() {
		return operator;
	}
	/**
	 * @param operator the operator to set
	 */
	public void setOperator(final String operator) {
		this.operator = operator;
	}
	/**
	 * @return the parent
	 */
	public LogicalOperator getParentLogicalOperator() {
		return parent;
	}
	/**
	 * @param parent the parent to set
	 */
	public void setParentLogicalOperator(final LogicalOperator parent) {
		this.parent = parent;
	}

	@Override
    public String toString() {
        return " {" + tagDefinition + "." + operator + " " + tagValue + "} ";
    }
}
