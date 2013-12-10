package com.elasticpath.importexport.common.summary.impl;

import java.util.List;
import java.util.Map;

import com.elasticpath.importexport.common.summary.Summary;
import com.elasticpath.importexport.common.summary.SummaryLayout;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.common.util.Message;
import com.elasticpath.importexport.common.util.MessageResolver;

/**
 * Simple implementation of SummaryLayout.
 */
public class SimpleSummaryLayout implements SummaryLayout {

	private static final char CRLF = '\n';

	private static final char TAB = '\t';
	
	private MessageResolver messageResolver;

	@Override
	//TODO implement message resolution
	public String format(final Summary summary) {

		final StringBuffer buffer = new StringBuffer();

		buffer.append("Summary Report").append(CRLF);

		buffer.append(CRLF);

		buffer.append("Started Time :").append(summary.getStartDate()).append(CRLF);
		buffer.append("Elapsed Time :").append(summary.getElapsedTime()).append(CRLF);

		buffer.append(CRLF);
		
		final List<Message> failures = summary.getFailures();
		final List<Message> warnings = summary.getWarnings();
		final List<Message> comments = summary.getComments();
		final Map<JobType, Integer> objectCounters = summary.getCounters();

		buffer.append("Total Number Of Objects     :").append(summary.sumAllCounters()).append(CRLF);
		buffer.append("Total Number Of Failures    :").append(failures.size()).append(CRLF);
		buffer.append("Total Number Of Warnings    :").append(warnings.size()).append(CRLF);
		buffer.append("Total Number Of Comments    :").append(comments.size()).append(CRLF);
		if (summary.getAddedToChangeSetCount() > 0) {
			buffer.append("Objects Added to Change Set :").append(summary.getAddedToChangeSetCount()).append(CRLF);
		}

		if (!objectCounters.isEmpty()) {
			buffer.append(CRLF);
			buffer.append("Objects :").append(CRLF);
			for (Map.Entry<JobType, Integer> entry : objectCounters.entrySet()) {
				buffer.append(TAB).append(entry.getKey().getTagName()).append(" :").append(entry.getValue()).append(CRLF);
			}
		}

		if (!failures.isEmpty()) {
			buffer.append(CRLF);
			buffer.append("Failures :").append(CRLF);
			for (Message failure : failures) {
				buffer.append(TAB).append(convertMessage(failure)).append(CRLF);
			}
		}
		
		if (!warnings.isEmpty()) {
			buffer.append(CRLF);
			buffer.append("Warnings :").append(CRLF);
			for (Message warning : warnings) {
				buffer.append(TAB).append(convertMessage(warning)).append(CRLF);
			}
		}

		if (!comments.isEmpty()) {
			buffer.append(CRLF);
			buffer.append("Comments :").append(CRLF);
			for (Message comment : comments) {
				buffer.append(TAB).append(convertMessage(comment)).append(CRLF);
			}
		}
		return buffer.toString();
	}

	/**
	 * Converts the message to string representation.
	 * 
	 * @param message the message 
	 * @return string representation
	 */
	protected String convertMessage(final Message message) {
		if (messageResolver == null) {
			return message.getCode();
		}
		
		return messageResolver.resolve(message);
	}

	/**
	 * Sets message resolver.
	 * 
	 * @param messageResolver the message resolver
	 */
	public void setMessageResolver(final MessageResolver messageResolver) {
		this.messageResolver = messageResolver;
	}

}
