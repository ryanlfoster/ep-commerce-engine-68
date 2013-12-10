package com.elasticpath.inventory.strategy.impl;

import java.util.List;

import org.apache.log4j.Level;
import org.springframework.core.NestedRuntimeException;
import org.springframework.orm.jpa.JpaOptimisticLockingFailureException;
import org.springframework.orm.jpa.JpaSystemException;

import com.elasticpath.inventory.InventoryKey;
import com.elasticpath.inventory.log.impl.InventoryLogContext;
import com.elasticpath.inventory.log.impl.InventoryLogSupport;
import com.elasticpath.inventory.strategy.InventoryJournalRollupService;

/**
 * Job which will rollup inventory journal.
 */
public class InventoryJournalRollupJobImpl {
	
	private InventoryJournalRollupService inventoryJournalRollupService;
	
	private InventoryLogSupport inventoryLogSupport;
	
	/**
	 * @param inventoryLogSupport the inventoryLogSupport to set
	 */
	public void setInventoryLogSupport(final InventoryLogSupport inventoryLogSupport) {
		this.inventoryLogSupport = inventoryLogSupport;
	}

	/**
	 * @param inventoryJournalRollupService the inventoryJournalRollupService to set
	 */
	public void setInventoryJournalRollupService(
			final InventoryJournalRollupService inventoryJournalRollupService) {
		this.inventoryJournalRollupService = inventoryJournalRollupService;
	}

	/**
	 * Rollup inventory journals. 
	 */
	public void rollup() {
		inventoryLogSupport.log(Level.INFO, InventoryLogContext.ROLLUP_STARTED_MSG, null);
		List<InventoryKey> inventoryKeys = inventoryJournalRollupService.getAllInventoryKeys();
		for (InventoryKey inventoryKey : inventoryKeys) {
			try {
				inventoryJournalRollupService.processRollup(inventoryKey);
			} catch (JpaSystemException e) {
				log(inventoryKey, e);
				break;
			} catch (JpaOptimisticLockingFailureException e) {
				log(inventoryKey, e);
				break;
			}
		}
		inventoryLogSupport.log(Level.INFO, InventoryLogContext.ROLLUP_ENDED_MSG, null);
	}

	private void log(final InventoryKey inventoryKey, final NestedRuntimeException jpaException) {
		InventoryLogContext inventoryLogContext = new InventoryLogContext(inventoryKey);
		inventoryLogSupport.log(Level.INFO, 
				InventoryLogContext.ROLLUP_CONTENTION_MSG + jpaException.getMostSpecificCause().getMessage(),
				inventoryLogContext);
	}

}
