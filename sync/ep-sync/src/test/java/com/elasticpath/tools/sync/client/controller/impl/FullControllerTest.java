package com.elasticpath.tools.sync.client.controller.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.tools.sync.beanfactory.SyncBeanFactoryMutator;
import com.elasticpath.tools.sync.client.SyncToolConfiguration;
import com.elasticpath.tools.sync.exception.SyncToolConfigurationException;
import com.elasticpath.tools.sync.job.DomainSorter;
import com.elasticpath.tools.sync.job.SourceObjectCache;
import com.elasticpath.tools.sync.job.TransactionJob;
import com.elasticpath.tools.sync.job.TransactionJobBuilder;
import com.elasticpath.tools.sync.job.TransactionJobUnit;
import com.elasticpath.tools.sync.job.dao.TransactionJobDao;
import com.elasticpath.tools.sync.job.descriptor.JobDescriptor;
import com.elasticpath.tools.sync.job.descriptor.SourceSyncRequestAdapter;
import com.elasticpath.tools.sync.job.descriptor.TransactionJobDescriptorEntry;
import com.elasticpath.tools.sync.job.descriptor.dao.JobDescriptorDao;
import com.elasticpath.tools.sync.processing.SerializableObject;
import com.elasticpath.tools.sync.processing.SerializableObjectListener;
import com.elasticpath.tools.sync.target.result.Summary;

/**
 * Tests FullController.
 */
@SuppressWarnings({ "PMD.AvoidThrowingRawExceptionTypes" })
public class FullControllerTest {

	private static final String CHANGE_SET_ADAPTER = "changeSetAdapter";

	private static final String TRANSACTION_JOB_BUILDER = "cachingTransactionJobBuilder";

	private static final String ADAPTER_PARAM = "adapterParam";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

    private final TransactionJob mockTransactionJob = context.mock(TransactionJob.class);

    private final JobDescriptor mockJobDescriptor = context.mock(JobDescriptor.class);

    private final SyncBeanFactoryMutator mockSyncBeanFactory = context.mock(SyncBeanFactoryMutator.class);

    private final TransactionJobBuilder mockTransactionJobBuilder = context.mock(TransactionJobBuilder.class);

	private FullController controller;

	private SourceSyncRequestAdapter changeSetAdapter;

	private SyncToolConfiguration syncToolConfiguration;

	private JobDescriptorDao jobDescriptorDao;

	private TransactionJobDao transactionJobDao;

	/**
	 * Sets up a test case.
	 */
	@Before
	public void setUp() {
		syncToolConfiguration = context.mock(SyncToolConfiguration.class);
		jobDescriptorDao = context.mock(JobDescriptorDao.class);
		transactionJobDao = context.mock(TransactionJobDao.class);

		controller = new FullController() {
            @Override
			protected void initConfig(final SystemConfig sourceSystem, final SystemConfig targetSystem) {
				// nothing to do
			}
        	protected void destroyConfig(final SystemConfig sourceConfig, final SystemConfig targetConfig) {
        		// nothing to do
        	}
        };
		controller.setSyncBeanFactory(mockSyncBeanFactory);
		controller.setSyncToolConfiguration(syncToolConfiguration);
		controller.setJobDescriptorDao(jobDescriptorDao);
		controller.setTransactionJobDao(transactionJobDao);

		changeSetAdapter = context.mock(SourceSyncRequestAdapter.class);

	}
	
	/**
	 * Test that synchronize() is called once.
	 */
	@Test
    public void testSynchronize() {
        final TransactionJobUnit mockTransactionJobUnit = getMockTransactionJobUnit();
        final List<TransactionJobUnit> transactionJobUnits = new ArrayList<TransactionJobUnit>();
        transactionJobUnits.add(mockTransactionJobUnit);
        final DomainSorter domainSorter = context.mock(DomainSorter.class);
        controller.setDomainSorter(domainSorter);
        AbstractObjectEventDistributor objectEventDistributor =
             new AbstractObjectEventDistributor() {
                 private int callCount = 0;
                 private static final int MAX_CALL_COUNT = 1;

                 @Override
                 protected void handleException(final Exception exc, final Summary summary, final TransactionJobUnit jobUnit) {
                     // This method must never be invoked during a successful test run
                     throw new RuntimeException(exc);
                 }
                 @Override
                 public void processObject(final SerializableObject object) {
                	 // do nothing
                 }
                 @Override
                 public void finished() {
                    callCount++;
                    assertEquals("AbstractObjectEventDistributor.finished() must be called only once",
                            callCount, MAX_CALL_COUNT);
                 }
        };

		controller.setObjectEventDistributor(objectEventDistributor);

		final SourceObjectCache sourceObjectCache = context.mock(SourceObjectCache.class);
		context.checking(new Expectations() { {
			oneOf(mockSyncBeanFactory).getSourceBean(TRANSACTION_JOB_BUILDER); will(returnValue(mockTransactionJobBuilder));
			oneOf(mockSyncBeanFactory).getSourceBean(CHANGE_SET_ADAPTER); will(returnValue(changeSetAdapter));
			oneOf(mockSyncBeanFactory).getSourceBean("sourceObjectCache"); will(returnValue(sourceObjectCache));
			oneOf(sourceObjectCache).supportsPreloading(); will(returnValue(false));
			oneOf(syncToolConfiguration).getAdapterParameter(); will(returnValue(ADAPTER_PARAM));
			oneOf(changeSetAdapter).buildJobDescriptor(ADAPTER_PARAM); will(returnValue(mockJobDescriptor));
			oneOf(mockTransactionJob).getTransactionJobUnits(); will(returnValue(transactionJobUnits));
			oneOf(jobDescriptorDao).save(mockJobDescriptor);
			oneOf(transactionJobDao).save(mockTransactionJob);
			oneOf(mockTransactionJobBuilder).build(mockJobDescriptor, false); will(returnValue(mockTransactionJob));
			oneOf(mockTransactionJob).iterator(); will(returnValue(transactionJobUnits.iterator()));
            oneOf(domainSorter).sort(with(mockTransactionJobUnit.getJobDescriptorEntries()));
		} });

        controller.synchronize();
    }

