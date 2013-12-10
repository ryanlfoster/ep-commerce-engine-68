package com.elasticpath.service.asset.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.asset.ImageMap;
import com.elasticpath.domain.asset.impl.ImageMapImpl;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeType;
import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.attribute.impl.AttributeImpl;
import com.elasticpath.domain.attribute.impl.ProductAttributeValueImpl;
import com.elasticpath.domain.attribute.impl.SkuAttributeValueImpl;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.service.asset.ImageDiscoveryService;
import com.elasticpath.service.catalog.ProductSkuService;

/**
 * Test that {@link SkuImageDiscoveryServiceImpl} behaves as expected.
 */
public class SkuImageDiscoveryServiceImplTest {

	private static final String ONE_KEY_EXPECTED = "There should be 1 key in the map";

	private static final String DEFAULT_KEY = "defaultSkuImage";

	private static final String DEFAULT_IMAGE = "default.jpg";

	private static final String IMAGE_PATH = "image.jpg";

	private static final String IMAGE_KEY = "someImage";

	private static final String ATTRIBUTE_KEY_PRODUCT_IMAGE_SMALL = "productImageSmall";

	private static final String DEFAULT_PRODUCT_IMAGE_KEY = "defaultImage";

	private static final String DEFAULT_PRODUCT_IMAGE = "defaultProductImage.jpg";

	private static final String PRODUCT_ATTRIBUTE_IMAGE = "productAttributeImage.jpg";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private BeanFactory beanFactory;

	private ProductSkuService productSkuService;

	private ImageDiscoveryService<ProductSku> service;

	/**
	 * Setup required for each test.
	 *
	 * @throws Exception the exception
	 */
	@Before
	public void setUp() throws Exception {
		beanFactory = context.mock(BeanFactory.class);
		productSkuService = context.mock(ProductSkuService.class);
		service = new SkuImageDiscoveryServiceImpl();
		((SkuImageDiscoveryServiceImpl) service).setBeanFactory(beanFactory);
		((SkuImageDiscoveryServiceImpl) service).setProductSkuService(productSkuService);

		context.checking(new Expectations() {
			{
				oneOf(beanFactory).getBean(ContextIdNames.IMAGE_MAP);
				will(returnValue(new ImageMapImpl()));
			}
		});
	}

	/**
	 * Test the behaviour of get image map no images.
	 */
	@Test
	public void testGetImageMapNoImages() {
		ProductSku sku = createSku();
		ImageMap imageMap = service.getImageMap(sku);
		assertNotNull("The image map should exist", imageMap);
		assertTrue("There should be no images in the map", imageMap.getImageKeys().isEmpty());
	}

	/**
	 * Test the behaviour of get image map with image from field.
	 */
	@Test
	public void testGetImageMapWithImageFromField() {
		ProductSku sku = createSku();
		sku.setImage(DEFAULT_IMAGE);
		ImageMap imageMap = service.getImageMap(sku);
		assertEquals(ONE_KEY_EXPECTED, 1, imageMap.getImageKeys().size());
		assertEquals("The key should match the default key", DEFAULT_KEY, imageMap.getImageKeys().iterator().next());
		assertEquals("The image should be found for a null locale", DEFAULT_IMAGE, imageMap.getImagePath(DEFAULT_KEY, null));
		assertEquals("The image should be found for any actual locale value", DEFAULT_IMAGE, imageMap.getImagePath(DEFAULT_KEY, Locale.ITALIAN));
	}

	/**
	 * Test the behaviour of get image map with non locale dependent image.
	 */
	@Test
	public void testGetImageMapWithNonLocaleDependentImage() {
		final AttributeValue attributeValue = new SkuAttributeValueImpl();
		final Attribute attribute = new AttributeImpl();
		attribute.setLocaleDependant(false);
		attribute.setKey(IMAGE_KEY);
		attributeValue.setAttribute(attribute);
		attributeValue.setAttributeType(AttributeType.IMAGE);
		attributeValue.setStringValue(IMAGE_PATH);

		ProductSku sku = createSku();
		Map<String, AttributeValue> attributeValueMap = new HashMap<String, AttributeValue>();
		attributeValueMap.put(IMAGE_KEY, attributeValue);
		sku.setAttributeValueMap(attributeValueMap);

		ImageMap imageMap = service.getImageMap(sku);
		assertEquals(ONE_KEY_EXPECTED, 1, imageMap.getImageKeys().size());
		assertEquals("The key should match the attribute key", IMAGE_KEY, imageMap.getImageKeys().iterator().next());
		assertEquals("The image should be found for a null locale", IMAGE_PATH, imageMap.getImagePath(IMAGE_KEY, null));
		assertEquals("The image should be found for any actual locale value", IMAGE_PATH, imageMap.getImagePath(IMAGE_KEY, Locale.JAPANESE));
	}

