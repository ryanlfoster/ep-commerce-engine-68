package com.elasticpath.tools.sync.client.controller.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.elasticpath.tools.sync.client.controller.exception.impl.DefaultExceptionHandler;
import org.junit.Assert;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.changeset.ChangeSet;
import com.elasticpath.tools.sync.beanfactory.SyncBeanFactoryMutator;
import com.elasticpath.tools.sync.client.controller.exception.ExceptionHandler;
import com.elasticpath.tools.sync.client.controller.exception.impl.ChangeSetExceptionHandlerImpl;
import com.elasticpath.tools.sync.exception.ChangeSetNotFoundException;
import com.elasticpath.tools.sync.job.TransactionJob;
import com.elasticpath.tools.sync.job.TransactionJobUnit;
import com.elasticpath.tools.sync.processing.SerializableObject;
import com.elasticpath.tools.sync.processing.SerializableObjectListener;
import com.elasticpath.tools.sync.processing.SyncJobObjectProcessor;
import com.elasticpath.tools.sync.target.SyncServiceTransactionRollBackException;
import com.elasticpath.tools.sync.target.result.Summary;
import com.elasticpath.tools.sync.target.result.SyncErrorResultItem;

/**
 * Tests AbstractSyncController.
 */
public class AbstractSyncControllerTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

    private final TransactionJob mockTransactionJob = context.mock(TransactionJob.class);
	
    private final TransactionJobUnit mockTransactionJobUnit = context.mock(TransactionJobUnit.class);
    
    private final SyncBeanFactoryMutator mockSyncBeanFactory = context.mock(SyncBeanFactoryMutator.class);
    
	private final SyncJobObjectProcessor mockSyncJobObjectProcessor = context.mock(SyncJobObjectProcessor.class);

	private AbstractSyncController abstractSyncController;

	private SyncJobObjectEventDistributor objectEventDistributor;

	/**
	 * Sets up the test case.
	 */
	@Before
	public void setUp() {
		
		abstractSyncController = new AbstractSyncController() {
			@Override
			protected void loadTransactionJob(final SerializableObjectListener listener) {
				// simulate that a transaction job has been loaded
				// notify the listener with the new objects
				for (SerializableObject object : mockTransactionJob) {
					listener.processObject(object);
				}
				listener.finished();
			}
			@Override
			protected SyncJobObjectProcessor getObjectProcessor() {
				return mockSyncJobObjectProcessor;
			}
			@Override
			protected void initConfig(final SystemConfig sourceSystem, final SystemConfig targetSystem) {
				// nothing to do
			}
			@Override
			protected void destroyConfig(final SystemConfig sourceConfig, final SystemConfig targetConfig) {
				// nothing to do
			}
		};

		abstractSyncController.setSyncBeanFactory(mockSyncBeanFactory);
		objectEventDistributor = new SyncJobObjectEventDistributor();
		abstractSyncController.setObjectEventDistributor(objectEventDistributor);
	}
	/**
	 * Tests main work flow of synchronize.
	 */
	@Test
	public void testSynchronize() {
		context.checking(new Expectations() { {
			oneOf(mockTransactionJob).iterator(); will(returnValue(Arrays.asList(mockTransactionJobUnit).iterator()));
			oneOf(mockSyncJobObjectProcessor).transactionJobUnitStart(mockTransactionJobUnit);
			oneOf(mockSyncJobObjectProcessor).transactionJobUnitEnd(with(aNonNull(TransactionJobUnit.class)), with(aNonNull(Summary.class)));
			oneOf(mockSyncJobObjectProcessor).finished(with(aNonNull(Summary.class)));
		} });

		abstractSyncController.synchronize();
	}
	
	/**
	 * Tests main work flow of synchronize with errors.
	 */
	@Test
	public void testSynchronizeFail1() {
		List<ExceptionHandler> exceptionHandlers = new ArrayList<ExceptionHandler>();
		exceptionHandlers.add(new ChangeSetExceptionHandlerImpl());
		objectEventDistributor.setExceptionHandlers(exceptionHandlers);
		
		context.checking(new Expectations() { {
			oneOf(mockTransactionJob).iterator();
			will(returnValue(Arrays.asList(mockTransactionJobUnit).iterator()));
			
			oneOf(mockSyncJobObjectProcessor).transactionJobUnitStart(mockTransactionJobUnit);
			will(throwException(new SyncServiceTransactionRollBackException("")));
			
			oneOf(mockSyncJobObjectProcessor).transactionJobUnitEnd(with(aNonNull(TransactionJobUnit.class)), with(aNonNull(Summary.class)));
			oneOf(mockSyncJobObjectProcessor).finished(with(aNonNull(Summary.class)));
		} });

		abstractSyncController.synchronize();
	}
	
	/**
	 * Tests main work flow of synchronize with errors.
	 */
	@Test
	public void testSynchronizeFail2() {
		List<ExceptionHandler> exceptionHandlers = new ArrayList<ExceptionHandler>();
		exceptionHandlers.add(new ChangeSetExceptionHandlerImpl());
		objectEventDistributor.setExceptionHandlers(exceptionHandlers);
		
		context.checking(new Expectations() { {
			oneOf(mockTransactionJob).iterator(); will(returnValue(Arrays.asList(mockTransactionJobUnit).iterator()));
			
			oneOf(mockSyncJobObjectProcessor).transactionJobUnitStart(mockTransactionJobUnit);
			will(throwException(new IllegalArgumentException("")));
			
			oneOf(mockSyncJobObjectProcessor).transactionJobUnitEnd(with(aNonNull(TransactionJobUnit.class)), with(aNonNull(Summary.class)));
			oneOf(mockSyncJobObjectProcessor).finished(with(aNonNull(Summary.class)));
		} });

		abstractSyncController.synchronize();
	}
	
	/**
	 * Tests main work flow of synchronize with errors. This test reproduces BB-234 bug.
	 * <p>
	 * When user specified change set name that doesn't exist in database then error message should be displayed.
	 * </p>
	 */
	@Test
	public void testSynchronizeFailWhenChangeSetNotFound() {
		List<ExceptionHandler> exceptionHandlers = new ArrayList<ExceptionHandler>();
		exceptionHandlers.add(new ChangeSetExceptionHandlerImpl());
		objectEventDistributor.setExceptionHandlers(exceptionHandlers);

		final String changeSetName = "unknown_change_set_name";
		
		context.checking(new Expectations() { {
			oneOf(mockTransactionJob).iterator(); will(returnValue(Arrays.asList(mockTransactionJobUnit).iterator()));
			
			oneOf(mockSyncJobObjectProcessor).transactionJobUnitStart(mockTransactionJobUnit);
			will(throwException(new ChangeSetNotFoundException("exception", changeSetName)));

			oneOf(mockSyncJobObjectProcessor).transactionJobUnitEnd(with(aNonNull(TransactionJobUnit.class)), with(aNonNull(Summary.class)));
			oneOf(mockSyncJobObjectProcessor).finished(with(aNonNull(Summary.class)));
		} });
		
		// perform method call
		Summary summary = abstractSyncController.synchronize();

		// check that correct error message is returned.
		Assert.assertEquals("One error message should be returned", summary.getSyncErrors().size(), 1);
		SyncErrorResultItem syncErrorResultItem = summary.getSyncErrors().get(0);
		Assert.assertEquals(ChangeSet.class, syncErrorResultItem.getJobEntryType());
		Assert.assertEquals(changeSetName, syncErrorResultItem.getTransactionJobUnitName());
	}

    /**
     * tests the {@link AbstractObjectEventDistributor#finished()} for the case of an exception. 
     *
     * @throws Exception
     */
	@Test
    public void testSynchronizeAbstractObjectEventDistributorFinishedThrowsException() {
	    List<ExceptionHandler> exceptionHandlers = new ArrayList<ExceptionHandler>();
	    exceptionHandlers.add(new DefaultExceptionHandler());
	    objectEventDistributor.setExceptionHandlers(exceptionHandlers);

	    context.checking(new Expectations() { {
			    oneOf(mockTransactionJob).iterator(); will(returnValue(Arrays.asList(mockTransactionJobUnit).iterator()));

			    oneOf(mockSyncJobObjectProcessor).transactionJobUnitStart(mockTransactionJobUnit);
			    oneOf(mockSyncJobObjectProcessor).transactionJobUnitEnd(with(aNonNull(TransactionJobUnit.class)),
				    with(aNonNull(Summary.class)));
			    oneOf(mockSyncJobObjectProcessor).finished(with(aNonNull(Summary.class)));
			    will(throwException(new Exception("Error in finish()")));
			    } });

	    // perform method call
	    Summary summary = abstractSyncController.synchronize();
	    Assert.assertEquals("One error message must be returned", summary.getSyncErrors().size(), 1);
    }
}
