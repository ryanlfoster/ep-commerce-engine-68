package com.elasticpath.service.security.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.service.customer.CustomerService;

/**
 * Tests {@link UpgradingCustomerDaoAuthenticationProviderTest}.
 */
public class UpgradingCustomerDaoAuthenticationProviderTest {
	private static final String DETAILS = "details";
	private static final String PRINCIPAL = "expectedPrincipal";
	private static final String PRESENTED_PASSWORD = "presentedPassword";
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private CustomerService customerService;
	private Customer customer;
	private Authentication authentication;
	private UpgradingCustomerDaoAuthenticationProvider upgradingCustomerDaoAuthenticationProvider;
	private List<GrantedAuthority> grantedAuthorities;
	
	/**
	 * Initialize mocks and class under test.
	 */
	@Before
	public void initializeMocksAndClassUnderTest() {
		customerService = context.mock(CustomerService.class);
		authentication = context.mock(Authentication.class);
		customer = context.mock(Customer.class);
		grantedAuthorities = new ArrayList<GrantedAuthority>(); 
		grantedAuthorities.add(new GrantedAuthorityImpl("role"));
		upgradingCustomerDaoAuthenticationProvider = new UpgradingCustomerDaoAuthenticationProvider();
		upgradingCustomerDaoAuthenticationProvider.setCustomerService(customerService);
	}
	
	/**
	 * Test creating the successful authentication.
	 */
	@Test
	public void createSuccessAuthentication() {
		context.checking(new Expectations() {
			{
				allowing(authentication).getCredentials();
				will(returnValue(PRESENTED_PASSWORD));

				allowing(authentication).getDetails();
				will(returnValue(DETAILS));
				
				allowing(customer).getAuthorities();
				will(returnValue(grantedAuthorities));
				
				oneOf(customer).setClearTextPassword(PRESENTED_PASSWORD);
				oneOf(customerService).update(customer);
			}
		});
		
		Authentication successfulAuthentication = upgradingCustomerDaoAuthenticationProvider
			.createSuccessAuthentication(PRINCIPAL, authentication, customer);
		
		assertEquals("Authentication has incorrect principal.", PRINCIPAL, successfulAuthentication.getPrincipal());
		assertEquals("Authentication has incorrect credentials.", PRESENTED_PASSWORD, successfulAuthentication.getCredentials());
		assertEquals("Authentication has incorrect authorities.", grantedAuthorities, successfulAuthentication.getAuthorities());
		assertEquals("Authentication has incorrect details.", DETAILS, successfulAuthentication.getDetails());
		assertEquals("Authentication has incorrect name.", PRINCIPAL, successfulAuthentication.getName());
		assertTrue("Authentication should be successful.", successfulAuthentication.isAuthenticated());
	}

}
