package com.elasticpath.test.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.domain.asset.ImageMap;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.service.asset.impl.SkuImageDiscoveryServiceImpl;
import com.elasticpath.service.catalog.ProductService;
import com.elasticpath.test.db.DbTestCase;
import com.elasticpath.test.persister.CatalogTestPersister;
import com.elasticpath.test.persister.TaxTestPersister;

/**
 * Test that {@link SkuImageDiscoveryServiceImpl} behaves as expected.
 */
public class SkuImageDiscoveryServiceImplTest extends DbTestCase {

	private CatalogTestPersister catalogTestPersister;
	private TaxCode taxCode;
	@Autowired private ProductService productService;

	@Autowired private SkuImageDiscoveryServiceImpl service;

	/**
	 * Setup required for each test.
	 *
	 * @throws Exception the exception
	 */
	@Before
	public void setUp() throws Exception {
		catalogTestPersister = getTac().getPersistersFactory().getCatalogTestPersister();
		taxCode = getTac().getPersistersFactory().getTaxTestPersister().getTaxCode(TaxTestPersister.TAX_CODE_GOODS);
	}

	/**
	 * Test the behaviour of get image map product sku.
	 */
	@DirtiesDatabase
	@Test
	public void testGetImageMapProductSku() {
		ProductSku sku = createProduct().getSkuByCode("SKU1");
		ImageMap imageMap = service.getImageMap(sku);
		Set<String> imageKeys = imageMap.getImageKeys();
		assertEquals("There should be 3 image keys", 3, imageKeys.size());
		assertTrue("There should be images for English", imageMap.hasImages(Locale.ENGLISH));
		assertEquals("There should be 3 image keys for English", 3, imageMap.getImageKeys(Locale.ENGLISH).size());
		assertTrue("There should be images for French Canadian", imageMap.hasImages(Locale.CANADA_FRENCH));
		assertEquals("There should be 1 image key for French Canadian", 1, imageMap.getImageKeys(Locale.CANADA_FRENCH).size());
		assertTrue("There should be images for even German, since we do have a default image!", imageMap.hasImages(Locale.GERMAN));
		assertEquals("There should be 1 image key for German", 1, imageMap.getImageKeys(Locale.GERMAN).size());

		assertNull("The there should be no image for the thumbnail", imageMap.getImagePath("thumbnail", Locale.ENGLISH));

		assertNull("There should not be a default product image", imageMap.getImagePath("defaultImage", Locale.ENGLISH));
		assertEquals("There should be a default sku image", "defaultSku1Image.jpg", imageMap.getImagePath("defaultSkuImage", null));

		assertNull("No product images should be found", imageMap.getImagePath("mainImage", new Locale("en", "CA")));
		assertEquals("The main sku image should be found", "sku1Image.jpg", imageMap.getImagePath("skuImage", Locale.ENGLISH));
		assertEquals("The sku thumbnail should be found with locale fallback", "sku1Thumbnail.jpg", imageMap.getImagePath("skuThumbnail", new Locale("en", "CA")));
	}

	/**
	 * Test the behaviour of get image map product sku no default.
	 */
	@DirtiesDatabase
	@Test
	public void testGetImageMapProductSkuNoDefault() {
		ProductSku sku = createProduct().getSkuByCode("SKU2");
		ImageMap imageMap = service.getImageMap(sku);
		Set<String> imageKeys = imageMap.getImageKeys();
		assertEquals("There should be 2 image keys", 2, imageKeys.size());
		assertTrue("There should be images for English", imageMap.hasImages(Locale.ENGLISH));
		assertEquals("There should be 1 image key for English", 1, imageMap.getImageKeys(Locale.ENGLISH).size());
		assertFalse("There should be no images for French Canadian", imageMap.hasImages(Locale.CANADA_FRENCH));
		assertEquals("There should be no image keys for French Canadian", 0, imageMap.getImageKeys(Locale.CANADA_FRENCH).size());
		assertFalse("There should not be images for German, since we do not have a default image!", imageMap.hasImages(Locale.GERMAN));
		assertTrue("There should be no image keys for German", imageMap.getImageKeys(Locale.GERMAN).isEmpty());

		assertNull("There should not be a default product image", imageMap.getImagePath("defaultImage", Locale.ENGLISH));
		assertNull("There should not be a default sku image", imageMap.getImagePath("defaultSkuImage", null));

		assertNull("The main sku image should not be found for English", imageMap.getImagePath("skuImage", Locale.ENGLISH));
		assertEquals("The sku thumbnail should be found for English", "sku2Thumbnail.jpg", imageMap.getImagePath("skuThumbnail", Locale.ENGLISH));
	}

