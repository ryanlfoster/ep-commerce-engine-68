package com.elasticpath.service.misc.impl;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.mail.EmailException;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.misc.EmailProperties;
import com.elasticpath.service.misc.OrderEmailPropertyHelper;
import com.elasticpath.domain.order.OrderReturn;
import com.elasticpath.service.misc.EmailService;
import com.elasticpath.service.order.ReturnAndExchangeService;

/** Test cases for <code>OrderReturnEmailServiceImpl</code>. */
public class OrderReturnEmailServiceImplTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private EmailService mockEmailService;

	private OrderReturnEmailServiceImpl orderReturnEmailService;

	private ReturnAndExchangeService mockReturnAndExchangeService;

	private OrderEmailPropertyHelper mockOrderEmailPropertyHelper;

	/**
	 * Prepare for the tests.
	 * 
	 * @throws Exception on error
	 */
	@Before
	public void setUp() throws Exception {
		orderReturnEmailService = new OrderReturnEmailServiceImpl();
		mockEmailService = context.mock(EmailService.class);
		orderReturnEmailService.setEmailService(mockEmailService);
		mockReturnAndExchangeService = context.mock(ReturnAndExchangeService.class);
		orderReturnEmailService.setReturnAndExchangeService(mockReturnAndExchangeService);
		mockOrderEmailPropertyHelper = context.mock(OrderEmailPropertyHelper.class);
		orderReturnEmailService.setOrderEmailPropertyHelper(mockOrderEmailPropertyHelper);

	}

	/**
	 * Test send order return confirmation email success.
	 * 
	 * @throws EmailException email exception
	 */
	@Test
	public void testSendOrderReturnEmailSuccess() throws EmailException {

		final EmailProperties mockEmailProperties = context.mock(EmailProperties.class);
		final long orderReturnUid = 12346L;
		final OrderReturn orderReturn = context.mock(OrderReturn.class);
		final List<OrderReturn> orderReturns = new ArrayList<OrderReturn>();
		orderReturns.add(orderReturn);

		context.checking(new Expectations() {
			{
				exactly(2).of(mockReturnAndExchangeService).findByUids(Collections.<Long>singletonList(orderReturnUid));
				will(returnValue(orderReturns));
				exactly(2).of(mockOrderEmailPropertyHelper).getOrderReturnEmailProperties(orderReturn);
				will(returnValue(mockEmailProperties));
				exactly(2).of(mockEmailService).sendOrderReturnEmail(mockEmailProperties);
				will(returnValue(true));
			}
		});

		Assert.assertTrue(orderReturnEmailService.sendOrderReturnEmail(orderReturnUid, null));
		final String recipientAddress = "test@test.com";
		context.checking(new Expectations() {
			{
				oneOf(mockEmailProperties).setRecipientAddress(recipientAddress);
			}
		});

		Assert.assertTrue(orderReturnEmailService.sendOrderReturnEmail(orderReturnUid, recipientAddress));
	}

	/**
	 * Test send order return confirmation email failure.
	 * 
	 * @throws EmailException email exception
	 */
	@Test
	public void testOrderReturnEmailFailure() throws EmailException {

		final long orderReturnUid = 12346L;
		final List<OrderReturn> orderReturns = new ArrayList<OrderReturn>();

		context.checking(new Expectations() {
			{
				oneOf(mockReturnAndExchangeService).findByUids(Collections.<Long>singletonList(orderReturnUid));
				will(returnValue(orderReturns));
			}
		});

		Assert.assertFalse(orderReturnEmailService.sendOrderReturnEmail(orderReturnUid, null));

		final OrderReturn orderReturn = context.mock(OrderReturn.class);
		orderReturns.add(orderReturn);

		final EmailProperties mockEmailProperties = context.mock(EmailProperties.class);
		context.checking(new Expectations() {
			{
				oneOf(mockReturnAndExchangeService).findByUids(Collections.<Long>singletonList(orderReturnUid));
				will(returnValue(orderReturns));
				oneOf(mockOrderEmailPropertyHelper).getOrderReturnEmailProperties(orderReturn);
				will(returnValue(mockEmailProperties));
				oneOf(mockEmailService).sendOrderReturnEmail(mockEmailProperties);
				will(throwException(new EmailException("exception")));
			}
		});

		try {

			orderReturnEmailService.sendOrderReturnEmail(orderReturnUid, null);
			fail("No exception is thrown");
		} catch (EmailException ee) { //NOPMD
			// no need to do anything here 
		} catch (Exception e) {
			fail("Send email threw unexpected exception");
		}
	}

}
