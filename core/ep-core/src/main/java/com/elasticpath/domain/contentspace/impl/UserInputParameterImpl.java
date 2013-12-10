package com.elasticpath.domain.contentspace.impl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import com.elasticpath.commons.constants.ValueTypeEnum;
import com.elasticpath.domain.contentspace.Parameter;

/**
 * User input parameter is a variation of parameter interface for content
 * wrappers which is used to define parameters that will be available
 * in the UI for user to input them. (if pass-to-template parameter is specified
 * then these may appear in template
 */
@XmlAccessorType(XmlAccessType.NONE)
public class UserInputParameterImpl implements Parameter {

	private static final long serialVersionUID = 1L;

	@XmlAttribute(name = "id")
	private String parameterId;

	//The name field is mapped to the name tag in the content wrapper XML file
	@XmlAttribute(name = "name")
	private String name;

	//The localizable field is mapped to the localizable attribute in a content wrapperXML file
	@XmlAttribute(name = "localizable")
	private boolean localizable = false;

	//The required field is mapped to the required attribute in a content wrapperXML file
	@XmlAttribute(name = "required")
	private boolean requiredByXmlTag = true;

	// The passToTemplate mapped to pass-to-template attribute  in a content wrapperXML file
	@XmlAttribute(name = "pass-to-template")
	private boolean passToTemplate = true;


	@XmlAttribute(name = "type")
	private ValueTypeEnum type = ValueTypeEnum.StringShort;


	@XmlAttribute(name = "description")
	private String description;

	@Override
	public String getParameterId() {
		return parameterId;
	}

	@Override
	public void setParameterId(final String parameterId) {
		this.parameterId = parameterId;
	}

	/**
	 * @return NULL because user input parameters do not evaluate.
	 */
	public String getScriptExpression() {
		return null;
	}

	/**
	 * do nothing  because user input parameters do not evaluate.
	 * @param scriptExpression disregarded value but required by interface to treat parameters uniformly
	 * @throws UnsupportedOperationException always is thrown.
	 */
	public void setScriptExpression(final String scriptExpression) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("UserInputParameterImpl cannot have script expression");
	}

	@Override
	public boolean isLocalizable() {
		return localizable;
	}

	/**
	 * Set localization flag.
	 * @param localizable set to true if need localization.
	 */
	public void setLocalizable(final boolean localizable) {
		this.localizable = localizable;
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
	 * Get required flag.
	 * @return required flag.
	 */
	public boolean isRequired() {
		return isRequiredByXmlTag() || isPassToTemplate();
	}

	/**
	 * Set required flag.
	 * @param required required flag.
	 */
	public void setRequired(final boolean required) {
		setRequiredByXmlTag(required);
	}

	/**
	 * @return true if parameter tag in content wrapper xml has required attribute true
	 */
	public boolean isRequiredByXmlTag() {
		return requiredByXmlTag;
	}

	/**
	 * set the required attribute of this parameter.
	 * @param requiredByXmlTag true or false set by required attribute for user-input-setting tag
	 */
	public void setRequiredByXmlTag(final boolean requiredByXmlTag) {
		this.requiredByXmlTag = requiredByXmlTag;
	}

	/**
	 * Constructor for ParameterImpl.
	 * @param parameterId Parameter id.
	 * @param localizable boolean flag is parameter localizable.
	 */
	public UserInputParameterImpl(final String parameterId, final  boolean localizable) {
		super();
		this.parameterId = parameterId;
		this.localizable = localizable;
	}

	/**
	 * Constructor for ParameterImpl. <code>localizable</code> by default is false.
	 * @param parameterId Parameter id.
	 */
	public UserInputParameterImpl(final String parameterId) {
		super();
		this.parameterId = parameterId;
	}

	/**
	 * Constructor for ParameterImpl. <code>localizable</code> by default is false.
	 */
	public UserInputParameterImpl() {
		super();
	}


	/**
	 * Get the type of parameter. ShortString by default.
	 * @return instance of <code>ValueTypeEnum</code>.
	 */
	public ValueTypeEnum getType() {
		return type;
	}

	/**
	 * Set parameter type.
	 * @param type instance of <code>ValueTypeEnum</code>.
	 */
	public void setType(final ValueTypeEnum type) {
		this.type = type;
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
	 * Get the pass to template flag.
	 * @return Pass to template flag. True by default.
	 */
	public boolean isPassToTemplate() {
		return passToTemplate;
	}

	/**
	 * Set the pass to template flag.
	 * @param passToTemplate pass to template flag.
	 */
	public void setPassToTemplate(final boolean passToTemplate) {
		this.passToTemplate = passToTemplate;
	}

	@Override
	public String toString() {
		return "Parameter: " + getName()
		+ ", localizable: " + isLocalizable()
		+ ", required: " + isRequired()
		+ ", type: " + getType();
	}


}
