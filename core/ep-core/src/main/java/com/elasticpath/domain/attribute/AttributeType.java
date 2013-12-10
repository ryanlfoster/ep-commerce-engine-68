package com.elasticpath.domain.attribute;

import java.util.Collection;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;

import com.elasticpath.commons.util.extenum.AbstractExtensibleEnum;


/**
 * Represents an attribute type.
 */
public class AttributeType extends AbstractExtensibleEnum<AttributeType> {

	private static final long serialVersionUID = -7167307554278632691L;

	/**
	 * Attribute type id for undefined.
	 */
	public static final int UNDEFINED_TYPE_ID = -1;
	
	/**
	 * Attribute type id for short text.
	 */
	public static final int SHORT_TEXT_TYPE_ID = 1;
	
	/**
	 * Text short attribute type contains 255 characters or less.
	 */
	public static final AttributeType SHORT_TEXT = new AttributeType(SHORT_TEXT_TYPE_ID, "SHORT_TEXT");
	
	/**
	 * Attribute type id for long text.
	 */
	public static final int LONG_TEXT_TYPE_ID = 2;

	/**
	 * Text long attribute type contains 2000 characters or less.
	 */
	public static final AttributeType LONG_TEXT = new AttributeType(LONG_TEXT_TYPE_ID, "LONG_TEXT");
	
	/**
	 * Attribute type id for integer.
	 */
	public static final int INTEGER_TYPE_ID = 3;

	/**
	 * The attribute type of a non-decimal number.
	 */
	public static final AttributeType INTEGER = new AttributeType(INTEGER_TYPE_ID, "INTEGER");
	
	/**
	 * Attribute type id for decimal.
	 */
	public static final int DECIMAL_TYPE_ID = 4;

	/**
	 * The attribute type of a decimal number.
	 */
	public static final AttributeType DECIMAL = new AttributeType(DECIMAL_TYPE_ID, "DECIMAL");
	
	/**
	 * Attribute type id for boolean.
	 */
	public static final int BOOLEAN_TYPE_ID = 5;

	/**
	 * The attribute type of boolean : true or false.
	 */
	public static final AttributeType BOOLEAN = new AttributeType(BOOLEAN_TYPE_ID, "BOOLEAN");
	
	/**
	 * Attribute type id for image.
	 */
	public static final int IMAGE_TYPE_ID = 7;

	/**
	 * The attribute type for any images for display.
	 */
	public static final AttributeType IMAGE = new AttributeType(IMAGE_TYPE_ID, "IMAGE", SHORT_TEXT.getStorageType());
	
	/**
	 * Attribute type id for file.
	 */
	public static final int FILE_TYPE_ID = 8;

	/**
	 * The attribute type for uploading files to the server.
	 */
	public static final AttributeType FILE = new AttributeType(FILE_TYPE_ID, "FILE", SHORT_TEXT.getStorageType());
	/**
	 * Attribute type id for data.
	 */
	public static final int DATE_TYPE_ID = 9;

	/**
	 * The attribute type for a date.
	 */
	public static final AttributeType DATE = new AttributeType(DATE_TYPE_ID, "DATE");
	
	/**
	 * Attribute type id for data time.
	 */
	public static final int DATETIME_TYPE_ID = 10;

	/**
	 * The attribute type for a date &amp; time.
	 */
	public static final AttributeType DATETIME = new AttributeType(DATETIME_TYPE_ID, "DATE_TIME");
	
	private static final AttributeType[] CUSTOMER_ATTRIBUTE_TYPES = { SHORT_TEXT, INTEGER, DECIMAL, BOOLEAN, DATE, DATETIME };

	private String storageType;

	private String nameMessageKey;
	
	/**
	 * Constructor for this extensible enum.
	 * @param ordinal the ordinal value
	 * @param name the named value
	 */
	protected AttributeType(final int ordinal, final String name) {
		this(ordinal, name, getStorageType(name));
	}
	
	/**
	 * Constructor for this extensible enum.
	 * @param ordinal the ordinal value
	 * @param name the named value
	 * @param storageType the storage type
	 */
	protected AttributeType(final int ordinal, final String name, final String storageType) {
		super(ordinal, name, AttributeType.class);
		this.storageType = storageType;
		this.nameMessageKey = getNameMessageKey(name); 
	}
	
	/**
	 * Find the enum value with the specified ordinal value.
	 * @param ordinal the ordinal value
	 * @return the enum value
	 */
	public static AttributeType valueOf(final int ordinal) {
		return valueOf(ordinal, AttributeType.class);
	}

	/**
	 * Find the enum value with the specified name.
	 * @param name the name
	 * @return the enum value
	 */
	public static AttributeType valueOf(final String name) {
		return valueOf(name, AttributeType.class);
	}

	/**
	 * Find all enum values for a particular enum type.
	 * @return the enum values
	 */
	public static Collection<AttributeType> values() {
		return values(AttributeType.class);
	}

	@Override
	protected Class<AttributeType> getEnumType() {
		return AttributeType.class;
	}

	/**
	 * Returns the attribute type Id.
	 * 
	 * @return the attribute type Id
	 */
	public int getTypeId() {
		return getOrdinal();
	}

	/**
	 * Returns a string representing the type/field used to store data for this attribute.
	 * For example, images (URLS) and short text attributes both store their
	 * values as shortTextValues despite being different attribute types.
	 * @return the string name of the field where this attribute's data is stored.
	 */
	public String getStorageType() {
		return storageType;
	}

	/**
	 * Returns the attribute type name message key.
	 *
	 * @return the attribute type name message key.
	 */
	public String getNameMessageKey() {
		return nameMessageKey;
	}

	/**
	 * Generate the storage type based on the name.
	 * @param name the enum name
	 * @return the storage type
	 */
	protected static String getStorageType(final String name) {
		final String camelCased = constantToCamelCase(name);
		
		// DungeonAndDragons --> dungeonAndDragons
		final String uncapitalizedCamelCased = StringUtils.uncapitalize(camelCased);
		
		return uncapitalizedCamelCased + "Value";
	}

	/**
	 * Convert a Java style constant to camel case.
	 * @param str the string to convert
	 * @return the string as camel case
	 */
	protected static String constantToCamelCase(final String str) {
		// DUNGEONS_AND_DRAGONS --> dungeons_and_dragons
		final String loweredCase = str.toLowerCase(Locale.ENGLISH);

		// dungeons_and_dragons --> Dungeons_And_Dragons
		final String camelCasedWithUnderscore = WordUtils.capitalizeFully(loweredCase, new char[] {'_'});

		// Dungeons_And_Dragons --> DungeonAndDragons
		return camelCasedWithUnderscore.replace("_", "");
	}

	/**
	 * Generate name message key from name.
	 * @param name the enum name
	 * @return the name message key from name
	 */
	protected static String getNameMessageKey(final String name) {
		return "AttributeType_" + constantToCamelCase(name);
	}
	
	@Override
	public String toString() {
		return getNameMessageKey();
	}
	
	public static AttributeType[] getCustomerAttributeTypes() {
		return CUSTOMER_ATTRIBUTE_TYPES.clone();
	}
}
