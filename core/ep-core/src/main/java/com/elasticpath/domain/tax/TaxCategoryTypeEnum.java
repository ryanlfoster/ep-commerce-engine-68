package com.elasticpath.domain.tax;

/**
 * TaxCategoryTypeEnum enumerates all types of tax category.
 */
public enum TaxCategoryTypeEnum {

	/**
	 * field match type: country.
	 */
	FIELD_MATCH_COUNTRY("TaxCategotyType_Country", 0),

	/**
	 * field match type: subcountry.
	 */
	FIELD_MATCH_SUBCOUNTRY("TaxCategotyType_Subcountry", 1),

	/**
	 * field match type: city.
	 */
	FIELD_MATCH_CITY("TaxCategotyType_City", 2),

	/**
	 * field match type: zip/postal code.
	 */
	FIELD_MATCH_ZIP_POSTAL_CODE("TaxCategotyType_Zip", 3);

	private static final int COUNTRY_INT_VALUE = 0;

	private static final int SUB_COUNTRY_INT_VALUE = 1;

	private static final int CITY_INT_VALUE = 2;

	private static final int ZIP_INT_VALUE = 3;

	private String name;

	private int intValue = 0;

	private TaxCategoryTypeEnum(final String name, final int value) {
		this.name = name;
		this.intValue = value;
	}

	/**
	 * Required for JPA mapping only.
	 * 
	 * @param value integer value representation
	 * @return particular TaxCategoryTypeEnum or null if int mapping not found.
	 */
	public static TaxCategoryTypeEnum getInstance(final int value) {
		switch (value) {
		case COUNTRY_INT_VALUE:
			return FIELD_MATCH_COUNTRY;
		case SUB_COUNTRY_INT_VALUE:
			return FIELD_MATCH_SUBCOUNTRY;
		case CITY_INT_VALUE:
			return FIELD_MATCH_CITY;
		case ZIP_INT_VALUE:
			return FIELD_MATCH_ZIP_POSTAL_CODE;
		default:
			return null;
		}
	}

	/**
	 * Required for JPA mapping only.
	 * 
	 * @return value integer value representation
	 */
	public int getIntValue() {
		return intValue;
	}

	/**
	 * Get name of this category type.
	 * 
	 * @return the name of the category
	 */
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return getName();
	}
}
