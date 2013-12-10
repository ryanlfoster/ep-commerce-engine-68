package com.elasticpath.common.dto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.pagination.Page;
import com.elasticpath.commons.pagination.Paginator;
import com.elasticpath.commons.pagination.PaginatorImpl;
import com.elasticpath.commons.pagination.SearchCriterion;
import com.elasticpath.commons.pagination.SearchablePaginatorLocator;


/**
 * Unit test for the {@code DatabaseMemoryMergeSearchablePaginatorLocator}.
 */
@SuppressWarnings({"PMD.AvoidDuplicateLiterals" })
public class DatabaseMemoryMergeSearchablePaginatorLocatorTest {

	private static final long NINE_LONG = 9L;
	private static final long TEN_LONG = 10L;
	private static final int TEN = 10;
	private static final int NINE = 9;
	private static final int EIGHT = 8;
	private static final int SEVEN = 7;
	private static final int SIX = 6;
	private static final int FIVE = 5;
	private static final int FOUR = 4;
	private static final int THREE = 3;
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	@SuppressWarnings("unchecked")
	private final SearchablePaginatorLocator<ItemModelFake> databasePaginator = context.mock(SearchablePaginatorLocator.class);

	@SuppressWarnings("unchecked")
	private final Paginator<ItemModelFake> paginator = context.mock(Paginator.class);

	/**
	 *  Fake class to use for instantiation.
	 */
	private class ItemModelFake implements UniquelyIdentifiable {
		private final long uidPk;
		
		ItemModelFake(final long uidPk) {
			this.uidPk = uidPk;
		}
		
		public long getUidPk() {
			return uidPk;
		}
	}
	
	/**
	 * Tests that calling find items passes through the results from the DatabasePaginator.
	 */
	@Test
	public void testFindItemsPassThrough() {
		DatabaseMemoryMergeSearchablePaginatorLocator<ItemModelFake> locator = new DatabaseMemoryMergeSearchablePaginatorLocator<ItemModelFake>();

		locator.setDatabasePaginatorLocator(databasePaginator);
		
		final Page<ItemModelFake> inputPage = new PaginatorImpl.PageImpl<ItemModelFake>(null, 0, 0, null);
		
		final List<SearchCriterion> searchCriteria = new ArrayList<SearchCriterion>();
		
		final List<ItemModelFake> outputList = new ArrayList<ItemModelFake>();
		ItemModelFake item1 = new ItemModelFake(1);
		ItemModelFake item2 = new ItemModelFake(2);
		outputList.add(item1);		
		outputList.add(item2);
		
		context.checking(new Expectations() { {
			oneOf(databasePaginator).findItems(inputPage, "TEST1", searchCriteria); will(returnValue(outputList));
			atLeast(1).of(databasePaginator).getTotalItems(searchCriteria, "TEST1"); will(returnValue(2L));
		} });
		
		final List<ItemModelFake> actualList = locator.findItems(inputPage, "TEST1", searchCriteria);
		
		assertEquals("Same as the outputList", 2, actualList.size());
		assertEquals("Same order as outputList", item1, actualList.get(0));
		assertEquals("Same order as outputList", item2, actualList.get(1));
	}
	
	/**
	 * Tests that calling getTotalItems passes through the results from the DatabasePaginator.
	 */
	@Test
	public void testGetTotalItemsPassThrough() {
		DatabaseMemoryMergeSearchablePaginatorLocator<ItemModelFake> locator = new DatabaseMemoryMergeSearchablePaginatorLocator<ItemModelFake>();
		
		locator.setDatabasePaginatorLocator(databasePaginator);
		
		final List<SearchCriterion> searchCriteria = new ArrayList<SearchCriterion>();
		
		context.checking(new Expectations() { {
			atLeast(1).of(databasePaginator).getTotalItems(searchCriteria, "TEST1"); will(returnValue(2L));
		} });
		
		long actualCount = locator.getTotalItems(searchCriteria, "TEST1");
		
		assertEquals("Return from decorated object", 2, actualCount);
		
	}
	
