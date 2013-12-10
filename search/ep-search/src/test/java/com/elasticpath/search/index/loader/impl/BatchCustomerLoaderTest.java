package com.elasticpath.search.index.loader.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.search.index.pipeline.IndexingStage;
import com.elasticpath.search.index.pipeline.stats.PipelinePerformance;
import com.elasticpath.search.index.pipeline.stats.impl.PipelinePerformanceImpl;
import com.elasticpath.service.customer.CustomerService;

/**
 * Test {@link BatchCustomerLoader}.
 */
public class BatchCustomerLoaderTest {
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	
	private final CustomerService customerService = context.mock(CustomerService.class);
	
	@SuppressWarnings("unchecked")
	private final IndexingStage<Customer, ?> nextStage = context.mock(IndexingStage.class);
	
	private final PipelinePerformance pipelinePerformance = new PipelinePerformanceImpl();
	
	private BatchCustomerLoader batchCustomerLoader;
	
	/**
	 * Initialize the services on the {@link BatchCustomerLoader}.
	 */
	@Before
	public void setUpBatchCustomerLoader() {
		batchCustomerLoader = new BatchCustomerLoader();
		batchCustomerLoader.setCustomerService(customerService);
		batchCustomerLoader.setPipelinePerformance(pipelinePerformance);
	}
	
	/**
	 * Test that loading a set of customers works.
	 */
	@Test
	public void testLoadingValidList() {
		
		final List<Customer> customers = new ArrayList<Customer>();
		final Customer firstCustomer = context.mock(Customer.class, "first");
		final Customer secondCustomer = context.mock(Customer.class, "second");
		customers.add(firstCustomer);
		customers.add(secondCustomer);

		final Set<Long> uidsToLoad = createUidsToLoad();

		context.checking(new Expectations() { {
			allowing(customerService).findByUids(with(uidsToLoad));
			will(returnValue(customers));

			oneOf(nextStage).send(with(firstCustomer));
			oneOf(nextStage).send(with(secondCustomer));
		} });

		batchCustomerLoader.setBatch(uidsToLoad);
		batchCustomerLoader.setNextStage(nextStage);
		batchCustomerLoader.run();
	}

	/**
	 * Test sending an empty set of {@link Customer} uids to load returns an empty list.
	 */
	@Test
	public void testLoadingNoCustomers() {
		batchCustomerLoader.setBatch(new HashSet<Long>());
		batchCustomerLoader.setNextStage(nextStage);
		batchCustomerLoader.run();
	}
	
	/**
	 * Test that trying to load a set of {@link Customer}s without setting the next stage fails.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testLoadingInvalidNextStage() {
		batchCustomerLoader.setBatch(createUidsToLoad());
		batchCustomerLoader.run();
	}

	private Set<Long> createUidsToLoad() {
		final Set<Long> uidsToLoad = new HashSet<Long>();
		uidsToLoad.add(Long.valueOf(1));
		return uidsToLoad;
	}
}
