package com.elasticpath.importexport.importer.controller.impl;

import java.io.InputStream;
import java.util.List;

import org.apache.log4j.Appender;
import org.apache.log4j.Logger;

import com.elasticpath.commons.ThreadLocalMap;
import com.elasticpath.importexport.common.configuration.ConfigurationLoader;
import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.common.logging.IESummaryAppender;
import com.elasticpath.importexport.common.summary.Summary;
import com.elasticpath.importexport.common.summary.SummaryLogger;
import com.elasticpath.importexport.common.summary.impl.SummaryImpl;
import com.elasticpath.importexport.common.transformers.Transformer;
import com.elasticpath.importexport.common.transformers.TransformersChainFactory;
import com.elasticpath.importexport.common.util.Message;
import com.elasticpath.importexport.common.util.Timer;
import com.elasticpath.importexport.common.util.Timer.Time;
import com.elasticpath.importexport.importer.configuration.ImportConfiguration;
import com.elasticpath.importexport.importer.context.ImportContext;
import com.elasticpath.importexport.importer.controller.ImportController;
import com.elasticpath.importexport.importer.controller.ImportStage;
import com.elasticpath.importexport.importer.controller.ImportStageFailedException;
import com.elasticpath.importexport.importer.retrieval.RetrievalMethod;
import com.elasticpath.importexport.importer.retrieval.RetrievalMethodFactory;
import com.elasticpath.importexport.importer.unpackager.Unpackager;
import com.elasticpath.importexport.importer.unpackager.UnpackagerFactory;
import com.elasticpath.service.impl.AbstractEpServiceImpl;

/**
 * Realizes import jobs processing using retrieval method, unpackager, transformers and import processor.
 */
public class ImportControllerImpl extends AbstractEpServiceImpl implements ImportController {

	private static final Logger LOG = Logger.getLogger(ImportControllerImpl.class);

	private ImportContext context;

	private UnpackagerFactory unpackagerFactory;

	private TransformersChainFactory transformersChainFactory;

	private RetrievalMethodFactory retrievalMethodFactory;

	private ConfigurationLoader configurationLoader;

	private boolean failures;

	private List<ImportStage> importStages;
	
	private ThreadLocalMap<String, Object> metadataMap;
	
	@Override
	public void loadConfiguration(final InputStream configurationStream) throws ConfigurationException {
		ImportConfiguration importConfiguration = configurationLoader.load(configurationStream, ImportConfiguration.class);
		loadConfiguration(importConfiguration);
	}
	
	@Override
	public void loadConfiguration(final ImportConfiguration importConfiguration) {
		context = new ImportContext(importConfiguration);
	}
	
	@Override
	public Summary executeImport() throws ConfigurationException {
		if (context == null) {
			throw new ConfigurationException("Configuration could not be loaded");
		}

		Appender summaryAppender = attachSummary();

		final ImportConfiguration importConfiguration = context.getImportConfiguration();

		LOG.debug("execute import");
		Timer timer = new Timer();
		
		for (ImportStage importStage : getImportStages()) {
			boolean importStageActiveState = importStage.isActive();
			LOG.debug(String.format("Import Stage: '%s' Active State: '%s'", importStage, importStageActiveState));
			if (!importStageActiveState) {
				continue;
			}
			getMetadataMap().put("activeImportStage", importStage.getId());
			
			final Unpackager unpackager = createUnpackager(importConfiguration);
	
			LOG.debug("Create appropriate chain of transformers");
			final List<Transformer> transformersChain =
				transformersChainFactory.createTransformersChain(importConfiguration.getTransformerConfigurationList());
	
			LOG.debug("Running import stage: " + importStage);
	
			LOG.info(new Message("IE-30005", Boolean.toString(importConfiguration.isXmlValidation())));

			try {
				while (unpackager.hasNext()) {
					InputStream entryToImport = unpackager.nextEntry();
					for (Transformer transformer : transformersChain) {
						entryToImport = transformer.transform(entryToImport);
					}
		
					importStage.execute(entryToImport, context);
				}
			} catch (ImportStageFailedException exc) {
				LOG.error(new Message("IE-30507", exc, importStage.getName()));
				// break the execution of the subsequent stages
				break;
			}
		}
		Time time = timer.getElapsedTime();
		
		Summary collectedSummary = detachSummary(summaryAppender);
		
		LOG.info("Import job finished in " + time.toString());
		
		failures = collectedSummary.failuresExist();
		return collectedSummary;
	}
	
