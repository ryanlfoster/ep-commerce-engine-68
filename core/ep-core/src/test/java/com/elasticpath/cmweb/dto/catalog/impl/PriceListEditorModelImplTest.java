package com.elasticpath.cmweb.dto.catalog.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.cmweb.dto.catalog.PriceListEditorModel;
import com.elasticpath.common.dto.pricing.BaseAmountDTO;
import com.elasticpath.common.dto.pricing.PriceListDescriptorDTO;

/**
 * Tests for the PriceListEditorModelImpl class.
 * 
 */
public class PriceListEditorModelImplTest {
	
	private static final String OBJ2 = "OBJ2"; //$NON-NLS-1$
	private static final String OBJ1 = "OBJ1"; //$NON-NLS-1$
	private static final String BAGUID2 = "BAGUID2"; //$NON-NLS-1$
	private static final String BAGUID1 = "BAGUID1"; //$NON-NLS-1$
	private static final String PL_GUID = "PL_GUID";  //$NON-NLS-1$
	private static final String OBJ_TYPE = "OBJ_TYPE";  //$NON-NLS-1$
	private final PriceListDescriptorDTO pldesc = new PriceListDescriptorDTO();
	private static final String CURRENCY = "USD"; //$NON-NLS-1$
	
	/**
	 * Set up work.
	 */
	@Before
	public void setUp() {
		pldesc.setGuid(PL_GUID);
		pldesc.setCurrencyCode(CURRENCY);
	}
	/**
	 * Test retrieval of base amounts in a model with no changes.
	 */
	@Test
	public void testGetBaseAmountsNoChange() {
		final BaseAmountDTO baDto1 = makeBaseAmountDto(PL_GUID, BAGUID1, OBJ1, OBJ_TYPE,
				BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE);
		final BaseAmountDTO baDto2 = makeBaseAmountDto(PL_GUID, BAGUID2, OBJ1, OBJ_TYPE,
				BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE); 
		
		List <BaseAmountDTO> badList = new ArrayList <BaseAmountDTO>();
		badList.add(baDto1);
		badList.add(baDto2);
		
		PriceListEditorModel model = new PriceListEditorModelImpl(pldesc, badList);
		Collection <BaseAmountDTO> retrievedBadList = model.getBaseAmounts();
		
		assertEquals(2, retrievedBadList.size());
		assertTrue(retrievedBadList.contains(baDto1));
		assertTrue(retrievedBadList.contains(baDto2));
	}
	
	/**
	 * Test adding a base amount to the model is reflected when the complete list is retrieved.
	 */
	@Test
	public void testAddBaseAmount() {
		final BaseAmountDTO baDto1 = makeBaseAmountDto(PL_GUID, BAGUID1, OBJ1, OBJ_TYPE,
				BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE);
		final BaseAmountDTO baDto2 = makeBaseAmountDto(PL_GUID, BAGUID2, OBJ1, OBJ_TYPE,
				BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE); 
		
		List <BaseAmountDTO> badList = new ArrayList <BaseAmountDTO>();
		badList.add(baDto1);
		
		PriceListEditorModel model = new PriceListEditorModelImpl(pldesc, badList);
		model.addBaseAmount(baDto2);
		Collection <BaseAmountDTO> retrievedBadList = model.getBaseAmounts();
		assertEquals(2, retrievedBadList.size());
		assertTrue(retrievedBadList.contains(baDto1));
		assertTrue(retrievedBadList.contains(baDto2));
	}
	
