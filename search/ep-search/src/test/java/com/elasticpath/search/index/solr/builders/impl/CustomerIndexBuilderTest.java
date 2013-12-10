package com.elasticpath.search.index.solr.builders.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.Date;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.solr.common.SolrInputDocument;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerAddress;
import com.elasticpath.domain.customer.impl.CustomerAddressImpl;
import com.elasticpath.search.index.pipeline.stats.impl.PipelinePerformanceImpl;
import com.elasticpath.search.index.solr.document.impl.CustomerSolrInputDocumentCreator;
import com.elasticpath.service.customer.CustomerService;
import com.elasticpath.service.search.solr.AnalyzerImpl;
import com.elasticpath.service.search.solr.SolrIndexConstants;

/**
 * Test <code>CustomerIndexBuilder</code>.
 */
public class CustomerIndexBuilderTest {

	private CustomerSolrInputDocumentCreator customerDocumentBuilder;

	private CustomerIndexBuilder customerIndexBuilder;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private CustomerService mockCustomerService;

	private AnalyzerImpl analyzer;

	/**
	 * Setup test.
	 *
	 * @throws Exception on error
	 */
	@Before
	public void setUp() throws Exception {

		this.customerIndexBuilder = new CustomerIndexBuilder();
		this.analyzer = new AnalyzerImpl();

		this.mockCustomerService = context.mock(CustomerService.class);
		this.customerIndexBuilder.setCustomerService(mockCustomerService);

		customerDocumentBuilder = new CustomerSolrInputDocumentCreator();
		customerDocumentBuilder.setAnalyzer(analyzer);
		customerDocumentBuilder.setPipelinePerformance(new PipelinePerformanceImpl());
	}

	/**
	 * Test method for 'com.elasticpath.service.index.impl.CustomerIndexBuildServiceImpl.getName()'.
	 */
	@Test
	public void testGetName() {
		assertNotNull(this.customerIndexBuilder.getName());
	}

	/**
	 * Test method for 'com.elasticpath.service.index.impl.CustomerIndexBuildServiceImpl.findDeletedUids(Date)'.
	 */
	@Test
	public void testFindDeletedUids() {
		final ArrayList<Long> uidList = new ArrayList<Long>();
		context.checking(new Expectations() {
			{
				oneOf(mockCustomerService).findUidsByDeletedDate(with(any(Date.class)));
				will(returnValue(uidList));
			}
		});
		assertSame(uidList, this.customerIndexBuilder.findDeletedUids(new Date()));
	}

	/**
	 * Test method for 'com.elasticpath.service.index.impl.CustomerIndexBuildServiceImpl.findAddedOrModifiedUids(Date)'.
	 */
	@Test
	public void testFindAddedOrModifiedUids() {
		final ArrayList<Long> uidList = new ArrayList<Long>();
		context.checking(new Expectations() {
			{
				oneOf(mockCustomerService).findUidsByModifiedDate(with(any(Date.class)));
				will(returnValue(uidList));
			}
		});
		assertSame(uidList, this.customerIndexBuilder.findAddedOrModifiedUids(new Date()));
	}

	/**
	 * Test method for 'com.elasticpath.service.index.impl.CustomerIndexBuildServiceImpl.findAllUids()'.
	 */
	@Test
	public void testFindAllUids() {
		final ArrayList<Long> uidList = new ArrayList<Long>();
		context.checking(new Expectations() {
			{
				oneOf(mockCustomerService).findUidsByModifiedDate(with(any(Date.class)));
				will(returnValue(uidList));
			}
		});
		assertSame(uidList, this.customerIndexBuilder.findAddedOrModifiedUids(new Date()));
	}

	/**
	 * Test that when the customer with the given UID has no userId, createDocument returns null.
	 */
	@Test
	public void testCreateDocumentWhenCustomerHasNoUserIdReturnsNull() {
		final Customer mockCustomer = context.mock(Customer.class);
		context.checking(new Expectations() {
			{
				allowing(mockCustomer).getUserId();
				will(returnValue(null));
			}
		});
		customerDocumentBuilder.setEntity(mockCustomer);
		assertNull(customerDocumentBuilder.createDocument());
	}

