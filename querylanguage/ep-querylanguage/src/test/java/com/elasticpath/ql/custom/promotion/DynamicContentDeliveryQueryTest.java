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
public class DynamicContentDeliveryQueryTest {

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
		
		DynamicContentDeliveryConfiguration dcdConf = new DynamicContentDeliveryConfiguration();
		dcdConf.setEpQLValueResolver(new JPQLValueResolver());
		dcdConf.setCompleteQueryBuilder(new JPQLQueryBuilder());
		dcdConf.initialize();
		epQLObjectConfiruration.put(EPQueryType.DYNAMICCONTENTDELIVERY, dcdConf);
		
		queryParser.setEpQueryAssembler(epQueryAssembler);

	}
	
	/**
	 * Tests price list assignment query parsing with condition.
	 */
	@Test
	public void testParsePriceListAssignment() {
		assertParseSuccessfull("FIND DynamicContentDelivery", queryParser);
	}
	
	/**
	 * Tests price list assignment query parsing with condition.
	 */
	@Test
	public void testParsePriceListAssignmentInvalid() {
		assertParseInvalid("FIND DynamicContentDeliverys", "Wrong query", queryParser);
	}
}
