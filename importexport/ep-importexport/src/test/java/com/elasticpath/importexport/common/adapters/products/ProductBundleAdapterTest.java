package com.elasticpath.importexport.common.adapters.products;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jmock.Expectations;
import org.jmock.Sequence;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.common.dto.pricing.PriceAdjustmentDto;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.util.MockInterface;
import com.elasticpath.domain.ElasticPath;
import com.elasticpath.domain.catalog.BundleConstituent;
import com.elasticpath.domain.catalog.ConstituentItem;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductConstituent;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.impl.BundleConstituentImpl;
import com.elasticpath.domain.catalog.impl.ProductConstituentImpl;
import com.elasticpath.domain.catalog.impl.SelectionRuleImpl;
import com.elasticpath.domain.pricing.PriceAdjustment;
import com.elasticpath.domain.pricing.impl.PriceAdjustmentImpl;
import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.domain.skuconfiguration.impl.SkuOptionImpl;
import com.elasticpath.domain.skuconfiguration.impl.SkuOptionValueImpl;
import com.elasticpath.domain.subscriptions.impl.PaymentScheduleImpl;
import com.elasticpath.importexport.common.caching.CachingService;
import com.elasticpath.importexport.common.dto.products.bundles.ProductBundleConstituentCodeDTO;
import com.elasticpath.importexport.common.dto.products.bundles.ProductBundleConstituentDTO;
import com.elasticpath.importexport.common.dto.products.bundles.ProductBundleDTO;
import com.elasticpath.importexport.common.exception.runtime.PopulationRollbackException;
import com.elasticpath.service.catalog.BundleConstituentFactory;
import com.elasticpath.service.catalog.BundleValidator;
import com.elasticpath.service.catalog.impl.BundleConstituentFactoryImpl;
import com.elasticpath.service.catalog.impl.BundleValidatorImpl;
import com.elasticpath.service.pricing.impl.PaymentScheduleHelperImpl;

/**
 * The test for ProductBundleAdapter.
 */
@SuppressWarnings({ "PMD.TooManyStaticImports", "PMD.TooManyMethods" })
public class ProductBundleAdapterTest {

	private static final String PRICELIST_GUID = "PRICELIST_GUID";
	private static final String PRODUCT_CODE = "CODE_1";
	private static final String PRODUCT_CODE2 = "CODE_2";
	private static final String PRODUCT = "product";
	private static final String GUID = "guid";
	private static final String GUID1 = "guid1";
	private static final String GUID2 = "guid2";

	private static final BigDecimal PRICE_ADJUSTMENT_AMOUNT = new BigDecimal("5.00");

	private ProductBundleAdapter productBundleAdapter;
	private BundleConstituentFactory constituentFactory;
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private final ProductBundle productBundle = context.mock(ProductBundle.class);
	private final BundleConstituent bundleConstituent = context.mock(BundleConstituent.class);
	private final MockInterface mock = context.mock(MockInterface.class);
	private final BeanFactory beanFactory = context.mock(BeanFactory.class);
	private Map<String, String> resolver;

	/** SetUps the test. */
	@Before
	public void setUp() {
		resolver = createBundleConstituentCodeTypeResolver();
		constituentFactory = new BundleConstituentFactoryImpl() {
			@Override
			public BundleConstituent createBundleConstituentInternal() {
				BundleConstituent constituent = new BundleConstituentImpl() {
					private static final long serialVersionUID = 1L;

					@SuppressWarnings("unchecked") //because of a bug in checkstyle, can't use generics in the anonymous inner-class.
					@Override
					protected Object getBean(final String beanName) {
						return beanFactory.getBean(beanName);
					}
				};
				return constituent;
			}

		};

		productBundleAdapter = new ProductBundleAdapter();
		productBundleAdapter.setBundleConstituentCodeTypeResolver(resolver);
		productBundleAdapter.setBundleConstituentFactory(constituentFactory);
		productBundleAdapter.setBundleValidator(new BundleValidatorImpl());
	}

