package com.elasticpath.importexport.common.dto.cmuser;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.elasticpath.common.dto.cmuser.CmUserDTO;

/**
 * Data Transfer Object for Commerce Manager Users. 
 */
@XmlRootElement(name = "cmusers")
@XmlType(name = "cmusersDTO", propOrder = { })
@XmlAccessorType(XmlAccessType.NONE)
public class CmUsersDTO {

	@XmlElement(name = "cmuser")
	private final List<CmUserDTO> cmUsers = new ArrayList<CmUserDTO>();

	/**
	 * @return the cmUsers
	 */
	public List<CmUserDTO> getCmUsers() {
		return cmUsers;
	}

}

