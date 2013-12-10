package com.elasticpath.importexport.importer.importers.impl;

import org.apache.log4j.Logger;

import com.elasticpath.common.dto.Dto;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.exception.runtime.ImportRuntimeException;
import com.elasticpath.importexport.importer.importers.CollectionsStrategy;
import com.elasticpath.importexport.importer.importers.SavingStrategy;
import com.elasticpath.importexport.importer.types.ImportStrategyType;
import com.elasticpath.persistence.api.Persistable;

/**
 * Abstract class for saving strategy.
 *
 * @param <DOMAIN> type of {@link Persistable} the strategy is for
 * @param <DTO> type of {@link Dto} the strategy is for
 */
public abstract class AbstractSavingStrategy<DOMAIN extends Persistable, DTO extends Dto> implements SavingStrategy<DOMAIN, DTO> {
	
	private static final Logger LOG = Logger.getLogger(AbstractSavingStrategy.class);

	private static final DefaultLifecycleListener DEFAULT_LIFECYCLE_LISTENER = new DefaultLifecycleListener();

	private DomainAdapter<DOMAIN, DTO> domainAdapter;

	private SavingManager<DOMAIN> savingManager;

	private LifecycleListener lifecycleListener;

	private CollectionsStrategy<DOMAIN, DTO> collectionsStrategy;

