package com.elasticpath.common.dto.tax;

import com.elasticpath.common.dto.PropertyDTO;
import com.elasticpath.common.dto.assembler.AbstractDtoAssembler;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.misc.LocalizedPropertyValue;
import com.elasticpath.domain.tax.TaxCategory;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.domain.tax.TaxJurisdiction;
import com.elasticpath.domain.tax.TaxRegion;
import com.elasticpath.domain.tax.TaxValue;
import com.elasticpath.service.tax.TaxCodeService;

/**
 * Maps the DTO onto the domain object and back again. When mapping onto the domain object, this assembler will update existing elements and insert
 * new ones.
 */
public class TaxJurisdictionDtoAssembler extends AbstractDtoAssembler<TaxJurisdictionDTO, TaxJurisdiction> {

	private BeanFactory beanFactory;

	private TaxCodeService taxCodeService;

	@Override
	public TaxJurisdiction getDomainInstance() {
		return beanFactory.getBean(ContextIdNames.TAX_JURISDICTION);
	}

	@Override
	public TaxJurisdictionDTO getDtoInstance() {
		return new TaxJurisdictionDTO();
	}

	/**
	 * Overridable factory method.
	 * 
	 * @return a TaxRegionDTO.
	 */
	protected TaxRegionDTO taxRegionDtoFactory() {
		return new TaxRegionDTO();
	}

	/**
	 * Overridable factory method.
	 * 
	 * @return a TaxCategoryDTO.
	 */
	protected TaxCategoryDTO taxCategoryDtoFactory() {
		return new TaxCategoryDTO();
	}

	/**
	 * Overridable factory method.
	 * 
	 * @return a TaxValueDTO.
	 */
	protected TaxValueDTO taxValueDtoFactory() {
		return new TaxValueDTO();
	}

	/**
	 * Overridable factory method.
	 * 
	 * @return a PropertyDTO.
	 */
	protected PropertyDTO localizedPropertyDtoFactory() {
		return new PropertyDTO();
	}

	/**
	 * Overridable factory method.
	 * 
	 * @return a TaxValue from the Spring context.
	 */
	protected TaxValue taxValueDomainFactory() {
		return beanFactory.getBean(ContextIdNames.TAX_VALUE);
	}

	/**
	 * Overridable factory method.
	 * 
	 * @return a TaxCategory from the Spring context.
	 */
	protected TaxCategory taxCategoryDomainFactory() {
		return beanFactory.getBean(ContextIdNames.TAX_CATEGORY);
	}

	/**
	 * Overridable factory method.
	 * 
	 * @return a TaxRegion from the Spring context.
	 */
	protected TaxRegion taxRegionDomainFactory() {
		return beanFactory.getBean(ContextIdNames.TAX_REGION);
	}

	/**
	 * Overridable factory method.
	 * 
	 * @return a (Tax Category) LocalizedPropertyValue from the Spring Context.
	 */
	protected LocalizedPropertyValue localizedPropertyValueDomainFactory() {
		return beanFactory.getBean(ContextIdNames.TAX_CATEGORY_LOCALIZED_PROPERTY_VALUE);
	}

	@Override
	public void assembleDto(final TaxJurisdiction source, final TaxJurisdictionDTO target) {

		target.setGuid(source.getGuid());
		target.setPriceCalculationMethod(source.getPriceCalculationMethod());
		target.setRegionCode(source.getRegionCode());
		for (TaxCategory category : source.getTaxCategorySet()) {

			TaxCategoryDTO targetCategory = taxCategoryDtoFactory();

			targetCategory.setGuid(category.getGuid());
			targetCategory.setName(category.getName());
			targetCategory.setFieldMatchType(category.getFieldMatchType());

			for (String key : category.getLocalizedProperties().getLocalizedPropertiesMap().keySet()) {
				LocalizedPropertyValue lpv = category.getLocalizedProperties().getLocalizedPropertiesMap().get(key);

				PropertyDTO prop = localizedPropertyDtoFactory();

				prop.setPropertyKey(lpv.getLocalizedPropertyKey());
				prop.setValue(lpv.getValue());

				targetCategory.getLocalizedProperties().add(prop);
			}

			for (TaxRegion region : category.getTaxRegionSet()) {
				TaxRegionDTO targetRegion = taxRegionDtoFactory();

				targetRegion.setRegionName(region.getRegionName());

				for (String taxCode : region.getTaxValuesMap().keySet()) {
					TaxValueDTO targetValue = taxValueDtoFactory();

					targetValue.setCode(taxCode);
					targetValue.setPercent(region.getTaxValuesMap().get(taxCode).getTaxValue());
					targetRegion.getValues().add(targetValue);

				}
				targetCategory.getRegions().add(targetRegion);
			}
			target.getTaxCategories().add(targetCategory);
		}

	}

	@Override
	public void assembleDomain(final TaxJurisdictionDTO source, final TaxJurisdiction target) {
		target.setGuid(source.getGuid());
		target.setRegionCode(source.getRegionCode());
		target.setPriceCalculationMethod(source.isPriceCalculationMethod());

		for (TaxCategoryDTO category : source.getTaxCategories()) {

			TaxCategory targetCategory;

			targetCategory = target.getTaxCategory(category.getName());

			if (targetCategory == null) {
				targetCategory = taxCategoryDomainFactory();
				target.addTaxCategory(targetCategory);
			}

			targetCategory.setName(category.getName());
			targetCategory.setFieldMatchType(category.getFieldMatchType());

			for (PropertyDTO localizedProperty : category.getLocalizedProperties()) {
				LocalizedPropertyValue lpv = targetCategory.getLocalizedProperties().getLocalizedPropertiesMap()
						.get(localizedProperty.getPropertyKey());

				if (lpv == null) {
					lpv = localizedPropertyValueDomainFactory();
					targetCategory.getLocalizedProperties().getLocalizedPropertiesMap().put(localizedProperty.getPropertyKey(), lpv);
				}

				lpv.setLocalizedPropertyKey(localizedProperty.getPropertyKey());
				lpv.setValue(localizedProperty.getValue());
			}

			for (TaxRegionDTO region : category.getRegions()) {

				TaxRegion targetRegion;
				// TODO Auto-generated method stub
				targetRegion = targetCategory.getTaxRegion(region.getRegionName());

				if (targetRegion == null) {
					targetRegion = taxRegionDomainFactory();
					targetCategory.getTaxRegionSet().add(targetRegion);
				}

				targetRegion.setRegionName(region.getRegionName());

				for (TaxValueDTO taxValue : region.getValues()) {
					TaxValue targetValue = taxValueDomainFactory();
					TaxCode taxCode = taxCodeService.findByCode(taxValue.getCode());
					targetValue.setTaxCode(taxCode);
					targetValue.setTaxValue(taxValue.getPercent());
					targetRegion.addTaxValue(targetValue);
				}

				/*
				 * This setter has a side effect where it sets a protected TaxValueSet that is otherwise inaccessible. So we do this to trigger that
				 * side-effect.
				 */
				targetRegion.setTaxValuesMap(targetRegion.getTaxValuesMap());

			}
		}
	}

	/**
	 * @param beanFactory the factory used for creating beans.
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * @param service tax code service injected
	 */
	public void setTaxCodeService(final TaxCodeService service) {
		this.taxCodeService = service;
	}
}
