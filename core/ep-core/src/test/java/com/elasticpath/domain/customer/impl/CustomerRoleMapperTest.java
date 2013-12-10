package com.elasticpath.domain.customer.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;

import com.elasticpath.domain.customer.Customer;

/**
 * Test for CustomerRoleMapper.
 */
public class CustomerRoleMapperTest {
	private static final String ROLE_ARBITRARY = "ROLE_ARBITRARY";

	private static final String REGISTERED = "REGISTERED";

	private static final String PUBLIC = "PUBLIC";
	
	private static final Set<String> TEST_ROLES = new HashSet<String>(Arrays.asList(PUBLIC, REGISTERED, ROLE_ARBITRARY));

	private final Mockery context = new JUnit4Mockery();

	private CustomerRoleMapper customerRoleMapper;
	private Customer customer;
	
	/**
	 * Initialize object under test.
	 */
	@Before
	public void initializeObjectsUnderTest() {
		customer = context.mock(Customer.class);
		customerRoleMapper = new CustomerRoleMapper(customer);
	}
	
	/**
	 * Ensure mapper gets correct roles for anonymous customer.
	 */
	@Test
	public void ensureMapperGetsAllCorrectRolesForAnonymousCustomer() {
		makeCustomerAnonymous();		
		assertMapperGetsCorrectRoles(PUBLIC);
	}
	
	/**
	 * Ensure mapper gets all correct roles for non anonymous customer.
	 */
	@Test
	public void ensureMapperGetsAllCorrectRolesForNonAnonymousCustomer() {
		makeCustomerNotAnonymous();		
		assertMapperGetsCorrectRoles(REGISTERED);
	}	
	
	/**
	 * Ensure non anonymous customer only has required roles.
	 */
	@Test
	public void ensureNonAnonymousCustomerOnlyHasRequiredRoles() {
		makeCustomerNotAnonymous();			
		assertCustomerHasRoles(REGISTERED);
		assertCustomerDoesNotHaveRoles(PUBLIC, ROLE_ARBITRARY);
	}	
	
	/**
	 * Ensure anonymous customer only has required roles.
	 */
	@Test
	public void ensureAnonymousCustomerOnlyHasRequiredRoles() {
		makeCustomerAnonymous();
		assertCustomerHasRoles(PUBLIC);
		assertCustomerDoesNotHaveRoles(REGISTERED, ROLE_ARBITRARY);
	}	
	
	private void makeCustomerAnonymous() {
		context.checking(new Expectations() {
			{
				allowing(customer).isAnonymous(); 
				will(returnValue(true));
			}
		});
	}
	
	private void makeCustomerNotAnonymous() {
		context.checking(new Expectations() {
			{
				allowing(customer).isAnonymous(); 
				will(returnValue(false));
			}
		});
	}

	private void assertMapperGetsCorrectRoles(final String... expectedRoles) {
		assertThat("Please ensure all roles are added to TEST_ROLES", TEST_ROLES, hasItems(expectedRoles));
		Set<String> actualRoles = customerRoleMapper.getAllRoles();
		assertThat("Customer roles should contain correct roles. ", actualRoles, hasItems(expectedRoles));
		assertEquals("Count differs between expected and actual roles.", expectedRoles.length, actualRoles.size());
	}
	
	private void assertCustomerDoesNotHaveRoles(final String ... roles) {
		for (String role : roles) {
			assertFalse("Customer should not have the " + role + " role.", customerRoleMapper.hasRole(role));
		}
	}
	
	private void assertCustomerHasRoles(final String ... roles) {
		for (String role : roles) {
			assertTrue("Customer should have the " + role + " role.", customerRoleMapper.hasRole(role));
		}
	}
}
