package com.elasticpath.domain.catalogview.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.elasticpath.commons.constants.CatalogViewConstants;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.constants.SeoConstants;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.catalogview.BrandFilter;
import com.elasticpath.domain.catalogview.EpCatalogViewRequestBindException;
import com.elasticpath.service.catalog.BrandService;

/**
 * This class is a filter implementation on the products' brands.
 */
public class BrandFilterImpl extends AbstractFilterImpl<BrandFilter> implements BrandFilter {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;

	private final Set<Brand> brands = new LinkedHashSet<Brand>();

	@Override
	public String getDisplayName(final Locale locale) {
		if (brands.isEmpty()) {
			return CatalogViewConstants.BRAND_FILTER_OTHERS;
		}
		
		StringBuilder brandDisplayNameBuilder = new StringBuilder();
		for (Brand brand : brands) {
			if (brandDisplayNameBuilder.length() > 0) {
				brandDisplayNameBuilder.append(",");
			}
			brandDisplayNameBuilder.append(brand.getDisplayName(locale, true));
		}
		
		return brandDisplayNameBuilder.toString();
	}
	
	/**
	 * Returns the hash code.
	 *
	 * @return the hash code
	 */
	@Override
	public int hashCode() {
		return this.getId().hashCode();
	}

	/**
	 * Returns <code>true</code> if this filter equals to the given object.
	 *
	 * @param object the object to compare
	 * @return <code>true</code> if this filter equals to the given object.
	 */
	@Override
	public boolean equals(final Object object) {
		if (!(object instanceof BrandFilter)) {
			return false;
		}
		return getId().equals(((BrandFilter) object).getId());
	}

	/**
	 * Compares this object with the specified object for ordering.
	 *
	 * @param other the given object
	 * @return a negative integer, zero, or a positive integer as this object is less than, equal to, or greater than the specified object.
	 * @throws EpDomainException in case the given object is not a <code>BrandFilter</code>
	 */
	@Override
	public int compareTo(final BrandFilter other) throws EpDomainException {
		if (this == other) {
			return 0;
		}
		if (brands.isEmpty()) {
			return Integer.MAX_VALUE;
		}
		if (other.getBrands().isEmpty()) {
			return Integer.MIN_VALUE;
		}
		
		if (brands.size() < other.getBrands().size()) {
			return Integer.MAX_VALUE;
		} else if (brands.size() > other.getBrands().size()) {
			return Integer.MIN_VALUE;
		}
		
		List<Brand> brands1 = new ArrayList<Brand>();
		brands1.addAll(brands);

		List<Brand> brands2 = new ArrayList<Brand>();
		brands2.addAll(other.getBrands());
		
		int count = 0;
		for (Brand brand : brands1) {
			if (!brand.getCode().equals(brands2.get(count).getCode())) {
				return Integer.MAX_VALUE;
			}
		}		
		return 0;
	}

	/**
	 * Returns the SEO url of the filter with the given locale. Currently the display name will be used as the seo url.
	 *
	 * @param locale the locale
	 * @return the SEO url of the filter with the given locale.
	 */
	@Override
	public String getSeoName(final Locale locale) {
		return getDisplayName(locale);
	}

	/**
	 * Returns the SEO identifier of the filter with the given locale.
	 *
	 * @return the SEO identifier of the filter with the given locale.
	 */
	@Override
	public String getSeoId() {
		if (!brands.isEmpty()) {
			StringBuilder seoIdBuilder = new StringBuilder(0);
			for (Brand brand : brands) {
				if (seoIdBuilder.length() > 0) {
					seoIdBuilder.append(getSeparatorInToken());
				}
				seoIdBuilder.append(brand.getCode());
			}
			seoIdBuilder.insert(0, SeoConstants.BRAND_FILTER_PREFIX);
			return seoIdBuilder.toString();
		}
		return SeoConstants.BRAND_FILTER_PREFIX + CatalogViewConstants.BRAND_FILTER_OTHERS;
	}	
	
	/**
	 * Gets the brand that this associated to this filter.
	 *
	 * @return the brand that is associated to this filter
	 */
	@Override
	public Brand getBrand() {
		if (brands.isEmpty()) {
			return null;
		}
		return brands.iterator().next();
	}

	@Override
	public void initializeWithCode(final String brandCode) {
		initializeWithCode(new String[] {brandCode});
	}

	@Override
	public void initializeWithCode(final String[] brandCodes) {
		
		if (brandCodes != null) {
		  int count = 0;	
		  StringBuilder brandFilterIdBuilder = new StringBuilder();	
		  for (String brandCode : brandCodes) {
			final Brand brand = getBrandService().findByCode(brandCode);
			if (brand == null) {
				throw new EpCatalogViewRequestBindException(String.format("Invalid brand code %1$s", brandCode));
			}
			addBrand(brand);
			brandFilterIdBuilder.append(brand.getCode());
			if (count < brandCodes.length - 1) {
				brandFilterIdBuilder.append(getSeparatorInToken());
			}
			count++;
		  }	
		  setId(SeoConstants.BRAND_FILTER_PREFIX + brandFilterIdBuilder.toString());
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void initialize(final Map<String, Object> properties) {
		this.brands.clear();
		Set<Brand> brands = (Set<Brand>) properties.get(BRAND_PROPERTY_KEY);
		if (brands != null) {
			for (Brand brand : brands) {
				addBrand(brand);
			}
		}
		setId(getSeoId());
	}	
	
	@Override
	public Map<String, Object> parseFilterString(final String filterIdStr) {
		if (!filterIdStr.startsWith(SeoConstants.BRAND_FILTER_PREFIX)) {
			throw new EpCatalogViewRequestBindException("Invalid brand filter id:" + filterIdStr);
		}

		Map<String, Object> tokenMap = new HashMap<String, Object>();

		final String idStr = filterIdStr.substring(filterIdStr.indexOf(SeoConstants.BRAND_FILTER_PREFIX)
				+ SeoConstants.BRAND_FILTER_PREFIX.length());
		
		if (idStr.equals(CatalogViewConstants.BRAND_FILTER_OTHERS)) {
			tokenMap.put(BRAND_PROPERTY_KEY, null);
		} else {
			final String[] brandCode = idStr.split(getSeparatorInToken());
			Set<Brand> brnds = new HashSet<Brand>(); 
			for (int i = 0; i < brandCode.length; i++) {
				Brand brand = getBrandService().findByCode(brandCode[i]);
				if (brand == null) {
					throw new EpCatalogViewRequestBindException("Invalid brand filter id:" + filterIdStr 
							+ ". Brand Code does not exist: " + brandCode[i]);
				}
				brnds.add(brand);
			}
			tokenMap.put(BRAND_PROPERTY_KEY, brnds);
		}
		
		return tokenMap;
	}
	
	private void addBrand(final Brand brand) {
		if (brand == null) {
			return;
		}
		brands.add(brand);
	}
	
	@Override
	public Set<Brand> getBrands() {
		return brands;
	}	
	
	/**
	 * Get brand service.
	 * 
	 * @return the brand service
	 */
	protected BrandService getBrandService() {
		return getBean(ContextIdNames.BRAND_SERVICE);
	}	
}
