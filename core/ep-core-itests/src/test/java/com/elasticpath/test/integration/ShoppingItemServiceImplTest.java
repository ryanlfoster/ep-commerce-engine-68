package com.elasticpath.test.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import com.elasticpath.domain.misc.impl.MoneyFactory;
import org.junit.Assert;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.impl.PriceImpl;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.persistence.api.LoadTuner;
import com.elasticpath.sellingchannel.ShoppingItemFactory;
import com.elasticpath.service.shoppingcart.ShoppingItemService;
import com.elasticpath.test.db.DbTestCase;
import com.elasticpath.test.persister.CatalogTestPersister;

/** . */
public class ShoppingItemServiceImplTest extends DbTestCase {

	@Autowired
	private ShoppingItemService service;

	@Autowired
	private ShoppingItemFactory cartItemFactory;

	@DirtiesDatabase
	@Test
	public void testLoadTunerForChildItems() {
		ShoppingItem item = persistNestedItem();

		List<ShoppingItem> childItems = new ArrayList<ShoppingItem>(
				item.getChildren());
		assertEquals(1, childItems.size());
		ShoppingItem child = childItems.get(0);
		assertNotNull(child.getPrice());
		assertEquals(2, child.getChildren().size());

	}
	
	@DirtiesDatabase
	@Test
	public void testFindByGuid() {
		ShoppingItem item = service.saveOrUpdate(createItem());
		LoadTuner loadTuner = getBeanFactory().getBean("SHOPPING_ITEM_LOAD_TUNER_DEFAULT");
		ShoppingItem foundItem = service.findByGuid(item.getGuid(), loadTuner);
		
		Assert.assertEquals(foundItem, item);
	}

	private ShoppingItem persistNestedItem() {
		ShoppingItem child1 = createItem();

		ShoppingItem child11 = createItem();
		ShoppingItem child12 = createItem();

		child1.addChildItem(child11);
		child1.addChildItem(child12);

		ShoppingItem top = createItem();
		top.addChildItem(child1);

		return service.saveOrUpdate(top);
	}

	private ShoppingItem createItem() {
		CatalogTestPersister catalogTestPersister = persisterFactory
				.getCatalogTestPersister();
		Product product = catalogTestPersister
				.createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
						scenario.getCategory(), scenario.getWarehouse());

		Currency currency = Currency.getInstance(Locale.CANADA);
		PriceImpl price = new PriceImpl();
		price.setCurrency(currency);
		price.setListPrice(MoneyFactory.createMoney(BigDecimal.ONE, currency));

		ShoppingItem cartItem = cartItemFactory.createShoppingItem(product
				.getDefaultSku(), price, 1, 0, Collections.<String, String>emptyMap());
		
		cartItem.setPrice(1, price);
		return cartItem;
	}

}
