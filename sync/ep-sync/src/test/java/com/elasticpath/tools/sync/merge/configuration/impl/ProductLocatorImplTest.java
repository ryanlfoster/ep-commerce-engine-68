package com.elasticpath.tools.sync.merge.configuration.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.service.catalog.ProductService;
import com.elasticpath.tools.sync.merge.configuration.EntityLocator;

/**
*
* The junit test class for ProductLocatorImpl.
*
*/
public class ProductLocatorImplTest {


    private static final String INVALID_GUID = "invalidGuid";
    private static final String VALID_GUID = "validGuid";
    @Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
    private EntityLocator productLocator;
    private final ProductService productService = context.mock(ProductService.class);

    /**
     * Setup the tests.
     */
    @Before
    public void setUp() {
        productLocator = new ProductLocatorImpl();
        ((ProductLocatorImpl) productLocator).setProductService(productService);
    }

    /**
     * Tests the entity exists method.
     */
    @Test
    public void testEntityExists() {

        context.checking(new Expectations() {
            {
                oneOf(productService).guidExists(VALID_GUID); will(returnValue(true));
                oneOf(productService).guidExists(INVALID_GUID); will(returnValue(false));
            }
        });
        boolean result = productLocator.entityExists(VALID_GUID, null);
        assertTrue("product  should exists", result);
        result = productLocator.entityExists(INVALID_GUID, null);
        assertFalse("product  should not exists", result);

    }

}
