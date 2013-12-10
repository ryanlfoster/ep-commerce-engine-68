/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.misc.impl;

import java.util.Locale;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.constants.ImportConstants;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.dataimport.ImportJobStatus;
import com.elasticpath.domain.impl.AbstractEpDomainImpl;
import com.elasticpath.domain.misc.EmailProperties;
import com.elasticpath.domain.misc.ImportEmailPropertyHelper;
import com.elasticpath.domain.store.Store;

/**
 * Helper for processing email properties for Import Job e-mails.
 */
public class ImportEmailPropertyHelperImpl extends AbstractEpDomainImpl implements ImportEmailPropertyHelper {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;

	@Override
	public EmailProperties getEmailProperties(final ImportJobStatus runningJob, final CmUser cmUser, final Locale locale) {
		final EmailProperties emailProperties = getEmailPropertiesBeanInstance();
		emailProperties.getTemplateResources().put("cmUser", cmUser);
		emailProperties.getTemplateResources().put("importRunningJob", runningJob);
		emailProperties.getTemplateResources().put("locale", locale);
		emailProperties.setDefaultSubject("Import Status Report");
		emailProperties.setLocaleDependentSubjectKey("importJob.emailSubject");
		emailProperties.setEmailLocale(locale);
		emailProperties.setTextTemplate(ImportConstants.EMAIL_IMPORT_REPORT_TXT_TEMPLATE);
		emailProperties.setRecipientAddress(cmUser.getEmail());

		Store store = runningJob.getImportJob().getStore();
		if (store != null) {
			emailProperties.setStoreCode(store.getCode());
		}

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
