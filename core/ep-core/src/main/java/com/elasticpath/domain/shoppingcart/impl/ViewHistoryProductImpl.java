package com.elasticpath.domain.shoppingcart.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.catalog.LocaleDependantFields;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalogview.SeoUrlBuilder;
import com.elasticpath.domain.impl.AbstractEpDomainImpl;
import com.elasticpath.domain.shoppingcart.ViewHistoryProduct;
import com.elasticpath.service.catalogview.StoreConfig;

/**
 * This represents a product recently viewed by the user.
 */
public class ViewHistoryProductImpl extends AbstractEpDomainImpl implements ViewHistoryProduct {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	private long uidPk;

	private Brand brand;

	private String image;

	private String guid;

	/** Product properties map. */
	private final Map<String, LocaleDependantFields> localeDependantFieldsMap = new HashMap<String, LocaleDependantFields>();

	/** Map of locale strings to SEO URLS. */
	private final Map<String, String> seoUrlMap = new HashMap<String, String>();

	/**
	 * Loads product information from the real <code>Product</code> into
	 * this representation of a viewed product. To keep this object light-weight,
	 * <b>no reference to the <code>product</code> is kept.</b>
	 * @param product the <code>Product</code> whose information is to be loaded
	 * @param seoUrlBuilder the seo url builder instance to use.
	 */
	public void loadProductInfo(final Product product, final SeoUrlBuilder seoUrlBuilder) {

		uidPk = product.getUidPk();
		brand = product.getBrand();
		image = product.getImage();
		guid  = product.getGuid();

		Collection<Locale> locales = getLocales();

		createProductLocaleDependantFields(product, locales);
		createSeoUrls(product, locales, seoUrlBuilder);
	}

	/**
	 * Get the list of locales for the store.
	 * @return a list of locales
	 */
	protected Collection<Locale> getLocales() {

		StoreConfig storeConfig = getBean("threadLocalStorage");
		Collection<Locale> locales = storeConfig.getStore().getSupportedLocales();

		return locales;
	}

	/**
	 * Create product locale dependent fields.
	 * @param product is the product to set the locale dependent fields for
	 * @param locales are the set of locales to process
	 */
	protected void createProductLocaleDependantFields(final Product product,
			                              			  final Collection<Locale> locales) {

		for (final Locale currLocale : locales) {

			LocaleDependantFields productLdf = product.getLocaleDependantFields(currLocale);
			localeDependantFieldsMap.put(currLocale.toString(), productLdf);
		}

	}

	/**
	 * Create the seo url's for this class.
	 * @param product is the product to set the locale dependent fields for
	 * @param locales are the set of locales to process
	 * @param seoUrlBuilder is the seo builder to use for generating the urls
	 */
	protected void createSeoUrls(final Product product,
		              			 final Collection<Locale> locales,
			                     final SeoUrlBuilder seoUrlBuilder) {

		for (final Locale currLocale : locales) {

			seoUrlMap.put(currLocale.toString().toLowerCase(), seoUrlBuilder.productSeoUrl(product, currLocale));
		}

	}

	/**
	 * Gets the ldf for the viewed product.
	 *
	 * @param locale the locale
	 * @return a <code>Map</code> of <code>Locale</code> to <code>LocaleDependantFields</code>
	 */
	public LocaleDependantFields getLdf(final Locale locale) {
		return localeDependantFieldsMap.get(locale.toString());
	}

	/**
	 * Gets the uidPk for the viewed product.
	 *
	 * @return <code>long</code> the uidPk of the viewed product.
	 */
	public long getUidPk() {
		return uidPk;
	}

	/**
	 * Sets the uidPk for the viewed product.
	 *
	 * @param uidPk the uidPk for the viewed product.
	 */
	public void setUidPk(final long uidPk) {
		this.uidPk = uidPk;
	}

	/**
	 * Gets the guid for the viewed product.
	 *
	 * @return <code>String</code> the guid of the viewed product.
	 */
	public String getGuid() {
		return guid;
	}

	/**
	 * Sets the guid for the viewed product.
	 *
	 * @param guid the guid for the viewed product.
	 */
	public void setGuid(final String guid) {
		this.guid = guid;
	}

	/**
	 * Gets the brand for the viewed product.
	 *
	 * @return <code>brand</code> the brand of the viewed product.
	 */
	public Brand getBrand() {
		return brand;
	}

	/**
	 * Sets the brand for the viewed product.
	 *
	 * @param brand the brand for the viewed product.
	 */
	public void setBrand(final Brand brand) {
		this.brand = brand;
	}

	/**
	 * Gets the image for the viewed product.
	 *
	 * @return <code>String</code> the image of the viewed product.
	 */
	public String getImage() {
		return image;
	}

	/**
	 * Sets the image for the viewed product.
	 *
	 * @param image the image for the viewed product.
	 */
	public void setImage(final String image) {
		this.image = image;
	}

	/**
	 * Returns the SEO url for the viewed product.
	 * @param locale the locale of the SEO URL to be returned
	 * @return the SEO url
	 */
	public String getSeoUrl(final Locale locale) {

		return seoUrlMap.get(locale.toString().toLowerCase());
	}

	/**
	 * Get local dependant fields map.
	 * @return locale dependant fields map
	 */
	Map<String, LocaleDependantFields> getLocaleDependantFieldsMap() {
		return localeDependantFieldsMap;
	}

	/**
	 * Get seo url map.
	 * @return seo url map
	 */
	Map<String, String> getSeoUrlMap() {
		return seoUrlMap;
	}
}
