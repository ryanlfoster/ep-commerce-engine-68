package com.elasticpath.search.index.solr.document.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Date;

import org.apache.solr.common.SolrInputDocument;
import org.jmock.Expectations;
import org.jmock.api.Invocation;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.lib.action.CustomAction;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerAddress;
import com.elasticpath.domain.customer.impl.CustomerImpl;
import com.elasticpath.search.index.pipeline.IndexingStage;
import com.elasticpath.search.index.pipeline.stats.impl.PipelinePerformanceImpl;
import com.elasticpath.service.search.index.Analyzer;
import com.elasticpath.service.search.solr.AnalyzerImpl;
import com.elasticpath.service.search.solr.SolrIndexConstants;

/**
 * Test construction of {@link SolrInputDocument}s from {@link Customer}s using {@link CustomerSolrInputDocumentCreator}.
 */
public class CustomerSolrInputDocumentCreatorTest {
	private Analyzer analyzer;

	private CustomerSolrInputDocumentCreator customerSolrInputDocumentCreator;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	/**
	 * Set up {@link CustomerSolrInputDocumentCreator}.
	 */
	@Before
	public void setUp() {
		analyzer = new AnalyzerImpl();
		customerSolrInputDocumentCreator = new CustomerSolrInputDocumentCreator();
		customerSolrInputDocumentCreator.setAnalyzer(analyzer);
		customerSolrInputDocumentCreator.setPipelinePerformance(new PipelinePerformanceImpl());
	}

	/**
	 * Test that when the customer has no userId, createDocument returns null.
	 */
	@Test
	public void testCreateDocumentWhenCustomerHasNoUserIdReturnsNull() {
		final Customer customer = new CustomerImpl();
		customerSolrInputDocumentCreator.setEntity(customer);
		assertNull(customerSolrInputDocumentCreator.createDocument());
	}

	/**
	 * If you pass a null into the document creator, it should give a null back (and not throw a exception).
	 */
	@Test
	public void testNullYieldsNull() {
		customerSolrInputDocumentCreator.setEntity(null);
		assertNull(customerSolrInputDocumentCreator.createDocument());
	}

	/**
	 * Test that createDocument creates a document based on that customer. Also checks that if the customer profile values are null the document is
	 * still created.
	 */
	@Test
	public void testCreateDocumentWithNulls() {
		final long uidPk = 2343L;
		final String storeCode = "dsfsdf";
		final Date date = new Date();
		final String userId = "some id";

		final Customer customer = context.mock(Customer.class);

		final int expectedCallsToGetUserId = 3;

		@SuppressWarnings("unchecked")
		final IndexingStage<SolrInputDocument, Long> nextStage = context.mock(IndexingStage.class);

		customerSolrInputDocumentCreator.setNextStage(nextStage);

		context.checking(new Expectations() {
			{

				oneOf(nextStage).send(with(any(SolrInputDocument.class)));
				will(new CustomAction("capture SolrInputDocument") {

					public Object invoke(final Invocation invocation) throws Throwable {
						final SolrInputDocument document = (SolrInputDocument) invocation.getParameter(0);
						assertNotNull(document);
						assertNotNull(document);

						assertEquals(Long.toString(customer.getUidPk()), document.getFieldValue(SolrIndexConstants.OBJECT_UID));
						assertEquals(userId, document.getFieldValue(SolrIndexConstants.USER_ID));
						assertEquals(null, document.getFieldValue(SolrIndexConstants.FIRST_NAME));
						assertEquals(null, document.getFieldValue(SolrIndexConstants.LAST_NAME));
						assertEquals(null, document.getFieldValue(SolrIndexConstants.EMAIL));
						assertEquals(null, document.getFieldValue(SolrIndexConstants.PHONE_NUMBER));
						assertEquals(analyzer.analyze(date), document.getFieldValue(SolrIndexConstants.CREATE_TIME));
						assertEquals(analyzer.analyze(storeCode), document.getFieldValue(SolrIndexConstants.STORE_CODE));
						assertNull(document.getFieldValue(SolrIndexConstants.ZIP_POSTAL_CODE));
						return null;
					}
				});

				allowing(customer).getStoreCode();
				will(returnValue(storeCode));
				exactly(2).of(customer).getUidPk();
				will(returnValue(uidPk));
				oneOf(customer).getAddresses();
				will(returnValue(new ArrayList<CustomerAddress>()));
				oneOf(customer).getCreationDate();
				will(returnValue(date));
				exactly(expectedCallsToGetUserId).of(customer).getUserId();
				will(returnValue(userId));
				oneOf(customer).getFirstName();
				oneOf(customer).getLastName();
				oneOf(customer).getEmail();
				oneOf(customer).getPhoneNumber();
				oneOf(customer).getPreferredBillingAddress();
			}
		});

		customerSolrInputDocumentCreator.setEntity(customer);
		customerSolrInputDocumentCreator.run();

	}

