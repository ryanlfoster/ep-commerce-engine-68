package com.elasticpath.test.persister;

import com.elasticpath.commons.beanframework.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Currency;
import java.util.Locale;

/**
 * Test Data Persister Factory. Initialized by TestApplicationContext, collects a set of more specific persisters.
 */
public class TestDataPersisterFactory {

	public static final Locale DEFAULT_LOCALE = Locale.US;

	public static final Currency DEFAULT_CURRENCY = Currency.getInstance(DEFAULT_LOCALE);

	public static final String DEFAULT_CURRENCY_CODE = DEFAULT_CURRENCY.getCurrencyCode();

	public static final String DEFAULT_CATALOG_NAME = "Default Catalog Name";

	@Autowired
	@Qualifier("catalogTestPersister")
	private CatalogTestPersister catalogTestPersister;
	
	@Autowired
	@Qualifier("taxTestPersister")
	private TaxTestPersister taxTestPersister;
	
	private StoreTestPersister storeTestPersister;
	private PromotionTestPersister promotionTestPersister;
	private OrderTestPersister orderTestPersister;
	private SettingsTestPersister settingsTestPersister;
	private ImportTestPersister importTestPersister;
	private DynamicContentDeliveryTestPersister dynamicContentDeliveryTestPersister;
	private PriceListAssignmentPersister priceListAssignmentPersister;
	private ConditionalExpressionPersister conditionalExpressionPersister;
	private SellingContextTestPersister sellingContextTestPersister;
	private CouponTestPersister couponTestPersister;
	private PriceListPersister priceListPersister;
	private ShippingRegionTestPersister shippingRegionTestPersister;
	private ShippingServiceLevelTestPersister shippingServiceLevelTestPersister;
	private GiftCertificateTestPersister giftCertificateTestPersister;
	private CmUserTestPersister cmUserTestPersister;
	private CmImportJobTestPersister cmImportJobTestPersister;
	private PaymentGatewayTestPersister paymentGatewayTestPersister;
	
	@Autowired
	private BeanFactory beanFactory;

	/**
	 * return the catalogTestPersister.
	 * @return catalogTestPersister the catalogTestPersister
	 */
	public CatalogTestPersister getCatalogTestPersister() {
		return catalogTestPersister;
	}
	
	/**
	 * return the settingsTestPersister.
	 * @return settingsTestPersister the settingsTestPersister
	 */
	public SettingsTestPersister getSettingsTestPersister() {
		if (settingsTestPersister == null) {
			settingsTestPersister = new SettingsTestPersister(beanFactory);
		}
		return settingsTestPersister;
	}

	/**
	 * return the taxTestPersister.
	 * @return taxTestPersister the taxTestPersister
	 */
	public TaxTestPersister getTaxTestPersister() {
		return taxTestPersister;
	}

	/**
	 * return the storeTestPersister.
	 * @return storeTestPersister the storeTestPersister
	 */
	public StoreTestPersister getStoreTestPersister() {
		if (storeTestPersister == null) {
			storeTestPersister = new StoreTestPersister(beanFactory);
		}
		return storeTestPersister;
	}

	/**
	 * return the sellingContextTestPersister.
	 * @return sellingContextTestPersister the sellingContextTestPersister
	 */
	public SellingContextTestPersister getSellingContextTestPersister() {
		if (sellingContextTestPersister == null) {
			sellingContextTestPersister = new SellingContextTestPersister(beanFactory);
		}
		return sellingContextTestPersister;
	}

	/**
	 * return the promotionTestPersister.
	 * @return promotionTestPersister the promotionTestPersister
	 */
	public PromotionTestPersister getPromotionTestPersister() {
		if (promotionTestPersister == null) {
			promotionTestPersister = new PromotionTestPersister(beanFactory);
		}
		return promotionTestPersister;
	}

	/**
	 * return the orderTestPersister.
	 * @return orderTestPersister the orderTestPersister
	 */
	public OrderTestPersister getOrderTestPersister() {
		if (orderTestPersister == null) {
			orderTestPersister = new OrderTestPersister(beanFactory, this);
		}
		return orderTestPersister;
	}

	/**
	 * return the importTestPersister.
	 * @return importTestPersister the importTestPersister
	 */
	public ImportTestPersister getImportTestPersister() {
		if (importTestPersister == null) {
			importTestPersister = new ImportTestPersister(beanFactory);
		}
		return importTestPersister;
	}

