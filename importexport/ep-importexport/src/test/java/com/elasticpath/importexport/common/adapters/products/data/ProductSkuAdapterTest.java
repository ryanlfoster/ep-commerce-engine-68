package com.elasticpath.importexport.common.adapters.products.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.ElasticPath;
import com.elasticpath.domain.attribute.AttributeValueGroup;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.domain.skuconfiguration.impl.JpaAdaptorOfSkuOptionValueImpl;
import com.elasticpath.importexport.common.dto.products.AttributeGroupDTO;
import com.elasticpath.importexport.common.dto.products.DigitalAssetItemDTO;
import com.elasticpath.importexport.common.dto.products.AttributeValuesDTO;
import com.elasticpath.importexport.common.dto.products.ProductSkuAvailabilityDTO;
import com.elasticpath.importexport.common.dto.products.ProductSkuDTO;
import com.elasticpath.importexport.common.dto.products.ShippableItemDTO;
import com.elasticpath.importexport.common.dto.products.SkuOptionDTO;
import com.elasticpath.importexport.common.exception.runtime.PopulationRuntimeException;

/**
 * Verify that ProductSkuAdapter populates category domain object from DTO properly and vice versa.
 * <br>Nested adapters should be tested separately.
 */
public class ProductSkuAdapterTest {

	private static final String ATTRIBUTE_KEY = "attributeKey";

	private static final String PRODUCT_SKU_CODE = "productSkuCode";

	private static final Integer MAX_DOWNLOAD_TIME = Integer.valueOf(5);

	private static final String PRODUCT_SKU_IMAGE = "skuOptionImage";

	private static final Date START_DATE = new Date();

	private static final Date END_DATE = new Date();

	private static final String SKU_OPTION_VALUE = "skuOptionValue";

	private static final String SKU_OPTION_CODE = "skuOptionCode";

	private ProductSkuAdapter productSkuAdapter;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private ProductSku mockProductSku;

	private ElasticPath mockElasticPath;

	private SkuOption mockSkuOption;

	private SkuOptionValue mockSkuOptionValue;

	private Product mockProduct;

	private AttributeValueGroup mockAttributeValueGroup;

	@Before
	public void setUp() throws Exception {
		mockProductSku = context.mock(ProductSku.class);
		mockSkuOption = context.mock(SkuOption.class);
		mockSkuOptionValue = context.mock(SkuOptionValue.class);
		mockElasticPath = context.mock(ElasticPath.class);
		mockProduct = context.mock(Product.class);
		mockAttributeValueGroup = context.mock(AttributeValueGroup.class);

		productSkuAdapter = new ProductSkuAdapter();
		productSkuAdapter.setElasticPath(mockElasticPath);
	}

	private SkuOptionDTO createSkuOptionDTO() {
		SkuOptionDTO skuOptionDTO = new SkuOptionDTO();
		skuOptionDTO.setCode(SKU_OPTION_CODE);
		skuOptionDTO.setSkuOptionValue(SKU_OPTION_VALUE);
		return skuOptionDTO;
	}

	/**
	 * Tests PopulateDomain.
	 *
	 * TODO: This Test can be improved if it necessary.
	 */
	@Test
	public void testPopulateDomain() {
		context.checking(new Expectations() {
			{
				oneOf(mockProductSku).setSkuCode(SKU_OPTION_CODE);
				oneOf(mockProductSku).setGuid(SKU_OPTION_CODE);
				oneOf(mockProductSku).setImage(PRODUCT_SKU_IMAGE);
				oneOf(mockProductSku).getAttributeValueGroup();
				will(returnValue(mockAttributeValueGroup));
			}
		});

		ProductSkuAdapter adapter = new ProductSkuAdapter() {
			@Override
			void populateDomainSkuOptions(final ProductSku productSku, final List<SkuOptionDTO> skuOptionDtoList) {
				// empty
			}
			@Override
			void populateProductSkuAvailability(final ProductSku productSku, final ProductSkuAvailabilityDTO productSkuAvailabilityDTO) {
				// empty
			}
			@Override
			void checkDigitalShippable(final ShippableItemDTO shippableItem, final DigitalAssetItemDTO digitalAssetItem) {
				// empty
			}
		};

		adapter.setShippableItemAdapter(new ShippableItemAdapter() {
			@Override
			public void populateDomain(final ShippableItemDTO source, final ProductSku target) {
				// empty
			}
		});

		adapter.setDigitalAssetItemAdapter(new DigitalAssetItemAdapter() {
			@Override
			public void populateDomain(final DigitalAssetItemDTO source, final ProductSku target) {
				// empty
			}
		});

		adapter.setAttributeGroupAdapter(new AttributeGroupAdapter() {
			@Override
			public void populateDomain(final AttributeGroupDTO attributeGroupDto, final AttributeValueGroup attributeValueGroup) {
				// empty
			}

		});

		ProductSkuDTO productSkuDTO = new ProductSkuDTO();
		productSkuDTO.setGuid(SKU_OPTION_CODE);
		productSkuDTO.setSkuCode(SKU_OPTION_CODE);
		productSkuDTO.setImage(PRODUCT_SKU_IMAGE);

		adapter.populateDomain(productSkuDTO , mockProductSku);
	}

