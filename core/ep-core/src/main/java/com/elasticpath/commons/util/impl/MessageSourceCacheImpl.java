package com.elasticpath.commons.util.impl;

import java.io.File;
import java.io.FileFilter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.util.DefaultPropertiesPersister;
import org.springframework.util.PropertiesPersister;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.commons.util.AssetRepository;
import com.elasticpath.commons.util.InvalidatableCache;
import com.elasticpath.commons.util.MessageSourceCache;


/**
 * Map based message resource cache. Implemented to support the EP multi-store and theme features.
 */
public class MessageSourceCacheImpl implements MessageSourceCache, InvalidatableCache {

    private static final Logger LOG = Logger.getLogger(MessageSourceCacheImpl.class);

	private static final String DEFAULT = "default";
	private static final String SEPARATOR = "_";
    private static final int PROPERTIES_FILE_PARTS = 3;

	private final Map<String, Map<String, String>> storeThemes = new ConcurrentHashMap<String, Map<String, String>>();

	private boolean failIfAssetsMissing = false;

    //TODO:fortification also hard coded in StoreResourceManager
    private static final String VELOCITY_TEMPLATES_DIR = "templates" + File.separator + "velocity";

    /**
     * Allows for reading from any Reader with a specified charset. This is a capability that standard java.util.Properties
     * unfortunately lacks: You can only load files using the ISO-8859-1 charset there.
     */
    private final PropertiesPersister propertiesPersister = new DefaultPropertiesPersister();

    private AssetRepository assetRepository;

    private String defaultLanguage = "";


    /**
     * Set default locale. It shows how properties files without
     * language specification will be treated.
     * @param defaultLanguage the default language.
     */
	public void setDefaultLanguage(final String defaultLanguage) {
		this.defaultLanguage = defaultLanguage;
	}


	/**
     * Initialise this message source.
     *
     * @throws EpSystemException when store assets folder or themes not found, but only if failIfAssetsMissing is set to true
     */
    public void init() {
        String assetLocation = assetRepository.getCatalogAssetPath();

        File assetLocationFile = new File(assetLocation);

        String themesSubfolder = assetRepository.getThemesSubfolder();
    	String cmAssetsSubFolder = assetRepository.getCmAssetsSubfolder();

        File themeAssetsFile = new File(assetLocationFile, themesSubfolder);
        File cmAssetsDir = new File(assetLocationFile, cmAssetsSubFolder);

        try {
        	loadThemesProperties(themeAssetsFile);
        	loadCMProperties(cmAssetsDir);
        } catch (EpSystemException e) {
        	if (failIfAssetsMissing) {
        		throw e;
        	}
    		LOG.error(e.getMessage());
        }
    }

    /**
     * Load themes properties from the given assets folder.
     *
     * @param themeAssetsDir the folder in which to find store assets
     */
    void loadThemesProperties(final File themeAssetsDir) {

    	if (!themeAssetsDir.exists()) {
    		throw new EpSystemException("Theme asset directory not found: " + themeAssetsDir.getAbsolutePath());
    	}

    	File[] themeDirs = themeAssetsDir.listFiles(new FileFilter() {
    		public boolean accept(final File pathname) {
    			return pathname.isDirectory() && !pathname.getName().startsWith(".");
    		}
    	});

    	if (themeDirs == null || themeDirs.length == 0) {
    		throw new EpSystemException("No themes found in theme assets directory: " + themeAssetsDir.getAbsolutePath());
    	}

    	for (File themeDir : themeDirs) {
   			loadStorePropertiesForTheme(themeDir);
        }
    }


    private void loadCMProperties(final File cmAssetsDir) {
    	LOG.info("Loading cm store independent resources from: " + cmAssetsDir.getName());

	    List<File> propertyFiles = findPropertyFiles(cmAssetsDir);
	    for (File propertyFile : propertyFiles) {
	    	//Store is blank for global global properties
	        readProperties(cmAssetsDir.getName(), "", propertyFile);
	    }
    }


