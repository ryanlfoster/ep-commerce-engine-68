package com.elasticpath.commons.util.config.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.elasticpath.commons.util.config.XmlParser;
import com.elasticpath.commons.util.config.XmlSectionParser;
import com.elasticpath.persistence.api.EpPersistenceException;

/**
 * Default implementation of {@link XmlParser}.
 */
public class XmlParserImpl implements XmlParser {

	private static final Logger LOG = Logger.getLogger(XmlParserImpl.class);

	private static final XmlSectionParser DEFAULT_PARSER = new DefaultConfigSectionParser();

	private XmlSectionParser defaultParser = DEFAULT_PARSER;

	private Map<String, XmlSectionParser> sectionParsers;

	/**
	 * Reads configuration in XML format from <code>configStream</code>. Dispatches all global
	 * sections to the given <code>sectionParser</code>.
	 * 
	 * @param configStream the configuration input stream
	 * @param throwException throws an exception rather than logging it
	 * @throws EpPersistenceException in case of any errors
	 */
	@SuppressWarnings("unchecked")
	// jdom getChildren() isn't generic
	public void loadConfugration(final InputStream configStream, final boolean throwException) throws EpPersistenceException {
		// build the document with SAX and Xerces, no validation
		final SAXBuilder builder = new SAXBuilder();
		// create the document
		Document doc = null;
		try {
			doc = builder.build(configStream);
		} catch (IOException e) {
			if (throwException) {
				throw new EpPersistenceException("Failed to read the file", e);
			}
			LOG.warn("Failed to read the file", e);
		} catch (JDOMException e) {
			if (throwException) {
				throw new EpPersistenceException("Failed to parse config file.", e);
			}
			LOG.warn("Failed to parse config file.", e);
		}
		if (doc != null) {
			final Element msRootNode = doc.getRootElement();
			for (Element sectionElement : (List<Element>) msRootNode.getChildren()) {
				getSectionParser(sectionElement.getName()).parseSection(sectionElement);
			}
		}
	}

	/**
	 * Gets the map of section parsers. The key of the map is the section name that a particular
	 * parser should parse.
	 * 
	 * @return the map of section parsers
	 */
	public Map<String, XmlSectionParser> getSectionParsers() {
		if (sectionParsers == null) {
			sectionParsers = new HashMap<String, XmlSectionParser>();
		}
		return sectionParsers;
	}

	/**
	 * Sets the map of section parsers. The key of the map is the section name that a particular
	 * parser should parse.
	 * 
	 * @param sectionParsers the map of section parsers
	 */
	public void setSectionParsers(final Map<String, XmlSectionParser> sectionParsers) {
		this.sectionParsers = sectionParsers;
	}

	/**
	 * Sets the default section parser. This parser is used if there are no appropriate section
	 * parsers in the section parser map.
	 * 
	 * @param sectionParser the default section parser
	 */
	public void setDefaultSectionParser(final XmlSectionParser sectionParser) {
		defaultParser = sectionParser;
	}

	private XmlSectionParser getSectionParser(final String section) {
		if (getSectionParsers().get(section) == null) {
			return defaultParser;
		}
		return getSectionParsers().get(section);
	}

	/** Default parser that is used if no other suitable parser exists. */
	private static class DefaultConfigSectionParser implements XmlSectionParser {
		@Override
		public void parseSection(final Element sectionElement) {
			// defaults to do nothing
		}
	}
}
