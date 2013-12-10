package com.elasticpath.test.persister;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.domain.tax.TaxJurisdiction;
import com.elasticpath.service.tax.TaxCodeService;
import com.elasticpath.service.tax.TaxJurisdictionService;
import com.elasticpath.tax.TaxCategoryTypeConverter;
import com.elasticpath.tax.TaxJurisdictionFacade;

/**
 * Persister allows to create and save into database tax related domain objects.
 */
public class TaxTestPersister {

	public static final String TAX_CODE_GOODS = "GOODS";

	public static final String TAX_CODE_SHIPPING = "SHIPPING";
	
	@Autowired
	private BeanFactory beanFactory;

	// Services for saving and updating persisted objects.
	@Autowired
	private TaxJurisdictionService taxJurisdictionService;
	
	@Autowired
	private TaxCodeService taxCodeService;

	/**
	 * Persist the tax jurisdictions for Canada.
	 */
	public void persistDefaultTaxJurisdictions() {

		TaxJurisdiction taxJurisdiction = persistTaxJurisdiction("CA", false);
		TaxJurisdictionFacade taxJurisdictionFacade = new TaxJurisdictionFacade(taxJurisdiction);
		taxJurisdictionFacade.putTaxCategory("GST", TaxCategoryTypeConverter.getInstance(TaxCategoryTypeConverter.COUNTRY));
		taxJurisdictionFacade.putTaxCategory("PST", TaxCategoryTypeConverter.getInstance(TaxCategoryTypeConverter.SUBCOUNTRY));

		taxJurisdictionFacade = new TaxJurisdictionFacade(persistTaxJurisdiction(taxJurisdictionFacade.getTaxJurisdiction()));

		taxJurisdictionFacade.putTaxRegion("GST", "CA");
		taxJurisdictionFacade.putTaxRegion("PST", "AB");
		taxJurisdictionFacade.putTaxRegion("PST", "BC");
		taxJurisdictionFacade.putTaxRegion("PST", "MB");
		taxJurisdictionFacade.putTaxRegion("PST", "NB");
		taxJurisdictionFacade.putTaxRegion("PST", "NL");
		taxJurisdictionFacade.putTaxRegion("PST", "NT");
		taxJurisdictionFacade.putTaxRegion("PST", "NS");
		taxJurisdictionFacade.putTaxRegion("PST", "NU");
		taxJurisdictionFacade.putTaxRegion("PST", "ON");
		taxJurisdictionFacade.putTaxRegion("PST", "PE");
		taxJurisdictionFacade.putTaxRegion("PST", "QC");
		taxJurisdictionFacade.putTaxRegion("PST", "SK");
		taxJurisdictionFacade.putTaxRegion("PST", "YT");

		final TaxCode goods = getTaxCode(TAX_CODE_GOODS);
		final TaxCode shipping = getTaxCode(TAX_CODE_SHIPPING);
		taxJurisdictionFacade.putTaxValue("GST", "CA", goods, new BigDecimal(5));
		taxJurisdictionFacade.putTaxValue("GST", "CA", shipping, new BigDecimal(5));
		taxJurisdictionFacade.putTaxValue("PST", "AB", goods, BigDecimal.ZERO);
		taxJurisdictionFacade.putTaxValue("PST", "AB", shipping, BigDecimal.ZERO);
		taxJurisdictionFacade.putTaxValue("PST", "BC", goods, new BigDecimal(7));
		taxJurisdictionFacade.putTaxValue("PST", "BC", shipping, new BigDecimal(7));
		taxJurisdictionFacade.putTaxValue("PST", "MB", goods, new BigDecimal(7));
		taxJurisdictionFacade.putTaxValue("PST", "MB", shipping, new BigDecimal(7));
		taxJurisdictionFacade.putTaxValue("PST", "NB", goods, new BigDecimal(8));
		taxJurisdictionFacade.putTaxValue("PST", "NB", shipping, new BigDecimal(8));
		taxJurisdictionFacade.putTaxValue("PST", "NL", goods, new BigDecimal(8));
		taxJurisdictionFacade.putTaxValue("PST", "NL", shipping, new BigDecimal(8));
		taxJurisdictionFacade.putTaxValue("PST", "NT", goods, new BigDecimal(8));
		taxJurisdictionFacade.putTaxValue("PST", "NT", shipping, new BigDecimal(8));
		taxJurisdictionFacade.putTaxValue("PST", "NS", goods, BigDecimal.ZERO);
		taxJurisdictionFacade.putTaxValue("PST", "NS", shipping, BigDecimal.ZERO);
		taxJurisdictionFacade.putTaxValue("PST", "NU", goods, new BigDecimal(8));
		taxJurisdictionFacade.putTaxValue("PST", "NU", shipping, new BigDecimal(8));
		taxJurisdictionFacade.putTaxValue("PST", "ON", goods, new BigDecimal(8));
		taxJurisdictionFacade.putTaxValue("PST", "ON", shipping, new BigDecimal(8));
		taxJurisdictionFacade.putTaxValue("PST", "PE", goods, new BigDecimal(1));
		taxJurisdictionFacade.putTaxValue("PST", "PE", shipping, new BigDecimal(1));
		taxJurisdictionFacade.putTaxValue("PST", "QC", goods, new BigDecimal(7.5));
		taxJurisdictionFacade.putTaxValue("PST", "QC", shipping, new BigDecimal(7.5));
		taxJurisdictionFacade.putTaxValue("PST", "SK", goods, new BigDecimal(7));
		taxJurisdictionFacade.putTaxValue("PST", "SK", shipping, new BigDecimal(7));
		taxJurisdictionFacade.putTaxValue("PST", "YT", goods, BigDecimal.ZERO);
		taxJurisdictionFacade.putTaxValue("PST", "YT", shipping, BigDecimal.ZERO);
		persistTaxJurisdiction(taxJurisdictionFacade.getTaxJurisdiction());
	}

