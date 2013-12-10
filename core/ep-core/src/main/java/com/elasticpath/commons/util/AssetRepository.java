/**
 * 
 */
package com.elasticpath.commons.util;

import java.net.URL;


/**
 * Provides paths to various asset storage locations as well the url path for retrieving a resource.
 */
public interface AssetRepository {

	/**
	 * @return the file system path to catalog assets.
	 */
	String getCatalogAssetPath();
	
	/**
	 * @return the file system path to catalog images.
	 */
	String getCatalogImagesPath();
	
	/**
	 * @return the name of the directory within which catalog images are stored.
	 */
	String getCatalogImagesSubfolder();
	
	/**
	 * @return the file system path to import files.
	 */
	String getImportAssetPath();
	
	/**
	 * @return the file system path to digital goods.
	 */
	String getCatalogDigitalGoodsPath();
	
	/**
	 * @return the name of the directory within which catalog digital goods are stored
	 */
	String getCatalogDigitalGoodsSubfolder();
	
	/**
	 * @return the file system path to store assets.
	 */
	String getStoreAssetsPath();
	
	/**
	 * @return the name of the top-level directory within which store assets are stored
	 */
	String getStoreAssetsSubfolder();
	
	/**
	 * @return the name of the top-level directory within which commerce manager assets are stored
	 */
	String getCmAssetsSubfolder();
	
	/**
	 * @return the name of the top-level directory within which themed resources are stored
	 */
	String getThemesSubfolder();
	
	/**
	 * @return the file system path to store assets.
	 */
	String getThemeAssetsPath();
	
	/**
	 * @return the name of the top-level directory within which dynamic content resources are stored
	 */
	String getDynamicContentAssetsSubfolder();
	
	/**
	 * @return the name of the top-level directory within which dynamic content resources are stored
	 */
	String getDynamicContentAssetsPath();
	
	/**
	 * @return the system path to content wrappers repository.
	 */
	String getContentWrappersPath();
	
	/**
	 * Gets the base url of the asset server. This can be prepended in front of an asset name
	 * to get an absolute url to retrieve the asset.
	 *
	 * @param storeCode the store code
	 * @return the base url of the asset server.
	 */
	String getAssetServerBaseUrl(String storeCode);
	
	/**
	 * Gets the url that the asset server serves images from.
	 *
	 * @param storeCode the store code
	 * @return the asset server images url
	 */
	URL getAssetServerImagesUrl(String storeCode);
}
