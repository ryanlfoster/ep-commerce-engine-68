/*
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.util.cryptotool;

import java.util.Arrays;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang.StringUtils;

import com.elasticpath.commons.util.security.impl.SimpleEncryption;

/**
 * Driver/configuration-interface for the credit card encryption tool.
 */
@SuppressWarnings({ "PMD.SystemPrintln", "PMD.CyclomaticComplexity", "PMD.DoNotCallSystemExit" })
public class CryptoTool {

	// Used for decrypting, encrypting, in case we need to change the key
	private static SimpleEncryption encryption;

	private static SimpleEncryption decryption;

	private static SimpleFilter filter;

	private static ColumnUpdater updater;

	private static final int FETCH_SIZE = 100;

	// option names
	private static final String DECRYPTKEY = "decryptKey";

	private static final String ENCRYPTKEY = "encryptKey";

	private static final String MASK = "mask";

	private static final String DRYRUN = "dryRun";

	private static final String HELP = "help";

	private static final String DBPASS = "dbPass";

	private static final String DBUSER = "dbUser";

	private static final String DBURL = "dbUrl";

	private static final String JDBCDRIVER = "jdbcDriver";

	private static final String TABLENAME = "tableName";

	private static final String SRC_COLUMNNAME = "srcColumnName";

	private static final String DEST_COLUMNNAME = "destColumnName";

	private static final String VERBOSE = "verbose";

	private static final String STORES = "stores";

	private static final String NULL_SRC_COLUMN = "nullSrcColumn";

	/**
	 * The main method--the entry-point of the tool. This method accepts configuration parameters from the command line and sets
	 * up the rest of the app.
	 * 
	 * @param args an array of command line arguments
	 */
	public static void main(final String[] args) {
		CryptoTool cTool = new CryptoTool();

		Options options = cTool.mkOptions();
		CommandLine cmd = cTool.parseCommandLine(args, options);
		if (cmd == null) {
			System.exit(1);
		}

		if (cmd.hasOption(HELP) || !cTool.validateCommandLine(cmd)) {
			cTool.printHelp(options);
			System.exit(0);
		}

		// Print out a 'welcome' message
		System.out.println("Elastic Path CryptoTool\n");

		updater.doUpdate();
	}

	/**
	 * Validates the options of the given {@link CommandLine}. 
	 *
	 * @param cmd the {@link CommandLine} object to check
	 * @return <code>true</code> if the vaidation passes; <code>false</code> otherwise
	 */
	private boolean validateCommandLine(final CommandLine cmd) {
		if (!hasReqOptions(cmd)) {
			System.err.println("Error: Missing required database connection options.");
			return false;
		}

		if (cmd.hasOption(STORES)
				&& (cmd.hasOption(TABLENAME) || cmd.hasOption(SRC_COLUMNNAME) || cmd.hasOption(DEST_COLUMNNAME) || cmd.hasOption(NULL_SRC_COLUMN))) {
			System.err.println("Error: Cannot specify tablename or column options when the store option is already specified.");
			return false;
		}

		if (cmd.hasOption(SRC_COLUMNNAME) && cmd.hasOption(DEST_COLUMNNAME)
				&& (cmd.getOptionValue(SRC_COLUMNNAME).equals(cmd.getOptionValue(DEST_COLUMNNAME))) && cmd.hasOption(NULL_SRC_COLUMN)) {
			System.err.println("Error: Source and destination columns cannot be the same when the nullSrcColumn option is specified.");
			return false;
		}

		return true;
	}

	/**
	 * Display usage instructions for the tool.
	 * 
	 * @param options {@link Options} class contains all the data needed to generate instructions.
	 */
	private void printHelp(final Options options) {
		// automatically generate the help statement
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("java CryptoTool <options>", options);
	}

