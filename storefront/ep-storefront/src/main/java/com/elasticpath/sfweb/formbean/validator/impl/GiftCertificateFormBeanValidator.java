/**
 * 
 */
package com.elasticpath.sfweb.formbean.validator.impl;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.owasp.esapi.ESAPI;
import org.springframework.validation.Errors;

import com.elasticpath.sfweb.formbean.GiftCertificateFormBean;
import com.elasticpath.sfweb.formbean.ShoppingItemFormBean;

/**
 * A validator for CartFormBean bean.
 *
 */
public class GiftCertificateFormBeanValidator extends ProductFormBeanValidator {

	private static final int CUSTOM_FIELD_MAXLEN = 255;
	private static final int EMAIL_MAXLEN = 255;
	private static final String ESAPI_SAFE_STRING_VALIDATOR = "SafeInputString";
	
	private Map<String, String> errorCodeMap;
	
	@Override
	public void validate(final ShoppingItemFormBean formBean, final Errors errors, final int shoppingItemIndex) {
		super.validate(formBean, errors, shoppingItemIndex);

		GiftCertificateFormBean gsFormBean = formBean.getGiftCertificateFields();

		boolean isValid = this.isValidRecipientEmail(shoppingItemIndex, gsFormBean.getRecipientEmail(), errors);
		isValid &= this.isValidConfirmEmail(shoppingItemIndex, gsFormBean.getConfirmEmail(), errors);
		
		String fieldPrefix = "cartItems[" + shoppingItemIndex + "].giftCertificateFields.";

		// if both valid, then check to be equals
		if (isValid && !gsFormBean.getRecipientEmail().equalsIgnoreCase(gsFormBean.getConfirmEmail())) {
			this.addError(fieldPrefix + "confirmEmail", getErrorCodeMap().get("errorInvalidEmailsAreNotEqual"), errors);
		}

		
		isValid = ESAPI.validator().isValidInput("GiftCertificate_from", gsFormBean.getSenderName(), 
				ESAPI_SAFE_STRING_VALIDATOR, CUSTOM_FIELD_MAXLEN, true);
		if (!isValid) {
			this.addError(fieldPrefix + "senderName", getErrorCodeMap().get("errorInvalidStringValue"), errors);
		}
		
		isValid = ESAPI.validator().isValidInput("GiftCertificate_to", gsFormBean.getRecipientName(), 
				ESAPI_SAFE_STRING_VALIDATOR, CUSTOM_FIELD_MAXLEN, true);
		if (!isValid) {
			this.addError(fieldPrefix + "recipientName", getErrorCodeMap().get("errorInvalidStringValue"), errors);
		}

		isValid = ESAPI.validator().isValidInput("GiftCertificate_message", gsFormBean.getMessage(), 
				ESAPI_SAFE_STRING_VALIDATOR, CUSTOM_FIELD_MAXLEN, true);
		if (!isValid) {
			this.addError(fieldPrefix + "message", getErrorCodeMap().get("errorInvalidStringValue"), errors);
		}
		
	}

	private boolean isValidRecipientEmail(final int index, final String email, final Errors errors) {
		if (StringUtils.isNotBlank(email) && isEmailValid(email)) {
			return true;
		}
		this.addError("cartItems[" + index + "].giftCertificateFields.recipientEmail", getErrorCodeMap().get("errorInvalidRecipientEmail"), errors);
		return false;
	}
	
	private boolean isValidConfirmEmail(final int index, final String email, final Errors errors) {
		if (StringUtils.isNotBlank(email) && isEmailValid(email)) {
			return true;
		}
		this.addError("cartItems[" + index + "].giftCertificateFields.confirmEmail", getErrorCodeMap().get("errorInvalidConfirmEmail"), errors);
		return false;
	}

	private boolean isEmailValid(final String email) {
		return ESAPI.validator().isValidInput("GifCertificate.email", email, "Email", EMAIL_MAXLEN, false);
	}

	/**
	 * Set the map of error name to error property key.
	 * 
	 * @param errorCodeMap the errorCodeMap to set
	 */
	public void setErrorCodeMap(final Map<String, String> errorCodeMap) {
		this.errorCodeMap = errorCodeMap;
	}

	/**
	 * Get the map of error name to error property key.
	 *
	 * @return the errorCodeMap
	 */
	public Map<String, String> getErrorCodeMap() {
		return errorCodeMap;
	}

}
