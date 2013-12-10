package com.elasticpath.tools.epcoretool.client;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.elasticpath.tools.epcoretool.client.parsers.BulkSetSettingParser;
import com.elasticpath.tools.epcoretool.client.parsers.IndexBuildStatusParser;
import com.elasticpath.tools.epcoretool.client.parsers.PingSearchServerParser;
import com.elasticpath.tools.epcoretool.client.parsers.RecompileRuleBaseParser;
import com.elasticpath.tools.epcoretool.client.parsers.RequestReindexParser;
import com.elasticpath.tools.epcoretool.client.parsers.SetCmUserPasswordParser;
import com.elasticpath.tools.epcoretool.client.parsers.SetSettingParser;
import com.elasticpath.tools.epcoretool.client.parsers.UnSetSettingParser;

/**
 * EP Core Tool CLI Main Class.
 */
@SuppressWarnings("PMD.DoNotCallSystemExit") // This is a not a J2EE app
public final class Cli {

	private static final Logger LOG = Logger.getLogger(Cli.class);

	private static final int SUCCESS = 0;

	private static final int WRONG_COMMAND_LINE_ARGUMENTS = 2;

	private static final int COMPLETE_FAIL = 8;

	private static Map<String, CliParser> parsers;
	
	private Cli() {
		// Do not allow this class to be instantiated directly.
	}

	/**
	 * Starts this application with given arguments.
	 * 
	 * @param args the available arguments
	 */
	public static void main(final String[] args) {
		// Initialize log4j
		PropertyConfigurator.configure(LoadPropertiesHelper.loadProperties("misc/log4j.properties"));

		parsers = new HashMap<String, CliParser>();
		parsers.put("bulk-set-settings", new BulkSetSettingParser());
		parsers.put("index-status", new IndexBuildStatusParser());
		parsers.put("ping-search", new PingSearchServerParser());
		parsers.put("recompile-rulebase", new RecompileRuleBaseParser());
		parsers.put("request-reindex", new RequestReindexParser());
		parsers.put("set-setting", new SetSettingParser());
		parsers.put("set-cmuser-password", new SetCmUserPasswordParser());
		parsers.put("unset-setting", new UnSetSettingParser());

		try {
			if (args.length == 0) {
				LOG.error("Please specify a command. \nEnter 'help' as a command-line argument for list of available commands.");
				System.exit(WRONG_COMMAND_LINE_ARGUMENTS);
			}

			if (args[0].equals("help")) {
				help();
			}

			CliParser parser = parsers.get(args[0]);

			if (parser == null) {
				LOG.error("Please specify a valid command. \nEnter 'help' as a command-line argument for list of available commands.");
				System.exit(WRONG_COMMAND_LINE_ARGUMENTS);
			}

			if (args.length == 1) {
				parser.execute(null);
			} else {
				parser.execute(args[1]);
			}
		} catch (RuntimeException ex) {
			LOG.error("Unexpected error occurred.", ex);
			System.exit(COMPLETE_FAIL);
		}
		System.exit(SUCCESS);
	}

	/**
	 * CLI help command.
	 */
	public static void help() {
		// LOG.error("Please specify one of the following command line parameters:\n" + StringUtils.join(parsers.keySet(), ", "));
		StringBuilder builder = new StringBuilder("");
		for (CliParser parser : parsers.values()) {
			builder.append(parser.help());
		}
		LOG.info("\n" + builder.toString());
		System.exit(SUCCESS);
	}
}
