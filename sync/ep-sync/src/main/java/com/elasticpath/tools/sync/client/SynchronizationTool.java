package com.elasticpath.tools.sync.client;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.AlreadySelectedException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

import com.elasticpath.tools.sync.exception.SyncToolConfigurationException;
import com.elasticpath.tools.sync.target.result.Summary;
import com.elasticpath.tools.sync.target.result.SyncErrorResultItem;
import com.elasticpath.tools.sync.target.result.SyncResultItem;

/**
 * This is the entry point for the synchronization tool. 
 * The tool reads the input from the command line, resolves source and target environment which may be any
 * combination of local or remote location.<br>
 */
@SuppressWarnings("PMD.SystemPrintln")
public final class SynchronizationTool {
	
	private static final String DEFAULT_SOURCECONF_XML = "sourceconfig.xml";

	private static final String DEFAULT_TARGETCONF_XML = "targetconfig.xml";
	
	private static final String OPTION_ROOT_PATH = "r";

	private static final String OPTION_SUB_DIR = "d";

	private static final String OPTION_ADAPTER_PARAM = "p";
	
	private static final String OPTION_SOURCE = "s";
	
	private static final String OPTION_TARGET = "t";

	private static final String OPTION_HELP = "h";

	private static final String OPTION_PROCESS_LOAD = "l";

	private static final String OPTION_PROCESS_EXPORT = "e";

	private static final String OPTION_PROCESS_FULL = "f";

	private static final List<String> REQUIRED_OPTIONS = Arrays.asList(OPTION_PROCESS_FULL, OPTION_PROCESS_EXPORT, OPTION_PROCESS_LOAD, OPTION_HELP);

	private static final String MISSING_OPTION_MESSAGE = "At least one process option " + REQUIRED_OPTIONS + " should be selected.\n"
					+ " Option [" + OPTION_ADAPTER_PARAM + "] is required for " + Arrays.asList(OPTION_PROCESS_FULL, OPTION_PROCESS_EXPORT) + "\n" 
					+ " Option [" + OPTION_ROOT_PATH + "] is required for " + Arrays.asList(OPTION_PROCESS_LOAD, OPTION_PROCESS_EXPORT) + "\n"
					+ " Option [" + OPTION_SUB_DIR + "] is required for [" + OPTION_PROCESS_LOAD + "]";

	private static final String HELP_STRING = "SynchronizationTool <options>";
	
	private static final String HELP_FOOTER = "\n\nsynctool.bat -f [-a <AdapterName>] -p <AdapterParameters> -r \"/root/directory\"\n"
											+ "synctool.bat -f [-a <AdapterName>] -p <AdapterParameters>\n"
											+ "synctool.bat -e [-a <AdapterName>] -p <AdapterParameters> -r \"/root/directory\"\n"
											+ "synctool.bat -l -r \"/root/directory/\" -d \"dataFileName\"\n\n";

