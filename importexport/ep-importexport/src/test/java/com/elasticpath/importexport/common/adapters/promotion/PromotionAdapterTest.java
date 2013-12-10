package com.elasticpath.importexport.common.adapters.promotion;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.LocaleUtils;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Test;

import com.elasticpath.common.dto.DisplayValue;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.ElasticPath;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.impl.CatalogImpl;
import com.elasticpath.domain.misc.LocalizedProperties;
import com.elasticpath.domain.misc.LocalizedPropertyValue;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.rules.RuleAction;
import com.elasticpath.domain.rules.RuleElement;
import com.elasticpath.domain.rules.RuleElementType;
import com.elasticpath.domain.rules.RuleScenarios;
import com.elasticpath.domain.rules.RuleSet;
import com.elasticpath.domain.rules.impl.CartAnySkuAmountDiscountActionImpl;
import com.elasticpath.domain.rules.impl.CartAnySkuPercentDiscountActionImpl;
import com.elasticpath.domain.rules.impl.PromotionRuleImpl;
import com.elasticpath.domain.rules.impl.RuleSetImpl;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.impl.StoreImpl;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.caching.CachingService;
import com.elasticpath.importexport.common.dto.promotion.cart.ActionDTO;
import com.elasticpath.importexport.common.dto.promotion.cart.AvailabilityDTO;
import com.elasticpath.importexport.common.dto.promotion.cart.PromotionDTO;
import com.elasticpath.importexport.common.exception.runtime.PopulationRollbackException;
import com.elasticpath.importexport.common.exception.runtime.PopulationRuntimeException;
import com.elasticpath.service.rules.RuleSetService;

/**
 * Tests for PromotionAdapter.
 */
@SuppressWarnings({ "PMD.TooManyMethods" })
public class PromotionAdapterTest {

	private static final Date START_DATE = new Date();

	private static final Date END_DATE = new Date(START_DATE.getTime() + 2);

	private static final String STORE_CODE = "StoreCode";

	private static final String CATALOG_CODE = "CatalogCode";

	@org.junit.Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	
	private final Rule rule = context.mock(Rule.class);

	private final PromotionAdapter promotionAdapter = new PromotionAdapter();

	private final ElasticPath elasticpath = context.mock(ElasticPath.class);

	private final CachingService cachingService = context.mock(CachingService.class);

	@SuppressWarnings("unchecked")
	private final DomainAdapter<RuleElement, ActionDTO> actionAdapter = context.mock(DomainAdapter.class);

	private int numberOfCalls;

	private final LocalizedProperties localizedProperties = context.mock(LocalizedProperties.class);;

	private final Map<String, LocalizedPropertyValue> localizedPropertiesMap = new HashMap<String, LocalizedPropertyValue>();

	private final LocalizedPropertyValue value1 = context.mock(LocalizedPropertyValue.class, "value1");

	private final LocalizedPropertyValue value2 = context.mock(LocalizedPropertyValue.class, "value2");

	private final DisplayValue displayValue1 = new DisplayValue("fr", "frValue");

	private final DisplayValue displayValue2 = new DisplayValue("en", "enValue");

	@Before
	public void setUp() throws Exception {
		promotionAdapter.setElasticPath(elasticpath);
		promotionAdapter.setCachingService(cachingService);
		promotionAdapter.setActionAdapter(actionAdapter);

		localizedPropertiesMap.put(Rule.LOCALIZED_PROPERTY_DISPLAY_NAME + "_" + displayValue1.getLanguage(), value1);
		localizedPropertiesMap.put(Rule.LOCALIZED_PROPERTY_DISPLAY_NAME + "_" + displayValue2.getLanguage(), value2);
	}

