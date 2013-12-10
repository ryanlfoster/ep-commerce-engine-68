package com.elasticpath.domain.shoppingcart;

import java.util.Locale;

import com.elasticpath.domain.EpDomain;
import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.catalog.LocaleDependantFields;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalogview.SeoUrlBuilder;

/**
 * This class represents a product recently viewed by a user.
 */
public interface ViewHistoryProduct extends EpDomain {

	/**
	 * Loads product information from the real <code>Product</code> into
	 * this representation of a viewed product. To keep this object light-weight,
	 * <b>no reference to the <code>product</code> is kept.</b>
	 * @param product the <code>Product</code> whose information is to be loaded
	 * @param seoUrlBuilder the seo url builder implementation to use to build the
	 *        seo url's for the ViewHistoryProduct.
	 */
	void loadProductInfo(final Product product, SeoUrlBuilder seoUrlBuilder);

	/**
	 * Gets the ldf for the viewed product.
	 *
	 * @param locale the locale
	 * @return a <code>LocaleDependantFields</code> object for this locale
	 */
	LocaleDependantFields getLdf(Locale locale);

	/**
	 * Gets the uidPk for the viewed product.
	 * @return <code>long</code> the uidPk of the viewed product.
	 */
	long getUidPk();


	/**
	 * Gets the code for the viewed product.
	 * @return <code>String</code> the code of the viewed product.
	 */
	String getGuid();

	/**
	 * Gets the brand for the viewed product.
	 * @return <code>brand</code> the brand of the viewed product.
	 */
	Brand getBrand();

	/**
	 * Gets the image for the viewed product.
	 * @return <code>String</code> the image of the viewed product.
	 */
	String getImage();

	/**
	 * Returns the SEO url for the viewed product.
	 * @param locale the locale of the SEO URL to be returned
	 * @return the SEO url
	 */
	String getSeoUrl(final Locale locale);
}
