package com.elasticpath.sfweb.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;

import com.elasticpath.sfweb.formbean.ShoppingItemFormBean;

/**
 * Provides an extendable interface for {@code ShoppingItemConfigController} to allow
 * configuration to be customised based on product template.
 */
public interface ProductConfigController {

	/**
	 * Retrieve a backing object for the current form from the given request.
	 * <p>The properties of the form object will correspond to the form field values
	 * in your form view. This object will be exposed in the model under the specified
	 * command name, to be accessed under that name in the view: for example, with
	 * a "spring:bind" tag. The default command name is "command".
	 * <p>Note that you need to activate session form mode to reuse the form-backing
	 * object across the entire form workflow. Else, a new instance of the command
	 * class will be created for each submission attempt, just using this backing
	 * object as template for the initial form.
	 *
	 * @param request current HTTP request
	 * @return the backing object
	 * @throws Exception in case of invalid state or arguments
	 * @see #setCommandName
	 * @see #setCommandClass
	 * @see #createCommand
	 */
	Object formBackingObject(HttpServletRequest request) throws Exception;

	/**
	 * Initialize the given binder instance, for example with custom editors.
	 * <p>This method allows you to register custom editors for certain fields of your
	 * command class. For instance, you will be able to transform Date objects into a
	 * String pattern and back, in order to allow your JavaBeans to have Date properties
	 * and still be able to set and display them in an HTML interface.
	 * <p>The default implementation is empty.
	 * @param request current HTTP request
	 * @param binder the new binder instance
	 * @param isFormSubmission true if called on form submission (POST)
	 * @see #createBinder
	 * @see org.springframework.validation.DataBinder#registerCustomEditor
	 * @see org.springframework.beans.propertyeditors.CustomDateEditor
	 */
	void initBinder(final HttpServletRequest request, final ServletRequestDataBinder binder, final boolean isFormSubmission);

	/**
	 * Template method for processing the final action of the shoppingItemConfig wizard.
	 * <p>Call <code>errors.getModel()</code> to populate the ModelAndView model
	 * with the command and the Errors instance, under the specified command name,
	 * as expected by the "spring:bind" tag.
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @param command form object with the current wizard state
	 * @param errors validation errors holder
	 * @return the finish view
	 * @throws Exception in case of invalid state or arguments
	 * @see org.springframework.validation.Errors
	 * @see org.springframework.validation.BindException#getModel
	 * @see #showPage(javax.servlet.http.HttpServletRequest, org.springframework.validation.BindException, int)
	 */
	ModelAndView processFinish(final HttpServletRequest request,
			final HttpServletResponse response, final Object command, final BindException errors)
			throws Exception;

	/**
	 * Validate page.
	 * @param shoppingItemFormBean command object to validate
	 * @param errors where error reports are placed
	 * @param shoppingItemIndex the index for shopping item in CartFormBean
	 */
	void validate(ShoppingItemFormBean shoppingItemFormBean, Errors errors, int shoppingItemIndex);

	/** Add required information for the page.
 	 *
	 * @param request current HTTP request
	 * @param command form object with the current wizard state
	 * @param errors validation errors holder
	 * @param page - for which page of the wizard this data is.
	 * @return a map with data necessary in the page
	 */
	Map<String, Object>  referenceData(final HttpServletRequest request, final Object command, final Errors errors, final int page);
}
