package com.elasticpath.commons.util.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.InvalidPropertyException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

/**
 *	<code>PluginBeanFactoryPostProcessor</code> allows plugins to self-configure without having
 *  to modify the original base configuration files.
 */
public class PluginBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
	private static final Logger LOG = Logger.getLogger(PluginBeanFactoryPostProcessor.class);
	
	private String extensionBeanName;
	private String extensionClassName = null;
	private String propertyName;
	private Object propertyValue;
	private Map<String, Object> propertiesMap = new HashMap<String, Object>();
	
	
	/**
	 * Modify the application context's internal bean factory after its standard initialization. All bean definitions
	 * will have been loaded, but no beans will have been instantiated yet. This allows us to override properties.
	 * @param beanFactory - the bean factory used by the application context
	 * @throws BeansException - in case of errors
	 */
	public void postProcessBeanFactory(final ConfigurableListableBeanFactory beanFactory)
			throws BeansException {
		
		BeanDefinition beanDef = beanFactory.getBeanDefinition(extensionBeanName);
		if (extensionClassName != null) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Bean " + extensionBeanName + ": replacing class " + beanDef.getBeanClassName() + " with " + extensionClassName);
			}
			beanDef.setBeanClassName(extensionClassName);
		}
		
		if (propertyName != null && propertyName.length() > 0) {
			setSingleProperty(beanDef);
		}
		
		
		if (propertiesMap != null && !propertiesMap.isEmpty()) {
			setMultipleProperties(beanDef);
		}
	}

	private void setMultipleProperties(final BeanDefinition beanDef) throws InvalidPropertyException {
		MutablePropertyValues propValues = beanDef.getPropertyValues();
		
		for (Map.Entry<String, Object> property : propertiesMap.entrySet()) {
			if (LOG.isDebugEnabled()) {
				LOG.debug(debugMessage(propValues, property.getKey(), property.getValue().toString()));
			}
			try {
				propValues.addPropertyValue(property.getKey(), property.getValue());
			} catch (Exception e) {
				if (LOG.isDebugEnabled()) {
					LOG.debug(exceptionMessage(property.getKey(), property.getValue().toString(), e.getMessage()));
				}
				throw new InvalidPropertyException(beanDef.getClass(), property.getKey(), "", e);
			}
		}
	}

	private void setSingleProperty(final BeanDefinition beanDef) throws InvalidPropertyException {
		MutablePropertyValues propValues = beanDef.getPropertyValues();
		
		if (LOG.isDebugEnabled()) {
			LOG.debug(debugMessage(propValues, propertyName, propertyValue.toString()));
		}
		
		try {
			propValues.addPropertyValue(propertyName, propertyValue);
		} catch (Exception e) {
			if (LOG.isDebugEnabled()) {
				LOG.debug(exceptionMessage(propertyName, propertyValue.toString(), e.getMessage()));
			}
			throw new InvalidPropertyException(beanDef.getClass(), propertyName, "", e);
		}
	}
	
	private String debugMessage(final MutablePropertyValues propValues, final String name, final String value) {
		StringBuffer message = new StringBuffer();
		if (propValues.contains(name)) {
			message.append("Overridding ").append(name).append(" from ").append(propValues.getPropertyValue(name).getValue());
			message.append(" to ").append(value).append(" in bean ").append(extensionBeanName);
		} else {
			message.append("Setting new property ").append(name).append(" to ").append(value).append(" in bean ").append(extensionBeanName);
		}
		return message.toString();
	}
	
	private String exceptionMessage(final String name, final String value, final String message) {
		return "Exception setting property " + name + " to " + value + ": " + message;
	}

	/**
	 * Setter for the bean name to extend.
	 * @param extensionBeanName - the extensionBeanName to set
	 */
	public void setExtensionBeanName(final String extensionBeanName) {
		this.extensionBeanName = extensionBeanName;
	}

	/**
	 * Set the class name for the bean to use.
	 * @param extensionClassName - the extensionClassName to set
	 */
	public void setExtensionClassName(final String extensionClassName) {
		this.extensionClassName = extensionClassName;
	}

	/**
	 * Set the name of the property to override.
	 * @param propertyName - the propertyName to set
	 */
	public void setPropertyName(final String propertyName) {
		this.propertyName = propertyName;
	}

	/**
	 * Set the value of the property being overridden.
	 * @param propertyValue - the propertyValue to set
	 */
	public void setPropertyValue(final Object propertyValue) {
		this.propertyValue = propertyValue;
	}
	
//	/**
//	 * Gets the propertiesMap collection.
//	 * 
//	 * @return the Map of properties
//	 */
//	public Map getPropertiesMap() {
//		return this.propertiesMap;
//	}
//	
	/**
	 * Sets a collection of properties.
	 * 
	 * @param propertiesMap - a Map containing the properties to set
	 */
	public void setPropertiesMap(final Map<String, Object> propertiesMap) {
		this.propertiesMap = propertiesMap;
	}

}
