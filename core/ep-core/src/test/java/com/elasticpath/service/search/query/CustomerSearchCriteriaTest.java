package com.elasticpath.service.search.query;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

/**
 * Test <code>CustomerSearchCriteriaImpl</code>.
 */
public class CustomerSearchCriteriaTest {


	private CustomerSearchCriteria customerSearchCriteria;

	@Before
	public void setUp() throws Exception {
		this.customerSearchCriteria = new CustomerSearchCriteria();
	}

	/**
	 * Test method for 'com.elasticpath.domain.search.impl.CustomerSearchCriteriaImpl.getFirstName()'.
	 */
	@Test
	public void testGetFirstName() {
		assertNull(this.customerSearchCriteria.getFirstName());
	}

	/**
	 * Test method for 'com.elasticpath.domain.search.impl.CustomerSearchCriteriaImpl.setFirstName(String)'.
	 */
	@Test
	public void testSetFirstName() {
		final String firstname = "firstname";
		this.customerSearchCriteria.setFirstName(firstname);
		assertEquals(firstname, this.customerSearchCriteria.getFirstName());
	}

	/**
	 * Test method for 'com.elasticpath.domain.search.impl.CustomerSearchCriteriaImpl.getLastName()'.
	 */
	@Test
	public void testGetLastName() {
		assertNull(this.customerSearchCriteria.getLastName());
	}

	/**
	 * Test method for 'com.elasticpath.domain.search.impl.CustomerSearchCriteriaImpl.setLastName(String)'.
	 */
	@Test
	public void testSetLastName() {
		final String lastname = "lastname";
		this.customerSearchCriteria.setLastName(lastname);
		assertEquals(lastname, this.customerSearchCriteria.getLastName());
	}

	/**
	 * Test method for 'com.eemailicpath.domain.search.impl.CustomerSearchCriteriaImpl.getEmail()'.
	 */
	@Test
	public void testGetEmail() {
		assertNull(this.customerSearchCriteria.getEmail());
	}

	/**
	 * Test method for 'com.eemailicpath.domain.search.impl.CustomerSearchCriteriaImpl.setEmail(String)'.
	 */
	@Test
	public void testSetEmail() {
		final String email = "email";
		this.customerSearchCriteria.setEmail(email);
		assertEquals(email, this.customerSearchCriteria.getEmail());
	}
	
	/**
	 * Test method for 'com.eemailicpath.domain.search.impl.CustomerSearchCriteriaImpl.getUserId()'.
	 */
	@Test
	public void testGetUserId() {
		assertNull(this.customerSearchCriteria.getUserId());
	}

	/**
	 * Test method for 'com.eemailicpath.domain.search.impl.CustomerSearchCriteriaImpl.setUserId(String)'.
	 */
	@Test
	public void testSetUserId() {
		final String userId = "userId";
		this.customerSearchCriteria.setUserId(userId);
		assertEquals(userId, this.customerSearchCriteria.getUserId());
	}

	/**
	 * Test method for 'com.ecustomerNumbericpath.domain.search.impl.CustomerSearchCriteriaImpl.getCustomerNumber()'.
	 */
	@Test
	public void testGetCustomerNumber() {
		assertNull(this.customerSearchCriteria.getCustomerNumber());
	}

	/**
	 * Test method for 'com.ecustomerNumbericpath.domain.search.impl.CustomerSearchCriteriaImpl.setCustomerNumber(String)'.
	 */
	@Test
	public void testSetCustomerNumber() {
		final String customerNumber = "customerNumber";
		this.customerSearchCriteria.setCustomerNumber(customerNumber);
		assertEquals(customerNumber, this.customerSearchCriteria.getCustomerNumber());
	}

	/**
	 * Test method for 'com.ephoneNumbericpath.domain.search.impl.customerSearchCriteria.getPhoneNumber()'.
	 */
	@Test
	public void testGetPhoneNumber() {
		assertNull(this.customerSearchCriteria.getPhoneNumber());
	}

	/**
	 * Test method for 'com.ephoneNumbericpath.domain.search.impl.customerSearchCriteria.setPhoneNumber(String)'.
	 */
	@Test
	public void testSetPhoneNumber() {
		final String phoneNumber = "phoneNumber";
		this.customerSearchCriteria.setPhoneNumber(phoneNumber);
		assertEquals(phoneNumber, this.customerSearchCriteria.getPhoneNumber());
	}
}
