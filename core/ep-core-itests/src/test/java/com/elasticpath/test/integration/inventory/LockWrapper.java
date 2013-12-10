package com.elasticpath.test.integration.inventory;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.elasticpath.inventory.InventoryKey;

/**
 * 
 * Lock wrapper is used to synchronize the 2 test rollup threads.
 *
 */
public class LockWrapper {
	
	private final InventoryKey inventoryKey;
	
	private final Lock lock = new ReentrantLock();

	private final Condition thread1GetUidsCondition = lock.newCondition();
	private final Condition thread2GetUidsCondition = lock.newCondition();
	private boolean thread1GetUidsFinished = false;
	private boolean thread2GetUidsFinished = false;
	
	private final Condition thread1ProcessRollupCondition = lock.newCondition();
	private final Condition thread2ProcessRollupCondition = lock.newCondition();
	private boolean thread1ProcessRollupFinished = false;
	private boolean thread2ProcessRollupFinished = false;

	/**
	 * Constructor.
	 * @param inventoryKey to set.
	 */
	public LockWrapper(final InventoryKey inventoryKey) {
		this.inventoryKey = inventoryKey;
	}

	/**
	 * @return the thread1GetUidsFinished
	 */
	public boolean isThread1GetUidsFinished() {
		return thread1GetUidsFinished;
	}

	/**
	 * @param thread1GetUidsFinished the thread1GetUidsFinished to set
	 */
	public void setThread1GetUidsFinished(final boolean thread1GetUidsFinished) {
		this.thread1GetUidsFinished = thread1GetUidsFinished;
	}

	/**
	 * @return the thread2GetUidsFinished
	 */
	public boolean isThread2GetUidsFinished() {
		return thread2GetUidsFinished;
	}

	/**
	 * @param thread2GetUidsFinished the thread2GetUidsFinished to set
	 */
	public void setThread2GetUidsFinished(final boolean thread2GetUidsFinished) {
		this.thread2GetUidsFinished = thread2GetUidsFinished;
	}

	/**
	 * @return the thread1ProcessRollupFinished
	 */
	public boolean isThread1ProcessRollupFinished() {
		return thread1ProcessRollupFinished;
	}

	/**
	 * @param thread1ProcessRollupFinished the thread1ProcessRollupFinished to set
	 */
	public void setThread1ProcessRollupFinished(final boolean thread1ProcessRollupFinished) {
		this.thread1ProcessRollupFinished = thread1ProcessRollupFinished;
	}

	/**
	 * @return the thread2ProcessRollupFinished
	 */
	public boolean isThread2ProcessRollupFinished() {
		return thread2ProcessRollupFinished;
	}

	/**
	 * @param thread2ProcessRollupFinished the thread2ProcessRollupFinished to set
	 */
	public void setThread2ProcessRollupFinished(final boolean thread2ProcessRollupFinished) {
		this.thread2ProcessRollupFinished = thread2ProcessRollupFinished;
	}

	/**
	 * @return the inventoryKey
	 */
	public InventoryKey getInventoryKey() {
		return inventoryKey;
	}

	/**
	 * @return the lock
	 */
	public Lock getLock() {
		return lock;
	}

	/**
	 * @return the thread1GetUidsCondition
	 */
	public Condition getThread1GetUidsCondition() {
		return thread1GetUidsCondition;
	}

	/**
	 * @return the thread2GetUidsCondition
	 */
	public Condition getThread2GetUidsCondition() {
		return thread2GetUidsCondition;
	}

	/**
	 * @return the thread1ProcessRollupCondition
	 */
	public Condition getThread1ProcessRollupCondition() {
		return thread1ProcessRollupCondition;
	}

	/**
	 * @return the thread2ProcessRollupCondition
	 */
	public Condition getThread2ProcessRollupCondition() {
		return thread2ProcessRollupCondition;
	}
	
}
