package com.elasticpath.service.misc.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.apache.openjpa.event.LoadListener;
import org.apache.openjpa.kernel.Broker;
import org.apache.openjpa.persistence.FetchPlan;
import org.apache.openjpa.persistence.JPAFacadeHelper;
import org.apache.openjpa.persistence.OpenJPAEntityManager;
import org.apache.openjpa.persistence.OpenJPAPersistence;

import com.elasticpath.domain.catalog.CategoryLoadTuner;
import com.elasticpath.persistence.api.AbstractEntityImpl;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;
import com.elasticpath.persistence.openjpa.JpaPersistenceSession;

/**
 * <p>Fetch plan helper that allows the registration of life cycle listeners
 * to perform additional actions during the OpenJPA object lifecycle, when a fetch plan
 * is being configured via a load tuner.</p>
 * 
 * <p>This class was originally created to work around what appears to be a bug in OpenJPA 1.0.1, 
 * where parent and master categories of linked categories were not loaded regardless of what
 * was specified in the FetchGroup. The intent was that a postLoad listener would be configured
 * to load the appropriate parents whenever a linked category was loaded and its parent or 
 * master categories were supposed to be loaded. However, this implementation is generic
 * enough that listeners could be added for any other classes as well.</p>
 * 
 * <p><em>NOTE: Post-load callback (i.e. listeners are only notified) for a named fetch group
 * if the annotation specifies postLoad=true. This is false by default EXCEPT for the default
 * fetch group (which is what a fetch plan uses).</em></p>
 * 
 * @deprecated As of EP 6.2.2. Use {@link com.elasticpath.persistence.openjpa.PersistenceEngineEntityListener}
 * for persistence listening.
 */
@Deprecated
public class OpenJPAEventListeningFetchPlanHelperImpl extends OpenJPAFetchPlanHelperImpl {
	
	private Map <LoadListener, Class< ? extends AbstractEntityImpl>[] > loadListeners; 

	/**
	 * Configure the Category fetch plan based on the given tuner.
	 * @param loadTuner the load tuner
	 */
	@Override
	public void configureCategoryFetchPlan(final CategoryLoadTuner loadTuner) {
		FetchPlan fetchPlan = getFetchPlan();
		if (fetchPlan == null || loadTuner == null) {
			return;
		}
		super.configureCategoryFetchPlan(loadTuner);
		registerListeners();
	}
	
	@Override
	public void configureFetchGroupLoadTuner(final FetchGroupLoadTuner groupLoadTuner, final boolean cleanExistingGroups) {		
		super.configureFetchGroupLoadTuner(groupLoadTuner, cleanExistingGroups);
		registerListeners();		
	}
	
	/**
	 * Clear the fetch plan configuration.
	 */
	@Override
	public void clearFetchPlan() {
		super.clearFetchPlan();
		unregisterListeners();
	}

	
	/**
	 * 
	 *  Register all configured listeners.
	 */
	private void registerListeners() {
		for (Map.Entry<LoadListener, Class< ? extends AbstractEntityImpl>[]> entry : loadListeners.entrySet()) {
			registerListener(entry.getKey(), entry.getValue());
		}
	}
	
	/**
	 *  Unregister all configured listeners.
	 */
	private void unregisterListeners() {
		for (Map.Entry<LoadListener, Class< ? extends AbstractEntityImpl>[]> entry : loadListeners.entrySet()) {
			unregisterListener(entry.getKey());
		}
	}
	
	
	/**
	 * Get the EntityManager.
	 * @return instance of EntityManager
	 */
	protected EntityManager getEntityManager() {
		return ((JpaPersistenceSession) getPersistenceEngine().getSharedPersistenceSession()).getEntityManager();
	}
	
	/**
	 * Register the listener for clazz entity life cycle.
	 * @param entitys the entity list.  
	 * @param listener life cycle listener.
	 * 
	 */
	protected void registerListener(final LoadListener listener, final Class< ? extends AbstractEntityImpl>[] entitys) {
		OpenJPAEntityManager openjpaEM = OpenJPAPersistence.cast(getEntityManager());
		Broker broker = JPAFacadeHelper.toBroker(openjpaEM);				 
		broker.addLifecycleListener(
				listener, 
				entitys);
	}
	
	/**
	 * Unregister the listener for entity life cycle. 
	 * @param listener LifecycleListener
	 */
	protected void unregisterListener(final LoadListener listener) {
		OpenJPAEntityManager openjpaEM = OpenJPAPersistence.cast(getEntityManager());
		Broker broker = JPAFacadeHelper.toBroker(openjpaEM);
		broker.removeLifecycleListener(listener);			
	}
	
	
	/**
	 * Set the listeners.
	 * @param loadListeners load listeners for registration
	 */
	public void setLoadListeners(final Map<LoadListener, List<Class < ? extends AbstractEntityImpl>>> loadListeners) {
		this.loadListeners = new HashMap<LoadListener, Class<? extends AbstractEntityImpl>[]>(loadListeners.size());
		for (Map.Entry<LoadListener, List<Class<? extends AbstractEntityImpl>>> entry : loadListeners.entrySet()) {
			List<Class<? extends AbstractEntityImpl>> classList = entry.getValue();

			@SuppressWarnings({"rawtypes", "unchecked"})
			final Class<? extends AbstractEntityImpl>[] classes = classList.toArray(new Class[classList.size()]);

			this.loadListeners.put(entry.getKey(), classes);
		}
	}
	

}
