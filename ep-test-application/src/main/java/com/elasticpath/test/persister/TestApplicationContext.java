package com.elasticpath.test.persister;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.transaction.support.TransactionTemplate;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.util.impl.MessageSourceCacheImpl;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.testcontext.ShoppingTestData;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.cmuser.CmUserService;
import com.elasticpath.service.rules.impl.DBCompilingRuleEngineImpl;
import com.elasticpath.settings.SettingsService;
import com.elasticpath.settings.domain.SettingDefinition;
import com.elasticpath.test.common.exception.TestApplicationException;
import com.elasticpath.test.persister.testscenarios.AbstractScenario;

/**
 * <code>TestApplicationContext</code> is a singleton class which manages the following categories:<br>
 * <ol>
 * <li> Application context initialization</li>
 * <li>  Db reinitialization</li>
 * <li> Scenario managing<</li>
 * </ol>
 * Application context is initialized once getInstance() is called. <br>
 * Also once request to change db schema is emitted the application context is also reinitialized. <br>
 * The reason is that JPA can be initialized only once and JPA context should be re-created in case of settings changing.<br>
 * The database initialization depends on 'force' flag and the TestApplicationContext class's applicationContextMode mode. <br>
 * See JavaDoc below. <br>
 * Also TestApplicationContext provides useful methods to initialize a bunch of scenarios and keep the reference on them. <br>
 * Since TestApplicationContext is singleton, the scenarios could be accessed from another test or fixture if required.
 */
public class TestApplicationContext {

	private static final Logger LOG = Logger.getLogger(TestApplicationContext.class);

	@Autowired
	/** The Spring application context that mimics the platform setup. */
	private GenericApplicationContext appCtx;

	@Autowired
	private TestDataPersisterFactory dataPersisterFactory;

	private final Map<Class<? extends AbstractScenario>, AbstractScenario> scenariosMap = new HashMap<Class<? extends AbstractScenario>, AbstractScenario>();

	@Autowired
	private BeanFactory beanFactory;

	@Autowired
	private org.springframework.beans.factory.BeanFactory springBeanFactory;
	
	@Autowired
	private TestConfig config;

	@Autowired
	protected JpaTransactionManager transactionManager;

	private TransactionTemplate txTemplate;

	protected TestApplicationContext() {
		LOG.debug("Initializing TestApplicationContext... started");

		releaseResources();

		resetRuleEngineCache();
		}

	public void clearStaleData() {
		resetRuleEngineCache();
	}

	private void resetRuleEngineCache() {
		// Can't use the BeanFactory until the object is created, so use the app context.
		if (appCtx != null) {
			((DBCompilingRuleEngineImpl) getBeanFactory().getBean(ContextIdNames.EP_RULE_ENGINE)).resetRuleBaseCache();
		}
	}

	protected void init() {
		configureSettingsServiceWithTestDefaults();

		LOG.trace("Completed initializing TestApplicationContext...done");
	}
	
	/**
	 * Integration tests require settings related to the filesystem that may differ from what's being added by the base-insert.sql script. We
	 * configure the settings needed by integration tests here. These settings should be global. Refrain from adding test specific settings here.
	 */
	public void configureSettingsServiceWithTestDefaults() {
		final SettingsService settingsService = getBeanFactory().getBean("settingsService");
		try {
			final SettingDefinition settingsDefAssetLocation = settingsService.getSettingDefinition("COMMERCE/SYSTEM/ASSETS/assetLocation");
			settingsDefAssetLocation.setDefaultValue(config.getAssetsDir());
			settingsService.updateSettingDefinition(settingsDefAssetLocation);

			final SettingDefinition settingsDefTheme = settingsService.getSettingDefinition("COMMERCE/STORE/theme");
			settingsDefTheme.setDefaultValue("fit");
			settingsService.updateSettingDefinition(settingsDefTheme);

			// disable emails. Fixtures should enable them, if they care about emails.
			final SettingDefinition emailEnabled = settingsService.getSettingDefinition("COMMERCE/SYSTEM/emailEnabled");
			emailEnabled.setDefaultValue(String.valueOf(false));
			settingsService.updateSettingDefinition(emailEnabled);

			// reinitialize the map with catalog theme's vm & properties files.
			final MessageSourceCacheImpl messageSourceCache = getBeanFactory().getBean("messageSourceCache");
			messageSourceCache.invalidate();
		} catch (final EpServiceException e) {
			throw new TestApplicationException("Could not configure Settings Service " + e);
		}
	}

	private void releaseResources() {
		ShoppingTestData.reset();
	}

	/**
	 * Return the instance of TestDataPersisterFactory. Can be used only after db initialization.
	 *
	 * @return the testDataPersisterFactory
	 */
	public TestDataPersisterFactory getPersistersFactory() {
		return dataPersisterFactory;
	}

	/**
	 * Evict an object from the cache.
	 *
	 * @param object the object to remove from the data cache.
	 */
	public void evictObjectFromCache(final Persistable object) {
		final PersistenceEngine persistenceEngine = getBeanFactory().getBean("persistenceEngine");
		persistenceEngine.evictObjectFromCache(object);
	}

	/**
	 * Tries to instantiate and initialize scenarios using scenarios' class as key. <br>
	 * If a scenario could not be found by key, it's ignored. <br>
	 * Keeps the reference to initialized scenarios. A scenario can later be obtained using getScenario(...) method. <br>
	 * If the keyed scenario already exists in the map, it will be overridden.
	 *
	 * @param scenarios list of scenario keys
	 * @return current scenario map.
	 */
	public <T extends AbstractScenario> Map<Class<? extends AbstractScenario>, AbstractScenario> useScenarios(final List<Class<T>>  scenarios) {

		for (final Class<T> scenarioClass : scenarios) {
			final T scenario = springBeanFactory.getBean(StringUtils.uncapitalize(scenarioClass.getSimpleName()), scenarioClass);
			if (scenario != null) {
				scenario.initialize();
				scenariosMap.put(scenarioClass, scenario);
			}
		}
		return scenariosMap;
	}

	/**
	 * Initializes single scenario.
	 *
	 * @param scenarioClass key for the scenario.
	 * @return current scenario.
	 */
	public <T extends AbstractScenario> T useScenario(final Class<T> scenarioClass) {
		final T scenario = springBeanFactory.getBean(StringUtils.uncapitalize(scenarioClass.getSimpleName()), scenarioClass);
		if (scenario != null) {
			scenario.initialize();
			scenariosMap.put(scenarioClass, scenario);
		}

		return scenario;
	}

	public AbstractScenario getScenario(final Class< ? extends AbstractScenario> scenarioClass) {
		return scenariosMap.get(scenarioClass);
	}

	/**
	 * Returns the transactionTemplate.
	 *
	 * @return the transactionTemplate
	 */
	public TransactionTemplate getTxTemplate() {
		if (txTemplate == null) {
			txTemplate = new TransactionTemplate(transactionManager);
		}
		return txTemplate;
	}

	/**
	 * Provides {@link Resource}s.
	 */

	protected CmUser getAdminCmUser() {
		final CmUserService cmUserService = getBeanFactory().getBean(ContextIdNames.CMUSER_SERVICE);
		return cmUserService.findByUserName("admin");
	}

	/**
	 * Puts the admin cmuser into the Spring security context.
	 */
	public void setAdminSecurityContext() {
		final UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(getAdminCmUser(), null);
		final SecurityContext context = new SecurityContextImpl();
		context.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(context);
	}
	
	public BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}
}