	/**
	 * Tests high level call of <code>PromotionDTO</code> population method.
	 * It should set several fields and call other methods populating parts of promotion DTO.
	 */
	@Test
	public void testPopulateDTO() {
		final PromotionDTO target = new PromotionDTO();
		final PromotionAdapter promotionAdapter = new PromotionAdapter() {
			@Override
			void populatePromotionDTOType(final PromotionDTO promotionDTO, final Catalog catalog, final Store store) {
				++numberOfCalls;
			}
			@Override
			AvailabilityDTO createAvailabilityDTO(final Rule rule) {
				++numberOfCalls;
				return null;
			}
			@Override
			List<ActionDTO> createElementDTOList(final Rule source) {
				++numberOfCalls;
				return null;
			}
		};

		final String ruleName = "Christmas discount";
		final String ruleCode = "PROMOTION_CODE";
		final String ruleDescription = "Great discount";

		context.checking(new Expectations() { {
			oneOf(rule).getCatalog(); will(returnValue(null));
			oneOf(rule).getStore(); will(returnValue(null));
			oneOf(rule).getName(); will(returnValue(ruleName));
			oneOf(rule).getCode(); will(returnValue(ruleCode));
			oneOf(rule).getDescription(); will(returnValue(ruleDescription));
			allowing(rule).getLocalizedProperties(); will(returnValue(localizedProperties));

			oneOf(localizedProperties).getLocalizedPropertiesMap();
			will(returnValue(localizedPropertiesMap));

			oneOf(localizedProperties).getPropertyNameFromKey("promotionDisplayName_en");
			will(returnValue("promotionDisplayName"));

			oneOf(localizedProperties).getPropertyNameFromKey("promotionDisplayName_fr");
			will(returnValue("promotionDisplayName"));

			oneOf(localizedProperties).getLocaleFromKey("promotionDisplayName_en");
			will(returnValue(LocaleUtils.toLocale("en")));

			oneOf(localizedProperties).getLocaleFromKey("promotionDisplayName_fr");
			will(returnValue(LocaleUtils.toLocale("fr")));

			oneOf(value1).getValue(); will(returnValue(displayValue1.getValue()));
			oneOf(value2).getValue(); will(returnValue(displayValue2.getValue()));
		} });

		numberOfCalls = 0;
		final int expectedNumberOfCalls = 3;
		promotionAdapter.populateDTO(rule, target);

		assertEquals(ruleName, target.getName());
		assertEquals(ruleCode, target.getCode());
		assertEquals(ruleDescription, target.getDescription());
		assertEquals(expectedNumberOfCalls, numberOfCalls);

		List<DisplayValue> displayNames = target.getDisplayNames();
		assertEquals("target should have two display names", 2, displayNames.size());

		boolean value1Found = false;
		boolean value2Found = false;
		for (int i = 0; i < displayNames.size(); i++) {
			DisplayValue value = displayNames.get(i);
			if (value.getLanguage().equals("fr")
					&& value.getValue().equals(displayValue1.getValue())) {
				value1Found = true;
			} else if (value.getLanguage().equals("en")
					&& value.getValue().equals(displayValue2.getValue())) {
				value2Found = true;
			}
		}

		assertTrue("both display name should be found", value1Found && value2Found);
	}

	/**
	 * In neither store nor catalog is provided then promotion type is undefined.
	 * It causes an exception
	 */
	@Test(expected = PopulationRuntimeException.class)
	public void testPopulatePromotionDTOType01() {
		promotionAdapter.populatePromotionDTOType(new PromotionDTO(), null, null);
	}

	/**
	 * If store is provided and catalog is not provided then promotion type is SHOPPING_CART_TYPE.
	 */
	@Test
	public void testPopulatePromotionDTOType02() {
		final PromotionDTO promotionDto = new PromotionDTO();
		final Store store = context.mock(Store.class);

		context.checking(new Expectations() { {
			oneOf(store).getCode(); will(returnValue(STORE_CODE));
		} });

		promotionAdapter.populatePromotionDTOType(promotionDto, null, store);

		assertEquals(STORE_CODE, promotionDto.getStoreCode());
		assertEquals(null, promotionDto.getCatalogCode());
		assertEquals(PromotionAdapter.SHOPPING_CART_TYPE, promotionDto.getType());
	}

	/**
	 * If catalog is provided and store is not provided then promotion type is CATALOG_TYPE.
	 */
	@Test
	public void testPopulatePromotionDTOType03() {
		final PromotionDTO promotionDto = new PromotionDTO();
		final Catalog catalog = context.mock(Catalog.class);

		context.checking(new Expectations() { {
			oneOf(catalog).getCode(); will(returnValue(CATALOG_CODE));
		} });

		promotionAdapter.populatePromotionDTOType(promotionDto, catalog, null);

		assertEquals(CATALOG_CODE, promotionDto.getCatalogCode());
		assertEquals(null, promotionDto.getStoreCode());
		assertEquals(PromotionAdapter.CATALOG_TYPE, promotionDto.getType());
	}

	/**
	 * If both store and catalog are provided then promotion type is SHOPPING_CART_TYPE both codes should be set.
	 */
	@Test
	public void testPopulatePromotionDTOType04() {
		final PromotionDTO promotionDto = new PromotionDTO();
		final Catalog catalog = context.mock(Catalog.class);
		final Store store = context.mock(Store.class);

		context.checking(new Expectations() { {
			oneOf(catalog).getCode(); will(returnValue(CATALOG_CODE));
			oneOf(store).getCode(); will(returnValue(STORE_CODE));
		} });

		promotionAdapter.populatePromotionDTOType(promotionDto, catalog, store);

		assertEquals(CATALOG_CODE, promotionDto.getCatalogCode());
		assertEquals(STORE_CODE, promotionDto.getStoreCode());
		assertEquals(PromotionAdapter.SHOPPING_CART_TYPE, promotionDto.getType());
	}

