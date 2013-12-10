package com.elasticpath.importexport.importer.importers.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.common.dto.pricing.BaseAmountDTO;
import com.elasticpath.domain.pricing.BaseAmount;
import com.elasticpath.importexport.common.adapters.pricelist.BaseAmountAdapter;
import com.elasticpath.service.pricing.BaseAmountService;

/**
 * Tests BaseAmountImporterImpl.
 */
public class BaseAmountImporterImplTest {

	private static final String BASE_AMOUNT_GUID = "BASE_AM_1";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	
	private final BaseAmountService baseAmountService = context.mock(BaseAmountService.class);
	
	private final BaseAmountImporterImpl baseAmountImporter = new BaseAmountImporterImpl();
	
	private BaseAmountDTO createBaseAmountDTO() {
		BaseAmountDTO dto = new BaseAmountDTO();
		
		dto.setGuid(BASE_AMOUNT_GUID);
		
		return dto;
	}

	@Before
	public void setUp() throws Exception {
		baseAmountImporter.setBaseAmountService(baseAmountService);
	}

	/**
	 * Tests findPersistentObject.
	 */
	@Test
	public void testFindPersistentObjectBaseAmountDTO() {		
		final BaseAmount baseAmount = context.mock(BaseAmount.class);
		context.checking(new Expectations() { {
			oneOf(baseAmountService).findByGuid(BASE_AMOUNT_GUID); will(returnValue(baseAmount));
		} });
		
		assertEquals(baseAmount, baseAmountImporter.findPersistentObject(createBaseAmountDTO()));
	}

	/**
	 * Tests getDtoGuid.
	 */
	@Test
	public void testGetDtoGuidBaseAmountDTO() {
		assertEquals(BASE_AMOUNT_GUID, baseAmountImporter.getDtoGuid(createBaseAmountDTO()));
	}

	/**
	 * Tests getDomainAdapter.
	 */
	@Test
	public void testGetDomainAdapter() {
		final BaseAmountAdapter baseAmountAdapter = new BaseAmountAdapter();
		
		baseAmountImporter.setBaseAmountAdapter(baseAmountAdapter);
		assertEquals(baseAmountAdapter, baseAmountImporter.getDomainAdapter());
	}

	/**
	 * Tests getImportedObjectName.
	 */
	@Test
	public void testGetImportedObjectName() {
		assertEquals(BaseAmountDTO.ROOT_ELEMENT, baseAmountImporter.getImportedObjectName());
	}


	/** The dto class must be present and correct. */
	@Test
	public void testDtoClass() {
		assertEquals("Incorrect DTO class", BaseAmountDTO.class, baseAmountImporter.getDtoClass());
	}

	/** The auxiliary JAXB class list must not be null (can be empty). */
	@Test
	public void testAuxiliaryJaxbClasses() {
		assertNotNull(baseAmountImporter.getAuxiliaryJaxbClasses());
	}
}
