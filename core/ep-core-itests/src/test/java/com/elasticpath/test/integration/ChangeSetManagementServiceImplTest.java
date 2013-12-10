package com.elasticpath.test.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.HashSet;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.commons.util.Pair;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.changeset.ChangeSet;
import com.elasticpath.domain.changeset.ChangeSetMember;
import com.elasticpath.domain.changeset.ChangeSetObjectStatus;
import com.elasticpath.domain.changeset.ChangeSetStateCode;
import com.elasticpath.domain.changeset.impl.ChangeSetImpl;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;
import com.elasticpath.domain.objectgroup.impl.BusinessObjectDescriptorImpl;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.service.changeset.ChangeSetManagementService;
import com.elasticpath.service.changeset.ChangeSetSearchCriteria;
import com.elasticpath.service.changeset.ChangeSetService;
import com.elasticpath.test.persister.StoreTestPersister;
import com.elasticpath.test.persister.TaxTestPersister;
import com.elasticpath.test.persister.testscenarios.SimpleStoreScenario;

/**
 * An integration test for {@link ChangeSetManagementService} implementation.
 */
@SuppressWarnings("PMD.TooManyStaticImports")
public class ChangeSetManagementServiceImplTest extends BasicSpringContextTest {
	
	private static final Logger LOG = Logger.getLogger(ChangeSetManagementServiceImplTest.class);
	
	private static final int TEST_CHANGE_SET_NUMBER = 3;
	
	@Autowired
	private ChangeSetManagementService changeSetManagementService;

	@Autowired
	private ChangeSetService changeSetService;

	private SimpleStoreScenario scenario;
	
	/**
	 * Sets up the test case.
	 */
	@Before
	public void setUp() {
		scenario = getTac().useScenario(SimpleStoreScenario.class);
	}
	
	/**
	 * Tests adding a new change set.
	 */
	@DirtiesDatabase
	@Test
	public void testAdd() {
		ChangeSet changeSet = addChangeSet();
		
		ChangeSet retrievedChangeSet = changeSetManagementService.get(changeSet.getGuid(), null);
		assertNotNull(retrievedChangeSet);
		
		assertEquals("Change sets should be equal", changeSet, retrievedChangeSet);
	}

	/**
	 *
	 */
	private ChangeSet addChangeSet() {
		return addChangeSet("changeSet1", "user1");
	}
	
	/**
	 *
	 */
	private ChangeSet addChangeSet(final String changeSetName, final String changeSetUserGuid) {
		ChangeSet changeSet = new ChangeSetImpl();
		changeSet.setName(changeSetName);
		changeSet.setCreatedByUserGuid(changeSetUserGuid);

		return changeSetManagementService.add(changeSet);
	}

	/**
	 * Tests removing of a change set.
	 */
	@DirtiesDatabase
	@Test
	public void testRemove() {
		ChangeSet changeSet = addChangeSet();
		
		changeSetManagementService.remove(changeSet.getGuid());
		
		ChangeSet retrievedChangeSet = changeSetManagementService.get(changeSet.getGuid(), null);
		assertNull("No change set should exist in the data source", retrievedChangeSet);
	}

	/**
	 * Tests updating a change set.
	 */
	@DirtiesDatabase
	@Test
	public void testUpdate() {
		ChangeSet changeSet = addChangeSet();

		final String name = "changeSetsNewName";
		changeSet.setName(name);
		
		ChangeSet updatedChangeSet = changeSetManagementService.update(changeSet, null);
		
		assertEquals("The change set name should be the new one", changeSet, updatedChangeSet);
	}

	/**
	 * Tests that changing the state of a change set works as expected.
	 */
	@DirtiesDatabase
	@Test
	public void testUpdateState() {
		ChangeSet changeSet = addChangeSet();
		assertEquals("When a change set is created it must be in active state", ChangeSetStateCode.OPEN, changeSet.getStateCode());
		
		changeSet = changeSetManagementService.updateState(changeSet.getGuid(), ChangeSetStateCode.LOCKED, null);
		// retrieve the change set again...
		changeSet = changeSetManagementService.get(changeSet.getGuid(), null);
		
		assertEquals("The state should be LOCKED", ChangeSetStateCode.LOCKED, changeSet.getStateCode());
		
		changeSet = changeSetManagementService.updateState(changeSet.getGuid(), ChangeSetStateCode.OPEN, null);
		// retrieve the change set again...
		changeSet = changeSetManagementService.get(changeSet.getGuid(), null);
		
		assertEquals("The state should be ACTIVE", ChangeSetStateCode.OPEN, changeSet.getStateCode());

	}
	
