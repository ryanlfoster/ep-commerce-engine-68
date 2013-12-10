package com.elasticpath.importexport.common.adapters.pricelist;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.math.BigDecimal;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.common.dto.assembler.pricing.BaseAmountDtoAssembler;
import com.elasticpath.common.dto.pricing.BaseAmountDTO;
import com.elasticpath.domain.pricing.BaseAmount;
import com.elasticpath.domain.pricing.PriceListDescriptor;
import com.elasticpath.domain.pricing.impl.BaseAmountImpl;
import com.elasticpath.service.pricing.PriceListDescriptorService;

/**
 * Tests for BaseAmountAdapter.
 */
public class BaseAmountAdapterTest {

	private static final String PRICE_LIST_DESCRIPTOR_GUID = "priceListDescriptorGuid";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery() {
		{
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};

	private final BaseAmountDtoAssembler baseAmountDtoAssembler = context.mock(BaseAmountDtoAssembler.class);

	private final PriceListDescriptorService priceListDescriptorService = context.mock(PriceListDescriptorService.class);

	private final BaseAmountAdapter baseAmountAdapter = new BaseAmountAdapter();

	private BaseAmountDTO createBaseAmountDTO() {
		final BaseAmountDTO source = new BaseAmountDTO();
		source.setGuid("guid");
		source.setObjectGuid("objectGuid");
		source.setObjectType("objectType");
		source.setQuantity(BigDecimal.ONE);
		source.setSaleValue(BigDecimal.ONE);
		source.setListValue(BigDecimal.TEN);
		source.setPriceListDescriptorGuid(PRICE_LIST_DESCRIPTOR_GUID);
		return source;
	}

	@Before
	public void setUp() throws Exception {
		baseAmountAdapter.setBaseAmountDtoAssembler(baseAmountDtoAssembler);
		baseAmountAdapter.setPriceListDescriptorService(priceListDescriptorService);
	}

	/**
	 * Tests populateDTO.
	 */
	@Test
	public void testPopulateDTO() {
		final BaseAmount source = null;
		final BaseAmountDTO target = null;

		context.checking(new Expectations() { {
			oneOf(baseAmountDtoAssembler).assembleDto(source, target);
		} });

		baseAmountAdapter.populateDTO(source, target);
	}

	/**
	 * Tests buildDomain.
	 */
	@Test
	public void testBuildDomain() {
		final BaseAmountDTO source = createBaseAmountDTO();

		final BaseAmount target = null;

		context.checking(new Expectations() { {
			oneOf(priceListDescriptorService).findByGuid(PRICE_LIST_DESCRIPTOR_GUID); will(returnValue(context.mock(PriceListDescriptor.class)));
			oneOf(baseAmountDtoAssembler).assembleDomain(source);
		} });

		baseAmountAdapter.buildDomain(source, target);
	}

	/**
	 * Tests populateDomain.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void testPopulateDomain() {
		BaseAmountDTO source = new BaseAmountDTO();
		BaseAmount target = new BaseAmountImpl();

		baseAmountAdapter.populateDomain(source, target);
	}

	/**
	 * Tests createDomainObject.
	 */
	@Test
	public void testCreateDomainObject() {
		assertNull(baseAmountAdapter.createDomainObject());
	}

	/**
	 * Tests createDtoObject.
	 */
	@Test
	public void testCreateDtoObject() {
		assertEquals(BaseAmountDTO.class, baseAmountAdapter.createDtoObject().getClass());
	}

}
