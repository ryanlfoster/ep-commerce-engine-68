/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.test.util;

import java.util.List;
import java.util.Properties;

import org.apache.commons.mail.EmailException;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.misc.EmailProperties;
import com.elasticpath.service.impl.AbstractEpServiceImpl;
import com.elasticpath.service.misc.EmailService;

/**
 * FakeEmailServiceImpl used for testing.
 */
public class FakeEmailServiceImpl extends AbstractEpServiceImpl implements EmailService {

	/**
	 * {@inheritdoc}.
	 * Does nothing.
	 * @deprecated
	 */
	@Deprecated
	public void sendMail(final EmailProperties emailProperties) {
		// Does nothing.
	}

	/**
	 * {@inheritdoc}.
	 * Does nothing.
	 * @deprecated
	 */
	@Deprecated
	public void sendMail(final List<String> toEmailList, final EmailProperties emailProperties) {
		// Does nothing.
	}

	/**
	 * {@inheritdoc}.
	 */
	public boolean sendOrderConfirmationEmail(final String orderNumber) throws EmailException {
		return true;
	}

	/**
	 * {@inheritdoc}.
	 */
	public boolean sendOrderShippedEmail(final Properties properties) throws EmailException {
		return false;
	}

	/**
	 * {@inheritdoc}.
	 */
	public boolean sendGiftCertificateEmails(final String orderNumber) throws EmailException {
		return false;
	}
	
	/**
	 * {@inheritdoc}.
	 */
	public boolean sendOrderReturnEmail(final EmailProperties emailProperties) throws EmailException {
		return false;
	}

	/**
	 * {@inheritdoc}.
	 */
	public boolean sendForgottenPasswordEmail(final Customer customer, final String newPassword) throws EmailException {
		return false;
	}

	/**
	 * {@inheritdoc}.
	 */
	public boolean sendNewCustomerEmailConfirmation(final Customer customer) throws EmailException {
		return false;
	}

	/**
	 * {@inheritdoc}.
	 */
	public boolean sendNewlyRegisteredCustomerEmail(final Customer customer, final String newPassword) throws EmailException {
		return false;
	}

	/**
	 * {@inheritdoc}.
	 */
	public boolean sendPasswordConfirmationEmail(final Customer customer) throws EmailException {
		return false;
	}
}