	/**
	 * Test to find change set by search criteria.
	 */
	@DirtiesDatabase
	@Test
	public void testFindByCriteria() {
		ChangeSet changeSet = addChangeSet();
		assertEquals("When a change set is created it must be in active state", ChangeSetStateCode.OPEN, changeSet.getStateCode());
		
		ChangeSet changeSet2 = addChangeSet("changeSet2", "user2");
		
		ChangeSet changeSet3 = addChangeSet("third change set", "user2");
		changeSet3 = changeSetManagementService.updateState(changeSet3.getGuid(), ChangeSetStateCode.LOCKED, null);
		
		ChangeSetSearchCriteria criteria = new ChangeSetSearchCriteria();
		Collection<ChangeSet> foundChangeSet = changeSetManagementService.findByCriteria(criteria, null);
		assertEquals("Empty criteria should return all change sets.", TEST_CHANGE_SET_NUMBER, foundChangeSet.size());
		
		criteria.setUserGuid("user2");
		foundChangeSet = changeSetManagementService.findByCriteria(criteria, null);
		assertEquals("Should find two changesets for user2.", 2, foundChangeSet.size());
		
		criteria.setChangeSetStateCode(ChangeSetStateCode.OPEN);
		foundChangeSet = changeSetManagementService.findByCriteria(criteria, null);
		assertEquals("Only one open change set should be found.", 1, foundChangeSet.size());
		assertEquals("The only open change set should be change set 2.", changeSet2, foundChangeSet.iterator().next());		
	}
	
	/**
	 * Test to find change set by search criteria.
	 */
	@DirtiesDatabase
	@Test
	public void testFindByCriteriaByChangeSetNameAndAssignedUserName() {
		StoreTestPersister storePersister = getTac().getPersistersFactory().getStoreTestPersister();
		CmUser cmUser1 = storePersister.persistDefaultCmUser();
		CmUser cmUser2 = storePersister.persistDefaultCmUser();
		
		ChangeSet changeSet = addChangeSet("changeSet1", cmUser1.getGuid());
		assertEquals("When a change set is created it has to be in active state", ChangeSetStateCode.OPEN, changeSet.getStateCode());
		
		addChangeSet("changeSet2", cmUser2.getGuid());
		
		ChangeSet changeSet3 = addChangeSet("changeSet3", cmUser2.getGuid());
		changeSet3 = changeSetManagementService.updateState(changeSet3.getGuid(), ChangeSetStateCode.LOCKED, null);
		
		ChangeSetSearchCriteria criteria = new ChangeSetSearchCriteria();
		criteria.setAssignedUserName(cmUser2.getUserName());
		Collection<ChangeSet> foundChangeSet = changeSetManagementService.findByCriteria(criteria, null);
		assertEquals("Should find two changesets for user2.", 2, foundChangeSet.size());
		
		criteria.setChangeSetName("changeSet3");
		foundChangeSet = changeSetManagementService.findByCriteria(criteria, null);
		assertEquals("Only one open change set should be found.", 1, foundChangeSet.size());
		assertEquals("The found change set name should be changeSet3.", changeSet3, foundChangeSet.iterator().next());
		
		criteria.setAssignedUserName("unkownUserName");
		foundChangeSet = changeSetManagementService.findByCriteria(criteria, null);
		assertEquals("Only one open change set should be found.", 0, foundChangeSet.size());
	}

	/**
	 * Test to find change set by search criteria.
	 */
	@DirtiesDatabase
	@Test
	public void testFindByCriteriaByChangeSetNameOnly() {
		ChangeSet changeSet = addChangeSet("change Set 1", "myUser1");
		assertEquals("When a change set is created it has to be in active state", ChangeSetStateCode.OPEN, changeSet.getStateCode());
		
		addChangeSet("change Set2", "myUser2");
		
		ChangeSet changeSet3 = addChangeSet("changeSet3", "myUser2");
		changeSet3 = changeSetManagementService.updateState(changeSet3.getGuid(), ChangeSetStateCode.LOCKED, null);
		
		ChangeSetSearchCriteria criteria = new ChangeSetSearchCriteria();
		criteria.setChangeSetName("Set");
		Collection<ChangeSet> foundChangeSet = changeSetManagementService.findByCriteria(criteria, null);
		final int expectedNumberOfChangeSets = 3;
		assertEquals("Should find all change sets", expectedNumberOfChangeSets, foundChangeSet.size());
	}

