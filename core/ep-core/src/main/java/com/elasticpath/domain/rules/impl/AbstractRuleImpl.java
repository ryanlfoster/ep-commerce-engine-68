/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.rules.impl; //NOPMD



import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.openjpa.persistence.DataCache;
import org.apache.openjpa.persistence.ElementDependent;
import org.apache.openjpa.persistence.FetchAttribute;
import org.apache.openjpa.persistence.FetchGroup;
import org.apache.openjpa.persistence.jdbc.ElementForeignKey;
import org.apache.openjpa.persistence.jdbc.ElementJoinColumn;
import org.apache.openjpa.persistence.jdbc.ElementJoinColumns;
import org.apache.openjpa.persistence.jdbc.ForeignKey;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.util.Pair;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.impl.CatalogImpl;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.cmuser.impl.CmUserImpl;
import com.elasticpath.domain.impl.AbstractLegacyEntityImpl;
import com.elasticpath.domain.misc.LocalizedProperties;
import com.elasticpath.domain.misc.LocalizedPropertyValue;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.rules.RuleAction;
import com.elasticpath.domain.rules.RuleCondition;
import com.elasticpath.domain.rules.RuleElement;
import com.elasticpath.domain.rules.RuleSet;
import com.elasticpath.domain.sellingcontext.SellingContext;
import com.elasticpath.domain.sellingcontext.impl.SellingContextImpl;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.impl.StoreImpl;
import com.elasticpath.persistence.support.FetchGroupConstants;
import com.elasticpath.tags.domain.Condition;
import com.elasticpath.tags.domain.ConditionalExpression;
import com.elasticpath.tags.domain.LogicalOperator;
import com.elasticpath.tags.domain.TagDictionary;
import com.elasticpath.tags.service.ConditionDSLBuilder;


/**
 * Represents a rule that can be applied by the rules engine. 
 * For example, promotion rules that execute the following may be implemented by
 * subclasses: "Give a 10% discount for all products in category X."
 * 
 * <b>NOTE:</b> this implementation is NOT THREAD SAFE and should not be used 
 * by multiple threads at one time.
 */
@MappedSuperclass
@SuppressWarnings({ "PMD.CyclomaticComplexity", "PMD.TooManyFields" })
@FetchGroup(name = FetchGroupConstants.PROMOTION_INDEX, attributes = { @FetchAttribute(name = "name"),
		@FetchAttribute(name = "enabled"), @FetchAttribute(name = "startDate"), @FetchAttribute(name = "endDate"),
		@FetchAttribute(name = "ruleSet"), @FetchAttribute(name = "store"), @FetchAttribute(name = "catalog"),
		@FetchAttribute(name = "sellingContext") })
@DataCache(enabled = false)
public abstract class AbstractRuleImpl extends AbstractLegacyEntityImpl implements Rule {
	
	private static final String LESS_THAN = "lessThan";
	private static final String GREATER_THAN = "greaterThan";

    private static final Pattern CHARS_NOT_ALLOWED = Pattern.compile("\"|[^ -\\xff]");
	
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TRULE";

	private String name;
	
	private Date lastModifiedDate;

	private Date startDate;

	private Date endDate;

	private RuleSet ruleSet;
	
	private Set<RuleElement> ruleElements = new HashSet<RuleElement>();

	private final Set<RuleCondition> conditions = new HashSet<RuleCondition>();

	private final Set<RuleAction> actions = new HashSet<RuleAction>();
	
	private boolean conditionOperator = Rule.AND_OPERATOR;

	private boolean eligibilityOperator = Rule.AND_OPERATOR;

	private String description;

	private Store store;
	
	private Catalog catalog;

	private CmUser cmUser;

	private boolean enabled;

	private long uidPk;
	
	private String code;
	
	/**
	 * How many times the Limited Usage Promotion was used.
	 */
	private long currentLupNumber;

	/**
	 * Has a setRuleElements been called since we last access the 
	 * conditions, actions or eligibilities?
	 */
	private boolean newRuleElementsSet = false;
	
	private SellingContext sellingContext;
	
	private LocalizedProperties localizedProperties;

	private Map<String, LocalizedPropertyValue> localizedPropertiesMap = new HashMap<String, LocalizedPropertyValue>();
	
