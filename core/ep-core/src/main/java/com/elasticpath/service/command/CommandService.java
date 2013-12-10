package com.elasticpath.service.command;

/**
 * Executes given command.
 */
public interface CommandService {

	/**
	 * Executes given command.
	 * 
	 * @param command command to execute
	 * @return command's outcome
	 */
	CommandResult execute(final Command command);
}
