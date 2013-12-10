package com.elasticpath.tools.sync.job;

import java.util.List;

import com.elasticpath.tools.sync.processing.SerializableObject;

/**
 * Lists <code>TransactionJobUnit</code> objects..
 */
public interface TransactionJob extends SerializableObject, Iterable<SerializableObject> {

	/**
	 * @return entries forming synchronization job
	 */
	List<TransactionJobUnit> getTransactionJobUnits();

	/**
	 * Adds a transaction job unit.
	 * 
	 * @param transactionJobUnit the new unit to add
	 */
	void addTransactionJobUnit(TransactionJobUnit transactionJobUnit);
	
	/**
	 * Returns the number of transaction job units to process.
	 * @return the number of transaction job units to process.
	 */
	int getTransactionJobUnitsNumber();
	
}