package com.elasticpath.importexport.common.dto.products;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import com.elasticpath.common.dto.Dto;

/**
 * The implementation of the <code>Dto</code> interface that contains data of digital asset object.
 * <p>
 * This implementation designed for JAXB to working with xml representation of data
 */
@XmlAccessorType(XmlAccessType.NONE)
public class DigitalAssetItemDTO implements Dto {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@XmlElement(name = "file")
	private String fileName;

	@XmlElement(name = "downloadexpiry")
	private Integer expiryDays;

	@XmlElement(name = "downloadlimit")
	private Integer maxDownloadTimes;

	@XmlAttribute(name = "enabled", required = true)
	private boolean enabled;

	/**
	 * Gets the file name.
	 * 
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * Sets the file name.
	 * 
	 * @param fileName the fileName to set
	 */
	public void setFileName(final String fileName) {
		this.fileName = fileName;
	}

	/**
	 * Gets the expiry days.
	 * 
	 * @return the expiryDays
	 */
	public Integer getExpiryDays() {
		return expiryDays;
	}

	/**
	 * Sets the expiry days.
	 * 
	 * @param expiryDays the expiryDays to set
	 */
	public void setExpiryDays(final Integer expiryDays) {
		this.expiryDays = expiryDays;
	}

	/**
	 * Gets the maximium download times.
	 * 
	 * @return the maxDownloadTimes
	 */
	public Integer getMaxDownloadTimes() {
		return maxDownloadTimes;
	}

	/**
	 * Sets the maximum download times.
	 * 
	 * @param maxDownloadTimes the maxDownloadTimes to set
	 */
	public void setMaxDownloadTimes(final Integer maxDownloadTimes) {
		this.maxDownloadTimes = maxDownloadTimes;
	}

	/**
	 * Gets the enabled.
	 * 
	 * @return the enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * Sets the enabled.
	 * 
	 * @param enabled the enabled to set
	 */
	public void setEnabled(final boolean enabled) {
		this.enabled = enabled;
	}

}
