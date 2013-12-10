/*
 * Copyright (c) Elastic Path Software Inc., 2006
 *
 */
package com.elasticpath.test.jmock;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Currency;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import com.elasticpath.domain.misc.impl.MoneyFactory;
import org.jmock.Expectations;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.catalog.AvailabilityCriteria;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.DefaultValueRemovalForbiddenException;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.PriceTier;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.catalog.impl.CatalogImpl;
import com.elasticpath.domain.catalog.impl.CategoryImpl;
import com.elasticpath.domain.catalog.impl.PriceImpl;
import com.elasticpath.domain.catalog.impl.PriceTierImpl;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.domain.catalog.impl.ProductTypeImpl;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.customer.impl.CustomerAddressImpl;
import com.elasticpath.domain.customer.impl.CustomerImpl;
import com.elasticpath.domain.misc.Money;
import com.elasticpath.domain.misc.impl.RandomGuidImpl;
import com.elasticpath.domain.shipping.ShippingCostCalculationMethod;
import com.elasticpath.domain.shipping.ShippingCostCalculationParameter;
import com.elasticpath.domain.shipping.ShippingCostCalculationParametersEnum;
import com.elasticpath.domain.shipping.ShippingRegion;
import com.elasticpath.domain.shipping.ShippingServiceLevel;
import com.elasticpath.domain.shipping.impl.FixedBaseAndOrderTotalPercentageMethodImpl;
import com.elasticpath.domain.shipping.impl.ShippingCostCalculationParameterImpl;
import com.elasticpath.domain.shipping.impl.ShippingRegionImpl;
import com.elasticpath.domain.shipping.impl.ShippingServiceLevelImpl;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.impl.ShoppingCartImpl;
import com.elasticpath.domain.shoppingcart.impl.ShoppingItemImpl;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.impl.StoreImpl;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.domain.tax.impl.TaxCodeImpl;
import com.elasticpath.service.tax.TaxCalculationResult;
import com.elasticpath.service.tax.TaxCalculationService;
import com.elasticpath.service.tax.impl.TaxCalculationResultImpl;
import com.elasticpath.test.factory.TestCustomerSessionFactory;
import com.elasticpath.test.factory.TestShopperFactory;
import com.elasticpath.test.factory.TestShoppingCartFactory;

/**
 * This class has the ability to create several interlinked catalog-related mock objects 
 * used by several test cases, this class should not be over-used as it previously became
 * a dumping ground, instead please write more specific test cases that mock specifically
 * what the test really needs.
 */
@SuppressWarnings({ "PMD.ExcessiveImports" })
public abstract class AbstractCatalogDataTestCase extends AbstractEPServiceTestCase {

	private Catalog masterCatalog;
	
	private Store store;

	/**
	 * Returns the default mocked store.
	 * 
	 * @return the default mocked store.
	 */
	protected Store getMockedStore() {
		if (store == null) {
			store = new StoreImpl();
			store.setCatalog(this.getCatalog());
			store.setCode("SAMPLE_STORECODE");
			store.setDefaultLocale(Locale.US);
			store.setDefaultCurrency(Currency.getInstance(Locale.US));
			store.setContentEncoding("UTF-8");
		}
		return store;
	}
	
	/**
	 * Returns a new <code>ProductSku</code> instance.
	 * 
	 * @return a new <code>ProductSku</code> instance.
	 */
	protected ProductSku getProductSku() {
		ProductSku productSku = new ProductSkuImpl();
		String skuCode = new RandomGuidImpl().toString();
		productSku.setGuid((new RandomGuidImpl()).toString());
		productSku.setSkuCode(skuCode);
		productSku.initialize();
		productSku.setProduct(this.getProduct());
		return productSku;
	}

	
	/**
	 * Returns a new <code>Price</code> instance.
	 * 
	 * @return a new <code>Price</code> instance.
	 */
	private Price getPrice() {
		// PriceImpl price = new PriceImpl();
		PriceImpl price = new PriceImpl();
		price.addOrUpdatePriceTier(getPriceTier());
		price.setCurrency(Currency.getInstance(Locale.CANADA));
		price.initialize();
		return price;
	}

	/**
	 * Returns a new <code>PriceTier</code> instance.
	 * 
	 * @return a new <code>PriceTier</code> instance.
	 */
	private PriceTier getPriceTier() {
		PriceTier priceTier = new PriceTierImpl();
		priceTier.initialize();
		return priceTier;
	}


	/**
	 * Returns a new <code>Category</code> instance.
	 * 
	 * @return a new <code>Category</code> instance.
	 */
	protected Category getCategory() {
		final Category category = new CategoryImpl();
		category.initialize();
		category.setCode((new RandomGuidImpl()).toString());
		category.setCatalog(getCatalog());

		return category;
	}

