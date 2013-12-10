package com.elasticpath.importexport.common.adapters.shipping;

import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.common.dto.DisplayValue;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.misc.LocalizedProperties;
import com.elasticpath.domain.misc.LocalizedPropertyValue;
import com.elasticpath.domain.misc.impl.ShippingServiceLevelLocalizedPropertyValueImpl;
import com.elasticpath.domain.shipping.ShippingCostCalculationMethod;
import com.elasticpath.domain.shipping.ShippingCostCalculationParameter;
import com.elasticpath.domain.shipping.ShippingRegion;
import com.elasticpath.domain.shipping.ShippingServiceLevel;
import com.elasticpath.domain.shipping.impl.ShippingCostCalculationParameterImpl;
import com.elasticpath.importexport.common.adapters.AbstractDomainAdapterImpl;
import com.elasticpath.importexport.common.dto.shipping.ShippingCostCalculationMethodDTO;
import com.elasticpath.importexport.common.dto.shipping.ShippingCostCalculationParameterDTO;
import com.elasticpath.importexport.common.dto.shipping.ShippingServiceLevelDTO;
import com.elasticpath.service.shipping.ShippingRegionService;
import com.elasticpath.service.store.StoreService;

/**
 * Helper class for mediating data between ShippingServiceLevel entitys and dtos. 
 *
 */
public class ShippingServiceLevelAdapter extends AbstractDomainAdapterImpl<ShippingServiceLevel, ShippingServiceLevelDTO>  {

	private BeanFactory beanFactory;

	private StoreService storeService;

	private ShippingRegionService shippingRegionService;
	
	/**
	 * Populate a dto from an entity.
	 * @param source the entity
	 * @param target the dto
	 */
	public void populateDTO(final ShippingServiceLevel source, final ShippingServiceLevelDTO target) {
		target.setGuid(source.getGuid());
		target.setStoreCode(source.getStore().getCode());
		target.setCarrier(source.getCarrier());
		target.setCode(source.getCode());
		target.setEnabled(source.isEnabled());
		target.setShippingCostCalculationMethodDto(createShippingCostCalculationMethodDto(source.getShippingCostCalculationMethod()));
		target.setShippingRegionName(source.getShippingRegion().getName());
		
		target.setNameValues(getLocalizedNameValueList(source));	
	}
	
	private List<DisplayValue> getLocalizedNameValueList(final ShippingServiceLevel source) {
		final LocalizedProperties localizedProperties = source.getLocalizedProperties();
		final List<DisplayValue> nameValues = new ArrayList<DisplayValue>();
		
		for (Locale locale : source.getStore().getSupportedLocales()) {			
			final String displayName 
				= localizedProperties.getValueWithoutFallBack(ShippingServiceLevel.LOCALIZED_PROPERTY_NAME, locale);
			nameValues.add(new DisplayValue(locale.toString(), displayName));
		}
		return nameValues;
	}
	
	/**
	 * Populate an entity from a dto.
	 * @param source the dto
	 * @param target the entity
	 */
	public void populateDomain(final ShippingServiceLevelDTO source, final ShippingServiceLevel target) {
		target.setGuid(source.getGuid());
		target.setCarrier(source.getCarrier());
		target.setCode(source.getCode());
		target.setEnabled(source.isEnabled());
		
		//this can throw a EpServiceException. i think its ok to let it fly up ...
		target.setStore(getStoreService().findStoreWithCode(source.getStoreCode()));
		
		target.setShippingCostCalculationMethod(
				createShippingCostCalculationMethod(source.getShippingCostCalculationMethodDto()));
		target.setShippingRegion(findShippingRegion(source.getShippingRegionName()));
		
		target.setLocalizedPropertiesMap(createLocalizedPropertiesMap(source.getNameValues()));
	}
	
