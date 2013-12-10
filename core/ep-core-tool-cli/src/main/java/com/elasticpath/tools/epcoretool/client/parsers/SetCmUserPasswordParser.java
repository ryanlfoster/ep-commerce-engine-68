package com.elasticpath.tools.epcoretool.client.parsers;

import com.elasticpath.epcoretool.LoggerFacade;
import com.elasticpath.epcoretool.logic.AbstractSetCmUserPassword;
import com.elasticpath.tools.epcoretool.client.CliDB;
import com.elasticpath.tools.epcoretool.client.CliParser;

/**
 * SetCmUserPasswordParser wapper around the logic classes.
 */
public class SetCmUserPasswordParser extends CliDB implements CliParser {

	@Override
	public void execute(final String param) {
		AbstractSetCmUserPassword logic = new AbstractSetCmUserPassword(getUrl(), getUsername(), getPassword(), getDriverClass(), getMinIdle(),
				getMaxIdle()) {

			@Override
			protected LoggerFacade getLogger() {
				return getLoggerFacade();
			}
		};

		String[] params = param.split("=");
		if (params.length == 2) {
			logic.execute(params[0], params[1]);
		} else {
			throw new IllegalArgumentException("set-cmuser-password should have a parameter of the format username=password");
		}
	}

	@Override
	public String help() {
		return "\nset-cmuser-password <username>=<password>\n" + "\tUpdate cmuser password.\n";
	}
}
