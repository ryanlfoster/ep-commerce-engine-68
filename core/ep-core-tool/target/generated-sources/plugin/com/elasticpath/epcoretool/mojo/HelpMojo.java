package com.elasticpath.epcoretool.mojo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * Display help information on ep-core-tool.<br/> Call <pre>  mvn ep-core-tool:help -Ddetail=true -Dgoal=&lt;goal-name&gt;</pre> to display parameter details.
 *
 * @version generated on Tue Dec 10 12:28:14 EST 2013
 * @author org.apache.maven.tools.plugin.generator.PluginHelpGenerator (version 2.9)
 * @goal help
 * @requiresProject false
 * @threadSafe
 */
@SuppressWarnings({"cast", "classfile", "deprecation", "dep-ann", "divzero",
					"empty", "fallthrough", "finally", "options", "overrides",
					"path", "processing", "rawtypes", "serial", "static",
					"try", "unchecked", "varargs", "PMD"}) public class HelpMojo
    extends AbstractMojo
{
    /**
     * If <code>true</code>, display all settable properties for each goal.
     * 
     * @parameter expression="${detail}" default-value="false"
     */
    private boolean detail;

    /**
     * The name of the goal for which to show help. If unspecified, all goals will be displayed.
     * 
     * @parameter expression="${goal}"
     */
    private java.lang.String goal;

    /**
     * The maximum length of a display line, should be positive.
     * 
     * @parameter expression="${lineLength}" default-value="80"
     */
    private int lineLength;

    /**
     * The number of spaces per indentation level, should be positive.
     * 
     * @parameter expression="${indentSize}" default-value="2"
     */
    private int indentSize;


    /** {@inheritDoc} */
    public void execute()
        throws MojoExecutionException
    {
        if ( lineLength <= 0 )
        {
            getLog().warn( "The parameter 'lineLength' should be positive, using '80' as default." );
            lineLength = 80;
        }
        if ( indentSize <= 0 )
        {
            getLog().warn( "The parameter 'indentSize' should be positive, using '2' as default." );
            indentSize = 2;
        }

        StringBuffer sb = new StringBuffer();

        append( sb, "com.elasticpath.tools:ep-core-tool:6.8.0.2013101613-RELEASE", 0 );
        append( sb, "", 0 );

        append( sb, "EP Core Tool Maven", 0 );
        append( sb, "Elastic Path Digital Commerce Platform", 1 );
        append( sb, "", 0 );

        if ( goal == null || goal.length() <= 0 )
        {
            append( sb, "This plugin has 9 goals:", 0 );
            append( sb, "", 0 );
        }

        if ( goal == null || goal.length() <= 0 || "bulk-set-settings".equals( goal ) )
        {
            append( sb, "ep-core-tool:bulk-set-settings", 0 );
            append( sb, "Updates the setting value in the Elastic Path database. If a value already exists, it will be removed before being re-added.", 1 );
            append( sb, "", 0 );
            if ( detail )
            {
                append( sb, "Available parameters:", 1 );
                append( sb, "", 0 );

                append( sb, "jdbcConnectionPoolMaxIdle", 2 );
                append( sb, "Maximum number of idle connections permitted in the connection pool, or negative for no limit. Set this property and jdbcConnectionPoolMinIdle to 0 to prohibit idle connections.", 3 );
                append( sb, "Expression: ${epdb.jdbc.max.idle}", 3 );
                append( sb, "", 0 );

                append( sb, "jdbcConnectionPoolMinIdle", 2 );
                append( sb, "Minimum number of idle connections permitted in the connection pool. Set this property and jdbcConnectionPoolMaxIdle to 0 to prohibit idle connections.", 3 );
                append( sb, "Expression: ${epdb.jdbc.min.idle}", 3 );
                append( sb, "", 0 );

                append( sb, "jdbcDriverClass", 2 );
                append( sb, "Database JDBC Driver Class.", 3 );
                append( sb, "Required: Yes", 3 );
                append( sb, "Expression: ${epdb.jdbc.driver}", 3 );
                append( sb, "", 0 );

                append( sb, "jdbcPassword", 2 );
                append( sb, "Database Password.", 3 );
                append( sb, "Expression: ${epdb.password}", 3 );
                append( sb, "", 0 );

                append( sb, "jdbcUrl", 2 );
                append( sb, "JDBC URL.", 3 );
                append( sb, "Required: Yes", 3 );
                append( sb, "Expression: ${epdb.url}", 3 );
                append( sb, "", 0 );

                append( sb, "jdbcUsername", 2 );
                append( sb, "Database Username.", 3 );
                append( sb, "Expression: ${epdb.username}", 3 );
                append( sb, "", 0 );

                append( sb, "settings", 2 );
                append( sb, "(no description available)", 3 );
                append( sb, "Required: Yes", 3 );
                append( sb, "", 0 );

                append( sb, "skip (Default: false)", 2 );
                append( sb, "(no description available)", 3 );
                append( sb, "Required: Yes", 3 );
                append( sb, "", 0 );
            }
        }

        if ( goal == null || goal.length() <= 0 || "help".equals( goal ) )
        {
            append( sb, "ep-core-tool:help", 0 );
            append( sb, "Display help information on ep-core-tool.\nCall\n\u00a0\u00a0mvn\u00a0ep-core-tool:help\u00a0-Ddetail=true\u00a0-Dgoal=<goal-name>\nto display parameter details.", 1 );
            append( sb, "", 0 );
            if ( detail )
            {
                append( sb, "Available parameters:", 1 );
                append( sb, "", 0 );

                append( sb, "detail (Default: false)", 2 );
                append( sb, "If true, display all settable properties for each goal.", 3 );
                append( sb, "Expression: ${detail}", 3 );
                append( sb, "", 0 );

                append( sb, "goal", 2 );
                append( sb, "The name of the goal for which to show help. If unspecified, all goals will be displayed.", 3 );
                append( sb, "Expression: ${goal}", 3 );
                append( sb, "", 0 );

                append( sb, "indentSize (Default: 2)", 2 );
                append( sb, "The number of spaces per indentation level, should be positive.", 3 );
                append( sb, "Expression: ${indentSize}", 3 );
                append( sb, "", 0 );

                append( sb, "lineLength (Default: 80)", 2 );
                append( sb, "The maximum length of a display line, should be positive.", 3 );
                append( sb, "Expression: ${lineLength}", 3 );
                append( sb, "", 0 );
            }
        }

        if ( goal == null || goal.length() <= 0 || "index-status".equals( goal ) )
        {
            append( sb, "ep-core-tool:index-status", 0 );
            append( sb, "Display the current search server index rebuild status.", 1 );
            append( sb, "", 0 );
            if ( detail )
            {
                append( sb, "Available parameters:", 1 );
                append( sb, "", 0 );

                append( sb, "jdbcConnectionPoolMaxIdle", 2 );
                append( sb, "Maximum number of idle connections permitted in the connection pool, or negative for no limit. Set this property and jdbcConnectionPoolMinIdle to 0 to prohibit idle connections.", 3 );
                append( sb, "Expression: ${epdb.jdbc.max.idle}", 3 );
                append( sb, "", 0 );

                append( sb, "jdbcConnectionPoolMinIdle", 2 );
                append( sb, "Minimum number of idle connections permitted in the connection pool. Set this property and jdbcConnectionPoolMaxIdle to 0 to prohibit idle connections.", 3 );
                append( sb, "Expression: ${epdb.jdbc.min.idle}", 3 );
                append( sb, "", 0 );

                append( sb, "jdbcDriverClass", 2 );
                append( sb, "Database JDBC Driver Class.", 3 );
                append( sb, "Required: Yes", 3 );
                append( sb, "Expression: ${epdb.jdbc.driver}", 3 );
                append( sb, "", 0 );

                append( sb, "jdbcPassword", 2 );
                append( sb, "Database Password.", 3 );
                append( sb, "Expression: ${epdb.password}", 3 );
                append( sb, "", 0 );

                append( sb, "jdbcUrl", 2 );
                append( sb, "JDBC URL.", 3 );
                append( sb, "Required: Yes", 3 );
                append( sb, "Expression: ${epdb.url}", 3 );
                append( sb, "", 0 );

                append( sb, "jdbcUsername", 2 );
                append( sb, "Database Username.", 3 );
                append( sb, "Expression: ${epdb.username}", 3 );
                append( sb, "", 0 );

                append( sb, "skip (Default: false)", 2 );
                append( sb, "(no description available)", 3 );
                append( sb, "Required: Yes", 3 );
                append( sb, "", 0 );
            }
        }

        if ( goal == null || goal.length() <= 0 || "ping-search".equals( goal ) )
        {
            append( sb, "ep-core-tool:ping-search", 0 );
            append( sb, "Interact with the search server, optionally checking different queries or polling for it to be fully functioning.", 1 );
            append( sb, "", 0 );
            if ( detail )
            {
                append( sb, "Available parameters:", 1 );
                append( sb, "", 0 );

                append( sb, "jdbcConnectionPoolMaxIdle", 2 );
                append( sb, "Maximum number of idle connections permitted in the connection pool, or negative for no limit. Set this property and jdbcConnectionPoolMinIdle to 0 to prohibit idle connections.", 3 );
                append( sb, "Expression: ${epdb.jdbc.max.idle}", 3 );
                append( sb, "", 0 );

                append( sb, "jdbcConnectionPoolMinIdle", 2 );
                append( sb, "Minimum number of idle connections permitted in the connection pool. Set this property and jdbcConnectionPoolMaxIdle to 0 to prohibit idle connections.", 3 );
                append( sb, "Expression: ${epdb.jdbc.min.idle}", 3 );
                append( sb, "", 0 );

                append( sb, "jdbcDriverClass", 2 );
                append( sb, "Database JDBC Driver Class.", 3 );
                append( sb, "Required: Yes", 3 );
                append( sb, "Expression: ${epdb.jdbc.driver}", 3 );
                append( sb, "", 0 );

                append( sb, "jdbcPassword", 2 );
                append( sb, "Database Password.", 3 );
                append( sb, "Expression: ${epdb.password}", 3 );
                append( sb, "", 0 );

                append( sb, "jdbcUrl", 2 );
                append( sb, "JDBC URL.", 3 );
                append( sb, "Required: Yes", 3 );
                append( sb, "Expression: ${epdb.url}", 3 );
                append( sb, "", 0 );

                append( sb, "jdbcUsername", 2 );
                append( sb, "Database Username.", 3 );
                append( sb, "Expression: ${epdb.username}", 3 );
                append( sb, "", 0 );

                append( sb, "skip (Default: false)", 2 );
                append( sb, "(no description available)", 3 );
                append( sb, "Required: Yes", 3 );
                append( sb, "", 0 );
            }
        }

        if ( goal == null || goal.length() <= 0 || "recompile-rulebase".equals( goal ) )
        {
            append( sb, "ep-core-tool:recompile-rulebase", 0 );
            append( sb, "Recompiles the EP Promo RuleBase.", 1 );
            append( sb, "", 0 );
            if ( detail )
            {
                append( sb, "Available parameters:", 1 );
                append( sb, "", 0 );

                append( sb, "jdbcConnectionPoolMaxIdle", 2 );
                append( sb, "Maximum number of idle connections permitted in the connection pool, or negative for no limit. Set this property and jdbcConnectionPoolMinIdle to 0 to prohibit idle connections.", 3 );
                append( sb, "Expression: ${epdb.jdbc.max.idle}", 3 );
                append( sb, "", 0 );

                append( sb, "jdbcConnectionPoolMinIdle", 2 );
                append( sb, "Minimum number of idle connections permitted in the connection pool. Set this property and jdbcConnectionPoolMaxIdle to 0 to prohibit idle connections.", 3 );
                append( sb, "Expression: ${epdb.jdbc.min.idle}", 3 );
                append( sb, "", 0 );

                append( sb, "jdbcDriverClass", 2 );
                append( sb, "Database JDBC Driver Class.", 3 );
                append( sb, "Required: Yes", 3 );
                append( sb, "Expression: ${epdb.jdbc.driver}", 3 );
                append( sb, "", 0 );

                append( sb, "jdbcPassword", 2 );
                append( sb, "Database Password.", 3 );
                append( sb, "Expression: ${epdb.password}", 3 );
                append( sb, "", 0 );

                append( sb, "jdbcUrl", 2 );
                append( sb, "JDBC URL.", 3 );
                append( sb, "Required: Yes", 3 );
                append( sb, "Expression: ${epdb.url}", 3 );
                append( sb, "", 0 );

                append( sb, "jdbcUsername", 2 );
                append( sb, "Database Username.", 3 );
                append( sb, "Expression: ${epdb.username}", 3 );
                append( sb, "", 0 );

                append( sb, "skip (Default: false)", 2 );
                append( sb, "(no description available)", 3 );
                append( sb, "Required: Yes", 3 );
                append( sb, "", 0 );
            }
        }

        if ( goal == null || goal.length() <= 0 || "request-reindex".equals( goal ) )
        {
            append( sb, "ep-core-tool:request-reindex", 0 );
            append( sb, "Adds a rebuild request to the index notification queue.", 1 );
            append( sb, "", 0 );
            if ( detail )
            {
                append( sb, "Available parameters:", 1 );
                append( sb, "", 0 );

                append( sb, "index", 2 );
                append( sb, "Name of index.", 3 );
                append( sb, "Expression: ${index}", 3 );
                append( sb, "", 0 );

                append( sb, "jdbcConnectionPoolMaxIdle", 2 );
                append( sb, "Maximum number of idle connections permitted in the connection pool, or negative for no limit. Set this property and jdbcConnectionPoolMinIdle to 0 to prohibit idle connections.", 3 );
                append( sb, "Expression: ${epdb.jdbc.max.idle}", 3 );
                append( sb, "", 0 );

                append( sb, "jdbcConnectionPoolMinIdle", 2 );
                append( sb, "Minimum number of idle connections permitted in the connection pool. Set this property and jdbcConnectionPoolMaxIdle to 0 to prohibit idle connections.", 3 );
                append( sb, "Expression: ${epdb.jdbc.min.idle}", 3 );
                append( sb, "", 0 );

                append( sb, "jdbcDriverClass", 2 );
                append( sb, "Database JDBC Driver Class.", 3 );
                append( sb, "Required: Yes", 3 );
                append( sb, "Expression: ${epdb.jdbc.driver}", 3 );
                append( sb, "", 0 );

                append( sb, "jdbcPassword", 2 );
                append( sb, "Database Password.", 3 );
                append( sb, "Expression: ${epdb.password}", 3 );
                append( sb, "", 0 );

                append( sb, "jdbcUrl", 2 );
                append( sb, "JDBC URL.", 3 );
                append( sb, "Required: Yes", 3 );
                append( sb, "Expression: ${epdb.url}", 3 );
                append( sb, "", 0 );

                append( sb, "jdbcUsername", 2 );
                append( sb, "Database Username.", 3 );
                append( sb, "Expression: ${epdb.username}", 3 );
                append( sb, "", 0 );

                append( sb, "skip (Default: false)", 2 );
                append( sb, "(no description available)", 3 );
                append( sb, "Required: Yes", 3 );
                append( sb, "", 0 );

                append( sb, "wait", 2 );
                append( sb, "Should execution continue until the requested indexes have been rebuild?", 3 );
                append( sb, "Expression: ${wait}", 3 );
                append( sb, "", 0 );
            }
        }

        if ( goal == null || goal.length() <= 0 || "set-cmuser-password".equals( goal ) )
        {
            append( sb, "ep-core-tool:set-cmuser-password", 0 );
            append( sb, "Set the password of the specified CM User.", 1 );
            append( sb, "", 0 );
            if ( detail )
            {
                append( sb, "Available parameters:", 1 );
                append( sb, "", 0 );

                append( sb, "jdbcConnectionPoolMaxIdle", 2 );
                append( sb, "Maximum number of idle connections permitted in the connection pool, or negative for no limit. Set this property and jdbcConnectionPoolMinIdle to 0 to prohibit idle connections.", 3 );
                append( sb, "Expression: ${epdb.jdbc.max.idle}", 3 );
                append( sb, "", 0 );

                append( sb, "jdbcConnectionPoolMinIdle", 2 );
                append( sb, "Minimum number of idle connections permitted in the connection pool. Set this property and jdbcConnectionPoolMaxIdle to 0 to prohibit idle connections.", 3 );
                append( sb, "Expression: ${epdb.jdbc.min.idle}", 3 );
                append( sb, "", 0 );

                append( sb, "jdbcDriverClass", 2 );
                append( sb, "Database JDBC Driver Class.", 3 );
                append( sb, "Required: Yes", 3 );
                append( sb, "Expression: ${epdb.jdbc.driver}", 3 );
                append( sb, "", 0 );

                append( sb, "jdbcPassword", 2 );
                append( sb, "Database Password.", 3 );
                append( sb, "Expression: ${epdb.password}", 3 );
                append( sb, "", 0 );

                append( sb, "jdbcUrl", 2 );
                append( sb, "JDBC URL.", 3 );
                append( sb, "Required: Yes", 3 );
                append( sb, "Expression: ${epdb.url}", 3 );
                append( sb, "", 0 );

                append( sb, "jdbcUsername", 2 );
                append( sb, "Database Username.", 3 );
                append( sb, "Expression: ${epdb.username}", 3 );
                append( sb, "", 0 );

                append( sb, "password", 2 );
                append( sb, "New plaintext password for the CM user.", 3 );
                append( sb, "Expression: ${password}", 3 );
                append( sb, "", 0 );

                append( sb, "skip (Default: false)", 2 );
                append( sb, "(no description available)", 3 );
                append( sb, "Required: Yes", 3 );
                append( sb, "", 0 );

                append( sb, "username", 2 );
                append( sb, "Username of the CM User.", 3 );
                append( sb, "Expression: ${username}", 3 );
                append( sb, "", 0 );
            }
        }

        if ( goal == null || goal.length() <= 0 || "set-setting".equals( goal ) )
        {
            append( sb, "ep-core-tool:set-setting", 0 );
            append( sb, "Updates the setting value in the Elastic Path database. If a value already exists, it will be removed.", 1 );
            append( sb, "", 0 );
            if ( detail )
            {
                append( sb, "Available parameters:", 1 );
                append( sb, "", 0 );

                append( sb, "jdbcConnectionPoolMaxIdle", 2 );
                append( sb, "Maximum number of idle connections permitted in the connection pool, or negative for no limit. Set this property and jdbcConnectionPoolMinIdle to 0 to prohibit idle connections.", 3 );
                append( sb, "Expression: ${epdb.jdbc.max.idle}", 3 );
                append( sb, "", 0 );

                append( sb, "jdbcConnectionPoolMinIdle", 2 );
                append( sb, "Minimum number of idle connections permitted in the connection pool. Set this property and jdbcConnectionPoolMaxIdle to 0 to prohibit idle connections.", 3 );
                append( sb, "Expression: ${epdb.jdbc.min.idle}", 3 );
                append( sb, "", 0 );

                append( sb, "jdbcDriverClass", 2 );
                append( sb, "Database JDBC Driver Class.", 3 );
                append( sb, "Required: Yes", 3 );
                append( sb, "Expression: ${epdb.jdbc.driver}", 3 );
                append( sb, "", 0 );

                append( sb, "jdbcPassword", 2 );
                append( sb, "Database Password.", 3 );
                append( sb, "Expression: ${epdb.password}", 3 );
                append( sb, "", 0 );

                append( sb, "jdbcUrl", 2 );
                append( sb, "JDBC URL.", 3 );
                append( sb, "Required: Yes", 3 );
                append( sb, "Expression: ${epdb.url}", 3 );
                append( sb, "", 0 );

                append( sb, "jdbcUsername", 2 );
                append( sb, "Database Username.", 3 );
                append( sb, "Expression: ${epdb.username}", 3 );
                append( sb, "", 0 );

                append( sb, "settingContext", 2 );
                append( sb, "Context for the setting (eg, store code).", 3 );
                append( sb, "Expression: ${settingContext}", 3 );
                append( sb, "", 0 );

                append( sb, "settingName", 2 );
                append( sb, "Name of setting.", 3 );
                append( sb, "Required: Yes", 3 );
                append( sb, "Expression: ${settingName}", 3 );
                append( sb, "", 0 );

                append( sb, "settingValue", 2 );
                append( sb, "New value.", 3 );
                append( sb, "Required: Yes", 3 );
                append( sb, "Expression: ${settingValue}", 3 );
                append( sb, "", 0 );

                append( sb, "skip (Default: false)", 2 );
                append( sb, "(no description available)", 3 );
                append( sb, "Required: Yes", 3 );
                append( sb, "", 0 );
            }
        }

        if ( goal == null || goal.length() <= 0 || "unset-setting".equals( goal ) )
        {
            append( sb, "ep-core-tool:unset-setting", 0 );
            append( sb, "Updates the setting value in the Elastic Path database. If a value already exists, it will be removed.", 1 );
            append( sb, "", 0 );
            if ( detail )
            {
                append( sb, "Available parameters:", 1 );
                append( sb, "", 0 );

                append( sb, "jdbcConnectionPoolMaxIdle", 2 );
                append( sb, "Maximum number of idle connections permitted in the connection pool, or negative for no limit. Set this property and jdbcConnectionPoolMinIdle to 0 to prohibit idle connections.", 3 );
                append( sb, "Expression: ${epdb.jdbc.max.idle}", 3 );
                append( sb, "", 0 );

                append( sb, "jdbcConnectionPoolMinIdle", 2 );
                append( sb, "Minimum number of idle connections permitted in the connection pool. Set this property and jdbcConnectionPoolMaxIdle to 0 to prohibit idle connections.", 3 );
                append( sb, "Expression: ${epdb.jdbc.min.idle}", 3 );
                append( sb, "", 0 );

                append( sb, "jdbcDriverClass", 2 );
                append( sb, "Database JDBC Driver Class.", 3 );
                append( sb, "Required: Yes", 3 );
                append( sb, "Expression: ${epdb.jdbc.driver}", 3 );
                append( sb, "", 0 );

                append( sb, "jdbcPassword", 2 );
                append( sb, "Database Password.", 3 );
                append( sb, "Expression: ${epdb.password}", 3 );
                append( sb, "", 0 );

                append( sb, "jdbcUrl", 2 );
                append( sb, "JDBC URL.", 3 );
                append( sb, "Required: Yes", 3 );
                append( sb, "Expression: ${epdb.url}", 3 );
                append( sb, "", 0 );

                append( sb, "jdbcUsername", 2 );
                append( sb, "Database Username.", 3 );
                append( sb, "Expression: ${epdb.username}", 3 );
                append( sb, "", 0 );

                append( sb, "settingContext", 2 );
                append( sb, "Context for the setting (eg, store code).", 3 );
                append( sb, "Expression: ${settingContext}", 3 );
                append( sb, "", 0 );

                append( sb, "settingName", 2 );
                append( sb, "Name of setting.", 3 );
                append( sb, "Required: Yes", 3 );
                append( sb, "Expression: ${settingName}", 3 );
                append( sb, "", 0 );

                append( sb, "skip (Default: false)", 2 );
                append( sb, "(no description available)", 3 );
                append( sb, "Required: Yes", 3 );
                append( sb, "", 0 );
            }
        }

        if ( getLog().isInfoEnabled() )
        {
            getLog().info( sb.toString() );
        }
    }

    /**
     * <p>Repeat a String <code>n</code> times to form a new string.</p>
     *
     * @param str String to repeat
     * @param repeat number of times to repeat str
     * @return String with repeated String
     * @throws NegativeArraySizeException if <code>repeat < 0</code>
     * @throws NullPointerException if str is <code>null</code>
     */
    private static String repeat( String str, int repeat )
    {
        StringBuffer buffer = new StringBuffer( repeat * str.length() );

        for ( int i = 0; i < repeat; i++ )
        {
            buffer.append( str );
        }

        return buffer.toString();
    }

    /** 
     * Append a description to the buffer by respecting the indentSize and lineLength parameters.
     * <b>Note</b>: The last character is always a new line.
     * 
     * @param sb The buffer to append the description, not <code>null</code>.
     * @param description The description, not <code>null</code>.
     * @param indent The base indentation level of each line, must not be negative.
     */
    private void append( StringBuffer sb, String description, int indent )
    {
        for ( Iterator it = toLines( description, indent, indentSize, lineLength ).iterator(); it.hasNext(); )
        {
            sb.append( it.next().toString() ).append( '\n' );
        }
    }

    /** 
     * Splits the specified text into lines of convenient display length.
     * 
     * @param text The text to split into lines, must not be <code>null</code>.
     * @param indent The base indentation level of each line, must not be negative.
     * @param indentSize The size of each indentation, must not be negative.
     * @param lineLength The length of the line, must not be negative.
     * @return The sequence of display lines, never <code>null</code>.
     * @throws NegativeArraySizeException if <code>indent < 0</code>
     */
    private static List toLines( String text, int indent, int indentSize, int lineLength )
    {
        List lines = new ArrayList();

        String ind = repeat( "\t", indent );
        String[] plainLines = text.split( "(\r\n)|(\r)|(\n)" );
        for ( int i = 0; i < plainLines.length; i++ )
        {
            toLines( lines, ind + plainLines[i], indentSize, lineLength );
        }

        return lines;
    }

    /** 
     * Adds the specified line to the output sequence, performing line wrapping if necessary.
     * 
     * @param lines The sequence of display lines, must not be <code>null</code>.
     * @param line The line to add, must not be <code>null</code>.
     * @param indentSize The size of each indentation, must not be negative.
     * @param lineLength The length of the line, must not be negative.
     */
    private static void toLines( List lines, String line, int indentSize, int lineLength )
    {
        int lineIndent = getIndentLevel( line );
        StringBuffer buf = new StringBuffer( 256 );
        String[] tokens = line.split( " +" );
        for ( int i = 0; i < tokens.length; i++ )
        {
            String token = tokens[i];
            if ( i > 0 )
            {
                if ( buf.length() + token.length() >= lineLength )
                {
                    lines.add( buf.toString() );
                    buf.setLength( 0 );
                    buf.append( repeat( " ", lineIndent * indentSize ) );
                }
                else
                {
                    buf.append( ' ' );
                }
            }
            for ( int j = 0; j < token.length(); j++ )
            {
                char c = token.charAt( j );
                if ( c == '\t' )
                {
                    buf.append( repeat( " ", indentSize - buf.length() % indentSize ) );
                }
                else if ( c == '\u00A0' )
                {
                    buf.append( ' ' );
                }
                else
                {
                    buf.append( c );
                }
            }
        }
        lines.add( buf.toString() );
    }

    /** 
     * Gets the indentation level of the specified line.
     * 
     * @param line The line whose indentation level should be retrieved, must not be <code>null</code>.
     * @return The indentation level of the line.
     */
    private static int getIndentLevel( String line )
    {
        int level = 0;
        for ( int i = 0; i < line.length() && line.charAt( i ) == '\t'; i++ )
        {
            level++;
        }
        for ( int i = level + 1; i <= level + 4 && i < line.length(); i++ )
        {
            if ( line.charAt( i ) == '\t' )
            {
                level++;
                break;
            }
        }
        return level;
    }
}
