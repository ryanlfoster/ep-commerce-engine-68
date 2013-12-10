package com.elasticpath.domain.catalog;

import com.elasticpath.persistence.api.Persistable;

/**
 * Represents a digital asset.
 *
 */
public interface DigitalAsset extends Persistable {
	/**
	 * Returns the file name.
	 *
	 * @return the file name
	 */
	String getFileName();

	/**
	 * Sets the file name.
	 *
	 * @param fileName the file name
	 */
	void setFileName(String fileName);

	/**
	 * Returns the expiry days.
	 *
	 * @return the expiry days
	 */
	int getExpiryDays();

	/**
	 * Sets the expiry days.
	 *
	 * @param expiryDays the expiry days
	 */
	void setExpiryDays(int expiryDays);

	/**
	 * Returns the maximum download times.
	 *
	 * @return the maximum download times
	 */
	int getMaxDownloadTimes();

	/**
	 * Sets the maximum download times.
	 *
	 * @param maxDownloadTimes the maximum download times
	 */
	void setMaxDownloadTimes(int maxDownloadTimes);

	/**
	 * Get the encrypted uidPk string.
	 *
	 * @return the encrypted uidPk string
	 */
	String getEncryptedUidPk();


	/**
	 * Returns the file name without the path information.
	 *
	 * @return the file name
	 */
	String getFileNameWithoutPath();

}
