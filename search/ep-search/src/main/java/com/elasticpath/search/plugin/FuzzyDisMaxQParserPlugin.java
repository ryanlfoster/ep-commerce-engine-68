package com.elasticpath.search.plugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.DisjunctionMaxQuery;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.solr.common.SolrException;
import org.apache.solr.common.params.CommonParams;
import org.apache.solr.common.params.DefaultSolrParams;
import org.apache.solr.common.params.DisMaxParams;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.schema.IndexSchema;
import org.apache.solr.search.CacheRegenerator;
import org.apache.solr.search.FunctionQParserPlugin;
import org.apache.solr.search.QParser;
import org.apache.solr.search.QParserPlugin;
import org.apache.solr.search.QueryParsing;
import org.apache.solr.search.SolrCache;
import org.apache.solr.search.SolrIndexSearcher;
import org.apache.solr.search.SolrQueryParser;
import org.apache.solr.util.SolrPluginUtils;

import com.elasticpath.service.search.solr.DisMaxConstants;

/**
 * Solr plugin to provide a Disjunction Max Query that can use fuzzy search if required.
 */
public class FuzzyDisMaxQParserPlugin extends QParserPlugin {

	@Override
	public QParser createParser(final String qstr, final SolrParams localParams,
			final SolrParams params, final SolrQueryRequest req) {
		return new DismaxQParser(qstr, localParams, params, req);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void init(final NamedList args) {
		// nothing required
	}
}

/**
 * Parser which creates {@code BooleanQuery}s or {@code FuzzyQuery}s as required.
 * 
 * Note: Copied from DismaxQParserPlugin. SuppressWarnings used to allow a compare to be less difficult.
 */
@SuppressWarnings({ "PMD.NPathComplexity", "PMD.ExcessiveMethodLength", "PMD.ShortVariable", "PMD.ConfusingTernary", "PMD.IfStmtsMustUseBraces" })
class DismaxQParser extends QParser {

	  /**
	   * A field we can't ever find in any schema, so we can safely tell
	   * DisjunctionMaxQueryParser to use it as our defaultField, and
	   * map aliases from it to any field in our schema.
	   */
	  private static final String IMPOSSIBLE_FIELD_NAME = "\uFFFC\uFFFC\uFFFC";

	  /**
	   * Default constructor.
	   * @param qstr The query string.
	   * @param localParams The parameters from the http request.
	   * @param params The parameters from the http request.
	   * @param req The query request
	   */
	  public DismaxQParser(final String qstr, final SolrParams localParams, final SolrParams params, final SolrQueryRequest req) {
	    super(qstr, localParams, params, req);
	  }

	  private Map<String, Float> queryFields;
	  private Query parsedUserQuery;


	  private String[] boostParams;
	  private List<Query> boostQueries;
	  private Query altUserQuery;
	  private QParser altQParser;
	  
