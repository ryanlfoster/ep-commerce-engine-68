package com.elasticpath.commons.util.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.Locale;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.commons.util.AssetRepository;

/**
 * MessageSourceCacheImpl test.
 * 
 */
public class MessageSourceCacheImplTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private MessageSourceCacheImpl cache;
	private final Locale localeEn = new Locale("en");
	private final Locale localeFr = new Locale("fr");

	private static final String THEME_GLOBAL = "global";
	private static final String THEME_1 = "theme1";
	private static final String THEME_2 = "theme2";
	
	private static final String STORE_1 = "store1";
	private static final String STORE_2 = "store2";
	
	private static final String KEY_1 = "key1";
	private static final String KEY_2 = "key2";
	
	/**
	 * Set up objects required for all tests.
	 * 
	 * @exception Exception in case of error
	 */
	@Before
	public void setUp() throws Exception {
		cache = new MessageSourceCacheImpl() {
			/**
			 * Mock out the load themes method for test purposes.
			 * @param themedAssetsDir the store assets folder.
			 */
			@Override
			void loadThemesProperties(final File themedAssetsDir) {
				// Doesn't need to do anything
			}
			
		};
		
		cache.setDefaultLanguage("ru");

	}

	private AssetRepository setupAssetRepository() {
		final AssetRepository mockAssetRepository = context.mock(AssetRepository.class);
		context.checking(new Expectations() {
			{
				oneOf(mockAssetRepository).getCatalogAssetPath();
				will(returnValue("nosuchfolder"));

				oneOf(mockAssetRepository).getThemesSubfolder();
				will(returnValue("nosuchfolder"));

				oneOf(mockAssetRepository).getCmAssetsSubfolder();
				will(returnValue("nosuchfolder"));
			}
		});
		
		return mockAssetRepository;
	}

	private String getValue(final String theme, final String store, final String key, final Locale locale) {
		return theme + "-" + store + "-" + key + "-" + locale.toString();
	}
	
	/**
	 * Test adding values and getting properties.
	 */
	@Test
	public void testAddAndGetProperty() {
		
		cache.addProperty(THEME_1, STORE_1, KEY_1, getValue(THEME_1, STORE_1, KEY_1, localeEn), localeEn);
		cache.addProperty(THEME_1, STORE_1, KEY_2, getValue(THEME_1, STORE_1, KEY_2, localeEn), localeEn);
		
		cache.addProperty(THEME_1, STORE_2, KEY_1, getValue(THEME_1, STORE_2, KEY_1, localeEn), localeEn);
		cache.addProperty(THEME_1, STORE_2, KEY_2, getValue(THEME_1, STORE_2, KEY_2, localeEn), localeEn);
		
		cache.addProperty(THEME_2, STORE_1, KEY_1, getValue(THEME_2, STORE_1, KEY_1, localeEn), localeEn);
		cache.addProperty(THEME_2, STORE_1, KEY_2, getValue(THEME_2, STORE_1, KEY_2, localeEn), localeEn);
		
		cache.addProperty(THEME_2, STORE_2, KEY_1, getValue(THEME_2, STORE_2, KEY_1, localeEn), localeEn);
		cache.addProperty(THEME_2, STORE_2, KEY_2, getValue(THEME_2, STORE_2, KEY_2, localeEn), localeEn);
		
		cache.addProperty(THEME_GLOBAL, "", KEY_1, getValue(THEME_GLOBAL, "", KEY_1, localeEn), localeEn);

		// test it now
		assertEquals(getValue(THEME_1, STORE_1, KEY_1, localeEn), cache.getProperty(THEME_1, STORE_1, KEY_1, localeEn));
		assertEquals(getValue(THEME_1, STORE_1, KEY_2, localeEn), cache.getProperty(THEME_1, STORE_1, KEY_2, localeEn));
		
		assertEquals(getValue(THEME_1, STORE_2, KEY_1, localeEn), cache.getProperty(THEME_1, STORE_2, KEY_1, localeEn));
		assertEquals(getValue(THEME_1, STORE_2, KEY_2, localeEn), cache.getProperty(THEME_1, STORE_2, KEY_2, localeEn));
		
		assertEquals(getValue(THEME_2, STORE_1, KEY_1, localeEn), cache.getProperty(THEME_2, STORE_1, KEY_1, localeEn));
		assertEquals(getValue(THEME_2, STORE_1, KEY_2, localeEn), cache.getProperty(THEME_2, STORE_1, KEY_2, localeEn));
		
		assertEquals(getValue(THEME_2, STORE_2, KEY_1, localeEn), cache.getProperty(THEME_2, STORE_2, KEY_1, localeEn));
		assertEquals(getValue(THEME_2, STORE_2, KEY_2, localeEn), cache.getProperty(THEME_2, STORE_2, KEY_2, localeEn));
		
		assertEquals(getValue(THEME_GLOBAL, "", KEY_1, localeEn), cache.getProperty(THEME_GLOBAL, "", KEY_1, localeEn));
	}
	
	/**
	 * testParseLocale.
	 */
	@Test
	public void testParseLocale() {
		assertEquals(new Locale("en", ""), cache.parseLocale("test_en.properties"));
		assertEquals(new Locale("en", "US"), cache.parseLocale("test_en_US.properties"));
		
		assertEquals(new Locale("fr", ""), cache.parseLocale("test_fr.properties"));
		assertEquals(new Locale("fr", "CA"), cache.parseLocale("test_fr_CA.properties"));
		
		assertEquals(new Locale("12", ""), cache.parseLocale("test_12.properties"));
		
		assertEquals(new Locale("ru"), cache.parseLocale("test_.properties"));
		assertEquals(new Locale("ru"), cache.parseLocale("test.properties"));
	}
	
	/**
	 * testGetPropertyLocales.
	 */
	@Test
	public void testGetPropertyLocales() {
		
		cache.addProperty(THEME_1, STORE_1, KEY_1, "value_en", localeEn);
		cache.addProperty(THEME_1, STORE_1, KEY_1, "value_fr", localeFr);
		
		assertEquals("value_en", cache.getProperty(THEME_1, STORE_1, KEY_1, localeEn));
		assertEquals("value_fr", cache.getProperty(THEME_1, STORE_1, KEY_1, localeFr));
	}
	
	/**
	 * testGetPropertyLocale with country codes.
	 */
	@Test
	public void testGetPropertyLocaleWithCountry() {
		Locale localeIEEnglish = new Locale("en", "IE");
		Locale localePHEnglish = new Locale("en", "PH");
		String localeValue1 = "value_en_ie";
		String localeValue2 = "value_en_ph";
		cache.addProperty(THEME_1, STORE_1, KEY_1, localeValue1, localeIEEnglish);
		cache.addProperty(THEME_1, STORE_1, KEY_1, localeValue2, localePHEnglish);
		
		assertEquals(localeValue1, cache.getProperty(THEME_1, STORE_1, KEY_1, localeIEEnglish));
		assertEquals(localeValue2, cache.getProperty(THEME_1, STORE_1, KEY_1, localePHEnglish));
	}
	
	/**
	 * Check there is fallback to just language when there is a locale with country.
	 */
	@Test
	public void testGetPropertyLocaleWithCountryFallback() {
		Locale localeIEEnglish = new Locale("en", "IE");
		Locale localeEnglish = new Locale("en");
		String localeValue1 = "value_en";
		cache.addProperty(THEME_1, STORE_1, KEY_1, localeValue1, localeEnglish);
		
		assertEquals(localeValue1, cache.getProperty(THEME_1, STORE_1, KEY_1, localeIEEnglish));
	}
	
	/**
	 * Test the init method uses the settings service appropriately.
	 */
	@Test
	public void testInit() {
		AssetRepository mockAssetRepository = setupAssetRepository();
		cache.setAssetRepository(mockAssetRepository);
		cache.init();
	}
	
	/**
	 * Test that an exception is thrown during init if we have asked it to fail when assets are not present.
	 */
	@Test
	public void testInitFailsAppropriately() {
		MessageSourceCacheImpl exceptionCache = new MessageSourceCacheImpl(); 
		AssetRepository mockAssetRepository = setupAssetRepository();
		exceptionCache.setAssetRepository(mockAssetRepository);
		exceptionCache.setFailIfAssetsMissing(true);
		
		try {
			exceptionCache.init();
			fail("Exception should be thrown when init called with non-existant assets location");
		} catch (EpSystemException e) {
			assertNotNull("An exception was expected", e.getMessage());
		}
	}
}
