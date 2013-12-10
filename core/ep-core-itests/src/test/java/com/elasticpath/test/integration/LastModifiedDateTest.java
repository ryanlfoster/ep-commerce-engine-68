package com.elasticpath.test.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;


import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.attribute.AttributeType;
import com.elasticpath.domain.attribute.impl.AttributeImpl;
import com.elasticpath.domain.attribute.impl.AttributeUsageImpl;
import com.elasticpath.domain.attribute.impl.CustomerProfileValueImpl;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.impl.CategoryImpl;
import com.elasticpath.domain.catalog.impl.PriceImpl;
import com.elasticpath.domain.event.EventOriginatorType;
import com.elasticpath.domain.misc.impl.MoneyFactory;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.impl.OrderEventImpl;
import com.elasticpath.domain.order.impl.OrderImpl;
import com.elasticpath.domain.order.impl.OrderPaymentImpl;
import com.elasticpath.domain.shoppingcart.impl.ShoppingItemImpl;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.persistence.api.PersistenceSession;
import com.elasticpath.persistence.api.Transaction;
import com.elasticpath.persistence.openjpa.PersistenceInterceptor;
import com.elasticpath.service.catalog.CategoryService;
import com.elasticpath.service.order.OrderService;
import com.elasticpath.test.persister.StoreTestPersister;
import com.elasticpath.test.persister.testscenarios.ShoppingCartSimpleStoreScenario;

/**
 * An integration test for domain object's last modified date field. We're testing if the field gets updated correctly by the database system. 
 */
// NOTE: Since we are testing that the EntityListeners are configured correctly,
// we need the application transaction boundaries to be obeyed.  Other tests
// create mock EntityListeners to simulate this, but we want a true integration
// test here.  Sorry for the slowness.
public class LastModifiedDateTest extends BasicSpringContextTest {

	private static final String CAD = "CAD";

	@Autowired
	private PersistenceEngine persistenceEngine;
	
	private PersistenceSession persistenceSession;
	
	private ShoppingCartSimpleStoreScenario scenario;

	/**
	 * Setup persistence engine for test cases.
	 *
	 * @throws Exception from TestCase
	 */
	@Before
	public void setUp() throws Exception {
		scenario = getTac().useScenario(ShoppingCartSimpleStoreScenario.class);
		persistenceSession = persistenceEngine.getSharedPersistenceSession();
	}
	
	/**
	 * Tests a CartItem's last modified date.
	 * @throws InterruptedException 
	 */
	@DirtiesDatabase
	@Test
	public void testCartItemLastModifiedDate() throws InterruptedException {
		Product product = scenario.getShippableProducts().get(0);
		ProductSku sku = product.getDefaultSku();
		
		ShoppingItemImpl shoppingCartItem = getBeanFactory().getBean(ContextIdNames.SHOPPING_ITEM);
		shoppingCartItem.setProductSku(sku);
		Price price = new PriceImpl();
		price.setCurrency(Currency.getInstance(CAD));
		price.setListPrice(MoneyFactory.createMoney(BigDecimal.ONE, Currency.getInstance(CAD)));
		price.setSalePrice(MoneyFactory.createMoney(BigDecimal.TEN, Currency.getInstance(CAD)));
		shoppingCartItem.setPrice(1, price);
		
		ShoppingItemImpl persistedShoppingCartItem = saveDomainModelObject(shoppingCartItem);

		final Date lastModifiedDate = persistedShoppingCartItem.getLastModifiedDate();
		shoppingCartItem.setQuantity(2);
		
		Thread.sleep(30); // make sure the time differs
		persistedShoppingCartItem = saveDomainModelObject(shoppingCartItem);
		assertFalse(lastModifiedDate.equals(persistedShoppingCartItem.getLastModifiedDate()));
		
	}

	/**
	 * Tests a CustomerProfileValue's last modified date.
	 */
	@DirtiesDatabase
	@Test
	public void testCustomerProfileValueLastModifiedDate() throws InterruptedException {
			
		AttributeImpl attribute = new AttributeImpl();
		attribute.setKey("TEST_ATTRIBUTE");
		attribute.setAttributeType(AttributeType.BOOLEAN);
		attribute.setName("TestName");
		attribute.setAttributeUsage(AttributeUsageImpl.CUSTOMERPROFILE_USAGE);
		attribute.setGlobal(false);
		
		AttributeImpl persistedAttribute = saveDomainModelObject(attribute);
		
		CustomerProfileValueImpl profileValue = new CustomerProfileValueImpl();
		profileValue.setAttribute(persistedAttribute);
		profileValue.setAttributeType(persistedAttribute.getAttributeType());
		profileValue.setLocalizedAttributeKey(persistedAttribute.getKey() + "_" + Locale.CANADA);
		
		CustomerProfileValueImpl persistedProfileValue = saveDomainModelObject(profileValue);
		
		final Date lastModifiedDateBeforeSave = persistedProfileValue.getLastModifiedDate();
		profileValue.setBooleanValue(true);
		
		final int millis = 30;
		Thread.sleep(millis); // make sure the time differs
		
		persistedProfileValue = saveDomainModelObject(profileValue);
		
		assertEquals("The date before save should be earlier than the last persistent date", 
				-1, lastModifiedDateBeforeSave.compareTo(persistedProfileValue.getLastModifiedDate()));
	}
	
