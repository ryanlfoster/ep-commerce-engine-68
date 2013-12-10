package com.elasticpath.search.index.solr.builders.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.solr.common.SolrInputDocument;

import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.LocaleDependantFields;
import com.elasticpath.search.index.pipeline.stats.impl.PipelinePerformanceImpl;
import com.elasticpath.search.index.solr.document.impl.CategorySolrInputDocumentCreator;
import com.elasticpath.service.catalog.CategoryService;
import com.elasticpath.service.search.solr.AnalyzerImpl;
import com.elasticpath.service.search.solr.IndexUtility;
import com.elasticpath.service.search.solr.IndexUtilityImpl;
import com.elasticpath.service.search.solr.SolrIndexConstants;

/**
 * Test <code>CategoryIndexBuildServiceImpl</code>.
 */
public class CategoryIndexBuilderTest {

	private CategorySolrInputDocumentCreator categoryDocumentCreator;

	private CategoryIndexBuilder categoryIndexBuilder;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private CategoryService mockCategoryService;

	private AnalyzerImpl analyzer;

	private IndexUtility indexUtility;

	/**
	 * Setup test.
	 *
	 * @throws Exception on error
	 */
	@Before
	public void setUp() throws Exception {
		categoryIndexBuilder = new CategoryIndexBuilder();
		analyzer = new AnalyzerImpl();
		indexUtility = new IndexUtilityImpl();

		mockCategoryService = context.mock(CategoryService.class);
		categoryIndexBuilder.setCategoryService(mockCategoryService);


		categoryDocumentCreator = new CategorySolrInputDocumentCreator();
		categoryDocumentCreator.setAnalyzer(analyzer);
		categoryDocumentCreator.setCategoryService(mockCategoryService);
		categoryDocumentCreator.setIndexUtility(indexUtility);
		categoryDocumentCreator.setPipelinePerformance(new PipelinePerformanceImpl());
	}

	/**
	 * Test method for 'com.elasticpath.service.index.impl.CategoryIndexBuildServiceImpl.getName()'.
	 */
	@Test
	public void testGetName() {
		assertNotNull(categoryIndexBuilder.getName());
	}

	/**
	 * Test method for 'com.elasticpath.service.index.impl.CategoryIndexBuildServiceImpl.findDeletedUids(Date)'.
	 */
	@Test
	public void testFindDeletedUids() {
		final ArrayList<Long> uidList = new ArrayList<Long>();
		context.checking(new Expectations() {
			{
				oneOf(mockCategoryService).findUidsByDeletedDate(with(any(Date.class)));
				will(returnValue(uidList));
			}
		});
		assertSame(uidList, categoryIndexBuilder.findDeletedUids(new Date()));
	}

	/**
	 * Test method for 'com.elasticpath.service.index.impl.CategoryIndexBuildServiceImpl.findAddedOrModifiedUids(Date)'.
	 */
	@Test
	public void testFindAddedOrModifiedUids() {
		final List<Long> directlyModifiedCategoryUids = new ArrayList<Long>();
		final Long categoryUid1 = Long.MAX_VALUE;
		directlyModifiedCategoryUids.add(categoryUid1);
		context.checking(new Expectations() {
			{
				oneOf(mockCategoryService).findUidsByModifiedDate(with(any(Date.class)));
				will(returnValue(directlyModifiedCategoryUids));
			}
		});

		final List<Long> indirectlyModifiedCategoryUids = new ArrayList<Long>();
		final Long categoryUid2 = Long.MIN_VALUE;
		indirectlyModifiedCategoryUids.add(categoryUid2);
		context.checking(new Expectations() {
			{
				oneOf(mockCategoryService).findDescendantCategoryUids(with(directlyModifiedCategoryUids));
				will(returnValue(indirectlyModifiedCategoryUids));
			}
		});
		final List<Long> result = categoryIndexBuilder.findAddedOrModifiedUids(new Date());
		assertEquals(2, result.size());
		assertTrue(result.contains(categoryUid1));
		assertTrue(result.contains(categoryUid2));
	}

	/**
	 * Test method for 'com.elasticpath.service.index.impl.CategoryIndexBuildServiceImpl.findAllUids()'.
	 */
	@Test
	public void testFindAllUids() {
		final ArrayList<Long> uidList = new ArrayList<Long>();
		context.checking(new Expectations() {
			{
				oneOf(mockCategoryService).findAllUids();
				will(returnValue(uidList));
			}
		});
		assertSame(uidList, categoryIndexBuilder.findAllUids());
	}

