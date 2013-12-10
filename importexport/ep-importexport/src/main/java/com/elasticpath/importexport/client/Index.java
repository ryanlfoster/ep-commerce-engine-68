package com.elasticpath.importexport.client;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.lang.LocaleUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.or.ObjectRenderer;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.RendererSupport;

import com.elasticpath.commons.ThreadLocalMap;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.enums.OperationEnum;
import com.elasticpath.domain.ElasticPath;
import com.elasticpath.domain.changeset.ChangeSet;
import com.elasticpath.importexport.common.ImportExportContextIdNames;
import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.common.exception.runtime.ImportRuntimeException;
import com.elasticpath.importexport.common.summary.Summary;
import com.elasticpath.importexport.common.summary.impl.SimpleSummaryLayout;
import com.elasticpath.importexport.common.util.Message;
import com.elasticpath.importexport.common.util.MessageResolver;
import com.elasticpath.importexport.exporter.controller.ExportController;
import com.elasticpath.importexport.importer.controller.ImportController;
import com.elasticpath.service.changeset.ChangeSetManagementService;
import com.elasticpath.service.changeset.ChangeSetService;

/**
 * The client that is responsible for working with import-export operations.
 */
@SuppressWarnings("PMD.DoNotCallSystemExit")
public class Index {

	private static final String HELP_STRING = "-i [-c importconfiguration.xml] [-g changeSetGuid] [-s stage1|stage2]\n"
		+ "-e searchconfiguration.xml [-c exportconfiguration.xml] [-l locale]";

	private static final Logger LOG = Logger.getLogger(Index.class);

	private final EngineInitialization engine;
	
	private final MessageResolver messageResolver;

	private String stage;

	private String changeSetGuid;
	
	private static final String DEFAULT_EXPORT_CONFIGURATION_FILE = "exportconfiguration.xml";
	
	private static final String DEFAULT_IMPORT_CONFIGURATION_FILE = "importconfiguration.xml";
	
	private static final int SUCCESS = 0;
	
	private static final int PARTIAL_SUCCESS = 1;
	
	private static final int WRONG_COMMAND_LINE_ARGUMENTS = 2;
	
	private static final int WRONG_CONFIGURATION  = 4;
	
	private static final int COMPLETE_FAIL = 8;

	/**
	 * Constructs Index.
	 */
	public Index() {
		engine = EngineInitialization.getInstance();
		messageResolver = engine.getElasticPath().getBean("messageResolver");
		configureLogRendering();
		LOG.info("Engine Initialization...");
	}