	/**
	 * Test method for {@link ProductBundleAdapter#populateDTO(ProductBundle, ProductBundleDTO)}.
	 */
	@Test
	public void testPopulateDTO() {
		productBundleAdapter = new ProductBundleAdapter() {
			@Override
			List<ProductBundleConstituentDTO> buildBundleConstituentDTOList(final List<BundleConstituent> constituents) {
				return mock.method(constituents);
			}
		};
		productBundleAdapter.setBundleConstituentFactory(constituentFactory);
		productBundleAdapter.setBundleConstituentCodeTypeResolver(resolver);

		final ProductBundleDTO productBundleDTO = new ProductBundleDTO();
		final ProductBundleConstituentDTO productBundleConstituentDTO = new ProductBundleConstituentDTO();

		final List<BundleConstituent> bundleConstituents = Arrays.asList(bundleConstituent);

		context.checking(new Expectations() { {
			oneOf(productBundle).getCode(); will(returnValue(PRODUCT_CODE));
			oneOf(productBundle).getSelectionRule(); will(returnValue(new SelectionRuleImpl()));
			oneOf(productBundle).getConstituents(); will(returnValue(bundleConstituents));
			oneOf(mock).method(bundleConstituents); will(returnValue(Arrays.asList(productBundleConstituentDTO)));
		} });

		productBundleAdapter.populateDTO(productBundle, productBundleDTO);

		assertEquals(PRODUCT_CODE, productBundleDTO.getCode());
		assertEquals(1, productBundleDTO.getConstituents().size());
		assertEquals(productBundleConstituentDTO, productBundleDTO.getConstituents().get(0));
	}

	/**
	 * Test method for {@link ProductBundleAdapter#buildBundleConstituentDTOList(List)}.
	 */
	@Test
	public void testBuildBundleConstituentDTOList() {
		productBundleAdapter = new ProductBundleAdapter() {
			@Override
			ProductBundleConstituentDTO buildBundleConstituentDTO(final BundleConstituent constituent) {
				return mock.method(constituent);
			}
		};
		productBundleAdapter.setBundleConstituentFactory(constituentFactory);
		productBundleAdapter.setBundleConstituentCodeTypeResolver(resolver);

		final ProductBundleConstituentDTO productBundleConstituentDTO = new ProductBundleConstituentDTO();

		final List<BundleConstituent> bundleConstituents = Arrays.asList(bundleConstituent);

		context.checking(new Expectations() { {
			oneOf(mock).method(bundleConstituent); will(returnValue(productBundleConstituentDTO));
		} });

		final List<ProductBundleConstituentDTO> buildBundleConstituentDTOs = productBundleAdapter.buildBundleConstituentDTOList(bundleConstituents);

		assertEquals(1, buildBundleConstituentDTOs.size());
		assertEquals(productBundleConstituentDTO, buildBundleConstituentDTOs.get(0));
	}

	/**
	 * Test method for {@link ProductBundleAdapter#buildBundleConstituentDTO(BundleConstituent)}.
	 */
	@Test
	public void testBuildBundleConstituentDTO() {
		productBundleAdapter = new ProductBundleAdapter() {
			@Override
			String getBundleConstituentCode(final BundleConstituent constituent) {
				return mock.method(constituent);
			}
		};
		productBundleAdapter.setBundleConstituentFactory(constituentFactory);
		productBundleAdapter.setBundleConstituentCodeTypeResolver(resolver);

		final ProductConstituentImpl productConstituentItem = new ProductConstituentImpl();

		context.checking(new Expectations() { {
			oneOf(bundleConstituent).getQuantity(); will(returnValue(1));
			oneOf(bundleConstituent).getOrdering(); will(returnValue(1));
			oneOf(bundleConstituent).getGuid(); will(returnValue(GUID));
			allowing(bundleConstituent).getConstituent(); will(returnValue(productConstituentItem));
			oneOf(mock).method(bundleConstituent); will(returnValue(PRODUCT_CODE));
			allowing(bundleConstituent).getPriceAdjustments(); will(returnValue(Collections.emptyList()));
		} });
		ProductBundleConstituentDTO buildBundleConstituentDTO = productBundleAdapter.buildBundleConstituentDTO(bundleConstituent);

		assertEquals(Integer.valueOf(1), buildBundleConstituentDTO.getQuantity());
		assertEquals(PRODUCT_CODE, buildBundleConstituentDTO.getCode().getValue());
		assertEquals(GUID, buildBundleConstituentDTO.getGuid());
	}

