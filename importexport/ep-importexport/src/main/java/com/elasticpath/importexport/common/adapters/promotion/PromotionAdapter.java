package com.elasticpath.importexport.common.adapters.promotion;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.LocaleUtils;

import com.elasticpath.common.dto.DisplayValue;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.misc.LocalizedProperties;
import com.elasticpath.domain.misc.LocalizedPropertyValue;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.rules.RuleAction;
import com.elasticpath.domain.rules.RuleElement;
import com.elasticpath.domain.rules.RuleScenarios;
import com.elasticpath.domain.rules.RuleSet;
import com.elasticpath.domain.rules.impl.AbstractRuleImpl;
import com.elasticpath.domain.sellingcontext.SellingContext;
import com.elasticpath.domain.store.Store;
import com.elasticpath.importexport.common.adapters.AbstractDomainAdapterImpl;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.adapters.tag.SellingContextAdapter;
import com.elasticpath.importexport.common.dto.promotion.cart.ActionDTO;
import com.elasticpath.importexport.common.dto.promotion.cart.AvailabilityDTO;
import com.elasticpath.importexport.common.dto.promotion.cart.PromotionDTO;
import com.elasticpath.importexport.common.dto.tag.SellingContextDTO;
import com.elasticpath.importexport.common.exception.runtime.PopulationRollbackException;
import com.elasticpath.importexport.common.exception.runtime.PopulationRuntimeException;
import com.elasticpath.importexport.importer.importers.RuleWrapper;
import com.elasticpath.service.rules.RuleSetService;

/**
 * The implementation of <code>DomainAdapter</code> interface.<br>
 * It is responsible for data transformation between <code>Rule</code> and <code>PromotionDTO</code> objects.
 */
public class PromotionAdapter extends AbstractDomainAdapterImpl<Rule, PromotionDTO> {

	/** Type of promotion containing only store code or both store and catalog codes. */
	public static final String SHOPPING_CART_TYPE = "Cart";

	/** Type of promotion containing only catalog code but not store code. */
	public static final String CATALOG_TYPE = "Catalog";

	private DomainAdapter<RuleElement, ActionDTO> actionAdapter;

	private RuleSetService ruleSetService;

	private SellingContextAdapter sellingContextAdapter;
	@Override
	public void populateDTO(final Rule source, final PromotionDTO target) {
		populatePromotionDTOType(target, source.getCatalog(), source.getStore());

		target.setName(source.getName());
		
		populateDTODisplayNameFromRule(source, target);
		
		target.setCode(source.getCode());
		target.setDescription(source.getDescription());

		target.setAvailability(createAvailabilityDTO(source));
		target.setActions(createElementDTOList(source));
		
		if (source instanceof AbstractRuleImpl) {			
			SellingContext sellingContext = source.getSellingContext();
			if (sellingContext == null) {
				return;
			}		
			AbstractRuleImpl promotionRule = (AbstractRuleImpl) source;
			SellingContextDTO sellingContextDTO = sellingContextAdapter.createDtoObject();			
			sellingContextAdapter.populateDTO(promotionRule.getSellingContext(), sellingContextDTO);
			target.setSellingContext(sellingContextDTO);
		}
	}

	private void populateRuleDisplayNameFromDTO(final PromotionDTO source, final Rule targetRule) {
		LocalizedProperties localizedProperties = targetRule.getLocalizedProperties();
		for (DisplayValue displayValue : source.getDisplayNames()) {
			try {
				Locale locale = LocaleUtils.toLocale(displayValue.getLanguage());
				if (!LocaleUtils.isAvailableLocale(locale)) {
					throw new IllegalArgumentException();
				}
				
				localizedProperties.setValue(Rule.LOCALIZED_PROPERTY_DISPLAY_NAME, locale, displayValue.getValue());
			} catch (IllegalArgumentException exception) {
				throw new PopulationRuntimeException("IE-10306", exception, displayValue.getLanguage(), displayValue.getValue());
			}
		}
	}
	
