package com.elasticpath.importexport.common.adapters.products.data;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


import com.elasticpath.domain.catalog.AvailabilityCriteria;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.importexport.common.dto.products.ProductAvailabilityDTO;
import com.elasticpath.importexport.common.exception.runtime.PopulationRuntimeException;

/**
 * Verify that ProductAvailabilityAdapter populates category domain object from DTO properly and vice versa. 
 * <br>Nested adapters should be tested separately.
 */
public class ProductAvailabilityAdapterTest {
	
	private static final int MIN_ORDER_QTY = 2;

	private static final int PRE_OR_BACK_ORDER_LIMIT = 10;

	private static final AvailabilityCriteria AVAILABILITY_CRITERIA = AvailabilityCriteria.AVAILABLE_FOR_BACK_ORDER;

	private static final boolean STORE_VISIBLE = true;

	private static final Date START_DATE = new Date(1);
	
	private static final Date END_DATE = new Date(2);
	
	private static final Date RELEASE_DATE = new Date();

	private static final String PRODUCT_CODE = "productCode";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private Product mockProduct;

	private ProductAvailabilityAdapter productAvailabilityAdapter;
	
	private static final boolean NOT_SOLD_SEPARATELY = false;

	@Before
	public void setUp() throws Exception {
		mockProduct = context.mock(Product.class);
		//mockDigitalAsset = context.mock(DigitalAsset.class);
		
		
		productAvailabilityAdapter = new ProductAvailabilityAdapter();
	}

	/**
	 * Test method for {@link com.elasticpath.importexport.common.adapters.products.data.ProductAvailabilityAdapter#populateDomain}.
	 */
	@Test
	public void testPopulateDomain() {
		ProductAvailabilityAdapter adapter = new ProductAvailabilityAdapter() {
			@Override
			void checkMinOrderQty(final int minOrderQty) {
				// empty, not necessary
			}
			@Override
			void checkOrderLimit(final int orderLimit) {
				// empty, not necessary
			}
			@Override
			void populateExpectedReleaseDate(final ProductAvailabilityDTO productAvailabilityDTO, final Product product) {
				product.setExpectedReleaseDate(productAvailabilityDTO.getExpectedReleaseDate());
			}
		};

		context.checking(new Expectations() {
			{
				oneOf(mockProduct).setAvailabilityCriteria(AVAILABILITY_CRITERIA);
				oneOf(mockProduct).setStartDate(START_DATE);
				oneOf(mockProduct).setEndDate(END_DATE);
				oneOf(mockProduct).setExpectedReleaseDate(RELEASE_DATE);
				oneOf(mockProduct).setMinOrderQty(MIN_ORDER_QTY);
				oneOf(mockProduct).setPreOrBackOrderLimit(PRE_OR_BACK_ORDER_LIMIT);
				oneOf(mockProduct).setHidden(!STORE_VISIBLE);
				oneOf(mockProduct).setNotSoldSeparately(NOT_SOLD_SEPARATELY);
			}
		});
		
		ProductAvailabilityDTO productAvailabilityDTO = new ProductAvailabilityDTO();
		productAvailabilityDTO.setAvailabilityCriteria(AVAILABILITY_CRITERIA);
		productAvailabilityDTO.setStartDate(START_DATE);
		productAvailabilityDTO.setEndDate(END_DATE);
		productAvailabilityDTO.setExpectedReleaseDate(RELEASE_DATE);
		productAvailabilityDTO.setMinOrderQty(MIN_ORDER_QTY);
		productAvailabilityDTO.setPreOrBackOrderLimit(PRE_OR_BACK_ORDER_LIMIT);
		productAvailabilityDTO.setStorevisible(STORE_VISIBLE);
		productAvailabilityDTO.setNotSoldSeparately(NOT_SOLD_SEPARATELY);
		adapter.populateDomain(productAvailabilityDTO, mockProduct);
	}

