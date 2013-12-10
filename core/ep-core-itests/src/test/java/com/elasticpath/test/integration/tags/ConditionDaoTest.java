package com.elasticpath.test.integration.tags;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.tags.dao.ConditionalExpressionDao;
import com.elasticpath.tags.domain.ConditionalExpression;
import com.elasticpath.test.integration.BasicSpringContextTest;
import com.elasticpath.test.integration.DirtiesDatabase;

/**
 * Condition DAO integration tests.
 */
public class ConditionDaoTest extends BasicSpringContextTest {

	@Autowired
	private ConditionalExpressionDao conditionDao;

    /**
     * Tests simple Condition creation.
     * 
     * @throws Exception a data access exception
     */
    @DirtiesDatabase
    @Test
    public void testCreate() throws Exception {
        ConditionalExpression condition = createCondition();

        assertNotNull(conditionDao.saveOrUpdate(condition));
    }
    
    /**
     * Tests find Condition by GUID.
     * 
     * @throws Exception a data access exception
     */
    @DirtiesDatabase
    @Test
    public void testFind() throws Exception {
    	ConditionalExpression condition = createCondition();
    	condition = conditionDao.saveOrUpdate(condition);
    	
    	assertEquals(condition, conditionDao.findByGuid(condition.getGuid()));
    }

    /**
     * Tests deletion of a Condition.
     * 
     * @throws Exception a data access exception
     */
    @DirtiesDatabase
    @Test
    public void testDelete() throws Exception {
    	// make sure it's in db
    	ConditionalExpression condition = createCondition();
    	condition = conditionDao.saveOrUpdate(condition);
    	assertEquals(condition, conditionDao.findByGuid(condition.getGuid()));
    	
    	// delete and test if it is still in db
    	conditionDao.remove(condition);
    	assertNull(conditionDao.findByGuid(condition.getGuid()));
    }

    /**
     * Tests getting list of a Conditions.
     * 
     * @throws Exception a data access exception
     */
    @DirtiesDatabase
    @Test
    public void testGetList() throws Exception {
    	// make sure it's empty first
    	assertEquals(0, conditionDao.getConditions().size());
    	
    	ConditionalExpression condition1 = createCondition();
    	condition1 = conditionDao.saveOrUpdate(condition1);
    	assertEquals(condition1, conditionDao.findByGuid(condition1.getGuid()));
    	
    	ConditionalExpression condition2 = createCondition();
    	condition2 = conditionDao.saveOrUpdate(condition2);
    	assertEquals(condition2, conditionDao.findByGuid(condition2.getGuid()));
    	
    	assertEquals(2, conditionDao.getConditions().size());
    }
    
    /**
     * Tests getting list of a Named Conditions.
     * 
     * @throws Exception a data access exception
     */
    @DirtiesDatabase
    @Test
    public void testGetNamedList() throws Exception {
    	// make sure it's empty first
    	assertEquals(0, conditionDao.getConditions().size());
    	
    	ConditionalExpression condition1 = createCondition();
    	condition1.setNamed(true);
    	condition1 = conditionDao.saveOrUpdate(condition1);
    	assertEquals(condition1, conditionDao.findByGuid(condition1.getGuid()));
    	
    	ConditionalExpression condition2 = createCondition();
    	condition2.setNamed(false);
    	condition2 = conditionDao.saveOrUpdate(condition2);
    	assertEquals(condition2, conditionDao.findByGuid(condition2.getGuid()));
    	
    	assertEquals(1, conditionDao.getNamedConditions().size());
    }
    
    /**
     * Get all (named or not) conditions by tag dictionary.
     * @throws Exception a data access exception
     */
    @DirtiesDatabase
    @Test
    public void testGetAllByTagDictionary() throws Exception {
    	
    	ConditionalExpression condition1 = createCondition();
    	condition1.setNamed(true);
    	condition1.setTagDictionaryGuid("SHOPPER");
    	condition1 = conditionDao.saveOrUpdate(condition1);
    	assertEquals(condition1, conditionDao.findByGuid(condition1.getGuid()));
    	
    	ConditionalExpression condition2 = createCondition();
    	condition2.setNamed(false);
    	condition2.setTagDictionaryGuid("STORES");
    	condition2 = conditionDao.saveOrUpdate(condition2);
    	assertEquals(condition2, conditionDao.findByGuid(condition2.getGuid()));
    	
    	assertEquals(1, conditionDao.getConditions("SHOPPER").size());
    	
    	assertEquals(1, conditionDao.getConditions("STORES").size());
    	
    	assertTrue(conditionDao.getConditions("WHEN").isEmpty());
    	
    	
    }
    
