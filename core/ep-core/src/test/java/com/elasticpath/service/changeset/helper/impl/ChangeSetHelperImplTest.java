package com.elasticpath.service.changeset.helper.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.ElasticPath;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.changeset.ChangeSetMember;
import com.elasticpath.domain.changeset.impl.ChangeSetMemberImpl;
import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;
import com.elasticpath.domain.objectgroup.BusinessObjectGroupMember;
import com.elasticpath.domain.objectgroup.BusinessObjectMetadata;
import com.elasticpath.domain.objectgroup.impl.BusinessObjectDescriptorImpl;
import com.elasticpath.domain.objectgroup.impl.BusinessObjectGroupMemberImpl;
import com.elasticpath.domain.objectgroup.impl.BusinessObjectMetadataImpl;

/**
 * Tests for ChangeSetHelperImpl.
 */
public class ChangeSetHelperImplTest {

	private static final String GROUP_ID = "groupId1";

	private static final String OBJECT_ID = "objID1";

	private static final String OBJECT_TYPE = "Product";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private ElasticPath elasticPath;

	private ChangeSetHelperImpl changeSetHelper;

	/**
	 * Sets up a test case.
	 */
	@Before
	public void setUp() {
		elasticPath = context.mock(ElasticPath.class);

		changeSetHelper = new ChangeSetHelperImpl();
		changeSetHelper.setElasticPath(elasticPath);
	}

	/**
	 * Tests that converting member object metadata to change set members works as expected.
	 */
	@Test
	public void testConvertGroupMembersToChangeSetMembers() {
		final Collection<BusinessObjectMetadata> metadataCollection = new HashSet<BusinessObjectMetadata>();

		final Collection<BusinessObjectGroupMember> memberCollection = new HashSet<BusinessObjectGroupMember>();

		final BusinessObjectGroupMember member1 = new BusinessObjectGroupMemberImpl();
		member1.setGroupId(GROUP_ID);
		member1.setObjectIdentifier(OBJECT_ID);
		member1.setObjectType(OBJECT_TYPE);
		memberCollection.add(member1);

		BusinessObjectMetadata metadata = new BusinessObjectMetadataImpl();
		metadata.setMetadataKey("key1");
		metadata.setMetadataValue("value1");

		metadata.setBusinessObjectGroupMember(member1);

		metadataCollection.add(metadata);

		metadataCollection.add(metadata);

		context.checking(new Expectations() {
			{
				oneOf(elasticPath).getBean(ContextIdNames.CHANGESET_MEMBER);
				will(returnValue(new ChangeSetMemberImpl()));

				oneOf(elasticPath).getBean(ContextIdNames.BUSINESS_OBJECT_DESCRIPTOR);
				will(returnValue(new BusinessObjectDescriptorImpl()));

			}
		});
		Collection<ChangeSetMember> result = changeSetHelper.convertGroupMembersToChangeSetMembers(memberCollection, metadataCollection);

		final BusinessObjectDescriptor expectedBusinessObjectDescriptor = new BusinessObjectDescriptorImpl();
		expectedBusinessObjectDescriptor.setObjectIdentifier(OBJECT_ID);
		expectedBusinessObjectDescriptor.setObjectType(OBJECT_TYPE);

		assertEquals("expects one change set member", 1, result.size());
		Iterator<ChangeSetMember> iterator = result.iterator();
		ChangeSetMember resultChangeSetMember1 = iterator.next();
		assertEquals("expects one meta data per member", 1, resultChangeSetMember1.getMetadata().size());
		assertEquals("expects the meta data key to be key1", "key1", resultChangeSetMember1.getMetadata().keySet().iterator().next());
		assertEquals("expects the meta data value to be value1", "value1", resultChangeSetMember1.getMetadata().values().iterator().next());
		assertEquals("expects the buisness object descriptor should be point to the same object.", 
						expectedBusinessObjectDescriptor, resultChangeSetMember1.getBusinessObjectDescriptor());
}
	
	/**
	 * Tests that if the {@link ChangeSetMember} bean does not implement the mutator we throw a sensible exception.
	 */
	@Test
	public void testConvertWithWrongChangeSetMemberImplementation() {
		// change set member not implementing ChangeSetMutator
		final ChangeSetMember changeSetMember = context.mock(ChangeSetMember.class);
		
		final Collection<BusinessObjectGroupMember> memberCollection = new HashSet<BusinessObjectGroupMember>();
		
		final Collection<BusinessObjectMetadata> metadataCollection = new HashSet<BusinessObjectMetadata>();

		final BusinessObjectGroupMember member1 = new BusinessObjectGroupMemberImpl();
		member1.setGroupId(GROUP_ID);
		member1.setObjectIdentifier(OBJECT_ID);
		member1.setObjectType(OBJECT_TYPE);

		BusinessObjectMetadata metadata = new BusinessObjectMetadataImpl();
		metadata.setMetadataKey("key1");
		metadata.setMetadataValue("value1");
		memberCollection.add(member1);

		metadata.setBusinessObjectGroupMember(member1);

		metadataCollection.add(metadata);

		context.checking(new Expectations() {
			{
				oneOf(elasticPath).getBean(ContextIdNames.CHANGESET_MEMBER);
				will(returnValue(changeSetMember));

				oneOf(elasticPath).getBean(ContextIdNames.BUSINESS_OBJECT_DESCRIPTOR);
				will(returnValue(new BusinessObjectDescriptorImpl()));

			}
		});

		try {
			changeSetHelper.convertGroupMembersToChangeSetMembers(memberCollection, metadataCollection);
			fail("Expected exception when change set member does not implement the mutator");
		} catch (EpDomainException exc) {
			assertNotNull(exc);
		}
	}
}
