package com.elasticpath.test.integration;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.commons.ThreadLocalMap;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductLoadTuner;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.changeset.ChangeSet;
import com.elasticpath.domain.changeset.impl.ChangeSetImpl;
import com.elasticpath.domain.objectgroup.BusinessObjectGroupMember;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.persistence.api.PersistenceSession;
import com.elasticpath.persistence.api.Transaction;
import com.elasticpath.service.catalog.ProductService;
import com.elasticpath.service.changeset.ChangeSetManagementService;
import com.elasticpath.service.changeset.dao.ChangeSetMemberDao;
import com.elasticpath.service.dataimport.ImportGuidHelper;
import com.elasticpath.test.persister.TestDataPersisterFactory;
import com.elasticpath.test.persister.testscenarios.SimpleStoreScenario;
import com.elasticpath.test.util.Utils;

/**
 * Tests that the ChangeSetPersistenceListener correctly handles changes with BigDecimal fields.
 */
public class ChangeSetPersistenceListenerImplIntegrationTest extends BasicSpringContextTest {

	private static final String EXPECT_TO_FIND_THE_PRODUCT_SKU_IN_THE_CHANGE_SET = "expect to find the ProductSku in the change set";

	private static final String PRODUCT_SKU = "Product SKU";

	private static final String TEST_VALUE = "0.90";

	private static final String PRODUCT = "product";

	private static final String GOODS = "GOODS";

	private static final String MULTI_SKU_PRODUCT = "Multi-Sku Product";

	private static final String NEW_SKU = "newSku";

	@Autowired
	private ProductService productService;
	
	private SimpleStoreScenario scenario;

	private TestDataPersisterFactory persisterFactory;
	
	private static final double PRODUCT_PRICE = 269.00;

	private Product databaseProduct;

	private String skuCode1;

	@Autowired
	private ChangeSetMemberDao changeSetMemberDao;

	@Autowired
	private ImportGuidHelper importGuidHelper;

	@Autowired
	private ThreadLocalMap<String, Object> metadataMap; 

	@Autowired
	private PersistenceEngine persistenceEngine;

	@Autowired
	private ChangeSetManagementService changeSetManagementService;

	/**
	 * Sets up each test.
	 * @throws Exception on failure.
	 */
	@Before
	public void setUp() throws Exception {
		metadataMap.clear();
		
		scenario = getTac().useScenario(SimpleStoreScenario.class);
		
		persisterFactory = getTac().getPersistersFactory();
		
		skuCode1 = Utils.uniqueCode(NEW_SKU);
		String skuCode2 = Utils.uniqueCode(NEW_SKU);
		Product product = this.persisterFactory.getCatalogTestPersister().persistMultiSkuProduct(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse(), BigDecimal.valueOf(PRODUCT_PRICE), TestDataPersisterFactory.DEFAULT_CURRENCY,
				null, Utils.uniqueCode(PRODUCT), MULTI_SKU_PRODUCT, GOODS, null, 0, skuCode1, skuCode2);
				
		// Set the length on the new product 
		final ProductLoadTuner productLoadTuner = getBeanFactory().getBean(ContextIdNames.PRODUCT_LOAD_TUNER);
        productLoadTuner.setLoadingSkus(true);
		databaseProduct = productService.getTuned(product.getUidPk(), productLoadTuner);
	}

	/** Test cleanup. */
	@After
	public void tearDown() {
		metadataMap.clear();
	}
	
