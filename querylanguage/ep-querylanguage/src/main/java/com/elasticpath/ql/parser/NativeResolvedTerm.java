package com.elasticpath.ql.parser;

import java.util.Collections;
import java.util.List;

/**
 * Class <code>NativeResolvedTerm</code> contains resolved field. Also it contains field descriptor.
 * Template is a base field part.
 */
public class NativeResolvedTerm {

	private final EpQLFieldDescriptor epQLFieldDescriptor;

	private String resolvedField;	/// NOPMD

	private List<String> resolvedValues;

	private String[] resolvedMultiField;

	/**
	 * Fills descriptor contained in this field.
	 * 
	 * @param epQLFieldDescriptor copy of epQLFieldDescriptor fields will be held in ResolvedSolrField.
	 * Field Descriptor may be required by further value resolution.
	 */
	public NativeResolvedTerm(final EpQLFieldDescriptor epQLFieldDescriptor) {
		this.epQLFieldDescriptor = epQLFieldDescriptor;
	}

	/**
	 * Gets resolved field name in Solr format.
	 * 
	 * @return the resolvedSolrField
	 */
	public String getResolvedField() {
		return resolvedField;
	}

	/**
	 * Sets resolved field name.
	 * 
	 * @param resolvedField the resolvedField to set
	 */
	public void setResolvedField(final String resolvedField) {
		this.resolvedField = resolvedField;
	}
	
	/**
	 * Gets string array containing values for resolved multi field.
	 * 
	 * @return multi field
	 */
	public String[] getResolvedMultiField() {
		return resolvedMultiField; //NOPMD
	}

	/**
	 * Sets string array containing values for resolved multi field.
	 * 
	 * @param resolvedMultiField multi field
	 */
	public void setResolvedMultiField(final String[] resolvedMultiField) { //NOPMD
		this.resolvedMultiField = resolvedMultiField;
	}

	/**
	 * Gets the list of resolved values to search for.
	 * 
	 * @return list of values to search by
	 */
	public List<String> getResolvedValues() {
		if (resolvedValues == null) {
			return Collections.emptyList();
		}
		return resolvedValues;
	}

	/**
	 * Sets the list of resolved values to search for.
	 * 
	 * @param resolvedValues list of values to search by
	 */
	public void setResolvedValues(final List<String> resolvedValues) {
		this.resolvedValues = resolvedValues;
	}

	/**
	 * Gets field descriptor.
	 * 
	 * @return field descriptor
	 */
	public EpQLFieldDescriptor getFieldDescriptor() {
		return epQLFieldDescriptor;
	}
}
