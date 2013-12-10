package com.elasticpath.test.persister;

import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.DefaultValueRemovalForbiddenException;
import com.elasticpath.domain.misc.LocalizedPropertyValue;
import com.elasticpath.domain.misc.impl.ShippingServiceLevelLocalizedPropertyValueImpl;
import com.elasticpath.domain.shipping.ShippingCostCalculationMethod;
import com.elasticpath.domain.shipping.ShippingCostCalculationParameter;
import com.elasticpath.domain.shipping.ShippingServiceLevel;
import com.elasticpath.domain.shipping.impl.ShippingCostCalculationParameterImpl;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.StoreState;
import com.elasticpath.domain.store.StoreType;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.shipping.ShippingRegionService;
import com.elasticpath.service.shipping.ShippingServiceLevelService;
import com.elasticpath.service.store.StoreService;

/**
 * Allow to save ShippingServiceLevels into a test database.
 */
public class ShippingServiceLevelTestPersister {
	
	private static final String COMMA_DELIM = ",";

	private static final String STORE_BEAN = "store";

	private final BeanFactory beanFactory;
	
	private final ShippingServiceLevelService shippingServiceLevelService;
	
	private final ShippingRegionService shippingRegionService;
	
	private final StoreService storeService;
	
	
	/**
	 * Construct ShippingServiceLevelService, ShippingRegionService and StoreService all used
	 * later by this class.
	 * @param beanFactory - elastic path bean factory. gets beans.
	 */
	public ShippingServiceLevelTestPersister(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
		shippingServiceLevelService = this.beanFactory.getBean(ContextIdNames.SHIPPING_SERVICE_LEVEL_SERVICE);
		shippingRegionService = this.beanFactory.getBean(ContextIdNames.SHIPPING_REGION_SERVICE);
		storeService = this.beanFactory.getBean(ContextIdNames.STORE_SERVICE);
	}

	/**
	 * persist the passed in object.
	 * @param shippingServiceLevel shippingServiceLevel to persist
	 * @return the persisted shippingServiceLevel
	 */
	public ShippingServiceLevel persistShippingServiceLevel(ShippingServiceLevel shippingServiceLevel) {
		return shippingServiceLevelService.add(shippingServiceLevel);
	}
	
	/**
	 * Create and persist a ShippingServiceLevel instance using the passed in vars and some 
	 * static ones hard coded into this class.
	 * @param storeCode storeCode
	 * @param shippingRegionName shippingRegionName
	 * @param levelCode levelCode
	 * @param localizedNames localizedNames 
	 * @param carrier carrier
	 * @param calculationMethod calculationMethod
	 * @param calculationMethodProperty calculationMethodProperty
	 * @param calculationMethodValue calculationMethodValue
	 * @param currency currency
	 * @param active active
	 * @return a ShippingServiceLevel instance
	 */
	
	public ShippingServiceLevel createAndPersistShippingServiceLevel(final String storeCode, final String storeLocales, 
			final String shippingRegionName, 
			final String levelCode, final String localizedNames, final String carrier,
			final String calculationMethod, final String calculationMethodProperty, 
			final String calculationMethodValue, final String currency, final String active, boolean persistStore) {
		
		ShippingServiceLevel shippingServiceLevel = createShippingServiceLevel(storeCode, storeLocales, shippingRegionName, 
			levelCode, localizedNames, carrier, calculationMethod, calculationMethodProperty, calculationMethodValue, currency, active, persistStore);
		
		shippingServiceLevel = persistShippingServiceLevel(shippingServiceLevel);
		shippingServiceLevelService.getPersistenceEngine().clearCache();
		return shippingServiceLevelService.findByCode(shippingServiceLevel.getCode());
	}
	
	/**
	 * Create a ShippingServiceLevel instance using the passed in vars and some 
	 * static ones hard coded into this class.
	 * @param storeCode storeCode
	 * @param shippingRegionName shippingRegionName
	 * @param levelCode levelCode
	 * @param localizedNames localizedNames 
	 * @param carrier carrier
	 * @param calculationMethod calculationMethod
	 * @param calculationMethodProperty calculationMethodProperty
	 * @param calculationMethodValue calculationMethodValue
	 * @param currency currency
	 * @param active active
	 * @return a ShippingServiceLevel instance
	 */
	public ShippingServiceLevel createShippingServiceLevel(final String storeCode, final String storeLocales, final String shippingRegionName, 
			final String levelCode, final String localizedNames, final String carrier,
			final String calculationMethod, final String calculationMethodProperty, 
			final String calculationMethodValue, final String currency, final String active, boolean persistStore) {
		
		ShippingServiceLevel shippingServiceLevel 
			= this.beanFactory.getBean(ContextIdNames.SHIPPING_SERVICE_LEVEL);
		List <Locale> supportedLocales = new ArrayList<Locale>();
		String [] locales = StringUtils.split(storeLocales, COMMA_DELIM);
		for(String locale : locales) {
			supportedLocales.add(new Locale(locale));
		}
		Store findStore = findStore(storeCode);
		if (findStore == null) {
			findStore = setUpTestStore(storeCode, supportedLocales, "Canada", "BC", persistStore);
		} 
		shippingServiceLevel.setStore(findStore);
		
		shippingServiceLevel.setCarrier(carrier);
		shippingServiceLevel.setEnabled(Boolean.valueOf(active).booleanValue());
		shippingServiceLevel.setCode(levelCode);
		//	look: a service call here...
		shippingServiceLevel.setShippingRegion(shippingRegionService.findByName(shippingRegionName));
		shippingServiceLevel.setShippingCostCalculationMethod(
			setUpShippingCostCalculationMethod(calculationMethod, calculationMethodProperty, calculationMethodValue, currency));
		
		shippingServiceLevel.setLocalizedPropertiesMap(createLocalizedPropertiesMap(localizedNames, supportedLocales));
		return shippingServiceLevel;
	}
	