	/**
	 * Create persisted tax jurisdiction.
	 * 
	 * @param regionCode
	 * @param inclusive
	 * @return persisted tax jurisdiction
	 */
	public TaxJurisdiction persistTaxJurisdiction(final String regionCode, final boolean inclusive) {
		TaxJurisdiction taxJurisdiction = beanFactory.getBean(ContextIdNames.TAX_JURISDICTION);
		taxJurisdiction.setRegionCode(regionCode);
		taxJurisdiction.setPriceCalculationMethod(inclusive);
		return persistTaxJurisdiction(taxJurisdiction);
	}

	/**
	 * Create persisted taxJurisdiction or update existing.
	 * 
	 * @param taxJurisdiction to be updated
	 * @return updated taxJurisdiction
	 */
	public TaxJurisdiction persistTaxJurisdiction(final TaxJurisdiction taxJurisdiction) {
		if (!taxJurisdiction.isPersisted()) {
			return taxJurisdictionService.add(taxJurisdiction);
		}
		return taxJurisdictionService.update(taxJurisdiction);
	}

	/**
	 * Create persisted tax code.
	 * 
	 * @param taxCodeString
	 * @return persisted tax code
	 */
	public TaxCode persistTaxCode(final String taxCodeString) {
		TaxCode taxCode = beanFactory.getBean(ContextIdNames.TAX_CODE);
		taxCode.setCode(taxCodeString);
		return taxCodeService.add(taxCode);
	}

	/**
	 * Find the first taxJurisdiction with the given regionCode.
	 * 
	 * @param regionCode
	 * @return taxJurisdiction
	 */
	public TaxJurisdiction getTaxJurisdiction(final String regionCode) {
		TaxJurisdiction taxJurisdiction = null;
		for (TaxJurisdiction currentTaxJurisdiction : taxJurisdictionService.list()) {
			if (currentTaxJurisdiction.getRegionCode().equals(regionCode)) {
				taxJurisdiction = currentTaxJurisdiction;
				break;
			}
		}
		return taxJurisdiction;
	}

	/**
	 * Find persisted taxCode instance by its code.
	 * 
	 * @param taxCodeString
	 * @return taxCode
	 */
	public TaxCode getTaxCode(final String taxCodeString) {
		return taxCodeService.findByCode(taxCodeString);
	}
}