	/**
	 * Tests PopulateProductSkuAvailability.
	 */
	@Test
	public void testPopulateProductSkuAvailability() {
		ProductSkuAvailabilityDTO productSkuAvailabilityDTO = new ProductSkuAvailabilityDTO();

		productSkuAvailabilityDTO.setStartDate(START_DATE);
		productSkuAvailabilityDTO.setEndDate(END_DATE);

		context.checking(new Expectations() {
			{
				oneOf(mockProductSku).setStartDate(START_DATE);
				oneOf(mockProductSku).setEndDate(END_DATE);
			}
		});

		productSkuAdapter.populateProductSkuAvailability(mockProductSku, productSkuAvailabilityDTO);
	}

	/**
	 * Tests PopulateDomainSkuOptions.
	 */
	@Test
	public void testPopulateDomainSkuOptions() {
		context.checking(new Expectations() {
			{
				oneOf(mockSkuOptionValue).setSkuOption(mockSkuOption);
			}
		});
		final JpaAdaptorOfSkuOptionValueImpl expectedSkuOptionValue = new JpaAdaptorOfSkuOptionValueImpl();
		expectedSkuOptionValue.setSkuOptionValue(mockSkuOptionValue);

		final List<SkuOptionDTO> skuOptionDtoList = new ArrayList<SkuOptionDTO>();
		skuOptionDtoList.add(createSkuOptionDTO());

		ProductSkuAdapter adapter = new ProductSkuAdapter() {
			@Override
			JpaAdaptorOfSkuOptionValueImpl createSkuOptionValue(final ProductSku productSku, final String skuOptionCode) {
				return expectedSkuOptionValue;
			}
		};
		adapter.setProductSkuOptionAdapter(new ProductSkuOptionAdapter() {
			@Override
			public void populateDomain(final SkuOptionDTO source, final JpaAdaptorOfSkuOptionValueImpl target) {
				assertEquals(SKU_OPTION_CODE, source.getCode());
				assertEquals(expectedSkuOptionValue, target);
				target.setSkuOption(mockSkuOption);
			}
		});

		adapter.populateDomainSkuOptions(mockProductSku, skuOptionDtoList);
	}

	/**
	 * Tests CreateSkuOptionValue.
	 */
	@Test
	public void testCreateSkuOptionValue() {
		final JpaAdaptorOfSkuOptionValueImpl expectedSkuOptionValue = new JpaAdaptorOfSkuOptionValueImpl();
		final Map<String, SkuOptionValue> optionValueMap = new HashMap<String, SkuOptionValue>();

		context.checking(new Expectations() {
			{
				atLeast(1).of(mockProductSku).getOptionValueMap();
				will(returnValue(optionValueMap));
				oneOf(mockElasticPath).getBean(ContextIdNames.SKU_OPTION_VALUE_JPA_ADAPTOR);
				will(returnValue(expectedSkuOptionValue));
			}
		});

		assertEquals(expectedSkuOptionValue, productSkuAdapter.createSkuOptionValue(mockProductSku, SKU_OPTION_CODE));
		assertEquals(1, optionValueMap.size());
		assertEquals(expectedSkuOptionValue, optionValueMap.get(SKU_OPTION_CODE));
	}

