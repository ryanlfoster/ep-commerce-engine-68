package com.elasticpath.tags.domain;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;


/**
 * Represents a Logical Operator such as AND, OR, NOT.
 *
 * A {@code LogicalOperator} is a node on a conditional expression tree, and it may
 * apply to two or more {@link LogicalOperator}s or any {@link Condition}s.
 */
public class LogicalOperator implements Serializable {
	
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 200903031L;
	
	private LogicalOperatorType operatorType;
	private final Set<LogicalOperator> logicalOperators = new LinkedHashSet<LogicalOperator>();
	private final Set<Condition> conditions = new LinkedHashSet<Condition>();
	private LogicalOperator parent = null;

    /**
	 * Constructor.
	 * @param operatorType the operator type
	 */
	public LogicalOperator(final LogicalOperatorType operatorType) {
		this.operatorType = operatorType;
	}

    /**
	 * Constructor.
	 * @param operatorType the operator type
	 * @param parent parent LogicalOperator
	 */
	public LogicalOperator(final LogicalOperatorType operatorType, final LogicalOperator parent) {
		this.operatorType = operatorType;
		this.parent = parent;
	}

	/**
	 * @return the operator type
	 */
	public LogicalOperatorType getOperatorType() {
		return operatorType;
	}

	/**
	 * Sets operator type.
	 * @param operatorType - the operator type
	 */
	public void setOperatorType(final LogicalOperatorType operatorType) {
		this.operatorType = operatorType;
	}
	
	/**
	 * Adds the given logical operand to the list of operands to
	 * which this operator applies, and sets its parent to this.
     *
	 * @param logicalOperator the operand
	 */
	public void addLogicalOperator(final LogicalOperator logicalOperator) {
		this.logicalOperators.add(logicalOperator);
	}

	/**
	 * Removes the given logical operand from the list of operands to
	 * which this operator applies, and sets its parent to null.
     *
	 * @param logicalOperator the operand
	 * @return true if the operand was in the list, false if not
	 */
	public boolean removeLogicalOperand(final LogicalOperator logicalOperator) {
		if (this.logicalOperators.contains(logicalOperator)) {
			this.logicalOperators.remove(logicalOperator);
			return true;
		}

		return false;
	}

	/**
	 * Adds the given condition to the list of conditions to
	 * which this operator applies.
     *
	 * @param condition the operand
	 */
	public void addCondition(final Condition condition) {
        condition.setParentLogicalOperator(this);
		this.conditions.add(condition);
	}

	/**
	 * Removes the given logical operand from the list of operands to
	 * which this operator applies, and sets its parent to null.
     *
	 * @param condition the operand
	 * @return true if the operand was in the list, false if not
	 */
	public boolean removeCondition(final Condition condition) {
		if (this.conditions.contains(condition)) {
            condition.setParentLogicalOperator(null);
			this.conditions.remove(condition);
			return true;
		}

		return false;
	}

	/**
	 * @return the logical operands to which this operator applies,
	 * as an unmodifiable set.
	 */
	public Set<LogicalOperator> getLogicalOperators() {
		return Collections.unmodifiableSet(this.logicalOperators);
	}

	/**
	 * @return the conditions to which this operator applies,
	 * as an unmodifiable set.
	 */
	public Set<Condition> getConditions() {
		return Collections.unmodifiableSet(this.conditions);
	}

	/**
	 * Sets the logical operator that will apply to this operator (if this
	 * operator is also an operand), and adds this operator to the given
	 * operator's list of operands if it is not null.
	 * @param parent the parent operator
	 */
	public void setParentLogicalOperator(final LogicalOperator parent) {
		this.parent = parent;
		if (parent != null) {
			parent.addLogicalOperator(this);
		}
	}

    /**
     * @return checks if the operator has child objects under it either other operators or conditions
     */
    public boolean hasChildren() {
        return (this.logicalOperators.size() + this.conditions.size()) != 0;
    }

    /**
	 * @return the logical operator that is applied to this
	 * operator, if this operator is not at the root of the
	 * expression tree, or null if it is the root.
	 */
	public LogicalOperator getParentLogicalOperator() {
		return parent;
	}

	/**
	 * @return true if this logical operator is at the root of the conditional
	 * expression tree (i.e. no operators apply to this operator).
	 */
	public boolean isRoot() {
		return getParentLogicalOperator() == null;
	}

	@Override
	public String toString() {
		StringBuffer stringBuffer = new StringBuffer("LogicalOperator: {[");
		stringBuffer.append(this.operatorType);
		stringBuffer.append("], ");
		if (this.conditions != null && this.conditions.size() > 0) {
			stringBuffer.append(" conditions: ");
			stringBuffer.append(this.conditions.toString());
		}
		if (this.logicalOperators != null && this.logicalOperators.size() > 0) {
			stringBuffer.append(" logical operators: ");
			stringBuffer.append(this.logicalOperators.toString());
		}
		stringBuffer.append('}');
		return stringBuffer.toString();
	}
	
}
