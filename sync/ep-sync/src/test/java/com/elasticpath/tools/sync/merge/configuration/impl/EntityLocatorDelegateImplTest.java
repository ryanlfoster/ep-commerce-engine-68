package com.elasticpath.tools.sync.merge.configuration.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.apache.lucene.util.cache.SimpleLRUCache;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.util.Pair;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.impl.BrandImpl;
import com.elasticpath.domain.catalog.impl.ProductBundleImpl;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.tools.sync.exception.SyncToolConfigurationException;
import com.elasticpath.tools.sync.merge.configuration.EntityLocator;
import com.elasticpath.tools.sync.merge.configuration.GuidLocator;

/**
 *
 * The junit test class for EntityLocatorDelegateImpl.
 *
 */
public class EntityLocatorDelegateImplTest {

	private static final String PRODUCT_BUNDLE_GUID = "productBundleGuid";

    @Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private EntityLocatorDelegateImpl entityLocatorDelegateImpl;

	private EntityLocator prodcutBundleLocator;

	private EntityLocator brandBundleLocator;


	/**
	 * SetUp method.
	 */
	@Before
	public void setUp() {
		entityLocatorDelegateImpl = new EntityLocatorDelegateImpl();
		prodcutBundleLocator = context.mock(EntityLocator.class, "prodcutBundleLocator");
		brandBundleLocator = context.mock(EntityLocator.class, "brandBundleLocator");
		entityLocatorDelegateImpl.setEntityLocators(Arrays.asList(prodcutBundleLocator, brandBundleLocator));
	}

	/**
	 * Test first entity locator for the object passed in.
	 */
	@Test
	public void testFirstEntityLocator() {
		context.checking(new Expectations() { {
			oneOf(prodcutBundleLocator).isResponsibleFor(ProductBundleImpl.class);
			will(returnValue(true));

			oneOf(prodcutBundleLocator).locatePersistence(PRODUCT_BUNDLE_GUID, ProductBundleImpl.class);
			will(returnValue(new ProductBundleImpl()));
		} });

		final Object object = entityLocatorDelegateImpl.locatePersistence(PRODUCT_BUNDLE_GUID, ProductBundleImpl.class);

		assertTrue("Return object is instance of ProductBundleImpl", (object instanceof ProductBundleImpl));
	}

	/**
	 * Test the next entity locator will be executed
	 * when the first entity locator does not fit for the object passed in.
	 */
	@Test
	public void testNextEntityLocatorInTheListWhenFirstOneDoesNotFit() {
		context.checking(new Expectations() { {
			oneOf(prodcutBundleLocator).isResponsibleFor(BrandImpl.class);
			will(returnValue(false));

			oneOf(brandBundleLocator).isResponsibleFor(BrandImpl.class);
			will(returnValue(true));

			oneOf(brandBundleLocator).locatePersistence("brandGuid", BrandImpl.class);
			will(returnValue(new BrandImpl()));
		} });

		final Object object = entityLocatorDelegateImpl.locatePersistence("brandGuid", BrandImpl.class);

		assertTrue("Return object is instance of BrandImpl", (object instanceof BrandImpl));
	}

	/**
	 * Test none entity locator fit for the object passed in.
	 */
	@Test (expected = SyncToolConfigurationException.class)
	public void testNoEntityLocatorFit() {
		context.checking(new Expectations() { {
			oneOf(prodcutBundleLocator).isResponsibleFor(Object.class);
			will(returnValue(false));

			oneOf(brandBundleLocator).isResponsibleFor(Object.class);
			will(returnValue(false));
		} });

		entityLocatorDelegateImpl.locatePersistence("Guid", Object.class);
	}

	/**
	 * Tests that entity exists gets delegated to the proper locators.
	 */
	@Test
	public void testExists() {

	    context.checking(new Expectations() { {
            oneOf(prodcutBundleLocator).isResponsibleFor(ProductBundleImpl.class);
            will(returnValue(true));

            oneOf(prodcutBundleLocator).entityExists(PRODUCT_BUNDLE_GUID, ProductBundleImpl.class);
            will(returnValue(true));
        } });

	    final boolean entityExists = entityLocatorDelegateImpl.entityExists(PRODUCT_BUNDLE_GUID, ProductBundleImpl.class);
	    assertTrue(" entity should exist", entityExists);

	}

	/**
	 * Tests that we call into the cache with the given key (guid,classs).
	 */
	@Test
	public void testCaching() {

	    final GuidLocator guidLocator = context.mock(GuidLocator.class);

	    entityLocatorDelegateImpl.setGuidLocator(guidLocator);
	    final SimpleLRUCache cache = new SimpleLRUCache(10);
	    entityLocatorDelegateImpl.setRefCache(cache);


	    final Product aProduct = new ProductImpl();
	    final Class<?> clazz = aProduct.getClass();
        final String guid = "guid";
        final Pair<String, Class<?>> key = new Pair<String, Class<?>>(guid, clazz);
        cache.put(key, aProduct);

        context.checking(new Expectations() {
            {
                oneOf(guidLocator).locateGuid(aProduct); will(returnValue(guid));
            }
        });
        final Persistable locatedPersistent = entityLocatorDelegateImpl.locatePersistentReference(aProduct);
        assertEquals("product not found from cache", aProduct, locatedPersistent);

	}

	 /**
     * Tests that a cache miss will both populate the cache, and get us an object.
     */
    @Test
    public void testCachingMiss() {

        final GuidLocator guidLocator = context.mock(GuidLocator.class);

        entityLocatorDelegateImpl.setGuidLocator(guidLocator);
        final SimpleLRUCache cache = new SimpleLRUCache(10);
        entityLocatorDelegateImpl.setRefCache(cache);


        final ProductBundle aBundle = new ProductBundleImpl();
        final Class<?> clazz = aBundle.getClass();
        final String guid = "guid";

        context.checking(new Expectations() {
            {
                oneOf(guidLocator).locateGuid(aBundle); will(returnValue(guid));

                oneOf(prodcutBundleLocator).isResponsibleFor(ProductBundleImpl.class);
                will(returnValue(true));
                oneOf(prodcutBundleLocator).locatePersistentReference(guid, clazz); will(returnValue(aBundle));
            }
        });
        final Persistable locatedPersistent = entityLocatorDelegateImpl.locatePersistentReference(aBundle);
        assertEquals("product not found from cache", aBundle, locatedPersistent);

    }
}