	/**
	 * Verify that if an update is made with 1 BigDecimal that is different that the sku appears in the changeset.
	 */	
	@DirtiesDatabase
	@Test
	public void testUpdateSku1BDDifferent() {
		ProductSku productSku = databaseProduct.getProductSkus().get(skuCode1);	
		String skuGuid = productSku.getGuid();
		productSku.setLength(new BigDecimal(TEST_VALUE));
		databaseProduct = productService.saveOrUpdate(databaseProduct);
		
		ChangeSet updatedChangeSet = createNewChangeSet();
				
		prepareMetadataMap(updatedChangeSet);
		
		PersistenceSession session = persistenceEngine.getSharedPersistenceSession();
		Transaction transaction = session.beginTransaction();
		
		// Note that we need to get the sku this way, and not from the product above, so that we are
		// in the correct transaction context.
		ProductSku importProductSku = importGuidHelper.findProductSkuByGuid(skuCode1);		
		importProductSku.setLength(new BigDecimal("7"));
		
		session.update(importProductSku);
		transaction.commit();		
		
		// Expectations check
		boolean expectedMemberFound = isObjectInChangeSet(updatedChangeSet, skuGuid);
		assertTrue(EXPECT_TO_FIND_THE_PRODUCT_SKU_IN_THE_CHANGE_SET, expectedMemberFound);
	}
	
	/**
	 * Verify that if an update is made with 1 BigDecimal that is the same that the sku does not appear in the changeset.
	 */	
	@DirtiesDatabase
	@Test
	public void testUpdateSku1BDSame() {
		
		ProductSku productSku = databaseProduct.getProductSkus().get(skuCode1);		
		productSku.setLength(new BigDecimal(TEST_VALUE));
		databaseProduct = productService.saveOrUpdate(databaseProduct);
		String skuGuid = productSku.getGuid();
		ChangeSet updatedChangeSet = createNewChangeSet();
				
		prepareMetadataMap(updatedChangeSet);
		
		PersistenceSession session = persistenceEngine.getSharedPersistenceSession();
		Transaction transaction = session.beginTransaction();
		
		// Note that we need to get the sku this way, and not from the product above, so that we are
		// in the correct transaction context.
		ProductSku importProductSku = importGuidHelper.findProductSkuByGuid(skuCode1);
		importProductSku.setLength(new BigDecimal(TEST_VALUE));
		
		session.update(importProductSku);
		transaction.commit();		
		
		boolean expectedMemberFound = isObjectInChangeSet(updatedChangeSet, skuGuid);
		assertFalse("expect to *not* find the ProductSku in the change set", expectedMemberFound);
	}
	
	/**
	 * Verify that if an update is made with no change that the sku does not appear in the changeset.
	 */	
	@DirtiesDatabase
	@Test
	public void testUpdateSkuNoChange() {
		
		ProductSku productSku = databaseProduct.getProductSkus().get(skuCode1);		
		productSku.setLength(new BigDecimal(TEST_VALUE));
		databaseProduct = productService.saveOrUpdate(databaseProduct);
		String skuGuid = productSku.getGuid();
		ChangeSet updatedChangeSet = createNewChangeSet();
				
		prepareMetadataMap(updatedChangeSet);
		
		PersistenceSession session = persistenceEngine.getSharedPersistenceSession();
		Transaction transaction = session.beginTransaction();
		
		// Note that we need to get the sku this way, and not from the product above, so that we are
		// in the correct transaction context.
		ProductSku importProductSku = importGuidHelper.findProductSkuByGuid(skuCode1);
		
		session.update(importProductSku);
		transaction.commit();		
		
		boolean expectedMemberFound = isObjectInChangeSet(updatedChangeSet, skuGuid);
		assertFalse("expect to *not* find the ProductSku in the change set", expectedMemberFound);
	}
	
