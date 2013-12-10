package com.elasticpath.epcoretool.logic;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.elasticpath.domain.search.IndexBuildStatus;
import com.elasticpath.domain.search.IndexNotification;
import com.elasticpath.domain.search.UpdateType;
import com.elasticpath.service.search.IndexBuildStatusService;
import com.elasticpath.service.search.IndexNotificationService;
import com.elasticpath.service.search.IndexType;

/**
 * The Class AbstractRequestReindex.
 */
public abstract class AbstractRequestReindex extends AbstractEpCore {

	private static final int THREAD_SLEEP_TIMEOUT = 3000;

	private final Map<IndexType, Date> originalIndexStatus = new HashMap<IndexType, Date>();

	private final Set<IndexType> indexTypes = new HashSet<IndexType>();

	/**
	 * Instantiates a new abstract request reindex.
	 * 
	 * @param jdbcUrl the jdbc url
	 * @param jdbcUsername the jdbc username
	 * @param jdbcPassword the jdbc password
	 * @param jdbcDriverClass the jdbc driver class
	 * @param jdbcConnectionPoolMinIdle the jdbc connection pool min idle
	 * @param jdbcConnectionPoolMaxIdle the jdbc connection pool max idle
	 */
	public AbstractRequestReindex(final String jdbcUrl, final String jdbcUsername, final String jdbcPassword, final String jdbcDriverClass,
			final Integer jdbcConnectionPoolMinIdle, final Integer jdbcConnectionPoolMaxIdle) {
		super(jdbcUrl, jdbcUsername, jdbcPassword, jdbcDriverClass, jdbcConnectionPoolMinIdle, jdbcConnectionPoolMaxIdle);
	}

	/**
	 * Capture starting status.
	 */
	private void captureStartingStatus() {
		IndexBuildStatusService statusService = epCore().getIndexBuildStatusService();

		for (IndexBuildStatus status : statusService.getIndexBuildStatuses()) {
			if (indexTypes.contains(status.getIndexType())) {
				originalIndexStatus.put(status.getIndexType(), status.getLastBuildDate());
			}
		}
	}

	/**
	 * Wait for completion.
	 */
	private void waitForCompletion() {

		List<IndexType> done = new ArrayList<IndexType>();

		IndexBuildStatusService statusService = epCore().getIndexBuildStatusService();

		for (;;) {

			if (originalIndexStatus.isEmpty()) {
				break;
			}

			boolean changes = false;

			try {
				Thread.sleep(THREAD_SLEEP_TIMEOUT);
			} catch (InterruptedException e) {
				return;
			}

			for (IndexBuildStatus status : statusService.getIndexBuildStatuses()) {

				IndexType indexType = status.getIndexType();

				if (!originalIndexStatus.keySet().contains(indexType)) {
					continue;
				}

				Date originalLastBuildDate = originalIndexStatus.get(indexType);

				if (originalLastBuildDate == null) {
					originalLastBuildDate = new Date(0);
				}

				if (status.getLastBuildDate() != null && status.getLastBuildDate().after(originalLastBuildDate)) {
					done.add(indexType);
					originalIndexStatus.remove(indexType);
					changes = true;
				}

			}
			if (changes) {
				getLogger().info("Indexes done: " + done + " Waiting for: " + originalIndexStatus.keySet());
			}
		}
	}

	/**
	 * Execute.
	 * 
	 * @param index the index
	 * @param wait the wait
	 */
	public void execute(final String index, final boolean wait) {

		indexTypes.clear();

		if (index != null && index.length() > 0) {
			indexTypes.add(IndexType.findFromName(index));
		} else {
			indexTypes.addAll(IndexType.values());
		}

		if (wait) {
			captureStartingStatus();
		}

		IndexNotificationService indexNotificationService = epCore().getIndexNotificationService();

		for (IndexType indexType : indexTypes) {
			IndexNotification notification = epCore().createIndexNotification(indexType, UpdateType.REBUILD);
			indexNotificationService.add(notification);
			getLogger().info("Created notification that " + indexType + " requires a rebuild.");
		}

		if (wait) {
			waitForCompletion();
		}
	}
}