	/**
	 * Tests createAvailabilityDTO.
	 */
	@Test
	public void testCreateAvailabilityDTO() {
		context.checking(new Expectations() { {
			oneOf(rule).isEnabled();    will(returnValue(true));
			oneOf(rule).getStartDate(); will(returnValue(START_DATE));
			oneOf(rule).getEndDate();   will(returnValue(END_DATE));
		} });

		AvailabilityDTO availabilityDTO = promotionAdapter.createAvailabilityDTO(rule);

		assertEquals(true, availabilityDTO.getEnabled());
		assertEquals(START_DATE, availabilityDTO.getEnableDate());
		assertEquals(END_DATE, availabilityDTO.getDisableDate());
	}

	/**
	 * Tests that method createElementDTOList calls populateDTOAction method with the list of actions from the source Rule.
	 */
	@Test
	public void testCreateElementDTOList() {

		final PromotionAdapter promotionAdapter = new PromotionAdapter() {
			@Override
			void populateDTOAction(final List<ActionDTO> actions, final Set<RuleAction> ruleActions) {
				++numberOfCalls;
			}
		};

		context.checking(new Expectations() { {
			oneOf(rule).getActions();
		} });

		numberOfCalls = 0;
		promotionAdapter.createElementDTOList(rule);
		assertEquals(1, numberOfCalls);
	}

	/**
	 * Tests one step of ActionDTO population.
	 */
	@Test
	public void testPopulateDTOAction() {
		final RuleAction firstRuleAction = context.mock(RuleAction.class);
		final ActionDTO firstActionDto = new ActionDTO();
		final Set<RuleAction> ruleActions = new HashSet<RuleAction>();
		ruleActions.add(firstRuleAction);
		final List<ActionDTO> actions = new ArrayList<ActionDTO>();

		context.checking(new Expectations() { {
			oneOf(actionAdapter).createDtoObject(); will(returnValue(firstActionDto));
			oneOf(actionAdapter).populateDTO(with(same(firstRuleAction)), with(same(firstActionDto)));
		} });

		promotionAdapter.populateDTOAction(actions, ruleActions);
		assertThat("Actions list should contain exactly one action", actions, hasSize(1));
		assertEquals("Actions list should contain the firstActionDto", firstActionDto, actions.get(0));
	}

	/**
	 * Tests createDtoObject.
	 */
	@Test
	public void testCreateDtoObject() {
		assertEquals(PromotionDTO.class, promotionAdapter.createDtoObject().getClass());
	}

	/**
	 * Tests createDomainObject.
	 */
	@Test
	public void testCreateDomainObject() {
		context.checking(new Expectations() { {
			oneOf(elasticpath).getBean(ContextIdNames.PROMOTION_RULE); will(returnValue(new PromotionRuleImpl()));
		} });

		assertNotNull(promotionAdapter.createDomainObject());
	}

	/**
	 * Store code must not be null.
	 */
	@Test(expected = PopulationRollbackException.class)
	public void testFindStoreByNullCode() {
		promotionAdapter.findStoreByCode(null);
	}

	/**
	 * Tests that <code>PromotionAdapter</code> uses <code>CachingService</code> to search for store.
	 */
	@Test
	public void testFindStoreByCode() {
		final String storeCode = "SNAPITUP";
		final Store store = new StoreImpl();

		context.checking(new Expectations() { {
			oneOf(cachingService).findStoreByCode(storeCode); will(returnValue(store));
		} });

		assertEquals(store, promotionAdapter.findStoreByCode(storeCode));
	}

	/**
	 * Catalog code must not be null.
	 */
	@Test(expected = PopulationRollbackException.class)
	public void testFindCatalogBNullyCode() {
		promotionAdapter.findCatalogByCode(null);
	}

	/**
	 * Tests that <code>PromotionAdapter</code> uses <code>CachingService</code> to search for catalog.
	 */
	@Test
	public void testFindCatalogByCode() {
		final String catalogCode = "CopperwoodAndCo";
		final Catalog catalog = new CatalogImpl();

		context.checking(new Expectations() { {
			oneOf(cachingService).findCatalogByCode(catalogCode); will(returnValue(catalog));
		} });

		assertEquals(catalog, promotionAdapter.findCatalogByCode(catalogCode));
	}

