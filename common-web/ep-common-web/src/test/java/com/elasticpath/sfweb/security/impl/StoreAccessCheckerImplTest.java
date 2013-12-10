/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.sfweb.security.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.StoreState;
import com.elasticpath.service.catalogview.StoreConfig;
import com.elasticpath.settings.SettingsReader;
import com.elasticpath.settings.domain.SettingValue;

/**
 * Test that the methods in <code>StoreAccessCheckerImpl</code> behave correctly.
 */
public class StoreAccessCheckerImplTest {

	private static final String PASSCODE_SETTING_PATH = "COMMERCE/STORE/restrictedAccessPasscode";

	private static final String TESTSTORE = "teststore";

	private static final String TESTSTORE_ATTRIBUTE = "RestrictedStorePasscode_teststore";

	private StoreAccessCheckerImpl accessChecker;

	private SettingsReader settingsReader;

	private StoreConfig storeConfig;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private Store store;
	
	/**
	 * Set up objects required for all tests.
	 * 
	 * @throws java.lang.Exception in case of an error setting up the objects.
	 */
	@Before
	public void setUp() throws Exception {
		// Create dependent objects
		settingsReader = context.mock(SettingsReader.class);
		storeConfig = context.mock(StoreConfig.class);
		store = context.mock(Store.class); 


		// Create the object to be tested
		accessChecker = new StoreAccessCheckerImpl();
		accessChecker.setSettingsReader(settingsReader);
		accessChecker.setStoreConfig(storeConfig);
	}

	/**
	 * Setup expectations for an open store.
	 */
	private void setupOpenStoreExpectations() {
		context.checking(new Expectations() {
			{
				oneOf(storeConfig).getStore();
				will(returnValue(store));
				
				oneOf(store).getStoreState();
				will(returnValue(StoreState.OPEN));
			}
		});
	}
	
	/**
	 * Setup expectations for a restrcited store.
	 */
	private void setupRestrictedStoreExpectations() {
		context.checking(new Expectations() {
			{
				oneOf(storeConfig).getStore();
				will(returnValue(store));
				
				oneOf(store).getStoreState();
				will(returnValue(StoreState.RESTRICTED));
			}
		});
	}

	/**
	 * Test that a store is accessible when in an OPEN state.
	 */
	@Test
	public void testIsAccessibleWhenStoreOpen() {
		MockHttpServletRequest mockRequest = new MockHttpServletRequest();
		setupOpenStoreExpectations();
		assertTrue("Open store should be accessible", accessChecker.isAccessible(mockRequest));
	}

	/**
	 * Test that a store is accessible when in an RESTRICTED state but where the passcode is in the session.
	 */
	@Test
	public void testIsAccessibleWhenPasscodeInSession() {
		// Objects required for this test
		MockHttpServletRequest mockRequest = new MockHttpServletRequest();
		MockHttpSession session = new MockHttpSession();
		final String passcode = "session-passcode";
		session.setAttribute(TESTSTORE_ATTRIBUTE, passcode);
		mockRequest.setSession(session);
		final SettingValue settingValue = context.mock(SettingValue.class);
		final int expectedStoreCodeCount = 3;
		
		// Set expectations
		setupRestrictedStoreExpectations();
		context.checking(new Expectations() {
			{
				exactly(expectedStoreCodeCount).of(storeConfig).getStoreCode();
				will(returnValue(TESTSTORE));
				
				oneOf(settingsReader).getSettingValue(PASSCODE_SETTING_PATH, TESTSTORE);
				will(returnValue(settingValue));
				
				oneOf(settingValue).getValue();
				will(returnValue(passcode));
			}
		});
		assertTrue("store should be accessible when passcode in session", accessChecker.isAccessible(mockRequest));
	}

	/**
	 * Test that a store is accessible when in an RESTRICTED state but where the passcode is in a request parameter.
	 */

