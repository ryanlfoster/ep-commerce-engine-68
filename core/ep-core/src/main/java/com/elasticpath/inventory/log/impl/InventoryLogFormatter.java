package com.elasticpath.inventory.log.impl;

import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.inventory.InventoryKey;

/**
 * Inventory log formatter. Formats information to be logged according to the specific log format. 
 */
public class InventoryLogFormatter {

	/**
	 * Formats the log message according to specified format.
	 * 
	 * @param message log message
	 * @param logContext log context
	 * @return a formatted string
	 */
	public String format(final String message, final InventoryLogContext logContext) {
		LogEntryFormatBuilder entryBuilder = LogEntryFormatBuilder.getInstance();
		entryBuilder.addLogMessage(message);
		if (logContext != null) {
			entryBuilder.addInventoryKey(logContext.getInventoryKey())
				.addOrderNumber(logContext.getAttribute(InventoryLogContext.ORDER_NUMBER))
				.addOriginator(logContext.getAttribute(InventoryLogContext.EVENT_ORIGINATOR))
				.addComment(logContext.getAttribute(InventoryLogContext.COMMENT))
				.addCommandInfo(logContext.getCommandName(), logContext.getQuantity())
				.addReason(logContext.getAttribute(InventoryLogContext.REASON))
				.addContextAttributes(logContext.getContextAttributes());
		}
		return entryBuilder.buildLogString();
	}
	
	/**
	 * Formats the log message according to specified format.
	 * 
	 * @param message log message
	 * @return a formatted string
	 */
	public String format(final String message) {
		return format(message, null);
	}
	
	/**
	 * Builds a formatted log string depending on which information has been provided to it. 
	 */
	protected static class LogEntryFormatBuilder {
		
		/** Left square bracket. */
		protected static final String L_SQ_BRACKET = "[";
		/** Right square bracket. */
		protected static final String R_SQ_BRACKET = "]";
		/** Left parentheses. */
		protected static final String L_BRACKET = "(";
		/** Right parentheses. */
		protected static final String R_BRACKET = ")";
		/** Space. */
		protected static final String SPACE = " ";
		/** Comma. */
		protected static final String COMMA = ",";
		
		private String message = StringUtils.EMPTY;
		private String skuCode = StringUtils.EMPTY;
		private String warehouseId = StringUtils.EMPTY;
		private String orderNumber = StringUtils.EMPTY;
		private String originator = StringUtils.EMPTY;
		private String comment = StringUtils.EMPTY;
		private String commandName = StringUtils.EMPTY;
		private String quantity = String.valueOf(0);
		private String reason = StringUtils.EMPTY;
		private final Map<String, Object> contextAttributes = new TreeMap<String, Object>();
		
		/**
		 * Creation method.
		 * 
		 * @return new instance of the builder
		 */
		static LogEntryFormatBuilder getInstance() {
			return new LogEntryFormatBuilder();
		}
		
		/**
		 * Adds a message to the log entry.
		 * 
		 * @param logMessage log message
		 * @return current builder instance
		 */
		public LogEntryFormatBuilder addLogMessage(final String logMessage) {
			this.message = logMessage;
			return this;
		}
		
		/**
		 * Adds an inventory key information to the log entry.
		 * 
		 * @param key inventory key
		 * @return current builder instance
		 */
		public LogEntryFormatBuilder addInventoryKey(final InventoryKey key) {
			this.skuCode = key.getSkuCode();
			this.warehouseId = String.valueOf(key.getWarehouseUid());
			return this;
		}
		
		/**
		 * Adds an order number to the log entry.
		 * 
		 * @param orderNumber order number
		 * @return current builder instance
		 */
		public LogEntryFormatBuilder addOrderNumber(final Object orderNumber) {
			if (orderNumber != null) {
				this.orderNumber = orderNumber.toString();
			}
			return this;
		}
		
		/**
		 * Adds an originator information to the log entry.
		 * 
		 * @param originator event originator
		 * @return current builder instance
		 */
		public LogEntryFormatBuilder addOriginator(final Object originator) {
			if (originator != null) {
				this.originator = originator.toString();
			}
			return this;
		}
		
		/**
		 * Adds a comment to the log entry.
		 * 
		 * @param comment comment
		 * @return current builder instance
		 */
		public LogEntryFormatBuilder addComment(final Object comment) {
			if (comment != null) {
				this.comment = comment.toString();
			}
			return this;
		}
		
		/**
		 * Adds a reason to the log entry.
		 * 
		 * @param reason the reason
		 * @return current builder instance
		 */
		public LogEntryFormatBuilder addReason(final Object reason) {
			if (reason != null) {
				this.reason = reason.toString();
			}
			return this;
		}
		
		/**
		 * Adds a command name and quantity to process to the log entry.
		 * 
		 * @param commandName command name
		 * @param quantity quantity to process
		 * @return current builder instance
		 */
		public LogEntryFormatBuilder addCommandInfo(final String commandName, final int quantity) {
			if (commandName != null) {
				this.commandName = commandName;
				this.quantity = String.valueOf(quantity);
			}
			return this;
		}
		
