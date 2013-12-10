package com.elasticpath.tools.epcoretool.client.parsers;

import com.elasticpath.epcoretool.LoggerFacade;
import com.elasticpath.epcoretool.logic.AbstractUnSetSetting;
import com.elasticpath.tools.epcoretool.client.CliDB;
import com.elasticpath.tools.epcoretool.client.CliParser;

/**
 * UnSetSettingParser wapper around the logic classes.
 */
public class UnSetSettingParser extends CliDB implements CliParser {

	@Override
	public void execute(final String param) {
		AbstractUnSetSetting logic = new AbstractUnSetSetting(getUrl(), getUsername(), getPassword(), getDriverClass(), getMinIdle(), getMaxIdle()) {

			@Override
			protected LoggerFacade getLogger() {
				return getLoggerFacade();
			}
		};

		if (param == null) {
			throw new IllegalArgumentException("unset-setting requires a parameter format of setting@context or setting");
		} else if (param.contains("@")) {
			String[] params = param.split("@");
			logic.execute(params[0], params[1]);
		} else {
			logic.execute(param, null);
		}
	}

	@Override
	public String help() {
		return "\nunset-setting <setting>@<context>\n" + "unset-setting <setting>\n"
		+ "\tUpdates the setting value in the Elastic Path database. If a\n" + "\tvalue already exists, it will be removed.\n";

	}
}
