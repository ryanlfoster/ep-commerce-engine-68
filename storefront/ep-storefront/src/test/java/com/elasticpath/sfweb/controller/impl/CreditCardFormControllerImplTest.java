/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.sfweb.controller.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.util.security.CreditCardEncrypter;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerCreditCard;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.customer.impl.CustomerCreditCardImpl;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.service.customer.CustomerService;
import com.elasticpath.settings.SettingsReader;
import com.elasticpath.settings.domain.SettingValue;
import com.elasticpath.sfweb.formbean.CreditCardFormBean;
import com.elasticpath.sfweb.formbean.impl.CreditCardFormBeanImpl;
import com.elasticpath.sfweb.test.BeanFactoryExpectationsFactory;
import com.elasticpath.sfweb.util.SfRequestHelper;

/**
 * Test the CreditCardFormController handling of credit card changes.
 */
public class CreditCardFormControllerImplTest {

	private static final String COMMAND = "command";
	private static final String BINDING_RESULT_COMMAND = "org.springframework.validation.BindingResult.command";
	private static final String START_YEAR = "2007";
	private static final String EXPIRY_YEAR = "2019";
	private static final String EXPIRY_MONTH = "01";
	private static final String START_MONTH = "01";
	private static final String CREDIT_CARD_TYPE = "VISA";
	private static final String CREDIT_CARD_NUMBER = "4111111111111111";
	private static final String CARD_HOLDER_NAME = "Testy Testington";
	private static final String SUCCESS_VIEW = "successview";
	private static final String DELETE_CREDIT_CARD_PARAMETER = "deleteCreditCard";
	private static final int ISSUE_NUMBER = 0;
	private CreditCardFormControllerImpl controller;
	private CustomerService customerService;
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private BeanFactory beanFactory;
	private SfRequestHelper requestHelper;
	private MockHttpServletRequest request;
	private MockHttpServletResponse response;
	private BeanFactoryExpectationsFactory bfef;

	/**
	 * Set up objects for the tests.
	 */
	@Before
	public void setUp() {
		beanFactory = context.mock(BeanFactory.class);
		requestHelper = context.mock(SfRequestHelper.class);
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		customerService = context.mock(CustomerService.class);
		bfef = new BeanFactoryExpectationsFactory(context, beanFactory);

		controller = new CreditCardFormControllerImpl();
		controller.setBeanFactory(beanFactory);
		controller.setRequestHelper(requestHelper);
		controller.setCustomerService(customerService);
		controller.setSuccessView(SUCCESS_VIEW);
	}

	@After
	public void tearDown() {
		bfef.close();
	}

	/**
	 * Test that submit handles new credit cards correctly.
	 */
	@Test
	public final void testSubmitWithNewCreditCard() {
		// Set up the form bean for a new card
		CreditCardFormBean creditCardFormBean = createCreditCardFormBean();
		final CreditCardEncrypter encrypter = context.mock(CreditCardEncrypter.class);
		final Customer customer = context.mock(Customer.class);
		createCartForCustomer(customer);

		context.checking(new Expectations() {
			{
				oneOf(beanFactory).getBean(ContextIdNames.CUSTOMER_CREDIT_CARD); will(returnValue(createCustomerCreditCard(false)));
				oneOf(beanFactory).getBean(ContextIdNames.CREDIT_CARD_ENCRYPTER); will(returnValue(encrypter));

				allowing(customer).getCreditCards(); will(returnValue(new ArrayList<CustomerCreditCard>()));

				oneOf(customer).addCreditCard(with(any(CustomerCreditCard.class)));
				oneOf(customerService).update(customer); will(returnValue(customer));
				oneOf(encrypter).encrypt(CREDIT_CARD_NUMBER); will(returnValue(""));
			}
		});

		BindException errors = new BindException(creditCardFormBean, COMMAND);
		ModelAndView mav = controller.onSubmit(request, response, creditCardFormBean, errors);
		assertEquals("Submit should have been successful", SUCCESS_VIEW, mav.getViewName());
	}

