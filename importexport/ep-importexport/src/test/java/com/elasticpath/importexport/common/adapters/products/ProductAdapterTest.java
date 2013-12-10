package com.elasticpath.importexport.common.adapters.products;

import java.util.ArrayList;
import java.util.Date;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.lib.concurrent.Synchroniser;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.common.dto.DisplayValue;
import com.elasticpath.commons.util.Collections;
import com.elasticpath.domain.attribute.AttributeValueGroup;
import com.elasticpath.domain.attribute.impl.AttributeValueGroupImpl;
import com.elasticpath.domain.attribute.impl.ProductAttributeValueFactoryImpl;
import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.catalog.ObjectWithLocaleDependantFields;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.catalog.impl.BrandImpl;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.domain.catalog.impl.ProductTypeImpl;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.domain.tax.impl.TaxCodeImpl;
import com.elasticpath.importexport.common.adapters.products.data.AttributeGroupAdapter;
import com.elasticpath.importexport.common.adapters.products.data.ProductAvailabilityAdapter;
import com.elasticpath.importexport.common.adapters.products.data.SeoAdapter;
import com.elasticpath.importexport.common.caching.CachingService;
import com.elasticpath.importexport.common.dto.products.AttributeGroupDTO;
import com.elasticpath.importexport.common.dto.products.ProductAvailabilityDTO;
import com.elasticpath.importexport.common.dto.products.ProductDTO;
import com.elasticpath.importexport.common.dto.products.ProductSkuDTO;
import com.elasticpath.importexport.common.dto.products.SeoDTO;
import com.elasticpath.importexport.common.exception.runtime.PopulationRollbackException;
import com.elasticpath.service.catalog.ProductSkuService;
import com.elasticpath.service.misc.TimeService;

/**
 * Verify that ProductAdapter populates product domain object from DTO properly. Nested adapters should be tested separately.
 */ // too all for the JMock context syntax
public class ProductAdapterTest {

	private ProductDTO productDto;

	private ProductAdapter productAdapter;

	private static final String PRODUCT_CODE = "ProductGUID";

	private static final String PRODUCT_IMAGE = "productImage.png";

	private static final String PRODUCT_TYPE = "ProductType";

	private static final String TAX_CODE = "GOODS";

	private static final String BRAND = "Nike";

	private SeoDTO seoDto;

	private ProductAvailabilityDTO productAvailability;

	private AttributeGroupDTO productAttributes;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery() {
		{
			setImposteriser(ClassImposteriser.INSTANCE); // to be able to mock classes
			setThreadingPolicy(new Synchroniser());
		}
	};

	private Product mockProductDomain;

	private CachingService mockCachingService;

	private ProductType productType;

	private TaxCode taxCode;

	private Brand brand;

	private Date modifiedDate;


	@Before
	public void setUp() throws Exception {
		productDto = new ProductDTO();
		productDto.setCode(PRODUCT_CODE);
		productDto.setImage(PRODUCT_IMAGE);
		productDto.setType(PRODUCT_TYPE);
		productDto.setTaxCode(TAX_CODE);
		productDto.setBrand(BRAND);
		seoDto = new SeoDTO();
		productDto.setSeoDto(seoDto);
		productAvailability = new ProductAvailabilityDTO();
		productDto.setProductAvailability(productAvailability);
		productAttributes = new AttributeGroupDTO();
		productDto.setProductAttributes(productAttributes);
		productDto.setNameValues(new ArrayList<DisplayValue>());
		productDto.setProductSkus(new ArrayList<ProductSkuDTO>());

		mockProductDomain = context.mock(Product.class);
		context.checking(new Expectations() {
			{
				allowing(mockProductDomain).getAttributeValueGroup();
				will(returnValue(new AttributeValueGroupImpl(new ProductAttributeValueFactoryImpl())));
			}
		});
		mockCachingService = context.mock(CachingService.class);
		final MockSeoAdapter mockSeoAdapter = new MockSeoAdapter();
		final MockProductAvailabilityAdapter mockProductAvailabilityAdapter = new MockProductAvailabilityAdapter();
		final MockAttributeGroupAdapter mockAttributeGroupAdapter = new MockAttributeGroupAdapter();

		modifiedDate = new Date();
		final TimeService mockTimeService = context.mock(TimeService.class);
		context.checking(new Expectations() {
			{
				allowing(mockTimeService).getCurrentTime();
				will(returnValue(modifiedDate));
			}
		});

		productAdapter = new ProductAdapter();
		productAdapter.setCachingService(mockCachingService);
		productAdapter.setSeoAdapter(mockSeoAdapter);
		productAdapter.setProductAvailabilityAdapter(mockProductAvailabilityAdapter);
		productAdapter.setAttributeGroupAdapter(mockAttributeGroupAdapter);
		productAdapter.setTimeService(mockTimeService);

		productType = new ProductTypeImpl();
		taxCode = new TaxCodeImpl();
		brand = new BrandImpl();
		context.checking(new Expectations() {
			{
				allowing(mockCachingService).findProductTypeByName(PRODUCT_TYPE);
				will(returnValue(productType));
				allowing(mockCachingService).findTaxCodeByCode(TAX_CODE);
				will(returnValue(taxCode));
				allowing(mockCachingService).findBrandByCode(BRAND);
				will(returnValue(brand));
			}
		});

	}

