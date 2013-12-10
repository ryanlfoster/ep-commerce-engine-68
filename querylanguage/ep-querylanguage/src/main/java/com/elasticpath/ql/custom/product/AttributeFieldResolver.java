package com.elasticpath.ql.custom.product;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;

import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeType;
import com.elasticpath.domain.attribute.AttributeUsage;
import com.elasticpath.domain.attribute.impl.AttributeUsageImpl;
import com.elasticpath.ql.parser.EPQueryType;
import com.elasticpath.ql.parser.EpQLField;
import com.elasticpath.ql.parser.EpQLFieldDescriptor;
import com.elasticpath.ql.parser.EpQLFieldType;
import com.elasticpath.ql.parser.EpQLTerm;
import com.elasticpath.ql.parser.EpQuery;
import com.elasticpath.ql.parser.NativeResolvedTerm;
import com.elasticpath.ql.parser.fieldresolver.EpQLFieldResolver;
import com.elasticpath.ql.parser.gen.ParseException;
import com.elasticpath.service.attribute.AttributeService;
import com.elasticpath.service.search.solr.IndexUtility;
import com.elasticpath.service.search.solr.SolrIndexConstants;

/**
 * Attribute EP QL field descriptor. This resolver treats parameter 1 as locale and parameter 2 as attribute name.
 */
public class AttributeFieldResolver implements EpQLFieldResolver {

	private static final Logger LOG = Logger.getLogger(AttributeFieldResolver.class);

	private static final String INEXISTING_ATTRIBUTE = "INEXISTING_ATTRIBUTE_777_st"; // to reduce the possibility that attribute exist.

	private IndexUtility indexUtility;

	private AttributeService attributeService;
	
	private AttributeUsage attributeUsage;

	private Map<EPQueryType, AttributeUsage> usageQueryTypeMap;

	@Override
	public NativeResolvedTerm resolve(final EpQuery epQuery, final EpQLTerm epQLTerm, final EpQLFieldDescriptor solrTemplateDescriptor) 
		throws ParseException {
		if (epQLTerm.getParameter2() == null) {
			throw new ParseException("Attribute name has to be specified in curly brackets.");
		}

		if (epQuery.isValidateOnly()) {
			NativeResolvedTerm resolvedSolrField = new NativeResolvedTerm(solrTemplateDescriptor);
			resolvedSolrField.setResolvedField("ValidatedField");
			return resolvedSolrField;
		}

		Attribute attribute = createAttribute(epQLTerm.getParameter2(), epQuery, epQLTerm.getEpQLField());
		if (attribute == null) {
			return createInexistentAttributeField(solrTemplateDescriptor);
		}

		final Locale locale = createLocaleFor(attribute, epQLTerm);

		return createResolvedSolrField(attribute, locale, solrTemplateDescriptor);
	}

	/**
	 * Sets resolved value to inexistent value. Solr will neither restrict the result by this attribute nor will thorow an exception.
	 * 
	 * @param solrTemplateDescriptor EpQLFieldDescriptor
	 * @return a NativeResolvedTerm containing fake attribute code.
	 */
	private NativeResolvedTerm createInexistentAttributeField(final EpQLFieldDescriptor solrTemplateDescriptor) {
		final NativeResolvedTerm resolvedSolrField = new NativeResolvedTerm(solrTemplateDescriptor);
		resolvedSolrField.setResolvedField(SolrIndexConstants.ATTRIBUTE_PREFIX + INEXISTING_ATTRIBUTE);
		return resolvedSolrField;
	}

	private NativeResolvedTerm createResolvedSolrField(final Attribute attribute, final Locale locale, 
			final EpQLFieldDescriptor solrTemplateDescriptor) {
		final NativeResolvedTerm resolvedSolrField = new NativeResolvedTerm(solrTemplateDescriptor);
		// TODO: consider another data types if required.
		if (AttributeType.DATE == attribute.getAttributeType()) {
			resolvedSolrField.getFieldDescriptor().setType(EpQLFieldType.DATE);
		}
		resolvedSolrField.setResolvedField(indexUtility.createAttributeFieldName(attribute, locale, false, false));
		return resolvedSolrField;
	}

