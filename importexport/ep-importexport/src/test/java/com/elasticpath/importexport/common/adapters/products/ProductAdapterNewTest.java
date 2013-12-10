package com.elasticpath.importexport.common.adapters.products;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.elasticpath.common.dto.DisplayValue;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeGroupAttribute;
import com.elasticpath.domain.attribute.AttributeType;
import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.attribute.impl.AttributeGroupAttributeImpl;
import com.elasticpath.domain.attribute.impl.AttributeImpl;
import com.elasticpath.domain.attribute.impl.AttributeUsageImpl;
import com.elasticpath.domain.attribute.impl.ProductAttributeValueImpl;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.catalog.impl.ProductBundleImpl;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.catalog.impl.ProductTypeImpl;
import com.elasticpath.domain.impl.ElasticPathImpl;
import com.elasticpath.importexport.common.dto.general.PricingMechanismValues;
import com.elasticpath.importexport.common.dto.products.AttributeGroupDTO;
import com.elasticpath.importexport.common.dto.products.ProductAvailabilityDTO;
import com.elasticpath.importexport.common.dto.products.ProductDTO;
import com.elasticpath.importexport.common.dto.products.ProductSkuDTO;
import com.elasticpath.importexport.common.dto.products.SeoDTO;
import com.elasticpath.importexport.common.exception.runtime.PopulationRollbackException;
import com.elasticpath.importexport.common.util.Message;

/**
 * Unit test for {@code ProductAdapter} using new JMock.
 */
@SuppressWarnings("PMD.DontUseElasticPathImplGetInstance")
public class ProductAdapterNewTest {

	private static final String LOCALIZED_ATTRIBUTE_KEY = "anAttribute_en";
	private static final int EXPECTED_PARAMS_LENGTH = 3;
	private static final String PRODUCT_CODE = "ProductGUID";
	private static final String PRODUCT_IMAGE = "productImage.png";
	private static final String PRODUCT_TYPE = "ProductType";
	private static final String TAX_CODE = "GOODS";
	private static final String BRAND = "Nike";

