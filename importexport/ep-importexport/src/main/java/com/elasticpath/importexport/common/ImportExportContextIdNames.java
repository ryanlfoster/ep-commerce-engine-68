package com.elasticpath.importexport.common;

/**
 * <code>ImportExportContextIdNames</code> contains spring container bean id constants.
 */
public final class ImportExportContextIdNames {
	
	/** bean id for implementation of com.elasticpath.importexport.exporter.controller.impl.ExportControllerImpl. */
	public static final String EXPORT_CONTROLLER = "exportController";
		
	/** bean id for implementation of com.elasticpath.importexport.importer.controller.impl.ImportControllerImpl. */
	public static final String IMPORT_CONTROLLER = "importController";

	/** bean id for implementation of com.elasticpath.importexport.common.manifest.impl.ManifestBuilderImpl. */
	public static final String MANIFEST_BUILDER = "manifestBuilder";

	/** bean id for implementation of com.elasticpath.importexport.importer.importers.RuleWrapper. */
	public static final String RULE_WRAPPER = "ruleWrapper";
}
