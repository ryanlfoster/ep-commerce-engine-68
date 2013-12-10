package com.elasticpath.tools.sync.target.impl;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.openjpa.persistence.OpenJPAEntityManager;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.pricing.BaseAmount;
import com.elasticpath.domain.pricing.impl.BaseAmountImpl;
import com.elasticpath.persistence.openjpa.JpaPersistenceEngine;

/**
 * Test for the persistence changed listener.
 */
public class PersistenceChangesTransactionCallbackListenerTest {


    @Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
    private PersistenceChangesTransactionCallbackListener listener;
    private final JpaPersistenceEngine persistenceEngine = context.mock(JpaPersistenceEngine.class);

    /**
     * setup code.
     */
    @Before
    public void setUp() {
        listener = new PersistenceChangesTransactionCallbackListener();
        listener.setPersistenceEngine(persistenceEngine);
        final Collection<Class<?>> listOfClassesToIgnore = new ArrayList<Class<?>>();
        listOfClassesToIgnore.add(BaseAmount.class);
        listener.setClassesToIgnore(listOfClassesToIgnore);
    }

    /**
     * Tests Happy Path.
     */
    @Test
    public void testHappyPath() {
        final BaseAmount targetPersistence = context.mock(BaseAmount.class);

        final OpenJPAEntityManager openJpaEM = context.mock(OpenJPAEntityManager.class);
        context.checking(new Expectations() {
            {
                oneOf(persistenceEngine).getEntityManager(); will(returnValue(openJpaEM));
                oneOf(openJpaEM).setIgnoreChanges(true);
            }
        });

        listener.preUpdateJobEntryHook(null, targetPersistence);
    }


    /**
     * Tests that a persistence implementing the class to ignore will still set it to true.
     */
    @Test
    public void testJustImpl() {
        final BaseAmountImpl targetPersistence = new BaseAmountImpl();

        final OpenJPAEntityManager openJpaEM = context.mock(OpenJPAEntityManager.class);
        context.checking(new Expectations() {
            {
                oneOf(persistenceEngine).getEntityManager(); will(returnValue(openJpaEM));
                oneOf(openJpaEM).setIgnoreChanges(true);
            }
        });

        listener.preUpdateJobEntryHook(null, targetPersistence);
    }


    /**
     * Tests that a product will set the ignore chantes to false.
     */
    @Test
    public void testWrongClass() {
        final Product targetPersistence = new ProductImpl();

        final OpenJPAEntityManager openJpaEM = context.mock(OpenJPAEntityManager.class);
        context.checking(new Expectations() {
            {
                oneOf(persistenceEngine).getEntityManager(); will(returnValue(openJpaEM));
                oneOf(openJpaEM).setIgnoreChanges(false);
            }
        });

        listener.preUpdateJobEntryHook(null, targetPersistence);
    }
}