	private final BeanFactory beanFactory = new BeanFactory() {
		@SuppressWarnings("unchecked")
		@Override
		public <T> T getBean(final String name) {
			if (ContextIdNames.ATTRIBUTE_USAGE.equals(name)) {
				return (T) new AttributeUsageImpl();
			}
			return null;
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T> Class<T> getBeanImplClass(final String name) {
			if (ContextIdNames.ATTRIBUTE_USAGE.equals(name)) {
				return (Class<T>) AttributeUsageImpl.class;
			}
			return null;
		}
	};

	@Before
	public void setUp() {
		ElasticPathImpl elasticPath = (ElasticPathImpl) ElasticPathImpl.getInstance();
		elasticPath.setBeanFactory(beanFactory);
	}

	@After
	public void tearDown() {
		ElasticPathImpl elasticPath = (ElasticPathImpl) ElasticPathImpl.getInstance();
		elasticPath.setBeanFactory(null);
	}

	/**
	 * Tests that if the product has attributes that are not in the product type
	 * that an exception is thrown.
	 */
	@Test
	public void testProductHasAttributesNotInProductType() {
		ProductAdapter adapter = new ProductAdapter();

		Set<AttributeGroupAttribute> productAttributeGroupAttributes = new HashSet<AttributeGroupAttribute>();

		ProductType productType = new ProductTypeImpl();
		productType.setProductAttributeGroupAttributes(productAttributeGroupAttributes);
		productType.setName("Telescope");
		Product product = new ProductImpl();

		Attribute attribute = new AttributeImpl();
		attribute.setKey("anAttribute");

		Map<String, AttributeValue> attributeValueMap = new HashMap<String, AttributeValue>();
		AttributeValue attributeValue = new ProductAttributeValueImpl();
		attributeValue.setAttribute(attribute);
		attributeValue.setAttributeType(AttributeType.INTEGER);
		attributeValue.setLocalizedAttributeKey(LOCALIZED_ATTRIBUTE_KEY);
		attributeValue.setValue(1);
		attributeValueMap.put(LOCALIZED_ATTRIBUTE_KEY, attributeValue);

		product.setAttributeValueMap(attributeValueMap);
		product.setProductType(productType);
		product.setCode("productCode");

		boolean expectedExceptionThrown = false;
		try {
			adapter.validateProductAttributes(product);
		} catch (PopulationRollbackException e) {
			expectedExceptionThrown = true;
			Message message = e.getIEMessage();
			assertEquals("IE-10328", message.getCode());
			String[] params = message.getParams();
			assertEquals(EXPECTED_PARAMS_LENGTH, params.length);
			assertEquals("productCode", params[0]);
			assertEquals("anAttribute", params[1]);
			assertEquals("Telescope", params[2]);
		}
		assertTrue("Expect the PopulationRollbackException", expectedExceptionThrown);
	}

	/**
	 * Tests that if the product has attributes that in the product type
	 * that an exception is not thrown.
	 */
	@Test
	public void testProductHasAttributesInProductType() {
		ProductAdapter adapter = new ProductAdapter();

		Attribute attribute = new AttributeImpl();
		attribute.setKey(LOCALIZED_ATTRIBUTE_KEY);

		Set<AttributeGroupAttribute> productAttributeGroupAttributes = new HashSet<AttributeGroupAttribute>();
		AttributeGroupAttribute attributeGroupAttribute = new AttributeGroupAttributeImpl();
		attributeGroupAttribute.setAttribute(attribute);
		productAttributeGroupAttributes.add(attributeGroupAttribute);

		ProductType productType = new ProductTypeImpl();
		productType.setProductAttributeGroupAttributes(productAttributeGroupAttributes);
		productType.setName("Telescope");
		Product product = new ProductImpl();

		Map<String, AttributeValue> attributeValueMap = new HashMap<String, AttributeValue>();
		AttributeValue attributeValue = new ProductAttributeValueImpl();
		attributeValue.setAttribute(attribute);
		attributeValue.setAttributeType(AttributeType.INTEGER);
		attributeValue.setLocalizedAttributeKey(LOCALIZED_ATTRIBUTE_KEY);
		attributeValue.setValue(1);
		attributeValueMap.put(LOCALIZED_ATTRIBUTE_KEY, attributeValue);

		product.setAttributeValueMap(attributeValueMap);
		product.setProductType(productType);
		product.setCode("productCode");

		adapter.validateProductAttributes(product);

	}

	/**
	 * Tests that if the pricing mechanism is correctly injected,
	 *  if none is specified in the XML.
	 */
	@Test
	public void testProductBundlePricingMechanismDefault() {
		final ProductAdapter adapter = new ProductAdapter();
		ProductDTO productDto = createAndInitializeProductDto();
		//product is a bundle
		productDto.setBundle(true);
		Product productDomain =  new ProductBundleImpl();
		// ((ProductBundle) productDomain).setCalculated(false);

		adapter.populatePricingMechanismForBundle(productDto, productDomain);
		assertFalse("Expect that defaultMechanism to be 'assigned'", ((ProductBundle) productDomain).isCalculated());
	}

	/**
	 * Tests that if the pricing mechanism is correctly injected,
	 *  if 'calculated' is specified in the XML.
	 */
	@Test
	public void testProductBundlePricingMechanismCalculated() {
		final ProductAdapter adapter = new ProductAdapter();
		ProductDTO productDto = createAndInitializeProductDto();
		//product is a bundle
		productDto.setBundle(true);
		productDto.setPricingMechanism(PricingMechanismValues.CALCULATED);
		Product productDomain =  new ProductBundleImpl();
		//((ProductBundle) productDomain).setCalculated(false);

		adapter.populatePricingMechanismForBundle(productDto, productDomain);
		assertTrue("Expect that defaultMechanism to be 'calculated'", ((ProductBundle) productDomain).isCalculated());
	}

	private ProductDTO createAndInitializeProductDto() {
		ProductDTO productDto = new ProductDTO();
		productDto.setCode(PRODUCT_CODE);
		productDto.setImage(PRODUCT_IMAGE);
		productDto.setType(PRODUCT_TYPE);
		productDto.setTaxCode(TAX_CODE);
		productDto.setBrand(BRAND);
		SeoDTO seoDto = new SeoDTO();
		productDto.setSeoDto(seoDto);
		ProductAvailabilityDTO productAvailability = new ProductAvailabilityDTO();
		productDto.setProductAvailability(productAvailability);
		AttributeGroupDTO productAttributes = new AttributeGroupDTO();
		productDto.setProductAttributes(productAttributes);
		productDto.setNameValues(new ArrayList<DisplayValue>());
		productDto.setProductSkus(new ArrayList<ProductSkuDTO>());

		return productDto;
	}
}