	/**
	 * Test document creation with all fields not null.
	 */
	@Test
	public void testCreateDocument() {
		final long uidPk = 234234;
		final String firstName = "Jimijimi";
		final String lastName = "LaibaLaiba";
		final String email = "jimi.laiba@elasticpath.com";
		final String userId = "jimi.laiba@elasticpath.com";
		final String phoneNumber = "012-345-6789";
		final Date createDate = new Date();
		final String storeCode = "sdfsdf";

		final Customer customer = context.mock(Customer.class);

		final int expectedCallsToGetUserId = 3;

		@SuppressWarnings("unchecked")
		final IndexingStage<SolrInputDocument, Long> nextStage = context.mock(IndexingStage.class);

		customerSolrInputDocumentCreator.setNextStage(nextStage);

		context.checking(new Expectations() {
			{
				oneOf(nextStage).send(with(any(SolrInputDocument.class)));
				will(new CustomAction("capture SolrInputDocument") {

					public Object invoke(final Invocation invocation) throws Throwable {
						final SolrInputDocument document = (SolrInputDocument) invocation.getParameter(0);
						assertNotNull(document);
						assertEquals(Long.toString(uidPk), document.getFieldValue(SolrIndexConstants.OBJECT_UID));
						assertEquals(analyzer.analyze(userId), document.getFieldValue(SolrIndexConstants.USER_ID));
						assertEquals(analyzer.analyze(firstName), document.getFieldValue(SolrIndexConstants.FIRST_NAME));
						assertEquals(analyzer.analyze(lastName), document.getFieldValue(SolrIndexConstants.LAST_NAME));
						assertEquals(analyzer.analyze(email), document.getFieldValue(SolrIndexConstants.EMAIL));
						assertEquals(analyzer.analyze(phoneNumber), document.getFieldValue(SolrIndexConstants.PHONE_NUMBER));
						assertEquals(analyzer.analyze(createDate), document.getFieldValue(SolrIndexConstants.CREATE_TIME));
						return null;
					}
				});

				allowing(customer).getStoreCode();
				will(returnValue(storeCode));
				oneOf(customer).getUidPk();
				will(returnValue(uidPk));
				oneOf(customer).getAddresses();
				will(returnValue(new ArrayList<CustomerAddress>()));
				oneOf(customer).getCreationDate();
				will(returnValue(createDate));
				exactly(expectedCallsToGetUserId).of(customer).getUserId();
				will(returnValue(userId));
				oneOf(customer).getFirstName();
				will(returnValue(firstName));
				oneOf(customer).getLastName();
				will(returnValue(lastName));
				oneOf(customer).getEmail();
				will(returnValue(email));
				oneOf(customer).getPhoneNumber();
				will(returnValue(phoneNumber));
				oneOf(customer).getPreferredBillingAddress();
			}
		});

		customerSolrInputDocumentCreator.setEntity(customer);
		customerSolrInputDocumentCreator.run();

	}

}
