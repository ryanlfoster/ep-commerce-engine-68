package com.elasticpath.importexport.common.dto.products;

import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import com.elasticpath.common.dto.DisplayValue;
import com.elasticpath.common.dto.Dto;

/**
 * The implementation of the <code>Dto</code> interface that contains data of locale dependent fields obejct.
 * <p>
 * This implementation designed for JAXB to working with xml representation of data
 */
@XmlAccessorType(XmlAccessType.NONE)
public class SeoDTO implements Dto {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String VALUE = "value";

	@XmlElementWrapper(name = "url")
	@XmlElement(name = VALUE, required = true)
	private List<DisplayValue> urlList;

	@XmlElementWrapper(name = "title")
	@XmlElement(name = VALUE, required = true)
	private List<DisplayValue> titleList;

	@XmlElementWrapper(name = "keywords")
	@XmlElement(name = VALUE, required = true)
	private List<DisplayValue> keywordsList;

	@XmlElementWrapper(name = "description")
	@XmlElement(name = VALUE, required = true)
	private List<DisplayValue> descriptionList;

	/**
	 * Gets the url <code>DisplayValue</code> list.
	 * 
	 * @return the urlList
	 */
	public List<DisplayValue> getUrlList() {
		return getListWithoutFallBack(urlList);
	}

	/**
	 * Sets the url <code>DisplayValue</code> list.
	 * 
	 * @param urlList the urlList to set
	 */
	public void setUrlList(final List<DisplayValue> urlList) {
		this.urlList = urlList;
	}

	/**
	 * Gets the title <code>DisplayValue</code> list.
	 * 
	 * @return the titleList
	 */
	public List<DisplayValue> getTitleList() {
		return getListWithoutFallBack(titleList);
	}

	/**
	 * Sets the title <code>DisplayValue</code> list.
	 * 
	 * @param titleList the titleList to set
	 */
	public void setTitleList(final List<DisplayValue> titleList) {
		this.titleList = titleList;
	}

	/**
	 * Gets the keywords <code>DisplayValue</code> list.
	 * 
	 * @return the keywordsList
	 */
	public List<DisplayValue> getKeywordsList() {
		return getListWithoutFallBack(keywordsList);
	}

	/**
	 * Sets the keywords <code>DisplayValue</code> list.
	 * 
	 * @param keywordsList the keywordsList to set
	 */
	public void setKeywordsList(final List<DisplayValue> keywordsList) {
		this.keywordsList = keywordsList;
	}

	/**
	 * Gets the description <code>DisplayValue</code> list.
	 * 
	 * @return the descriptionList
	 */
	public List<DisplayValue> getDescriptionList() {
		return getListWithoutFallBack(descriptionList);
	}

	/**
	 * Sets the description <code>DisplayValue</code> list.
	 * 
	 * @param descriptionList the descriptionList to set
	 */
	public void setDescriptionList(final List<DisplayValue> descriptionList) {
		this.descriptionList = descriptionList;
	}
	
	private List<DisplayValue> getListWithoutFallBack(final List<DisplayValue> list) {
		if (list == null) {
			return Collections.emptyList();
		}
		return list;
	}
}
