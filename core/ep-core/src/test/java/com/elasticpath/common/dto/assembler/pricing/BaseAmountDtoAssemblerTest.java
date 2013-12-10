package com.elasticpath.common.dto.assembler.pricing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.math.RandomUtils;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.common.dto.pricing.BaseAmountDTO;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.misc.RandomGuid;
import com.elasticpath.domain.pricing.BaseAmount;
import com.elasticpath.domain.pricing.BaseAmountObjectType;
import com.elasticpath.domain.pricing.impl.BaseAmountImpl;
import com.elasticpath.service.catalog.ProductService;
import com.elasticpath.service.catalog.ProductSkuService;
import com.elasticpath.service.pricing.impl.BaseAmountFactoryImpl;
import com.elasticpath.service.pricing.impl.BaseAmountValidatorImpl;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * Test for the BaseAmountDtoAssembler.
 */
@SuppressWarnings({ "PMD.TooManyStaticImports" })
public class BaseAmountDtoAssemblerTest {
	private static final String OBJ_GUID = "OBJ_GUID";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private BeanFactory beanFactory;
	private BeanFactoryExpectationsFactory expectationsFactory;


	private final ProductService productService = context.mock(ProductService.class);

	private final ProductSkuService productSkuService = context.mock(ProductSkuService.class);

	private final BaseAmountDtoAssembler assembler = new BaseAmountDtoAssembler();

	private Product product;

	private ProductBundle bundle;

	private ProductSku sku;

	/**
	 * Set up and mocking.
	 */
	@Before
	public void setUp() {
	    beanFactory = context.mock(BeanFactory.class);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);

