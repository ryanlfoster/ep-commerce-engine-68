/**
 * 
 */
package com.elasticpath.domain.contentspace;

import java.io.Serializable;
import java.util.List;


/**
 * A template and meta data required to display a given type of dynamic content.
 */
public interface ContentWrapper extends Serializable {

	/**
	 * 
	 * @return The name of template to render.
	 */
	String getTemplateName();
	
	/**
	 * Set the template name.
	 * @param templateName Name of template
	 */
	void setTemplateName(String templateName);
	

	/**
	 * Returns the service definitions associated with this content wrapper.
	 * 
	 * @return the service definitions
	 */
	List<ContentWrapperService> getServiceDefinitions();
	
	/**
	 * Set the service definitions associated with this content wrapper.
	 * 
	 * @param serviceDefinitions the service definitions
	 */
	void setServiceDefinitions(List<ContentWrapperService> serviceDefinitions);
	

	/**
	 * Will return the wrapperId for the content wrapper.
	 * @return the wrapperId of the content wrapper
	 */
	String getWrapperId();
	
	/**
	 * Will set the wrapperId for the content wrapper.
	 * @param wrapperId for the content wrapper to be set to
	 */
	void setWrapperId(final String wrapperId);
	
	/**
	 * Return the input parameters.
	 * @return List of input parameters. 
	 */
	List<Parameter> getUserInputSettings();
	
	/**
	 * Set the list of input parameters.
	 * @param userInputSettings list of input parameters.
	 */
	void setUserInputSettings(List<Parameter> userInputSettings);
	
	/**
	 * Return the template parameters.
	 * @return List of template parameters. 
	 */
	List<Parameter> getTemplateParameters();
	
	/**
	 * Set the list of template parameters.
	 * @param templateParameters list of template parameters.
	 */
	void setTemplateParameters(List<Parameter> templateParameters);
	
	/**
	 * Get the multiline string with script language expressions.
	 * @return Multiline string with script language expressions
	 */
	String getInitSection();

	/**
	 * Set the multiline string with script language expressions.
	 * @param init string with script language expressions.
	 */
	void setInitSection(String init);
	
	/**
	 * Get the script language for this content wrapper.
	 * @return script language for this content wrapper.
	 */
	String getScriptLanguage();

	/**
	 * Set the script language for this content wrapper.
	 * @param scriptLanguage script language.
	 */
	void setScriptLanguage(String scriptLanguage);
	
	/**
	 * Get the human readable content wrapper name. 
	 * @return human readable content wrapper name.
	 */
	String getName();
	
	/**
	 * Set the human readable content wrapper name.
	 * @param name human readable content wrapper name.
	 */
	void setName(String name);

}
