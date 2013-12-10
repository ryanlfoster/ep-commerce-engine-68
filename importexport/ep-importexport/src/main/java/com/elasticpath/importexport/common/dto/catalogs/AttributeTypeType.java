package com.elasticpath.importexport.common.dto.catalogs;

import com.elasticpath.domain.attribute.AttributeType;

/**
 * The AttributeTypeType. Is Used in DTO.
 */
public enum AttributeTypeType {

	/**
	 * Attribute type for short text.
	 */
	ShortText {
		@Override
		public AttributeType type() {
			return AttributeType.SHORT_TEXT;
		}
	},

	/**
	 * Attribute type for long text.
	 */
	LongText {
		@Override
		public AttributeType type() {
			return AttributeType.LONG_TEXT;
		}
	},

	/**
	 * Attribute type for integer.
	 */
	Integer {
		@Override
		public AttributeType type() {
			return AttributeType.INTEGER;
		}
	},

	/**
	 * Attribute type for decimal.
	 */
	Decimal {
		@Override
		public AttributeType type() {
			return AttributeType.DECIMAL;
		}
	},

	/**
	 * Attribute type for boolean.
	 */
	Boolean {
		@Override
		public AttributeType type() {
			return AttributeType.BOOLEAN;
		}
	},

	/**
	 * Attribute type for image.
	 */
	Image {
		@Override
		public AttributeType type() {
			return AttributeType.IMAGE;
		}
	},

	/**
	 * Attribute type for file.
	 */
	File {
		@Override
		public AttributeType type() {
			return AttributeType.FILE;
		}
	},

	/**
	 * Attribute type for data.
	 */
	Date {
		@Override
		public AttributeType type() {
			return AttributeType.DATE;
		}
	},

	/**
	 * Attribute type for data time.
	 */
	DateTime {
		@Override
		public AttributeType type() {
			return AttributeType.DATETIME;
		}
	};

	/**
	 * Returns AttributeType for current type. 
	 * 
	 * @return AttributeType
	 */
	public abstract AttributeType type();
	
	/**
	 * Converts AttributeType to AttributeTypeType. 
	 * Is used part of string AttributeType name. 
	 * 
	 * @param type AttributeType
	 * @return AttributeTypeType
	 */
	public static AttributeTypeType valueOf(final AttributeType type) {
		return valueOf(type.toString().substring("AttributeType_".length()));
	}
}
