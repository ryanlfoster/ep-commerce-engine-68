package com.elasticpath.importexport.common.adapters.products.data;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.importexport.common.dto.products.ShippableItemDTO;
import com.elasticpath.importexport.common.dto.products.UnitDTO;
import com.elasticpath.importexport.common.exception.runtime.PopulationRuntimeException;
import com.elasticpath.settings.SettingsReader;
import com.elasticpath.settings.domain.SettingValue;

/**
 * Test for ShippableItemAdapter class.
 */
public class ShippableItemAdapterTest {

	private static final BigDecimal THE_ONE = BigDecimal.ONE;

	private static final BigDecimal MINUS_ONE = new BigDecimal("-1");

	private static final String WEIGHT = "10.0";

	private static final String LENGTH = "15.0";

	private static final String WIDTH = "15.0";

	private static final String HEIGHT = "10.0";

	private ShippableItemAdapter shippableItemAdapter;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private SettingsReader mockSettingsReader;

	@Before
	public void setUp() throws Exception {
		final SettingValue unitsWeightSettingValue = context.mock(SettingValue.class, "weights");
		mockSettingsReader = context.mock(SettingsReader.class);
		final SettingValue unitsLengthSettingValue = context.mock(SettingValue.class, "lengths");
		context.checking(new Expectations() {
			{
				allowing(unitsWeightSettingValue).getValue();
				will(returnValue("KG"));
				allowing(mockSettingsReader).getSettingValue("COMMERCE/SYSTEM/UNITS/weight");
				will(returnValue(unitsWeightSettingValue));

				allowing(unitsLengthSettingValue).getValue();
				will(returnValue("CM"));
				allowing(mockSettingsReader).getSettingValue("COMMERCE/SYSTEM/UNITS/length");
				will(returnValue(unitsLengthSettingValue));
			}
		});

		shippableItemAdapter = new ShippableItemAdapter();
		shippableItemAdapter.setSettingsReader(mockSettingsReader);
	}

	/**
	 * Test method for ShippableItemAdapter.checkNegative.
	 */
	@Test(expected = PopulationRuntimeException.class)
	public void testCheckNegative() {
		shippableItemAdapter.checkNegative(THE_ONE, "TheOne");

		shippableItemAdapter.checkNegative(MINUS_ONE, "MinusOne");
	}

	/**
	 * Test method for 'com.elasticpath.importexport.common.adapters.products.data.ShippableItemAdapter.populateDTO(ProductSku, ShippableItemDTO)'.
	 */
	@Test
	public void testPopulateDTO() {
		ProductSku productSku = createProdcutSku();

		ShippableItemDTO shippableItemDTO = new ShippableItemDTO();
		shippableItemAdapter.populateDTO(productSku, shippableItemDTO);

		UnitDTO unitDto = shippableItemDTO.getHeight();
		assertEquals("CM", unitDto.getUnits());
		assertEquals(productSku.getHeight(), unitDto.getValue());

		unitDto = shippableItemDTO.getLength();
		assertEquals("CM", unitDto.getUnits());
		assertEquals(productSku.getLength(), unitDto.getValue());

		unitDto = shippableItemDTO.getWidth();
		assertEquals("CM", unitDto.getUnits());
		assertEquals(productSku.getWidth(), unitDto.getValue());

		unitDto = shippableItemDTO.getWeight();
		assertEquals("KG", unitDto.getUnits());
		assertEquals(productSku.getWeight(), unitDto.getValue());

	}

	/**
	 * Test method for 'com.elasticpath.importexport.common.adapters.products.data.ShippableItemAdapter.populateDomain(ShippableItemDTO,
	 * ProductSku)'.
	 */
	@Test
	public void testPopulateDomain() {
		ShippableItemDTO shippableItemDTO = createShippableItemDTO();

		ProductSku productSku = new ProductSkuImpl();
		shippableItemDTO.setEnabled(Boolean.TRUE);
		shippableItemAdapter.populateDomain(shippableItemDTO, productSku);

		assertEquals(shippableItemDTO.getHeight().getValue(), productSku.getHeight());
		assertEquals(shippableItemDTO.getLength().getValue(), productSku.getLength());
		assertEquals(shippableItemDTO.getWidth().getValue(), productSku.getWidth());
		assertEquals(shippableItemDTO.getWeight().getValue(), productSku.getWeight());
	}

	/**
	 * Creates product sku.
	 * @return the product sku
	 */
	private ProductSku createProdcutSku() {
		ProductSku productSku = new ProductSkuImpl();
		productSku.setWeight(new BigDecimal(WEIGHT));
		productSku.setLength(new BigDecimal(LENGTH));
		productSku.setWidth(new BigDecimal(WIDTH));
		productSku.setHeight(new BigDecimal(HEIGHT));
		return productSku;
	}

	/**
	 * Creates shippable item DTO.
	 * @return the shippable item DTO
	 */
	public ShippableItemDTO createShippableItemDTO() {
		ShippableItemDTO shippableItemDTO = new ShippableItemDTO();
		shippableItemDTO.setWeight(new UnitDTO("KG", new BigDecimal(WEIGHT)));
		shippableItemDTO.setLength(new UnitDTO("CM", new BigDecimal(LENGTH)));
		shippableItemDTO.setWidth(new UnitDTO(null, new BigDecimal(WIDTH)));
		shippableItemDTO.setHeight(new UnitDTO(null, new BigDecimal(HEIGHT)));
		return shippableItemDTO;
	}

}
