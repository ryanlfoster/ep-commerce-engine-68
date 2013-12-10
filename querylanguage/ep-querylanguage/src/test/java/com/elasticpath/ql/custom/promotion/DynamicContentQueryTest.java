package com.elasticpath.ql.custom.promotion;

import static com.elasticpath.ql.asserts.ParseAssert.assertParseInvalid;
import static com.elasticpath.ql.asserts.ParseAssert.assertParseSuccessfull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.ql.parser.AbstractEpQLCustomConfiguration;
import com.elasticpath.ql.parser.EPQueryType;
import com.elasticpath.ql.parser.EpQueryAssembler;
import com.elasticpath.ql.parser.EpQueryParser;
import com.elasticpath.ql.parser.gen.EpQueryParserImpl;
import com.elasticpath.ql.parser.querybuilder.impl.JPQLQueryBuilder;
import com.elasticpath.ql.parser.valueresolver.impl.JPQLValueResolver;

/**
 * Tests price list assignment EPQL queries.
 */
public class DynamicContentQueryTest {

	private EpQueryParser queryParser;
	
	/**
	 * Set up.
	 */
	@Before
	public void setUp() {
		queryParser = new EpQueryParserImpl();

		EpQueryAssembler epQueryAssembler = new EpQueryAssembler();

		Map<EPQueryType, AbstractEpQLCustomConfiguration> epQLObjectConfiruration = new HashMap<EPQueryType, AbstractEpQLCustomConfiguration>();

		epQueryAssembler.setEpQLObjectConfiguration(epQLObjectConfiruration);
		
		DynamicContentConfiguration dcConf = new DynamicContentConfiguration();
		dcConf.setEpQLValueResolver(new JPQLValueResolver());
		dcConf.setCompleteQueryBuilder(new JPQLQueryBuilder());
		dcConf.initialize();
		epQLObjectConfiruration.put(EPQueryType.DYNAMICCONTENT, dcConf);
		
		queryParser.setEpQueryAssembler(epQueryAssembler);

	}
	
	/**
	 * Tests price list assignment query parsing with condition.
	 */
	@Test
	public void testParsePriceListAssignment() {
		assertParseSuccessfull("FIND DynamicContent", queryParser);
	}
	
	/**
	 * Tests price list assignment query parsing with condition.
	 */
	@Test
	public void testParsePriceListAssignmentInvalid() {
		assertParseInvalid("FIND DynamicContents", "Wrong query", queryParser);
	}
}
