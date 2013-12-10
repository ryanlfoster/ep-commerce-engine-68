/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.rules.impl;

import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.rules.RuleScenarios;
import com.elasticpath.domain.rules.RuleSet;
import com.elasticpath.domain.store.Store;
import com.elasticpath.persistence.api.AbstractEntityImpl;
import com.elasticpath.persistence.support.FetchGroupConstants;
import org.apache.openjpa.persistence.DataCache;
import org.apache.openjpa.persistence.FetchAttribute;
import org.apache.openjpa.persistence.FetchGroup;
import org.apache.openjpa.persistence.jdbc.ElementJoinColumn;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


/**
 * Represents a set of rules. This currently represents a set of promotion rules. When another rule set is introduced, this class should implement an
 * abstract rule set and subclasses will implement specific rule sets such as promotion rule sets. Note that this rule set is specific to a
 * particular collection of rules because of the import statements that those rules need.
 */
@Entity
@Table(name = RuleSetImpl.TABLE_NAME)
@FetchGroup(name = FetchGroupConstants.PROMOTION_INDEX, attributes = { @FetchAttribute(name = "name") })
@DataCache(enabled = false)
public class RuleSetImpl extends AbstractEntityImpl implements RuleSet {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	private String name;

	private Set<Rule> rules;

	private Date lastModifiedDate;

	private static final Set<String> IMPORTS = new HashSet<String>();

	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TRULESET";
	static {
		IMPORTS.add("com.elasticpath.domain.catalog.Product");
		IMPORTS.add("com.elasticpath.domain.catalog.ProductSku");
		IMPORTS.add("com.elasticpath.domain.shoppingcart.ShoppingItem");
		IMPORTS.add("com.elasticpath.domain.shoppingcart.ShoppingCart");
		IMPORTS.add("com.elasticpath.service.rules.PromotionRuleDelegate");
		IMPORTS.add("com.elasticpath.service.rules.PromotionRuleExceptions");
		IMPORTS.add("com.elasticpath.domain.discounts.Discount");		
		IMPORTS.add("com.elasticpath.domain.discounts.impl.CartAnySkuAmountDiscountImpl");
		IMPORTS.add("com.elasticpath.domain.discounts.impl.CartAnySkuPercentDiscountImpl");
		IMPORTS.add("com.elasticpath.domain.discounts.impl.CartProductAmountDiscountImpl");
		IMPORTS.add("com.elasticpath.domain.discounts.impl.CartProductPercentDiscountImpl");
		IMPORTS.add("com.elasticpath.domain.discounts.impl.CartSkuAmountDiscountImpl");
		IMPORTS.add("com.elasticpath.domain.discounts.impl.CartSkuPercentDiscountImpl");
		IMPORTS.add("com.elasticpath.domain.discounts.impl.CartNFreeSkusDiscountImpl");
		IMPORTS.add("com.elasticpath.domain.discounts.impl.CartNthProductPercentDiscountImpl");
		IMPORTS.add("com.elasticpath.domain.discounts.impl.CartCategoryAmountDiscountImpl");
		IMPORTS.add("com.elasticpath.domain.discounts.impl.CartCategoryPercentDiscountImpl");
		IMPORTS.add("com.elasticpath.domain.discounts.impl.CartSubtotalAmountDiscountImpl");
		IMPORTS.add("com.elasticpath.domain.discounts.impl.CartSubtotalPercentDiscountImpl");
		IMPORTS.add("java.util.Set");
		IMPORTS.add("java.util.Currency");
		IMPORTS.add("java.util.Map");
		IMPORTS.add("com.elasticpath.domain.rules.ActiveRule");
	}

	private int scenarioId;

	private long uidPk;

	private String guid;

	/**
	 * Get the name of this rule set.
	 *
	 * @return the name of this rule set.
	 */
	@Basic
	@Column(name = "NAME", unique = true)
	public String getName() {
		return this.name;
	}