	/**
	 * Test removing a base amount to the model is reflected when the complete list is retrieved.
	 */
	@Test
	public void testRemoveBaseAmount() {
		final BaseAmountDTO baDto1 = makeBaseAmountDto(PL_GUID, BAGUID1, OBJ1, OBJ_TYPE,
				BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE);
		final BaseAmountDTO baDto2 = makeBaseAmountDto(PL_GUID, BAGUID2, OBJ2, OBJ_TYPE,
				BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE); 
		
		List <BaseAmountDTO> badList = new ArrayList <BaseAmountDTO>();
		badList.add(baDto1);
		badList.add(baDto2);
		
		PriceListEditorModel model = new PriceListEditorModelImpl(pldesc, badList);
		assertEquals(2, model.getBaseAmounts().size());
		assertTrue(model.getBaseAmounts().contains(baDto1));
		assertTrue(model.getBaseAmounts().contains(baDto2));
		
		model.removeBaseAmount(baDto2);
		
		assertEquals(1, model.getBaseAmounts().size());
		assertTrue(model.getBaseAmounts().contains(baDto1));
		assertFalse(model.getBaseAmounts().contains(baDto2));
	}
	
	/**
	 * Test updating an already persisted base amount in the model and check if it is reflected when the complete list is retrieved.
	 */
	@Test
	public void testUpdatePersistedBaseAmount() {
		final BaseAmountDTO baDto1 = makeBaseAmountDto(PL_GUID, BAGUID1, OBJ1, OBJ_TYPE,
				BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE);
		final BaseAmountDTO baDto2 = makeBaseAmountDto(PL_GUID, BAGUID2, OBJ2, OBJ_TYPE,
				BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE); 
		
		final BaseAmountDTO baDto2mod = makeBaseAmountDto(PL_GUID, BAGUID2, OBJ2, OBJ_TYPE,
				BigDecimal.ONE, BigDecimal.TEN, BigDecimal.TEN); 
		
		List <BaseAmountDTO> badList = new ArrayList <BaseAmountDTO>();
		badList.add(baDto1);
		badList.add(baDto2);
		PriceListEditorModel model = new PriceListEditorModelImpl(pldesc, badList);
		assertEquals(2, model.getBaseAmounts().size());
		assertTrue(model.getBaseAmounts().contains(baDto1));
		assertTrue(model.getBaseAmounts().contains(baDto2));
		
		model.updateBaseAmount(baDto2, baDto2mod);
		
		assertEquals(2, model.getBaseAmounts().size());
		assertTrue(model.getBaseAmounts().contains(baDto1));
		assertFalse(model.getBaseAmounts().contains(baDto2));
		assertTrue(model.getBaseAmounts().contains(baDto2mod));
	}
	
	/**
	 * Test a mix of operations on already persisted base amount in the model and check if it is reflected when the complete list is retrieved.
	 */
	@Test
	public void testMixedOperationsOnBaseAmount1() {
		final BaseAmountDTO baDto1 = makeBaseAmountDto(PL_GUID, BAGUID1, OBJ1, OBJ_TYPE,
				BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE);
		final BaseAmountDTO baDto2 = makeBaseAmountDto(PL_GUID, BAGUID2, OBJ2, OBJ_TYPE,
				BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE); 
		
		final BaseAmountDTO baDto2mod = makeBaseAmountDto(PL_GUID, BAGUID2, OBJ2, OBJ_TYPE,
				BigDecimal.ONE, BigDecimal.TEN, BigDecimal.TEN); 
		
		final BaseAmountDTO baDto3 = makeBaseAmountDto(PL_GUID, "BAGUID3", "OBJ3", OBJ_TYPE, //$NON-NLS-1$ //$NON-NLS-2$
				BigDecimal.ONE, BigDecimal.TEN, BigDecimal.TEN); 
		
		List <BaseAmountDTO> badList = new ArrayList <BaseAmountDTO>();
		badList.add(baDto1);
		
		PriceListEditorModel model = new PriceListEditorModelImpl(pldesc, badList);
		assertEquals(1, model.getBaseAmounts().size());
		assertTrue(model.getBaseAmounts().contains(baDto1));
		
		model.addBaseAmount(baDto3);
		model.updateBaseAmount(baDto2, baDto2mod);
		model.removeBaseAmount(baDto1);
		
		assertEquals(2, model.getBaseAmounts().size());
		assertFalse(model.getBaseAmounts().contains(baDto1));
		assertFalse(model.getBaseAmounts().contains(baDto2));
		assertTrue(model.getBaseAmounts().contains(baDto2mod));
		assertTrue(model.getBaseAmounts().contains(baDto3));
	}
	
