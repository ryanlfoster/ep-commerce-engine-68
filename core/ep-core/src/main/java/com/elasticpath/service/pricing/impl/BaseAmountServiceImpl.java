package com.elasticpath.service.pricing.impl;

import java.util.Collection;
import java.util.List;

import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.common.pricing.service.BaseAmountFilter;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.pricing.BaseAmount;
import com.elasticpath.domain.pricing.exceptions.BaseAmountInvalidException;
import com.elasticpath.domain.pricing.exceptions.DuplicateBaseAmountException;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.service.pricing.BaseAmountService;
import com.elasticpath.service.pricing.PriceUpdatedNotificationService;
import com.elasticpath.service.pricing.dao.BaseAmountDao;
import com.elasticpath.service.pricing.exceptions.BaseAmountNotExistException;
/**
 * Default implementation of BaseAmountService, provides a database backed implementation
 * of {@link com.elasticpath.service.pricing.datasource.BaseAmountDataSource}.
 */
public class BaseAmountServiceImpl implements BaseAmountService {

	private BaseAmountDao dao;
	private BeanFactory beanFactory;
	private PriceUpdatedNotificationService priceUpdatedNotificationService;
	private Validator validator;
	private PersistenceEngine persistenceEngine;

	@Override
	public BaseAmount findByGuid(final String guid) {
		return dao.findBaseAmountByGuid(guid);
	}

	@Override
	public void delete(final BaseAmount baseAmount) {
		final String priceListDescriptorGuid = baseAmount.getPriceListDescriptorGuid();
		final String objectType = baseAmount.getObjectType();
		final String objectGuid = baseAmount.getObjectGuid();
		try {
			dao.delete(baseAmount);
		} catch (final Exception ex) {
			throw new EpServiceException("Failed to delete " + baseAmount, ex);
		}

		// DO NOT use the method notifyBaseAmountUpdated(baseAmount), due to this scenario:
		// if the baseAmount value gets retrieved using a fetch plan that doesn't load all it's properties
		// before delete happens, then at delete time when the notification for the index is created
		// openJPA will try to load the properties it needs, but will through an exception because the baseamount was deleted
		notifyBaseAmountUpdated(priceListDescriptorGuid, objectType, objectGuid);
	}

	@Override
	public void delete(final String priceListDescriptiptorGuid) {
		dao.delete(priceListDescriptiptorGuid);
	}

	@Override
	public Collection<BaseAmount> findBaseAmounts(final BaseAmountFilter filter) {
		return dao.findBaseAmounts(filter);
	}

	@Override
	public List<BaseAmount> findBaseAmounts(
			final String namedQuery,
			final Object [] searchCriteria,
			final int limit, final List<String> guids) {
		return dao.findBaseAmounts(
				namedQuery,
				searchCriteria,
				limit, guids);
	}

	@Override
	public List<BaseAmount> findBaseAmounts(
			final String namedQuery,
			final Object [] searchCriteria,
			final int startIndex, final int pageSize, final List<String> guids) {
		return dao.findBaseAmounts(
				namedQuery,
				searchCriteria,
				startIndex, pageSize, guids);
	}

	@Override
	public List<BaseAmount> findBaseAmounts(final String priceListDescriptorGuid,
			final String objectType, final String ... objectGuids) {

		return dao.findBaseAmounts(
				priceListDescriptorGuid,
				objectType,
				objectGuids);

	}

	@Override
	public List<BaseAmount> getBaseAmounts(final List<String> plGuids, final List<String> objectGuids) {
		return dao.getBaseAmounts(plGuids, objectGuids);
	}

	@Override
	public BaseAmount add(final BaseAmount baseAmount) {
		if (exists(baseAmount)) {
			throw new DuplicateBaseAmountException("Duplicate BaseAmount cannot be added.", baseAmount);
		}
		BaseAmount newBaseAmount = null;
		try {
			newBaseAmount = dao.add(baseAmount);
		} catch (final Exception ex) {
			throw new EpServiceException("Failed to add " + baseAmount, ex);
		}
		notifyBaseAmountUpdated(newBaseAmount.getPriceListDescriptorGuid(), newBaseAmount.getObjectType(), newBaseAmount.getObjectGuid());
		return newBaseAmount;
	}

