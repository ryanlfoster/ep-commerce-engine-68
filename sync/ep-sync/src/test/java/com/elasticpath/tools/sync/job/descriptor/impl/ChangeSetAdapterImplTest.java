package com.elasticpath.tools.sync.job.descriptor.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.changeset.ChangeSet;
import com.elasticpath.domain.changeset.impl.ChangeSetImpl;
import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;
import com.elasticpath.domain.objectgroup.impl.BusinessObjectDescriptorImpl;
import com.elasticpath.persistence.api.LoadTuner;
import com.elasticpath.service.changeset.ChangeSetManagementService;
import com.elasticpath.service.changeset.ChangeSetPolicy;
import com.elasticpath.service.changeset.ChangeSetSearchCriteria;
import com.elasticpath.tools.sync.exception.ChangeSetNotFoundException;
import com.elasticpath.tools.sync.exception.SyncToolRuntimeException;
import com.elasticpath.tools.sync.job.Command;
import com.elasticpath.tools.sync.job.descriptor.CommandResolver;
import com.elasticpath.tools.sync.job.descriptor.JobDescriptor;
import com.elasticpath.tools.sync.job.descriptor.TransactionJobDescriptor;
import com.elasticpath.tools.sync.job.descriptor.TransactionJobDescriptorEntry;

/**
 * Tests <code>ChangeSetAdapterImpl</code>.
 */
public class ChangeSetAdapterImplTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private static final String TYPE1 = "product";

	private static final String GUID_2 = "GUID_2";

	private static final String GUID_1 = "GUID_1";

	private ChangeSetAdapterImpl changeSetAdapterImpl;

	private ChangeSetManagementService changeSetManagementService;

	private ChangeSetPolicy changeSetPolicy;

	private CommandResolver commandResolver;

	private List<ChangeSet> changeSets;
	/**
	 * Sets required mock objects.
	 */
	@Before
	public void setUp() {
		changeSets = new ArrayList<ChangeSet>();
		changeSetAdapterImpl = new ChangeSetAdapterImpl() {
			@Override
			Collection<ChangeSet> initialize(final Object configuration) {
				return changeSets;
			}
		};
		changeSetManagementService = context.mock(ChangeSetManagementService.class);
		changeSetAdapterImpl.setChangeSetManagementService(changeSetManagementService);

		commandResolver = context.mock(CommandResolver.class);
		changeSetPolicy = context.mock(ChangeSetPolicy.class);
		changeSetAdapterImpl.setCommandResolver(commandResolver);
		changeSetAdapterImpl.setChangeSetPolicy(changeSetPolicy);
	}

	/**
	 * Tests main flow having provided two change sets. 
	 */
	@Test
	public void testBuildJobDescriptor() {
		
		final BusinessObjectDescriptor bod1 = createBusinessObjectDescriptor(GUID_2, TYPE1);
		
		final Collection<BusinessObjectDescriptor> bodCollection = Arrays.asList(bod1);
		final ChangeSet changeSet = context.mock(ChangeSet.class);
				
		context.checking(new Expectations() {
			{
				oneOf(changeSet).getMemberObjects();
				will(returnValue(bodCollection));
				oneOf(changeSetPolicy).getObjectClass(with(same(bod1)));
				will(returnValue(ProductImpl.class));
				oneOf(commandResolver).resolveCommandUsingSourceEnv(with(same(bod1)));
				will(returnValue(Command.REMOVE));
				oneOf(changeSet).getName(); will(returnValue("ChangeSet"));
			}
		});
		changeSets.add(changeSet);
		
		JobDescriptor buildJobDescriptor = changeSetAdapterImpl.buildJobDescriptor(null);
		assertEquals(1, buildJobDescriptor.getTransactionJobDescriptors().size());
		TransactionJobDescriptor transactionJobDescriptor = buildJobDescriptor.getTransactionJobDescriptors().iterator().next();
		
		assertEquals(1, transactionJobDescriptor.getJobDescriptorEntries().size());
		
		TransactionJobDescriptorEntry jobDescriptorEntry = transactionJobDescriptor.getJobDescriptorEntries().iterator().next();
		assertEquals(Command.REMOVE, jobDescriptorEntry.getCommand());
		assertEquals(GUID_2, jobDescriptorEntry.getGuid());
		assertEquals(ProductImpl.class, jobDescriptorEntry.getType());
	}
	
	/**
	 * Checks that exception is thrown if change set adapter receives wrong initialization parameter.
	 */
	@Test(expected = SyncToolRuntimeException.class)
	public void testWrongInitializationParameter() {
		new ChangeSetAdapterImpl().initialize(new ChangeSetSearchCriteria());
	}

	/**
	 * Checks that duplicated <code>BusinessObjectDescriptor</code>s describing same objects are removed.
	 */
	@Test
	public void testRemoveDuplicated() {
		BusinessObjectDescriptor bod1 = createBusinessObjectDescriptor(GUID_1, TYPE1);
		BusinessObjectDescriptor bod2 = createBusinessObjectDescriptor(GUID_2, TYPE1);
		BusinessObjectDescriptor bod3 = createBusinessObjectDescriptor(GUID_2, TYPE1);

		Collection<BusinessObjectDescriptor> uniqueBods = changeSetAdapterImpl.removeDuplicated(Arrays.asList(bod1, bod2, bod3));

		assertEquals(2, uniqueBods.size());
		assertTrue(uniqueBods.contains(bod1));
		assertTrue(uniqueBods.contains(bod2));
	}

	private BusinessObjectDescriptor createBusinessObjectDescriptor(final String guid, final String type) {
		final BusinessObjectDescriptor bod = new BusinessObjectDescriptorImpl();
		bod.setObjectIdentifier(guid);
		bod.setObjectType(type);
		return bod;
	}

	/**
	 * Checks that if object doesn't exist on staging then operation is "remove", "update" otherwise.
	 */
	@Test
	public void testResolveCommands() {
		final BusinessObjectDescriptor bod1 = createBusinessObjectDescriptor(GUID_1, TYPE1);
		final BusinessObjectDescriptor bod2 = createBusinessObjectDescriptor(GUID_2, TYPE1);

		context.checking(new Expectations() {
			{
				oneOf(commandResolver).resolveCommandUsingSourceEnv(with(same(bod1)));
				will(returnValue(Command.REMOVE));
				oneOf(changeSetPolicy).getObjectClass(with(same(bod1)));
				will(returnValue(ProductImpl.class));
				oneOf(commandResolver).resolveCommandUsingSourceEnv(with(same(bod2)));
				will(returnValue(Command.UPDATE));
				oneOf(changeSetPolicy).getObjectClass(with(same(bod2)));
				will(returnValue(ProductImpl.class));
			}
		});

		List<TransactionJobDescriptorEntry> entryList = changeSetAdapterImpl.resolveCommands(Arrays.asList(bod1, bod2));

		assertEquals(2, entryList.size());

		assertEquals(GUID_1, entryList.get(0).getGuid());
		assertEquals(GUID_2, entryList.get(1).getGuid());

		assertEquals(Command.REMOVE, entryList.get(0).getCommand());
		assertEquals(Command.UPDATE, entryList.get(1).getCommand());

		assertEquals(ProductImpl.class, entryList.get(0).getType());
		assertEquals(ProductImpl.class, entryList.get(1).getType());
	}

	/**
	 * Checks that initialisation method fails with appropriate exception if change set doesn't exist.
	 */
	@Test(expected = ChangeSetNotFoundException.class)
	public void testInitializeMissingChangeSet() {
		changeSetAdapterImpl = new ChangeSetAdapterImpl();
		changeSetAdapterImpl.setChangeSetManagementService(changeSetManagementService);

		context.checking(new Expectations() { {
			oneOf(changeSetManagementService).get(with(any(String.class)), with(aNull(LoadTuner.class)));
			will(returnValue(null));
		} });

		changeSetAdapterImpl.initialize("");
	}

	/**
	 * Collection of change sets should be successfully found.
	 */
	@Test
	public void testInitialize() {
		changeSetAdapterImpl = new ChangeSetAdapterImpl();
		changeSetAdapterImpl.setChangeSetManagementService(changeSetManagementService);
		final ChangeSet changeSet = new ChangeSetImpl();
		
		context.checking(new Expectations() { {
			oneOf(changeSetManagementService).get(with(any(String.class)), with(aNull(LoadTuner.class)));
			will(returnValue(changeSet));
		} });
		
		assertEquals(Arrays.asList(changeSet), changeSetAdapterImpl.initialize(""));
	}
}