	/**
	 * Test method for {@link ProductBundleAdapter#getBundleConstituentCode(BundleConstituent)}.
	 */
	@Test
	public void testGetBundleConstituentCode() {
		final ConstituentItem constituentItem = context.mock(ConstituentItem.class);

		context.checking(new Expectations() { {
			oneOf(bundleConstituent).getConstituent(); will(returnValue(constituentItem));
			oneOf(constituentItem).getCode(); will(returnValue(PRODUCT_CODE));
		} });

		assertEquals(PRODUCT_CODE, productBundleAdapter.getBundleConstituentCode(bundleConstituent));
	}

	/**
	 * Test method for {@link ProductBundleAdapter#populateDomain(ProductBundleDTO, ProductBundle)}.
	 */
	@Test
	public void testPopulateDomain() {
		productBundleAdapter = new ProductBundleAdapter() {
			@Override
			BundleConstituent createOrUpdateBundleConstituent(final ProductBundle bundle,	final ProductBundleConstituentDTO constituentDTO) {
				return mock.method(bundle, constituentDTO);
			}

			@Override
			void checkForCyclicDependencies(final ProductBundle target) {
				//do nothing
			}

			@Override
			void replaceConstituents(final ProductBundle bundle, final Collection<BundleConstituent> newConstituents) {
				//do nothing
			};
		};
		productBundleAdapter.setBundleConstituentFactory(constituentFactory);
		productBundleAdapter.setBundleConstituentCodeTypeResolver(resolver);
		productBundleAdapter.setBundleValidator(new BundleValidatorImpl());

		final ProductBundleConstituentDTO productBundleConstituentDTO = new ProductBundleConstituentDTO();

		ProductBundleDTO productBundleDTO = new ProductBundleDTO();
		productBundleDTO.setCode(PRODUCT_CODE);
		productBundleDTO.setConstituents(Arrays.asList(productBundleConstituentDTO));

		context.checking(new Expectations() { {
			oneOf(productBundle).setCode(PRODUCT_CODE);
			oneOf(productBundle).isCalculated(); will(returnValue(false));
			allowing(productBundle).getConstituents(); will(returnValue(Collections.emptyList()));
			oneOf(mock).method(productBundle, productBundleConstituentDTO); will(returnValue(null));
			allowing(productBundle).getSelectionRule(); will(returnValue(null));
			allowing(productBundle).setSelectionRule(null);
		} });

		productBundleAdapter.populateDomain(productBundleDTO, productBundle);

		assertEquals(PRODUCT_CODE, productBundleDTO.getCode());
	}

	/**
	 * .
	 */
    @Test (expected = PopulationRollbackException.class)
    public void testPopulateDomainWithRecurringChargeInAssignedBundle() {
        productBundleAdapter = new ProductBundleAdapter() {
            @Override
            BundleConstituent createOrUpdateBundleConstituent(final ProductBundle bundle,   final ProductBundleConstituentDTO constituentDTO) {
                return mock.method(bundle, constituentDTO);
            }

            @Override
            void checkForCyclicDependencies(final ProductBundle target) {
                //do nothing
            }

            @Override
			void replaceConstituents(final ProductBundle bundle, final Collection<BundleConstituent> newConstituents) {
            	//do nothing
            };
        };

        productBundleAdapter.setBundleConstituentFactory(constituentFactory);
        productBundleAdapter.setBundleConstituentCodeTypeResolver(resolver);

        final SkuOption frequencyOption = new SkuOptionImpl();
        final BundleValidator bundleValidator = new BundleValidatorImpl();
        final PaymentScheduleHelperImpl paymentScheduleHelper = getPaymentScheduleHelper(frequencyOption);

        ((BundleValidatorImpl) bundleValidator).setPaymentScheduleHelper(paymentScheduleHelper);

        productBundleAdapter.setBundleValidator(bundleValidator);

        final ProductBundleConstituentDTO productBundleConstituentDTO = new ProductBundleConstituentDTO();
        productBundleConstituentDTO.setGuid(GUID);
        final BundleConstituent bundleConstituent = context.mock(BundleConstituent.class, "recurringChargeBundleConstituent");
        final ProductConstituent productConstituent = context.mock(ProductConstituent.class);
        final ProductSku productSku = context.mock(ProductSku.class);
        final Product product = context.mock(Product.class);

        final SkuOptionValue frequencyValue = new SkuOptionValueImpl();
        frequencyValue.setOptionValueKey("monthly");

        final ProductBundleDTO productBundleDTO = new ProductBundleDTO();
        productBundleDTO.setCode(PRODUCT_CODE);
        productBundleDTO.setConstituents(Arrays.asList(productBundleConstituentDTO));

        context.checking(new Expectations() { {
            oneOf(productBundle).setCode(PRODUCT_CODE);
            oneOf(productBundle).isCalculated(); will(returnValue(false));
            allowing(productBundle).getConstituents(); will(returnValue(Arrays.asList(bundleConstituent)));
            allowing(productBundle).getCode(); will(returnValue(PRODUCT_CODE));
            oneOf(bundleConstituent).getConstituent(); will(returnValue(productConstituent));
            allowing(bundleConstituent).getGuid(); will(returnValue(GUID));
            oneOf(productConstituent).isBundle(); will(returnValue(false));
            oneOf(productConstituent).isProduct(); will(returnValue(false));
            oneOf(productConstituent).isProductSku(); will(returnValue(true));
            oneOf(productConstituent).getProductSku(); will(returnValue(productSku));
            oneOf(productSku).getProduct(); will(returnValue(product));
            oneOf(productSku).getSkuOptionValue(frequencyOption); will(returnValue(frequencyValue));
            oneOf(productSku).getOptionValueMap(); will(returnValue(Collections.emptyMap()));

            oneOf(mock).method(productBundle, productBundleConstituentDTO); will(returnValue(null));
        } });

        productBundleAdapter.populateDomain(productBundleDTO, productBundle);
    }

