package com.elasticpath.domain.catalog.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import org.apache.openjpa.persistence.DataCache;
import org.apache.openjpa.persistence.ElementDependent;
import org.apache.openjpa.persistence.jdbc.ElementForeignKey;
import org.apache.openjpa.persistence.jdbc.ElementJoinColumn;

import com.elasticpath.domain.catalog.TopSeller;
import com.elasticpath.domain.catalog.TopSellerProduct;
import com.elasticpath.persistence.api.AbstractPersistableImpl;

/**
 * This is a default implementation of <code>TopSeller</code>.
 */
@Entity
@Table(name = TopSellerImpl.TABLE_NAME)
@DataCache(enabled = false)
public class TopSellerImpl extends AbstractPersistableImpl implements TopSeller {

	/**
	 * Initial capacity for the hash map.
	 */
	private static final int INITIAL_CAPACITY = 4;

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TTOPSELLER";

	private long categoryUid;

	private Map<Long, TopSellerProduct> topSellerProducts = new HashMap<Long, TopSellerProduct>(INITIAL_CAPACITY);

	private long uidPk;

	/**
	 * Returns the category uid.
	 *
	 * @return the category uid
	 */
	@Basic
	@Column(name = "CATEGORY_UID")
	public long getCategoryUid() {
		return this.categoryUid;
	}

	/**
	 * Sets the category uid.
	 *
	 * @param categoryUid the category uid
	 */
	public void setCategoryUid(final long categoryUid) {
		this.categoryUid = categoryUid;
	}

	/**
	 * Returns the top seller products.
	 *
	 * @return the top seller products
	 */
	@OneToMany(targetEntity = TopSellerProductImpl.class, cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	@MapKey(name = "productUid")
	@ElementJoinColumn(name = "TOP_SELLER_UID", nullable = false)
	@ElementForeignKey
	@ElementDependent
	public Map<Long, TopSellerProduct> getTopSellerProducts() {
		return this.topSellerProducts;
	}

	/**
	 * Sets the top seller products.
	 *
	 * @param topSellerProducts the top seller products
	 */
	public void setTopSellerProducts(final Map<Long, TopSellerProduct> topSellerProducts) {
		this.topSellerProducts = topSellerProducts;
	}

	/**
	 * Returns a collection of top selling products uids.
	 *
	 * @return a collection of top selling products uids
	 */
	@Transient
	public Collection<Long> getProductUids() {
		return getTopSellerProducts().keySet();
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
}
