/**
 * Copyright (c) Elastic Path Software Inc., 2012
 */
package com.elasticpath.test.integration.cart;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.BundleConstituent;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.impl.BundleConstituentImpl;
import com.elasticpath.domain.catalog.impl.PriceImpl;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.domain.misc.impl.MoneyFactory;
import com.elasticpath.domain.shipping.ShipmentType;
import com.elasticpath.domain.shipping.evaluator.impl.ShoppingCartShipmentTypeEvaluator;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.impl.ShoppingCartImpl;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.sellingchannel.ShoppingItemFactory;
import com.elasticpath.sellingchannel.director.ShoppingItemAssembler;
import com.elasticpath.service.catalog.ProductService;
import com.elasticpath.test.integration.BasicSpringContextTest;
import com.elasticpath.test.integration.DirtiesDatabase;
import com.elasticpath.test.persister.CatalogTestPersister;
import com.elasticpath.test.persister.TaxTestPersister;
import com.elasticpath.test.persister.TestDataPersisterFactory;
import com.elasticpath.test.persister.testscenarios.ShoppingCartSimpleStoreScenario;

/**
 * Test the {@link ShoppingCartShipmentTypeEvaluator} usage with shopping items in the shopping cart.
 */
public class ShoppingCartShipmentTypeEvaluatorTest extends BasicSpringContextTest {

	private ShoppingCartSimpleStoreScenario scenario;

	private ShoppingCart shoppingCart;

	private Product physicalProduct;

	private Product electronicProduct;

	private ShoppingItem physicalShoppingItem;

	private ShoppingItem electronicShoppingItem;

	@Autowired
	private ShoppingItemFactory shoppingItemFactory;

	/**
	 * Get a reference to TestApplicationContext for use within the test. Setup scenarios.
	 *
	 * @throws Exception on error.
	 */
	@Before
	public void setUp() throws Exception {
		scenario = getTac().useScenario(ShoppingCartSimpleStoreScenario.class);

		shoppingCart = createShoppingCart();

		physicalProduct = scenario.getShippableProducts().get(0);
		electronicProduct = scenario.getNonShippableProducts().get(0);

		physicalShoppingItem = shoppingItemFactory.createShoppingItem(physicalProduct.getDefaultSku(), createPrice(), 1, 1, null);
		electronicShoppingItem = shoppingItemFactory.createShoppingItem(electronicProduct.getDefaultSku(), createPrice(), 1, 1, null);
	}

	@DirtiesDatabase
	@Test
	public void testEmptyShoppingCartHasNoShipmentTypes() {
		Set<ShipmentType> shipmentTypes = shoppingCart.getShipmentTypes();
		assertTrue("No shipment types should be returned from an empty cart.", shipmentTypes.isEmpty());
	}

	@DirtiesDatabase
	@Test
	public void testSinglePhysicalShipmentTypeInCart() {
		shoppingCart.addShoppingCartItem(physicalShoppingItem);
		Set<ShipmentType> expectedShipmentTypes = createShipmentTypes(ShipmentType.PHYSICAL);
		Set<ShipmentType> shipmentTypes = shoppingCart.getShipmentTypes();
		assertShipmentTypesEquals(expectedShipmentTypes, shipmentTypes);
	}

	@DirtiesDatabase
	@Test
	public void testSingleElectronicShipmentTypeInCart() {
		shoppingCart.addShoppingCartItem(electronicShoppingItem);
		Set<ShipmentType> expectedShipmentTypes = createShipmentTypes(ShipmentType.ELECTRONIC);
		Set<ShipmentType> shipmentTypes = shoppingCart.getShipmentTypes();
		assertShipmentTypesEquals(expectedShipmentTypes, shipmentTypes);
	}

	@DirtiesDatabase
	@Test
	public void testCombinationOfShipmentTypesInCart() {
		shoppingCart.addShoppingCartItem(physicalShoppingItem);
		shoppingCart.addShoppingCartItem(electronicShoppingItem);
		Set<ShipmentType> expectedShipmentTypes = createShipmentTypes(ShipmentType.PHYSICAL, ShipmentType.ELECTRONIC);
		Set<ShipmentType> shipmentTypes = shoppingCart.getShipmentTypes();
		assertShipmentTypesEquals(expectedShipmentTypes, shipmentTypes);
	}

	@DirtiesDatabase
	@Test
	public void testShipmentTypesWithSimpleBundleInCart() {
		Product simpleBundle = persistSimpleBundle(electronicProduct);
		ShoppingItem simpleBundleShoppingItem = createBundleShoppingItem(simpleBundle);

		shoppingCart.addShoppingCartItem(simpleBundleShoppingItem);

		Set<ShipmentType> expectedShipmentTypes = createShipmentTypes(ShipmentType.ELECTRONIC);
		Set<ShipmentType> shipmentTypes = shoppingCart.getShipmentTypes();
		assertShipmentTypesEquals(expectedShipmentTypes, shipmentTypes);
	}

