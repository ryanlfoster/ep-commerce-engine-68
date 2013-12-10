package com.elasticpath.tools.sync.job.impl;

import static org.junit.Assert.assertEquals;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.tools.sync.job.SourceObjectCache;

/**
 *
 * Tests for the cache aware job entry impl.
 */
public class CacheAwareJobEntryImplTest {

    private static final String A_GUID = "aGuid";
    @Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
    private final SourceObjectCache cache = context.mock(SourceObjectCache.class);
    private final CacheAwareJobEntryImpl jobEntry = new CacheAwareJobEntryImpl(cache);

    /**
     * Test for happy path.
     */
    @Test
    public void testHappyPath() {

        final ProductImpl aProduct = new ProductImpl();
        jobEntry.setType(aProduct.getClass());
        jobEntry.setGuid(A_GUID);

        context.checking(new Expectations() {
            {
                oneOf(cache).retrieve(A_GUID, aProduct.getClass());
                will(returnValue(aProduct));
            }
        });
        final Persistable sourceObject = jobEntry.getSourceObject();
        assertEquals("persistent object should be a product", aProduct, sourceObject);

    }

    /**
     * Test that an exception will be thrown when source is set.
     */
    @Test (expected = IllegalArgumentException.class)
    public void testExceptionWhenSettingSource() {
        jobEntry.setSourceObject(null);

    }



}
