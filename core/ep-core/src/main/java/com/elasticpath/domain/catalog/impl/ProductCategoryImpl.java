/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.catalog.impl;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.openjpa.persistence.FetchAttribute;
import org.apache.openjpa.persistence.FetchGroup;
import org.apache.openjpa.persistence.FetchGroups;
import org.apache.openjpa.persistence.jdbc.ForeignKey;
import org.apache.openjpa.persistence.jdbc.VersionStrategy;

import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductCategory;
import com.elasticpath.persistence.api.AbstractPersistableImpl;
import com.elasticpath.persistence.support.FetchGroupConstants;

/**
 * The default implementation of <code>Product</code>.
 */
@Entity
@VersionStrategy("state-comparison")
@Table(name = ProductCategoryImpl.TABLE_NAME, uniqueConstraints = @UniqueConstraint(columnNames = { "PRODUCT_UID", "CATEGORY_UID" }))
@FetchGroups({
		@FetchGroup(name = FetchGroupConstants.PRODUCT_INDEX, attributes = { @FetchAttribute(name = "category"),
				@FetchAttribute(name = "featuredProductOrder"), @FetchAttribute(name = "product"), @FetchAttribute(name = "defaultCategory") }),
		@FetchGroup(name = FetchGroupConstants.LINK_PRODUCT_CATEGORY, attributes = { @FetchAttribute(name = "category"),
				@FetchAttribute(name = "product"), @FetchAttribute(name = "defaultCategory") }) })
@SuppressWarnings({ "PMD.CyclomaticComplexity" })
public class ProductCategoryImpl extends AbstractPersistableImpl implements ProductCategory {

	private static final long serialVersionUID = 5000000001L;

	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TPRODUCTCATEGORY";

	private Category category;

	private int featuredProductOrder;

	private transient Product product;

	private boolean defaultCategory;

	private long uidPk;

	@Override
	@ManyToOne(targetEntity = AbstractCategoryImpl.class, cascade = { CascadeType.MERGE, CascadeType.REFRESH })
	@JoinColumn(name = "CATEGORY_UID", nullable = false)
	@ForeignKey(name = "TPRODUCTCATEGORY_IBFK_2")
	public Category getCategory() {
		return category;
	}

	@Override
	public void setCategory(final Category category) {
		this.category = category;
	}

	@Override
	@Basic
	@Column(name = "FEAT_PRODUCT_ORDER")
	public int getFeaturedProductOrder() {
		return featuredProductOrder;
	}

	@Override
	public void setFeaturedProductOrder(final int featuredProductOrder) {
		this.featuredProductOrder = featuredProductOrder;
	}

	@Override
	@ManyToOne(targetEntity = ProductImpl.class, cascade = { CascadeType.MERGE, CascadeType.REFRESH })
	@JoinColumn(name = "PRODUCT_UID", nullable = false)
	@ForeignKey(name = "TPRODUCTCATEGORY_IBFK_1")
	public Product getProduct() {
		return product;
	}

	@Override
	public void setProduct(final Product product) {
		this.product = product;
	}

	@Override
	@Basic
	@Column(name = "DEFAULT_CATEGORY")
	public boolean isDefaultCategory() {
		return defaultCategory;
	}

	@Override
	public void setDefaultCategory(final boolean defaultCategory) {
		this.defaultCategory = defaultCategory;
	}

	@Override
	@Id
	@Column(name = "UIDPK")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
	@TableGenerator(name = TABLE_NAME, table = "JPA_GENERATED_KEYS", pkColumnName = "ID", valueColumnName = "LAST_VALUE", pkColumnValue = TABLE_NAME)
	public long getUidPk() {
		return uidPk;
	}

	@Override
	public void setUidPk(final long uidPk) {
		this.uidPk = uidPk;
	}

	/**
	 * Compares this product category with the specified object for order.
	 *
	 * @param productCategory the given product category
	 * @return a negative integer, zero, or a positive integer if this object is less than, equal to, or greater than the specified object.
	 * @throws EpDomainException if the given object is not a <code>Money</code>
	 */
	@Override
	public int compareTo(final ProductCategory productCategory) throws EpDomainException {
		if (productCategory == null) {
			throw new EpDomainException("Object to compare with is null.");
		}

		if (getCategory().getUidPk() != productCategory.getCategory().getUidPk()) {
			throw new EpDomainException("Cannot compare two productCategories in different categories");
		}

		// Just Compare featured product order, featuredProductOrder == 0 means it is not featured product.
		if (getFeaturedProductOrder() == 0 && productCategory.getFeaturedProductOrder() != 0) {
			return 1;
		} else if (getFeaturedProductOrder() != 0 && productCategory.getFeaturedProductOrder() == 0) {
			return -1;
		} else {
			return getFeaturedProductOrder() - productCategory.getFeaturedProductOrder();
		}
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

		if (!(obj instanceof ProductCategoryImpl)) {
			return false;
		}

		ProductCategoryImpl other = (ProductCategoryImpl) obj;
		EqualsBuilder builder = new EqualsBuilder();
		return builder.append(category, other.category)
				.append(product, other.product)
				.isEquals();
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();
		return builder.append(category)
				.append(product)
				.toHashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
				.append("featuredProductOrder", getFeaturedProductOrder())
				.append("category", getCategory()).toString();
	}

}