	/**
	 * Tests an OrderPayment's last modified date.
	 * @throws InterruptedException 
	 */
	@DirtiesDatabase
	@Test
	public void testOrderPaymentLastModifiedDate() throws InterruptedException {
		
		OrderPaymentImpl orderPayment = getBeanFactory().getBean(ContextIdNames.ORDER_PAYMENT);
		orderPayment.getCardNumber();
		orderPayment.setCreatedDate(new Date());
		orderPayment.setAmount(BigDecimal.ZERO);
		
		OrderPaymentImpl persistedOrderPayment = saveDomainModelObject(orderPayment);
		
		final Date lastModifiedDateBeforeSave = persistedOrderPayment.getLastModifiedDate();
		orderPayment.setAmount(BigDecimal.ONE);
		Thread.sleep(1);
		
		persistedOrderPayment = saveDomainModelObject(orderPayment);
		
		assertEquals("The date before save should be earlier than the last persistent date", 
				-1, lastModifiedDateBeforeSave.compareTo(persistedOrderPayment.getLastModifiedDate()));
			
	}
	
	/**
	 * Tests an OrderEvent's last modified date.
	 * @throws InterruptedException 
	 */
	@DirtiesDatabase
	@Test
	public void testOrderEventLastModifiedDate() throws InterruptedException {
		
		OrderEventImpl orderEvent = getBeanFactory().getBean(ContextIdNames.ORDER_EVENT);
		orderEvent.setCreatedDate(new Date());
		orderEvent.setOriginatorType(EventOriginatorType.CMUSER);
		orderEvent.setTitle("TestTitle");
		
		OrderEventImpl persistedOrderEvent = saveDomainModelObject(orderEvent);
		
		final Date lastModifiedDateBeforeSave = persistedOrderEvent.getLastModifiedDate();
		orderEvent.setNote("TestNote");
		Thread.sleep(1);

		persistedOrderEvent = saveDomainModelObject(orderEvent);
		
		assertEquals("The date before save should be earlier than the last persistent date", 
				-1, lastModifiedDateBeforeSave.compareTo(persistedOrderEvent.getLastModifiedDate()));
	}
	
	/**
	 * Tests that the last modified date of Order gets updated on add/update.
	 * 
	 * @throws InterruptedException if error occurs
	 */
	@DirtiesDatabase
	@Test
	public void testOrderLastModifiedDate() throws InterruptedException {
		OrderService orderService = getBeanFactory().getBean(ContextIdNames.ORDER_SERVICE);

		Order order = new OrderImpl();
		order.setLocale(Locale.US);
		order.setCreatedDate(new Date());
		order.setStoreCode(StoreTestPersister.STORE_CODE);

		assertNull(order.getLastModifiedDate());
		
		orderService.add(order);
		
		order = orderService.get(order.getUidPk());
		
		Date lastModifiedDateAfterPersist = order.getLastModifiedDate();
		assertNotNull("The last modified date should not be null", lastModifiedDateAfterPersist);
		
		final int millis = 1000;
		Thread.sleep(millis);
		
		order.setLocale(Locale.CANADA);
		Order updatedOrder = orderService.update(order);
		
		order = orderService.get(order.getUidPk());
		
		assertNotNull(updatedOrder.getLastModifiedDate());
		
		assertEquals("The date after persist should be earlier than the last persistent date", 
				-1, lastModifiedDateAfterPersist.compareTo(updatedOrder.getLastModifiedDate()));
	}

