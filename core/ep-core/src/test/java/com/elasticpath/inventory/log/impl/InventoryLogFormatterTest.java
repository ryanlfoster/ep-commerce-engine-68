package com.elasticpath.inventory.log.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.elasticpath.inventory.InventoryKey;

/**
 * Tests for the inventory log formatter. 
 */
public class InventoryLogFormatterTest {

	private static final String CONTEXT_FORMAT_PART = "] CONTEXT(Sku=";
	private static final String WAREHOUSE_FORMAT_PART = ", Warehouse=";
	private static final String FORMATTED_LOG_ENTRY_DOESN_T_MATCH_THE_EXPECTED_RESULT = 
		"Formatted log entry doesn't match the expected result";
	private static final int QTY = 100500;
	private static final String COMMAND_NAME = "adjust_legacy";
	private static final long WAREHOUSE = 42L;
	private static final String SKU_CODE = "skuCode";
	private static final long ORDER_NUMBER = 64L;
	private static final String ORIGINATOR = "productInventoryManagementService";
	private static final String MESSAGE = "INV002 Inventory rollup started.";
	private static final String COMMENT = "Testing inventory logging here";
	private static final String REASON = "Reason for adjustment";

	private final InventoryLogFormatter formatter = new InventoryLogFormatter();
	
	/**
	 * The simplest case. Just format the message since no context is provided.
	 * 
	 * Log string: [INV002 Inventory rollup started].
	 */
	@Test
	public void testWithNullContext() {
		final String logEntry = "[" + MESSAGE + "]";
		
		String result = formatter.format(MESSAGE, null);
		assertEquals(FORMATTED_LOG_ENTRY_DOESN_T_MATCH_THE_EXPECTED_RESULT, logEntry, result);
		
		result = formatter.format(MESSAGE, null);
		assertEquals(FORMATTED_LOG_ENTRY_DOESN_T_MATCH_THE_EXPECTED_RESULT, logEntry, result);
		
		result = formatter.format(MESSAGE);
		assertEquals(FORMATTED_LOG_ENTRY_DOESN_T_MATCH_THE_EXPECTED_RESULT, logEntry, result);
	}
	
	/**
	 * In this case we display a message and minimal context info - sku code and warehouse ID.
	 * 
	 * Log string: [INV002 Inventory rollup started] CONTEXT(Sku=skuCode, Warehouse=42).
	 */
	@Test
	public void testWithoutCommandInfoAndWithMinimalContext() {
		final InventoryKey key = new InventoryKey(SKU_CODE, WAREHOUSE);
		final InventoryLogContext logContext = new InventoryLogContext(key);
		final String logEntry = "[" + MESSAGE + CONTEXT_FORMAT_PART + SKU_CODE + WAREHOUSE_FORMAT_PART + WAREHOUSE + ")";
		
		String result = formatter.format(MESSAGE, logContext); 
		assertEquals(FORMATTED_LOG_ENTRY_DOESN_T_MATCH_THE_EXPECTED_RESULT, logEntry, result);
	}
	
	/**
	 * Here we display all message, predefined context information first without a comment.
	 * 
	 * Log string(1): [INV002 Inventory rollup started] CONTEXT(Sku=skuCode, Warehouse=42, Order=64, Originator=productInventoryManagementService)
	 */
	@Test
	public void testWithoutCommandInfoAndWithPredefinedContext() {
		final InventoryKey key = new InventoryKey(SKU_CODE, WAREHOUSE);
		final InventoryLogContext logContext = new InventoryLogContext(key);
		logContext.addContextAttribute(InventoryLogContext.ORDER_NUMBER, ORDER_NUMBER);
		logContext.addContextAttribute(InventoryLogContext.EVENT_ORIGINATOR, ORIGINATOR);
		final String logEntryNoComment = "[" + MESSAGE + CONTEXT_FORMAT_PART + SKU_CODE + WAREHOUSE_FORMAT_PART + WAREHOUSE 
			+ ", Order=" + ORDER_NUMBER +  ", Originator=" + ORIGINATOR + ")";
		
		String result = formatter.format(MESSAGE, logContext); 
		assertEquals(FORMATTED_LOG_ENTRY_DOESN_T_MATCH_THE_EXPECTED_RESULT, logEntryNoComment, result);
	}
	