	  @Override	  
	  public Query parse() throws ParseException {
		  SolrParams solrParams = null;
		  if (getLocalParams() == null) {
			  solrParams = getParams();
		  } else {
			  solrParams = new DefaultSolrParams(getLocalParams(), getParams());
		  }

	    IndexSchema schema = getReq().getSchema();

	    queryFields = SolrPluginUtils.parseFieldBoosts(solrParams.getParams(DisMaxParams.QF));
	    Map<String, Float> phraseFields = SolrPluginUtils.parseFieldBoosts(solrParams.getParams(DisMaxParams.PF));

	    float tiebreaker = solrParams.getFloat(DisMaxParams.TIE, 0.0f);

	    int pslop = solrParams.getInt(DisMaxParams.PS, 0);
	    int qslop = solrParams.getInt(DisMaxParams.QS, 0);
	    
	    // Additional code for fuzzy queries starts here
	    boolean fuzzy = solrParams.getBool(DisMaxConstants.FUZZY, false);
		float minimumSimilarity = solrParams.getFloat(DisMaxConstants.MINIMUM_SIMILARITY, FuzzyQuery.defaultMinSimilarity);
		int prefixLength = solrParams.getInt(DisMaxConstants.MINIMUM_LENGTH, FuzzyQuery.defaultPrefixLength);
		
	    /* a parser for dealing with user input, which will convert
	     * things to DisjunctionMaxQueries
	     */
	    FuzzyDisjunctionMaxQueryParser up =
	      new FuzzyDisjunctionMaxQueryParser(schema, IMPOSSIBLE_FIELD_NAME);
	    up.addAlias(IMPOSSIBLE_FIELD_NAME,
	                tiebreaker, queryFields);
	    up.setPhraseSlop(qslop);

	    up.setFuzzy(fuzzy);
	    up.setFuzzyMinSim(minimumSimilarity);
	    up.setFuzzyPrefixLength(prefixLength);
	    // Additional code for fuzzy queries ends here

	    /* for parsing sloppy phrases using DisjunctionMaxQueries */
	    SolrPluginUtils.DisjunctionMaxQueryParser pp =
	      new SolrPluginUtils.DisjunctionMaxQueryParser(schema, IMPOSSIBLE_FIELD_NAME);
	    pp.addAlias(IMPOSSIBLE_FIELD_NAME,
	                tiebreaker, phraseFields);
	    pp.setPhraseSlop(pslop);

	    /* the main query we will execute.  we disable the coord because
	     * this query is an artificial construct
	     */
	    BooleanQuery query = new BooleanQuery(true);

	    /* * * Main User Query * * */
	    parsedUserQuery = null;
	    String userQuery = getString();
	    altUserQuery = null;
		if (userQuery == null || userQuery.trim().length() < 1) {
			// If no query is specified, we may have an alternate
			String altQ = solrParams.get(DisMaxParams.ALTQ);
			if (altQ != null) {
				altQParser = subQuery(altQ, null);
				altUserQuery = altQParser.parse();
				query.add(altUserQuery, BooleanClause.Occur.MUST);
			} else {
				throw new SolrException(SolrException.ErrorCode.BAD_REQUEST,
						"missing query string");
			}
		} else {
	      // There is a valid query string
	      userQuery = SolrPluginUtils.partialEscape(SolrPluginUtils.stripUnbalancedQuotes(userQuery)).toString();
	      userQuery = SolrPluginUtils.stripIllegalOperators(userQuery).toString();

	      String minShouldMatch = solrParams.get(DisMaxParams.MM, "100%");
	      Query dis = up.parse(userQuery);
	      parsedUserQuery = dis;

	      if (dis instanceof BooleanQuery) {
	        BooleanQuery t = new BooleanQuery();
	        SolrPluginUtils.flattenBooleanQuery(t, (BooleanQuery) dis);
	        SolrPluginUtils.setMinShouldMatch(t, minShouldMatch);
	        parsedUserQuery = t;
	      }
	      query.add(parsedUserQuery, BooleanClause.Occur.MUST);

	      /* * * Add on Phrases for the Query * * */
	      /* build up phrase boosting queries */
	      /* if the userQuery already has some quotes, stip them out.
	       * we've already done the phrases they asked for in the main
	       * part of the query, this is to boost docs that may not have
	       * matched those phrases but do match looser phrases.
	       */
	      String userPhraseQuery = userQuery.replace("\"", "");
	      Query phrase = pp.parse("\"" + userPhraseQuery + "\"");
	      if (null != phrase) {
	        query.add(phrase, BooleanClause.Occur.SHOULD);
	      }
	    }

	    /* * * Boosting Query * * */
	    boostParams = solrParams.getParams(DisMaxParams.BQ);
	    //List<Query> boostQueries = SolrPluginUtils.parseQueryStrings(req, boostParams);
	    boostQueries = null;
	    if (boostParams != null && boostParams.length > 0) {
	      boostQueries = new ArrayList<Query>();
	      for (String qs : boostParams) {
	        if (qs.trim().length() == 0) { continue; }
	        Query q = subQuery(qs, null).parse();
	        boostQueries.add(q);
	      }
	    }
	    if (null != boostQueries) {
	      if (1 == boostQueries.size() && 1 == boostParams.length) {
	        /* legacy logic */
	        Query f = boostQueries.get(0);
	        if (1.0f == f.getBoost() && f instanceof BooleanQuery) {
	          /* if the default boost was used, and we've got a BooleanQuery
	           * extract the subqueries out and use them directly
	           */
	          for (Object c : ((BooleanQuery) f).clauses()) {
	            query.add((BooleanClause) c);
	          }
	        } else {
	          query.add(f, BooleanClause.Occur.SHOULD);
	        }
	      } else {
	        for (Query f : boostQueries) {
	          query.add(f, BooleanClause.Occur.SHOULD);
	        }
	      }
	    }

	    /* * * Boosting Functions * * */

	    String[] boostFuncs = solrParams.getParams(DisMaxParams.BF);
	    if (null != boostFuncs && 0 != boostFuncs.length) {
	      for (String boostFunc : boostFuncs) {
	        if (null == boostFunc || "".equals(boostFunc)) { continue; }
	        Map<String, Float> ff = SolrPluginUtils.parseFieldBoosts(boostFunc);
	        for (String f : ff.keySet()) {
	          Query fq = subQuery(f, FunctionQParserPlugin.NAME).parse();
	          Float b = ff.get(f);
	          if (null != b) {
	            fq.setBoost(b);
	          }
	          query.add(fq, BooleanClause.Occur.SHOULD);
	        }
	      }
	    }

	    return query;
	  }

