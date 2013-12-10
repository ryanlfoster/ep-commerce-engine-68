/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.catalog.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.AvailabilityCriteria;
import com.elasticpath.domain.catalog.InsufficientInventoryException;
import com.elasticpath.domain.catalog.InventoryAudit;
import com.elasticpath.domain.catalog.InventoryEventType;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.impl.InventoryAuditImpl;
import com.elasticpath.domain.catalog.impl.PreOrBackOrderDetails;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.domain.misc.Money;
import com.elasticpath.domain.misc.impl.MoneyFactory;
import com.elasticpath.domain.misc.impl.StandardMoneyFormatter;
import com.elasticpath.domain.order.AllocationEventType;
import com.elasticpath.domain.order.AllocationResult;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.order.impl.AllocationResultImpl;
import com.elasticpath.domain.order.impl.OrderImpl;
import com.elasticpath.domain.order.impl.OrderSkuImpl;
import com.elasticpath.domain.order.impl.PhysicalOrderShipmentImpl;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.Warehouse;
import com.elasticpath.domain.store.impl.StoreImpl;
import com.elasticpath.domain.store.impl.WarehouseImpl;
import com.elasticpath.inventory.InventoryDto;
import com.elasticpath.inventory.InventoryKey;
import com.elasticpath.inventory.dao.InventoryDao;
import com.elasticpath.inventory.dao.InventoryJournalDao;
import com.elasticpath.inventory.domain.Inventory;
import com.elasticpath.inventory.domain.impl.InventoryImpl;
import com.elasticpath.inventory.domain.impl.InventoryJournalImpl;
import com.elasticpath.inventory.impl.InventoryDtoAssembler;
import com.elasticpath.inventory.impl.InventoryDtoImpl;
import com.elasticpath.inventory.impl.InventoryExecutionResultImpl;
import com.elasticpath.inventory.impl.InventoryFacadeImpl;
import com.elasticpath.inventory.log.impl.InventoryLogContext;
import com.elasticpath.inventory.log.impl.InventoryLogSupport;
import com.elasticpath.inventory.strategy.InventoryStrategy;
import com.elasticpath.inventory.strategy.impl.InventoryJournalRollupImpl;
import com.elasticpath.inventory.strategy.impl.JournalingInventoryStrategy;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.persistence.api.PersistenceSession;
import com.elasticpath.persistence.api.Query;
import com.elasticpath.service.catalog.ProductInventoryManagementService;
import com.elasticpath.service.catalog.ProductSkuService;
import com.elasticpath.service.order.impl.AllocationServiceImpl;
import com.elasticpath.service.search.IndexNotificationService;
import com.elasticpath.service.search.IndexType;
import com.elasticpath.service.store.StoreService;
import com.elasticpath.service.store.impl.WarehouseServiceImpl;
import com.elasticpath.service.tax.TaxCalculationResult;
import com.elasticpath.service.tax.impl.TaxCalculationResultImpl;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * Test suite for <code>InventoryServiceImpl</code>.
 */
@SuppressWarnings({ "PMD.ExcessiveClassLength", "PMD.ExcessiveImports", "PMD.TooManyFields" })
public class ProductInventoryManagementServiceImplTest {

	private static final String INVENTORY_JOURNAL_DAO2 = "inventoryJournalDao2";

	private static final String INVENTORY_DAO2 = "inventoryDao2";

	private static final String SKU_CODE = "1";

	private static final long WAREHOUSE_UID = 1L;

	private static final String INVENTORY_JOURNAL = "inventoryJournal";

	private static final int THREE = 3;

	private static final String ALLOCATION_RESULT = "allocationResult";

	private static final String PRODUCT = "PRODUCT";

	private static final String EXECUTION_RESULT = ContextIdNames.INVENTORY_EXECUTION_RESULT;

	private static final String ORDER_SKU_SELECT_BY_CODE_AND_STATUS = "ORDER_SKU_SELECT_BY_CODE_AND_STATUS";

	private static final int QUANTITY_0 = 0;

	private static final int ORDER_UID_2000 = 2000;

	private static final int QUANTITY_ONHAND = 100;

	private static final int QUANTITY_5 = 5;

	private static final int QUANTITY_NEG_10 = -10;

	private static final int QUANTITY_10 = 10;

	private static final long INVENTORY_UID_1000 = 1000;

	private static final String EVENT_ORIGINATOR_TESTER = "Tester";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private ProductInventoryManagementServiceImpl productInventoryManagementService;

	private PersistenceEngine persistenceEngine;

	private InventoryDao inventoryDao;

	private InventoryJournalDao inventoryJournalDao;

	private AllocationServiceImpl allocationService;

	private ProductSkuService productSkuService;

	private InventoryDtoImpl inventoryDto;

	private InventoryFacadeImpl inventoryFacade;

	private JournalingInventoryStrategy journalingInventoryStrategy;

	private ProductInventoryManagementService productInventoryManagementService1;

	private OrderSku orderSku1;

	private OrderSku orderSku2;

	private static final String AVAILABLE_QUANTITY_SHOULD_BE_MESSAGE = "Available Quantity should be ";

	private static final int QUANTITY_OF_10 = 10;

	private static final int QUANTITY_OF_5 = 5;

	private static final int QUANTITY_OF_15 = 15;

	private final InventoryDtoAssembler assembler = new InventoryDtoAssembler();

	private final InventoryLogSupport logSupport = new InventoryLogSupport() {
		@Override
		public void logCommandExecution(final InventoryLogContext logContext) {
			// do nothing
		}
	};

	private BeanFactory beanFactory;
	private BeanFactoryExpectationsFactory expectationsFactory;
	private InventoryKey inventoryKey;
	private StoreService storeService;
	private Store store;

