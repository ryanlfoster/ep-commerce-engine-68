/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.impl;

import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.beanframework.MessageSource;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.ElasticPath;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.catalogview.AttributeRangeFilter;
import com.elasticpath.domain.catalogview.AttributeValueFilter;
import com.elasticpath.domain.catalogview.PriceFilter;
import com.elasticpath.service.catalogview.filterednavigation.FilteredNavigationConfiguration;
import com.elasticpath.settings.SettingsService;

/**
 * This class provides elastic path configurations and context to others.
 */
public class ElasticPathImpl implements ElasticPath, BeanFactory, MessageSource, FilteredNavigationConfiguration {

	private static ElasticPath epInstance = new ElasticPathImpl();

	private String webInfPath;

	private BeanFactory beanFactory;

	private MessageSource messageSource;

	private FilteredNavigationConfiguration filteredNavigationConfiguration;

	/**
	 * Return the singleton <code>ElasticPath</code>. Create default EP if one hasn't been created already.
	 *
	 * @return the singleton <code>ElasticPath</code>
	 */
	public static ElasticPath getInstance() {
		return epInstance;
	}

	/**
	 * Default constructor.
	 */
	protected ElasticPathImpl() {
		super();
	}

	@Override
	public <T> T getBean(final String name) {
		return beanFactory.getBean(name);
	}

	@Override
	public <T> Class<T> getBeanImplClass(final String beanName) {
		return beanFactory.getBeanImplClass(beanName);
	}

	/**
	 * Gets the localized message.
	 *
	 * @param code - the message key.
	 * @param args - the args for the message if needed.
	 * @param defaultMessage - the default message to display if the given message key does not exists.
	 * @param locale - the locale that message needs to be in.
	 * @return localized message string.
	 */
	@Override
	public String getMessage(final String code, final Object[] args, final String defaultMessage, final Locale locale) {
		return messageSource.getMessage(code, args, defaultMessage, locale);
	}

	/**
	 * Returns a map used by Spring/Velocity for checkboxes.
	 *
	 * @return a map containing {Boolean.TRUE -> ""}
	 */
	@Override
	public Map<Boolean, String> getBoolMap() {
		Map<Boolean, String> boolMap = new HashMap<Boolean, String>();
		boolMap.put(Boolean.TRUE, "");
		return boolMap;
	}

	/**
	 * Return the absolute WEB-INF directory path.
	 *
	 * @return the absolute WEB-INF directory path
	 */
	@Override
	public String getWebInfPath() {
		return webInfPath;
	}

	/**
	 * Set the absolute path to the WEB-INF directory.
	 *
	 * @param webInfPath the absolute path
	 */
	@Override
	public void setWebInfPath(final String webInfPath) {
		this.webInfPath = webInfPath;
	}

	/**
	 * @return the wrapped FilteredNavigationConfiguration implementation.
	 */
	FilteredNavigationConfiguration getFilteredNavigationConfiguration() {
		if (filteredNavigationConfiguration == null) {
			filteredNavigationConfiguration = beanFactory.getBean("filteredNavigationConfiguration");
		}
		return filteredNavigationConfiguration;
	}

	/**
	 * Returns all defined price ranges as a <code>Map</code>.
	 * <p>
	 * The key will be like : "price-between-USD-90-and-100".
	 * <p>
	 * And the value will be a <code>PriceFilter</code>.
	 * This implementation calls {@link #getFilteredNavigationConfiguration()} so that it can
	 * delegate to the FilteredNavigationConfiguration object.
	 *
	 * @return all defined price ranges as a <code>Map</code>.
	 */
	@Override
	public Map<String, PriceFilter> getAllPriceRanges() {
		return getFilteredNavigationConfiguration().getAllPriceRanges();
	}

	/**
	 * Clears all price range information, including bottom level price ranges.
	 * This implementation calls {@link #getFilteredNavigationConfiguration()} so that it can
	 * delegate to the FilteredNavigationConfiguration object.
	 */
	@Override
	public void clearAllPriceRanges() {
		getFilteredNavigationConfiguration().clearAllPriceRanges();
	}

	/**
	 * Returns price ranges of the given currency, which are defined at bottom level of the price-range-tree. This is a lookup map for a
	 * <code>Product</code> to decide which price range it should belongs to.
	 * <p>
	 * The key and the value will be the same <code>PriceFilter</code>.
	 * This implementation calls {@link #getFilteredNavigationConfiguration()} so that it can
	 * delegate to the FilteredNavigationConfiguration object.
	 *
	 * @param currency the currency
	 * @return price ranges of the given currency, which are defined at bottom level
	 */
	@Override
	public SortedMap<PriceFilter, PriceFilter> getBottomLevelPriceRanges(final Currency currency) {
		return getFilteredNavigationConfiguration().getBottomLevelPriceRanges(currency);
	}

