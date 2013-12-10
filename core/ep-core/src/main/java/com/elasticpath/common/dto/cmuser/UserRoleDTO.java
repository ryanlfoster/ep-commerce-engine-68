package com.elasticpath.common.dto.cmuser;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.elasticpath.common.dto.Dto;

/**
 * Data Transfer Object for {@link com.elasticpath.domain.cmuser.UserRole UserRole}.
 */
@XmlRootElement(name = UserRoleDTO.ROOT_ELEMENT)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { })
public class UserRoleDTO implements Dto {

	private static final long serialVersionUID = -1884914668048436985L;

	/**
	 * Root element name for {@link CmUserDTO}.
	 */
	public static final String ROOT_ELEMENT = "user_role";

	@XmlAttribute(name = "guid", required = true)
	private String guid;

	@XmlElement(name = "name", required = true)
	private String name;

	@XmlElement(name = "description", required = false)
	private String description;
	
	@XmlElementWrapper(name = "permissions")
	@XmlElement(name = "permission")
	private Set<String> permissions = new HashSet<String>();

	/**
	 * @return the guid
	 */
	public String getGuid() {
		return guid;
	}

	/**
	 * @param guid the guid to set
	 */
	public void setGuid(final String guid) {
		this.guid = guid;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(final String description) {
		this.description = description;
	}

	/**
	 * @return the permissions
	 */
	public Set<String> getPermissions() {
		return permissions;
	}

	/**
	 * @param permissions the permissions to set
	 */
	public void setPermissions(final Set<String> permissions) {
		this.permissions = permissions;
	}

}
