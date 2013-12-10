/*
 * Copyright (c) Elastic Path Software Inc., 2005
 */
package com.elasticpath.commons.util.impl;

import java.io.File;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.constants.GlobalConstants;
import com.elasticpath.commons.constants.SeoConstants;
import com.elasticpath.commons.util.PasswordGenerator;
import com.elasticpath.commons.util.Utility;
import com.elasticpath.domain.store.CreditCardType;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.store.StoreService;
import com.elasticpath.settings.SettingsReader;

/**
 * The default implementation of <code>Utility</code>.
 */
public class UtilityImpl implements Utility {

	private static final long serialVersionUID = 5000000001L;

	private static final int MONTHS_IN_YEAR = 12;

	private static final int NUM_EXPIRY_YEARS = 18;

	private static Map<String, String> monthMap = null;

	private static Map<String, String> yearMap = null;

	private static final String SETTING_LOCALE_DATE_FORMAT = "COMMERCE/SYSTEM/localeDateFormat";
	private static final Map<Character, Character> WESTERN_TO_ENGLISH_MAP = new HashMap<Character, Character>();

	private BeanFactory beanFactory;
	private SettingsReader settingsReader;

	static {
		monthMap = new LinkedHashMap<String, String>();
		for (int i = 1; i <= MONTHS_IN_YEAR; i++) {
			String month = String.valueOf(i);
			if (month.length() == 1) {
				month = "0" + month;
			}
			monthMap.put(month, month);
		}

		yearMap = new LinkedHashMap<String, String>();
		GregorianCalendar calendar = new GregorianCalendar();
		int currentYear = calendar.get(Calendar.YEAR);
		for (int i = 0; i <= NUM_EXPIRY_YEARS; i++) {
			String yearToAdd = String.valueOf(currentYear + i);
			yearMap.put(yearToAdd, yearToAdd);
		}

		for (char[] chars : SeoConstants.WESTERN_TO_ENGLISH_ARRAY) {
			WESTERN_TO_ENGLISH_MAP.put(chars[0], chars[1]);
		}
	}

	@Override
	public String getLocalizedFile(final String baseDir, final String filePath, final String fileExtension, final Locale locale) {
		if (baseDir == null) {
			throw new EpSystemException("Base directory cannot be null.");
		}
		if (filePath == null) {
			throw new EpSystemException("File path cannot be null.");
		}
		if (fileExtension == null) {
			throw new EpSystemException("File extension cannot be null.");
		}
		if (locale == null) {
			throw new EpSystemException("Locale cannot be null.");
		}
		if (!filePath.endsWith(fileExtension)) {
			throw new EpSystemException("The file path doesn't end with the given file extension.");
		}

		String localizedFilePath;
		final String fpWithoutSuffix = filePath.substring(0, filePath.indexOf(fileExtension));

		// Get the locale specific email template
		final String fpWithLocale = fpWithoutSuffix + "_" + locale.getLanguage().toLowerCase() + "_" + locale.getCountry().toUpperCase()
				+ fileExtension;
		File templateFile = new File(baseDir + File.separator + fpWithLocale);
		if (templateFile.exists()) {
			localizedFilePath = fpWithLocale;
		} else {
			String fpWithLang = fpWithoutSuffix + "_" + locale.getLanguage().toLowerCase() + fileExtension;
			templateFile = new File(baseDir + File.separator + fpWithLang);
			if (templateFile.exists()) {
				localizedFilePath = fpWithLang;
			} else {
				localizedFilePath = filePath;
			}
		}
		return localizedFilePath;
	}

	@Override
	@Deprecated
	public String escapeName2UrlFriendly(final String name) {
		return escapeName2UrlFriendly(name, Locale.getDefault());
	}

