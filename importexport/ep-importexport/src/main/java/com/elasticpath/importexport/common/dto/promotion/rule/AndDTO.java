package com.elasticpath.importexport.common.dto.promotion.rule;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlRootElement;

import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.rules.RuleElement;
import com.elasticpath.importexport.common.adapters.DomainAdapter;

/**
 * Reflects composition of two <code>BooleanComponentDTO</code> objects united by AND operator.
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "and")
public class AndDTO implements BooleanComponentDTO {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@XmlElementRefs({
        @XmlElementRef(type = AndDTO.class),
        @XmlElementRef(type = ConditionDTO.class),
        @XmlElementRef(type = OrDTO.class)
    })
	private List<BooleanComponentDTO> components;

	@Override
	public List<BooleanComponentDTO> getComponents() {
		return components;
	}

	@Override
	public void setComponents(final List<BooleanComponentDTO> components) {
		this.components = components;
	}

	@Override
	public void populateDomainObject(final Rule rule, final DomainAdapter<RuleElement, ConditionDTO> adapter) {
		components.get(0).populateDomainObject(rule, adapter);
		if (components.size() > 1) {
			components.get(1).populateDomainObject(rule, adapter);
		}
	}

	@Override
	@SuppressWarnings("PMD.BooleanGetMethodName")
	public boolean getCompositeOperator() {
		return true;
	}
}