	/**
	 * Tests that when an item is updated that calling findItems passes through the results 
	 * from the DatabasePaginator and overrides the item with the same uidPk.
	 * getTotalItems should be unchanged.
	 */
	@Test
	public void testUpdate() {
		DatabaseMemoryMergeSearchablePaginatorLocator<ItemModelFake> locator = new DatabaseMemoryMergeSearchablePaginatorLocator<ItemModelFake>();
		
		locator.setDatabasePaginatorLocator(databasePaginator);
		
		final Page<ItemModelFake> inputPage = new PaginatorImpl.PageImpl<ItemModelFake>(null, 0, 0, null);
		
		final List<SearchCriterion> searchCriteria = new ArrayList<SearchCriterion>();
		
		final List<ItemModelFake> outputList = new ArrayList<ItemModelFake>();
		ItemModelFake item1 = new ItemModelFake(1);
		ItemModelFake item2 = new ItemModelFake(2);
		outputList.add(item1);		
		outputList.add(item2);
		
		context.checking(new Expectations() { {
			oneOf(databasePaginator).findItems(inputPage, "TEST1", searchCriteria); will(returnValue(outputList));
			atLeast(1).of(databasePaginator).getTotalItems(searchCriteria, "TEST1"); will(returnValue(2L));
		} });
		
		ItemModelFake updatedItem1 = new ItemModelFake(1);
		locator.update(updatedItem1);
		
		final List<ItemModelFake> actualList = locator.findItems(inputPage, "TEST1", searchCriteria);
		
		assertEquals("Same as the outputList", 2, actualList.size());
		assertEquals("Same order as outputList", updatedItem1, actualList.get(0));
		assertEquals("Same order as outputList", item2, actualList.get(1));
		
		long actualCount = locator.getTotalItems(searchCriteria, "TEST1");
		assertEquals("Return from decorated object", 2, actualCount);
		
		Collection<ItemModelFake> updatedCollection = locator.getUpdatedItems();
		assertEquals("Only 1 item updated", 1, updatedCollection.size());
		assertTrue("Contains the argument to update.", updatedCollection.contains(updatedItem1));
		
	}
	
	/**
	 * Tests that when an item is added that calling findItems, for the first page, 
	 * passes through the results from the DatabasePaginator.
	 * getTotalItems should be incremented.
	 */
	@Test
	public void testAddFirstPage() {
		DatabaseMemoryMergeSearchablePaginatorLocator<ItemModelFake> locator = new DatabaseMemoryMergeSearchablePaginatorLocator<ItemModelFake>();
		
		locator.setDatabasePaginatorLocator(databasePaginator);
		
		final Page<ItemModelFake> inputPage = new PaginatorImpl.PageImpl<ItemModelFake>(null, 0, 0, null);
		
		final List<SearchCriterion> searchCriteria = new ArrayList<SearchCriterion>();
		
		final List<ItemModelFake> outputList = new ArrayList<ItemModelFake>();
		ItemModelFake item1 = new ItemModelFake(1);
		outputList.add(item1);
		ItemModelFake item2 = new ItemModelFake(2);				
		outputList.add(item2);
		ItemModelFake item3 = new ItemModelFake(THREE);				
		outputList.add(item3);
		ItemModelFake item4 = new ItemModelFake(FOUR);				
		outputList.add(item4);
		ItemModelFake item5 = new ItemModelFake(FIVE);				
		outputList.add(item5);
		ItemModelFake item6 = new ItemModelFake(SIX);				
		outputList.add(item6);
		ItemModelFake item7 = new ItemModelFake(SEVEN);				
		outputList.add(item7);
		ItemModelFake item8 = new ItemModelFake(EIGHT);				
		outputList.add(item8);
		ItemModelFake item9 = new ItemModelFake(NINE);				
		outputList.add(item9);
		ItemModelFake item10 = new ItemModelFake(TEN);				
		outputList.add(item10);
		
		context.checking(new Expectations() { {
			oneOf(databasePaginator).findItems(inputPage, "TEST1", searchCriteria); will(returnValue(outputList));
			atLeast(1).of(databasePaginator).getTotalItems(searchCriteria, "TEST1"); will(returnValue(TEN_LONG));
		} });
		
		ItemModelFake addedItem1 = new ItemModelFake(1);
		locator.add(addedItem1);
		
		final List<ItemModelFake> actualList = locator.findItems(inputPage, "TEST1", searchCriteria);
		
		assertEquals("Same as the outputList", TEN, actualList.size());
		assertEquals("Same order as outputList", item1, actualList.get(0));
		assertEquals("Same order as outputList", item2, actualList.get(1));
		
		long actualCount = locator.getTotalItems(searchCriteria, "TEST1");
		assertEquals("Return from decorated object + 1", TEN + 1, actualCount);
		
		Collection<ItemModelFake> addedCollection = locator.getAddedItems();
		assertEquals("Only 1 item added", 1, addedCollection.size());
		assertTrue("Contains the argument to add.", addedCollection.contains(addedItem1));
		
	}
	
