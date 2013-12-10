/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.rules.impl;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.apache.openjpa.persistence.DataCache;
import org.apache.openjpa.persistence.ElementDependent;
import org.apache.openjpa.persistence.jdbc.ElementForeignKey;
import org.apache.openjpa.persistence.jdbc.ElementJoinColumn;

import com.elasticpath.base.Initializable;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.impl.AbstractLegacyPersistenceImpl;
import com.elasticpath.domain.rules.RuleElement;
import com.elasticpath.domain.rules.RuleElementType;
import com.elasticpath.domain.rules.RuleException;
import com.elasticpath.domain.rules.RuleExceptionType;
import com.elasticpath.domain.rules.RuleParameter;
import com.elasticpath.service.rules.PromotionRuleExceptions;

/**
 * Abstract class with behaviour common to all rule elements.
 */
@Entity
@Table(name = AbstractRuleElementImpl.TABLE_NAME)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TYPE", discriminatorType = DiscriminatorType.STRING)
@DataCache(enabled = false)
public abstract class AbstractRuleElementImpl extends AbstractLegacyPersistenceImpl implements RuleElement, Initializable {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TRULEELEMENT";

	private String kind;

	private String type;

	private Set<RuleParameter> parameters = new HashSet<RuleParameter>();

	private Set<RuleException> exceptions = new HashSet<RuleException>();

	private long ruleId = 0;

	private long uidPk;

	/**
	 * Must be implemented by subclasses to return their kind. (E.g. eligibility, condition, action)
	 * 
	 * @return the kind of the element subclass.
	 */
	protected abstract String getElementKind();

	/**
	 * Must be implemented by subclasses. Returns the <code>RuleElementType</code> associated with this <code>RuleElement</code> subclass. The
	 * <code>RuleElementType</code>'s property key must match this class' discriminator-value and the spring context bean id for this
	 * <code>RuleElement</code> implementation.
	 * 
	 * @return the <code>RuleElementType</code> associated with this <code>RuleElement</code> subclass.
	 */
	public abstract RuleElementType getElementType();

	/**
	 * Get the kind of this <code>RuleElement</code> (e.g. eligibility, condition, action).
	 * 
	 * @return the kind
	 */
	@Basic
	@Column(name = "KIND")
	public String getKind() {
		if (this.kind == null) {
			this.kind = getElementKind();
		}
		return this.kind;
	}

	/**
	 * Set the kind of this <code>RuleElement</code> (e.g. eligibility, condition, action)
	 * 
	 * @param kind the kind of the rule element
	 */
	public void setKind(final String kind) {
		this.kind = kind;
	}

	/**
	 * Get the type of this rule element. (e.g. cartCategoryPercentDiscountAction. Should match spring bean factory bean id.)
	 * 
	 * @return the type
	 */
	@Basic
	@Column(name = "TYPE")
	public String getType() {
		if (this.type == null) {
			this.type = getElementType().getPropertyKey();
		}
		return this.type;
	}

	/**
	 * Set the type of element. (e.g. cartCategoryPercentDiscountAction. Should match bean name)
	 * 
	 * @param type the type of element
	 */
	public void setType(final String type) {
		this.type = type;
	}

	/**
	 * Returns the value of a parameter with the specified key.
	 * 
	 * @param key The key of the parameter to be returned
	 * @return the value of the parameter with the specified key or "" if no matching parameter was found.
	 */
	public String getParamValue(final String key) {
		if (getParameters() != null) {
			for (RuleParameter currParam : getParameters()) {
				if (currParam.getKey().equals(key)) {
					return currParam.getValue();
				}
			}
		}
		return "";
	}

	/**
	 * Checks that the rule set domain model is well formed. For example, rule conditions must have all required parameters specified. Note: the
	 * parameter value of categoryId, productId and skuCode are allowed to be empty, which mean "ANY".
	 * 
	 * @throws EpDomainException if the structure is not correct.
	 */
	public void validate() throws EpDomainException {
		String[] keys = getParameterKeys();
		if (keys != null) {
			for (int i = 0; i < keys.length; i++) {
				String paramValue = getParamValue(keys[i]); 
				if (StringUtils.isBlank(paramValue)) {
					throw new EpDomainException("Rule element " + this.getType() + " must have parameter with key " + keys[i]);
				}
			}
		}
	}