    /**
     * Test find by condition name , tag dictionary, condition expression method.
     * @throws Exception a data access exception
     */
    @DirtiesDatabase
    @Test
    public void testFindByNameDictTag() throws Exception {
    	
    	ConditionalExpression condition1 = createCondition();
    	condition1.setName("name1");
    	condition1.setNamed(true);
    	condition1.setTagDictionaryGuid("SHOPPER");
    	condition1.setConditionString("AND {  DAYS_SINSE_LAST_VISIT.lessThan 3  }");
    	condition1 = conditionDao.saveOrUpdate(condition1);
    	assertEquals(condition1, conditionDao.findByGuid(condition1.getGuid()));
    	
    	ConditionalExpression condition2 = createCondition();
    	condition2.setName("name2");
    	condition2.setNamed(true);
    	condition2.setTagDictionaryGuid("STORES");
    	condition2.setConditionString("AND {  SELLING_CHANELL.equalsTo 'someStoreCode'  }");
    	condition2 = conditionDao.saveOrUpdate(condition2);
    	assertEquals(condition2, conditionDao.findByGuid(condition2.getGuid()));
    	
    	ConditionalExpression condition3 = createCondition();
    	condition3.setNamed(false);
    	condition3.setTagDictionaryGuid("STORES");
    	condition3 = conditionDao.saveOrUpdate(condition3);
    	assertEquals(condition3, conditionDao.findByGuid(condition3.getGuid()));
    	
    	assertEquals(2, conditionDao.getNamedConditionsByNameTagDictionaryConditionTag(null, null, null).size());
    	assertEquals(1, conditionDao.getNamedConditionsByNameTagDictionaryConditionTag(null, "SHOPPER", null).size());    	
    	assertEquals(1, conditionDao.getNamedConditionsByNameTagDictionaryConditionTag(null, "STORES", "SELLING_CHANELL").size());
    	assertEquals(1, conditionDao.getNamedConditionsByNameTagDictionaryConditionTag(null, "SHOPPER", "DAYS_SINSE_LAST_VISIT").size());
    	assertTrue(conditionDao.getNamedConditionsByNameTagDictionaryConditionTag(null, "SHOPPER", "SOME_TAG").isEmpty());
    	
    	assertEquals(1, conditionDao.getNamedConditionsByNameTagDictionaryConditionTag("name1", "SHOPPER", null).size());
    	assertEquals(1, conditionDao.getNamedConditionsByNameTagDictionaryConditionTag("name2", "STORES", null).size());
    	assertTrue(conditionDao.getNamedConditionsByNameTagDictionaryConditionTag("name1", "STORES", null).isEmpty());
    	assertTrue(conditionDao.getNamedConditionsByNameTagDictionaryConditionTag("name3", null, null).isEmpty());
    	
    	
    }
    
    /**
     * Test find by condition name , tag dictionary, condition expression and delivery name method.
     * @throws Exception a data access exception
     */
    @DirtiesDatabase
    @Test
    public void testFindByNameDictTagDelivery() throws Exception {
    	
    	ConditionalExpression condition1 = createCondition();
    	condition1.setName("name1");
    	condition1.setNamed(true);
    	condition1.setTagDictionaryGuid("SHOPPER");
    	condition1.setConditionString("AND {  DAYS_SINSE_LAST_VISIT2.lessThan 3  }");
    	condition1 = conditionDao.saveOrUpdate(condition1);
    	assertEquals(condition1, conditionDao.findByGuid(condition1.getGuid()));
    	
    	ConditionalExpression condition2 = createCondition();
    	condition2.setName("name2");
    	condition2.setNamed(true);
    	condition2.setTagDictionaryGuid("STORES");
    	condition2.setConditionString("AND {  SELLING_CHANELL.equalsTo 'someStoreCode'  }");
    	condition2 = conditionDao.saveOrUpdate(condition2);
    	assertEquals(condition2, conditionDao.findByGuid(condition2.getGuid()));
    	
    	ConditionalExpression condition3 = createCondition();
    	condition3.setNamed(false);
    	condition3.setTagDictionaryGuid("STORES");
    	condition3 = conditionDao.saveOrUpdate(condition3);
    	assertEquals(condition3, conditionDao.findByGuid(condition3.getGuid()));
    	
    	assertEquals(2, conditionDao.getNamedConditionsByNameTagDictionaryConditionTagSellingContext(null, null, null, null).size());
    	assertTrue(conditionDao.getNamedConditionsByNameTagDictionaryConditionTagSellingContext(
    			null, null, null, "someSellingContextGuid").isEmpty());
    	assertEquals(1, conditionDao.getNamedConditionsByNameTagDictionaryConditionTagSellingContext(null, "SHOPPER", null, null).size());    	
    	assertEquals(1, conditionDao.getNamedConditionsByNameTagDictionaryConditionTagSellingContext(
    			null, "STORES", "SELLING_CHANELL", null).size());
    	assertEquals(1, conditionDao.getNamedConditionsByNameTagDictionaryConditionTagSellingContext(
    			null, "SHOPPER", "DAYS_SINSE_LAST_VISIT2", null).size());
    	assertTrue(conditionDao.getNamedConditionsByNameTagDictionaryConditionTagSellingContext(null, "SHOPPER", "SOME_TAG", null).isEmpty());
    	
    	assertEquals(1, conditionDao.getNamedConditionsByNameTagDictionaryConditionTagSellingContext("name1", "SHOPPER", null, null).size());
    	assertEquals(1, conditionDao.getNamedConditionsByNameTagDictionaryConditionTagSellingContext("name2", "STORES", null, null).size());
    	assertTrue(conditionDao.getNamedConditionsByNameTagDictionaryConditionTagSellingContext("name1", "STORES", null, null).isEmpty());
    	assertTrue(conditionDao.getNamedConditionsByNameTagDictionaryConditionTagSellingContext("name3", null, null, null).isEmpty());
    }
    
	private ConditionalExpression createCondition() {
		ConditionalExpression condition = getBeanFactory().getBean(ContextIdNames.TAG_CONDITION);

        condition.setName("Demo Condition");
        condition.setDescription("Description of demo condition");
        condition.setConditionString("A > B");
        
		return condition;
	}
}