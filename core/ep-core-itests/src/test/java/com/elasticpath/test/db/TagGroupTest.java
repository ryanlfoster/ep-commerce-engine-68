package com.elasticpath.test.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.tags.domain.TagDefinition;
import com.elasticpath.tags.domain.TagGroup;
import com.elasticpath.tags.service.TagDefinitionService;
import com.elasticpath.tags.service.TagGroupService;
import com.elasticpath.test.integration.DirtiesDatabase;

/**
 * Integration test suite for tag group entity and service. 
 */
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class TagGroupTest extends DbTestCase {

	private static final String GEO_LOCATION_LOCALIZED_GROUP_NAME = "Geo Location";

	private static final String CUSTOMER_PROFILE_GROUP = "CUSTOMER_PROFILE";

	private static final String UPDATED_GROUP_GUID = "UPDATED_GUID";

	private static final String NEW_GROUP_GUID = "NEW_GROUP";

	private static final String GEO_LOCATION_GROUP = "GEO_LOCATION";
	
	@Autowired
	private TagDefinitionService tagDefinitionService;

	@Autowired
	private TagGroupService tagGroupService;

	/**
	 * Test for finding all groups.
	 */
	@DirtiesDatabase
	@Test
	public void testFindAllGroups() {
		getTxTemplate().execute(new TransactionCallback<Object>() {
			public Object doInTransaction(final TransactionStatus arg0) {
				List<TagGroup> tagGroups = tagGroupService.getTagGroups();
				assertNotNull(tagGroups);
				assertFalse(tagGroups.isEmpty());
				return null;
			}
		});
	}

	/**
	 * Test for finding group by it's guid.
	 */
	@DirtiesDatabase
	@Test
	public void testFindGroupByGuid() {
		getTxTemplate().execute(new TransactionCallback<Object>() {
			public Object doInTransaction(final TransactionStatus arg0) {
				TagGroup tagGroup = tagGroupService
						.findByGuid(GEO_LOCATION_GROUP);
				assertNotNull(tagGroup);
				assertNotNull(tagGroup.getTagDefinitions());
				assertFalse(tagGroup.getTagDefinitions().isEmpty());
				return null;
			}
		});
	}
	
	/**
	 * Test for creating new group.
	 */
	@DirtiesDatabase
	@Test
	public void testAddGroup() {
		final TagGroup tagGroup = getBeanFactory().getBean(ContextIdNames.TAG_GROUP);
		tagGroup.setGuid(NEW_GROUP_GUID);
		
		getTxTemplate().execute(new TransactionCallback<Object>() {
			public Object doInTransaction(final TransactionStatus arg0) {
				tagGroupService.saveOrUpdate(tagGroup);
				return tagGroup;
			}
		});		
		
		assertTrue(tagGroup.isPersisted());
	}
	
	/**
	 * Test for updating group properties.
	 */
	@DirtiesDatabase
	@Test
	public void testUpdateGroup() {
		final TagGroup tagGroup = tagGroupService.findByGuid(GEO_LOCATION_GROUP);
		tagGroup.setGuid(UPDATED_GROUP_GUID);
		TagGroup updatedGroup = (TagGroup) getTxTemplate().execute(new TransactionCallback<Object>() {
			public Object doInTransaction(final TransactionStatus arg0) {
				tagGroupService.saveOrUpdate(tagGroup);
				return tagGroupService.findByGuid(UPDATED_GROUP_GUID);
			}
		});		
		
		assertNotNull("No group found by updated guid", updatedGroup);
		assertEquals("Group has wrong guid", UPDATED_GROUP_GUID, updatedGroup.getGuid());
	}
	
	/**
	 * Test for group deletion. If the group has assigned tag definitions an exception should be thrown.
	 */
	@DirtiesDatabase
	@Test
	public void testDeleteGroup() {
		final TagGroup tagGroupGeoLocation = tagGroupService.findByGuid(GEO_LOCATION_GROUP);
		final TagGroup tagGroupProfile = tagGroupService.findByGuid(CUSTOMER_PROFILE_GROUP);
		List<TagDefinition> tagDefsList = tagDefinitionService.getTagDefinitionsByTagGroup(tagGroupGeoLocation);
		assertFalse("Didn't find any tag definitions that should be assigned to the group", 
				tagDefsList.isEmpty());
		
		try {
			getTxTemplate().execute(new TransactionCallback<Object>() {
				public Object doInTransaction(final TransactionStatus arg0) {
						tagGroupService.delete(tagGroupGeoLocation);
					return null;
				}
			});
			fail("Should have gotten JpaSystemException - " 
					+ "trying to delete the group that has assigned tag definitions");			
		} catch (JpaSystemException e) {
			// Expected : intentional
		}

		Set<TagDefinition> tagDefs = tagGroupGeoLocation.getTagDefinitions();	

		for (TagDefinition tagDefinition : tagDefs) {
			tagGroupProfile.addTagDefinition(tagDefinition);
		}		
		tagGroupGeoLocation.getTagDefinitions().clear();

		TagGroup deletedGroup = (TagGroup) getTxTemplate().execute(new TransactionCallback<Object>() {
			public Object doInTransaction(final TransactionStatus arg0) {
				tagGroupService.saveOrUpdate(tagGroupProfile);
				tagGroupService.delete(tagGroupGeoLocation);
				return tagGroupService.findByGuid(GEO_LOCATION_GROUP);
			}
		});
		
		assertNull("Group was not deleted", deletedGroup);
		
		List<TagDefinition> emptyTagDefsList = 
			tagDefinitionService.getTagDefinitionsByTagGroup(tagGroupGeoLocation);
		
		assertTrue("Found tag definitions with unexistent group", emptyTagDefsList.isEmpty());
	}

	/**
	 * Test for getting localized group name for UI usage. If locale is missing or null - the guid value should be returned as the display name.
	 * If the localized value for the locale exists - it should be returned.
	 */
	@DirtiesDatabase
	@Test
	public void testLocalizedGroupName() {
		final TagGroup tagGroup = tagGroupService.findByGuid(GEO_LOCATION_GROUP);
		
		String localizedGroupName = tagGroup.getLocalizedGroupName(Locale.ENGLISH);
		String missingLanguageName = tagGroup.getLocalizedGroupName(Locale.FRANCE);
		String nullLocaleName = tagGroup.getLocalizedGroupName(null);
		
		assertEquals("Incorrect localized group name", GEO_LOCATION_LOCALIZED_GROUP_NAME, localizedGroupName);
		assertEquals("Incorrect localized group name", GEO_LOCATION_GROUP, missingLanguageName);
		assertEquals("Incorrect localized group name", GEO_LOCATION_GROUP, nullLocaleName);
		
	}

}
