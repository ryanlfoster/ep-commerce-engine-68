package com.elasticpath.service.catalogview.impl;  //NOPMD

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.attribute.AttributeValueGroup;
import com.elasticpath.domain.catalog.AvailabilityCriteria;
import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.LocaleDependantFields;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.localization.LocaleFallbackPolicy;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.service.catalogview.ProductWrapper;

/**
 * Abstract notion of a abstract wrapped product.
 */
public abstract class AbstractWrappedProductImpl implements Product, ProductWrapper {
	private static final long serialVersionUID = 3937889907525137716L;

	private final Product wrappedProduct;

	/**
	 * Constructor.
	 * 
	 * @param wrappedProduct the product to wrap
	 */
	protected AbstractWrappedProductImpl(final Product wrappedProduct) {
		this.wrappedProduct = wrappedProduct;
	}
	
	@Override
	public Product getWrappedProduct() {
		return wrappedProduct;
	}
	
	@Override
	public void addCategory(final Category category) {
		wrappedProduct.addCategory(category);
	}


	@Override
	public void addOrUpdateSku(final ProductSku productSku) {
		wrappedProduct.addOrUpdateSku(productSku);
	}

	@Override
	public AttributeValueGroup getAttributeValueGroup() {
		return wrappedProduct.getAttributeValueGroup();
	}

	@Override
	public Map<String, AttributeValue> getAttributeValueMap() {
		return wrappedProduct.getAttributeValueMap();
	}

	@Override
	public List<AttributeValue> getAttributeValues(final Locale locale) {
		return wrappedProduct.getAttributeValues(locale);
	}

	@Override
	public AvailabilityCriteria getAvailabilityCriteria() {
		return wrappedProduct.getAvailabilityCriteria();
	}

	@Override
	public Brand getBrand() {
		return wrappedProduct.getBrand();
	}

	@Override
	public Set<Catalog> getCatalogs() {
		return wrappedProduct.getCatalogs();
	}

	@Override
	public Set<Category> getCategories() {
		return wrappedProduct.getCategories();
	}
	
	@Override
	public Set<Category> getCategories(final Catalog catalog) {
		return wrappedProduct.getCategories(catalog);
	}

	@Override
	public String getCode() {
		return wrappedProduct.getCode();
	}

	@Override
	public Category getDefaultCategory(final Catalog catalog) {
		return wrappedProduct.getDefaultCategory(catalog);
	}

	@Override
	public ProductSku getDefaultSku() {
		return wrappedProduct.getDefaultSku();
	}

	@Override
	public Date getEndDate() {
		return wrappedProduct.getEndDate();
	}

	@Override
	public Date getExpectedReleaseDate() {
		return wrappedProduct.getExpectedReleaseDate();
	}

	@Override
	public List<AttributeValue> getFullAttributeValues(final Locale locale) {
		return wrappedProduct.getFullAttributeValues(locale);
	}

	@Override
	public String getImage() {
		return wrappedProduct.getImage();
	}

	@Override
	public Date getLastModifiedDate() {
		return wrappedProduct.getLastModifiedDate();
	}

	@Override
	public Catalog getMasterCatalog() {
		return wrappedProduct.getMasterCatalog();
	}

	@Override
	public int getMaxFeaturedProductOrder() {
		return wrappedProduct.getMaxFeaturedProductOrder();
	}

	@Override
	public int getMinOrderQty() {
		return wrappedProduct.getMinOrderQty();
	}

	@Override
	public int getPreOrBackOrderLimit() {
		return wrappedProduct.getPreOrBackOrderLimit();
	}

	@Override
	public int getFeaturedRank(final Category category) {
		return wrappedProduct.getFeaturedRank(category);
	}
	
	@Override
	public void setFeaturedRank(final Category category, final int index) {
		this.wrappedProduct.setFeaturedRank(category, index);
	}
	
	@Override
	public void removeAllCategories() {
		this.wrappedProduct.removeAllCategories();
	}

