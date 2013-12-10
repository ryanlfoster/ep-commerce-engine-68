package com.elasticpath.ql.parser;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.elasticpath.ql.parser.fieldresolver.EpQLFieldResolver;
import com.elasticpath.ql.parser.gen.ParseException;
import com.elasticpath.ql.parser.query.NativeBooleanClause;
import com.elasticpath.ql.parser.query.NativeQuery;
import com.elasticpath.ql.parser.querybuilder.CompleteQueryBuilder;
import com.elasticpath.ql.parser.querybuilder.SubQueryBuilder;
import com.elasticpath.ql.parser.valueresolver.EpQLValueResolver;

/**
 * Helper class to the EpQueryParser implementation (generated from ep.jj), the <code>EpQueryAssembler</code> populates the EpQuery object. The
 * methods in this class are called by the EpQueryParser implementation to process parts of a query string. Each section of a query string is parsed
 * by its own method. The name of the method designated to parse each section of a query string is specified in the query string definition, which is
 * in the ep.jj JavaCC configuration file.
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
public class EpQueryAssembler {
	private static final Logger LOG = Logger.getLogger(EpQueryAssembler.class);

	private Map<EPQueryType, AbstractEpQLCustomConfiguration> epQLObjectConfigurationMap;

	/**
	 * Analyzes passed string type to check against pre-defined EP query types. If the type is correct, holds obtained query type in internal enum.
	 * 
	 * @param epQuery ep query to be populated.
	 * @param type String representation of EP Query type
	 * @throws ParseException in case of wrong type
	 */
	public void checkQueryType(final EpQuery epQuery, final String type) throws ParseException {
		if (type == null) {
			/** can't actually get here. Parser will track itself. */
			throw new ParseException("Null query type");
		}

		EPQueryType queryType = EPQueryType.findFromName(type);
		if (!isQueryTypeSupported(queryType)) {
			throw new ParseException("Invalid query type: " + type);
		}
		epQuery.setQueryType(queryType);
		LOG.debug("Analyzed query type: " + queryType.getTypeName());
	}

	/**
	 * Sets fetch type which can be either UID or GUID.
	 * 
	 * @param epQuery ep query to be populated.
	 * @param type string representation of the type.
	 * @throws ParseException in case of incorrect type.
	 */
	public void checkFetchType(final EpQuery epQuery, final String type) throws ParseException {
		if (type == null) {
			/** can't actually get here. Parser will track itself. */
			throw new ParseException("Null fetch type");
		}
		FetchType fetchType;

		if (FetchType.UID.getType().equals(type)) {
			fetchType = FetchType.UID;
		} else if (FetchType.GUID.getType().equals(type)) {
			fetchType = FetchType.GUID;
		} else {
			throw new ParseException("Unknown fetch type: " + type);
		}
		epQuery.setFetchType(fetchType);
		LOG.debug("Analyzed fetch type: " + fetchType.getType());
	}

	/**
	 * Sets maximum fetch limit parameter.
	 * 
	 * @param epQuery ep query to be populated.
	 * @param limitString string representation of the limit.
	 * @throws ParseException in case of errors.
	 */
	public void checkLimit(final EpQuery epQuery, final String limitString) throws ParseException {
		if (limitString == null) {
			/** can't actually get here. Parser will track itself. */
			throw new ParseException("Null limit");
		}

		// Safe operation - JavaCC has tracked down numeric type.
		int limit = Integer.parseInt(limitString);
		epQuery.setLimit(limit);

		LOG.debug("Limit: " + limit);
	}

	/**
	 * Sets start index to start search from.
	 * 
	 * @param epQuery ep query to be populated.
	 * @param startIndex string representation of the start index.
	 * @throws ParseException in case of errors.
	 */
	public void checkStartIndex(final EpQuery epQuery, final String startIndex) throws ParseException {
		if (startIndex == null) {
			/** can't actually get here. Parser will track itself. */
			throw new ParseException("Null limit");
		}

		// Safe operation - JavaCC has tracked down numeric type.
		epQuery.setStartIndex(Integer.parseInt(startIndex));

		LOG.debug("Start index: " + startIndex);
	}

	/**
	 * This method calls when query was successfully parsed, and used for processing some necessary action that should be done only when query has
	 * already parsed.
	 * 
	 * @param epQuery the epQuery
	 * @param nativeQuery the native query
	 * @return modified native query
	 */
	public NativeQuery postParseHandling(final EpQuery epQuery, final NativeQuery nativeQuery) {
		checkFetchType(epQuery);
		final CompleteQueryBuilder completeQueryBuilder = getCompleteQueryBuilder(epQuery);
		return completeQueryBuilder.checkProcessedQuery(nativeQuery);
	}

	/**
	 * Glues collected clauses to a single boolean query.
	 * 
	 * @param epQuery the query
	 * @param clauses a list of Boolean clauses
	 * @return Boolean query
	 */
	public NativeQuery getBooleanQuery(final EpQuery epQuery, final List<NativeBooleanClause> clauses) {
		final CompleteQueryBuilder completeQueryBuilder = getCompleteQueryBuilder(epQuery);
		return completeQueryBuilder.getBooleanQuery(clauses);
	}

	/**
	 * Having passed EpQLTerm, e.g. Price{SNAPITUP}[CAD] <= 150, creates native subquery using native sub query builder.
	 * 
	 * @param epQuery ep query to be populated.
	 * @param epQLTerm EpQL term to build Query from
	 * @return collected query.
	 * @throws ParseException in case of errors.
	 */
	public NativeQuery getFieldQuery(final EpQuery epQuery, final EpQLTerm epQLTerm) throws ParseException {
		final EpQLFieldDescriptor epQLFieldDescriptor = getSolrFieldDescriptor(epQuery, epQLTerm);		
		final EpQLFieldResolver epQLFieldResolver = epQLFieldDescriptor.getEpQLFieldResolver();
		final EpQLValueResolver epQLValueResolver = epQLFieldDescriptor.getEpQLValueResolver();
		final SubQueryBuilder nativeSubQueryBuilder = epQLFieldDescriptor.getSubQueryBuilder();
		
		/** Transform Ep QL term to a native term*/
		final NativeResolvedTerm nativeResolvedTerm = epQLFieldResolver.resolve(epQuery, epQLTerm, epQLFieldDescriptor);
		final List<String> resolvedValues = epQLValueResolver.resolve(epQLTerm, epQLFieldDescriptor.getType(), epQuery);		
		nativeResolvedTerm.setResolvedValues(resolvedValues);
		
		/** Build native query using information, encapsulated in a native term*/
		return nativeSubQueryBuilder.buildQuery(nativeResolvedTerm, epQLTerm);
	}

	/**
	 * Adds Boolean Clause to the list of clauses considering operator.
	 * 
	 * @param epQuery the query
	 * @param clauses list of all collected clauses.
	 * @param conj either AND or OR
	 * @param query the sub query
	 * @param operator either "=" or "!="
	 */
	public void addClause(final EpQuery epQuery, final List<NativeBooleanClause> clauses, final int conj, final NativeQuery query,
			final String operator) {
		getCompleteQueryBuilder(epQuery).addBooleanClause(clauses, conj, query, operator);
	}
	
	/**
	 * Gets descriptor of Solr field by indexed object and EPQL field.
	 *  
	 * @param epQuery EPQL query containing information about index type (Product, Category, etc.)
	 * @param epQLTerm EPQL term containing information about EPQL field
	 * @return appropriate Solr field descriptor or fail
	 * @throws ParseException if descriptor could not be found
	 */
	EpQLFieldDescriptor getSolrFieldDescriptor(final EpQuery epQuery, final EpQLTerm epQLTerm) throws ParseException {
		AbstractEpQLCustomConfiguration epQLObjectConfiguration = epQLObjectConfigurationMap.get(epQuery.getQueryType());

		if (epQLObjectConfiguration == null) {
			throw new ParseException("Can not resolve EP QL field or value. Term resolver wasn't found.");
		}

		EpQLFieldDescriptor solrTemplateDescriptor = epQLObjectConfiguration.getAvailableEpQLObjectFields().get(epQLTerm.getEpQLField());

		if (solrTemplateDescriptor == null) {
			throw new ParseException("Field: " + epQLTerm.getEpQLField().getFieldName() + " is not supported by "
					+ epQuery.getQueryType().getTypeName() + " objects.");
		}

		return solrTemplateDescriptor;
	}
	
	/**
	 * Checks if query types supported.
	 * 
	 * @param queryType the query type to check
	 * @return true if query type is supported or false otherwise
	 */
	boolean isQueryTypeSupported(final EPQueryType queryType) {
		return epQLObjectConfigurationMap.containsKey(queryType);
	}
	
	/**
	 * Checks the fetch type for query.
	 * 
	 * @param epQuery the query
	 */
	void checkFetchType(final EpQuery epQuery) {
		epQuery.setFetchType(epQLObjectConfigurationMap.get(epQuery.getQueryType()).getFetchType());
	}
	
	/**
	 * Gets complete query builder.
	 * 
	 * @param epQuery the EpQuery
	 * @return the complete query builder
	 */
	CompleteQueryBuilder getCompleteQueryBuilder(final EpQuery epQuery) {
		final AbstractEpQLCustomConfiguration epQLObjectConfiguration = epQLObjectConfigurationMap.get(epQuery.getQueryType());
		final CompleteQueryBuilder completeQueryBuilder = epQLObjectConfiguration.getCompleteQueryBuilder();
		completeQueryBuilder.setQueryPrefix(epQLObjectConfiguration.getQueryPrefix());
		completeQueryBuilder.setQueryPostfix(epQLObjectConfiguration.getQueryPostfix());
		return completeQueryBuilder;
	}

	/**
	 * Sets a map between query type (e.g. PRODUCT) and <code>EpQLObjectConfiguration</code> object.
	 * 
	 * @param epQLObjectConfiruration epQLObjectConfiruration
	 */
	public void setEpQLObjectConfiguration(final Map<EPQueryType, AbstractEpQLCustomConfiguration> epQLObjectConfiruration) {
		this.epQLObjectConfigurationMap = epQLObjectConfiruration;
	}	
}
