package com.elasticpath.importexport.importer.importers.impl;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.rules.RuleAction;
import com.elasticpath.domain.rules.RuleCondition;
import com.elasticpath.domain.rules.RuleElement;
import com.elasticpath.domain.rules.RuleSet;
import com.elasticpath.domain.rules.impl.AbstractRuleImpl;
import com.elasticpath.domain.store.Store;
import com.elasticpath.importexport.importer.importers.RuleWrapper;

/**
 * Used as old rule containing its updated version as a field.
 */
public class RuleWrapperImpl extends AbstractRuleImpl implements RuleWrapper {

	private static final long serialVersionUID = 1L;

	private Rule wrappedRule;

	private Rule originalRule;

	/**
	 * @return last version of rule which can be updated
	 */
	public Rule getUpdatedRule() {
		return getWrappedRule();
	}

	/**
	 * @param rule new rule encapsulated by this wrapper.
	 */
	public void setUpdatedRule(final Rule rule) {
		wrappedRule = rule;
	}

	/**
	 * @return old version of rule stored as a snapshot
	 */
	public Rule getOldRule() {
		return originalRule;
	}

	/**
	 * Takes snapshot of the fresh rule and stores it in old rule.
	 */
	public void takeSnapshot() {
		originalRule = getBean(ContextIdNames.PROMOTION_RULE);

		for (RuleAction action : getWrappedRule().getActions()) {
			originalRule.addAction(action);
		}
		originalRule.setCatalog(getWrappedRule().getCatalog());
		originalRule.setCmUser(getWrappedRule().getCmUser());
		originalRule.setCode(getWrappedRule().getCode());
		originalRule.setConditionOperator(getWrappedRule().getConditionOperator());
		for (RuleCondition condition : getWrappedRule().getConditions()) {
			originalRule.addCondition(condition);
		}
		originalRule.setCurrentLupNumber(getWrappedRule().getCurrentLupNumber());
		originalRule.setDescription(getWrappedRule().getDescription());
		originalRule.setEligibilityOperator(getWrappedRule().getEligibilityOperator());

		originalRule.setEnabled(getWrappedRule().isEnabled());
		originalRule.setEndDate(getWrappedRule().getEndDate());
		originalRule.setGuid(getGuid());
		originalRule.setLastModifiedDate(getWrappedRule().getLastModifiedDate());
		originalRule.setName(getWrappedRule().getName());
		originalRule.setCouponEnabled(isCouponEnabled());
		originalRule.setRuleElements(new HashSet<RuleElement>(getWrappedRule().getRuleElements()));
		originalRule.setRuleSet(getWrappedRule().getRuleSet());
		originalRule.setStartDate(getWrappedRule().getStartDate());
		originalRule.setStore(getWrappedRule().getStore());
		originalRule.setUidPk(getWrappedRule().getUidPk());
	}

	/**
	 * @return this.delegate if not null
	 */
	private Rule getWrappedRule() {
		if (wrappedRule == null) {
			wrappedRule = getBean(ContextIdNames.PROMOTION_RULE);
		}
		return wrappedRule;
	}

	@Override
	public void addAction(final RuleAction ruleAction) {
		getWrappedRule().addAction(ruleAction);
	}

	@Override
	public void addCondition(final RuleCondition condition) {
		getWrappedRule().addCondition(condition);
	}

	@Override
	public void addRuleElement(final RuleElement ruleElement) {
		getWrappedRule().addRuleElement(ruleElement);
	}

	@Override
	public Set<RuleAction> getActions() {
		return getWrappedRule().getActions();
	}

	@Override
	public Catalog getCatalog() {
		return getWrappedRule().getCatalog();
	}

	@Override
	public CmUser getCmUser() {
		return getWrappedRule().getCmUser();
	}

	@Override
	public String getCode() {
		return getWrappedRule().getCode();
	}

	@Override
	@SuppressWarnings("PMD.BooleanGetMethodName")
	public boolean getConditionOperator() {
		return getWrappedRule().getConditionOperator();
	}

	@Override
	public Set<RuleCondition> getConditions() {
		return getWrappedRule().getConditions();
	}

	@Override
	public long getCurrentLupNumber() {
		return getWrappedRule().getCurrentLupNumber();
	}

	@Override
	public String getDescription() {
		return getWrappedRule().getDescription();
	}

	@Override
	@SuppressWarnings("PMD.BooleanGetMethodName")
	public boolean getEligibilityOperator() {
		return getWrappedRule().getEligibilityOperator();
	}