	/**
	 * Verify that if an update is made with 1 BigDecimal that differs only in scale that the sku does not appear in the changeset.
	 */	
	@DirtiesDatabase
	@Test
	public void testUpdateSku1BDOnlyScaleDifferent() {
						
		ProductSku productSku = databaseProduct.getProductSkus().get(skuCode1);		
		productSku.setLength(new BigDecimal(TEST_VALUE));
		databaseProduct = productService.saveOrUpdate(databaseProduct);
		String skuGuid = productSku.getGuid();
		ChangeSet updatedChangeSet = createNewChangeSet();
				
		prepareMetadataMap(updatedChangeSet);
		
		PersistenceSession session = persistenceEngine.getSharedPersistenceSession();
		Transaction transaction = session.beginTransaction();
		
		// Note that we need to get the sku this way, and not from the product above, so that we are
		// in the correct transaction context.
		ProductSku importProductSku = importGuidHelper.findProductSkuByGuid(skuCode1);
		importProductSku.setLength(new BigDecimal("0.9"));
		
		session.update(importProductSku);
		transaction.commit();		
		
		boolean expectedMemberFound = isObjectInChangeSet(updatedChangeSet, skuGuid);
		// Is the reverse on Oracle
		assertTrue(EXPECT_TO_FIND_THE_PRODUCT_SKU_IN_THE_CHANGE_SET, expectedMemberFound);
	}
	
	/**
	 * Verify that if an update is made with 2 BigDecimals that are different that the sku appears in the changeset.
	 */	
	@DirtiesDatabase
	@Test
	public void testUpdateSku2BDsDifferent() {
						
		ProductSku productSku = databaseProduct.getProductSkus().get(skuCode1);		
		productSku.setLength(new BigDecimal(TEST_VALUE));
		productSku.setWidth(new BigDecimal("1.5"));
		databaseProduct = productService.saveOrUpdate(databaseProduct);
		String skuGuid = productSku.getGuid();
		ChangeSet updatedChangeSet = createNewChangeSet();
				
		prepareMetadataMap(updatedChangeSet);
		
		PersistenceSession session = persistenceEngine.getSharedPersistenceSession();
		Transaction transaction = session.beginTransaction();
		
		// Note that we need to get the sku this way, and not from the product above, so that we are
		// in the correct transaction context.
		ProductSku importProductSku = importGuidHelper.findProductSkuByGuid(skuCode1);
		importProductSku.setLength(new BigDecimal("7"));
		importProductSku.setLength(new BigDecimal("9.5"));
		
		session.update(importProductSku);
		transaction.commit();		
		
		boolean expectedMemberFound = isObjectInChangeSet(updatedChangeSet, skuGuid);
		assertTrue(EXPECT_TO_FIND_THE_PRODUCT_SKU_IN_THE_CHANGE_SET, expectedMemberFound);
	}
	
	/**
	 * Verify that if an update is made with 2 BigDecimals, 1 the same, 1 different only in scale, that the sku does not appear in the changeset.
	 */	
	@DirtiesDatabase
	@Test
	public void testUpdateSku2BDs1SameInScale() {
						
		ProductSku productSku = databaseProduct.getProductSkus().get(skuCode1);		
		productSku.setLength(new BigDecimal(TEST_VALUE));
		productSku.setWidth(new BigDecimal("1.5"));
		databaseProduct = productService.saveOrUpdate(databaseProduct);
		String skuGuid = productSku.getGuid();
		ChangeSet updatedChangeSet = createNewChangeSet();
				
		prepareMetadataMap(updatedChangeSet);
		
		PersistenceSession session = persistenceEngine.getSharedPersistenceSession();
		Transaction transaction = session.beginTransaction();
		
		// Note that we need to get the sku this way, and not from the product above, so that we are
		// in the correct transaction context.
		ProductSku importProductSku = importGuidHelper.findProductSkuByGuid(skuCode1);
		importProductSku.setLength(new BigDecimal("0.9"));
		importProductSku.setLength(new BigDecimal("1.5"));
		
		session.update(importProductSku);
		transaction.commit();		
		
		boolean expectedMemberFound = isObjectInChangeSet(updatedChangeSet, skuGuid);
		// is the reverse in Oracle
		assertTrue(EXPECT_TO_FIND_THE_PRODUCT_SKU_IN_THE_CHANGE_SET, expectedMemberFound);
	}
	
