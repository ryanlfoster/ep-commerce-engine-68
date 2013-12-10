package com.elasticpath.domain.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.junit.Test;

/**
 * Tests adding property listeners to {@link AbstractListenableValueObjectImpl}.
 */
public class AbstractListenableValueObjectImplTest {
	
	private static final String PROPERTY_NAME = "moo";

	/**
	 * The ConcreteAbstractListenableValueObjectImpl used for testing.
	 */
	private static class ConcreteAbstractListenableValueObjectImpl extends AbstractListenableValueObjectImpl {
		private static final long serialVersionUID = -3519194238184024466L;

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
		
		ConcreteAbstractListenableValueObjectImpl listenableValueObject = new ConcreteAbstractListenableValueObjectImpl();
		
		assertEquals("Initial list of property listeners should be empty.", // NOPMD
				0, listenableValueObject.getPropertyChangeSupport().getPropertyChangeListeners().length);

		SimplePropertyListener listener1 = new SimplePropertyListener();
		listenableValueObject.addPropertyChangeListener(listener1);

		assertEquals("List of property listeners should contain one entry.", // NOPMD
				1, listenableValueObject.getPropertyChangeSupport().getPropertyChangeListeners().length);
		assertSame("Property listener should be the same!",
				listener1, listenableValueObject.getPropertyChangeSupport().getPropertyChangeListeners()[0]);
		
		// Add same listener again. Should replace.
		listenableValueObject.addPropertyChangeListener(listener1);
		assertEquals("List of property listeners should still contain only one entry.",
				1, listenableValueObject.getPropertyChangeSupport().getPropertyChangeListeners().length);
		assertSame("Property listener should be the same!",
				listener1, listenableValueObject.getPropertyChangeSupport().getPropertyChangeListeners()[0]);
	}

	/**
	 * Tests adding a property listener without replacing.
	 */
	@Test
	public void testAddPropertyListenerWithoutReplace() {
		
		ConcreteAbstractListenableValueObjectImpl listenableValueObject = new ConcreteAbstractListenableValueObjectImpl();
		
		assertEquals("Initial list of property listeners should be empty.",
				0, listenableValueObject.getPropertyChangeSupport().getPropertyChangeListeners().length);

		SimplePropertyListener listener1 = new SimplePropertyListener();
		listenableValueObject.addPropertyChangeListener(listener1);
		
		assertEquals("List of property listeners should contain one entry.",
				1, listenableValueObject.getPropertyChangeSupport().getPropertyChangeListeners().length);
		assertSame("Property listener should be the same!",
				listener1, listenableValueObject.getPropertyChangeSupport().getPropertyChangeListeners()[0]);
		
		// Add same listener again. Should not replace.
		listenableValueObject.addPropertyChangeListener(listener1, false);

		assertEquals("List of property listeners should contain two entries.",
				2, listenableValueObject.getPropertyChangeSupport().getPropertyChangeListeners().length);
		assertSame("First property listener should be the same!",
				listener1, listenableValueObject.getPropertyChangeSupport().getPropertyChangeListeners()[0]);
		assertSame("Second property listener should be the same!",
				listener1, listenableValueObject.getPropertyChangeSupport().getPropertyChangeListeners()[1]);
	}

	/**
	 * Tests adding a property listener for a specific property.
	 */
	@Test
	public void testAddPropertyListenerWithPropertyNameAndReplace() {
		
		ConcreteAbstractListenableValueObjectImpl listenableValueObject = new ConcreteAbstractListenableValueObjectImpl();
		
		assertEquals("Initial list of property listeners should be empty.", 
				0, listenableValueObject.getPropertyChangeSupport().getPropertyChangeListeners().length);

		SimplePropertyListener listener1 = new SimplePropertyListener();
		listenableValueObject.addPropertyChangeListener(PROPERTY_NAME, listener1);
		assertEquals("List of property listeners should contain one entry.",
				1, listenableValueObject.getPropertyChangeSupport().getPropertyChangeListeners().length);
		
		// Add same listener again. Should replace.
		listenableValueObject.addPropertyChangeListener(PROPERTY_NAME, listener1);
		assertEquals("List of property listeners should still contain only one entry.",
				1, listenableValueObject.getPropertyChangeSupport().getPropertyChangeListeners().length);
	}

	/**
	 * Tests adding a property listener for a specific property without replacing.
	 */
	@Test
	public void testAddPropertyListenerWithPropertyNameWithoutReplace() {
		
		ConcreteAbstractListenableValueObjectImpl listenableValueObject = new ConcreteAbstractListenableValueObjectImpl();
		
		assertEquals("Initial list of property listeners should be empty.", 
				0, listenableValueObject.getPropertyChangeSupport().getPropertyChangeListeners().length);

		SimplePropertyListener listener1 = new SimplePropertyListener();
		listenableValueObject.addPropertyChangeListener(PROPERTY_NAME, listener1);
		assertEquals("List of property listeners should contain one entry.",
				1, listenableValueObject.getPropertyChangeSupport().getPropertyChangeListeners().length);
		
		// Add same listener again. Should not replace.
		listenableValueObject.addPropertyChangeListener(PROPERTY_NAME, listener1, false);
		assertEquals("List of property listeners should contain two entries.",
				2, listenableValueObject.getPropertyChangeSupport().getPropertyChangeListeners().length);
	}
	
}