	/**
	 * Set up and persist a Store object needed to persist a ShippingServiceLevelImpl correctly.
	 * @param storeCode unique store code
	 * @return the persisted Store
	 * @throws DefaultValueRemovalForbiddenException 
	 */
	public Store setUpTestStore(final String storeCode, final List <Locale> supportedLocales, final String country, final String subCountry, boolean persistStore) {
	 	Store mockStore = beanFactory.getBean(STORE_BEAN);
		mockStore.setCountry(country);
		mockStore.setSubCountry(subCountry);
		mockStore.setCode(storeCode);
		mockStore.setName(storeCode);
		mockStore.setEnabled(true);
		mockStore.setCreditCardCvv2Enabled(false);
		mockStore.setDisplayOutOfStock(false);
		mockStore.setStoreFullCreditCardsEnabled(false);
		mockStore.setStoreType(StoreType.B2C);
		mockStore.setTimeZone(TimeZone.getDefault());
		try  {
			mockStore.setSupportedLocales(supportedLocales);
		} catch (Exception e) {
			throw new EpServiceException(e.getMessage());
		}
		//a bunch of indexing is skipped when calling saveOrUpdate
		//if u do this. saves time and effort
		mockStore.setStoreState(StoreState.UNDER_CONSTRUCTION);
		if (persistStore) {
			mockStore = persistTestStore(mockStore);
		}
		return mockStore;
	}
	
	public Store persistTestStore(final Store mockStore) {
		return storeService.saveOrUpdate(mockStore);
	}
	
	private Store findStore(final String storeCode) {
		return storeService.findStoreWithCode(storeCode);
	}
	
	/**
	 * Set up the ShippingCostCalculationMethod object needed to persist a ShippingServiceLevelImpl correctly.
	 * @param calculationMethod  calculationMethod
	 * @param calculationMethodProperty calculationMethodProperty
	 * @param calculationMethodValue calculationMethodValue
	 * @param currency currency
	 * @return a ShippingCostCalculationMethod instance
	 */
	private ShippingCostCalculationMethod setUpShippingCostCalculationMethod(final String calculationMethod, final String calculationMethodProperty, 
			final String calculationMethodValue, final String currency) {
		ShippingCostCalculationMethod shippingCostCalculationMethod
				= beanFactory.getBean(calculationMethod);
		ShippingCostCalculationParameter param = new ShippingCostCalculationParameterImpl();
		
		param.setCurrency(Currency.getInstance(currency));
		param.setDisplayText(calculationMethodProperty);
		param.setKey(calculationMethodProperty);
		param.setValue(calculationMethodValue);
		
		Set <ShippingCostCalculationParameter> params = new HashSet <ShippingCostCalculationParameter>();
		params.add(param);
		shippingCostCalculationMethod.setParameters(params);
		
		return shippingCostCalculationMethod;
	}
	
	private Map <String, LocalizedPropertyValue> createLocalizedPropertiesMap(final String localizedNames, final List <Locale> locales) {
		final Map <String, LocalizedPropertyValue> localizedProperties = new HashMap<String, LocalizedPropertyValue>();
		String [] names = StringUtils.split(localizedNames, COMMA_DELIM);	
		int count = 0;
		for (Locale locale : locales) {
			String localeKey = ShippingServiceLevel.LOCALIZED_PROPERTY_NAME + "_" + locale.getLanguage();
			LocalizedPropertyValue localizedPropertyValue = new ShippingServiceLevelLocalizedPropertyValueImpl();
			localizedPropertyValue.setLocalizedPropertyKey(localeKey);
			localizedPropertyValue.setValue(StringUtils.trim(names[count++]));
		
			localizedProperties.put(localeKey, localizedPropertyValue);
		}
		return localizedProperties;
	}
}
	