	  @Override
	  @SuppressWarnings("PMD.UnnecessaryLocalBeforeReturn")
	  public String[] getDefaultHighlightFields() {
	    String[] highFields = queryFields.keySet().toArray(new String[0]);
	    return highFields;
	  }

	  @Override
	  public Query getHighlightQuery() throws ParseException {
	    return parsedUserQuery;
	  }

	  /**
	   * Adds debug info.
	   * @param debugInfo The debug info
	   */
	  public void addDebugInfo(final NamedList<Object> debugInfo) {
	    super.addDebugInfo(debugInfo);
	    debugInfo.add("altquerystring", altUserQuery);
	    if (null != boostQueries) {
	      debugInfo.add("boost_queries", boostParams);
	      debugInfo.add("parsed_boost_queries",
	                QueryParsing.toString(boostQueries, getReq().getSchema()));
	    }
	    debugInfo.add("boostfuncs", getReq().getParams().getParams(DisMaxParams.BF));   
	  }

	/**
	 * A subclass of SolrQueryParser that supports aliasing fields for
	 * constructing DisjunctionMaxQueries.
	 * 
	 * Copied from SolrPlugUtils.DisjunctionMaxQueryParser from solr-core-1.3.0.
	 * Parts of patch for SOLR 1.3 from SOLR-629 merged in. Did not merge in the parsing stuff.
	 * Note that the vendor branch for queryParser has a prefixFields parameter. That code
	 * allowed a PrefixQuery to be used for some fields. This code has not been included. @see
	 * https://svn.elasticpath.com/perfect_ep/pd/vendor/lucene/custom/ep/org.apache.lucene/src/java/org/apache/lucene/queryParser/QueryParser.java
	 */
	@SuppressWarnings({ "PMD.NPathComplexity", "PMD.ExcessiveMethodLength", "PMD.ShortVariable", "PMD.ConfusingTernary", "PMD.IfStmtsMustUseBraces" })
	public class FuzzyDisjunctionMaxQueryParser extends SolrQueryParser {
		
		private static final int DEFAULT_ALIAS_MAP_SIZE = 3;
		private boolean doFuzzyQuery = false;
		
		/**
		 * If set to true then this parser will return {@code FuzzyQuery}s. The default is false.
		 * @param doFuzzyQuery True if fuzzy queries should be returned. 
		 */
		public void setFuzzy(final boolean doFuzzyQuery) {
			this.doFuzzyQuery = doFuzzyQuery;
		}
	
	  /** A simple container for storing alias info.
	   * @see #aliases
	   */
	  protected class Alias {
	    private float tie;
	    private Map<String, Float> fields;
	    
	    /**
	     * Gets the tie value.
	     * @return The tie value.
	     */
	    public float getTie() {
	    	return tie;
	    }
	    
	    /**
	     * The fields for this alias.
	     * @return The fields.
	     */
	    public Map<String, Float> getFields() {
	    	return fields;
	    }
	  }
	
	  /**
	   * Where we store a map from field name we expect to see in our query
	   * string, to Alias object containing the fields to use in our
	   * DisjunctionMaxQuery and the tiebreaker to use.
	   */
	  private final Map<String, Alias> aliases = new HashMap<String, Alias>(DEFAULT_ALIAS_MAP_SIZE);
	      
	  /**
	   * Normal constructor.
	   * @param s the index schema.
	   * @param defaultField The default field.
	   */
	  public FuzzyDisjunctionMaxQueryParser(final IndexSchema s, final String defaultField) {
	    super(s, defaultField);
	    // don't trust that our parent class won't ever change it's default
	    setDefaultOperator(QueryParser.Operator.OR);
	  }
	  
	  /**
	   * Constructor.
	   * @param s The index schema
	   */
	  public FuzzyDisjunctionMaxQueryParser(final IndexSchema s) {
	    this(s, null);
	  }
	
	  /**
	   * Add an alias to this query parser.
	   *
	   * @param field the field name that should trigger alias mapping
	   * @param fieldBoosts the mapping from fieldname to boost value that
	   *                    should be used to build up the clauses of the
	   *                    DisjunctionMaxQuery.
	   * @param tiebreaker to the tiebreaker to be used in the
	   *                   DisjunctionMaxQuery
	   * @see SolrPluginUtils#parseFieldBoosts
	   */
	  public void addAlias(final String field, final float tiebreaker,
	                       final Map<String, Float> fieldBoosts) {
	
	    Alias a = new Alias();
	    a.tie = tiebreaker;
	    a.fields = fieldBoosts;
	    aliases.put(field, a);
	  }
	