	/**
	 * Test method for {@link ProductBundleAdapter#populateDomain(ProductBundleDTO, ProductBundle)}.
	 */
	@Test
	public void testPopulateDomainPreservesConstituentOrdering() {
		productBundleAdapter = new ProductBundleAdapter() {
			@Override
			BundleConstituent createOrUpdateBundleConstituent(final ProductBundle bundle, final ProductBundleConstituentDTO constituentDTO) {
				return mock.method(bundle, constituentDTO);
			}

			@Override
			void checkForCyclicDependencies(final ProductBundle target) {
				//do nothing
			}

			@Override
			void replaceConstituents(final ProductBundle bundle, final Collection<BundleConstituent> newConstituents) {
				//no-op
			};
		};
		productBundleAdapter.setBundleConstituentFactory(constituentFactory);
		productBundleAdapter.setBundleConstituentCodeTypeResolver(resolver);
		productBundleAdapter.setBundleValidator(new BundleValidatorImpl());

		final ProductBundleConstituentDTO productBundleConstituentDTO1 = new ProductBundleConstituentDTO();
		final ProductBundleConstituentDTO productBundleConstituentDTO2 = new ProductBundleConstituentDTO();
		productBundleConstituentDTO1.setOrdering(2);
		productBundleConstituentDTO2.setOrdering(1);
		final ProductBundleDTO productBundleDTO = new ProductBundleDTO();
		productBundleDTO.setCode(PRODUCT_CODE);
		productBundleDTO.setConstituents(Arrays.asList(productBundleConstituentDTO1, productBundleConstituentDTO2));

	    final Sequence sequence = context.sequence("printSequence");
		context.checking(new Expectations() { {
			oneOf(productBundle).setCode(PRODUCT_CODE);
			oneOf(productBundle).isCalculated(); will(returnValue(false));
			allowing(productBundle).getConstituents(); will(returnValue(Collections.emptyList()));
			oneOf(mock).method(productBundle, productBundleConstituentDTO2); inSequence(sequence); will(returnValue(null));
			oneOf(mock).method(productBundle, productBundleConstituentDTO1); inSequence(sequence); will(returnValue(null));
			allowing(productBundle).getSelectionRule(); will(returnValue(null));
			allowing(productBundle).setSelectionRule(null);
		} });

		productBundleAdapter.populateDomain(productBundleDTO, productBundle);

	}

