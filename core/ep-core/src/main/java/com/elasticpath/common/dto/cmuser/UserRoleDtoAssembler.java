package com.elasticpath.common.dto.cmuser;

import com.elasticpath.common.dto.assembler.AbstractDtoAssembler;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.cmuser.UserPermission;
import com.elasticpath.domain.cmuser.UserRole;

/**
 * Assembler for UserRole domain objects and their associated DTOs. 
 */
public class UserRoleDtoAssembler extends AbstractDtoAssembler<UserRoleDTO, UserRole> {

	private BeanFactory beanFactory;

	@Override
	public UserRole getDomainInstance() {
		return beanFactory.getBean(ContextIdNames.USER_ROLE);
	}

	@Override
	public UserRoleDTO getDtoInstance() {
		return new UserRoleDTO();
	}

	/**
	 * Factory for {@link UserPermission}.
	 * @return a new, uninitialized {@link UserPermission} object.
	 */
	protected UserPermission userPermissionDomainFactory() {
		return beanFactory.getBean(ContextIdNames.USER_PERMISSION);
	}
	
	@Override
	public void assembleDto(final UserRole source, final UserRoleDTO target) {
		target.setGuid(source.getGuid());
		target.setName(source.getName());
		target.setDescription(source.getDescription());
		
		for (UserPermission sourcePermission : source.getUserPermissions()) {
			target.getPermissions().add(sourcePermission.getAuthority());
		}
	}

	@Override
	public void assembleDomain(final UserRoleDTO source, final UserRole target) {
		target.setGuid(source.getGuid());
		target.setName(source.getName());
		target.setDescription(source.getDescription());
		
		for (String sourcePermission : source.getPermissions()) {

			UserPermission currentUserPermission = userPermissionDomainFactory();
			currentUserPermission.setAuthority(sourcePermission);
			
			if (!target.getUserPermissions().contains(currentUserPermission)) {
				target.getUserPermissions().add(currentUserPermission);
			}
			
		}
	}

	/**
	 * @return the beanFactory
	 */
	public BeanFactory getBeanFactory() {
		return beanFactory;
	}

	/**
	 * @param beanFactory the beanFactory to set
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}
	
}
