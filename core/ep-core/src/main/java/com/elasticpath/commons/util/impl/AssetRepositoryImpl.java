/**
 * 
 */
package com.elasticpath.commons.util.impl;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.commons.util.AssetRepository;
import com.elasticpath.domain.ElasticPath;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.settings.SettingsReader;
import com.elasticpath.settings.domain.SettingValue;

/**
 * Provides paths to various Asset storage locations as well the url path for retrieving a resource.
 * This implementation uses the SettingsService to retrieve the various
 * paths, and ensure that the paths are valid for the platform
 * upon which the JVM is running.
 */
public class AssetRepositoryImpl implements AssetRepository {
	private static final Logger LOG = Logger.getLogger(AssetRepositoryImpl.class);

	private static final Pattern MULTIPLE_SLASHES = Pattern.compile("/{2,}|\\\\{2,}");
	private static final Pattern LEADING_SLASH = Pattern.compile("^/|^\\\\");
	private static final Pattern TRAILING_SLASH = Pattern.compile("/$|\\\\$");

	private SettingsReader settingsReader;
	
	//TODO: Remove reference to ElasticPath object once we figure out how to reference the application root path in a better manner.
	private ElasticPath elasticPath;
	
	/**
	 * Gets the absolute path to the catalog assets folder on the file system.
	 * This implementation retrieves the path from the settings service.
	 * Calls {@link #isAbsolute(String)} to determine whether the configured path is relative, and if so, calls
	 * {@link #getAbsoluteCatalogAssetPathFromRelativePath(String)} to determine the absolute path.
	 * @return the absolute file system path to catalog assets.
	 * @throws com.elasticpath.base.exception.EpServiceException if for some reason the catalog asset path setting does not exist
	 */
	@Override
	public String getCatalogAssetPath() {
		String definedPath = getCatalogAssetPathFromSettingsService();
		
		if (!isAbsolute(definedPath)) {
			return getAbsoluteCatalogAssetPathFromRelativePath(definedPath);
		}
		return definedPath;
	}
	
	/**
	 * @return the catalog asset path as configured in the settings service
	 */
	String getCatalogAssetPathFromSettingsService() {
		return getSettingsReader().getSettingValue("COMMERCE/SYSTEM/ASSETS/assetLocation").getValue();
	}
	
	/**
	 * Takes in a relative path to the catalog assets directory and converts it
	 * to an absolute path, assuming that the given path is relative to the
	 * web application's root. Calls {{@link #getApplicationRootPath()}.
	 * 
	 * @param relativePath the relative path to the catalog assets directory
	 * @return the absolute path to the catalog assets directory
	 */
	String getAbsoluteCatalogAssetPathFromRelativePath(final String relativePath) {
		return FilenameUtils.concat(getApplicationRootPath(), relativePath);
	}
	
	/**
	 * Get the application's absolute root path.
	 * This implementation gets the path to the WEB-INF directory from the ElasticPath
	 * singleton, and strips off the trailing 'WEB-INF' portion to retrieve the root
	 * path to the web application.
	 * @return the root path to the application.
	 */
	String getApplicationRootPath() {
		StringBuffer sbf = new StringBuffer();
		String webInfPath = getElasticPath().getWebInfPath();
		if (StringUtils.isBlank(webInfPath)) {
			LOG.warn("WEB-INF path is undefined in ElasticPath object.");
		} else {
			sbf.append(webInfPath.replaceFirst("WEB-INF", ""));
		}
		return sbf.toString();
	}
	
	/**
	 * @return the file system path to catalog images.
	 */
	@Override
	public String getCatalogImagesPath() {
		return FilenameUtils.concat(getCatalogAssetPath(), getCatalogImagesSubfolder());
	}
	
	/**
	 * @return the name of the directory within which catalog images are stored.
	 */
	@Override
	public String getCatalogImagesSubfolder() {
		return normalizeSubfolder(getSettingsReader().getSettingValue("COMMERCE/SYSTEM/ASSETS/imageAssetsSubfolder").getValue());
	}
	
	/**
	 * @return the file system path to import files.
	 */
	@Override
	public String getImportAssetPath() {
		return FilenameUtils.concat(getCatalogAssetPath(), getImportAssetSubfolder());
	}
	
	/**
	 * @return the name of the directory within which import csv files are stored.
	 */
	String getImportAssetSubfolder() {
		return getSettingsReader().getSettingValue("COMMERCE/SYSTEM/ASSETS/importAssetsSubfolder").getValue();
	}
	
	/**
	 * @return the file system path to digital goods.
	 */
	@Override
	public String getCatalogDigitalGoodsPath() {
		return FilenameUtils.concat(getCatalogAssetPath(), getCatalogDigitalGoodsSubfolder());
	}
	
	/**
	 * @return the name of the directory within which catalog digital goods are stored
	 */
	@Override
	public String getCatalogDigitalGoodsSubfolder() {
		return getSettingsReader().getSettingValue("COMMERCE/SYSTEM/ASSETS/digitalGoodsAssetsSubfolder").getValue();
	}
	
