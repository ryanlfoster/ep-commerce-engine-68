/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.commons.pagination;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Before;
import org.junit.Test;

import com.elasticpath.commons.pagination.impl.PaginationConfigImpl;
import com.elasticpath.service.changeset.ChangeSetMemberSortingField;

/**
 * Test cases for {@link PaginatorImpl}.
 */
public class PaginatorImplTest {

	private PaginatorImpl<String> paginator;

	private static final List<String> ITEMS = Arrays.asList("item1", "item2", "item11");
	private static final int PAGE_SIZE = 2;
	private static final DirectedSortingField [] ORDERING_FIELD = new DirectedSortingField [] { 
		new DirectedSortingField(ChangeSetMemberSortingField.OBJECT_ID, SortingDirection.ASCENDING) };

	/**
	 * Sets up the test case by creating a pagination adapter.
	 * 
	 * @throws java.lang.Exception on error
	 */
	@Before
	public void setUp() throws Exception {
		paginator = new PaginatorImpl<String>();
		
		PaginatorLocator<String> paginatorLocator = new PaginatorLocator<String>() {
			public java.util.List<String> findItems(final Page<String> page, final String objectId) {
				int fromIndex = page.getPageStartIndex() - 1;
				int toIndex = Math.min(fromIndex + page.getPageSize(), ITEMS.size());
				return ITEMS.subList(fromIndex, toIndex);
			}

			public long getTotalItems(final String objectId) {
				return ITEMS.size();
			}	
		};
		
		paginator.setPaginatorLocator(paginatorLocator);
		
		PaginationConfig paginationConfig = new PaginationConfigImpl();
		paginationConfig.setPageSize(PAGE_SIZE);
		paginationConfig.setSortingFields(ORDERING_FIELD);
		paginationConfig.setObjectId("");
		paginator.init(paginationConfig);
	}

	/**
	 * Tests next() returns the next page.
	 */
	@Test
	public void testGoToNextPage() {
		Page<String> newPage = paginator.next();
		assertNotNull("The page is expected to not be undefined", newPage);
		assertEquals(ITEMS.get(2), newPage.getItems().get(0));
		assertEquals(1, newPage.getItems().size());
	}

	/**
	 * Tests last() disposes the current page and returns a new one.
	 */
	@Test
	public void testGoToLastPage() {
		Page<String> newPage = paginator.last();
		assertNotNull("The page is expected to not be null", newPage);
		assertEquals(ITEMS.get(2), newPage.getItems().get(0));
		assertEquals(1, newPage.getItems().size());
	}

	/**
	 * Tests last() disposes the current page and returns a new one.
	 */
	@Test
	public void testGoToFirstPage() {
		Page<String> newPage = paginator.first();
		assertNotNull("The page is expected to not be null", newPage);
		assertEquals(ITEMS.get(0), newPage.getItems().get(0));
		assertEquals(ITEMS.get(1), newPage.getItems().get(1));
		assertEquals(2, newPage.getItems().size());
	}

	/**
	 * Tests last() disposes the current page and returns a new one.
	 */
	@Test
	public void testGoToPreviousPage() {
		paginator.last();
		Page<String> newPage = paginator.previous();
		assertNotNull("The page is expected to not be null", newPage);
		assertEquals(ITEMS.get(0), newPage.getItems().get(0));
		assertEquals(ITEMS.get(1), newPage.getItems().get(1));
		assertEquals(2, newPage.getItems().size());
	}


	/**
	 * Tests that if previous() is invoked on the first available page 
	 * <code>null</code> value will be returned.
	 */
	@Test
	public void testPreviousBeforeFirstPageIsNull() {
		Page<String> firstPage = paginator.first();
		Page<String> beforeFirstPage = paginator.previous();
		assertEquals(firstPage.getPageNumber(), beforeFirstPage.getPageNumber());
		assertEquals(firstPage.getPageEndIndex(), beforeFirstPage.getPageEndIndex());
		assertEquals(firstPage.getPageStartIndex(), beforeFirstPage.getPageStartIndex());
		assertEquals(firstPage.getPageSize(), beforeFirstPage.getPageSize());
		assertEquals(firstPage.getItems(), beforeFirstPage.getItems());
		assertEquals(firstPage.getTotalItems(), beforeFirstPage.getTotalItems());
		assertEquals(firstPage.getTotalPages(), beforeFirstPage.getTotalPages());
	}