	@Override
	public Date getEndDate() {
		return getWrappedRule().getEndDate();
	}

	@Override
	public Date getLastModifiedDate() {
		return getWrappedRule().getLastModifiedDate();
	}

	@Override
	public String getName() {
		return getWrappedRule().getName();
	}

	@Override
	public String getRuleCode() {
		return getWrappedRule().getRuleCode();
	}

	@Override
	public Set<RuleElement> getRuleElements() {
		return getWrappedRule().getRuleElements();
	}

	@Override
	public RuleSet getRuleSet() {
		return getWrappedRule().getRuleSet();
	}

	@Override
	public Date getStartDate() {
		return getWrappedRule().getStartDate();
	}

	@Override
	public Store getStore() {
		return getWrappedRule().getStore();
	}

	@Override
	public long getUidPk() {
		return getWrappedRule().getUidPk();
	}

	@Override
	public boolean isEnabled() {
		return getWrappedRule().isEnabled();
	}

	@Override
	public void removeAction(final RuleAction ruleAction) {
		getWrappedRule().removeAction(ruleAction);
	}

	@Override
	public void removeCondition(final RuleCondition ruleCondition) {
		getWrappedRule().removeCondition(ruleCondition);
	}

	@Override
	public void removeRuleElement(final RuleElement ruleElement) {
		((AbstractRuleImpl) getWrappedRule()).removeRuleElement(ruleElement);
	}

	@Override
	public void setCatalog(final Catalog catalog) {
		getWrappedRule().setCatalog(catalog);
	}

	@Override
	public void setCmUser(final CmUser cmUser) {
		getWrappedRule().setCmUser(cmUser);
	}

	@Override
	public void setCode(final String code) {
		getWrappedRule().setCode(code);
	}

	@Override
	public void setConditionOperator(final boolean conditionOperator) {
		getWrappedRule().setConditionOperator(conditionOperator);
	}

	@Override
	public void setCurrentLupNumber(final long currentLupNumber) {
		getWrappedRule().setCurrentLupNumber(currentLupNumber);
	}

	@Override
	public void setDescription(final String description) {
		getWrappedRule().setDescription(description);
	}

	@Override
	public void setEligibilityOperator(final boolean eligibilityOperator) {
		getWrappedRule().setEligibilityOperator(eligibilityOperator);
	}

	@Override
	public void setEnabled(final boolean enabledState) {
		getWrappedRule().setEnabled(enabledState);
	}

	@Override
	public void setEndDate(final Date endDate) {
		getWrappedRule().setEndDate(endDate);
	}

	@Override
	public void setLastModifiedDate(final Date lastModifiedDate) {
		getWrappedRule().setLastModifiedDate(lastModifiedDate);
	}

	@Override
	public void setName(final String name) {
		getWrappedRule().setName(name);
	}

	@Override
	public void setRuleElements(final Set<RuleElement> ruleElements) {
		getWrappedRule().setRuleElements(ruleElements);
	}

	@Override
	public void setRuleSet(final RuleSet ruleSet) {
		getWrappedRule().setRuleSet(ruleSet);
	}

	@Override
	public void setStartDate(final Date startDate) {
		getWrappedRule().setStartDate(startDate);
	}

	@Override
	public void setStore(final Store store) {
		getWrappedRule().setStore(store);
	}

	@Override
	public void setUidPk(final long uidPk) {
		getWrappedRule().setUidPk(uidPk);
	}

	@Override
	public void validate() throws EpDomainException {
		getWrappedRule().validate();
	}

	@Override
	public String getGuid() {
		return getWrappedRule().getGuid();
	}

	@Override
	public void setGuid(final String guid) {
		getWrappedRule().setGuid(guid);
	}

	@Override
	public boolean isPersisted() {
		return getWrappedRule().isPersisted();
	}

	@Override
	public boolean equals(final Object obj) {
		return getWrappedRule().equals(obj);
	}

	@Override
	public int hashCode() {
		return getWrappedRule().hashCode();
	}

	@Override
	public String toString() {
		return getWrappedRule().toString();
	}

	@Override
	public boolean isCouponEnabled() {
		return getWrappedRule().isCouponEnabled();
	}

	@Override
	public void setCouponEnabled(final boolean couponEnabled) {
		getWrappedRule().setCouponEnabled(couponEnabled);
	}
}
