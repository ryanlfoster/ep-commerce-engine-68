package com.elasticpath.epcoretool;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.ClassPathResource;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.search.IndexNotification;
import com.elasticpath.domain.search.UpdateType;
import com.elasticpath.domain.search.impl.IndexNotificationImpl;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.persistence.api.PersistenceSessionFactory;
import com.elasticpath.service.cmuser.CmUserService;
import com.elasticpath.service.rules.RecompilingRuleEngine;
import com.elasticpath.service.search.IndexBuildStatusService;
import com.elasticpath.service.search.IndexNotificationService;
import com.elasticpath.service.search.IndexType;
import com.elasticpath.service.search.index.IndexSearchService;
import com.elasticpath.settings.SettingValueFactory;
import com.elasticpath.settings.SettingsService;

/**
 * Bootstrap enough of Elastic Path "core" to use what we need within Maven and provide access to various beans. Much of this is copied from
 * test.application's TestApplicationContext. We start by initializing log4j programatically, then loading our beans via a BeanFactory. BeanFactories
 * do not wire things together by default, so we then override most of the beans marking them as lazily initialized. After that we wire in the data
 * source as specified via Maven configurations.
 */
public class EmbeddedEpCore {

	/** The Spring application context that mimics the platform setup. */
	private ApplicationContext appCtx;

	private XmlBeanFactory beanFactory;

	/**
	 * The constructor for this will fully initialize a Spring application context with the beans in epcore in it and get the database ready to go.
	 */
	public EmbeddedEpCore() {
		configureLog4j();
		initializeBeanFactory();
		wireDataSource();
		clearJPACache();
	}

	/**
	 * Gets the index search service.
	 *
	 * @return the index search service
	 */
	public IndexSearchService getIndexSearchService() {
		return (IndexSearchService) appCtx.getBean(ContextIdNames.INDEX_SEARCH_SERVICE);
	}

	/**
	 * Gets the cm user service.
	 *
	 * @return the cm user service
	 */
	public CmUserService getCmUserService() {
		return (CmUserService) appCtx.getBean(ContextIdNames.CMUSER_SERVICE);
	}

	/**
	 * Gets the index build status service.
	 *
	 * @return the index build status service
	 */
	public IndexBuildStatusService getIndexBuildStatusService() {
		return (IndexBuildStatusService) appCtx.getBean("indexBuildStatusService");
	}

	/**
	 * Gets the settings service.
	 *
	 * @return the settings service
	 */
	public SettingsService getSettingsService() {
		return (SettingsService) appCtx.getBean(ContextIdNames.SETTINGS_SERVICE);
	}

	/**
	 * Gets the setting value factory.
	 *
	 * @return the setting value factory
	 */
	public SettingValueFactory getSettingValueFactory() {
		return (SettingValueFactory) appCtx.getBean("settingValueFactory");
	}

	/**
	 * Creates the index notification.
	 *
	 * @param indexType the index type
	 * @param updateType the update type
	 * @return the index notification
	 */
	public IndexNotification createIndexNotification(final IndexType indexType, final UpdateType updateType) {
		/* This is only available from the PrototypeBeanFactory which I do not want to initialize here. */
		IndexNotification notification = new IndexNotificationImpl();

		notification.setIndexType(indexType);
		notification.setUpdateType(updateType);
		return notification;
	}

	/**
	 * Gets the index notification service.
	 *
	 * @return the index notification service
	 */
	public IndexNotificationService getIndexNotificationService() {
		return (IndexNotificationService) appCtx.getBean(ContextIdNames.INDEX_NOTIFICATION_SERVICE);

	}

	/**
	 * Gets the recompiling rule engine.
	 *
	 * @return the recompiling rule engine
	 */
	public RecompilingRuleEngine getRuleEngine() {
		return (RecompilingRuleEngine) appCtx.getBean(ContextIdNames.EP_RULE_ENGINE);
	}

	/**
	 * Wire data source.
	 */
	private void wireDataSource() {
		PersistenceEngine persistenceEngine = (PersistenceEngine) appCtx.getBean("persistenceEngine");
		PersistenceSessionFactory sessionFactory = persistenceEngine.getSessionFactory();

		sessionFactory.setConnectionFactoryName(null);
		sessionFactory.setConnectionDriverName(DataSourceFactory.class.getName());

	}

	/**
	 * Clear jpa cache.
	 */
	private void clearJPACache() {
		PersistenceEngine persistenceEngine = (PersistenceEngine) appCtx.getBean("persistenceEngine");
		if (persistenceEngine.isCacheEnabled()) {
			persistenceEngine.clearCache();
		}
	}

	/**
	 * Lazily initialized beans that are ApplicationContextAware do not have their ApplicationContext injected in them. Therefore we skip over lazily
	 * initializing coreBeanFactory and elasticPath.
	 */
	private void overrideMostDefinitionsToLazy() {
		for (int i = 0; i < beanFactory.getBeanDefinitionNames().length; i++) {
			String name = beanFactory.getBeanDefinitionNames()[i];
			if (name != null) {
				if ("coreBeanFactory".equals(name) || "elasticPath".equals(name)) {
					AbstractBeanDefinition beanDefinition = (AbstractBeanDefinition) beanFactory.getBeanDefinition(name);
					beanDefinition.setLazyInit(false);
				} else if (!name.startsWith("beanRegistrar")) {
					AbstractBeanDefinition beanDefinition = (AbstractBeanDefinition) beanFactory.getBeanDefinition(name);
					beanDefinition.setLazyInit(true);
				}
			}
		}
	}

	/**
	 * Create and initialize the bean factory. Note, that when loading beans through a bean factory, one needs to call refresh after all the loading
	 * is done for Spring to take care of things like autowiring and alike. So after we override definitions to lazy, we call refresh to wire things
	 * up.
	 */
	private void initializeBeanFactory() {

		releaseResources();

		beanFactory = new XmlBeanFactory(new ClassPathResource("META-INF/spring/elasticpath-core-runtime.xml"));

		overrideMostDefinitionsToLazy();

		appCtx = new GenericApplicationContext(beanFactory);

		((AbstractApplicationContext) appCtx).refresh();

	}

	/**
	 * Configure log4j.
	 */
	private void configureLog4j() {
		Logger rootLogger = Logger.getRootLogger();

		if (rootLogger.getAllAppenders().hasMoreElements()) {
			return;
		}

		rootLogger.setLevel(Level.WARN);
		rootLogger.addAppender(new ConsoleAppender(new PatternLayout("%-5p [%t]: %m%n")));

	}

	/**
	 * Release resources.
	 */
	private void releaseResources() {
		if (appCtx != null) {
			((GenericApplicationContext) appCtx).close();
		}
	}

}
