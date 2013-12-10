package com.elasticpath.test.integration.tags;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.tags.dao.TagDefinitionDao;
import com.elasticpath.tags.dao.TagDictionaryDao;
import com.elasticpath.tags.domain.TagDefinition;
import com.elasticpath.tags.domain.TagDictionary;
import com.elasticpath.tags.domain.TagValueType;
import com.elasticpath.tags.service.TagGroupService;
import com.elasticpath.tags.service.TagValueTypeService;
import com.elasticpath.test.integration.BasicSpringContextTest;
import com.elasticpath.test.integration.DirtiesDatabase;


/**
 * Tag Dictionary DAO integration tests.
 */
public class TagDictionaryDaoTest extends BasicSpringContextTest {
	@Autowired
    private TagDictionaryDao tagDictionaryDao;
	@Autowired
    private TagDefinitionDao tagDefinitionDao;    
	@Autowired
	private TagValueTypeService tagValueTypeService;
	@Autowired
	private TagGroupService tagGroupService;

    /**
     * Tests simple Tag Dictionary creation.
     * 
     * @throws Exception a data access exception
     */
    @DirtiesDatabase
    @Test
    public void testCreate() throws Exception {
        TagDictionary tagDictionary = createTagDictionary();

        assertNotNull(tagDictionaryDao.saveOrUpdate(tagDictionary));
    }
    
    /**
     * Tests Tag Dictionary creation with Tag Defintions.
     * 
     * @throws Exception a data access exception
     */
    @DirtiesDatabase
    @Test
    public void testCreateWithTagDefinitions() throws Exception {
    	TagDictionary tagDictionary = createTagDictionary();
    	
    	addTagDefinitions(tagDictionary);
    	
    	tagDictionary = tagDictionaryDao.saveOrUpdate(tagDictionary);
    	
    	assertEquals(Integer.parseInt("2"), tagDictionary.getTagDefinitions().size());
    }

    /**
     * Tests Remove Tag Dictionary with Tag Defintions.
     * 
     * @throws Exception a data access exception
     */
    @DirtiesDatabase
    @Test
    public void testRemoveTagDefinitions() throws Exception {
    	TagDictionary tagDictionaryToDelete = tagDictionaryDao.findByGuid("STORES");
    	assertNotNull(tagDictionaryToDelete);
    	tagDictionaryDao.remove(tagDictionaryToDelete);
    	assertNull(tagDictionaryDao.findByGuid("STORES"));
    }
    
    /**
     * Tests find Tag Dictionary by GUID.
     * 
     * @throws Exception a data access exception
     */
    @DirtiesDatabase
    @Test
    public void testFindByGuid() throws Exception {
    	TagDictionary tagDictionary = createTagDictionary();
    	tagDictionary = tagDictionaryDao.saveOrUpdate(tagDictionary);
    	
    	assertEquals(tagDictionary, tagDictionaryDao.findByGuid(tagDictionary.getGuid()));
    }

    /**
     * Tests deletion of a Tag Dictionary.
     * 
     * @throws Exception a data access exception
     */
    @DirtiesDatabase
    @Test
    public void testDelete() throws Exception {
    	// make sure it's in db
    	TagDictionary tagDictionary = createTagDictionary();
    	tagDictionary = tagDictionaryDao.saveOrUpdate(tagDictionary);
    	assertEquals(tagDictionary, tagDictionaryDao.findByGuid(tagDictionary.getGuid()));
    	
    	// delete and test if it is still in db
    	tagDictionaryDao.remove(tagDictionary);
    	assertNull(tagDictionaryDao.findByGuid(tagDictionary.getGuid()));
    }

    /**
     * Tests getting list of a Tag Dictionaries.
     * 
     * @throws Exception a data access exception
     */
    @DirtiesDatabase
    @Test
    public void testGetList() throws Exception {
    	
    	// make sure what we know a quantity records of TagDictionary.
    	int dictionarySize = tagDictionaryDao.getTagDictionaries().size();
    	
    	TagDictionary tagDictionary1 = createTagDictionary();
    	tagDictionary1 = tagDictionaryDao.saveOrUpdate(tagDictionary1);
    	assertEquals(tagDictionary1, tagDictionaryDao.findByGuid(tagDictionary1.getGuid()));
    	
    	TagDictionary tagDictionary2 = createTagDictionary();
    	tagDictionary2 = tagDictionaryDao.saveOrUpdate(tagDictionary2);
    	assertEquals(tagDictionary2, tagDictionaryDao.findByGuid(tagDictionary2.getGuid()));
    	
    	assertEquals(dictionarySize + 2, tagDictionaryDao.getTagDictionaries().size());
    }

	private TagDictionary createTagDictionary() {
		TagDictionary tagDictionary = getBeanFactory().getBean(ContextIdNames.TAG_DICTIONARY);

        tagDictionary.setName("Demo Dictionary");
        tagDictionary.setPurpose("Purpose of demo dictionary");
        
		return tagDictionary;
	}
	
	private TagDefinition createTagDefinition() {
		TagDefinition tagDefinition = getBeanFactory().getBean(ContextIdNames.TAG_DEFINITION);
		TagValueType tagValueType =  tagValueTypeService.findByGuid("text");

        tagDefinition.setName("Demo Tag Definition");
        tagDefinition.setDescription("Description of demo tag definition");
        tagDefinition.setGroup(tagGroupService.findByGuid("CUSTOMER_PROFILE"));
		tagDefinition.setValueType(tagValueType);
        
		return tagDefinition;
	}
	
	private void addTagDefinitions(final TagDictionary tagDictionary) {
		// create some tag definitions
    	TagDefinition tagDefinition1 = createTagDefinition();
    	tagDefinition1 = tagDefinitionDao.saveOrUpdate(tagDefinition1);

    	TagDefinition tagDefinition2 = createTagDefinition();
    	tagDefinition2 = tagDefinitionDao.saveOrUpdate(tagDefinition2);
    	
    	// add these tag definitions to tag dictionary;
    	tagDictionary.addTagDefinition(tagDefinition1);
    	tagDictionary.addTagDefinition(tagDefinition2);
	}
}