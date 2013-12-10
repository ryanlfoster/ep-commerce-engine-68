package com.elasticpath.domain.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.junit.Test;

/**
 * Tests adding property listeners to AbstractListenableEntityImpl.
 */
public class AbstractListenableEntityImplTest {
	
	private static final String PROPERTY_NAME = "moo";

	/**
	 * The Class ConcreteAbstractListenableEntityImpl.
	 */
	private static class ConcreteAbstractListenableEntityImpl extends AbstractListenableEntityImpl {
		private static final long serialVersionUID = -2340647138872117505L;

		/** The uid pk. */
		private long uidPk;

		@Override
		public long getUidPk() {
			return uidPk;
		}

		@Override
		public void setUidPk(final long uidPk) {
			this.uidPk = uidPk;
		}

		@Override
		public String getGuid() {
			return null;
		}

		@Override
		public void setGuid(final String guid) {
			// stub
		}
	}
	
	/**
	 * Simple implementation of a PropertyChangeListener.
	 */
	private static class SimplePropertyListener implements PropertyChangeListener {

		@Override
		public void propertyChange(final PropertyChangeEvent evt) {
			// Do nothing.
		}
		
	}


	/**
	 * Tests adding a property listener.
	 */
	@Test
	public void testAddPropertyListenerWithReplace() {
		
		ConcreteAbstractListenableEntityImpl listenableEntity = new ConcreteAbstractListenableEntityImpl();
		
		assertEquals("Initial list of property listeners should be empty.", // NOPMD
				0, listenableEntity.getPropertyChangeSupport().getPropertyChangeListeners().length);

		SimplePropertyListener listener1 = new SimplePropertyListener();
		listenableEntity.addPropertyChangeListener(listener1);
		
		assertEquals("List of property listeners should contain one entry.", // NOPMD
				1, listenableEntity.getPropertyChangeSupport().getPropertyChangeListeners().length);
		assertSame("Property listener should be the same!",
				listener1, listenableEntity.getPropertyChangeSupport().getPropertyChangeListeners()[0]);
		
		// Add same listener again. Should replace.
		listenableEntity.addPropertyChangeListener(listener1);
		assertEquals("List of property listeners should still contain only one entry.",
				1, listenableEntity.getPropertyChangeSupport().getPropertyChangeListeners().length);
		assertSame("Property listener should be the same!",
				listener1, listenableEntity.getPropertyChangeSupport().getPropertyChangeListeners()[0]);
	}

	/**
	 * Tests adding a property listener without replacing.
	 */
	@Test
	public void testAddPropertyListenerWithoutReplace() {
		
		ConcreteAbstractListenableEntityImpl listenableEntity = new ConcreteAbstractListenableEntityImpl();
		
		assertEquals("Initial list of property listeners should be empty.",
				0, listenableEntity.getPropertyChangeSupport().getPropertyChangeListeners().length);

		SimplePropertyListener listener1 = new SimplePropertyListener();
		listenableEntity.addPropertyChangeListener(listener1);
		
		assertEquals("List of property listeners should contain one entry.",
				1, listenableEntity.getPropertyChangeSupport().getPropertyChangeListeners().length);
		assertSame("Property listener should be the same!",
				listener1, listenableEntity.getPropertyChangeSupport().getPropertyChangeListeners()[0]);
		
		// Add same listener again. Should not replace.
		listenableEntity.addPropertyChangeListener(listener1, false);
		assertEquals("List of property listeners should contain two entries.",
				2, listenableEntity.getPropertyChangeSupport().getPropertyChangeListeners().length);
		assertSame("First property listener should be the same!",
				listener1, listenableEntity.getPropertyChangeSupport().getPropertyChangeListeners()[0]);
		assertSame("Second property listener should be the same!",
				listener1, listenableEntity.getPropertyChangeSupport().getPropertyChangeListeners()[1]);
	}

	/**
	 * Tests adding a property listener for a specific property.
	 */
	@Test
	public void testAddPropertyListenerWithPropertyNameAndReplace() {
		
		ConcreteAbstractListenableEntityImpl listenableEntity = new ConcreteAbstractListenableEntityImpl();
		
		assertEquals("Initial list of property listeners should be empty.",
				0, listenableEntity.getPropertyChangeSupport().getPropertyChangeListeners().length);

		SimplePropertyListener listener1 = new SimplePropertyListener();
		listenableEntity.addPropertyChangeListener(PROPERTY_NAME, listener1);
		assertEquals("List of property listeners should contain one entry.",
				1, listenableEntity.getPropertyChangeSupport().getPropertyChangeListeners().length);
		
		// Add same listener again. Should replace.
		listenableEntity.addPropertyChangeListener(PROPERTY_NAME, listener1);
		assertEquals("List of property listeners should still contain only one entry.",
				1, listenableEntity.getPropertyChangeSupport().getPropertyChangeListeners().length);
	}

	/**
	 * Tests adding a property listener for a specific property without replacing.
	 */
	@Test
	public void testAddPropertyListenerWithPropertyNameWithoutReplace() {
		
		ConcreteAbstractListenableEntityImpl listenableEntity = new ConcreteAbstractListenableEntityImpl();
		
		assertEquals("Initial list of property listeners should be empty.",
				0, listenableEntity.getPropertyChangeSupport().getPropertyChangeListeners().length);

		SimplePropertyListener listener1 = new SimplePropertyListener();
		listenableEntity.addPropertyChangeListener(PROPERTY_NAME, listener1);
		assertEquals("List of property listeners should contain one entry.", 
				1, listenableEntity.getPropertyChangeSupport().getPropertyChangeListeners().length);
		
		// Add same listener again. Should not replace.
		listenableEntity.addPropertyChangeListener(PROPERTY_NAME, listener1, false);
		assertEquals("List of property listeners should contain two entries.",
				2, listenableEntity.getPropertyChangeSupport().getPropertyChangeListeners().length);
	}
	
}
