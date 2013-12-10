package com.elasticpath.commons.util.config;

import java.io.InputStream;
import java.util.Map;

import com.elasticpath.persistence.api.EpPersistenceException;

/**
 * Reads XML configuration files and dispatches parsing the actual sections to
 * {@link XmlSectionParser}s.
 */
public interface XmlParser {

	/**
	 * Reads configuration in XML format from <code>configStream</code>. Dispatches all global
	 * sections to the given <code>sectionParser</code>.
	 * 
	 * @param configStream the configuration input stream
	 * @param throwException throws an exception rather than logging it
	 * @throws EpPersistenceException in case of any errors
	 */
	void loadConfugration(final InputStream configStream, final boolean throwException) throws EpPersistenceException;

	/**
	 * Gets the map of section parsers. The key of the map is the section name that a particular
	 * parser should parse.
	 * 
	 * @return the map of section parsers
	 */
	Map<String, XmlSectionParser> getSectionParsers();

	/**
	 * Sets the map of section parsers. The key of the map is the section name that a particular
	 * parser should parse.
	 * 
	 * @param sectionParsers the map of section parsers
	 */
	void setSectionParsers(final Map<String, XmlSectionParser> sectionParsers);

	/**
	 * Sets the default section parser. This parser is used if there are no appropriate section
	 * parsers in the section parser map.
	 * 
	 * @param sectionParser the default section parser
	 */
	void setDefaultSectionParser(final XmlSectionParser sectionParser);
}
