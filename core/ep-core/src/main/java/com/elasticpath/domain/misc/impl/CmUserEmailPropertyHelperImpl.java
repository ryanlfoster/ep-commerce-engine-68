/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.misc.impl;

import java.util.Locale;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.impl.AbstractEpDomainImpl;
import com.elasticpath.domain.misc.CmUserEmailPropertyHelper;
import com.elasticpath.domain.misc.EmailProperties;

/**
 * Helper for processing email properties for CmUser e-mails.
 */
public class CmUserEmailPropertyHelperImpl extends AbstractEpDomainImpl implements CmUserEmailPropertyHelper {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;

	private static final String CREATE_PWD_EMAIL_TXT_TEMPLATE = "cmCreatePassword.txt";

	private static final String RESET_PWD_EMAIL_TXT_TEMPLATE = "cmResetPassword.txt";

	@Override
	public EmailProperties getCreateEmailProperties(final CmUser cmUser, final String newPassword, final Locale locale) {
		final EmailProperties emailProperties = getEmailPropertiesBeanInstance();
		emailProperties.getTemplateResources().put("cmUser", cmUser);
		emailProperties.getTemplateResources().put("newPassword", newPassword);
		emailProperties.setDefaultSubject("Your Password Reminder");
		emailProperties.setLocaleDependentSubjectKey("cm.passwordCreate.emailSubject");
		emailProperties.setEmailLocale(locale);
		emailProperties.setTextTemplate(CREATE_PWD_EMAIL_TXT_TEMPLATE);
		emailProperties.setRecipientAddress(cmUser.getEmail());
		emailProperties.setStoreCode(null);

		return emailProperties;
	}

	@Override
	public EmailProperties getResetEmailProperties(final CmUser cmUser, final String newPassword, final Locale locale) {
		final EmailProperties emailProperties = getEmailPropertiesBeanInstance();
		emailProperties.getTemplateResources().put("cmUser", cmUser);
		emailProperties.getTemplateResources().put("newPassword", newPassword);
		emailProperties.setDefaultSubject("Commerce Manager");
		emailProperties.setLocaleDependentSubjectKey("cm.passwordReset.emailSubject");
		emailProperties.setEmailLocale(locale);
		emailProperties.setTextTemplate(RESET_PWD_EMAIL_TXT_TEMPLATE);
		emailProperties.setRecipientAddress(cmUser.getEmail());
		emailProperties.setStoreCode(null);

		return emailProperties;
	}

	/**
	 *
	 * @return
	 */
	private EmailProperties getEmailPropertiesBeanInstance() {
		return getBean(ContextIdNames.EMAIL_PROPERTIES);
	}

}
