package com.elasticpath.domain.contentspace;

import java.util.List;

import com.elasticpath.persistence.api.Entity;

/**
 * Defines everything needed to describe a piece of content that may be delivered conditionally.
 * <p>
 * Example:<br/>
 * A ContentWrapper may be of type "Flash". A instance of a "Flash" 
 * ContentWrapper may require a parameter in order to display a Flash file: 
 * the filename. The DynamicContent carries a list of parameter values, so in
 * this example one of its parameter values would be a filename that references
 * a piece of content. In this way the DynamicContent references the piece of content
 * to be displayed as well as the ContentWrapper that describes how to display it.</p>
 */
public interface DynamicContent extends Entity {

	
	/**
	 * Gets the identity of the action which is 
	 * specific to the action type.
	 * 
	 * @return the identity of this action
	 */
	String getIdentity();
	
	/**
	 * Set the parameter values.
	 * @param parameterValues parameter values
	 */
	void setParameterValues(List<ParameterValue> parameterValues);

	/**
	 * Get method for obtaining the resolved parameters.
	 * @return parameter values
	 */
	List<ParameterValue> getParameterValues();
	
	/**
	 * Get the type of action.
	 *
	 * @return the type of action
	 */
	String getType();
	
	/**
	 * Gets the content wrapper Id for this content wrapper action.
	 * 
	 * @return content wrapper Id
	 */
	String getContentWrapperId();
	
	/**
	 * Sets the content wrapper Id for this content wrapper action.
	 * 
	 * @param contentWrapperId content wrapper Id
	 */
	void setContentWrapperId(String contentWrapperId);

	/**
	 * Get the content name.
	 * @return content name
	 */
	String getName();
	
	/**
	 * Set the content name.
	 * @param name content name
	 */
	void setName(String name);
	
	/**
	 * Get the content description. 
	 * @return content description
	 */
	String getDescription();
	
	/**
	 * Set the content description.
	 * @param description content description
	 */
	void setDescription(String description);
}