	private int productTypeMockCounter = 0;
	/**
	 * Returns a new <code>Product</code> instance.
	 * 
	 * @return a new <code>Product</code> instance.
	 */
	protected Product getProduct() {
		final Product product = newProductImpl();
		product.initialize();
		product.setCode((new RandomGuidImpl()).toString());
		final ProductType mock = context.mock(ProductType.class, "productType " + productTypeMockCounter++);
		final TaxCodeImpl taxCodeImpl = new TaxCodeImpl();
		taxCodeImpl.setCode(SALES_TAX_CODE_BOOKS);
		taxCodeImpl.setGuid(SALES_TAX_CODE_BOOKS);
		context.checking(new Expectations() {
			{
				allowing(mock).getTaxCode();
				will(returnValue(taxCodeImpl));

				allowing(mock).isExcludedFromDiscount();
				will(returnValue(Boolean.FALSE.booleanValue()));
			}
		});
		ProductType productType = mock;
		product.setProductType(productType);
		
		Category category = getCategory();
		product.addCategory(category);
		product.setCategoryAsDefault(category);
		
		return product;
	}

	/**
	 * Override to change behaviour of product.
	 * @return new instance of producyImpl
	 */
	protected Product newProductImpl() {
		return new ProductImpl();
	}

	/** GUID of the sku of cart item 1. */
	protected static final String SKU_GUID_1 = "Sku1";

	/** GUID of the sku of cart item 2. */
	private static final String SKU_GUID_2 = "Sku2";

	/** UID of the first product. */
	protected static final long PRODUCT_UID_1 = 123;

	/** UID of the second product. */
	private static final long PRODUCT_UID_2 = 456;

	/** Sales tax code associted with the product type of cart item 1. */
	protected static final String SALES_TAX_CODE_BOOKS = "BOOKS";

	/** Sales tax code associted with the product type of cart item 2. */
	protected static final String SALES_TAX_CODE_DVDS = "DVDS";

	private static final String CART_CURRENCY = "CAD";

	private static final String CARTITEM_PRICE_5 = "5";

	private static final String CARTITEM_PRICE_10 = "10";

	private static final int CARTITEM_QTY_3 = 3;

	private static final int CARTITEM_QTY_5 = 5;

	private int taxCalculationMockCounter = 0;
	/**
	 * Returns a standard shoppingCart, contains a customer, shipping/billing addresses, two
	 * items, a selected shipping service level and etc.
	 * 
	 * @return a standard shoppingCart.
	 */
	@SuppressWarnings("unchecked")
	protected ShoppingCart getShoppingCart() {
		final ShoppingCart shoppingCart = createNewShoppingCart();
		
		final TaxCalculationService mockTaxCalculationService =
				context.mock(TaxCalculationService.class, "tax calculation service " + taxCalculationMockCounter++);
		final TaxCalculationResultImpl taxCalculationResult = new TaxCalculationResultImpl() {
			private static final long serialVersionUID = 8044527486459666664L;

			@Override
			public void applyTaxes(final Collection< ? extends ShoppingItem> lineItems) { //NOPMD
			}
		};
		taxCalculationResult.setDefaultCurrency(Currency.getInstance("CAD"));
		context.checking(new Expectations() {
			{
				allowing(mockTaxCalculationService).calculateTaxes(
						with(any(String.class)),
						with(any(Address.class)),
						with(any(Currency.class)),
						with(any(Money.class)),
						with(any(Collection.class)),
						with(any(Money.class)));
				will(returnValue(taxCalculationResult));

				allowing(mockTaxCalculationService).calculateTaxesAndAddToResult(
						with(any(TaxCalculationResult.class)),
						with(any(String.class)),
						with(any(Address.class)),
						with(any(Currency.class)),
						with(any(Money.class)),
						with(any((Collection.class))),
						with(any(Money.class)));
				will(returnValue(taxCalculationResult));
			}
		});
		
		stubGetBean(ContextIdNames.TAX_CALCULATION_SERVICE, mockTaxCalculationService);
		((ShoppingCartImpl) shoppingCart).setTaxCalculationService(mockTaxCalculationService);

		// Set the cart items with a cart subtotal $65.00.
		this.addCartItemsTo(shoppingCart);

		// With the select shipping method and the above cart subtotal, the shipping cost will be
		// $11.50
		final long selectedSSlUid = 1000;
		List<ShippingServiceLevel> shippingServiceLevelList = new ArrayList<ShippingServiceLevel>();
		shippingServiceLevelList.add(getShippingServiceLevel(selectedSSlUid));
		shoppingCart.setShippingServiceLevelList(shippingServiceLevelList);
		shoppingCart.setSelectedShippingServiceLevelUid(selectedSSlUid);

		return shoppingCart;
	}

