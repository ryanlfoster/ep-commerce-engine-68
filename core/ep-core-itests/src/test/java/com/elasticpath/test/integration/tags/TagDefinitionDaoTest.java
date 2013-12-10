package com.elasticpath.test.integration.tags;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.tags.dao.TagDefinitionDao;
import com.elasticpath.tags.domain.TagDefinition;
import com.elasticpath.tags.domain.TagValueType;
import com.elasticpath.tags.service.TagGroupService;
import com.elasticpath.tags.service.TagValueTypeService;
import com.elasticpath.test.integration.BasicSpringContextTest;
import com.elasticpath.test.integration.DirtiesDatabase;

/**
 * Tag Definition DAO Integration tests.
 */
public class TagDefinitionDaoTest extends BasicSpringContextTest {
	@Autowired
    private TagDefinitionDao tagDefinitionDao;

	@Autowired
	private TagValueTypeService tagValueTypeService;

	@Autowired
	private TagGroupService tagGroupService;

    /**
     * Tests simple Tag Definition creation.
     * 
     * @throws Exception a data access exception
     */
	@DirtiesDatabase
    @Test
    public void testCreate() throws Exception {
        TagDefinition tagDefinition = createTagDefinition();
		assertNotNull(tagDefinition);
    }
    
    /**
     * Tests find Tag Definition by GUID.
     * 
     * @throws Exception a data access exception
     */
	@DirtiesDatabase
    @Test
    public void testFind() throws Exception {
    	TagDefinition tagDefinition = createTagDefinition();
    	tagDefinition = tagDefinitionDao.saveOrUpdate(tagDefinition);
    	assertEquals(tagDefinition, tagDefinitionDao.findByGuid(tagDefinition.getGuid()));
    }

    /**
     * Tests deletion of a Tag Definition.
     * 
     * @throws Exception a data access exception
     */
	@DirtiesDatabase
    @Test
    public void testDelete() throws Exception {  	    	
    	TagDefinition tagDefinitionToDelete = tagDefinitionDao.findByGuid("CUSTOMER_GENDER");
    	assertNotNull(tagDefinitionToDelete);
    	tagDefinitionDao.remove(tagDefinitionToDelete);
    	assertNull(tagDefinitionDao.findByGuid("CUSTOMER_GENDER"));
    }

    /**
     * Tests getting list of a Tag Definitions.
     * 
     * @throws Exception a data access exception
     */
	@DirtiesDatabase
    @Test
    public void testGetList() throws Exception {
    	int existingSize = tagDefinitionDao.getTagDefinitions().size();
    	
    	TagDefinition tagDefinition1 = createTagDefinition();
    	tagDefinition1 = tagDefinitionDao.saveOrUpdate(tagDefinition1);
    	assertEquals(tagDefinition1, tagDefinitionDao.findByGuid(tagDefinition1.getGuid()));
    	
    	TagDefinition tagDefinition2 = createTagDefinition();
    	tagDefinition2 = tagDefinitionDao.saveOrUpdate(tagDefinition2);
    	assertEquals(tagDefinition2, tagDefinitionDao.findByGuid(tagDefinition2.getGuid()));
    	
    	assertEquals(existingSize + 2, tagDefinitionDao.getTagDefinitions().size());
    }

	private TagDefinition createTagDefinition() {
		TagDefinition tagDefinition = getBeanFactory().getBean(ContextIdNames.TAG_DEFINITION);
		TagValueType tagValueType = tagValueTypeService.findByGuid("text");
        tagDefinition.setName("Demo Tag Definition");
        tagDefinition.setDescription("Description of demo tag definition");        
        tagDefinition.setGroup(tagGroupService.findByGuid("CUSTOMER_PROFILE"));     
        tagDefinition.setValueType(tagValueType);
		return tagDefinition;
	}
}