	@Override
    public BaseAmount updateWithoutLoad(final BaseAmount baseAmountToUpdate) throws BaseAmountNotExistException {
		verifyBaseAmount(baseAmountToUpdate);

		// Work Around AuditEntityListener problems with pre-attached objects
		final BaseAmount detachedInstance = getPersistenceEngine().detach(baseAmountToUpdate);

		BaseAmount updatedBaseAmount;
		try {
			updatedBaseAmount = dao.update(detachedInstance);
		} catch (final Exception ex) {
			throw new EpServiceException("Failed to update " + baseAmountToUpdate, ex);
		}

		notifyBaseAmountUpdated(updatedBaseAmount.getPriceListDescriptorGuid(), updatedBaseAmount.getObjectType(), updatedBaseAmount.getObjectGuid());

		return updatedBaseAmount;

    }

    @Override
	public BaseAmount update(final BaseAmount newBaseAmount) throws EpServiceException {
		final BaseAmount oldBaseAmount = findByGuid(newBaseAmount.getGuid());
		if (oldBaseAmount == null) {
			throw new BaseAmountNotExistException("A BaseAmount with GUID=" + newBaseAmount.getGuid() + " does not exist in the persistence layer.");
		}

		oldBaseAmount.setListValue(newBaseAmount.getListValue());
		oldBaseAmount.setSaleValue(newBaseAmount.getSaleValue());
		return updateWithoutLoad(oldBaseAmount);
	}

	private void verifyBaseAmount(final BaseAmount baseAmount) {
		final Errors errors = new BeanPropertyBindingResult(baseAmount, "BaseAmount");
		validator.validate(baseAmount, errors);
		if (errors.hasErrors()) {
			throw new BaseAmountInvalidException("BaseAmount validation error", errors);
		}
	}

	private void notifyBaseAmountUpdated(final String priceListDescriptorGuid, final String objectType, final String  objectGuid) {
		priceUpdatedNotificationService.notifyPriceUpdated(priceListDescriptorGuid, objectType, objectGuid);
	}

	/**
	 * Set the BaseAmountDao.
	 *
	 * @param dao object to use
	 */
	public void setBaseAmountDao(final BaseAmountDao dao) {
		this.dao = dao;
	}

	/**
	 * Determines whether a BaseAmount equal to the given BaseAmount exists in the persistence layer.
	 * A BaseAmount exists if there exists a BaseAmount with the same PriceListDesciptorGuid, ObjectGuid, ObjectType,
	 * and Quantity as the given BaseAmount.
	 * @param baseAmount the BaseAmount to test for existence
	 * @return true if a BaseAmount whose key fields match the given BaseAmount exists in the persistence layer
	 */
	public boolean exists(final BaseAmount baseAmount) {
		final BaseAmountFilter filter = createBaseAmountFilter();
		filter.setObjectGuid(baseAmount.getObjectGuid());
		filter.setObjectType(baseAmount.getObjectType());
		filter.setQuantity(baseAmount.getQuantity());
		filter.setPriceListDescriptorGuid(baseAmount.getPriceListDescriptorGuid());
		final Collection<BaseAmount> persistentBaseAmounts = findBaseAmounts(filter);
		if (persistentBaseAmounts.isEmpty()) {
			return false;
		}
		return true;
	}

	@Override
	public boolean guidExists(final String guid) {
		return dao.guidExists(guid);
	}

	/**
	 * @return a new empty BaseAmountFilter bean
	 */
	protected BaseAmountFilter createBaseAmountFilter() {
		return getBeanFactory().getBean(ContextIdNames.BASE_AMOUNT_FILTER);
	}

	/**
	 * @return the beanFactory
	 */
	public BeanFactory getBeanFactory() {
		return beanFactory;
	}

	/**
	 * @param beanFactory the beanFactory to set
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * Sets the {@link PriceUpdatedNotificationService}.
	 *
	 * @param priceNotificationService {@link PriceUpdatedNotificationService}.
	 */
	public void setPriceUpdatedNotificationService(final PriceUpdatedNotificationService priceNotificationService) {
		priceUpdatedNotificationService = priceNotificationService;
	}

	/**
	 * Set validator for BaseAmount object.
	 * @param validator the validator to set
	 */
	public void setValidator(final Validator validator) {
		this.validator = validator;
	}

	protected PersistenceEngine getPersistenceEngine() {
		return persistenceEngine;
	}

	public void setPersistenceEngine(final PersistenceEngine persistenceEngine) {
		this.persistenceEngine = persistenceEngine;
	}
}
