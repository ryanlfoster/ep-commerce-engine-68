package com.elasticpath.service.order.impl;

import static com.elasticpath.domain.catalog.AvailabilityCriteria.ALWAYS_AVAILABLE;
import static com.elasticpath.domain.catalog.AvailabilityCriteria.AVAILABLE_FOR_BACK_ORDER;
import static com.elasticpath.domain.catalog.AvailabilityCriteria.AVAILABLE_FOR_PRE_ORDER;
import static com.elasticpath.domain.catalog.AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK;
import static com.elasticpath.domain.catalog.InventoryEventType.STOCK_ALLOCATE;
import static com.elasticpath.domain.catalog.InventoryEventType.STOCK_DEALLOCATE;
import static com.elasticpath.domain.order.AllocationEventType.ORDER_ADJUSTMENT_ADDSKU;
import static com.elasticpath.domain.order.AllocationEventType.ORDER_ADJUSTMENT_CHANGEQTY;
import static com.elasticpath.domain.order.AllocationEventType.ORDER_ADJUSTMENT_REMOVESKU;
import static com.elasticpath.domain.order.AllocationEventType.ORDER_CANCELLATION;
import static com.elasticpath.domain.order.AllocationEventType.ORDER_PLACED;
import static com.elasticpath.domain.order.AllocationEventType.ORDER_SHIPMENT_COMPLETED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.util.capabilities.Capabilities;
import com.elasticpath.commons.util.capabilities.impl.CapabilitiesImpl;
import com.elasticpath.domain.catalog.AvailabilityCriteria;
import com.elasticpath.domain.catalog.InsufficientInventoryException;
import com.elasticpath.domain.catalog.InventoryEventType;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.impl.PreOrBackOrderDetails;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.inventory.InventoryCapabilities;
import com.elasticpath.service.catalog.ProductInventoryManagementService;

/**
 * Tests the logic that was extracted from the {@link AllocationServiceImpl} into the appropriate enums.
 */
@SuppressWarnings({ "PMD.TooManyStaticImports" })
public class AllocationServiceImplTest {
	
	private static final int AVAIL_IN_STOCK_INSUFFICIENT = 9;
	private static final int AVAIL_IN_STOCK = 20;
	private static final int QTY_TO_ALLOCATE = 10;
	private static final int DETAILS_QTY = 5;
	private static final int LIMIT = 10;
	private static final int INSUFFICIENT_QTY = 6;
	private static final int SUFFICIENT_QTY = 4;
	private static final int ALLOC_ABOVE_LIMIT = INSUFFICIENT_QTY;
	private static final int ALLOC_BELOW_LIMIT = SUFFICIENT_QTY;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private ProductInventoryManagementService pims;
	private final Capabilities noCapabilities = new CapabilitiesImpl();
	private final Capabilities preOrBackOrderLimitCapabilities = new CapabilitiesImpl(InventoryCapabilities.PRE_OR_BACK_ORDER_LIMIT);
	
	/**
	 * Setup.
	 */
	@Before
	public void setUp() {
		pims = context.mock(ProductInventoryManagementService.class);
	}
	
	/**
	 * Tests that stock allocation event delegates to availability criteria and uses {@link ProductInventoryManagementService} for
	 * calculations.
	 */
	@Test
	public void testPreProcessInventoryCommandStockAllocate() {
		
		final ProductSku sku = createProductSku(AVAILABLE_FOR_BACK_ORDER);
		final long warehouseUid = 1L;

		context.checking(new Expectations() { {
			oneOf(pims).getAvailableInStockQty(sku, warehouseUid);
		} });
		
		STOCK_ALLOCATE.preProcessInventoryCommand(pims, sku, warehouseUid, SUFFICIENT_QTY, QTY_TO_ALLOCATE);
		
	}

	/**
	 * Tests stock deallocation event handling. Should return a minimimum from quantity and allocated quantity.
	 */
	@Test
	public void testPreProcessInventoryCommandStockDeallocate() {
		final ProductSku sku = createProductSku(AVAILABLE_FOR_BACK_ORDER);
		final long warehouseUid = 1L;
		
		int deallocatedQty = STOCK_DEALLOCATE.preProcessInventoryCommand(pims, sku, warehouseUid, SUFFICIENT_QTY, QTY_TO_ALLOCATE);
		assertEquals(SUFFICIENT_QTY, deallocatedQty);
		
		deallocatedQty = STOCK_DEALLOCATE.preProcessInventoryCommand(pims, sku, warehouseUid, INSUFFICIENT_QTY, SUFFICIENT_QTY);
		assertEquals(SUFFICIENT_QTY, deallocatedQty);
	}
	
