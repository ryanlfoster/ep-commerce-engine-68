package com.elasticpath.domain.contentspace;

import java.io.Serializable;

import com.elasticpath.commons.constants.ValueTypeEnum;

/**
 * Parameter definition type for <code>ContentWrapper</code>.
 */
public interface Parameter extends Serializable {
	
	/**
	 * @return unique identifier of this parameter.
	 */
	String getParameterId();
	
	/**
	 * set unique parameter id.
	 * @param parameterId the unique id
	 */
	void setParameterId(String parameterId);
	
	/**
	 * Is parameter localizable.
	 * @return true if value of parameter depends from locale.  
	 */
	boolean isLocalizable();
	
	/**
	 * Parameter name. 
	 * @return Name of parameter.
	 */
	String getName();
	
	/**
	 * Set parameter name.
	 * @param name - Name of parameter.
	 */
	void setName(String name);
	

	/**
	 * Sets if this parameter is localizable.
	 * 
	 * @param localizable true if the parameter is localizable
	 * @throws UnsupportedOperationException if this operation is not supported
	 */
	void setLocalizable(boolean localizable) throws UnsupportedOperationException;
	
	/**
	 * Get the expression.
	 * @return Expression to evaluate.
	 */
	String getScriptExpression();

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
	void setScriptExpression(String scriptExpression) throws UnsupportedOperationException;
	
	/**
	 * Get required flag.
	 * @return required flag.
	 */
	boolean isRequired();

	/**
	 * Set required flag.
	 * @param required required flag.
	 * @throws UnsupportedOperationException if this operation is not supported
	 */
	void setRequired(boolean required) throws UnsupportedOperationException;
	
	/**
	 * Get the type of parameter. ShortString by default.
	 * @return instance of <code>ValueTypeEnum</code>.
	 */
	ValueTypeEnum getType();

	/**
	 * Set parameter type.
	 * @param type instance of <code>ValueTypeEnum</code>. 
	 * @throws UnsupportedOperationException if this operation is not supported
	 */
	void setType(ValueTypeEnum type) throws UnsupportedOperationException;
	
	/**
	 * Parameter description. 
	 * @return Description of parameter.
	 */
	String getDescription();

	/**
	 * Set description.
	 *
	 * @param description of the parameter 
	 */
	void setDescription(String description);
	
	/**
	 * Get the pass to template flag. 
	 * @return Pass to template flag. True by default.
	 */
	boolean isPassToTemplate();

	/**
	 * Set the pass to template flag.
	 * @param passToTemplate pass to template flag.
	 * @throws UnsupportedOperationException if this operation is not supported
	 */
	void setPassToTemplate(boolean passToTemplate) throws UnsupportedOperationException;
	
}