	/**
	 * Tests that when an item is added that calling findItems, for the first page, 
	 * passes through the results from the DatabasePaginator.
	 * This test is where the item should appear at the end of the first page.
	 * getTotalItems should be incremented.
	 */
	@Test
	public void testAddEndOfFirstPage() {
		DatabaseMemoryMergeSearchablePaginatorLocator<ItemModelFake> locator = new DatabaseMemoryMergeSearchablePaginatorLocator<ItemModelFake>();
		
		locator.setDatabasePaginatorLocator(databasePaginator);

		final Page<ItemModelFake> inputPage = new PaginatorImpl.PageImpl<ItemModelFake>(paginator, 1, TEN, null);
		
		final List<SearchCriterion> searchCriteria = new ArrayList<SearchCriterion>();
		
		final List<ItemModelFake> outputList = new ArrayList<ItemModelFake>();
		ItemModelFake item1 = new ItemModelFake(1);
		outputList.add(item1);
		ItemModelFake item2 = new ItemModelFake(2);				
		outputList.add(item2);
		ItemModelFake item3 = new ItemModelFake(THREE);				
		outputList.add(item3);
		ItemModelFake item4 = new ItemModelFake(FOUR);				
		outputList.add(item4);
		ItemModelFake item5 = new ItemModelFake(FIVE);				
		outputList.add(item5);
		ItemModelFake item6 = new ItemModelFake(SIX);				
		outputList.add(item6);
		ItemModelFake item7 = new ItemModelFake(SEVEN);				
		outputList.add(item7);
		ItemModelFake item8 = new ItemModelFake(EIGHT);				
		outputList.add(item8);
		ItemModelFake item9 = new ItemModelFake(NINE);				
		outputList.add(item9);
		
		context.checking(new Expectations() { {
			oneOf(databasePaginator).findItems(inputPage, "TEST1", searchCriteria); will(returnValue(outputList));
			atLeast(1).of(databasePaginator).getTotalItems(searchCriteria, "TEST1"); will(returnValue(NINE_LONG));
			allowing(paginator).getTotalItems(); will(returnValue(1L));
		} });
		
		ItemModelFake addedItem1 = new ItemModelFake(1);
		locator.add(addedItem1);
		
		final List<ItemModelFake> actualList = locator.findItems(inputPage, "TEST1", searchCriteria);
		
		assertEquals("Same as the outputList", TEN, actualList.size());
		assertEquals("Same order as outputList", item1, actualList.get(0));
		assertEquals("Same order as outputList", item2, actualList.get(1));
		assertEquals("Same order as outputList", item9, actualList.get(EIGHT));
		assertEquals("Same order as outputList", addedItem1, actualList.get(NINE));
				
	}
	
	/**
	 * Tests that when an item is added that calling findItems, for the first page, 
	 * passes through the results from the DatabasePaginator.
	 * This test is where the item should appear at the start of the second page.
	 * getTotalItems should be incremented.
	 */
	@Test
	public void testAddStartOfSecondPage() {
		DatabaseMemoryMergeSearchablePaginatorLocator<ItemModelFake> locator = new DatabaseMemoryMergeSearchablePaginatorLocator<ItemModelFake>();
		
		locator.setDatabasePaginatorLocator(databasePaginator);
		
		final Page<ItemModelFake> inputPage = new PaginatorImpl.PageImpl<ItemModelFake>(paginator, 1, TEN, null);
		final Page<ItemModelFake> inputPage2 = new PaginatorImpl.PageImpl<ItemModelFake>(paginator, 2, TEN, null);
		
		final List<SearchCriterion> searchCriteria = new ArrayList<SearchCriterion>();
		
		final List<ItemModelFake> outputList = new ArrayList<ItemModelFake>();
		ItemModelFake item1 = new ItemModelFake(1);
		outputList.add(item1);
		ItemModelFake item2 = new ItemModelFake(2);				
		outputList.add(item2);
		ItemModelFake item3 = new ItemModelFake(THREE);				
		outputList.add(item3);
		ItemModelFake item4 = new ItemModelFake(FOUR);				
		outputList.add(item4);
		ItemModelFake item5 = new ItemModelFake(FIVE);				
		outputList.add(item5);
		ItemModelFake item6 = new ItemModelFake(SIX);				
		outputList.add(item6);
		ItemModelFake item7 = new ItemModelFake(SEVEN);				
		outputList.add(item7);
		ItemModelFake item8 = new ItemModelFake(EIGHT);				
		outputList.add(item8);
		ItemModelFake item9 = new ItemModelFake(NINE);				
		outputList.add(item9);
		ItemModelFake item10 = new ItemModelFake(TEN);				
		outputList.add(item10);
		
		context.checking(new Expectations() { {
			oneOf(databasePaginator).findItems(inputPage, "TEST1", searchCriteria); will(returnValue(outputList));
			oneOf(databasePaginator).findItems(inputPage2, "TEST1", searchCriteria); will(returnValue(new ArrayList<ItemModelFake>()));
			atLeast(1).of(databasePaginator).getTotalItems(searchCriteria, "TEST1"); will(returnValue(TEN_LONG));
			
			allowing(paginator).getTotalItems(); will(returnValue(TEN_LONG));
		} });
		
		ItemModelFake addedItem1 = new ItemModelFake(1);
		locator.add(addedItem1);
		
		final List<ItemModelFake> actualList = locator.findItems(inputPage, "TEST1", searchCriteria);
		
		assertEquals("Same as the outputList", TEN, actualList.size());
		assertEquals("Same order as outputList", item1, actualList.get(0));
		assertEquals("Same order as outputList", item2, actualList.get(1));
		assertEquals("Same order as outputList", item9, actualList.get(EIGHT));
		assertEquals("Same order as outputList", item10, actualList.get(NINE));
		
		final List<ItemModelFake> actualListPage2 = locator.findItems(inputPage2, "TEST1", searchCriteria);
		assertEquals("Has only the added item", 1, actualListPage2.size());
		assertEquals("Same order as outputList", addedItem1, actualListPage2.get(0));
				
	}
	