	/**
	 * Tests sufficient in stock alloc qty for pre- and back-order available skus.
	 */
	@Test
	public void testHandlePreBackOrderStockAllocationWhenInStockQtyIsGreaterThanAllocQty() {
		handlePreBackOrderStockAllocationWhenInStockQtyIsGreaterThanAllocQty(AVAILABLE_FOR_BACK_ORDER, QTY_TO_ALLOCATE, AVAIL_IN_STOCK);
		handlePreBackOrderStockAllocationWhenInStockQtyIsGreaterThanAllocQty(AVAILABLE_FOR_PRE_ORDER, QTY_TO_ALLOCATE, AVAIL_IN_STOCK);
	}
	
	/**
	 * Tests insufficient in stock alloc qty for pre- and back-order available skus.
	 */
	@Test
	public void testHandlePreBackOrderStockAllocationWhenInStockQtyIsLessThanAllocQty() {
		handlePreBackOrderStockAllocationWhenInStockQtyIsLessThanAllocQty(AVAILABLE_FOR_BACK_ORDER, QTY_TO_ALLOCATE, AVAIL_IN_STOCK_INSUFFICIENT);
		handlePreBackOrderStockAllocationWhenInStockQtyIsLessThanAllocQty(AVAILABLE_FOR_PRE_ORDER, QTY_TO_ALLOCATE, AVAIL_IN_STOCK_INSUFFICIENT);
	}

	/**
	 * Tests in stock alloc for other than pre- or back-order availability type. Should just return qty.
	 */
	@Test
	public void testHandleStockAllocationWithOtherAvailabilityCriteria() {
		final int qty = 42;
		ProductSku sku = createProductSku(AVAILABLE_WHEN_IN_STOCK);
		assertEquals(qty, AVAILABLE_WHEN_IN_STOCK.handlePreBackOrderStockAllocation(pims, sku, 0L, qty));
		sku = createProductSku(ALWAYS_AVAILABLE);
		assertEquals(qty, ALWAYS_AVAILABLE.handlePreBackOrderStockAllocation(pims, sku, 0L, qty));
	}
	
	private void handlePreBackOrderStockAllocationWhenInStockQtyIsGreaterThanAllocQty(final AvailabilityCriteria availabilityCriteria, 
			final int quantity, final int availInStock) {
		final int qtyInStock = setUpStockAllocation(availabilityCriteria, quantity, availInStock);
		assertEquals(quantity, qtyInStock);
		
		
	}

	private void handlePreBackOrderStockAllocationWhenInStockQtyIsLessThanAllocQty(final AvailabilityCriteria availabilityCriteria, 
			final int quantity, final int availInStock) {
		final int qtyInStock = setUpStockAllocation(availabilityCriteria, quantity, availInStock);
		assertEquals(availInStock, qtyInStock);
		
		
	}
	
	private int setUpStockAllocation(final AvailabilityCriteria availabilityCriteria, final int quantity, final int availInStock) {
		final ProductSku sku = createProductSku(availabilityCriteria);
		final long warehouseUid = 1L;
		
		context.checking(new Expectations() { {
			allowing(pims).getAvailableInStockQty(with(sku), with(warehouseUid)); will(returnValue(availInStock));
		} });
		
		return availabilityCriteria.handlePreBackOrderStockAllocation(pims, sku, warehouseUid, quantity);
	}
	
	/**
	 * Tests sufficient unallocated qty for back- or pre-order available skus.  
	 */
	@Test
	public void testHasSufficientUnallocatedQtyForPreOrBackOrderWithNoCapabilities() {
		checkUnallocatedPreOrBackOrder(AVAILABLE_FOR_BACK_ORDER, SUFFICIENT_QTY, LIMIT, true, noCapabilities);
		checkUnallocatedPreOrBackOrder(AVAILABLE_FOR_BACK_ORDER, INSUFFICIENT_QTY, LIMIT, true, noCapabilities);
		checkUnallocatedPreOrBackOrder(AVAILABLE_FOR_PRE_ORDER, SUFFICIENT_QTY, LIMIT, true, noCapabilities);
		checkUnallocatedPreOrBackOrder(AVAILABLE_FOR_PRE_ORDER, INSUFFICIENT_QTY, LIMIT, true, noCapabilities);
	}

	@Test
	public void testHasSufficientUnallocatedQtyForPreOrBackOrder() {
		checkUnallocatedPreOrBackOrder(AVAILABLE_FOR_BACK_ORDER, SUFFICIENT_QTY, LIMIT, true, preOrBackOrderLimitCapabilities);
		checkUnallocatedPreOrBackOrder(AVAILABLE_FOR_BACK_ORDER, INSUFFICIENT_QTY, LIMIT, false, preOrBackOrderLimitCapabilities);
		checkUnallocatedPreOrBackOrder(AVAILABLE_FOR_PRE_ORDER, SUFFICIENT_QTY, LIMIT, true, preOrBackOrderLimitCapabilities);
		checkUnallocatedPreOrBackOrder(AVAILABLE_FOR_PRE_ORDER, INSUFFICIENT_QTY, LIMIT, false, preOrBackOrderLimitCapabilities);
	}
	