	/**
	 * Tests loadTransactionJob.
	 */
	@Test
	public void testLoadTransactionJob() {
		final SerializableObjectListener objectListener = context.mock(SerializableObjectListener.class);
		
        final TransactionJobUnit mockTransactionJobUnit = getMockTransactionJobUnit();
        final List<TransactionJobUnit> transactionJobUnits = new ArrayList<TransactionJobUnit>();
        transactionJobUnits.add(mockTransactionJobUnit);
        final DomainSorter domainSorter = context.mock(DomainSorter.class);
        controller.setDomainSorter(domainSorter);
		
		final SourceObjectCache sourceObjectCache = context.mock(SourceObjectCache.class);
		context.checking(new Expectations() { {
			oneOf(mockSyncBeanFactory).getSourceBean(TRANSACTION_JOB_BUILDER); will(returnValue(mockTransactionJobBuilder));
			oneOf(mockSyncBeanFactory).getSourceBean(CHANGE_SET_ADAPTER); will(returnValue(changeSetAdapter));

			oneOf(mockSyncBeanFactory).getSourceBean("sourceObjectCache"); will(returnValue(sourceObjectCache));
			oneOf(sourceObjectCache).supportsPreloading(); will(returnValue(false));
			oneOf(syncToolConfiguration).getAdapterParameter(); will(returnValue(ADAPTER_PARAM));
			oneOf(changeSetAdapter).buildJobDescriptor(ADAPTER_PARAM); will(returnValue(mockJobDescriptor));
			oneOf(mockTransactionJob).getTransactionJobUnits(); will(returnValue(transactionJobUnits));
			oneOf(jobDescriptorDao).save(mockJobDescriptor);
			oneOf(transactionJobDao).save(mockTransactionJob);
			oneOf(mockTransactionJobBuilder).build(mockJobDescriptor, false); will(returnValue(mockTransactionJob));
			oneOf(mockTransactionJob).iterator(); will(returnValue(transactionJobUnits.iterator()));
			exactly(transactionJobUnits.size()).of(objectListener).processObject(with(any(SerializableObject.class)));
            oneOf(domainSorter).sort(with(mockTransactionJobUnit.getJobDescriptorEntries()));
		} });

		controller.loadTransactionJob(objectListener);
	}

	/**
	 * @return Get the MockTransactionJobUnit.
	 */
    protected TransactionJobUnit getMockTransactionJobUnit() {
        final TransactionJobUnit mockJobUnit = context.mock(TransactionJobUnit.class);
        final List<TransactionJobDescriptorEntry> mockJobEntries =
                new ArrayList<TransactionJobDescriptorEntry>();
        mockJobEntries.add(context.mock(TransactionJobDescriptorEntry.class));

        context.checking(new Expectations() { {
            atLeast(1).of(mockJobUnit).getJobDescriptorEntries();
            will(returnValue(mockJobEntries));
        } });
        return mockJobUnit;
    }
	
	/**
	 * Tests buildJobDescriptor.
	 */
	@Test
	public void testBuildJobDescriptor() {
		context.checking(new Expectations() { {
			oneOf(syncToolConfiguration).getAdapterParameter(); will(returnValue(ADAPTER_PARAM));
			oneOf(mockSyncBeanFactory).getSourceBean(CHANGE_SET_ADAPTER); will(returnValue(changeSetAdapter));
			oneOf(changeSetAdapter).buildJobDescriptor(ADAPTER_PARAM); will(returnValue(mockJobDescriptor));
		} });

		assertSame(mockJobDescriptor, controller.buildJobDescriptor());

	}

	/**
	 * Tests getSourceSyncRequestAdapter.
	 */
	@Test(expected = SyncToolConfigurationException.class)
	public void testGetSourceSyncRequestAdapterFail() {
		context.checking(new Expectations() { {
			oneOf(mockSyncBeanFactory).getSourceBean(CHANGE_SET_ADAPTER); will(returnValue(null));
		} });

		controller.getSourceSyncRequestAdapter();
	}

	/**
	 * Tests buildTransactionJob throwing exception.
	 */
	@Test(expected = SyncToolConfigurationException.class)
	public void testBuildTransactionJobFail() {

		controller.buildTransactionJob(null);
	}

	/**
	 * Tests buildTransactionJob.
	 */
	@Test
	public void testBuildTransactionJob() {
		context.checking(new Expectations() { {
			oneOf(mockSyncBeanFactory).getSourceBean(TRANSACTION_JOB_BUILDER); will(returnValue(mockTransactionJobBuilder));
			oneOf(mockTransactionJobBuilder).build(mockJobDescriptor, false); will(returnValue(mockTransactionJob));
		} });

		assertSame(mockTransactionJob, controller.buildTransactionJob(mockJobDescriptor));
	}

}