	/**
	 * Test the behaviour of get image map with locale dependent image.
	 */
	@Test
	public void testGetImageMapWithLocaleDependentImage() {
		final AttributeValue attributeValue = new ProductAttributeValueImpl();
		final Attribute attribute = new AttributeImpl();
		attribute.setLocaleDependant(true);
		attribute.setKey(IMAGE_KEY);
		attributeValue.setAttribute(attribute);
		attributeValue.setAttributeType(AttributeType.IMAGE);
		attributeValue.setStringValue(IMAGE_PATH);
		attributeValue.setLocalizedAttributeKey("someImage_en");

		ProductSku sku = createSku();
		Map<String, AttributeValue> attributeValueMap = new HashMap<String, AttributeValue>();
		attributeValueMap.put("someImage_en", attributeValue);
		sku.setAttributeValueMap(attributeValueMap);

		ImageMap imageMap = service.getImageMap(sku);
		assertEquals(ONE_KEY_EXPECTED, 1, imageMap.getImageKeys().size());
		assertEquals("The key should match the attribute key", IMAGE_KEY, imageMap.getImageKeys().iterator().next());
		assertNull("No image should be found for a null locale", imageMap.getImagePath(IMAGE_KEY, null));
		assertEquals("An image should be found for English", IMAGE_PATH, imageMap.getImagePath(IMAGE_KEY, Locale.ENGLISH));
		assertNull("No image should be found for a different locale", imageMap.getImagePath(IMAGE_KEY, Locale.JAPANESE));
	}

	/**
	 * Test the behaviour of get image map with non image attributes.
	 */
	@Test
	public void testGetImageMapWithNonImageAttributes() {
		final AttributeValue attributeValue = new ProductAttributeValueImpl();
		final Attribute attribute = new AttributeImpl();
		attribute.setLocaleDependant(false);
		attribute.setKey(IMAGE_KEY);
		attributeValue.setAttribute(attribute);
		attributeValue.setAttributeType(AttributeType.FILE);
		attributeValue.setStringValue(IMAGE_PATH);

		ProductSku sku = createSku();
		Map<String, AttributeValue> attributeValueMap = new HashMap<String, AttributeValue>();
		attributeValueMap.put(IMAGE_KEY, attributeValue);
		sku.setAttributeValueMap(attributeValueMap);

		ImageMap imageMap = service.getImageMap(sku);
		assertNotNull("The image map should exist", imageMap);
		assertTrue("There should be no images in the map", imageMap.getImageKeys().isEmpty());
	}

	/**
	 * Test the behaviour of get image map by non existent code.
	 */
	@Test
	public void testGetImageMapByNonExistentCode() {
		context.checking(new Expectations() {
			{
				oneOf(productSkuService).findBySkuCode("NoSuchCode");
				will(returnValue(null));
			}
		});
		ImageMap imageMap = service.getImageMapByCode("NoSuchCode");
		assertNotNull("An image map should have been returned", imageMap);
		assertTrue("The imageMap should be empty", imageMap.getImageKeys().isEmpty());
	}

	/**
	 * Test the behaviour of get image map by code.
	 */
	@Test
	public void testGetImageMapByCode() {
		final ProductSku sku = createSku();
		sku.setImage(DEFAULT_IMAGE);
		context.checking(new Expectations() {
			{
				oneOf(productSkuService).findBySkuCode("sku1");
				will(returnValue(sku));
			}
		});
		ImageMap imageMap = service.getImageMapByCode("sku1");
		assertEquals(ONE_KEY_EXPECTED, 1, imageMap.getImageKeys().size());
		assertEquals("The key should match the default key", DEFAULT_KEY, imageMap.getImageKeys().iterator().next());
	}

	/**
	 * Test that get image map does not traverse into the associated product to obtain image candidates.
	 */
	@Test
	public void testGetImageMapDoesNotTraverseIntoAssociatedProduct() {
		final ProductSku sku = createSku();

		sku.setImage(DEFAULT_IMAGE);

		final Product product = sku.getProduct();
		product.setImage(DEFAULT_PRODUCT_IMAGE);

		createImageProductAttributeOnProduct(product, PRODUCT_ATTRIBUTE_IMAGE);

		ImageMap imageMap = service.getImageMap(sku);
		assertEquals(ONE_KEY_EXPECTED, 1, imageMap.getImageKeys().size());
		assertNull("No default product image should be contained within the image map.", imageMap.getImagePath(DEFAULT_PRODUCT_IMAGE_KEY, null));
		assertNull("No product attribute images should be contained within the image map.",
				imageMap.getImagePath(ATTRIBUTE_KEY_PRODUCT_IMAGE_SMALL, null));
	}

	private ProductSku createSku() {
		ProductSku sku = new ProductSkuImpl();
		Product product = new ProductImpl();
		sku.setProduct(product);
		return sku;
	}

	private void createImageProductAttributeOnProduct(final Product product, final String image) {
		Attribute productAttribute = createAttribute(AttributeType.IMAGE, ATTRIBUTE_KEY_PRODUCT_IMAGE_SMALL, true);
		AttributeValue productAttributeValue = createProductAttributeValue(productAttribute, AttributeType.IMAGE, image);
		product.getAttributeValueMap().put(productAttributeValue.getLocalizedAttributeKey(), productAttributeValue);
	}

	private Attribute createAttribute(final AttributeType attributueType, final String key, final boolean localeDependant) {
		Attribute result = new AttributeImpl();
		result = new AttributeImpl();
		result.setAttributeType(attributueType);
		result.setKey(key);
		result.setLocaleDependant(localeDependant);
		return result;
	}

	private AttributeValue createProductAttributeValue(final Attribute attribute, final AttributeType attributeType, final Object value) {
		AttributeValue attributeValue = new ProductAttributeValueImpl();
		attributeValue.setAttribute(attribute);
		attributeValue.setAttributeType(attributeType);
		attributeValue.setValue(value);
		return attributeValue;
	}
}
