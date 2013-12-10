package com.elasticpath.importexport.common.logging;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;

import com.elasticpath.importexport.common.summary.SummaryLogger;
import com.elasticpath.importexport.common.util.Message;

/**
 * Implements summary appender for import export.
 */
public class IESummaryAppender extends AppenderSkeleton {

	private SummaryLogger summary;

	/**
	 * Constructs summary appender.
	 */
	public IESummaryAppender() {
		super();
		addFilter(new Filter() {
			@Override
			public int decide(final LoggingEvent event) {
				int result = Filter.DENY;
				if (event.getMessage() instanceof Message) {
					result = Filter.ACCEPT;
				}
				return result;
			}
		});
	}

	@Override
	protected void append(final LoggingEvent event) {
		final Message message = (Message) event.getMessage();
		if (Level.ERROR.equals(event.getLevel())) {
			summary.addFailure(message);
		} else if (Level.WARN.equals(event.getLevel())) {
			summary.addWarning(message);
		} else if (Level.INFO.equals(event.getLevel())) {
			summary.addComment(message);
		}
	}

	@Override
	public void close() {
		// do nothing
	}

	@Override
	public boolean requiresLayout() {
		return false;
	}

	/**
	 * Sets the summary.
	 * 
	 * @param summary the summary
	 */
	public void setSummaryLogger(final SummaryLogger summary) {
		this.summary = summary;
	}
}
