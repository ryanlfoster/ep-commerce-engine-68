/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.service.catalogview.filterednavigation.impl;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedMap;

import org.apache.commons.lang.LocaleUtils;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalogview.AttributeFilter;
import com.elasticpath.domain.catalogview.AttributeRangeFilter;
import com.elasticpath.domain.catalogview.AttributeValueFilter;
import com.elasticpath.domain.catalogview.FilterDisplayInfo;
import com.elasticpath.domain.catalogview.PriceFilter;
import com.elasticpath.domain.catalogview.RangeFilter;
import com.elasticpath.domain.catalogview.impl.FilterDisplayInfoImpl;
import com.elasticpath.persistence.api.EpPersistenceException;
import com.elasticpath.service.catalogview.FilterFactory;
import com.elasticpath.service.catalogview.filterednavigation.FilteredNavigationConfiguration;
import com.elasticpath.service.catalogview.filterednavigation.FilteredNavigationConfigurationParser;

/**
 * Parses the XML representation of the filtered navigation configuration; creates all the filters
 * defined by the configuration and populates a given FilteredNavigationConfiguration object.
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
public class FilteredNavigationConfigurationXmlParserImpl implements FilteredNavigationConfigurationParser {
	private static final Logger LOG = Logger.getLogger(FilteredNavigationConfigurationXmlParserImpl.class);
	
	private BeanFactory beanFactory;
	
	private FilterFactory filterFactory;
	
	private static final String SEO_ID = "id";
	
	private static final String ATTRIBUTE = "attribute";
	
	private static final String VALUE = "value";

	private static final String ATTRIBUTE_RANGE = "attributeRange";

	private static final String PRICE = "price";
	
	private static final String ATTRIBUTE_KEY = "key";

	private static final String ATTRIBUTE_SIMPLE_VALUE = "simple";
	
	private static final String LOCALIZED = "localized";
	
	private static final String LANGUAGE = "language";
	
	private static final String RANGE = "range";
	
	private static final String UPPER_VALUE = "upper";

	private static final String LOWER_VALUE = "lower";
	
	private static final String DISPLAY_INFO = "display";
	
	private static final String TRUE = "true";

	private static final String BRANDS = "brands";

	private static final String BRAND = "brand";
	
	private static final String BRAND_KEY = "key";
	
	/**
	 * Parses the given FilteredNavigation XML configuration file and populated the
	 * given configuration object.
	 * @param filteredNavigationConfigurationXml the xml stream to parse
	 * @param config the configuration object to populate
	 */
	@Override
	public void parse(final InputStream filteredNavigationConfigurationXml, final FilteredNavigationConfiguration config) {
		// Create the document
		final Document doc = constructIntelligentBrowsingXmlDocument(filteredNavigationConfigurationXml);
		
		// Reset any existing price ranges to avoid duplicate entries when the file is
		// read multiple times due to restart by the application server
		config.clearAllPriceRanges();
		config.clearAllAttributeRanges();
		config.clearAllAttributeSimpleValues();
		config.clearAllBrandCodes();

		final Element msRootNode = doc.getRootElement();
		final List<Element> children = getChildren(msRootNode);
		for (Element sectionElement : children) {
			if (sectionElement.getName().equals(ATTRIBUTE)) {
				parseAttributeNode(sectionElement, config);
			} else if (sectionElement.getName().equals(ATTRIBUTE_RANGE)) {
				parseAttributeRangeNode(sectionElement, config);
			} else if (sectionElement.getName().equals(PRICE)) {
				parsePriceNode(sectionElement, config);
			} else if (sectionElement.getName().equals(BRANDS)) {
				parseBrandNode(sectionElement, config);
			}
		}
	}

	/**
	 * Get the intelligent browsing configuration xml from the settings service
	 * and parse it into a JDOM Document.
	 * @param intelligentBrowsingXmlStream the XML document as a string
	 * @return the Document, or null if there was a parsing error
	 */
	Document constructIntelligentBrowsingXmlDocument(final InputStream intelligentBrowsingXmlStream) {
		// Build the document with SAX and Xerces, no validation
		final SAXBuilder builder = new SAXBuilder();
		Document doc = null;
		try {
			doc = builder.build(intelligentBrowsingXmlStream);
		} catch (JDOMException e) {
			LOG.error("Exception parsing intelligent browsing xml configuration.", e);
		} catch (IOException e) {
			LOG.error("Exception parsing intelligent browsing xml configuration.", e);
		}
		return doc;
	}
	
	private void parseAttributeNode(final Element sectionElement, final FilteredNavigationConfiguration config) {
		if (!sectionElement.getName().equals(ATTRIBUTE)) {
			throw new EpPersistenceException("Not an attribute node:" + sectionElement.getName());
		}

		final String attributeKey = sectionElement.getAttributeValue(ATTRIBUTE_KEY);
		if (config.getAllAttributeSimpleValues().get(attributeKey) != null) {
			throw new EpPersistenceException("Attribute has been defined multiple times:" + attributeKey);
		}
		AttributeValueFilter rootFilter = filterFactory.getFilterBean(ContextIdNames.ATTRIBUTE_FILTER);
		rootFilter.setId(attributeKey);
		rootFilter.setAttributeKey(attributeKey);
		// If attribute not found in database, just ignore it.
		if (rootFilter.getAttribute() == null) {
			LOG.error("Attribute not found: " + attributeKey);
			return;
		}

		final String localized = sectionElement.getAttributeValue(LOCALIZED);
		rootFilter.setLocalized(TRUE.equals(localized));
		config.getAllAttributeSimpleValues().put(rootFilter.getId(), rootFilter);
		config.getAllAttributesMap().put(attributeKey, rootFilter.getAttribute());

		final List<Element> subNodes = getChildren(sectionElement);
		if (subNodes != null && !subNodes.isEmpty()) {
			for (Element childNode : subNodes) {
				parseAttributeSimpleNode(childNode, config, rootFilter);
			}
		}

	}

	private AttributeFilter< ? > parseAttributeSimpleNode(final Element subNode, final FilteredNavigationConfiguration config,
			final AttributeFilter< ? > parent) {
		if (!subNode.getName().equals(ATTRIBUTE_SIMPLE_VALUE)) {
			throw new EpPersistenceException("Not an attribute simple node:" + subNode.getName());
		}

		final AttributeValueFilter attributeFilter = filterFactory.getFilterBean(ContextIdNames.ATTRIBUTE_FILTER);
		Map<String, Object> filterProperties = new HashMap<String, Object>();
		attributeFilter.setLocalized(parent.isLocalized());
		filterProperties.put(AttributeFilter.ATTRIBUTE_PROPERTY, parent.getAttribute());

		String seoId = subNode.getAttributeValue(SEO_ID);
		if ((seoId == null) || (seoId.length() < 0)) {
			throw new EpPersistenceException("Attribute simple value should have seoId defined for: "
					+ attributeFilter.getAttributeKey());
		}
		
		filterProperties.put(AttributeFilter.ATTRIBUTE_VALUES_ALIAS_PROPERTY, seoId);
		String attributeValue = subNode.getAttributeValue(VALUE);
		filterProperties.put(AttributeValueFilter.ATTRIBUTE_VALUE_PROPERTY, attributeValue);
		
		attributeFilter.initialize(filterProperties);
		
		attributeFilter.setDisplayName(subNode.getAttributeValue("displayName"));

		String identicalMapKey = null;
		if (attributeFilter.isLocalized()) {
			String language = subNode.getAttributeValue(LANGUAGE);
			if ((language == null) || (language.length() == 0)) {
				throw new EpPersistenceException("Attribute node should have language defined since it is localized."
						+ attributeFilter.getId());
			}
			attributeFilter.setLocale(LocaleUtils.toLocale(language));
			identicalMapKey = String.valueOf(attributeFilter.getLocale()).concat(String.valueOf(attributeFilter.getAttributeValue()));
		} else {
			identicalMapKey = String.valueOf(attributeFilter.getAttributeValue());
		}

		final SortedMap<String, AttributeValueFilter> attributeValuesMap = config.getAttributeSimpleValuesMap(attributeFilter.getAttributeKey());
		final AttributeValueFilter overLapFilter = attributeValuesMap.get(String.valueOf(attributeFilter
				.getAttributeValue()));
		if (overLapFilter != null) {
			throw new EpPersistenceException("Attribute value has overlap with another value:" + overLapFilter.getId());
		}
		attributeValuesMap.put(identicalMapKey, attributeFilter);
		config.getAllAttributeSimpleValues().put(attributeFilter.getId(), attributeFilter);

		return attributeFilter;
	}

	private void parsePriceNode(final Element sectionElement, final FilteredNavigationConfiguration config) {
		if (!sectionElement.getName().equals(PRICE)) {
			throw new EpPersistenceException("Not an attribute range node:" + sectionElement.getName());
		}

		final String currencyCode = sectionElement.getAttributeValue("currency");
		// final Currency currency = Currency.getInstance(currencyCode);
		if (config.getAllPriceRanges().get(currencyCode) != null) {
			throw new EpPersistenceException("Price range has been defined multiple times:" + currencyCode);
		}
		PriceFilter rootFilter = filterFactory.getFilterBean(ContextIdNames.PRICE_FILTER);
		rootFilter.setId(currencyCode);
		rootFilter.setCurrency(Currency.getInstance(currencyCode));
		final String localized = sectionElement.getAttributeValue(LOCALIZED);
		rootFilter.setLocalized(TRUE.equals(localized));
		config.getAllPriceRanges().put(rootFilter.getId(), rootFilter);

		final List<Element> subRangeNodes = getChildren(sectionElement);
		if (subRangeNodes != null && !subRangeNodes.isEmpty()) {
			for (Element childNode : subRangeNodes) {
				rootFilter.addChild(parsePriceRangeNode(childNode, config, rootFilter));
			}
		}

	}

	private PriceFilter parsePriceRangeNode(final Element rangeNode,
			final FilteredNavigationConfiguration config, final PriceFilter parent) {
		if (!rangeNode.getName().equals(RANGE)) {
			throw new EpPersistenceException("Not an range node:" + rangeNode.getName());
		}

		final PriceFilter priceFilter = filterFactory.getFilterBean(ContextIdNames.PRICE_FILTER);
		final Map<String, Object> filterProperties = new HashMap<String, Object>();

		filterProperties.put(PriceFilter.CURRENCY_PROPERTY, parent.getCurrency());
		
		priceFilter.setLocalized(parent.isLocalized());

		final BigDecimal lowerValue = constructBigDecimalValue(rangeNode.getAttributeValue(LOWER_VALUE));
		final BigDecimal upperValue = constructBigDecimalValue(rangeNode.getAttributeValue(UPPER_VALUE));
		
		filterProperties.put(RangeFilter.LOWER_VALUE_PROPERTY, lowerValue);	
		filterProperties.put(RangeFilter.UPPER_VALUE_PROPERTY, upperValue);
		

		String seoId = rangeNode.getAttributeValue(SEO_ID);
		if ((seoId == null) || (seoId.length() < 0)) {
			throw new EpPersistenceException("Range should have seoId defined for: " + lowerValue + " - " + upperValue);
		}
		filterProperties.put(PriceFilter.ALIAS_PROPERTY, seoId);
		
		priceFilter.initialize(filterProperties);
		
		final List<Element> subNodes = getChildren(rangeNode);
		if (subNodes != null && !subNodes.isEmpty()) {
			for (Element childNode : subNodes) {
				if (childNode.getName().equals(DISPLAY_INFO)) {
					parseDisplayInfo(priceFilter, childNode);
				} else if (childNode.getName().equals(RANGE)) {
					priceFilter.addChild(parsePriceRangeNode(childNode, config, priceFilter));
				}
			}
		}
		if ((priceFilter.getChildren() == null) || (priceFilter.getChildren().size() == 0)) {
			// This is the leaf range.
			final SortedMap<PriceFilter, PriceFilter> bottomLevelPriceRanges = config.getBottomLevelPriceRanges(priceFilter.getCurrency());
			if (bottomLevelPriceRanges.get(priceFilter) != null) {
				final PriceFilter overLapPriceFilter = bottomLevelPriceRanges.get(priceFilter);
				throw new EpPersistenceException("Price range has overlap with another price range :"
						+ overLapPriceFilter.getId());
			}
			bottomLevelPriceRanges.put(priceFilter, priceFilter);
		}
		config.getAllPriceRanges().put(priceFilter.getId(), priceFilter);

		return priceFilter;
	}

	private BigDecimal constructBigDecimalValue(final String attributeValue) {
		try {
			return new BigDecimal(attributeValue);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	private void parseAttributeRangeNode(final Element sectionElement, final FilteredNavigationConfiguration config) {
		if (!sectionElement.getName().equals(ATTRIBUTE_RANGE)) {
			throw new EpPersistenceException("Not an attribute range node:" + sectionElement.getName());
		}

		final String attributeKey = sectionElement.getAttributeValue(ATTRIBUTE_KEY);
		if (config.getAllAttributeRanges().get(attributeKey) != null) {
			throw new EpPersistenceException("Attribute range has been defined multiple times:" + attributeKey);
		}
		AttributeRangeFilter rootFilter = filterFactory.getFilterBean(ContextIdNames.ATTRIBUTE_RANGE_FILTER);
		rootFilter.setId(attributeKey);
		rootFilter.setAttributeKey(attributeKey);
		// If attribute not found in database, just ignore it.
		if (rootFilter.getAttribute() == null) {
			LOG.error("Attribute not found: " + attributeKey);
			return;
		}

		final String localized = sectionElement.getAttributeValue(LOCALIZED);
		rootFilter.setLocalized(TRUE.equals(localized));
		config.getAllAttributeRanges().put(rootFilter.getId(), rootFilter);
		config.getAllAttributesMap().put(attributeKey, rootFilter.getAttribute());

		final List<Element> subRangeNodes = getChildren(sectionElement);
		if (subRangeNodes != null && !subRangeNodes.isEmpty()) {
			for (Element childNode : subRangeNodes) {
				rootFilter.addChild(parseAttributeRangeNode(childNode, config, rootFilter));
			}
		}

	}

	@SuppressWarnings("PMD.NPathComplexity")
	private AttributeRangeFilter parseAttributeRangeNode(final Element rangeNode, final FilteredNavigationConfiguration config,
			final AttributeRangeFilter parent) {
		if (!rangeNode.getName().equals(RANGE)) {
			throw new EpPersistenceException("Not an range node:" + rangeNode.getName());
		}

		final AttributeRangeFilter attributeRangeFilter = filterFactory.getFilterBean(ContextIdNames.ATTRIBUTE_RANGE_FILTER);
		Map<String, Object> filterProperties = new HashMap<String, Object>();
		filterProperties.put(AttributeFilter.ATTRIBUTE_PROPERTY, parent.getAttribute());
		attributeRangeFilter.setLocalized(parent.isLocalized());
		final String lowerValue = rangeNode.getAttributeValue(LOWER_VALUE);
		final String upperValue = rangeNode.getAttributeValue(UPPER_VALUE);
		if (lowerValue != null && lowerValue.trim().length() != 0) {
			filterProperties.put(RangeFilter.LOWER_VALUE_PROPERTY, lowerValue);
		}
		if (upperValue != null && upperValue.trim().length() != 0) {
			filterProperties.put(RangeFilter.UPPER_VALUE_PROPERTY, upperValue);
		}

		String seoId = rangeNode.getAttributeValue(SEO_ID);
		if ((seoId == null) || (seoId.length() < 0)) {
			throw new EpPersistenceException("Range should have seoId defined for: " + lowerValue + " - " + upperValue);
		}
		filterProperties.put(AttributeFilter.ATTRIBUTE_VALUES_ALIAS_PROPERTY, seoId);
		attributeRangeFilter.initialize(filterProperties);

		final List<Element> subNodes = getChildren(rangeNode);
		if (subNodes != null && !subNodes.isEmpty()) {
			for (Element childNode : subNodes) {
				if (childNode.getName().equals(DISPLAY_INFO)) {
					parseDisplayInfo(attributeRangeFilter, childNode);
				} else if (childNode.getName().equals(RANGE)) {
					attributeRangeFilter.addChild(parseAttributeRangeNode(childNode, config, attributeRangeFilter));
				}
			}
		}
		if ((attributeRangeFilter.getChildren() == null) || (attributeRangeFilter.getChildren().size() == 0)) {
			// This is the leaf range.
			final SortedMap<AttributeRangeFilter, AttributeRangeFilter> bottomLevelAttributeRanges =
				config.getBottomLevelAttributeRanges(attributeRangeFilter.getAttributeKey());
			if (bottomLevelAttributeRanges.get(attributeRangeFilter) != null) {
				final AttributeRangeFilter overLapAttributeFilter = bottomLevelAttributeRanges
						.get(attributeRangeFilter);
				throw new EpPersistenceException("Attribute range has overlap with another range :"
						+ overLapAttributeFilter.getId());
			}
			bottomLevelAttributeRanges.put(attributeRangeFilter, attributeRangeFilter);
		}
		config.getAllAttributeRanges().put(attributeRangeFilter.getId(), attributeRangeFilter);

		return attributeRangeFilter;
	}

	/**
	 * Parse the displayInfo node. Add the displayInfo to the rangeFilter.
	 * 
	 * @param rangeFilter the filter which the displayInfo belongs to.
	 * @param childNode the xml node contains displayInfo.
	 */
	private <T extends RangeFilter<T, E>, E extends Comparable<E>> void parseDisplayInfo(final RangeFilter<T, E> rangeFilter,
			final Element childNode) {
		String language = childNode.getAttributeValue(LANGUAGE);
// if (rangeFilter.isLocalized() && ((language == null) || (language.length() == 0))) {
// throw new EpPersistenceException("Range should have language defined since it is localized."
// + rangeFilter.getId());
// }
		FilterDisplayInfo displayInfo = parseRangeFilterDisplayInfo(childNode);
		Locale locale = LocaleUtils.toLocale(language);
		rangeFilter.addDisplayInfo(locale, displayInfo);
	}

	/**
	 * @param childNode
	 * @return
	 */
	private FilterDisplayInfo parseRangeFilterDisplayInfo(final Element childNode) {
		FilterDisplayInfo displayInfo = new FilterDisplayInfoImpl();
		final List<Element> infoNodes = getChildren(childNode);
		if ((infoNodes != null) && !infoNodes.isEmpty()) {
			for (Element infoNode : infoNodes) {
				if (infoNode.getName().equals("value")) {
					displayInfo.setDisplayName(infoNode.getText());
				} else if (infoNode.getName().equals("seo")) {
					displayInfo.setSeoName(infoNode.getText());
				}
			}
		}
		return displayInfo;
	}

	@SuppressWarnings("unchecked")
	private <T> List<T> getChildren(final Element node) {
		return node.getChildren();
	}

	/**
	 * Parse the brand nodes and populate the configuration object.
	 * @param sectionElement the element to parse.
	 * @param config the config to populate.
	 */
	private void parseBrandNode(final Element sectionElement, final FilteredNavigationConfiguration config) {
		for (Object obj : sectionElement.getChildren(BRAND)) {
			final String brandCode = ((Element) obj).getAttributeValue(BRAND_KEY);
			
			if (brandCode == null) {
				throw new EpPersistenceException("Brand key cannot be null");
			}
			config.getAllBrandCodes().add(brandCode);				
		}
	}
	
	/**
	 * @return the beanFactory
	 */
	public BeanFactory getBeanFactory() {
		return beanFactory;
	}

	/**
	 * @param beanFactory the beanFactory to set
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * Sets the filter factory.
	 *
	 * @param filterFactory the new filter factory
	 */
	public void setFilterFactory(final FilterFactory filterFactory) {
		this.filterFactory = filterFactory;
	}
}
