/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.catalog.impl;

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
import org.apache.openjpa.persistence.FetchAttribute;
import org.apache.openjpa.persistence.FetchGroup;
import org.apache.openjpa.persistence.FetchGroups;
import org.apache.openjpa.persistence.jdbc.ElementForeignKey;
import org.apache.openjpa.persistence.jdbc.ElementJoinColumn;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.attribute.AttributeGroup;
import com.elasticpath.domain.attribute.AttributeGroupAttribute;
import com.elasticpath.domain.attribute.impl.CategoryTypeAttributeImpl;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.CategoryType;
import com.elasticpath.domain.impl.AbstractLegacyEntityImpl;
import com.elasticpath.persistence.support.FetchGroupConstants;

/**
 * Represents a default implementation of <code>CategoryType</code>.
 */
@Entity
@Table(name = CategoryTypeImpl.TABLE_NAME)
@FetchGroups({
	@FetchGroup(name = FetchGroupConstants.CATEGORY_BASIC,
			attributes = { @FetchAttribute(name = "template") })
})
@DataCache(enabled = false)
public class CategoryTypeImpl extends AbstractLegacyEntityImpl implements CategoryType {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TCATEGORYTYPE";

	private AttributeGroup categoryAttributeGroup;

	private Set<AttributeGroupAttribute> categoryAttributeGroupAttributes;

	private String name;

	private String description;

	private String template;

	private Catalog catalog;

	private long uidPk;

	private String guid;

	/**
	 * The default constructor.
	 */
	public CategoryTypeImpl() {
		super();
	}

	/**
	 * Sets the category attribute group.
	 *
	 * @param categoryAttributeGroup the category attribute group.
	 */
	public void setAttributeGroup(final AttributeGroup categoryAttributeGroup) {
		this.categoryAttributeGroup = categoryAttributeGroup;
		if (categoryAttributeGroup == null) {
			setCategoryAttributeGroupAttributes(null);
		} else {
			setCategoryAttributeGroupAttributes(categoryAttributeGroup.getAttributeGroupAttributes());
		}
	}

	/**
	 * Returns the category attribute group.
	 *
	 * @return the category attribute group
	 */
	@Transient
	public AttributeGroup getAttributeGroup() {
		if (categoryAttributeGroup == null) {
			categoryAttributeGroup = getBean(ContextIdNames.ATTRIBUTE_GROUP);
		}
		categoryAttributeGroup.setAttributeGroupAttributes(getCategoryAttributeGroupAttributes());
		return categoryAttributeGroup;
	}

	/**
	 * Get the category type name.
	 *
	 * @return the category type name
	 */
	@Basic
	@Column(name = "NAME")
	public String getName() {
		return name;
	}

	/**
	 * Set the category type name.
	 *
	 * @param name the category type name
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * Get the category type description.
	 *
	 * @return the category type description
	 */
	@Transient
	public String getDescription() {
		return description;
	}

	/**
	 * Set the category type description.
	 *
	 * @param description the category type description
	 */
	public void setDescription(final String description) {
		this.description = description;
	}

	/**
	 * Returns the display template of this category type.
	 *
	 * @return the template
	 */
	@Basic
	@Column(name = "TEMPLATE")
	public String getTemplate() {
		return template;
	}

	/**
	 * Set the template.
	 *
	 * @param template the template
	 */
	public void setTemplate(final String template) {
		this.template = template;
	}

	/**
	 * Set default values for those fields need default values.
	 */
	@Override
	public void initialize() {
		super.initialize();

		if (categoryAttributeGroup == null) {
			categoryAttributeGroup = getBean(ContextIdNames.ATTRIBUTE_GROUP);
		}
	}

	/**
	 * Get the Category attribute group attributes.
	 *
	 * @return the set of attributes
	 */
	@OneToMany(targetEntity = CategoryTypeAttributeImpl.class, cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	@ElementJoinColumn(name = "CATEGORY_TYPE_UID", nullable = false)
	@ElementForeignKey(name = "TCATEGORYTYPEATTRIBUTE_IBFK_2")
	@ElementDependent
	public Set<AttributeGroupAttribute> getCategoryAttributeGroupAttributes() {
		return categoryAttributeGroupAttributes;
	}

	/**
	 * Set the Category attribute group attributes.
	 *
	 * @param categoryAttributeGroupAttributes the set of attributes
	 */
	public void setCategoryAttributeGroupAttributes(final Set<AttributeGroupAttribute> categoryAttributeGroupAttributes) {
		this.categoryAttributeGroupAttributes = categoryAttributeGroupAttributes;
	}

	/**
	 * Return the guid.
	 *
	 * @return the guid.
	 */
	@Override
	@Basic
	@Column(name = "GUID")
	public String getGuid() {
		return guid;
	}

	/**
	 * Set the guid.
	 *
	 * @param guid the guid to set.
	 */
	@Override
	public void setGuid(final String guid) {
		this.guid = guid;
	}

	/**
	 * Get the catalog that this category type belongs to.
	 * @return the catalog
	 */
	@ManyToOne(optional = false, targetEntity = CatalogImpl.class)
	@JoinColumn(name = "CATALOG_UID", nullable = false)
	public Catalog getCatalog() {
		return catalog;
	}

	/**
	 * Set the catalog that this category type belongs to.
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

}