	/**
	 * Turn the application's argument array into a {@link CommandLine} object.
	 * 
	 * @param args an array of command line arguments
	 * @param options The Options object
	 * @return an initialized CommandLine object or null if there's a problem
	 */
	private CommandLine parseCommandLine(final String[] args, final Options options) {
		CommandLine cmd = null;
		CommandLineParser parser = new GnuParser();

		try {
			cmd = parser.parse(options, args);
		} catch (ParseException exp) {
			System.err.println("parsing failed. reason: " + exp.getMessage());
			return null;
		}

		if (cmd.hasOption(ENCRYPTKEY)) {
			encryption = new SimpleEncryption(cmd.getOptionValue(ENCRYPTKEY));
		}
		if (cmd.hasOption(DECRYPTKEY)) {
			decryption = new SimpleEncryption(cmd.getOptionValue(DECRYPTKEY));
		}

		filter = new SimpleFilter(decryption, encryption, cmd.hasOption(MASK));
		updater = new ResultSetUpdater(filter, FETCH_SIZE, cmd.hasOption(VERBOSE), cmd.hasOption(DRYRUN));

		if (cmd.hasOption(TABLENAME)) {
			updater.setTableName(cmd.getOptionValue(TABLENAME));
		}
		if (cmd.hasOption(SRC_COLUMNNAME)) {
			updater.setSrcColumnName(cmd.getOptionValue(SRC_COLUMNNAME));
		}
		if (cmd.hasOption(DEST_COLUMNNAME)) {
			updater.setDestColumnName(cmd.getOptionValue(DEST_COLUMNNAME));
		}
		if (cmd.hasOption(STORES)) {
			updater.setStores(Arrays.asList(StringUtils.split(cmd.getOptionValue(STORES), ',')));
			// if the 'stores' option is specified, null the source column
			updater.setNullSrcColumnFlag(true);
		}
		if (cmd.hasOption(NULL_SRC_COLUMN)) {
			updater.setNullSrcColumnFlag(cmd.hasOption(NULL_SRC_COLUMN));
		}

		updater.setJdbcDriver(cmd.getOptionValue(JDBCDRIVER));
		updater.setDbUrl(cmd.getOptionValue(DBURL));
		updater.setDbPass(cmd.getOptionValue(DBPASS));
		updater.setDbUser(cmd.getOptionValue(DBUSER));

		return cmd;
	}

	/**
	 * Return true if the command line has the required options.
	 * 
	 * @param cmd the command line object
	 * @return true if the command line has the required options
	 */
	private boolean hasReqOptions(final CommandLine cmd) {
		if (!cmd.hasOption(JDBCDRIVER)) {
			return false;
		}
		if (!cmd.hasOption(DBURL)) {
			return false;
		}
		if (!cmd.hasOption(DBPASS)) {
			return false;
		}
		if (!cmd.hasOption(DBUSER)) {
			return false;
		}
		if (cmd.hasOption(STORES)) {
			return true;
		}

		return cmd.hasOption(TABLENAME) && cmd.hasOption(SRC_COLUMNNAME) && cmd.hasOption(DEST_COLUMNNAME);
	}

	@SuppressWarnings({"static", "static-access"})
	private Options mkOptions() {
		final Option encryptKey = OptionBuilder.withArgName("key").hasArg().withDescription("key used to encrypt each field").create(ENCRYPTKEY);
		final Option decryptKey = OptionBuilder.withArgName("key").hasArg().withDescription("key used to decrypt each field").create(DECRYPTKEY);
		final Option jdbcDriver = OptionBuilder.withArgName("driver string").hasArg().withDescription("jdbc driver string").create(JDBCDRIVER);
		final Option dbUrl = OptionBuilder.withArgName("url").hasArg().withDescription("the database url").create(DBURL);
		final Option dbUser = OptionBuilder.withArgName("username").hasArg().withDescription("database user username").create(DBUSER);
		final Option dbPass = OptionBuilder.withArgName("password").hasArg().withDescription("database user password").create(DBPASS);
		final Option tableName = OptionBuilder.withArgName("name").hasArg().withDescription("database table").create(TABLENAME);
		final Option srcColumnName = OptionBuilder.withArgName("name").hasArg().withDescription("source database column").create(SRC_COLUMNNAME);
		final Option destColumnName = OptionBuilder.withArgName("name").hasArg().withDescription("destination database column").create(
				DEST_COLUMNNAME);
		final Option stores = OptionBuilder.withArgName("stores").hasArg().withDescription("a comma-separated list of store codes").create(STORES);
		final Option mask = new Option(MASK, "mask card numbers");
		final Option dryRun = new Option(DRYRUN, "don't update the database, just print informative messages");
		final Option help = new Option(HELP, "print help listing");
		final Option verbose = new Option(VERBOSE, "verbose output");
		final Option nullSrcColumn = new Option(NULL_SRC_COLUMN, "null the source column values");

		Options options = new Options();

		options.addOption(encryptKey);
		options.addOption(decryptKey);
		options.addOption(mask);
		options.addOption(dryRun);
		options.addOption(help);
		options.addOption(jdbcDriver);
		options.addOption(dbUrl);
		options.addOption(dbUser);
		options.addOption(dbPass);
		options.addOption(tableName);
		options.addOption(srcColumnName);
		options.addOption(destColumnName);
		options.addOption(stores);
		options.addOption(verbose);
		options.addOption(nullSrcColumn);

		return options;
	}
}