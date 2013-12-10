package com.elasticpath.tools.epcoretool.client.parsers;

import com.elasticpath.epcoretool.LoggerFacade;
import com.elasticpath.epcoretool.logic.AbstractRequestReindex;
import com.elasticpath.tools.epcoretool.client.CliDB;
import com.elasticpath.tools.epcoretool.client.CliParser;

/**
 * RequestReindexParser wapper around the logic classes.
 */
public class RequestReindexParser extends CliDB implements CliParser {

	@Override
	public void execute(final String param) {
		AbstractRequestReindex logic = new AbstractRequestReindex(getUrl(), getUsername(), getPassword(), getDriverClass(), getMinIdle(),
				getMaxIdle()) {
			@Override
			protected LoggerFacade getLogger() {
				return getLoggerFacade();
			}
		};
		logic.execute(param, false);
	}

	@Override
	public String help() {
		return "\nrequest-reindex [<index>]\n" + "\tAdds a rebuild request to the index notification queue.\n";
	}

}
