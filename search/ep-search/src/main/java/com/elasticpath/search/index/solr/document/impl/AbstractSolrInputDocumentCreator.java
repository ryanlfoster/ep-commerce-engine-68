package com.elasticpath.search.index.solr.document.impl;

import java.util.Collection;

import org.apache.commons.collections.CollectionUtils;
import org.apache.solr.common.SolrInputDocument;

import com.elasticpath.search.index.pipeline.AbstractIndexingTask;
import com.elasticpath.service.search.index.Analyzer;

/**
 * Abstract {@link SolrInputDocument} creator.
 * 
 * @param <IN> see {@code IndexingTask}
 * @param <OUT> see {@code IndexingTask}
 */
public abstract class AbstractSolrInputDocumentCreator<IN, OUT> extends AbstractIndexingTask<IN, OUT> {

	private Analyzer analyzer;

	/**
	 * Add the value for the field to a {@link SolrInputDocument}. If the fields already has a value, than the new value gets concatenated to the
	 * existing value, this way creating a single value field, used for sorting.
	 * 
	 * @param document - the document to which to add
	 * @param fieldName - the name of the field
	 * @param value - the value to add
	 * @return whether the operation was successful (was not aborted)
	 */
	protected boolean addFieldNotMultiValuedToDocument(final SolrInputDocument document, final String fieldName, final String value) {
		String valueToAdd = value;
		if (document.containsKey(fieldName) && value != null && value.length() > 0) {
			Object existingFieldValue = document.getFieldValue(fieldName);
			valueToAdd = value.concat(existingFieldValue.toString());
			document.removeField(fieldName);
		}

		return addFieldToDocument(document, fieldName, valueToAdd);
	}

	/**
	 * Adds a field and value to a {@link SolrInputDocument}. If the value is empty or null, it is not added to the document in order to optimize it.
	 * 
	 * @param document the document to add fields to
	 * @param fieldName the field name
	 * @param value the value to add to the field
	 * @return whether the value was added
	 */
	protected boolean addFieldToDocument(final SolrInputDocument document, final String fieldName, final String value) {
		if (value == null) {
			return false;
		} else if (value.length() == 0) {
			return false;
		}
		document.addField(fieldName, value);
		return true;
	}

	/**
	 * Adds a field and a value collection to a {@link SolrInputDocument}. If the value is empty or null, it is not added to the document in order to
	 * optimize it.
	 * 
	 * @param document the document to add fields to
	 * @param fieldName the field name
	 * @param value the value to add to the field
	 * @return whether the value was added
	 */
	protected boolean addFieldToDocument(final SolrInputDocument document, final String fieldName, final Collection<?> value) {
		if (CollectionUtils.isEmpty(value)) {
			return false;
		}
		document.addField(fieldName, value);
		return true;
	}

	/**
	 * @param analyzer the analyzer to set
	 */
	public void setAnalyzer(final Analyzer analyzer) {
		this.analyzer = analyzer;
	}

	/**
	 * @return the analyzer
	 */
	public Analyzer getAnalyzer() {
		return analyzer;
	}
}
