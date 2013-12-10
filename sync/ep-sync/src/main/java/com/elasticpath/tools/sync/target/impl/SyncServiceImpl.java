package com.elasticpath.tools.sync.target.impl;

import java.util.List;

import org.apache.log4j.Logger;

import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.service.EpService;
import com.elasticpath.service.impl.AbstractEpServiceImpl;
import com.elasticpath.tools.sync.exception.SyncToolConfigurationException;
import com.elasticpath.tools.sync.exception.SyncToolRuntimeException;
import com.elasticpath.tools.sync.job.Command;
import com.elasticpath.tools.sync.job.JobEntry;
import com.elasticpath.tools.sync.job.TransactionJobUnit;
import com.elasticpath.tools.sync.merge.MergeEngine;
import com.elasticpath.tools.sync.target.DaoAdapter;
import com.elasticpath.tools.sync.target.DaoAdapterFactory;
import com.elasticpath.tools.sync.target.JobTransactionCallback;
import com.elasticpath.tools.sync.target.SyncService;
import com.elasticpath.tools.sync.target.SyncServiceTransactionRollBackException;
import com.elasticpath.tools.sync.target.result.SyncErrorResultItem;

/**
 * Provides synchronization of a transaction unit to target environment in a single transaction.
 */
public class SyncServiceImpl extends AbstractEpServiceImpl implements EpService, SyncService {
	private static final String PROCESS_JOB_ENTRY_EXCEPTION = "Unable to process job entry";

	private static final Logger LOG = Logger.getLogger(SyncServiceImpl.class);

	private DaoAdapterFactory daoAdapterFactory;

	private MergeEngine mergeEngine;

	private List<JobTransactionCallback> callbacks;

	/**
	 * Provides synchronization of a transaction unit to target environment in a single transaction.
	 *
	 * @param transactionJobUnit transaction job unit to process
	 * @throws SyncServiceTransactionRollBackException if the transaction has been rolled back
	 */
	public void processTransactionJobUnit(final TransactionJobUnit transactionJobUnit) throws SyncServiceTransactionRollBackException {
		for (final JobEntry jobEntry : transactionJobUnit.createJobEntries()) {
			processJobEntry(jobEntry);
		}
	}

	/**
	 * Processes a job entry in the outer transaction.
	 *
	 * @param jobEntry Job Entry to process.
	 * @throws SyncServiceTransactionRollBackException if the transaction has been rolled back
	 */
	public void processJobEntry(final JobEntry jobEntry) throws SyncServiceTransactionRollBackException {
		final Class< ? > entryType = jobEntry.getType();

		final Command command = jobEntry.getCommand();

		try {
			final DaoAdapter< ? super Persistable> daoAdapter = daoAdapterFactory.getDaoAdapter(entryType);

			switch (command) {
			case UPDATE:
				processUpdate(jobEntry, daoAdapter);
				break;
			case REMOVE:
				processRemove(jobEntry, daoAdapter);
				break;
			default:
			}
		} catch (final Exception e) {
			final SyncErrorResultItem syncError = createSyncError(jobEntry);
			LOG.error(PROCESS_JOB_ENTRY_EXCEPTION, e);
			throw new SyncServiceTransactionRollBackException(PROCESS_JOB_ENTRY_EXCEPTION, e, syncError);
		}
	}

	private SyncErrorResultItem createSyncError(final JobEntry jobEntry) {
		final SyncErrorResultItem syncError = new SyncErrorResultItem();
		syncError.setTransactionJobUnitName(jobEntry.getTransactionJobUnitName());
		syncError.setJobEntryType(jobEntry.getType());
		syncError.setJobEntryGuid(jobEntry.getGuid());
		syncError.setJobEntryCommand(jobEntry.getCommand());
		return syncError;
	}

	private void processRemove(final JobEntry jobEntry, final DaoAdapter< ? > daoAdapter) {
		final Persistable targetPersistence = daoAdapter.get(jobEntry.getGuid());
		firePreRemoveCallback(jobEntry, targetPersistence);
		 if (!daoAdapter.remove(jobEntry.getGuid())) {
			 LOG.warn("Failed to process remove for jobEntry: " + jobEntry);
		 }
		firePostRemoveCallbacks(jobEntry, targetPersistence);
	}

	private void processUpdate(final JobEntry jobUnit, final DaoAdapter< ? super Persistable> daoAdapter) throws SyncToolConfigurationException {

		boolean newPersistence = false;

		Persistable targetPersistence = daoAdapter.get(jobUnit.getGuid());

		firePreUpdateCallbacks(jobUnit, targetPersistence);

		final Persistable sourceObject = jobUnit.getSourceObject();
        if (targetPersistence == null) {
			targetPersistence = daoAdapter.createBean(sourceObject);
			newPersistence = true;
		}

		merge(sourceObject, targetPersistence);

		if (newPersistence) {
			daoAdapter.add(targetPersistence);
		} else {
			targetPersistence = daoAdapter.update(targetPersistence);
		}
		firePostUpdateCallbacks(jobUnit, targetPersistence);
	}