	@Override
	public Map<String, ProductSku> getProductSkus() {
		return wrappedProduct.getProductSkus();
	}

	@Override
	public ProductType getProductType() {
		return wrappedProduct.getProductType();
	}

	@Override
	public int getSalesCount() {
		return wrappedProduct.getSalesCount();
	}

	@Override
	public ProductSku getSkuByGuid(final String guid) {
		return wrappedProduct.getSkuByGuid(guid);
	}
	
	@Override
	public ProductSku getSkuByCode(final String guid) {
		return wrappedProduct.getSkuByCode(guid);
	}

	@Override
	public Date getStartDate() {
		return wrappedProduct.getStartDate();
	}

	@Override
	public TaxCode getTaxCode() {
		return wrappedProduct.getTaxCode();
	}

	@Override
	public String getTemplate() {
		return wrappedProduct.getTemplate();
	}

	@Override
	public String getTemplateWithFallBack(final String defaultTemplate) {
		return wrappedProduct.getTemplateWithFallBack(defaultTemplate);
	}

	@Override
	public int getUidPkInt() {
		return wrappedProduct.getUidPkInt();
	}

	@Override
	public boolean hasMultipleSkus() {
		return wrappedProduct.hasMultipleSkus();
	}

	@Override
	public boolean isBelongToCategory(final long categoryUid) {
		return wrappedProduct.isBelongToCategory(categoryUid);
	}

	@Override
	public boolean isHidden() {
		return wrappedProduct.isHidden();
	}

	@Override
	public boolean isInCatalog(final Catalog catalog) {
		return wrappedProduct.isInCatalog(catalog);
	}

	@Override
	public boolean isInCatalog(final Catalog catalog, final boolean checkForLinkedCategories) {
		return wrappedProduct.isInCatalog(catalog, checkForLinkedCategories);
	}

	@Override
	public boolean isInCategory(final String compoundCategoryGuid) {
		return wrappedProduct.isInCategory(compoundCategoryGuid);
	}

	@Override
	public boolean isWithinDateRange(final Date date) {
		return wrappedProduct.isWithinDateRange(date);
	}

	@Override
	public void removeCategory(final Category category) {
		wrappedProduct.removeCategory(category);
	}

	@Override
	public void removeSku(final ProductSku productSku) {
		wrappedProduct.removeSku(productSku);
	}

	@Override
	public void setAttributeValueGroup(final AttributeValueGroup attributeValueGroup) {
		wrappedProduct.setAttributeValueGroup(attributeValueGroup);
	}

	@Override
	public void setAttributeValueMap(final Map<String, AttributeValue> attributeValueMap) {
		wrappedProduct.setAttributeValueMap(attributeValueMap);
	}

	@Override
	public void setAvailabilityCriteria(final AvailabilityCriteria availabilityCriteria) {
		wrappedProduct.setAvailabilityCriteria(availabilityCriteria);
	}

	@Override
	public void setBrand(final Brand brand) {
		wrappedProduct.setBrand(brand);
	}

	@Override
	public void setCategories(final Set<Category> newCategories) {
		wrappedProduct.setCategories(newCategories);
	}

	@Override
	public void setCode(final String code) {
		wrappedProduct.setCode(code);
	}

	@Override
	public void setCategoryAsDefault(final Category category) {
		wrappedProduct.setCategoryAsDefault(category);
	}

	@Override
	public void setDefaultSku(final ProductSku defaultSku) {
		wrappedProduct.setDefaultSku(defaultSku);
	}

	@Override
	public void setEndDate(final Date endDate) {
		wrappedProduct.setEndDate(endDate);
	}

	@Override
	public void setExpectedReleaseDate(final Date releaseDate) {
		wrappedProduct.setExpectedReleaseDate(releaseDate);
	}

	@Override
	public void setHidden(final boolean hidden) {
		wrappedProduct.setHidden(hidden);
	}