	/**
	 * Test a mix of operations on not yet persisted base amount in the model and check if it is reflected when the complete list is retrieved.
	 * Non persisted base amounts don't have GUIDs, and therefore entails a different check in code for equality
	 */
	@Test
	public void testMixedOperationsOnBaseAmount2() {
		//Not persisted. No GUID.
		final BaseAmountDTO baDto2 = makeBaseAmountDto(PL_GUID, null, OBJ2, OBJ_TYPE,
				BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE); 
		
		//Updating a non persisted base amount
		final BaseAmountDTO baDto2mod = makeBaseAmountDto(PL_GUID, null, OBJ2, OBJ_TYPE,
				BigDecimal.ONE, BigDecimal.TEN, BigDecimal.TEN); 
		
		List <BaseAmountDTO> badList = new ArrayList <BaseAmountDTO>();
		PriceListEditorModel model = new PriceListEditorModelImpl(pldesc, badList);
		
		model.addBaseAmount(baDto2);
		model.updateBaseAmount(baDto2, baDto2mod);
		
		assertEquals(1, model.getBaseAmounts().size());
		assertFalse(model.getBaseAmounts().contains(baDto2));
		assertTrue(model.getBaseAmounts().contains(baDto2mod));
	}
	/**
	 * Test the business logic for equality of base amounts.
	 */
	@Test
	public void testLogicalEquals() {
		final BaseAmountDTO baDto1 = makeBaseAmountDto(PL_GUID, BAGUID1, OBJ1, OBJ_TYPE,
				BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE);
		final BaseAmountDTO baDto2 = makeBaseAmountDto(PL_GUID, BAGUID2, OBJ1, OBJ_TYPE,
				BigDecimal.ONE, BigDecimal.TEN, BigDecimal.ONE);
		final BaseAmountDTO baDto3 = makeBaseAmountDto(PL_GUID, BAGUID2, OBJ1, OBJ_TYPE,
				BigDecimal.ONE, BigDecimal.ONE, BigDecimal.TEN);
		//The following differs from baDto1 by only one field
		final BaseAmountDTO baDto4 = makeBaseAmountDto(PL_GUID, BAGUID2, OBJ1, "ANOTHERTYPE", //$NON-NLS-1$
				BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE);
		final BaseAmountDTO baDto5 = makeBaseAmountDto(PL_GUID, BAGUID1, OBJ1, OBJ_TYPE,
				BigDecimal.TEN, BigDecimal.ONE, BigDecimal.ONE);
		final BaseAmountDTO baDto6 = makeBaseAmountDto(PL_GUID, BAGUID1, OBJ2, OBJ_TYPE,
				BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE);
		final BaseAmountDTO baDto7 = makeBaseAmountDto(PL_GUID, BAGUID1, null, OBJ_TYPE,
				BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE);
		
		PriceListEditorModelImpl impl = new PriceListEditorModelImpl(null, new ArrayList <BaseAmountDTO>());
		assertTrue(impl.logicalEquals(baDto1, baDto2));
		assertTrue(impl.logicalEquals(baDto1, baDto3));
		assertFalse(impl.logicalEquals(baDto1, baDto4));
		assertFalse(impl.logicalEquals(baDto1, baDto5));
		assertFalse(impl.logicalEquals(baDto1, baDto6));
		assertFalse(impl.logicalEquals(baDto6, baDto7));
	}

	private BaseAmountDTO makeBaseAmountDto(final String plGuid, final String guid, final String objGuid, 
			final String objType, final BigDecimal quantity, final BigDecimal list, final BigDecimal sale) {
		BaseAmountDTO baDTO = new BaseAmountDTO();
		baDTO.setGuid(guid);
		baDTO.setPriceListDescriptorGuid(plGuid);
		baDTO.setObjectGuid(objGuid);
		baDTO.setObjectType(objType);
		baDTO.setQuantity(quantity);
		baDTO.setListValue(list);
		baDTO.setSaleValue(sale);
		return baDTO;
	}

}
