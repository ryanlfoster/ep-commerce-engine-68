package com.elasticpath.importexport.common.dto.promotion.rule;

import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.elasticpath.domain.impl.ElasticPathImpl;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.rules.RuleCondition;
import com.elasticpath.domain.rules.RuleElement;
import com.elasticpath.domain.rules.RuleElementType;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.dto.promotion.ExceptionDTO;
import com.elasticpath.importexport.common.dto.promotion.ParameterDTO;
import com.elasticpath.importexport.common.exception.runtime.PopulationRuntimeException;

/**
 * Contains XML mapping for <code>RuleElement</code> domain object.
 * Designed for JAXB
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "condition")
public class ConditionDTO implements BooleanComponentDTO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@XmlElement(name = "kind", required = true)
	private String kind;

	@XmlElement(name = "type", required = true)
	private String type;

	@XmlElementWrapper(name = "parameters")
	@XmlElement(name = "parameter")
	private List<ParameterDTO> parameters;

	@XmlElementWrapper(name = "exceptions")
	@XmlElement(name = "exception")
	private List<ExceptionDTO> exceptions;

	/**
	 * Gets rule element type.
	 * 
	 * @return rule element type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets rule element type.
	 * 
	 * @param type the type to set
	 */
	public void setType(final String type) {
		this.type = type;
	}

	/**
	 * Gets rule element kind.
	 * 
	 * @return rule element kind
	 */
	public String getKind() {
		return kind;
	}

	/**
	 * Sets rule element kind.
	 * 
	 * @param kind the kind to set
	 */
	public void setKind(final String kind) {
		this.kind = kind;
	}

	/**
	 * Gets rule element parameters.
	 * 
	 * @return the parameters
	 */
	public List<ParameterDTO> getParameters() {
		if (parameters == null) {
			return Collections.emptyList();
		}
		return parameters;
	}

	/**
	 * Sets rule element parameters.
	 * 
	 * @param parameters the parameters to set
	 */
	public void setParameters(final List<ParameterDTO> parameters) {
		this.parameters = parameters;
	}

	/**
	 * Gets rule exceptions.
	 * 
	 * @return the exceptions
	 */
	public List<ExceptionDTO> getExceptions() {
		if (exceptions == null) {
			return Collections.emptyList();
		}
		return exceptions;
	}

	/**
	 * Sets rule exceptions.
	 * 
	 * @param exceptions the exceptions to set
	 */
	public void setExceptions(final List<ExceptionDTO> exceptions) {
		this.exceptions = exceptions;
	}

	@Override
	@SuppressWarnings("PMD.BooleanGetMethodName")
	public boolean getCompositeOperator() {
		return true;
	}

	@Override
	public void populateDomainObject(final Rule rule, final DomainAdapter<RuleElement, ConditionDTO> adapter) {
		if (RuleCondition.CONDITION_KIND.equals(kind) && RuleElementType.LIMITED_USE_COUPON_CODE_CONDITION.equals(type)) {
			rule.setCouponEnabled(true);
		} else {
			final RuleElement ruleElement = createRuleElement(this.type);
			adapter.populateDomain(this, ruleElement);
			rule.addRuleElement(ruleElement);
		}
	}

	@SuppressWarnings("PMD.DontUseElasticPathImplGetInstance")
	private RuleElement createRuleElement(final String elementKind) {
		final RuleElement ruleElement = ElasticPathImpl.getInstance().getBean(elementKind);

		if (ruleElement == null) {
			throw new PopulationRuntimeException("IE-10708", elementKind);
		}

		return ruleElement;
	}

	@Override
	public List<BooleanComponentDTO> getComponents() {
		throw new PopulationRuntimeException("IE-10709");
	}

	@Override
	public void setComponents(final List<BooleanComponentDTO> components) {
		throw new PopulationRuntimeException("IE-10710");
	}	
}
