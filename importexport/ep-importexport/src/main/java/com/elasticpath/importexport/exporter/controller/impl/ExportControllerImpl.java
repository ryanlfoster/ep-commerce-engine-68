package com.elasticpath.importexport.exporter.controller.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Appender;
import org.apache.log4j.Logger;

import com.elasticpath.importexport.common.ImportExportContextIdNames;
import com.elasticpath.importexport.common.configuration.ConfigurationLoader;
import com.elasticpath.importexport.common.configuration.PackagerConfiguration;
import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.common.exception.runtime.ExportRuntimeException;
import com.elasticpath.importexport.common.logging.IESummaryAppender;
import com.elasticpath.importexport.common.manifest.Manifest;
import com.elasticpath.importexport.common.manifest.ManifestBuilder;
import com.elasticpath.importexport.common.marshalling.XMLMarshaller;
import com.elasticpath.importexport.common.summary.Summary;
import com.elasticpath.importexport.common.summary.SummaryLayout;
import com.elasticpath.importexport.common.summary.SummaryLogger;
import com.elasticpath.importexport.common.summary.impl.SimpleSummaryLayout;
import com.elasticpath.importexport.common.summary.impl.SummaryImpl;
import com.elasticpath.importexport.common.transformers.Transformer;
import com.elasticpath.importexport.common.transformers.TransformersChainFactory;
import com.elasticpath.importexport.common.types.PackageType;
import com.elasticpath.importexport.common.util.MessageResolver;
import com.elasticpath.importexport.common.util.Timer;
import com.elasticpath.importexport.common.util.Timer.Time;
import com.elasticpath.importexport.common.util.runner.AbstractPipedStreamRunner;
import com.elasticpath.importexport.common.util.runner.PipedStreamRunner;
import com.elasticpath.importexport.exporter.configuration.ExportConfiguration;
import com.elasticpath.importexport.exporter.configuration.search.SearchConfiguration;
import com.elasticpath.importexport.exporter.context.DependencyRegistry;
import com.elasticpath.importexport.exporter.context.ExportContext;
import com.elasticpath.importexport.exporter.controller.ExportController;
import com.elasticpath.importexport.exporter.delivery.DeliveryMethod;
import com.elasticpath.importexport.exporter.delivery.DeliveryMethodFactory;
import com.elasticpath.importexport.exporter.exportentry.ExportEntry;
import com.elasticpath.importexport.exporter.exporters.Exporter;
import com.elasticpath.importexport.exporter.exporters.ExporterFactory;
import com.elasticpath.importexport.exporter.packager.Packager;
import com.elasticpath.importexport.exporter.packager.PackagerFactory;
import com.elasticpath.service.impl.AbstractEpServiceImpl;

/**
 * Realizes export jobs processing using exporters, transformers and delivery method.
 */
public class ExportControllerImpl extends AbstractEpServiceImpl implements ExportController {

	private static final Logger LOG = Logger.getLogger(ExportControllerImpl.class);

	private static final int KILOBYTE = 1024;

	private static final String EXPORT_CONIGURATION_FILENAME = "configuration/exportconfiguration.xml";

	private static final String SEARCH_CONFIGURATION_FILENAME = "configuration/searchconfiguration.xml";

	private ExportContext context;

	private ExporterFactory exporterFactory;

	private TransformersChainFactory transformersChainFactory;

	private DeliveryMethodFactory deliveryMethodFactory;

	private PackagerFactory packagerFactory;

	private ConfigurationLoader configurationLoader;

	private MessageResolver messageResolver;

	private boolean failures;

	@Override
	public void loadConfiguration(final ExportConfiguration exportConfiguration, final SearchConfiguration searchConfiguration) {
		context = new ExportContext(exportConfiguration, searchConfiguration);
	}

	@Override
	public void loadConfiguration(final InputStream configurationStream, final InputStream searchCriteriaStream) throws ConfigurationException {
		ExportConfiguration exportConfiguration = configurationLoader.load(configurationStream, ExportConfiguration.class);
		SearchConfiguration searchConfiguration = configurationLoader.load(searchCriteriaStream, SearchConfiguration.class);
		loadConfiguration(exportConfiguration, searchConfiguration);
	}

