package com.elasticpath.importexport.common.dto.catalogs;

import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.elasticpath.common.dto.Dto;

/**
 * The implementation of the <code>ImportDto</code> interface that contains data of SynonymGroupDTO.
 * <p>
 * This implementation designed for JAXB to working with xml representation of data
 */
@XmlAccessorType(XmlAccessType.NONE)
public class SynonymGroupDTO implements Dto {

	private static final long serialVersionUID = 1L;

	@XmlElement(name = "locale", required = true)
	private String locale;

	@XmlElement(name = "conceptterm", required = true)
	private String conceptTerm;

	//@XmlElementWrapper(name = "synonyms")
	@XmlElement(name = "synonym", required = true)
	private List<String> synonyms;

	/**
	 * Gets locale.
	 *
	 * @return the locale
	 */
	public String getLocale() {
		return locale;
	}

	/**
	 * Sets locale.
	 *
	 * @param locale the locale to set
	 */
	public void setLocale(final String locale) {
		this.locale = locale;
	}

	/**
	 * Gets conceptTerm.
	 *
	 * @return the conceptTerm
	 */
	public String getConceptTerm() {
		return conceptTerm;
	}

	/**
	 * Sets conceptTerm.
	 *
	 * @param conceptTerm the conceptTerm to set
	 */
	public void setConceptTerm(final String conceptTerm) {
		this.conceptTerm = conceptTerm;
	}

	/**
	 * Gets synonyms.
	 *
	 * @return the synonyms
	 */
	public List<String> getSynonyms() {
		if (synonyms == null) {
			return Collections.emptyList();
		}
		return synonyms;
	}

	/**
	 * Sets synonyms.
	 *
	 * @param synonyms the synonyms to set
	 */
	public void setSynonyms(final List<String> synonyms) {
		this.synonyms = synonyms;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
			.append("locale", getLocale())
			.append("conceptTerm", getConceptTerm())
			.append("synonyms", getSynonyms())
			.toString();
	}
}