	/**
	 * Test that submit handles an updated credit card correctly.
	 */
	@Test
	public final void testSubmitWithUpdatedCreditCard() {
		// Set up the form bean for an existing card
		final CreditCardFormBean creditCardFormBean = createCreditCardFormBean();
		final long cardUidPk = 10L;
		creditCardFormBean.setCardUidPk(cardUidPk);
		final CustomerCreditCard customerCreditCard = createCustomerCreditCard(true);
		customerCreditCard.setUidPk(cardUidPk);

		// Set up the customer mock
		final Customer customer = context.mock(Customer.class);
		createCartForCustomer(customer);

		context.checking(new Expectations() {
			{
				oneOf(beanFactory).getBean(ContextIdNames.CUSTOMER_CREDIT_CARD); will(returnValue(createCustomerCreditCard(false)));
				oneOf(customer).getCreditCardByUid(cardUidPk); will(returnValue(customerCreditCard));
				oneOf(customer).updateCreditCard(customerCreditCard);
				oneOf(customerService).update(customer); will(returnValue(customer));
			}
		});

		BindException errors = new BindException(creditCardFormBean, COMMAND);
		ModelAndView mav = controller.onSubmit(request, response, creditCardFormBean, errors);
		assertEquals("Submit should have been successful", SUCCESS_VIEW, mav.getViewName());
	}

	/**
	 * Test that submit handles deleting a credit card correctly.
	 */
	@Test
	public final void testSubmitWithDeleteRequest() {
		// Set up the form bean for an existing card
		final CreditCardFormBean creditCardFormBean = createCreditCardFormBean();
		final long cardUidPk = 10L;
		creditCardFormBean.setCardUidPk(cardUidPk);
		final CustomerCreditCard customerCreditCard = createCustomerCreditCard(true);
		customerCreditCard.setUidPk(cardUidPk);

		// Set up the customer mock
		final Customer customer = context.mock(Customer.class);
		createCartForCustomer(customer);

		context.checking(new Expectations() {
			{
				oneOf(beanFactory).getBean(ContextIdNames.CUSTOMER_CREDIT_CARD); will(returnValue(createCustomerCreditCard(false)));
				oneOf(customer).getCreditCardByUid(cardUidPk); will(returnValue(customerCreditCard));
				oneOf(customer).removeCreditCard(customerCreditCard);
				oneOf(customerService).update(customer); will(returnValue(customer));

			}
		});

		// Set up the controller and send the request
		BindException errors = new BindException(creditCardFormBean, COMMAND);
		request.addParameter(DELETE_CREDIT_CARD_PARAMETER, "true");
		ModelAndView mav = controller.onSubmit(request, response, creditCardFormBean, errors);
		assertEquals("Submit should have been successful", SUCCESS_VIEW, mav.getViewName());
	}

