package com.elasticpath.domain.catalog;

import java.util.Locale;

/**
 * Represents a collection of locale-dependant fields of a catalog object, like <code>Product</code>, or <code>Category</code>.
 * 
 * These fields are used for SEO purposes (SEO URL String, SEO Keywords, SEO Description (not Category description), SEO Title, and SEO DisplayName).
 * 
 * Note that the display name is also often used in the storefront.
 */
public interface LocaleDependantFields {
	/**
	 * Sets the url.
	 *
	 * @param url the url to set.
	 */
	void setUrl(String url);

	/**
	 * Returns the url.
	 *
	 * @return the url
	 */
	String getUrl();

	/**
	 * Sets the locale.
	 *
	 * @param locale the locale to set.
	 */
	void setLocale(Locale locale);

	/**
	 * Returns the locale.
	 *
	 * @return the locale
	 */
	Locale getLocale();

	/**
	 * Sets the key words.
	 *
	 * @param keyWords the key words to set.
	 */
	void setKeyWords(String keyWords);

	/**
	 * Returns the key words.
	 *
	 * @return the key words
	 */
	String getKeyWords();

	/**
	 * Sets the description.
	 *
	 * @param description the description to set.
	 */
	void setDescription(String description);

	/**
	 * Returns the description.
	 *
	 * @return the description
	 */
	String getDescription();

	/**
	 * Sets the title.
	 *
	 * @param title the title to set.
	 */
	void setTitle(String title);

	/**
	 * Returns the title.
	 *
	 * @return the title
	 */
	String getTitle();

	/**
	 * Sets the display name.
	 *
	 * @param displayName the title to set.
	 */
	void setDisplayName(String displayName);

	/**
	 * Returns the display name.
	 *
	 * @return the title
	 */
	String getDisplayName();

}
