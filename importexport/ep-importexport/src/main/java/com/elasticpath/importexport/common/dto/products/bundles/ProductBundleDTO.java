package com.elasticpath.importexport.common.dto.products.bundles;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.elasticpath.common.dto.Dto;

/**
 * The implementation of the <code>Dto</code> interface that contains data of product bundle mapping.
 */
@XmlRootElement(name = ProductBundleDTO.ROOT_ELEMENT)
@XmlAccessorType(XmlAccessType.NONE)
public class ProductBundleDTO implements Dto {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The name of root element in XML representation of product association.
	 */
	public static final String ROOT_ELEMENT = "bundle";
	
	@XmlAttribute(name = "code", required = true)
	private String code;
	
	@XmlElementWrapper(name = "constituents", required = true)
	@XmlElement(name = "constituent")
	private List<ProductBundleConstituentDTO> constituents;

	@XmlElement(name = "selectionrule")
	private SelectionRuleDTO selectionRule;
	
	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(final String code) {
		this.code = code;
	}

	/**
	 * @return the constituents
	 */
	public List<ProductBundleConstituentDTO> getConstituents() {
		return constituents;
	}

	/**
	 * @param constituents the constituents to set
	 */
	public void setConstituents(final List<ProductBundleConstituentDTO> constituents) {
		this.constituents = constituents;
	}

	/**
	 *
	 * @param selectionRule the selectionRule to set
	 */
	public void setSelectionRule(final SelectionRuleDTO selectionRule) {
		this.selectionRule = selectionRule;
	}

	/**
	 *
	 * @return the selectionRule
	 */
	public SelectionRuleDTO getSelectionRule() {
		return selectionRule;
	}
}
