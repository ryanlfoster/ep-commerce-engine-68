package com.elasticpath.importexport.common.dto.userrole;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.elasticpath.common.dto.cmuser.UserRoleDTO;

/**
 * Wrapper for schema generation for a collection of UserRoleDTOs.
 */
@XmlRootElement(name = "user_roles")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "user_rolesDTO", propOrder = { })
public class UserRolesDTO {
	@XmlElement(name = "user_role")
	private final List<UserRoleDTO> userRoles = new ArrayList<UserRoleDTO>();

	/**
	 * @return the userRoles
	 */
	public List<UserRoleDTO> getUserRoles() {
		return userRoles;
	}

}