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

import org.apache.openjpa.persistence.DataCache;
import org.apache.openjpa.persistence.ElementDependent;
import org.apache.openjpa.persistence.jdbc.ElementForeignKey;
import org.apache.openjpa.persistence.jdbc.ElementJoinColumn;

import com.elasticpath.base.Initializable;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.rules.RuleException;
import com.elasticpath.domain.rules.RuleExceptionType;
import com.elasticpath.domain.rules.RuleParameter;
import com.elasticpath.persistence.api.AbstractPersistableImpl;

/**
 * Abstract class with behaviour common to all rule elements.
 */
@Entity
@Table(name = AbstractRuleExceptionImpl.TABLE_NAME)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TYPE", discriminatorType = DiscriminatorType.STRING)
@DataCache(enabled = false)
public class AbstractRuleExceptionImpl extends AbstractPersistableImpl implements RuleException, Initializable {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TRULEEXCEPTION";

	private String type;

	private Set<RuleParameter> parameters = new HashSet<RuleParameter>();

	private long uidPk;

	/**
	 * Must be implemented by subclasses. Returns the <code>RuleExceptionType</code> associated with this <code>RuleException</code> subclass. The
	 * <code>RuleExceptionType</code>'s property key must match this class' discriminator-value and the spring context bean id for this
	 * <code>RuleException</code> implementation.
	 * 
	 * @return the <code>RuleExceptionType</code> associated with this <code>RuleException</code> subclass.
	 */
	@Transient
	public RuleExceptionType getExceptionType() {
		// the subclass must implement this method; we define this method concrete to make JPA work.
		return null;
	}

	/**
	 * Get the type of this <code>RuleException</code>. (e.g. skuException. Should match spring bean factory bean id.)
	 *
	 * @return the type
	 */
	@Basic
	@Column(name = "TYPE", nullable = false, insertable = false, updatable = false)
	public String getType() {
		if (this.type == null) {
			this.type = getExceptionType().getPropertyKey();
		}
		return this.type;
	}

	/**
	 * Set the type of element. (e.g. skuException. Should match bean name)
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
	@Transient
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
	 * Checks that the rule set domain model is well formed. For example, rule conditions must have all required parameters specified.
	 *
	 * @throws EpDomainException if the structure is not correct.
	 */
	public void validate() throws EpDomainException {
		String[] keys = getParameterKeys();
		for (int i = 0; i < keys.length; i++) {
			final String paramValue = getParamValue(keys[i]);
			if (paramValue == null || paramValue.equals("")) {
				throw new EpDomainException("Rule element " + this.getType() + " must have parameter with key " + keys[i]);
			}
		}
	}

	/**
	 * Get the parameters associated with this rule condition.
	 *
	 * @return the parameters
	 */
	@OneToMany(targetEntity = RuleParameterImpl.class, fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
	@ElementJoinColumn(name = "RULE_EXCEPTION_UID")
	@ElementDependent
	@ElementForeignKey(name = "TRULEPARAMETER_IBFK_1")
	public Set<RuleParameter> getParameters() {
		return this.parameters;
	}

	/**
	 * Set the parameters of this rule condition.
	 *
	 * @param parameters a set of <code>RuleParameter</code> objects
	 */
	public void setParameters(final Set<RuleParameter> parameters) {
		this.parameters = parameters;
	}

	/**
	 * Add a parameter of this rule condition.
	 *
	 * @param ruleParameter a <code>RuleParameter</code> object
	 */
	public void addParameter(final RuleParameter ruleParameter) {
		getParameters().add(ruleParameter);
	}

	/**
	 * Sets default values.
	 */
	@Override
	public void initialize() {
		// initialize exception parameters
		final String[] paramKeys = this.getParameterKeys();
		if (paramKeys != null) {
			for (final String currParamKey : paramKeys) {
				final RuleParameter currRuleParameter = new RuleParameterImpl();
				currRuleParameter.setKey(currParamKey);
				getParameters().add(currRuleParameter);
			}
		}
	}

	/**
	 * Check if this <code>RuleException</code> is valid in the specified scenario.
	 *
	 * @param scenarioId the Id of the scenario to check (defined in RuleScenarios)
	 * @return true if the <code>RuleException</code> is applicable in the given scenario
	 */
	public boolean appliesInScenario(final int scenarioId) {
//		Sub class must overide this method. We make it concrete for jpa.
		return false;
	}

	/**
	 * Return the array of the required parameter keys for the rule.
	 *
	 * @return an array of String of the required parameter keys for the rule.
	 */
	@Transient
	public String[] getParameterKeys() {
//		Sub class must overide this method. We make it concrete for jpa.
		return null;
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
}