	/**
	 * Tests that the last modified date of a category and its children gets updated on add/update.
	 * 
	 * @throws InterruptedException if error occurs
	 */
	@DirtiesDatabase
	@Test
	public void testCategoryLastModifiedDate() throws InterruptedException {
		CategoryService categoryService = getBeanFactory().getBean(ContextIdNames.CATEGORY_SERVICE);

		// create a category
		Category category = new CategoryImpl();
		category.setCatalog(scenario.getCatalog());
		category.setCategoryType(scenario.getCategory().getCategoryType());
		category.setCode("cat1");
		category.setStartDate(new Date());

		// create a child category
		Category childCategory = new CategoryImpl();
		childCategory.setCatalog(scenario.getCatalog());
		childCategory.setCategoryType(scenario.getCategory().getCategoryType());
		childCategory.setCode("sub-cat1");
		childCategory.setStartDate(new Date());
		
		category.addChild(childCategory);
		
		assertNull("The initial value of last modified date should be null", category.getLastModifiedDate());
		assertNull("The initial value of last modified date should be null", childCategory.getLastModifiedDate());
		
		// persist the category
		categoryService.add(category);
		// load it from the database
		category = categoryService.get(category.getUidPk());
		// verify the last modified date
		Date lastModifiedDateAfterPersist = category.getLastModifiedDate();
		assertNotNull("The last modified date should not be null", lastModifiedDateAfterPersist);
		assertEquals("Exactly one child is expected", 1, category.getChildren().size());
		Date childLastModifiedDateAfterPersist = category.getChildren().iterator().next().getLastModifiedDate();
		assertNotNull("The last modified date should not be null", childLastModifiedDateAfterPersist);
		
		// wait a sec
		final int timeout = 500;
		Thread.sleep(timeout);
		// change something on the category
		category.setHidden(true);
		
		// update the category
		Category updatedCategory = categoryService.update(category);
		// retrieve it from the database
		category = categoryService.get(category.getUidPk());
		// verify the date
		assertNotNull("The date should have been populated", updatedCategory.getLastModifiedDate());
		assertEquals("The date after update should be earlier than the last persistent date", 
				-1, lastModifiedDateAfterPersist.compareTo(updatedCategory.getLastModifiedDate()));
		assertEquals("The child was not changed so its date should be the same", 
				0, childLastModifiedDateAfterPersist.compareTo(updatedCategory.getChildren().iterator().next().getLastModifiedDate()));

		Date lastModifiedDateAfterUpdate = updatedCategory.getLastModifiedDate();
		Date childLastModifiedDateAfterUpdate = updatedCategory.getChildren().iterator().next().getLastModifiedDate();
		
		// change something on the category
		updatedCategory.setOrdering(2);
		// add the child category
		updatedCategory.getChildren().iterator().next().setHidden(true);
		
		Thread.sleep(timeout);
		
		updatedCategory = categoryService.saveOrUpdate(updatedCategory);
		// reload from the database
		updatedCategory = categoryService.get(category.getUidPk());
		// assert the date was modified
		assertEquals("The date after update should be earlier than the last update date", 
				-1, lastModifiedDateAfterUpdate.compareTo(updatedCategory.getLastModifiedDate()));
		assertEquals("The date after update of the child should be earlier than the last update date", 
				-1, childLastModifiedDateAfterUpdate.compareTo(updatedCategory.
						getChildren().iterator().next().getLastModifiedDate()));
		
		// Take note of the last modified date then update the display name
		lastModifiedDateAfterUpdate = updatedCategory.getLastModifiedDate();
		updatedCategory.setDisplayName("new Category Name", Locale.ENGLISH);

		Thread.sleep(timeout);
		
		updatedCategory = categoryService.saveOrUpdate(updatedCategory);
		// reload from the database
		updatedCategory = categoryService.get(category.getUidPk());
		// assert the date was modified
		assertEquals("The date from the previous update should be earlier than the last update date", 
				-1, lastModifiedDateAfterUpdate.compareTo(updatedCategory.getLastModifiedDate()));

		// Take note of the last modified date then update the display name
		lastModifiedDateAfterUpdate = updatedCategory.getLastModifiedDate();
		updatedCategory.setDisplayName("Changed Category Name", Locale.ENGLISH);

		Thread.sleep(timeout);
		
		updatedCategory = categoryService.saveOrUpdate(updatedCategory);
		// reload from the database
		updatedCategory = categoryService.get(category.getUidPk());
		// assert the date was modified
		assertEquals("The date from the previous update should be earlier than the last update date", 
				-1, lastModifiedDateAfterUpdate.compareTo(updatedCategory.getLastModifiedDate()));
}

	/**
	 * A transaction that saves a domain model object to database.
	 * 
	 * @param object the domain model object to persist.
	 * @return the persisted domain model object.
	 */
	private <T extends Persistable> T saveDomainModelObject(final T object) {
		Transaction transaction;
		
		if (object instanceof PersistenceInterceptor) {
			((PersistenceInterceptor) object).executeBeforePersistAction();
		}
		transaction = persistenceSession.beginTransaction();
		T persistedObj = persistenceEngine.saveOrUpdate(object);
		transaction.commit();
		
		return persistedObj;
	}
}
