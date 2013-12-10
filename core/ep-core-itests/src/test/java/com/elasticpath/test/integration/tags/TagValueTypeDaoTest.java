package com.elasticpath.test.integration.tags;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.tags.dao.TagValueTypeDao;
import com.elasticpath.tags.domain.TagAllowedValue;
import com.elasticpath.tags.domain.TagValueType;
import com.elasticpath.tags.domain.impl.TagAllowedValueImpl;
import com.elasticpath.test.integration.BasicSpringContextTest;
import com.elasticpath.test.integration.DirtiesDatabase;

/**
 * Tag Value Type DAO Integration tests.
 */
public class TagValueTypeDaoTest extends BasicSpringContextTest {
	
	@Autowired
	private TagValueTypeDao tagValueTypeDao;
	
    /**
     * Tests simple Tag Definition creation.
     * 
     * @throws Exception a data access exception
     */
	@DirtiesDatabase
    @Test
    public void testCreate() throws Exception {
    	TagValueType tagValueType = createTagValueType();
    	assertNotNull(tagValueTypeDao.saveOrUpdate(tagValueType));
    }
    
    /** */
	@DirtiesDatabase
    @Test
    public void testFindByGuid() {
    	TagValueType tagValueType = createTagValueType();
    	TagValueType savedTagValueType = tagValueTypeDao.saveOrUpdate(tagValueType);
    	TagValueType foundTagValueType = tagValueTypeDao.findByGuid(tagValueType.getGuid());
		assertEquals(savedTagValueType, foundTagValueType);
    }
    
    /**
     * Tests simple Tag Definition creation.
     * 
     * @throws Exception a data access exception
     */
	@DirtiesDatabase
    @Test
    public void testCreateWithAllowedValues() throws Exception {
    	TagValueType tagValueType = createTagValueType();
    	tagValueType.addAllowedValue(new TagAllowedValueImpl("GOLD", Integer.parseInt("4")));
    	tagValueType.addAllowedValue(new TagAllowedValueImpl("PREMIUM", Integer.parseInt("5")));
    	tagValueType.addAllowedValue(new TagAllowedValueImpl("PLATINUM", Integer.parseInt("6")));

    	TagValueType savedTagValueType = tagValueTypeDao.saveOrUpdate(tagValueType);
		assertNotNull(savedTagValueType);
    	assertEquals(Integer.parseInt("3"), savedTagValueType.getAllowedValues().size());
    }
    
    /** */
	@DirtiesDatabase
    @Test
    public void testDeleteAllowedValues() throws Exception {
    	TagValueType tagValueType = createTagValueType();
    	TagAllowedValue tagAllowedValueToDelete = new TagAllowedValueImpl("PREMIUM", Integer.parseInt("5"));
    	tagValueType.addAllowedValue(new TagAllowedValueImpl("GOLD", Integer.parseInt("4")));
    	tagValueType.addAllowedValue(tagAllowedValueToDelete);
    	tagValueType.addAllowedValue(new TagAllowedValueImpl("PLATINUM", Integer.parseInt("6")));

    	TagValueType savedTagValueType = tagValueTypeDao.saveOrUpdate(tagValueType);
    	// this removal logic for allowed values should go in the DAO
    	savedTagValueType.getAllowedValues().clear();    	
    	TagValueType updatedTagValueType = tagValueTypeDao.saveOrUpdate(tagValueType);
    	
    	assertEquals(0, updatedTagValueType.getAllowedValues().size());    	
    }
    
    /** */
	@DirtiesDatabase
    @Test
    public void testDelete() throws Exception {
    	TagValueType tagValueType = createTagValueType();
    	TagValueType savedTagValueType = tagValueTypeDao.saveOrUpdate(tagValueType);
    	tagValueTypeDao.remove(savedTagValueType);
    	
    	assertNull(tagValueTypeDao.findByGuid(savedTagValueType.getGuid())); 
    }       
    
    private TagValueType createTagValueType() {
		return getBeanFactory().getBean(ContextIdNames.TAG_VALUE_TYPE);
    }
}