	@Test
	public void testIsAccessibleWhenPasscodeInRequest() {
		// objects required for this test
		MockHttpServletRequest mockRequest = new MockHttpServletRequest();
		final String passcode = "request-passcode";
		mockRequest.setParameter("passcode", passcode);
		final SettingValue settingValue = context.mock(SettingValue.class);

		// Set expectations
		setupRestrictedStoreExpectations();
		context.checking(new Expectations() {
			{
				allowing(storeConfig).getStoreCode();
				will(returnValue(TESTSTORE));
				
				oneOf(settingsReader).getSettingValue(PASSCODE_SETTING_PATH, TESTSTORE);
				will(returnValue(settingValue));
				
				oneOf(settingValue).getValue();
				will(returnValue(passcode));
			}
		});
		assertTrue("store should be accessible when passcode in request", accessChecker.isAccessible(mockRequest));
	}
	
	/**
	 * Test that a store is not accessible when in an RESTRICTED state and no passcode present.
	 */
	@Test
	public void testIsAccessibleWhenNoPasscodePresent() {
		MockHttpServletRequest mockRequest = new MockHttpServletRequest();

		// Set expectations
		setupRestrictedStoreExpectations();
		assertFalse("store should be not accessible when no passcode given", accessChecker.isAccessible(mockRequest));
	}
	
	/**
	 * Test that a store is not accessible when in an RESTRICTED state and an incorrect passcode is present.
	 */
	@Test
	public void testIsAccessibleWhenPasscodeIncorrect() {
		// Objects required for this test
		MockHttpServletRequest mockRequest = new MockHttpServletRequest();
		MockHttpSession session = new MockHttpSession();
		session.setAttribute(TESTSTORE_ATTRIBUTE, "bad-passcode");
		mockRequest.setSession(session);
		final SettingValue settingValue = context.mock(SettingValue.class);
		
		// Set expectations
		setupRestrictedStoreExpectations();
		context.checking(new Expectations() {
			{
				exactly(2).of(storeConfig).getStoreCode();
				will(returnValue(TESTSTORE));
				
				oneOf(settingsReader).getSettingValue(PASSCODE_SETTING_PATH, TESTSTORE);
				will(returnValue(settingValue));
				
				oneOf(settingValue).getValue();
				will(returnValue("session-passcode"));
			}
		});
		assertFalse("store should be not accessible when passcode incorrect", accessChecker.isAccessible(mockRequest));
	}

	/**
	 * Test that when a store is open the <code>isStoreOpen</code> returns true.
	 */
	@Test
	public void testIsStoreOpenForOpenStore() {
		setupOpenStoreExpectations();
		assertTrue("Store should be open", accessChecker.isStoreOpen());
	}

	/**
	 * Test that when a store is restricted the <code>isStoreOpen</code> returns false.
	 */
	@Test
	public void testIsStoreOpenForRestrictedStore() {
		setupRestrictedStoreExpectations();
		assertFalse("Store should not be be open", accessChecker.isStoreOpen());
	}
	
	/**
	 * Test getting the passcode from the session.
	 */
	@Test
	public void testGetPasscodeFromSession() {
		// Objects required for this test
		MockHttpServletRequest mockRequest = new MockHttpServletRequest();
		MockHttpSession session = new MockHttpSession();
		final String passcode = "session-passcode";
		session.setAttribute(TESTSTORE_ATTRIBUTE, passcode);
		mockRequest.setSession(session);
		context.checking(new Expectations() {
			{
				allowing(storeConfig).getStoreCode();
				will(returnValue(TESTSTORE));
			}
		});
		assertEquals("The session passcode was expected", passcode, accessChecker.getPasscode(mockRequest));
	}

	/**
	 * Test getting the passcode from the request.
	 */
	@Test
	public void testGetPasscodeFromRequest() {
		// Objects required for this test
		MockHttpServletRequest mockRequest = new MockHttpServletRequest();
		mockRequest.setParameter("passcode", "request-passcode");

		// Expectations
		context.checking(new Expectations() {
			{
				allowing(storeConfig).getStoreCode();
				will(returnValue(TESTSTORE));
			}
		});
		assertEquals("The request passcode was expected", "request-passcode" , accessChecker.getPasscode(mockRequest));
	}
	
	/**
	 * Test getting the passcode returns null when it isn't in the session or the request.
	 */
	@Test
	public void testGetPasscodeNotFound() {
		// Objects required for this test
		MockHttpServletRequest mockRequest = new MockHttpServletRequest();

		// Expectations
		context.checking(new Expectations() {
			{
				allowing(storeConfig).getStoreCode();
				will(returnValue(TESTSTORE));
			}
		});
		assertNull("A null passcode was expected", accessChecker.getPasscode(mockRequest));
	}