	private Pair<Date, Date> sellingContextDates = null;	


	/**
	 * Get the starting date that this rule can be applied.
	 * 
	 * @return the start date
	 */
	@Basic
	@Column(name = "START_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getStartDate() {
		return this.startDate;
	}

	/**
	 * Set the starting date that this rule can be applied.
	 * 
	 * @param startDate the start date
	 * @throws EpDomainException if this method used for shopping cart / not defined scenario
	 */
	public void setStartDate(final Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * Get the end date. After the end date, the rule will no longer be applied.
	 * 
	 * @return the end date
	 */
	@Basic
	@Column(name = "END_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getEndDate() {
		return this.endDate;
	}

	/**
	 * Set the end date.
	 * 
	 * @param endDate the end date
	 * @throws EpDomainException if this method used for shopping cart / not defined scenario 
	 */
	public void setEndDate(final Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * Returns the Drools code corresponding to this rule.
	 * 
	 * @return the rule code.
	 */
	@Transient
	public abstract String getRuleCode();

	/**
	 * Get the conditions associated with this rule.
	 * 
	 * @return the conditions
	 */
	@Transient
	public Set<RuleCondition> getConditions() {
		initConditionActionEligibilityIfNeeded();
		return Collections.unmodifiableSet(this.conditions);
	}

	/**
	 * Gets the operator (AND/OR) if there are multiple conditions.
	 * 
	 * @return the eligibility condition operator (ANR/OR)
	 */
	@Basic
	@Column(name = "CONDITION_OPERATOR")
	@SuppressWarnings("PMD.BooleanGetMethodName")
	public boolean getConditionOperator() {
		return this.conditionOperator;
	}

	/**
	 * Sets the operator (AND/OR) if there are multiple eligibility conditions.
	 * 
	 * @param conditionOperator - the condition operator (ANR/OR).
	 */
	public void setConditionOperator(final boolean conditionOperator) {
		this.conditionOperator = conditionOperator;
	}

	/**
	 * Adds a condition to the set of conditions.
	 * 
	 * @param condition the condition to add
	 * @throws EpDomainException if anything goes wrong.
	 */
	public void addCondition(final RuleCondition condition) {
		addRuleElement(condition);
	}
	
	/**
	 * Removes the given <code>RuleCondition</code> from the set of rule conditions.
	 * 
	 * @param ruleCondition the <code>RuleCondition</code> to remove
	 * @throws EpDomainException if anything goes wrong.
	 */
	public void removeCondition(final RuleCondition ruleCondition) {
		removeRuleElement(ruleCondition);
	}

	/**
	 * Get the actions associated with this rule.
	 * 
	 * @return the actions
	 */
	@Transient
	public Set<RuleAction> getActions() {
		initConditionActionEligibilityIfNeeded();
		return Collections.unmodifiableSet(this.actions);
	}

	/**
	 * Add an action to the rule.
	 * 
	 * @param ruleAction the action to add.
	 * @throws EpDomainException if anything goes wrong.
	 */
	public void addAction(final RuleAction ruleAction) {
		addRuleElement(ruleAction);
	}
	
	/**
	 * Removes the given <code>RuleAction</code> from the set of rule actions.
	 * 
	 * @param ruleAction the <code>RuleAction</code> to remove
	 * @throws EpDomainException if anything goes wrong.
	 */
	public void removeAction(final RuleAction ruleAction) {
		removeRuleElement(ruleAction);
	}

	/**
	 * Get the rule elements associated with this rule.
	 * 
	 * @return the rule elements
	 */
	@OneToMany(targetEntity = AbstractRuleElementImpl.class, cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	@ElementJoinColumn(name = "RULE_UID")
	@ElementDependent
	@ElementForeignKey(name = "TRULEELEMENT_IBFK_1")
	public Set<RuleElement> getRuleElements() {
		return this.ruleElements;
	}

	/**
	 * Set the rule elements of this rule.
	 * 
	 * @param ruleElements a set of <code>RuleElement</code> objects
	 */
	public void setRuleElements(final Set<RuleElement> ruleElements) {
		this.ruleElements = ruleElements;
		this.newRuleElementsSet = true;
	}

	private void initConditionActionEligibilityIfNeeded() {		
		if (!newRuleElementsSet) {
			return;
		}
		
		newRuleElementsSet = false;
		
		conditions.clear();
		actions.clear();

		for (final RuleElement ruleElement : getRuleElements()) {
			addToTransientBucket(ruleElement);
		}
	}

	private void addToTransientBucket(final RuleElement ruleElement) {
		// Need to set the RuleElement's ruleId since it is stored in a transient field
		ruleElement.setRuleId(getUidPk());

		if (RuleCondition.CONDITION_KIND.equals(ruleElement.getKind())) {
			conditions.add((RuleCondition) ruleElement);
		} else if (RuleAction.ACTION_KIND.equals(ruleElement.getKind())) {
			// actiion salience is configured in spring config domainModel.xml
			RuleAction action = getBean(ruleElement.getType());
			// update transient value from spring setting 
			((RuleAction) ruleElement).setSalience(action.getSalience());
			actions.add((RuleAction) ruleElement);
		}
	}

	private void removeFromTransientBucket(final RuleElement ruleElement) {
		if (ruleElement.getKind().equals(RuleCondition.CONDITION_KIND)) {
			if ((this.conditions == null) || (this.conditions.size() == 0)) {
				throw new EpDomainException("Rule does not contain any rule conditions.");
			} else if (!this.conditions.contains(ruleElement)) {
				throw new EpDomainException("Rule does not contain given rule conditions.");
			}
			conditions.remove(ruleElement);
		} else if (ruleElement.getKind().equals(RuleAction.ACTION_KIND)) {
			if ((this.actions == null) || (this.actions.size() == 0)) {
				throw new EpDomainException("Rule does not contain any rule actions.");
			} else if (!this.actions.contains(ruleElement)) {
				throw new EpDomainException("Rule does not contain given rule action.");
			}
			actions.remove(ruleElement);
		}
	}

	/**
	 * Adds a rule element to the set of rule elements.
	 * 
	 * @param ruleElement the <code>RuleElement</code> to add
	 * @throws EpDomainException if anything goes wrong.
	 */
	public void addRuleElement(final RuleElement ruleElement) {
		if (ruleElement == null) {
			throw new EpDomainException("Can not add a null rule ruleElement.");
		}
		addToTransientBucket(ruleElement);
		getRuleElements().add(ruleElement);
	}

	/**
	 * Removes the given <code>RuleElement</code> from the set of rule elements.
	 * 
	 * @param ruleElement the <code>RuleElement</code> to remove
	 * @throws EpDomainException if anything goes wrong.
	 */
	public void removeRuleElement(final RuleElement ruleElement) {
		if (ruleElement == null) {
			throw new EpDomainException("Can not remove a null rule element.");
		}
		
		if ((getRuleElements() == null) || (this.getRuleElements().size() == 0)) {
			throw new EpDomainException("Rule does not contain any rule elements.");
		} else if (!getRuleElements().contains(ruleElement)) {
			throw new EpDomainException("Rule does not contain given rule element.");
		}
		removeFromTransientBucket(ruleElement);
		getRuleElements().remove(ruleElement);
	}

	/**
	 * Gets the operator (AND/OR) if there are multiple eligibility conditions.
	 * 
	 * @return the eligibility condition operator (ANR/OR)
	 */
	@Basic
	@Column(name = "ELIGIBILITY_OPERATOR")
	@SuppressWarnings("PMD.BooleanGetMethodName")
	public boolean getEligibilityOperator() {
		return this.eligibilityOperator;
	}

	/**
	 * Sets the operator (AND/OR) if there are multiple eligibility conditions.
	 * 
	 * @param eligibilityOperator - the eligibility condition operator (ANR/OR).
	 */
	public void setEligibilityOperator(final boolean eligibilityOperator) {
		this.eligibilityOperator = eligibilityOperator;
	}

	/**
	 * Get the name of this rule.
	 * 
	 * @return the name of the rule
	 */
	@Basic
	@Column(name = "NAME", unique = true)
	public String getName() {
		return name;
	}

	/**
	 * Set the name of the rule.
	 * 
	 * @param name the name of the rule
	 */
	public void setName(final String name) {
		if (name != null) {
			// replace any character outside of range [space] to \xff, and "
			this.name = CHARS_NOT_ALLOWED.matcher(name).replaceAll("").trim();
		}
	}

	/**
	 * Return the rule code.
	 *
	 * @return the rule code.
	 */
	@Basic
	@Column(name = "RULECODE")
	public String getCode() {
		return code;
	}

	/**
	 * Set the rule code.
	 *
	 * @param code the rule code.
	 */
	public void setCode(final String code) {
		this.code = code;
	}
	
	@Override
	@Transient
	public String getGuid() {
		return getCode();
	}
	
	@Override
	public void setGuid(final String guid) {
		setCode(guid);
	}


	/**
	 * Get the description of this rule.
	 * 
	 * @return the description of the rule
	 */
	@Basic
	@Column(name = "DESCRIPTION")
	public String getDescription() {
		return this.description;
	}

	/**
	 * Set the description of the rule.
	 * 
	 * @param description the description of the rule
	 */
	public void setDescription(final String description) {
		if (description == null) {
			return;
		}
		this.description = description;
	}

	/**
	 * Gets the <code>Store</code> associated with this rule.
	 * 
	 * @return the <code>Store</code> associated with this rule
	 */
	@ManyToOne(targetEntity = StoreImpl.class)
	@JoinColumn(name = "STORE_UID")
	public Store getStore() {
		return this.store;
	}

	/**
	 * Set the <code>Store</code> associated with this the rule.
	 * 
	 * @param store the <code>Store</code> associated with this the rule.
	 */
	public void setStore(final Store store) {
		this.store = store;
	}

	/**
	 * Gets the <code>CmUser</code> that created this rule.
	 * 
	 * @return the <code>CmUser</code> that created this rule
	 */
	@ManyToOne(targetEntity = CmUserImpl.class)
	@JoinColumn(name = "CM_USER_UID")
	public CmUser getCmUser() {
		return this.cmUser;
	}

	/**
	 * Set the <code>CmUser</code> that created of the rule.
	 * 
	 * @param cmUser the <code>CmUser</code> that created of the rule.
	 */
	public void setCmUser(final CmUser cmUser) {
		this.cmUser = cmUser;
	}

	@Override
	public boolean isWithinDateRange(final Date date) {
		Date startDate = getStartDate();
		if (startDate == null) {
			startDate = getStartDateFromSellingContext();
		}

		Date endDate = getEndDate();
		if (endDate == null) {
			endDate = getEndDateFromSellingContext();
		}

		return (startDate == null || date.after(startDate)) && (endDate == null || date.before(endDate));
	}

	@Override
	public boolean isWithinDateRange() {
		return isWithinDateRange(new Date());
	}

	/**
	 * Get the starting date from selling context 
	 * that this rule can be applied.
	 * @return the start date
	 */
	@Transient
	public Date getStartDateFromSellingContext() {
		if (sellingContextDates == null) {
			extractSellingContextDates();
		}
		return sellingContextDates.getFirst();
	}
	
	/**
	 * Get the end date from selling context. After the end date, the rule will no longer be applied.
	 * @return the end date
	 */
	@Transient
	public Date getEndDateFromSellingContext() {
		if (sellingContextDates == null) {
			extractSellingContextDates();
		}			
		return sellingContextDates.getSecond();
	}
	
	/**
	 * Extract Start date and End date from {@link LogicalOperator}.
	 */
	private void extractSellingContextDates() {
		Date sellingContextStartDate = null;
		Date sellingContextEndDate = null;
		if (this.getSellingContext() != null) {
			final ConditionalExpression expression = this.getSellingContext().getCondition(TagDictionary.DICTIONARY_TIME_GUID);
			if (expression != null) {
				
				final LogicalOperator logicalOperator = 
					getConditionDSLBuilder().getLogicalOperationTree(expression.getConditionString());
				
				final Set<Condition> conditions = 
					logicalOperator.getConditions();
				
				for (Condition condition : conditions) {
					String operator = condition.getOperator();
					
					Date conditionDate = new Date();
					conditionDate.setTime((Long) condition.getTagValue());

					if (GREATER_THAN.equals(operator)) {
						sellingContextStartDate = conditionDate;
					}
					
					if (LESS_THAN.equals(operator)) {				
						sellingContextEndDate = conditionDate;
					}
				}
			}
		}
		this.sellingContextDates = new Pair<Date, Date>(sellingContextStartDate, sellingContextEndDate);
	}
	
	@Transient
	private ConditionDSLBuilder getConditionDSLBuilder() {
		return getBean(ContextIdNames.TAG_CONDITION_DSL_BUILDER);
	}
	
	
	

	/**
	 * Returns <code>true</code> if this rule is enabled, <code>false</code> if it is disabled.
	 * 
	 * @return <code>true</code> if this rule is enabled, <code>false</code> if it is disabled
	 */
	@Basic
	@Column(name = "ENABLED")
	public boolean isEnabled() {
		return this.enabled;
	}

	/**
	 * Set the state of the rule. <code>true</code> sets the state to enabled, <code>false</code> sets the state to disabled.
	 * 
	 * @param enabledState the state to set
	 */
	public void setEnabled(final boolean enabledState) {
		this.enabled = enabledState;
	}

	/**
	 * Checks that the rule set domain model is well formed. For example, rule conditions must have all required parameters specified.
	 * 
	 * @throws EpDomainException if the structure is not correct.
	 */
	@SuppressWarnings("PMD.CyclomaticComplexity")
	public void validate() throws EpDomainException {

		// Validate start and end dates
		if (getEndDate() != null && getStartDate() != null && getEndDate().getTime() < getStartDate().getTime()) {
			throw new EpDomainException("End date cannot be before the start date.");
		}
		
		// Validate all rule conditions
		for (RuleCondition currCondition : getConditions()) {
			currCondition.validate();
		}

		// Validate all rule actions
		if (getActions() == null || getActions().isEmpty()) {
			throw new EpDomainException("A rule must have at least one action.");
		}

		for (RuleAction currAction : getActions()) {
			currAction.validate();
		}
	}

	/**
	 * Get the ruleSet this rule belongs to.
	 * 
	 * @return the ruleSet it belongs to.
	 */
	@ManyToOne(targetEntity = RuleSetImpl.class, fetch = FetchType.EAGER)
	@JoinColumn(name = "RULE_SET_UID")
	public RuleSet getRuleSet() {
		return this.ruleSet;
	}

	/**
	 * Set the ruleSet this rule belongs to.
	 * 
	 * @param ruleSet the ruleSet it belongs to.
	 */
	public void setRuleSet(final RuleSet ruleSet) {
		this.ruleSet = ruleSet;
	}

	/**
	 * Returns the salience value of this rule. The salience is computed as the highest salience value found among the actions in the rule.
	 * 
	 * @return the highest salience value
	 */
	@Transient
	protected int getSalience() {
		int maxSalience = Integer.MIN_VALUE;
		for (RuleAction currAction : this.actions) {
			if (currAction.getSalience() > maxSalience) {
				maxSalience = currAction.getSalience();
			}
		}
		return maxSalience;
	}

	/**
	 * Returns the collection of actions in decreasing order of salience.
	 * 
	 * @return a <code>List</code> of <code>RuleAction</code>s
	 */
	@Transient
	protected List<RuleAction> getActionsBySalience() {
		List<RuleAction> actionList = new ArrayList<RuleAction>();
		actionList.addAll(this.getActions());
		Collections.sort(actionList, new Comparator<RuleAction>() {

			public int compare(final RuleAction action1, final RuleAction action2) {
				if (action1.getSalience() > action2.getSalience()) {
					return -1;
				} else if (action1.getSalience() < action2.getSalience()) {
					return 1;
				}
				return 0;
			}

		});
		return actionList;
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
	
	/**
	 * Get the date that the rule was last modified on.
	 * 
	 * @return the last modified date
	 */
	@Basic
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LAST_MODIFIED_DATE", nullable = false)
	public Date getLastModifiedDate() {
		return this.lastModifiedDate;
	}

	/**
	 * Set the date that the rule was last modified on.
	 * 
	 * @param lastModifiedDate the date that the order was last modified
	 */
	public void setLastModifiedDate(final Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
	
	/**
	 * Gets the {@link Catalog} associated with this rule. If this promotion is a store promotion,
	 * the catalog of the store is returned.
	 * 
	 * @return the {@link Catalog} associated with this rule
	 */
	@ManyToOne(targetEntity = CatalogImpl.class)
	@JoinColumn(name = "CATALOG_UID")
	public Catalog getCatalog() {
		return catalog;
	}
	
	/**
	 * Set the {@link Catalog} associated with this the rule. This clears the associated rule
	 * store.
	 * 
	 * @param catalog the {@link Catalog} associated with this the rule
	 */
	public void setCatalog(final Catalog catalog) {
		this.catalog = catalog;
	}

	/**
	 *
	 * @return the currentLupNumber
	 */
	@Basic
	@Column(name = "CURRENT_LUP_NUMBER")
	public long getCurrentLupNumber() {
		return currentLupNumber;
	}

	/**
	 *
	 * @param currentLupNumber the currentLupNumber to set
	 */
	public void setCurrentLupNumber(final long currentLupNumber) {
		this.currentLupNumber = currentLupNumber;
	}

	/** 
	 * Get the selling context. Rule can have no SC to be backward 
	 * compatible so this column is optional and nullable.
	 * 
	 * @return Selling context
	 */	
	@OneToOne(targetEntity = SellingContextImpl.class, cascade = {
		CascadeType.REFRESH, CascadeType.MERGE, CascadeType.REMOVE, CascadeType.PERSIST }, 
			optional = true, fetch = FetchType.EAGER) 
	@JoinColumn(name = "SELLING_CTX_UID", nullable = true) 
	@ForeignKey(name = "SCRULE_FK")
	public SellingContext getSellingContext() {
		return sellingContext;
	}

	/**
	 * Sets the selling context.
	 * 
	 * @param sellingContext selling context to set
	 */	
	public void setSellingContext(final SellingContext sellingContext) {
		this.sellingContext = sellingContext;
	}

	/**
	 *
	 * @param localizedPropertiesMap the localizedPropertiesMap to set
	 */
	protected void setLocalizedPropertiesMap(final Map<String, LocalizedPropertyValue> localizedPropertiesMap) {
		this.localizedPropertiesMap = localizedPropertiesMap;
	}

	/**
	 * Get the localized properties map.
	 * @return the map
	 */
	@OneToMany(targetEntity = RuleLocalizedPropertyValueImpl.class, fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
	@MapKey(name = "localizedPropertyKey")
	@ElementJoinColumns({
		@ElementJoinColumn(name = "OBJECT_UID", referencedColumnName = "UIDPK", nullable = false),
		@ElementJoinColumn(name = "TLOCALIZEDPROPERTIES.TYPE", referencedColumnName = "'Rule'")
	})
	@ElementDependent
	protected Map<String, LocalizedPropertyValue> getLocalizedPropertiesMap() {
		return localizedPropertiesMap;
	}

	/**
	 * Returns the <code>LocalizedProperties</code>.
	 *
	 * @return the <code>LocalizedProperties</code>
	 */
	@Transient
	public LocalizedProperties getLocalizedProperties() {
		if (localizedProperties == null) {
			localizedProperties = getBean(ContextIdNames.LOCALIZED_PROPERTIES);
			localizedProperties.setLocalizedPropertiesMap(getLocalizedPropertiesMap(), ContextIdNames.RULE_LOCALIZED_PROPERTY_VALUE);
		}
		return localizedProperties;
	}

	/**
	 * Sets the <code>LocalizedProperties</code>.
	 *
	 * @param localizedProperties the <code>LocalizedProperties</code>
	 */
	public void setLocalizedProperties(final LocalizedProperties localizedProperties) {
		this.localizedProperties = localizedProperties;
		if (localizedProperties == null) {
			setLocalizedPropertiesMap(null);
		} else {
			setLocalizedPropertiesMap(localizedProperties.getLocalizedPropertiesMap());
		}
	}

	/**
	 * Returns the display name of the <code>TaxCategory</code> with the given locale.
	 * 
	 * @param locale the locale
	 * @return the display name of the taxCategory displayName
	 */
	public String getDisplayName(final Locale locale) {
		String displayName = null;
		if (getLocalizedProperties() != null) {
			displayName = getLocalizedProperties().getValue(Rule.LOCALIZED_PROPERTY_DISPLAY_NAME, locale);
		}
		return displayName;
	}
	
}
