package com.elasticpath.search.index.solr.document.impl;

import org.apache.log4j.Logger;
import org.apache.solr.common.SolrInputDocument;

import com.elasticpath.domain.shipping.ShippingServiceLevel;
import com.elasticpath.service.search.solr.IndexUtility;
import com.elasticpath.service.search.solr.SolrIndexConstants;

/**
 * Creates a {@link SolrInputDocument} for an {@link ShippingServiceLevel}.
 */
public class ShippingServiceLevelSolrInputDocumentCreator extends AbstractDocumentCreatingTask<ShippingServiceLevel> {

	private static final Logger LOG = Logger.getLogger(ShippingServiceLevelSolrInputDocumentCreator.class);

	private IndexUtility indexUtility;

	/**
	 * Creates a {@link SolrInputDocument} based on the {@link ShippingServiceLevel}.
	 * 
	 * @return the {@link SolrInputDocument}.
	 */
	public SolrInputDocument createDocument() {
		if (getEntity() == null) {
			return null;
		}

		final SolrInputDocument document = new SolrInputDocument();
		addShippingServiceLevelFieldsToDocument(document, getEntity());
		LOG.trace("Constructing shipping service level document is finished for uid " + getEntity().getUidPk());

		return document;
	}

	/**
	 * Puts simple fields from the given shipping service level to the index document.
	 * 
	 * @param document index document
	 * @param shippingServiceLevel shipping service level to index
	 */
	void addShippingServiceLevelFieldsToDocument(final SolrInputDocument document, final ShippingServiceLevel shippingServiceLevel) {
		addFieldToDocument(document, SolrIndexConstants.OBJECT_UID, String.valueOf(shippingServiceLevel.getUidPk()));
		addFieldToDocument(document, SolrIndexConstants.SERVICE_LEVEL_CODE, shippingServiceLevel.getCode());
		addFieldToDocument(document, SolrIndexConstants.ACTIVE_FLAG, String.valueOf(shippingServiceLevel.isEnabled()));
		addFieldToDocument(document, SolrIndexConstants.CARRIER, shippingServiceLevel.getCarrier());
		addFieldToDocument(document, SolrIndexConstants.REGION, shippingServiceLevel.getShippingRegion().getName());
		addFieldToDocument(document, SolrIndexConstants.SERVICE_LEVEL_NAME, shippingServiceLevel.getDisplayName(shippingServiceLevel.getStore()
				.getCatalog().getDefaultLocale(), true));
		addFieldToDocument(document, SolrIndexConstants.STORE_NAME, shippingServiceLevel.getStore().getName());
		LOG.trace("Finished adding basic fields");
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
