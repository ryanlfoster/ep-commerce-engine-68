package com.elasticpath.search.index.solr.document.impl;

import java.util.Locale;
import java.util.Set;

import org.apache.solr.common.SolrInputDocument;

import com.elasticpath.domain.catalog.Category;
import com.elasticpath.service.catalog.CategoryService;
import com.elasticpath.service.search.solr.IndexUtility;
import com.elasticpath.service.search.solr.SolrIndexConstants;

/**
 * Creates a {@link SolrInputDocument} for an {@link Category}.
 */
public class CategorySolrInputDocumentCreator extends AbstractDocumentCreatingTask<Category> {

	private CategoryService categoryService;

	private IndexUtility indexUtility;

	/**
	 * Creates a {@link SolrInputDocument} based on the {@link Category}.
	 * 
	 * @return the {@link SolrInputDocument}.
	 */
	public SolrInputDocument createDocument() {

		if (getEntity() == null) {
			return null;
		}

		final SolrInputDocument document = new SolrInputDocument();
		addFieldToDocument(document, SolrIndexConstants.OBJECT_UID, String.valueOf(getEntity().getUidPk()));
		addFieldToDocument(document, SolrIndexConstants.PARENT_CATEGORY_CODES, retrieveAncestorCodes(getEntity()));
		addFieldToDocument(document, SolrIndexConstants.START_DATE, getAnalyzer().analyze(getEntity().getStartDate()));
		addFieldToDocument(document, SolrIndexConstants.END_DATE, getAnalyzer().analyze(getEntity().getEndDate()));
		addFieldToDocument(document, SolrIndexConstants.CATEGORY_CODE, getAnalyzer().analyze(getEntity().getCode()));
		addFieldToDocument(document, SolrIndexConstants.DISPLAYABLE, String.valueOf(getEntity().isAvailable()));
		addFieldToDocument(document, SolrIndexConstants.CATALOG_CODE, String.valueOf(getEntity().getCatalog().getCode()));
		addFieldToDocument(document, SolrIndexConstants.CATEGORY_LINKED, String.valueOf(getEntity().isLinked()));

		// have to use all the supported locales here because if one locale isn't defined, we need
		// to allow fallback (as in the SF)
		for (final Locale locale : getEntity().getCatalog().getSupportedLocales()) {
			addLocaleFields(document, getEntity(), locale);
		}

		return document;
	}

	private void addLocaleFields(final SolrInputDocument document, final Category category, final Locale locale) {
		addFieldToDocument(document, getIndexUtility().createLocaleFieldName(SolrIndexConstants.CATEGORY_NAME, locale), getAnalyzer().analyze(
				category.getDisplayName(locale)));
	}

	/**
	 * Returns the ancestor category codes of the given <code>category</code>.
	 * 
	 * @param category - the category to search for ancestor category codes.
	 * @return Set of ancestor category codes
	 */
	private Set<String> retrieveAncestorCodes(final Category category) {
		return categoryService.findAncestorCategoryCodesByCategoryUid(category.getUidPk());
	}

	/**
	 * @param categoryService the categoryService to set
	 */
	public void setCategoryService(final CategoryService categoryService) {
		this.categoryService = categoryService;
	}

	/**
	 * @return the categoryService
	 */
	public CategoryService getCategoryService() {
		return categoryService;
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
