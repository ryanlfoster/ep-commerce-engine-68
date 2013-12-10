package com.elasticpath.epcoretool.mojo;

import org.apache.maven.plugin.MojoExecutionException;

import com.elasticpath.epcoretool.LoggerFacade;
import com.elasticpath.epcoretool.logic.AbstractRequestReindex;

/** Suppress unknown tag warning.  */
@SuppressWarnings("javadoc")

/**
 * Adds a rebuild request to the index notification queue.
 * 
 * @goal request-reindex
 */
public class RequestReindexMojo extends AbstractEpCoreMojo {

	/**
	 * Name of index.
	 * 
	 * @parameter expression="${index}"
	 */
	private String index;

	/**
	 * Should execution continue until the requested indexes have been rebuild?
	 * 
	 * @parameter expression="${wait}"
	 */
	private boolean wait;

	@Override
	public void executeMojo() throws MojoExecutionException {

		AbstractRequestReindex requestReindex = new AbstractRequestReindex(getJdbcUrl(), getJdbcUsername(), getJdbcPassword(), getJdbcDriverClass(),
				getJdbcConnectionPoolMinIdle(), getJdbcConnectionPoolMaxIdle()) {
			@Override
			protected LoggerFacade getLogger() {
				return getLoggerFacade();
			}
		};
		try {
			requestReindex.execute(index, wait);
		} catch (RuntimeException ex) {
			throw new MojoExecutionException(ex.getMessage(), ex);
		}
	}
}
