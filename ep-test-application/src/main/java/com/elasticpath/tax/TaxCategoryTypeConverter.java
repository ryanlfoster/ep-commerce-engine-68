package com.elasticpath.tax;

import com.elasticpath.domain.tax.TaxCategoryTypeEnum;

/**
 * Utility class for converting a string to a tax category type.<br>
 * NOTE: TaxCategoryTypeEnum could instead be refactored to use the Strings below as the string references. Currently the string references are
 * misspelled and the type is actually stored as an int.
 */
public final class TaxCategoryTypeConverter {

	private TaxCategoryTypeConverter() {		
	}
	/** COUNTRY. */
	public static final String COUNTRY = "COUNTRY";

	/** SUBCOUNTRY. */
	public static final String SUBCOUNTRY = "SUBCOUNTRY";

	/** CITY. */
	public static final String CITY = "CITY";

	/** ZIP_POSTAL_CODE. */
	public static final String ZIP_POSTAL_CODE = "ZIP_POSTAL_CODE";

	/**
	 * Returns the TaxCategoryTypeEnum corresponding to the given taxCategoryTypeString.
	 * 
	 * @param taxCategoryTypeString the string representation of the taxCategoryType
	 * @return the TaxCategoryTypeEnum corresponding to the given addressField
	 */
	public static TaxCategoryTypeEnum getInstance(final String taxCategoryTypeString) {
		TaxCategoryTypeEnum categoryType;
		if (COUNTRY.equals(taxCategoryTypeString)) {
			categoryType = TaxCategoryTypeEnum.FIELD_MATCH_COUNTRY;
		} else if (SUBCOUNTRY.equals(taxCategoryTypeString)) {
			categoryType = TaxCategoryTypeEnum.FIELD_MATCH_SUBCOUNTRY;
		} else if (CITY.equals(taxCategoryTypeString)) {
			categoryType = TaxCategoryTypeEnum.FIELD_MATCH_CITY;
		} else if (ZIP_POSTAL_CODE.equals(taxCategoryTypeString)) {
			categoryType = TaxCategoryTypeEnum.FIELD_MATCH_ZIP_POSTAL_CODE;
		} else {
			throw new IllegalArgumentException("Invalid tax category type: " + taxCategoryTypeString);
		}
		return categoryType;
	}
}
