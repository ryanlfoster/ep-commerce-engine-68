package com.elasticpath.domain.pricing.impl;

import java.math.BigDecimal;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.openjpa.persistence.DataCache;

import com.elasticpath.domain.pricing.BaseAmount;
import com.elasticpath.domain.pricing.exceptions.BaseAmountInvalidException;
import com.elasticpath.persistence.api.AbstractEntityImpl;

/** @see com.elasticpath.domain.pricing.BaseAmount */
@Entity
@Table(name = BaseAmountImpl.TABLE_NAME)
@DataCache(enabled = true)
public class BaseAmountImpl extends AbstractEntityImpl implements BaseAmount {
	
	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	/** Database Table. */
	public static final String TABLE_NAME = "TBASEAMOUNT";
	
	private String objectGuid;
	private String objectType;
	private BigDecimal quantity;
	private BigDecimal list;
	private BigDecimal sale;
	private String descriptorGuid;
	
	private String guid;
	
	private long uidPk;
	
	/**
	 * Default constructor.
	 */
	public BaseAmountImpl() {
		//Nothing to do
	}
	
	/**
	 * Full argument list constructor for creation by factory.
	 * 
	 * @param guid identifier
	 * @param objGuid target object GUID
	 * @param objType target object type
	 * @param qty quantity
	 * @param list amount
	 * @param sale amount
	 * @param descriptorGuid identifier of the price list descriptor
	 * @throws BaseAmountInvalidException exception for wrong quantity, sale price, list price.
	 */
	public BaseAmountImpl(final String guid, final String objGuid, final String objType, final BigDecimal qty, final BigDecimal list,
			final BigDecimal sale, final String descriptorGuid)
			throws BaseAmountInvalidException {
		this.guid = guid;
		this.objectGuid = objGuid;
		this.objectType = objType;
		this.quantity = qty;
		this.list = list;
		this.sale = sale;
		this.descriptorGuid = descriptorGuid;
	}

	/** Internal version of {@code getListValue}.
	 * @return the list value */
	@Basic
	@Column(name = "LIST")
	protected BigDecimal getListValueInternal() {
		return this.list;
	}

	/** Internal version of {@code getSaleValue}. 
	 * @return the sale value */
	@Basic
	@Column(name = "SALE")
	protected BigDecimal getSaleValueInternal() {
		return this.sale;
	}
	
	@Override
	@Basic
	@Column(name = "OBJECT_GUID")
	public String getObjectGuid() {
		return this.objectGuid;
	}
	
	@Override
	@Basic
	@Column(name = "OBJECT_TYPE")
	public String getObjectType() {
		return this.objectType;
	}
	
	@Override
	@Basic
	@Column(name = "PRICE_LIST_GUID")
	public String getPriceListDescriptorGuid() {
		return this.descriptorGuid;
	}
	
	/** Internal version of {@code getQuantity}.
	 * @return the quantity */
	@Basic
	@Column(name = "QUANTITY")
	protected BigDecimal getQuantityInternal() {
		return this.quantity;
	}
	
	@Override
	@Transient
	public BigDecimal getListValue() {
		return getListValueInternal();
	}

	@Override
	@Transient
	public BigDecimal getSaleValue() {
		return getSaleValueInternal();
	}
	
	@Override
	@Transient
	public BigDecimal getQuantity() {
		return getQuantityInternal();
	}
	
	/**
	 * Set the target object's GUID identifier.
	 * 
	 * @param objectGuid the target object's GUID identifier
	 */
	public void setObjectGuid(final String objectGuid) {
		this.objectGuid = objectGuid;
	}

	/**
	 * Set the target object's type. One of Product or SKU
	 * 
	 * @param objectType the target object's type.
	 */
	public void setObjectType(final String objectType) {
		this.objectType = objectType;
	}
	
	/**
	 * Set the quantity threshold at which this particular List Price and Sale Price take 
	 * affect for a particular Product or SKU when in a Price List.
	 * 
	 * Note that schema only stores 2 decimal places.
	 * 
	 * @param quantity the threshold quantity
	 */
	protected void setQuantityInternal(final BigDecimal quantity) {
		this.quantity = quantity;
	}
	
	/**
	 * Set the quantity threshold at which this particular List Price and Sale Price take 
	 * affect for a particular Product or SKU when in a Price List.
	 * 
	 * Note that schema only stores 2 decimal places.
	 * 
	 * @param quantity the threshold quantity
	 */
	public void setQuantity(final BigDecimal quantity) {
		setQuantityInternal(quantity.setScale(0));
	}
	
	/**
	 * Set the list amount.
	 * Note that schema only stores 2 decimal places.
	 * 
	 * @param list amount
	 */
	public void setListValue(final BigDecimal list) {
		setListValueInternal(list);
	}
	
	/**
	 * Set the sale amount. (optional)
	 * 
	 * Note that schema only stores 2 decimal places.
	 * 
	 * @param sale amount
	 */
	public void setSaleValue(final BigDecimal sale) {
		setSaleValueInternal(sale);
	}
	
	/**
	 * Set the guid of the price list descriptor this base amount belongs to.
	 * 
	 * @param guid of the price list descriptor
	 */
	public void setPriceListDescriptorGuid(final String guid) {
		this.descriptorGuid = guid;
	}
	
	/**
	 * Set the list amount.
	 * Note that schema only stores 2 decimal places.
	 * 
	 * @param list amount
	 */
	protected void setListValueInternal(final BigDecimal list) {
		this.list = list;
	}
	
	/**
	 * Set the sale amount. (optional)
	 * 
	 * Note that schema only stores 2 decimal places.
	 * 
	 * @param sale amount
	 */
	protected void setSaleValueInternal(final BigDecimal sale) {
		this.sale = sale;
	}
	
	/**
	 * Return the guid.
	 * 
	 * @return the guid.
	 */
	@Override
	@Basic
	@Column(name = "GUID")
	@SuppressWarnings({ "PMD.UselessOverridingMethod" })
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
	
	@Override
	@Id
	@Column(name = "UIDPK")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
	@TableGenerator(name = TABLE_NAME, table = "JPA_GENERATED_KEYS", pkColumnName = "ID", valueColumnName = "LAST_VALUE", pkColumnValue = TABLE_NAME)
	public long getUidPk() {
		return this.uidPk;
	}

	@Override
	public void setUidPk(final long uidPk) {
		this.uidPk = uidPk;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("BaseAmount");
		builder.append("\nGUID=");
		builder.append(this.getGuid());
		builder.append("\nObjectGUID=");
		builder.append(this.getObjectGuid());
		builder.append("\nObjectType=");
		builder.append(this.getObjectType());
		builder.append("\nListValue=");
		builder.append(this.getListValue());
		builder.append("\nSaleValue=");
		builder.append(this.getSaleValue());
		builder.append("\nQuantity=");
		builder.append(this.getQuantity());
		builder.append("\nPriceListDescriptorGUID=");
		builder.append(this.getPriceListDescriptorGuid());
		return builder.toString();
	}

	@Override
	public void setQuantityScaleToInteger() {
		quantity = quantity.setScale(0);
	}
	
	@Override
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}
		
		if (!(other instanceof BaseAmountImpl)) {
			return false;
		}
		
		BaseAmountImpl baseAmount = (BaseAmountImpl) other;
		return ObjectUtils.equals(this.guid, baseAmount.guid);
	}

	@Override
	public int hashCode() {
		return ObjectUtils.hashCode(this.guid);
	}

}
