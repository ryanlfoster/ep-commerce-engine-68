package com.elasticpath.importexport.common.adapters.products.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.LocaleUtils;
import org.apache.log4j.Logger;

import com.elasticpath.common.dto.DisplayValue;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.attribute.AttributeValueGroup;
import com.elasticpath.domain.attribute.impl.AbstractAttributeValueImpl;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.importexport.common.adapters.AbstractDomainAdapterImpl;
import com.elasticpath.importexport.common.dto.products.AttributeValuesDTO;
import com.elasticpath.importexport.common.exception.runtime.PopulationRollbackException;
import com.elasticpath.importexport.common.exception.runtime.PopulationRuntimeException;
import com.elasticpath.importexport.common.util.Message;
import com.elasticpath.validation.service.ValidatorUtils;

/**
 * The implementation of <code>DomainAdapter</code> interface. It is responsible for data transformation between
 * <code>Collection&lt;AttributeValue></code> and <code>AttributeValuesDTO</code> objects.
 */
@SuppressWarnings({ "PMD.CyclomaticComplexity" })
public class AttributeValuesAdapter extends AbstractDomainAdapterImpl<Collection<AttributeValue>, AttributeValuesDTO> {

	private static final String SEPARATOR = "_";

	private AttributeValueGroup attributeValueGroup;
	
	private ValidatorUtils validatorUtils;
	
	private static final Logger LOG = Logger.getLogger(AttributeValuesAdapter.class);

	/**
	 * {@inheritDoc}
	 * 
	 * @throws PopulationRollbackException if attributeValueGroup is not initialized
	 */
	public void populateDomain(final AttributeValuesDTO attributesDTO, final Collection<AttributeValue> attributeValueCollection) {
		sanityCheck();

		Map<String, List<String>> attributesMap = new HashMap<String, List<String>>();
		String attributeKey = attributesDTO.getKey();		
		Attribute attribute = getCachingService().findAttribiteByKey(attributeKey);
		if (attribute == null) {
			// In the ProductAdapter the product code is added to the parameters.
			throw new PopulationRollbackException("IE-10309", attributeKey);
		}

		for (DisplayValue displayValue : attributesDTO.getValues()) {
			List<String> values = attributesMap.get(displayValue.getLanguage());
			if (values == null) {
				values = new ArrayList<String>();
				attributesMap.put(displayValue.getLanguage(), values);
			}
			values.add(displayValue.getValue());
		}

		for (Entry<String, List<String>> entry : attributesMap.entrySet()) {
			String key = entry.getKey();
			try {
				Locale locale = null;
				if (key != null) {
					locale = LocaleUtils.toLocale(key);
					if (!LocaleUtils.isAvailableLocale(locale)) {
						throw new IllegalArgumentException();
					}
					if (!isLocaleSupportedByCatalog(attribute.getCatalog(), locale)) {
						continue;
					}
				}

				AttributeValue attributeValue = attributeValueGroup.getAttributeValue(attributeKey, locale);
				if (attributeValue == null) {
					StringBuffer localizedKey = new StringBuffer(attributeKey);
					if (key != null) {
						localizedKey = localizedKey.append(SEPARATOR).append(key);
					}

					attributeValue = attributeValueGroup.getAttributeValueFactory().createAttributeValue(attribute, localizedKey.toString());
				}

				String value = AbstractAttributeValueImpl.buildShortTextMultiValues(entry.getValue());
				
				if (value != null) { 
					attributeValue.setStringValue(value);
				}
				
				getValidatorUtils().validateAttributeValue(attributeValue);
				
				attributeValueCollection.add(attributeValue);
			} catch (IllegalArgumentException exception) {
				throw new PopulationRuntimeException("IE-10306", exception, key, entry.getValue().toString());
			}
		}
	}

	/**
	 * @return True if Catalog is not null and Locale is not supported.
	 */
	private boolean isLocaleSupportedByCatalog(final Catalog catalog, final Locale locale) {
		if (catalog != null && !catalog.getSupportedLocales().contains(locale)) {
			LOG.warn(new Message("IE-10000", locale.toString()));
			return false;
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws PopulationRollbackException if attributeValueGroup is not initialized
	 */
	public void populateDTO(final Collection<AttributeValue> attributeValueCollection, final AttributeValuesDTO attributeValuesDTO) {
		List<DisplayValue> displayValueList = new ArrayList<DisplayValue>();
		for (AttributeValue attributeValue : attributeValueCollection) {
			attributeValuesDTO.setKey(attributeValue.getAttribute().getKey());

			String localizedKey = attributeValue.getLocalizedAttributeKey();
			String language = null;

			int indexOfSeparator = localizedKey.lastIndexOf(SEPARATOR);
			if (indexOfSeparator != -1) {
				language = localizedKey.substring(indexOfSeparator + 1);
				try {
					Locale locale = LocaleUtils.toLocale(language);
					if (!LocaleUtils.isAvailableLocale(locale)) {
						throw new IllegalArgumentException();
					}
				} catch (IllegalArgumentException e) {
					language = null;
				}
			}

			String stringValue = null;
			if (attributeValue.getValue() != null) {
				 stringValue = attributeValue.getStringValue();
			}
			if (attributeValue.getAttribute().isMultiValueEnabled()) {
				List<String> valueList = AbstractAttributeValueImpl.parseShortTextMultiValues(stringValue);
				if (valueList != null) {
					for (String value : valueList) {
						displayValueList.add(new DisplayValue(language, value));
					}
				}
			} else {
				displayValueList.add(new DisplayValue(language, stringValue));
			}
		}
		attributeValuesDTO.setValues(displayValueList);
	}

	private void sanityCheck() {
		if (attributeValueGroup == null) {
			throw new PopulationRollbackException("IE-10310");
		}
	}

	/**
	 * Sets the attributeValueGroup.
	 * 
	 * @param attributeValueGroup the attributeValueGroup to set
	 */
	public void setAttributeValueGroup(final AttributeValueGroup attributeValueGroup) {
		this.attributeValueGroup = attributeValueGroup;
	}

	protected ValidatorUtils getValidatorUtils() {
		return validatorUtils;
	}

	public void setValidatorUtils(final ValidatorUtils validatorUtils) {
		this.validatorUtils = validatorUtils;
	}
}
