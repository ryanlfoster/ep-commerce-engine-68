package com.elasticpath.importexport.importer.importers;

import com.elasticpath.common.dto.Dto;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.importer.importers.impl.LifecycleListener;
import com.elasticpath.importexport.importer.importers.impl.SavingManager;
import com.elasticpath.persistence.api.Persistable;

/**
 * Saving strategy interface.
 *
 * @param <DOMAIN> type of {@link Persistable} the strategy is saving
 * @param <DTO> type of {@link Dto} the strategy is saving
 */
public interface SavingStrategy<DOMAIN extends Persistable, DTO extends Dto> {

	/**
	 * Populates the <code>savedObject</code> based on <code>importDtoObject</code> if <code>savedObject != null</code> otherwise creates new
	 * domain object and does the same. And saves this object to database through PersistenceSession.
	 * <p>
	 * Note: the <code>DomainAdapter</code> must be set before calling this method.
	 * 
	 * @param savedObject the object that exists in database for importDtoObject, it can be null if there is no entity for importDtoObject in
	 *            database
	 * @param importDtoObject the import dto object
	 * @return the merged object if it is merged, or the persisted object for save action also it can return null value if this object was not
	 *         imported because of the import strategies reasons
	 */
	DOMAIN populateAndSaveObject(DOMAIN savedObject, DTO importDtoObject);

	/**
	 * Saves the domain object to database through <code>PersistenceSession</code> using which must be set before calling this method.
	 * 
	 * @param importObject the object to save
	 * @return the merged object if it is merged, or the persisted object for save action
	 */
	DOMAIN saveDomainObject(DOMAIN importObject);
	
	/**
	 * Gets information about import of data transfer object concerned with  given object.
	 * 
	 * @param object the persistable object, it can be null
	 * @return true if data transfer object concerned with given object should be imported and false otherwise. 
	 */
	boolean isImportRequired(final Persistable object);

	/**
	 * Gets the saving manager.
	 * 
	 * @return the session
	 */
	SavingManager<DOMAIN> getSavingManager();

	/**
	 * Sets the saving manager.
	 * 
	 * @param session the session to set
	 */
	void setSavingManager(final SavingManager<DOMAIN> session);

	/**
	 * Gets the domainAdapter.
	 * 
	 * @return the domainAdapter
	 */
	DomainAdapter<DOMAIN, DTO> getDomainAdapter();

	/**
	 * Sets the domainAdapter.
	 * 
	 * @param domainAdapter the domainAdapter to set
	 */
	void setDomainAdapter(DomainAdapter<DOMAIN, DTO> domainAdapter);

	/**
	 * Gets the lifecycleListener.
	 * 
	 * @return the lifecycleListener
	 * @see LifecycleListener
	 */
	LifecycleListener getLifecycleListener();

	/**
	 * Sets the lifecycleListener.
	 * 
	 * @param lifecycleListener the lifecycleListener to set
	 * @see LifecycleListener
	 */
	void setLifecycleListener(final LifecycleListener lifecycleListener);
	
	/**
	 * Gets the collections strategy.
	 * 
	 * @return the collectionsStrategy
	 */
	CollectionsStrategy<DOMAIN, DTO> getCollectionsStrategy();
	
	/**
	 * Sets the collections strategy.
	 * 
	 * @param collectionsStrategy the collectionsStrategy to set
	 */
	void setCollectionsStrategy(CollectionsStrategy<DOMAIN, DTO> collectionsStrategy);
}
