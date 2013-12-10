package com.elasticpath.ql.parser;

import static com.elasticpath.ql.asserts.ParseAssert.assertParseInvalid;
import static com.elasticpath.ql.asserts.ParseAssert.assertParseSuccessfull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.ql.custom.tax.TaxJurisdictionConfiguration;
import com.elasticpath.ql.parser.fieldresolver.impl.NonLocalizedFieldResolver;
import com.elasticpath.ql.parser.gen.EpQueryParserImpl;
import com.elasticpath.ql.parser.querybuilder.impl.JPQLQueryBuilder;
import com.elasticpath.ql.parser.querybuilder.impl.JPQLSubQueryBuilder;
import com.elasticpath.ql.parser.valueresolver.impl.JPQLValueResolver;

/**
 * Test cases for JavaCC based implementation of EpQueryParser.
 */
public class EpTaxJurisdictionQueryParserTest { // NOPMD

	private EpQueryParser queryParser;

	/**
	 * Setup test.
	 */
	@Before
	public void setUp() {
		TaxJurisdictionConfiguration taxJurisdictionConf = new TaxJurisdictionConfiguration();
		taxJurisdictionConf.setCompleteQueryBuilder(new JPQLQueryBuilder());
		taxJurisdictionConf.setEpQLValueResolver(new JPQLValueResolver());
		taxJurisdictionConf.setNonLocalizedFieldResolver(new NonLocalizedFieldResolver());
		taxJurisdictionConf.setSubQueryBuilder(new JPQLSubQueryBuilder());
		taxJurisdictionConf.initialize();
		
		Map<EPQueryType, AbstractEpQLCustomConfiguration> epQLObjectConfiguration = new HashMap<EPQueryType, AbstractEpQLCustomConfiguration>();
		epQLObjectConfiguration.put(EPQueryType.TAXJURISDICTION, taxJurisdictionConf);
		
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
		assertParseSuccessfull("FIND TaxJurisdiction", queryParser);
	}
	
	/**
	 * Should parse it.
	 */
	@Test
	public void testWhereCodeParse() {
		assertParseSuccessfull("FIND TaxJurisdiction WHERE TaxJurisdictionCode = 'hello'", queryParser);
	}
	
	/**
	 * Should parse it.
	 */
	@Test
	public void testWhereRegionParse() {
		assertParseSuccessfull("FIND TaxJurisdiction WHERE TaxJurisdictionRegion = 'hello'", queryParser);
	}
	
	/**
	 * Should parse it.
	 */
	@Test
	public void testWhereCodeOrRegionParse() {
		assertParseSuccessfull("FIND TaxJurisdiction WHERE TaxJurisdictionCode = 'hello' OR TaxJurisdictionRegion = 'hello'", queryParser);
	}
	
	/**
	 * Should parse it.
	 */
	@Test
	public void testWhereCodeAndRegionParse() {
		assertParseSuccessfull("FIND TaxJurisdiction WHERE TaxJurisdictionCode = 'hello' AND TaxJurisdictionRegion = 'hello'", queryParser);
	}
	
	/**
	 * Should not parse it.
	 */
	@Test
	public void testInvalidParse() {
		assertParseInvalid("FIND TaxJurisdiction WHERE foo = 'hello'", "Should not parse it.", queryParser);
	}

}
