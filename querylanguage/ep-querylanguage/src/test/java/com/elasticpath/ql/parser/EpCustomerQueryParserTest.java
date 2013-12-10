package com.elasticpath.ql.parser;

import static com.elasticpath.ql.asserts.ParseAssert.assertParseInvalid;
import static com.elasticpath.ql.asserts.ParseAssert.assertParseSuccessfull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.ql.custom.customer.CustomerConfiguration;
import com.elasticpath.ql.parser.gen.EpQueryParserImpl;
import com.elasticpath.ql.parser.querybuilder.impl.LuceneQueryBuilder;
import com.elasticpath.ql.parser.valueresolver.impl.LuceneValueResolver;
import com.elasticpath.service.search.solr.AnalyzerImpl;

/**
 * Test cases for JavaCC based implementation of EpQueryParser.
 */
public class EpCustomerQueryParserTest { // NOPMD

	private EpQueryParser queryParser;

	/**
	 * Setup test.
	 */
	@Before
	public void setUp() {
		queryParser = new EpQueryParserImpl();

		EpQueryAssembler epQueryAssembler = new EpQueryAssembler();

		Map<EPQueryType, AbstractEpQLCustomConfiguration> epQLObjectConfiruration = new HashMap<EPQueryType, AbstractEpQLCustomConfiguration>();

		LuceneValueResolver conventionalValueResolver = new LuceneValueResolver();
		conventionalValueResolver.setAnalyzer(new AnalyzerImpl());
		epQueryAssembler.setEpQLObjectConfiguration(epQLObjectConfiruration);
		
		CustomerConfiguration customerConf = new CustomerConfiguration();
		customerConf.setEpQLValueResolver(conventionalValueResolver);
		customerConf.setCompleteQueryBuilder(new LuceneQueryBuilder());
		customerConf.initialize();
		epQLObjectConfiruration.put(EPQueryType.CUSTOMER, customerConf);
	
		queryParser.setEpQueryAssembler(epQueryAssembler);
	}

	/**
	 * Should parse it.
	 */
	@Test
	public void testSuccessfulParse() {
		assertParseSuccessfull("FIND Customer", queryParser);
	}
	
	/**
	 * Should not parse it.
	 */
	@Test
	public void testInvalidParse() {
		assertParseInvalid("FIND Customer WHERE email = \"foo@bar.com\"", "Should not parse it.", queryParser);
	}

}
