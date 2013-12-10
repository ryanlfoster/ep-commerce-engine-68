package com.elasticpath.service.catalog.impl;

import static org.junit.Assert.assertEquals;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.catalog.ItemCharacteristics;
import com.elasticpath.domain.catalog.ItemConfigurationMemento.ItemConfigurationId;
import com.elasticpath.service.catalog.ItemCharacteristicsService;

/**
 * Test that {@link CachingItemCharacteristicsServiceImpl} behaves as expected.
 */
public class CachingItemCharacteristicsServiceImplTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	
	private ItemCharacteristicsService itemCharacteristicsService;
	
	private ItemCharacteristicsService delegate;
	
	/**
	 * Setup required for each test.
	 *
	 * @throws Exception the exception
	 */
	@Before
	public void setUp() throws Exception {
		itemCharacteristicsService = new CachingItemCharacteristicsServiceImpl();
		delegate = context.mock(ItemCharacteristicsService.class);
		((CachingItemCharacteristicsServiceImpl) itemCharacteristicsService).setDelegate(delegate);
	}

	/**
	 * Test the behavior of get item characteristics.
	 */
	@Test
	public void testGetItemCharacteristics() {
		final ItemConfigurationId itemConfigurationId = new ItemConfigurationId("1234");
		final ItemCharacteristics itemCharacteristics = context.mock(ItemCharacteristics.class);
		context.checking(new Expectations() {
			{
				oneOf(delegate).getItemCharacteristics(itemConfigurationId); will(returnValue(itemCharacteristics));
			}
		});
		ItemCharacteristics firstResult = itemCharacteristicsService.getItemCharacteristics(itemConfigurationId);
		assertEquals("The first result should equal the delegate's return", itemCharacteristics, firstResult);

		ItemCharacteristics secondResult = itemCharacteristicsService.getItemCharacteristics(itemConfigurationId);
		assertEquals("The second result should equal the delegate's return", itemCharacteristics, secondResult);
	}

}
