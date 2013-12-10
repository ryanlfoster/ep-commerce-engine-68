package com.elasticpath.commons.beanframework;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.config.CacheConfiguration;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;

/**
 * Tests {@link EhCacheDefaultConfigurationFactoryBean}.
 */
public class EhCacheDefaultConfigurationFactoryBeanTest {

	private static final String CACHE_NAME = "cache name";

	private static final String BEAN_NAME = "bean name";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery() {
		{
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};

	private EhCacheDefaultConfigurationFactoryBean factory;

	/**
	 * Tests the cache name for the case only the bean name is set.
	 */
	@Test
	public void testNoName() {
		factory = new EhCacheDefaultConfigurationFactoryBean() {
			@Override
			protected Ehcache createCache() {
				return null;
			}
		};

		factory.setBeanName(BEAN_NAME);

		factory.afterPropertiesSet();

		assertEquals("If no name is set, the bean name should be used for the cache name.", BEAN_NAME, factory.getName());
	}

	/**
	 * Tests the cache name for the case both the name and the bean name are set.
	 */
	@Test
	public void testNameAndBeanName() {
		factory = new EhCacheDefaultConfigurationFactoryBean() {
			@Override
			protected Ehcache createCache() {
				return null;
			}
		};
		factory.setBeanName(BEAN_NAME);
		factory.setName(CACHE_NAME);

		factory.afterPropertiesSet();

		assertEquals("If a name is set, that name should be used for the cache name.", CACHE_NAME, factory.getName());
	}

	/**
	 * Verifies that a custom cache manager will be used if it is set.
	 */
	@Test
	public void testCacheManager() {
		factory = new EhCacheDefaultConfigurationFactoryBean() {
			@Override
			protected Ehcache createCache() {
				return null;
			}
		};
		CacheManager cacheManager = context.mock(CacheManager.class);
		factory.setCacheManager(cacheManager);

		factory.afterPropertiesSet();

		assertSame("The custom cache manager should be used.", cacheManager, factory.getCacheManager());
	}

	/**
	 * Verifies that the default cache manager will be used if none is specified.
	 */
	@Test
	public void testNoCacheManager() {
		factory = new EhCacheDefaultConfigurationFactoryBean() {
			@Override
			protected Ehcache createCache() {
				return null;
			}
		};

		factory.afterPropertiesSet();

		assertSame("The default cache manager should be used.", CacheManager.getInstance(), factory.getCacheManager());
	}

	/**
	 * Tests creating a cache without mocking anything. Overrides two of the three configurations. The Ehcache library is packed with static and
	 * final methods, and there is no way for JMock to mock those methods.
	 */
	@Test
	public void testCreateCache() {
		factory = new EhCacheDefaultConfigurationFactoryBean();

		final long timeToIdle = 1001L;
		final long timeToLive = 2002L;
		final long maxEntriesLocalHeap = 3003L;

		factory.setTimeToIdle(timeToIdle);
		factory.setTimeToLive(timeToLive);
		factory.setMaxEntriesLocalHeap(maxEntriesLocalHeap);
		factory.setBeanName(BEAN_NAME);

		Ehcache cache = doGetCache();

		CacheConfiguration configuration = cache.getCacheConfiguration();

		assertEquals("timeToIdle should be overridden to what we set", timeToIdle, configuration.getTimeToIdleSeconds());
		assertEquals("maxEntriesLocalHeap should be overridden to what we set", maxEntriesLocalHeap, configuration.getMaxEntriesLocalHeap());

		assertEquals("timeToLive should be equal to overridden to what we set", timeToLive, configuration.getTimeToLiveSeconds());

		assertEquals("The name should come from the bean name.", BEAN_NAME, cache.getName());

		assertTrue("The cache should be added to the cache manager.", CacheManager.getInstance().cacheExists(BEAN_NAME));
	}

	/**
	 * Tests creating a cache without mocking anything. Doesn't override anything from the configuration. The Ehcache library is packed with static
	 * and final methods, and there is no way for JMock to mock those methods.
	 */
	@Test
	public void testDefaultConfiguration() {
		factory = new EhCacheDefaultConfigurationFactoryBean();
		factory.setBeanName(BEAN_NAME);

		Ehcache cache = doGetCache();

		CacheConfiguration defaultConfiguration = CacheManager.getInstance().getConfiguration().getDefaultCacheConfiguration();
		CacheConfiguration cacheConfiguration = cache.getCacheConfiguration();

		assertNotSame("A clone of the default configuration should be used.", defaultConfiguration, cacheConfiguration);

		// CacheConfiguration does not have a proper equals() method :(
		assertEquals("Cache configuration should be equal to the default.", defaultConfiguration.getTimeToIdleSeconds(),
				cacheConfiguration.getTimeToIdleSeconds());
		assertEquals("Cache configuration should be equal to the default.", defaultConfiguration.getTimeToLiveSeconds(),
				cacheConfiguration.getTimeToLiveSeconds());
		assertEquals("Cache configuration should be equal to the default.", defaultConfiguration.getMaxEntriesLocalHeap(),
				cacheConfiguration.getMaxEntriesLocalHeap());
	}

	/**
	 * Test that the created cache is removed when the destruction callback is called.
	 * 
	 * @throws Exception to accommodate the method signature of {@link org.springframework.beans.factory.DisposableBean#destroy()}
	 */
	@Test
	public void testCacheIsRemovedOnDestroy() throws Exception {
		final CacheManager cacheManager = context.mock(CacheManager.class);
		EhCacheDefaultConfigurationFactoryBean factoryBean = createFactoryBeanWithCacheManager(BEAN_NAME, cacheManager);

		context.checking(new Expectations() { {
			oneOf(cacheManager).removeCache(BEAN_NAME);
		} });

		factoryBean.destroy();
	}

	/**
	 * Test that any exceptions thrown by the {@link CacheManager} during removal of the cache are not
	 * swallowed by the factory.
	 * 
	 * @throws Exception to accommodate the method signature of {@link org.springframework.beans.factory.DisposableBean#destroy()}
	 */
	@Test(expected = IllegalStateException.class)
	public void testExceptionPropagatedOnDestroy() throws Exception {
		final CacheManager cacheManager = context.mock(CacheManager.class);
		EhCacheDefaultConfigurationFactoryBean factoryBean = createFactoryBeanWithCacheManager(BEAN_NAME, cacheManager);

		context.checking(new Expectations() { {
			oneOf(cacheManager).removeCache(BEAN_NAME);
			will(throwException(new IllegalStateException()));
		} });

		factoryBean.destroy();
	}

	private EhCacheDefaultConfigurationFactoryBean createFactoryBeanWithCacheManager(final String beanName, final CacheManager cacheManager) {
		EhCacheDefaultConfigurationFactoryBean factoryBean = new EhCacheDefaultConfigurationFactoryBean();
		factoryBean.setName(beanName);
		factoryBean.setCacheManager(cacheManager);
		return factoryBean;
	}

	/**
	 * Runs after each test.
	 */
	@After
	public void tearDown() {
		CacheManager.getInstance().shutdown();
	}

	private Ehcache doGetCache() {
		factory.afterPropertiesSet();
		return factory.getObject();
	}
}
