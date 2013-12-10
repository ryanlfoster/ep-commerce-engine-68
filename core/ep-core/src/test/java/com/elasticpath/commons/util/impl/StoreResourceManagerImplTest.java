/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.commons.util.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.Sequence;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.Log;
import org.apache.velocity.runtime.parser.node.SimpleNode;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.ResourceManager;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;

import com.elasticpath.commons.util.AssetRepository;
import com.elasticpath.service.catalogview.StoreConfig;
import com.elasticpath.settings.domain.SettingValue;

/**
 * Test for {@link StoreResourceManagerImpl}.
 */
public class StoreResourceManagerImplTest {
	private static final String THEME1 = "theme1";

	private static final String NO_EXCEPTION_EXPECTED = "No exception should have been thrown";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery() {
		{
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};

	// this is the instance that spring creates with storeCode = ""
	private static final StoreResourceManagerImpl MAIN_INSTANCE_STORE_RESOURCE_MANAGER = 
													(StoreResourceManagerImpl) StoreResourceManagerImpl.getInstance();
	private StoreResourceManagerImpl storeResourceManagerForTestStore;

	private static final String CMASSETS = "cmassets";

	private static final String TESTSTORE = "TESTSTORE";

	private static final String TEST_VM = "test.vm";

	private static final String GLOBAL_VM = "email" + File.separator + "global1.vm";

	private static final String RESOURCE_SHOULD_HAVE_BEEN_FOUND = "Resource should have been found";

	private static final String RESOURCE_SHOULD_NOT_BE_NULL = "Resource should not be null";

	private static final String RESOURCE_SHOULD_BE_NULL = "Resource should be null";

	private static final String TESTSTORE_TEMPLATES_TEST_VM = THEME1 + File.separator + TESTSTORE + File.separator + "templates" + File.separator
			+ "velocity" + File.separator + TEST_VM;

	private static final String DEFAULT_TEMPLATES_TEST_VM = THEME1 + File.separator + "default" + File.separator + "templates" + File.separator
			+ "velocity" + File.separator + TEST_VM;

	private static final String GLOBAL_TEMPLATES_TEST_VM = CMASSETS + File.separator + "templates" + File.separator + "velocity" + File.separator
			+ GLOBAL_VM;

	private static final String UTF_8 = "UTF-8";

	private static final List<String> RESOURCE_NAMES = new ArrayList<String>();
	static {
		RESOURCE_NAMES.add(DEFAULT_TEMPLATES_TEST_VM);
		RESOURCE_NAMES.add(TESTSTORE_TEMPLATES_TEST_VM);
		RESOURCE_NAMES.add(TEST_VM);
	}

	private static final ResourceLoader STRING_RESOURCE_LOADER = new StringResourceLoader();

	/**
	 * @throws Exception if something goes wrong
	 */
	@Before
	public void setUp() throws Exception {
		final List<String> resourceLoaderNames = new ArrayList<String>();
		final String loaderName = "testResourceLoader";
		resourceLoaderNames.add(loaderName);

		final ExtendedProperties runtimeConfiguration = new ExtendedProperties();
		runtimeConfiguration.addProperty(RuntimeConstants.RESOURCE_LOADER, resourceLoaderNames);

		final StringBuffer loaderID = new StringBuffer(loaderName);
		loaderID.append('.').append(RuntimeConstants.RESOURCE_LOADER);
		runtimeConfiguration.addProperty(loaderID.toString() + ".instance", STRING_RESOURCE_LOADER);

		final RuntimeServices rsvc = context.mock(RuntimeServices.class);
		// create a threadlocal
		final StoreConfig mockStoreConfig = context.mock(StoreConfig.class);
		context.checking(new Expectations() {
			{
				allowing(rsvc).getConfiguration();
				will(returnValue(runtimeConfiguration));

				allowing(rsvc).getLog();
				will(returnValue(new Log()));

				allowing(rsvc).getBoolean(with(any(String.class)), with(any(Boolean.class)));
				will(returnValue(true));

				allowing(rsvc).getString(with(any(String.class)));
				will(returnValue(null));

				allowing(rsvc).getInt(with(any(String.class)), with(any(Integer.class)));
				will(returnValue(0));

				allowing(rsvc).parse(with(any(Reader.class)), with(any(String.class)));
				will(returnValue(new SimpleNode(0)));
				allowing(mockStoreConfig).getStoreCode();
				will(returnValue(TESTSTORE));
			}
		});

		// attach a storeConfig to our instance
		MAIN_INSTANCE_STORE_RESOURCE_MANAGER.setStoreConfig(mockStoreConfig);
		//create an instance in our map for the store "TESTSTORE"
		storeResourceManagerForTestStore = (StoreResourceManagerImpl) StoreResourceManagerImpl.getInstance();
		storeResourceManagerForTestStore.initialize(rsvc);
	}

	/**
	 * Cleans up some of the objects that need to be static so that one test doesn't influence another.
	 */
	@After
	public void tearDown() {
		// Need to clean up resource listings in repository between tests since it is static and we don't want one test to affect another.
		final StringResourceRepository repo = StringResourceLoader.getRepository();
		for (final String resourceName : RESOURCE_NAMES) {
			repo.removeStringResource(resourceName);
		}
		MAIN_INSTANCE_STORE_RESOURCE_MANAGER.invalidate();
	}

	/**
	 * Tests that the resource is successfully retrieved when calling
	 * {@link StoreResourceManagerImpl#getResourceFromLastResolvedLocation(String, int, String)} for a resource that already exists in the resource
	 * lookup cache.
	 */
	@Test
	public void testGetResourceFromLastResolvedLocation() {
		final Resource testStoreResource = context.mock(Resource.class);
		context.checking(new Expectations() {
			{
				oneOf(testStoreResource).getName();
				will(returnValue(TESTSTORE_TEMPLATES_TEST_VM));
			}
		});
		storeResourceManagerForTestStore.getResourceLookupCache().put(TEST_VM, testStoreResource);

		final StringResourceRepository repo = StringResourceLoader.getRepository();
		repo.putStringResource(TESTSTORE_TEMPLATES_TEST_VM, "");
		try {
			Resource resource = storeResourceManagerForTestStore
					.getResourceFromLastResolvedLocation(TEST_VM, ResourceManager.RESOURCE_TEMPLATE, UTF_8);
			assertNotNull(RESOURCE_SHOULD_NOT_BE_NULL, resource);
		} catch (Exception e) {
			fail(RESOURCE_SHOULD_HAVE_BEEN_FOUND);
		}
	}

	/**
	 * Tests that the resource is not retrieved when calling
	 * {@link StoreResourceManagerImpl#getResourceFromLastResolvedLocation(String, int, String)} for a resource that does not exist in the resource
	 * lookup cache.
	 */
	@Test
	public void testGetResourceFromLastResolvedLocationWhenNotYetLoaded() {
		try {
			Resource resource = storeResourceManagerForTestStore.getResourceFromLastResolvedLocation(TEST_VM, 
																							ResourceManager.RESOURCE_TEMPLATE, UTF_8);
			assertNull(RESOURCE_SHOULD_BE_NULL, resource);
		} catch (Exception e) {
			fail(NO_EXCEPTION_EXPECTED);
		}
	}

	/**
	 * Tests that the resource is not retrieved when calling
	 * {@link StoreResourceManagerImpl#getResourceFromLastResolvedLocation(String, int, String)} for a resource that exists in the resource lookup
	 * cache, but can no longer be found by the resource loaders. Also verify that the entry is then removed from the lookup cache.
	 */
	@Test
	public void testGetResourceFromLastResolvedLocationWhenNoLongerThere() {
		final Resource testStoreResource = context.mock(Resource.class);
		context.checking(new Expectations() {
			{
				oneOf(testStoreResource).getName();
				will(returnValue(TESTSTORE_TEMPLATES_TEST_VM));
			}
		});
		storeResourceManagerForTestStore.getResourceLookupCache().put(TEST_VM, testStoreResource);

		try {
			Resource resource = storeResourceManagerForTestStore.getResourceFromLastResolvedLocation(TEST_VM, 
																					ResourceManager.RESOURCE_TEMPLATE, UTF_8);
			assertNull(RESOURCE_SHOULD_BE_NULL, resource);
			assertNull("Resource should have been removed from the cache", storeResourceManagerForTestStore.getResourceLookupCache().get(TEST_VM,
					ResourceManager.RESOURCE_TEMPLATE, UTF_8));
		} catch (Exception e) {
			fail(RESOURCE_SHOULD_HAVE_BEEN_FOUND);
		}
	}

	/**
	 * Tests that a store-specific resource is successfully retrieved when calling
	 * {@link StoreResourceManagerImpl#getResourceStoreSpecific(java.lang.String, int, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testGetResourceStoreSpecific() {
		final StringResourceRepository repo = StringResourceLoader.getRepository();
		repo.putStringResource(TESTSTORE_TEMPLATES_TEST_VM, "");
		try {
			Resource resource = storeResourceManagerForTestStore.getResourceStoreSpecific(TEST_VM, ResourceManager.RESOURCE_TEMPLATE, UTF_8, THEME1);
			assertNotNull(RESOURCE_SHOULD_NOT_BE_NULL, resource);
		} catch (Exception e) {
			fail(NO_EXCEPTION_EXPECTED);
		}
	}

	/**
	 * Tests that a resource is successfully retrieved from the theme's default directory when calling
	 * {@link StoreResourceManagerImpl#getResourceFromThemeDefault(java.lang.String, int, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testGetResourceFromThemeDefault() {
		final StringResourceRepository repo = StringResourceLoader.getRepository();
		repo.putStringResource(DEFAULT_TEMPLATES_TEST_VM, "");
		try {
			Resource resource = storeResourceManagerForTestStore.getResourceFromThemeDefault(TEST_VM, 
																			ResourceManager.RESOURCE_TEMPLATE, UTF_8, THEME1);
			assertNotNull(RESOURCE_SHOULD_NOT_BE_NULL, resource);
		} catch (Exception e) {
			fail(NO_EXCEPTION_EXPECTED);
		}
	}

	/**
	 * Tests that the various methods for resolving a store resource get called in the correct order when calling
	 * {@link StoreResourceManagerImpl#getResource(String, int, String)}.
	 */
	@Test
	public void testGetResourceFallbackLogicForStoreResource() {
		final StoreResourceManagerImpl mockStoreResourceManager = context.mock(StoreResourceManagerImpl.class);
		final Sequence fallbackSequence = context.sequence("fallback-sequence");
		final SettingValue themeSettingValue = context.mock(SettingValue.class);
		final StoreConfig storeConfig = context.mock(StoreConfig.class, "willFallback");

		try {
			context.checking(new Expectations() {
				{
					oneOf(mockStoreResourceManager).getResourceFromLastResolvedLocation(TEST_VM, ResourceManager.RESOURCE_TEMPLATE, UTF_8);
					inSequence(fallbackSequence);
					will(returnValue(null));

					oneOf(mockStoreResourceManager).getResourceStoreSpecific(TEST_VM, ResourceManager.RESOURCE_TEMPLATE, UTF_8, THEME1);
					inSequence(fallbackSequence);
					will(returnValue(null));

					oneOf(mockStoreResourceManager).getResourceFromThemeDefault(TEST_VM, ResourceManager.RESOURCE_TEMPLATE, UTF_8, THEME1);
					inSequence(fallbackSequence);
					will(returnValue(null));

					oneOf(mockStoreResourceManager).getResourceFromParent(TEST_VM, ResourceManager.RESOURCE_TEMPLATE, UTF_8);
					inSequence(fallbackSequence);
					will(returnValue(null));

					allowing(storeConfig).getStoreCode();
					will(returnValue(TESTSTORE));

					allowing(storeConfig).getSetting(with(equal("COMMERCE/STORE/theme")));
					will(returnValue(themeSettingValue));

					allowing(themeSettingValue).getValue();
					will(returnValue(THEME1));
				}
			});
		} catch (Exception e) {
			fail("Something went very wrong here");
		}

		final StoreResourceManagerImpl resourceManagerWithFallbackTracking = new TraceableStoreResourceManager(mockStoreResourceManager);
		resourceManagerWithFallbackTracking.setStoreConfig(storeConfig);

		try {
			resourceManagerWithFallbackTracking.getResource(TEST_VM, ResourceManager.RESOURCE_TEMPLATE, UTF_8);
		} catch (Exception e) {
			fail(NO_EXCEPTION_EXPECTED);
		}
	}

	/**
	 * Tests that the various methods for resolving a CM resource get called in the correct order when calling
	 * {@link StoreResourceManagerImpl#getResource(String, int, String)}.
	 */
	@Test
	public void testGetResourceFallbackLogicForCmResource() {
		final StoreResourceManagerImpl mockStoreResourceManager = context.mock(StoreResourceManagerImpl.class);
		final Sequence fallbackSequence = context.sequence("fallback-sequence");
		final StoreConfig storeConfig = context.mock(StoreConfig.class, "willFallback");

		try {
			context.checking(new Expectations() {
				{
					oneOf(mockStoreResourceManager).getResourceFromLastResolvedLocation(TEST_VM, ResourceManager.RESOURCE_TEMPLATE, UTF_8);
					inSequence(fallbackSequence);
					will(returnValue(null));

					oneOf(mockStoreResourceManager).getResourceCMGlobal(TEST_VM, ResourceManager.RESOURCE_TEMPLATE, UTF_8);
					inSequence(fallbackSequence);
					will(returnValue(null));

					oneOf(mockStoreResourceManager).getResourceFromParent(TEST_VM, ResourceManager.RESOURCE_TEMPLATE, UTF_8);
					inSequence(fallbackSequence);
					will(returnValue(null));

					allowing(storeConfig).getStoreCode();
					will(returnValue(null));
				}
			});
		} catch (Exception e) {
			fail("Something went very wrong here");
		}

		final StoreResourceManagerImpl resourceManagerWithFallbackTracking = new TraceableStoreResourceManager(mockStoreResourceManager);
		resourceManagerWithFallbackTracking.setStoreConfig(storeConfig);

		try {
			resourceManagerWithFallbackTracking.getResource(TEST_VM, ResourceManager.RESOURCE_TEMPLATE, UTF_8);
		} catch (Exception e) {
			fail(NO_EXCEPTION_EXPECTED);
		}
	}

	/**
	 * Tests that a CM global resource is successfully retrieved when calling
	 * {@link StoreResourceManagerImpl#getResourceCMGlobal(java.lang.String, int, java.lang.String)}.
	 */
	@Test
	public void testGetResourceCMGlobal() {
		final AssetRepository assetRepository = context.mock(AssetRepository.class);

		context.checking(new Expectations() {
			{
				oneOf(assetRepository).getCmAssetsSubfolder();
				will(returnValue(CMASSETS));
			}
		});

		storeResourceManagerForTestStore.setAssetRepository(assetRepository);
		final StringResourceRepository repo = StringResourceLoader.getRepository();
		repo.putStringResource(GLOBAL_TEMPLATES_TEST_VM, "");

		try {
			Resource resource = storeResourceManagerForTestStore.getResourceCMGlobal(GLOBAL_VM, ResourceManager.RESOURCE_TEMPLATE, UTF_8);
			assertNotNull(RESOURCE_SHOULD_NOT_BE_NULL, resource);
		} catch (Exception e) {
			fail(NO_EXCEPTION_EXPECTED);
		}
	}

	/**
	 * Tests that a resource loader is successfully found when {@link StoreResourceManagerImpl#getLoaderNameForResource(String)} is called for an
	 * existing resource.
	 */
	@Test
	public void testGetLoaderNameForStoreResource() {
		final StringResourceRepository repo = StringResourceLoader.getRepository();
		repo.putStringResource(TESTSTORE_TEMPLATES_TEST_VM, "");
		assertNotNull("Resource Loader should have been found for this resource.", storeResourceManagerForTestStore
				.getLoaderNameForResource(TESTSTORE_TEMPLATES_TEST_VM));
	}

	/**
	 * Tests that a resource loader is null when {@link StoreResourceManagerImpl#getLoaderNameForResource(String)} is called for a resource that does
	 * not exist.
	 */
	@Test
	public void testGetLoaderNameForNonExistantResource() {
		assertNull("Resource Loader should be null for this resource.", 
											storeResourceManagerForTestStore.getLoaderNameForResource(TESTSTORE_TEMPLATES_TEST_VM));
	}

	/**
	 * Customization of {@link StoreResourceManagerImpl} that can have a mock object set as a delegate, allowing internal method calls to be traced.
	 */
	private class TraceableStoreResourceManager extends StoreResourceManagerImpl {
		private final StoreResourceManagerImpl delegate;

		public TraceableStoreResourceManager(final StoreResourceManagerImpl delegate) {
			super();
			this.delegate = delegate;
		}

		@Override
		protected Resource getResourceCMGlobal(final String resourceName, final int resourceType, final String encoding) throws Exception {
			return delegate.getResourceCMGlobal(resourceName, resourceType, encoding);
		}

		@Override
		protected Resource getResourceFromLastResolvedLocation(final String resourceName, final int resourceType, final String encoding)
				throws Exception {
			return delegate.getResourceFromLastResolvedLocation(resourceName, resourceType, encoding);
		}

		@Override
		protected Resource getResourceFromParent(final String resourceName, final int resourceType, final String encoding) throws Exception {
			return delegate.getResourceFromParent(resourceName, resourceType, encoding);
		}

		@Override
		protected Resource getResourceFromThemeDefault(final String resourceName, final int resourceType, final String encoding, final String theme)
				throws Exception {
			return delegate.getResourceFromThemeDefault(resourceName, resourceType, encoding, theme);
		}

		@Override
		protected Resource getResourceStoreSpecific(final String resourceName, final int resourceType, final String encoding, final String theme)
				throws Exception {
			return delegate.getResourceStoreSpecific(resourceName, resourceType, encoding, theme);
		}
	}

}
