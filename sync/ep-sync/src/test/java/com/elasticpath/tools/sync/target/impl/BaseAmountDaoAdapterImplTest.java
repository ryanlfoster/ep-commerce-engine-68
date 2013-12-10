package com.elasticpath.tools.sync.target.impl;

import static org.junit.Assert.assertEquals;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.pricing.BaseAmount;
import com.elasticpath.domain.pricing.impl.BaseAmountImpl;
import com.elasticpath.service.pricing.BaseAmountService;
import com.elasticpath.service.pricing.dao.BaseAmountDao;

/**
 * Test cases for {@link BaseAmountDaoAdapterImpl}.
 */
public class BaseAmountDaoAdapterImplTest {
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	
	private BaseAmountDaoAdapterImpl baseAmountDaoAdapter;
	private BaseAmountService baseAmountService;
	private BaseAmountDao baseAmountDao;
	
	/**
	 * Sets up a test case.
	 */
	@Before
	public void setUp() {
		baseAmountService = context.mock(BaseAmountService.class);
		baseAmountDao = context.mock(BaseAmountDao.class);
		
		baseAmountDaoAdapter = new BaseAmountDaoAdapterImpl();
		baseAmountDaoAdapter.setBaseAmountService(baseAmountService);
		baseAmountDaoAdapter.setBaseAmountDao(baseAmountDao);
	}
	
	/**
	 * Test {@link BaseAmountDaoAdapterImpl#update(BaseAmount)}.
	 * 
	 * Ensure that BaseAmountDao rather than {@link BaseAmountService} is called to prevent disruptive price updated notifications.
	 * Price update notifications are handled in SyncJobObjectProcessor#finish().
	 */
	@Test
	public void testUpdate() {
		final BaseAmount baseAmount = new BaseAmountImpl();
		final BaseAmount updatedBaseAmount = new BaseAmountImpl();
		
		context.checking(new Expectations() { {
			oneOf(baseAmountDao).update(baseAmount); will(returnValue(updatedBaseAmount));
		} });
		
		assertEquals(baseAmountDaoAdapter.update(baseAmount), updatedBaseAmount);
	}
}
