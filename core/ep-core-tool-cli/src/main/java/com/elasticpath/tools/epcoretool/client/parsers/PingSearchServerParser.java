package com.elasticpath.tools.epcoretool.client.parsers;

import com.elasticpath.epcoretool.LoggerFacade;
import com.elasticpath.epcoretool.logic.AbstractPingSearchServer;
import com.elasticpath.tools.epcoretool.client.CliDB;
import com.elasticpath.tools.epcoretool.client.CliParser;

/**
 * PingSearchServerParser wapper around the logic classes.
 */
public class PingSearchServerParser extends CliDB implements CliParser {

	@Override
	public void execute(final String param) {
		AbstractPingSearchServer logic = new AbstractPingSearchServer(getUrl(), getUsername(), getPassword(), getDriverClass(), getMinIdle(),
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
		return "\nping-search\n" + "\tPing search server.\n";
	}
}
