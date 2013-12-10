package com.elasticpath.service.misc;

import java.util.List;
import java.util.Properties;

import org.apache.commons.mail.EmailException;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.misc.EmailProperties;
import com.elasticpath.base.exception.EpServiceException;

/**
 * Provides email services.
 */
public interface EmailService  {

	/**
	 * Sends a HTML or text email to a list of recipients using the provided email properties.
	 * Each recipients' email will be constructed with the same passed email properties (short the
	 * recipient address). The recipient address in the passed email properties will be
	 * overwritten and not used.
	 * 
	 * @param toEmailList a list of recipient email addresses
	 * @param emailProperties the properties used to create the email
	 * @throws EpServiceException in case of any errors
	 * @deprecated This will be removed in the near future in favour of sendOrderConfirmationEmail et al. 
	 */
	@Deprecated
	void sendMail(final List<String> toEmailList, final EmailProperties emailProperties) throws EpServiceException;

	/**
	 * Sends a HTML or text email to a recipient using the provided email properties.
	 * 
	 * @param emailProperties the properties used to create the email
	 * @throws EpServiceException in case of any errors
	 * @deprecated This will be removed in the near future in favour of sendOrderConfirmationEmail et al.
	 */
	@Deprecated
	void sendMail(final EmailProperties emailProperties) throws EpServiceException;
	
	/**
	 * Send order confirmation email.	 
	 * @param orderNumber is the order number
	 * @return whether the email was sent successfully
	 * @throws EmailException an email exception
	 */
	boolean sendOrderConfirmationEmail(final String orderNumber) throws EmailException;
	
	/**
	 * Send gift certificate email.	 
	 * @param orderNumber is the order number
	 * @return whether the email was sent successfully
	 * @throws EmailException an email exception
	 */
	boolean sendGiftCertificateEmails(final String orderNumber)	throws EmailException; 

	/**
	 * Send order shipped email.
	 * @param properties are the email properties
	 * @return whether the email was sent successfully
	 * @throws EmailException an email exception
	 */
	boolean sendOrderShippedEmail(final Properties properties) throws EmailException;
	
	/**
	 * Send the email for order returns.
	 * @param emailProperties the email properties
	 * @return whether the email was sent successfully
	 * @throws EmailException on email exception
	 */
	boolean sendOrderReturnEmail(final EmailProperties emailProperties) throws EmailException;
	
	/**
	 * Send the email for password change confirmation.
	 * @param customer the customer who's password has been changed
	 * @return whether the email was sent successfully
	 * @throws EmailException on email exception
	 */
	boolean sendPasswordConfirmationEmail(final Customer customer) throws EmailException;
	
	/**
	 * Send the email for forgotten password change confirmation.
	 * @param customer the customer who's password has been changed
	 * @param newPassword the new password to use when logged in
	 * @return whether the email was sent successfully
	 * @throws EmailException on email exception
	 */
	boolean sendForgottenPasswordEmail(final Customer customer, final String newPassword) throws EmailException;
	
	/**
	 * Send the email confirmation for a newly registered customer.
	 * @param customer the customer who's password has been changed
	 * @param newPassword the new password to use when logged in
	 * @return whether the email was sent successfully
	 * @throws EmailException on email exception
	 */
	boolean sendNewlyRegisteredCustomerEmail(final Customer customer, final String newPassword) throws EmailException;

	/**
	 * Send the email confirmation for a newly registered customer.
	 * @param customer the customer who's password has been changed
	 * @return whether the email was sent successfully
	 * @throws EmailException on email exception
	 */
	boolean sendNewCustomerEmailConfirmation(final Customer customer) throws EmailException;

}