package com.elasticpath.tools.epcoretool.client.parsers;

import com.elasticpath.epcoretool.LoggerFacade;
import com.elasticpath.epcoretool.logic.AbstractIndexBuildStatus;
import com.elasticpath.tools.epcoretool.client.CliDB;
import com.elasticpath.tools.epcoretool.client.CliParser;

/**
 * IndexBuildStatusParser wapper around the logic classes.
 */
public class IndexBuildStatusParser extends CliDB implements CliParser {

	@Override
	public void execute(final String param) {
		AbstractIndexBuildStatus logic = new AbstractIndexBuildStatus(getUrl(), getUsername(), getPassword(), getDriverClass(), getMinIdle(),
				getMaxIdle()) {

			@Override
			protected LoggerFacade getLogger() {
				return getLoggerFacade();
			}
		};
		logic.execute();
	}

	@Override
	public String help() {
		return "\nindex-status\n" + "\tDisplay the current search server index rebuild status.\n";
	}
}
