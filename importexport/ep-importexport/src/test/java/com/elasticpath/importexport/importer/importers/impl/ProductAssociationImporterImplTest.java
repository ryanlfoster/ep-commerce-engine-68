package com.elasticpath.importexport.importer.importers.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.util.impl.UtilityImpl;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductAssociation;
import com.elasticpath.domain.catalog.impl.CatalogImpl;
import com.elasticpath.domain.catalog.impl.ProductAssociationImpl;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.impl.ElasticPathImpl;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.dto.productassociation.ProductAssociationDTO;
import com.elasticpath.importexport.common.dto.productassociation.ProductAssociationType;
import com.elasticpath.importexport.common.exception.runtime.ImportRuntimeException;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.importer.configuration.ImportConfiguration;
import com.elasticpath.importexport.importer.configuration.ImporterConfiguration;
import com.elasticpath.importexport.importer.context.ImportContext;
import com.elasticpath.importexport.importer.importers.CollectionsStrategy;
import com.elasticpath.importexport.importer.importers.SavingStrategy;
import com.elasticpath.persistence.api.LoadTuner;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.service.catalog.ProductAssociationService;
import com.elasticpath.service.search.query.ProductAssociationSearchCriteria;

/**
 * Test for <code>ProductAssociationImporterImpl</code>. 
 */
public class ProductAssociationImporterImplTest {

	private ProductAssociationImporterImpl productAssociationImporter;
	
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private SavingStrategy<ProductAssociation, ProductAssociationDTO> mockSavingStrategy;
	
	private ProductAssociationService mockProductAssociationService;
	
	private ProductAssociationDTO productAssociationDTO;
	
	/**
	 * Prepare for tests.
	 * 
	 * @throws Exception in case of error happens
	 */
	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		productAssociationDTO = new ProductAssociationDTO();
		productAssociationDTO.setCatalogCode("c_code");
		productAssociationDTO.setProductAssociationType(ProductAssociationType.Accessory);
		
		productAssociationImporter = new ProductAssociationImporterImpl();
		mockSavingStrategy = context.mock(SavingStrategy.class);
		mockProductAssociationService = context.mock(ProductAssociationService.class);

		context.checking(new Expectations() {
			{
				allowing(mockSavingStrategy).setDomainAdapter(with(aNull(DomainAdapter.class)));
				allowing(mockSavingStrategy).populateAndSaveObject(with(any(ProductAssociation.class)), with(any(ProductAssociationDTO.class)));
				allowing(mockSavingStrategy).getSavingManager();
				allowing(mockSavingStrategy).setCollectionsStrategy(with(any(CollectionsStrategy.class)));
				allowing(mockSavingStrategy).isImportRequired(with(any(Persistable.class)));
				will(returnValue(true));
			}
		});
		productAssociationImporter.setProductAssociationService(mockProductAssociationService);
		productAssociationImporter.setStatusHolder(new ImportStatusHolder());
	}

	/**
	 * Check an import with non-initialized importer.
	 */
	@Test(expected = ImportRuntimeException.class)
	public void testExecuteNonInitializedImport() {
		productAssociationImporter.executeImport(productAssociationDTO);
	}
	
	/**
	 * Check an import of one product.
	 */
	@Test
	public void testExecuteImport() {
		final List<? extends ProductAssociation> productAssociationList = Collections.singletonList(new ProductAssociationImpl());
		context.checking(new Expectations() {
			{
				allowing(mockProductAssociationService).findByCriteria(
						with(any(ProductAssociationSearchCriteria.class)),
						with(aNull(LoadTuner.class)));
				will(returnValue(productAssociationList));
			}
		});

		ImportConfiguration importConfiguration = new ImportConfiguration();
		importConfiguration.setImporterConfigurationMap(new HashMap<JobType, ImporterConfiguration>());
		productAssociationImporter.initialize(new ImportContext(importConfiguration), mockSavingStrategy);
		productAssociationImporter.executeImport(productAssociationDTO);
		assertEquals("productassociation", productAssociationImporter.getImportedObjectName());
		assertNotNull(productAssociationImporter.getProductAssociationService());
		assertNotNull(productAssociationImporter.getSavingStrategy());
	}

	/**
	 * Test clear collections strategy for product associations.
	 */
	@SuppressWarnings("PMD.DontUseElasticPathImplGetInstance")
	@Test
	public void testCollectionsStrategy() {
		ImportConfiguration importConfiguration = new ImportConfiguration();
		importConfiguration.setImporterConfigurationMap(new HashMap<JobType, ImporterConfiguration>());
		
		final BeanFactory mockBeanFactory = context.mock(BeanFactory.class);
		context.checking(new Expectations() {
			{
				allowing(mockBeanFactory).getBean(ContextIdNames.UTILITY);
				will(returnValue(new UtilityImpl()));
			}
		});
		((ElasticPathImpl) ElasticPathImpl.getInstance()).setBeanFactory(mockBeanFactory);
				
		productAssociationImporter.initialize(new ImportContext(importConfiguration), mockSavingStrategy);
		
		CollectionsStrategy<ProductAssociation, ProductAssociationDTO> collectionsStrategy = productAssociationImporter.getCollectionsStrategy();
		assertFalse(collectionsStrategy.isForPersistentObjectsOnly());
		
		Catalog catalog = new CatalogImpl();
		catalog.setCode("ctalogCode");
		Product sourceProduct = new ProductImpl();
		sourceProduct.setCode("sourceProduct");
		Product targetProduct = new ProductImpl();
		sourceProduct.setCode("targetProduct");
		ProductAssociation productAssociation = new ProductAssociationImpl();
		productAssociation.setUidPk(1L);
		productAssociation.setSourceProduct(sourceProduct);
		productAssociation.setTargetProduct(targetProduct);
		productAssociation.setAssociationType(ProductAssociation.CROSS_SELL);
		productAssociation.setCatalog(catalog);
		
		ProductAssociationDTO productAssociationDTO = new ProductAssociationDTO();
		productAssociationDTO.setCatalogCode("catalogCode");
		productAssociationDTO.setSourceProductCode("sourceProduct");
		productAssociationDTO.setTargetProductCode("sometargetProduct");
		
		final ProductAssociation productAssociationForDelete = new ProductAssociationImpl();
		productAssociationForDelete.setUidPk(2L);
		productAssociationForDelete.setSourceProduct(sourceProduct);
		productAssociationForDelete.setTargetProduct(new ProductImpl());
		productAssociationForDelete.setAssociationType(ProductAssociation.REPLACEMENT);
		productAssociationForDelete.setCatalog(catalog);
		
		final List<ProductAssociation> prAssociationList = new ArrayList<ProductAssociation>();
		prAssociationList.add(productAssociation);
		prAssociationList.add(productAssociationForDelete);
		context.checking(new Expectations() {
			{
				oneOf(mockProductAssociationService).findByCriteria(
						with(any(ProductAssociationSearchCriteria.class)),
						with(aNull(LoadTuner.class)));
				will(returnValue(prAssociationList));
				oneOf(mockProductAssociationService).remove(with(productAssociationForDelete));
			}
		});
		
		collectionsStrategy.prepareCollections(productAssociation, productAssociationDTO);
		
	}

	/** The dto class must be present and correct. */
	@Test
	public void testDtoClass() {
		assertEquals("Incorrect DTO class", ProductAssociationDTO.class, productAssociationImporter.getDtoClass());
	}

	/** The auxiliary JAXB class list must not be null (can be empty). */
	@Test
	public void testAuxiliaryJaxbClasses() {
		assertNotNull(productAssociationImporter.getAuxiliaryJaxbClasses());
	}
}
