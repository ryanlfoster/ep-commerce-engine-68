package com.elasticpath.ql.parser.valueresolver.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.ql.parser.EpQLField;
import com.elasticpath.ql.parser.EpQLTerm;
import com.elasticpath.ql.parser.gen.ParseException;
import com.elasticpath.service.search.solr.AnalyzerImpl;

/**
 * Tests for abstract functionality of <code>com.elasticpath.ql.parser.AbstractEpQLTermResolver</code>.
 */
public class AbstractEpQLTermResolverTest {

	private LuceneValueResolver valueResolver;

	/**
	 * Setup test.
	 */
	@Before
	public void setUp() throws Exception {
		valueResolver = new LuceneValueResolver();
		valueResolver.setAnalyzer(new AnalyzerImpl());
	}

	/**
	 * Tests for 'com.elasticpath.ql.parser.AbstractEpQLTermResolver.resolveDateValue(String field, String queryText) throws
	 * ParseException'.
	 */
	@Test
	public void testResolveDateValue() {
		try {
			EpQLTerm epQLTerm = new EpQLTerm(EpQLField.PRODUCT_START_DATE.getFieldName(), null, null, "=", "'2008-01-30T10:22:22'");
			// not a good idea to test actual strings, since resolved value is in UTC as opposed to queryText which is in local time.
			assertNotNull(valueResolver.resolveDateValue(epQLTerm));
		} catch (ParseException e) {
			fail();
		}
	}

	/**
	 * Tests for 'com.elasticpath.ql.parser.AbstractEpQLTermResolver.resolveStringValue(String field, String queryText) throws
	 * ParseException'.
	 */
	@Test
	public void testResolveStringValue() {
		try {
			EpQLTerm epQLTerm = new EpQLTerm(EpQLField.PRODUCT_CODE.getFieldName(), null, null, "=", "'+-&&||!(){}[]^\"~*?:\\'");
			assertEquals("\\+\\-\\&\\&\\|\\|\\!\\(\\)\\{\\}\\[\\]\\^\\&quot;\\~\\*\\?\\:\\\\",
					valueResolver.resolveStringValue(epQLTerm));
		} catch (ParseException e) {
			fail();
		}
	}

	/**
	 * Tests that value resolver throws exceptions when trying to resolve string values without quotes.
	 */
	@Test
	public void testWrongStringValue() {
		try {
			EpQLTerm epQLTerm = new EpQLTerm(EpQLField.PRODUCT_CODE.getFieldName(), null, null, "=", "no quotes");
			valueResolver.resolveStringValue(epQLTerm);
			fail("This query should throw exception because string must be enclosed with single quotes");
		} catch (ParseException expected) {
			assertNotNull(expected.getMessage());
		}
	}

	/**
	 * Tests that value resolver throws exception when trying to resolve date values without quotes.
	 */
	@Test
	public void testWrongDateValue() {
		try {
			EpQLTerm epQLTerm = new EpQLTerm(EpQLField.PRODUCT_END_DATE.getFieldName(), null, null, "=", "2008-03-14:00:00:00");
			valueResolver.resolveDateValue(epQLTerm);
			fail("This query should throw exception because date must be enclosed with single quotes");
		} catch (ParseException expected) {
			assertNotNull(expected.getMessage());
		}
	}
}
