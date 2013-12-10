package com.elasticpath.tools.sync.client.controller.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanFactory;

import com.elasticpath.tools.sync.beanfactory.ContextInitializer;
import com.elasticpath.tools.sync.beanfactory.ContextInitializerFactory;
import com.elasticpath.tools.sync.beanfactory.SyncBeanFactoryMutator;
import com.elasticpath.tools.sync.client.SyncToolConfiguration;
import com.elasticpath.tools.sync.client.controller.SyncToolController;
import com.elasticpath.tools.sync.configuration.ConnectionConfiguration;
import com.elasticpath.tools.sync.configuration.dao.ConnectionConfigurationDao;
import com.elasticpath.tools.sync.exception.SyncToolRuntimeException;
import com.elasticpath.tools.sync.processing.ObjectProcessingException;
import com.elasticpath.tools.sync.processing.SerializableObjectListener;
import com.elasticpath.tools.sync.processing.SyncJobObjectProcessor;
import com.elasticpath.tools.sync.target.result.Summary;
import com.elasticpath.tools.sync.target.result.impl.SummaryImpl;

/**
 * This abstract implementation of a {@link SyncToolController} outlines the
 * basic steps of performing a system synchronization - source, target or both.
 */
public abstract class AbstractSyncController implements SyncToolController {
	
	/**
	 * A system configuration interface for triggering initialization on demand.
	 */
	public interface SystemConfig {
		
		/**
		 * Initializes a system configuration.
		 */
		void initSystem();
		
		/**
		 * Destroy system.
		 */
		void destroySystem();
	}

	private static final Logger LOG = Logger.getLogger(AbstractSyncController.class);
	
	private SyncToolConfiguration syncToolConfiguration;

	private ContextInitializerFactory contextInitializerFactory;
	
	private ConnectionConfigurationDao connectionConfigDao;

	private SyncBeanFactoryMutator syncBeanFactory;
	
	private AbstractObjectEventDistributor objectEventDistributor;
	
	/**
	 * Loads a TransactionJob instance by using a listener.
	 * <p>It can be loaded from database or read from file, or magically created</p>
	 * 
	 * @param listener the object listener
	 * @throws SyncToolRuntimeException if an error occurs while loading
	 */
	protected abstract void loadTransactionJob(SerializableObjectListener listener) throws SyncToolRuntimeException;
	
	/**
	 * Processes two steps.
	 * <ul>
	 *  <li>Initialize the environment</li>
	 * 	<li>Load {@link com.elasticpath.tools.sync.job.TransactionJob}</li>
	 * 	<li>Process synchronization</li>
	 * </ul>
	 * @return Summary instance
	 */
	public Summary synchronize() {
		this.synchronizationToBeStarted();
		
		Summary summary = new SummaryImpl();

		SystemConfig sourceConfig = getSourceConfig();
		SystemConfig targetConfig = getTargetConfig();
		this.initConfig(sourceConfig, targetConfig);
		
		AbstractObjectEventDistributor eventDistributor = getObjectEventDistributor();
		
		// set the properties of the distributor
		eventDistributor.setObjectProcessor(getObjectProcessor());
		eventDistributor.setSummary(summary);
		
		try {
			loadTransactionJob(eventDistributor);
		} catch (SyncToolRuntimeException exc) {
			eventDistributor.handleException(exc, summary, null);
			return summary;
		} catch (ObjectProcessingException exc) {
			LOG.error("Error while performing synchronization.", exc);
			eventDistributor.handleException(exc, summary, null);
			return summary;
		} catch (Exception exc) {
			LOG.error("Error while performing synchronization.", exc);
			eventDistributor.handleException(exc, summary, null);
			return summary;
		} finally {
			try {
				eventDistributor.finished();
			} catch (Exception exc) {
		                eventDistributor.handleException(exc, summary, null);
				LOG.error("Error while finishing up the synchronization process.", exc);
			}
		}
		
		this.synchronizationCompleted();
		this.destroyConfig(sourceConfig, targetConfig);
		
		return summary;
	}

	/**
	 * A callback method for the event of a starting synchronization.
	 */
	protected void synchronizationToBeStarted() {
		LOG.debug("Begin synchronization...");
	}

