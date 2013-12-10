package com.elasticpath.epcoretool.logic;

import com.elasticpath.epcoretool.LoggerFacade;

/**
 * TestLogger used to print error messages in test classes.
 */
@SuppressWarnings("PMD.SystemPrintln")
public class TestLogger implements LoggerFacade {

	@Override
	public void error(final String message) {
		System.out.println("ERROR: " + message);
	}

	@Override
	public void warn(final String message) {
		System.out.println("WARN: " + message);
	}

	@Override
	public void info(final String message) {
		System.out.println("INFO: " + message);
	}

	@Override
	public void debug(final String message) {
		System.out.println("DEBUG: " + message);
	}

}