	/**
	 * Checks that <code>PromotionAdapter</code> throws an exception if <code>RuleAction</code> cannot be created.
	 */
	@Test(expected = PopulationRollbackException.class)
	public void testCreateRuleActionException() {
		final String actionType = RuleElementType.CART_ANY_SKU_AMOUNT_DISCOUNT_ACTION.getPropertyKey();

		context.checking(new Expectations() { {
			oneOf(elasticpath).getBean(actionType); will(returnValue(null));
		} });

		promotionAdapter.createRuleAction(actionType);
	}

	/**
	 * Checks that <code>PromotionAdapter</code> returns <code>RuleAction</code> instantiated by bean factory.
	 */
	@Test
	public void testCreateRuleAction() {
		final String actionType = RuleElementType.CART_ANY_SKU_AMOUNT_DISCOUNT_ACTION.getPropertyKey();
		final RuleAction ruleAction = new CartAnySkuAmountDiscountActionImpl();

		context.checking(new Expectations() { {
			oneOf(elasticpath).getBean(actionType); will(returnValue(ruleAction));
		} });

		assertEquals(ruleAction, promotionAdapter.createRuleAction(actionType));
	}

	/**
	 * Tests population of <code>AvailabilityDTO</code> object.
	 *
	 * @throws ParseException if date cannot be parsed successfully
	 */
	@Test
	public void testPopulateDomainAvailability() throws ParseException {
		final Date enableDate = new Date();
		final Date disableDate = new Date();
		AvailabilityDTO availabilityDto = new AvailabilityDTO();
		availabilityDto.setEnabled(true);
		availabilityDto.setEnableDate(enableDate);
		availabilityDto.setDisableDate(disableDate);

		PromotionDTO promotionDto = new PromotionDTO();
		promotionDto.setAvailability(availabilityDto);

		context.checking(new Expectations() { {
			oneOf(rule).setEnabled(true);
			oneOf(rule).setStartDate(with(same(enableDate)));
			oneOf(rule).setEndDate(with(same(disableDate)));
		} });

		promotionAdapter.populateDomainAvailability(promotionDto, rule);
	}

	/**
	 * Tests population of <code>Rule</code> with <code>RuleElement</code> objects.
	 */
	@Test
	public void testPopulatedDomainActions() {
		final RuleAction ruleAction1 = new CartAnySkuAmountDiscountActionImpl();
		final RuleAction ruleAction2 = new CartAnySkuPercentDiscountActionImpl();

		final ActionDTO actionDto1 = new ActionDTO();
		actionDto1.setType(RuleElementType.CART_ANY_SKU_AMOUNT_DISCOUNT_ACTION.getPropertyKey());
		final ActionDTO actionDto2 = new ActionDTO();
		actionDto2.setType(RuleElementType.CART_ANY_SKU_PERCENT_DISCOUNT_ACTION.getPropertyKey());

		final List<ActionDTO> actionDtoList = Arrays.asList(actionDto1, actionDto2);

		context.checking(new Expectations() { {
			oneOf(elasticpath).getBean(RuleElementType.CART_ANY_SKU_AMOUNT_DISCOUNT_ACTION.getPropertyKey());
			will(returnValue(ruleAction1));
			oneOf(actionAdapter).populateDomain(with(same(actionDto1)), with(same(ruleAction1)));
			oneOf(rule).addRuleElement(ruleAction1);

			oneOf(elasticpath).getBean(RuleElementType.CART_ANY_SKU_PERCENT_DISCOUNT_ACTION.getPropertyKey());
			will(returnValue(ruleAction2));
			oneOf(actionAdapter).populateDomain(with(same(actionDto2)), with(same(ruleAction2)));
			oneOf(rule).addRuleElement(ruleAction2);
		} });

		promotionAdapter.populatedDomainActions(rule, actionDtoList);
	}

	/**
	 * Tests that if DTO object is of type CATALOG then only catalog should be set.
	 */
	@Test
	public void testPopulatePromotionDomainType1() {
		final Catalog catalog = new CatalogImpl();
		final String catalogCode = "CopperwoodAndCo";
		final PromotionDTO promotionDto = new PromotionDTO();
		promotionDto.setCatalogCode(catalogCode);
		promotionDto.setType(PromotionAdapter.CATALOG_TYPE);

		context.checking(new Expectations() { {
			oneOf(cachingService).findCatalogByCode(catalogCode); will(returnValue(catalog));
			oneOf(rule).setCatalog(with(same(catalog)));
		} });

		promotionAdapter.populatePromotionDomainType(promotionDto, rule);
	}

