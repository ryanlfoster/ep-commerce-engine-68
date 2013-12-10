/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.misc.impl;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Map;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.GiftCertificate;
import com.elasticpath.domain.misc.EmailProperties;
import com.elasticpath.domain.misc.GiftCertificateEmailPropertyHelper;
import com.elasticpath.domain.misc.Money;
import com.elasticpath.domain.misc.MoneyFormatter;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderSku;

/**
 * Helper for processing email properties for gift certificate e-mails.
 */
public class GiftCertificateEmailPropertyHelperImpl implements GiftCertificateEmailPropertyHelper {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;

	private static final String GIFT_CERT_RECIPIENT_EMAIL_HTML_TEMPLATE = "GiftCertificateRecipientEmail.html";
	private static final String GIFT_CERT_RECIPIENT_EMAIL_TXT_TEMPLATE = "GiftCertificateRecipientEmail.txt";

	private MoneyFormatter moneyFormatter;
	private BeanFactory beanFactory;

	@Override
	public EmailProperties getEmailProperties(final Order order, final OrderSku orderSku, final String giftCertificateImageFilename) {
		final EmailProperties emailProperties = getEmailPropertiesBeanInstance();
		Map<String, Object> templateMap = emailProperties.getTemplateResources();
		templateMap.put("senderEmail", orderSku.getFieldValue(GiftCertificate.KEY_SENDER_EMAIL));
		templateMap.put("senderName", orderSku.getFieldValue(GiftCertificate.KEY_SENDER_NAME));
		templateMap.put("purchaseAmount", convertBigDecimalStringToMoneyValueAndSymbol(orderSku, order.getLocale()));
		templateMap.put("giftCertCode", orderSku.getFieldValue(GiftCertificate.KEY_CODE));
		templateMap.put("recipientName", orderSku.getFieldValue(GiftCertificate.KEY_RECIPIENT_NAME));
		templateMap.put("recipientEmail", orderSku.getFieldValue(GiftCertificate.KEY_RECIPIENT_EMAIL));
		templateMap.put("message", orderSku.getFieldValue(GiftCertificate.KEY_MESSAGE));
		templateMap.put("locale", order.getLocale());
		templateMap.put("giftCertificateThemeImageFilename", giftCertificateImageFilename);
		emailProperties.setLocaleDependentSubjectKey("email.giftCert.emailSubject");
		emailProperties.setDefaultSubject("Gift Certificate");
		emailProperties.setEmailLocale(order.getLocale());
		emailProperties.setHtmlTemplate(GIFT_CERT_RECIPIENT_EMAIL_HTML_TEMPLATE);
		emailProperties.setTextTemplate(GIFT_CERT_RECIPIENT_EMAIL_TXT_TEMPLATE);
		emailProperties.setRecipientAddress(orderSku.getFieldValue(GiftCertificate.KEY_RECIPIENT_EMAIL));
		emailProperties.setStoreCode(order.getStoreCode());

		return emailProperties;
	}

	/**
	 * Get a string representation of a decimal representation of an amount of money, including the currency symbol.
	 * @param orderSku the orderSku
	 * @param locale the order's locale
	 * @return the string
	 */
	String convertBigDecimalStringToMoneyValueAndSymbol(final OrderSku orderSku, final Locale locale) {
		Money money = MoneyFactory.createMoney(new BigDecimal(orderSku.getTotal().getAmount().toPlainString()), orderSku.getTotal().getCurrency());
		return getMoneyFormatter().formatCurrency(money, locale);
	}

	private EmailProperties getEmailPropertiesBeanInstance() {
		return beanFactory.getBean(ContextIdNames.EMAIL_PROPERTIES);
	}

	public void setMoneyFormatter(final MoneyFormatter formatter) {
		this.moneyFormatter = formatter;
	}

	protected MoneyFormatter getMoneyFormatter() {
		return moneyFormatter;
	}
	
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	protected BeanFactory getBeanFactory() {
		return beanFactory;
	}
	
}
