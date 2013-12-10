package com.elasticpath.commons.beanframework;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.config.CacheConfiguration;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * Factory for EhCache caches that uses the default cache manager's default configuration. Bits of configuration can be overridden 
 * by setting the properties. 
 * 
 * This class is similar to Spring's {@link org.springframework.cache.ehcache.EhCacheFactoryBean}, however it uses default configuration from the 
 * cache manager instead of hard-coded defaults.
 */
public class EhCacheDefaultConfigurationFactoryBean implements FactoryBean<Ehcache>, BeanNameAware, InitializingBean, DisposableBean {
	private String beanName;
	private Ehcache cache;
	private CacheManager cacheManager;
	private String name;
	private Long maxEntriesLocalHeap;
	private Long timeToLive;
	private Long timeToIdle;
	
	/**
	 * Creates a cache using the default configuration from the cache manager.
	 * @return the newly created cache
	 */
	protected Ehcache createCache() {
		Ehcache cache = createCacheInstance();
		cache.setName(getName());
		getCacheManager().addCache(cache);
		return cache;
	}

	/**
	 * Creates an instance of {@link Cache} using the given configuration.
	 *
	 * @return the cache
	 */
	protected Ehcache createCacheInstance() {
		return new Cache(createConfiguration());
	}

	/**
	 * Creates a cache configuration.
	 *
	 * @return the cache configuration to be used to create a cache.
	 */
	protected CacheConfiguration createConfiguration() {
		CacheConfiguration configuration = getConfigurationTemplate();
		if (timeToIdle != null) {
			configuration.setTimeToIdleSeconds(timeToIdle);
		}
		
		if (timeToLive != null) {
			configuration.setTimeToLiveSeconds(timeToLive);
		}
		
		if (maxEntriesLocalHeap != null) {
			configuration.setMaxEntriesLocalHeap(maxEntriesLocalHeap);
		}
		return configuration;
	}

	protected CacheConfiguration getConfigurationTemplate() {
		return getCacheManager().getConfiguration().getDefaultCacheConfiguration().clone();
	}

	@Override
	public void afterPropertiesSet() {
		if (getCacheManager() == null) {
			setCacheManager(getDefaultCacheManager());
		}
		
		if (StringUtils.isBlank(getName())) {
			setName(getBeanName());
		}
		
		cache = createCache();
	}

	protected CacheManager getDefaultCacheManager() {
		return CacheManager.getInstance();
	}

	@Override
	public void setBeanName(final String name) {
		this.beanName = name;
	}
	
	public String getBeanName() {
		return beanName;
	}

	@Override
	public Ehcache getObject() {
		return cache;
	}

	@Override
	public Class<?> getObjectType() {
		return Cache.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	public void setCacheManager(final CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	protected CacheManager getCacheManager() {
		return cacheManager;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	protected Long getMaxEntriesLocalHeap() {
		return maxEntriesLocalHeap;
	}

	public void setMaxEntriesLocalHeap(final Long maxEntriesLocalHeap) {
		this.maxEntriesLocalHeap = maxEntriesLocalHeap;
	}

	protected Long getTimeToLive() {
		return timeToLive;
	}

	public void setTimeToLive(final Long timeToLive) {
		this.timeToLive = timeToLive;
	}

	protected Long getTimeToIdle() {
		return timeToIdle;
	}

	public void setTimeToIdle(final Long timeToIdle) {
		this.timeToIdle = timeToIdle;
	}

	@Override
	public void destroy() throws Exception {
		cacheManager.removeCache(name);
	}
}