	/**
	 * Tests PopulateDTO.
	 */
	@Test
	public void testPopulateDTO() {
		context.checking(new Expectations() {
			{
				oneOf(mockProduct).hasMultipleSkus();
				will(returnValue(Boolean.TRUE));

				oneOf(mockProductSku).getSkuCode();
				will(returnValue(PRODUCT_SKU_CODE));
				oneOf(mockProductSku).getGuid();
				will(returnValue(PRODUCT_SKU_CODE));
				oneOf(mockProductSku).getProduct();
				will(returnValue(mockProduct));

				oneOf(mockProductSku).getImage();
				will(returnValue(PRODUCT_SKU_IMAGE));
				oneOf(mockProductSku).getAttributeValueGroup();
				will(returnValue(mockAttributeValueGroup));

				oneOf(mockProductSku).getStartDate();
				will(returnValue(START_DATE));
				oneOf(mockProductSku).getEndDate();
				will(returnValue(END_DATE));
			}
		});

		ProductSkuAdapter adapter = new ProductSkuAdapter() {
			@Override
			void populateDTOSkuOptions(final ProductSku productSku, final ProductSkuDTO productSkuDTO) {
				assertNotNull(productSku);
				SkuOptionDTO skuOptionDTO = new SkuOptionDTO();
				skuOptionDTO.setCode(SKU_OPTION_CODE);
				productSkuDTO.setSkuOptionList(Arrays.asList(skuOptionDTO));
			}
		};

		ProductSkuDTO productSkuDTO = new ProductSkuDTO();

		adapter.setShippableItemAdapter(new ShippableItemAdapter() {
			@Override
			public void populateDTO(final ProductSku source, final ShippableItemDTO target) {
				assertNotNull(source);
				target.setEnabled(true);
			}
		});
		adapter.setDigitalAssetItemAdapter(new DigitalAssetItemAdapter() {
			@Override
			public void populateDTO(final ProductSku source, final DigitalAssetItemDTO target) {
				assertNotNull(source);
				target.setEnabled(true);
				target.setMaxDownloadTimes(MAX_DOWNLOAD_TIME);
			}
		});
		adapter.setAttributeGroupAdapter(new AttributeGroupAdapter() {
			@Override
			public void populateDTO(final AttributeValueGroup attributeValueGroup, final AttributeGroupDTO attributeGroupDto) {
				assertNotNull(attributeValueGroup);
				AttributeValuesDTO attributeValuesDTO = new AttributeValuesDTO();
				attributeValuesDTO.setKey(ATTRIBUTE_KEY);
				attributeGroupDto.setAttributeValues(Arrays.asList(attributeValuesDTO));
			}
		});
		adapter.populateDTO(mockProductSku , productSkuDTO);

		assertEquals(PRODUCT_SKU_CODE, productSkuDTO.getSkuCode());
		assertEquals(PRODUCT_SKU_IMAGE, productSkuDTO.getImage());
		assertEquals(END_DATE, productSkuDTO.getProductSkuAvailabilityDTO().getEndDate());
		assertEquals(START_DATE, productSkuDTO.getProductSkuAvailabilityDTO().getStartDate());
		assertEquals(END_DATE, productSkuDTO.getProductSkuAvailabilityDTO().getEndDate());
		assertEquals(MAX_DOWNLOAD_TIME, productSkuDTO.getDigitalAssetItem().getMaxDownloadTimes());
		assertEquals(true, productSkuDTO.getDigitalAssetItem().isEnabled());
		assertEquals(true, productSkuDTO.getShippableItem().isEnabled());
		assertEquals(SKU_OPTION_CODE, productSkuDTO.getSkuOptionList().get(0).getCode());
		assertEquals(1, productSkuDTO.getSkuOptionList().size());
		assertEquals(1, productSkuDTO.getAttributeGroupDTO().getAttributeValuess().size());
		assertEquals(ATTRIBUTE_KEY, productSkuDTO.getAttributeGroupDTO().getAttributeValuess().get(0).getKey());
	}

	/**
	 * Tests PopulateDTOSkuOptions.
	 */
	@Test
	public void testPopulateDTOSkuOptions() {
		final JpaAdaptorOfSkuOptionValueImpl expectedJpaAdaptorOfSkuOptionValue = new JpaAdaptorOfSkuOptionValueImpl();
		final Map<String, SkuOptionValue> optionValueMap = new HashMap<String, SkuOptionValue>();
		optionValueMap.put(SKU_OPTION_CODE, expectedJpaAdaptorOfSkuOptionValue);
		context.checking(new Expectations() {
			{
				oneOf(mockProductSku).getOptionValueMap();
				will(returnValue(optionValueMap));
			}
		});

		ProductSkuDTO productSkuDTO = new ProductSkuDTO();
		productSkuAdapter.setProductSkuOptionAdapter(new ProductSkuOptionAdapter() {
			@Override
			public void populateDTO(final JpaAdaptorOfSkuOptionValueImpl source, final SkuOptionDTO target) {
				assertSame(expectedJpaAdaptorOfSkuOptionValue, source);
				target.setCode(SKU_OPTION_CODE);
			}
		});
		productSkuAdapter.populateDTOSkuOptions(mockProductSku, productSkuDTO);

		assertEquals(1, productSkuDTO.getSkuOptionList().size());
		assertEquals(SKU_OPTION_CODE, productSkuDTO.getSkuOptionList().get(0).getCode());
	}

	/**
	 * Tests CheckDigitalShippable.
	 */
	@Test(expected = PopulationRuntimeException.class)
	public void testCheckDigitalShippable() {
		final ShippableItemDTO shippableItem = new ShippableItemDTO();
		final DigitalAssetItemDTO digitalAssetItem = new DigitalAssetItemDTO();

		shippableItem.setEnabled(true);
		digitalAssetItem.setEnabled(true);

		productSkuAdapter.checkDigitalShippable(shippableItem, digitalAssetItem);
	}

}
