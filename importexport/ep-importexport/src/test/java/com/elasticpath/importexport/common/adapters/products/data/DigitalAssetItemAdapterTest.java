/**
 * 
 */
package com.elasticpath.importexport.common.adapters.products.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.ElasticPath;
import com.elasticpath.domain.catalog.DigitalAsset;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.importexport.common.dto.products.DigitalAssetItemDTO;

/**
 * Verify that DigitalAssetItemAdapter populates category domain object from DTO properly and vice versa. 
 * <br>Nested adapters should be tested separately.
 */
public class DigitalAssetItemAdapterTest {
	
	private static final Integer EXPIRY_DAYS = 10;

	private static final Integer MAX_DOWNLOAD_TIMES = 15;

	private static final String FILE_NAME = "fileName";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private ProductSku mockProductSku;

	private DigitalAsset mockDigitalAsset;

	private ElasticPath mockElasticPath;

	private DigitalAssetItemAdapter digitalAssetItemAdapter;

	/**
	 * Setup test.
	 */
	@Before
	public void setUp() throws Exception {

		mockProductSku = context.mock(ProductSku.class);
		mockElasticPath = context.mock(ElasticPath.class);
		mockDigitalAsset = context.mock(DigitalAsset.class);
		
		digitalAssetItemAdapter = new DigitalAssetItemAdapter();
		digitalAssetItemAdapter.setElasticPath(mockElasticPath);
	}

	/**
	 * Test method for {@link com.elasticpath.importexport.common.adapters.products.data.DigitalAssetItemAdapter#populateDomain}.
	 */
	@Test
	public void testPopulateDomain() {
		DigitalAssetItemAdapter adapter = new DigitalAssetItemAdapter() {
			@Override
			void checkDownloadExpiry(final Integer expiryDays) {
				// empty, not necessary
			}
			@Override
			void checkDownloadLimit(final Integer maxDownloadTimes) {
				// empty, not necessary
			}
			@Override
			DigitalAsset createDigitalAsset(final ProductSku productSku) {
				assertNotNull(productSku);
				return mockDigitalAsset;
			}
		};

		context.checking(new Expectations() {
			{
				oneOf(mockProductSku).setDigital(true);
				oneOf(mockProductSku).setDigitalAsset(mockDigitalAsset);

				oneOf(mockDigitalAsset).setFileName(FILE_NAME);
				oneOf(mockDigitalAsset).setExpiryDays(EXPIRY_DAYS);
				oneOf(mockDigitalAsset).setMaxDownloadTimes(MAX_DOWNLOAD_TIMES);
			}
		});
		
		DigitalAssetItemDTO digitalAssetItemDTO = new DigitalAssetItemDTO();
		digitalAssetItemDTO.setEnabled(true);
		digitalAssetItemDTO.setExpiryDays(EXPIRY_DAYS);
		digitalAssetItemDTO.setMaxDownloadTimes(MAX_DOWNLOAD_TIMES);
		digitalAssetItemDTO.setFileName(FILE_NAME);
		
		adapter.populateDomain(digitalAssetItemDTO, mockProductSku);
	}

	/**
	 * Test method for {@link com.elasticpath.importexport.common.adapters.products.data.DigitalAssetItemAdapter#createDigitalAsset}.
	 */
	@Test
	public void testCreateDigitalAsset() {
		context.checking(new Expectations() {
			{
				oneOf(mockProductSku).getDigitalAsset();
				will(returnValue(null));
				oneOf(mockElasticPath).getBean(ContextIdNames.DIGITAL_ASSET);
				will(returnValue(mockDigitalAsset));
			}
		});
		
		DigitalAsset result = digitalAssetItemAdapter.createDigitalAsset(mockProductSku);
		
		assertEquals(mockDigitalAsset, result);
	}

	/**
	 * Test method for {@link com.elasticpath.importexport.common.adapters.products.data.DigitalAssetItemAdapter#populateDTO}.
	 */
	@Test
	public void testPopulateDTO() {
		context.checking(new Expectations() {
			{
				oneOf(mockProductSku).isDigital();
				will(returnValue(true));
				oneOf(mockProductSku).getDigitalAsset();
				will(returnValue(mockDigitalAsset));

				oneOf(mockDigitalAsset).getFileName();
				will(returnValue(FILE_NAME));
				oneOf(mockDigitalAsset).getExpiryDays();
				will(returnValue(EXPIRY_DAYS));
				oneOf(mockDigitalAsset).getMaxDownloadTimes();
				will(returnValue(MAX_DOWNLOAD_TIMES));
			}
		});
		
		DigitalAssetItemDTO digitalAssetItemDTO = new DigitalAssetItemDTO();
		digitalAssetItemAdapter.populateDTO(mockProductSku, digitalAssetItemDTO);
		
		assertEquals(true, digitalAssetItemDTO.isEnabled());
		assertEquals(FILE_NAME, digitalAssetItemDTO.getFileName());
		assertEquals(EXPIRY_DAYS, digitalAssetItemDTO.getExpiryDays());
		assertEquals(MAX_DOWNLOAD_TIMES, digitalAssetItemDTO.getMaxDownloadTimes());
	}

}
