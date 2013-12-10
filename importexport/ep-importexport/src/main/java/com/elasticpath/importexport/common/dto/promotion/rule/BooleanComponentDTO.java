package com.elasticpath.importexport.common.dto.promotion.rule;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import com.elasticpath.common.dto.Dto;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.rules.RuleElement;
import com.elasticpath.importexport.common.adapters.DomainAdapter;

/**
 * Component in Composition of conditions united by logical operators.
 * Designed to support recursive XML in JAXB
 */
@XmlAccessorType(XmlAccessType.NONE)
public interface BooleanComponentDTO extends Dto {

	/**
	 * Gets union operator.
	 * 
	 * @return true by default
	 */
	@SuppressWarnings("PMD.BooleanGetMethodName")
	boolean getCompositeOperator();

	/**
	 * Populates Rule with this component and its children.
	 * 
	 * @param rule domain object to populate
	 * @param adapter adapter populating domain objects
	 */
	void populateDomainObject(final Rule rule, final DomainAdapter<RuleElement, ConditionDTO> adapter);

	/**
	 * @param components the list of components to set
	 */
	void setComponents(final List<BooleanComponentDTO> components);

	/**
	 * @return the list of components
	 */
	List<BooleanComponentDTO> getComponents();
}
