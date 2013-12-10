package com.elasticpath.service.search.solr;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.common.SolrInputDocument;

import com.elasticpath.domain.misc.SearchConfig;
import com.elasticpath.persistence.api.EpPersistenceException;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.impl.AbstractEpServiceImpl;
import com.elasticpath.service.search.IndexType;
import com.elasticpath.service.search.SearchConfigFactory;
import com.elasticpath.service.search.SolrIndexConstantFactory;

/**
 * Default implementation of <code>SolrManagerService</code>.
 */
public class DefaultSolrManager extends AbstractEpServiceImpl implements SolrManager {

	private SearchConfigFactory searchConfigFactory;

	private final Map<IndexType, SolrHolder> solrServers = new HashMap<IndexType, SolrHolder>();

	private SolrIndexConstantFactory solrIndexConstantFactory;

	private SolrDocumentPublisherFactory solrDocumentPublisherFactory;

	@Override
	public SolrServer getServer(final IndexType indexType) throws EpPersistenceException {
		SolrHolder holder;
		synchronized (solrServers) {
			holder = solrServers.get(indexType);

			if (holder == null) {
				SolrServer httpServer = createServer(indexType);

				SolrDocumentPublisher publisher = getSolrDocumentPublisherFactory().createSolrDocumentPublisher();
				publisher.setSolrServer(httpServer);
				publisher.start();

				holder = new SolrHolder();
				holder.server = httpServer;
				holder.publisher = publisher;

				solrServers.put(indexType, holder);
			}
		}

		return holder.server;
	}

	@Override
	public SearchConfig getSearchConfig(final IndexType indexType) {
		final String solrIndex = solrIndexConstantFactory.getSolrIndexConstant(indexType);
		if (solrIndex == null) {
			throw new EpServiceException("SearchConfig not defined for : " + indexType);
		}
		return getSearchConfig(solrIndex);
	}

	/**
	 * Simple wrapper for searchConfig lookup.
	 */
	private SearchConfig getSearchConfig(final String name) {
		return getSearchConfigFactory().getSearchConfig(name);
	}

	@Override
	public void addUpdateDocument(final SolrServer server, final SolrInputDocument document) throws EpPersistenceException {
		try {
			server.add(document);
		} catch (SolrServerException e) {
			throw new EpPersistenceException("SOLR Error -- add document", e);
		} catch (IOException e) {
			throw new EpPersistenceException("IO Error -- add document", e);
		}
	}

	@Override
	public void addUpdateDocument(final SolrServer server, final Collection<SolrInputDocument> documents) throws EpPersistenceException {
		try {
			server.add(documents);
		} catch (SolrServerException e) {
			throw new EpPersistenceException("SOLR Error -- add document", e);
		} catch (IOException e) {
			throw new EpPersistenceException("IO Error -- add document", e);
		}
	}

	@Override
	public void deleteDocument(final SolrServer server, final long uid) throws EpPersistenceException {
		try {
			server.deleteById(String.valueOf(uid));
		} catch (SolrServerException e) {
			throw new EpPersistenceException("SOLR Error -- delete document", e);
		} catch (IOException e) {
			throw new EpPersistenceException("IO Error -- delete document", e);
		}
	}

	@Override
	public void flushChanges(final SolrServer server, final boolean optimize) throws EpPersistenceException {
		// the list of solr servers is generally low, iterating shouldn't degrade performance
		boolean known = false;
		synchronized (solrServers) {
			for (SolrHolder holder : solrServers.values()) {
				if (holder.server.equals(server)) {
					known = true;
					break;
				}
			}
		}
		if (!known) {
			throw new EpPersistenceException("Unable to flush changes of an unmanaged SOLR server.");
		}

		try {
			if (optimize) {
				server.optimize();
			} else {
				server.commit();
			}
		} catch (SolrServerException e) {
			throw new EpServiceException("SOLR Error -- flush", e);
		} catch (IOException e) {
			throw new EpServiceException("IO Error -- flush", e);
		}
	}

	@Override
	public void rebuildSpelling(final SolrServer server) throws EpPersistenceException {
		final SolrQuery query = new SolrQuery();
		query.setQueryType(SolrIndexConstants.SPELL_CHECKER);
		query.set("cmd", "rebuild");
		try {
			server.query(query);
		} catch (SolrServerException e) {
			throw new EpPersistenceException("SOLR Error -- spelling rebuild", e);
		}
	}

	@Override
	public SolrDocumentPublisher getDocumentPublisher(final IndexType indexType) {
		SolrDocumentPublisher publisher = null;
		synchronized (solrServers) {
			SolrHolder holder = solrServers.get(indexType);
			if (holder != null) {
				publisher = holder.publisher;
			}
		}
		return publisher;
	}

	/**
	 * Creates a HTTP SOLR server for a particular server name.
	 *
	 * @param indexType {@link IndexType} to create a server for
	 * @return a HTTP SOLR server
	 * @throws EpServiceException in case of any errors
	 */
	protected SolrServer createServer(final IndexType indexType) {
		String name = indexType.getIndexName();
		String searchUrl = getSearchConfig(name).getSearchHost();
		// check in case of user error
		if (!searchUrl.endsWith("/")) {
			searchUrl = searchUrl.concat("/");
		}
		searchUrl = searchUrl.concat(name);
		try {
			return new CommonsHttpSolrServer(searchUrl);
		} catch (MalformedURLException e) {
			throw new EpServiceException("Unable to create HTTP SOLR server", e);
		}
	}

	protected SearchConfigFactory getSearchConfigFactory() {
		return searchConfigFactory;
	}

	public void setSearchConfigFactory(final SearchConfigFactory searchConfigFactory) {
		this.searchConfigFactory = searchConfigFactory;
	}

	public void setSolrIndexConstantFactory(final SolrIndexConstantFactory solrIndexConstantFactory) {
		this.solrIndexConstantFactory = solrIndexConstantFactory;
	}

	public SolrDocumentPublisherFactory getSolrDocumentPublisherFactory() {
		return solrDocumentPublisherFactory;
	}

	public void setSolrDocumentPublisherFactory(final SolrDocumentPublisherFactory solrDocumentPublisherFactory) {
		this.solrDocumentPublisherFactory = solrDocumentPublisherFactory;
	}

	/** Container class for solr index lookups. */
	private static class SolrHolder {
		private SolrServer server;

		private SolrDocumentPublisher publisher;
	}
}
