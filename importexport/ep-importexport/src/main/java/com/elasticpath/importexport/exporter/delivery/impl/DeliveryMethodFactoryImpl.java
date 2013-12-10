package com.elasticpath.importexport.exporter.delivery.impl;

import java.util.Map;

import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.common.types.TransportType;
import com.elasticpath.importexport.exporter.configuration.DeliveryConfiguration;
import com.elasticpath.importexport.exporter.delivery.DeliveryMethod;
import com.elasticpath.importexport.exporter.delivery.DeliveryMethodFactory;

/**
 * DeliveryMethodFactory creates Delivery Methods by delivery configuration.<br>
 * Map of available delivery methods is initialized by Spring.
 */
public class DeliveryMethodFactoryImpl implements DeliveryMethodFactory {

	private Map<TransportType, AbstractDeliveryMethodImpl> deliveryMethods;

	@Override
	public DeliveryMethod createDeliveryMethod(final DeliveryConfiguration deliveryConfiguration) throws ConfigurationException {
		final TransportType methodType = deliveryConfiguration.getMethod();
		final AbstractDeliveryMethodImpl deliveryMethod = deliveryMethods.get(methodType);

		if (deliveryMethod == null) {
			throw new ConfigurationException("Delivery method of type " + methodType.toString() + " doesn't exist");
		}

		deliveryMethod.initialize(deliveryConfiguration.getTarget());
		return deliveryMethod;
	}

	/**
	 * Gets available delivery methods.
	 * 
	 * @return the map of available delivery methods
	 */
	public Map<TransportType, AbstractDeliveryMethodImpl> getDeliveryMethods() {
		return deliveryMethods;
	}

	/**
	 * Sets available delivery methods.
	 * 
	 * @param deliveryMethods the map of delivery methods
	 */
	public void setDeliveryMethods(final Map<TransportType, AbstractDeliveryMethodImpl> deliveryMethods) {
		this.deliveryMethods = deliveryMethods;
	}
}
