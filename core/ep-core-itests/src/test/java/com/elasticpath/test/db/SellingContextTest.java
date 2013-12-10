package com.elasticpath.test.db;
/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.sellingcontext.SellingContext;
import com.elasticpath.service.sellingcontext.SellingContextService;
import com.elasticpath.tags.domain.ConditionalExpression;
import com.elasticpath.tags.domain.LogicalOperator;
import com.elasticpath.tags.domain.LogicalOperatorType;
import com.elasticpath.tags.service.ConditionDSLBuilder;
import com.elasticpath.tags.service.InvalidConditionTreeException;
import com.elasticpath.test.integration.DirtiesDatabase;

/**
 * Test named queries for selling context.
 */
public class SellingContextTest extends DbTestCase {
	
	@Autowired
	private SellingContextService sellingContextService;
	
	@Autowired
	private ConditionDSLBuilder conditionalBuilder;
	
	private int idx = 0;
	
	private int getIdx() {
		return ++idx;
	}
	
	/**
	 * Create conditional expression.
	 * @param tagDictionaryGuid the tag dictionary guid
	 * @return empty conditional expression. 
	 * @throws InvalidConditionTreeException 
	 */
	private ConditionalExpression  createConditionalExpression(final String tagDictionaryGuid) throws InvalidConditionTreeException {
		LogicalOperator logicalOperator = new LogicalOperator(LogicalOperatorType.AND);
		ConditionalExpression conditionalExpression = getBeanFactory().getBean(ContextIdNames.TAG_CONDITION);
		conditionalExpression.setTagDictionaryGuid(tagDictionaryGuid);
		conditionalExpression.setName("name_" + getIdx());
		conditionalExpression.setDescription("" + getIdx());
		conditionalExpression.setConditionString(conditionalBuilder.getConditionalDSLString(logicalOperator));
		return conditionalExpression;	
	}
	
	private SellingContext createSellingContext() throws InvalidConditionTreeException {
		
		SellingContext sellingContext = getBeanFactory().getBean(ContextIdNames.SELLING_CONTEXT);
		
		sellingContext.setPriority(1);
		sellingContext.setCondition("SHOPPER", createConditionalExpression("SHOPPER"));
		sellingContext.setCondition("STORES", createConditionalExpression("STORES"));
		sellingContext.setCondition("TIME" , createConditionalExpression("TIME"));
		sellingContext.setName("DCA SellingContext " + getIdx());
		sellingContext.setDescription(sellingContext.getName());
		
		return sellingContextService.saveOrUpdate(sellingContext);
		
	}
	
	/**
	 * Test FIND_ALL_SELLING_CONTEXTS selling context named query.
	 * @throws InvalidConditionTreeException if the condition tree has an invalid format or data
	 */
	@DirtiesDatabase
	@Test
	public void testFindAllSellingContext() throws InvalidConditionTreeException {		

		createSellingContext();
		createSellingContext();
		
		
		List<SellingContext> sellingContextList = sellingContextService.findAll();
		
		 
		assertNotNull(sellingContextList);
		assertEquals(2, sellingContextList.size());
	}
	
	/**
	 * test SELLING_CONTEXT_FIND_BY_GUID named query.
	 * @throws InvalidConditionTreeException if the condition tree has an invalid format or data
	 */
	@DirtiesDatabase
	@Test
	public void testFindByGuid() throws InvalidConditionTreeException {
		
		final SellingContext sellingContext = createSellingContext();
		final SellingContext sellingContext2 = createSellingContext();
		
		
		SellingContext sellingContextByGuid = sellingContextService.getByGuid(sellingContext.getGuid());
		
		 
		assertNotNull(sellingContextByGuid);
		assertEquals(sellingContext, sellingContextByGuid);
		
		sellingContextByGuid = sellingContextService.getByGuid(sellingContext2.getGuid());

		assertNotNull(sellingContextByGuid);
		assertEquals(sellingContext2, sellingContextByGuid);
		
		
	}
	
	
	/**
	 * test SELLING_CONTEXT_FIND_BY_NAMED_CONDITION_GUID named query.
	 * @throws InvalidConditionTreeException if the condition tree has an invalid format or data
	 */
	@DirtiesDatabase
	@Test
	public void testFindByNamedConditionGuid() throws InvalidConditionTreeException {
		
		final SellingContext sellingContext = createSellingContext();
		
		
		List<SellingContext> sellingContextByGuid = sellingContextService.getByNamedConditionGuid(
				sellingContext.getCondition("SHOPPER").getGuid()
				);
		
		 
		assertNotNull(sellingContextByGuid);
		assertFalse(sellingContextByGuid.isEmpty());
		assertEquals(sellingContext, sellingContextByGuid.get(0));
		
		
		sellingContextService.getByNamedConditionGuid(
				sellingContext.getCondition("TIME").getGuid()
				);
		
		 
		assertNotNull(sellingContextByGuid);
		assertFalse(sellingContextByGuid.isEmpty());
		assertEquals(sellingContext, sellingContextByGuid.get(0));
		
		sellingContextService.getByNamedConditionGuid(
				sellingContext.getCondition("STORES").getGuid()
				);
		
		 
		assertNotNull(sellingContextByGuid);
		assertFalse(sellingContextByGuid.isEmpty());
		assertEquals(sellingContext, sellingContextByGuid.get(0));		
	}
}
