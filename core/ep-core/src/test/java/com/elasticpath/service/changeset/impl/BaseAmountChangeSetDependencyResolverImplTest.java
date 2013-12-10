package com.elasticpath.service.changeset.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.common.dto.pricing.PriceListDescriptorDTO;
import com.elasticpath.common.pricing.service.PriceListService;
import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;
import com.elasticpath.domain.objectgroup.impl.BusinessObjectDescriptorImpl;
import com.elasticpath.domain.pricing.BaseAmount;
import com.elasticpath.domain.pricing.impl.BaseAmountImpl;
import com.elasticpath.service.pricing.BaseAmountService;

/**
 * The unit test class for BaseAmount change set dependency resolver.
 */
public class BaseAmountChangeSetDependencyResolverImplTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private final BaseAmountChangeSetDependencyResolverImpl resolver = new BaseAmountChangeSetDependencyResolverImpl();
	private final BaseAmountService baseAmountService;
	private final PriceListService priceListService;
	
	private final BaseAmount baseAmount;
	private final String baseAmountGuid;
	private final PriceListDescriptorDTO priceListDescriptor;
	private final String priceListGuid;
	
	/**
	 * Constructor.
	 */
	public BaseAmountChangeSetDependencyResolverImplTest() {
		baseAmountGuid = "baseAmountGuid";
		priceListGuid = "priceListGuid";
		priceListDescriptor = new PriceListDescriptorDTO();
		baseAmount = new BaseAmountImpl() {
			private static final long serialVersionUID = 2263728819087443794L;

			public String getPriceListDescriptorGuid() {
				return priceListGuid;
			};
		};
		
		baseAmountService = context.mock(BaseAmountService.class);
		priceListService = context.mock(PriceListService.class);
		
		resolver.setBaseAmountService(baseAmountService);
		resolver.setPriceListService(priceListService);
	}
	
	/**
	 * Test getting the change set dependency for a BaseAmount.
	 */
	@Test
	public void testGetChangeSetDependency() {
		context.checking(new Expectations() { {
			oneOf(priceListService).getPriceListDescriptor(priceListGuid);
		    will(returnValue(priceListDescriptor));
		} });

		Set< ? > dependencies = resolver.getChangeSetDependency(baseAmount);
		assertEquals(
				"the PriceList is not found in the dependency list of the BaseAmount.",
				priceListDescriptor,
				CollectionUtils.get(dependencies, 0));
	}

	/**
	 * Test getting the change set dependency for the wrong kind of object.
	 */
	@Test
	public void testGetChangeSetDependencyWithWrongObject() {
		Object obj = new Object();
		Set< ? > dependencies = resolver.getChangeSetDependency(obj);
		assertTrue("Non-BaseAmount object should not be processed", dependencies.isEmpty());
	}

	/**
	 * Test getting BaseAmount object from BaseAmountChangeSetDependencyResolverImpl.
	 */
	@Test
	public void testGetObject() {
		context.checking(new Expectations() { {
			oneOf(baseAmountService).findByGuid(baseAmountGuid);
		    will(returnValue(baseAmount));
		} });
		
		BusinessObjectDescriptor businessObjectDescriptor = new BusinessObjectDescriptorImpl();
		businessObjectDescriptor.setObjectIdentifier(baseAmountGuid);
		assertEquals(
				"The baseAmount object was not returned for the baseAmountGuid.",
				resolver.getObject(businessObjectDescriptor, BaseAmount.class),
				baseAmount);
	}

}
