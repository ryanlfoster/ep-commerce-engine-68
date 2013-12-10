/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.commons.constants;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

/**
 * This enumeration represents definitions of possible types - 
 * either <code>AttributeValue</code> or <code>ParameterValue</code>.
 * 
 */
@SuppressWarnings({ "PMD.CyclomaticComplexity" })
@XmlEnum
public enum ValueTypeEnum {
    /** StringLong definition. */
	@XmlEnumValue("StringLong")
    StringLong("StringLong", IType.LONG_TEXT_TYPE_ID, "ValueType_StringLong"), //$NON-NLS-1$
    /** StringShort definition.  */
    @XmlEnumValue("StringShort")
    StringShort("StringShort", IType.SHORT_TEXT_TYPE_ID, "ValueType_StringShort"), //$NON-NLS-1$
    /** StringShortMultiValue definition. */
    @XmlEnumValue("StringShortMultiValue")
    StringShortMultiValue("StringShortMultiValue", IType.SHORT_TEXT_MULTI_VALUE_TYPE_ID, "ValueType_StringShortMultiValue"), //$NON-NLS-1$
    /** Boolean definition.*/
    @XmlEnumValue("Boolean")
    Boolean("Boolean", IType.BOOLEAN_TYPE_ID, "ValueType_Boolean"), //$NON-NLS-1$
    /** Decimal  definition.*/
    @XmlEnumValue("Decimal")
    Decimal("Decimal", IType.DECIMAL_TYPE_ID, "ValueType_Decimal"), //$NON-NLS-1$
    /** Integer  definition.*/
    @XmlEnumValue("Integer")
    Integer("Integer", IType.INTEGER_TYPE_ID, "ValueType_Integer"), //$NON-NLS-1$
    /** Image  definition.*/
    @XmlEnumValue("Image")
    Image("Image", IType.IMAGE_TYPE_ID, "ValueType_Image"), //$NON-NLS-1$
    /** File  definition.*/
    @XmlEnumValue("File")
    File("File", IType.FILE_TYPE_ID, "ValueType_File"), //$NON-NLS-1$
    /** Date  definition.*/
    @XmlEnumValue("Date")
    Date("Date", IType.DATE_TYPE_ID, "ValueType_Date"), //$NON-NLS-1$
    /** DateTime  definition.*/
    @XmlEnumValue("Datetime")
    Datetime("Datetime", IType.DATETIME_TYPE_ID, "ValueType_DateTime"), //$NON-NLS-1$
    /** Product  definition.*/
    @XmlEnumValue("Product")
    Product("Product", IType.PRODUCT_TYPE_ID, "ValueType_Product"), //$NON-NLS-1$
    /** Category  definition.*/
    @XmlEnumValue("Category")
    Category("Category", IType.CATEGORY_TYPE_ID, "ValueType_Category"), //$NON-NLS-1$    
    /** URL definition. */
    @XmlEnumValue("Url")
    Url("Url", IType.URL_TYPE_ID, "ValueType_Url"), //$NON-NLS-1$
    /** HTML type from content wrappers.*/
    @XmlEnumValue("HTML")
    HTML("HTML", IType.HTML_TYPE_ID, "ValueType_HTML"); //$NON-NLS-1$
	

    private final String name;

    private final int typeId;
    
    private final String messageResourceKey;

    /**
     * Default constructor.
     *
     * @param name - name of enumeration
     * @param typeId - typeId of enumeration
     * @param messageResourceKey message resource key
     */
    ValueTypeEnum(final String name, final int typeId, final String messageResourceKey) {
        this.name = name;
        this.typeId = typeId;
        this.messageResourceKey = messageResourceKey;
    }

    /**
     * Get ValueTypeEnum by String representation.
     * @param value String representation of ValueTypeEnum 
     * @return instance of ValueTypeEnum
     */
    public static ValueTypeEnum fromValue(final String value) {
    	for (ValueTypeEnum v : ValueTypeEnum.values()) {
    		// Ignore case will be more user friendly
            if (v.name.equalsIgnoreCase(value)) {  
                return v;
            }
        }
        throw new IllegalArgumentException(value);
    }

    /**
     * Return enumeration for the given type Id.
     *
     * @param typeId - typeId of the enumeration to be returned.
     * @return enumeration with given id
     */
    @SuppressWarnings({ "PMD.CyclomaticComplexity" })
    public static ValueTypeEnum get(final int typeId) {
        switch (typeId) {
        case (IType.LONG_TEXT_TYPE_ID):
            return StringLong;
        case (IType.SHORT_TEXT_TYPE_ID):
            return StringShort;
        case (IType.SHORT_TEXT_MULTI_VALUE_TYPE_ID):
            return StringShortMultiValue;
        case (IType.BOOLEAN_TYPE_ID):
            return Boolean;
        case (IType.DECIMAL_TYPE_ID):
            return Decimal;
        case (IType.INTEGER_TYPE_ID):
            return Integer;
        case (IType.IMAGE_TYPE_ID):
            return Image;
        case (IType.FILE_TYPE_ID):
            return File;
        case (IType.DATE_TYPE_ID):
            return Date;
        case (IType.DATETIME_TYPE_ID):
            return Datetime;
        case (IType.URL_TYPE_ID):
            return Url;
        case (IType.PRODUCT_TYPE_ID):
            return Product;
        case (IType.CATEGORY_TYPE_ID):
            return Category;
        case (IType.HTML_TYPE_ID):
            return HTML;        
        default:
            return StringShort;
        }
    }
    
    /**
     *  Return name. 
     *
     * @return name
     */
    public final String getName() {
        return name;
    }

    /**
     * Return type Id.
     *
     * @return type Id
     */
    public final int getTypeId() {
        return typeId;
    }

	/**
	 * Return the message resource key.
	 * @return the messageResourceKey
	 */
	public String getMessageResourceKey() {
		return messageResourceKey;
	}
}