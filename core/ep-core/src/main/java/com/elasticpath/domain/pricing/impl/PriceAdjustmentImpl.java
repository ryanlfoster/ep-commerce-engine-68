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

import org.apache.openjpa.persistence.DataCache;
import org.apache.openjpa.persistence.FetchAttribute;
import org.apache.openjpa.persistence.FetchGroup;

import com.elasticpath.domain.pricing.PriceAdjustment;
import com.elasticpath.persistence.api.AbstractEntityImpl;
import com.elasticpath.persistence.support.FetchGroupConstants;

/** Domain for Price Adjustments. */
@Entity
@Table(name = PriceAdjustmentImpl.TABLE_NAME)
@DataCache(enabled = true)
@FetchGroup(name = FetchGroupConstants.PRODUCT_INDEX, attributes = {
				@FetchAttribute(name = "priceListGuid"),
				@FetchAttribute(name = "adjustmentAmount")
				})
public class PriceAdjustmentImpl extends AbstractEntityImpl implements PriceAdjustment {
	
	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	/** Database Table. */
	public static final String TABLE_NAME = "TPRICEADJUSTMENT";

	private long uidPk;
	private String plGuid;
	private BigDecimal amount;

	private String guid;
	
	@Override
	@Basic
	@Column(name = "AMOUNT")
	public BigDecimal getAdjustmentAmount() {
		return amount;
	}

	@Override
	@Basic
	@Column(name = "PRICE_LIST_GUID")
	public String getPriceListGuid() {
		return plGuid;
	}

	/**
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

	/**
	 * @param plGuid the plGuid to set
	 */
	public void setPriceListGuid(final String plGuid) {
		this.plGuid = plGuid;
	}

	/**
	 * @param amount the amount to set
	 */
	public void setAdjustmentAmount(final BigDecimal amount) {
		this.amount = amount;
	}
	
	@Override
	public boolean equals(final Object other) {
		if (!(other instanceof PriceAdjustmentImpl)) {
			return false;
		}
		return super.equals(other);
	}

	@Override
    @SuppressWarnings("PMD.UselessOverridingMethod")
	public int hashCode() {
		return super.hashCode();
	}
	
}
