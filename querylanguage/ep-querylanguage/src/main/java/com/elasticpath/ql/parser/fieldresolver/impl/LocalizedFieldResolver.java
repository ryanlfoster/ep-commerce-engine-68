/**
 * 
 */
package com.elasticpath.ql.parser.fieldresolver.impl;

import java.util.Locale;

import org.apache.commons.lang.LocaleUtils;

import com.elasticpath.ql.parser.EpQLTerm;
import com.elasticpath.ql.parser.EpQuery;
import com.elasticpath.ql.parser.EpQLFieldDescriptor;
import com.elasticpath.ql.parser.NativeResolvedTerm;
import com.elasticpath.ql.parser.fieldresolver.EpQLFieldResolver;
import com.elasticpath.ql.parser.gen.ParseException;
import com.elasticpath.service.search.solr.IndexUtility;

/**
 * Localized EP QL field descriptor. This resolver treats parameter 1 as locale and restricts specification of parameter 2.
 */
public class LocalizedFieldResolver implements EpQLFieldResolver {
	
	private IndexUtility indexUtility;

	@Override
	public NativeResolvedTerm resolve(final EpQuery epQuery, final EpQLTerm epQLTerm,
			final EpQLFieldDescriptor solrTemplateDescriptor) throws ParseException {
		if (epQLTerm.getParameter2() != null) {
			throw new ParseException("Parameter: " + epQLTerm.getParameter2()
					+ " can not be specified for this field: " + epQLTerm.getEpQLField().getFieldName());
		}
		if (epQLTerm.getParameter1() == null) {
			throw new ParseException("Locale must be specified for this field: " + epQLTerm.getEpQLField().getFieldName());
		}

		final NativeResolvedTerm resolvedSolrField = new NativeResolvedTerm(solrTemplateDescriptor);
		try {
			final Locale locale = LocaleUtils.toLocale(epQLTerm.getParameter1());
			if (!LocaleUtils.isAvailableLocale(locale)) {
				throw new ParseException("Specified locale: '" + locale + "' is not available");
			}
			resolvedSolrField.setResolvedField(assembleLocaleDependentField(solrTemplateDescriptor.getFieldTemplate(), locale));
		} catch (IllegalArgumentException exception) {
			throw new ParseException("Specified locale: '" + epQLTerm.getParameter1() + "' is not available"); // NOPMD			
		}
		return resolvedSolrField;
	}

	/**
	 * Returns locale-aware Solr field.
	 * 
	 * @param solrField locale-dependent solr field.
	 * @param locale locale parameter
	 * @return Returns the field ID name of a locale aware field
	 */
	String assembleLocaleDependentField(final String solrField, final Locale locale) {
		return indexUtility.createLocaleFieldName(solrField, locale);
	}

	/**
	 * Sets index utility for building field in Solr format.
	 * 
	 * @param indexUtility the indexUtility to set
	 */
	public void setIndexUtility(final IndexUtility indexUtility) {
		this.indexUtility = indexUtility;
	}
}