	/**
	 * @return the file system path to store assets.
	 */
	@Override
	public String getStoreAssetsPath() {
		return FilenameUtils.concat(getCatalogAssetPath(), getStoreAssetsSubfolder());
	}
	
	/**
	 * @return the name of the top-level directory within which store assets are stored
	 */
	@Override
	public String getStoreAssetsSubfolder() {
		return getSettingsReader().getSettingValue("COMMERCE/SYSTEM/ASSETS/storeAssetsSubfolder").getValue();
	}

	/**
	 * @return the file system path to store assets.
	 */
	@Override
	public String getThemeAssetsPath() {
		return FilenameUtils.concat(getCatalogAssetPath(), getThemesSubfolder());
	}
	
	/**
	 * @return the name of the top-level directory within which themed resources are stored
	 */
	@Override
	public String getThemesSubfolder() {
		return getSettingsReader().getSettingValue("COMMERCE/SYSTEM/ASSETS/themesSubfolder").getValue();
	}
	
	/**
	 * @return the name of the top-level directory within which commerce manager assets are stored
	 */
	@Override
	public String getCmAssetsSubfolder() {
		return getSettingsReader().getSettingValue("COMMERCE/SYSTEM/ASSETS/cmAssetsSubfolder").getValue();
	}

	/**
	 * @return the file system path to dynamic content assets
	 */
	@Override
	public String getDynamicContentAssetsPath() {
		return FilenameUtils.concat(getCatalogAssetPath(), getDynamicContentAssetsSubfolder());
	}
	
	/**
	 * @return the name of the top-level directory within which dynamic content resources are stored
	 */
	@Override
	public String getDynamicContentAssetsSubfolder() {
		return getSettingsReader().getSettingValue("COMMERCE/SYSTEM/ASSETS/dynamicContentAssetsSubfolder").getValue();
	}
	
	/**
	 * @return the system path to content wrappers repository.
	 */
	@Override
	public String getContentWrappersPath() {
		final String contentWrapperSubDirectory = getSettingsReader().getSettingValue("COMMERCE/SYSTEM/ASSETS/contentWrappersLocation").getValue();
		
		return FilenameUtils.concat(getCatalogAssetPath(), contentWrapperSubDirectory);
	}
	
	/**
	 * Test hook to allow testing of getCatalogAssetPath().
	 * http://commons.apache.org/io/api-release/org/apache/commons/io/FilenameUtils.html
	 * @param path the path to check.
	 * @return true if the path represents an absolute path location, false otherwise.
	 */
	boolean isAbsolute(final String path) {
		return new File(path).isAbsolute();
	}

	/**
	 * @return the settingsReader
	 */
	public SettingsReader getSettingsReader() {
		return settingsReader;
	}

	/**
	 * @param settingsReader the settingsReader to set
	 */
	public void setSettingsReader(final SettingsReader settingsReader) {
		this.settingsReader = settingsReader;
	}

	@Override
	public String getAssetServerBaseUrl(final String storeCode) {
		SettingValue settingValue = settingsReader.getSettingValue("COMMERCE/STORE/ASSETS/assetServerBaseUrl", storeCode);
		if (settingValue == null) {
			throw new EpSystemException("The system setting COMMERCE/STORE/ASSETS/assetServerBaseUrl was not found in the database.");
		}
		return settingValue.getValue();
	}

	@Override
	public URL getAssetServerImagesUrl(final String storeCode) {
		String assetServerBaseUrl = getAssetServerBaseUrl(storeCode);
		try {
			return new URL(withTrailingSlash(assetServerBaseUrl));
		} catch (MalformedURLException e) {
			throw new EpServiceException("Could not construct valid URL from asset server base url = " + assetServerBaseUrl, e);
		}
	}
	
	private String withTrailingSlash(final String input) {
		if (StringUtils.isEmpty(input) || input.endsWith("/")) {
			return input;
		}
		return input + "/";
	}
	
	/**
	 * @return the elasticPath
	 */
	public ElasticPath getElasticPath() {
		return elasticPath;
	}

	/**
	 * @param elasticPath the elasticPath to set
	 */
	public void setElasticPath(final ElasticPath elasticPath) {
		this.elasticPath = elasticPath;
	}

	/**
	 * Normalize the subfolder.  Remove any leading/trailing slash as well.  Emits a warning if necessary.
	 * <pre>
	 * \\to\\images//	-->	to\images
	 * to//images//		-->	to/images
	 * <pre>
	 * @param subfolder The subfolder to normalize.
	 * @return the normalized subfolder
	 */
	private String normalizeSubfolder(final String subfolder) {
		String normalized = MULTIPLE_SLASHES.matcher(subfolder).replaceAll(Matcher.quoteReplacement(File.separator));
		normalized = LEADING_SLASH.matcher(normalized).replaceAll("");
		normalized = TRAILING_SLASH.matcher(normalized).replaceAll("");
		normalized = FilenameUtils.normalize(normalized);
		if (!normalized.equals(subfolder)) {
			LOG.warn("The path \"" + subfolder + "\" is not a valid subfolder location.  "
					+ "Please correct it in system configuration settings.  Using \""
					+ normalized + "\" instead.");
		}
		return normalized;
	}
}