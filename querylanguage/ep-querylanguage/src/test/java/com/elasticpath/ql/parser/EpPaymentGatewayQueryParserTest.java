package com.elasticpath.ql.parser;

import static com.elasticpath.ql.asserts.ParseAssert.assertParseInvalid;
import static com.elasticpath.ql.asserts.ParseAssert.assertParseSuccessfull;
import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.domain.payment.impl.PaymentGatewayImpl;
import com.elasticpath.ql.custom.store.PaymentGatewayConfiguration;
import com.elasticpath.ql.parser.gen.EpQueryParserImpl;
import com.elasticpath.ql.parser.querybuilder.impl.JPQLQueryBuilder;
import com.elasticpath.ql.parser.valueresolver.impl.JPQLValueResolver;

/**
 * Test cases for JavaCC based implementation of EpQueryParser.
 */
public class EpPaymentGatewayQueryParserTest { // NOPMD

	private EpQueryParser queryParser;

	/**
	 * Setup test.
	 */
	@Before
	public void setUp() {
		PaymentGatewayConfiguration pgConf = new PaymentGatewayConfiguration();
		pgConf.setCompleteQueryBuilder(new JPQLQueryBuilder());
		pgConf.setEpQLValueResolver(new JPQLValueResolver());
		pgConf.initialize();
		
		Map<EPQueryType, AbstractEpQLCustomConfiguration> epQLObjectConfiguration = new HashMap<EPQueryType, AbstractEpQLCustomConfiguration>();
		epQLObjectConfiguration.put(EPQueryType.PAYMENTGATEWAY, pgConf);
		
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
		assertParseSuccessfull("FIND PaymentGateway", queryParser);
	}
	
	/**
	 * Should not parse it.
	 */
	@Test
	public void testInvalidParse() {
		assertParseInvalid("FIND PaymentGateway WHERE foo = 'hello'", "Should not parse it.", queryParser);
	}
	
	/**
	 * Tests that jpql query generated as expected.
	 * @throws EpQLParseException parse exception
	 */
	@Test
	public void testJPQLQuery() throws EpQLParseException {
		String nativeQuery = queryParser.parse("FIND PaymentGateway").getNativeQuery().getNativeQuery();
		assertEquals(String.format("SELECT pg.name FROM %s pg     ORDER BY pg.name ASC", PaymentGatewayImpl.class.getName()), nativeQuery);
	}
}
