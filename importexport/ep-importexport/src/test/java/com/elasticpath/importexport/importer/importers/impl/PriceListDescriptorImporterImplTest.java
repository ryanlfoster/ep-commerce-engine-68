package com.elasticpath.importexport.importer.importers.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.common.dto.pricing.PriceListDescriptorDTO;
import com.elasticpath.domain.pricing.PriceListDescriptor;
import com.elasticpath.service.pricing.BaseAmountService;
import com.elasticpath.service.pricing.PriceListDescriptorService;

/**
 * Test for PriceListDescriptorImporterImpl.
 */
public class PriceListDescriptorImporterImplTest {

	private static final String PL_GUID = "GUID_1";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	
	private final BaseAmountService baseAmountService = context.mock(BaseAmountService.class);
	
	private final PriceListDescriptorService priceListDescriptorService = context.mock(PriceListDescriptorService.class);
	
	private final PriceListDescriptorImporterImpl priceListDescriptorImporter = new PriceListDescriptorImporterImpl();
	
	private PriceListDescriptorDTO createPriceListDTO() {
		PriceListDescriptorDTO dto = new PriceListDescriptorDTO();
		
		dto.setGuid(PL_GUID);
		
		return dto;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		priceListDescriptorImporter.setBaseAmountService(baseAmountService);
		priceListDescriptorImporter.setPriceListDescriptorService(priceListDescriptorService);
	}

	/**
	 * Tests findPersistentObjectPriceListDescriptorDTO. 
	 * 
	 * @throws Exception
	 */
	@Test
	public void testFindPersistentObjectPriceListDescriptorDTO() {
		final PriceListDescriptor priceListDescriptor = context.mock(PriceListDescriptor.class);
		context.checking(new Expectations() { {
			oneOf(priceListDescriptorService).findByGuid(PL_GUID); will(returnValue(priceListDescriptor));
		} });
		
		assertEquals(priceListDescriptor, priceListDescriptorImporter.findPersistentObject(createPriceListDTO()));
	}

	/**
	 * Tests getDtoGuid.
	 */
	@Test
	public void testGetDtoGuidPriceListDescriptorDTO() {
		assertEquals(PL_GUID, priceListDescriptorImporter.getDtoGuid(createPriceListDTO()));
	}

	/**
	 * Tests getImportedObjectName.
	 */
	@Test
	public void testGetImportedObjectName() {
		assertEquals(PriceListDescriptorDTO.ROOT_ELEMENT, priceListDescriptorImporter.getImportedObjectName());
	}

	/** The dto class must be present and correct. */
	@Test
	public void testDtoClass() {
		assertEquals("Incorrect DTO class", PriceListDescriptorDTO.class, priceListDescriptorImporter.getDtoClass());
	}

	/** The auxiliary JAXB class list must not be null (can be empty). */
	@Test
	public void testAuxiliaryJaxbClasses() {
		assertNotNull(priceListDescriptorImporter.getAuxiliaryJaxbClasses());
	}
}
