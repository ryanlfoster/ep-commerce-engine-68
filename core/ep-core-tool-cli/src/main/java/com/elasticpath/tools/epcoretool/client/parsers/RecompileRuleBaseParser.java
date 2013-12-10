package com.elasticpath.tools.epcoretool.client.parsers;

import com.elasticpath.epcoretool.LoggerFacade;
import com.elasticpath.epcoretool.logic.AbstractRecompileRuleBase;
import com.elasticpath.tools.epcoretool.client.CliDB;
import com.elasticpath.tools.epcoretool.client.CliParser;

/**
 * RecompileRuleBaseParser wrapper around the logic classes.
 */
public class RecompileRuleBaseParser extends CliDB implements CliParser {

	@Override
	public void execute(final String param) {
		AbstractRecompileRuleBase logic = new AbstractRecompileRuleBase(getUrl(), getUsername(), getPassword(), getDriverClass(), getMinIdle(),
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
		return "\nrecompile-rulebase\n" + "\tRecompiles the rulebase.\n";
	}

}
