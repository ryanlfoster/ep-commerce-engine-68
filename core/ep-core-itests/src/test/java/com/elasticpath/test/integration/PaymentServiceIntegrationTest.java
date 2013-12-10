 /**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.test.integration;

 import org.junit.Ignore;

 /**
 * Tests various scenarios of order maintenance.
 */
@Ignore
public class PaymentServiceIntegrationTest {

//	/** Predefined quantities. */
//	private static final int QTY_1 = 1;
//
//	private static final int QTY_2 = 2;
//
//	private static final int QTY_3 = 3;
//
//	private static final int QTY_4 = 4;
//
//	private static final int QTY_5 = 5;
//
//	private static final int QTY_6 = 6;
//
//	private PaymentHelper paymentHelper;
//
//	private ProductHelper skuHelper;
//
//	private OrderHandler handler;
//
//	private OrderHandlerVerifier verifier;
//
//	private ElasticPath elasticPath;
//
//	private SimpleStoreScenario scenario;
//
//	private TestApplicationContext tac;
//
//	private TestDataPersisterFactory persisterContainer;
//
//	/**
//	 * Get a reference to TestApplicationContext for use within the test. Setup scenarios.
//	 * 
//	 * @throws Exception if any exception is thrown
//	 */
//	@Override
//	public void setUp() throws Exception {
//		super.setUp();
//		tac = TestApplicationContext.getInstance();
//		tac.useDb(getClass().getName());
//		scenario = (SimpleStoreScenario) tac.useScenario(SimpleStoreScenario.class);
//		elasticPath = tac.getElasticPath();
//		persisterContainer = tac.getPersistersFactory();
//
//		handler = new OrderHandler(elasticPath, persisterContainer, scenario);
//
//		skuHelper = new ProductHelper();
//		skuHelper.setElasticPath(handler.getElasticPath());
//		skuHelper.setProduct(handler.getProduct());
//
//		paymentHelper = PaymentHelper.createPaymentHelper(handler.getElasticPath());
//		verifier = new OrderHandlerVerifier();
//	}
//
//	/**
//	 * The method: <br>
//	 * 1) create order of 4 skus + $10 shipping cost. Total=$50. Paid by GC(max$50)<br>
//	 * 2) decrease 1st shipment on 1 sku. Total=$40.<br>
//	 * 3) increase 1st shipment on 2 sku. Total=$60. Additional auth by 1st GC. Should fail.<br>
//	 * 4) again increase 1st shipment on 2 sku. Total=$60. Additional auth by CC.<br>
//	 * 5) add shipment of 4 skus + $10 shipping cost. Total=$50. Pay by 1st GC. Is Ok, since it should be reversed.<br>
//	 * 6) Release both shipments<br>
//	 * 7) Complete both shipments<br>
//	 */
//	public void testPureGC() {
//		GiftCertificate giftCertificate = handler.createGiftCertificate();
//
//		/** (total = 4 skus * $10 + $10 shipping = $50) (CC, GC(max$50)) */
//		handler.createOrderByConventionalPaymentAndGCs(skuHelper.getShoppingCartItem(QTY_4), paymentHelper.creditCardTemplatePayment(),
//				giftCertificate);
//		assertTrue(verifier.checkPayments(handler, 1, paymentHelper.giftCertificateAuthPayment(PaymentHelper.AMOUNT_50)));
//
//		handler.decreaseShipment(1, QTY_1);
//		assertTrue(verifier.checkSkus(handler, 1, QTY_3));
//		assertTrue(verifier.checkPayments(handler, 1, paymentHelper.giftCertificateAuthPayment(PaymentHelper.AMOUNT_50)));
//
//		/** Try to reauth with the same GC */
//		PaymentResult result = handler.increaseShipment(1, QTY_2, paymentHelper.giftCertificateTemplatePayment(giftCertificate));
//		assertSame(result.getResultCode(), PaymentResult.CODE_FAILED);
//
//		/** Same state should be. */
//		assertTrue(verifier.checkPayments(handler, 1, paymentHelper.giftCertificateAuthPayment(PaymentHelper.AMOUNT_50)));
//
//		handler.increaseShipment(1, QTY_2, paymentHelper.creditCardTemplatePayment());
//		assertTrue(verifier.checkSkus(handler, 1, QTY_5));
//		assertTrue(verifier.checkPayments(handler, 1, paymentHelper.giftCertificateAuthReversedPayment(PaymentHelper.AMOUNT_50), paymentHelper
//				.giftCertificateReversePayment(PaymentHelper.AMOUNT_50), paymentHelper.creditCartAuthPayment(PaymentHelper.AMOUNT_60)));
//
//		/** (total = 4 skus * $10 + $10 shipping = $50) (GC(max$50), make sure that 1st GC was successfully reversed) */
//		handler.addPhysicalShipment(skuHelper.getOrderSku(QTY_4, handler), paymentHelper.giftCertificateTemplatePayment(giftCertificate));
//		assertTrue(verifier.checkPayments(handler, 2, paymentHelper.giftCertificateAuthPayment(PaymentHelper.AMOUNT_50)));
//
//		handler.releaseShipment(1);
//		handler.releaseShipment(2);
//
//		handler.completeShipment(1);
//		handler.completeShipment(2);
//
//		assertTrue(verifier.checkPayments(handler, 1, paymentHelper.giftCertificateAuthReversedPayment(PaymentHelper.AMOUNT_50), paymentHelper
//				.giftCertificateReversePayment(PaymentHelper.AMOUNT_50), paymentHelper.creditCartAuthPayment(PaymentHelper.AMOUNT_60), paymentHelper
//				.creditCartCapturePayment(PaymentHelper.AMOUNT_60)));
//
//		assertTrue(verifier.checkPayments(handler, 2, paymentHelper.giftCertificateAuthPayment(PaymentHelper.AMOUNT_50), paymentHelper
//				.giftCertificateCapturePayment(PaymentHelper.AMOUNT_50)));
//
//		handler.clear();
//
//	}
//
//	/**
//	 * The method: <br>
//	 * 1) create 2 gift certificates(max$50 each)<br>
//	 * 2) create order of 6 skus + $10 shipping cost. Total=$70. Paid by GC1 and GC2<br>
//	 * 3) decrease 1st shipment on 4 sku. Total=$30.<br>
//	 * 4) release shipment<br>
//	 * 5) complete shipment<br>
//	 * 6) add shipment of 1 skus + $10 shipping cost. Total=$20. Pay by 1st GC.<br>
//	 * 7) add shipment of 4 skus + $10 shipping cost. Total=$50. Pay by 2nd GC.<br>
//	 * 7) Release 2nd and 3rd shipments<br>
//	 * 8) Complete 2nd and 3rd shipments<br>
//	 */
//	public void testPureGCDecrease() {
//		GiftCertificate giftCertificate1 = handler.createGiftCertificate();
//		GiftCertificate giftCertificate2 = handler.createGiftCertificate();
//
//		/** (total = 6 skus * $10 + $10 shipping = $70) (2 GC(max$50)) */
//		handler.createOrderByConventionalPaymentAndGCs(skuHelper.getShoppingCartItem(QTY_6), paymentHelper.creditCardTemplatePayment(),
//				giftCertificate1, giftCertificate2);
//		assertTrue(verifier.checkPayments(handler, 1, paymentHelper.giftCertificateAuthPayment(PaymentHelper.AMOUNT_50), paymentHelper
//				.giftCertificateAuthPayment(PaymentHelper.AMOUNT_20)));
//
//		handler.decreaseShipment(1, QTY_4);
//		assertTrue(verifier.checkSkus(handler, 1, QTY_2));
//
//		handler.releaseShipment(1);
//		handler.completeShipment(1);
//
//		assertTrue(verifier.checkPayments(handler, 1, paymentHelper.giftCertificateAuthPayment(PaymentHelper.AMOUNT_50), paymentHelper
//				.giftCertificateAuthReversedPayment(PaymentHelper.AMOUNT_20), paymentHelper.giftCertificateCapturePayment(PaymentHelper.AMOUNT_30),
//				paymentHelper.giftCertificateReversePayment(PaymentHelper.AMOUNT_20)));
//
//		/** now 1st GC should have $20 balance and second - $50 */
//		handler.addPhysicalShipment(skuHelper.getOrderSku(QTY_1, handler), paymentHelper.giftCertificateTemplatePayment(giftCertificate1));
//
//		handler.addPhysicalShipment(skuHelper.getOrderSku(QTY_4, handler), paymentHelper.giftCertificateTemplatePayment(giftCertificate2));
//
//		handler.releaseShipment(2);
//		handler.releaseShipment(3);
//		handler.completeShipment(2);
//		handler.completeShipment(3);
//
//		assertTrue(verifier.checkPayments(handler, 2, paymentHelper.giftCertificateAuthPayment(PaymentHelper.AMOUNT_20), paymentHelper
//				.giftCertificateCapturePayment(PaymentHelper.AMOUNT_20)));
//
//		assertTrue(verifier.checkPayments(handler, 3, paymentHelper.giftCertificateAuthPayment(PaymentHelper.AMOUNT_50), paymentHelper
//				.giftCertificateCapturePayment(PaymentHelper.AMOUNT_50)));
//
//		handler.clear();
//
//	}
//
//	/**
//	 * The method: <br>
//	 * 1) create 1 gift certificates(max$50 each)<br>
//	 * 2) create order of 6 skus + $10 shipping cost. Total=$70. Paid by GC and CC<br>
//	 * 3) cancels the order<br>
//	 */
//	public void testCancelation() {
//		GiftCertificate giftCertificate = handler.createGiftCertificate();
//
//		/** (total = 6 skus * $10 + $10 shipping = $70) (CC, GC(max$50)) */
//		handler.createOrderByConventionalPaymentAndGCs(skuHelper.getShoppingCartItem(6), paymentHelper.creditCardTemplatePayment(), giftCertificate);
//		assertTrue(verifier.checkPayments(handler, paymentHelper.creditCartAuthPayment(PaymentHelper.AMOUNT_20), paymentHelper
//				.giftCertificateAuthPayment(PaymentHelper.AMOUNT_50)));
//
//		handler.cancelOrder();
//		assertTrue(verifier.checkPayments(handler, paymentHelper.creditCartAuthReversedPayment(PaymentHelper.AMOUNT_20), paymentHelper
//				.giftCertificateAuthReversedPayment(PaymentHelper.AMOUNT_50), paymentHelper.creditCartReversePayment(PaymentHelper.AMOUNT_20),
//				paymentHelper.giftCertificateReversePayment(PaymentHelper.AMOUNT_50)));
//		handler.clear();
//
//	}
//
//	/**
//	 * The method: <br>
//	 * 1) create order of 2 skus + $10 shipping cost. Total=$30. Paid by CC<br>
//	 * 2) decrease 1st shipment on 1 sku. Total=$20.<br>
//	 * 3) add shipment of 1 skus + $10 shipping cost. Total=$20. Pay by CC.<br>
//	 * 4) add shipment of 1 skus + $10 shipping cost. Total=$20. Pay by CC.<br>
//	 * 5) increase 3rd shipment on 2 skus. Total=$40<br>
//	 * 6) decrease 3rd shipment on 1 sku. Total=$30.<br>
//	 */
//	public void testPureCreditCard() {
//		/** 2 skus, $10 each paid by CC */
//		handler.createOrderByConventionalPayment(skuHelper.getShoppingCartItem(QTY_2), paymentHelper.creditCardTemplatePayment());
//		assertTrue(verifier.checkPayments(handler, paymentHelper.creditCartAuthPayment(PaymentHelper.AMOUNT_30)));
//		handler.decreaseShipment(1, QTY_1);
//		assertTrue(verifier.checkSkus(handler, 1, QTY_1));
//		/** No auth changes should occur. */
//		assertTrue(verifier.checkPayments(handler, paymentHelper.creditCartAuthPayment(PaymentHelper.AMOUNT_30)));
//
//		handler.addPhysicalShipment(skuHelper.getOrderSku(QTY_1, handler), paymentHelper.creditCardTemplatePayment());
//		verifier.checkShipments(handler, QTY_2);
//
//		/** 1 sku, $10 each paid by CC */
//		assertTrue(verifier.checkPayments(handler, 2, paymentHelper.creditCartAuthPayment(PaymentHelper.AMOUNT_20)));
//
//		handler.addPhysicalShipment(skuHelper.getOrderSku(QTY_1, handler), paymentHelper.creditCardTemplatePayment());
//		verifier.checkShipments(handler, QTY_3);
//
//		/** 1 sku, $10 each paid by CC + $10 shipment */
//		assertTrue(verifier.checkPayments(handler, 3, paymentHelper.creditCartAuthPayment(PaymentHelper.AMOUNT_20)));
//		handler.increaseShipment(3, QTY_2, paymentHelper.creditCardTemplatePayment());
//
//		assertTrue(verifier.checkPayments(handler, 3, paymentHelper.creditCartAuthReversedPayment(PaymentHelper.AMOUNT_20), paymentHelper
//				.creditCartReversePayment(PaymentHelper.AMOUNT_20), paymentHelper.creditCartAuthPayment(PaymentHelper.AMOUNT_40)));
//		handler.decreaseShipment(3, QTY_1);
//
//		handler.releaseShipment(1);
//		handler.releaseShipment(2);
//		handler.releaseShipment(3);
//
//		handler.completeShipment(1);
//		handler.completeShipment(2);
//		handler.completeShipment(3);
//
//		assertTrue(verifier.checkPayments(handler, 1, paymentHelper.creditCartAuthPayment(PaymentHelper.AMOUNT_30), paymentHelper
//				.creditCartCapturePayment(PaymentHelper.AMOUNT_20)));
//		assertTrue(verifier.checkPayments(handler, 2, paymentHelper.creditCartAuthPayment(PaymentHelper.AMOUNT_20), paymentHelper
//				.creditCartCapturePayment(PaymentHelper.AMOUNT_20)));
//
//		assertTrue(verifier.checkPayments(handler, 3, paymentHelper.creditCartAuthReversedPayment(PaymentHelper.AMOUNT_20), paymentHelper
//				.creditCartReversePayment(PaymentHelper.AMOUNT_20), paymentHelper.creditCartAuthPayment(PaymentHelper.AMOUNT_40), paymentHelper
//				.creditCartCapturePayment(PaymentHelper.AMOUNT_30)));
//
//		handler.clear();
//	}
}
