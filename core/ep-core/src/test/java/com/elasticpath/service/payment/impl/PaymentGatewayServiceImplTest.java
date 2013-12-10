/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.service.payment.impl;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;

import org.jmock.Expectations;
import org.junit.Test;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.payment.PaymentGateway;
import com.elasticpath.domain.payment.PaymentGatewayFactory;
import com.elasticpath.domain.payment.impl.PaymentGatewayImpl;
import com.elasticpath.service.payment.PaymentGatewayService;
import com.elasticpath.service.payment.gateway.impl.NullPaymentGatewayPluginImpl;
import com.elasticpath.test.jmock.AbstractEPServiceTestCase;


/**
 * Test case for <code>PaymentGatewayServiceImpl</code>.
 */
public class PaymentGatewayServiceImplTest extends AbstractEPServiceTestCase {

	private PaymentGatewayService paymentGatewayServiceImpl;
	private PaymentGatewayFactory mockPaymentGatewayFactory;

	/**
	 * Prepares for tests.
	 *
	 * @throws Exception -- in case of any errors.
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		paymentGatewayServiceImpl = new PaymentGatewayServiceImpl();
		paymentGatewayServiceImpl.setPersistenceEngine(getPersistenceEngine());

		mockPaymentGatewayFactory = context.mock(PaymentGatewayFactory.class);
		stubGetBean(ContextIdNames.PAYMENT_GATEWAY_FACTORY, mockPaymentGatewayFactory);
	}

	/**
	 * Test method for
	 * 'com.elasticpath.service.payment.PaymentGatewayServiceImpl.saveOrUpdate(PaymentGateway)'.
	 */
	@Test
	public void testSaveOrUpdate() {
		final PaymentGateway paymentGateway = createNullPaymentGateway();
		final PaymentGateway updatedPaymentGateway = createNullPaymentGateway();

		// expectations
		context.checking(new Expectations() {
			{
				oneOf(getMockPersistenceEngine()).saveOrUpdate(with(same(paymentGateway)));
				will(returnValue(updatedPaymentGateway));

				oneOf(mockPaymentGatewayFactory).getPluginGatewayPluginManagement("paymentGatewayNull");
				will(returnValue(new NullPaymentGatewayPluginImpl()));
			}
		});
		final PaymentGateway returnedPaymentGateway = paymentGatewayServiceImpl.saveOrUpdate(paymentGateway);
		assertSame(updatedPaymentGateway, returnedPaymentGateway);
	}

	/**
	 * Test method for
	 * 'com.elasticpath.service.payment.PaymentGatewayServiceImpl.remove(PaymentGateway)'.
	 */
	@Test
	public void testRemove() {
		final PaymentGateway paymentGateway = createNullPaymentGateway();
		context.checking(new Expectations() {
			{
				oneOf(getMockPersistenceEngine()).delete(with(same(paymentGateway)));
			}
		});
		paymentGatewayServiceImpl.remove(paymentGateway);
	}

	/**
	 * Test method for
	 * 'com.elasticpath.service.payment.PaymentGatewayServiceImpl.getGateway(Long)'.
	 */
	@Test
	public void testGetGateway() {
		stubGetBean(ContextIdNames.ABSTRACT_PAYMENT_GATEWAY, PaymentGatewayImpl.class);

		final long uid = 1234L;
		final PaymentGateway paymentGateway = new PaymentGatewayImpl();
		paymentGateway.setUidPk(uid);
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).get(PaymentGatewayImpl.class, uid);
				will(returnValue(paymentGateway));
			}
		});
		assertSame(paymentGateway, paymentGatewayServiceImpl.getGateway(uid));
		assertSame(paymentGateway, paymentGatewayServiceImpl.getObject(uid));

		final long nonExistUid = 3456L;
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).get(PaymentGatewayImpl.class, nonExistUid);
				will(returnValue(null));
			}
		});
		assertNull(paymentGatewayServiceImpl.getGateway(nonExistUid));

		assertNull(paymentGatewayServiceImpl.getGateway(0));
	}

	/**
	 * Test method for
	 * 'com.elasticpath.service.payment.PaymentGatewayServiceImpl.findAllPaymentGatewayUids()'.
	 */
	@Test
	public void testFindAllWarehouseUids() {
		final List<Long> uidList = new ArrayList<Long>();
		// expectations
		context.checking(new Expectations() {
			{
				oneOf(getMockPersistenceEngine()).retrieveByNamedQuery(with("FIND_ALL_PAYMENT_GATEWAY_UIDS"), with(any(Object[].class)));
				will(returnValue(uidList));
			}
		});
		assertSame(uidList, paymentGatewayServiceImpl.findAllPaymentGatewayUids());
		// make sure the query returns something seemingly valid
		final PaymentGateway paymentGateway = new PaymentGatewayImpl();
		final long warehouseUid = 1234L;
		paymentGateway.setUidPk(warehouseUid);
		uidList.add(warehouseUid);
		// expectations
		context.checking(new Expectations() {
			{
				oneOf(getMockPersistenceEngine()).retrieveByNamedQuery(with("FIND_ALL_PAYMENT_GATEWAY_UIDS"), with(any(Object[].class)));
				will(returnValue(uidList));
			}
		});
		assertSame(uidList, paymentGatewayServiceImpl.findAllPaymentGatewayUids());
	}

	/**
	 * Test method for
	 * 'com.elasticpath.service.payment.PaymentGatewayServiceImpl.findAllPaymentGateways()'.
	 */
	@Test
	public void testFindAllWarehouses() {
		final List<PaymentGateway> paymentGatewayList = new ArrayList<PaymentGateway>();
		// expectations
		context.checking(new Expectations() {
			{
				oneOf(getMockPersistenceEngine()).retrieveByNamedQuery(with("FIND_ALL_PAYMENT_GATEWAYS"), with(any(Object[].class)));
				will(returnValue(paymentGatewayList));
			}
		});
		assertSame(paymentGatewayList, paymentGatewayServiceImpl.findAllPaymentGateways());

		// make sure the query returns something seemingly valid
		final PaymentGateway paymentGateway = new PaymentGatewayImpl();
		final long paymentGatewayUid = 1234L;
		paymentGateway.setUidPk(paymentGatewayUid);
		paymentGatewayList.add(paymentGateway);
		// expectations
		context.checking(new Expectations() {
			{
				oneOf(getMockPersistenceEngine()).retrieveByNamedQuery(with("FIND_ALL_PAYMENT_GATEWAYS"), with(any(Object[].class)));
				will(returnValue(paymentGatewayList));
			}
		});
		assertSame(paymentGatewayList, paymentGatewayServiceImpl.findAllPaymentGateways());
	}

	private PaymentGateway createNullPaymentGateway() {
		final PaymentGateway paymentGateway = new PaymentGatewayImpl();
		paymentGateway.setType("paymentGatewayNull");
		return paymentGateway;
	}

}
