package com.elasticpath.tools.sync.job.descriptor.impl;

import org.apache.log4j.Logger;

import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;
import com.elasticpath.service.changeset.ChangeSetPolicy;
import com.elasticpath.tools.sync.exception.SyncToolConfigurationException;
import com.elasticpath.tools.sync.job.Command;
import com.elasticpath.tools.sync.job.descriptor.CommandResolver;
import com.elasticpath.tools.sync.merge.configuration.EntityLocator;

/**
 * If object doesn't exist on source environment then it should be removed on target system,
 * otherwise it should be updated or added.
 */
public class CommandResolverImpl implements CommandResolver {

	private static final Logger LOG = Logger.getLogger(CommandResolverImpl.class);

	private EntityLocator entityLocator;

	private ChangeSetPolicy changeSetPolicy;

	@Override
	public Command resolveCommandUsingSourceEnv(final BusinessObjectDescriptor businessObjectDescriptor) {
		final String identifier = businessObjectDescriptor.getObjectIdentifier();
		boolean entityExists = false;
        try {
			final Class<?> objectClass = changeSetPolicy.getObjectClass(businessObjectDescriptor);
			    entityExists = entityLocator.entityExists(identifier, objectClass);

		} catch (final SyncToolConfigurationException exception) {
			LOG.error("Unable to locate object of type '" + businessObjectDescriptor.getObjectType()
					+ "' with guid = " + identifier, exception);
		}
		return getCommand(entityExists);
	}

	/**
	 * Command to use is determined depending on object existence.
	 * @param doesEntityExist TODO
	 *
	 * @return command to use for synchronization
	 */
	Command getCommand(final Boolean doesEntityExist) {
	    if (doesEntityExist) {
	        return Command.UPDATE;
	    }
	    return Command.REMOVE;
	}

	/**
	 * @param entityLocator retrieves entity by class and guid.
	 */
	public void setEntityLocator(final EntityLocator entityLocator) {
		this.entityLocator = entityLocator;
	}

	/**
	 * @param changeSetPolicy change set policy to retrieve business objects' types
	 */
	public void setChangeSetPolicy(final ChangeSetPolicy changeSetPolicy) {
		this.changeSetPolicy = changeSetPolicy;
	}
}
