package com.elasticpath.importexport.common.dto.promotion.rule;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlType;

import com.elasticpath.common.dto.Dto;

/**
 * Encloses the list of conditions combined into conjunction or disjunction.
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "BooleanOperand")
public class ConditionsDTO implements Dto {

	private static final long serialVersionUID = 1L;

	@XmlElementRefs({ @XmlElementRef(type = AndDTO.class), @XmlElementRef(type = ConditionDTO.class), @XmlElementRef(type = OrDTO.class) })
	private BooleanComponentDTO conditionsComposite;

	/**
	 * @return the conditionsComposite
	 */
	public BooleanComponentDTO getConditionsComposite() {
		return conditionsComposite;
	}

	/**
	 * @param conditionsComponent the conditionsComposite to set
	 */
	public void setConditionsComponent(final BooleanComponentDTO conditionsComponent) {
		this.conditionsComposite = conditionsComponent;
	}
}