	/**
	 * Tests sufficient unallocated qty for back- or pre-order with no order limit skus.
	 */
	@Test
	public void testHasSufficientUnallocatedQtyForPreOrBackOrderWithNoOrderLimit() {
		checkUnallocatedPreOrBackOrder(AVAILABLE_FOR_BACK_ORDER, INSUFFICIENT_QTY, -1, true, noCapabilities);
		checkUnallocatedPreOrBackOrder(AVAILABLE_FOR_PRE_ORDER, INSUFFICIENT_QTY, -1, true, noCapabilities);
		
		checkUnallocatedPreOrBackOrder(AVAILABLE_FOR_BACK_ORDER, INSUFFICIENT_QTY, -1, true, preOrBackOrderLimitCapabilities);
		checkUnallocatedPreOrBackOrder(AVAILABLE_FOR_PRE_ORDER, INSUFFICIENT_QTY, -1, true, preOrBackOrderLimitCapabilities);
	}
	
	/**
	 * Tests sufficient unallocated qty for always available skus.
	 */
	@Test
	public void testHasSufficientUnallocatedQtyForAlwaysAwailable() {
		final ProductSku sku = createProductSku(ALWAYS_AVAILABLE);
		final long warehouseUid = 1L;
		final boolean sufficientUnalloc =  ALWAYS_AVAILABLE.hasSufficientUnallocatedQty(pims, sku, warehouseUid, 10);
		assertTrue(sufficientUnalloc);
	}
	
	/**
	 * Tests sufficient allocated qty for available while in stock skus.
	 */
	@Test
	public void testHasSufficientUnallocatedQtyForAvailableWhileInStock() {
		final ProductSku sku = createProductSku(AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK);
		final long warehouseUid = 1L;
		
		context.checking(new Expectations() { {
			oneOf(pims).hasSufficientInventory(sku, warehouseUid, LIMIT); will(returnValue(true));
			oneOf(pims).hasSufficientInventory(sku, warehouseUid, LIMIT); will(returnValue(false));
		} });
		
		boolean sufficientUnalloc =  AVAILABLE_WHEN_IN_STOCK.hasSufficientUnallocatedQty(pims, sku, warehouseUid, LIMIT);
		assertTrue(sufficientUnalloc);
		
		sufficientUnalloc =  AVAILABLE_WHEN_IN_STOCK.hasSufficientUnallocatedQty(pims, sku, warehouseUid, LIMIT);
		assertFalse(sufficientUnalloc);
		
	}
	
	private void checkUnallocatedPreOrBackOrder(final AvailabilityCriteria availabilityCriteria, final int quantity, final int limit, 
			final boolean expected, final Capabilities inventoryCapabilities) {
		final ProductSku sku = createProductSku(availabilityCriteria);
		final long warehouseUid = 1L;
		
		// Refresh the mocks because new returns values are required.
		context.checking(new Expectations() { {
			allowing(pims).getPreOrBackOrderDetails(sku.getSkuCode());
			will(returnValue(new PreOrBackOrderDetails(sku.getSkuCode(), limit, DETAILS_QTY)));

			allowing(pims).hasSufficientInventory(with(sku), with(warehouseUid), with(any(Integer.class)));
			will(returnValue(false));

			allowing(pims).getInventoryCapabilities();
			will(returnValue(inventoryCapabilities));
		} });
		
		final boolean sufficientUnalloc = availabilityCriteria.hasSufficientUnallocatedQty(pims, sku, warehouseUid, quantity);
		assertEquals(expected, sufficientUnalloc);

	}

	
	/**
	 * Tests the happy path for pre- or back-order allocation.
	 */
	@Test
	public void testHandlePreOrBackOrderAllocationHappyPath() {
		allocationHappyPath(AvailabilityCriteria.AVAILABLE_FOR_BACK_ORDER, ALLOC_BELOW_LIMIT, noCapabilities);
		allocationHappyPath(AvailabilityCriteria.AVAILABLE_FOR_PRE_ORDER, ALLOC_BELOW_LIMIT, noCapabilities);
		
		allocationHappyPath(AvailabilityCriteria.AVAILABLE_FOR_BACK_ORDER, ALLOC_BELOW_LIMIT, preOrBackOrderLimitCapabilities);
		allocationHappyPath(AvailabilityCriteria.AVAILABLE_FOR_PRE_ORDER, ALLOC_BELOW_LIMIT, preOrBackOrderLimitCapabilities);
	}
	