	/**
	 * Tests that when a page full of items is added that calling findItems, for the second page, 
	 * returns the new items.
	 */
	@Test
	public void testAddNewPage() {
		DatabaseMemoryMergeSearchablePaginatorLocator<ItemModelFake> locator = new DatabaseMemoryMergeSearchablePaginatorLocator<ItemModelFake>();
		
		locator.setDatabasePaginatorLocator(databasePaginator);
		
		final Page<ItemModelFake> inputPage = new PaginatorImpl.PageImpl<ItemModelFake>(paginator, 1, TEN, null);
		final Page<ItemModelFake> inputPage2 = new PaginatorImpl.PageImpl<ItemModelFake>(paginator, 2, TEN, null);
		
		final List<SearchCriterion> searchCriteria = new ArrayList<SearchCriterion>();
		
		final List<ItemModelFake> outputList = new ArrayList<ItemModelFake>();
		ItemModelFake item1 = new ItemModelFake(1);
		outputList.add(item1);
				
		context.checking(new Expectations() { {
			oneOf(databasePaginator).findItems(inputPage, "TEST1", searchCriteria); will(returnValue(outputList));
			oneOf(databasePaginator).findItems(inputPage2, "TEST1", searchCriteria); will(returnValue(new ArrayList<ItemModelFake>()));
			atLeast(1).of(databasePaginator).getTotalItems(searchCriteria, "TEST1"); will(returnValue(1L));
			
			allowing(paginator).getTotalItems(); will(returnValue(1L));
		} });

		ItemModelFake addedItem1 = new ItemModelFake(1);
		locator.add(addedItem1);
		ItemModelFake item2 = new ItemModelFake(2);
		locator.add(item2);
		ItemModelFake item3 = new ItemModelFake(THREE);
		locator.add(item3);
		ItemModelFake item4 = new ItemModelFake(FOUR);
		locator.add(item4);
		ItemModelFake item5 = new ItemModelFake(FIVE);
		locator.add(item5);
		ItemModelFake item6 = new ItemModelFake(SIX);
		locator.add(item6);
		ItemModelFake item7 = new ItemModelFake(SEVEN);
		locator.add(item7);
		ItemModelFake item8 = new ItemModelFake(EIGHT);
		locator.add(item8);
		ItemModelFake item9 = new ItemModelFake(NINE);
		locator.add(item9);
		ItemModelFake item10 = new ItemModelFake(TEN);
		locator.add(item10);
		
		final List<ItemModelFake> actualList = locator.findItems(inputPage, "TEST1", searchCriteria);
		
		assertEquals("page size", TEN, actualList.size());
		assertEquals("Same order as outputList", item1, actualList.get(0));
		assertEquals("Same order as outputList", addedItem1, actualList.get(1));
		assertEquals("Same order as outputList", item8, actualList.get(EIGHT));
		assertEquals("Same order as outputList", item9, actualList.get(NINE));
		
		final List<ItemModelFake> actualListPage2 = locator.findItems(inputPage2, "TEST1", searchCriteria);
		assertEquals("Has only the last item added", 1, actualListPage2.size());
		assertEquals("Same order as outputList", item10, actualListPage2.get(0));
				
	}
}
