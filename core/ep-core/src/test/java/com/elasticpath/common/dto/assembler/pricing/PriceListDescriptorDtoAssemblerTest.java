package com.elasticpath.common.dto.assembler.pricing;

import static org.junit.Assert.assertEquals;

import org.apache.commons.lang.math.RandomUtils;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.common.dto.pricing.PriceListDescriptorDTO;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.pricing.PriceListDescriptor;
import com.elasticpath.domain.pricing.impl.PriceListDescriptorImpl;

/**
 * Test assembler for PriceListDescriptorDTOs.
 */
public class PriceListDescriptorDtoAssemblerTest {
	private static final boolean HIDDEN = true;
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	
	private final PriceListDescriptorDtoAssembler assembler = new PriceListDescriptorDtoAssembler();
	
	/**
	 * Set up and mocking.
	 */
	@Before
	public void setUp() {
		final BeanFactory beanFactory = context.mock(BeanFactory.class);
		context.checking(new Expectations() { {
			allowing(beanFactory).getBean(ContextIdNames.PRICE_LIST_DESCRIPTOR);
			will(returnValue(new PriceListDescriptorImpl()));
		} });
		assembler.setBeanFactory(beanFactory);
	}
	
	
	/**
	 * Test assembling a DTO from a domain object version.
	 */
	@Test
	public void testAssembleDtoFromDomain() {
		PriceListDescriptor pld = getNewPriceListDescriptor();
		PriceListDescriptorDTO dto = assembler.assembleDto(pld);
		assertEquals(pld.getGuid(), dto.getGuid());
		assertEquals(pld.getName(), dto.getName());
		assertEquals(pld.getDescription(), dto.getDescription());
		assertEquals(pld.getCurrencyCode(), dto.getCurrencyCode());
		assertEquals(pld.isHidden(), dto.isHidden());
	}
	
	/**
	 * Test assembling a DTO from a domain object version.
	 */
	@Test
	public void testAssembleDomainFromDto() {
		PriceListDescriptorDTO dto = getNewPriceListDescriptorDTO();
		PriceListDescriptor pld = assembler.assembleDomain(dto);
		assertEquals(pld.getGuid(), dto.getGuid());
		assertEquals(pld.getName(), dto.getName());
		assertEquals(pld.getDescription(), dto.getDescription());
		assertEquals(pld.getCurrencyCode(), dto.getCurrencyCode());
		assertEquals(pld.isHidden(), dto.isHidden());
	}	
	
	private PriceListDescriptor getNewPriceListDescriptor() {
		PriceListDescriptor pld = new PriceListDescriptorImpl();
		long modifier = RandomUtils.nextInt();
		pld.setCurrencyCode("CURRENCY" + modifier);
		pld.setDescription("DESC" + modifier);
		pld.setName("NAME" + modifier);
		pld.setGuid("GUID" + modifier);
		pld.setHidden(HIDDEN);
		return pld;
	}
	
	private PriceListDescriptorDTO getNewPriceListDescriptorDTO() {
		PriceListDescriptorDTO dto = new PriceListDescriptorDTO();
		long modifier = RandomUtils.nextInt();
		dto.setCurrencyCode("CURRENCY" + modifier);
		dto.setDescription("DESC" + modifier);
		dto.setName("NAME" + modifier);
		dto.setGuid("GUID" + modifier);
		dto.setHidden(HIDDEN);
		return dto;
	}
}
