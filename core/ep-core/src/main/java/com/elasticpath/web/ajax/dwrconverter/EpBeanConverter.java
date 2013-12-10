/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.web.ajax.dwrconverter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.directwebremoting.convert.BeanConverter;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.ElasticPath;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeGroup;
import com.elasticpath.domain.attribute.AttributeGroupAttribute;
import com.elasticpath.domain.attribute.AttributeUsage;
import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.attribute.CustomerProfileValue;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerAddress;
import com.elasticpath.domain.customer.CustomerAuthentication;
import com.elasticpath.domain.customer.CustomerCreditCard;
import com.elasticpath.domain.customer.CustomerGroup;
import com.elasticpath.domain.customer.CustomerProfile;
import com.elasticpath.domain.impl.ElasticPathImpl;
import com.elasticpath.service.misc.GeneralJpaLoaderService;

/**
 * <code>EpBeanConverter</code> is a customized bean converter for integration with dwr.
 * This handles all persisted bean.
 */
@SuppressWarnings({ 
	"PMD.CyclomaticComplexity",
	"PMD.CouplingBetweenObjects", 
	"PMD.ExcessiveImports",
	"PMD.DontUseElasticPathImplGetInstance" })
public class EpBeanConverter extends BeanConverter {
	
	private static final ElasticPath ELASTICPATH = ElasticPathImpl.getInstance();
	private static final Map<Class<?>, Object> BEAN_SERVICE_MAPPING = new HashMap<Class<?>, Object>();
	private static final Map<Class<?>, String> BEAN_CONTEXTID_MAPPING = new HashMap<Class<?>, String>();
	static {
		// For each entity bean, add an entry of the entity class type to service class mapping here.
		// This will be used to load the proper entity during inbound conversion.
		BEAN_SERVICE_MAPPING.put(Customer.class, ELASTICPATH.getBean(ContextIdNames.CUSTOMER_SERVICE));
		BEAN_SERVICE_MAPPING.put(CustomerGroup.class, ELASTICPATH.getBean(ContextIdNames.CUSTOMER_GROUP_SERVICE));
		BEAN_SERVICE_MAPPING.put(Attribute.class, ELASTICPATH.getBean(ContextIdNames.ATTRIBUTE_SERVICE));		

		// For non-entity (component) bean, add an entry of the bean class type to spring bean factory beanid mapping here.
		// This will be used to load the new instance of the entity from the spring bean factory during inbound conversion.
		BEAN_CONTEXTID_MAPPING.put(CustomerAddress.class, ContextIdNames.CUSTOMER_ADDRESS);
		BEAN_CONTEXTID_MAPPING.put(CustomerCreditCard.class, ContextIdNames.CUSTOMER_CREDIT_CARD);

		BEAN_CONTEXTID_MAPPING.put(AttributeUsage.class, ContextIdNames.ATTRIBUTE_USAGE);
		BEAN_CONTEXTID_MAPPING.put(AttributeGroup.class, ContextIdNames.ATTRIBUTE_GROUP);
		BEAN_CONTEXTID_MAPPING.put(AttributeGroupAttribute.class, ContextIdNames.ATTRIBUTE_GROUP_ATTRIBUTE);
		BEAN_CONTEXTID_MAPPING.put(AttributeValue.class, ContextIdNames.ATTRIBUTE_VALUE);
		BEAN_CONTEXTID_MAPPING.put(CustomerProfile.class, ContextIdNames.CUSTOMER_PROFILE);
		BEAN_CONTEXTID_MAPPING.put(CustomerAuthentication.class, ContextIdNames.CUSTOMER_AUTHENTICATION);
		BEAN_CONTEXTID_MAPPING.put(CustomerProfileValue.class, ContextIdNames.CUSTOMER_PROFILE_VALUE);
	}
	
	private GeneralJpaLoaderService jpaLoader;
	
	/**
	 * Get the general loader service for JPA.
	 *
	 * @return the generalJpaLoaderService
	 */
	protected GeneralJpaLoaderService getGeneralJpaLoaderService() {
		if (jpaLoader == null) {
			jpaLoader = ELASTICPATH.getBean(ContextIdNames.JPA_GENRAL_LOADER_SERVICE);
		}
		return jpaLoader;
	}

	/**
	 * Calls setter on bean with value. Protected so that it can be overriden.
	 * @param setter setter method
	 * @param bean bean object
	 * @param value value to set
	 * @throws IllegalArgumentException exception
	 * @throws IllegalAccessException exception
	 * @throws InvocationTargetException exception
	 */
	@SuppressWarnings("unchecked")
	protected void setMethod(final Method setter, final Object bean, final Object value)
		throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		if (setter.getName().equalsIgnoreCase("setPriceTiers")) {
			setter.invoke(bean, new TreeMap<Object, Object>((Map<Object, Object>) value));
			return;
		}
		setter.invoke(bean, value);
	}

	/**
	 * Return the context id mapping.
	 * @return the context id mapping.
	 */
	protected Map<Class<?>, String> getBeanContextIdMapping() {
		return BEAN_CONTEXTID_MAPPING;
	}

}
