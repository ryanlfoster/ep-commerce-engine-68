package com.elasticpath.sfweb.listeners;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import org.apache.log4j.Logger;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.service.order.OrderService;
import com.elasticpath.service.search.query.CustomerSearchCriteria;
import com.elasticpath.service.search.query.OrderSearchCriteria;
import com.elasticpath.sfweb.servlet.facade.HttpServletRequestFacade;
import com.elasticpath.sfweb.servlet.listeners.CustomerLoginEventListener;
import com.elasticpath.sfweb.servlet.listeners.NewHttpSessionEventListener;
import com.elasticpath.tags.Tag;
import com.elasticpath.tags.TagSet;

/**
 * Applies CUSTOMER_AGE_YEARS and CUSTOMER_GENDER tags to the customer's tag cloud.
 */
public class CustomerProfileTagger implements NewHttpSessionEventListener, CustomerLoginEventListener {
	
	private static final String CUSTOMER_AGE_YEARS = "CUSTOMER_AGE_YEARS";
	private static final String CUSTOMER_GENDER = "CUSTOMER_GENDER";
	/** FIRST_TIME_BUYER tag.*/
	protected static final String FIRST_TIME_BUYER = "FIRST_TIME_BUYER";
	/** REGISTERED_CUSTOMER tag.*/
	protected static final String REGISTERED_CUSTOMER = "REGISTERED_CUSTOMER";
	
	private static final Logger LOG = Logger.getLogger(CustomerProfileTagger.class);

	private OrderService orderService;
	private BeanFactory beanFactory;
	
	/**
	 * Apply the start shopping tag to the given session.
	 * 
	 * @param session instance of CustomerSession
	 * @param request the originating HttpServletRequest
	 */
	public void execute(final CustomerSession session, final HttpServletRequestFacade request) {
		final TagSet tagCloud = session.getCustomerTagSet();
		final Customer customer = session.getShopper().getCustomer();
		addCustomerAgeTag(customer, tagCloud);
		addCustomerGenderTag(customer, tagCloud);
		addFirstTimeBuyerTag(customer, tagCloud);
		addRegisteredCustomerTag(customer, tagCloud);
	}

	/**
	 * Adds the CUSTOMER_AGE_YEARS tag to the given tag cloud. The value of the tag
	 * is the integer floor of the customer's age (i.e. in the case of a customer who is
	 * 18.9999 years old, the value will be 18).
	 * The tag is only added if the customer's date of birth is known.
	 * @param customer the customer
	 * @param tagCloud the tag cloud to which the tag should be added
	 */
	void addCustomerAgeTag(final Customer customer, final TagSet tagCloud) {
		Date dob = customer.getDateOfBirth();
		if (dob == null) {
			LOG.debug("Customer's Date Of Birth not available; tag not added to tag cloud.");
			return;
		}
		LOG.debug("Adding customer's age to tag cloud: " + getCustomerAge(dob));
		tagCloud.addTag(CUSTOMER_AGE_YEARS, new Tag(getCustomerAge(dob)));
	}
	
	/**
	 * Calculate someone's age based on their date of birth.
	 * @param dateOfBirth the date of birth
	 * @return the person's age
	 */
	int getCustomerAge(final Date dateOfBirth) {
		Calendar dob = Calendar.getInstance();
		dob.setTime(dateOfBirth);
		Calendar today = Calendar.getInstance();
		int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
		if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
			age--;
		}
		return age;
	}
	
	/**
	 * Adds the CUSTOMER_GENDER tag to the given tag cloud, only if the information is known.
	 * @param customer the customer
	 * @param tagCloud the tag cloud
	 */
	void addCustomerGenderTag(final Customer customer, final TagSet tagCloud) {
		char gender = customer.getGender();
		if (gender == 0) {
			LOG.debug("Customer's gender not available; tag not added to tag cloud.");
			return;
		}
		LOG.debug("Adding customer's gender to tag cloud: " + gender);
		tagCloud.addTag(CUSTOMER_GENDER, new Tag(String.valueOf(gender)));
	}
	
	/**
	 * Adds the FIRST_TIME_BUYER tag to the given tag cloud.
	 * Sets false if customer is registered, and has at least one order on current email.
	 * 
	 * @param customer the customer
	 * @param tagCloud the tag cloud
	 */
	private void addFirstTimeBuyerTag(final Customer customer, final TagSet tagCloud) {
		if (customer == null) {
			//|| !customer.isRegistered()) this should not be checked!, registered is irrelevant
			tagCloud.addTag(FIRST_TIME_BUYER, new Tag(true));
			return;
		}

		OrderSearchCriteria orderSearchCriteria = beanFactory.getBean(ContextIdNames.ORDER_SEARCH_CRITERIA);
		orderSearchCriteria.setStoreCodes(Collections.singleton(customer.getStoreCode()));
		
		CustomerSearchCriteria customerSearchCriteria = beanFactory.getBean(ContextIdNames.CUSTOMER_SEARCH_CRITERIA);
		customerSearchCriteria.setCustomerNumber(String.valueOf(customer.getUidPk()));
		orderSearchCriteria.setCustomerSearchCriteria(customerSearchCriteria);

		boolean hasNoOrders = orderService.getOrderCountBySearchCriteria(orderSearchCriteria) == 0;
		tagCloud.addTag(FIRST_TIME_BUYER, new Tag(hasNoOrders));
	}
	
	/**
	 * Adds the REGISTERED_CUSTOMER tag to the given tag cloud.
	 * Sets true if customer is registered, false otherwise.
	 * 
	 * @param customer the customer
	 * @param tagCloud the tag cloud
	 */
	private void addRegisteredCustomerTag(final Customer customer, final TagSet tagCloud) {
		tagCloud.addTag(REGISTERED_CUSTOMER, new Tag(customer != null && customer.isRegistered()));
	}
	
	/**
	 * Sets the order service.
	 * @param orderService the order service.
	 */
	public void setOrderService(final OrderService orderService) {
		this.orderService = orderService;
	}

	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}
}
