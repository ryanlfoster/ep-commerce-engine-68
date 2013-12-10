package com.elasticpath.importexport.common.adapters.category;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.LocaleUtils;
import org.apache.log4j.Logger;

import com.elasticpath.common.dto.DisplayValue;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.CategoryType;
import com.elasticpath.domain.catalog.LocaleDependantFields;
import com.elasticpath.importexport.common.adapters.AbstractDomainAdapterImpl;
import com.elasticpath.importexport.common.adapters.products.data.AttributeGroupAdapter;
import com.elasticpath.importexport.common.adapters.products.data.SeoAdapter;
import com.elasticpath.importexport.common.dto.category.CategoryAvailabilityDTO;
import com.elasticpath.importexport.common.dto.category.CategoryDTO;
import com.elasticpath.importexport.common.dto.products.AttributeGroupDTO;
import com.elasticpath.importexport.common.dto.products.SeoDTO;
import com.elasticpath.importexport.common.exception.runtime.PopulationRollbackException;
import com.elasticpath.importexport.common.exception.runtime.PopulationRuntimeException;
import com.elasticpath.importexport.common.util.Message;
import com.elasticpath.service.catalog.CategoryService;

/**
 * The implementation of <code>DomainAdapter</code> interface.<br>
 * It is responsible for data transformation between <code>Category</code> and <code>CategoryDTO</code> objects.
 */
public class CategoryAdapter extends AbstractDomainAdapterImpl<Category, CategoryDTO> {

	private static final Logger LOG = Logger.getLogger(CategoryAdapter.class);

	private AttributeGroupAdapter attributeGroupAdapter;

	private SeoAdapter seoAdapter;

	private CategoryService categoryService;

	@Override
	public void populateDTO(final Category category, final CategoryDTO categoryDTO) {
		categoryDTO.setCatalogCode(category.getCatalog().getCode());

		final Category parent = category.getParent();
		if (parent != null) {
			categoryDTO.setParentCategoryCode(parent.getCode());
		}

		categoryDTO.setCategoryCode(category.getCode());

		populateDtoNameValues(category, categoryDTO);

		categoryDTO.setCategoryType(category.getCategoryType().getName());
		categoryDTO.setOrder(category.getOrdering());

		CategoryAvailabilityDTO categoryAvailabilityDTO = new CategoryAvailabilityDTO();
		categoryAvailabilityDTO.setStoreVisible(!category.isHidden());
		categoryAvailabilityDTO.setStartDate(category.getStartDate());
		categoryAvailabilityDTO.setEndDate(category.getEndDate());
		categoryDTO.setCategoryAvailabilityDTO(categoryAvailabilityDTO);

		populateNestedDto(category, categoryDTO);
	}

	/**
	 * Use nested Adapters to populate nested DTO.
	 * 
	 * @param category the category to populate from
	 * @param categoryDto the DTO to populate
	 */
	void populateNestedDto(final Category category, final CategoryDTO categoryDto) {
		AttributeGroupDTO attributeGroupDto = new AttributeGroupDTO();
		attributeGroupAdapter.populateDTO(category.getAttributeValueGroup(), attributeGroupDto);
		categoryDto.setAttributeGroupDTO(attributeGroupDto);

		SeoDTO seoDTO = new SeoDTO();

		seoAdapter.setSupportedLocales(category.getCatalog().getSupportedLocales());
		seoAdapter.populateDTO(category, seoDTO);
		categoryDto.setSeoDto(seoDTO);
	}

	/**
	 * Populates DTO Name Values.
	 * 
	 * @param category the category to populate from
	 * @param categoryDTO the DTO to populate
	 */
	void populateDtoNameValues(final Category category, final CategoryDTO categoryDTO) {
		final List<DisplayValue> nameValues = new ArrayList<DisplayValue>();
		for (Locale locale : category.getCatalog().getSupportedLocales()) {
			LocaleDependantFields fields = category.getLocaleDependantFieldsWithoutFallBack(locale);
			DisplayValue value = new DisplayValue(locale.toString(), fields.getDisplayName());
			nameValues.add(value);
		}
		categoryDTO.setNameValues(nameValues);
	}

	@Override
	public void populateDomain(final CategoryDTO categoryDTO, final Category category) {
		if ("".equals(categoryDTO.getCategoryCode())) {
			throw new PopulationRollbackException("IE-10102");
		}
		category.setCode(categoryDTO.getCategoryCode());
		category.setOrdering(categoryDTO.getOrder());

		populateMasterCatalog(category, categoryDTO.getCatalogCode());
		populateParentCategory(category, categoryDTO);
		populateDomainNameValues(category, categoryDTO);
		populateCategoryType(category, categoryDTO.getCategoryType());
		populateAvailability(category, categoryDTO.getCategoryAvailabilityDTO());
		populateNestedDomainObjects(category, categoryDTO);
	}

	/**
	 * Used nested Adapters for populate nested Domain Objects.
	 * 
	 * @param category the category to populate
	 * @param categoryDTO the categoryDto to populate from
	 */
	void populateNestedDomainObjects(final Category category, final CategoryDTO categoryDTO) {
		attributeGroupAdapter.populateDomain(categoryDTO.getAttributeGroupDTO(), category.getAttributeValueGroup());
		seoAdapter.populateDomain(categoryDTO.getSeoDto(), category);
	}

	/**
	 * Populates MasterCatalog for Category.
	 * 
	 * @param category the category to populate
	 * @param catalogCode the master catalog code
	 */
	void populateMasterCatalog(final Category category, final String catalogCode) {
		final Catalog catalog = getCachingService().findCatalogByCode(catalogCode);
		if (catalog == null) {
			throw new PopulationRollbackException("IE-10103", catalogCode);
		}
		category.setCatalog(catalog);
		category.setVirtual(!catalog.isMaster());
	}