	private void merge(final Persistable persistable, final Persistable targetPersistence) throws SyncToolConfigurationException {
		mergeEngine.processMerge(persistable, targetPersistence);
	}

	/**
	 * @param daoAdapterFactory the daoAdapterFactory to set
	 */
	public void setDaoAdapterFactory(final DaoAdapterFactory daoAdapterFactory) {
		this.daoAdapterFactory = daoAdapterFactory;
	}

	/**
	 * @param mergeEngine the mergeEngine to set
	 */
	public void setMergeEngine(final MergeEngine mergeEngine) {
		this.mergeEngine = mergeEngine;
	}

	/**
	 * Uses spring to register a set of callbacks which will be called before and after
	 * doing an update or remove for each job entry. The callbacks are synchronous and
	 * operate in the same transaction as the sync so they should complete quickly. This
	 * is intentional because we desire any changes made as part of the sync to be part of
	 * the transaction so it appears at the end, not in the middle of a potentially long
	 * transaction.
	 *
	 *  The callbacks will be called in the order they are supplied in
	 *
	 * @param callbacks set of callbacks to install for notifications/modifying process
	 */
	public void setHookCallbacks(final List<JobTransactionCallback> callbacks) {
		this.callbacks = callbacks;
	}

	/**
	 * Fires pre-update callback synchronously, in the order the callbacks are specified in the initial list. Updates
	 * includes modifications and additions.
	 *
	 * @param jobEntry the job entry just updated
	 * @param targetPersistence the Persistable object on the target that corresponds to the jobEntry
	 */
	protected void firePostUpdateCallbacks(final JobEntry jobEntry, final Persistable targetPersistence) {
		for (final JobTransactionCallback callback : callbacks) {
			try {
				callback.postUpdateJobEntryHook(jobEntry, targetPersistence);
			} catch (final RuntimeException e) {
				throw new SyncToolRuntimeException(composeErrorMessage(callback, "Post-update callback ", jobEntry), e);
			}
		}
	}

	/**
	 * Fires pre-update callback synchronously, in the order the callbacks are specified in the initial list. Updates
	 * includes modifications and additions.
	 *
	 * @param jobEntry the job entry to be updated
	 * @param targetPersistence the Persistable object on the target that corresponds to the jobEntry
	 */
	protected void firePreUpdateCallbacks(final JobEntry jobEntry, final Persistable targetPersistence) {
		for (final JobTransactionCallback callback : callbacks) {
			try {
				callback.preUpdateJobEntryHook(jobEntry, targetPersistence);
			} catch (final RuntimeException e) {
				throw new SyncToolRuntimeException(composeErrorMessage(callback, "Pre-update callback ", jobEntry), e);
			}
		}
	}

	/**
	 * Fires post-remove callback synchronously, in the order the callbacks are specified in the initial list.
	 *
	 * @param jobEntry the job entry just removed
	 * @param targetPersistence the Persistable object on the target that corresponds to the jobEntry
	 */
	protected void firePostRemoveCallbacks(final JobEntry jobEntry, final Persistable targetPersistence) {
		for (final JobTransactionCallback callback : callbacks) {
			try {
				callback.postRemoveJobEntryHook(jobEntry, targetPersistence);
			} catch (final RuntimeException e) {
				throw new SyncToolRuntimeException(composeErrorMessage(callback, "Post-remove callback ", jobEntry), e);
			}
		}
	}

	/**
	 * Fires pre-remove callback synchronously, in the order the callbacks are specified in the initial list.
	 *
	 * @param jobEntry the job entry to be removed
	 * @param targetPersistence the Persistable object on the target that corresponds to the jobEntry
	 */
	protected void firePreRemoveCallback(final JobEntry jobEntry, final Persistable targetPersistence) {
		for (final JobTransactionCallback callback : callbacks) {
			try {
				callback.preRemoveJobEntryHook(jobEntry, targetPersistence);
			} catch (final RuntimeException e) {
				throw new SyncToolRuntimeException(composeErrorMessage(callback, "Pre-remove callback ", jobEntry), e);
			}
		}
	}

	/**
	 * Compose an error message to providing consistent information about the objects in use when the error occured.
	 */
	private String composeErrorMessage(final JobTransactionCallback callback, final String callbackType, final JobEntry jobUnit) {
		String callbackID;
		if (callback == null) {
			callbackID = "null callback";
		} else {
			callbackID = callback.getCallbackID();
		}

		String jobUnitID;
		if (jobUnit == null) {
			jobUnitID = "null jobUnit";
		} else {
			jobUnitID = jobUnit.getGuid();
		}

		return callbackType + callbackID + " caused exception on jobEntry " + jobUnitID;
	}

	@Override
	public void addHookCallback(final JobTransactionCallback callback) {
		callbacks.add(callback);
	}
}
