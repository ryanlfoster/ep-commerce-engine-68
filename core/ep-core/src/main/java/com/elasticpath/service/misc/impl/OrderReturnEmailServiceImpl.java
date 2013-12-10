package com.elasticpath.service.misc.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.mail.EmailException;
import org.apache.log4j.Logger;

import com.elasticpath.domain.misc.EmailProperties;
import com.elasticpath.service.misc.OrderEmailPropertyHelper;
import com.elasticpath.domain.order.OrderReturn;
import com.elasticpath.service.misc.EmailService;
import com.elasticpath.service.misc.OrderReturnEmailService;
import com.elasticpath.service.order.ReturnAndExchangeService;

/**
 * 
 * The default implementation for {@link OrderReturnEmailService} which is responsible for
 * sending out emails for Order Returns.
 */
public class OrderReturnEmailServiceImpl implements OrderReturnEmailService {
	
	private EmailService emailService;
	
	private ReturnAndExchangeService returnAndExchangeService;
	
	private OrderEmailPropertyHelper orderEmailPropertyHelper;
	
	private static final Logger LOG = Logger.getLogger(OrderReturnEmailServiceImpl.class);

	@Override
	public boolean sendOrderReturnEmail(final long orderReturnUid, final String emailRecipient) throws EmailException {
		
		List<Long> uids = new ArrayList<Long>();
		uids.add(orderReturnUid);
		
		List<OrderReturn> orderReturns = getReturnAndExchangeService().findByUids(uids);
		
		if (orderReturns.isEmpty()) {
			LOG.error("Could not find the order return");
			return false;
		}	
		
		EmailProperties emailProperties = getOrderEmailPropertyHelper().getOrderReturnEmailProperties(orderReturns.get(0));
		
		//if the value for email address is overridden, change the recipient email address so email would be sent out
		// to this new provided email address instead
		if (emailRecipient != null) {
			emailProperties.setRecipientAddress(emailRecipient);
		}		
		
		return getEmailService().sendOrderReturnEmail(emailProperties);
		
	}

	/**
	 *
	 * @return the emailService
	 */
	public EmailService getEmailService() {
		return emailService;
	}

	/**
	 *
	 * @param emailService the emailService to set
	 */
	public void setEmailService(final EmailService emailService) {
		this.emailService = emailService;
	}

	/**
	 *
	 * @return the returnAndExchangeService
	 */
	public ReturnAndExchangeService getReturnAndExchangeService() {
		return returnAndExchangeService;
	}

	/**
	 *
	 * @param returnAndExchangeService the returnAndExchangeService to set
	 */
	public void setReturnAndExchangeService(final ReturnAndExchangeService returnAndExchangeService) {
		this.returnAndExchangeService = returnAndExchangeService;
	}

	/**
	 *
	 * @return the orderEmailPropertyHelper
	 */
	public OrderEmailPropertyHelper getOrderEmailPropertyHelper() {
		return orderEmailPropertyHelper;
	}

	/**
	 *
	 * @param orderEmailPropertyHelper the orderEmailPropertyHelper to set
	 */
	public void setOrderEmailPropertyHelper(final OrderEmailPropertyHelper orderEmailPropertyHelper) {
		this.orderEmailPropertyHelper = orderEmailPropertyHelper;
	}
	
	

}
