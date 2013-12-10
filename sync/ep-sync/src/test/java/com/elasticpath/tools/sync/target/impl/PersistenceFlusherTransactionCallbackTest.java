package com.elasticpath.tools.sync.target.impl;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.pricing.BaseAmount;
import com.elasticpath.persistence.api.PersistenceEngine;

/**
 * Tests for {@link PersistenceFlusherTransactionCallback}.
 */
public class PersistenceFlusherTransactionCallbackTest {
    @Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
    
    private PersistenceFlusherTransactionCallback listener;
    private PersistenceEngine persistenceEngine;

    /**
     * Sets up a test.
     */
    @Before
    public void setUp() {
    	persistenceEngine = context.mock(PersistenceEngine.class);
        listener = new PersistenceFlusherTransactionCallback();
        listener.setPersistenceEngine(persistenceEngine);
    }

    /**
     * Test object not changed.
     */
    @Test
    public void testObjectTypeNotChanged() {
    	final BaseAmount targetPersistence = context.mock(BaseAmount.class);
    	
        context.checking(new Expectations() {
            {
                oneOf(persistenceEngine).flush();
            }
        });

        listener.preUpdateJobEntryHook(null, targetPersistence);
        listener.preUpdateJobEntryHook(null, targetPersistence);
    }

    /**
     * Test object changed.
     */
    @Test
    public void testObjectTypeChanged() {
    	final BaseAmount targetPersistence = context.mock(BaseAmount.class);
    	final Product targetPersistence2 = context.mock(Product.class);
    	
        context.checking(new Expectations() {
            {
                exactly(2).of(persistenceEngine).flush();
            }
        });

        listener.preUpdateJobEntryHook(null, targetPersistence);
        listener.preUpdateJobEntryHook(null, targetPersistence2);
    }
}