	@Override
	public void setImage(final String image) {
		wrappedProduct.setImage(image);
	}

	@Override
	public void setLastModifiedDate(final Date lastModifiedDate) {
		wrappedProduct.setLastModifiedDate(lastModifiedDate);
	}

	@Override
	public void setMinOrderQty(final int minOrderQty) {
		wrappedProduct.setMinOrderQty(minOrderQty);
	}

	@Override
	public void setPreOrBackOrderLimit(final int orderLimit) {
		wrappedProduct.setPreOrBackOrderLimit(orderLimit);
	}

	@Override
	public void setProductSkus(final Map<String, ProductSku> productSkus) {
		wrappedProduct.setProductSkus(productSkus);
	}

	@Override
	public void setProductType(final ProductType productType) {
		wrappedProduct.setProductType(productType);
	}

	@Override
	public void setSalesCount(final int salesCount) {
		wrappedProduct.setSalesCount(salesCount);
	}

	@Override
	public void setStartDate(final Date startDate) {
		wrappedProduct.setStartDate(startDate);
	}

	@Override
	public void setTaxCode(final TaxCode taxCode) {
		wrappedProduct.setTaxCode(taxCode);
	}

	@Override
	public int compareTo(final Product product) {
		return wrappedProduct.compareTo(product);
	}

	@Override
	public String getGuid() {
		return wrappedProduct.getGuid();
	}

	@Override
	public void setGuid(final String guid) {
		wrappedProduct.setGuid(guid);
	}

	@Override
	public long getUidPk() {
		return wrappedProduct.getUidPk();
	}

	@Override
	public boolean isPersisted() {
		return wrappedProduct.isPersisted();
	}

	@Override
	public void setUidPk(final long uidPk) {
		wrappedProduct.setUidPk(uidPk);
	}

	@Override
	public void initialize() {
		wrappedProduct.initialize();
	}

	@Override
	public void addOrUpdateLocaleDependantFields(final LocaleDependantFields ldf) {
		wrappedProduct.addOrUpdateLocaleDependantFields(ldf);
	}

	@Override
	public String getDisplayName(final Locale locale) {
		return wrappedProduct.getDisplayName(locale);
	}

	@Override
	public LocaleDependantFields getLocaleDependantFields(final Locale locale) {
		return wrappedProduct.getLocaleDependantFields(locale);
	}

	@Override
	public LocaleDependantFields getLocaleDependantFieldsWithoutFallBack(final Locale locale) {
		return wrappedProduct.getLocaleDependantFieldsWithoutFallBack(locale);
	}

	@Override
	public void setDisplayName(final String name, final Locale locale) {
		wrappedProduct.setDisplayName(name, locale);
	}

	@Override
	public void setLocaleDependantFieldsMap(final Map<Locale, LocaleDependantFields> localeDependantFieldsMap) {
		wrappedProduct.setLocaleDependantFieldsMap(localeDependantFieldsMap);
	}

	@Override
	public ProductSku findSkuWithOptionValueCodes(final Collection<String> optionValueKeysToFind) {
		return wrappedProduct.findSkuWithOptionValueCodes(optionValueKeysToFind);
	}

	@Override
	public LocaleDependantFields getLocaleDependantFields(final LocaleFallbackPolicy policy) {
		return wrappedProduct.getLocaleDependantFields(policy);
	}

	@Override
	public boolean isNotSoldSeparately() {
		return wrappedProduct.isNotSoldSeparately();
	}
	
	@Override
	public void setNotSoldSeparately(final boolean notSoldSeparately) {
		wrappedProduct.setNotSoldSeparately(notSoldSeparately);
	}

	@Override
	public boolean hasSkuWithinDateRange(final Date currentDate) {
		return wrappedProduct.hasSkuWithinDateRange(currentDate);
	}

	@Override
	public void validateRequiredAttributes(final Set<Locale> allLocales) {
		getWrappedProduct().validateRequiredAttributes(allLocales);
		
	}
}
