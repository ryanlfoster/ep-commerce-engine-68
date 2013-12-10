package com.elasticpath.persistence.impl;

import com.elasticpath.persistence.openjpa.impl.AbstractPersistenceEngineEntityListener;
import org.apache.openjpa.enhance.PersistenceCapable;
import org.apache.openjpa.event.LifecycleEvent;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.DatabaseLastModifiedDate;
import com.elasticpath.service.misc.TimeService;

/**
 * EntityListener which sets the {@code lastModifiedDate} on entities.
 */
public class LastModifiedEntityListener extends AbstractPersistenceEngineEntityListener {
	
	private TimeService timeService;
	
	private BeanFactory beanFactory;
	
	@Override
	public void eventOccurred(final LifecycleEvent event) {
		switch(event.getType()) {
		case LifecycleEvent.BEFORE_ATTACH:
			PersistenceCapable pcObject = (PersistenceCapable) event.getSource();
			if (pcObject.pcIsDirty()) {
				setLastModifiedDate(event);
			}
			break;
		case LifecycleEvent.BEFORE_PERSIST:
			setLastModifiedDate(event);
			break;
		default:
			// No - op
		}
	}

	private void setLastModifiedDate(final LifecycleEvent event) {
		Object source = event.getSource();
		if (source instanceof DatabaseLastModifiedDate) {
			DatabaseLastModifiedDate lmdObject = (DatabaseLastModifiedDate) source;
			lmdObject.setLastModifiedDate(getTimeService().getCurrentTime());
		}
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
