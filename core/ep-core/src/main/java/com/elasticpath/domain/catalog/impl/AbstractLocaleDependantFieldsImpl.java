package com.elasticpath.domain.catalog.impl;

import java.util.Locale;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.openjpa.persistence.Externalizer;
import org.apache.openjpa.persistence.Factory;
import org.apache.openjpa.persistence.FetchAttribute;
import org.apache.openjpa.persistence.FetchGroup;
import org.apache.openjpa.persistence.FetchGroups;

import com.elasticpath.domain.catalog.LocaleDependantFields;
import com.elasticpath.persistence.api.AbstractPersistableImpl;
import com.elasticpath.persistence.support.FetchGroupConstants;

/**
 * This is a default implementation of <code>LocaleDependantFields</code>.
 */
@MappedSuperclass
@FetchGroups({
	@FetchGroup(name = FetchGroupConstants.CATEGORY_INDEX, attributes = { @FetchAttribute(name = "displayName"),
				@FetchAttribute(name = "locale") }),
		@FetchGroup(name = FetchGroupConstants.PRODUCT_INDEX, attributes = { @FetchAttribute(name = "displayName"),
				@FetchAttribute(name = "locale") }),
		@FetchGroup(name = FetchGroupConstants.CATEGORY_BASIC, attributes = { @FetchAttribute(name = "displayName"),
				@FetchAttribute(name = "locale"), @FetchAttribute(name = "urlInternal"), @FetchAttribute(name = "title") }),
		@FetchGroup(name = FetchGroupConstants.CATEGORY_LDF, fetchGroups = { FetchGroupConstants.CATEGORY_BASIC },
				attributes = {	@FetchAttribute(name = "keyWords"),	@FetchAttribute(name = "description") })
})
@SuppressWarnings({ "PMD.AvoidDuplicateLiterals" })
public abstract class AbstractLocaleDependantFieldsImpl extends AbstractPersistableImpl implements LocaleDependantFields, Cloneable {

	private static final long serialVersionUID = 5000000001L;

	private static final int LOCALE_LENGTH = 20;

	private String url;

	private Locale locale;

	private String keyWords;

	private String description;

	private String title;

	private String displayName;

	/**
	 * Sets the url. Converts given string to lower case.
	 * Calls {@link #setUrlInternal(String)}.
	 *
	 * @param url the url to set.
	 */
	@Override
	public void setUrl(final String url) {
		Locale locale = getLocale();
		String urlToSet = url;
		if (locale == null) {
			locale = Locale.getDefault();
		}
		if (url != null) {
			urlToSet = url.toLowerCase(locale);
		}
		setUrlInternal(urlToSet);
	}

	@Override
	@Transient
	public String getUrl() {
		return getUrlInternal();
	}

	/**
	 * Sets the url. Internal method for JPA.
	 *
	 * @param url the url to set.
	 */
	protected void setUrlInternal(final String url) {
		this.url = url;
	}

	/**
	 * Returns the url. Internal method for JPA.
	 *
	 * @return the url
	 */
	@Basic
	@Column(name = "URL")
	protected String getUrlInternal() {
		return url;
	}

	@Override
	public void setLocale(final Locale locale) {
		this.locale = locale;
	}

	@Override
	@Basic
	@Column(name = "LOCALE", length = LOCALE_LENGTH, nullable = false)
	@Externalizer("toString")
	@Factory("org.apache.commons.lang.LocaleUtils.toLocale")
	public Locale getLocale() {
		return locale;
	}

	@Override
	public void setKeyWords(final String keyWords) {
		this.keyWords = keyWords;
	}

	@Override
	@Basic
	@Column(name = "KEY_WORDS")
	public String getKeyWords() {
		return keyWords;
	}

	@Override
	public void setDescription(final String description) {
		this.description = description;
	}

	@Override
	@Basic
	@Column(name = "DESCRIPTION")
	public String getDescription() {
		return description;
	}

	@Override
	public void setTitle(final String title) {
		this.title = title;
	}

	@Override
	@Basic
	@Column(name = "TITLE")
	public String getTitle() {
		return title;
	}

	@Override
	public void setDisplayName(final String displayName) {
		this.displayName = displayName;
	}

	@Override
	@Basic
	@Column(name = "DISPLAY_NAME")
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * Implements equals semantics.<br>
	 * This class more than likely would be extended to add functionality that would not effect the equals method in comparisons, and as such would
	 * act as an entity type. In this case, content is not crucial in the equals comparison. Using instanceof within the equals method enables
	 * comparison in the extended classes where the equals method can be reused without violating symmetry conditions. If getClass() was used in the
	 * comparison this could potentially cause equality failure when we do not expect it. If when extending additional fields are included in the
	 * equals method, then the equals needs to be overridden to maintain symmetry.
	 *
	 * @param obj the other object to compare
	 * @return true if equal
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof AbstractLocaleDependantFieldsImpl)) {
			return false;
		}

		final AbstractLocaleDependantFieldsImpl other = (AbstractLocaleDependantFieldsImpl) obj;
		EqualsBuilder builder = new EqualsBuilder();
		return builder.append(locale, other.locale)
				.isEquals();
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();
		return builder.append(locale)
				.toHashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
			.append("locale", getLocale())
			.append("url", getUrl())
			.append("keywords", getKeyWords())
			.append("decsription", getDescription())
			.append("title", getTitle())
			.append("displayName", getDisplayName())
			.toString();
	}

}