	/**
	 * return the dynamicContentDeliveryTestPersister.
	 * @return dynamicContentDeliveryTestPersister the dynamicContentDeliveryTestPersister
	 */
	public DynamicContentDeliveryTestPersister getDynamicContentDeliveryTestPersister() {
		if (dynamicContentDeliveryTestPersister == null) {
			dynamicContentDeliveryTestPersister = new DynamicContentDeliveryTestPersister(beanFactory);
		}
		return dynamicContentDeliveryTestPersister;
	}

	/**
	 * return the priceListAssignmentPersister.
	 * @return priceListAssignmentPersister the priceListAssignmentPersister
	 */
	public PriceListAssignmentPersister getPriceListAssignmentPersister() {
		if (priceListAssignmentPersister == null) {
			priceListAssignmentPersister = new PriceListAssignmentPersister(beanFactory);
		}
		return priceListAssignmentPersister;
	}

	/**
	 * return the conditionalExpressionPersister.
	 * @return conditionalExpressionPersister the conditionalExpressionPersister
	 */
	public ConditionalExpressionPersister getConditionalExpressionPersister() {
		if (conditionalExpressionPersister == null) {
			conditionalExpressionPersister = new ConditionalExpressionPersister(beanFactory);
		}
		return conditionalExpressionPersister;
	}

	/**
	 * return the couponTestPersister.
	 * @return couponTestPersister the couponTestPersister
	 */
	public CouponTestPersister getCouponTestPersister() {
		if (couponTestPersister == null) {
			couponTestPersister = new CouponTestPersister(beanFactory);
		}
		return couponTestPersister;
	}

	/**
	 * return the priceListPersister.
	 * @return priceListPersister the priceListPersister
	 */
	public PriceListPersister getPriceListPersister() {
		if (priceListPersister == null) {
			priceListPersister = new PriceListPersister(beanFactory);
		}
		return priceListPersister;
	}

	/**
	 * return the shippingRegionTestPersister.
	 * @return shippingRegionTestPersister the shippingRegionTestPersister
	 */
	public ShippingRegionTestPersister getShippingRegionTestPersister() {
		if (shippingRegionTestPersister == null) {
			shippingRegionTestPersister = new ShippingRegionTestPersister(beanFactory);
		}
		return shippingRegionTestPersister;
	}

	/**
	 * return the shippingServiceLevelTestPersister.
	 * @return shippingServiceLevelService the shippingServiceLevelTestPersister
	 */
	public ShippingServiceLevelTestPersister getShippingServiceLevelTestPersister() {
		if (shippingServiceLevelTestPersister == null) {
			shippingServiceLevelTestPersister = new ShippingServiceLevelTestPersister(beanFactory);
		}
		return shippingServiceLevelTestPersister;
	}

	/**
	 * return the giftCertificateTestPersister.
	 * @return giftCertificateTestPersister the giftCertificateTestPersister
	 */
	public GiftCertificateTestPersister getGiftCertificateTestPersister() {
		if (giftCertificateTestPersister == null) {
			giftCertificateTestPersister = new GiftCertificateTestPersister(beanFactory);
		}
		return giftCertificateTestPersister;
	}

	/**
	 * return the cmUserTestPersister.
	 * @return cmUserTestPersister the cmUserTestPersister
	 */
	public CmUserTestPersister getCmUserTestPersister() {
		if (cmUserTestPersister == null) {
			cmUserTestPersister = new CmUserTestPersister(beanFactory);
		}
		return cmUserTestPersister;
	}

	/**
	 * return the cmImportJobTestPersister.
	 * @return cmImportJobTestPersister the cmImportJobTestPersister
	 */
	public CmImportJobTestPersister getCmImportJobTestPersister() {
		if (cmImportJobTestPersister == null) {
			cmImportJobTestPersister = new CmImportJobTestPersister(beanFactory);
		}
		return cmImportJobTestPersister;
	}

	/**
	 * return a {@link PaymentGatewayTestPersister}.
	 * 
	 * @return the {@link PaymentGatewayTestPersister}
	 */
	public PaymentGatewayTestPersister getPaymentGatewayTestPersister() {
		if (paymentGatewayTestPersister == null) {
			paymentGatewayTestPersister = new PaymentGatewayTestPersister(beanFactory);
		}
		return paymentGatewayTestPersister;
	}
}
