package com.elasticpath.service.catalogview.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Test pagination service. 
 */
public class PaginationServiceImplTest {

	private static final int NUM_RESULTS_0 = 0;

	private static final int PAGENUMBER_0 = 0;

	private static final int PAGENUMBER_4 = 4;

	private static final int PAGENUMBER_3 = 3;

	private static final int NUM_RESULTS_67 = 67;

	private static final int PAGINATION_20 = 20;

	private static final int NUM_RESULTS_60 = 60;

	private final PaginationServiceImpl paginationService = new PaginationServiceImpl();

	/**
	 * Calculate last page number when results even.
	 * @param pagination
	 * @param numberOfResults
	 */
	@Test
	public void testGetLastPageNumberEven() {
						
		int pageNumber = paginationService.getLastPageNumber(PAGINATION_20, NUM_RESULTS_60);
		assertEquals(PAGENUMBER_3, pageNumber);
		
	}
	
	/**
	 * Calculate last page number when results odd.
	 * @param pagination
	 * @param numberOfResults
	 */
	@Test
	public void testGetLastPageNumberOdd() {				
				
		int pageNumber = paginationService.getLastPageNumber(PAGINATION_20, NUM_RESULTS_67);
		assertEquals(PAGENUMBER_4, pageNumber);
		
	}
	
	/**
	 * Calculate last page number when results zero.
	 * @param pagination
	 * @param numberOfResults
	 */
	@Test
	public void testGetLastPageNumberZero() {			
				
		int pageNumber = paginationService.getLastPageNumber(PAGINATION_20, NUM_RESULTS_0);
		assertEquals(PAGENUMBER_0, pageNumber);
		
	}
}
