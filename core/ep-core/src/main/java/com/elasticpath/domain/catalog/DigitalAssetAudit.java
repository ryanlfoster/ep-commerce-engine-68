package com.elasticpath.domain.catalog;

import java.util.Date;

import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.persistence.api.Persistable;

/**
 * Represents a digital asset audit. Digital asset audit is associated with <code>DigitalAsset</code> and <code>OrderSku</code>
 *
 */
public interface DigitalAssetAudit extends Persistable {
	/**
	 * Returns the <code>OrderSku</code>.
	 *
	 * @return the orderSku
	 */
	OrderSku getOrderSku();

	/**
	 * Sets the <code>OrderSku</code>.
	 *
	 * @param orderSku the order sku
	 */
	void setOrderSku(OrderSku orderSku);

	/**
	 * Returns the <code>DigitalAsset</code>.
	 *
	 * @return the digital asset
	 */
	DigitalAsset getDigitalAsset();

	/**
	 * Sets the <code>DigitalAsset</code>.
	 *
	 * @param digitalAsset the digital asset
	 */
	void setDigitalAsset(DigitalAsset digitalAsset);

	/**
	 * Returns the download time.
	 *
	 * @return the download time
	 */
	Date getDownloadTime();

	/**
	 * Sets the download time.
	 *
	 * @param downloadTime the download time
	 */
	void setDownloadTime(Date downloadTime);

	/**
	 * Returns the download IP Address.
	 *
	 * @return the download IP Address
	 */
	String getIpAddress();

	/**
	 * Sets download IP Address.
	 *
	 * @param iPAddress the download IP Address
	 */
	void setIpAddress(final String iPAddress);
}