	/**
	 * Test the behaviour of get image map by code.
	 */
	@DirtiesDatabase
	@Test
	public void testGetImageMapByCode() {
		createProduct();
		ImageMap imageMap = service.getImageMapByCode("SKU1");
		Set<String> imageKeys = imageMap.getImageKeys();
		assertEquals("There should be 3 image keys", 3, imageKeys.size());
		assertEquals("The main sku image should be found", "sku1Image.jpg", imageMap.getImagePath("skuImage", Locale.ENGLISH));
		assertEquals("The sku thumbnail should be found with locale fallback", "sku1Thumbnail.jpg", imageMap.getImagePath("skuThumbnail", new Locale("en", "CA")));
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
		Attribute skuImageAttribute = catalogTestPersister.persistAttribute(scenario.getCatalog().getCode(),
				"skuImage", "Sku Image", "SKU", "AttributeType_Image", true, true, false);
		Attribute skuThumbnailAttribute = catalogTestPersister.persistAttribute(scenario.getCatalog().getCode(),
				"skuThumbnail", "SKU Thumbnail Image", "SKU", "AttributeType_Image", true, true, false);

		ProductType productType = catalogTestPersister.persistDefaultMultiSkuProductType(scenario.getCatalog());
		productType = catalogTestPersister.assignProductAttributesToProductType(new String[] { "mainImage", "thumbnail" }, productType.getName());
		productType = catalogTestPersister.assignSkuAttributesToProductType(new String[] { "skuImage", "skuThumbnail" }, productType.getName());

		Product product = catalogTestPersister.createSimpleProduct(productType.getName(), "PROD1", scenario.getCatalog(), taxCode, scenario.getCategory());
		product.getAttributeValueGroup().setStringAttributeValue(mainImageAttribute, Locale.ENGLISH, "mainImage.jpg");
		product.getAttributeValueGroup().setStringAttributeValue(mainImageAttribute, new Locale("en", "CA"), "mainImageCanada.jpg");
		product.getAttributeValueGroup().setStringAttributeValue(mainImageAttribute, Locale.CANADA_FRENCH, "mainImageCanadaFrench.jpg");
		product.getAttributeValueGroup().setStringAttributeValue(thumbnailAttribute, Locale.ENGLISH, "thumbnail.jpg");

		ProductSku sku1 = catalogTestPersister.createProductSku("SKU1", Collections.<SkuOptionValue>emptyList(), new Date(), new Date(), false);
		sku1.getAttributeValueGroup().setStringAttributeValue(skuImageAttribute, Locale.ENGLISH, "sku1Image.jpg");
		sku1.getAttributeValueGroup().setStringAttributeValue(skuThumbnailAttribute, Locale.ENGLISH, "sku1Thumbnail.jpg");
		sku1.setImage("defaultSku1Image.jpg");
		product.addOrUpdateSku(sku1);

		ProductSku sku2 = catalogTestPersister.createProductSku("SKU2", Collections.<SkuOptionValue>emptyList(), new Date(), new Date(), false);
		sku2.getAttributeValueGroup().setStringAttributeValue(skuImageAttribute, new Locale("en", "CA"), "sku2Image.jpg");
		sku2.getAttributeValueGroup().setStringAttributeValue(skuThumbnailAttribute, Locale.ENGLISH, "sku2Thumbnail.jpg");
		product.addOrUpdateSku(sku2);

		product = productService.saveOrUpdate(product);
		return product;
	}
}