	@Override
	// TODO : This code must be refactored
	public Summary executeExport() throws ConfigurationException {
		Appender summaryAppender = attachSummary();
		if (context == null) {
			throw new ConfigurationException("configuration could not be loaded");
		}

		final Timer timer = new Timer();

		LOG.debug("Create export sequence");

		final ExportConfiguration exportProperties = context.getExportConfiguration();

		// TODO: Create specific transformers chain for every export job type.
		LOG.debug("Create appropriate chain of transformers");
		final List<Transformer> chainedTransformers = transformersChainFactory.createTransformersChain(exportProperties
				.getTransformerConfigurationList());
		final PackagerConfiguration packagerConfiguration = exportProperties.getPackagerConfiguration();
		final Packager packager = createPackager(exportProperties, packagerConfiguration);

		final ManifestBuilder manifestBuilder = getBean(ImportExportContextIdNames.MANIFEST_BUILDER);
		final SimpleSummaryLayout layout = new SimpleSummaryLayout();
		layout.setMessageResolver(messageResolver);

		initializeContext(new ArrayList<Exporter>(exporterFactory.getAllConfiguredExporters(context)));
		
		List<Exporter> exporterSequence = exporterFactory.createExporterSequence(context);
		while (exporterSequence != null) {
			export(exporterSequence, chainedTransformers, packager, manifestBuilder);

			exporterSequence = exporterFactory.createExporterSequence(context);
		}

		Time time = timer.getElapsedTime();
		Summary collectedSummary = detachSummary(summaryAppender);
		LOG.info("Export job finished in " + time.toString());

		assemblePackage(packager, manifestBuilder, layout, collectedSummary);
		if (packagerConfiguration.getType() == PackageType.ZIP && packagerConfiguration.getPackageName() != null) {
			LOG.info("Package complete (" + packagerConfiguration.getPackageName() + ")");
			LOG.info("Package size : " + new File(packagerConfiguration.getPackageName()).length() / KILOBYTE + "kb");
		}
		failures = collectedSummary.failuresExist();
		return collectedSummary;
	}

	/**
	 * Create summary object for this export process, associate it with IESummaryAppender to get control over logging. All export-related application
	 * messages will be collected in summary.
	 * 
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

	/**
	 * Obtain the export entry and perform any transformations on the export entry that are necessary, then proceed to add the entry to pack.
	 * Delivering the file is done in assemblePackage.
	 * 
	 * @param exporterSequence sequence to export
	 * @param chainedTransformers transformers to be applied
	 * @param packager entity that will add the entry
	 * @param manifestBuilder builds the manifest
	 */
	private void export(final List<Exporter> exporterSequence, final List<Transformer> chainedTransformers, final Packager packager,
			final ManifestBuilder manifestBuilder) {

		for (Exporter exporter : exporterSequence) {
			while (!exporter.isFinished()) {
				ExportEntry exportEntry = null;
				InputStream transformedStream = null;
				try {
					try {
						exportEntry = exporter.executeExport();
					} catch (ExportRuntimeException e) {
						LOG.error(e.getIEMessage());
						continue;
					}

					transformedStream = transformEntry(chainedTransformers, exportEntry);

					LOG.info("Pack and deliver : " + exportEntry.getName());
					packager.addEntry(transformedStream, exportEntry.getName());

				} finally {
					try {
						if (transformedStream != null) {
							transformedStream.close();
						}
					} catch (IOException ioe) {
						LOG.warn("Failed to close transformed stream", ioe);
					}

					if (exportEntry != null) {
						exportEntry.close();
					}
				}
				// TODO: Check that entry has been really packed.
				manifestBuilder.addResource(exporter.getJobType(), exporter.getJobType().getTagName() + ".xml");
			}
		}
	}

	private void assemblePackage(final Packager packager, final ManifestBuilder manifestBuilder, final SummaryLayout layout,
			final Summary collectedSummary) {
		try {
			final PipedStreamRunner manifestRunner = createManifestStreamRunner(manifestBuilder);
			packager.addEntry(manifestRunner.createResultStream(), Manifest.MANIFEST_XML);

			LOG.info("Pack Summary");
			packager.addEntry(new ByteArrayInputStream(layout.format(collectedSummary).getBytes()), Summary.SUMMARY);

			packager.addEntry(createMarshaledStream(context.getExportConfiguration()), EXPORT_CONIGURATION_FILENAME);
			packager.addEntry(createMarshaledStream(context.getSearchConfiguration()), SEARCH_CONFIGURATION_FILENAME);
		} finally {
			packager.finish();
		}
	}

	private InputStream transformEntry(final List<Transformer> chainedTransformers, final ExportEntry exportEntry) {
		InputStream stream = exportEntry.getInputStream();
		LOG.info("Transform marshalled objects");
		for (Transformer transformer : chainedTransformers) {
			stream = transformer.transform(stream);
		}
		return stream;
	}

	private void initializeContext(final List<Exporter> exporterSequence) {
		final DependencyRegistry dependencyRegistry = new DependencyRegistry(getDependentTypes(exporterSequence));
		context.setDependencyRegistry(dependencyRegistry);
	}

