package com.elasticpath.tools.sync.job.descriptor.impl;

import static org.junit.Assert.assertEquals;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;
import com.elasticpath.domain.objectgroup.impl.BusinessObjectDescriptorImpl;
import com.elasticpath.service.changeset.ChangeSetPolicy;
import com.elasticpath.tools.sync.exception.SyncToolConfigurationException;
import com.elasticpath.tools.sync.job.Command;
import com.elasticpath.tools.sync.merge.configuration.EntityLocator;

/**
 * Command resolver should determine synchronization <code>Command</code> depending
 * on existence of domain object on staging server.
 */
public class CommandResolverImplTest {

	private CommandResolverImpl commandResolver;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private EntityLocator entityLocator;

	private ChangeSetPolicy changeSetPolicy;

	/**
	 * Overrides command resolver under the test to inject mock syncService.
	 */
	@Before
	public void setUp() {
		commandResolver = new CommandResolverImpl();
		entityLocator = context.mock(EntityLocator.class);
		changeSetPolicy = context.mock(ChangeSetPolicy.class);

		commandResolver.setEntityLocator(entityLocator);
		commandResolver.setChangeSetPolicy(changeSetPolicy);
	}

	/**
	 * If object doesn't exist then command is REMOVE, otherwise UPDATE.
	 */
	@Test
	public void testGetCommand() {
		assertEquals(Command.REMOVE, commandResolver.getCommand(false));
		assertEquals(Command.UPDATE, commandResolver.getCommand(true));
	}

	/**
	 * Checks that resolver seeks for object on staging server and resolves
	 * command depending on existence of object described by <code>BusinessObjectDescriptor</code>.
	 *
	 * @throws SyncToolConfigurationException in case business object couldn't be located
	 */
	@Test
	public void testResolveCommandUsingStaging() throws SyncToolConfigurationException {
		final BusinessObjectDescriptor businessObjectDescriptor = new BusinessObjectDescriptorImpl();
		final String businessObjectIdentifier = "PRODUCT_1234";
		businessObjectDescriptor.setObjectIdentifier(businessObjectIdentifier);

		context.checking(new Expectations() { {
			oneOf(entityLocator).entityExists(businessObjectIdentifier, ProductImpl.class); will(returnValue(false));
			oneOf(changeSetPolicy).getObjectClass(businessObjectDescriptor); will(returnValue(ProductImpl.class));
			oneOf(entityLocator).entityExists(businessObjectIdentifier, ProductImpl.class); will(returnValue(true));
			oneOf(changeSetPolicy).getObjectClass(businessObjectDescriptor); will(returnValue(ProductImpl.class));
		} });

		assertEquals(Command.REMOVE, commandResolver.resolveCommandUsingSourceEnv(businessObjectDescriptor));
		assertEquals(Command.UPDATE, commandResolver.resolveCommandUsingSourceEnv(businessObjectDescriptor));
	}
}
