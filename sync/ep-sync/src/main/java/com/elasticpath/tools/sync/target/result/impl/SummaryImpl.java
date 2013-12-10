package com.elasticpath.tools.sync.target.result.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.elasticpath.tools.sync.job.JobEntry;
import com.elasticpath.tools.sync.target.result.Summary;
import com.elasticpath.tools.sync.target.result.SyncErrorResultItem;
import com.elasticpath.tools.sync.target.result.SyncResultItem;

/**
 * Default implementation of <code>ErrorCollector</code>.
 */
public class SummaryImpl implements Summary {

	private final List<SyncErrorResultItem> errorItems = new ArrayList<SyncErrorResultItem>();
	
	private final List<SyncResultItem> successItems = new ArrayList<SyncResultItem>();

	@Override
	public void addSyncError(final SyncErrorResultItem syncError) {
		errorItems.add(syncError);
	}

	@Override
	public List<SyncErrorResultItem> getSyncErrors() {
		return Collections.unmodifiableList(errorItems);
	}

	@Override
	public boolean hasErrors() {
		return !errorItems.isEmpty();
	}

	@Override
	public int getNumberOfErrors() {		
		return errorItems.size();
	}

	@Override
	public void addSuccessJobEntry(final JobEntry entry) {
		SyncResultItem item = new SyncResultItem();
		item.setJobEntryCommand(entry.getCommand());
		item.setJobEntryGuid(entry.getGuid());
		item.setJobEntryType(entry.getType());
		item.setTransactionJobUnitName(entry.getTransactionJobUnitName());
		successItems.add(item);
	}

	@Override
	public List<SyncResultItem> getAllResults() {
		List<SyncResultItem> all = new ArrayList<SyncResultItem>();
		all.addAll(getSuccessResults());
		all.addAll(getSyncErrors());
		return all;
	}

	@Override
	public List<SyncResultItem> getSuccessResults() {
		return Collections.unmodifiableList(successItems);
	}
}
