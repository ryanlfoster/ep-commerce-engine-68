package com.elasticpath.importexport.importer.importers.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.attribute.impl.ProductAttributeValueImpl;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.adapters.pricing.TestAdapterHelper;
import com.elasticpath.importexport.common.dto.products.ProductDTO;
import com.elasticpath.importexport.common.dto.products.ProductSkuDTO;
import com.elasticpath.importexport.common.exception.runtime.ImportRuntimeException;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.importer.configuration.ImportConfiguration;
import com.elasticpath.importexport.importer.configuration.ImporterConfiguration;
import com.elasticpath.importexport.importer.context.ImportContext;
import com.elasticpath.importexport.importer.importers.CollectionsStrategy;
import com.elasticpath.importexport.importer.importers.SavingStrategy;
import com.elasticpath.persistence.api.LoadTuner;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.persistence.dao.ProductDao;

/**
 * Test for <code>ProductExporterImpl</code>. 
 */
public class ProductImporterImplTest {
	
	private static final String SKU2 = "sku2";

	private ProductImporterImpl productImporter;
	
	private TestAdapterHelper helper;
	
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private SavingStrategy<Product, ProductDTO> mockSavingStrategy;
	
	private ProductDao mockProductDao;
	
	private ProductDTO productDTO;
	
	private Product product;

	/**
	 *
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		helper = new TestAdapterHelper();
		productDTO = helper.createProductDTO();
		product = helper.createProduct();
		productImporter = new ProductImporterImpl();
		mockSavingStrategy = context.mock(SavingStrategy.class);
		mockProductDao = context.mock(ProductDao.class);
		context.checking(new Expectations() {
			{
				allowing(mockSavingStrategy).setDomainAdapter(with(aNull(DomainAdapter.class)));
				allowing(mockSavingStrategy).setLifecycleListener(with(any(LifecycleListener.class)));
				allowing(mockSavingStrategy).populateAndSaveObject(with(any(Product.class)), with(any(ProductDTO.class)));
				allowing(mockSavingStrategy).getSavingManager();
				allowing(mockSavingStrategy).setCollectionsStrategy(with(any(CollectionsStrategy.class)));
				allowing(mockSavingStrategy).setSavingManager(with(any(SavingManager.class)));
				allowing(mockSavingStrategy).isImportRequired(with(any(Persistable.class)));
				will(returnValue(true));
				allowing(mockProductDao).findByGuid(with(any(String.class)), with(aNull(LoadTuner.class)));
				will(returnValue(product));
			}
		});
		productImporter.setProductDao(mockProductDao);
		productImporter.setStatusHolder(new ImportStatusHolder());
	}

	/**
	 * Check an import with non-initialized importer.
	 */
	@Test(expected = ImportRuntimeException.class)
	public void testExecuteNonInitializedImport() {
		productImporter.executeImport(productDTO);
	}
	
	/**
	 * Check an import of one product.
	 */
	@Test
	public void testExecuteImport() {
		ImportConfiguration importConfiguration = new ImportConfiguration();
		importConfiguration.setImporterConfigurationMap(new HashMap<JobType, ImporterConfiguration>());
		productImporter.initialize(new ImportContext(importConfiguration), mockSavingStrategy);
		productImporter.executeImport(productDTO);
		assertEquals("product", productImporter.getImportedObjectName());
		assertNotNull(productImporter.getProductDao());
		assertNotNull(productImporter.getSavingStrategy());
	}
	
	/**
	 * Test different collections strategies (more precisely check clear collections for sku, for product attributes and for sku attributes).
	 */
	@Test
	public void testCollectionsStrategy() {
		ImportConfiguration importConfiguration = new ImportConfiguration();
		importConfiguration.setImporterConfigurationMap(new HashMap<JobType, ImporterConfiguration>());
		productImporter.initialize(new ImportContext(importConfiguration), mockSavingStrategy);
		
		CollectionsStrategy<Product, ProductDTO> collectionsStrategy = productImporter.getCollectionsStrategy();
		ProductSku productSku1 = new ProductSkuImpl();
		productSku1.setSkuCode("sku1");
		productSku1.setGuid("sku1");
		HashMap<String, AttributeValue> prSkuAttr1 = new HashMap<String, AttributeValue>();
		productSku1.setAttributeValueMap(prSkuAttr1);
		product.addOrUpdateSku(productSku1);
		
		ProductSku productSku2 = new ProductSkuImpl();
		productSku2.setSkuCode(SKU2);
		productSku2.setGuid(SKU2);
		HashMap<String, AttributeValue> prSkuAttr2 = new HashMap<String, AttributeValue>();
		prSkuAttr2.put("aattr", new ProductAttributeValueImpl());
		productSku2.setAttributeValueMap(prSkuAttr2);
		product.addOrUpdateSku(productSku2);
		
		HashMap<String, AttributeValue> prAttr = new HashMap<String, AttributeValue>();
		prAttr.put("aattr", new ProductAttributeValueImpl());
		product.setAttributeValueMap(prAttr);
		
		List<ProductSkuDTO> productSkus = new ArrayList<ProductSkuDTO>();
		ProductSkuDTO productSkuDTO2 = new ProductSkuDTO();
		productSkuDTO2.setSkuCode(SKU2);
		productSkuDTO2.setGuid(SKU2);
		ProductSkuDTO productSkuDTO3 = new ProductSkuDTO();
		productSkuDTO3.setSkuCode("sku3");
		productSkuDTO3.setGuid("sku3");
		productSkus.add(productSkuDTO2);
		productSkus.add(productSkuDTO3);
		productDTO.setProductSkus(productSkus);
		
		//check clear collections
		collectionsStrategy.prepareCollections(product, productDTO);
		assertTrue(collectionsStrategy.isForPersistentObjectsOnly());
		assertEquals(product.getProductSkus().size(), 1);
		ProductSku prSkuActual = product.getProductSkus().values().iterator().next();
		assertEquals(prSkuActual, productSku2);
		assertTrue(prSkuActual.getAttributeValueMap().isEmpty());
		assertTrue(product.getAttributeValueMap().isEmpty());
	}

	/** The import classes should at least contain the DTO class we are operating on. */
	@Test
	public void testDtoClass() {
		assertEquals("Incorrect DTO class", ProductDTO.class, productImporter.getDtoClass());
	}

	/** The auxiliary JAXB class list must not be null (can be empty). */
	@Test
	public void testAuxiliaryJaxbClasses() {
		assertNotNull(productImporter.getAuxiliaryJaxbClasses());
	}
}
