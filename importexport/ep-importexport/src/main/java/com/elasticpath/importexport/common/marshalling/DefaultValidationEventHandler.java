package com.elasticpath.importexport.common.marshalling;

import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.ValidationEventLocator;

import org.apache.log4j.Logger;

/**
 * Default implementation of validation event handler.
 */
public class DefaultValidationEventHandler implements ValidationEventHandler {

	private static final Logger LOG = Logger.getLogger(DefaultValidationEventHandler.class);

	private String lastErrorStatus = "";

	@Override
	public boolean handleEvent(final ValidationEvent event) {
		if (event.getSeverity() == ValidationEvent.FATAL_ERROR) {
			LOG.error(event.getMessage());
			ValidationEventLocator locator = event.getLocator();
			lastErrorStatus = event.getMessage() + "(line " + locator.getLineNumber() + " column " + locator.getColumnNumber() + ")";
			return false;
		}
		return true;
	}

	/**
	 * Gets last error status.
	 * 
	 * @return the error status
	 */
	public String getLastErrorStatus() {
		return lastErrorStatus;
	}

}