	/**
	 * Tests that if DTO object is of type SHOPPING_CART then only store should be set.
	 */
	@Test
	public void testPopulatePromotionDomainType2() {
		final Store store = new StoreImpl();
		final String storeCode = "SNAPITUP";
		final PromotionDTO promotionDto = new PromotionDTO();
		promotionDto.setStoreCode(storeCode);
		promotionDto.setType(PromotionAdapter.SHOPPING_CART_TYPE);

		context.checking(new Expectations() { {
			oneOf(cachingService).findStoreByCode(storeCode); will(returnValue(store));
			oneOf(rule).setStore(with(same(store)));
		} });

		promotionAdapter.populatePromotionDomainType(promotionDto, rule);
	}

	/**
	 * Tests that if neither Catalog nor Shopping Cart type is set then <code>PopulationRuntimeException</code> is thrown.
	 */
	@Test(expected = PopulationRollbackException.class)
	public void testPopulatePromotionDomainTypeException() {
		final PromotionDTO promotionDto = new PromotionDTO();
		promotionDto.setType("WAREHOUSE_TYPE");		// something meaningless

		promotionAdapter.populatePromotionDomainType(promotionDto, rule);
	}

	/**
	 * Tests that <code>Rule</code> is populated with <code>RuleSet</code> for Shopping Cart.
	 */
	@Test
	public void testPopulateRuleSet() {
		final PromotionDTO promotionDTO = new PromotionDTO();
		promotionDTO.setType(PromotionAdapter.SHOPPING_CART_TYPE);

		final RuleSetService ruleSetService = context.mock(RuleSetService.class);
		promotionAdapter.setRuleSetService(ruleSetService);
		final RuleSet ruleSet = new RuleSetImpl();

		context.checking(new Expectations() { {
			oneOf(ruleSetService).findByScenarioId(RuleScenarios.CART_SCENARIO);
			will(returnValue(ruleSet));
			oneOf(rule).setRuleSet(with(same(ruleSet)));
		} });

		promotionAdapter.populateRuleSet(promotionDTO, rule);

		promotionDTO.setType(PromotionAdapter.CATALOG_TYPE);

		context.checking(new Expectations() { {
			oneOf(ruleSetService).findByScenarioId(RuleScenarios.CATALOG_BROWSE_SCENARIO);
			will(returnValue(ruleSet));
			oneOf(rule).setRuleSet(with(same(ruleSet)));
		} });

		promotionAdapter.populateRuleSet(promotionDTO, rule);
	}

	/**
	 * Verifies that required fields are set to <code>Rule</code> as a result of population.
	 */
	@Test
	public void testPopulateDomain() {
		final PromotionAdapter promotionAdapter = new PromotionAdapter() {
			/**
			 * This method is already covered by another test.
			 */
			@Override
			void populatePromotionDomainType(final PromotionDTO source, final Rule target) {
				// do nothing.
			}

			/**
			 * This method is already covered by another test.
			 */
			@Override
			void populateDomainAvailability(final PromotionDTO source, final Rule target) {
				// do nothing.
			}

			/**
			 * This method is already covered by another test.
			 */
			@Override
			public void populatedDomainActions(final Rule rule, final List<ActionDTO> actions) {
				// do nothing.
			}

			/**
			 * This method is already covered by another test.
			 */
			@Override
			void populateRuleSet(final PromotionDTO promotionDTO, final Rule target) {
				// do nothing.
			}
		};

		final PromotionDTO promotionDto = new PromotionDTO();
		final String promotionName = "Christmas discount";
		promotionDto.setName(promotionName);
		final String promotionCode = "PROMO_1234";
		promotionDto.setCode(promotionCode);
		final String description = "Joyful Christmas discount for everyone";
		promotionDto.setDescription(description);

		List<DisplayValue> displayNames = new ArrayList<DisplayValue>();
		displayNames.add(displayValue1);
		displayNames.add(displayValue2);
		promotionDto.setDisplayNames(displayNames);

		context.checking(new Expectations() { {
			oneOf(rule).setName(promotionName);
			oneOf(rule).setCode(promotionCode);
			oneOf(rule).setDescription(description);
			oneOf(rule).getLocalizedProperties(); will(returnValue(localizedProperties));
			oneOf(localizedProperties).setValue(Rule.LOCALIZED_PROPERTY_DISPLAY_NAME,
					LocaleUtils.toLocale("fr"), displayValue1.getValue());
			oneOf(localizedProperties).setValue(Rule.LOCALIZED_PROPERTY_DISPLAY_NAME,
					LocaleUtils.toLocale("en"), displayValue2.getValue());
		} });

		promotionAdapter.populateDomain(promotionDto, rule);
	}
}