	private Locale createLocaleFor(final Attribute attribute, final EpQLTerm epQLTerm) {
		if (attribute.isLocaleDependant() && epQLTerm.getParameter1() != null) {
			return new Locale(epQLTerm.getParameter1());
		} else if (attribute.isLocaleDependant() && epQLTerm.getParameter1() == null) {
			LOG.warn("Locale must be specified for locale dependent attribute: " + attribute.getName());
			// method should return nonexistent locale in case attribute is locale dependent and locale is no set because search should not find
			// something in this case
			return new Locale("non_existent_locale");
		} else if (!attribute.isLocaleDependant() && epQLTerm.getParameter1() != null) { // NOPMD
			LOG.warn("Locale must not be specified for the attribute: " + attribute.getName());
		}
		return null;
	}

	private Attribute createAttribute(final String attributeName, final EpQuery epQuery, final EpQLField epQLField) throws ParseException {
		final String userAttributeName = attributeName.replaceAll("\\\\\\\\", "\\\\").replaceAll("\\\\\\{", "{").replaceAll("\\\\}", "}");
		Attribute attribute = null;
		if (epQLField == EpQLField.SKU_ATTRIBUTE) {
			// TODO : Not esthetic code.
			attribute = attributeService.findByNameAndUsage(userAttributeName, AttributeUsageImpl.SKU_USAGE);
		} else {
			attribute = attributeService.findByNameAndUsage(userAttributeName, getAttributeUsageFor(epQuery.getQueryType()));
		}

		if (attribute == null) {
			LOG.warn("Attribute with the name: " + userAttributeName + " can not be found.");
		}

		return attribute;
	}

	private AttributeUsage getAttributeUsageFor(final EPQueryType queryType) throws ParseException {
		final AttributeUsage attributeUsage = getUsageQueryTypeMap().get(queryType);
		if (attributeUsage == null) {
			throw new ParseException("Can't resolve attribute usage for " + queryType + " object");
		}
		return attributeUsage;
	}

	/**
	 * Returns locale-aware Solr field.
	 * 
	 * @param solrField locale-dependent solr field.
	 * @param localeString locale
	 * @return Returns the field ID name of a locale aware field
	 */
	String assembleLocaleDependentField(final String solrField, final String localeString) {
		return indexUtility.createLocaleFieldName(solrField, new Locale(localeString));
	}

	/**
	 * @param indexUtility the indexUtility to set
	 */
	public void setIndexUtility(final IndexUtility indexUtility) {
		this.indexUtility = indexUtility;
	}

	/**
	 * Sets the attribute service.
	 * 
	 * @param attributeService the attribute service to set
	 */
	public void setAttributeService(final AttributeService attributeService) {
		this.attributeService = attributeService;
	}

	/**
	 * Set the attribute usage class.
	 *
	 * @param attributeUsage attribute usage
	 */
	public void setAttributeUsage(final AttributeUsage attributeUsage) {
		this.attributeUsage = attributeUsage;
	}

	/**
	 * Get the map of query type to attribute usage.
	 * 
	 * @return the map of attribute usages.
	 */
	public Map<EPQueryType, AttributeUsage> getUsageQueryTypeMap() {
		if (usageQueryTypeMap == null) {
			usageQueryTypeMap = new HashMap<EPQueryType, AttributeUsage>();
			usageQueryTypeMap.put(EPQueryType.PRODUCT, attributeUsage.getAttributeUsageById(AttributeUsage.PRODUCT));
			usageQueryTypeMap.put(EPQueryType.CATEGORY, attributeUsage.getAttributeUsageById(AttributeUsage.CATEGORY));
		}
		return usageQueryTypeMap;
	}

}
