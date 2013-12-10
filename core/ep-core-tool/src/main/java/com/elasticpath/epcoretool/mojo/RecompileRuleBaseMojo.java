package com.elasticpath.epcoretool.mojo;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import com.elasticpath.epcoretool.LoggerFacade;
import com.elasticpath.epcoretool.logic.AbstractRecompileRuleBase;

/** Suppress unknown tag warning.  */
@SuppressWarnings("javadoc")

/**
 * Recompiles the EP Promo RuleBase.
 *
 * @goal recompile-rulebase
 */
public class RecompileRuleBaseMojo extends AbstractEpCoreMojo {

	@Override
	public void executeMojo() throws MojoExecutionException, MojoFailureException {
		AbstractRecompileRuleBase recompileRuleBase = new AbstractRecompileRuleBase(getJdbcUrl(), getJdbcUsername(), getJdbcPassword(),
				getJdbcDriverClass(), getJdbcConnectionPoolMinIdle(), getJdbcConnectionPoolMaxIdle()) {
			@Override
			protected LoggerFacade getLogger() {
				return getLoggerFacade();
			}
		};
		try {
			recompileRuleBase.execute();
		} catch (RuntimeException ex) {
			throw new MojoExecutionException(ex.getMessage(), ex);
		}
	}
}
