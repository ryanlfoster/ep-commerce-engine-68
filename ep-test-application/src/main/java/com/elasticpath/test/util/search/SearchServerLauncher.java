package com.elasticpath.test.util.search;

import java.net.BindException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.servlet.DispatcherType;

import org.apache.log4j.Logger;
import org.apache.solr.servlet.SolrDispatchFilter;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.search.solr.SolrIndexConstants;
import com.elasticpath.test.persister.TestConfig;
import com.elasticpath.test.util.FreePortScanner;

/**
 * Launches the Search Server for testing purposes.
 */
public class SearchServerLauncher {

	private static final Logger LOG = Logger.getLogger(SearchServerLauncher.class);

	private static final String SEARCHSERVER_CONTEXT = "/searchserver";

	private static Boolean searchServerStarted = false;

	private static int searchPort = -1;

	private static Object searchServerLock = new Object();

	private static final int MAX_EPHEMERAL_PORT_NUMBER = 65535;
	private static final int MIN_EPHEMERAL_PORT_NUMBER = 49152;

	private List<String> schedulers;
	private List<String> registrars;

	@Autowired
	private ConfigurableListableBeanFactory beanFactory;

	@Autowired
	private TestConfig config;

	/**
	 * Initialize the SearchServer.
	 */
	@PostConstruct
	void init() {
		// initialize search capabilities if required by configuration
		if (config.isSearchAvailable()) {
			LOG.debug("Initializing SearchServer...started");
			System.setProperty(SolrIndexConstants.SOLR_HOME_PROPERTY, config.getSearchHomeDir());
			startSearchServer();
			LOG.debug("Initializing SearchServer...done");
		}
	}

	/**
	 * Return the SearchServer URL.
	 *
	 * @return the SearchServer URL
	 */
	public String getSearchHostUrl() {
		try {
			return new URL("http", "localhost", searchPort, SEARCHSERVER_CONTEXT).toExternalForm();
		} catch (MalformedURLException e) {
			throw new IllegalStateException("Could not create search host URL for port " + searchPort + " and context " + SEARCHSERVER_CONTEXT, e);
		}
	}

	private static int startSearchServer() {
		if (searchServerStarted) {
			return searchPort;
		}

		synchronized(searchServerLock) {
			if (!searchServerStarted) {
				boolean successful = false;
				int tries = 0;
				int port = -1;
				do {
					try {
						port = FreePortScanner.getFreePort(MIN_EPHEMERAL_PORT_NUMBER, MAX_EPHEMERAL_PORT_NUMBER);
					} catch (IllegalStateException e) {
						throw new EpServiceException("No ports available to start Jetty", e);
					}
					try {
						startJetty(port);
						successful = true;
					} catch (BindException e) {
						LOG.debug(String.format("Port [%s] was already in use. Trying the next available port...", port));
						++tries;
					} catch (Exception e) {
						throw new EpServiceException("Can't start jetty. ", e);
					}
				} while (tries < 10 && !successful);

				if (successful) {
					searchPort = port;
				} else {
					throw new EpServiceException("Could not find an available port for the search server.");
				}

				searchServerStarted = true;
			}
		}
		return searchPort;
	}

	private static void startJetty(final int port) throws Exception {
		LOG.info(String.format("Trying to start search server on port[%s].", port));
		Server server = new Server(port);

		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath(SEARCHSERVER_CONTEXT);
        server.setHandler(context);

		// the solr filter handles all the solr requests.
		context.addFilter(SolrDispatchFilter.class, "/*", EnumSet.allOf(DispatcherType.class));

		// we need at least one servlet for jetty to handle requests, otherwise it will not call the filter at all.
		context.addServlet(DefaultServlet.class, "/*");
		server.setHandler(context);

		server.start();
		LOG.info("Search server started up...");
	}

	private void pauseScheduledTasks() {
		List<ExecutorService> executors = new ArrayList<ExecutorService>();
		List<String> schedulers = getSchedulerBeanNames();
		for (String name : schedulers) {
			ThreadPoolTaskScheduler scheduler = (ThreadPoolTaskScheduler) beanFactory.getBean(name);
			ExecutorService executorService = scheduler.getScheduledExecutor();
			executors.add(executorService);
			executorService.shutdown();
			beanFactory.destroyBean(name, scheduler);
			LOG.info("Terminating scheduler : " + name);
		}
		waitForExecutors(executors);
	}
	
	private void waitForExecutors(final List<ExecutorService> executorServices) {
		for (ExecutorService executorService : executorServices) {
			try {
				if (!executorService.awaitTermination(2, TimeUnit.MINUTES)) {
					LOG.error("Executor service " + executorService + " failed to terminate");
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				LOG.error("Interrupted while shutting down executor service " + executorService);
			}
		}
	}
		
	private void enableScheduledTasks() {
		LOG.info("Enabling scheduled tasks");
		List<String> schedulers = getSchedulerBeanNames();
		for (String name : schedulers) {
			Object scheduler = beanFactory.getBean(name);
			beanFactory.initializeBean(scheduler, name);
		}
		List<String> registrars = getRegistrarBeanNames();
		for (String name : registrars) {
			ScheduledTaskRegistrar registrar = (ScheduledTaskRegistrar) beanFactory.getBean(name);
			beanFactory.initializeBean(registrar, name);
		}
	}

	protected List<String> getSchedulerBeanNames() {
		if (schedulers == null) {
			schedulers = findMatchingBeans(Matchers.containsString("ThreadPoolTaskScheduler"));
		}
		return schedulers;
	}
	
	protected List<String> getRegistrarBeanNames() {
		if (registrars == null) {
			registrars = findMatchingBeans(Matchers.containsString("ScheduledTaskRegistrar"));
		}
		return registrars;
	}

	private List<String> findMatchingBeans(final Matcher<String> matcher) {
		List<String> foundBeanNames = new ArrayList<String>();
		for (int i = 0; i < beanFactory.getBeanDefinitionNames().length; i++) {
			final String name = beanFactory.getBeanDefinitionNames()[i];
			if (name != null) {
				final AbstractBeanDefinition beanDefinition = (AbstractBeanDefinition) beanFactory.getBeanDefinition(name);
				if (beanDefinition.getBeanClassName() != null && matcher.matches(beanDefinition.getBeanClassName())) {
					foundBeanNames.add(name);
				}
			}
		}
		return foundBeanNames;
	}
	

}
