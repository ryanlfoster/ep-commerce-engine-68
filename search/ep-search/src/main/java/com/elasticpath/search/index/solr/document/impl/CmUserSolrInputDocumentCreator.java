package com.elasticpath.search.index.solr.document.impl;

import org.apache.log4j.Logger;
import org.apache.solr.common.SolrInputDocument;

import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.cmuser.UserRole;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.search.solr.IndexUtility;
import com.elasticpath.service.search.solr.SolrIndexConstants;

/**
 * Creates a {@link SolrInputDocument} for an {@link CmUser}.
 */
public class CmUserSolrInputDocumentCreator extends AbstractDocumentCreatingTask<CmUser> {

	private static final Logger LOG = Logger.getLogger(CmUserSolrInputDocumentCreator.class);

	private IndexUtility indexUtility;

	/**
	 * Creates a {@link SolrInputDocument} based on the {@link CmUser}.
	 * 
	 * @return the {@link SolrInputDocument}.
	 */
	public SolrInputDocument createDocument() {
		if (getEntity() == null) {
			return null;
		}

		final SolrInputDocument solrInputDocument = new SolrInputDocument();

		addFieldToDocument(solrInputDocument, SolrIndexConstants.OBJECT_UID, String.valueOf(getEntity().getUidPk()));
		addFieldToDocument(solrInputDocument, SolrIndexConstants.USER_NAME, getAnalyzer().analyze(getEntity().getUserName()));
		addFieldToDocument(solrInputDocument, SolrIndexConstants.LAST_NAME, getAnalyzer().analyze(getEntity().getLastName()));
		addFieldToDocument(solrInputDocument, SolrIndexConstants.FIRST_NAME, getAnalyzer().analyze(getEntity().getFirstName()));
		addFieldToDocument(solrInputDocument, SolrIndexConstants.EMAIL, getAnalyzer().analyze(getEntity().getEmail()));
		addFieldToDocument(solrInputDocument, SolrIndexConstants.STATUS, getAnalyzer().analyze(getEntity().getUserStatus().getPropertyKey()));
		addFieldToDocument(solrInputDocument, SolrIndexConstants.ALL_CATALOGS_ACCESS, getAnalyzer().analyze(
				String.valueOf(getEntity().isAllCatalogsAccess())));
		addFieldToDocument(solrInputDocument, SolrIndexConstants.ALL_STORES_ACCESS, getAnalyzer().analyze(
				String.valueOf(getEntity().isAllStoresAccess())));

		for (final UserRole userRole : getEntity().getUserRoles()) {
			addFieldToDocument(solrInputDocument, SolrIndexConstants.USER_ROLE, getAnalyzer().analyze(userRole.getName()));
		}

		for (final Catalog catalog : getEntity().getCatalogs()) {
			addFieldToDocument(solrInputDocument, SolrIndexConstants.CATALOG_CODE, getAnalyzer().analyze(catalog.getCode()));
		}

		for (final Store store : getEntity().getStores()) {
			addFieldToDocument(solrInputDocument, SolrIndexConstants.STORE_CODE, getAnalyzer().analyze(store.getCode()));
		}

		LOG.trace("Constructing cmuser document finished for uid " + getEntity().getUidPk());

		return solrInputDocument;
	}

	/**
	 * @param indexUtility the indexUtility to set
	 */
	public void setIndexUtility(final IndexUtility indexUtility) {
		this.indexUtility = indexUtility;
	}

	/**
	 * @return the indexUtility
	 */
	public IndexUtility getIndexUtility() {
		return indexUtility;
	}

}
