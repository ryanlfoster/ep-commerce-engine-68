package com.elasticpath.sfweb.formbean.validator;

import org.springframework.validation.Errors;

import com.elasticpath.sfweb.formbean.ShoppingItemFormBean;

/**
 * A validator for ShoppingItems form beans.
 *
 */
public interface ShoppingItemValidator {

	/**
	 * Validate shopping item form bean.
	 * @param formBean a shopping item for validate
	 * @param errors a errors consumer 
	 * @param shoppingItemIndex shopping item index in CartFormBean
	 */
	void validate(ShoppingItemFormBean formBean, Errors errors, int shoppingItemIndex);
}