	/**
	 * Tests creating a search document for a category with all fields set.
	 */
	@Test
	public void testCreateDocument() {
		final long uidPk = 234L;
		final Date startDate = new Date(234234);
		final Date endDate = new Date(4334);
		final String code = "some code";
		final Locale locale = Locale.ENGLISH;
		final String displayName = "some name";
		final boolean available = true;
		final boolean isLinked = false;
		final long catalogUid = 223234L;
		final String catalogCode = "DABIZZLESTORE";

		final Catalog mockCatalog = context.mock(Catalog.class);
		context.checking(new Expectations() {
			{
				allowing(mockCatalog).getUidPk();
				will(returnValue(catalogUid));
				allowing(mockCatalog).getCode();
				will(returnValue(catalogCode));
				allowing(mockCatalog).getSupportedLocales();
				will(returnValue(new HashSet<Locale>(Arrays.asList(locale))));
			}
		});

		final Category mockCategory = context.mock(Category.class);
		context.checking(new Expectations() {
			{
				allowing(mockCategory).getUidPk();
				will(returnValue(uidPk));
				allowing(mockCategory).getStartDate();
				will(returnValue(startDate));
				allowing(mockCategory).getEndDate();
				will(returnValue(endDate));
				allowing(mockCategory).getCode();
				will(returnValue(code));
				allowing(mockCategory).getDisplayName(with(any(Locale.class)));
				will(returnValue(displayName));
				allowing(mockCategory).isAvailable();
				will(returnValue(available));
				allowing(mockCategory).getCatalog();
				will(returnValue(mockCatalog));
				allowing(mockCategory).isLinked();
				will(returnValue(isLinked));
			}
		});

		final LocaleDependantFields mockLocaleDependentFields = context.mock(LocaleDependantFields.class);
		final Set<String> ancestorCodes = new HashSet<String>(Arrays.asList("90000023", "90000022"));
		context.checking(new Expectations() {
			{
				allowing(mockLocaleDependentFields).getDisplayName();
				will(returnValue(displayName));

				oneOf(mockCategoryService).findAncestorCategoryCodesByCategoryUid(with(any(long.class)));
				will(returnValue(ancestorCodes));
			}
		});

		categoryDocumentCreator.setEntity(mockCategory);
		final SolrInputDocument document = categoryDocumentCreator.createDocument();
		assertNotNull(document);

		assertEquals(String.valueOf(uidPk), document.getFieldValue(SolrIndexConstants.OBJECT_UID));
		documentFieldEquals(startDate, (String) document.getFieldValue(SolrIndexConstants.START_DATE));
		documentFieldEquals(endDate, (String) document.getFieldValue(SolrIndexConstants.END_DATE));
		assertEquals(analyzer.analyze(code), document.getFieldValue(SolrIndexConstants.CATEGORY_CODE));
		documentFieldEquals(ancestorCodes, document.getFieldValues(SolrIndexConstants.PARENT_CATEGORY_CODES));

		assertEquals(analyzer.analyze(displayName),
				document.getFieldValue(indexUtility.createLocaleFieldName(SolrIndexConstants.CATEGORY_NAME, locale)));
		assertEquals(String.valueOf(mockCategory.isAvailable()), document.getFieldValue(SolrIndexConstants.DISPLAYABLE));
		assertEquals(String.valueOf(available), document.getFieldValue(SolrIndexConstants.DISPLAYABLE));
		assertEquals(analyzer.analyze(mockCatalog.getCode()), document.getFieldValue(SolrIndexConstants.CATALOG_CODE));
		assertEquals(String.valueOf(mockCategory.isLinked()), document.getFieldValue(SolrIndexConstants.CATEGORY_LINKED));
		assertEquals(String.valueOf(isLinked), document.getFieldValue(SolrIndexConstants.CATEGORY_LINKED));
	}

	private void documentFieldEquals(final Date expected, final String actual) {
		if (expected == null) {
			return;
		}
		assertEquals(analyzer.analyze(expected), actual);
	}

	private void documentFieldEquals(final Collection<?> expected, final Collection<?> actual) {
		if (expected == null || expected.isEmpty()) {
			return;
		}
		assertEquals(expected, actual);
	}
}
