package com.elasticpath.epcoretool.mojo;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import com.elasticpath.epcoretool.LoggerFacade;
import com.elasticpath.epcoretool.logic.AbstractPingSearchServer;

/** Suppress unknown tag warning.  */
@SuppressWarnings("javadoc")

/**
 * Interact with the search server, optionally checking different queries or polling for it to be fully functioning.
 * 
 * @goal ping-search
 */
public class PingSearchServerMojo extends AbstractEpCoreMojo {

	@Override
	public void executeMojo() throws MojoExecutionException, MojoFailureException {
		AbstractPingSearchServer pingSearchServer = new AbstractPingSearchServer(getJdbcUrl(), getJdbcUsername(), getJdbcPassword(),
				getJdbcDriverClass(), getJdbcConnectionPoolMinIdle(), getJdbcConnectionPoolMaxIdle()) {
			@Override
			protected LoggerFacade getLogger() {
				return getLoggerFacade();
			}
		};
		try {
			pingSearchServer.execute();
		} catch (RuntimeException ex) {
			throw new MojoExecutionException(ex.getMessage(), ex);
		}
	}
}
