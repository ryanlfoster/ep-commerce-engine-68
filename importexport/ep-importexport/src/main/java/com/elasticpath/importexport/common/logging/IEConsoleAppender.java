package com.elasticpath.importexport.common.logging;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;

import com.elasticpath.importexport.common.util.Message;

/**
 * Implements console appender for import export.
 */
public class IEConsoleAppender extends ConsoleAppender {

	/**
	 * Constructs <code>IEConsoleAppender</code> appender.
	 */
	public IEConsoleAppender() {
		super();
		configureFilter();
	}

	/**
	 * Constructs configured <code>IEConsoleAppender</code> appender.
	 * 
	 * @param layout layout, may not be null.
	 * @param target target, either "System.err" or "System.out".
	 */
	public IEConsoleAppender(final Layout layout, final String target) {
		super(layout, target);
		configureFilter();
	}

	/**
	 * Constructs configured <code>IEConsoleAppender</code> appender.
	 * 
	 * @param layout layout, may not be null.
	 */
	public IEConsoleAppender(final Layout layout) {
		super(layout);
		configureFilter();
	}

	private void configureFilter() {
		addFilter(new Filter() {
			@Override
			public int decide(final LoggingEvent event) {
				int result = Filter.ACCEPT;
				if (event.getMessage() instanceof Message) {
					result = Filter.DENY;
				}
				return result;
			}
		});
	}
}