	private PipedStreamRunner createManifestStreamRunner(final ManifestBuilder manifestBuilder) {
		LOG.info("Prepare and Pack Manifest");
		final PipedStreamRunner runner = new AbstractPipedStreamRunner() {
			@Override
			protected void runInternal(final OutputStream outputStream) {
				(new XMLMarshaller(false, Manifest.class)).marshal(manifestBuilder.build(), outputStream);
			}
		};
		return runner;
	}

	private Packager createPackager(final ExportConfiguration exportProperties, final PackagerConfiguration packagerConfiguration)
			throws ConfigurationException {
		LOG.debug("Create delivery method");
		final DeliveryMethod deliveryMethod = deliveryMethodFactory.createDeliveryMethod(exportProperties.getDeliveryConfiguration());
		LOG.info("Delivery target : " + exportProperties.getDeliveryConfiguration().getTarget());

		LOG.debug("Create packager");
		return packagerFactory.createPackager(packagerConfiguration, deliveryMethod);
	}

	private InputStream createMarshaledStream(final Object object) {
		return new AbstractPipedStreamRunner() {
			@Override
			protected void runInternal(final OutputStream outputStream) {
				(new XMLMarshaller(false, object.getClass())).marshal(object, outputStream);
			}
		} .createResultStream();
	}

	private List<Class<?>> getDependentTypes(final List<Exporter> exporterSequence) {
		List<Class<?>> dependentTypes = new ArrayList<Class<?>>();
		for (Exporter exporter : exporterSequence) {
			for (Class<?> clazz : exporter.getDependentClasses()) {
				dependentTypes.add(clazz);
			}
		}
		return dependentTypes;
	}

	/**
	 * Gets the exporterFactory which creates a sequence of exporters.
	 * 
	 * @return the exporterFactory
	 */
	public ExporterFactory getExporterFactory() {
		return exporterFactory;
	}

	/**
	 * Sets the exporterFactory to create a sequence of exporters.
	 * 
	 * @param exporterFactory the exporterFactory to set
	 */
	public void setExporterFactory(final ExporterFactory exporterFactory) {
		this.exporterFactory = exporterFactory;
	}

	/**
	 * Gets the transformersChainFactory which creates a chain of transformers for every export job type.
	 * 
	 * @return the transformersChainFactory
	 */
	public TransformersChainFactory getTransformersChainFactory() {
		return transformersChainFactory;
	}

	/**
	 * Sets the transformersChainFactory to create a chain of transformers for every export job type.
	 * 
	 * @param transformersChainFactory the transformersChainFactory to set
	 */
	public void setTransformersChainFactory(final TransformersChainFactory transformersChainFactory) {
		this.transformersChainFactory = transformersChainFactory;
	}

	/**
	 * Gets the deliveryMethodFactory which creates delivery method.
	 * 
	 * @return the deliveryMethodFactory
	 */
	public DeliveryMethodFactory getDeliveryMethodFactory() {
		return deliveryMethodFactory;
	}

	/**
	 * Sets the deliveryMethodFactory to create delivery method.
	 * 
	 * @param deliveryMethodFactory the deliveryMethodFactory to set
	 */
	public void setDeliveryMethodFactory(final DeliveryMethodFactory deliveryMethodFactory) {
		this.deliveryMethodFactory = deliveryMethodFactory;
	}

	/**
	 * Gets the packagerFactory which creates packager.
	 * 
	 * @return the packagerFactory
	 */
	public PackagerFactory getPackagerFactory() {
		return packagerFactory;
	}

	/**
	 * Sets the packagerFactory to create packager.
	 * 
	 * @param packagerFactory the packagerFactory to set
	 */
	public void setPackagerFactory(final PackagerFactory packagerFactory) {
		this.packagerFactory = packagerFactory;
	}

	/**
	 * Gets the configurationLoader which loads export configuration from input stream with XML.
	 * 
	 * @return the configurationLoader
	 */
	public ConfigurationLoader getConfigurationLoader() {
		return configurationLoader;
	}

	/**
	 * Sets the configurationLoader to load export configuration from input stream with XML.
	 * 
	 * @param configurationLoader the configurationLoader to set
	 */
	public void setConfigurationLoader(final ConfigurationLoader configurationLoader) {
		this.configurationLoader = configurationLoader;
	}

	@Override
	public boolean failuresExist() {
		return failures;
	}

	/**
	 * Sets message resolver.
	 * 
	 * @param messageResolver the message resolver
	 */
	public void setMessageResolver(final MessageResolver messageResolver) {
		this.messageResolver = messageResolver;
	}
}
