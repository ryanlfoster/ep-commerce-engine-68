package com.elasticpath.ql.parser;

import static com.elasticpath.ql.asserts.ParseAssert.assertParseInvalid;
import static com.elasticpath.ql.asserts.ParseAssert.assertParseSuccessfull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.internal.runners.JUnit4ClassRunner;
import org.junit.runner.RunWith;

import com.elasticpath.ql.custom.store.StoreConfiguration;
import com.elasticpath.ql.parser.fieldresolver.impl.NonLocalizedFieldResolver;
import com.elasticpath.ql.parser.gen.EpQueryParserImpl;
import com.elasticpath.ql.parser.querybuilder.impl.JPQLQueryBuilder;
import com.elasticpath.ql.parser.querybuilder.impl.JPQLSubQueryBuilder;
import com.elasticpath.ql.parser.valueresolver.impl.JPQLValueResolver;

/**
 * Tests that EPQL queries for the Store object are parsed correctly. 
 */
@RunWith(JUnit4ClassRunner.class)
public class EpStoreQueryParserTest {

	private static final String PARSING_SHOULD_FAIL = "Parsing should fail";
	private EpQueryParser epQueryParser;
	
	/**
	 * Sets up the parser with the store configuration.
	 */
	@Before
	public void setUp() {
		StoreConfiguration storeConfiguration = new StoreConfiguration();
		storeConfiguration.setCompleteQueryBuilder(new JPQLQueryBuilder());
		storeConfiguration.setEpQLValueResolver(new JPQLValueResolver());
		storeConfiguration.setNonLocalizedFieldResolver(new NonLocalizedFieldResolver());
		storeConfiguration.setSubQueryBuilder(new JPQLSubQueryBuilder());
		storeConfiguration.initialize();
		
		Map<EPQueryType, AbstractEpQLCustomConfiguration> epQLObjectConfiguration = new HashMap<EPQueryType, AbstractEpQLCustomConfiguration>();
		epQLObjectConfiguration.put(EPQueryType.STORE, storeConfiguration);
		
		EpQueryAssembler epQueryAssembler = new EpQueryAssembler();
		epQueryAssembler.setEpQLObjectConfiguration(epQLObjectConfiguration);
	
		epQueryParser = new EpQueryParserImpl();
		epQueryParser.setEpQueryAssembler(epQueryAssembler);
	}
	
	/**
	 * Tests that a simple correct store query is parsed correctly.
	 */
	@Test
	public void testCorrectSimpleQueryParsedSuccessfully() {
		assertParseSuccessfull("FIND Store", epQueryParser);
	}
	
	/**
	 * Tests that a simple correct store query is parsed correctly.
	 */
	@Test
	public void testIncorrectSimpleQueryParsingFailed() {
		assertParseInvalid("FIND _Store", PARSING_SHOULD_FAIL, epQueryParser);
	}
	
	/**
	 * Tests that a valid query with a simple store code restriction is parsed correctly.
	 */
	@Test
	public void testCorrectQueryWithStoreCodeParsedSuccessfully() {
		assertParseSuccessfull("FIND Store WHERE StoreCode = 'SNAPITUP'", epQueryParser);
	}
	
	/**
	 * Tests that an invalid query with a simple store code restriction is not parsed.
	 */
	@Test
	public void testIncorrectQueryWithStoreCodeParsingFailed() {
		assertParseInvalid("FIND Store WHERE StoreCode == 'SNAPITUP'", PARSING_SHOULD_FAIL, epQueryParser);
	}
	
	/**
	 * Tests that a valid query with multiple store code restriction is parsed correctly.
	 */
	@Test
	public void testCorrectQueryWithMultipleStoreCodesParsedSuccessfully() {
		assertParseSuccessfull("FIND Store WHERE (StoreCode = 'SNAPITUP' OR StoreCode = 'SLRWORLD')", epQueryParser);
		assertParseSuccessfull("FIND Store WHERE (StoreCode = 'SNAPITUP' AND StoreCode = 'SLRWORLD')", epQueryParser);
	}
	
	/**
	 * Tests that an invalid query with multiple store code restriction is not parsed.
	 */
	@Test
	public void testIncorrectQueryWithMultipleStoreCodesParsingFailed() {
		assertParseInvalid("FIND Store WHERE (StoreCode = 'SNAPITUP', StoreCode = 'SLRWORLD')", 
				PARSING_SHOULD_FAIL, epQueryParser);
		assertParseInvalid("FIND Store WHERE (StoreCode = 'SNAPITUP' AND Code = 'SLRWORLD')", 
				PARSING_SHOULD_FAIL, epQueryParser);
	}
}