	/**
	 * Check that all required fields for Dto object are being set during domain population.
	 */
	@Test
	public void testPopulateDTO() {
		// empty yet
	}

	/**
	 * Check that all required fields for product domain object are being set during domain population.
	 */
	@Test
	public void testPopulateDomain() {
		context.checking(new Expectations() {
			{
				// expectations
				oneOf(mockProductDomain).setCode(PRODUCT_CODE);
				oneOf(mockProductDomain).setImage(PRODUCT_IMAGE);
				oneOf(mockProductDomain).setProductType(productType);
				oneOf(mockProductDomain).setTaxCode(taxCode);
				oneOf(mockProductDomain).setBrand(brand);
				oneOf(mockProductDomain).setLastModifiedDate(modifiedDate);
				// The product type attribute validation is tested in ProductAdapterNewTest.
				allowing(mockProductDomain).getProductType();
				will(returnValue(productType));
				allowing(mockProductDomain).getAttributeValueMap();
				will(returnValue(Collections.emptySortedMap()));
			}
		});
		productAdapter.populateDomain(productDto, mockProductDomain);
	}


	/**
	 * Tests that populateExtra() with a new sku for the product having a code that exists
	 * for an already persisted sku throws an exception. This method mocks out SeoAdapter,
	 * ProductAvailabilityAdapter, and AttributeGroupAdapter so that the primary focus of the test
	 * is populateDomainProductSkus().
	 *
	 */
	@Test(expected = PopulationRollbackException.class)
	public void testPopulateExtraWithDuplicateSkuCode() {

		setupAdaptersOnProductAdapter();
		context.checking(new Expectations() {
			{
				// Mock methods that we dont't want to test and would throw null pointer exceptions
				allowing(mockProductDomain).getCode();
				will(returnValue("P111"));

				// Mock getSkuByGuid() to return null indicating the sku is new for the product
				allowing(mockProductDomain).getSkuByGuid(with(aNull(String.class)));
				will(returnValue(null));
			}
		});

		// Create test ProductSkuDTO with sku code PSC111
		ProductSkuDTO productSkuDto = new ProductSkuDTO();
		productSkuDto.setSkuCode("PSC111");
		productDto.getProductSkus().add(productSkuDto);

		// Mock productSkuService to return nothing for findByGuid (indicating the sku does not already exist)
		// and return a test ProductSku for findBySkuCode (indicating an existing sku has the same code)
		final ProductSkuService mockProductSkuService = context.mock(ProductSkuService.class);
		context.checking(new Expectations() {
			{
				allowing(mockProductSkuService).findByGuid(with(aNull(String.class)));
				will(returnValue(null));
				allowing(mockProductSkuService).findBySkuCode("PSC111");
				will(returnValue(new ProductSkuImpl()));
			}
		});
		productAdapter.setProductSkuService(mockProductSkuService);

		// Execute method under test and assert that the correct error code is in the exception
		productAdapter.populateExtra(productDto, mockProductDomain);
	}

	private void setupAdaptersOnProductAdapter() {
		// Setup mock adapter classes that will not be tested
		final SeoAdapter mockSeoAdapter = context.mock(SeoAdapter.class);
		productAdapter.setSeoAdapter(mockSeoAdapter);

		final ProductAvailabilityAdapter mockProductAvailabilityAdapter = context.mock(ProductAvailabilityAdapter.class);
		productAdapter.setProductAvailabilityAdapter(mockProductAvailabilityAdapter);

		final AttributeGroupAdapter mockAttributeGroupAdapter = context.mock(AttributeGroupAdapter.class);
		productAdapter.setAttributeGroupAdapter(mockAttributeGroupAdapter);

		// Add the adapters to the context
		context.checking(new Expectations() {
			{
				allowing(mockSeoAdapter).populateDomain(productDto.getSeoDTO(), mockProductDomain);
				allowing(mockProductAvailabilityAdapter).populateDomain(productDto.getProductAvailability(), mockProductDomain);
				allowing(mockAttributeGroupAdapter).populateDomain(productDto.getProductAttributes(), mockProductDomain.getAttributeValueGroup());
			}
		});
	}

	/**
	 * Using this mock adapter as a stub because mock based on DomainAdapter can't be used instead.
	 */
	private static class MockSeoAdapter extends SeoAdapter {
		@Override
		public void populateDomain(final SeoDTO seoDTO, final ObjectWithLocaleDependantFields product) {
			// do nothing
		}

		@Override
		public void populateDTO(final ObjectWithLocaleDependantFields product, final SeoDTO seoDTO) {
			// do nothing
		}
	}

	/**
	 * Using this mock adapter as a stub because mock based on DomainAdapter can't be used instead.
	 */
	private static class MockProductAvailabilityAdapter extends ProductAvailabilityAdapter {

		@Override
		public void populateDomain(final ProductAvailabilityDTO source, final Product target) {
			//do nothing
		}

		@Override
		public void populateDTO(final Product source, final ProductAvailabilityDTO target) {
			//do nothing
		}

	}

	/**
	 * Using this mock adapter as a stub because mock based on DomainAdapter can't be used instead.
	 */
	private static class MockAttributeGroupAdapter extends AttributeGroupAdapter {
		@Override
		public void populateDomain(final AttributeGroupDTO attributeGroupDto, final AttributeValueGroup attributeValueGroup) {
			// do nothing
		}
	}
}
