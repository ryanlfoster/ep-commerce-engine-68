package com.elasticpath.domain.catalog.impl;

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
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.openjpa.persistence.DataCache;

import com.elasticpath.commons.util.security.StringEncrypter;
import com.elasticpath.domain.catalog.DigitalAsset;
import com.elasticpath.domain.impl.AbstractLegacyPersistenceImpl;

/**
 * This is a default implementation of <code>DigitalAsset</code>.
 */
@Entity
@Table(name = DigitalAssetImpl.TABLE_NAME)
@DataCache(enabled = false)
public class DigitalAssetImpl extends AbstractLegacyPersistenceImpl implements DigitalAsset {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TDIGITALASSETS";

	private String fileName;

	private int expiryDays;

	private int maxDownloadTimes;

	private String encryptedUidPk;

	private long uidPk;

	/**
	 * Returns the file name.
	 *
	 * @return the file name
	 */
	@Basic
	@Column(name = "FILE_NAME")
	public String getFileName() {
		return this.fileName;
	}

	/**
	 * Sets the file name.
	 *
	 * @param fileName the file name
	 */
	public void setFileName(final String fileName) {
		this.fileName = fileName;
	}

	/**
	 * Returns the expiry days.
	 *
	 * @return the expiry days
	 */
	@Basic
	@Column(name = "EXPIRY_DAYS")
	public int getExpiryDays() {
		return this.expiryDays;
	}

	/**
	 * Sets the expiry days.
	 *
	 * @param expiryDays the expiry days
	 */
	public void setExpiryDays(final int expiryDays) {
		this.expiryDays = expiryDays;
	}

	/**
	 * Returns the maximum download times.
	 *
	 * @return the maximum download times
	 */
	@Basic
	@Column(name = "MAX_DOWNLOAD_TIMES")
	public int getMaxDownloadTimes() {
		return this.maxDownloadTimes;
	}

	/**
	 * Sets the maximum download times.
	 *
	 * @param maxDownloadTimes the maximum download times
	 */
	public void setMaxDownloadTimes(final int maxDownloadTimes) {
		this.maxDownloadTimes = maxDownloadTimes;
	}

	@Override
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}
		
		if (!(other instanceof DigitalAssetImpl)) {
			return false;
		}
		
		DigitalAssetImpl asset = (DigitalAssetImpl) other;
		return ObjectUtils.equals(fileName, asset.fileName);
	}

	@Override
	public int hashCode() {
		return ObjectUtils.hashCode(fileName);
	}

	/**
	 * Get the encrypted uidPk string.
	 *
	 * @return the encrypted uidPk string
	 */
	@Transient
	public String getEncryptedUidPk() {

		if (encryptedUidPk == null || encryptedUidPk.length() == 0) {
			StringEncrypter stringEncrypter = getBean("digitalAssetStringEncrypter");
			encryptedUidPk = stringEncrypter.encrypt(String.valueOf(getUidPk()));
		}
		return encryptedUidPk;

	}

	/**
	 * Returns the file name without the path information.
	 *
	 * @return the file name
	 */
	@Transient
	public String getFileNameWithoutPath() {
		String fileName = getFileName();
		return fileName.substring(fileName.lastIndexOf('/') + 1);

	}

	/**
	 * The string representation of this object.
	 *
	 * @return the string representation
	 */
	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
				.append("fileName", getFileName())
				.append("expiryDays", getElasticPath())
				.append("maxDownloadTimes", getMaxDownloadTimes())
				.toString();
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