	/**
	 * Test method for {@link com.elasticpath.importexport.common.adapters.products.data.ProductAvailabilityAdapter#populateExpectedReleaseDate}.
	 */
	@Test(expected = PopulationRuntimeException.class)
	public void testPopulateExpectedReleaseDateOnNullExpectedReleaseDate() {
		ProductAvailabilityDTO productAvailabilityDTO = new ProductAvailabilityDTO();
		
		productAvailabilityDTO.setAvailabilityCriteria(AvailabilityCriteria.AVAILABLE_FOR_PRE_ORDER);
		productAvailabilityDTO.setExpectedReleaseDate(null);
		context.checking(new Expectations() {
			{
				oneOf(mockProduct).getCode();
				will(returnValue(PRODUCT_CODE));
			}
		});
		
		productAvailabilityAdapter.populateExpectedReleaseDate(productAvailabilityDTO, mockProduct);
	}

	/**
	 * Test method for {@link com.elasticpath.importexport.common.adapters.products.data.ProductAvailabilityAdapter#populateExpectedReleaseDate}.
	 */
	@Test
	public void testPopulateExpectedReleaseDateOnGoodExpectedReleaseDate() {
		ProductAvailabilityDTO productAvailabilityDTO = new ProductAvailabilityDTO();
		
		productAvailabilityDTO.setAvailabilityCriteria(AvailabilityCriteria.ALWAYS_AVAILABLE);
		productAvailabilityDTO.setExpectedReleaseDate(RELEASE_DATE);
		context.checking(new Expectations() {
			{
				oneOf(mockProduct).getCode();
				will(returnValue(PRODUCT_CODE));
			}
		});
		productAvailabilityAdapter.populateExpectedReleaseDate(productAvailabilityDTO, mockProduct);
	}

	/**
	 * Test method for {@link com.elasticpath.importexport.common.adapters.products.data.ProductAvailabilityAdapter#populateExpectedReleaseDate}.
	 */
	@Test
	public void testPopulateExpectedReleaseDateOnWrongAvailabilityCriteria() {
		ProductAvailabilityDTO productAvailabilityDTO = new ProductAvailabilityDTO();
		
		productAvailabilityDTO.setAvailabilityCriteria(AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK);
		productAvailabilityAdapter.populateExpectedReleaseDate(productAvailabilityDTO, mockProduct);

		productAvailabilityDTO.setAvailabilityCriteria(AvailabilityCriteria.ALWAYS_AVAILABLE);
		productAvailabilityAdapter.populateExpectedReleaseDate(productAvailabilityDTO, mockProduct);
		
		// Should happen nothing.
	}
		
	/**
	 * Test method for {@link com.elasticpath.importexport.common.adapters.products.data.ProductAvailabilityAdapter#populateDTO}.
	 */
	@Test
	public void testPopulateDTO() {
		context.checking(new Expectations() {
			{
				oneOf(mockProduct).getAvailabilityCriteria();
				will(returnValue(AVAILABILITY_CRITERIA));
				oneOf(mockProduct).getStartDate();
				will(returnValue(START_DATE));
				oneOf(mockProduct).getEndDate();
				will(returnValue(END_DATE));
				oneOf(mockProduct).getExpectedReleaseDate();
				will(returnValue(RELEASE_DATE));
				oneOf(mockProduct).getMinOrderQty();
				will(returnValue(MIN_ORDER_QTY));
				oneOf(mockProduct).getPreOrBackOrderLimit();
				will(returnValue(PRE_OR_BACK_ORDER_LIMIT));
				oneOf(mockProduct).isHidden();
				will(returnValue(!STORE_VISIBLE));
				oneOf(mockProduct).isNotSoldSeparately();
				will(returnValue(NOT_SOLD_SEPARATELY));
			}
		});
		
		ProductAvailabilityDTO productAvailabilityDTO = new ProductAvailabilityDTO();
		productAvailabilityAdapter.populateDTO(mockProduct, productAvailabilityDTO);
		
		assertEquals(AVAILABILITY_CRITERIA, productAvailabilityDTO.getAvailabilityCriteria());
		assertEquals(START_DATE, productAvailabilityDTO.getStartDate());
		assertEquals(END_DATE, productAvailabilityDTO.getEndDate());
		assertEquals(RELEASE_DATE, productAvailabilityDTO.getExpectedReleaseDate());
		assertEquals(MIN_ORDER_QTY, productAvailabilityDTO.getMinOrderQty());
		assertEquals(PRE_OR_BACK_ORDER_LIMIT, productAvailabilityDTO.getPreOrBackOrderLimit());
		assertEquals(STORE_VISIBLE, productAvailabilityDTO.isStoreVisible());
	}

}
