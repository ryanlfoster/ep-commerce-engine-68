package com.elasticpath.service.search.solr.query;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import org.apache.lucene.search.Query;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.domain.order.OrderShipmentStatus;
import com.elasticpath.domain.order.OrderStatus;
import com.elasticpath.service.search.index.QueryComposer;
import com.elasticpath.service.search.query.CustomerSearchCriteria;
import com.elasticpath.service.search.query.OrderSearchCriteria;
import com.elasticpath.service.search.query.SearchCriteria;
import com.elasticpath.service.search.solr.SolrIndexConstants;

/**
 * Test <code>OrderIndexSearchServiceImpl</code>.
 */
public class OrderQueryComposerImplTest extends QueryComposerTestCase {

	private static final String WHITESPACE_REGEX = "\\s";

	private OrderQueryComposerImpl orderQueryComposerImpl;

	private OrderSearchCriteria orderSearchCriteria;

	private CustomerSearchCriteria customerSearchCriteria;


	/**
	 * Prepare for tests.
	 * 
	 * @throws Exception in case of any errors.
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();

		orderQueryComposerImpl = new OrderQueryComposerImpl();
		orderQueryComposerImpl.setAnalyzer(getAnalyzer());
		orderQueryComposerImpl.setIndexUtility(getIndexUtility());

		orderSearchCriteria = new OrderSearchCriteria();
		customerSearchCriteria = new CustomerSearchCriteria();
		orderSearchCriteria.setCustomerSearchCriteria(customerSearchCriteria);
	}

	/**
	 * Test method for {@link OrderSearchCriteria#setOrderNumber(String)}.
	 */
	@Test
	public void testOrderNumber() {
		final String orderNumber = "order number";
		orderSearchCriteria.setOrderNumber(orderNumber);

		Query query = orderQueryComposerImpl.composeQuery(orderSearchCriteria, getSearchConfig());
		assertQueryContains(query, SolrIndexConstants.ORDER_NUMBER, orderNumber);
		query = orderQueryComposerImpl.composeFuzzyQuery(orderSearchCriteria, getSearchConfig());
		assertQueryContains(query, SolrIndexConstants.ORDER_NUMBER, orderNumber);
	}

	/**
	 * Test method for {@link OrderSearchCriteria#setOrderStatus(OrderStatus)}.
	 */
	@Test
	public void testOrderStatus() {
		final OrderStatus status = OrderStatus.ONHOLD;
		orderSearchCriteria.setOrderStatus(status);

		Query query = orderQueryComposerImpl.composeQuery(orderSearchCriteria, getSearchConfig());
		assertQueryContains(query, SolrIndexConstants.ORDER_STATUS, status.toString());
		query = orderQueryComposerImpl.composeFuzzyQuery(orderSearchCriteria, getSearchConfig());
		assertQueryContains(query, SolrIndexConstants.ORDER_STATUS, status.toString());
	}

	/**
	 * Test method for {@link OrderSearchCriteria#setOrderToDate(Date)}.
	 */
	@Test
	public void testOrderToDate() {
		final Date fromDate = new Date(1000L);
		orderSearchCriteria.setOrderFromDate(fromDate);

		Query query = orderQueryComposerImpl.composeQuery(orderSearchCriteria, getSearchConfig());
		assertQueryContainsDateRange(query, SolrIndexConstants.CREATE_TIME, fromDate, null, true, true);
		query = orderQueryComposerImpl.composeFuzzyQuery(orderSearchCriteria, getSearchConfig());
		assertQueryContainsDateRange(query, SolrIndexConstants.CREATE_TIME, fromDate, null, true, true);
	}

	/**
	 * Test method for {@link OrderSearchCriteria#setOrderFromDate(Date)}.
	 */
	@Test
	public void testOrderFromDate() {
		final Date toDate = new Date(2000L);
		orderSearchCriteria.setOrderToDate(toDate);

		Query query = orderQueryComposerImpl.composeQuery(orderSearchCriteria, getSearchConfig());
		assertQueryContainsDateRange(query, SolrIndexConstants.CREATE_TIME, null, toDate, true, true);
		query = orderQueryComposerImpl.composeFuzzyQuery(orderSearchCriteria, getSearchConfig());
		assertQueryContainsDateRange(query, SolrIndexConstants.CREATE_TIME, null, toDate, true, true);
	}