    private void loadStorePropertiesForTheme(final File themeDir) {
		File[] storeDirs = themeDir.listFiles(new FileFilter() {
		    public boolean accept(final File pathname) {
		        return pathname.isDirectory() && !pathname.getName().startsWith(".");
		    }
		});

		if (storeDirs == null || storeDirs.length == 0) {
			return;
		}

		for (File storeDir : storeDirs) {
			LOG.info("Loading resources for theme and store: " + themeDir.getName() + ", " + storeDir.getName());

		    List<File> propertyFiles = findPropertyFiles(storeDir);
		    for (File propertyFile : propertyFiles) {
		        readProperties(themeDir.getName(), storeDir.getName(), propertyFile);
		    }
		}
	}


    /**
     * Given a store directory, lists all .properties files recursively.
     *
     * @param storeDir The store root directory.
     * @return A List of properties files.
     */
    private List<File> findPropertyFiles(final File storeDir) {
        final List<File> files = new ArrayList<File>();

        File velocityDir = new File(storeDir, VELOCITY_TEMPLATES_DIR);
        if (velocityDir.exists()) {
            @SuppressWarnings("unchecked")
			final Iterator<File> iter = FileUtils.iterateFiles(velocityDir, new String[] { "properties" }, true);
            while (iter.hasNext()) {
                files.add(iter.next());
            }
        }

        return files;
    }

    /**
     * Reads the given a property file, and adds all properties to the message source cache.
     *
     * @param themeCode The theme this property file is associated with.
     * @param storeCode The store this property file is associated with.
     * @param propertyFile The properties file to load.
     */
    private void readProperties(final String themeCode, final String storeCode, final File propertyFile) {
        final Properties properties = new Properties();
        try {
            propertiesPersister.load(properties, new InputStreamReader(propertyFile.toURL().openStream(), "UTF-8"));
        } catch (Exception e) {
            LOG.error("PropertiesPersister failed to read properties file: " + propertyFile, e);
        }

        for (Iterator<Object> iter = properties.keySet().iterator(); iter.hasNext();) {
            final String propertyKey = iter.next().toString();
            final String propertyValue = properties.getProperty(propertyKey);
            final Locale locale = parseLocale(propertyFile.getName());

            this.addProperty(themeCode, storeCode, propertyKey, propertyValue, locale);
        }
    }


    /**
     * Parse filename to get locale.
     *
     * <br>name_en.properties
     * <br>name_fr.properties
     * <br>name_es.properties
     *
     * <br>name.properties filenames without language specification will be treated
     * as pointed by <code>delaultLanguage</code> instead of OS locale.
     *
     * @param filename The property file name.
     * @return The locale parsed from the file name.
     */
    Locale parseLocale(final String filename) {
        String name = filename.split("\\.")[0];
        String[] parts = name.split("_");

        // defaults
        String language = Locale.getDefault().getLanguage();
        String country = Locale.getDefault().getCountry();

        // read the locale (country and language) from the back
        if (parts.length >= PROPERTIES_FILE_PARTS) {
            country = parts[parts.length - 1];
            language = parts[parts.length - 2];

        } else if (parts.length == PROPERTIES_FILE_PARTS - 1) {
            country = "";
            language = parts[parts.length - 1];
        } else if (parts.length == 1) {
        	// Locale for files without language specification (filename_lg, where lg is language)
        	// will be created as pointed by delaultLanguage.
        	country = "";
        	language = defaultLanguage;
        }

        return new Locale(language, country);
    }

	/**
	 * Method to add a property to the cache.
	 *
	 * @param themeCode The theme this store property is associated with.
	 * @param storeCode The store this property is associated with.
	 * @param propertyKey The unique property key.
	 * @param propertyValue The property value.
	 * @param locale The locale of the property value, such as 'en' and 'fr'.
	 */
	public void addProperty(final String themeCode, final String storeCode,
			final String propertyKey, final String propertyValue, final Locale locale) {

		Map<String, String> storeThemeMap = getStoreThemeMap(themeCode, storeCode);

		storeThemeMap.put(getPropertyLocaleKey(propertyKey, locale), propertyValue);
	}