	/**
	 * Create summary object for this export process, associate it with IESummaryAppender to get control over logging. All import-related application
	 * messages will be collected in summary.
	 * @return IESummaryAppender to deassociate it from log4j later
	 */
	private IESummaryAppender attachSummary() {
		IESummaryAppender summaryAppender = new IESummaryAppender();
		SummaryLogger summary = new SummaryImpl();
		summaryAppender.setSummaryLogger(summary);
		Logger.getRootLogger().addAppender(summaryAppender);
		context.setSummary(summary);
		return summaryAppender;
	}

	private Summary detachSummary(final Appender summaryAppender) {
		Summary summary = context.getSummary();
		context.setSummary(null);
		Logger.getRootLogger().removeAppender(summaryAppender);
		return summary;
	}

	private Unpackager createUnpackager(final ImportConfiguration importConfiguration) throws ConfigurationException {
		LOG.debug("Create retrieval method");
		final RetrievalMethod retrievalMethod =
			retrievalMethodFactory.createRetrievalMethod(importConfiguration.getRetrievalConfiguration());

		LOG.debug("Create unpackager");
		final Unpackager unpackager =
			unpackagerFactory.createUnpackager(importConfiguration.getPackagerConfiguration().getType(), retrievalMethod);
		return unpackager;
	}

	/**
	 * Gets the unpackagerFactory which creates an unpackager.
	 * 
	 * @return the unpackagerFactory
	 */
	public UnpackagerFactory getUnpackagerFactory() {
		return unpackagerFactory;
	}

	/**
	 * Sets the unpackagerFactory to create an unpackager.
	 * 
	 * @param unpackagerFactory the unpackagerFactory to set
	 */
	public void setUnpackagerFactory(final UnpackagerFactory unpackagerFactory) {
		this.unpackagerFactory = unpackagerFactory;
	}

	/**
	 * Gets the transformersChainFactory which creates a chain of transformers for every import job type.
	 * 
	 * @return the transformersChainFactory
	 */
	public TransformersChainFactory getTransformersChainFactory() {
		return transformersChainFactory;
	}

	/**
	 * Sets the transformersChainFactory to create a chain of transformers for every import job type.
	 * 
	 * @param transformersChainFactory the transformersChainFactory to set
	 */
	public void setTransformersChainFactory(final TransformersChainFactory transformersChainFactory) {
		this.transformersChainFactory = transformersChainFactory;
	}

	/**
	 * Gets the retrievalMethodFactory which creates retrieval method.
	 * 
	 * @return the retrievalMethodFactory
	 */
	public RetrievalMethodFactory getRetrievalMethodFactory() {
		return retrievalMethodFactory;
	}

	/**
	 * Sets the retrievalMethodFactory to create retrieval method.
	 * 
	 * @param retrievalMethodFactory the retrievalMethodFactory to set
	 */
	public void setRetrievalMethodFactory(final RetrievalMethodFactory retrievalMethodFactory) {
		this.retrievalMethodFactory = retrievalMethodFactory;
	}

	/**
	 * Sets the configuration loader.
	 *
	 * @param configurationLoader configuration loader to set
	 */
	public void setConfigurationLoader(final ConfigurationLoader configurationLoader) {
		this.configurationLoader = configurationLoader;
	}
	
	@Override
	public boolean failuresExist() {
		return failures;
	}

	/**
	 *
	 * @param importStages the importStages to set
	 */
	public void setImportStages(final List<ImportStage> importStages) {
		this.importStages = importStages;
	}

	/**
	 * Gets the import stages in the order they should be executed.
	 * 
	 * @return a list of import stages
	 */
	protected List<ImportStage> getImportStages() {
		return importStages;
	}

	/**
	 * Get the metadata map.
	 * 
	 * @return the threadLocalMap
	 */
	public ThreadLocalMap<String, Object> getMetadataMap() {
		return metadataMap;
	}

	/**
	 * Set the metadata map.
	 * 
	 * @param threadLocalMap the threadLocalMap to set
	 */
	public void setMetadataMap(final ThreadLocalMap<String, Object> threadLocalMap) {
		this.metadataMap = threadLocalMap;
	}

}