	/**
	 * Returns all defined attribute ranges as a <code>Map</code>.
	 * <p>
	 * The attribute code will be used as the map key .
	 * <p>
	 * And the value will be a <code>AttributeRangeFilter</code>.
	 * This implementation calls {@link #getFilteredNavigationConfiguration()} so that it can
	 * delegate to the FilteredNavigationConfiguration object.
	 *
	 * @return all defined attribute ranges as a <code>Map</code>.
	 */
	@Override
	public Map<String, AttributeRangeFilter> getAllAttributeRanges() {
		return getFilteredNavigationConfiguration().getAllAttributeRanges();
	}

	/**
	 * Clears all attribute range information, including bottom level attribute ranges.
	 * This implementation calls {@link #getFilteredNavigationConfiguration()} so that it can
	 * delegate to the FilteredNavigationConfiguration object.
	 */
	@Override
	public void clearAllAttributeRanges() {
		getFilteredNavigationConfiguration().clearAllAttributeRanges();
	}

	/**
	 * Returns all defined attribute simple value filters as a <code>Map</code>.
	 * <p>
	 * The filter id will be used as the map key .
	 * <p>
	 * And the value will be a <code>AttributeFilter</code>.
	 * This implementation calls {@link #getFilteredNavigationConfiguration()} so that it can
	 * delegate to the FilteredNavigationConfiguration object.
	 *
	 * @return all defined attribute filters as a <code>Map</code>.
	 */
	@Override
	public Map<String, AttributeValueFilter> getAllAttributeSimpleValues() {
		return getFilteredNavigationConfiguration().getAllAttributeSimpleValues();
	}

	/**
	 * Clears all attribute simple value information.
	 * This implementation calls {@link #getFilteredNavigationConfiguration()} so that it can
	 * delegate to the FilteredNavigationConfiguration object.
	 */
	@Override
	public void clearAllAttributeSimpleValues() {
		getFilteredNavigationConfiguration().clearAllAttributeSimpleValues();
	}

	/**
	 * Returns attribute ranges of the given attributeCode, which are defined at bottom level of the attribute-range-tree. This is a lookup map for a
	 * <code>Product</code> to decide which attribute range it should belongs to.
	 * <p>
	 * The key and the value will be the same <code>AttributeRangeFilter</code>.
	 * This implementation calls {@link #getFilteredNavigationConfiguration()} so that it can
	 * delegate to the FilteredNavigationConfiguration object.
	 *
	 * @param attributeCode the attributeCode
	 * @return attribute ranges of the given attribute code, which are defined at bottom level
	 */
	@Override
	public SortedMap<AttributeRangeFilter, AttributeRangeFilter> getBottomLevelAttributeRanges(final String attributeCode) {
		return getFilteredNavigationConfiguration().getBottomLevelAttributeRanges(attributeCode);
	}

	/**
	 * {@inheritDoc}
	 * This implementation calls {@link #getFilteredNavigationConfiguration()} so that it can
	 * delegate to the FilteredNavigationConfiguration object.
	 */
	@Override
	public SortedMap<String, AttributeValueFilter> getAttributeSimpleValuesMap(final String attributeCode) {
		return getFilteredNavigationConfiguration().getAttributeSimpleValuesMap(attributeCode);
	}

	/**
	 * This map contains all the attributes defined in the filtered navigation configuration.
	 * The key will be the attribute code, and the value will be the <code>Attribute</code>.
	 * This implementation calls {@link #getFilteredNavigationConfiguration()} so that it can
	 * delegate to the FilteredNavigationConfiguration object.
	 *
	 * @return All the attributes.
	 */
	@Override
	public Map<String, Attribute> getAllAttributesMap() {
		return getFilteredNavigationConfiguration().getAllAttributesMap();
	}

	/**
	 * Sets the bean factory object.
	 *
	 * @param beanFactory the bean factory instance.
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * Sets the message source object that can provide localized messages.
	 *
	 * @param messageSource the message source instance.
	 */
	public void setMessageSource(final MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	/**
	 * Return the setting value of the given path.
	 *
	 * @param path the path of the setting
	 * @return the value for a given path and context
	 */
	protected String getSettingValue(final String path) {
		SettingsService settingsService = beanFactory.getBean(ContextIdNames.SETTINGS_SERVICE);
		return settingsService.getSettingValue(path).getValue();
	}

	@Override
	public Set<String> getAllSimpleAttributeKeys() {
		return filteredNavigationConfiguration.getAllSimpleAttributeKeys();
	}

	@Override
	public void clearAllBrandCodes() {
		filteredNavigationConfiguration.clearAllBrandCodes();

	}

	@Override
	public Set<String> getAllBrandCodes() {
		return filteredNavigationConfiguration.getAllBrandCodes();
	}

}