	/**
	 * Test method for {@link OrderSearchCriteria#setOrderToDate(Date)} and
	 * {@link OrderSearchCriteria#setOrderFromDate(Date)}.
	 */
	@Test
	public void testOrderToFromDate() {
		final Date toDate = new Date(2000L);
		final Date fromDate = new Date(1000L);
		orderSearchCriteria.setOrderToDate(toDate);
		orderSearchCriteria.setOrderFromDate(fromDate);

		Query query = orderQueryComposerImpl.composeQuery(orderSearchCriteria, getSearchConfig());
		assertQueryContainsDateRange(query, SolrIndexConstants.CREATE_TIME, fromDate, toDate, true, true);
		query = orderQueryComposerImpl.composeFuzzyQuery(orderSearchCriteria, getSearchConfig());
		assertQueryContainsDateRange(query, SolrIndexConstants.CREATE_TIME, fromDate, toDate, true, true);
	}

	/**
	 * Test method for {@link OrderSearchCriteria#setShipmentStatus(OrderShipmentStatus)}.
	 */
	@Test
	public void testShipmentStatus() {
		final OrderShipmentStatus shippmentStatus = OrderShipmentStatus.ONHOLD;
		orderSearchCriteria.setShipmentStatus(shippmentStatus);

		Query query = orderQueryComposerImpl.composeQuery(orderSearchCriteria, getSearchConfig());
		assertQueryContains(query, SolrIndexConstants.SHIPMENT_STATUS, shippmentStatus.toString());
		query = orderQueryComposerImpl.composeFuzzyQuery(orderSearchCriteria, getSearchConfig());
		assertQueryContains(query, SolrIndexConstants.SHIPMENT_STATUS, shippmentStatus.toString());
	}

	/**
	 * Test method for {@link OrderSearchCriteria#setShipmentZipcode(String)}.
	 */
	@Test
	public void testShipmentZipcode() {
		final String shipmentZipCode = "shipment zip code";
		orderSearchCriteria.setShipmentZipcode(shipmentZipCode);

		Query query = orderQueryComposerImpl.composeQuery(orderSearchCriteria, getSearchConfig());
		assertQueryContains(query, SolrIndexConstants.SHIPMENT_ZIPCODE, shipmentZipCode);
		query = orderQueryComposerImpl.composeFuzzyQuery(orderSearchCriteria, getSearchConfig());
		assertQueryContains(query, SolrIndexConstants.SHIPMENT_ZIPCODE, shipmentZipCode);
	}

	/**
	 * Test method for {@link OrderSearchCriteria#setFilterUids(Set)}.
	 */
	@Test
	public void testFilterUids() {
		final Set<Long> emptySet = Collections.emptySet();
		final Set<Long> someSet = new HashSet<Long>(Arrays.asList(123L, 34325L, 123124124L));
		Query query;

		orderSearchCriteria.setFilterUids(emptySet);
		try {
			query = orderQueryComposerImpl.composeQuery(orderSearchCriteria, getSearchConfig());
			fail("EpServiceException expected for empty filter UID set in search criteria.");
		} catch (EpServiceException e) {
			assertNotNull(e);
		}
		try {
			query = orderQueryComposerImpl.composeFuzzyQuery(orderSearchCriteria, getSearchConfig());
			fail("EpServiceException expected for empty filter UID set in search criteria.");
		} catch (EpServiceException e) {
			assertNotNull(e);
		}

		orderSearchCriteria.setFilterUids(someSet);
		query = orderQueryComposerImpl.composeQuery(orderSearchCriteria, getSearchConfig());
		assertQueryContainsSet(query, SolrIndexConstants.OBJECT_UID, someSet);
		query = orderQueryComposerImpl.composeFuzzyQuery(orderSearchCriteria, getSearchConfig());
		assertQueryContainsSet(query, SolrIndexConstants.OBJECT_UID, someSet);
	}

