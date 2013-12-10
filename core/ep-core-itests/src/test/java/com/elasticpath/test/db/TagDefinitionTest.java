package com.elasticpath.test.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.tags.domain.TagDefinition;
import com.elasticpath.tags.domain.TagGroup;
import com.elasticpath.tags.domain.TagValueType;
import com.elasticpath.tags.service.TagDefinitionService;
import com.elasticpath.tags.service.TagGroupService;
import com.elasticpath.tags.service.TagValueTypeService;
import com.elasticpath.test.integration.DirtiesDatabase;

/**
 * Test for tag definition service DB API.
 */
public class TagDefinitionTest extends DbTestCase {

	private static final String GEO_LOCATION_GROUP = "GEO_LOCATION";

	private static final String CART_SUBTOTAL_DESCR = "Cart subtotal value after cart modification.";

	private static final String CART_SUBTOTAL = "CART_SUBTOTAL";

	private static final String NEW_DESC = "NEW_DESC";

	private static final String NAME = "NAME-1234";

	private static final String GUID = "GUID-2222-1238";
	
	@Autowired
	private TagDefinitionService tagDefinitionService;
	
	@Autowired
	private TagGroupService tagGroupService;
	
	@Autowired
	private TagValueTypeService tagValueTypeService;
	
	/**
	 * Test add operation.
	 */
	@DirtiesDatabase
	@Test
	public void testAdd() {
		final TagDefinition tagDefinition = getBeanFactory().getBean(ContextIdNames.TAG_DEFINITION);
		TagValueType tagValueType =  tagValueTypeService.findByGuid("text");
		
		tagDefinition.setValueType(tagValueType);		
		tagDefinition.setGuid(GUID);
		tagDefinition.setName(NAME);

		final TagGroup tagGroup = tagGroupService.findByGuid(GEO_LOCATION_GROUP);
		
		tagGroup.addTagDefinition(tagDefinition);
		
		getTxTemplate().execute(new TransactionCallback<TagDefinition>() {
			public TagDefinition doInTransaction(final TransactionStatus arg0) {
				assertNotNull(tagDefinition);
				tagDefinitionService.saveOrUpdate(tagDefinition);
				tagGroupService.saveOrUpdate(tagGroup);
				return tagDefinition;
			}
		});	

	}
	
	/**
	 * Test update operation.
	 */
	@DirtiesDatabase
	@Test
	public void testUpdate() {
		final TagDefinition tagDefinition = tagDefinitionService.findByName(CART_SUBTOTAL);
		final TagGroup tagGroup = tagGroupService.findByGuid(GEO_LOCATION_GROUP);
		
		assertNotNull("The tag definition is null", tagDefinition);
		assertNotNull("The tag group is null", tagDefinition.getGroup());
		
		tagDefinition.setDescription(NEW_DESC);
		tagGroup.addTagDefinition(tagDefinition);
		
		final TagDefinition updatedTagDefinition = getTxTemplate().execute(new TransactionCallback<TagDefinition>() {
			public TagDefinition doInTransaction(final TransactionStatus arg0) {
				tagDefinitionService.saveOrUpdate(tagDefinition);
				return tagDefinitionService.findByName(CART_SUBTOTAL);
			}
		});		


		assertEquals("Description was not updated", NEW_DESC, updatedTagDefinition.getDescription());		
		assertEquals("Group was not updated", tagGroup, updatedTagDefinition.getGroup());
	}
	
	/**
	 * Test delete operation.
	 */
	@DirtiesDatabase
	@Test
	public void testDelete() {
		final TagDefinition tagDefinition = tagDefinitionService.findByName(CART_SUBTOTAL);
		final String tagGroupGuid = tagDefinition.getGroup().getGuid();
		TagDefinition deletedTagDefinition = getTxTemplate().execute(new TransactionCallback<TagDefinition>() {
			public TagDefinition doInTransaction(final TransactionStatus arg0) {
				tagDefinitionService.delete(tagDefinition);
				return tagDefinitionService.findByName(CART_SUBTOTAL);
			}
		});
		
		assertNull("Tag definition was not deleted", deletedTagDefinition);
		
		final TagGroup tagGroup = tagGroupService.findByGuid(tagGroupGuid);
		
		assertNotNull("Tag group was deleted", tagGroup);

	}
	
	
	/**
	 * Test finding all tag definitions.
	 */
	@DirtiesDatabase
	@Test
	public void testFindAll() {
		getTxTemplate().execute(new TransactionCallback<TagDefinition>() {
			public TagDefinition doInTransaction(final TransactionStatus arg0) {
				List<TagDefinition> tDefs = tagDefinitionService.getTagDefinitions();
				assertNotNull(tDefs);
				assertFalse(tDefs.isEmpty());
				return null;
			}
		});
	}
	
	/**
	 * Test finding all tag definitions.
	 */
	@DirtiesDatabase
	@Test
	public void testGetTagDefinitionsByGroup() {
		final TagGroup tagGroup = tagGroupService.findByGuid(GEO_LOCATION_GROUP);
		
		getTxTemplate().execute(new TransactionCallback<TagDefinition>() {
			public TagDefinition doInTransaction(final TransactionStatus arg0) {
				List<TagDefinition> tDefs = tagDefinitionService.getTagDefinitionsByTagGroup(tagGroup);
				assertNotNull(tDefs);
				assertFalse(tDefs.isEmpty());
				return null;
			}
		});
	}
	
	/**
	 * Test find tag definition by name.
	 */
	@DirtiesDatabase
	@Test
	public void testFindByName() {
		getTxTemplate().execute(new TransactionCallback<TagDefinition>() {
			public TagDefinition doInTransaction(final TransactionStatus arg0) {
				TagDefinition tDef = tagDefinitionService.findByName(CART_SUBTOTAL);
				assertNotNull(tDef);
				assertEquals(CART_SUBTOTAL_DESCR, tDef.getDescription());
				assertNotNull(tDef.getGroup());
				return null;
			}
		});
	}
	
	
	/**
	 * Test find tag definition by guid.
	 */
	@DirtiesDatabase
	@Test
	public void testFindByGuid() {
		getTxTemplate().execute(new TransactionCallback<TagDefinition>() {
			public TagDefinition doInTransaction(final TransactionStatus arg0) {
				TagDefinition tDef = tagDefinitionService.findByGuid(CART_SUBTOTAL);
				assertNotNull(tDef);
				assertEquals(CART_SUBTOTAL_DESCR, tDef.getDescription());
				return null;
			}
		});
	}
	
}
