package com.elasticpath.service.search.solr.query;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.lucene.search.Query;

import com.elasticpath.domain.misc.SearchConfig;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.search.index.QueryComposer;
import com.elasticpath.service.search.query.CustomerSearchCriteria;
import com.elasticpath.service.search.query.OrderReturnSearchCriteria;
import com.elasticpath.service.search.query.SearchCriteria;
import com.elasticpath.service.search.solr.AnalyzerImpl;
import com.elasticpath.service.search.solr.SolrIndexConstants;

/**
 * Test <code>OrderReturnQueryComposerImpl</code>.
 */
public class OrderReturnQueryComposerImplTest extends QueryComposerTestCase {

	private static final String WHITESPACE_REGEX = "\\s";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private OrderReturnQueryComposerImpl orderReturnQueryComposer;
	
	/**
	 * Prepare for tests.
	 * 
	 * @throws Exception -- in case of any errors.
	 */
	@Before
	public void setUp() throws Exception {

		orderReturnQueryComposer = new OrderReturnQueryComposerImpl();
		orderReturnQueryComposer.setAnalyzer(new AnalyzerImpl());
	}

	/**
	 * Test method for 'com.elasticpath.service.index.impl.OrderQueryComposerImpl.composeQuery(SearchCriteria)'.
	 */
	@Test
	public void testSearch() {
		final OrderReturnSearchCriteria orderReturnSearchCriteria = new OrderReturnSearchCriteria();
		final SearchConfig mockSearchConfig = context.mock(SearchConfig.class);
		final SearchConfig searchConfig = mockSearchConfig;
		context.checking(new Expectations() {
			{
				atLeast(1).of(mockSearchConfig).getBoostValue("orderNumber");
				will(returnValue(SearchConfig.BOOST_DEFAULT));

				atLeast(1).of(mockSearchConfig).getBoostValue("firstName");
				will(returnValue(SearchConfig.BOOST_DEFAULT));

				atLeast(1).of(mockSearchConfig).getBoostValue("lastName");
				will(returnValue(SearchConfig.BOOST_DEFAULT));

				atLeast(1).of(mockSearchConfig).getMinimumSimilarity();
				will(returnValue(SearchConfig.MINIMUM_SIMILARITY_DEFAULT));

				atLeast(1).of(mockSearchConfig).getPrefixLength();
				will(returnValue(SearchConfig.PREFIX_LENGTH_DEFAULT));
			}
		});

		try {
			orderReturnQueryComposer.composeQuery(orderReturnSearchCriteria, searchConfig);
			fail("EpServiceException expected for no parameters in search criteria.");
		} catch (EpServiceException e) {
			assertNotNull(e);
		}
		
		// testing query values
		final String orderNumber = "order number";
		Query query;

		orderReturnSearchCriteria.setOrderNumber(orderNumber);
		query = orderReturnQueryComposer.composeQuery(orderReturnSearchCriteria, searchConfig);
		assertQueryContains(query, SolrIndexConstants.ORDER_NUMBER, orderNumber);
		query = orderReturnQueryComposer.composeFuzzyQuery(orderReturnSearchCriteria, searchConfig);
		assertQueryContains(query, SolrIndexConstants.ORDER_NUMBER, orderNumber);
		

		testForCustomerSearchCriteria(orderReturnSearchCriteria, searchConfig);
	}

	private void testForCustomerSearchCriteria(final OrderReturnSearchCriteria orderReturnSearchCriteria, final SearchConfig searchConfig) {
		Query query;
		final CustomerSearchCriteria customerSearchCriteria = new CustomerSearchCriteria();
		orderReturnSearchCriteria.setCustomerSearchCriteria(customerSearchCriteria);
		
		final String firstName = "first name";
		final String lastName = "last name";

		customerSearchCriteria.setFirstName(firstName);
		query = orderReturnQueryComposer.composeQuery(orderReturnSearchCriteria, searchConfig);
		assertQueryContains(query, SolrIndexConstants.FIRST_NAME, firstName.split(WHITESPACE_REGEX));
		query = orderReturnQueryComposer.composeFuzzyQuery(orderReturnSearchCriteria, searchConfig);
		assertQueryContainsFuzzy(query, SolrIndexConstants.FIRST_NAME, firstName.split(WHITESPACE_REGEX));

		customerSearchCriteria.setLastName(lastName);
		query = orderReturnQueryComposer.composeQuery(orderReturnSearchCriteria, searchConfig);
		assertQueryContains(query, SolrIndexConstants.LAST_NAME, lastName.split(WHITESPACE_REGEX));
		query = orderReturnQueryComposer.composeFuzzyQuery(orderReturnSearchCriteria, searchConfig);
		assertQueryContainsFuzzy(query, SolrIndexConstants.LAST_NAME, lastName.split(WHITESPACE_REGEX));
		
	}

	@Override
	protected QueryComposer getComposerUnderTest() {
		return orderReturnQueryComposer;
	}

	@Override
	protected SearchCriteria getCriteriaUnderTest() {
		return new OrderReturnSearchCriteria();
	}
}
