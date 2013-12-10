package com.elasticpath.domain.catalogview.impl;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;

import org.jmock.Expectations;
import org.jmock.Sequence;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.domain.catalogview.SeoUrlBuilder;
import com.elasticpath.domain.misc.impl.RandomGuidImpl;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.impl.StoreImpl;
import com.elasticpath.service.catalogview.StoreConfig;

/**
 * SingleStoreSeoUrlBuilderImplTest class.
 */
public class StoreSeoUrlBuilderFactoryTest {

	private static final String STORE_CODES_SHOULD_BE_EQUAL_MESSAGE = "Store codes should be equal";

	private static final String US_STORE_CODE = "US001";

	private static final String UK_STORE_CODE = "UK001";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private StoreSeoUrlBuilderFactoryImpl storeSeoUrlBuilderFactory;

	private final BeanFactory beanFactory = context.mock(BeanFactory.class);

	private final StoreConfig storeConfig = context.mock(StoreConfig.class);

	/**
	 * Setup method for the test cases.
	 */
	@Before
	public void setUp() {
		storeSeoUrlBuilderFactory = new StoreSeoUrlBuilderFactoryImpl();
		storeSeoUrlBuilderFactory.setBeanFactory(beanFactory);
		storeSeoUrlBuilderFactory.setStoreConfig(storeConfig);

		setupCommonExpectations();
	}

	/**
	 * Setup common expectations.
	 */
	private void setupCommonExpectations() {

		// expectations
		context.checking(new Expectations() {
			{
				allowing(beanFactory).getBean("coreSeoUrlBuilder");
				will(onConsecutiveCalls(returnValue(new SeoUrlBuilderImpl()), returnValue(new SeoUrlBuilderImpl())));
			}
		});
	}

	/**
	 * Test get store seo url builder.
	 */
	@Test
	public void testSingleStoreSeoUrlBuilder() {

		final Store ukStore = new StoreImpl();
		ukStore.setCode(UK_STORE_CODE);

		context.checking(new Expectations() {
			{
				allowing(storeConfig).getStore();
				will(returnValue(ukStore));
			}
		});

		SeoUrlBuilderImpl ukSeoUrlBuilder = (SeoUrlBuilderImpl) storeSeoUrlBuilderFactory.getStoreSeoUrlBuilder();

		Assert.assertNotNull(ukSeoUrlBuilder);
		Assert.assertSame(STORE_CODES_SHOULD_BE_EQUAL_MESSAGE, UK_STORE_CODE, ukSeoUrlBuilder.getStore().getCode());

		Assert.assertEquals("Only one storeSeoUrlBuilder should exist here.", 1, storeSeoUrlBuilderFactory.getStoreSeoUrlBuilders().size());
	}

	/**
	 * Test get store seo url builder map.
	 */
	@Test
	public void testSingleStoreSeoUrlBuilderMap() {

		final Store ukStore = new StoreImpl();
		ukStore.setCode(UK_STORE_CODE);

		context.checking(new Expectations() {
			{
				allowing(storeConfig).getStore();
				will(returnValue(ukStore));
			}
		});

		SeoUrlBuilderImpl ukSeoUrlBuilder = (SeoUrlBuilderImpl) storeSeoUrlBuilderFactory.getStoreSeoUrlBuilder();

		Map<String, SeoUrlBuilder> storeSeoUrlBuilders = new HashMap<String, SeoUrlBuilder>();
		storeSeoUrlBuilders.put(ukStore.getCode(), ukSeoUrlBuilder);
		storeSeoUrlBuilderFactory.setStoreSeoUrlBuilders(storeSeoUrlBuilders);

		Assert.assertNotNull(storeSeoUrlBuilderFactory.getStoreSeoUrlBuilders());
		Assert.assertEquals("Size of storeseoUrlBuilders should be 1", 1, storeSeoUrlBuilderFactory.getStoreSeoUrlBuilders().size());
	}

