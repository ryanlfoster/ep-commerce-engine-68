package com.elasticpath.importexport.importer.importers.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.elasticpath.importexport.common.dto.assets.AssetDTO;
import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.common.exception.runtime.ImportRuntimeException;
import com.elasticpath.importexport.common.types.PackageType;
import com.elasticpath.importexport.common.util.assets.ImportAssetFileManager;
import com.elasticpath.importexport.importer.context.ImportContext;
import com.elasticpath.importexport.importer.importers.Importer;
import com.elasticpath.importexport.importer.importers.SavingStrategy;
import com.elasticpath.importexport.util.ApplicationPropertiesHelper;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.service.impl.AbstractEpServiceImpl;

/**
 * Import asset files.
 */
public class AssetsImporterImpl extends AbstractEpServiceImpl implements Importer<Persistable, AssetDTO> {

	private String schemaPath;

	private ImportAssetFileManager assetFileManager;

	private ImportStatusHolder statusHolder;

	private ApplicationPropertiesHelper applicationPropertiesHelper;

	@Override
	public void initialize(final ImportContext context, final SavingStrategy<Persistable, AssetDTO> savingStrategy)
			throws ConfigurationException {
		final PackageType packageType = context.getImportConfiguration().getPackagerConfiguration().getType();
		final      String packageName = context.getImportConfiguration().getRetrievalConfiguration().getSource();

		if (assetFileManager == null || packageType == null || packageName == null) {
			throw new ConfigurationException("Some assets importer's field hasn't been initialized");
		}

		assetFileManager.initialize(getVfsProperties(), packageType, packageName);
	}

	private Map<String, String> getVfsProperties() {
		return getApplicationPropertiesHelper().getPropertiesWithNameStartsWith("asset");
	}

	/**
	 * Actual assets import processing.
	 *
	 * @param assetDto DTO containing the list of asset files to be imported
	 * @return true if asset was actually uploaded to the server
	 */
	@Override
	public boolean executeImport(final AssetDTO assetDto) {
		if (assetFileManager == null) {
			throw new ImportRuntimeException("IE-30900");
		}

		setImportStatus(assetDto);

		return assetFileManager.importFile(assetDto.getAsset());
	}

	@Override
	public int getCommitUnit() {
		return 1;
	}

	@Override
	public String getImportedObjectName() {
		return AssetDTO.ROOT_ELEMENT;
	}

	@Override
	public int getObjectsQty(final AssetDTO dto) {
		return 1;
	}

	@Override
	public SavingStrategy<Persistable, AssetDTO> getSavingStrategy() {
		return null;
	}

	@Override
	public String getSchemaPath() {
		return schemaPath;
	}

	/**
	 * Sets XSD schema path.
	 *
	 * @param schemaPath the path to XSD schema
	 */
	public void setSchemaPath(final String schemaPath) {
		this.schemaPath = schemaPath;
	}

	@Override
	public void setSavingStrategy(final SavingStrategy<Persistable, AssetDTO> savingStrategy) {
		// do nothing
	}

	@Override
	public void postProcessingImportHandling() {
		assetFileManager.close();
	}

	/**
	 * Sets Virtual File System helper.
	 *
	 * @param assetFileManager helper
	 */
	public void setAssetFileManager(final ImportAssetFileManager assetFileManager) {
		this.assetFileManager = assetFileManager;
	}

	/**
	 * Sets status holder.
	 *
	 * @param statusHolder status holder
	 */
	public void setStatusHolder(final ImportStatusHolder statusHolder) {
		this.statusHolder = statusHolder;
	}

	@Override
	public ImportStatusHolder getStatusHolder() {
		return statusHolder;
	}

	/**
	 * Sets Import Status ("object.asset").
	 *
	 * @param object the AssetDTO instance.
	 */
	protected void setImportStatus(final AssetDTO object) {
		getStatusHolder().setImportStatus("\"" + object.getAsset() + "\"");
	}

	protected ApplicationPropertiesHelper getApplicationPropertiesHelper() {
		return applicationPropertiesHelper;
	}

	public void setApplicationPropertiesHelper(final ApplicationPropertiesHelper applicationPropertiesHelper) {
		this.applicationPropertiesHelper = applicationPropertiesHelper;
	}

	@Override
	public Class<? extends AssetDTO> getDtoClass() {
		return AssetDTO.class;
	}

	@Override
	public List<Class<?>> getAuxiliaryJaxbClasses() {
		return Collections.emptyList();
	}
}
