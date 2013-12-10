package com.elasticpath.service.shoppingcart.actions.impl;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.catalog.GiftCertificate;
import com.elasticpath.domain.catalog.impl.GiftCertificateImpl;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.plugin.payment.exceptions.InsufficientGiftCertificateBalanceException;
import com.elasticpath.service.catalog.GiftCertificateNotFoundException;
import com.elasticpath.service.catalog.GiftCertificateService;

/**
 * Test class for {@link GiftCertificateBalanceCheckerCheckoutAction}.
 */
public class GiftCertificateBalanceCheckerCheckoutActionTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private GiftCertificateBalanceCheckerCheckoutAction checkoutAction;

	private GiftCertificateService giftCertificateService;

	private ShoppingCart shoppingCart;

	@Before
	public void setUp() throws Exception {
		giftCertificateService = context.mock(GiftCertificateService.class);
		shoppingCart = context.mock(ShoppingCart.class);

		context.checking(new Expectations() {
			{
				allowing(shoppingCart).getGuid();
				will(returnValue("SHOPCART1"));
			}
		});

		checkoutAction = new GiftCertificateBalanceCheckerCheckoutAction();
		checkoutAction.setGiftCertificateService(giftCertificateService);
	}

	/**
	 * Verifies that no action is performed when no gift certificates are applied.
	 */
	@Test
	public void testExecuteDoesNothingWhenNoAppliedGiftCertificates() throws Exception {
		context.checking(new Expectations() {
			{
				allowing(shoppingCart).getNumItems();
				will(returnValue(1));

				atLeast(1).of(shoppingCart).getAppliedGiftCertificates();
				will(returnValue(new HashSet<GiftCertificate>()));
			}
		});

		checkoutAction.execute(new CheckoutActionContextImpl(shoppingCart, null, false, false, null));
	}

	/**
	 * Verifies an exception is thrown when an applied gift certificate does not exist.
	 */
	@Test(expected = GiftCertificateNotFoundException.class)
	public void testExecuteThrowsExceptionWhenAppliedGiftCertificateDoesNotExist() throws Exception {
		context.checking(new Expectations() {
			{
				allowing(shoppingCart).getNumItems();
				will(returnValue(1));

				allowing(shoppingCart).getGiftCertificateDiscount();
				will(returnValue(BigDecimal.ONE));

				final String noSuchGiftCertificateCode = "GC123";
				final GiftCertificate noSuchGiftCertificate = new GiftCertificateImpl();
				noSuchGiftCertificate.setGiftCertificateCode(noSuchGiftCertificateCode);

				atLeast(1).of(shoppingCart).getAppliedGiftCertificates();
				will(returnValue(Collections.singleton(noSuchGiftCertificate)));

				atLeast(1).of(giftCertificateService).findByGiftCertificateCode(noSuchGiftCertificateCode);
				will(returnValue(null));
			}
		});

		checkoutAction.execute(new CheckoutActionContextImpl(shoppingCart, null, false, false, null));
	}

	/**
	 * Verifies an exception is thrown when the amount to be paid in Gift Certificates exceeds the combined balance of all Gift Certificates.
	 */
	@Test(expected = InsufficientGiftCertificateBalanceException.class)
	public void testExecuteThrowsExceptionWhenInsufficientGiftCertificateBalance() throws Exception {
		context.checking(new Expectations() {
			{
				allowing(shoppingCart).getNumItems();
				will(returnValue(1));

				allowing(shoppingCart).getGiftCertificateDiscount();
				will(returnValue(BigDecimal.TEN));

				final String oneDollarGiftCertificateCode = "GCONEDOLLAR";
				final String fiveDollarGiftCertificateCode = "GCFIVEDOLLAR";

				final GiftCertificate oneDollarGiftCertificate = new GiftCertificateImpl();
				oneDollarGiftCertificate.setUidPk(1);
				oneDollarGiftCertificate.setGiftCertificateCode(oneDollarGiftCertificateCode);

				final long fiveDollarUid = 5L;
				final GiftCertificate fiveDollarGiftCertificate = new GiftCertificateImpl();
				fiveDollarGiftCertificate.setUidPk(fiveDollarUid);
				fiveDollarGiftCertificate.setGiftCertificateCode(fiveDollarGiftCertificateCode);

				final Set<GiftCertificate> giftCertificates = new HashSet<GiftCertificate>(2);
				giftCertificates.add(oneDollarGiftCertificate);
				giftCertificates.add(fiveDollarGiftCertificate);

				atLeast(1).of(shoppingCart).getAppliedGiftCertificates();
				will(returnValue(giftCertificates));

				atLeast(1).of(giftCertificateService).findByGiftCertificateCode(oneDollarGiftCertificateCode);
				will(returnValue(oneDollarGiftCertificate));
				atLeast(1).of(giftCertificateService).findByGiftCertificateCode(fiveDollarGiftCertificateCode);
				will(returnValue(fiveDollarGiftCertificate));

				atLeast(1).of(giftCertificateService).getBalance(oneDollarGiftCertificate);
				will(returnValue(BigDecimal.ONE));
				atLeast(1).of(giftCertificateService).getBalance(fiveDollarGiftCertificate);
				will(returnValue(new BigDecimal("5")));
			}
		});

		checkoutAction.execute(new CheckoutActionContextImpl(shoppingCart, null, false, false, null));
	}

	/**
	 * Tests the case when the balance is paid exactly by one gift certificate.
	 */
	@Test
	public void testExecuteWithOneExactValueGiftCertificate() throws Exception {
		context.checking(new Expectations() {
			{
				allowing(shoppingCart).getNumItems();
				will(returnValue(1));

				allowing(shoppingCart).getGiftCertificateDiscount();
				will(returnValue(BigDecimal.TEN));

				final String tenDollarGiftCertificateCode = "GCTENDOLLAR";

				final long tenDollarUid = 10L;
				final GiftCertificate tenDollarGiftCertificate = new GiftCertificateImpl();
				tenDollarGiftCertificate.setUidPk(tenDollarUid);
				tenDollarGiftCertificate.setGiftCertificateCode(tenDollarGiftCertificateCode);

				final Set<GiftCertificate> giftCertificates = new HashSet<GiftCertificate>(1);
				giftCertificates.add(tenDollarGiftCertificate);

				atLeast(1).of(shoppingCart).getAppliedGiftCertificates();
				will(returnValue(giftCertificates));

				atLeast(1).of(giftCertificateService).findByGiftCertificateCode(tenDollarGiftCertificateCode);
				will(returnValue(tenDollarGiftCertificate));

				atLeast(1).of(giftCertificateService).getBalance(tenDollarGiftCertificate);
				will(returnValue(BigDecimal.TEN));
			}
		});

		checkoutAction.execute(new CheckoutActionContextImpl(shoppingCart, null, false, false, null));
	}

	/**
	 * Tests the case when the balance is paid by one gift certificate with gift certificate funds to spare.
	 */
	@Test
	public void testExecuteWithExtraFundsInGiftCertificate() throws Exception {
		context.checking(new Expectations() {
			{
				allowing(shoppingCart).getNumItems();
				will(returnValue(1));

				allowing(shoppingCart).getGiftCertificateDiscount();
				will(returnValue(BigDecimal.ONE));

				final String tenDollarGiftCertificateCode = "GCTENDOLLAR";

				final long tenDollarUid = 10L;
				final GiftCertificate tenDollarGiftCertificate = new GiftCertificateImpl();
				tenDollarGiftCertificate.setUidPk(tenDollarUid);
				tenDollarGiftCertificate.setGiftCertificateCode(tenDollarGiftCertificateCode);

				final Set<GiftCertificate> giftCertificates = new HashSet<GiftCertificate>(1);
				giftCertificates.add(tenDollarGiftCertificate);

				atLeast(1).of(shoppingCart).getAppliedGiftCertificates();
				will(returnValue(giftCertificates));

				atLeast(1).of(giftCertificateService).findByGiftCertificateCode(tenDollarGiftCertificateCode);
				will(returnValue(tenDollarGiftCertificate));

				atLeast(1).of(giftCertificateService).getBalance(tenDollarGiftCertificate);
				will(returnValue(BigDecimal.TEN));
			}
		});

		checkoutAction.execute(new CheckoutActionContextImpl(shoppingCart, null, false, false, null));
	}

	/**
	 * Tests the case when the balance is paid exactly by two gift certificates.
	 */
	@Test
	public void testExecuteWithTwoExactValueGiftCertificates() throws Exception {
		context.checking(new Expectations() {
			{
				allowing(shoppingCart).getNumItems();
				will(returnValue(1));

				allowing(shoppingCart).getGiftCertificateDiscount();
				will(returnValue(new BigDecimal("6")));

				final String oneDollarGiftCertificateCode = "GCONEDOLLAR";
				final String fiveDollarGiftCertificateCode = "GCFIVEDOLLAR";

				final GiftCertificate oneDollarGiftCertificate = new GiftCertificateImpl();
				oneDollarGiftCertificate.setUidPk(1);
				oneDollarGiftCertificate.setGiftCertificateCode(oneDollarGiftCertificateCode);

				final long fiveDollarUid = 5L;
				final GiftCertificate fiveDollarGiftCertificate = new GiftCertificateImpl();
				fiveDollarGiftCertificate.setUidPk(fiveDollarUid);
				fiveDollarGiftCertificate.setGiftCertificateCode(fiveDollarGiftCertificateCode);

				final Set<GiftCertificate> giftCertificates = new HashSet<GiftCertificate>(2);
				giftCertificates.add(oneDollarGiftCertificate);
				giftCertificates.add(fiveDollarGiftCertificate);

				atLeast(1).of(shoppingCart).getAppliedGiftCertificates();
				will(returnValue(giftCertificates));

				atLeast(1).of(giftCertificateService).findByGiftCertificateCode(oneDollarGiftCertificateCode);
				will(returnValue(oneDollarGiftCertificate));
				atLeast(1).of(giftCertificateService).findByGiftCertificateCode(fiveDollarGiftCertificateCode);
				will(returnValue(fiveDollarGiftCertificate));

				atLeast(1).of(giftCertificateService).getBalance(oneDollarGiftCertificate);
				will(returnValue(BigDecimal.ONE));
				atLeast(1).of(giftCertificateService).getBalance(fiveDollarGiftCertificate);
				will(returnValue(new BigDecimal("5")));
			}
		});

		checkoutAction.execute(new CheckoutActionContextImpl(shoppingCart, null, false, false, null));
	}

}