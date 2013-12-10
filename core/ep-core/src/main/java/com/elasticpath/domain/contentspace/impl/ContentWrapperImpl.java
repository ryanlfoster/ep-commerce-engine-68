package com.elasticpath.domain.contentspace.impl;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.elasticpath.domain.contentspace.ContentWrapper;
import com.elasticpath.domain.contentspace.ContentWrapperService;
import com.elasticpath.domain.contentspace.Parameter;

/**
 * Default XML bound implementation of a content wrapper.
 */
@XmlRootElement(name = "content-wrapper")
@XmlAccessorType(XmlAccessType.NONE)
public class ContentWrapperImpl implements ContentWrapper {

	private static final long serialVersionUID = 1L;

	/**
	 * The parameterDefinitions field maps to a "parameter-definitions" tag in a content
	 * wrapper XML file, the type Parameter is mapped to the "parameter-definition" tag in the same file.
	 */
	@XmlElementWrapper(name = "template-parameters")
	@XmlElement(name = "template-parameter", type = TemplateParameterImpl.class)
	private List<Parameter> templateParameters = new ArrayList<Parameter>();


	/**
	 * The parameterDefinitions field maps to a "user-input-settings" tag in a content
	 * wrapper XML file, the type Parameter is mapped to the "user-input-setting" tag in the same file.
	 */
	@XmlElementWrapper(name = "user-input-settings")
	@XmlElement(name = "user-input-setting", type = UserInputParameterImpl.class)
	private List<Parameter> userInputSettings = new ArrayList<Parameter>();

	/**
	 * The serviceDefinitions field maps to a "service-definitions" tag in a content
	 * wrapper XML file, the type Parameter is mapped to the "service-definition" tag in the same file.
	 */
	@XmlElementWrapper(name = "service-definitions")
	@XmlElement(name = "service-definition", type = ContentWrapperServiceImpl.class)
	private List<ContentWrapperService> serviceDefinitions = new ArrayList<ContentWrapperService>();


	/**
	 * The templateName field maps to the "template-name" tag in a content wrapper XML file.
	 */
	@XmlElement(name = "template-name")
	private String templateName;

	/**
	 * The human readable name of content wrapper.
	 */
	@XmlElement(name = "wrapper-name")
	private String wrapperName;


	/**
	 * The wrapperId field maps to the "wrapper-id" tag in a content wrapper XML file.
	 */
	@XmlElement(name = "wrapper-id")
	private String wrapperId;

	/**
	 * Initial section of resolver. String can be multiline.
	 */
	@XmlElement(name = "init")
	private String initSection;

	/**
	 * Script language for this content wrapper.
	 */
	@XmlElement(name = "script-language")
	private String scriptLanguage;


	/**
	 * Returns the service definitions associated with this content wrapper.
	 * 
	 * @return the service definitions
	 */
	public List<ContentWrapperService> getServiceDefinitions() {
		return serviceDefinitions;
	}

	/**
	 * Set the service definitions associated with this content wrapper.
	 * 
	 * @param serviceDefinitions the service definitions
	 */
	public void setServiceDefinitions(final List<ContentWrapperService> serviceDefinitions) {
		this.serviceDefinitions = serviceDefinitions;
	}


	/**
	 * Returns the templateName that this content wrapper is designated for.
	 * 
	 * @return the template name
	 */
	public String getTemplateName() {
		return templateName;
	}

	/**
	 * Sets the template name for which this wrapper will represent.
	 * 
	 * @param templateName the name of the template to be set, including the .vm suffix
	 */
	public void setTemplateName(final String templateName) {
		this.templateName = templateName;
	}

	/**
	 * Returns the content wrapper Id for this wrapper.
	 * 
	 * @return a string Id representing this wrapper
	 */
	public String getWrapperId() {
		return wrapperId;
	}

	/**
	 * Sets the wrapper Id for the content wrapper.
	 * 
	 * @param wrapperId to be set for this content wrapper.
	 */
	public void setWrapperId(final String wrapperId) {
		this.wrapperId = wrapperId;
	}

	/**
	 * Return the input parameters.
	 * @return List of input parameters.
	 */
	public List<Parameter> getUserInputSettings() {
		return userInputSettings;
	}

	/**
	 * Set the list of input parameters.
	 * @param userInputSettings list of input parameters.
	 */
	public void setUserInputSettings(final List<Parameter> userInputSettings) {
		this.userInputSettings = userInputSettings;
	}

	/**
	 * Return the template parameters.
	 * @return List of template parameters.
	 */
	public List<Parameter> getTemplateParameters() {
		return templateParameters;
	}

	/**
	 * Set the list of template parameters.
	 * @param templateParameters list of template parameters.
	 */
	public void setTemplateParameters(final List<Parameter> templateParameters) {
		this.templateParameters = templateParameters;
	}

	/**
	 * Get the multiline string with script language expressions.
	 * @return Multiline string with script language expressions
	 */
	public String getInitSection() {
		return initSection;
	}

	/**
	 * Set the multiline string with script language expressions.
	 * @param initSection string with script language expressions.
	 */
	public void setInitSection(final String initSection) {
		this.initSection = initSection;
	}

	/**
	 * Get the script language for this content wrapper.
	 * @return script language for this content wrapper.
	 */
	public String getScriptLanguage() {
		return scriptLanguage;
	}

	/**
	 * Set the script language for this content wrapper.
	 * @param scriptLanguage script language.
	 */
	public void setScriptLanguage(final String scriptLanguage) {
		this.scriptLanguage = scriptLanguage;
	}

	/**
	 * Get the human readable content wrapper name.
	 * @return human readable content wrapper name.
	 */
	public String getName() {
		return wrapperName;
	}

	/**
	 * Set the human readable content wrapper name.
	 * @param name human readable content wrapper name.
	 */
	public void setName(final String name) {
		wrapperName = name;
	}



}