	/**
	 * Test method for {@link CustomerSearchCriteria#setFirstName(String)}.
	 */
	@Test
	public void testFirstName() {
		final String firstName = "first name";
		customerSearchCriteria.setFirstName(firstName);

		Query query = orderQueryComposerImpl.composeQuery(orderSearchCriteria, getSearchConfig());
		assertQueryContains(query, SolrIndexConstants.FIRST_NAME, firstName.split(WHITESPACE_REGEX));
		query = orderQueryComposerImpl.composeFuzzyQuery(orderSearchCriteria, getSearchConfig());
		assertQueryContainsFuzzy(query, SolrIndexConstants.FIRST_NAME, firstName.split(WHITESPACE_REGEX));
	}

	/**
	 * Test method for {@link CustomerSearchCriteria#setLastName(String)}.
	 */
	@Test
	public void testLastName() {
		final String lastName = "last name";
		customerSearchCriteria.setLastName(lastName);

		Query query = orderQueryComposerImpl.composeQuery(orderSearchCriteria, getSearchConfig());
		assertQueryContains(query, SolrIndexConstants.LAST_NAME, lastName.split(WHITESPACE_REGEX));
		query = orderQueryComposerImpl.composeFuzzyQuery(orderSearchCriteria, getSearchConfig());
		assertQueryContainsFuzzy(query, SolrIndexConstants.LAST_NAME, lastName.split(WHITESPACE_REGEX));
	}

	/**
	 * Test method for {@link CustomerSearchCriteria#setEmail(String)}.
	 */
	@Test
	public void testUserIdAndEmail() {
		final String email = "email";
		customerSearchCriteria.setEmail(email);
		String userId = "userId1";
		customerSearchCriteria.setUserId(userId);

		Query query = orderQueryComposerImpl.composeQuery(orderSearchCriteria, getSearchConfig());
		assertQueryContains(query, SolrIndexConstants.USERID_AND_EMAIL, email);
		assertQueryContains(query, SolrIndexConstants.USERID_AND_EMAIL, userId);
		query = orderQueryComposerImpl.composeFuzzyQuery(orderSearchCriteria, getSearchConfig());
		assertQueryContainsFuzzy(query, SolrIndexConstants.USERID_AND_EMAIL, userId);
		assertQueryContainsFuzzy(query, SolrIndexConstants.USERID_AND_EMAIL, email);
	}

	/**
	 * Test method for {@link CustomerSearchCriteria#setCustomerNumber(String)}.
	 */
	@Test
	public void testCustomerNumber() {
		final String customerNumber = "customer number";
		customerSearchCriteria.setCustomerNumber(customerNumber);

		Query query = orderQueryComposerImpl.composeQuery(orderSearchCriteria, getSearchConfig());
		assertQueryContains(query, SolrIndexConstants.CUSTOMER_NUMBER, customerNumber);
		query = orderQueryComposerImpl.composeFuzzyQuery(orderSearchCriteria, getSearchConfig());
		assertQueryContains(query, SolrIndexConstants.CUSTOMER_NUMBER, customerNumber);
	}

	/**
	 * Test method for {@link CustomerSearchCriteria#setPhoneNumber(String)}.
	 */
	@Test
	public void testPhoneNumber() {
		final String phoneNumber = "phone number";
		customerSearchCriteria.setPhoneNumber(phoneNumber);

		Query query = orderQueryComposerImpl.composeQuery(orderSearchCriteria, getSearchConfig());
		assertQueryContains(query, SolrIndexConstants.PHONE_NUMBER, phoneNumber);
		query = orderQueryComposerImpl.composeFuzzyQuery(orderSearchCriteria, getSearchConfig());
		assertQueryContains(query, SolrIndexConstants.PHONE_NUMBER, phoneNumber);
	}

	@Override
	protected QueryComposer getComposerUnderTest() {
		return orderQueryComposerImpl;
	}

	@Override
	protected SearchCriteria getCriteriaUnderTest() {
		return orderSearchCriteria;
	}
}