	/**
	 * Test that createDocument asks the persistence layer for the customer with the given UID, and upon retrieving it will create a document based
	 * on that customer. Also checks that if the customer profile values are null the document is still created.
	 */
	@Test
	public void testCreateDocumentWithNulls() {
		final long uidPk = 2343L;
		final String storeCode = "dsfsdf";
		final Date date = new Date();
		final String userId = "some id";

		final Customer mockCustomer = context.mock(Customer.class);
		context.checking(new Expectations() {
			{
				allowing(mockCustomer).getUidPk();
				will(returnValue(uidPk));

				allowing(mockCustomer).getStoreCode();
				will(returnValue(storeCode));
				allowing(mockCustomer).getAddresses();
				will(returnValue(new ArrayList<CustomerAddress>()));
				allowing(mockCustomer).getCreationDate();
				will(returnValue(date));
				allowing(mockCustomer).getUserId();
				will(returnValue(userId));

				allowing(mockCustomer).getFirstName();
				will(returnValue(null));
				allowing(mockCustomer).getLastName();
				will(returnValue(null));
				allowing(mockCustomer).getEmail();
				will(returnValue(null));
				allowing(mockCustomer).getPhoneNumber();
				will(returnValue(null));
				allowing(mockCustomer).getPreferredBillingAddress();
				will(returnValue(null));
			}
		});

		customerDocumentBuilder.setEntity(mockCustomer);

		final SolrInputDocument document = customerDocumentBuilder.createDocument();
		assertNotNull(document);

		assertEquals(Long.toString(mockCustomer.getUidPk()), document.getFieldValue(SolrIndexConstants.OBJECT_UID));
		assertEquals(userId, document.getFieldValue(SolrIndexConstants.USER_ID));
		assertEquals(null, document.getFieldValue(SolrIndexConstants.FIRST_NAME));
		assertEquals(null, document.getFieldValue(SolrIndexConstants.LAST_NAME));
		assertEquals(null, document.getFieldValue(SolrIndexConstants.EMAIL));
		assertEquals(null, document.getFieldValue(SolrIndexConstants.PHONE_NUMBER));
		assertEquals(analyzer.analyze(date), document.getFieldValue(SolrIndexConstants.CREATE_TIME));
		assertEquals(analyzer.analyze(storeCode), document.getFieldValue(SolrIndexConstants.STORE_CODE));
		assertNull(document.getFieldValue(SolrIndexConstants.ZIP_POSTAL_CODE));
	}

	/**
	 * Test method for 'com.elasticpath.service.index.impl.CustomerIndexBuildServiceImpl.createDocument(long, Locale)'.
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

		final Customer mockCustomer = context.mock(Customer.class);
		context.checking(new Expectations() {
			{
				allowing(mockCustomer).getUidPk();
				will(returnValue(uidPk));
				allowing(mockCustomer).getFirstName();
				will(returnValue(firstName));
				allowing(mockCustomer).getLastName();
				will(returnValue(lastName));
				allowing(mockCustomer).getEmail();
				will(returnValue(email));
				allowing(mockCustomer).getUserId();
				will(returnValue(userId));
				allowing(mockCustomer).getPhoneNumber();
				will(returnValue(phoneNumber));
				allowing(mockCustomer).getCreationDate();
				will(returnValue(createDate));
				allowing(mockCustomer).getAddresses();
				will(returnValue(new ArrayList<CustomerAddress>()));
				allowing(mockCustomer).getPreferredBillingAddress();
				will(returnValue(new CustomerAddressImpl()));

				allowing(mockCustomer).getStoreCode();
				will(returnValue(storeCode));
			}
		});

		customerDocumentBuilder.setEntity(mockCustomer);

		final SolrInputDocument document = customerDocumentBuilder.createDocument();
		assertNotNull(document);

		assertEquals(Long.toString(uidPk), document.getFieldValue(SolrIndexConstants.OBJECT_UID));
		assertEquals(analyzer.analyze(userId), document.getFieldValue(SolrIndexConstants.USER_ID));
		assertEquals(analyzer.analyze(firstName), document.getFieldValue(SolrIndexConstants.FIRST_NAME));
		assertEquals(analyzer.analyze(lastName), document.getFieldValue(SolrIndexConstants.LAST_NAME));
		assertEquals(analyzer.analyze(email), document.getFieldValue(SolrIndexConstants.EMAIL));
		assertEquals(analyzer.analyze(phoneNumber), document.getFieldValue(SolrIndexConstants.PHONE_NUMBER));
		assertEquals(analyzer.analyze(createDate), document.getFieldValue(SolrIndexConstants.CREATE_TIME));
	}
}
