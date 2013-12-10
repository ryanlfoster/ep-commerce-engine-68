package com.elasticpath.epcoretool.logic;

import java.util.Locale;

import com.elasticpath.service.search.index.IndexSearchResult;
import com.elasticpath.service.search.index.IndexSearchService;
import com.elasticpath.service.search.query.ProductSearchCriteria;

/**
 * The Class AbstractPingSearchServer.
 */
public abstract class AbstractPingSearchServer extends AbstractEpCore {

	/**
	 * Instantiates a new abstract ping search server.
	 * 
	 * @param jdbcUrl the jdbc url
	 * @param jdbcUsername the jdbc username
	 * @param jdbcPassword the jdbc password
	 * @param jdbcDriverClass the jdbc driver class
	 * @param jdbcConnectionPoolMinIdle the jdbc connection pool min idle
	 * @param jdbcConnectionPoolMaxIdle the jdbc connection pool max idle
	 */
	public AbstractPingSearchServer(final String jdbcUrl, final String jdbcUsername, final String jdbcPassword, final String jdbcDriverClass,
			final Integer jdbcConnectionPoolMinIdle, final Integer jdbcConnectionPoolMaxIdle) {
		super(jdbcUrl, jdbcUsername, jdbcPassword, jdbcDriverClass, jdbcConnectionPoolMinIdle, jdbcConnectionPoolMaxIdle);
	}

	/**
	 * Execute.
	 */
	public void execute() {

		getLogger().info("Pinging search service for Products in your default locale (" + Locale.getDefault() + ").");

		IndexSearchService indexSearchService = epCore().getIndexSearchService();

		ProductSearchCriteria criteria = new ProductSearchCriteria();
		criteria.setLocale(Locale.getDefault());
		criteria.setMatchAll(true);

		IndexSearchResult res = indexSearchService.search(criteria);

		getLogger().info("Search service has " + res.getNumFound() + " products indexed in your default locale.");

	}
}