	  /**
	   * Delegates to the super class unless the field has been specified
	   * as an alias.
	   * @param field the field to query
	   * @param queryText the text to query with.
	   * @return the query
	   * @throws ParseException If a failure is detected.
	   */
	  @SuppressWarnings("PMD.AvoidDeeplyNestedIfStmts")
	  protected Query getFieldQuery(final String field, final String queryText)
	    throws ParseException {
	          
	    if (aliases.containsKey(field)) {
	              
	      Alias a = aliases.get(field);
	      DisjunctionMaxQuery q = new DisjunctionMaxQuery(a.tie);
	
	      /* we might not get any valid queries from delegation,
	       * in which we should return null
	       */
	      boolean ok = false;
	              
	      for (String f : a.fields.keySet()) {
	    	  
	    	  Query sub;
	    	  if (doFuzzyQuery) {
	    		  // N.B.: SOLR-629 patch contained code that handled aliases and recursed through them to
	    		  // create the correct fuzzy query. That code has not been included because it relies on the
	    		  // parsing code which also has not been included.
	    		  sub = getFuzzyQuery(f, queryText, getFuzzyMinSim());
	    	  } else {
	    		  sub = getFieldQuery(f, queryText);
	    	  }

	        if (null != sub) {
	          if (null != a.fields.get(f)) {
	            sub.setBoost(a.fields.get(f));
	          }
	          q.add(sub);
	          ok = true;
	        }
	      }
	      if (ok) {
	    	  return q;
	      }
	      return null;
	    } 
	    return super.getFieldQuery(field, queryText);	    
	  }
	      
	}

	/**
	 * Determines the correct Sort based on the request parameter "sort".
	 *
	 * @param req The request.
	 * @return null if no sort is specified.
	 */
	public static Sort getSort(final SolrQueryRequest req) {
	
		
	  String sort = req.getParams().get(CommonParams.SORT); 
	  if (null == sort || sort.equals("")) {
	    return null;
	  }
	
	  SolrException sortE = null;
	  Sort ss = null;
	  try {
	    ss = QueryParsing.parseSort(sort, req.getSchema());
	  } catch (SolrException e) {
	    sortE = e;
	  }
	
	  if ((null == ss) || (null != sortE)) {
	    /* we definitely had some sort of sort string from the user,
	     * but no SortSpec came out of it
	     */
//	    SolrCore.log.warn("Invalid sort \"" + sort + "\" was specified, ignoring", sortE);
	    return null;
	  }
	      
	  return ss;
	}
	
	/**
	 * Builds a list of Query objects that should be used to filter results.
	 * @see CommonParams#FQ
	 * @param req The request
	 * @return null if no filter queries
	 * @throws ParseException if a failure is detected
	 */
	public static List<Query> parseFilterQueries(final SolrQueryRequest req) throws ParseException {
	  return parseQueryStrings(req, req.getParams().getParams(org.apache.solr.common.params.CommonParams.FQ));
	}
	
	/** Turns an array of query strings into a List of Query objects.
	 *
	 * @param req The request
	 * @param queries The queries
	 * @return null if no queries are generated
	 * @throws ParseException if a failure is detected
	 */
	@SuppressWarnings({ "PMD.IfStmtsMustUseBraces" })
	public static List<Query> parseQueryStrings(final SolrQueryRequest req, 
	                                            final String[] queries) throws ParseException {    
	  if (null == queries || 0 == queries.length) { return null; }
	  List<Query> out = new LinkedList<Query>();
	  for (String q : queries) {
	    if (null != q && 0 != q.trim().length()) {
	      out.add(QParser.getParser(q, null, req).getQuery());
	    }
	  }
	  return out;
	}
	
	/**
	 * A CacheRegenerator that can be used whenever the items in the cache
	 * are not dependant on the current searcher.
	 *
	 * <p>
	 * Flat out copies the oldKey=&gt;oldVal pair into the newCache
	 * </p>
	 */
	public static class IdentityRegenerator implements CacheRegenerator {
		/**
		 * Regenerates the item.
		 * 
		 * @param newSearcher The new searcher.
		 * @param newCache The new cache.
		 * @param oldCache The old cache.
		 * @param oldKey The old key
		 * @param oldVal The old value
		 * @return true
		 * @throws IOException if a failure is detected.
		 */
	  public boolean regenerateItem(final SolrIndexSearcher newSearcher,
	                                final SolrCache newCache,
	                                final SolrCache oldCache,
	                                final Object oldKey,
	                                final Object oldVal)
	    throws IOException {
	
	    newCache.put(oldKey, oldVal);
	    return true;
	  }
	          
	}

}

