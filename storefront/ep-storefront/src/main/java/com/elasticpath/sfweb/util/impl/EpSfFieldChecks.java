package com.elasticpath.sfweb.util.impl; //NOPMD

import org.apache.commons.validator.Field;
import org.apache.commons.validator.ValidatorAction;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springmodules.validation.commons.FieldChecks;

import com.elasticpath.commons.validator.impl.EpFieldChecks;
import com.elasticpath.sfweb.formbean.BillingAndReviewFormBean;

/**
 * <code>EpFieldChecks</code> defines customized validation rules to be integrated into the springmodules validator. This class extends
 * EpFieldChecks to implement checks that are specific to the Storefront.
 * 
 */
public class EpSfFieldChecks extends EpFieldChecks { //NOPMD

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;
	
	/**
	 * This is a specialized validation that checks that a security code for an existing credit card has been entered <b>when</b> it has been
	 * selected as the card to use for a checkout payment.
	 *
	 * @param bean - the current form bean (command object).
	 * @param validatorAction - validator action instance.
	 * @param field - the form field to check.
	 * @param errors - errors.
	 * @return - result.
	 */
	public static boolean validateRequiredExistingCardSecurityCode(final Object bean, final ValidatorAction validatorAction, final Field field,
			final Errors errors) {

		BillingAndReviewFormBean billingAndReviewFormBean = (BillingAndReviewFormBean) ((BindException) errors).getTarget();
		final String value = FieldChecks.extractValue(bean, field);

		//Return true if there's no need to check this field
		if (!(billingAndReviewFormBean.getSelectedPaymentOption() == BillingAndReviewFormBean.PAYMENT_OPTION_EXISTING_CREDIT_CARD
				&& billingAndReviewFormBean.getSelectedExistingCreditCardUid() == getCardUidPkByFieldKeyIndex(billingAndReviewFormBean, field
						.getKey()))) {
			return true;
		}

		//Check that something was entered
		if (value.length() == 0) {
			rejectValue(errors, field, validatorAction);
			return false;
		}

		//Check for a valid integer
		Integer newValue = null;
		try {
			newValue = Integer.valueOf(value);
		} catch (NumberFormatException e) {
			rejectValue(errors, field, validatorAction);
			return false;
		}
		if (newValue.intValue() < 0) {
			rejectValue(errors, field, validatorAction);
			return false;
		}

		return true;
	}

	private static long getCardUidPkByFieldKeyIndex(final BillingAndReviewFormBean billingAndReviewFormBean, final String fieldKey) {
		int index = Integer.parseInt(fieldKey.substring(fieldKey.indexOf("[") + 1, fieldKey.indexOf("]"))); // NOPMD (Java 1.4 doesn't support PMD's
																											// recommendation)
		return billingAndReviewFormBean.getExistingCreditCards().get(index).getUidPk();
	}

	private void pmdWorkAround1() {
		// This method is a workaround for
		// a PMD warning that isn't silenced
		// by the NOPMD tag
		this.pmdWorkAround2();
	}

	private void pmdWorkAround2() {
		// This method is a workaround for
		// a PMD warning that isn't silenced
		// by the NOPMD tag
		this.pmdWorkAround1();
	}

}
