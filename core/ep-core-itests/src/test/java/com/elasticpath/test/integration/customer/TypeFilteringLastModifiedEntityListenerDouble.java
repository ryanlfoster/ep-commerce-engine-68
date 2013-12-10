package com.elasticpath.test.integration.customer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.openjpa.enhance.PersistenceCapable;
import org.apache.openjpa.event.LifecycleEvent;

import com.elasticpath.domain.DatabaseLastModifiedDate;
import com.elasticpath.persistence.impl.LastModifiedEntityListener;

/**
 * A last modified entity listener that skips updating the last modified date of specified types when they are persisted.
 * This acts as a test double for the real LastModifiedEntityListener in cases where the last modified date of a type
 * needs to be manually specified.
*/
public class TypeFilteringLastModifiedEntityListenerDouble extends LastModifiedEntityListener {
	private final Set<Class<?>> ignoredTypes = new HashSet<Class<?>>();

	public TypeFilteringLastModifiedEntityListenerDouble(final Class<?>... ignoredTypes) {
		this.ignoredTypes.addAll(Arrays.asList(ignoredTypes));
	}

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

	/**
	 * Override last modified date listener on Customers so that we can modify dates manually to test date boundaries.
	 *
	 * @param event the lifecycle event
	 */
	private void setLastModifiedDate(final LifecycleEvent event) {
		Object source = event.getSource();

		for (Class<?> ignoredType : ignoredTypes) {
			if (ignoredType.isAssignableFrom(source.getClass())) {
				return;
			}
		}

		if (source instanceof DatabaseLastModifiedDate) {
			DatabaseLastModifiedDate lmdObject = (DatabaseLastModifiedDate) source;
			lmdObject.setLastModifiedDate(getTimeService().getCurrentTime());
		}
	}
}
