/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.impl;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.apache.openjpa.persistence.DataCache;

import com.elasticpath.domain.ListenableObject;

/**
 * Abstract implementation of a value domain object with property listener support.
 */
@MappedSuperclass
@SuppressWarnings("PMD.AbstractClassWithoutAbstractMethod")
@DataCache(enabled = false)
public abstract class AbstractListenableValueObjectImpl extends AbstractLegacyPersistenceImpl implements ListenableObject {

	private static final long serialVersionUID = -1939900551276831808L;

	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

	@Override
	public void addPropertyChangeListener(final PropertyChangeListener listener) {
		addPropertyChangeListener(listener, true);
	}
	
	@Override
	public void addPropertyChangeListener(final PropertyChangeListener listener, final boolean replace) {
		if (replace) {
			propertyChangeSupport.removePropertyChangeListener(listener);
		}
		propertyChangeSupport.addPropertyChangeListener(listener);
	}
	
	@Override
	public void addPropertyChangeListener(final String propertyName, final PropertyChangeListener listener) {
		addPropertyChangeListener(propertyName, listener, true);
	}

	@Override
	public void addPropertyChangeListener(final String propertyName, final PropertyChangeListener listener, final boolean replace) {
		if (replace) {
			propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
		}
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	@Override
	public void removePropertyChangeListener(final PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	@Override
	public void removePropertyChangeListener(final String propertyName, final PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
	}

	/**
	 * Notify listeners of a property change.
	 * 
	 * @param propertyName the name of the property that is being changed
	 * @param oldValue the old value
	 * @param newValue the new value
	 */
	protected void firePropertyChange(final String propertyName, final Object oldValue, final Object newValue) {
		propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
	}

	/**
	 * Get the property change support object.
	 * 
	 * @return the propertyChangeSupport
	 */
	@Transient
	public PropertyChangeSupport getPropertyChangeSupport() {
		return propertyChangeSupport;
	}

	/**
	 *	Set the property change support object.
	 *
	 * @param propertyChangeSupport the propertyChangeSupport to set
	 */
	public void setPropertyChangeSupport(final PropertyChangeSupport propertyChangeSupport) {
		this.propertyChangeSupport = propertyChangeSupport;
	}

}
