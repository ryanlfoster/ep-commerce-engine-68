package com.elasticpath.commons.util.config;

import org.jdom.Element;

/**
 * Parsing handler of given section elements.
 */
public interface XmlSectionParser {

	/**
	 * Parse the given <code>sectionElement</code>.
	 * 
	 * @param sectionElement the section element
	 */
	void parseSection(Element sectionElement);
}
