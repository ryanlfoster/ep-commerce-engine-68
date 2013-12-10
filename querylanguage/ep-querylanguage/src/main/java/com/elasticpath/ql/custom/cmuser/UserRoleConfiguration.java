package com.elasticpath.ql.custom.cmuser;

import com.elasticpath.ql.parser.AbstractEpQLCustomConfiguration;

/**
 * Holds mapping between EPQL fields and field descriptors for UserRole.
 */
public class UserRoleConfiguration extends AbstractEpQLCustomConfiguration {
	
	@Override
	public void initialize() {
		setQueryPrefix("SELECT u.guid FROM UserRoleImpl u ");
		setQueryPostfix("");
	}
	
}
