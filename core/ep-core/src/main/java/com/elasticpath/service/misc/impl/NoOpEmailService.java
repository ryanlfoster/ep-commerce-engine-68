/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.service.misc.impl;

import java.util.List;
import java.util.Properties;

import org.apache.commons.mail.EmailException;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.misc.EmailProperties;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.misc.EmailService;

/**
 * An implementation of <code>EmailService</code> that does absolutely nothing.
 */
public class NoOpEmailService implements EmailService {

	/**
	 * Does not send a gift certificate email.
	 *  
	 * @param orderNumber the order number to ignore.
	 * @return false
	 * @throws EmailException if an error occurs doing nothing
	 */
	public boolean sendGiftCertificateEmails(final String orderNumber) throws EmailException {
		return false;
	}

	/**
	 * Does not send an email.
	 * @param toEmailList the to list to ignore
	 * @param emailProperties email properties to ignore
	 * @throws EpServiceException if an error occurs doing nothing
	 * @deprecated
	 */
	@Deprecated
	public void sendMail(final List<String> toEmailList, final EmailProperties emailProperties) throws EpServiceException {
		// Do nothing
	}

	/**
	 * Does not send an email.
	 * @param emailProperties the email properties to ignore
	 * @throws EpServiceException if an error occurs doing nothing
	 * @deprecated
	 */
	@Deprecated
	public void sendMail(final EmailProperties emailProperties) throws EpServiceException {
		// Do nothing
	}

	/**
	 * Does not send an order confirmation email.
	 * 
	 * @param orderNumber the order number to ignore
	 * @return false
	 * @throws EmailException if an error occurs doing nothing
	 */
	public boolean sendOrderConfirmationEmail(final String orderNumber) throws EmailException {
		return false;
	}

	/**
	 * Does not send an order shipped email.
	 * 
	 * @param properties the properties to ignore
	 * @return false
	 * @throws EmailException if an error occurs doing nothing
	 */
	public boolean sendOrderShippedEmail(final Properties properties) throws EmailException {
		return false;
	}
	
	/**
	 * Does not send an order return email.
	 * @param emailProperties - the email properties
	 * @return false
	 * @throws EmailException if an error occurs doing nothing
	 */
	public boolean sendOrderReturnEmail(final EmailProperties emailProperties) throws EmailException {
		return false;
	}

	/**
	 * Does not sent a forgotten password email.
	 * @param customer the customer
	 * @param newPassword the password
	 * @return false
	 * @throws EmailException if an error occurs doing nothing
	 */
	public boolean sendForgottenPasswordEmail(final Customer customer, final String newPassword) throws EmailException {
		return false;
	}

	/**
	 * Does not sent a new customer email.
	 * @param customer the customer
	 * @return false
	 * @throws EmailException if an error occurs doing nothing
	 */
	public boolean sendNewCustomerEmailConfirmation(final Customer customer) throws EmailException {
		return false;
	}

	/**
	 * Does not sent a newly registered customer email.
	 * @param customer the customer
	 * @param newPassword the password
	 * @return false
	 * @throws EmailException if an error occurs doing nothing
	 */
	public boolean sendNewlyRegisteredCustomerEmail(final Customer customer, final String newPassword) throws EmailException {
		return false;
	}

	/**
	 * Does not sent a confirmation email.
	 * @param customer the customer
	 * @return false
	 * @throws EmailException if an error occurs doing nothing
	 */
	public boolean sendPasswordConfirmationEmail(final Customer customer) throws EmailException {
		return false;
	}

}
