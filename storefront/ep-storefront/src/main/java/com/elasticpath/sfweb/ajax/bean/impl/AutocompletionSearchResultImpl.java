/**
 * 
 */
package com.elasticpath.sfweb.ajax.bean.impl;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.sfweb.ajax.bean.AutocompletionSearchResult;

/**
 * Implementation of <code>AutocompletionSearchResult</code>.
 *
 */
public class AutocompletionSearchResultImpl implements AutocompletionSearchResult {

	private String guid = StringUtils.EMPTY;
	private String name = StringUtils.EMPTY;
	private String image = StringUtils.EMPTY;
	private String url = StringUtils.EMPTY;
	private String description = StringUtils.EMPTY;
	private String price = StringUtils.EMPTY;
	
	
	/**
	 * Constructor for autocompletion object.
	 */
	public AutocompletionSearchResultImpl() {
		super();
	}

	/**
	 * Constructor for autocompletion object.
	 * @param guid product Guid
	 * @param name product name
	 * @param description product description
	 * @param image image name
	 * @param url product page url
	 * @param price product's price
	 * 
	 */
	public AutocompletionSearchResultImpl(final String guid, final String name, final String description, 
			final String image,
			final String url,
			final String price
			) {
		super();
		this.guid = adaptString(guid);
		this.name = adaptString(name);
		this.description = adaptString(description);
		this.image = adaptString(image);
		this.url = adaptString(url);
		this.price = adaptString(price);
	}

	/**
	 * TODO html escape. 
	 * @param string
	 * @return given string if it not null, otherwise empty string.
	 */
	private String adaptString(final String string) {
		if (null == string) {
			return StringUtils.EMPTY;
		}
		return string;
	}
	
	
	@Override
	public String getPrice() {
		return price;
	}

	@Override
	public String getGuid() {
		return this.guid;
	}

	@Override
	public String getImage() {
		return this.image;
	}

	@Override
	public String getName() {
		return this.name;
	}
	
	@Override
	public void setPrice(final String price) {
		this.price = adaptString(price);
	}	

	/**
	 * @param guid the guid to set
	 */
	public void setGuid(final String guid) {
		this.guid = adaptString(guid);
	}

	/**
	 * @param name the name to set
	 */
	public void setName(final String name) {
		this.name = adaptString(name);
	}

	/**
	 * @param image the image to set
	 */
	public void setImage(final String image) {
		this.image = adaptString(image);
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(final String url) {
		this.url = adaptString(url);
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(final String description) {
		this.description = adaptString(description);
	}

}
