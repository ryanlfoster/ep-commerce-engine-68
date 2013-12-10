package com.elasticpath.test.integration;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.common.dto.pricing.PriceListDescriptorDTO;
import com.elasticpath.common.pricing.service.PriceListService;

/**
 * Test the client side PriceListService.
 */
public class PriceListServiceImplTest extends BasicSpringContextTest {

	private static final String DEFAULT_CURRENCY = "CAD";

	private static final String DEFAULT_DESCRIPTION = "DESC";

	@Autowired
	private PriceListService service;

	/**
	 * Test the creation of a price list with a pricelist descriptor.
	 * Coming form the client side, no UIDs/GUIDs will be available. 
	 * 
	 * After saving, fields from DTO should be persisted and GUID assigned.
	 */
	@DirtiesDatabase
	@Test
	public void testCreatePriceList() {
		PriceListDescriptorDTO dto = new PriceListDescriptorDTO();
		dto.setCurrencyCode(DEFAULT_CURRENCY);
		dto.setName("PL");
		dto.setDescription(DEFAULT_DESCRIPTION);
		PriceListDescriptorDTO merged = service.saveOrUpdate(dto);
		assertNotNull(merged.getGuid());
		assertEquals(dto.getCurrencyCode(), merged.getCurrencyCode());
		assertEquals(dto.getName(), merged.getName());
		assertEquals(dto.getDescription(), merged.getDescription());
	}
	
	/**
	 * Test the updating of a Price List Descriptor. GUID is available on the DTO since
	 * original domain object is already persisted.
	 * All fields should be copied.
	 */
	@DirtiesDatabase
	@Test
	public void testUpdatePriceList() {
		PriceListDescriptorDTO dto = new PriceListDescriptorDTO();
		dto.setCurrencyCode(DEFAULT_CURRENCY);
		dto.setName("PL");
		dto.setDescription(DEFAULT_DESCRIPTION);
		PriceListDescriptorDTO saved = service.saveOrUpdate(dto);
		assertNotNull(saved.getGuid());
		PriceListDescriptorDTO retrieved = service.getPriceListDescriptor(saved.getGuid());
		retrieved.setDescription("CHANGED_DESC");
		retrieved.setName("CHANGED_PL");
		retrieved.setCurrencyCode("USD");
		PriceListDescriptorDTO merged = service.saveOrUpdate(retrieved);
		assertEquals(saved.getGuid(), merged.getGuid());
		assertEquals(retrieved.getDescription(), merged.getDescription());
		assertEquals(retrieved.getCurrencyCode(), merged.getCurrencyCode());
		assertEquals(retrieved.getName(), merged.getName());
	}
	
	/**
	 * Test the updating of a Price List Descriptor with a GUID that is not in the database.
	 */
	@DirtiesDatabase
	@Test
	public void testUpdatePriceListWithNewGuid() {
		PriceListDescriptorDTO dto = new PriceListDescriptorDTO();
		dto.setCurrencyCode(DEFAULT_CURRENCY);
		dto.setName("PL");
		dto.setDescription(DEFAULT_DESCRIPTION);
		PriceListDescriptorDTO saved = service.saveOrUpdate(dto);
		assertNotNull("Guid of saved dto object which didn't have GUID specified before save should be set.", saved.getGuid());
		PriceListDescriptorDTO retrieved = service.getPriceListDescriptor(saved.getGuid());
		String newGuid = "NEW_GUID";
		retrieved.setGuid(newGuid);
		retrieved.setName("NEW_NAME");
		PriceListDescriptorDTO newSaved = service.saveOrUpdate(retrieved);
		assertNotNull("DTO with unknown guid should have saved", newSaved);
		assertEquals("Expected set guid to be saved correctly.", newGuid, newSaved.getGuid());
	}
}
