package com.elasticpath.ql.parser;


/**
 * The enum holds all EP QL fields along with user string representation.
 */
public enum EpQLField {
	/** attribute field. */
	ATTRIBUTE("AttributeName"),

	/** product SKU attribute field. */
	SKU_ATTRIBUTE("SkuAttributeName"),

	/** CatalogCode field. */
	CATALOG_CODE("CatalogCode"),

	/** CategoryCode field. */
	CATEGORY_CODE("CategoryCode"),

	/** CategoryName field. */
	CATEGORY_NAME("CategoryName"),

	/** ProductCode field. */
	PRODUCT_CODE("ProductCode"),

	/** SkuCode field. */
	SKU_CODE("SkuCode"),

	/** ProductName field. */
	PRODUCT_NAME("ProductName"),

	/** BrandCode field. */
	BRAND_CODE("BrandCode"),

	/** BrandName field. */
	BRAND_NAME("BrandName"),

	/** ProductState field. */
	PRODUCT_ACTIVE("ProductActive"),

	/** ProductPrice field. */
	PRODUCT_PRICE("Price"),

	/** ProductEndDate field. */
	PRODUCT_END_DATE("ProductEndDate"),

	/** ProductStartDate field. */
	PRODUCT_START_DATE("ProductStartDate"),

	/** Last time when product was modified. */
	PRODUCT_LAST_MODIFIED_DATE("LastModifiedDate"),

	/** StoreCode field. */
	STORE_CODE("StoreCode"),
	
	/** PromotionName field. */
	PROMOTION_NAME("PromotionName"),
	
	/** PromotionType field. */
	PROMOTION_TYPE("PromotionType"),
	
	/** State field. */
	STATE("State"),
	
	/** Price list name field. */
	PRICE_LIST_NAME("PriceListName"),
	
	/** Namespace field. */
	NAMESPACE("Namespace"),
	
	/** Context field. */
	CONTEXT("Context"),
	
	/** MetadataKey field. */
	METADATAKEY("MetadataKey"),
	
	/** Tax jurisdiction guid field. */
	TAX_JURISDICTION_CODE("TaxJurisdictionCode"),
	
	/** Tax jurisdiction region field. */
	TAX_JURISDICTION_REGION("TaxJurisdictionRegion"),
	
	/** CM User Role  field. */
	CMUSER_ROLE("Role");


	private String fieldName;

	/**
	 * The constructor.
	 * 
	 * @param fieldName ep ql field. E.g. examplefield in WHERE examplefield='value'
	 */
	EpQLField(final String fieldName) {
		this.fieldName = fieldName;
	}

	/**
	 * @return ep ql field name.
	 */
	public String getFieldName() {
		return fieldName;
	}

	/**
	 * Returns EpQLFields enum by field name.
	 * 
	 * @param epQLField ep ql field
	 * @return EpQLFields enum object.
	 */
	public static EpQLField getEpQLField(final String epQLField) {
		for (EpQLField field : EpQLField.values()) {
			if (field.getFieldName().equals(epQLField)) {
				return field;
			}
		}
		return null;
	}
}
