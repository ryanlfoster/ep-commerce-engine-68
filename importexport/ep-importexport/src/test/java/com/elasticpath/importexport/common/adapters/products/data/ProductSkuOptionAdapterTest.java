package com.elasticpath.importexport.common.adapters.products.data;

import static org.junit.Assert.assertEquals;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.domain.skuconfiguration.impl.JpaAdaptorOfSkuOptionValueImpl;
import com.elasticpath.importexport.common.caching.CachingService;
import com.elasticpath.importexport.common.dto.products.SkuOptionDTO;
import com.elasticpath.importexport.common.exception.runtime.PopulationRuntimeException;

/**
 * Verify that ProductSkuOptionAdapter populates category domain object from DTO properly and vice versa.
 * <br>Nested adapters should be tested separately.
 */
public class ProductSkuOptionAdapterTest {

	private static final String OPTION_KEY_CODE = "optionKeyCode";

	private static final String OPTION_VALUE_KEY = "optionValueKey";

	private JpaAdaptorOfSkuOptionValueImpl jpaAdaptorOfSkuOptionValue;

	private ProductSkuOptionAdapter productSkuOptionAdapter;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private SkuOption mockSkuOption;

	private SkuOptionValue mockSkuOptionValue;

	private CachingService mockCachingService;

	@Before
	public void setUp() throws Exception {
		mockSkuOption = context.mock(SkuOption.class);
		mockSkuOptionValue = context.mock(SkuOptionValue.class);
		mockCachingService = context.mock(CachingService.class);

		jpaAdaptorOfSkuOptionValue = new JpaAdaptorOfSkuOptionValueImpl();

		productSkuOptionAdapter = new ProductSkuOptionAdapter();
		productSkuOptionAdapter.setCachingService(mockCachingService);
	}


	/**
	 * Tests populateDTO.
	 */
	@Test
	public void testPopulateDTO() {
		context.checking(new Expectations() {
			{
				oneOf(mockSkuOption).getOptionKey();
				will(returnValue(OPTION_KEY_CODE));
				oneOf(mockSkuOptionValue).setSkuOption(mockSkuOption);
				oneOf(mockSkuOptionValue).setOptionValueKey(OPTION_VALUE_KEY);
				oneOf(mockSkuOptionValue).getOptionValueKey();
				will(returnValue(OPTION_VALUE_KEY));
				oneOf(mockSkuOptionValue).getSkuOption();
				will(returnValue(mockSkuOption));
			}
		});

		jpaAdaptorOfSkuOptionValue.setSkuOptionValue(mockSkuOptionValue);
		jpaAdaptorOfSkuOptionValue.setSkuOption(mockSkuOption);
		jpaAdaptorOfSkuOptionValue.setOptionValueKey(OPTION_VALUE_KEY);

		SkuOptionDTO skuOptionDTO = new SkuOptionDTO();
		productSkuOptionAdapter.populateDTO(jpaAdaptorOfSkuOptionValue, skuOptionDTO);

		assertEquals(OPTION_KEY_CODE, skuOptionDTO.getCode());
		assertEquals(OPTION_VALUE_KEY, skuOptionDTO.getSkuOptionValue());
	}

	/**
	 * Tests populateDomain.
	 */
	@Test(expected = PopulationRuntimeException.class)
	public void testPopulateDomainOnCouldNotFindSkuOption() {
		SkuOptionDTO skuOptionDTO = new SkuOptionDTO();
		skuOptionDTO.setCode(OPTION_KEY_CODE);

		context.checking(new Expectations() {
			{
				oneOf(mockCachingService).findSkuOptionByKey(OPTION_KEY_CODE);
				will(returnValue(null));
			}
		});
		productSkuOptionAdapter.populateDomain(skuOptionDTO, jpaAdaptorOfSkuOptionValue);
	}

	/**
	 * Tests populateDomain.
	 */
	@Test(expected = PopulationRuntimeException.class)
	public void testPopulateDomainOnDoesNotContain() {
		SkuOptionDTO skuOptionDTO = new SkuOptionDTO();
		skuOptionDTO.setCode(OPTION_KEY_CODE);
		skuOptionDTO.setSkuOptionValue(OPTION_VALUE_KEY);

		context.checking(new Expectations() {
			{
				oneOf(mockCachingService).findSkuOptionByKey(OPTION_KEY_CODE);
				will(returnValue(mockSkuOption));
				oneOf(mockSkuOption).getOptionKey();
				will(returnValue(OPTION_KEY_CODE));
				oneOf(mockSkuOption).contains(OPTION_VALUE_KEY);
				will(returnValue(Boolean.FALSE));
			}
		});
		productSkuOptionAdapter.populateDomain(skuOptionDTO, jpaAdaptorOfSkuOptionValue);
	}

	/**
	 * Tests populateDomain.
	 */
	@Test
	public void testPopulateDomain() {
		SkuOptionDTO skuOptionDTO = new SkuOptionDTO();
		skuOptionDTO.setCode(OPTION_KEY_CODE);
		skuOptionDTO.setSkuOptionValue(OPTION_VALUE_KEY);

		context.checking(new Expectations() {
			{
				oneOf(mockCachingService).findSkuOptionByKey(OPTION_KEY_CODE);
				will(returnValue(mockSkuOption));
				oneOf(mockSkuOption).contains(OPTION_VALUE_KEY);
				will(returnValue(Boolean.TRUE));
				oneOf(mockSkuOption).getOptionValue(OPTION_VALUE_KEY);
				will(returnValue(mockSkuOptionValue));
				oneOf(mockSkuOption).getOptionKey();
				will(returnValue(OPTION_KEY_CODE));
			}
		});

		productSkuOptionAdapter.populateDomain(skuOptionDTO , jpaAdaptorOfSkuOptionValue);
	}

}
