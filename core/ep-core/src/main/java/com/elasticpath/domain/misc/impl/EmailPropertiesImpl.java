/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.misc.impl;

import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import com.elasticpath.domain.impl.AbstractEpDomainImpl;
import com.elasticpath.domain.misc.EmailProperties;

/**
 * Default implementation of <code>EmailProperties</code>.
 */
public class EmailPropertiesImpl extends AbstractEpDomainImpl implements EmailProperties {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;

	private String storeCode;
	private String recipientAddress;
	private Locale emailLocale;
	private String localeDependentSubjectKey;
	private String defaultSubject;
	private String textTemplate;
	private String htmlTemplate;
	private String emailImageUrl;
	private Map<String, Object> templateResources;
	private Boolean textOnly;

	@Override
	public String getStoreCode() {
		return storeCode;
	}

	@Override
	public void setStoreCode(final String storeCode) {
		this.storeCode = storeCode;
	}

	@Override
	public String getRecipientAddress() {
		return recipientAddress;
	}

	@Override
	public void setRecipientAddress(final String recipientAddress) {
		this.recipientAddress = recipientAddress;
	}

	@Override
	public String getTextTemplate() {
		return textTemplate;
	}

	@Override
	public void setTextTemplate(final String textTemplate) {
		this.textTemplate = textTemplate;
	}

	@Override
	public String getHtmlTemplate() {
		return htmlTemplate;
	}

	@Override
	public void setHtmlTemplate(final String htmlTemplate) {
		this.htmlTemplate = htmlTemplate;
	}

	@Override
	public Map<String, Object> getTemplateResources() {
		if (templateResources == null) {
			templateResources = new TreeMap<String, Object>();
		}
		return templateResources;
	}

	@Override
	public void setTemplateResources(final Map<String, Object> templateResources) {
		this.templateResources = templateResources;
	}

	@Override
	public Boolean isTextOnly() {
		return textOnly;
	}
	@Override
	public void setTextOnly(final Boolean textOnly) {
		this.textOnly = textOnly;
	}

	public String getEmailImageUrl() {
		return emailImageUrl;
	}

	public void setEmailImageUrl(final String emailImageUrl) {
		this.emailImageUrl = emailImageUrl;
	}

	@Override
	public Locale getEmailLocale() {
		return emailLocale;
	}

	@Override
	public void setEmailLocale(final Locale emailLocale) {
		this.emailLocale = emailLocale;
	}

	@Override
	public String getLocaleDependentSubjectKey() {
		return localeDependentSubjectKey;
	}

	@Override
	public void setLocaleDependentSubjectKey(final String localeDependentSubjectKey) {
		this.localeDependentSubjectKey = localeDependentSubjectKey;
	}

	@Override
	public String getDefaultSubject() {
		return defaultSubject;
	}

	@Override
	public void setDefaultSubject(final String defaultSubject) {
		this.defaultSubject = defaultSubject;
	}


}
