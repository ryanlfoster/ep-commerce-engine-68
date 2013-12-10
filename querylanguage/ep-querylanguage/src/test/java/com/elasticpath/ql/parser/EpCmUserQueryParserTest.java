package com.elasticpath.ql.parser;

import static com.elasticpath.ql.asserts.ParseAssert.assertParseInvalid;
import static com.elasticpath.ql.asserts.ParseAssert.assertParseSuccessfull;
import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.ql.custom.cmuser.CmUserConfiguration;
import com.elasticpath.ql.parser.fieldresolver.impl.NonLocalizedFieldResolver;
import com.elasticpath.ql.parser.gen.EpQueryParserImpl;
import com.elasticpath.ql.parser.querybuilder.impl.JPQLQueryBuilder;
import com.elasticpath.ql.parser.querybuilder.impl.JPQLSubQueryBuilder;
import com.elasticpath.ql.parser.valueresolver.impl.JPQLValueResolver;

/**
 * Test cases for JavaCC based implementation of EpQueryParser.
 */
public class EpCmUserQueryParserTest { // NOPMD

	private EpQueryParser queryParser;

	/**
	 * Setup test.
	 */
	@Before
	public void setUp() {
		CmUserConfiguration configuration = new CmUserConfiguration();
		configuration.setCompleteQueryBuilder(new JPQLQueryBuilder());
		configuration.setEpQLValueResolver(new JPQLValueResolver());
		configuration.setNonLocalizedFieldResolver(new NonLocalizedFieldResolver());
		configuration.setSubQueryBuilder(new JPQLSubQueryBuilder());
		configuration.initialize();
		
		Map<EPQueryType, AbstractEpQLCustomConfiguration> epQLObjectConfiguration = new HashMap<EPQueryType, AbstractEpQLCustomConfiguration>();
		epQLObjectConfiguration.put(EPQueryType.CMUSER, configuration);
		
		EpQueryAssembler epQueryAssembler = new EpQueryAssembler();
		epQueryAssembler.setEpQLObjectConfiguration(epQLObjectConfiguration);
	
		queryParser = new EpQueryParserImpl();
		queryParser.setEpQueryAssembler(epQueryAssembler);
	}

	/**
	 * Test if a epql WITH a where clause parses ok and returns expected JPQL.
	 */
	@Test
	public void testSuccessfulParseWithWhere() {
		//first assert a syntactically correct parse
		EpQuery epQuery = assertParseSuccessfull("FIND CmUser WHERE Role='SUPERUSER'", queryParser);

		//then assert the JPQL was exactly as expected
		assertEquals("The returned JPQL was not as expected",
				"SELECT DISTINCT cm.guid FROM CmUserImpl cm LEFT JOIN cm.userRoles roles"
						+ "  where  (  (  (roles.name=?1) ) )  ORDER BY cm.guid ASC",
				epQuery.getNativeQuery().getNativeQuery());
	}
	

	/**
	 * Test if a epql WITHOUT a where clause parses ok and returns expected JPQL.
	 */
	@Test
	public void testSuccessfulParseWithOutWhere() {
		//first assert a syntactically correct parse
		EpQuery epQuery = assertParseSuccessfull("FIND CmUser", queryParser);

		//then assert the JPQL was exactly as expected (ignoring case) 
		assertEquals("The returned JPQL was not as expected", 
					 "SELECT DISTINCT cm.guid FROM CmUserImpl cm LEFT JOIN cm.userRoles roles     ORDER BY cm.guid ASC",
					 epQuery.getNativeQuery().getNativeQuery());
	}
	
	/**
	 * Should not parse it.
	 */
	@Test
	public void testInvalidParse() {
		assertParseInvalid("FIND CmUser WHERE Role == 'SUPERUSER'", "Should not parse it.", queryParser);
	}
	
}
