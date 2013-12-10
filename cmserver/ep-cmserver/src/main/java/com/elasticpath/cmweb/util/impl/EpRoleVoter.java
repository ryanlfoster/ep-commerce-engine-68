package com.elasticpath.cmweb.util.impl;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.core.Authentication;

import com.elasticpath.domain.cmuser.CmUser;

/**
 * Extends the <code>RoleVoter</code> to allow the CM user access web based application on the cmAccess setting.
 *
 * TODO: use secured object to distinguish the web call and ws call.
 *
 */
public class EpRoleVoter extends RoleVoter {
	private static final Logger LOG = Logger.getLogger(EpRoleVoter.class);
	
	private String permissionPrefix = "PERMISSION_";

	/**
	 * Check if the given config attribute is supported by <code>EpRoleVoter</code>.
	 *
	 * @param attribute the config attribute to check on.
	 * @return true if the given config attribute is supported by <code>EpRoleVoter</code>; otherwise, false.
	 */
	public boolean supports(final ConfigAttribute attribute) {
        if (attribute.getAttribute() != null
        		&& (attribute.getAttribute().startsWith(getRolePrefix()) || attribute.getAttribute().startsWith(getPermissionPrefix()))) {
            return true;
        }
        return false;
    }

	/**
	 * @param authentication - the caller invoking the method
	 * @param object - the secured object
     * @param attributes the configuration attributes associated with the method being invoked
  	 * @return Vote decision which must be affirmative (ACCESS_GRANTED), negative (ACCESS_DENIED)
	 * 			or abstain (ACCESS_ABSTAIN).
	 */
	@Override
	public int vote(final Authentication authentication, final Object object, final Collection<ConfigAttribute> attributes) {
		Object principal = authentication.getPrincipal();
		if (principal instanceof CmUser) {
			CmUser cmUser = (CmUser) principal;
			if (!cmUser.isWsAccess() && !cmUser.isCmAccess()) {
				LOG.debug("User does not have WS access or CM access. Authorization denied.");
				return ACCESS_DENIED;
			} 
		}
		return super.vote(authentication, object, attributes);
	}

    /**
     * Allows the default permission prefix of <code>PERMISSION_</code> to be overriden.
     *
     * @param permissionPrefix the new prefix
     */
    public void setPermissionPrefix(final String permissionPrefix) {
        this.permissionPrefix = permissionPrefix;
    }

    /**
     * Return the permission prefix.
     * @return the permission prefix.
     */
    public String getPermissionPrefix() {
        return this.permissionPrefix;
    }
}
