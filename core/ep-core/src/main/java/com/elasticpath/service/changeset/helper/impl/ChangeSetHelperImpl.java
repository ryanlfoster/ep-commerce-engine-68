package com.elasticpath.service.changeset.helper.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.ElasticPath;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.changeset.ChangeSetMember;
import com.elasticpath.domain.changeset.ChangeSetMemberMutator;
import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;
import com.elasticpath.domain.objectgroup.BusinessObjectGroupMember;
import com.elasticpath.domain.objectgroup.BusinessObjectMetadata;
import com.elasticpath.service.changeset.helper.ChangeSetHelper;

/**
 * A helper class for managing change set member and group members.
 */
public class ChangeSetHelperImpl implements ChangeSetHelper {

	private static final Logger LOG = Logger.getLogger(ChangeSetHelperImpl.class);

	private ElasticPath elasticpath;

	/**
	 * Set Elastic Path.
	 *
	 * @param elasticpath the instance of elastic path
	 */
	public void setElasticPath(final ElasticPath elasticpath) {
		this.elasticpath = elasticpath;
	}

	@Override
	public List<ChangeSetMember> convertGroupMembersToChangeSetMembers(final Collection<BusinessObjectGroupMember> businessObjectGroupMembers,
																			final Collection<BusinessObjectMetadata> memberObjectsMetadata) {

		final Map<BusinessObjectGroupMember, Map<String, String>> memberMap = createMetadataMap(memberObjectsMetadata);

		final List<ChangeSetMember> changeSetMembers = new ArrayList<ChangeSetMember>();

		for (BusinessObjectGroupMember groupMember : businessObjectGroupMembers) {
			BusinessObjectDescriptor businessObjectDescriptor = convertGroupMemberToDescriptor(groupMember);

			ChangeSetMember changeSetMember = createChangeSetMember(memberMap.get(groupMember), businessObjectDescriptor);

			changeSetMembers.add(changeSetMember);

		}

		return changeSetMembers;
	}

	/**
	 * Converts a simple group member to a descriptor.
	 *
	 * @param member a member
	 * @return an object descriptor
	 */
	public BusinessObjectDescriptor convertGroupMemberToDescriptor(final BusinessObjectGroupMember member) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Convert a group member to descriptor: " + member);
		}

		final BusinessObjectDescriptor desc = elasticpath.getBean(ContextIdNames.BUSINESS_OBJECT_DESCRIPTOR);

		desc.setObjectIdentifier(member.getObjectIdentifier());
		desc.setObjectType(member.getObjectType());

		return desc;
	}

	/**
	 * Create a instance of change set member from business object descriptor and its meta data map.
	 * @param metadataMap the meta data map of business object
	 * @param businessObjectDescriptor the business object descriptor
	 * @return the instance of change set member
	 */
	protected ChangeSetMember createChangeSetMember(final Map<String, String> metadataMap, final BusinessObjectDescriptor businessObjectDescriptor) {
		ChangeSetMember changeSetMember = elasticpath.getBean(ContextIdNames.CHANGESET_MEMBER);
		if (changeSetMember instanceof ChangeSetMemberMutator) {
			ChangeSetMemberMutator mutator = (ChangeSetMemberMutator) changeSetMember;
			mutator.setBusinessObjectDescriptor(businessObjectDescriptor);
			mutator.setMetadata(metadataMap);
		} else {
			throw new EpDomainException("ChangeSetMember bean does not implement ChangeSetMemberMutator.");
		}
		return changeSetMember;
	}

	/**
	 * Convert the object meta data got from database to a meta data map.
	 * @param objectsMetadata a collection of object meta data
	 * @return map of business object meta data
	 * 		   which the key object is the business object group member
	 * 		   and the value object is the meta data map
	 */
	protected Map<BusinessObjectGroupMember, Map<String, String>> createMetadataMap(final Collection<BusinessObjectMetadata> objectsMetadata) {
		final Map<BusinessObjectGroupMember, Map<String, String>> memberMap = new HashMap<BusinessObjectGroupMember, Map<String, String>>();

		for (BusinessObjectMetadata metadata : objectsMetadata) {
			if (memberMap.containsKey(metadata.getBusinessObjectGroupMember())) {
				memberMap.get(metadata.getBusinessObjectGroupMember()).put(metadata.getMetadataKey(), metadata.getMetadataValue());
			} else {
				HashMap<String, String> metadataMap = new HashMap<String, String>();
				metadataMap.put(metadata.getMetadataKey(), metadata.getMetadataValue());

				memberMap.put(metadata.getBusinessObjectGroupMember(), metadataMap);
			}
		}
		return memberMap;
	}
}