	/**
	 * Creates saving strategy.
	 * 
	 * @param strategyType the strategyType
	 * @param savingManager the saving manager
	 * @return the appropriate strategy
	 * @param <DOMAIN> type of {@link Persistable} object the strategy is for
	 * @param <DTO> tyep of {@link Dto} object the strategy is for
	 */
	public static <DOMAIN extends Persistable, DTO extends Dto> SavingStrategy<DOMAIN, DTO> createStrategy(
			final ImportStrategyType strategyType, final SavingManager<DOMAIN> savingManager) {
		SavingStrategy<DOMAIN, DTO> savingStrategy = null;
		switch (strategyType) {
		case INSERT_OR_UPDATE:
			savingStrategy = new InsertOrUpdateSavingStrategy<DOMAIN, DTO>();
			break;
		case INSERT:
			savingStrategy = new InsertSavingStrategy<DOMAIN, DTO>();
			break;
		case UPDATE:
			savingStrategy = new UpdateSavingStrategy<DOMAIN, DTO>();
			break;
		case IMMUTABLE:
			savingStrategy = new ImmutableSavingStrategy<DOMAIN, DTO>();
			break;			
		default:
			throw new ImportRuntimeException("IE-30550");
		}
		savingStrategy.setSavingManager(savingManager);
		savingStrategy.setLifecycleListener(DEFAULT_LIFECYCLE_LISTENER);
		savingStrategy.setCollectionsStrategy(AbstractSavingStrategy.<DOMAIN, DTO> getDefaultCollectionsStrategy());
		return savingStrategy;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws ImportRuntimeException in case {@link SavingManager} or {@link DomainAdapter} are not initialized
	 */
	@Override
	public DOMAIN populateAndSaveObject(final DOMAIN savedObject, final DTO importDtoObject) {
		if (savingManager == null || domainAdapter == null) {
			throw new ImportRuntimeException("IE-30551");
		}		

		if (!isImportRequired(savedObject)) {
			return null;
		}
		
		DOMAIN objectToImport = createDomainObject(savedObject);
		
		prepareCollections(importDtoObject, objectToImport);
		
		objectToImport = getDomainAdapter().buildDomain(importDtoObject, objectToImport);
		
		return saveDomainObject(objectToImport);
	}

	private void prepareCollections(final DTO importDtoObject, final DOMAIN importObject) {
		if (importObject == null) {
			return;
		}
		if (!collectionsStrategy.isForPersistentObjectsOnly() || importObject.isPersisted()) {
			collectionsStrategy.prepareCollections(importObject, importDtoObject);
		}
	}

	/**
	 * Creates the domain object based on <code>savedObject</code> from database.
	 * 
	 * @param savedObject the object from database or null if it does not exist yet
	 * @return persistable object that will be populated and saved to data base or null if it does not need to persist this object
	 */
	protected abstract DOMAIN createDomainObject(DOMAIN savedObject);

	/**
	 * {@inheritDoc}
	 * 
	 * @throws ImportRuntimeException in case {@link SavingManager} is not initialized
	 */
	@Override
	public DOMAIN saveDomainObject(final DOMAIN importObject) {
		if (savingManager == null) {
			throw new ImportRuntimeException("IE-30551");
		}
		LOG.debug("Saving domain object: " + importObject);
		lifecycleListener.beforeSave(importObject);
		DOMAIN resultObject = saveObject(importObject);
		lifecycleListener.afterSave(resultObject);

		return resultObject;
	}

	/**
	 * Saves the object to database.
	 * 
	 * @param importObject the object for saving
	 * @return the merged object if it is merged, or the persisted object for save action
	 */
	protected abstract DOMAIN saveObject(DOMAIN importObject);

	/**
	 * Gets the domainAdapter.
	 * 
	 * @return the domainAdapter
	 */
	@Override
	public DomainAdapter<DOMAIN, DTO> getDomainAdapter() {
		return domainAdapter;
	}

	/**
	 * Sets the domainAdapter.
	 * 
	 * @param domainAdapter the domainAdapter to set
	 */
	@Override
	public void setDomainAdapter(final DomainAdapter<DOMAIN, DTO> domainAdapter) {
		this.domainAdapter = domainAdapter;
	}

	/**
	 * Gets the session.
	 * 
	 * @return the session
	 */
	@Override
	public SavingManager<DOMAIN> getSavingManager() {
		return savingManager;
	}

	/**
	 * Sets the session.
	 * 
	 * @param session the session to set
	 */
	@Override
	public void setSavingManager(final SavingManager<DOMAIN> session) {
		this.savingManager = session;
	}

	/**
	 * Gets the lifecycleListener.
	 * 
	 * @return the lifecycleListener
	 * @see LifecycleListener
	 */
	@Override
	public LifecycleListener getLifecycleListener() {
		return lifecycleListener;
	}

	/**
	 * Sets the lifecycleListener.
	 * <p>
	 * Note: In case of null argument system will set the default life cycle listener.
	 * 
	 * @param  lifecycleListener the lifecycleListener to set
	 * @see LifecycleListener
	 */
	@Override
	public void setLifecycleListener(final LifecycleListener lifecycleListener) {
		this.lifecycleListener = lifecycleListener;
		
		if (lifecycleListener == null) {
			this.lifecycleListener = DEFAULT_LIFECYCLE_LISTENER;
		}
	}

	/**
	 * Gets the collections strategy.
	 * 
	 * @return the collectionsStrategy
	 */
	@Override
	public CollectionsStrategy<DOMAIN, DTO> getCollectionsStrategy() {
		return collectionsStrategy;
	}

	/**
	 * Sets the collections strategy.
	 * <p>
	 * Note: In case of null argument system will set the default collections strategy.
	 * 
	 * @param collectionsStrategy the collectionsStrategy to set
	 */
	@Override
	public void setCollectionsStrategy(final CollectionsStrategy<DOMAIN, DTO> collectionsStrategy) {
		this.collectionsStrategy = collectionsStrategy;
		
		if (collectionsStrategy == null) {
			this.collectionsStrategy = getDefaultCollectionsStrategy();
		}
	}

	private static <DOMAIN extends Persistable, DTO extends Dto> CollectionsStrategy<DOMAIN, DTO> getDefaultCollectionsStrategy() {
		return new DefaultCollectionsStrategy<DOMAIN, DTO>();
	}

	/**
	 * Implementation of Insert or Update saving strategy.
	 */
	static class InsertOrUpdateSavingStrategy<DOMAIN extends Persistable, DTO extends Dto> extends AbstractSavingStrategy<DOMAIN, DTO> {

		@Override
		protected DOMAIN createDomainObject(final DOMAIN savedObject) {
			DOMAIN importObject = savedObject;
			if (importObject == null) {
				importObject = getDomainAdapter().createDomainObject();
			}
			return importObject;
		}

		@Override
		protected DOMAIN saveObject(final DOMAIN importObject) {
			DOMAIN resultObject = importObject;
			if (importObject.isPersisted()) {
				resultObject = getSavingManager().update(importObject);
			} else {
				getSavingManager().save(resultObject);
			}
			return resultObject;
		}

		@Override
		public boolean isImportRequired(final Persistable object) {
			return true;
		}

	}

	/**
	 * Implementation of Insert saving strategy.
	 */
	static class InsertSavingStrategy<DOMAIN extends Persistable, DTO extends Dto> extends AbstractSavingStrategy<DOMAIN, DTO> {

		@Override
		protected DOMAIN createDomainObject(final DOMAIN savedObject) {
			if (isImportRequired(savedObject)) {
				return getDomainAdapter().createDomainObject();
			}
			return null;
		}

		@Override
		protected DOMAIN saveObject(final DOMAIN importObject) {
			getSavingManager().save(importObject);
			return importObject;
		}
		
		@Override
		public boolean isImportRequired(final Persistable object) {
			return object == null;
		}
	}

	/**
	 * Implementation of Update saving strategy.
	 */
	static class UpdateSavingStrategy<DOMAIN extends Persistable, DTO extends Dto> extends AbstractSavingStrategy<DOMAIN, DTO> {

		@Override
		protected DOMAIN createDomainObject(final DOMAIN savedObject) {
			return savedObject;
		}

		@Override
		protected DOMAIN saveObject(final DOMAIN importObject) {
			return getSavingManager().update(importObject);
		}
		
		@Override
		public boolean isImportRequired(final Persistable object) {
			return object != null;
		}
	}
	
	/**
	 * Implementation of Immutable saving strategy.
	 */
	static class ImmutableSavingStrategy<DOMAIN extends Persistable, DTO extends Dto> extends AbstractSavingStrategy<DOMAIN, DTO> {

		@Override
		protected DOMAIN createDomainObject(final DOMAIN savedObject) {
			//immutable object can't be created once and populated then.
			return null;
		}

		@Override
		protected DOMAIN saveObject(final DOMAIN importObject) {
			//immutable object can only be inserted, can't be updated as it doesn't exist
			getSavingManager().save(importObject);
			return importObject;
		}

		@Override
		public boolean isImportRequired(final Persistable object) {
			return true;
		}

	}

	/**
	 * Default implementation of <code>CollectionsStrategy</code> interface.
	 */
	private static final class DefaultCollectionsStrategy<DOMAIN extends Persistable, DTO extends Dto> implements
			CollectionsStrategy<DOMAIN, DTO> {
		@Override
		public void prepareCollections(final Persistable object, final Dto importDto) {
			// default empty implementation
		}

		@Override
		public boolean isForPersistentObjectsOnly() {
			return true;
		}
	}

}

/**
 * Default implementation of <code>LifecycleListener</code> interface.
 */
class DefaultLifecycleListener implements LifecycleListener {
	@Override
	public void afterSave(final Persistable persistable) {
		// default empty implementation
	}

	@Override
	public void beforeSave(final Persistable persistable) {
		// default empty implementation
	}

	@Override
	public void beforePopulate(final Persistable persistable) {
		// default empty implementation
	}
}

