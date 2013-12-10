package com.elasticpath.tools.epcoretool.client.parsers;

import com.elasticpath.epcoretool.LoggerFacade;
import com.elasticpath.epcoretool.logic.AbstractSetSetting;
import com.elasticpath.epcoretool.logic.dto.EpSetting;
import com.elasticpath.tools.epcoretool.client.CliDB;
import com.elasticpath.tools.epcoretool.client.CliParser;

/**
 * SetSettingParser wapper around the logic classes.
 */
public class SetSettingParser extends CliDB implements CliParser {

	@Override
	public void execute(final String param) {
		AbstractSetSetting logic = new AbstractSetSetting(getUrl(), getUsername(), getPassword(), getDriverClass(), getMinIdle(), getMaxIdle()) {

			@Override
			protected LoggerFacade getLogger() {
				return getLoggerFacade();
			}
		};
		EpSetting setting = logic.parseSettingString(param);
		logic.execute(setting.getName(), setting.getContext(), setting.getValue());
	}

	@Override
	public String help() {
		return "\nset-setting <setting>@<context>=<value>\n" + "set-setting <setting>=<value>\n"
		+ "\tUpdates the setting value in the Elastic Path database. If a\n" + "\tvalue already exists, it will be removed.\n";
	}
}