	/**
	 * Should pass when the pre-order allocation exceeds the order limit size because
     * there is no pre or back order limit support.
	 */
	@Test
	public void testHandlePreOrderAllocationExceedOrderLimitWithNoSupport() {
		allocationHappyPath(AVAILABLE_FOR_PRE_ORDER, ALLOC_ABOVE_LIMIT, noCapabilities);
	}

	/**
	 * Should fail with an exception when the pre-order allocation exceeds the order limit size.
	 */
	@Test(expected = InsufficientInventoryException.class)
	public void testHandlePreOrderAllocationExceedOrderLimitWithSupport() {
		allocationHappyPath(AVAILABLE_FOR_PRE_ORDER, ALLOC_ABOVE_LIMIT, new CapabilitiesImpl(InventoryCapabilities.PRE_OR_BACK_ORDER_LIMIT));
	}

	/**
	 * Should pass when the back-order allocation exceeds the order limit size because
     * there is no pre or back order limit support.
	 */
	@Test
	public void testHandleBackOrderAllocationExceedOrderLimitWithNoSupport() {
		allocationHappyPath(AVAILABLE_FOR_BACK_ORDER, ALLOC_ABOVE_LIMIT, noCapabilities);
	}

	/**
	 * Should fail with an exception when the back-order allocation exceeds the order limit size.
	 */
	@Test(expected = InsufficientInventoryException.class)
	public void testHandleBackOrderAllocationExceedOrderLimitWithSupport() {
		allocationHappyPath(AVAILABLE_FOR_BACK_ORDER, ALLOC_ABOVE_LIMIT, new CapabilitiesImpl(InventoryCapabilities.PRE_OR_BACK_ORDER_LIMIT));
	}

	/**
	 * Tests that allocation type other than pre- or back-order allocates zero skus.
	 */
	@Test
	public void testHandleOtherAllocationType() {
		final ProductSku sku = createProductSku(ALWAYS_AVAILABLE);
		final int allocationQty = ALWAYS_AVAILABLE.handlePreOrBackOrderAllocation(pims, sku, LIMIT);
		assertEquals(0, allocationQty);
		assertEquals(0, sku.getPreOrBackOrderedQuantity());
	}
	
	private void allocationHappyPath(final AvailabilityCriteria availabilityCriteria, final int quantity, final Capabilities inventoryCapabilities) {
		final ProductSku sku = createProductSku(availabilityCriteria);
	
		final PreOrBackOrderDetails details = new PreOrBackOrderDetails(sku.getSkuCode(), LIMIT, DETAILS_QTY);
		
		// Refresh the mocks because new returns values are required.
		context.checking(new Expectations() { {
			allowing(pims).getPreOrBackOrderDetails(sku.getSkuCode()); will(returnValue(details));
			allowing(pims).getInventoryCapabilities(); will(returnValue(inventoryCapabilities));
		} });
		
		final int allocationQty = availabilityCriteria.handlePreOrBackOrderAllocation(pims, sku, quantity);
		assertEquals(quantity, allocationQty);
		assertEquals(details.getQuantity() + quantity, sku.getPreOrBackOrderedQuantity());
		
	}

	private ProductSku createProductSku(final AvailabilityCriteria availabilityCriteria) {
		final ProductSku sku = new ProductSkuImpl();
		sku.setSkuCode("SKUCODE");
		final Product product = new ProductImpl();
		product.setAvailabilityCriteria(availabilityCriteria);
		sku.setProduct(product);
		return sku;
	}
	
	/**
	 * Tests the translation between allocation events and inventory type events.
	 */
	@Test
	public void testTranslateAllocationEventEnum() {
		InventoryEventType result = ORDER_PLACED.translateAllocationEvent(0);
		assertEquals(InventoryEventType.STOCK_ALLOCATE, result);
		
		result = ORDER_ADJUSTMENT_ADDSKU.translateAllocationEvent(0);
		assertEquals(InventoryEventType.STOCK_ALLOCATE, result);
		
		result = ORDER_CANCELLATION.translateAllocationEvent(0);
		assertEquals(InventoryEventType.STOCK_DEALLOCATE, result);
		
		result = ORDER_ADJUSTMENT_REMOVESKU.translateAllocationEvent(0);
		assertEquals(InventoryEventType.STOCK_DEALLOCATE, result);
		
		result = ORDER_SHIPMENT_COMPLETED.translateAllocationEvent(0);
		assertEquals(InventoryEventType.STOCK_RELEASE, result);

		result = ORDER_ADJUSTMENT_CHANGEQTY.translateAllocationEvent(0);
		assertEquals(InventoryEventType.STOCK_ALLOCATE, result);

		result = ORDER_ADJUSTMENT_CHANGEQTY.translateAllocationEvent(-1);
		assertEquals(InventoryEventType.STOCK_DEALLOCATE, result);
	}

}
