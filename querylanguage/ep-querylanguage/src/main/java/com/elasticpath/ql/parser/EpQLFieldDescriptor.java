package com.elasticpath.ql.parser;

import com.elasticpath.ql.parser.fieldresolver.EpQLFieldResolver;
import com.elasticpath.ql.parser.querybuilder.SubQueryBuilder;
import com.elasticpath.ql.parser.valueresolver.EpQLValueResolver;

/**
 * Holds information like locale, type, currency to describe field.
 */
public class EpQLFieldDescriptor {

	private String fieldTemplate;

	private EpQLFieldType type = EpQLFieldType.STRING;

	private EpQLFieldResolver epQLFieldResolver;

	private EpQLValueResolver epQLValueResolver;

	private SubQueryBuilder subQueryBuilder;

	private String[] multiFieldTemplate;

	/**
	 * Gets field template.
	 * 
	 * @return field template
	 */
	public String getFieldTemplate() {
		return fieldTemplate;
	}

	/**
	 * Template is a base field part.
	 * 
	 * @param fieldTemplate the field template to set
	 */
	public void setFieldTemplate(final String fieldTemplate) {
		this.fieldTemplate = fieldTemplate;
	}
	
	/**
	 * Gets string array containing values for multi field templates.
	 * 
	 * @return multi field templates
	 */
	public String[] getMultiFieldTemplate() {
		return multiFieldTemplate; //NOPMD
	}

	/**
	 * Sets string array containing values for multi field templates.
	 * 
	 * @param multiFieldTemplate the field templates to set
	 */
	public void setMultiFieldTemplate(final String[] multiFieldTemplate) { //NOPMD
		this.multiFieldTemplate = multiFieldTemplate;
	}

	/**
	 * Gets field type.
	 * 
	 * @return type
	 */
	public EpQLFieldType getType() {
		return type;
	}

	/**
	 * Sets field type.
	 * 
	 * @param type the type to set
	 */
	public void setType(final EpQLFieldType type) {
		this.type = type;
	}

	/**
	 * Gets resolver used to validate and interpret field described by this descriptor.
	 * 
	 * @return the epQLFieldResolver
	 */
	public EpQLFieldResolver getEpQLFieldResolver() {
		return epQLFieldResolver;
	}

	/**
	 * Sets resolver used to validate and interpret field described by this descriptor.
	 * 
	 * @param epQLFieldResolver the epQLFieldResolver to set
	 */
	public void setEpQLFieldResolver(final EpQLFieldResolver epQLFieldResolver) {
		this.epQLFieldResolver = epQLFieldResolver;
	}

	/**
	 * Gets resolver used to prepare field values for search.
	 * 
	 * @return field value resolver
	 */
	public EpQLValueResolver getEpQLValueResolver() {
		return epQLValueResolver;
	}

	/**
	 * Sets value resolver used to prepare field values for search.
	 * 
	 * @param epQLValueResolver value resolver
	 */
	public void setEpQLValueResolver(final EpQLValueResolver epQLValueResolver) {
		this.epQLValueResolver = epQLValueResolver;
	}

	/**
	 * Gets sub query builder for building native queries.
	 * 
	 * @return query builder
	 */
	public SubQueryBuilder getSubQueryBuilder() {
		return subQueryBuilder;
	}

	/**
	 * Sets sub query builder used to build query described by this descriptor.
	 * 
	 * @param subQueryBuilder sub query builder
	 */
	public void setSubQueryBuilder(final SubQueryBuilder subQueryBuilder) {
		this.subQueryBuilder = subQueryBuilder;
	}
}
