/**
 * 
 */
package com.elasticpath.sfweb.formbean.validator.impl;

import org.springframework.validation.Errors;

import com.elasticpath.sfweb.formbean.ShoppingItemFormBean;
import com.elasticpath.sfweb.formbean.validator.ShoppingItemValidator;

/**
 * A validator for CartFormBean bean.
 *
 */
public class ProductFormBeanValidator implements ShoppingItemValidator  {

	private String errorInvalidQuantity;

	@Override
	public void validate(final ShoppingItemFormBean formBean, final Errors errors, final int shoppingItemIndex) {
		if (formBean.getQuantity() <= 0) {
			this.addError("cartItems[" + shoppingItemIndex + "].quantity", this.errorInvalidQuantity, errors);
		}
	}
	
	/**
	 * Add error info to the errors object.
	 * @param field affected field
	 * @param errorCode resource key for error message
	 * @param errors errors result object
	 */
	protected void addError(final String field, final String errorCode, final Errors errors) {
		errors.rejectValue(field, errorCode);
	}

	/**
	 * @param errorInvalidQuantity the errorInvalidQuantity to set
	 */
	public void setErrorInvalidQuantity(final String errorInvalidQuantity) {
		this.errorInvalidQuantity = errorInvalidQuantity;
	}

}
