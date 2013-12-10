/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.persistence.openjpa.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.orm.jpa.persistenceunit.MutablePersistenceUnitInfo;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitPostProcessor;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * This processing was provided as a workaround for JBoss 5.1.0. It removes the trailing backslash in order to avoid
 * OpenJPA from not recognizing the URL as a jar file. The AbstractCFMetadataFactory determines the URL type by checking its
 * file extension. If it ends in jar then further lookup for persistence metadata is performed. If the URL is of unknown type it
 * is tried on to be loaded as an XML stream. This causes a SaxParserException when trying the load that URL which in its essence
 * is a pure jar file. */
public class RemoveTrailingBackslashPersistenceUnitPostProcessor implements PersistenceUnitPostProcessor {

	private static final Logger LOG = Logger.getLogger(RemoveTrailingBackslashPersistenceUnitPostProcessor.class);

	/**
	 * Remove the trailing backslash from the root url of the persistence unit.
	 *
	 * @param persistenceUnitInfo the persistence unit info to process
	 */
	public void postProcessPersistenceUnitInfo(final MutablePersistenceUnitInfo persistenceUnitInfo) {
		URL puRootUrl = persistenceUnitInfo.getPersistenceUnitRootUrl();
		if (puRootUrl.getFile() != null && puRootUrl.getFile().endsWith("/")) {
			String noTrailingBackslashString = StringUtils.stripEnd(puRootUrl.getFile(), "/");
			try {
				URL newPuRootUrl = new URL(puRootUrl.getProtocol(), puRootUrl.getHost(), noTrailingBackslashString);
				persistenceUnitInfo.setPersistenceUnitRootUrl(newPuRootUrl);
			} catch (MalformedURLException e) {
				LOG.error("Error removing the persistence unit URL's backslash", e);
			}
		}
	}

}
