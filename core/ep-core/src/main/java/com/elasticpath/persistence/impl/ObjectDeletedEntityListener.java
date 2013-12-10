package com.elasticpath.persistence.impl;

import com.elasticpath.persistence.openjpa.impl.AbstractPersistenceEngineEntityListener;
import org.apache.openjpa.enhance.PersistenceCapable;
import org.apache.openjpa.event.LifecycleEvent;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.search.ObjectDeleted;
import com.elasticpath.domain.search.impl.ObjectDeletedImpl;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.service.misc.TimeService;

/**
 * EntityListener which sets the {@code lastModifiedDate} on entities.
 */
public class ObjectDeletedEntityListener extends AbstractPersistenceEngineEntityListener {
	
	private TimeService timeService;
	
	private BeanFactory beanFactory;
	
	@Override
	public void eventOccurred(final LifecycleEvent event) {
		switch(event.getType()) {
		case LifecycleEvent.BEFORE_DELETE:
			PersistenceCapable pcObject = (PersistenceCapable) event.getSource();
			if (pcObject instanceof ProductSku) {
				ObjectDeleted objectDeleted = new ObjectDeletedImpl();
				objectDeleted.setObjectType(ObjectDeleted.OBJECT_DELETED_TYPE_SKU);
				objectDeleted.setObjectUid(((ProductSku) pcObject).getUidPk());
				objectDeleted.setDeletedDate(getTimeService().getCurrentTime());
				
				getPersistenceEngine().save(objectDeleted);
			}
			break;
		default:
			// No - op
		}
	}
	
	private PersistenceEngine getPersistenceEngine() {
		return beanFactory.getBean(ContextIdNames.PERSISTENCE_ENGINE);
	}

	/**
	 * Get the time service.
	 * 
	 * @return the time service.
	 */
	protected TimeService getTimeService() {
		if (timeService == null) {
			timeService = beanFactory.getBean(ContextIdNames.TIME_SERVICE);
		}
		return timeService;
	}
	
	/**
	 * 
	 * @param beanFactory The bean factory.
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory; 
	}
}
