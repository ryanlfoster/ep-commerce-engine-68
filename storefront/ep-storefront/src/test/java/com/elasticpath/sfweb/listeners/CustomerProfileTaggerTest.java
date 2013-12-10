/**
 * 
 */
package com.elasticpath.sfweb.listeners;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.service.order.OrderService;
import com.elasticpath.service.search.query.CustomerSearchCriteria;
import com.elasticpath.service.search.query.OrderSearchCriteria;
import com.elasticpath.sfweb.servlet.facade.HttpServletFacadeFactory;
import com.elasticpath.sfweb.servlet.facade.HttpServletRequestFacade;
import com.elasticpath.sfweb.servlet.facade.impl.HttpServletFacadeFactoryImpl;
import com.elasticpath.tags.Tag;
import com.elasticpath.tags.TagSet;

/**
 * Tests for CustomerProfileTagger.
 *
 */
public class CustomerProfileTaggerTest {

	private CustomerProfileTagger tagger;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private MockHttpServletRequest request;
	private HttpServletRequestFacade requestFacade;
	private CustomerSession session;
	private TagSet tagSet;
	private Customer customer;
	private Shopper shopper;
	private OrderService orderService;

	/**
	 * Set up context.
	 */
	@Before
	public void setUp() {
		tagSet = new TagSet();
		tagger = new CustomerProfileTagger();
		session = context.mock(CustomerSession.class);
		customer = context.mock(Customer.class);
		shopper = context.mock(Shopper.class);
		request = new MockHttpServletRequest();
		final HttpServletFacadeFactory httpServletFacadeFactory = new HttpServletFacadeFactoryImpl(null, null, null);
		requestFacade = httpServletFacadeFactory.createRequestFacade(request);

		final BeanFactory beanFactory = context.mock(BeanFactory.class);
		tagger.setBeanFactory(beanFactory);
		context.checking(new Expectations() {
			{
				allowing(beanFactory).getBean(ContextIdNames.ORDER_SEARCH_CRITERIA); will(returnValue(new OrderSearchCriteria()));
				allowing(beanFactory).getBean(ContextIdNames.CUSTOMER_SEARCH_CRITERIA); will(returnValue(new CustomerSearchCriteria()));
			}
		});

		orderService = context.mock(OrderService.class);
		tagger.setOrderService(orderService);
	}
	
	/**
	 * Test that if a customer was born 20 years ago tomorrow, their age is still 19.
	 */
	@Test
	public void testGetCustomerAgeIsRoundedDown() {
		final int twenty = 20;
		final int customerAge = 19;
		Calendar now = Calendar.getInstance();
		Calendar twentyYearsAgoTomorrow = Calendar.getInstance();
		twentyYearsAgoTomorrow.set(now.get(Calendar.YEAR) - twenty, now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH) + 1);
		assertEquals(customerAge, tagger.getCustomerAge(twentyYearsAgoTomorrow.getTime()));
	}
	
	/**
	 * Test that if a customer was born 20 years ago today, their age is 20.
	 */
	@Test
	public void testGetCustomerAgeIsCorrect() {
		final int twenty = 20;
		final int customerAge = 20;
		Calendar now = Calendar.getInstance();
		Calendar twentyYearsAgoToday = Calendar.getInstance();
		twentyYearsAgoToday.set(now.get(Calendar.YEAR) - twenty, now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
		assertEquals(customerAge, tagger.getCustomerAge(twentyYearsAgoToday.getTime()));
	}
	
	/**
	 * Tests FIRST_TIME_BUYER tag set to true by tagger when customer registered but has no orders.
	 */
	@Test
	public void testFirstTimeBuyerSetTrue() {
		setUpExpectations(true);
		tagger.execute(session, requestFacade);
		Tag tagValue = tagSet.getTagValue(CustomerProfileTagger.FIRST_TIME_BUYER);
		assertEquals(Boolean.TRUE,  tagValue.getValue()); 
	}
	
	/**
	 * Tests FIRST_TIME_BUYER tag set to true by tagger when customer has orders but not registered.
	 */
	@Test
	public void testFirstTimeBuyerSetTrue1() {
		setUpExpectations(false); // not registered, no orders
		tagger.execute(session, requestFacade);
		Tag tagValue = tagSet.getTagValue(CustomerProfileTagger.FIRST_TIME_BUYER);
		assertEquals(Boolean.TRUE,  tagValue.getValue()); 
	}
	/**
	 * Tests FIRST_TIME_BUYER tag set to false by tagger because customer registered and has orders.
	 */
	@Test
	public void testFirstTimeBuyerSetFalse() {
		setUpExpectations(false, context.mock(Order.class));
		tagger.execute(session, requestFacade);
		Tag tagValue = tagSet.getTagValue(CustomerProfileTagger.FIRST_TIME_BUYER);
		assertEquals(Boolean.FALSE,  tagValue.getValue()); 
	}

	/**
	 * Tests REGISTERED_CUSTOMER tag set to false by tagger because customer is not registered.
	 */
	@Test
	public void testRegisteredCustomerSetFalse() {
		setUpExpectations(false);
		tagger.execute(session, requestFacade);
		Tag tagValue = tagSet.getTagValue(CustomerProfileTagger.REGISTERED_CUSTOMER);
		assertEquals(Boolean.FALSE,  tagValue.getValue()); 
	}
	
	/**
	 * Tests REGISTERED_CUSTOMER tag set to true by tagger because customer is registered.
	 */
	@Test
	public void testRegisteredCustomerSetTrue() {
		setUpExpectations(true);
		tagger.execute(session, requestFacade);
		Tag tagValue = tagSet.getTagValue(CustomerProfileTagger.REGISTERED_CUSTOMER);
		assertEquals(Boolean.TRUE,  tagValue.getValue()); 
	}
	
	private void setUpExpectations(final boolean isRegistered, final Order... orders) {
		context.checking(new Expectations() { {
			allowing(session).getCustomerTagSet(); will(returnValue(tagSet));
			allowing(session).getShopper(); will(returnValue(shopper));
			allowing(shopper).getCustomer(); will(returnValue(customer));
			allowing(customer).getEmail(); will(returnValue("test_email"));
			allowing(customer).isRegistered(); will(returnValue(isRegistered));
			allowing(customer).getDateOfBirth(); will(returnValue(Calendar.getInstance().getTime()));
			allowing(customer).getGender(); will(returnValue('M'));
			allowing(customer).getStoreCode(); will(returnValue("storeCode"));
			allowing(customer).getUidPk(); will(returnValue(1L));
			allowing(orderService).getOrderCountBySearchCriteria(with(Expectations.<OrderSearchCriteria> anything()));
				will(returnValue((long) orders.length));
		} });
	}
}