	/**
	 * Prepares for tests.
	 *
	 * @throws Exception
	 *             -- in case of any errors.
	 */
	@Before
	public void setUp() throws Exception {
		beanFactory = context.mock(BeanFactory.class);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);

		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.MONEY_FORMATTER, StandardMoneyFormatter.class);

		productInventoryManagementService = new ProductInventoryManagementServiceImpl() {
			@Override
			public InventoryDto saveOrUpdate(final InventoryDto inventoryDto)
			throws EpServiceException {
				return inventoryDto;
			}
		};

		persistenceEngine = context.mock(PersistenceEngine.class);
		productSkuService = context.mock(ProductSkuService.class);
		inventoryDao = context.mock(InventoryDao.class);
		inventoryJournalDao = context.mock(InventoryJournalDao.class);

		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.INVENTORY, InventoryDtoImpl.class);

		productInventoryManagementService.setProductSkuService(productSkuService);

		inventoryKey = new InventoryKey();
		inventoryKey.setSkuCode(SKU_CODE);
		inventoryKey.setWarehouseUid(WAREHOUSE_UID);

		final Warehouse warehouse = new WarehouseImpl();
		warehouse.setCode("US_WAREHOUSE");
		warehouse.setUidPk(WAREHOUSE_UID);
		productInventoryManagementService.setWarehouseService(new WarehouseServiceImpl() {
			@Override
			public Warehouse findByCode(final String code)
			throws EpServiceException {
				return warehouse;
			}

			@Override
			public Warehouse getWarehouse(final long warehouseUid)
			throws EpServiceException {
				return warehouse;
			}
		});

		store = new StoreImpl();
		store.setCode("store");
		store.setWarehouses(Arrays.asList(warehouse));

		storeService = context.mock(StoreService.class);
		context.checking(new Expectations() { {
			allowing(storeService).findStoreWithCode(store.getCode()); will(returnValue(store));
		} });

		allocationService = new AllocationServiceImpl();
		allocationService.setPersistenceEngine(persistenceEngine);
		allocationService.setProductInventoryManagementService(productInventoryManagementService);
		allocationService.setStoreService(storeService);

		context.checking(new Expectations() {
			{
				allowing(persistenceEngine).isCacheEnabled(); will(returnValue(false));
			}
		});

		inventoryDto = new InventoryDtoImpl();
		orderSku1 = new OrderSkuTestImpl();
		orderSku2 = new OrderSkuTestImpl();

		productInventoryManagementService1 = new ProductInventoryManagementServiceImpl() {
			@Override
			public InventoryDto getInventory(final String skuCode, final long warehouseUid) {
				return inventoryDto;
			}
		};

		journalingInventoryStrategy = new JournalingInventoryStrategy();
		journalingInventoryStrategy.setInventoryDao(inventoryDao);
		journalingInventoryStrategy.setInventoryJournalDao(inventoryJournalDao);
		journalingInventoryStrategy.setBeanFactory(beanFactory);
		journalingInventoryStrategy.setInventoryLogSupport(logSupport);

		inventoryFacade = new InventoryFacadeImpl();
		Map<String, InventoryStrategy> strategies = new HashMap<String, InventoryStrategy>();
		strategies.put("allocatedjournaling", journalingInventoryStrategy);
		inventoryFacade.setStrategies(strategies);
		inventoryFacade.selectStrategy("allocatedjournaling");
		productInventoryManagementService.setInventoryFacade(inventoryFacade);

	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	/**
	 * OrderSKu override for skuAllocation tests.
	 */
	class OrderSkuTestImpl extends OrderSkuImpl {
		private static final long serialVersionUID = WAREHOUSE_UID;
		/**
		 * @return if allocated
		 */
		@Override
		public boolean isAllocated() {
			return true;
		}
		/**
		 * @param quantity - quantity
		 */
		@Override
		public void setQuantity(final int quantity) {
			super.setQuantity(quantity);
			super.setAllocatedQuantity(quantity);
			inventoryDto.setAllocatedQuantity(inventoryDto.getAllocatedQuantity() + orderSku1.getQuantity());
		}
	};

	/**
	 * Test method for 'processInventoryUpdate()' when order placed.
	 */
	@Test
	public void testProcessInventoryUpdateOrderPlaced() {
		final Inventory inventory = new InventoryImpl();
		inventory.setUidPk(INVENTORY_UID_1000);
		inventory.setQuantityOnHand(QUANTITY_ONHAND);
		inventory.setWarehouseUid(WAREHOUSE_UID);

		final ProductSku productSku = new ProductSkuImpl();
		productSku.setSkuCode(SKU_CODE);
		final ProductImpl product = new ProductImpl();
		product.setAvailabilityCriteria(AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK);
		productSku.setProduct(product);
		inventory.setSkuCode(productSku.getSkuCode());

		final Order order = new OrderImpl();
		order.setUidPk(ORDER_UID_2000);

		final InventoryJournalRollupImpl ijRollup = new InventoryJournalRollupImpl();
		ijRollup.setAllocatedQuantityDelta(QUANTITY_10);

		context.checking(new Expectations() {
			{
				InventoryJournalImpl inventoryJournal = new InventoryJournalImpl();
				allowing(beanFactory).getBean(EXECUTION_RESULT); will(returnValue(new InventoryExecutionResultImpl()));
				allowing(beanFactory).getBean(INVENTORY_JOURNAL);
				will(returnValue(inventoryJournal));
				allowing(inventoryDao).getInventory(SKU_CODE, WAREHOUSE_UID); will(returnValue(inventory));
				allowing(inventoryJournalDao).getRollup(inventoryKey); will(returnValue(ijRollup));

				oneOf(inventoryJournalDao).saveOrUpdate(inventoryJournal); will(returnValue(new InventoryJournalImpl()));
			}
		});



		productInventoryManagementService.processInventoryUpdate(
				productSku, WAREHOUSE_UID,	InventoryEventType.STOCK_ALLOCATE, EVENT_ORIGINATOR_TESTER, QUANTITY_10, order, null);

		// WE SHALL CHECK THE RESULT FROM processInventoryUpdate()
		InventoryDto inventoryDto = productInventoryManagementService.getInventory(inventory.getSkuCode(), inventory.getWarehouseUid());
		assertEquals(inventoryDto.getQuantityOnHand(), QUANTITY_ONHAND);
		assertEquals(inventoryDto.getAvailableQuantityInStock(), QUANTITY_ONHAND - QUANTITY_10);
		assertEquals(inventoryDto.getAllocatedQuantity(), QUANTITY_10);
	}

	private PhysicalOrderShipmentImpl getMockPhysicalOrderShipment() {

		final TaxCalculationResult result = new TaxCalculationResultImpl() {
			private static final long serialVersionUID = WAREHOUSE_UID;

			@Override
			public Money getBeforeTaxShippingCost() {
				return MoneyFactory.createMoney(BigDecimal.ONE, Currency.getInstance(Locale.US));
			}

			@Override
			public Money getBeforeTaxSubTotal() {
				return MoneyFactory.createMoney(BigDecimal.TEN, Currency.getInstance(Locale.US));
			}

			@Override
			public void applyTaxes(final Collection< ? extends ShoppingItem> shoppingItems) { //NOPMD
			}
		};
		result.setDefaultCurrency(Currency.getInstance(Locale.US));

		final PhysicalOrderShipmentImpl physicalOrderShipmentImpl = new PhysicalOrderShipmentImpl() {
			private static final long serialVersionUID = WAREHOUSE_UID;

			@Override
			protected TaxCalculationResult calculateTaxes() {
				return result;
			}
		};

		return physicalOrderShipmentImpl;
	}

	/**
	 * Test method for 'processInventoryUpdate()' when sku is added to an order shipment.
	 */
	@Test
	public void testProcessInventoryUpdateOrderAdjustmentAddSku() {
		final Inventory inventory = new InventoryImpl();
		inventory.setUidPk(INVENTORY_UID_1000);
		inventory.setQuantityOnHand(QUANTITY_ONHAND);
		inventory.setWarehouseUid(WAREHOUSE_UID);
		final ProductSku productSku = new ProductSkuImpl();
		productSku.setSkuCode(SKU_CODE);
		productSku.setGuid(SKU_CODE);
		final ProductImpl product = new ProductImpl();
		product.setAvailabilityCriteria(AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK);
		product.setGuid(PRODUCT);
		productSku.setProduct(product);

		inventory.setSkuCode(productSku.getSkuCode());

		final Order order = new OrderImpl();
		order.setUidPk(ORDER_UID_2000);
		order.setStoreCode(store.getCode());
		final PhysicalOrderShipmentImpl shipment = getMockPhysicalOrderShipment();
		final OrderSku orderSku = new OrderSkuImpl();
		orderSku.setProductSku(productSku);
		shipment.addShipmentOrderSku(orderSku);
		order.addShipment(shipment);

		final InventoryJournalRollupImpl ijRollup = new InventoryJournalRollupImpl();
		ijRollup.setAllocatedQuantityDelta(QUANTITY_10);

		context.checking(new Expectations() {
			{
				oneOf(beanFactory).getBean(ALLOCATION_RESULT); will(returnValue(new AllocationResultImpl()));

				InventoryJournalImpl inventoryJournal = new InventoryJournalImpl();
				allowing(beanFactory).getBean(EXECUTION_RESULT); will(returnValue(new InventoryExecutionResultImpl()));
				allowing(beanFactory).getBean(INVENTORY_JOURNAL);
				will(returnValue(inventoryJournal));
				allowing(inventoryDao).getInventory(SKU_CODE, WAREHOUSE_UID); will(returnValue(inventory));
				allowing(inventoryJournalDao).getRollup(inventoryKey); will(returnValue(ijRollup));
				oneOf(inventoryJournalDao).saveOrUpdate(inventoryJournal); will(returnValue(new InventoryJournalImpl()));
			}
		});

		allocationService.processAllocationEvent(orderSku, AllocationEventType.ORDER_ADJUSTMENT_ADDSKU,	EVENT_ORIGINATOR_TESTER, QUANTITY_10, null);

		InventoryDto inventoryDto = productInventoryManagementService.getInventory(inventory.getSkuCode(), inventory.getWarehouseUid());
		assertEquals(inventoryDto.getQuantityOnHand(), QUANTITY_ONHAND);
		assertEquals(inventoryDto.getAvailableQuantityInStock(), QUANTITY_ONHAND - QUANTITY_10);
		assertEquals(inventoryDto.getAllocatedQuantity(), QUANTITY_10);
	}

	/**
	 * Test method for 'processInventoryUpdate()' when order adjustment with sku
	 * removed.
	 */
	@Test
	public void testProcessInventoryUpdateOrderAdjustmentRemoveSku() {
		final Inventory inventory = new InventoryImpl();
		inventory.setUidPk(INVENTORY_UID_1000);
		inventory.setQuantityOnHand(QUANTITY_ONHAND);
		inventory.setWarehouseUid(WAREHOUSE_UID);
		final ProductSku productSku = new ProductSkuImpl();
		productSku.setSkuCode(SKU_CODE);
		final ProductImpl product = new ProductImpl();
		product.setAvailabilityCriteria(AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK);
		productSku.setProduct(product);

		inventory.setSkuCode(productSku.getSkuCode());

		final Order order = new OrderImpl();
		order.setUidPk(ORDER_UID_2000);

		final InventoryJournalRollupImpl ijRollup = new InventoryJournalRollupImpl();
		ijRollup.setAllocatedQuantityDelta(QUANTITY_10);


		context.checking(new Expectations() {
			{
				InventoryJournalImpl inventoryJournal = new InventoryJournalImpl();
				oneOf(beanFactory).getBean(INVENTORY_JOURNAL); will(returnValue(inventoryJournal));
				allowing(beanFactory).getBean(EXECUTION_RESULT); will(returnValue(new InventoryExecutionResultImpl()));

				allowing(inventoryDao).getInventory(SKU_CODE, WAREHOUSE_UID); will(returnValue(inventory));
				allowing(inventoryJournalDao).getRollup(inventoryKey); will(returnValue(ijRollup));
				oneOf(inventoryJournalDao).saveOrUpdate(inventoryJournal); will(returnValue(new InventoryJournalImpl()));
			}
		});
		productInventoryManagementService.processInventoryUpdate(
				productSku, 1,	InventoryEventType.STOCK_ALLOCATE, EVENT_ORIGINATOR_TESTER,	QUANTITY_10, order, null);

		InventoryDto inventoryDto = productInventoryManagementService.getInventory(inventory.getSkuCode(), inventory.getWarehouseUid());

		assertEquals(inventoryDto.getQuantityOnHand(), QUANTITY_ONHAND);
		assertEquals(inventoryDto.getAvailableQuantityInStock(), QUANTITY_ONHAND - QUANTITY_10);
		assertEquals(inventoryDto.getAllocatedQuantity(), QUANTITY_10);

		final Inventory inventory2 = assembler.assembleDomainFromDto(inventoryDto);
		final InventoryJournalRollupImpl ijRollup2 = new InventoryJournalRollupImpl();
		ijRollup2.setAllocatedQuantityDelta(QUANTITY_NEG_10);

		final InventoryDao inventoryDao2 = context.mock(InventoryDao.class, INVENTORY_DAO2);
		final InventoryJournalDao inventoryJournalDao2 = context.mock(InventoryJournalDao.class, INVENTORY_JOURNAL_DAO2);

		journalingInventoryStrategy.setInventoryDao(inventoryDao2);
		journalingInventoryStrategy.setInventoryJournalDao(inventoryJournalDao2);

		context.checking(new Expectations() {
			{
				InventoryJournalImpl inventoryJournal = new InventoryJournalImpl();
				oneOf(beanFactory).getBean(INVENTORY_JOURNAL); will(returnValue(inventoryJournal));
				allowing(beanFactory).getBean(EXECUTION_RESULT); will(returnValue(new InventoryExecutionResultImpl()));
				allowing(inventoryDao2).getInventory(SKU_CODE, WAREHOUSE_UID); will(returnValue(inventory2));
				allowing(inventoryJournalDao2).getRollup(inventoryKey); will(returnValue(ijRollup2));
				oneOf(inventoryJournalDao2).saveOrUpdate(inventoryJournal); will(returnValue(new InventoryJournalImpl()));
			}
		});

		productInventoryManagementService.processInventoryUpdate(
				productSku, WAREHOUSE_UID,	InventoryEventType.STOCK_DEALLOCATE, EVENT_ORIGINATOR_TESTER, QUANTITY_10, order, null);

		inventoryDto = productInventoryManagementService.getInventory(inventory2.getSkuCode(), inventory2.getWarehouseUid());
		assertEquals(QUANTITY_ONHAND, inventoryDto.getQuantityOnHand());
		assertEquals(QUANTITY_ONHAND, inventoryDto.getAvailableQuantityInStock());
		assertEquals(QUANTITY_0, inventoryDto.getAllocatedQuantity());
	}

	/**
	 * Test method for 'processInventoryUpdate()' when order adjustment with
	 * quantity changed.
	 */
	@Test
	public void testProcessInventoryUpdateOrderAdjustmentChangeQty() {
		final Inventory inventory = new InventoryImpl();
		inventory.setUidPk(INVENTORY_UID_1000);
		inventory.setQuantityOnHand(QUANTITY_ONHAND);
		final long warehouseUid = WAREHOUSE_UID;
		inventory.setWarehouseUid(warehouseUid);
		final ProductSku productSku = new ProductSkuImpl();
		final String skuCode = SKU_CODE;
		productSku.setSkuCode(skuCode);
		final ProductImpl product = new ProductImpl();
		product.setAvailabilityCriteria(AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK);
		product.setGuid(PRODUCT);
		productSku.setProduct(product);

		inventory.setSkuCode(productSku.getSkuCode());

		final Order order = new OrderImpl();
		order.setUidPk(ORDER_UID_2000);
		order.setStoreCode(store.getCode());
		final PhysicalOrderShipmentImpl shipment = getMockPhysicalOrderShipment();
		final OrderSku orderSku = new OrderSkuImpl();
		orderSku.setProductSku(productSku);
		shipment.addShipmentOrderSku(orderSku);
		order.addShipment(shipment);

		final InventoryJournalRollupImpl ijRollup = new InventoryJournalRollupImpl();
		ijRollup.setAllocatedQuantityDelta(QUANTITY_10);

		context.checking(new Expectations() {
			{
				InventoryJournalImpl inventoryJournal = new InventoryJournalImpl();
				oneOf(beanFactory).getBean(INVENTORY_JOURNAL); will(returnValue(inventoryJournal));
				oneOf(beanFactory).getBean(ALLOCATION_RESULT); will(returnValue(new AllocationResultImpl()));
				oneOf(beanFactory).getBean(EXECUTION_RESULT); will(returnValue(new InventoryExecutionResultImpl()));

				allowing(inventoryDao).getInventory(skuCode, warehouseUid); will(returnValue(inventory));
				allowing(inventoryJournalDao).getRollup(inventoryKey); will(returnValue(ijRollup));
				oneOf(inventoryJournalDao).saveOrUpdate(inventoryJournal); will(returnValue(new InventoryJournalImpl()));
			}
		});

		productInventoryManagementService.processInventoryUpdate(
				productSku, 1,	InventoryEventType.STOCK_ALLOCATE, EVENT_ORIGINATOR_TESTER, QUANTITY_10, order, null);
		InventoryDto inventoryDto = productInventoryManagementService.getInventory(inventory.getSkuCode(), inventory.getWarehouseUid());
		assertEquals(QUANTITY_ONHAND, inventoryDto.getQuantityOnHand());
		assertEquals(QUANTITY_ONHAND - QUANTITY_10, inventoryDto.getAvailableQuantityInStock());
		assertEquals(QUANTITY_10, inventoryDto.getAllocatedQuantity());

		context.checking(new Expectations() {
			{
				oneOf(beanFactory).getBean(EXECUTION_RESULT); will(returnValue(new InventoryExecutionResultImpl()));
			}
		});

		final Inventory inventory2 = assembler.assembleDomainFromDto(inventoryDto);

		final InventoryDao inventoryDao2 = context.mock(InventoryDao.class, INVENTORY_DAO2);

		journalingInventoryStrategy.setInventoryDao(inventoryDao2);

		context.checking(new Expectations() {
			{
				InventoryJournalImpl inventoryJournal = new InventoryJournalImpl();
				oneOf(beanFactory).getBean(INVENTORY_JOURNAL); will(returnValue(inventoryJournal));
				allowing(inventoryDao2).getInventory(skuCode, warehouseUid); will(returnValue(inventory2));
				oneOf(inventoryJournalDao).saveOrUpdate(inventoryJournal); will(returnValue(new InventoryJournalImpl()));
			}
		});

		allocationService.processAllocationEvent(
				orderSku, AllocationEventType.ORDER_ADJUSTMENT_CHANGEQTY, EVENT_ORIGINATOR_TESTER, QUANTITY_10, null);

		inventoryDto = productInventoryManagementService.getInventory(inventory.getSkuCode(), inventory.getWarehouseUid());

		assertEquals(QUANTITY_ONHAND, inventoryDto.getQuantityOnHand());
		assertEquals(QUANTITY_ONHAND - QUANTITY_10 - QUANTITY_10, inventoryDto.getAvailableQuantityInStock());
		assertEquals(QUANTITY_10 + QUANTITY_10, inventoryDto.getAllocatedQuantity());
	}

	/**
	 * Test method for 'processInventoryUpdate()' with order canceled.
	 */
	@Test
	public void testProcessInventoryUpdateOrderCancellation() {
		final Inventory inventory = new InventoryImpl();
		inventory.setUidPk(INVENTORY_UID_1000);
		inventory.setQuantityOnHand(QUANTITY_ONHAND);
		inventory.setWarehouseUid(WAREHOUSE_UID);
		final ProductSku productSku = new ProductSkuImpl();
		productSku.setSkuCode(SKU_CODE);
		final ProductImpl product = new ProductImpl();
		product.setAvailabilityCriteria(AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK);
		productSku.setProduct(product);

		inventory.setSkuCode(productSku.getSkuCode());

		final Order order = new OrderImpl();
		order.setUidPk(ORDER_UID_2000);

		final InventoryJournalRollupImpl ijRollup = new InventoryJournalRollupImpl();
		ijRollup.setAllocatedQuantityDelta(QUANTITY_10);

		context.checking(new Expectations() {
			{
				InventoryJournalImpl inventoryJournal = new InventoryJournalImpl();
				oneOf(beanFactory).getBean(INVENTORY_JOURNAL); will(returnValue(inventoryJournal));
				oneOf(beanFactory).getBean(EXECUTION_RESULT); will(returnValue(new InventoryExecutionResultImpl()));

				allowing(inventoryDao).getInventory(SKU_CODE, WAREHOUSE_UID); will(returnValue(inventory));
				allowing(inventoryJournalDao).getRollup(inventoryKey); will(returnValue(ijRollup));
				oneOf(inventoryJournalDao).saveOrUpdate(inventoryJournal); will(returnValue(new InventoryJournalImpl()));
			}
		});

		productInventoryManagementService.processInventoryUpdate(
				productSku, 1,	InventoryEventType.STOCK_ALLOCATE, EVENT_ORIGINATOR_TESTER, QUANTITY_10, order, null);
		InventoryDto inventoryDto = productInventoryManagementService.getInventory(inventory.getSkuCode(), inventory.getWarehouseUid());
		assertEquals(QUANTITY_ONHAND, inventoryDto.getQuantityOnHand());
		assertEquals(QUANTITY_ONHAND - QUANTITY_10, inventoryDto.getAvailableQuantityInStock());
		assertEquals(QUANTITY_10, inventoryDto.getAllocatedQuantity());

		context.checking(new Expectations() {
			{
				oneOf(beanFactory).getBean(EXECUTION_RESULT); will(returnValue(new InventoryExecutionResultImpl()));
			}
		});

		final Inventory inventory2 = assembler.assembleDomainFromDto(inventoryDto);

		final InventoryDao inventoryDao2 = context.mock(InventoryDao.class, INVENTORY_DAO2);
		final InventoryJournalDao inventoryJournalDao2 = context.mock(InventoryJournalDao.class, INVENTORY_JOURNAL_DAO2);
		journalingInventoryStrategy.setInventoryDao(inventoryDao2);
		journalingInventoryStrategy.setInventoryJournalDao(inventoryJournalDao2);

		final InventoryJournalRollupImpl ijRollup2 = new InventoryJournalRollupImpl();
		ijRollup2.setAllocatedQuantityDelta(QUANTITY_NEG_10);
		context.checking(new Expectations() {
			{
				InventoryJournalImpl inventoryJournal = new InventoryJournalImpl();
				oneOf(beanFactory).getBean(INVENTORY_JOURNAL); will(returnValue(inventoryJournal));
				atLeast(1).of(inventoryDao2).getInventory(SKU_CODE, WAREHOUSE_UID); will(returnValue(inventory2));
				atLeast(1).of(inventoryJournalDao2).saveOrUpdate(inventoryJournal); will(returnValue(new InventoryJournalImpl()));
				atLeast(1).of(inventoryJournalDao2).getRollup(inventoryKey); will(returnValue(ijRollup2));
			}
		});

		productInventoryManagementService.processInventoryUpdate(
				productSku, WAREHOUSE_UID,	InventoryEventType.STOCK_DEALLOCATE, EVENT_ORIGINATOR_TESTER, QUANTITY_10, order, null);
		inventoryDto = productInventoryManagementService.getInventory(inventory.getSkuCode(), inventory.getWarehouseUid());
		assertEquals(QUANTITY_ONHAND, inventoryDto.getQuantityOnHand());
		assertEquals(QUANTITY_ONHAND, inventoryDto.getAvailableQuantityInStock());
		assertEquals(QUANTITY_0, inventoryDto.getAllocatedQuantity());
	}

	/**
	 * Test method for 'processInventoryUpdate()'.
	 */
	@Test
	public void testProcessInventoryUpdateOrderShipmentRelease() {
		final Inventory inventory = new InventoryImpl();
		inventory.setUidPk(INVENTORY_UID_1000);
		inventory.setQuantityOnHand(QUANTITY_ONHAND);
		inventory.setWarehouseUid(WAREHOUSE_UID);
		final ProductSku productSku = new ProductSkuImpl();
		final String skuCode = SKU_CODE;
		productSku.setSkuCode(skuCode);
		final ProductImpl product = new ProductImpl();
		product.setAvailabilityCriteria(AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK);
		productSku.setProduct(product);

		inventory.setSkuCode(productSku.getSkuCode());

		final Order order = new OrderImpl();
		order.setUidPk(ORDER_UID_2000);

		final InventoryJournalRollupImpl ijRollup = new InventoryJournalRollupImpl();
		ijRollup.setAllocatedQuantityDelta(QUANTITY_10);

		context.checking(new Expectations() {
			{
				InventoryJournalImpl inventoryJournal = new InventoryJournalImpl();
				oneOf(beanFactory).getBean(INVENTORY_JOURNAL); will(returnValue(inventoryJournal));
				oneOf(beanFactory).getBean(EXECUTION_RESULT); will(returnValue(new InventoryExecutionResultImpl()));

				atLeast(1).of(inventoryDao).getInventory(skuCode, WAREHOUSE_UID); will(returnValue(inventory));
				atLeast(1).of(inventoryJournalDao).getRollup(inventoryKey); will(returnValue(ijRollup));
				oneOf(inventoryJournalDao).saveOrUpdate(inventoryJournal); will(returnValue(new InventoryJournalImpl()));
			}
		});

		productInventoryManagementService.processInventoryUpdate(
				productSku, 1,	InventoryEventType.STOCK_ALLOCATE, EVENT_ORIGINATOR_TESTER, QUANTITY_10, order, null);
		InventoryDto inventoryDto = productInventoryManagementService.getInventory(inventory.getSkuCode(), inventory.getWarehouseUid());
		assertEquals(QUANTITY_ONHAND, inventoryDto.getQuantityOnHand());
		assertEquals(QUANTITY_ONHAND - QUANTITY_10, inventoryDto.getAvailableQuantityInStock());
		assertEquals(QUANTITY_10, inventoryDto.getAllocatedQuantity());

		context.checking(new Expectations() {
			{
				oneOf(beanFactory).getBean(EXECUTION_RESULT); will(returnValue(new InventoryExecutionResultImpl()));
			}
		});

		final Inventory inventory2 = assembler.assembleDomainFromDto(inventoryDto);

		final InventoryDao inventoryDao2 = context.mock(InventoryDao.class, INVENTORY_DAO2);
		final InventoryJournalDao inventoryJournalDao2 = context.mock(InventoryJournalDao.class, INVENTORY_JOURNAL_DAO2);
		journalingInventoryStrategy.setInventoryDao(inventoryDao2);
		journalingInventoryStrategy.setInventoryJournalDao(inventoryJournalDao2);

		final InventoryJournalRollupImpl ijRollup2 = new InventoryJournalRollupImpl();
		ijRollup2.setAllocatedQuantityDelta(QUANTITY_NEG_10);
		ijRollup2.setQuantityOnHandDelta(QUANTITY_NEG_10);

		context.checking(new Expectations() {
			{
				InventoryJournalImpl inventoryJournal = new InventoryJournalImpl();
				oneOf(beanFactory).getBean(INVENTORY_JOURNAL); will(returnValue(inventoryJournal));
				atLeast(1).of(inventoryDao2).getInventory(skuCode, WAREHOUSE_UID); will(returnValue(inventory2));
				atLeast(1).of(inventoryJournalDao2).saveOrUpdate(inventoryJournal); will(returnValue(new InventoryJournalImpl()));
				atLeast(1).of(inventoryJournalDao2).getRollup(inventoryKey); will(returnValue(ijRollup2));
			}
		});

		productInventoryManagementService.processInventoryUpdate(
				productSku, WAREHOUSE_UID,	InventoryEventType.STOCK_RELEASE, EVENT_ORIGINATOR_TESTER, QUANTITY_10, order, null);
		inventoryDto = productInventoryManagementService.getInventory(inventory.getSkuCode(), inventory.getWarehouseUid());
		assertEquals(QUANTITY_ONHAND - QUANTITY_10, inventoryDto.getQuantityOnHand());
		assertEquals(QUANTITY_ONHAND - QUANTITY_10, inventoryDto.getAvailableQuantityInStock());
		assertEquals(QUANTITY_0, inventoryDto.getAllocatedQuantity());
	}

	/**
	 * Test method for 'getQuantityAwaitingAllocation()'.
	 */
	@Test
	public void testGetQuantityAwaitingAllocation() {
		final List<OrderSku> listSkus = new ArrayList<OrderSku>();
		final OrderSku orderSku1 = new OrderSkuImpl();
		orderSku1.setPrice(1, null);
		orderSku1.setAllocatedQuantity(1);
		final OrderSku orderSku2 = new OrderSkuImpl();
		orderSku2.setPrice(1, null);
		orderSku2.setAllocatedQuantity(0);
		listSkus.add(orderSku1);
		listSkus.add(orderSku2);

		final InventoryDtoImpl inventoryDto = new InventoryDtoImpl();
		inventoryDto.setQuantityOnHand(QUANTITY_ONHAND);
		inventoryDto.setWarehouseUid(WAREHOUSE_UID);
		final ProductSku productSku = new ProductSkuImpl();
		productSku.setSkuCode(SKU_CODE);
		inventoryDto.setSkuCode(productSku.getSkuCode());

		final PersistenceSession persistanceSession = context.mock(PersistenceSession.class);
		final Query query = context.mock(Query.class);

		context.checking(new Expectations() {
			{
				allowing(persistenceEngine).getPersistenceSession(); will(returnValue(persistanceSession));
				allowing(persistanceSession).createNamedQuery(ORDER_SKU_SELECT_BY_CODE_AND_STATUS); will(returnValue(query));
				allowing(persistenceEngine).retrieveByNamedQuery(with(equal(ORDER_SKU_SELECT_BY_CODE_AND_STATUS)), with(any(Object[].class)));
				will(returnValue(Arrays.asList(orderSku1, orderSku2)));
				ignoring(query).setParameter(with(any(int.class)), with(any(Object.class)));
				allowing(query).list(); will(returnValue(listSkus));
			}
		});

		assertEquals(
				orderSku2.getQuantity() - orderSku2.getAllocatedQuantity(),
				allocationService.getQuantityAwaitingAllocation(productSku
						.getSkuCode(), WAREHOUSE_UID));

	}

	/**
	 * Tests isSelfAllocationSufficient() for a single sku.
	 */
	@Test
	public void testSingleSkuAllocation() {
		final int qty = 1;

		inventoryDto.setQuantityOnHand(qty);
		assertEquals(AVAILABLE_QUANTITY_SHOULD_BE_MESSAGE + qty, qty, inventoryDto.getAvailableQuantityInStock());

		orderSku1.setQuantity(qty);

		inventoryDto.setAllocatedQuantity(qty);
		assertEquals("Allocated Quantity should be " + qty, qty, inventoryDto.getAllocatedQuantity());
		assertEquals(AVAILABLE_QUANTITY_SHOULD_BE_MESSAGE + 0, 0, inventoryDto.getAvailableQuantityInStock());

		assertTrue("There should be enough allocation for this sku", productInventoryManagementService1.isSelfAllocationSufficient(orderSku1, 0));
	}

	/**
	 * Tests isSelfAllocationSufficient() for multiple skus with not enough inventory for the second sku/shipment.
	 */
	@Test
	public void testMultipleSkuAllocation() {
		final int skuQty = 2;
		final int stockReductionQty = 1;

		inventoryDto.setQuantityOnHand(skuQty * 2);
		assertEquals(AVAILABLE_QUANTITY_SHOULD_BE_MESSAGE + skuQty * 2, skuQty * 2, inventoryDto.getAvailableQuantityInStock());

		orderSku1.setQuantity(skuQty);
		assertEquals(AVAILABLE_QUANTITY_SHOULD_BE_MESSAGE + (skuQty * 2 - skuQty), skuQty * 2 - skuQty, inventoryDto.getAvailableQuantityInStock());
		orderSku2.setQuantity(skuQty);
		assertEquals(AVAILABLE_QUANTITY_SHOULD_BE_MESSAGE + 0, 0, inventoryDto.getAvailableQuantityInStock());

		//stock reduction, but it doesn't affect allocation
		inventoryDto.setQuantityOnHand(inventoryDto.getQuantityOnHand() - stockReductionQty);

		assertEquals("Quantity on Hand should be " + (skuQty * 2 - stockReductionQty),
				skuQty * 2 - stockReductionQty, inventoryDto.getQuantityOnHand());

		inventoryDto.getAvailableQuantityInStock();

		assertTrue("There should be enough allocation for this sku", productInventoryManagementService1.isSelfAllocationSufficient(orderSku1, 0));

		inventoryDto.setAllocatedQuantity(inventoryDto.getAllocatedQuantity() - skuQty);
		inventoryDto.setQuantityOnHand(inventoryDto.getQuantityOnHand() - skuQty);

		assertEquals(AVAILABLE_QUANTITY_SHOULD_BE_MESSAGE + (inventoryDto.getQuantityOnHand() - inventoryDto.getAllocatedQuantity()),
				inventoryDto.getQuantityOnHand() - inventoryDto.getAllocatedQuantity(), inventoryDto.getAvailableQuantityInStock());
		assertFalse("There should NOT be enough allocation for this sku",
				productInventoryManagementService1.isSelfAllocationSufficient(orderSku2, 0));
	}

	/**
	 * Test the notify of product sku change
	 * when 'processInventoryUpdate()' changed availability.
	 */
	@Test
	public void testProcessInventoryUpdateUpdateProductIndex() {
		final Inventory inventory = new InventoryImpl();
		inventory.setUidPk(INVENTORY_UID_1000);
		inventory.setQuantityOnHand(QUANTITY_ONHAND);
		inventory.setWarehouseUid(WAREHOUSE_UID);
		final ProductSku productSku = new ProductSkuImpl();
		productSku.setSkuCode(SKU_CODE);
		final ProductImpl product = new ProductImpl();
		product.setAvailabilityCriteria(AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK);
		productSku.setProduct(product);
		inventory.setSkuCode(productSku.getSkuCode());

		final Order order = new OrderImpl();
		order.setUidPk(ORDER_UID_2000);

		final IndexNotificationService indexNotificationService =  context.mock(IndexNotificationService.class);
		productInventoryManagementService.setIndexNotificationService(indexNotificationService);

		context.checking(new Expectations() {
			{
				InventoryJournalImpl inventoryJournal = new InventoryJournalImpl();
				oneOf(beanFactory).getBean(INVENTORY_JOURNAL); will(returnValue(inventoryJournal));
				allowing(beanFactory).getBean(EXECUTION_RESULT); will(returnValue(new InventoryExecutionResultImpl()));

				allowing(inventoryDao).getInventory(SKU_CODE, WAREHOUSE_UID); will(returnValue(inventory));
				allowing(inventoryJournalDao).getRollup(inventoryKey); will(returnValue(new InventoryJournalRollupImpl()));
				oneOf(inventoryJournalDao).saveOrUpdate(inventoryJournal); will(returnValue(new InventoryJournalImpl()));

				exactly(2).of(indexNotificationService).addNotificationForEntityIndexUpdate(IndexType.PRODUCT, product.getUidPk());
			}
		});

		//Allocate
		productInventoryManagementService.processInventoryUpdate(
				productSku, 1, InventoryEventType.STOCK_ALLOCATE, EVENT_ORIGINATOR_TESTER, QUANTITY_ONHAND, order, null);

		final InventoryJournalDao inventoryJournalDao2 = context.mock(InventoryJournalDao.class, INVENTORY_JOURNAL_DAO2);
		journalingInventoryStrategy.setInventoryJournalDao(inventoryJournalDao2);

		final InventoryJournalRollupImpl ijRollup2 = new InventoryJournalRollupImpl();
		ijRollup2.setAllocatedQuantityDelta(QUANTITY_ONHAND);

		context.checking(new Expectations() {
			{
				atLeast(1).of(inventoryJournalDao2).getRollup(inventoryKey); will(returnValue(ijRollup2));
			}
		});

		InventoryDto inventoryDto = productInventoryManagementService.getInventory(inventory.getSkuCode(), inventory.getWarehouseUid());
		assertEquals(QUANTITY_ONHAND, inventoryDto.getQuantityOnHand());
		assertEquals(0, inventoryDto.getAvailableQuantityInStock());

		//Deallocate
		final Inventory inventory3 = assembler.assembleDomainFromDto(inventoryDto);

		final InventoryDao inventoryDao3 = context.mock(InventoryDao.class, "inventoryDao3");
		final InventoryJournalDao inventoryJournalDao3 = context.mock(InventoryJournalDao.class, "inventoryJournalDao3");
		journalingInventoryStrategy.setInventoryDao(inventoryDao3);
		journalingInventoryStrategy.setInventoryJournalDao(inventoryJournalDao3);

		context.checking(new Expectations() {
			{
				final InventoryJournalImpl inventoryJournal = new InventoryJournalImpl();
				oneOf(beanFactory).getBean(INVENTORY_JOURNAL); will(returnValue(inventoryJournal));
				allowing(inventoryDao3).getInventory(SKU_CODE, WAREHOUSE_UID); will(returnValue(inventory3));
				atLeast(1).of(inventoryJournalDao3).saveOrUpdate(inventoryJournal); will(returnValue(inventoryJournal));
				atLeast(1).of(inventoryJournalDao3).getRollup(inventoryKey); will(returnValue(new InventoryJournalRollupImpl()));
			}
		});

		productInventoryManagementService.processInventoryUpdate(
				productSku, 1,	InventoryEventType.STOCK_DEALLOCATE, EVENT_ORIGINATOR_TESTER, QUANTITY_ONHAND, order, null);

		final InventoryJournalDao inventoryJournalDao4 = context.mock(InventoryJournalDao.class, "inventoryJournalDao4");
		journalingInventoryStrategy.setInventoryJournalDao(inventoryJournalDao4);

		final InventoryJournalRollupImpl ijRollup4 = new InventoryJournalRollupImpl();
		ijRollup4.setAllocatedQuantityDelta(-QUANTITY_ONHAND);

		context.checking(new Expectations() {
			{
				atLeast(1).of(inventoryJournalDao4).getRollup(inventoryKey); will(returnValue(ijRollup4));
			}
		});

		inventoryDto = productInventoryManagementService.getInventory(inventory.getSkuCode(), inventory.getWarehouseUid());
		assertEquals(QUANTITY_ONHAND, inventoryDto.getAvailableQuantityInStock());
	}

	/**
	 * Test moved from ProductAvailabilityTest.
	 */
	@Test(expected = InsufficientInventoryException.class)
	public void testTypeInStockWhenOutOfStock() {
		final Inventory inventory = new InventoryImpl();
		inventory.setUidPk(INVENTORY_UID_1000);
		inventory.setQuantityOnHand(QUANTITY_OF_10);
		inventory.setReservedQuantity(QUANTITY_OF_5);
		inventory.setWarehouseUid(WAREHOUSE_UID);

		final ProductSku productSku = new ProductSkuImpl();
		productSku.setSkuCode(SKU_CODE);
		final ProductImpl product = new ProductImpl();
		product.setAvailabilityCriteria(AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK);
		product.setGuid(PRODUCT);
		productSku.setProduct(product);

		inventory.setSkuCode(productSku.getSkuCode());

		final Order order = new OrderImpl();
		order.setUidPk(ORDER_UID_2000);
		order.setStoreCode(store.getCode());
		final PhysicalOrderShipmentImpl shipment = getMockPhysicalOrderShipment();
		final OrderSku orderSku = new OrderSkuImpl();
		orderSku.setProductSku(productSku);
		orderSku.setQuantity(QUANTITY_OF_5);
		shipment.addShipmentOrderSku(orderSku);
		order.addShipment(shipment);

		final IndexNotificationService indexNotificationService =  context.mock(IndexNotificationService.class);
		productInventoryManagementService.setIndexNotificationService(indexNotificationService);

		final InventoryKey inventoryKey = new InventoryKey();
		inventoryKey.setSkuCode(SKU_CODE);
		inventoryKey.setWarehouseUid(WAREHOUSE_UID);

		context.checking(new Expectations() {
			{
				InventoryJournalImpl inventoryJournal = new InventoryJournalImpl();
				oneOf(beanFactory).getBean(INVENTORY_JOURNAL); will(returnValue(inventoryJournal));
				oneOf(beanFactory).getBean(EXECUTION_RESULT); will(returnValue(new InventoryExecutionResultImpl()));
				atLeast(1).of(beanFactory).getBean(ALLOCATION_RESULT); will(returnValue(new AllocationResultImpl()));

				allowing(inventoryDao).getInventory(SKU_CODE, WAREHOUSE_UID); will(returnValue(inventory));
				allowing(inventoryJournalDao).getRollup(inventoryKey); will(returnValue(new InventoryJournalRollupImpl()));
				oneOf(inventoryJournalDao).saveOrUpdate(inventoryJournal); will(returnValue(new InventoryJournalImpl()));

				oneOf(indexNotificationService).addNotificationForEntityIndexUpdate(IndexType.PRODUCT, product.getUidPk());
			}
		});

		allocationService.processAllocationEvent(orderSku, AllocationEventType.ORDER_PLACED, "", QUANTITY_OF_5, "");

		final InventoryJournalRollupImpl ijRollup2 = new InventoryJournalRollupImpl();
		ijRollup2.setAllocatedQuantityDelta(QUANTITY_5);

		final InventoryJournalDao inventoryJournalDao2 = context.mock(InventoryJournalDao.class, INVENTORY_JOURNAL_DAO2);
		journalingInventoryStrategy.setInventoryJournalDao(inventoryJournalDao2);

		context.checking(new Expectations() {
			{
				allowing(inventoryJournalDao2).getRollup(inventoryKey); will(returnValue(ijRollup2));
			}
		});

		InventoryDto inventoryDto = productInventoryManagementService.getInventory(inventory.getSkuCode(), inventory.getWarehouseUid());
		assertEquals(QUANTITY_OF_10, inventoryDto.getQuantityOnHand());
		assertEquals(0, inventoryDto.getAvailableQuantityInStock());
		assertEquals(QUANTITY_OF_5, inventoryDto.getAllocatedQuantity());

		final Inventory inventory3 = assembler.assembleDomainFromDto(inventoryDto);
		final InventoryDao inventoryDao3 = context.mock(InventoryDao.class, "inventoryDao3");
		journalingInventoryStrategy.setInventoryDao(inventoryDao3);

		context.checking(new Expectations() {
			{
				atLeast(1).of(inventoryDao3).getInventory(SKU_CODE, WAREHOUSE_UID); will(returnValue(inventory3));
			}
		});

		allocationService.processAllocationEvent(orderSku, AllocationEventType.ORDER_PLACED, "", QUANTITY_OF_5, "");
	}

	/**
	 * Tests quantity on hand, reserved quantity, and a back order limit.
	 * The user should be able to buy more than what's on hand even when the reserved quantity is taken into account.
	 */
	@Test(expected = InsufficientInventoryException.class)
	public void testTypeBackOrderWhenOutOfStock() {
		assertInventoryWithAvailabilityCriteria(AvailabilityCriteria.AVAILABLE_FOR_BACK_ORDER);
	}

	/**
	 * Tests quantity on hand, reserved quantity, and a pre order limit.
	 * The user should be able to buy more than what's on hand even when the reserved quantity is taken into account.
	 */
	@Test(expected = InsufficientInventoryException.class)
	public void testTypePreOrderWhenOutOfStock() {
		assertInventoryWithAvailabilityCriteria(AvailabilityCriteria.AVAILABLE_FOR_PRE_ORDER);
	}

	@SuppressWarnings("PMD.ExcessiveMethodLength")
	private  void assertInventoryWithAvailabilityCriteria(final AvailabilityCriteria availabilityCriteria) {
		final Inventory inventory = new InventoryImpl();
		inventory.setUidPk(INVENTORY_UID_1000);
		inventory.setQuantityOnHand(QUANTITY_OF_10);
		inventory.setReservedQuantity(QUANTITY_OF_5);

		inventory.setWarehouseUid(WAREHOUSE_UID);
		final ProductSku productSku = new ProductSkuImpl();
		productSku.setSkuCode(SKU_CODE);
		final ProductImpl product = new ProductImpl();
		product.setAvailabilityCriteria(availabilityCriteria);
		product.setPreOrBackOrderLimit(QUANTITY_OF_10);
		product.setGuid(PRODUCT);
		productSku.setProduct(product);

		inventory.setSkuCode(productSku.getSkuCode());

		final Order order = new OrderImpl();
		order.setUidPk(ORDER_UID_2000);
		order.setStoreCode(store.getCode());
		final PhysicalOrderShipmentImpl shipment = getMockPhysicalOrderShipment();
		final OrderSku orderSku = new OrderSkuImpl();
		orderSku.setProductSku(productSku);
		orderSku.setQuantity(QUANTITY_OF_5);
		shipment.addShipmentOrderSku(orderSku);
		order.addShipment(shipment);

		InventoryDto inventoryDto = assembler.assembleDtoFromDomain(inventory);

		int availableQty = inventoryDto.getAvailableQuantityInStock() + product.getPreOrBackOrderLimit() - productSku.getPreOrBackOrderedQuantity();
		assertEquals(QUANTITY_OF_15, availableQty);

		assertEquals(QUANTITY_OF_10, product.getPreOrBackOrderLimit() - productSku.getPreOrBackOrderedQuantity());

		final IndexNotificationService indexNotificationService =  context.mock(IndexNotificationService.class);
		productInventoryManagementService.setIndexNotificationService(indexNotificationService);

		final PreOrBackOrderDetails preOrBackOrderDetails = new PreOrBackOrderDetails(
				productSku.getSkuCode(), productSku.getProduct().getPreOrBackOrderLimit(), productSku.getPreOrBackOrderedQuantity());

		context.checking(new Expectations() {
			{
				atLeast(1).of(beanFactory).getBean(EXECUTION_RESULT); will(returnValue(new InventoryExecutionResultImpl()));
				atLeast(1).of(beanFactory).getBean(ALLOCATION_RESULT); will(returnValue(new AllocationResultImpl()));
				InventoryJournalImpl inventoryJournal = new InventoryJournalImpl();
				allowing(beanFactory).getBean(INVENTORY_JOURNAL); will(returnValue(inventoryJournal));
				oneOf(productSkuService).getPreOrBackOrderDetails(SKU_CODE); will(returnValue(preOrBackOrderDetails));
				exactly(THREE).of(inventoryDao).getInventory(SKU_CODE, WAREHOUSE_UID); will(returnValue(inventory));
				never(indexNotificationService).addNotificationForEntityIndexUpdate(IndexType.PRODUCT, product.getUidPk());
				allowing(inventoryJournalDao).getRollup(inventoryKey); will(returnValue(new InventoryJournalRollupImpl()));
				allowing(inventoryJournalDao).saveOrUpdate(inventoryJournal); will(returnValue(new InventoryJournalImpl()));
			}
		});

		AllocationResult allocationResult = allocationService.processAllocationEvent(
				orderSku,
				AllocationEventType.ORDER_PLACED,
				"",
				QUANTITY_OF_15,
		"");
		assertEquals(QUANTITY_OF_5, allocationResult.getInventoryResult().getQuantity());

		final InventoryJournalRollupImpl ijRollup2 = new InventoryJournalRollupImpl();
		ijRollup2.setAllocatedQuantityDelta(QUANTITY_5);

		final InventoryJournalDao inventoryJournalDao2 = context.mock(InventoryJournalDao.class, INVENTORY_JOURNAL_DAO2);
		journalingInventoryStrategy.setInventoryJournalDao(inventoryJournalDao2);

		context.checking(new Expectations() {
			{
				allowing(inventoryJournalDao2).getRollup(inventoryKey); will(returnValue(ijRollup2));
			}
		});

		inventoryDto = productInventoryManagementService.getInventory(inventory.getSkuCode(), inventory.getWarehouseUid());
		assertEquals(0, inventoryDto.getAvailableQuantityInStock());
		assertEquals(QUANTITY_OF_5, inventoryDto.getAllocatedQuantity());

		final Inventory inventory3 = assembler.assembleDomainFromDto(inventoryDto);
		final InventoryDao inventoryDao3 = context.mock(InventoryDao.class, "inventoryDao3");
		journalingInventoryStrategy.setInventoryDao(inventoryDao3);

		context.checking(new Expectations() {
			{
				exactly(2).of(inventoryDao3).getInventory(SKU_CODE, WAREHOUSE_UID); will(returnValue(inventory3));
			}
		});

		// no items should be left so exception should be thrown
		allocationService.processAllocationEvent(
				orderSku,
				AllocationEventType.ORDER_PLACED,
				"",
				QUANTITY_OF_5,
		"");
	}


	/**
	 * Tests no dao operations if product is always available.
	 */
	@Test
	public void testProcessInventoryUpdateOrderShipmentReleaseForAlwaysAvailable() {
		final ProductSku productSku = new ProductSkuImpl();
		productSku.setSkuCode(SKU_CODE);
		final ProductImpl product = new ProductImpl();
		product.setAvailabilityCriteria(AvailabilityCriteria.ALWAYS_AVAILABLE);
		productSku.setProduct(product);

		final Order order = new OrderImpl();
		order.setUidPk(ORDER_UID_2000);

		context.checking(new Expectations() {
			{
				allowing(beanFactory).getBean(EXECUTION_RESULT); will(returnValue(new InventoryExecutionResultImpl()));
				allowing(productSkuService).findBySkuCode(SKU_CODE); will(returnValue(productSku));
			}
		});

		InventoryDto inventoryDto = new InventoryDtoImpl();
		inventoryDto.setSkuCode(productSku.getSkuCode());
		inventoryDto.setWarehouseUid(WAREHOUSE_UID);

		// No DAO operations expected.
		productInventoryManagementService.processInventoryUpdate(inventoryDto, buildInventoryAudit(InventoryEventType.STOCK_ALLOCATE, 1));
		// No DAO operations expected.
		productInventoryManagementService.processInventoryUpdate(inventoryDto, buildInventoryAudit(InventoryEventType.STOCK_RELEASE, 1));

	}

	private InventoryAudit buildInventoryAudit(final InventoryEventType inventoryEventType, final int inventoryQuantity) {
		InventoryAudit inventoryAudit = new InventoryAuditImpl();
		inventoryAudit.setEventType(inventoryEventType);
		inventoryAudit.setQuantity(inventoryQuantity);
		return inventoryAudit;
	}

}