	/**
	 * Populates Availability for category.
	 * 
	 * @param category the category to populate
	 * @param categoryAvailabilityDTO the DTO to populate from
	 */
	void populateAvailability(final Category category, final CategoryAvailabilityDTO categoryAvailabilityDTO) {
		if (categoryAvailabilityDTO != null) {
			category.setHidden(!categoryAvailabilityDTO.isStoreVisible());

			if (!isAvailabilityDatesCorrect(categoryAvailabilityDTO.getStartDate(), categoryAvailabilityDTO.getEndDate())) {
				throw new PopulationRollbackException("IE-10105", category.getCode());
			}

			category.setStartDate(categoryAvailabilityDTO.getStartDate());
			category.setEndDate(categoryAvailabilityDTO.getEndDate());
		}
	}

	/**
	 * Populates CategoryType.
	 * 
	 * @param category the category to populate
	 * @param categoryTypeName the name of the categoryType
	 */
	void populateCategoryType(final Category category, final String categoryTypeName) {
		final CategoryType categoryType = getCachingService().findCategoryTypeByName(categoryTypeName);
		final Catalog catalog = category.getCatalog();
		if (categoryType == null || (!categoryType.getCatalog().equals(catalog) && catalog.isMaster())) {
			throw new PopulationRollbackException("IE-10104", categoryTypeName, catalog.getCode());
		}

		category.setCategoryType(categoryType);
	}

	/**
	 * Populates Parent Category.
	 * 
	 * @param category the category to populate
	 * @param categoryDto the DTO to populate from
	 */
	void populateParentCategory(final Category category, final CategoryDTO categoryDto) {
		final String parentCategoryCode = categoryDto.getParentCategoryCode();
		if (parentCategoryCode != null && !parentCategoryCode.equals("")) {
			final String catalogCode = categoryDto.getCatalogCode();
			Category parentCategory = getCachingService().findCategoryByCode(parentCategoryCode, catalogCode);
			category.setParent(parentCategory);
			if (parentCategory == null) {
				LOG.warn(new Message("IE-10100", parentCategoryCode));
			}
		}
	}

	/**
	 * Populates LocaleDependantFieldsMap of Category.
	 * 
	 * @param category the category to populate
	 * @param categoryDto the DTO to populate from
	 */
	void populateDomainNameValues(final Category category, final CategoryDTO categoryDto) {
		List<DisplayValue> nameValues = categoryDto.getNameValues();
		Map<Locale, LocaleDependantFields> localeDependantFieldsMap = new HashMap<Locale, LocaleDependantFields>();
		for (DisplayValue displayValue : nameValues) {
			try {
				Locale locale = LocaleUtils.toLocale(displayValue.getLanguage());
				if (LocaleUtils.isAvailableLocale(locale)) {
					LocaleDependantFields dependantFields = category.getLocaleDependantFieldsWithoutFallBack(locale);
					dependantFields.setDisplayName(displayValue.getValue());
					localeDependantFieldsMap.put(locale, dependantFields);
				} else {
					throw new PopulationRuntimeException("IE-10101", displayValue.getLanguage(), displayValue.getValue());
				}
			} catch (IllegalArgumentException exception) {
				throw new PopulationRuntimeException("IE-10101", exception, displayValue.getLanguage(), displayValue.getValue());
			}
		}
		category.setLocaleDependantFieldsMap(localeDependantFieldsMap);
	}

	@Override
	public Category createDomainObject() {
		return getBean(ContextIdNames.CATEGORY);
	}

	@Override
	public CategoryDTO createDtoObject() {
		return new CategoryDTO();
	}

	/**
	 * Checks the correctness of availability dates.
	 * 
	 * @param enableDate the enable date
	 * @param disableDate the disable date
	 * @return true if dates are correct and false otherwise
	 */
	boolean isAvailabilityDatesCorrect(final Date enableDate, final Date disableDate) {
		if (disableDate == null) {
			return true;
		}
		return enableDate.compareTo(disableDate) < 0;
	}

	/**
	 * Gets the attributeGroupAdapter.
	 * 
	 * @return the attributeGroupAdapter
	 */
	public AttributeGroupAdapter getAttributeGroupAdapter() {
		return attributeGroupAdapter;
	}

	/**
	 * Sets the attributeGroupAdapter.
	 * 
	 * @param attributeGroupAdapter the attributeGroupAdapter to set
	 */
	public void setAttributeGroupAdapter(final AttributeGroupAdapter attributeGroupAdapter) {
		this.attributeGroupAdapter = attributeGroupAdapter;
	}

	/**
	 * Gets the seoAdapter.
	 * 
	 * @return the seoAdapter
	 */
	public SeoAdapter getSeoAdapter() {
		return seoAdapter;
	}

	/**
	 * Sets the seoAdapter.
	 * 
	 * @param seoAdapter the seoAdapter to set
	 */
	public void setSeoAdapter(final SeoAdapter seoAdapter) {
		this.seoAdapter = seoAdapter;
	}

	/**
	 * Gets the categoryService.
	 * 
	 * @return the categoryService
	 */
	public CategoryService getCategoryService() {
		return categoryService;
	}

	/**
	 * Sets the categoryService.
	 * 
	 * @param categoryService the categoryService to set
	 */
	public void setCategoryService(final CategoryService categoryService) {
		this.categoryService = categoryService;
	}
}