	/**
	 * Returns the property value from the cache.
	 *
	 * @param themeCode The theme this store property is associated with.
	 * @param storeCode The store this property is associated with.
	 * @param propertyKey The unique property key.
	 * @param locale The locale of the property value, such as 'en' and 'fr'.
	 * @return The property value. Null if it does not exist.
	 */
	public String getProperty(final String themeCode,
			final String storeCode, final String propertyKey, final Locale locale) {

		String value = null;

		Map<String, String> storeThemeMap = storeThemes.get(getStoreThemeKey(themeCode, storeCode));
		if (storeThemeMap != null) {
			value = getPropertyWithLocaleFallback(propertyKey, locale, storeThemeMap);
		}

		if (value == null) {
			// no such store/theme configuration, use 'default' store map instead
			storeThemeMap = storeThemes.get(getStoreThemeKey(themeCode, DEFAULT));
			if (storeThemeMap != null) {
				value = getPropertyWithLocaleFallback(propertyKey, locale, storeThemeMap);
			}
		}

		return value;
	}

	/**
	 * Get a property value by trying with the full locale, and if not found, try the more generic locale.
	 *
	 * @param propertyKey key to the property
	 * @param locale of the property to get, may include language AND country. NOTE: Variant is ignored here like rest of system.
	 * @param storeThemeMap map of constructed property keys.
	 * @return value if found
	 */
	protected String getPropertyWithLocaleFallback(final String propertyKey, final Locale locale, final Map<String, String> storeThemeMap) {
		String value = storeThemeMap.get(getPropertyLocaleKey(propertyKey, locale));
		if (StringUtils.isEmpty(value) && !StringUtils.isEmpty(locale.getCountry())) {
			value = storeThemeMap.get(getPropertyLocaleKey(propertyKey, new Locale(locale.getLanguage())));
		}
		return value;
	}

	private Map<String, String> getStoreThemeMap(final String themeCode, final String storeCode) {
		Map<String, String> storeThemeMap = storeThemes.get(getStoreThemeKey(themeCode, storeCode));
		if (storeThemeMap == null) {
			storeThemeMap = new ConcurrentHashMap<String, String>();

			storeThemes.put(getStoreThemeKey(themeCode, storeCode), storeThemeMap);
		}

		return storeThemeMap;
	}

	private String getStoreThemeKey(final String themeCode, final String storeCode) {
		String storeThemeKey = themeCode + SEPARATOR + storeCode;

		return storeThemeKey;
	}

	private String getPropertyLocaleKey(final String propertyKey, final Locale locale) {
		if (locale == null) {
			throw new IllegalArgumentException("Property locale is required.");
		}

		String propertyLocaleKey = propertyKey + SEPARATOR + locale.toString();

		return propertyLocaleKey;
	}

	/**
	 * Print the cache data.
	 *
	 * @return String representation of the cache.
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		for (String storeThemeKey : storeThemes.keySet()) {
			Map<String, String> storeThemeMap = storeThemes.get(storeThemeKey);
			for (String propertyKey : storeThemeMap.keySet()) {
				builder.append("\n[" + storeThemeKey + "] " + propertyKey + ": " + storeThemeMap.get(propertyKey));
			}
		}

		return builder.toString();
	}

    /**
	 * @param assetRepository the assetRepository to set
	 */
	public void setAssetRepository(final AssetRepository assetRepository) {
		this.assetRepository = assetRepository;
	}

    /**
	 * Set whether the init should fail if the assets can't be found.
	 *
	 * @param failIfAssetsMissing set to true to throw an exception when the assets are not found.
	 */
	public void setFailIfAssetsMissing(final boolean failIfAssetsMissing) {
		this.failIfAssetsMissing = failIfAssetsMissing;
	}

	/**
	 * Invalidates the message cache and re-crawls the assets directories for properties files.
	 */
	public void invalidate() {
		storeThemes.clear();
		init();
	}

	@Override
	public void invalidate(final Object objectUid) {
		//TODO: implement if necessary

	}

}
