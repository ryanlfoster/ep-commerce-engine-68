package com.elasticpath.epcoretool.mojo;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import com.elasticpath.epcoretool.LoggerFacade;
import com.elasticpath.epcoretool.logic.AbstractIndexBuildStatus;

/** Suppress unknown tag warning.  */
@SuppressWarnings("javadoc")

/**
 * Display the current search server index rebuild status.
 * 
 * @goal index-status
 */
public class IndexBuildStatusMojo extends AbstractEpCoreMojo {

	@Override
	public void executeMojo() throws MojoExecutionException, MojoFailureException {
		AbstractIndexBuildStatus indexBuildStatus = new AbstractIndexBuildStatus(getJdbcUrl(), getJdbcUsername(), getJdbcPassword(),
				getJdbcDriverClass(), getJdbcConnectionPoolMinIdle(), getJdbcConnectionPoolMaxIdle()) {
			@Override
			protected LoggerFacade getLogger() {
				return getLoggerFacade();
			}
		};
		try {
			indexBuildStatus.execute();
		} catch (RuntimeException ex) {
			throw new MojoExecutionException(ex.getMessage(), ex);
		}
	}

}