	/**
	 * Starts this application with given arguments.
	 * <p>
	 * Available three options for starting:
	 * <p>  
	 * -h prints help information
	 * <p>
	 * -i starts the import <b>Note: the information for import receives from importconfiguration.xml file by default.
	 *  Custom configuration filename can be specified by argument for -c option. If the configuration file does not exist exception
	 * will be thrown.</b>
	 * <p>
	 * -e starts the export <b>Note: the information for export receives from exportconfiguration.xml file.
	 *  Custom configuration filename can be specified by argument for -c option. If this file does not exist exception
	 * will be thrown.</b>
	 * <p>
	 * configuration.xml file with search settings must be provided as argument for -e option. 
	 * 
	 * @param args the available arguments (-h, -i, -e, -c)
	 * @throws FileNotFoundException exception in case missing configuration files for import or export
	 */
	public static void main(final String[] args) throws FileNotFoundException {
		// create Options object
		Options options = new Options();

		// add options
		options.addOption("e", "export", true, "do the export");
		options.addOption("i", "import", false, "do the import");
		options.addOption("h", "help",   false, "prints current message");
		options.addOption("c", "config", true, "set the configuration");
		options.addOption("l", "locale", true, "set the locale");
		options.addOption("g", "changeset", true, "set the change set guid to import into");
		options.addOption("s", "stage", true, "set the processing stage to run [default: both stage 1 and 2]. "
				+ "stage1 only adds objects to the specified change set. stage2 means only import data"
				+ "If change set guid is not specified, stage parameter will be ignored");
				
		try {
			// parse
			CommandLineParser parser = new PosixParser();
			CommandLine cmd = parser.parse(options, args);

			if (cmd.hasOption("e")) {
				int exitCode = createIndex(cmd).doExport(
						getConfigurationFileName(cmd, DEFAULT_EXPORT_CONFIGURATION_FILE),
						cmd.getOptionValue("e"));
				System.exit(exitCode);
			} else if (cmd.hasOption("i")) {
				int exitCode = createIndex(cmd).doImport(
						getConfigurationFileName(cmd, DEFAULT_IMPORT_CONFIGURATION_FILE));
				System.exit(exitCode);
			} else {
				// automatically generate the help statement
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp(HELP_STRING, options);
				System.exit(WRONG_COMMAND_LINE_ARGUMENTS);
			}
		} catch (ParseException e) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp(HELP_STRING, options);
			System.exit(WRONG_COMMAND_LINE_ARGUMENTS);
		}
	}

	private void processChangeSet() throws ConfigurationException {
		ChangeSetService changeSetService = getElasticPath().getBean(ContextIdNames.CHANGESET_SERVICE);
 		
		ThreadLocalMap<String, Object> metadataMap = getElasticPath().getBean("persistenceListenerMetadataMap");
 		if (changeSetGuid == null) {
 			importOnly(metadataMap);
 		} else {
 			if (!changeSetService.isChangeSetEnabled()) {
 	 			LOG.warn(new Message("IE-30506"));
 	 			importOnly(metadataMap);
 	 			return;
 			}
	 		ChangeSetManagementService changeSetManagementService = getElasticPath().getBean(ContextIdNames.CHANGESET_MANAGEMENT_SERVICE);
			ChangeSet changeSet = changeSetManagementService.get(changeSetGuid, null);
			if (changeSet == null) {
				throw new ConfigurationException(String.format("Change set %s does not exist.", changeSetGuid));
			} else if (!changeSetManagementService.isChangeAllowed(changeSetGuid)) {
				throw new ConfigurationException(String.format(
						"Change set %s does not allow changes. It is probably locked or finalized.", changeSetGuid));
			}
			
			metadataMap.put("changeSetGuid", changeSetGuid);
			setChangeSetProcessingMode(metadataMap, stage);
 		}
	}

	private void setChangeSetProcessingMode(final ThreadLocalMap<String, Object> metadataMap, final String stageArg) throws ConfigurationException {
		if (StringUtils.isBlank(stageArg)) {
			addToChangeSetAndImport(metadataMap);
		} else 	if ("stage1".equals(stageArg)) {
			addToChangeSetOnly(metadataMap);
		} else if ("stage2".equals(stageArg)) {
			importOnly(metadataMap);
		} else {
			throw new ConfigurationException(stageArg + " is not a valid argument. Accepted stages: stage1, stage2");
		}
	}

	private void addToChangeSetAndImport(
			final ThreadLocalMap<String, Object> metadatMap) {
		metadatMap.put("changeSetOperation", OperationEnum.OPERATIONAL);
		metadatMap.put("importOperation", OperationEnum.OPERATIONAL);
	}

	private void addToChangeSetOnly(
			final ThreadLocalMap<String, Object> metadataMap) {
		metadataMap.put("changeSetOperation", OperationEnum.OPERATIONAL);
		metadataMap.put("importOperation", OperationEnum.NONOPERATIONAL);
	}

	private void importOnly(
			final ThreadLocalMap<String, Object> metadataMap) {
		metadataMap.put("changeSetOperation", OperationEnum.NONOPERATIONAL);
		metadataMap.put("importOperation", OperationEnum.OPERATIONAL);
	}
	
	private static String getConfigurationFileName(final CommandLine cmd, final String defaultConfiguration) {
		String configurationFileName = defaultConfiguration;
		if (cmd.hasOption("c")) {
			configurationFileName = cmd.getOptionValue("c");
		}
		return configurationFileName;
	}

	/**
	 * Does the Import.
	 * 
	 * @param configFileName the name of file containing import configuration
	 * @return result code: SUCCESS, PARTIAL_SUCCESS, WRONG_CONFIGURATION or COMPLETE_FAIL
	 * @throws FileNotFoundException in case configuration hasn't been loaded
	 */
	public int doImport(final String configFileName) throws FileNotFoundException {
		
		ImportController controller = getElasticPath().getBean(ImportExportContextIdNames.IMPORT_CONTROLLER);
		FileInputStream configStream = null;
		try {
			processChangeSet();
			
			configStream = new FileInputStream(configFileName);
			controller.loadConfiguration(configStream);
			LOG.debug("Import Controller Prepared.");
			LOG.info("Import Started.");
			Summary summary = controller.executeImport();
			printResults(summary);
			LOG.info("Import Executed.");
			if (controller.failuresExist()) {
				return PARTIAL_SUCCESS; 
			}
			return SUCCESS;
		} catch (ConfigurationException e) {
			LOG.error("There are configuration problems on import", e);
			return WRONG_CONFIGURATION;
		} catch (ImportRuntimeException e) {
			LOG.error("Fatal error during import. See log for details.", e);
			LOG.error(e.getIEMessage());
			return COMPLETE_FAIL;
		} catch (RuntimeException e) {
			LOG.error("Fatal error during import", e);
			return COMPLETE_FAIL;
		} finally {
			if (configStream != null) {
				try {
					configStream.close();
				} catch (IOException e) {
					LOG.error("IOException closing the configuration file stream", e);
				}
			}
		}

	}

	/**
	 * Does the Export.
	 * 
	 * @param configFileName the name of file containing export configuration
	 * @param searchCriteriaFileName the name of file containing export search query 
	 * @return result code: SUCCESS, PARTIAL_SUCCESS, WRONG_CONFIGURATION or COMPLETE_FAIL
	 * @throws FileNotFoundException  in case configuration hasn't been loaded
	 */
	public int doExport(final String configFileName, final String searchCriteriaFileName) throws FileNotFoundException {
		ExportController controller = getElasticPath().getBean(ImportExportContextIdNames.EXPORT_CONTROLLER);		
		FileInputStream configStream = null;
		FileInputStream searchCriteriaStream = null;
		try {
			configStream = new FileInputStream(configFileName);
			searchCriteriaStream = new FileInputStream(searchCriteriaFileName);
			controller.loadConfiguration(configStream, searchCriteriaStream);
			LOG.debug("Export Controller Prepared.");
			LOG.info("Export Started.");
			Summary summary = controller.executeExport();
			printResults(summary);
			LOG.info("Export Executed.");
			if (controller.failuresExist()) {
				return PARTIAL_SUCCESS;
			}
			return SUCCESS;
		} catch (ConfigurationException e) {
			LOG.error("There are configuration problems on export", e);
			return WRONG_CONFIGURATION;
		} catch (RuntimeException e) {
			LOG.error("Fatal error during export", e);
			return COMPLETE_FAIL;
		} finally {
			if (configStream != null) {
				try {
					configStream.close();
				} catch (IOException e) {
					LOG.error("IOException closing the configuration file stream", e);
				}
			}
			if (searchCriteriaStream != null) {
				try {
					searchCriteriaStream.close();
				} catch (IOException e) {
					LOG.error("IOException closing the search criteria file stream", e);
				}
			}
		}
	}

	private void printResults(final Summary summary) {
		final SimpleSummaryLayout layout = new SimpleSummaryLayout();
		layout.setMessageResolver(messageResolver);
		LOG.info(layout.format(summary));
	}

	private ElasticPath getElasticPath() {
		return engine.getElasticPath();
	}
	
	private void setLocale(final Locale locale) {
		messageResolver.setLocale(locale);
	}
	
	private void setStage(final String stage) {
		this.stage = stage;
	}

	private void setChangeSetGuid(final String changeSetGuid) {
		this.changeSetGuid = changeSetGuid;
	}

	private static Locale parseLocale(final CommandLine cmd) {
		Locale locale = LocaleUtils.toLocale(cmd.getOptionValue("l"));
		if (!LocaleUtils.isAvailableLocale(locale)) {
			throw new IllegalArgumentException("Could not find locale with code " + locale + ". Locale is not supported.");
		}
		return locale;
	}
	
	private static Index createIndex(final CommandLine cmd) {
		Locale locale = null;
		if (cmd.hasOption("l")) {
			locale = parseLocale(cmd);
		}
		Index index = new Index();
		if (locale != null) {
			index.setLocale(locale);
		}
		if (cmd.hasOption("g")) {
			index.setChangeSetGuid(cmd.getOptionValue("g"));
		}
		if (cmd.hasOption("s")) {
			index.setStage(cmd.getOptionValue("s"));
		}
		return index;
	}

	private void configureLogRendering() {
		LoggerRepository loggerRepository = LogManager.getLoggerRepository();
		if (loggerRepository instanceof RendererSupport) {
			((RendererSupport) loggerRepository).setRenderer(Message.class, new ObjectRenderer() {

				public String doRender(final Object message) {
					Message theMessage = (Message) message;
					return messageResolver.resolve(theMessage) + getExceptionInfo(theMessage.getException());
				}
				
				private String getExceptionInfo(final Throwable exception) {
					if (exception == null) {
						return "";
					}
					StringWriter stringWriter = new StringWriter();
					stringWriter.write(". Associated exception: ");
					exception.printStackTrace(new PrintWriter(stringWriter));
					return stringWriter.toString();
				}

			});
		}
	}
}
