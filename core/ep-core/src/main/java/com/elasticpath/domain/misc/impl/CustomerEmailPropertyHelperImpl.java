/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.misc.impl;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.impl.AbstractEpDomainImpl;
import com.elasticpath.domain.misc.CustomerEmailPropertyHelper;
import com.elasticpath.domain.misc.EmailProperties;

/**
 * Helper for processing email properties for Customer e-mails.
 */
public class CustomerEmailPropertyHelperImpl extends AbstractEpDomainImpl implements CustomerEmailPropertyHelper {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;


	private static final String LOCALE = "locale";

	private static final String CONFIRM_PWD_EMAIL_TXT_TEMPLATE = "confirmPasswd.txt";
	private static final String REGISTER_CUSTOMER_EMAIL_TXT_TEMPLATE = "sendRegisteredCustomerPasswd.txt";
	private static final String FORGOTTEN_PWD_EMAIL_TXT_TEMPLATE = "customerForgottenPasswd.txt";
	private static final String CREATE_ACCOUNT_EMAIL_HTML_TEMPLATE = "createAccount.html";
	private static final String CREATE_ACCOUNT_EMAIL_TXT_TEMPLATE = "createAccount.txt";

	@Override
	public EmailProperties getPasswordConfirmationEmailProperties(final Customer customer) {
		final EmailProperties emailProperties = getEmailPropertiesBeanInstance();
		emailProperties.getTemplateResources().put("customerUid", customer.getUidPk());
		emailProperties.getTemplateResources().put(LOCALE, customer.getPreferredLocale());
		emailProperties.setDefaultSubject("Password Change Confirmation");
		emailProperties.setLocaleDependentSubjectKey("account.passwdConf.emailSubject");
		emailProperties.setEmailLocale(customer.getPreferredLocale());
		emailProperties.setTextTemplate(CONFIRM_PWD_EMAIL_TXT_TEMPLATE);
		emailProperties.setRecipientAddress(customer.getEmail());
		emailProperties.setStoreCode(customer.getStoreCode());

		return emailProperties;
	}

	private EmailProperties getEmailPropertiesBeanInstance() {
		return getBean(ContextIdNames.EMAIL_PROPERTIES);
	}

	@Override
	public EmailProperties getNewlyRegisteredCustomerEmailProperties(final Customer customer, final String newPassword) {
		final EmailProperties emailProperties = getEmailPropertiesBeanInstance();
		emailProperties.getTemplateResources().put("customerUid", customer.getUidPk());
		emailProperties.getTemplateResources().put("newPassword", newPassword);
		emailProperties.getTemplateResources().put(LOCALE, customer.getPreferredLocale());
		emailProperties.setDefaultSubject("Customer Registration Confirmation");
		emailProperties.setLocaleDependentSubjectKey("account.registerCustomer.emailSubject");
		emailProperties.setEmailLocale(customer.getPreferredLocale());
		emailProperties.setTextTemplate(REGISTER_CUSTOMER_EMAIL_TXT_TEMPLATE);
		emailProperties.setRecipientAddress(customer.getEmail());
		emailProperties.setStoreCode(customer.getStoreCode());

		return emailProperties;
	}

	@Override
	public EmailProperties getForgottenPasswordEmailProperties(final Customer customer, final String newPassword) {
		final EmailProperties emailProperties = getEmailPropertiesBeanInstance();
		emailProperties.getTemplateResources().put("customer", customer);
		emailProperties.getTemplateResources().put("newPassword", newPassword);
		emailProperties.getTemplateResources().put(LOCALE, customer.getPreferredLocale());
		emailProperties.setDefaultSubject("Your Password Reminder");
		emailProperties.setLocaleDependentSubjectKey("account.passwdForgotton.emailSubject");
		emailProperties.setEmailLocale(customer.getPreferredLocale());
		emailProperties.setTextTemplate(FORGOTTEN_PWD_EMAIL_TXT_TEMPLATE);
		emailProperties.setRecipientAddress(customer.getEmail());
		emailProperties.setStoreCode(customer.getStoreCode());

		return emailProperties;
	}

	@Override
	public EmailProperties getNewAccountEmailProperties(final Customer customer) {
		final EmailProperties emailProperties = getEmailPropertiesBeanInstance();
		emailProperties.getTemplateResources().put("customer", customer);
		emailProperties.getTemplateResources().put(LOCALE, customer.getPreferredLocale());
		emailProperties.setDefaultSubject("Create Account Confirmation");
		emailProperties.setLocaleDependentSubjectKey("account.create.emailSubject");
		emailProperties.setEmailLocale(customer.getPreferredLocale());
		emailProperties.setHtmlTemplate(CREATE_ACCOUNT_EMAIL_HTML_TEMPLATE);
		emailProperties.setTextTemplate(CREATE_ACCOUNT_EMAIL_TXT_TEMPLATE);
		emailProperties.setRecipientAddress(customer.getEmail());
		emailProperties.setStoreCode(customer.getStoreCode());

		return emailProperties;
	}

}
