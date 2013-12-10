package com.elasticpath.service.catalog.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.AvailabilityCriteria;
import com.elasticpath.domain.catalog.InventoryAudit;
import com.elasticpath.domain.catalog.InventoryEventType;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.impl.InventoryAuditImpl;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.inventory.CommandFactory;
import com.elasticpath.inventory.InventoryCommand;
import com.elasticpath.inventory.InventoryDto;
import com.elasticpath.inventory.InventoryExecutionResult;
import com.elasticpath.inventory.InventoryFacade;
import com.elasticpath.inventory.InventoryKey;
import com.elasticpath.inventory.impl.InventoryDtoImpl;
import com.elasticpath.inventory.impl.InventoryExecutionResultImpl;
import com.elasticpath.service.catalog.ProductSkuService;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * Tests the calculated inventory returned by the
 * {@link ProductInventoryManagementServiceImpl#processInventoryUpdate(com.elasticpath.inventory.InventoryDto,
 * com.elasticpath.domain.catalog.InventoryAudit)}
 * after different inventory operations.
 */
public class ProductInventoryManagementServiceImplInventoryResultTest {
	private static final int ALLOCATED_QUANTITY = 10;

	private static final String SKU_CODE = "skuCode";

	private static final int MIN_ORDER_QUANTITY = 2;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private InventoryFacade inventoryFacade;
	private CommandFactory commandFactory;
	private InventoryCommand command;
	private InventoryDto inventoryDto;
	private InventoryKey inventoryKey;
	private ProductInventoryManagementServiceImpl service;
	private InventoryExecutionResultImpl executionResult;

	private BeanFactory beanFactory;
	private BeanFactoryExpectationsFactory expectationFactory;

	private ProductSku productSku;
	private ProductSkuService productSkuService;
	private Product product;

	private static final int QUANTITY_ON_HAND = 20;


	/**
	 * Runs before every test case.
	 */
	@Before
	public void setUp() {
		inventoryFacade = context.mock(InventoryFacade.class);
		commandFactory = context.mock(CommandFactory.class);
		command = context.mock(InventoryCommand.class);
		beanFactory = context.mock(BeanFactory.class);
		executionResult = new InventoryExecutionResultImpl();
		expectationFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
		expectationFactory.allowingBeanFactoryGetBean(ContextIdNames.INVENTORY_EXECUTION_RESULT, executionResult);

		productSkuService = context.mock(ProductSkuService.class);
		productSku = new ProductSkuImpl();
		product = new ProductImpl();
		productSku.setProduct(product);
		product.setMinOrderQty(MIN_ORDER_QUANTITY);
		product.setAvailabilityCriteria(AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK);

		context.checking(new Expectations() {
			{
				allowing(inventoryFacade).getInventoryCommandFactory(); will(returnValue(commandFactory));
				allowing(productSkuService).findBySkuCode(SKU_CODE); will(returnValue(productSku));
				allowing(command).getExecutionResult(); will(returnValue(executionResult));
				allowing(inventoryFacade).executeInventoryCommand(command);
			}
		});

		inventoryDto = new InventoryDtoImpl();
		inventoryDto.setAllocatedQuantity(ALLOCATED_QUANTITY);
		inventoryDto.setQuantityOnHand(QUANTITY_ON_HAND);
		inventoryDto.setSkuCode(SKU_CODE);

		inventoryKey = new InventoryKey();
		inventoryKey.setSkuCode(SKU_CODE);

		service = new ProductInventoryManagementServiceImpl() {
			@Override
			protected void fireNewInventoryEvent(final InventoryDto inventoryDto) {
				//no-op
			};
		};
		service.setInventoryFacade(inventoryFacade);
		service.setProductSkuService(productSkuService);

	}

	@After
	public void tearDown() {
		expectationFactory.close();
	}

	/**
	 * Tests the inventory after an allocate command.
	 */
	@Test
	public void testAllocate() {
		final int quantityToAllocate = 5;
		InventoryAudit audit = createAudit(InventoryEventType.STOCK_ALLOCATE, quantityToAllocate);
		context.checking(new Expectations() {
			{
				oneOf(commandFactory).getAllocateInventoryCommand(inventoryKey, quantityToAllocate);
				will(returnValue(command));
			}
		});

		InventoryExecutionResult result = service.processInventoryUpdate(inventoryDto, audit);
		InventoryDto inventoryAfter = result.getInventoryAfter();
		assertEquals("the inventory should be allocated.",
				inventoryDto.getAllocatedQuantity() + quantityToAllocate, inventoryAfter.getAllocatedQuantity());
		assertEquals("the quantity on hand should not change.", inventoryDto.getQuantityOnHand(), inventoryAfter.getQuantityOnHand());
	}