		/**
		 * Adds context attributes to the log entry. Predefined log context attributes (order number, event originator and comment are not accepted 
		 * by this method and are filtered out. you need to set them explicitly using the provided methods).
		 * 
		 * @param contextAttributes log context attributes
		 * @return current builder instance
		 */
		public LogEntryFormatBuilder addContextAttributes(final Map<String, Object> contextAttributes) {
			for (String key : contextAttributes.keySet()) {
				if (isPredefinedContextAttribute(key)) {
					continue;
				}
				getContextAttributes().put(key, contextAttributes.get(key));
			}
			return this;
		}

		/**
		 * Determines if the provided key belongs to predefined log context attributes.
		 * 
		 * @param key attribute key
		 * @return true if belongs, otherwise false
		 */
		protected boolean isPredefinedContextAttribute(final String key) {
			return InventoryLogContext.COMMENT.equals(key) 
					|| InventoryLogContext.ORDER_NUMBER.equals(key)
					|| InventoryLogContext.EVENT_ORIGINATOR.equals(key)
					|| InventoryLogContext.REASON.equals(key);
		}
		
		/**
		 * Builds the whole log entry string.
		 * 
		 * @return a formatted log string
		 */
		public String buildLogString() {
			StringBuffer logEntry = new StringBuffer();
			logEntry = buildMessagePart(logEntry);
			logEntry = buildContextPart(logEntry);
			logEntry = buildCommentPart(logEntry);
			logEntry = buildReasonPart(logEntry);
			return logEntry.toString();
		}

		/**
		 * Builds the message part of the log string.
		 * 
		 * @param logEntry log string being built
		 * @return log string being built with appended message part
		 */
		protected StringBuffer buildMessagePart(final StringBuffer logEntry) {
			StringBuffer messagePart = logEntry;
			if (StringUtils.isNotBlank(getMessage())) {
				messagePart.append(String.valueOf(L_SQ_BRACKET + getMessage()));
				messagePart = buildCommandInfoPart(messagePart);
				messagePart.append(R_SQ_BRACKET);
			}
			return messagePart;
		}

		/**
		 * Builds the command info part of the log string.
		 * 
		 * @param logEntry log string being built
		 * @return log string being built with appended command info part
		 */
		protected StringBuffer buildCommandInfoPart(final StringBuffer logEntry) {
			if (StringUtils.isNotBlank(getCommandName())) {
				logEntry.append(String.valueOf(SPACE + "Command=" + getCommandName() + COMMA + " Qty=" + getQuantity()));
			}
			return logEntry;
		}
		
		/**
		 * Builds the context part of the log string.
		 * 
		 * @param logEntry log string being built
		 * @return log string being built with appended context part
		 */
		protected StringBuffer buildContextPart(final StringBuffer logEntry) {
			if (StringUtils.isNotBlank(getSkuCode()) || StringUtils.isNotBlank(getWarehouseId())) {
				logEntry.append(SPACE + "CONTEXT" + L_BRACKET);
				if (StringUtils.isNotBlank(getSkuCode())) {
					logEntry.append(String.valueOf("Sku=" + getSkuCode()));
				}
				if (StringUtils.isNotBlank(getWarehouseId())) {
					logEntry.append(String.valueOf(COMMA + SPACE + "Warehouse=" + getWarehouseId()));
				}
				if (StringUtils.isNotBlank(getOrderNumber())) {
					logEntry.append(String.valueOf(COMMA + SPACE + "Order=" + getOrderNumber()));
				}
				if (StringUtils.isNotBlank(getOriginator())) {
					logEntry.append(String.valueOf(COMMA + SPACE + "Originator=" + getOriginator()));
				}
				for (String key : getContextAttributes().keySet()) {
					logEntry.append(String.valueOf(COMMA + SPACE + key + "=" + getContextAttributes().get(key)));
				}
				logEntry.append(R_BRACKET);
			}
			return logEntry;
		}

		/**
		 * Builds the reason part of the log string.
		 * 
		 * @param logEntry log string being built
		 * @return log string being built with appended comment part
		 */
		protected StringBuffer buildReasonPart(final StringBuffer logEntry) {
			if (StringUtils.isNotBlank(getReason())) {
				logEntry.append(String.valueOf(SPACE + "REASON" + L_BRACKET + getReason() + R_BRACKET));
			}
			return logEntry;
		}
		
		/**
		 * Builds the comment part of the log string.
		 * 
		 * @param logEntry log string being built
		 * @return log string being built with appended comment part
		 */
		protected StringBuffer buildCommentPart(final StringBuffer logEntry) {
			if (StringUtils.isNotBlank(getComment())) {
				logEntry.append(String.valueOf(SPACE + "COMMENT" + L_BRACKET + getComment() + R_BRACKET));
			}
			return logEntry;
		}

		/** @return message */
		protected String getMessage() {
			return message;
		}
		
		/** @return reason */
		protected String getReason() {
			return reason;
		}
		
		/** @return sku code */
		protected String getSkuCode() {
			return skuCode;
		}
		
		/** @return warehouse ID */
		protected String getWarehouseId() {
			return warehouseId;
		}

		/** @return order number */
		protected String getOrderNumber() {
			return orderNumber;
		}

		/** @return originator */
		protected String getOriginator() {
			return originator;
		}

		/** @return comment */
		protected String getComment() {
			return comment;
		}

		/** @return command name */
		protected String getCommandName() {
			return commandName;
		}

		/** @return quantity */
		protected String getQuantity() {
			return quantity;
		}

		/** @return context attributes */
		protected Map<String, Object> getContextAttributes() {
			return contextAttributes;
		}
	}
	
}
