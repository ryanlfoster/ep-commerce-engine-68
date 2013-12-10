package com.elasticpath.service.changeset.helper;

import java.util.Collection;
import java.util.List;

import com.elasticpath.domain.changeset.ChangeSetMember;
import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;
import com.elasticpath.domain.objectgroup.BusinessObjectGroupMember;
import com.elasticpath.domain.objectgroup.BusinessObjectMetadata;

/**
 * Change set helper.
 */
public interface ChangeSetHelper {

	/**
	 * Converts a collection of group member and their object meta data to a collection of {@link ChangeSetMember}s.
	 *
	 * @param businessObjectGroupMembers the group members
	 * @param memberObjectsMetadata the meta data of the group member
	 * @return a collection of {@link ChangeSetMember}s
	 */
	List<ChangeSetMember> convertGroupMembersToChangeSetMembers(Collection<BusinessObjectGroupMember> businessObjectGroupMembers,
			Collection<BusinessObjectMetadata> memberObjectsMetadata);

	/**
	 * Converts a simple group member to a descriptor.
	 *
	 * @param member a member
	 * @return an object descriptor
	 */
	BusinessObjectDescriptor convertGroupMemberToDescriptor(BusinessObjectGroupMember member);
}