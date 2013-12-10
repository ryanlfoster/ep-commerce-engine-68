package com.elasticpath.domain.catalog.impl;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.openjpa.persistence.DataCache;

import com.elasticpath.domain.catalog.DigitalAsset;
import com.elasticpath.domain.catalog.DigitalAssetAudit;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.order.impl.OrderSkuImpl;
import com.elasticpath.persistence.api.AbstractPersistableImpl;

/**
 * This is a default implementation of <code>DigitalAssetAudit</code>.
 */
@Entity
@Table(name = DigitalAssetAuditImpl.TABLE_NAME)
@DataCache(enabled = false)
public class DigitalAssetAuditImpl extends AbstractPersistableImpl implements DigitalAssetAudit {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TDIGITALASSETAUDIT";

	private OrderSku orderSku;

	private DigitalAsset digitalAsset;

	private Date downloadTime;

	private String ipAddress;

	private long uidPk;

	/**
	 * Returns the <code>OrderSku</code>.
	 *
	 * @return the orderSku
	 */
	@ManyToOne(targetEntity = OrderSkuImpl.class)
	@JoinColumn(name = "ORDERSKU_UID", nullable = false)
	public OrderSku getOrderSku() {
		return this.orderSku;
	}

	/**
	 * Sets the <code>OrderSku</code>.
	 *
	 * @param orderSku the order sku
	 */
	public void setOrderSku(final OrderSku orderSku) {
		this.orderSku = orderSku;
	}

	/**
	 * Returns the <code>DigitalAsset</code>.
	 *
	 * @return the digital asset
	 */
	@ManyToOne(targetEntity = DigitalAssetImpl.class)
	@JoinColumn(name = "DIGITALASSET_UID", nullable = false)
	public DigitalAsset getDigitalAsset() {
		return this.digitalAsset;
	}

	/**
	 * Sets the <code>DigitalAsset</code>.
	 *
	 * @param digitalAsset the digital asset
	 */
	public void setDigitalAsset(final DigitalAsset digitalAsset) {
		this.digitalAsset = digitalAsset;
	}

	/**
	 * Returns the download time.
	 *
	 * @return the download time
	 */
	@Basic
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DOWNLOAD_TIME", nullable = false)
	public Date getDownloadTime() {
		return this.downloadTime;
	}

	/**
	 * Sets the download time.
	 *
	 * @param downloadTime the download time
	 */
	public void setDownloadTime(final Date downloadTime) {
		this.downloadTime = downloadTime;
	}

	/**
	 * Returns the download IP Address.
	 *
	 * @return the download IP Address
	 */
	@Basic
	@Column(name = "IP_ADDRESS")
	public String getIpAddress() {
		return this.ipAddress;
	}

	/**
	 * Sets download IP Address.
	 *
	 * @param ipAddress the download IP Address
	 */
	public void setIpAddress(final String ipAddress) {
		this.ipAddress = ipAddress;

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
