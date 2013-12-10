package com.elasticpath.ql.parser;

import static com.elasticpath.ql.asserts.ParseAssert.assertParseInvalid;
import static com.elasticpath.ql.asserts.ParseAssert.assertParseSuccessfull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.ql.custom.tax.TaxCodeConfiguration;
import com.elasticpath.ql.parser.gen.EpQueryParserImpl;
import com.elasticpath.ql.parser.querybuilder.impl.JPQLQueryBuilder;
import com.elasticpath.ql.parser.valueresolver.impl.JPQLValueResolver;

/**
 * Test cases for JavaCC based implementation of EpQueryParser.
 */
public class EpTaxCodeQueryParserTest { // NOPMD

	private EpQueryParser queryParser;

	/**
	 * Setup test.
	 */
	@Before
	public void setUp() {
		TaxCodeConfiguration taxCodeConf = new TaxCodeConfiguration();
		taxCodeConf.setCompleteQueryBuilder(new JPQLQueryBuilder());
		taxCodeConf.setEpQLValueResolver(new JPQLValueResolver());
		taxCodeConf.initialize();
		
		Map<EPQueryType, AbstractEpQLCustomConfiguration> epQLObjectConfiguration = new HashMap<EPQueryType, AbstractEpQLCustomConfiguration>();
		epQLObjectConfiguration.put(EPQueryType.TAXCODE, taxCodeConf);
		
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
		assertParseSuccessfull("FIND TaxCode", queryParser);
	}
	
	/**
	 * Should not parse it.
	 */
	@Test
	public void testInvalidParse() {
		assertParseInvalid("FIND TaxCode WHERE foo = 'hello'", "Should not parse it.", queryParser);
	}
	
}
