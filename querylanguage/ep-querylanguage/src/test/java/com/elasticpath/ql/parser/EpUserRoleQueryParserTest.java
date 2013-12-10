package com.elasticpath.ql.parser;

import static com.elasticpath.ql.asserts.ParseAssert.assertParseInvalid;
import static com.elasticpath.ql.asserts.ParseAssert.assertParseSuccessfull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.ql.custom.cmuser.UserRoleConfiguration;
import com.elasticpath.ql.parser.gen.EpQueryParserImpl;
import com.elasticpath.ql.parser.querybuilder.impl.JPQLQueryBuilder;
import com.elasticpath.ql.parser.valueresolver.impl.JPQLValueResolver;

/**
 * Test cases for JavaCC based implementation of EpQueryParser.
 */
public class EpUserRoleQueryParserTest { // NOPMD

	private EpQueryParser queryParser;

	/**
	 * Setup tests.
	 */
	@Before
	public void setUp() {
		UserRoleConfiguration urConf = new UserRoleConfiguration();
		urConf.setCompleteQueryBuilder(new JPQLQueryBuilder());
		urConf.setEpQLValueResolver(new JPQLValueResolver());
		urConf.initialize();
		
		Map<EPQueryType, AbstractEpQLCustomConfiguration> epQLObjectConfiguration = new HashMap<EPQueryType, AbstractEpQLCustomConfiguration>();
		epQLObjectConfiguration.put(EPQueryType.USER_ROLE, urConf);
		
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
		assertParseSuccessfull("FIND UserRole", queryParser);
	}
	
	/**
	 * Should not parse it.
	 */
	@Test
	public void testInvalidParse() {
		assertParseInvalid("FIND UserRole WHERE foo = 'hello'", "Should not parse it.", queryParser);
	}
	
}
