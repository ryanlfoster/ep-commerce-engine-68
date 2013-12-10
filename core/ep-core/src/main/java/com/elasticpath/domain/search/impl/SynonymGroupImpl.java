package com.elasticpath.domain.search.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import org.apache.openjpa.persistence.DataCache;
import org.apache.openjpa.persistence.ElementDependent;
import org.apache.openjpa.persistence.Externalizer;
import org.apache.openjpa.persistence.Factory;
import org.apache.openjpa.persistence.Persistent;
import org.apache.openjpa.persistence.jdbc.ElementForeignKey;
import org.apache.openjpa.persistence.jdbc.ElementJoinColumn;
import org.apache.openjpa.persistence.jdbc.ForeignKey;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.constants.GlobalConstants;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.impl.CatalogImpl;
import com.elasticpath.domain.impl.AbstractLegacyPersistenceImpl;
import com.elasticpath.domain.search.Synonym;
import com.elasticpath.domain.search.SynonymGroup;

/**
 * Default implementation of {@link SynonymGroup}.
 */
@Entity
@Table(name = SynonymGroupImpl.TABLE_NAME)
@DataCache(enabled = false)
public class SynonymGroupImpl extends AbstractLegacyPersistenceImpl implements SynonymGroup {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	/** The name of the table & generator to use for persistence.*/
	public static final String TABLE_NAME = "TSYNONYMGROUPS";

	/** Defines 20 as a constant to use for maximum string lengths. */
	private static final int LENGTH_20 = 20;

	private String conceptTerm;

	private Set<Synonym> synonyms = new LinkedHashSet<Synonym>();

	private Locale locale;

	private Catalog catalog;

	private long uidPk;

	/**
	 * Return the concept term which will spawn synonyms.
	 * 
	 * @return the concept term which will spawn synonyms
	 */
	@Basic(optional = false)
	@Column(name = "CONCEPT_TERM", unique = true, length = GlobalConstants.SHORT_TEXT_MAX_LENGTH)
	public String getConceptTerm() {
		return conceptTerm;
	}

	/**
	 * Set the concept term which will spawn synonyms.
	 * 
	 * @param conceptTerm the concept term which will spawn synonyms
	 */
	public void setConceptTerm(final String conceptTerm) {
		this.conceptTerm = conceptTerm;
	}

	/**
	 * Returns a readonly set of synonyms.
	 * 
	 * @return a readonly set of synonyms
	 */
	@Transient
	public Set<Synonym> getSynonyms() {
		return Collections.unmodifiableSet(synonyms);
	}

