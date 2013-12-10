package com.elasticpath.search.index.solr.document.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.common.SolrInputDocument;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerAddress;
import com.elasticpath.service.search.solr.SolrIndexConstants;

/**
 * Creates a {@link SolrInputDocument} for an {@link Customer}.
 */
public class CustomerSolrInputDocumentCreator extends AbstractDocumentCreatingTask<Customer> {

	/**
	 * Creates a {@link SolrInputDocument} based on the {@link Customer}.
	 * 
	 * @return the {@link SolrInputDocument}.
	 */
	public SolrInputDocument createDocument() {
		if (getEntity() == null) {
			return null;
		}
		if (getEntity().getUserId() == null || getEntity().getUserId().trim().length() == 0) {
			return null;
		}

		final SolrInputDocument document = new SolrInputDocument();
		addFieldToDocument(document, SolrIndexConstants.OBJECT_UID, String.valueOf(getEntity().getUidPk()));
		addFieldToDocument(document, SolrIndexConstants.USER_ID, getAnalyzer().analyze(getEntity().getUserId()));
		addFieldToDocument(document, SolrIndexConstants.FIRST_NAME, getAnalyzer().analyze(getEntity().getFirstName()));
		addFieldToDocument(document, SolrIndexConstants.LAST_NAME, getAnalyzer().analyze(getEntity().getLastName()));
		addFieldToDocument(document, SolrIndexConstants.EMAIL, getAnalyzer().analyze(getEntity().getEmail()));
		addFieldToDocument(document, SolrIndexConstants.PHONE_NUMBER, getAnalyzer().analyze(getEntity().getPhoneNumber()));
		addFieldToDocument(document, SolrIndexConstants.CREATE_TIME, getAnalyzer().analyze(getEntity().getCreationDate()));
		addFieldToDocument(document, SolrIndexConstants.STORE_CODE, getAnalyzer().analyze(getEntity().getStoreCode()));

		List<String> addresses = new ArrayList<String>();

		for (CustomerAddress address : getEntity().getAddresses()) {
			addresses.add(getAnalyzer().analyze(address.getZipOrPostalCode()));
		}

		addFieldToDocument(document, SolrIndexConstants.ZIP_POSTAL_CODE, addresses);

		addPreferredBillingAddress(getEntity(), document);

		return document;
	}

	private void addPreferredBillingAddress(final Customer customer, final SolrInputDocument document) {
		CustomerAddress preferredBillingAddress = customer.getPreferredBillingAddress();

		if (preferredBillingAddress != null) {
			addFieldToDocument(document, SolrIndexConstants.PREFERRED_BILLING_ADDRESS, getAnalyzer()
					.analyze(preferredBillingAddress.toPlainString()));
		}
	}
}