	private ShoppingCart createNewShoppingCart() {
		final Shopper shopper = TestShopperFactory.getInstance().createNewShopperWithMemento();
		final CustomerSession customerSession = createCustomerSessionForShopper(shopper);

		final ShoppingCart shoppingCart = TestShoppingCartFactory.getInstance().createNewCartWithMemento(
				customerSession.getShopper(), getMockedStore());
		shoppingCart.setCustomerSession(customerSession);
		shoppingCart.setBillingAddress(getAddress());
		shoppingCart.setShippingAddress(getAddress());
		shoppingCart.setCurrency(Currency.getInstance(CART_CURRENCY));
		return shoppingCart;
	}

	private CustomerSession createCustomerSessionForShopper(final Shopper shopper) {
		final CustomerSession customerSession = TestCustomerSessionFactory.getInstance().createNewCustomerSessionWithContext(shopper);
		final Customer customer = createCustomer();
		customerSession.getShopper().setCustomer(customer);
		return customerSession;
	}

	private Customer createCustomer() {
		return new CustomerImpl() {
			private static final long serialVersionUID = 5085646833754066257L;

			@Override
			public String getEmail() {
				return "Joe.Doe@elasticpath.com";
			}
		};
	}

	/**
	 * Get two cart items - qty 3 of $5.00 each, and qty 5 of $10.00 each.
	 * 
	 * @return
	 */
	private List<ShoppingItem> addCartItemsTo(final ShoppingCart shoppingCart) {

		List<ShoppingItem> cartItems = new ArrayList<ShoppingItem>();
		ShoppingItem cartItem1 = new ShoppingItemImpl();
		cartItem1.setUidPk(Calendar.getInstance().getTimeInMillis());
		ProductSku productSkuImpl1 = getProductSku();
		productSkuImpl1.setUidPk(Calendar.getInstance().getTimeInMillis());
		productSkuImpl1.setSkuCode(SKU_GUID_1);
		productSkuImpl1.initialize();
		productSkuImpl1.setWeight(BigDecimal.ONE);

		// create product 1
		Product productImpl1 = getProduct();
		productImpl1.setUidPk(PRODUCT_UID_1);
		productImpl1.setProductSkus(new HashMap<String, ProductSku>());
		ProductTypeImpl productTypeImpl1 = new ProductTypeImpl();
		TaxCode taxCode1 = new TaxCodeImpl();
		taxCode1.setCode(SALES_TAX_CODE_BOOKS);
		taxCode1.setGuid(SALES_TAX_CODE_BOOKS);
		productTypeImpl1.setTaxCode(taxCode1);
		productImpl1.setProductType(productTypeImpl1);
		productImpl1.setAvailabilityCriteria(AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK);
		productSkuImpl1.setProduct(productImpl1);
		productImpl1.addOrUpdateSku(productSkuImpl1);
		
		final Money price1 = MoneyFactory.createMoney(new BigDecimal(CARTITEM_PRICE_5), Currency.getInstance(CART_CURRENCY));
		Price productSkuPrice = getPrice();
		productSkuPrice.setCurrency(Currency.getInstance(CART_CURRENCY));
		productSkuPrice.setListPrice(price1);
		productSkuPrice.setSalePrice(price1);
		cartItem1.setProductSku(productSkuImpl1);
		cartItem1.setPrice(CARTITEM_QTY_3, productSkuPrice);
		
		shoppingCart.addCartItem(cartItem1);
		cartItems.add(cartItem1);
		
		ShoppingItem cartItem2 = new ShoppingItemImpl();
		cartItem2.setUidPk(Calendar.getInstance().getTimeInMillis() / 2);
		ProductSku productSkuImpl2 = getProductSku();
		productSkuImpl2.setUidPk(Calendar.getInstance().getTimeInMillis());
		productSkuImpl2.setSkuCode(SKU_GUID_2);
		productSkuImpl2.initialize();
		// create product 2
		Product productImpl2 = getProduct();
		productImpl2.setUidPk(PRODUCT_UID_2);
		productImpl2.setProductSkus(new HashMap<String, ProductSku>());
		productImpl2.setAvailabilityCriteria(AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK);
		ProductTypeImpl productTypeImpl2 = new ProductTypeImpl();
		TaxCode taxCode2 = new TaxCodeImpl();
		taxCode2.setCode(SALES_TAX_CODE_DVDS);
		taxCode2.setGuid(SALES_TAX_CODE_DVDS);
		productTypeImpl2.setTaxCode(taxCode2);
		productImpl2.setProductType(productTypeImpl2);
		productSkuImpl2.setProduct(productImpl2);
		productImpl2.addOrUpdateSku(productSkuImpl2);

		final Money price2 = MoneyFactory.createMoney(new BigDecimal(CARTITEM_PRICE_10), Currency.getInstance(CART_CURRENCY));
		productSkuPrice = getPrice();
		productSkuPrice.setCurrency(Currency.getInstance(CART_CURRENCY));
		productSkuPrice.setListPrice(price2);
		productSkuPrice.setSalePrice(price2);
		
		cartItem2.setProductSku(productSkuImpl2);
		cartItem2.setPrice(CARTITEM_QTY_5, productSkuPrice);
		
		shoppingCart.addCartItem(cartItem2);
		cartItems.add(cartItem2);
		
		return cartItems;
	}