	/**
	 * Entry point to the sync tool.
	 * @param args command line args:
	 * <table>
	 *  <tr>
	 *  	<td>Command:</td>
	 *  	<td>Parameter:</td>
	 *  	<td>Description:</td>
	 *  </tr>
	 * 	<tr>
	 * 		<td>(-h, --help)</td>
	 * 		<td>(none)</td>
	 * 		<td>prints Help</td>
	 * 	</tr>
	 *  <tr>
	 *  	<td>(-f, --full)</td>
	 *  	<td>(none)</td>
	 *  	<td>means "full" process including loading data from staging and merging it onto production</td>
	 *  </tr>  
	 *  <tr>
	 *  	<td>(-e, --export)</td>
	 *  	<td>(none)</td>
	 *  	<td>means "export" data to synchronize into files</td>
	 *  </tr>
	 *  <tr>
	 *  	<td>(-l, --load)</td>
	 *  	<td>(none)</td>
	 *  	<td>means "load" data from files prepared before into target </td>
	 *  </tr>
	 *  <tr>
	 *  	<td>(-r, --root)</td>  	
	 *  	<td>&lt;dir name&gt;</td>
	 *  	<td>means "root" directory</td></tr>
	 *  <tr>
	 *  	<td>(-d, --subdir)</td>
	 *  	<td>&lt;dir name&gt;</td>
	 *  	<td>means "sub directory" to load all files from</td>
	 *  </tr>
	 *  <tr>
	 *  	<td>(-p, --param)</td>
	 *  	<td>&lt;adapter parameter string&gt;</td>
	 *  	<td>the adapter parameter string can define the path and adapter config file name or key value parameters 
	 *  		for a specify adapter. It is Change Set name for ChangeSetAdapter</td>
	 *  </tr>
	 *  <tr>
	 *  	<td>(-s, --source)</td>
	 *  	<td>&lt;sourceconfig.xml&gt;</td>
	 *  	<td>the file contains the parameters to get the access to the source</td>
	 *  </tr>
	 *  <tr>
	 *  	<td>(-t, --target)</td>
	 *  	<td>&lt;targetconfig.xml&gt;</td>
	 *  	<td>the file contains the parameters to get the access to the source</td>
	 *  </tr>
	 * </table>
	 *   
	 * @throws SyncToolConfigurationException if there is misconfiguration.
	 */
	public static void main(final String[] args) throws SyncToolConfigurationException {
		SynchronizationTool syncTool = new SynchronizationTool();
		
		Options options = createOptions();
		
		CommandLineParser parser = new GnuParser();

		try {
			CommandLine commandLine = parser.parse(options, args);
			if (commandLine.hasOption(OPTION_HELP)) {
				printHelp(options);
				return;
			}
			syncTool.processCommandLine(commandLine);
			
		} catch (AlreadySelectedException e) {
			printMessage("Only one option of " + REQUIRED_OPTIONS + " should be selected at once");
			printHelp(options);
		} catch (MissingSyncToolOptionException e) {
			printMessage(MISSING_OPTION_MESSAGE);
			printHelp(options);
		} catch (ParseException e) {
			printMessage("Error : " + e.getMessage());
			printHelp(options);
		}
	}

	/**
	 * Verifies whether all the required arguments are in place.
	 */
	private void verifyConfiguration(final SyncToolConfiguration configuration) {
		if (ObjectUtils.equals(configuration.getControllerType(), SyncToolControllerType.EXPORT_CONTROLLER)) {
			assertValuesNotNull(configuration.getAdapterParameter(), configuration.getRootPath());
		} else if (ObjectUtils.equals(configuration.getControllerType(), SyncToolControllerType.FULL_CONTROLLER)) {
			assertValuesNotNull(configuration.getAdapterParameter());
		} else if (ObjectUtils.equals(configuration.getControllerType(), SyncToolControllerType.LOAD_CONTROLLER)) {
			assertValuesNotNull(configuration.getRootPath(), configuration.getSubDir());
		}
	}

	/**
	 *
	 */
	private void assertValuesNotNull(final String... values) {
		for (String value : values) {
			if (value == null) {
				throw new MissingSyncToolOptionException("Missing a required argument");
			}
		}
	}

	/**
	 * Create the command line options.
	 * 
	 * @return the command line options 
	 */
	public static Options createOptions() {
		Options options = new Options();
		
		OptionGroup group = new OptionGroup();
		
		group.addOption(
				new Option(OPTION_PROCESS_FULL, "full", false, "full process including loading data from staging and merging it onto production"));
		group.addOption(new Option(OPTION_PROCESS_EXPORT, "export", false, "export data to synchronize into files"));
		group.addOption(new Option(OPTION_PROCESS_LOAD, "load", false, "load data from files prepared before into target"));
		group.addOption(new Option(OPTION_HELP, "help", false, "prints help"));
		group.setRequired(true);
		
		options.addOptionGroup(group);
		
		options.addOption(OPTION_ROOT_PATH, "root", true, "root directory");
		options.addOption(OPTION_SUB_DIR, "subdir", true, "sub directory to load all files from");
		options.addOption(OPTION_ADAPTER_PARAM, "param", true, 
				"adapter parameter string can define the path and adapter config file name or key value parameters for a specify adapter.");
		
		options.addOption(OPTION_SOURCE, "source", true, 
				"specifies the file with connection configuration for source system. sourceconfig.xml is default");
		options.addOption(OPTION_TARGET, "target", true, 
				"specifies the file with connection configuration for target system. targetconfig.xml is default");
		
		return options;
	}