	@DirtiesDatabase
	@Test
	public void testShipmentTypesWithMultipleConstituentBundleInCart() {
		Product simpleBundle = persistSimpleBundle(physicalProduct, electronicProduct);
		ShoppingItem multipleConstituentBundleShoppingItem = createBundleShoppingItem(simpleBundle);

		shoppingCart.addShoppingCartItem(multipleConstituentBundleShoppingItem);

		Set<ShipmentType> expectedShipmentTypes = createShipmentTypes(ShipmentType.PHYSICAL, ShipmentType.ELECTRONIC);
		Set<ShipmentType> shipmentTypes = shoppingCart.getShipmentTypes();
		assertShipmentTypesEquals(expectedShipmentTypes, shipmentTypes);
	}

	private void assertShipmentTypesEquals(final Set<ShipmentType> expected, final Set<ShipmentType> actual) {
		assertEquals("The number of shipment types should be equal.", expected.size(), actual.size());
		assertTrue(String.format("The expected shipment types: %s should equal the actual shipment types %s.",
				expected.toString(),
				actual.toString()),
				actual.containsAll(expected));
	}

	private Product persistSimpleBundle(final Product... products) {
		TaxTestPersister taxTestPersister = getTac().getPersistersFactory().getTaxTestPersister();
		final TaxCode taxCode = taxTestPersister.getTaxCode(TaxTestPersister.TAX_CODE_GOODS);
		CatalogTestPersister catalogTestPersister = getTac().getPersistersFactory().getCatalogTestPersister();
		ProductBundle productBundle = catalogTestPersister.createSimpleProductBundle("simpleBundleProductTypeName",
				"simpleBundleCode",
				scenario.getCatalog(),
				scenario.getCategory(),
				taxCode);

		for (Product product : products) {
			BundleConstituent bundleConstituent = catalogTestPersister.createSimpleBundleConstituent(product, 1);
			productBundle.addConstituent(bundleConstituent);
		}

		productBundle.setCalculated(true);
		final ProductSku sku = new ProductSkuImpl();
		sku.setStartDate(new Date());
		sku.setSkuCode("bundleskucode");
		sku.initialize();
		productBundle.addOrUpdateSku(sku);

		ProductService productService = getBeanFactory().getBean(ContextIdNames.PRODUCT_SERVICE);

		productBundle = (ProductBundle) productService.saveOrUpdate(productBundle);
		for (BundleConstituent constituent : productBundle.getConstituents()) {
			((BundleConstituentImpl) constituent).setConstituentItem();
		}

		return productBundle;
	}

	private Set<ShipmentType> createShipmentTypes(final ShipmentType... types) {
		Set<ShipmentType> result = new HashSet<ShipmentType>(types.length);
		for (ShipmentType type : types) {
			result.add(type);
		}
		return result;
	}

	private Price createPrice() {
		Currency currency = TestDataPersisterFactory.DEFAULT_CURRENCY;
		PriceImpl price = getBeanFactory().getBean(ContextIdNames.PRICE);
		price.setCurrency(currency);
		price.setListPrice(MoneyFactory.createMoney(BigDecimal.ONE, currency));

		return price;
	}

	private ShoppingCart createShoppingCart() {
		ShoppingCart shoppingCart = new ShoppingCartImpl() {
			private static final long serialVersionUID = 1L;

			@Override
			public void fireRules() {
				// Do nothing, we don't want drools involved.
			}
		};
		return shoppingCart;
	}

	/**
	 * CreateShoppingItem only creates shopping items at a root level and does not drill into creating constituents for a bundle.<br>
	 * This method performs the bundle child shopping items for the shopping item as well.
	 */
	private ShoppingItem createBundleShoppingItem(final Product productBundle) {
		ShoppingItem bundleShoppingItem = shoppingItemFactory.createShoppingItem(productBundle.getDefaultSku(), null, 1, 1, null);

		// Derive a ShoppingItemDTO, select all the constituents and convert back to a ShoppingItem.
		final ShoppingItemAssembler shoppingItemAssembler = getBeanFactory().getBean("shoppingItemAssembler");
		final ShoppingItemDto dto = shoppingItemAssembler.assembleShoppingItemDtoFrom(bundleShoppingItem);
		dto.setSelected(true);
		for (final ShoppingItemDto child : dto.getConstituents()) {
			child.setSelected(true);
		}

		bundleShoppingItem = shoppingItemAssembler.createShoppingItem(dto);

		return bundleShoppingItem;
	}

}