	/**
	 * Returns a newly created address.
	 * 
	 * @return a newly created address
	 */
	private Address getAddress() {
		Address address = new CustomerAddressImpl();
		address.setFirstName("Joe");
		address.setLastName("Doe");
		address.setCountry("US");
		address.setStreet1("1295 Charleston Road");
		address.setCity("Mountain View");
		address.setSubCountry("CA");
		address.setZipOrPostalCode("94043");

		return address;
	}

	/**
	 * Returns a newly created shippingRegion.
	 * 
	 * @param regionStr - the region str, i.e. "[CA(AB,BC)][US()]".
	 * @return a newly created shippingRegion
	 */
	private ShippingRegion getShippingRegion(final String regionStr) {
		ShippingRegionImpl shippingRegionImpl = new ShippingRegionImpl();
		shippingRegionImpl.setRegionStr(regionStr);
		return shippingRegionImpl;
	}

	/**
	 * Returns a newly created shippingServiceLevel of type
	 * fixedBaseAndOrderTotalPercentageMethod, with base value 5.0 and 10% of the order total and
	 * supports shippingRegion CA(AB, BC) and US.
	 * 
	 * @param uidPk - the uidPk for the newly created shippingServiceLevel.
	 * @return a newly created shippingServiceLevel
	 */
	protected ShippingServiceLevel getShippingServiceLevel(final long uidPk) {
		ShippingServiceLevel shippingServiceLevel = new ShippingServiceLevelImpl();
		shippingServiceLevel.setUidPk(uidPk);
		shippingServiceLevel.setCarrier("Fed Ex");

		ShippingCostCalculationMethod fixedBaseOrderTotPertMeth = new FixedBaseAndOrderTotalPercentageMethodImpl();
		ShippingCostCalculationParameter param1 = new ShippingCostCalculationParameterImpl();
		Set<ShippingCostCalculationParameter> paramSet = new HashSet<ShippingCostCalculationParameter>();
		param1.setKey(ShippingCostCalculationParametersEnum.FIXED_BASE.getKey());
		param1.setValue("5.0");
		param1.setCurrency(Currency.getInstance(CART_CURRENCY));
		paramSet.add(param1);
		ShippingCostCalculationParameter param2 = new ShippingCostCalculationParameterImpl();
		param2.setKey(ShippingCostCalculationParametersEnum.PERCENTAGE_OF_ORDER_TOTOAL.getKey());
		param2.setValue("10");
		param2.setCurrency(Currency.getInstance(CART_CURRENCY));
		paramSet.add(param2);
		fixedBaseOrderTotPertMeth.setParameters(paramSet);

		shippingServiceLevel.setShippingCostCalculationMethod(fixedBaseOrderTotPertMeth);
		shippingServiceLevel.setShippingRegion(getShippingRegion("[CA(AB,BC)][US()]"));

		return shippingServiceLevel;
	}

	/**
	 * Gets the master catalog singleton.
	 *
	 * @return the master catalog singleton
	 */
	protected Catalog getCatalog() {
		if (masterCatalog == null) {
			masterCatalog = new CatalogImpl();
			masterCatalog.setMaster(true);
			masterCatalog.setCode("a master catalog code that no one would ever think of");
		}
		
		try {
			// Supported Currencies
			final Set<Currency> supportedCatalogCurrencies = new HashSet<Currency>();
			supportedCatalogCurrencies.add(Currency.getInstance(Locale.CANADA));
			supportedCatalogCurrencies.add(Currency.getInstance(Locale.US));
				
			// Supported Locales
			final Set<Locale> supportedLocales = new HashSet<Locale>();
			supportedLocales.add(Locale.CANADA);
			supportedLocales.add(Locale.CANADA_FRENCH); // en_fr
			supportedLocales.add(Locale.US); //us
			masterCatalog.setSupportedLocales(supportedLocales);
			masterCatalog.setDefaultLocale(Locale.CANADA);
		} catch (DefaultValueRemovalForbiddenException ex) {
			throw new EpDomainException("Default locale not set, so this shouldn't happen", ex);
		}
		
		return masterCatalog;
	}
	
}