	/**
	 * Set the name of this rule set.
	 *
	 * @param name the name of this rule set
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * Get the id of the scenario that this rule set applies to.
	 *
	 * @return the scenario id <code>(constant in RuleScenarios)</code>
	 */
	@Basic
	@Column(name = "SCENARIO", nullable = false)
	public int getScenario() {
		return this.scenarioId;
	}

	/**
	 * Set the scenario that this rule set applies to.
	 *
	 * @param scenarioId a constant in <code>RuleScenarios</code>.
	 */
	public void setScenario(final int scenarioId) {
		this.scenarioId = scenarioId;
	}

	/**
	 * Get the rules in this rule set.
	 *
	 * @return the rules
	 */
	@OneToMany(targetEntity = PromotionRuleImpl.class, fetch = FetchType.EAGER)
	@ElementJoinColumn(name = "RULE_SET_UID")
	public Set<Rule> getRules() {
		return this.rules;
	}

	/**
	 * Set the rules in this rule set.
	 *
	 * @param rules a set of <code>Rule</code> objects
	 */
	public void setRules(final Set<Rule> rules) {
		this.rules = rules;
	}

	/**
	 * Returns the Drools code corresponding to this rule set.
	 *
	 * @param store the store. Cannot be null
	 * @return the rule code.
	 * @throws EpDomainException if the rule set is not well formed
	 */
	@Transient
	public String getRuleCode(final Store store) throws EpDomainException {
		if (store == null) {
			throw new IllegalArgumentException("Store cannot be null");
		}
		validate();

		StringBuffer code = new StringBuffer();

		code.append("package " + getName() + "\n\n");

		for (String currImport : IMPORTS) {
			code.append("import " + currImport + "; \n");
		}

		code.append("\n\n");

		for (Rule currRule : getRules()) {
			// filter rules by store or catalog depending on the rule scenario
			if (getScenario() == RuleScenarios.CATALOG_BROWSE_SCENARIO
					&& currRule.getCatalog().getUidPk() == store.getCatalog().getUidPk()) {
				code.append(currRule.getRuleCode());
			} else if (getScenario() == RuleScenarios.CART_SCENARIO
					&& currRule.getStore().getUidPk() == store.getUidPk()) {
				code.append(currRule.getRuleCode());
			}
		}		
		if (getScenario() == RuleScenarios.CART_SCENARIO) {
			code.append("query \"").append(RuleSet.QUERY_NAME).append("\" \n\t");
			code.append(RuleSet.DISCOUNT_NAME).append(" : Discount(); \nend;");
		}

		return code.toString();
	}

	/**
	 * Adds a rule to the rule set.
	 *
	 * @param rule the rule to add
	 */
	public void addRule(final Rule rule) {
		if (getRules() == null) {
			setRules(new HashSet<Rule>());
		}
		getRules().add(rule);
	}

	/**
	 * Get the names of classes imported by this rule set.
	 *
	 * @see addImport
	 * @return the set of imports (fully qualified class names)
	 */
	@Transient
	public Set<String> getImports() {
		return IMPORTS;
	}

	/**
	 * Checks that the rule set domain model is well formed. For example, rule conditions must have all required parameters specified.
	 *
	 * @throws EpDomainException if the structure is not correct.
	 */
	public void validate() throws EpDomainException {
		if (getRules() == null || getRules().isEmpty()) {
			// throw new EpDomainException("A rule set must have at least one rule");
			return;
		}

		for (Rule currRule : getRules()) {
			currRule.validate();
		}
	}

	/**
	 * Returns the date when the rule set was last modified.
	 *
	 * @return the date when the rule set was last modified
	 */
	@Basic
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LAST_MODIFIED_DATE")
	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}

	/**
	 * Set the date when the rule set was last modified.
	 *
	 * @param lastModifiedDate the date when the rule set was last modified
	 */
	public void setLastModifiedDate(final Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

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

	@Transient
	@Override
	public String getGuid() {
		return guid;
	}

	@Override
	public void setGuid(final String guid) {
		this.guid = guid;
	}
}