	/**
	 * Returns the set of synonyms for the concept term.
	 * 
	 * @return the set of synonyms for the concept term
	 */
	@OneToMany(targetEntity = SynonymImpl.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@ElementJoinColumn(name = "SYNONYM_UID", nullable = false)
	@ElementForeignKey
	@ElementDependent
	protected Set<Synonym> getSynonymsInternal() {
		return synonyms;
	}

	/**
	 * Sets the set of synonyms for the concept term.
	 * 
	 * @param synonyms the set of synonyms for the concept term
	 */
	protected void setSynonymsInternal(final Set<Synonym> synonyms) {
		this.synonyms = synonyms;
	}

	/**
	 * Gets the locale for these synonyms.
	 * 
	 * @return the locale for these synonyms
	 */
	@Persistent(optional = false)
	@Externalizer("toString")
	@Factory("org.apache.commons.lang.LocaleUtils.toLocale")
	@Column(name = "LOCALE", length = LENGTH_20)
	public Locale getLocale() {
		return locale;
	}

	/**
	 * Sets the locale for these synonyms.
	 * 
	 * @param locale the locale for these synonyms
	 */
	public void setLocale(final Locale locale) {
		this.locale = locale;
	}

	/**
	 * Get the catalog this category belongs to.
	 * 
	 * @return the catalog
	 */
	@ManyToOne(optional = false, targetEntity = CatalogImpl.class, cascade = { CascadeType.REFRESH, CascadeType.MERGE })
	@JoinColumn(name = "CATALOG_UID", nullable = false)
	@ForeignKey
	public Catalog getCatalog() {
		return catalog;
	}

	/**
	 * Set the catalog this category belongs to.
	 * 
	 * @param catalog the catalog to set
	 */
	public void setCatalog(final Catalog catalog) {
		this.catalog = catalog;
	}

	/**
	 * Gets the unique identifier for this domain model object.
	 * 
	 * @return the unique identifier.
	 */
	@Id
	@Column(name = "UIDPK")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
	@TableGenerator(name = TABLE_NAME, table = "JPA_GENERATED_KEYS", pkColumnName = "ID", valueColumnName = "LAST_VALUE", pkColumnValue = TABLE_NAME)
	public long getUidPk() {
		return uidPk;
	}

	/**
	 * Sets the unique identifier for this domain model object.
	 * 
	 * @param uidPk the new unique identifier.
	 */
	public void setUidPk(final long uidPk) {
		this.uidPk = uidPk;
	}

	/**
	 * Removes a list of synonyms. Does nothing for given synonyms that aren't contained within
	 * this group.
	 * 
	 * @param synonyms the synonym to remove
	 */
	public void removeSynonyms(final String... synonyms) {
		if (synonyms == null) {
			return;
		}
		for (final String synonymStr : synonyms) {
			for (final Iterator<Synonym> synonymIter = getSynonymsInternal().iterator(); synonymIter.hasNext();) {
				if (synonymIter.next().getSynonym().equals(synonymStr.trim())) {
					synonymIter.remove();
				}
			}
		}
	}

	/**
	 * Convenience method that checks that this group contains <i>all</i> of the given synonyms.
	 * The concept term is also considered contained within this synonym group.
	 * 
	 * @param synonyms the synonyms to check
	 * @return whether the synonym group contains all the given synonyms
	 */
	public boolean containsSynonyms(final String... synonyms) {
		if (synonyms == null) {
			return true;
		}
		for (final String synonymStr : synonyms) {
			boolean contains = false;
			for (final Synonym synonym : getSynonyms()) {
				if (synonym.getSynonym().equals(synonymStr)) {
					contains = true;
					break;
				}
			}
			if (!contains && !getConceptTerm().equals(synonymStr)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Adds the given list of synonyms to the synonym group. Does nothing for synonyms that are
	 * currently contained within this group.
	 * 
	 * @param synonyms the synonyms to add
	 */
	public void addSynonyms(final String... synonyms) {
		if (synonyms == null) {
			return;
		}
		for (final String synonymStr : synonyms) {
			if (synonymStr.equals(getConceptTerm())) {
				continue;
			}
			final Synonym synonym = getBean(ContextIdNames.SYNONYM);
			synonym.setSynonym(synonymStr);
			addSynonym(synonym);
		}
	}

	/**
	 * Adds the given list of synonyms to the synonym group. Does nothing for synonyms that are
	 * currently contained within this group.
	 * 
	 * @param synonyms the synonyms to add
	 */
	public void addSynonyms(final List<Synonym> synonyms) {
		getSynonymsInternal().addAll(synonyms);
	}

	/**
	 * Sets the synonyms to the given list of synonyms. This removes records that are not given in
	 * the list and adds those that are in the list.
	 * 
	 * @param synonyms the synonyms to set
	 */
	public void setSynonyms(final String... synonyms) {
		if (synonyms == null || synonyms.length == 0) {
			getSynonymsInternal().clear();
			return;
		}

		// keep a reference to the old values so that we don't create new values if don't need to
		final Map<String, Synonym> oldSynonyms = new HashMap<String, Synonym>();
		for (final Synonym synonym : getSynonymsInternal()) {
			oldSynonyms.put(synonym.getSynonym(), synonym);
		}
		getSynonymsInternal().clear();

		for (final String synonymStr : synonyms) {
			if (oldSynonyms.get(synonymStr) == null) {
				addSynonyms(synonymStr);
			} else {
				addSynonym(oldSynonyms.get(synonymStr));
				oldSynonyms.remove(synonymStr);
			}
		}
	}

	/**
	 * Sets the synonyms to the given list of synonyms. This removes records that are not given in
	 * the list and adds those that are in the list.
	 * 
	 * @param synonyms the synonyms to set
	 */
	public void setSynonyms(final List<Synonym> synonyms) {
		getSynonymsInternal().clear();
		getSynonymsInternal().addAll(synonyms);
	}

	private void addSynonym(final Synonym synonym) {
		if ("".equals(synonym.getSynonym().trim())) {
			return;
		}
		getSynonymsInternal().add(synonym);
	}
}