	/**
	 * Test method for {@link ProductBundleAdapter#createOrUpdateBundleConstituent()} for create.
	 */
	@Test
	public void testCreateOrUpdateBundleConstituentOnCreate() {
		final CachingService cachingService = context.mock(CachingService.class);

		ProductBundleAdapter productBundleAdapter = createStubProductBundleAdapter(cachingService);

		final ProductBundleConstituentDTO constituentDTO = createProductBundleConstituentDto(GUID, PRODUCT_CODE, new ArrayList<PriceAdjustmentDto>());

		final Product product = context.mock(Product.class);

		final BundleConstituent expectedConstituent = constituentFactory.createBundleConstituent(product, 1);
		expectedConstituent.setGuid(GUID);

		context.checking(new Expectations() {
			{
				oneOf(productBundle).getConstituents();
				will(returnValue(Arrays.asList(bundleConstituent)));

				allowing(bundleConstituent).getGuid();

				oneOf(cachingService).findProductByCode(PRODUCT_CODE);
				will(returnValue(product));

				oneOf(productBundle).addConstituent(expectedConstituent);
			}
		});

		productBundleAdapter.createOrUpdateBundleConstituent(productBundle, constituentDTO);
	}

	/**
	 * Test method for {@link ProductBundleAdapter#createOrUpdateBundleConstituent()} for an update happy path.
	 */
	@Test
	public void testCreateOrUpdateBundleConstituentOnUpdateWithNonMatchingPriceAdjustment() {
		final CachingService cachingService = context.mock(CachingService.class);

		ProductBundleAdapter productBundleAdapter = createStubProductBundleAdapter(cachingService);

		final ProductBundleConstituentDTO constituentDTO = createProductBundleConstituentDto(GUID, PRODUCT_CODE, createPriceAdjustmentDtos());

		final Product product = context.mock(Product.class);

		final ProductConstituentImpl productConstituentItem = new ProductConstituentImpl();
		productConstituentItem.setProduct(product);

		context.checking(new Expectations() {
			{
				allowing(bundleConstituent).getGuid();
				will(returnValue(GUID));

				allowing(productBundle).getConstituents();
				will(returnValue(Arrays.asList(bundleConstituent)));

				allowing(bundleConstituent).getConstituent();
				will(returnValue(productConstituentItem));

				oneOf(product).getCode();
				will(returnValue(PRODUCT_CODE));

				allowing(bundleConstituent).setQuantity(Integer.valueOf(1));

				allowing(bundleConstituent).getPriceAdjustments();
				will(returnValue(new ArrayList<PriceAdjustment>()));

				ignoring(bundleConstituent).addPriceAdjustment(with(any(PriceAdjustment.class)));

			}
		});

		productBundleAdapter.createOrUpdateBundleConstituent(productBundle, constituentDTO);
	}

	/**
	 * Test method for {@link ProductBundleAdapter#createOrUpdateBundleConstituent()} for update when the updating constituent product code
	 * does not match the original product code.
	 */
	@Test (expected = PopulationRollbackException.class)
	public void testCreateOrUpdateBundleConstituentOnUpdateWhenConstituentCodeDiffers() {
		final CachingService cachingService = context.mock(CachingService.class);

		ProductBundleAdapter productBundleAdapter = createStubProductBundleAdapter(cachingService);

		final ProductBundleConstituentDTO constituentDTO = createProductBundleConstituentDto(GUID, PRODUCT_CODE2, createPriceAdjustmentDtos());

		final Product product = context.mock(Product.class);

		final ProductConstituentImpl productConstituentItem = new ProductConstituentImpl();
		productConstituentItem.setProduct(product);

		context.checking(new Expectations() {
			{
				allowing(bundleConstituent).getGuid();
				will(returnValue(GUID));

				allowing(productBundle).getConstituents();
				will(returnValue(Arrays.asList(bundleConstituent)));

				allowing(bundleConstituent).getConstituent();
				will(returnValue(productConstituentItem));

				allowing(product).getCode();
				will(returnValue(PRODUCT_CODE));
			}
		});

		productBundleAdapter.createOrUpdateBundleConstituent(productBundle, constituentDTO);
	}

	private ProductBundleAdapter createStubProductBundleAdapter(final CachingService cachingService) {
		final ElasticPath elasticpath = context.mock(ElasticPath.class);

		ProductBundleAdapter productBundleAdapter = new ProductBundleAdapter();
		productBundleAdapter.setBundleConstituentCodeTypeResolver(resolver);
		productBundleAdapter.setBundleConstituentFactory(constituentFactory);
		productBundleAdapter.setCachingService(cachingService);
		productBundleAdapter.setBundleValidator(new BundleValidatorImpl());
		productBundleAdapter.setElasticPath(elasticpath);

		shouldAllowProductConstituentBeanCreation();
		shouldAllowPriceAdjustmentBeanCreation(elasticpath);

		return productBundleAdapter;
	}

