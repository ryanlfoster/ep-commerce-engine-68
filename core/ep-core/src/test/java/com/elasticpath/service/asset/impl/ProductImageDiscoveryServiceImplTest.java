package com.elasticpath.service.asset.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
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
import com.elasticpath.commons.util.AssetRepository;
import com.elasticpath.domain.asset.ImageMap;
import com.elasticpath.domain.asset.ImageMapWithAbsolutePath;
import com.elasticpath.domain.asset.impl.ImageMapImpl;
import com.elasticpath.domain.asset.impl.ImageMapWithAbsolutePathImpl;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeType;
import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.attribute.impl.AttributeImpl;
import com.elasticpath.domain.attribute.impl.ProductAttributeValueImpl;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.service.asset.ImageDiscoveryService;
import com.elasticpath.service.query.QueryCriteria;
import com.elasticpath.service.query.QueryResult;
import com.elasticpath.service.query.QueryService;
import com.elasticpath.service.query.impl.QueryResultImpl;

/**
 * Test that {@link ProductImageDiscoveryServiceImpl} behaves as expected.
 */
public class ProductImageDiscoveryServiceImplTest {

	private static final String ONE_KEY_EXPECTED = "There should be 1 key in the map";
	private static final String DEFAULT_KEY = "defaultImage";
	private static final String DEFAULT_IMAGE = "default.jpg";
	private static final String IMAGE_PATH = "image.jpg";
	private static final String IMAGE_KEY = "someImage";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private BeanFactory beanFactory;
	private QueryService<Product> productQueryService;
	private AssetRepository assetRepository;

	private ImageDiscoveryService<Product> service;
	
	/**
	 * Setup required for each test.
	 *
	 * @throws Exception the exception
	 */
	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		beanFactory = context.mock(BeanFactory.class);
		productQueryService = context.mock(QueryService.class);
		assetRepository = context.mock(AssetRepository.class);
		service = new ProductImageDiscoveryServiceImpl();
		((ProductImageDiscoveryServiceImpl) service).setBeanFactory(beanFactory);
		((ProductImageDiscoveryServiceImpl) service).setProductQueryService(productQueryService);
		((ProductImageDiscoveryServiceImpl) service).setAssetRepository(assetRepository);

		context.checking(new Expectations() {
			{
				oneOf(beanFactory).getBean(ContextIdNames.IMAGE_MAP); will(returnValue(new ImageMapImpl()));
			}
		});
	}

	/**
	 * Test the behaviour of get image map no images.
	 */
	@Test
	public void testGetImageMapNoImages() {
		Product product = new ProductImpl();
		ImageMap imageMap = service.getImageMap(product);
		assertNotNull("The image map should exist", imageMap);
		assertTrue("There should be no images in the map", imageMap.getImageKeys().isEmpty());
	}

	/**
	 * Test the behaviour of get image map with image from field.
	 */
	@Test
	public void testGetImageMapWithImageFromField() {
		Product product = new ProductImpl();
		product.setImage(DEFAULT_IMAGE);
		ImageMap imageMap = service.getImageMap(product);
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
		final AttributeValue attributeValue = new ProductAttributeValueImpl();
		final Attribute attribute = new AttributeImpl();
		attribute.setLocaleDependant(false);
		attribute.setKey(IMAGE_KEY);
		attributeValue.setAttribute(attribute);
		attributeValue.setAttributeType(AttributeType.IMAGE);
		attributeValue.setStringValue(IMAGE_PATH);
		
		Product product = new ProductImpl();
		Map<String, AttributeValue> attributeValueMap = new HashMap<String, AttributeValue>();
		attributeValueMap.put(IMAGE_KEY, attributeValue);
		product.setAttributeValueMap(attributeValueMap);
		
		ImageMap imageMap = service.getImageMap(product);
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
		
		Product product = new ProductImpl();
		Map<String, AttributeValue> attributeValueMap = new HashMap<String, AttributeValue>();
		attributeValueMap.put("someImage_en", attributeValue);
		product.setAttributeValueMap(attributeValueMap);
		
		ImageMap imageMap = service.getImageMap(product);
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

		Product product = new ProductImpl();
		Map<String, AttributeValue> attributeValueMap = new HashMap<String, AttributeValue>();
		attributeValueMap.put(IMAGE_KEY, attributeValue);
		product.setAttributeValueMap(attributeValueMap);
		
		ImageMap imageMap = service.getImageMap(product);
		assertNotNull("The image map should exist", imageMap);
		assertTrue("There should be no images in the map", imageMap.getImageKeys().isEmpty());
	}
	
	/**
	 * Test the behaviour of get image map by non existent code.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testGetImageMapByNonExistentCode() {
		final QueryResult<Product> emptyResult = new QueryResultImpl<Product>();
		context.checking(new Expectations() {
			{
				oneOf(productQueryService).query(with(any(QueryCriteria.class))); will(returnValue(emptyResult));
			}
		});
		ImageMap imageMap = service.getImageMapByCode("NoSuchCode");
		assertNotNull("An image map should have been returned", imageMap);
		assertTrue("The imageMap should be empty", imageMap.getImageKeys().isEmpty());
	}
	
	/**
	 * Test the behaviour of get image map by code.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testGetImageMapByCode() {
		final QueryResultImpl<Product> queryResult = new QueryResultImpl<Product>();
		Product product = new ProductImpl();
		product.setImage(DEFAULT_IMAGE);
		queryResult.setResults(Arrays.asList(product));
		context.checking(new Expectations() {
			{
				oneOf(productQueryService).query(with(any(QueryCriteria.class))); will(returnValue(queryResult));
			}
		});
		ImageMap imageMap = service.getImageMapByCode("Product1");
		assertEquals(ONE_KEY_EXPECTED, 1, imageMap.getImageKeys().size());
		assertEquals("The key should match the default key", DEFAULT_KEY, imageMap.getImageKeys().iterator().next());
	}

	/**
	 * Test the behaviour of absolute paths for image map.
	 *
	 * @throws MalformedURLException the malformed url exception
	 */
	@Test
	public void testAbsolutePathsForImageMap() throws MalformedURLException {
		Product product = new ProductImpl();
		product.setImage(DEFAULT_IMAGE);
		context.checking(new Expectations() {
			{
				oneOf(beanFactory).getBean(ContextIdNames.IMAGE_MAP_WITH_ABSOLUTE_PATH); will(returnValue(new ImageMapWithAbsolutePathImpl()));
				oneOf(assetRepository).getAssetServerImagesUrl("STORE1"); will(returnValue(new URL("http://mobee.elasticpath.com/")));
			}
		});
		ImageMapWithAbsolutePath imageMapWithAbsolutePath = service.absolutePathsForImageMap(service.getImageMap(product), "STORE1");
		assertNotNull("The absolute image path map should exist", imageMapWithAbsolutePath);
		assertEquals("The relative image path should be relative", DEFAULT_IMAGE, imageMapWithAbsolutePath.getImagePath(DEFAULT_KEY, null));
		assertEquals("The absolute image path should be absolute", "http://mobee.elasticpath.com/default.jpg",
				imageMapWithAbsolutePath.getImageAbsolutePath(DEFAULT_KEY, null));
	}
}