		final String randomGuid = "RANDOMGUID";

		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.RANDOM_GUID,
				new RandomGuid() {
					private static final long serialVersionUID = -7638043786071701838L;

			@Override
			public String toString() {
				return randomGuid;
			}
		});

		product = context.mock(Product.class);
		bundle = context.mock(ProductBundle.class);
		sku = context.mock(ProductSku.class);

		BaseAmountFactoryImpl baFactory = new BaseAmountFactoryImpl();
		baFactory.setValidator(new BaseAmountValidatorImpl());
		assembler.setBaseAmountFactory(baFactory);
	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	/**
	 * Test that the assembler creates a domain object from a DTO.
	 */
	@Test
	public void testBaseAmountDomainAssemblyFromDto() {
		BaseAmountDTO dto = getNewBaseAmountDto();
		BaseAmount baseAmount = assembler.assembleDomain(dto);

		assertEquals(dto.getGuid(), baseAmount.getGuid());
		assertEquals(dto.getSaleValue(), baseAmount.getSaleValue());
		assertEquals(dto.getListValue(), baseAmount.getListValue());
		assertEquals(dto.getPriceListDescriptorGuid(), baseAmount.getPriceListDescriptorGuid());
		assertEquals(dto.getQuantity(), baseAmount.getQuantity());
		assertEquals(dto.getObjectGuid(), baseAmount.getObjectGuid());
		assertEquals(dto.getObjectType(), baseAmount.getObjectType());
	}

	/**
	 * Test that the assembler creates a domain object from a DTO. If Guid is null, the domain object guid should be generated.
	 */
	@Test
	public void testBaseAmountDomainAssemblyFromDtoGuidNull() {
		BaseAmountDTO dto = getNewBaseAmountDto();
		dto.setGuid(null);
		BaseAmount baseAmount = assembler.assembleDomain(dto);

		assertNotNull(baseAmount.getGuid());
		assertEquals(dto.getSaleValue(), baseAmount.getSaleValue());
		assertEquals(dto.getListValue(), baseAmount.getListValue());
		assertEquals(dto.getPriceListDescriptorGuid(), baseAmount.getPriceListDescriptorGuid());
		assertEquals(dto.getQuantity(), baseAmount.getQuantity());
		assertEquals(dto.getObjectGuid(), baseAmount.getObjectGuid());
		assertEquals(dto.getObjectType(), baseAmount.getObjectType());
	}

	/**
	 * Test that the assembler creates a list of Domain objects given list of DTOs.
	 */
	@Test
	public void testBaseAmountDomainAssemblyFromDtoAsList() {
		List<BaseAmountDTO> dtoList = new ArrayList<BaseAmountDTO>();
		BaseAmountDTO dto1 = getNewBaseAmountDto();
		BaseAmountDTO dto2 = getNewBaseAmountDto();
		dtoList.add(dto1);
		dtoList.add(dto2);

		List<BaseAmount> domainList = assembler.assembleDomain(dtoList);
		assertEquals(2, domainList.size());
		BaseAmount baseAmount = domainList.get(0);
		assertEquals(dto1.getGuid(), baseAmount.getGuid());
		assertEquals(dto1.getSaleValue(), baseAmount.getSaleValue());
		assertEquals(dto1.getListValue(), baseAmount.getListValue());
		assertEquals(dto1.getPriceListDescriptorGuid(), baseAmount.getPriceListDescriptorGuid());
		assertEquals(dto1.getQuantity(), baseAmount.getQuantity());
		assertEquals(dto1.getObjectGuid(), baseAmount.getObjectGuid());
		assertEquals(dto1.getObjectType(), baseAmount.getObjectType());

		baseAmount = domainList.get(1);
		assertEquals(dto2.getGuid(), baseAmount.getGuid());
		assertEquals(dto2.getSaleValue(), baseAmount.getSaleValue());
		assertEquals(dto2.getListValue(), baseAmount.getListValue());
		assertEquals(dto2.getPriceListDescriptorGuid(), baseAmount.getPriceListDescriptorGuid());
		assertEquals(dto2.getQuantity(), baseAmount.getQuantity());
		assertEquals(dto2.getObjectGuid(), baseAmount.getObjectGuid());
		assertEquals(dto2.getObjectType(), baseAmount.getObjectType());
	}

	/**
	 * Test the assembler creates proper DTOs from Domain object.
	 */
	@Test
	public void testBaseAmountDtoAssemblyFromDomain() {
		BaseAmount baseAmount = getNewBaseAmount();
		addExpectationsForProduct(baseAmount.getObjectGuid(), null);
		BaseAmountDTO dto = assembler.assembleDto(baseAmount);
		assertEquals(dto.getGuid(), baseAmount.getGuid());
		assertEquals(dto.getSaleValue(), baseAmount.getSaleValue());
		assertEquals(dto.getListValue(), baseAmount.getListValue());
		assertEquals(dto.getPriceListDescriptorGuid(), baseAmount.getPriceListDescriptorGuid());
		assertEquals(dto.getQuantity(), baseAmount.getQuantity());
		assertEquals(dto.getObjectGuid(), baseAmount.getObjectGuid());
		assertEquals(dto.getObjectType(), baseAmount.getObjectType());
	}

	/**
	 * Test that the assembler creates a list of DTOs from given list of domain objects.
	 */
	@Test
	public void testBaseAmountDtoAssemblyFromDomainAsList() {
		List<BaseAmount> baList = new ArrayList<BaseAmount>();
		BaseAmount ba1 = getNewBaseAmount();
		BaseAmount ba2 = getNewBaseAmount();
		baList.add(ba1);
		addExpectationsForProduct(ba1.getObjectGuid(), null);
		baList.add(ba2);
		addExpectationsForBundle(ba2.getObjectGuid(), null);

		List<BaseAmountDTO> dtoList = assembler.assembleDto(baList);
		assertEquals(2, dtoList.size());
		BaseAmountDTO dto = dtoList.get(0);
		assertEquals(ba1.getGuid(), dto.getGuid());
		assertEquals(ba1.getSaleValue(), dto.getSaleValue());
		assertEquals(ba1.getListValue(), dto.getListValue());
		assertEquals(ba1.getPriceListDescriptorGuid(), dto.getPriceListDescriptorGuid());
		assertEquals(ba1.getQuantity(), dto.getQuantity());
		assertEquals(ba1.getObjectGuid(), dto.getObjectGuid());
		assertEquals(ba1.getObjectType(), dto.getObjectType());

		dto = dtoList.get(1);
		assertEquals(ba2.getGuid(), dto.getGuid());
		assertEquals(ba2.getSaleValue(), dto.getSaleValue());
		assertEquals(ba2.getListValue(), dto.getListValue());
		assertEquals(ba2.getPriceListDescriptorGuid(), dto.getPriceListDescriptorGuid());
		assertEquals(ba2.getQuantity(), dto.getQuantity());
		assertEquals(ba2.getObjectGuid(), dto.getObjectGuid());
		assertEquals(ba2.getObjectType(), dto.getObjectType());
	}


	private BaseAmountDTO getNewBaseAmountDto() {
		BaseAmountDTO dto = new BaseAmountDTO();
		long modifier = RandomUtils.nextInt();
		dto.setGuid("GUID" + modifier);
		dto.setListValue(new BigDecimal(2 + modifier));
		dto.setSaleValue(new BigDecimal(1 + modifier));
		dto.setPriceListDescriptorGuid("PLGUID" + modifier);
		dto.setObjectGuid(OBJ_GUID + modifier);
		dto.setQuantity(new BigDecimal(1 + modifier));
		dto.setObjectType(BaseAmountObjectType.PRODUCT.getName());
		return dto;
	}

	private BaseAmount getNewBaseAmount() {
		long modifier = RandomUtils.nextInt();
		BaseAmount baseAmount = new BaseAmountImpl("GUID" + modifier, OBJ_GUID + modifier, BaseAmountObjectType.PRODUCT.getName(),
				new BigDecimal(1 + modifier), new BigDecimal(2 + modifier), new BigDecimal(1 + modifier), "PLD_GUID" + modifier);
		return baseAmount;
	}

	private void addExpectationsForBundle(final String prodGuid, final String skuGuid) {
		context.checking(new Expectations() {
			{
				allowing(sku).getProduct();
				will(returnValue(bundle));
				allowing(productService).findByGuid(prodGuid);
				will(returnValue(bundle));
				allowing(productSkuService).findBySkuCode(skuGuid);
				will(returnValue(sku));
			}
		});
	}

	private void addExpectationsForProduct(final String prodGuid, final String skuGuid) {
		context.checking(new Expectations() {
			{
				allowing(sku).getProduct();
				will(returnValue(product));
				allowing(productService).findByGuid(prodGuid);
				will(returnValue(product));
				allowing(productSkuService).findBySkuCode(skuGuid);
				will(returnValue(sku));
			}
		});
	}

	/**
	 * Tests that the object guid is copied to the appropriate code for product.
	 */
	@Test
	public void copyObjectGuidToAppropriateCodeForProduct() {
		BaseAmountDTO dto = getNewBaseAmountDtoForTypeTest("PRODUCT");
		assembler.copyObjectGuidToAppropriateCode(dto);
		assertNotNull(dto.getProductCode());
		assertNull(dto.getSkuCode());
	}

	/**
	 * Tests that the object guid is copied to the appropriate code for sku.
	 */
	@Test
	public void copyObjectGuidToAppropriateCodeForSku() {
		BaseAmountDTO dto = getNewBaseAmountDtoForTypeTest("SKU");
		assembler.copyObjectGuidToAppropriateCode(dto);
		assertNotNull(dto.getSkuCode());
		assertNull(dto.getProductCode());
	}

	private BaseAmountDTO getNewBaseAmountDtoForTypeTest(final String type) {
		BaseAmountDTO dto = new BaseAmountDTO();
		dto.setObjectGuid(OBJ_GUID);
		dto.setObjectType(type);
		return dto;
	}

}