	/**
	 * Test that the the form will redirect to an error if storage of credit cards has been disabled.
	 */
	@Test
	public final void testRedirectIfCCStorageDisabled() {
		final SettingValue settingValue = context.mock(SettingValue.class);
		final SettingsReader settingsReader = context.mock(SettingsReader.class);
		context.checking(new Expectations() {
			{
				oneOf(settingValue).getBooleanValue(); will(returnValue(false));
				oneOf(settingsReader).getSettingValue(with("COMMERCE/SYSTEM/storeCustomerCreditCards")); will(returnValue(settingValue));
			}
		});
		controller.setSettingsReader(settingsReader);
		controller.setErrorView("error");
		try {
			ModelAndView mav = controller.showForm(request, response, null);
			assertEquals("returned view should be the error view", "error", mav.getViewName());
			assertEquals("return model should contain a not found error code", String.valueOf(HttpServletResponse.SC_NOT_FOUND),
					mav.getModel().get("errorKey"));
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}

	/**
	 * Test that trying to add a duplicate card returns the appropriate error.
	 */
	@Test
	public void testAddDuplicateCreditCard() {
		CreditCardFormBean creditCardFormBean = createCreditCardFormBean();

		final long cardUidPk = 10L;
		final CustomerCreditCard customerCreditCard = createCustomerCreditCard(true);
		customerCreditCard.setUidPk(cardUidPk);
		customerCreditCard.setCardNumber("encryptednumber");
		final List<CustomerCreditCard> existingCards = new ArrayList<CustomerCreditCard>();
		existingCards.add(customerCreditCard);

		final Customer customer = context.mock(Customer.class);
		createCartForCustomer(customer);

		final CreditCardEncrypter encrypter = context.mock(CreditCardEncrypter.class);

		context.checking(new Expectations() {
			{
				oneOf(beanFactory).getBean(ContextIdNames.CUSTOMER_CREDIT_CARD); will(returnValue(createCustomerCreditCard(false)));
				oneOf(beanFactory).getBean(ContextIdNames.CREDIT_CARD_ENCRYPTER); will(returnValue(encrypter));
				oneOf(encrypter).encrypt(CREDIT_CARD_NUMBER); will(returnValue("encryptednumber"));

				oneOf(customer).getCreditCards(); will(returnValue(existingCards));
			}
		});

		BindException errors = new BindException(creditCardFormBean, COMMAND);
		CreditCardFormControllerImpl controller = new CreditCardFormControllerImpl() {
			@Override
			protected Map<String, Map<String, String>> referenceData(final HttpServletRequest request) {
				return new HashMap<String, Map<String, String>>();
			}

		};
		controller.setBeanFactory(beanFactory);
		controller.setRequestHelper(requestHelper);
		controller.setCustomerService(customerService);

		ModelAndView mav = controller.onSubmit(request, response, creditCardFormBean, errors);
		BeanPropertyBindingResult bindingResult = (BeanPropertyBindingResult) mav.getModel().get(BINDING_RESULT_COMMAND);
		assertEquals("There should be an error in the binding result", 1, bindingResult.getErrorCount());
		assertEquals("We should get an duplicate card error", "error.creditcard.exist", bindingResult.getAllErrors().get(0).getCode());

	}

	private CreditCardFormBean createCreditCardFormBean() {
		CreditCardFormBean bean = new CreditCardFormBeanImpl();
		bean.setCardHolderName(CARD_HOLDER_NAME);
		bean.setCardNumber(CREDIT_CARD_NUMBER);
		bean.setCardType(CREDIT_CARD_TYPE);
		bean.setExpiryMonth(EXPIRY_MONTH);
		bean.setExpiryYear(EXPIRY_YEAR);
		bean.setDefaultCard(false);
		bean.setStartMonth(START_MONTH);
		bean.setStartYear(START_YEAR);
		bean.setIssueNumber(ISSUE_NUMBER);
		return bean;
	}

	private CustomerCreditCard createCustomerCreditCard(final boolean populate) {
		CustomerCreditCard card = new CustomerCreditCardImpl();
		if (populate) {
			card.setCardHolderName(CARD_HOLDER_NAME);
			card.setCardNumber(CREDIT_CARD_NUMBER);
			card.setCardType(CREDIT_CARD_TYPE);
			card.setExpiryMonth(EXPIRY_MONTH);
			card.setExpiryYear(EXPIRY_YEAR);
			card.setDefaultCard(false);
			card.setStartMonth(START_MONTH);
			card.setStartYear(START_YEAR);
			card.setIssueNumber(ISSUE_NUMBER);
		}
		return card;
	}


	private void createCartForCustomer(final Customer customer) {
		final CustomerSession customerSession = context.mock(CustomerSession.class);
		final Shopper shopper = context.mock(Shopper.class);

		context.checking(new Expectations() {
			{
				allowing(requestHelper).getCustomerSession(request); will(returnValue(customerSession));
				allowing(customerSession).getShopper(); will(returnValue(shopper));
				allowing(shopper).getCustomer(); will(returnValue(customer));
				allowing(shopper).setCustomer(with(customer));
				allowing(customerSession).getShopper().setCustomer(customer);
			}
		});
	}
}
