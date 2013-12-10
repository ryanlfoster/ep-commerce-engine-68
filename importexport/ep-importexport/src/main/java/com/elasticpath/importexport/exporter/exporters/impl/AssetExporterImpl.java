package com.elasticpath.importexport.exporter.exporters.impl;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.vfs.FileObject;
import org.apache.log4j.Logger;

import com.elasticpath.domain.catalog.DigitalAsset;
import com.elasticpath.importexport.common.dto.assets.AssetDTO;
import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.common.exception.runtime.ExportRuntimeException;
import com.elasticpath.importexport.common.marshalling.XMLMarshaller;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.common.util.assets.ExportAssetFileManager;
import com.elasticpath.importexport.common.util.runner.AbstractPipedStreamRunner;
import com.elasticpath.importexport.exporter.context.ExportContext;
import com.elasticpath.importexport.exporter.exportentry.ExportEntry;
import com.elasticpath.importexport.exporter.exportentry.impl.FileObjectExportEntry;
import com.elasticpath.importexport.exporter.exportentry.impl.PipedStreamExportEntry;
import com.elasticpath.importexport.exporter.exporters.Exporter;
import com.elasticpath.importexport.util.ApplicationPropertiesHelper;
import com.elasticpath.service.impl.AbstractEpServiceImpl;

/**
 * Export only asset files: product and brand images, files for digital assets.
 */
public class AssetExporterImpl extends AbstractEpServiceImpl implements Exporter {
	
	private ExportContext context;

	private static final Logger LOG = Logger.getLogger(AssetExporterImpl.class);
	
	private Iterator<String> assetsIterator;
	
	private ExportAssetFileManager assetFileManager;
	
	private final List<AssetDTO> assets = new ArrayList<AssetDTO>();
	
	private boolean exportExecuted;

	private ExportEntry entry;
	
	private ApplicationPropertiesHelper applicationPropertiesHelper;

	/**
	 * Initializes exporter with context to make VFS settings available.
	 * 
	 * @param context containing export settings
	 * @throws ConfigurationException if VFS helper couldn't be configured
	 */
	public void initialize(final ExportContext context) throws ConfigurationException {
		this.context = context;
		this.exportExecuted = false;
		this.assetFileManager.initialize(getApplicationPropertiesHelper().getPropertiesWithNameStartsWith("asset"));
	}

	@Override
	public boolean isFinished() {
		if (null == assetsIterator) {
			assetsIterator = context.getDependencyRegistry().getAssetFileNames().iterator();
			LOG.info("Started asset files export execution");
		}
		return exportExecuted;
	}
	
	@Override
	public JobType getJobType() {
		return JobType.ASSETS;
	}
	
	@Override
	public ExportEntry executeExport() {
		if (!assetsIterator.hasNext()) {
			if (exportExecuted) {
				throw new ExportRuntimeException("IE-21000");
			}
			exportExecuted = true;
			return produceAssetsXMLList(assets);
		}
		String currentEntryName = assetsIterator.next();
		
		FileObject file = assetFileManager.exportFile(currentEntryName);		
		entry = new FileObjectExportEntry(currentEntryName, file);
		LOG.info("Loaded asset file to export: " + currentEntryName);
			
		assets.add(new AssetDTO(currentEntryName));
		context.getSummary().addToCounter(getJobType());
		return entry;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class<?>[] getDependentClasses() {
		return new Class[] { DigitalAsset.class };
	}

	/*
	 * Generates input stream with the list of asset file names.
	 */
	private ExportEntry produceAssetsXMLList(final List<AssetDTO> assets) {
		return new PipedStreamExportEntry(
				getJobType().getTagName() + ".xml",
				new AbstractPipedStreamRunner() {
					@Override
					protected void runInternal(final OutputStream outputStream) {
						final PrintStream printer = new PrintStream(outputStream);
						printer.print("<" + getJobType().getTagName() + ">");
						XMLMarshaller marshaller = new XMLMarshaller(AssetDTO.class);
						for (AssetDTO assetDto : assets) {
							marshaller.marshal(assetDto, outputStream);
						}
						printer.print("</" + getJobType().getTagName() + ">");
					}
			
				});
	}
	
	/**
	 * Sets Virtual File System helper.
	 * 
	 * @param assetFileManager helper
	 */
	public void setAssetFileManager(final ExportAssetFileManager assetFileManager) {
		this.assetFileManager = assetFileManager;
	}

	protected ApplicationPropertiesHelper getApplicationPropertiesHelper() {
		return applicationPropertiesHelper;
	}

	public void setApplicationPropertiesHelper(final ApplicationPropertiesHelper applicationPropertiesHelper) {
		this.applicationPropertiesHelper = applicationPropertiesHelper;
	}
}
