package com.elasticpath.ql.custom.pricelist;

import static com.elasticpath.ql.asserts.ParseAssert.assertParseInvalid;
import static com.elasticpath.ql.asserts.ParseAssert.assertParseSuccessfull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.ql.parser.AbstractEpQLCustomConfiguration;
import com.elasticpath.ql.parser.EPQueryType;
import com.elasticpath.ql.parser.EpQLField;
import com.elasticpath.ql.parser.EpQueryAssembler;
import com.elasticpath.ql.parser.EpQueryParser;
import com.elasticpath.ql.parser.fieldresolver.impl.NonLocalizedFieldResolver;
import com.elasticpath.ql.parser.gen.EpQueryParserImpl;
import com.elasticpath.ql.parser.querybuilder.impl.JPQLQueryBuilder;
import com.elasticpath.ql.parser.querybuilder.impl.JPQLSubQueryBuilder;
import com.elasticpath.ql.parser.valueresolver.impl.JPQLValueResolver;

/**
 * Tests price list assignment EPQL queries.
 */
public class PriceListAssignmentQueryTest {

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
		
		PriceListAssignmentConfiguration plaConf = new PriceListAssignmentConfiguration();
		plaConf.setEpQLValueResolver(new JPQLValueResolver());
		plaConf.setCompleteQueryBuilder(new JPQLQueryBuilder());
		plaConf.setNonLocalizedFieldResolver(new NonLocalizedFieldResolver());
		plaConf.setSubQueryBuilder(new JPQLSubQueryBuilder());
		plaConf.initialize();
		epQLObjectConfiruration.put(EPQueryType.PRICELISTASSIGNMENT, plaConf);
		
		queryParser.setEpQueryAssembler(epQueryAssembler);

	}
	
	/**
	 * Tests price list assignment query parsing with condition.
	 */
	@Test
	public void testParsePriceListAssignment() {
		assertParseSuccessfull("FIND " + EPQueryType.PRICELISTASSIGNMENT.getTypeName(), queryParser);
	}
	
	/**
	 * Should parse it.
	 */
	@Test
	public void testWhereCatalogCodeParse() {
		assertParseSuccessfull("FIND " + EPQueryType.PRICELISTASSIGNMENT.getTypeName()
				+ " WHERE " + EpQLField.CATALOG_CODE.getFieldName() + " = 'hello'", queryParser);
	}
	
	/**
	 * Tests price list assignment query parsing with condition.
	 */
	@Test
	public void testParsePriceListAssignmentInvalid() {
		assertParseInvalid("FIND " + EPQueryType.PRICELISTASSIGNMENT.getTypeName() + "s", "Wrong query", queryParser);
	}
}
