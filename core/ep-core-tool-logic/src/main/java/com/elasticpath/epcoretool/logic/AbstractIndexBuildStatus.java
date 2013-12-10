package com.elasticpath.epcoretool.logic;

import java.util.Formatter;
import java.util.List;

import com.elasticpath.domain.search.IndexBuildStatus;
import com.elasticpath.service.search.IndexBuildStatusService;

/**
 * The Class AbstractIndexBuildStatus.
 */
public abstract class AbstractIndexBuildStatus extends AbstractBaseSettings {

	/**
	 * Instantiates a new abstract index build status.
	 * 
	 * @param jdbcUrl the jdbc url
	 * @param jdbcUsername the jdbc username
	 * @param jdbcPassword the jdbc password
	 * @param jdbcDriverClass the jdbc driver class
	 * @param jdbcConnectionPoolMinIdle the jdbc connection pool min idle
	 * @param jdbcConnectionPoolMaxIdle the jdbc connection pool max idle
	 */
	public AbstractIndexBuildStatus(final String jdbcUrl, final String jdbcUsername, final String jdbcPassword, final String jdbcDriverClass,
			final Integer jdbcConnectionPoolMinIdle, final Integer jdbcConnectionPoolMaxIdle) {
		super(jdbcUrl, jdbcUsername, jdbcPassword, jdbcDriverClass, jdbcConnectionPoolMinIdle, jdbcConnectionPoolMaxIdle);
	}

	/**
	 * Execute.
	 */
	public void execute() {
		IndexBuildStatusService statusService = epCore().getIndexBuildStatusService();

		List<IndexBuildStatus> statuses = statusService.getIndexBuildStatuses();

		for (IndexBuildStatus status : statuses) {
			StringBuilder statusLine = new StringBuilder();
			Formatter formatter = new Formatter(statusLine);
			formatter.format("%-30s %15s %-15s", status.getIndexType(), status.getLastBuildDate(), status.getIndexStatus());
			formatter.close();
			getLogger().info(statusLine.toString());
		}
	}
}