	@Override
	public String escapeName2UrlFriendly(final String name, final Locale locale) {
		// Currently, we only allow alphabetic and numbers in a seo url.
		// In some cases, you might need to use URLEncoder to encode url-nonfriendly characters.

		// convert the western European characters to English
		StringBuffer buf = new StringBuffer(name.trim());
		for (int x = 0; x < buf.length(); x++) {
			char character = buf.charAt(x);
			Character replaceChar = WESTERN_TO_ENGLISH_MAP.get(character);
			if (replaceChar == null) {
				continue;
			}
			buf.setCharAt(x, replaceChar);
		}

		return buf.toString().toLowerCase(locale).replaceAll("[^\\p{Alnum}]+", "-");
	}

	/**
	 * Returns the system-default date formatting string and locale.
	 * @return the system-default date formatting string and locale
	 */
	@Override
	public LocalizedDateFormat getDefaultLocalizedDateFormat() {
		return new LocalizedDateFormat(getDefaultDateFormatPattern(), getDefaultDateFormatLocale());
	}

	/**
	 * Default pattern to be used, when one is not send.
	 * @return the default date format
	 * @throws com.elasticpath.base.exception.EpServiceException if the locale date format setting cannot be retrieved for any reason
	 */
	protected String getDefaultDateFormatPattern() {
		return getSettingsReader().getSettingValue(SETTING_LOCALE_DATE_FORMAT, "").getValue();
	}

	/**
	 * Locale to be used by methods that are not sending one.
	 *
	 * @return {@link Locale} to be used in the {@link java.text.DateFormat}
	 */
	protected Locale getDefaultDateFormatLocale() {
		return Locale.getDefault();
	}

	@Override
	public boolean checkShortTextMaxLength(final String value) {
		if (value == null) {
			return true;
		}
		return (value.length() <= GlobalConstants.SHORT_TEXT_MAX_LENGTH);
	}

	@Override
	public boolean checkLongTextMaxLength(final String value) {
		if (value == null) {
			return true;
		}
		return (value.length() <= GlobalConstants.LONG_TEXT_MAX_LENGTH);
	}

	@Override
	public boolean isValidGuidStr(final String string) {
		if (string == null) {
			return false;
		}
		return string.matches("^[\\p{Alnum}\\-_]+$");
	}

	@Override
	public boolean isValidZipPostalCode(final String zipPostalCode) {
		if (zipPostalCode == null) {
			return false;
		}
		return zipPostalCode.matches("^\\p{Alnum}[\\p{Alnum}\\s]+\\p{Alnum}$");
	}

	@Override
	public Map<String, String> getMonthMap() {
		return monthMap;
	}

	@Override
	public Map<String, String> getYearMap() {
		return yearMap;
	}

	@Override
	public Map<String, String> getStoreCreditCardTypesMap(final String storeCode) {
		StoreService storeService = getBeanFactory().getBean(ContextIdNames.STORE_SERVICE);
		Store store = storeService.findStoreWithCode(storeCode);
		Set <CreditCardType> cardTypes = store.getCreditCardTypes();
		Map <String, String> supportedCardMap = new TreeMap <String, String>(String.CASE_INSENSITIVE_ORDER);
		for (CreditCardType card : cardTypes) {
			supportedCardMap.put(card.getCreditCardType(), card.getCreditCardType());
		}
		return supportedCardMap;
	}

	@Override
	@Deprecated
	public Map <String, String> getAllCreditCardTypesMap() {

		Map <String, String> allCreditCardsMap = new LinkedHashMap <String, String>();

		StoreService storeService = getBeanFactory().getBean(ContextIdNames.STORE_SERVICE);
		List <Store> stores = storeService.findAllCompleteStores();

		for (Store s : stores) {
			allCreditCardsMap.putAll(getStoreCreditCardTypesMap(s.getCode()));
		}
		return allCreditCardsMap;
	}

	@Override
	public String getRandomStringWithLength(final int length) {
		final PasswordGenerator passwordGeneraor = getBeanFactory().getBean("passwordGenerator");
		return passwordGeneraor.getPassword().substring(0, length);
	}

	protected BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	protected SettingsReader getSettingsReader() {
		return settingsReader;
	}

	public void setSettingsReader(final SettingsReader settingsReader) {
		this.settingsReader = settingsReader;
	}
}
