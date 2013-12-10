package com.elasticpath.importexport.common.adapters.associations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.Date;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.ElasticPath;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductAssociation;
import com.elasticpath.domain.catalog.impl.ProductAssociationImpl;
import com.elasticpath.importexport.common.caching.CachingService;
import com.elasticpath.importexport.common.dto.productassociation.ProductAssociationDTO;
import com.elasticpath.importexport.common.dto.productassociation.ProductAssociationType;
import com.elasticpath.importexport.common.exception.runtime.PopulationRollbackException;
import com.elasticpath.persistence.api.LoadTuner;

/**
 * Verify that ProductAssociationAdapter (from Catalogs) populates catalog domain object from DTO properly and vice versa.
 * Nested adapters should be tested separately.
 */
public class ProductAssociationAdapterTest {

	private static final String CATALOG_CODE = "catalog_code";

	private static final int DEFAULT_QUANITY = 10;

	private static final int ORDERING = 1;

	private static final Date END_DATE = null;

	private static final Date START_DATE = new Date();

	private static final String SOURCE_PRODUCT_CODE = "source_code";

	private static final String TARGET_PRODUCT_CODE = "target_code";

	private static final ProductAssociationType ASSOCIATION_TYPE = ProductAssociationType.Accessory;

	private ProductAssociationAdapter productAssociationAdapter;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	@Mock
	private ElasticPath mockElasticPath;

	@Mock
	private CachingService mockCachingService;

	@Mock
	private Product sourceProduct;

	@Mock
	private Product targetProduct;

	@Mock
	private Catalog catalog;

	@Before
	public void setUp() throws Exception {
		context.checking(new Expectations() {
			{
				allowing(catalog).getCode();
				will(returnValue(CATALOG_CODE));

				allowing(sourceProduct).getCode();
				will(returnValue(SOURCE_PRODUCT_CODE));

				allowing(targetProduct).getCode();
				will(returnValue(TARGET_PRODUCT_CODE));

				allowing(mockCachingService).findProductByCode(with(SOURCE_PRODUCT_CODE), with(aNull(LoadTuner.class)));
				will(returnValue(sourceProduct));
				allowing(mockCachingService).findProductByCode(with(TARGET_PRODUCT_CODE), with(aNull(LoadTuner.class)));
				will(returnValue(targetProduct));
				allowing(mockCachingService).findCatalogByCode(with(CATALOG_CODE), with(aNull(LoadTuner.class)));
				will(returnValue(catalog));

				allowing(mockElasticPath).getBean(ContextIdNames.PRODUCT_ASSOCIATION);
				will(returnValue(new ProductAssociationImpl()));
			}
		});


		productAssociationAdapter = new ProductAssociationAdapter();
		productAssociationAdapter.setCachingService(mockCachingService);
		productAssociationAdapter.setElasticPath(mockElasticPath);
	}

	/**
	 * Tests PopulateDTO.
	 */
	@Test
	public void testPopulateDTO() {
		final ProductAssociation mockDomain = context.mock(ProductAssociation.class);
		context.checking(new Expectations() {
			{
				oneOf(mockDomain).getAssociationType();
				will(returnValue(ASSOCIATION_TYPE.typeId()));
				oneOf(mockDomain).getDefaultQuantity();
				will(returnValue(DEFAULT_QUANITY));
				oneOf(mockDomain).getStartDate();
				will(returnValue(START_DATE));
				oneOf(mockDomain).getEndDate();
				will(returnValue(END_DATE));
				oneOf(mockDomain).getSourceProduct();
				will(returnValue(sourceProduct));
				oneOf(mockDomain).getTargetProduct();
				will(returnValue(targetProduct));
				oneOf(mockDomain).getOrdering();
				will(returnValue(ORDERING));
				oneOf(mockDomain).getCatalog();
				will(returnValue(catalog));
				oneOf(mockDomain).isSourceProductDependent();
				will(returnValue(false));
			}
		});

		ProductAssociationDTO dto = productAssociationAdapter.createDtoObject();

		productAssociationAdapter.populateDTO(mockDomain, dto);

		assertEquals(CATALOG_CODE, dto.getCatalogCode());
		assertEquals(DEFAULT_QUANITY, dto.getDefaultQuantity());
		assertEquals(START_DATE, dto.getStartDate());
		assertEquals(END_DATE, dto.getEndDate());
		assertEquals(ORDERING, dto.getOrdering());
		assertEquals(ASSOCIATION_TYPE, dto.getProductAssociationType());
		assertEquals(SOURCE_PRODUCT_CODE, dto.getSourceProductCode());
		assertEquals(TARGET_PRODUCT_CODE, dto.getTargetProductCode());
		assertFalse(dto.isSourceProductDependent());
	}

	/**
	 * Tests PopulateDomain.
	 */
	@Test
	public void testPopulateDomain() {
		ProductAssociationDTO dto = productAssociationAdapter.createDtoObject();

		dto.setCatalogCode(CATALOG_CODE);
		dto.setDefaultQuantity(DEFAULT_QUANITY);
		dto.setStartDate(START_DATE);
		dto.setEndDate(END_DATE);
		dto.setOrdering(ORDERING);
		dto.setProductAssociationType(ASSOCIATION_TYPE);
		dto.setSourceProductCode(SOURCE_PRODUCT_CODE);
		dto.setTargetProductCode(TARGET_PRODUCT_CODE);
		dto.setSourceProductDependent(false);

		final ProductAssociation mockDomain = context.mock(ProductAssociation.class);

		context.checking(new Expectations() {
			{
				oneOf(mockDomain).setAssociationType(ASSOCIATION_TYPE.typeId());
				oneOf(mockDomain).setDefaultQuantity(DEFAULT_QUANITY);
				oneOf(mockDomain).setStartDate(START_DATE);
				oneOf(mockDomain).setEndDate(END_DATE);
				oneOf(mockDomain).setSourceProduct(sourceProduct);
				oneOf(mockDomain).setTargetProduct(targetProduct);
				oneOf(mockDomain).setOrdering(ORDERING);
				oneOf(mockDomain).setCatalog(catalog);
				oneOf(mockDomain).setSourceProductDependent(false);
			}
		});

		productAssociationAdapter.populateDomain(dto, mockDomain);
	}

