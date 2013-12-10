package com.elasticpath.domain.contentspace.impl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

import com.elasticpath.commons.constants.ValueTypeEnum;
import com.elasticpath.domain.contentspace.Parameter;

/**
 * Template parameter is a variation of parameter interface for content
 * wrappers which is used to define parameters that will be available
 * for the template (these do not appear in IU).
 */
@XmlAccessorType(XmlAccessType.NONE)
public class TemplateParameterImpl implements Parameter {

	private static final long serialVersionUID = 1L;

	@XmlAttribute(name = "id")
	private String parameterId;

	//The name field is mapped to the name tag in the content wrapper XML file
	@XmlAttribute(name = "name")
	private String name;

	@XmlAttribute(name = "description")
	private String description;

	@XmlValue
	private String scriptExpression;

	@Override
	public String getParameterId() {
		return parameterId;
	}

	@Override
	public void setParameterId(final String parameterId) {
		this.parameterId = parameterId;
	}

	/**
	 * Get the expression.
	 * @return Expression to evaluate.
	 */
	public String getScriptExpression() {
		return scriptExpression;
	}

	/**
	 * Set the expression.
	 * @param scriptExpression something like:
	 * 
	 * Product product = productService.findByGuid(code, loadTuner);
	 * priceLookupService.getProductPrice(product, storeConfig.getStore().getCatalog(), storeConfig.getStore().getDefaultCurrency()).
	 *     getLowestPrice(1).getMoneyValueAndSymbol();
	 * 
	 * @throws UnsupportedOperationException if this operation is not supported
	 */
	public void setScriptExpression(final String scriptExpression) {
		this.scriptExpression = scriptExpression;
	}

	/**
	 * @return false (template parameters return expression result)
	 */
	public boolean isLocalizable() {
		return false;
	}

	/**
	 * template parameters return expression result, so they are never localizable.
	 * @param localizable disregarded (template parameters return expression result).
	 * @throws UnsupportedOperationException always is thrown.
	 */
	public void setLocalizable(final boolean localizable) throws UnsupportedOperationException {
		throw new UnsupportedOperationException(
		"TemplateParameterImpl are evaluated and their localizable settings is always false");
	}

	@Override
	public String getName() {
		return name;
	}

	/**
	 * Set parameter name.
	 * @param name - Name of parameter.
	 */
	public void setName(final String name) {
		this.name = name;
	}


	/**
	 * @return false (template parameters return expression result, nut the rule of thumb for
	 * rendering "if anything happens fallback onto alternative" so they are always required).
	 */
	public boolean isRequired() {
		return true;
	}

	/**
	 * template parameters return expression result, so they are never required to be inputted.
	 * @param required disregarded.
	 * @throws UnsupportedOperationException always is thrown.
	 */
	public void setRequired(final boolean required) throws UnsupportedOperationException {
		throw new UnsupportedOperationException(
		"TemplateParameterImpl are evaluated and their required settings is always false");
	}

	/**
	 * Constructor for ParameterImpl. <code>localizable</code> by default is false.
	 * @param parameterId Parameter id.
	 */
	public TemplateParameterImpl(final String parameterId) {
		super();
		this.parameterId = parameterId;
	}

	/**
	 * Constructor for ParameterImpl. <code>localizable</code> by default is false.
	 */
	public TemplateParameterImpl() {
		super();
	}


	/**
	 * @return <code>ValueTypeEnum.StringShort</code>.
	 * (template parameters return expression result, so they do not have a type).
	 */
	public ValueTypeEnum getType() {
		return ValueTypeEnum.StringShort;
	}

	/**
	 * template parameters return expression result, so they do not have a type.
	 * @param type disregarded.
	 * @throws UnsupportedOperationException always is thrown.
	 */
	public void setType(final ValueTypeEnum type) throws UnsupportedOperationException {
		throw new UnsupportedOperationException(
		"TemplateParameterImpl are evaluated and their type settings is always ValueTypeEnum.StringShort");
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void setDescription(final String description) {
		this.description = description;
	}

	/**
	 * @return TRUE because template parameters are always sent to templates.
	 */
	public boolean isPassToTemplate() {
		return true;
	}

	/**
	 * do nothing because template parameters are always sent to templates.
	 * @param passToTemplate disregarded value but required by interface to treat parameters uniformly
	 * @throws UnsupportedOperationException always is thrown.
	 */
	public void setPassToTemplate(final boolean passToTemplate) throws UnsupportedOperationException {
		throw new UnsupportedOperationException(
		"TemplateParameterImpl are always available to template");
	}

	@Override
	public String toString() {
		return "Parameter: " + getName()
		+ ", localizable: *false"
		+ ", required: *true"
		+ ", type: *ValueTypeEnum.StringShort";
	}


}
