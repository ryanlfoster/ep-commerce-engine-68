/**
 * 
 */
package com.elasticpath.service.search.solr.query;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.BooleanClause.Occur;

import com.elasticpath.commons.exception.EpUnsupportedOperationException;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.misc.SearchConfig;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.search.query.EpEmptySearchCriteriaException;
import com.elasticpath.service.search.query.ProductAutocompleteSearchCriteria;
import com.elasticpath.service.search.query.ProductSearchCriteria;
import com.elasticpath.service.search.query.SearchCriteria;
import com.elasticpath.service.search.solr.SolrIndexConstants;

/**
 * A Product autocomplete query composer, based on ProductQueryComposerImpl.
 * Main requirement - to search a product with name which starts with search keyword.
 */
public class ProductAutocompleteQueryComposerImpl extends ProductQueryComposerImpl {

	@Override
	public Query composeFuzzyQueryInternal(final SearchCriteria searchCriteria, final SearchConfig searchConfig) {
		return super.composeFuzzyQueryInternal(searchCriteria, searchConfig);
	}

	@Override
	public Query composeQueryInternal(final SearchCriteria searchCriteria, final SearchConfig searchConfig) {
		ProductAutocompleteSearchCriteria productSearchCriteria = (ProductAutocompleteSearchCriteria) searchCriteria;

		final BooleanQuery booleanQuery = new BooleanQuery();
		boolean hasSomeCriteria = false;
		
		if (searchCriteria.getLocale() == null) {
			throw new EpServiceException("Locale not set on product search criteria");
		}
		
		final String searchText = productSearchCriteria.getSearchText();  
		
		if (!isValidText(searchText)) {
			throw new EpEmptySearchCriteriaException("Search text not valid: [" + searchText + "]");
		}
		
		// split should always produce valid text
		for (String str : searchText.trim().toLowerCase().split("\\\\\\s")) {
			if (str.length() == 0) { continue; }
			
			hasSomeCriteria |= addSearchCriteriaForTerm(str, booleanQuery, searchCriteria, searchConfig);
		}
		
		if (!hasSomeCriteria) {
			throw new EpEmptySearchCriteriaException("Empty search criteria is not allowed!");
		}
		// check in category and subcategories
		Query categoryQuery = checkForCategory(productSearchCriteria);
		if (categoryQuery != null) {
			booleanQuery.add(categoryQuery, Occur.MUST);
		}
		// query for enabled products only
		this.addTermForEnabledProducts(productSearchCriteria, booleanQuery, searchConfig);
		// query for active products only
		this.addTermForActiveOnly(productSearchCriteria, booleanQuery);
		
		return booleanQuery;
	}
	
	private boolean addSearchCriteriaForTerm(final String word, final BooleanQuery booleanQuery,
			final SearchCriteria searchCriteria, final SearchConfig searchConfig) {
		
		BooleanQuery booleanSubQuery = new BooleanQuery();
		
		boolean hasSomeCriteria = false;
		
		hasSomeCriteria |= addWholeFieldToQuery(SolrIndexConstants.PRODUCT_NAME, 
				word, searchCriteria.getLocale(), searchConfig, booleanSubQuery, Occur.SHOULD, false);

		hasSomeCriteria |= addWholeFieldToQuery(SolrIndexConstants.BRAND_NAME, 
				word, searchCriteria.getLocale(), searchConfig, booleanSubQuery, Occur.SHOULD, false);
		
		booleanQuery.add(booleanSubQuery, Occur.MUST);
		
		return hasSomeCriteria;
			
	}

	private boolean addTermForEnabledProducts(final ProductSearchCriteria productSearchCriteria, 
			final BooleanQuery booleanQuery, final SearchConfig searchConfig) {
		
		if (productSearchCriteria.isDisplayableOnly()) {
			if (StringUtils.isBlank(productSearchCriteria.getStoreCode())) {
				throw new EpUnsupportedOperationException("StoreCode must be defined to include displayable products");
			}
			return addWholeFieldToQuery(getIndexUtility().createDisplayableFieldName(SolrIndexConstants.DISPLAYABLE,
					productSearchCriteria.getStoreCode()), String.valueOf(true), null, searchConfig, booleanQuery, Occur.MUST,
					false);
		}
		return false;
	}
	
	/**
	 * @param productSearchCriteria
	 */
	private Query checkForCategory(final ProductAutocompleteSearchCriteria productSearchCriteria) {

		BooleanQuery booleanQuery = null;
		
		if (productSearchCriteria.getCategoryUid() != null) {
			
			Category category = this.getCategoryService().get(productSearchCriteria.getCategoryUid());
			
			if (category != null) {
				// check for catalog code
				String catalogCode;
				if (productSearchCriteria.getCatalogCode() == null) {
					catalogCode = category.getCatalog().getCode();
				} else {
					catalogCode = productSearchCriteria.getCatalogCode();
				}
				// prepare boolean query
				booleanQuery = new BooleanQuery();
				// add parent category
				createQueryForCategoryAndSubCategories(booleanQuery, category, catalogCode);
			}
		}
		return booleanQuery;
	}

	/**
	 * @param booleanQuery boolean query
	 * @param category category object
	 * @param catalogCode catalog code
	 */
	private void createQueryForCategoryAndSubCategories(final BooleanQuery booleanQuery, final Category category, final String catalogCode) {
		// get category, just not lost a children categories
		Category testCategory = this.getCategoryService().get(category.getUidPk());
		
		// create query
		Query categoryQuery = createQueryForCategory(testCategory, catalogCode);
		booleanQuery.add(categoryQuery, Occur.SHOULD);
		
		// add subcategories
		if (testCategory.getChildren() == null) { return; }
		
		for (Category subCategory : testCategory.getChildren()) {
			createQueryForCategoryAndSubCategories(booleanQuery, subCategory, catalogCode);
		}
	}

	/**
	 * @param category category object 
	 * @param catalogCode catalog code
	 * @return query object
	 */
	private Query createQueryForCategory(final Category category, final String catalogCode) {
		Query categoryQuery = new TermQuery(
			new Term(this.getIndexUtility().createProductCategoryFieldName(SolrIndexConstants.PRODUCT_CATEGORY, catalogCode), 
			this.getAnalyzer().analyze(category.getCode())));
		return categoryQuery;
	}
	
	@Override
	protected Query createQuery(final String text, final String fieldName) {
		String fixedText = getAnalyzer().analyze(text);
		BooleanQuery resultQuery = new BooleanQuery();
		resultQuery.add(new PrefixQuery(new Term(fieldName, fixedText)), Occur.SHOULD);
		resultQuery.add(new TermQuery(new Term(fieldName, fixedText)), Occur.SHOULD);
		return resultQuery;
	}

}
