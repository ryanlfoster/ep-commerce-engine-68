package com.elasticpath.service.command;

import java.io.Serializable;

/**
 * Encapsulates abstract command executed on the server by <code>CommandService</code>.
 */
public interface Command extends Serializable {

	/**
	 * Accomplishes execution of command and returns <code>CommandResult</code>.
	 * 
	 * @return CommandResult as outcome of command execution
	 */
	CommandResult execute();
}
