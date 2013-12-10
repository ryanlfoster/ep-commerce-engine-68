package com.elasticpath.service.catalogview.impl;

import org.apache.commons.lang.math.NumberUtils;

import com.elasticpath.service.catalogview.PaginationService;
import com.elasticpath.settings.SettingsReader;

/**
 * Paginates. 
  */
public class PaginationServiceImpl implements PaginationService {
	private static final int DEFAULT_PAGINATION_VALUE = 20;

	private SettingsReader settingsReader;
	
	/**
	 * Calculate last page number.
	 * @param numberOfResults are the number of results returned
	 * @param storeCode Store code.
	 * @return the last page number
	 */
	public int getLastPageNumber(final int numberOfResults,
			                     final String storeCode) {
		
		int paginationNumber = getNumberOfItemsPerPage(storeCode);
		
		return getLastPageNumber(paginationNumber, numberOfResults);
	}

	/**
	 * Gets last page number.
	 * 
	 * @param itemsPerPage itemsPerPage
	 * @param numberOfItems numberOfItems
	 * @return last page number.
	 */
	int getLastPageNumber(final int itemsPerPage, final int numberOfItems) {
		int maxNumPages = (numberOfItems / itemsPerPage);
		
		if (numberOfItems % itemsPerPage != 0) {
			maxNumPages += 1;
		}
		return maxNumPages;
	}

	/**
	 * Number of items per page.
	 * @param storeCode Store code.
	 * @return number of items per page.
	 */
	public int getNumberOfItemsPerPage(final String storeCode) {
		String pagination = getSettingsReader().getSettingValue("COMMERCE/STORE/CATALOG/catalogViewPagination", storeCode).getValue();		
		return NumberUtils.toInt(pagination, DEFAULT_PAGINATION_VALUE);
	}
	
	/**
	 * Sets the Settings Reader.
	 * @param settingsReader the settings reader
	 */
	public void setSettingsReader(final SettingsReader settingsReader) {
		this.settingsReader = settingsReader;
	}
	
	/**
	 * Gets the Settings Reader.
	 * @return the settings reader
	 */
	public SettingsReader getSettingsReader() {
		return this.settingsReader;
	}
}
