package com.elasticpath.tools.epcoretool.client.parsers;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import com.elasticpath.epcoretool.LoggerFacade;
import com.elasticpath.epcoretool.logic.AbstractBulkSetSetting;
import com.elasticpath.epcoretool.logic.dto.EpSetting;
import com.elasticpath.tools.epcoretool.client.CliDB;
import com.elasticpath.tools.epcoretool.client.CliParser;
import com.elasticpath.tools.epcoretool.client.LoadPropertiesHelper;

/**
 * BulkSetSettingParser wapper around the logic classes.
 */
public class BulkSetSettingParser extends CliDB implements CliParser {

	@Override
	public void execute(final String param) {
		AbstractBulkSetSetting logic = new AbstractBulkSetSetting(getUrl(), getUsername(), getPassword(), getDriverClass(), getMinIdle(),
				getMaxIdle()) {
			@Override
			protected LoggerFacade getLogger() {
				return getLoggerFacade();
			}
		};

		// Use the properties config file parser used by the CliDB
		Properties configProperties = LoadPropertiesHelper.loadProperties(param);

		Set<EpSetting> settings = new HashSet<EpSetting>();
		for (Entry<Object, Object> setting : configProperties.entrySet()) {
			settings.add(logic.parseSettingString((String) setting.getKey(), (String) setting.getValue()));
		}
		logic.execute(settings);
	}

	@Override
	public String help() {
		return "\nbulk-set-settings <bulk-settings.properties>\n" + "\tUpdates the setting value in the Elastic Path database. If a\n"
		+ "\tvalue already exists, it will be removed before being re-added.\n";
	}
}