	/**
	 * Here we display all message, predefined context information with a comment.
	 * 
	 * Log string(1): [INV002 Inventory rollup started] CONTEXT(Sku=skuCode, Warehouse=42, Order=64, Originator=productInventoryManagementService) 
	 * 				  COMMENT(Testing inventory logging here).
	 */
	@Test
	public void testWithoutCommandInfoAndWithPredefinedContextAndComment() {
		final InventoryKey key = new InventoryKey(SKU_CODE, WAREHOUSE);
		final InventoryLogContext logContext = new InventoryLogContext(key);
		logContext.addContextAttribute(InventoryLogContext.ORDER_NUMBER, ORDER_NUMBER);
		logContext.addContextAttribute(InventoryLogContext.EVENT_ORIGINATOR, ORIGINATOR);
		
		final String logEntryWithComment = "[" + MESSAGE + CONTEXT_FORMAT_PART + SKU_CODE + WAREHOUSE_FORMAT_PART + WAREHOUSE 
		+ ", Order=" + ORDER_NUMBER +  ", Originator=" + ORIGINATOR + ") COMMENT(" + COMMENT + ")";
		logContext.addContextAttribute(InventoryLogContext.COMMENT, COMMENT);
		
		//with comment
		String result = formatter.format(MESSAGE, logContext); 
		assertEquals(FORMATTED_LOG_ENTRY_DOESN_T_MATCH_THE_EXPECTED_RESULT, logEntryWithComment, result);
	}

	/**
	 * Here we display all message, predefined context information with a reason.
	 * 
	 * Log string(1): [INV002 Inventory rollup started.] CONTEXT(Sku=skuCode, Warehouse=42, Order=64, Originator=productInventoryManagementService) 
	 * 					REASON(Reason for adjustment).
	 */
	@Test
	public void testWithoutCommandInfoAndWithPredefinedContextAndReason() {
		final InventoryKey key = new InventoryKey(SKU_CODE, WAREHOUSE);
		final InventoryLogContext logContext = new InventoryLogContext(key);
		logContext.addContextAttribute(InventoryLogContext.ORDER_NUMBER, ORDER_NUMBER);
		logContext.addContextAttribute(InventoryLogContext.EVENT_ORIGINATOR, ORIGINATOR);
		
		final String logEntryWithReason = "[" + MESSAGE + CONTEXT_FORMAT_PART + SKU_CODE + WAREHOUSE_FORMAT_PART + WAREHOUSE 
		+ ", Order=" + ORDER_NUMBER +  ", Originator=" + ORIGINATOR + ") REASON(" + REASON + ")";
		logContext.addContextAttribute(InventoryLogContext.REASON, REASON);
		
		//with comment
		String result = formatter.format(MESSAGE, logContext); 
		assertEquals(logEntryWithReason, logEntryWithReason, result);
	}
	
	/**
	 * In this case we display a message and minimal context info - sku code and warehouse ID.
	 * 
	 * Log string: [INV002 Inventory rollup started Command=adjust_legacy, Qty=100500] CONTEXT(Sku=skuCode, Warehouse=42).
	 */
	@Test
	public void testWithCommandInfoAndWithMinimalContext() {
		final InventoryKey key = new InventoryKey(SKU_CODE, WAREHOUSE);
		final InventoryLogContext logContext = new InventoryLogContext(key, COMMAND_NAME, QTY);
		final String logEntry = "[" + MESSAGE + " Command=" + COMMAND_NAME + ", Qty=" + QTY + CONTEXT_FORMAT_PART 
			+ SKU_CODE + WAREHOUSE_FORMAT_PART + WAREHOUSE + ")";
		
		String result = formatter.format(MESSAGE, logContext); 
		assertEquals(FORMATTED_LOG_ENTRY_DOESN_T_MATCH_THE_EXPECTED_RESULT, logEntry, result);
	}
	
	/**
	 * In this case we display a message and minimal context info - sku code and warehouse ID. Also the context contains a set of arbitrary attributes
	 * that should be displayed in the context section in the ascending order (ordered by names).
	 * 
	 * Log string: [INV002 Inventory rollup started Command=adjust_legacy, Qty=100500] CONTEXT(Sku=skuCode, Warehouse=42, a2=v2, A1=some other 
	 * stuff, b1=777).
	 */
	@Test
	public void testWithCommandInfoAndWithMinimalContextContainingArbitraryAttributes() {
		final InventoryKey key = new InventoryKey(SKU_CODE, WAREHOUSE);
		final InventoryLogContext logContext = new InventoryLogContext(key, COMMAND_NAME, QTY);
		final String logEntry = "[" + MESSAGE + " Command=" + COMMAND_NAME + ", Qty=" + QTY + CONTEXT_FORMAT_PART 
			+ SKU_CODE + WAREHOUSE_FORMAT_PART + WAREHOUSE + ", A1=some other stuff, a2=v2, b1=777)";
		
		final int valA1 = 777; 
		logContext.addContextAttribute("a2", "v2");
		logContext.addContextAttribute("b1", valA1);
		logContext.addContextAttribute("A1", "some other stuff");
		
		String result = formatter.format(MESSAGE, logContext); 
		assertEquals(FORMATTED_LOG_ENTRY_DOESN_T_MATCH_THE_EXPECTED_RESULT, logEntry, result);
	}
}
