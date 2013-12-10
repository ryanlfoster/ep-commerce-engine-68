package com.elasticpath.importexport.common.dto.category;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.elasticpath.common.dto.Dto;

/**
 * The implementation of the <code>Dto</code> interface that contains data of Category availability object.
 * <p>
 * This implementation designed for JAXB to working with xml representation of data
 */
@XmlAccessorType(XmlAccessType.NONE)
public class CategoryAvailabilityDTO implements Dto {

	private static final long serialVersionUID = 1L;

	@XmlElement(name = "storevisible", required = true)
	private boolean storeVisible;

	@XmlElement(name = "enabledate", required = true)
	private Date startDate;

	@XmlElement(name = "disabledate")
	private Date endDate;

	/**
	 * Gets the storeVisible.
	 *
	 * @return the storeVisible
	 */
	public boolean isStoreVisible() {
		return storeVisible;
	}

	/**
	 * Sets the storeVisible.
	 *
	 * @param storeVisible the storeVisible to set
	 */
	public void setStoreVisible(final boolean storeVisible) {
		this.storeVisible = storeVisible;
	}

	/**
	 * Gets the startDate.
	 *
	 * @return the startDate
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * Sets the startDate.
	 *
	 * @param startDate the startDate to set
	 */
	public void setStartDate(final Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * Gets the endDate.
	 *
	 * @return the endDate
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * Sets the endDate.
	 *
	 * @param endDate the endDate to set
	 */
	public void setEndDate(final Date endDate) {
		this.endDate = endDate;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
			.append("storeVisible", isStoreVisible())
			.append("startDate", getStartDate())
			.append("endDate", getEndDate())
			.toString();
	}

}
