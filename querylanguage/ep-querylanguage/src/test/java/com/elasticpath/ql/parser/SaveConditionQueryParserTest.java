package com.elasticpath.ql.parser;

import static com.elasticpath.ql.asserts.ParseAssert.assertParseInvalid;
import static com.elasticpath.ql.asserts.ParseAssert.assertParseSuccessfull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.ql.custom.promotion.SavedConditionConfiguration;
import com.elasticpath.ql.parser.gen.EpQueryParserImpl;
import com.elasticpath.ql.parser.querybuilder.impl.JPQLQueryBuilder;
import com.elasticpath.ql.parser.valueresolver.impl.JPQLValueResolver;

/**
 * Test cases for JavaCC based implementation of EpQueryParser.
 */
public class SaveConditionQueryParserTest { // NOPMD

	private EpQueryParser queryParser;

	/**
	 * Setup test.
	 */
	@Before
	public void setUp() {
		SavedConditionConfiguration tcConf = new SavedConditionConfiguration();
		tcConf.setCompleteQueryBuilder(new JPQLQueryBuilder());
		tcConf.setEpQLValueResolver(new JPQLValueResolver());
		tcConf.initialize();
		
		Map<EPQueryType, AbstractEpQLCustomConfiguration> epQLObjectConfiguration = new HashMap<EPQueryType, AbstractEpQLCustomConfiguration>();
		epQLObjectConfiguration.put(EPQueryType.SAVED_CONDITION, tcConf);
		
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
		assertParseSuccessfull("FIND SavedCondition", queryParser);
	}
	
	/**
	 * Should not parse it.
	 */
	@Test
	public void testInvalidParse() {
		assertParseInvalid("FIND SavedCondition WHERE foo = 'hello'", "Should not parse it.", queryParser);
	}
	
}
