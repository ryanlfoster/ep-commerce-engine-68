package com.elasticpath.test.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Locale;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.domain.asset.ImageMap;
import com.elasticpath.domain.asset.ImageMapWithAbsolutePath;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.service.asset.impl.ProductImageDiscoveryServiceImpl;
import com.elasticpath.service.catalog.ProductService;
import com.elasticpath.test.db.DbTestCase;
import com.elasticpath.test.persister.CatalogTestPersister;
import com.elasticpath.test.persister.SettingsTestPersister;
import com.elasticpath.test.persister.TaxTestPersister;

/**
 * Test that {@link ProductImageDiscoveryServiceImpl} behaves as expected.
 */
public class ProductImageDiscoveryServiceImplTest extends DbTestCase {

	private CatalogTestPersister catalogTestPersister;
	private SettingsTestPersister settingsTestPersister;
	private TaxCode taxCode;
	@Autowired private ProductService productService;
	
	@Autowired private ProductImageDiscoveryServiceImpl service;

	/**
	 * Setup required for each test.
	 *
	 * @throws Exception the exception
	 */
	@Before
	public void setUp() throws Exception {
		catalogTestPersister = persisterFactory.getCatalogTestPersister();
		settingsTestPersister = persisterFactory.getSettingsTestPersister();
		taxCode = persisterFactory.getTaxTestPersister().getTaxCode(TaxTestPersister.TAX_CODE_GOODS);
	}

	/**
	 * Test the behaviour of getImageMap by product.
	 */
	@DirtiesDatabase
	@Test
	public void testGetImageMapProduct() {
		Product product = createProduct();
		
		ImageMap imageMap = service.getImageMap(product);
		Set<String> imageKeys = imageMap.getImageKeys();
		assertEquals("There should be 3 image keys", 3, imageKeys.size());
		assertEquals("There should be 3 keys for English", 3, imageMap.getImageKeys(Locale.ENGLISH).size());
		assertEquals("There should be 3 keys for English Canadian", 3, imageMap.getImageKeys(new Locale("en", "CA")).size());
		assertEquals("There should be 2 keys for French Canadian", 2, imageMap.getImageKeys(Locale.CANADA_FRENCH).size());
		assertEquals("There should be 1 key for other languages", 1, imageMap.getImageKeys(Locale.GERMAN).size());
		
		assertTrue("There should be images for English", imageMap.hasImages(Locale.ENGLISH));
		assertTrue("There should be images for French Canadian", imageMap.hasImages(Locale.CANADA_FRENCH));
		assertTrue("There should be images for even German, since we do have a default image!", imageMap.hasImages(Locale.GERMAN));
		
		assertEquals("The image for the thumbnail should point to thumbnail.jpg", "thumbnail.jpg", imageMap.getImagePath("thumbnail", Locale.ENGLISH));

		assertEquals("There should be a default image", "defaultImage.jpg", imageMap.getImagePath("defaultImage", Locale.ENGLISH));
		assertEquals("The english image should be found", "mainImage.jpg", imageMap.getImagePath("mainImage", Locale.ENGLISH));
		assertEquals("The more specific image should be found", "mainImageCanada.jpg", imageMap.getImagePath("mainImage", new Locale("en", "CA")));
		assertEquals("There should be fallback if an image doesn't exist for a specific locale/country", "mainImage.jpg",
				imageMap.getImagePath("mainImage", new Locale("en", "AU")));
	}

	/**
	 * Test the behaviour of getImageMap by product code.
	 */
	@DirtiesDatabase
	@Test
	public void testGetImageMapProductCode() {
		Product product = createProduct();
		
		ImageMap imageMap = service.getImageMapByCode(product.getCode());
		Set<String> imageKeys = imageMap.getImageKeys();
		assertEquals("There should be 3 image keys", 3, imageKeys.size());
		assertTrue("There should be images for English", imageMap.hasImages(Locale.ENGLISH));
		assertTrue("There should be images for French Canadian", imageMap.hasImages(Locale.CANADA_FRENCH));
		assertTrue("There should be images for even German, since we do have a default image!", imageMap.hasImages(Locale.GERMAN));
		
		assertEquals("The image for the thumbnail should point to thumbnail.jpg", "thumbnail.jpg", imageMap.getImagePath("thumbnail", Locale.ENGLISH));
		
		assertEquals("There should be a default image", "defaultImage.jpg", imageMap.getImagePath("defaultImage", Locale.ENGLISH));
		assertEquals("The more specific image should be found", "mainImageCanada.jpg", imageMap.getImagePath("mainImage", new Locale("en", "CA")));
	}
	
	@DirtiesDatabase
	@Test
	public void testGetAbsolutePathImageMap() {
		final Product product = createProduct();
		
		final String storeCode = scenario.getStore().getCode();
		settingsTestPersister.updateSettingValue("COMMERCE/STORE/ASSETS/assetServerBaseUrl", storeCode, "http://mobee.elasticpath.com:8080");
		
		ImageMapWithAbsolutePath imageMapWithAbsolutePath = service.absolutePathsForImageMap(service.getImageMap(product), storeCode);
		
		assertEquals("There should be 3 image keys", 3, imageMapWithAbsolutePath.getImageKeys().size());
		assertEquals("The relative path should be untouched", "mainImage.jpg", imageMapWithAbsolutePath.getImagePath("mainImage", Locale.ENGLISH));
		assertEquals("The absolute image should be absolute", "http://mobee.elasticpath.com:8080/mainImage.jpg",
				imageMapWithAbsolutePath.getImageAbsolutePath("mainImage", Locale.ENGLISH));
	}
	
	/**
	 * Creates the product.
	 *
	 * @return the product
	 */
	private Product createProduct() {
		Attribute mainImageAttribute = catalogTestPersister.persistAttribute(scenario.getCatalog().getCode(),
				"mainImage", "Main Image", "Product", "AttributeType_Image", true, true, false);
		Attribute thumbnailAttribute = catalogTestPersister.persistAttribute(scenario.getCatalog().getCode(),
				"thumbnail", "Thumbnail Image", "Product", "AttributeType_Image", true, true, false);
		ProductType productType = catalogTestPersister.persistDefaultSingleSkuProductType(scenario.getCatalog());
		productType = catalogTestPersister.assignProductAttributesToProductType(new String[] { "mainImage", "thumbnail" }, productType.getName());
		Product product = catalogTestPersister.createSimpleProduct(productType.getName(), "PROD1", scenario.getCatalog(), taxCode, scenario.getCategory());
		product.getAttributeValueGroup().setStringAttributeValue(mainImageAttribute, Locale.ENGLISH, "mainImage.jpg");
		product.getAttributeValueGroup().setStringAttributeValue(mainImageAttribute, new Locale("en", "CA"), "mainImageCanada.jpg");
		product.getAttributeValueGroup().setStringAttributeValue(mainImageAttribute, Locale.CANADA_FRENCH, "mainImageCanadaFrench.jpg");
		product.getAttributeValueGroup().setStringAttributeValue(thumbnailAttribute, Locale.ENGLISH, "thumbnail.jpg");
		product.setImage("defaultImage.jpg");
		product = productService.saveOrUpdate(product);
		return product;
	}

}
