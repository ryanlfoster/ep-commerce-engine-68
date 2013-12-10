package com.elasticpath.common.dto.cmuser;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.elasticpath.common.dto.Dto;

/**
 * Data Transfer Object for {@link com.elasticpath.domain.cmuser.UserPasswordHistoryItem}.
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { })
public class UserPasswordHistoryItemDTO implements Dto {

	private static final long serialVersionUID = 4492235218051684527L;

	/**
	 * Root element name for UserPasswordHistoryItemDTO.
	 */
	public static final String ROOT_ELEMENT = "userPasswordHistoryItem";

	@XmlElement(name = "oldpassword", required = true)
	private String oldPassword;

	@XmlElement(name = "expirationdate", required = true)
	private Date expirationDate;

	/**
	 * @param password the oldPassword to set
	 */
	public void setOldPassword(final String password) {
		this.oldPassword = password;
	}

	/**
	 * @return the oldPassword
	 */
	public String getOldPassword() {
		return oldPassword;
	}

	/**
	 * @param expirationDate the expirationDate to set
	 */
	public void setExpirationDate(final Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	/**
	 * @return the expirationDate
	 */
	public Date getExpirationDate() {
		return expirationDate;
	}

}
