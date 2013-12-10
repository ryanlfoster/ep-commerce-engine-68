/**
 * 
 */
package com.elasticpath.ql.parser.querybuilder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import org.apache.lucene.search.Query;

import com.elasticpath.ql.parser.EpQLField;
import com.elasticpath.ql.parser.EpQLFieldDescriptor;
import com.elasticpath.ql.parser.EpQLFieldType;
import com.elasticpath.ql.parser.EpQLTerm;
import com.elasticpath.ql.parser.NativeResolvedTerm;
import com.elasticpath.ql.parser.gen.ParseException;
import com.elasticpath.ql.parser.querybuilder.impl.LuceneRangeSubQueryBuilder;

/**
 * Tests for RangeQueryBuilder.
 */
public class RangeQueryAssemblerTest {

	private LuceneRangeSubQueryBuilder queryAssembler;

	/**
	 * Setup test.
	 */
	@Before
	public void setUp() throws Exception {
		queryAssembler = new LuceneRangeSubQueryBuilder();
	}

	/**
	 * Test method for
	 * {@link com.elasticpath.ql.parser.RangeQueryAssemblerImpl#getRangeQuery(com.elasticpath.ql.parser.EpQLFieldDescriptor, 
	 * java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testGetRangeQuery() {
		try {
			EpQLTerm epQLTerm = new EpQLTerm(EpQLField.PRODUCT_CODE.getFieldName(), null, null, "=", null);
			assertNull(queryAssembler.getRangeQuery(null, epQLTerm, null));
		} catch (ParseException e) {
			fail();
		}
		try {
			EpQLTerm epQLTerm = new EpQLTerm(EpQLField.PRODUCT_CODE.getFieldName(), null, null, "&", null);
			assertNull(queryAssembler.getRangeQuery(null, epQLTerm, null));
			fail();
		} catch (ParseException expected) {
			assertNotNull(expected);
		}

		try {
			NativeResolvedTerm solrAssembler = new NativeResolvedTerm(new EpQLFieldDescriptor());
			// String type - wrong
			EpQLTerm epQLTerm = new EpQLTerm(EpQLField.PRODUCT_CODE.getFieldName(), null, null, ">", null);
			assertNull(queryAssembler.getRangeQuery(solrAssembler, epQLTerm, null));
			fail();
		} catch (ParseException expected) {
			assertNotNull(expected);
		}
	}

	/**
	 * Test method for
	 * {@link com.elasticpath.ql.parser.RangeQueryAssemblerImpl#getDateRangeQuery(com.elasticpath.ql.parser.assembler.NativeResolvedTerm, 
	 * java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testGetDateRangeQuery() {
		NativeResolvedTerm solrAssembler = new NativeResolvedTerm(new EpQLFieldDescriptor());
		solrAssembler.getFieldDescriptor().setType(EpQLFieldType.DATE);
		solrAssembler.setResolvedField("startDate");
		String date = "2008-07-31T23:59:59.999Z";

		try {
			EpQLTerm epQLTerm = new EpQLTerm(EpQLField.PRODUCT_START_DATE.getFieldName(), null, null, ">", null);
			Query rangeQuery = queryAssembler.getRangeQuery(solrAssembler, epQLTerm, date);
			assertNotNull(rangeQuery);
			assertEquals("startDate:{2008-07-31T23:59:59.999Z TO *}", rangeQuery.toString());
		} catch (ParseException e) {
			fail();
		}

		try {
			EpQLTerm epQLTerm = new EpQLTerm(EpQLField.PRODUCT_START_DATE.getFieldName(), null, null, ">=", null);
			Query rangeQuery = queryAssembler.getRangeQuery(solrAssembler, epQLTerm, date);
			assertNotNull(rangeQuery);
			assertEquals("startDate:[2008-07-31T23:59:59.999Z TO *]", rangeQuery.toString());
		} catch (ParseException e) {
			fail();
		}
		
		try {
			EpQLTerm epQLTerm = new EpQLTerm(EpQLField.PRODUCT_START_DATE.getFieldName(), null, null, "<", null);
			Query rangeQuery = queryAssembler.getRangeQuery(solrAssembler, epQLTerm, date);
			assertNotNull(rangeQuery);
			assertEquals("startDate:{* TO 2008-07-31T23:59:59.999Z}", rangeQuery.toString());
		} catch (ParseException e) {
			fail();
		}
		try {
			EpQLTerm epQLTerm = new EpQLTerm(EpQLField.PRODUCT_START_DATE.getFieldName(), null, null, "<=", null);
			Query rangeQuery = queryAssembler.getRangeQuery(solrAssembler, epQLTerm, date);
			assertNotNull(rangeQuery);
			assertEquals("startDate:[* TO 2008-07-31T23:59:59.999Z]", rangeQuery.toString());
		} catch (ParseException e) {
			fail();
		}
	}

	/**
	 * Test method for
	 * {@link com.elasticpath.ql.parser.RangeQueryAssemblerImpl#getRangeQuery(com.elasticpath.ql.parser.assembler.NativeResolvedTerm, 
	 * java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testGetFloatRangeQuery() {
		NativeResolvedTerm solrAssembler = new NativeResolvedTerm(new EpQLFieldDescriptor());
		solrAssembler.getFieldDescriptor().setType(EpQLFieldType.FLOAT);
		solrAssembler.setResolvedField("price");
		String floatString = "65.21";

		try {
			EpQLTerm epQLTerm = new EpQLTerm(EpQLField.PRODUCT_PRICE.getFieldName(), null, null, ">", null);
			Query rangeQuery = queryAssembler.getRangeQuery(solrAssembler, epQLTerm, floatString);
			assertNotNull(rangeQuery);
			assertEquals("price:{65.21 TO *}", rangeQuery.toString());
		} catch (ParseException e) {
			fail();
		}
	}
}
