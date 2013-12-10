package com.elasticpath.ql.parser;

import static com.elasticpath.ql.asserts.ParseAssert.assertParseInvalid;
import static com.elasticpath.ql.asserts.ParseAssert.assertParseSuccessfull;
import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.ql.custom.shipping.WarehouseConfiguration;
import com.elasticpath.ql.parser.gen.EpQueryParserImpl;
import com.elasticpath.ql.parser.querybuilder.impl.JPQLQueryBuilder;
import com.elasticpath.ql.parser.valueresolver.impl.JPQLValueResolver;

/**
 * Test cases for JavaCC based implementation of EpQueryParser.
 */
public class EpWarehouseQueryParserTest { // NOPMD

	private EpQueryParser queryParser;

	/**
	 * Setup tests.
	 */
	@Before
	public void setUp() {
		WarehouseConfiguration srConf = new WarehouseConfiguration();
		srConf.setCompleteQueryBuilder(new JPQLQueryBuilder());
		srConf.setEpQLValueResolver(new JPQLValueResolver());
		srConf.initialize();
		
		Map<EPQueryType, AbstractEpQLCustomConfiguration> epQLObjectConfiguration = new HashMap<EPQueryType, AbstractEpQLCustomConfiguration>();
		epQLObjectConfiguration.put(EPQueryType.WAREHOUSE, srConf);
		
		EpQueryAssembler epQueryAssembler = new EpQueryAssembler();
		epQueryAssembler.setEpQLObjectConfiguration(epQLObjectConfiguration);
	
		queryParser = new EpQueryParserImpl();
		queryParser.setEpQueryAssembler(epQueryAssembler);
	}

	/**
	 * Should parse it.
	 */
	@Test
	public void testSuccessfulParse() {
		assertParseSuccessfull("FIND Warehouse", queryParser);
	}
	
	/**
	 * Should not parse it.
	 */
	@Test
	public void testInvalidParse() {
		assertParseInvalid("FIND Warehouse WHERE foo = 'hello'", "Should not parse it.", queryParser);
	}
	
	/**
	 * Tests that jpql query has not changed.
	 * @throws EpQLParseException parse esception
	 */
	@Test
	public void testJPQLQuery() throws EpQLParseException {
		String nativeQuery = queryParser.parse("FIND Warehouse").getNativeQuery().getNativeQuery();
		assertEquals("SELECT w.code FROM WarehouseImpl w     ORDER BY w.code ASC ", nativeQuery);
	}
}
