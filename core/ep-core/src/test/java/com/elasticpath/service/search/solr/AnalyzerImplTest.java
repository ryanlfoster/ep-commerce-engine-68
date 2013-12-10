package com.elasticpath.service.search.solr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Date;
import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;

/**
 * Test <code>Analyzer</code>.
 */
public class AnalyzerImplTest {
	
	private static final String NULL = "";


	private AnalyzerImpl analyzer;

	@Before
	public void setUp() throws Exception {
		analyzer = new AnalyzerImpl();
	}

	/**
	 * Test method for 'com.elasticpath.service.index.impl.AnalyzerImpl.analyze(Date)'.
	 */
	@Test
	public void testAnalyzeDate() {
		assertEquals(NULL, analyzer.analyze((Date) null));
		final Date date = new Date();
		
		final String xmlDateRegex = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}Z";
		if (!Pattern.matches(xmlDateRegex, analyzer.analyze(date))) {
			fail("Expected regex <" + xmlDateRegex + "> bus was: <" + analyzer.analyze(date) + ">");
		}
	}

	/**
	 * Test method for 'com.elasticpath.service.index.impl.AnalyzerImpl.analyze(int)'.
	 */
	@Test
	public void testAnalyzeInt() {
		assertEquals("0", analyzer.analyze(0));
		assertEquals("1", analyzer.analyze(1));
	}

	/**
	 * Test method for 'com.elasticpath.service.index.impl.AnalyzerImpl.analyze(long)'.
	 */
	@Test
	public void testAnalyzeLong() {
		assertEquals("0", analyzer.analyze(0L));
		assertEquals("1", analyzer.analyze(1L));
	}
	
	/**
	 * Test method for 'com.elasticpath.service.index.impl.AnalyzerImpl.analyze(String)'.
	 */
	@Test
	public void testAnalyzeString() {
		assertEquals("0", analyzer.analyze("0"));
		assertEquals("1", analyzer.analyze("1"));
		assertEquals("", analyzer.analyze((String) null));
	}

	/**
	 * Test method for 'com.elasticpath.service.index.impl.AnalyzerImpl.analyze(String, Boolean)'.
	 */
	@Test
	public void testAnalyzeStringForceEscapeQuotes() {
		assertEquals("0", analyzer.analyze("0", true));
		assertEquals("1", analyzer.analyze("1", true));
		assertEquals("", analyzer.analyze(null, true));
		assertEquals("test\\\"", analyzer.analyze("test\"", true));
		assertEquals("\\\"test", analyzer.analyze("\"test", true));
		assertEquals("\\\"test\\\"", analyzer.analyze("\"test\"", true));
		assertEquals("\\\"test\\\"", analyzer.analyze("\"test\\\"", true));
		assertEquals("\\\"test\\\"", analyzer.analyze("\\\"test\"", true));
	}
}