	/**
	 * Test get store seo url builder.
	 */
	@Test
	public void testMultipleSameStoreSeoUrlBuilder() {

		final Store ukStore = new StoreImpl();
		ukStore.setCode(UK_STORE_CODE);

		final Store anotherUkStore = new StoreImpl();
		anotherUkStore.setCode(UK_STORE_CODE);

		context.checking(new Expectations() {
			{
				allowing(storeConfig).getStore();
				will(onConsecutiveCalls(returnValue(ukStore), returnValue(anotherUkStore)));
			}
		});

		SeoUrlBuilderImpl ukSeoUrlBuilder = (SeoUrlBuilderImpl) storeSeoUrlBuilderFactory.getStoreSeoUrlBuilder();

		SeoUrlBuilderImpl anotherUkSeoUrlBuilder = (SeoUrlBuilderImpl) storeSeoUrlBuilderFactory.getStoreSeoUrlBuilder();

		Assert.assertNotNull(ukSeoUrlBuilder);
		Assert.assertNotNull(anotherUkSeoUrlBuilder);
		Assert.assertSame(STORE_CODES_SHOULD_BE_EQUAL_MESSAGE, UK_STORE_CODE, ukSeoUrlBuilder.getStore().getCode());
		Assert.assertSame(STORE_CODES_SHOULD_BE_EQUAL_MESSAGE, UK_STORE_CODE, anotherUkSeoUrlBuilder.getStore().getCode());

		Assert.assertEquals("Only one storeSeoUrlBuilder should exist here.", 1, storeSeoUrlBuilderFactory.getStoreSeoUrlBuilders().size());

	}

	/**
	 * Test get store seo url builder.
	 */
	@Test
	public void testMultipleStoresSeoUrlBuilder() {

		final Store ukStore = new StoreImpl();
		ukStore.setCode(UK_STORE_CODE);

		final Store usStore = new StoreImpl();
		usStore.setCode(US_STORE_CODE);

		context.checking(new Expectations() {
			{
				allowing(storeConfig).getStore();
				will(onConsecutiveCalls(returnValue(ukStore), returnValue(usStore)));
			}
		});

		SeoUrlBuilderImpl ukSeoUrlBuilder = (SeoUrlBuilderImpl) storeSeoUrlBuilderFactory.getStoreSeoUrlBuilder();

		SeoUrlBuilderImpl usSeoUrlBuilder = (SeoUrlBuilderImpl) storeSeoUrlBuilderFactory.getStoreSeoUrlBuilder();

		Assert.assertNotNull(ukSeoUrlBuilder);
		Assert.assertSame(STORE_CODES_SHOULD_BE_EQUAL_MESSAGE, UK_STORE_CODE, ukSeoUrlBuilder.getStore().getCode());

		Assert.assertNotNull(usSeoUrlBuilder);
		Assert.assertSame(STORE_CODES_SHOULD_BE_EQUAL_MESSAGE, US_STORE_CODE, usSeoUrlBuilder.getStore().getCode());

		Assert.assertEquals("Only two storeSeoUrlBuilder's should exist here.", 2, storeSeoUrlBuilderFactory.getStoreSeoUrlBuilders().size());

	}

	/**
	 * Test multi threaded access to the SeoUrlBuilderFactory.
	 */
	@Test
	public void testMultiThreadedAccess() {

		context.checking(new Expectations() {
			{

				// Allow any number of seoUrlBuilders to be created
				final Sequence builderSequence = context.sequence("builderSequence");
				allowing(beanFactory).getBean("coreSeoUrlBuilder");
				inSequence(builderSequence);
				will(returnValue(new SeoUrlBuilderImpl()));

				// Allow any number of stores to be created
				final Sequence storeSequence = context.sequence("storeSequence");
				final Store newStore = context.mock(Store.class);
				allowing(storeConfig).getStore();
				inSequence(storeSequence);
				will(returnValue(newStore));

				final Sequence codeSequence = context.sequence("codeSequence");
				allowing(newStore).getCode();
				inSequence(codeSequence);
				will(returnValue(new RandomGuidImpl().toString()));

			}
		});

		final int numberExecutions = 100;

		for (int n = 0; n < numberExecutions; n++) {

			Thread thread = new Thread() {

				public void run() {

					// Use random guid to generate unique id for the store
					SeoUrlBuilder seoUrlBuilder = storeSeoUrlBuilderFactory.getStoreSeoUrlBuilder();
					// System.out.println(seoUrlBuilder.toString() + " " + this.getName());
				}
			};

			thread.start();
		}

		// Map<String, SeoUrlBuilder> builders = storeSeoUrlBuilderFactory.getStoreSeoUrlBuilders();
		// for (String builderKey : builders.keySet()) {
		//			
		// System.out.println(("SeoUrlBuilder: " + builders.get(builderKey).toString()));
		// }
	}
}