	/**
	 * Tests the inventory after a de-allocate command.
	 */
	@Test
	public void testDeallocate() {
		final int quantityToDeallocate = 5;
		InventoryAudit audit = createAudit(InventoryEventType.STOCK_DEALLOCATE, quantityToDeallocate);
		context.checking(new Expectations() {
			{
				oneOf(commandFactory).getDeallocateInventoryCommand(inventoryKey, quantityToDeallocate);
				will(returnValue(command));
			}
		});

		InventoryExecutionResult result = service.processInventoryUpdate(inventoryDto, audit);
		InventoryDto inventoryAfter = result.getInventoryAfter();
		assertNotSame(inventoryDto, inventoryAfter);
		assertEquals("the inventory should be deallocated.",
				inventoryDto.getAllocatedQuantity() - quantityToDeallocate, inventoryAfter.getAllocatedQuantity());
		assertEquals("the quantity on hand should not change.", inventoryDto.getQuantityOnHand(), inventoryAfter.getQuantityOnHand());
	}

	/**
	 * Tests the inventory after an adjustment command.
	 */
	@Test
	public void testAdjustment() {
		final int quantityToAdjust = 5;
		InventoryAudit audit = createAudit(InventoryEventType.STOCK_ADJUSTMENT, quantityToAdjust);
		context.checking(new Expectations() {
			{
				oneOf(commandFactory).getAdjustInventoryCommand(inventoryKey, quantityToAdjust);
				will(returnValue(command));
			}
		});

		InventoryExecutionResult result = service.processInventoryUpdate(inventoryDto, audit);
		InventoryDto inventoryAfter = result.getInventoryAfter();
		assertNotSame(inventoryDto, inventoryAfter);
		assertEquals("the allocated quantity should not change.", inventoryDto.getAllocatedQuantity(), inventoryAfter.getAllocatedQuantity());
		assertEquals("the quantity on hand should be adjusted.",
				inventoryDto.getQuantityOnHand() + quantityToAdjust, inventoryAfter.getQuantityOnHand());
	}

	/**
	 * Tests the inventory after a received command.
	 */
	@Test
	public void testReceive() {
		final int quantityToReceive = 5;
		InventoryAudit audit = createAudit(InventoryEventType.STOCK_RECEIVED, quantityToReceive);
		context.checking(new Expectations() {
			{
				oneOf(commandFactory).getAdjustInventoryCommand(inventoryKey, quantityToReceive);
				will(returnValue(command));
			}
		});

		InventoryExecutionResult result = service.processInventoryUpdate(inventoryDto, audit);
		InventoryDto inventoryAfter = result.getInventoryAfter();
		assertNotSame(inventoryDto, inventoryAfter);
		assertEquals("the allocated quantity should not change.", inventoryDto.getAllocatedQuantity(), inventoryAfter.getAllocatedQuantity());
		assertEquals("the quantity on hand should be adjusted.",
				inventoryDto.getQuantityOnHand() + quantityToReceive, inventoryAfter.getQuantityOnHand());
	}

	/**
	 * Tests the inventory after a release command.
	 */
	@Test
	public void testRelease() {
		final int quantityToRelease = 5;
		InventoryAudit audit = createAudit(InventoryEventType.STOCK_RELEASE, quantityToRelease);
		context.checking(new Expectations() {
			{
				oneOf(commandFactory).getReleaseInventoryCommand(inventoryKey, quantityToRelease);
				will(returnValue(command));
			}
		});

		InventoryExecutionResult result = service.processInventoryUpdate(inventoryDto, audit);
		InventoryDto inventoryAfter = result.getInventoryAfter();
		assertNotSame(inventoryDto, inventoryAfter);
		assertEquals("the previously allocated quantity should be de-allocated..",
				inventoryDto.getAllocatedQuantity() - quantityToRelease, inventoryAfter.getAllocatedQuantity());
		assertEquals("the quantity on hand should be adjusted.",
				inventoryDto.getQuantityOnHand() - quantityToRelease, inventoryAfter.getQuantityOnHand());
	}


	private InventoryAudit createAudit(final InventoryEventType eventType, final int quantity) {
		InventoryAudit audit = new InventoryAuditImpl();
		audit.setEventType(eventType);
		audit.setQuantity(quantity);
		executionResult.setQuantity(quantity);
		return audit;
	}


}