	/**
	 * Tests PopulateDomainThrowsOnEqualCode.
	 */
	@Test(expected = PopulationRollbackException.class)
	public void testPopulateDomainThrowsOnEqualCode() {
		ProductAssociation mockDomain = createMockDomainWithStubs();
		ProductAssociationDTO dto = productAssociationAdapter.createDtoObject();

		dto.setSourceProductCode(SOURCE_PRODUCT_CODE);
		dto.setTargetProductCode(SOURCE_PRODUCT_CODE);
		dto.setSourceProductDependent(false);

		productAssociationAdapter.populateDomain(dto, mockDomain);
	}

	/**
	 * Tests PopulateDomainThrowsOnDefaultQuantity.
	 */
	@Test(expected = PopulationRollbackException.class)
	public void testPopulateDomainThrowsOnDefaultQuantity() {
		ProductAssociation mockDomain = createMockDomainWithStubs();
		ProductAssociationDTO dto = createPrepopulatedDTO();

		dto.setDefaultQuantity(0);

		productAssociationAdapter.populateDomain(dto, mockDomain);
	}

	/**
	 * Tests PopulateDomainThrowsOnStartDate.
	 */
	@Test(expected = PopulationRollbackException.class)
	public void testPopulateDomainThrowsOnStartDate() {
		ProductAssociation mockDomain = createMockDomainWithStubs();
		ProductAssociationDTO dto = createPrepopulatedDTO();

		dto.setStartDate(null);

		productAssociationAdapter.populateDomain(dto, mockDomain);
	}

	/**
	 * Tests PopulateDomainThrowsOnNoSourceProduct.
	 */
	@Test(expected = PopulationRollbackException.class)
	public void testPopulateDomainThrowsOnNoSourceProduct() {
		final CachingService mockBadCachingService = context.mock(CachingService.class);
		context.checking(new Expectations() {
			{
				oneOf(mockBadCachingService).findProductByCode(with(SOURCE_PRODUCT_CODE), with(aNull(LoadTuner.class)));
				will(returnValue(null));
			}
		});
		productAssociationAdapter.setCachingService(mockBadCachingService);

		ProductAssociation mockDomain = createMockDomainWithStubs();

		ProductAssociationDTO dto = createPrepopulatedDTO();

		productAssociationAdapter.populateDomain(dto, mockDomain);
	}

	/**
	 * Tests PopulateDomainThrowsOnNoTargetProduct.
	 */
	@Test(expected = PopulationRollbackException.class)
	public void testPopulateDomainThrowsOnNoTargetProduct() {
		final CachingService mockBadCachingService = context.mock(CachingService.class);
		context.checking(new Expectations() {
			{
				oneOf(mockBadCachingService).findProductByCode(with(SOURCE_PRODUCT_CODE), with(aNull(LoadTuner.class)));
				will(returnValue(sourceProduct));
				oneOf(mockBadCachingService).findProductByCode(with(TARGET_PRODUCT_CODE), with(aNull(LoadTuner.class)));
				will(returnValue(null));
			}
		});
		productAssociationAdapter.setCachingService(mockBadCachingService);

		ProductAssociation mockDomain = createMockDomainWithStubs();

		ProductAssociationDTO dto = createPrepopulatedDTO();

		productAssociationAdapter.populateDomain(dto, mockDomain);
	}

	private ProductAssociationDTO createPrepopulatedDTO() {
		ProductAssociationDTO dto = productAssociationAdapter.createDtoObject();

		dto.setSourceProductCode(SOURCE_PRODUCT_CODE);
		dto.setTargetProductCode(TARGET_PRODUCT_CODE);
		dto.setDefaultQuantity(DEFAULT_QUANITY);
		dto.setProductAssociationType(ASSOCIATION_TYPE);
		dto.setOrdering(1);
		dto.setStartDate(START_DATE);
		dto.setSourceProductCode(SOURCE_PRODUCT_CODE);
		dto.setTargetProductCode(TARGET_PRODUCT_CODE);

		return dto;
	}

	private ProductAssociation createMockDomainWithStubs() {
		final ProductAssociation mockDomain = context.mock(ProductAssociation.class);
		context.checking(new Expectations() {
			{
				allowing(mockDomain).setAssociationType(ASSOCIATION_TYPE.typeId());
				allowing(mockDomain).setDefaultQuantity(DEFAULT_QUANITY);
				allowing(mockDomain).setStartDate(START_DATE);
				allowing(mockDomain).setEndDate(END_DATE);
				allowing(mockDomain).setSourceProduct(sourceProduct);
				allowing(mockDomain).setTargetProduct(targetProduct);
				allowing(mockDomain).setOrdering(ORDERING);
				allowing(mockDomain).setCatalog(catalog);
			}
		});
		return mockDomain;
	}

	/**
	 * Tests creation of Domain Object.
	 */
	@Test
	public void testCreateDomainObject() {
		assertNotNull(productAssociationAdapter.createDomainObject());
	}

	/**
	 * Tests creation of DTO Object.
	 */
	@Test
	public void testCreateDtoObject() {
		assertNotNull(productAssociationAdapter.createDtoObject());
	}
}
