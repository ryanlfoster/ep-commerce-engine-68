package com.elasticpath.ql.custom.product;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.ConstantScoreRangeQuery;
import org.apache.lucene.search.BooleanClause.Occur;

import com.elasticpath.commons.util.impl.DateUtils;
import com.elasticpath.ql.parser.EpQLOperator;
import com.elasticpath.ql.parser.EpQLTerm;
import com.elasticpath.ql.parser.NativeResolvedTerm;
import com.elasticpath.ql.parser.gen.ParseException;
import com.elasticpath.ql.parser.query.LuceneQuery;
import com.elasticpath.ql.parser.query.NativeQuery;
import com.elasticpath.ql.parser.querybuilder.impl.LuceneSubQueryBuilder;
import com.elasticpath.service.search.solr.SolrIndexConstants;

/**
 * Builds boolean queries.
 */
public class ProductStateQueryBuilder extends LuceneSubQueryBuilder {

	@Override
	public NativeQuery buildQuery(final NativeResolvedTerm resolvedSolrField, final EpQLTerm epQLTerm) throws ParseException {
		final String resolvedValue = getResolvedValue(resolvedSolrField);
		return new LuceneQuery(buildProductStateQuery(resolvedValue, epQLTerm));
	}

	/**
	 * Builds query to return active or inactive products.
	 * <br>Example query for active products  : startDate:[* TO 2009-11-17T11:18:58Z] -endDate:[* TO 2009-11-17T11:18:58Z]
	 * <br>Example query for inactive products: startDate:[2009-11-17T11:18:58Z TO *] endDate:[* TO 2009-11-17T11:18:58Z]
	 * 
	 * @param epQLTerm EP QL Term
	 * @param queryText analyzed text
	 * @return Lucene boolean query
	 * @throws ParseException if query couldn't be interpreted as boolean query for ProductState
	 */
	protected BooleanQuery buildProductStateQuery(final String queryText, final EpQLTerm epQLTerm) throws ParseException {
		if (EpQLOperator.EQUAL != epQLTerm.getOperator() && EpQLOperator.NOT_EQUAL != epQLTerm.getOperator()) {
			throw new ParseException("Only operator = may be used for "
					+ epQLTerm.getEpQLField().getFieldName() + " query");
		}
		checkValue(queryText, epQLTerm);
		
		// are we searching active or inactive products
		boolean conditionActive = Boolean.valueOf(queryText);
		
		// get NOW as a SOLR date string
		final String now = getDate(new Date());
		
		final BooleanQuery booleanQuery = new BooleanQuery();
		if (conditionActive) {
			// build the SOLR query for active products
			// start date is in the past
			booleanQuery.add(new ConstantScoreRangeQuery(SolrIndexConstants.START_DATE, null, now, true, true), Occur.MUST);
			// AND end date is NOT in the past - the NOT correctly handled possible null end dates
			booleanQuery.add(new ConstantScoreRangeQuery(SolrIndexConstants.END_DATE, null, now, true, true), Occur.MUST_NOT);
		} else {
			// start date is in the future, which means end date is also in the future
			booleanQuery.add(new ConstantScoreRangeQuery(SolrIndexConstants.START_DATE, now, null, true, true), Occur.SHOULD);
			// OR end date in the past, which means start date is also in the past
			booleanQuery.add(new ConstantScoreRangeQuery(SolrIndexConstants.END_DATE, null, now, true, true), Occur.SHOULD);
		}
		
		return booleanQuery;
	}
	
	/**
	 * Format the date suitable for SOLR range queries on product start and end dates.
	 *  
	 * @param date The date to format.
	 * @return The formatted date string.
	 */
	protected String getDate(final Date date) {
		final SimpleDateFormat dateFormat = new SimpleDateFormat(DateUtils.DATE_TIME_FORMAT_STRING_US_INTERNAL, Locale.US);
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		
		return dateFormat.format(date);
	}

	private void checkValue(final String queryText, final EpQLTerm epQLTerm) throws ParseException {
		if (!Boolean.TRUE.toString().equalsIgnoreCase(queryText) && !Boolean.FALSE.toString().equalsIgnoreCase(queryText)) {
			throw new ParseException("Only TRUE, true, FALSE or false values are valid for filed "
					+ epQLTerm.getEpQLField().getFieldName());
		}
	}
}