	/**
	 * Tests that after a change set gets FINALIZED the objects are released and can be added to another change set.
	 */
	@DirtiesDatabase
	@Test
	public void testObjectsReleasedAfterAddingToFinalizedChangeSet() {
		// create the a new change set
		ChangeSet firstChangeSet = addChangeSet();
		assertEquals("When a change set is created it has to be in active state", ChangeSetStateCode.OPEN, firstChangeSet.getStateCode());
		
		// create a sample product
		Catalog catalog = scenario.getCatalog();
		TaxCode taxCode = getTac().getPersistersFactory().
			getTaxTestPersister().getTaxCode(TaxTestPersister.TAX_CODE_GOODS);
		Product product = getTac().getPersistersFactory().getCatalogTestPersister().
			createSimpleProduct("productType123", "productCode123", catalog, taxCode, scenario.getCategory());
		
		// add the product to a change set
		changeSetService.addObjectToChangeSet(firstChangeSet.getGuid(), product, null);
		
		// check the product's status
		ChangeSetObjectStatus status = changeSetService.getStatus(product);
		assertTrue("the object should be a member of the change set", status.isMember(firstChangeSet.getGuid()));
		assertTrue("the object should be available that change set", status.isAvailable(firstChangeSet.getGuid()));
		
		// FINALIZE the change set
		firstChangeSet = changeSetManagementService.updateState(firstChangeSet.getGuid(), ChangeSetStateCode.FINALIZED, null);
		assertEquals("Finalized state is expected", ChangeSetStateCode.FINALIZED, firstChangeSet.getStateCode());
		
		// check the product's state after the change set was FINALIZED
		status = changeSetService.getStatus(product);
		assertFalse("the object should not be a member of any change set", status.isMember(firstChangeSet.getGuid()));
		assertTrue("the object should be available to be added to any change set", status.isAvailable(firstChangeSet.getGuid()));
		
		// add the object to a new change set
		ChangeSet newChangeSet = addChangeSet();
		changeSetService.addObjectToChangeSet(newChangeSet.getGuid(), product, null);
		
		// check status for the first (FINALIZED) change set
		status = changeSetService.getStatus(product);
		assertFalse("the object should be a member of the change set", status.isMember(firstChangeSet.getGuid()));
		assertFalse("the object should be available the new change set", status.isAvailable(firstChangeSet.getGuid()));
		
		// check status with the new change set
		status = changeSetService.getStatus(product);
		assertTrue("the object should be a member of the change set", status.isMember(newChangeSet.getGuid()));
		assertTrue("the object should be available the new change set", status.isAvailable(newChangeSet.getGuid()));

	}
	
	/**
	 * Tests moving objects between change sets.
	 */
	@DirtiesDatabase
	@Test
	public void testMoveObjects() {
		
		ChangeSet changeSetA = addChangeSet();
		
		// Add change set B
		ChangeSet changeSetB = addChangeSet();
		
		// Add object to change set B
		BusinessObjectDescriptor objectDescriptorB = new BusinessObjectDescriptorImpl();		
		objectDescriptorB.setObjectIdentifier("productGuid693");
		objectDescriptorB.setObjectType("Sku");
		changeSetService.addObjectToChangeSet(changeSetB.getObjectGroupId(), objectDescriptorB, null);
		
		LOG.debug("Change set B guid: " + changeSetB.getGuid());
		
		// Get the change set with the change set members
		ChangeSet originChangeSet = changeSetManagementService.get(changeSetB.getGuid(), null);
		
		Collection<BusinessObjectDescriptor> checkedBods = new HashSet<BusinessObjectDescriptor>(0);
		
		for (ChangeSetMember changeSetMember : originChangeSet.getChangeSetMembers()) {
			
			checkedBods.add(changeSetMember.getBusinessObjectDescriptor());
		}											
				
		// Move the checked change set member to change set A from B
		Pair<ChangeSet, ChangeSet> updatedPair = changeSetManagementService.updateAndMoveObjects(changeSetB.getGuid(), 
				                                                                        		 changeSetA.getGuid(), 
				                                                                        		 checkedBods, null);
		ChangeSet initialFirstChangeSet = updatedPair.getFirst();
		ChangeSet initialLastChangeSet = updatedPair.getSecond();
		
		assertFalse("First change set still contains the moved object",
				initialFirstChangeSet.getChangeSetMembers().contains(
						objectDescriptorB));
		
		assertTrue("Last change set DOES NOT contain the object moved",
				initialLastChangeSet.getMemberObjects().contains(
						objectDescriptorB));	
	}
}
