package com.elasticpath.tax;

import java.math.BigDecimal;
import java.util.Map;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.ElasticPath;
import com.elasticpath.domain.impl.ElasticPathImpl;
import com.elasticpath.domain.tax.TaxCategory;
import com.elasticpath.domain.tax.TaxCategoryTypeEnum;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.domain.tax.TaxJurisdiction;
import com.elasticpath.domain.tax.TaxRegion;
import com.elasticpath.domain.tax.TaxValue;

/**
 * Facade to help configure a tax jurisdiction. Using this facade will alleviate the need to work directly with tax category, tax region and tax
 * value domain objects.
 */
public class TaxJurisdictionFacade {

	private final ElasticPath elasticPath;

	private final TaxJurisdiction taxJurisdiction;

	/**
	 * Constructor for tax jurisdiction facade.
	 * 
	 * @param taxJurisdiction taxJurisdiction this facade will access
	 */
	@SuppressWarnings("PMD.DontUseElasticPathImplGetInstance")
	public TaxJurisdictionFacade(final TaxJurisdiction taxJurisdiction) {
		this.elasticPath = ElasticPathImpl.getInstance();
		this.taxJurisdiction = taxJurisdiction;
	}

	/**
	 * Returns the tax jurisdiction in this tax jurisdiction facade.
	 * 
	 * @return the tax jurisdiction
	 */
	public TaxJurisdiction getTaxJurisdiction() {
		return taxJurisdiction;
	}

	/**
	 * Associates the specified tax category name with the specified address field in the tax jurisdiction. If the tax category already exists, the
	 * old address field will be updated with the specified address field.
	 * 
	 * @param name name of the tax category
	 * @param taxCategoryType tax category match type
	 * @return the newly associated tax category or the existing updated tax category if tax category name already exists
	 */
	public TaxCategory putTaxCategory(final String name, final TaxCategoryTypeEnum taxCategoryType) {
		TaxCategory taxCategory = taxJurisdiction.getTaxCategory(name);
		if (taxCategory == null) {
			taxCategory = elasticPath.getBean(ContextIdNames.TAX_CATEGORY);
			taxCategory.setName(name);
			taxJurisdiction.addTaxCategory(taxCategory);
		}
		taxCategory.setFieldMatchType(taxCategoryType);
		return taxCategory;
	}

	/**
	 * Associates the specified region name in the specified tax category.
	 * 
	 * @param taxCategoryName tax category to associate the tax region to
	 * @param regionName region name of the tax region to associate
	 * @return the newly associated tax region or the existing tax region if tax region name already exists
	 */ 
	public TaxRegion putTaxRegion(final String taxCategoryName, final String regionName) {
		TaxCategory taxCategory = taxJurisdiction.getTaxCategory(taxCategoryName);
		if (taxCategory == null) {
			throw new IllegalArgumentException("taxCategoryName does not exist: " + taxCategoryName);
		}
		if (regionName == null) {
			throw new IllegalArgumentException("regionName must be a valid region code.");
		}
		TaxRegion taxRegion = taxCategory.getTaxRegion(regionName);
		if (taxRegion == null) {
			taxRegion = createTaxRegion(regionName);
			taxCategory.addTaxRegion(taxRegion);
		}
		return taxRegion;
	}

	/**
	 * Associates the specified tax code with the specified tax value in the specified region's tax category. Tax category and region must already
	 * exist.
	 * 
	 * @param taxCategoryName tax category to associate the tax value to
	 * @param regionName tax region to associate the tax value to
	 * @param taxValueNumber tax value number
	 * @param taxCode tax code
	 * @return the newly associated tax value or the existing updated tax value if tax value already exists
	 */
	public TaxValue putTaxValue(final String taxCategoryName, final String regionName, final TaxCode taxCode, final BigDecimal taxValueNumber) {
		TaxRegion taxRegion = putTaxRegion(taxCategoryName, regionName);
		TaxValue taxValue = taxRegion.getTaxValuesMap().get(taxCode);
		if (taxValue == null) {
			taxValue = createTaxValue(taxValueNumber, taxCode);
			addTaxValueToTaxRegion(taxValue, taxRegion);
		} else {
			taxValue.setTaxValue(taxValueNumber);
		}
		return taxValue;
	}

	private TaxRegion createTaxRegion(final String name) {
		TaxRegion taxRegion = elasticPath.getBean(ContextIdNames.TAX_REGION);
		taxRegion.setRegionName(name);
		return taxRegion;
	}

	private TaxValue createTaxValue(final BigDecimal taxValueNumber, final TaxCode taxCode) {
		TaxValue taxValue = elasticPath.getBean(ContextIdNames.TAX_VALUE);
		taxValue.setTaxCode(taxCode);
		taxValue.setTaxValue(taxValueNumber);
		return taxValue;
	}

	// method specifically created to easily add taxValue to taxRegion.
	// NOTE: TaxRegion should be refactored to have taxRegion store a set instead of a map for taxValues
	private void addTaxValueToTaxRegion(final TaxValue taxValue, final TaxRegion taxRegion) {
		Map<String, TaxValue> taxValuesMap = taxRegion.getTaxValuesMap();
		taxValuesMap.put(taxValue.getTaxCode().getCode(), taxValue);
		taxRegion.setTaxValuesMap(taxValuesMap);
	}
}