	private void populateDTODisplayNameFromRule(final Rule source, final PromotionDTO target) {
		List<DisplayValue> displayNames = new ArrayList<DisplayValue>();
		LocalizedProperties localizedProperties = source.getLocalizedProperties();
		Map<String, LocalizedPropertyValue> localizedPropertiesMap = localizedProperties.getLocalizedPropertiesMap();
		for (String key : localizedPropertiesMap.keySet()) {
			String propertyName = localizedProperties.getPropertyNameFromKey(key);
			if (propertyName.equals(Rule.LOCALIZED_PROPERTY_DISPLAY_NAME)) {
				try {
					DisplayValue displayValue = new DisplayValue();
					displayValue.setLanguage(localizedProperties.getLocaleFromKey(key).toString());
					displayValue.setValue(localizedPropertiesMap.get(key).getValue());
					displayNames.add(displayValue);
				} catch (IllegalArgumentException exception) {
					throw new PopulationRuntimeException("IE-10306", exception, key, localizedPropertiesMap.get(key).getValue());
				}
			}
		}
		target.setDisplayNames(displayNames);
	}

	/**
	 * Populates promotion type related fields of <code>PromotionDTO</code>.
	 * 
	 * @param promotionDTO promotion DTO
	 * @param catalog catalog
	 * @param store store
	 */
	@SuppressWarnings({ "PMD.ConfusingTernary" })
	void populatePromotionDTOType(final PromotionDTO promotionDTO, final Catalog catalog, final Store store) {
		if (catalog != null && store != null) {
			promotionDTO.setType(SHOPPING_CART_TYPE);
			promotionDTO.setCatalogCode(catalog.getCode());
			promotionDTO.setStoreCode(store.getCode());
		} else if (catalog != null) {
			promotionDTO.setType(CATALOG_TYPE);
			promotionDTO.setCatalogCode(catalog.getCode());
		} else if (store != null) {
			promotionDTO.setType(SHOPPING_CART_TYPE);
			promotionDTO.setStoreCode(store.getCode());
		} else {
			throw new PopulationRuntimeException("IE-10702");
		}
	}

	/**
	 * Creates AvailabilityDTO using Rule.
	 * 
	 * @param rule the Rule
	 * @return RuleAvailabilityDTO instance
	 */
	AvailabilityDTO createAvailabilityDTO(final Rule rule) {
		final AvailabilityDTO availability = new AvailabilityDTO();

		availability.setEnabled(rule.isEnabled());
		availability.setEnableDate(rule.getStartDate());
		availability.setDisableDate(rule.getEndDate());
		return availability;
	}

	/**
	 * Creates populated RuleElementDTO List from the Set of RuleElements.
	 * 
	 * @param source set of RuleElements
	 * @return List of RuleElementDTO
	 */
	List<ActionDTO> createElementDTOList(final Rule source) {
		final List<ActionDTO> elements = new ArrayList<ActionDTO>();

		populateDTOAction(elements, source.getActions());

		return elements;
	}

	/**
	 * Populates <code>ActionDTO</code> List from Set of <code>RuleAction</code>.
	 * 
	 * @param actions List of ActionDTO
	 * @param ruleActions Set of RuleAction
	 */
	void populateDTOAction(final List<ActionDTO> actions, final Set<RuleAction> ruleActions) {
		for (final RuleAction ruleAction : ruleActions) {
			final ActionDTO actionDTO = actionAdapter.createDtoObject();

			actionAdapter.populateDTO(ruleAction, actionDTO);

			actions.add(actionDTO);
		}
	}

	@Override
	public void populateDomain(final PromotionDTO source, final Rule rule) {
		Rule target = null;
		if (rule instanceof RuleWrapper) {
			target = ((RuleWrapper) rule).getUpdatedRule();
		} else {
			target = rule;
		}
		
		populatePromotionDomainType(source, target);

		target.setName(source.getName());

		populateRuleDisplayNameFromDTO(source, target);
		
		target.setCode(source.getCode());
		target.setDescription(source.getDescription());

		populateDomainAvailability(source, target);

		populatedDomainActions(target, source.getActions());

		populateRuleSet(source, target);
		
		if (target instanceof AbstractRuleImpl) {
			SellingContextDTO sellingContextDTO = source.getSellingContext(); 
			if (sellingContextDTO == null) {
				return;
			}
				
			SellingContext sellingContext = sellingContextAdapter.getDomainObject(sellingContextDTO.getGuid());
			sellingContextAdapter.populateDomain(sellingContextDTO, sellingContext);
			target.setSellingContext(sellingContext);
		}
	}