	private void shouldAllowProductConstituentBeanCreation() {
		context.checking(new Expectations() {
			{
				allowing(beanFactory).getBean("productConstituent");
				will(returnValue(new ProductConstituentImpl()));
			}
		});
	}

	private void shouldAllowPriceAdjustmentBeanCreation(final ElasticPath elasticpath) {
		context.checking(new Expectations() {
			{
				allowing(elasticpath).getBean(ContextIdNames.PRICE_ADJUSTMENT);
				will(returnValue(new PriceAdjustmentImpl()));
			}
		});
	}

	private ProductBundleConstituentDTO createProductBundleConstituentDto(final String guid,
			final String productBundleConstituentCodeValue,
			final List<PriceAdjustmentDto> priceAdjustmentDtos) {
		final ProductBundleConstituentDTO constituentDTO = new ProductBundleConstituentDTO();
		final ProductBundleConstituentCodeDTO codeDTO = new ProductBundleConstituentCodeDTO();
		codeDTO.setValue(productBundleConstituentCodeValue);
		codeDTO.setType(PRODUCT);
		constituentDTO.setCode(codeDTO);
		constituentDTO.setQuantity(1);
		constituentDTO.setOrdering(2);
		constituentDTO.setAdjustments(priceAdjustmentDtos);
		constituentDTO.setGuid(guid);
		return constituentDTO;
	}

	private List<PriceAdjustmentDto> createPriceAdjustmentDtos() {
		final PriceAdjustmentDto priceAdjustmentDto = new PriceAdjustmentDto(PRICELIST_GUID, PRICE_ADJUSTMENT_AMOUNT);
		List<PriceAdjustmentDto> priceAdjustments = new ArrayList<PriceAdjustmentDto>();
		priceAdjustments.add(priceAdjustmentDto);
		return priceAdjustments;
	}

	/**
	 * Test the pricing mechanism validation rule:
	 *  if a bundle contains as a constituent another bundle,
	 *   then the pricing mechanism of the constituent should match the parent.
	 */
	@Test
	public void testPricingMechanismValidationSucccesful() {
		final ConstituentItem constituentItem = context.mock(ConstituentItem.class);
		final ProductBundle innerProductBundle = context.mock(ProductBundle.class, "constituentBundle");
		boolean expectedExceptionThrown = false;

		context.checking(new Expectations() { {
			oneOf(bundleConstituent).getConstituent(); will(returnValue(constituentItem));
			oneOf(constituentItem).isBundle(); will(returnValue(Boolean.TRUE));
			oneOf(constituentItem).getProduct(); will(returnValue(innerProductBundle));
			oneOf(innerProductBundle).isCalculated(); will(returnValue(Boolean.TRUE));
			atLeast(1).of(productBundle).isCalculated(); will(returnValue(Boolean.TRUE));
		} });

		try {
			productBundleAdapter.checkPricingMechanismForBundleConstituent(productBundle, bundleConstituent);
		} catch (PopulationRollbackException e) {
			assertEquals("IE-10332", e.getIEMessage().getCode());
		}

		assertFalse("Expect the PopulationRollbackException", expectedExceptionThrown);
	}

	/**
	 * Test the pricing mechanism validation rule:
	 *  if a bundle contains as a constituent another bundle,
	 *   then the pricing mechanism of the constituent should match the parent.
	 */
	@Test
	public void testPricingMechanismValidationFails() {
		final ConstituentItem constituentItem = context.mock(ConstituentItem.class);
		final ProductBundle innerProductBundle = context.mock(ProductBundle.class, "constituentBundle");
		boolean expectedExceptionThrown = false;

		context.checking(new Expectations() { {
			oneOf(bundleConstituent).getConstituent(); will(returnValue(constituentItem));
			oneOf(constituentItem).isBundle(); will(returnValue(Boolean.TRUE));
			oneOf(constituentItem).getProduct(); will(returnValue(innerProductBundle));
			oneOf(innerProductBundle).isCalculated(); will(returnValue(Boolean.TRUE));
			atLeast(1).of(productBundle).isCalculated(); will(returnValue(Boolean.FALSE));
		} });

		try {
			productBundleAdapter.checkPricingMechanismForBundleConstituent(productBundle, bundleConstituent);
		} catch (PopulationRollbackException e) {
			expectedExceptionThrown = true;
			assertEquals("IE-10332", e.getIEMessage().getCode());
		}
		assertTrue("Expect the PopulationRollbackException", expectedExceptionThrown);
	}