	private SystemConfig getTargetConfig() {
		return new SystemConfig() {
			private BeanFactory beanFactory;
			private ContextInitializer initializer;
			
			public void initSystem() {
				ConnectionConfiguration targetSystemConnectionConfig = connectionConfigDao.load(getSyncToolConfiguration().getTargetConfigName());
				String destinationType = "target";
				String connectionType = targetSystemConnectionConfig.getType();
				ContextInitializer targetContextInitializer = contextInitializerFactory.create(connectionType, destinationType);
				BeanFactory targetBeanFactory = targetContextInitializer.initializeContext(targetSystemConnectionConfig);
				initializer = targetContextInitializer;
				beanFactory = targetBeanFactory;
				getSyncBeanFactory().setTargetBeanFactory(targetBeanFactory);
				
			}
			
			public void destroySystem() {
				// This may be null in the case of an export
				if (initializer != null) {
					initializer.destroyContext(beanFactory);
				}
			}
		};
	}

	private SystemConfig getSourceConfig() {
		return new SystemConfig() {
			private BeanFactory beanFactory;
			private ContextInitializer initializer;
			
			public void initSystem() {
				ConnectionConfiguration sourceSystemConnectionConfig = connectionConfigDao.load(getSyncToolConfiguration().getSourceConfigName());
				String destinationType = "source";
				String connectionType = sourceSystemConnectionConfig.getType();
				ContextInitializer sourceContextInitializer = contextInitializerFactory.create(connectionType, destinationType);
				BeanFactory sourceBeanFactory = sourceContextInitializer.initializeContext(sourceSystemConnectionConfig);
				initializer = sourceContextInitializer;
				beanFactory = sourceBeanFactory;
				getSyncBeanFactory().setSourceBeanFactory(sourceBeanFactory);
			}

			public void destroySystem() {
				initializer.destroyContext(beanFactory);
			}
		};
	}
	
	/**
	 *
	 * @param sourceSystem source system configuration
	 * @param targetSystem target system configuration
	 */
	protected abstract void initConfig(SystemConfig sourceSystem, SystemConfig targetSystem);

	/**
	 * Destroy config.
	 *
	 * @param sourceConfig the source config
	 * @param targetConfig the target config
	 */
	protected abstract void destroyConfig(SystemConfig sourceConfig, SystemConfig targetConfig);
	
	/**
	 * Gets the object processor reponsible for properly handling the objects.
	 * 
	 * @return the object processor to be used
	 */
	protected abstract SyncJobObjectProcessor getObjectProcessor();
	
	/**
	 * A callback method for when the synchronization completes.
	 */
	protected void synchronizationCompleted() {
		LOG.debug("Synchronization completed.");
	}
	
	/**
	 *
	 * @return the syncToolConfiguration
	 */
	protected SyncToolConfiguration getSyncToolConfiguration() {
		return syncToolConfiguration;
	}

	/**
	 *
	 * @param syncToolConfiguration the syncToolConfiguration to set
	 */
	public void setSyncToolConfiguration(final SyncToolConfiguration syncToolConfiguration) {
		this.syncToolConfiguration = syncToolConfiguration;
	}

	/**
	 *
	 * @return the contextInitializerFactory
	 */
	public ContextInitializerFactory getContextInitializerFactory() {
		return contextInitializerFactory;
	}

	/**
	 *
	 * @param contextInitializerFactory the contextInitializerFactory to set
	 */
	public void setContextInitializerFactory(final ContextInitializerFactory contextInitializerFactory) {
		this.contextInitializerFactory = contextInitializerFactory;
	}

	/**
	 *
	 * @return the connectionConfigDao
	 */
	public ConnectionConfigurationDao getConnectionConfigDao() {
		return connectionConfigDao;
	}

	/**
	 *
	 * @param connectionConfigDao the connectionConfigDao to set
	 */
	public void setConnectionConfigDao(final ConnectionConfigurationDao connectionConfigDao) {
		this.connectionConfigDao = connectionConfigDao;
	}

	/**
	 *
	 * @return the syncBeanFactory
	 */
	protected SyncBeanFactoryMutator getSyncBeanFactory() {
		return syncBeanFactory;
	}

	/**
	 *
	 * @param syncBeanFactory the syncBeanFactory to set
	 */
	public void setSyncBeanFactory(final SyncBeanFactoryMutator syncBeanFactory) {
		this.syncBeanFactory = syncBeanFactory;
	}

	/**
	 *
	 * @return the objectEventDistributor
	 */
	public AbstractObjectEventDistributor getObjectEventDistributor() {
		return objectEventDistributor;
	}

	/**
	 *
	 * @param objectEventDistributor the objectEventDistributor to set
	 */
	public void setObjectEventDistributor(final AbstractObjectEventDistributor objectEventDistributor) {
		this.objectEventDistributor = objectEventDistributor;
	}

}