	/**
	 * Verify that if an update is made with 2 BigDecimals that are the same that the sku does not appear in the changeset.
	 */	
	@DirtiesDatabase
	@Test
	public void testUpdateSku2BDsSame() {
		
		ProductSku productSku = databaseProduct.getProductSkus().get(skuCode1);		
		productSku.setLength(new BigDecimal(TEST_VALUE));
		productSku.setWidth(new BigDecimal("1.50"));
		databaseProduct = productService.saveOrUpdate(databaseProduct);
		String skuGuid = productSku.getGuid();
		ChangeSet updatedChangeSet = createNewChangeSet();
				
		prepareMetadataMap(updatedChangeSet);
		
		PersistenceSession session = persistenceEngine.getSharedPersistenceSession();
		Transaction transaction = session.beginTransaction();
		
		// Note that we need to get the sku this way, and not from the product above, so that we are
		// in the correct transaction context.
		ProductSku importProductSku = importGuidHelper.findProductSkuByGuid(skuCode1);
		importProductSku.setLength(new BigDecimal(TEST_VALUE));
		importProductSku.setWidth(new BigDecimal("1.50"));
		
		session.update(importProductSku);
		transaction.commit();		
		
		boolean expectedMemberFound = isObjectInChangeSet(updatedChangeSet, skuGuid);
		assertFalse("expect to *not* find the ProductSku in the change set", expectedMemberFound);
	}
	
	/**
	 * Verify that if an update is made with a change in text but BigDecimal different only in scale that the sku appears in the changeset.
	 */	
	@DirtiesDatabase
	@Test
	public void testUpdateSkuTextDifferentBDOnlyScaleDifferent() {
						
		ProductSku productSku = databaseProduct.getProductSkus().get(skuCode1);	
		productSku.setImage("image1.jpg");
		productSku.setLength(new BigDecimal("7"));
		databaseProduct = productService.saveOrUpdate(databaseProduct);
		String skuGuid = productSku.getGuid();
		ChangeSet updatedChangeSet = createNewChangeSet();
				
		prepareMetadataMap(updatedChangeSet);
		
		PersistenceSession session = persistenceEngine.getSharedPersistenceSession();
		Transaction transaction = session.beginTransaction();
		
		// Note that we need to get the sku this way, and not from the product above, so that we are
		// in the correct transaction context.
		ProductSku importProductSku = importGuidHelper.findProductSkuByGuid(skuCode1);
		productSku.setImage("image2.jpg");
		importProductSku.setLength(new BigDecimal("7.0"));
		
		session.update(importProductSku);
		transaction.commit();		
		
		boolean expectedMemberFound = isObjectInChangeSet(updatedChangeSet, skuGuid);
		assertTrue(EXPECT_TO_FIND_THE_PRODUCT_SKU_IN_THE_CHANGE_SET, expectedMemberFound);
	}

	private void prepareMetadataMap(final ChangeSet updatedChangeSet) {
		metadataMap.put("changeSetGuid", updatedChangeSet.getGuid());
		metadataMap.put("changeSetUserGuid", "300");
		metadataMap.put("activeImportStage", "stage2");
		metadataMap.put("addToChangeSetFlag", "true");
	}

	private ChangeSet createNewChangeSet() {
		// Create a change set.	 (This is from CreateChangeSetWizard)
		ChangeSet changeSet = new ChangeSetImpl();
		changeSet.setCreatedByUserGuid("300");
		changeSet.setDescription("");
		changeSet.setName("Test Changeset");
		
		// create a new change set
		return changeSetManagementService.add(changeSet);
	}


	private boolean isObjectInChangeSet(final ChangeSet updatedChangeSet, final String objectGuid) {
		Collection<BusinessObjectGroupMember> businessObjects = changeSetMemberDao.findGroupMembersByGroupId(updatedChangeSet.getGuid());
		
		boolean expectedMemberFound = false;
		for (BusinessObjectGroupMember businessObject : businessObjects) {
			
			if (businessObject.getObjectType().equals(PRODUCT_SKU) && businessObject.getObjectIdentifier().equals(objectGuid)) {
				expectedMemberFound = true;
			}			
		}
		return expectedMemberFound;
	}
	
}
