/**
 * 
 */
package com.elasticpath.importexport.importer.importers.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.dto.products.bundles.ProductBundleDTO;
import com.elasticpath.importexport.common.exception.runtime.PopulationRuntimeException;
import com.elasticpath.service.catalog.ProductBundleService;

/**
 * Test for ProductBundleImporterImplTest.
 */
public class ProductBundleImporterImplTest {
	
	private static final String PRODUCT_BUNDLE_GUID = "GUID_1";
	
	private static final String ROOT_ELEMENT = "bundle";
	
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	
	private final ProductBundleService productBundleService = context.mock(ProductBundleService.class);

	@SuppressWarnings("unchecked")
	private final DomainAdapter<ProductBundle, ProductBundleDTO> domainAdapter = context.mock(DomainAdapter.class);
	
	private ProductBundleImporterImpl productBundleImporter;
	
	private ProductBundleDTO createProductBundleDTO() {
		ProductBundleDTO dto = new ProductBundleDTO();
		
		dto.setCode(PRODUCT_BUNDLE_GUID);
		
		
		return dto;
	}

	/** SetUps the test. */
	@Before
	public void setUp() {
		productBundleImporter = new ProductBundleImporterImpl();
		productBundleImporter.setProductBundleService(productBundleService);
		productBundleImporter.setProductBundleAdapter(domainAdapter);
	}

	/**
	 * Test method for 
	 * {@link ProductBundleImporterImpl#findPersistentObject(com.elasticpath.importexport.common.dto.products.bundles.ProductBundleDTO)}.
	 */
	@Test
	public void testFindPersistentObjectProductBundleDTO() {
		final ProductBundle productBundle = context.mock(ProductBundle.class);
		
		context.checking(new Expectations() { {
			oneOf(productBundleService).findByGuid(PRODUCT_BUNDLE_GUID); will(returnValue(productBundle));
			
		} });
		
		assertEquals(productBundle, productBundleImporter.findPersistentObject(createProductBundleDTO()));
	}
	
	/**
	 * Test method for 
	 * {@link ProductBundleImporterImpl#findPersistentObject(com.elasticpath.importexport.common.dto.products.bundles.ProductBundleDTO)}.
	 */
	@Test(expected = PopulationRuntimeException.class)
	public void testFindPersistentObjectProductBundleDTOfail() {
		
		context.checking(new Expectations() { {
			oneOf(productBundleService).findByGuid(PRODUCT_BUNDLE_GUID); will(returnValue(null));
			
		} });
		
		productBundleImporter.findPersistentObject(createProductBundleDTO());
	}

	/**
	 * Test method for {@link com.elasticpath.importexport.importer.importers.impl.ProductBundleImporterImpl#getDomainAdapter()}.
	 */
	@Test
	public void testGetDomainAdapter() {
		 
		
		assertEquals(domainAdapter, productBundleImporter.getDomainAdapter());
	}

	/**
	 * Test method for 
	 * {@link com.elasticpath.importexport.importer.importers.impl.ProductBundleImporterImpl
	 * #getDtoGuid(com.elasticpath.importexport.common.dto.products.bundles.ProductBundleDTO)}.
	 */
	@Test
	public void testGetDtoGuidProductBundleDTO() {
		final ProductBundleDTO dto = createProductBundleDTO();
		
		assertEquals(PRODUCT_BUNDLE_GUID, productBundleImporter.getDtoGuid(dto));
	}

	/**
	 * Test method for {@link com.elasticpath.importexport.importer.importers.impl.ProductBundleImporterImpl#getImportedObjectName()}.
	 */
	@Test
	public void testGetImportedObjectName() {
		
		assertEquals(ROOT_ELEMENT, productBundleImporter.getImportedObjectName());
	}

	/** The dto class must be present and correct. */
	@Test
	public void testDtoClass() {
		assertEquals("Incorrect DTO class", ProductBundleDTO.class, productBundleImporter.getDtoClass());
	}

	/** The auxiliary JAXB class list must not be null (can be empty). */
	@Test
	public void testAuxiliaryJaxbClasses() {
		assertNotNull(productBundleImporter.getAuxiliaryJaxbClasses());
	}
}