	/**
	 * Get the parameters associated with this rule element.
	 * 
	 * @return the parameters
	 */
	@OneToMany(targetEntity = RuleParameterImpl.class, fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
	@ElementJoinColumn(name = "RULE_ELEMENT_UID")
	@ElementDependent
	@ElementForeignKey(name = "TRULEPARAMETER_IBFK_1")
	public Set<RuleParameter> getParameters() {
		return this.parameters;
	}

	/**
	 * Set the parameters of this rule element.
	 * 
	 * @param parameters a set of <code>RuleParameter</code> objects
	 */
	public void setParameters(final Set<RuleParameter> parameters) {
		this.parameters = parameters;
	}

	/**
	 * Add a parameter to this rule element.
	 * 
	 * @param ruleParameter a <code>RuleParameter</code> object
	 */
	public void addParameter(final RuleParameter ruleParameter) {
		getParameters().add(ruleParameter);
	}

	/**
	 * Get the <code>RuleException</code> objects associated with this <code>RuleElement</code>.
	 * 
	 * @return the set of ruleExceptions
	 */
	@OneToMany(targetEntity = AbstractRuleExceptionImpl.class, fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
	@ElementJoinColumn(name = "RULE_ELEMENT_UID")
	@ElementDependent
	@ElementForeignKey(name = "TRULEEXCEPTION_IBFK_1")
	public Set<RuleException> getExceptions() {
		return this.exceptions;
	}

	/**
	 * Set the exceptions of this rule element.
	 * 
	 * @param ruleExceptions a set of <code>RuleException</code> objects.
	 */
	public void setExceptions(final Set<RuleException> ruleExceptions) {
		this.exceptions = ruleExceptions;
	}

	/**
	 * Add an exception to this rule element.
	 * 
	 * @param ruleException the <code>RuleException</code> object to add
	 */
	public void addException(final RuleException ruleException) {
		getExceptions().add(ruleException);
	}

	/**
	 * Get the string representation of the <code>RuleException</code>s associated with this <code>RuleElement</code>. This string will be used
	 * in the generated Drools code.
	 * 
	 * @return the string representation of the <code>RuleException</code>s.
	 */
	@Transient
	protected String getExceptionStr() {
		final StringBuffer categoryCodes = new StringBuffer();
		final StringBuffer productCodes = new StringBuffer();
		final StringBuffer skuCodes = new StringBuffer();

		for (RuleException ruleException : getExceptions()) {
			if (ruleException.getExceptionType() == RuleExceptionType.CATEGORY_EXCEPTION) {
				categoryCodes.append(ruleException.getParamValue(RuleParameter.CATEGORY_CODE_KEY)).append(
						PromotionRuleExceptions.EXCEPTION_STRING_SEPARATOR);
			} else if (ruleException.getExceptionType() == RuleExceptionType.PRODUCT_EXCEPTION) {
				productCodes.append(ruleException.getParamValue(RuleParameter.PRODUCT_CODE_KEY)).append(
						PromotionRuleExceptions.EXCEPTION_STRING_SEPARATOR);
			} else if (ruleException.getExceptionType() == RuleExceptionType.SKU_EXCEPTION) {
				skuCodes.append(ruleException.getParamValue(RuleParameter.SKU_CODE_KEY)).append(
						PromotionRuleExceptions.EXCEPTION_STRING_SEPARATOR);
			}
		}

		StringBuffer sbf = new StringBuffer();
		sbf.append(PromotionRuleExceptions.CATEGORY_CODES).append(categoryCodes.toString());
		sbf.append(PromotionRuleExceptions.PRODUCR_CODES).append(productCodes.toString());
		sbf.append(PromotionRuleExceptions.PRODUCTSKU_CODES).append(skuCodes.toString());
		return sbf.toString();
	}

	/**
	 * Set the identifier for the rule that contains this action. (For traceablility)
	 * 
	 * @param ruleId the id of the rule containing this action.
	 */
	public void setRuleId(final long ruleId) {
		this.ruleId = ruleId;
	}

	/**
	 * Get the id of the rule containing this element (for traceability).
	 * 
	 * @return the rule id.
	 */
	@Transient
	protected long getRuleId() {
		return this.ruleId;
	}

	/**
	 * Check if this rule element is valid in the specified scenario.
	 * 
	 * @param scenarioId the Id of the scenario to check (defined in RuleScenarios)
	 * @return true if the rule element is applicable in the given scenario
	 */
	public abstract boolean appliesInScenario(final int scenarioId);

	/**
	 * Return the array of the allowed <code>RuleException</code> types for the rule.
	 * 
	 * @return an array of String of the allowed <code>RuleException</code> types for the rule.
	 */
	public abstract RuleExceptionType[] getAllowedExceptions();

	/**
	 * Return an array of parameter keys required by this rule action.
	 * 
	 * @return the parameter key array
	 */
	public abstract String[] getParameterKeys();

	/**
	 * Return the Drools code corresponding to this action.
	 * 
	 * @return the Drools code
	 * @throws EpDomainException if the rule is not well formed
	 */
	public abstract String getRuleCode() throws EpDomainException;

	/**
	 * Gets the unique identifier for this domain model object.
	 * 
	 * @return the unique identifier.
	 */
	@Id
	@Column(name = "UIDPK")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
	@TableGenerator(name = TABLE_NAME, table = "JPA_GENERATED_KEYS", pkColumnName = "ID", valueColumnName = "LAST_VALUE", pkColumnValue = TABLE_NAME)
	public long getUidPk() {
		return this.uidPk;
	}

	/**
	 * Sets the unique identifier for this domain model object.
	 * 
	 * @param uidPk the new unique identifier.
	 */
	public void setUidPk(final long uidPk) {
		this.uidPk = uidPk;
	}
	
	/**
	 * Sets default values.
	 */
	@Override
	public void initialize() {
		// initialize rule parameters
		final String[] paramKeys = this.getParameterKeys();
		if (paramKeys != null) {
			for (final String currParamKey : paramKeys) {
				final RuleParameter currRuleParameter = new RuleParameterImpl();
				currRuleParameter.setKey(currParamKey);
				getParameters().add(currRuleParameter);
			}
		}
	}
}