	/**
	 * Adds <code>RuleSet</code> to the given <code>Rule</code>.
	 * 
	 * @param source the source promotion dto
	 * @param target <code>Rule</code> to populate
	 */
	void populateRuleSet(final PromotionDTO source, final Rule target) {
		final String type = source.getType();
		int scenarioId = -1;
		if (CATALOG_TYPE.equals(type)) {
			scenarioId = RuleScenarios.CATALOG_BROWSE_SCENARIO;
		} else if (SHOPPING_CART_TYPE.equals(type)) {
			scenarioId = RuleScenarios.CART_SCENARIO;
		} else {
			throw new PopulationRollbackException("IE-10703", type);
		}

		RuleSet ruleSet = ruleSetService.findByScenarioId(scenarioId);
		target.setRuleSet(ruleSet);
	}

	/**
	 * Sets domain type to promotion depending on availability of Catalog / Store codes.
	 * 
	 * @param source source DTO object
	 * @param target target domain <code>Rule</code> object.
	 */
	void populatePromotionDomainType(final PromotionDTO source, final Rule target) {
		if (CATALOG_TYPE.equals(source.getType())) {
			target.setCatalog(findCatalogByCode(source.getCatalogCode()));
		} else if (SHOPPING_CART_TYPE.equals(source.getType())) {
			target.setStore(findStoreByCode(source.getStoreCode()));
		} else {
			throw new PopulationRollbackException("IE-10702");
		}
	}

	/**
	 * Finds <code>Store</code> by code.
	 * 
	 * @param storeCode store code
	 * @return <code>Store</code> retrieved from cache
	 */
	Store findStoreByCode(final String storeCode) {
		if (storeCode == null) {
			throw new PopulationRollbackException("IE-10704");
		}
		return getCachingService().findStoreByCode(storeCode);
	}

	/**
	 * Finds <code>Catalog</code> by code.
	 * 
	 * @param catalogCode catalog code
	 * @return <code>Catalog</code> retrieved from cache
	 */
	Catalog findCatalogByCode(final String catalogCode) {
		if (catalogCode == null) {
			throw new PopulationRollbackException("IE-10705");
		}
		return getCachingService().findCatalogByCode(catalogCode);
	}

	/**
	 * Populates Domain availability (StartDate, EndDate, Enablement).
	 * 
	 * @param source the PromotionDTO
	 * @param target the Rule Domain object.
	 */
	void populateDomainAvailability(final PromotionDTO source, final Rule target) {
		final AvailabilityDTO availability = source.getAvailability();

		target.setStartDate(availability.getEnableDate());
		target.setEndDate(availability.getDisableDate());
		target.setEnabled(availability.getEnabled());
	}

	/**
	 * Populates the list of rule element DTO objects.
	 * 
	 * @param rule the <code>Rule</code> to populate
	 * @param actions DTO objects to populate from
	 */
	public void populatedDomainActions(final Rule rule, final List<ActionDTO> actions) {
		for (final ActionDTO actionDTO : actions) {
			final RuleAction ruleAction = createRuleAction(actionDTO.getType());

			actionAdapter.populateDomain(actionDTO, ruleAction);

			rule.addRuleElement(ruleAction);
		}
	}

	/**
	 * Creates <code>RuleAction</code> by its type.
	 * 
	 * @param actionType action type. See <code>RuleElementType</code>
	 * @return instantiated <code>RuleAction</code>
	 */
	RuleAction createRuleAction(final String actionType) {
		final RuleAction ruleAction = getBean(actionType);

		if (ruleAction == null) {
			throw new PopulationRollbackException("IE-10706");
		}

		return ruleAction;
	}

	@Override
	public PromotionDTO createDtoObject() {
		return new PromotionDTO();
	}

	@Override
	public Rule createDomainObject() {
		return getBean(ContextIdNames.PROMOTION_RULE);
	}

	/**
	 * @param actionAdapter the actionAdapter to set
	 */
	public void setActionAdapter(final DomainAdapter<RuleElement, ActionDTO> actionAdapter) {
		this.actionAdapter = actionAdapter;
	}

	/**
	 * @param ruleSetService the ruleSetService to set
	 */
	public void setRuleSetService(final RuleSetService ruleSetService) {
		this.ruleSetService = ruleSetService;
	}

	/** 
	 * @param sellingContextAdapter selling context adapter to be set.
	 */
	public void setSellingContextAdapter(final SellingContextAdapter sellingContextAdapter) {
		this.sellingContextAdapter = sellingContextAdapter;
	}
	
}
