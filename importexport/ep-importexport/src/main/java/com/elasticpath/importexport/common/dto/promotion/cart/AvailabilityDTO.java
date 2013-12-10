package com.elasticpath.importexport.common.dto.promotion.cart;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import com.elasticpath.common.dto.Dto;

/**
 * Contains XML mapping on data responsible for availability of <code>AbstractRuleImpl</code>.
 * Designed for JAXB
 */
@XmlAccessorType(XmlAccessType.NONE)
public class AvailabilityDTO implements Dto {

	private static final long serialVersionUID = 1L;

	@XmlElement(name = "enabled", required = true)
	private Boolean enabled;

	@XmlElement(name = "enabledate")
	private Date enableDate;

	@XmlElement(name = "disabledate")
	private Date disableDate;

	/**
	 * Gets the flag determining whether the rule is enabled or not.
	 *  
	 * @return the enabled flag value
	 */
	public Boolean getEnabled() {
		return enabled;
	}

	/**
	 * Sets the flag determining whether the rule is enabled or not.
	 * 
	 * @param enabled flag to set
	 */
	public void setEnabled(final Boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * Gets a date when rule becomes enabled.
	 * 
	 * @return the enableDate
	 */
	public Date getEnableDate() {
		return enableDate;
	}

	/**
	 * Sets a date when rule becomes enabled.
	 * 
	 * @param enableDate the enableDate to set
	 */
	public void setEnableDate(final Date enableDate) {
		this.enableDate = enableDate;
	}

	/**
	 * Gets a date when rule becomes disabled.
	 * 
	 * @return the disableDate
	 */
	public Date getDisableDate() {
		return disableDate;
	}

	/**
	 * Sets a date when rule becomes disabled.
	 * 
	 * @param disableDate the disableDate to set
	 */
	public void setDisableDate(final Date disableDate) {
		this.disableDate = disableDate;
	}
}