	/**
	 * Test getting the name of the passcode session attribute.
	 */
	@Test
	public void testGetPasscodeAttributeName() {
		// Expectations
		context.checking(new Expectations() {
			{
				allowing(storeConfig).getStoreCode();
				will(returnValue(TESTSTORE));
			}
		});
		
		assertEquals("the passcode attribute name should be as expected", TESTSTORE_ATTRIBUTE, 
				accessChecker.getPasscodeAttributeName());
	}

	/**
	 * Test getting the name of the passcode request parameter.
	 */
	@Test
	public void testGetPasscodeParameterName() {
		assertEquals("the passcode parameter name should be as expected", "passcode", accessChecker.getPasscodeParameterName());
	}
	
	/**
	 * Test storing the passcode in the session.
	 */
	@Test
	public void testStorePasscodeInSession() {
		MockHttpSession session = new MockHttpSession();
		String passcode = "whatever";
		// Expectations
		context.checking(new Expectations() {
			{
				allowing(storeConfig).getStoreCode();
				will(returnValue(TESTSTORE));
			}
		});

		accessChecker.storePasscodeInSession(session, passcode);
		assertEquals("test passcode should equal 'whatever'", passcode, session.getAttribute(TESTSTORE_ATTRIBUTE));
	}
	
	/**
	 * Test that a null passcode is not valid.
	 */
	@Test
	public void testIsPasscodeValidForNullPasscode() {
		assertFalse("null passcode should not be valid", accessChecker.isPassCodeValid(null));
	}
	
	/**
	 * Test that the expected passcode is valid.
	 */
	@Test
	public void testIsPasscodeValidForExpectedPasscode() {
		final SettingValue settingValue = context.mock(SettingValue.class);

		// Set expectations
		context.checking(new Expectations() {
			{
				allowing(storeConfig).getStoreCode();
				will(returnValue(TESTSTORE));
				
				oneOf(settingsReader).getSettingValue(PASSCODE_SETTING_PATH, TESTSTORE);
				will(returnValue(settingValue));
				
				oneOf(settingValue).getValue();
				will(returnValue("valid-passcode"));
			}
		});
		
		assertTrue("expected passcode should be valid", accessChecker.isPassCodeValid("valid-passcode"));
	}
	
	/**
	 * Test that an incorrect passcode is invalid. 
	 */
	@Test
	public void testIsPasscodeValidForWrongPassword() {
		final SettingValue settingValue = context.mock(SettingValue.class);

		// Set expectations
		context.checking(new Expectations() {
			{
				allowing(storeConfig).getStoreCode();
				will(returnValue(TESTSTORE));
				
				oneOf(settingsReader).getSettingValue(PASSCODE_SETTING_PATH, TESTSTORE);
				will(returnValue(settingValue));
				
				oneOf(settingValue).getValue();
				will(returnValue("valid-passcode"));
			}
		});
		
		assertFalse("incorrect passcode should not be valid", accessChecker.isPassCodeValid("invalid"));
	}
	
	/**
	 * Test that getting the expected password gets it from the setting.
	 */
	@Test
	public void testGetExpectedPasscode() {
		final SettingValue settingValue = context.mock(SettingValue.class);

		// Set expectations
		context.checking(new Expectations() {
			{
				allowing(storeConfig).getStoreCode();
				will(returnValue(TESTSTORE));
				
				oneOf(settingsReader).getSettingValue(PASSCODE_SETTING_PATH, TESTSTORE);
				will(returnValue(settingValue));
				
				oneOf(settingValue).getValue();
				will(returnValue("expected-passcode"));
			}
		});
		assertEquals("The expected passcode should be as expected", "expected-passcode", accessChecker.getExpectedPasscode());
	}
	
	/**
	 * Test that getting the store state gets it from the store config.
	 */
	@Test
	public void testGetStoreState() {
		setupRestrictedStoreExpectations();
		assertEquals("The store state should come from the store config", StoreState.RESTRICTED, accessChecker.getStoreState());
	}
}
