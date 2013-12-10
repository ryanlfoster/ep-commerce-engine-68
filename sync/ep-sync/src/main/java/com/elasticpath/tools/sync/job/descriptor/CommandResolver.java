package com.elasticpath.tools.sync.job.descriptor;

import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;
import com.elasticpath.tools.sync.job.Command;

/**
 * Resolves which command should be used for synchronization of domain object from source server.
 */
public interface CommandResolver {

	/**
	 * Determines command which should be applied for synchronization of domain object, 
	 * based on the fact of existing of the entity on the source environment.
	 *
	 * @param businessObjectDescriptor descriptor of domain object
	 * @return command to use
	 */
    Command resolveCommandUsingSourceEnv(final BusinessObjectDescriptor businessObjectDescriptor);
}