	/**
	 * Test method for {@link ProductBundleAdapter#createDtoObject()}.
	 */
	@Test
	public void testCreateDtoObject() {
		assertNotNull(productBundleAdapter.createDtoObject());
	}

	/**
	 * Test method for {@link ProductBundleAdapter#createDomainObject()}.
	 */
	@Test (expected = UnsupportedOperationException.class)
	public void testCreateDomainObject() {
		productBundleAdapter.createDomainObject();
	}

	/**
	 * Create a map of the bundle constituent code type resolver similar to one that would be created via bean creation.
	 * @return
	 */
	private Map<String, String> createBundleConstituentCodeTypeResolver() {
		final Map<String, String> resolver = new HashMap<String, String>();
		resolver.put("com.elasticpath.domain.catalog.impl.ProductConstituentImpl", "product");
		resolver.put("com.elasticpath.domain.catalog.impl.ProductSkuConstituentImpl", "sku");
		return resolver;
	}

	private PaymentScheduleHelperImpl getPaymentScheduleHelper(final SkuOption frequencyOption) {
		final PaymentScheduleHelperImpl paymentScheduleHelper = new PaymentScheduleHelperImpl() {
        	@Override
            protected SkuOption getFrequencyOption(final Product product) {
                return frequencyOption;
            }
        };

		paymentScheduleHelper.setBeanFactory(beanFactory);

		context.checking(new Expectations() {
			{
				allowing(beanFactory).getBean(ContextIdNames.PAYMENT_SCHEDULE);
				will(returnValue(new PaymentScheduleImpl()));
			}
		});
		return paymentScheduleHelper;
	}

	/**
	 * Test finding a matching bundle constituent for a DTO with <code>null</code> GUID.
	 */
	@Test
	public void testFindMatchingBundleConstituentNullGuid() {
		ProductBundleConstituentDTO dto = new ProductBundleConstituentDTO();
		ProductBundle bundle = mockBundleWithTwoConstituents();

		assertNull("should not find any matching constituents, since the DTO GUID is missing.",
				productBundleAdapter.findMatchingBundleConstituent(bundle, dto));
	}

	/**
	 * Test finding a matching bundle constituent were the GUIDs don't match.
	 */
	@Test
	public void testFindMatchingBundleConstituentNotMatching() {
		ProductBundleConstituentDTO dto = new ProductBundleConstituentDTO();
		dto.setGuid(GUID);
		ProductBundle bundle = mockBundleWithTwoConstituents();

		assertNull("should not find any matching constituent, since the DTO GUID does not match.",
				productBundleAdapter.findMatchingBundleConstituent(bundle, dto));
	}

	/**
	 * Test finding a matching bundle constituent.
	 */
	@Test
	public void testFindMatchingBundleConstituent() {
		ProductBundleConstituentDTO dto = new ProductBundleConstituentDTO();
		dto.setGuid(GUID1);
		ProductBundle bundle = mockBundleWithTwoConstituents();

		BundleConstituent foundConstituent = productBundleAdapter.findMatchingBundleConstituent(bundle, dto);
		assertNotNull("should find a matching constituent.", foundConstituent);
		assertEquals("The found constituent should have the same GUID as the dto ", dto.getGuid(), foundConstituent.getGuid());
	}

	private ProductBundle mockBundleWithTwoConstituents() {
		final ProductBundle bundle = context.mock(ProductBundle.class, "bundle");
		final BundleConstituent constituent1 = context.mock(BundleConstituent.class, "constituent1");
		final BundleConstituent constituent2 = context.mock(BundleConstituent.class, "constituent2");

		context.checking(new Expectations() {
			{
				allowing(bundle).getConstituents();
					will(returnValue(Arrays.asList(constituent1, constituent2)));
				allowing(constituent1).getGuid();
					will(returnValue(GUID1));
				allowing(constituent2).getGuid();
					will(returnValue(GUID2));
			}
		});
		return bundle;
	}
}
