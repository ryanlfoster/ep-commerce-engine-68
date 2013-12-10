package com.elasticpath.sfweb.listeners;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

/**
 * Tests for SearchEngineQueryTagger.
 */
public class ReferralSearchEngineQueryTaggerTest {
	
	private static final String ECOMMERCE = "ecommerce";
	private static final String ELASTIC = "elastic";
	
	/**
	 * Test that GOOGLE search terms are correctly identified in a search for
	 * two search terms. 
	 */
	@Test
	public void testGoogleTypical() {
		String refererHeader = "http://www.google.ca/search?hl=en&q=ecommerce+elastic&btnG=Google+Search&meta=";
		ReferralSearchEngineQueryTagger tagger = new ReferralSearchEngineQueryTagger();
		List<String> searchTerms = tagger.getSearchTermsFromHeader(refererHeader);
		assertEquals(2, searchTerms.size());
		assertTrue(searchTerms.contains(ECOMMERCE));
		assertTrue(searchTerms.contains(ELASTIC));
	}
	
	/**
	 * Test that GOOGLE search terms are correctly identified in a search for a single search term. 
	 */
	@Test
	public void testGoogleSingle() {
		String refererHeader = "http://www.google.ca/search?hl=en&q=ecommerce&btnG=Google+Search&meta=";
		ReferralSearchEngineQueryTagger tagger = new ReferralSearchEngineQueryTagger();
		List<String> searchTerms = tagger.getSearchTermsFromHeader(refererHeader);
		assertEquals(1, searchTerms.size());
		assertTrue(searchTerms.contains(ECOMMERCE));
	}
	
	/**
	 * Test that YAHOO search terms are correctly identified in a search for
	 * two search terms. 
	 */
	@Test
	public void testYahooTypical() {
		String refererHeader = "http://ca.search.yahoo.com/search?p=ecommerce+elastic&fr=yfp-t-501&toggle=1&cop=&ei=UTF-8";
		ReferralSearchEngineQueryTagger tagger = new ReferralSearchEngineQueryTagger();
		List<String> searchTerms = tagger.getSearchTermsFromHeader(refererHeader);
		assertEquals(2, searchTerms.size());
		assertTrue(searchTerms.contains(ECOMMERCE));
		assertTrue(searchTerms.contains(ELASTIC));
	}
	
	/**
	 * Test that YAHOO search terms are correctly identified in a search for a single search term. 
	 */
	@Test
	public void testYahooSingle() {
		String refererHeader = "http://ca.search.yahoo.com/search?p=ecommerce&fr=yfp-t-501&toggle=1&cop=&ei=UTF-8";
		ReferralSearchEngineQueryTagger tagger = new ReferralSearchEngineQueryTagger();
		List<String> searchTerms = tagger.getSearchTermsFromHeader(refererHeader);
		assertEquals(1, searchTerms.size());
		assertTrue(searchTerms.contains(ECOMMERCE));
	}
	
	/**
	 * Test that MSN search terms are correctly identified in a search for
	 * two search terms. 
	 */
	@Test
	public void testMsnTypical() {
		String refererHeader = "http://search.live.com/results.aspx?q=ecommerce+elastic&go=&form=QBLH&qs=n";
		ReferralSearchEngineQueryTagger tagger = new ReferralSearchEngineQueryTagger();
		List<String> searchTerms = tagger.getSearchTermsFromHeader(refererHeader);
		assertEquals(2, searchTerms.size());
		assertTrue(searchTerms.contains(ECOMMERCE));
		assertTrue(searchTerms.contains(ELASTIC));
	}
	
	/**
	 * Test that MSN search terms are correctly identified in a search for a single search term. 
	 */
	@Test
	public void testMsnSingle() {
		String refererHeader = "http://search.live.com/results.aspx?q=ecommerce&go=&form=QBLH&qs=n";
		ReferralSearchEngineQueryTagger tagger = new ReferralSearchEngineQueryTagger();
		List<String> searchTerms = tagger.getSearchTermsFromHeader(refererHeader);
		assertEquals(1, searchTerms.size());
		assertTrue(searchTerms.contains(ECOMMERCE));
	}
}
