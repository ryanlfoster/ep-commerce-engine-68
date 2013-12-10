package com.elasticpath.domain.search;

import java.util.List;
import java.util.Locale;
import java.util.Set;

import com.elasticpath.domain.catalog.CatalogObject;
import com.elasticpath.persistence.api.Persistable;

/**
 * Represents a collection of synonyms for a given term.
 */
public interface SynonymGroup extends CatalogObject, Persistable {

	/**
	 * Return the concept term which will spawn synonyms.
	 * 
	 * @return the concept term which will spawn synonyms
	 */
	String getConceptTerm();

	/**
	 * Set the concept term which will spawn synonyms.
	 * 
	 * @param conceptTerm the concept term which will spawn synonyms
	 */
	void setConceptTerm(String conceptTerm);

	/**
	 * Returns the set of synonyms. This list should not be modified.
	 * 
	 * @return the set of synonyms
	 */
	Set<Synonym> getSynonyms();

	/**
	 * Gets the locale for these synonyms.
	 * 
	 * @return the locale for these synonyms
	 */
	Locale getLocale();

	/**
	 * Sets the locale for these synonyms.
	 * 
	 * @param locale the locale for these synonyms
	 */
	void setLocale(Locale locale);

	/**
	 * Removes a list of synonyms. Does nothing for given synonyms that aren't contained within
	 * this group.
	 * 
	 * @param synonyms the synonym to remove
	 */
	void removeSynonyms(String... synonyms);

	/**
	 * Convenience method that checks that this group contains <i>all</i> of the given synonyms.
	 * The concept term is also considered contained within this synonym group.
	 * 
	 * @param synonyms the synonyms to check
	 * @return whether the synonym group contains all the given synonyms
	 */
	boolean containsSynonyms(String... synonyms);

	/**
	 * Adds the given list of synonyms to the synonym group. Does nothing for synonyms that are
	 * currently contained within this group.
	 * 
	 * @param synonyms the synonyms to add
	 */
	void addSynonyms(String... synonyms);
	
	/**
	 * Adds the given list of synonyms to the synonym group. Does nothing for synonyms that are
	 * currently contained within this group.
	 * 
	 * @param synonyms the synonyms to add
	 */
	void addSynonyms(List<Synonym> synonyms);

	/**
	 * Sets the synonyms to the given list of synonyms. This removes records that are not given in
	 * the list and adds those that are in the list.
	 * 
	 * @param synonyms the synonyms to set
	 */
	void setSynonyms(String... synonyms);
	
	/**
	 * Sets the synonyms to the given list of synonyms. This removes records that are not given in
	 * the list and adds those that are in the list.
	 * 
	 * @param synonyms the synonyms to set
	 */
	void setSynonyms(List<Synonym> synonyms);
}
