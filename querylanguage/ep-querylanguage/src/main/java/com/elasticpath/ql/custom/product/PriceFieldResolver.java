package com.elasticpath.ql.custom.product;

import com.elasticpath.ql.parser.EpQLFieldDescriptor;
import com.elasticpath.ql.parser.EpQLTerm;
import com.elasticpath.ql.parser.EpQuery;
import com.elasticpath.ql.parser.NativeResolvedTerm;
import com.elasticpath.ql.parser.fieldresolver.EpQLFieldResolver;
import com.elasticpath.ql.parser.gen.ParseException;
import com.elasticpath.service.search.solr.IndexUtility;

/**
 * Price EP QL field descriptor. This resolver treats parameter 1 as currency code and parameter 2 as store code.
 */

public class PriceFieldResolver implements EpQLFieldResolver {

//	private IndexUtility indexUtility;

	@Override
	public NativeResolvedTerm resolve(final EpQuery epQuery, final EpQLTerm epQLTerm, final EpQLFieldDescriptor solrTemplateDescriptor) 
		throws ParseException {

		//TODO - Change to provide the correct solr field once the EpQL syntax has been updated. 
		throw new ParseException("Solr now requires catalog code and pricelist not store and currency");
			
			
//		if (epQLTerm.getParameter1() == null) {
//			throw new ParseException("Currency was not specified for price of field: " + epQLTerm.getEpQLField().getFieldName());
//		}
//		if (epQLTerm.getParameter2() == null) {
//			throw new ParseException("Store was not specified for price of field: " + epQLTerm.getEpQLField().getFieldName());
//		}
//		final NativeResolvedTerm resolvedSolrField = new NativeResolvedTerm(solrTemplateDescriptor);
//		// check that such currency and store exist
//		final String storeCode = ShieldUtility.shieldString(epQLTerm.getParameter2());
//		final String currencyCode = epQLTerm.getParameter1();
//		Currency currency = null;
//		try {
//			currency = Currency.getInstance(currencyCode);
//		} catch (IllegalArgumentException e) {
//			throw new ParseException("Currency with the code " + currencyCode + " is invalid."); // NOPMD
//		}
//		resolvedSolrField.setResolvedField(indexUtility.createPriceFieldName(solrTemplateDescriptor.getFieldTemplate(), storeCode, currency));
//		return resolvedSolrField;
	}

	/**
	 * Sets index utility for building fields in Solr format.
	 * 
	 * @param indexUtility the indexUtility to set
	 */
	public void setIndexUtility(final IndexUtility indexUtility) {
//		this.indexUtility = indexUtility;
	}
}