	/**
	 * Creates a sync tool configuration, verifies the configuration and launches the sync tool.
	 * 
	 * @param commandLine the command line
	 * @throws SyncToolConfigurationException on error
	 */
	private void processCommandLine(final CommandLine commandLine) throws SyncToolConfigurationException {
		
		SyncToolConfiguration configuration = new CommandLineConfiguration(commandLine);
		// verify the configuration
		verifyConfiguration(configuration);

		// create a launcher and launch the tool
		SyncToolLauncher launcher = new SyncToolLauncher();
		Summary resultSummary = launcher.launch(configuration);
		// process the result summary
		processSummary(resultSummary);
	}
	
	private void processSummary(final Summary summary) {
		System.out.println("\nSummary:");
		for (SyncResultItem resultItem : summary.getSuccessResults()) {
			System.out.println(resultItem);
		}
		if (summary.hasErrors()) {
			System.err.println(summary.getNumberOfErrors() + " errors found");
			for (SyncErrorResultItem errorItem : summary.getSyncErrors()) {
				System.err.println(errorItem);
			}
		}
	}

	private static void printHelp(final Options options) {
		HelpFormatter formatter = new HelpFormatter();
		//formatter.printHelp(HELP_STRING, options);
		formatter.printHelp(HELP_STRING, HELP_FOOTER, options, StringUtils.EMPTY);
	}

	/**
	 * Prints message to standard error output stream, for pure console output as a Tool.
	 * @param message the text message.
	 */
	private static void printMessage(final String message) {
		System.err.println(message);
	}

	/**
	 * A configuration based on the command line parameters.
	 */
	public class CommandLineConfiguration implements SyncToolConfiguration {

		private final CommandLine commandLine;

		/**
		 * Constructor.
		 * 
		 * @param commandLine the command line
		 */
		public CommandLineConfiguration(final CommandLine commandLine) {
			this.commandLine = commandLine;
		}

		@Override
		public String getAdapterParameter() {
			return getParameter(OPTION_ADAPTER_PARAM);
		}

		/**
		 * Determines the controller type out of the passed option.
		 * 
		 * @return the controller type
		 */
		public SyncToolControllerType getControllerType() {
			if (commandLine.hasOption(OPTION_PROCESS_LOAD)) {
				return SyncToolControllerType.LOAD_CONTROLLER;
			} else if (commandLine.hasOption(OPTION_PROCESS_FULL)) {
				if (getRootPath() == null) {
					return SyncToolControllerType.FULL_CONTROLLER;
				}
				return SyncToolControllerType.FULL_AND_SAVE_CONTROLLER;
			} else if (commandLine.hasOption(OPTION_PROCESS_EXPORT)) {
				return SyncToolControllerType.EXPORT_CONTROLLER;
			}
			
			throw new MissingSyncToolOptionException("No option is specified for the requested operation.");
		}

		@Override
		public String getRootPath() {
			return getParameter(OPTION_ROOT_PATH, null);
		}

		@Override
		public String getSourceConfigName() {
			return getParameter(OPTION_SOURCE, DEFAULT_SOURCECONF_XML);
		}

		@Override
		public String getSubDir() {
			return getParameter(OPTION_SUB_DIR);
		}

		@Override
		public String getTargetConfigName() {
			return getParameter(OPTION_TARGET, DEFAULT_TARGETCONF_XML);
		}

		private String getParameter(final String option) throws MissingSyncToolOptionException {
			if (commandLine.hasOption(option)) {
				return commandLine.getOptionValue(option); // if it is not found default value will be returned
			}		
			throw new MissingSyncToolOptionException("Mising [" + option + "]");
		}	

		private String getParameter(final String option, final String defaultValue) {
			return commandLine.getOptionValue(option, defaultValue); // if it is not found default value will be returned
		}
	}
}
