/*
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.catalog.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.openjpa.persistence.FetchAttribute;
import org.apache.openjpa.persistence.FetchGroup;
import org.apache.openjpa.persistence.FetchGroups;
import org.apache.openjpa.persistence.jdbc.ForeignKey;

import com.elasticpath.commons.constants.GlobalConstants;
import com.elasticpath.domain.DatabaseLastModifiedDate;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.impl.AbstractLegacyEntityImpl;
import com.elasticpath.persistence.support.FetchGroupConstants;

/**
 * Abstract implementation of a <code>Category</code> object. Holds the fields common to categories that are linked and categories that are not
 * linked. Uses JPA's JOINED inheritance strategy.<br/>
 * 
 * NOTE that the presence of the {@code DatabaseLastModifiedDate} means that whenever this object is saved or updated to the database
 * the lastModifiedDate will be set by the {@code LastModifiedPersistenceEngineImpl} if that class in configured in Spring. 
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
@Entity
@Table(name = AbstractCategoryImpl.TABLE_NAME)
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "TYPE", discriminatorType = DiscriminatorType.STRING, length = GlobalConstants.SHORT_TEXT_MAX_LENGTH)
@FetchGroups({
	@FetchGroup(name = FetchGroupConstants.INFINITE_CHILD_CATEGORY_DEPTH, attributes = {
			@FetchAttribute(name = "children", recursionDepth = -1), 
			@FetchAttribute(name = "parentInternal", recursionDepth = -1) }),
	@FetchGroup(name = FetchGroupConstants.INIFINITE_PARENT_CATEGORY_DEPTH, attributes = {
			@FetchAttribute(name = "parentInternal", recursionDepth = -1) }),
	@FetchGroup(name = FetchGroupConstants.CATALOG, attributes = { 
			@FetchAttribute(name = "catalog") }),
	@FetchGroup(name = FetchGroupConstants.CATEGORY_BASIC, attributes = { 
			@FetchAttribute(name = "ordering"),
			@FetchAttribute(name = "catalog") }),
	@FetchGroup(name = FetchGroupConstants.CATEGORY_CHILD_LEVEL_1, attributes = { 
			@FetchAttribute(name = "children", recursionDepth = 1) }),
	@FetchGroup(name = FetchGroupConstants.CATEGORY_HASH_MINIMAL, attributes = { 
			@FetchAttribute(name = "catalog") }),
	@FetchGroup(name = FetchGroupConstants.LINK_PRODUCT_CATEGORY, 
			fetchGroups = { FetchGroupConstants.CATEGORY_HASH_MINIMAL })
})
public abstract class AbstractCategoryImpl extends AbstractLegacyEntityImpl implements Category, DatabaseLastModifiedDate {
	
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TCATEGORY";

	private Set<Category> children = new TreeSet<Category>();

	private Category parent;

	private int ordering;

	private int level = 0;

	private Stack<Category> path = null;

	private Set<Category> availableChildren;

	private List<Category> pathAsList;

	private Date lastModifiedDate;

	private Catalog catalog;

	private long uidPk;

	/**
	 * Default constructor.
	 */
	public AbstractCategoryImpl() {
		super();
	}

	/**
	 * Get the parent category of this category. Returns null if this category doesn't have a parent.
	 * 
	 * @return the parent category(or null no parent)
	 */
	@Transient
	public Category getParent() {
		return this.getParentInternal();
	}

	/**
	 * Sets the parent category.
	 * This method maintains the bidirectional relationships between parent
	 * and child category. So it cannot be used by the persistence layer to
	 * load instance from database. The setParentInternal() method is used
	 * by the persistence layer instead.
	 * 
	 * @param newParent the new parent category
	 */
	public void setParent(final Category newParent) {
		
		this.setParentOnly(newParent);
		
		if (newParent != null && !this.getParentInternal().getChildren().contains(this)) {
			this.getParentInternal().addChild(this);
		}

	}
	
	/**
	 * Set the parent category without adding this category to the given parent category's children
	 * collection. This should only be called if the category is going to be persisted and then not
	 * used without being reloaded from the database. Otherwise use setParent which will maintain
	 * the bidirectional relationship. 
	 * 
	 * @param newParent the new parent category
	 */
	public void setParentOnly(final Category newParent) {

		if ((newParent == null && this.getParentInternal() == null) 
		    || (newParent != null && newParent.equals(this.getParentInternal()))) {
			return;
		}

		final Category oldParent = this.getParentInternal();
		this.setParentInternal(newParent);

		if (oldParent != null) {
			oldParent.removeChild(this);
		}

		// *MUST* reset level whenever parent changed.
		this.reset();
	}

	/**
	 * Set the parent category. This method is used by JPA only, because it
	 * doesn't maintain the bidirectional relationships between the parent
	 * category and this category. It will not add this category to the given
	 * parent category's children collection. 
	 * 
	 * @param newParent the new parent category
	 */
	protected void setParentInternal(final Category newParent) {
		this.parent = newParent;
	}

	/**
	 * Gets the Parent category to this category. This method is used by JPA only.
	 * 
	 * @return the parent category
	 */
	@ManyToOne(targetEntity = AbstractCategoryImpl.class, cascade = { CascadeType.MERGE, CascadeType.REFRESH })
	@JoinColumn(name = "PARENT_CATEGORY_UID")
	@ForeignKey(name = "TCATEGORY_IBFK_2")
	protected Category getParentInternal() {
		return this.parent;
	}

	/**
	 * Get the ordering number.
	 * 
	 * @return the ordering number
	 */
	@Basic
	@Column(name = "ORDERING")
	public int getOrdering() {
		return this.ordering;
	}

	/**
	 * Set the ordering number.
	 * 
	 * @param ordering the ordering number
	 */
	public void setOrdering(final int ordering) {
		this.ordering = ordering;
	}
	
	/**
	 * Return the compound category guid based on category code and appropriate catalog code.
	 *
	 * @return the compound guid.
	 */
	@Transient
	public String getCompoundGuid() {
		return new StringBuilder(this.getCode()).append(CATEGORY_GUID_DELIMITER).append(getCatalog().getCode()).toString();
	}

	/**
	 * Get the direct children categories of this category,
	 * one level deep.
	 * 
	 * @return the child categories as a set
	 */
	@OneToMany(targetEntity = AbstractCategoryImpl.class, mappedBy = "parentInternal", cascade = { CascadeType.ALL })
	public Set<Category> getChildren() {
		return this.children;
	}

	/**
	 * Set the child categories.
	 * 
	 * @param children the child category set.
	 */
	public void setChildren(final Set<Category> children) {
		this.children = children;
	}

	/**
	 * Get the available child categories.
	 * 
	 * @return the available child categories as a set
	 */
	@Transient
	public Set<Category> getAvailableChildren() {
		if (this.availableChildren == null) {
			this.availableChildren = new TreeSet<Category>();
			for (final Category category : this.getChildren()) {
				if (category.isAvailable()) {
					this.availableChildren.add(category);
				}
			}
		}

		return this.availableChildren;
	}

	/**
	 * Add the given category as a child.
	 * 
	 * @param category the category to be added as a child
	 * @throws EpDomainException if the given category is not in the same catalog
	 */
	public void addChild(final Category category) {
		if (category != null) {
			if (!category.getCatalog().getCode().equals(getCatalog().getCode())) {
				throw new EpDomainException("Cannot add child category whose catalog is not the same as its parents");
			}
			this.getChildren().add(category);
			if (!this.equals(category.getParent())) {
				category.setParent(this);
			}
		}
	}

	/**
	 * Remove the given category from the children list.
	 * 
	 * @param category the category to be removed
	 */
	public void removeChild(final Category category) {
		if (category != null) {
			this.getChildren().remove(category);
			if (category.getParent() == this) {
				category.setParent(null);
			}
		}
	}

	/**
	 * Compares this category with the specified object for order.
	 * 
	 * @param category the given object
	 * @return a negative integer, zero, or a positive integer if this object is less than, equal to, or greater than the specified object.
	 */
	public int compareTo(final Category category) {
		// Compare object id
		if (this == category) {
			return 0;
		}

		// Compare ordering
		if (this.getOrdering() < category.getOrdering()) {
			return -1;
		} else if (this.getOrdering() > category.getOrdering()) {
			return 1;
		} else {
			// Finally compare guid
			return this.getGuid().compareTo(category.getGuid());
		}
	}

	/**
	 * Get category level. The root categories will have level 1. <br>
	 * A category's level = it's parent category's level + 1
	 * 
	 * @return the category level
	 */
	@Transient
	public int getLevel() {
		if (this.level == 0) { // not initalized.
			if (this.getParent() == null) {
				this.level = 1;
			} else {
				this.level = this.getParent().getLevel() + 1;
			}
		}

		return this.level;
	}

	/**
	 * Reset the generated data.
	 */
	private void reset() {
		this.level = 0;
		this.path = null;
		this.pathAsList = null;
		this.availableChildren = null;
	}

	/**
	 * Get the path from the root category to this category on the tree.
	 * 
	 * @return a stack contains the path, the root category is on the top.
	 */
	@SuppressWarnings("unchecked")
	@Transient
	public Stack<Category> getPath() {
		// Initialize the stack
		if (this.path == null) {
			this.path = new Stack<Category>();
			Category cursor = this;
			while (cursor != null) {
				this.path.push(cursor);
				cursor = cursor.getParent();
			}
		}

		// *MUST* return the copied path
		// because the returned stack is going to be tweaked
		return (Stack<Category>) this.path.clone();
	}

	/**
	 * Set default values for those fields that need default values.
	 */
	@Override
	public void initialize() {
		super.initialize();

		if (this.getChildren() == null) {
			this.setChildren(new TreeSet<Category>());
		}
	}

	/**
	 * Returns the category path as a <code>List</code>. The root category will be the first.
	 * 
	 * @return the category path as a <code>List</code>.
	 */
	@Transient
	public List<Category> getPathAsList() {
		// Initialize the list
		if (this.pathAsList == null) {
			this.pathAsList = new ArrayList<Category>();
			final Stack<Category> path = this.getPath();
			while (!path.isEmpty()) {
				this.pathAsList.add(path.pop());
			}
		}

		return this.pathAsList;
	}

	/**
	 * Returns the date when the category was last modified.
	 * 
	 * @return the date when the category was last modified
	 */
	@Basic(optional = true)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LAST_MODIFIED_DATE", nullable = false)
	public Date getLastModifiedDate() {
		return this.lastModifiedDate;
	}

	/**
	 * Set the date when the category was last modified.
	 * 
	 * @param lastModifiedDate the date when the category was last modified
	 */
	public void setLastModifiedDate(final Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	/**
	 * Return the flag to indicate whether this category has subCategories or not.
	 * 
	 * @return the flag to indicate whether this category has subCategories or not.
	 */
	@Transient
	@SuppressWarnings("PMD.BooleanGetMethodName")
	public boolean getHasSubCategories() {
		return this.getChildren().size() > 0;
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
		return this.catalog;
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
	@GeneratedValue(strategy = GenerationType.TABLE, generator = AbstractCategoryImpl.TABLE_NAME)
	@TableGenerator(name = AbstractCategoryImpl.TABLE_NAME, table = "JPA_GENERATED_KEYS",
			pkColumnName = "ID", valueColumnName = "LAST_VALUE", pkColumnValue = AbstractCategoryImpl.TABLE_NAME)
	public long getUidPk() {
		return this.uidPk;
	}

	/**
	 * Sets the unique identifier for this domain model object.
	 * 
	 * @param uidPk the new unique identifier.
	 */
	public void setUidPk(final long uidPk) {
		this.uidPk = uidPk;
	}
	
	@Override
	public int hashCode() {
		return ObjectUtils.hashCode(this.catalog);
	}

	@Override
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof AbstractCategoryImpl)) {
			return false;
		}
		final AbstractCategoryImpl category = (AbstractCategoryImpl) other;
		return ObjectUtils.equals(this.catalog, category.catalog);
	}

}