	private Map <String, LocalizedPropertyValue> createLocalizedPropertiesMap(final List <DisplayValue> nameValues) {
		final Map <String, LocalizedPropertyValue> localizedProperties = new HashMap<String, LocalizedPropertyValue>();
		
		for (DisplayValue displayValue : nameValues) {
			LocalizedPropertyValue localizedPropertyValue = new ShippingServiceLevelLocalizedPropertyValueImpl();
			localizedPropertyValue.setLocalizedPropertyKey(ShippingServiceLevel.LOCALIZED_PROPERTY_NAME + "_" + displayValue.getLanguage());
			localizedPropertyValue.setValue(displayValue.getValue());
			
			localizedProperties.put(ShippingServiceLevel.LOCALIZED_PROPERTY_NAME + "_" + displayValue.getLanguage(), localizedPropertyValue);
		}
		return localizedProperties;
	}
	
	private ShippingCostCalculationMethodDTO createShippingCostCalculationMethodDto(final ShippingCostCalculationMethod source) {
		ShippingCostCalculationMethodDTO target = new ShippingCostCalculationMethodDTO();
		
		target.setDisplayText(source.getDisplayText());
		target.setType(source.getType());
		target.setShippingCostCalculationParams(createShippingCostCalculationParamDtos(source));
		
		return target;
	}

	private List <ShippingCostCalculationParameterDTO> createShippingCostCalculationParamDtos(final ShippingCostCalculationMethod source) {
		Set <ShippingCostCalculationParameter> sourceParams = source.getParameters();
		List <ShippingCostCalculationParameterDTO> targetParams = new ArrayList <ShippingCostCalculationParameterDTO>();
		
		for (ShippingCostCalculationParameter sourceParam : sourceParams) {
			ShippingCostCalculationParameterDTO target = new ShippingCostCalculationParameterDTO();
			target.setCurrency(getCurrencyCode(sourceParam)); // NOPMD
			target.setDisplayText(sourceParam.getDisplayText());
			target.setKey(sourceParam.getKey());
			target.setValue(sourceParam.getValue());
			
			targetParams.add(target);
		}
		
		return targetParams;
	}
	
	private String getCurrencyCode(final ShippingCostCalculationParameter sourceParam) {
		String currency = "";
		if (sourceParam.getCurrency() != null) {
			currency = sourceParam.getCurrency().getCurrencyCode();
		}
		if (currency == null) {
			return StringUtils.EMPTY;
		}
		return currency;
	}
	
	private ShippingCostCalculationMethod createShippingCostCalculationMethod(final ShippingCostCalculationMethodDTO source) {
		ShippingCostCalculationMethod target 
			= getBeanFactory().getBean(source.getType());
		
		target.setType(source.getType());
		target.setParameters(createShippingCostCalculationParams(source));
	
		return target;
	}
	
	private Set <ShippingCostCalculationParameter> createShippingCostCalculationParams(final ShippingCostCalculationMethodDTO source) {
		List <ShippingCostCalculationParameterDTO> sourceParams = source.getShippingCostCalculationParams();
		Set <ShippingCostCalculationParameter> targetParams = new HashSet <ShippingCostCalculationParameter>();
		
		for (ShippingCostCalculationParameterDTO sourceParam : sourceParams) {
			ShippingCostCalculationParameter target = new ShippingCostCalculationParameterImpl();
			if (StringUtils.isNotEmpty(sourceParam.getCurrency())) {
				target.setCurrency(Currency.getInstance(sourceParam.getCurrency()));
			}
			target.setDisplayText(sourceParam.getDisplayText());
			target.setKey(sourceParam.getKey());
			target.setValue(sourceParam.getValue());
			
			targetParams.add(target);
		}
		
		return targetParams;
	}
	
	private ShippingRegion findShippingRegion(final String name) {
		return shippingRegionService.findByName(name);
	}
	
	@Override
	public ShippingServiceLevelDTO createDtoObject() {
		return new ShippingServiceLevelDTO();
	}

	@Override
	public ShippingServiceLevel createDomainObject() {
		return getBean(ContextIdNames.SHIPPING_SERVICE_LEVEL);
	}
	
	public BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}
	
	public StoreService getStoreService() {
		return storeService;
	}

	public void setStoreService(final StoreService storeService) {
		this.storeService = storeService;
	}
	
	public ShippingRegionService getShippingRegionService() {
		return shippingRegionService;
	}

	public void setShippingRegionService(final ShippingRegionService shippingRegionService) {
		this.shippingRegionService = shippingRegionService;
	}

}
