package com.elasticpath.inventory.log.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.elasticpath.inventory.InventoryKey;

/**
 * Holds the information that needs to be logged. Inventory specific logging context. 
 * Contains information about the inventory command that is executed. 
 */
public class InventoryLogContext {
	
	/** Inventory rollup trace log message. */
	public static final String ROLLUP_TRACE_MSG = "INV002 InventoryJournal SKU/Warehouse rollup";

	/** Inventory rollup start log message. */
	public static final String ROLLUP_STARTED_MSG = "INV010 InventoryJournal rollup started...";

	/** Inventory rollup end log message. */
	public static final String ROLLUP_ENDED_MSG = "INV011 InventoryJournal rollup ended.";
	
	/** Inventory rollup contention log messages. */
	public static final String ROLLUP_CONTENTION_MSG = "INV012 InventoryJournal rollup contention, task terminated: ";
	
	/** Attribute name for order number. */
	public static final String ORDER_NUMBER = "orderNumber";
	
	/** Attribute name for comment. */
	public static final String COMMENT = "comment";

	/** Attribute name for reason. */
	public static final String REASON = "reason";

	/** Attribute name for event originator. */
	public static final String EVENT_ORIGINATOR = "eventOriginator";
	
	private final Map<String, Object> contextAttributes = new HashMap<String, Object>();
	
	private final InventoryKey inventoryKey;
	private String commandName;
	private int quantity;
	
	/**
	 * Constructor.
	 * 
	 * @param inventoryKey inventory key
	 * @param commandName command name
	 * @param quantity quantity
	 */
	public InventoryLogContext(final InventoryKey inventoryKey, final String commandName,
			final int quantity) {
		this.inventoryKey = inventoryKey;
		this.commandName = commandName;
		this.quantity = quantity;
	}
	
	/**
	 * Constructor.
	 * 
	 * @param inventoryKey inventory key
	 */
	public InventoryLogContext(final InventoryKey inventoryKey) {
		this.inventoryKey = inventoryKey;
	}
	
	/**
	 * Adds an attribute to the context.
	 * 
	 * @param name name of attribute
	 * @param value value of attribute
	 */
	public void addContextAttribute(final String name, final Object value) {
		contextAttributes.put(name, value);
	}
	
	/**
	 * @return attribute names as strings
	 */
	public Set<String> getAttributeNames() {
		return Collections.unmodifiableSet(contextAttributes.keySet());
	}
	
	/**
	 * Gets an attribute by it's name.
	 * @param name attribute name
	 * @return attribute object or null if not found by name
	 */
	public Object getAttribute(final String name) {
		return contextAttributes.get(name);
	}
	
	/**
	 * @return inventory key
	 */
	public InventoryKey getInventoryKey() {
		return inventoryKey;
	}

	/**
	 * @return command name
	 */
	public String getCommandName() {
		return commandName;
	}
	
	/**
	 * @return quantity
	 */
	public int getQuantity() {
		return quantity;
	}

	/**
	 * @return context attributes
	 */
	public Map<String, Object> getContextAttributes() {
		return contextAttributes;
	}
	
}