	/**
	 * Tests that if next() is invoked on the last available page 
	 * a page with the same parameters will be returned.
	 */
	@Test
	public void testNextAfterLastPageIsSame() {
		Page<String> nextPage = paginator.next();
		Page<String> oneAfterNextPage = paginator.next();
		assertEquals(nextPage.getPageNumber(), oneAfterNextPage.getPageNumber());
		assertEquals(nextPage.getPageEndIndex(), oneAfterNextPage.getPageEndIndex());
		assertEquals(nextPage.getPageStartIndex(), oneAfterNextPage.getPageStartIndex());
		assertEquals(nextPage.getPageSize(), oneAfterNextPage.getPageSize());
		assertEquals(nextPage.getItems(), oneAfterNextPage.getItems());
		assertEquals(nextPage.getTotalItems(), oneAfterNextPage.getTotalItems());
		assertEquals(nextPage.getTotalPages(), oneAfterNextPage.getTotalPages());
	}

	/**
	 * Tests getTotalItems() uses the pagination adapter to retrieve the value.
	 */
	@Test
	public void testGetTotalItems() {
		Page<String> page = paginator.next();		
		assertEquals("The total items are expected to be retrieved from the adapter", ITEMS.size(), page.getTotalItems());
	}
	
	/**
	 *
	 */
	@Test
	public void testPaginatorWithNoAvailableData() {
		
		PaginatorLocator<String> paginatorLocator = new PaginatorLocator<String>() {
			public java.util.List<String> findItems(final Page<String> page, final String objectId) {
				fail("The page should not even try to load from the database when total items retured is 0");
				return null;
			}

			public long getTotalItems(final String objectId) {
				return 0;
			}	
		};
		
		paginator.setPaginatorLocator(paginatorLocator);
		
		PaginationConfig paginationConfig = new PaginationConfigImpl();
		paginationConfig.setPageSize(PAGE_SIZE);
		paginationConfig.setSortingFields(ORDERING_FIELD);
		paginationConfig.setObjectId("");
		paginator.init(paginationConfig);

		assertEquals(0, paginator.getCurrentPage().getTotalItems());
		
		Page<String> page = paginator.first();
		verifyPageEmpty(page);
		
		page = paginator.last();
		verifyPageEmpty(page);

		page = paginator.next();
		verifyPageEmpty(page);

		page = paginator.previous();
		verifyPageEmpty(page);

	}

	/**
	 *
	 */
	@Test
	public void testPaginatorReturningNullItemsList() {
		
		PaginatorLocator<String> paginatorLocator = new PaginatorLocator<String>() {
			public java.util.List<String> findItems(final Page<String> page, final String objectId) {
				fail("The page should not even try to load from the database when total items retured is 0");
				return null;
			}

			public long getTotalItems(final String objectId) {
				return 0;
			}	
		};
		
		paginator.setPaginatorLocator(paginatorLocator);
		
		
		PaginationConfig paginationConfig = new PaginationConfigImpl();
		paginationConfig.setPageSize(PAGE_SIZE);
		paginationConfig.setSortingFields(ORDERING_FIELD);
		paginationConfig.setObjectId("");
		paginator.init(paginationConfig);
		
		Page<String> page = paginator.first();
		verifyPageEmpty(page);
		
		page = paginator.last();
		verifyPageEmpty(page);

		page = paginator.next();
		verifyPageEmpty(page);

		page = paginator.previous();
		verifyPageEmpty(page);

	}

	/**
	 *
	 * @param page
	 */
	private void verifyPageEmpty(final Page<String> page) {
		assertNotNull(page);
		assertNotNull(page.getItems());
		assertTrue(CollectionUtils.isEmpty(page.getItems()));
		assertEquals(PAGE_SIZE, page.getPageSize());
		assertEquals(0, page.getPageEndIndex());
		assertEquals(0, page.getPageStartIndex());
		assertEquals(1, page.getPageNumber());
	}

}
