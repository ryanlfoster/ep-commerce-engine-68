package com.elasticpath.tools.sync.target.impl;

import java.util.HashSet;
import java.util.Set;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.domain.pricing.BaseAmount;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.service.pricing.PriceUpdatedNotificationService;
import com.elasticpath.tools.sync.job.JobEntry;
import com.elasticpath.tools.sync.target.JobUnitTransactionCallbackListener;

/**
 * The Class BaseAmountNotificationJobTransactionCallback.
 */
public class BaseAmountJobTransactionCallbackListener extends AbstractJobTransactionCallbackAdapter implements JobUnitTransactionCallbackListener {

	private final Set<BaseAmount> baseAmountsToUpdate = new HashSet<BaseAmount>();
	
	private BeanFactory coreBeanFactory;

	@Override
	public String getCallbackID() {
		return "Base Amount Notification Callback";
	}

	/**
	 * Called after the update is processed to capture the guid for later
	 * notification.
	 * 
	 * @param jobEntry
	 *            the job entry containing the guid.
	 * @param targetPersistence
	 *            the persistent object.
	 */
	@Override
	public void postUpdateJobEntryHook(final JobEntry jobEntry, final Persistable targetPersistence) {
		addGuidToSet(jobEntry);
	}

	private void addGuidToSet(final JobEntry jobEntry) {
		final Persistable sourceObject = jobEntry.getSourceObject();
		if (sourceObject instanceof BaseAmount) {
			final BaseAmount baseAmount = (BaseAmount) sourceObject;
			getBaseAmountsToUpdate().add(baseAmount);
		}
	}

	/**
	 * Sends price update notifications for base amounts.
	 */
	public void preCommitHook() {
		final PriceUpdatedNotificationService priceUpdatedNotificationService = getPriceUpdatedNotificationService();
		// Separate loop to allow batch updates
		for (final BaseAmount baseAmount : getBaseAmountsToUpdate()) {
			priceUpdatedNotificationService.notifyPriceUpdated(baseAmount.getPriceListDescriptorGuid(), baseAmount.getObjectType(),
					baseAmount.getObjectGuid());
		}
	}

	/**
	 * Called after the remove is processed to capture the guid for later notification.
	 * 
	 * @param jobEntry
	 *            the job entry containing the guid.
	 * @param targetPersistence
	 *            the persistent object.
	 */
	@Override
	public void postRemoveJobEntryHook(final JobEntry jobEntry, final Persistable targetPersistence) {
		addGuidToSet(jobEntry);
	}

	/**
	 * Gets the base amounts to update.
	 *
	 * @return the baseAmountsToUpdate
	 */
	private Set<BaseAmount> getBaseAmountsToUpdate() {
		return baseAmountsToUpdate;
	}

	/**
	 * Gets the price updated notification service.
	 * 
	 * @return the priceUpdatedNotificationService
	 */
	public PriceUpdatedNotificationService getPriceUpdatedNotificationService() {
		return coreBeanFactory.getBean("priceUpdatedNotificationService");
	}

	public void setCoreBeanFactory(final BeanFactory coreBeanFactory) {
		this.coreBeanFactory = coreBeanFactory;
	}

}
