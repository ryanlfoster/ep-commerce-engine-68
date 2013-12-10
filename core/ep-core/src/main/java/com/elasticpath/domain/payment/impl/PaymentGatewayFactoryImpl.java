package com.elasticpath.domain.payment.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.payment.PaymentGateway;
import com.elasticpath.domain.payment.PaymentGatewayFactory;
import com.elasticpath.domain.payment.PaymentGatewayProperty;
import com.elasticpath.plugin.payment.PaymentGatewayPluginInvoker;
import com.elasticpath.plugin.payment.PaymentGatewayPluginManagement;
import com.elasticpath.plugin.payment.PaymentType;
import com.elasticpath.service.payment.GiftCertificateTransactionService;
import com.elasticpath.service.payment.gateway.impl.GiftCertificatePaymentGatewayPluginImpl;
import com.elasticpath.service.payment.gateway.impl.UnresolvablePaymentGatewayPluginImpl;
import com.elasticpath.settings.SettingsReader;
import com.elasticpath.settings.domain.SettingValue;

/**
 * Default implementation of <code>PaymentGatewayFactory</code>.
 */
public class PaymentGatewayFactoryImpl implements PaymentGatewayFactory {

	private static final Logger LOG = Logger.getLogger(PaymentGatewayFactoryImpl.class);
	
	private static final String CERT_PATH_SETTING = "COMMERCE/SYSTEM/PAYMENTGATEWAY/certificatesDirectory";

	// plugin type -> class of PaymentGatewayPlugin
	private final Map<String, Class<? extends PaymentGatewayPluginManagement>> gatewayClassesByType =
			new HashMap<String, Class<? extends PaymentGatewayPluginManagement>>();

	private SettingsReader settingsReader;

	private BeanFactory beanFactory;

	private GiftCertificateTransactionService giftCertificateTransactionService;

	/**
	 * Create an return a {@link PaymentGateway} with the specified plugin type.
	 *
	 * @param pluginType the plugin type
	 * @return a prototype of a payment gateway with the specified plugin type
	 */
	@Override
	public PaymentGateway getPaymentGateway(final String pluginType) {
		PaymentGateway paymentGateway = beanFactory.getBean(ContextIdNames.ABSTRACT_PAYMENT_GATEWAY);
		paymentGateway.setType(pluginType);
		return paymentGateway;
	}

	@Override
	public Map<String, Class<? extends PaymentGatewayPluginManagement>> getAvailableGatewayPlugins() {
		return gatewayClassesByType;
	}

	@Override
	public PaymentGatewayPluginInvoker createConfiguredPaymentGatewayPluginInstance(final String pluginType,
			final Map<String, PaymentGatewayProperty> properties) {

		PaymentGatewayPluginManagement instance = getPluginGatewayPluginManagement(pluginType);
		if (instance == null) {
			throw new EpSystemException("Unable to find payment gateway plugin of pluginType " + pluginType);
		}
		
		// Inject the certificate path prefix
		instance.setCertificatePathPrefix(getCertificatePathPrefix());
		
		// Inject configuration settings
				Map<String, String> configurations = new HashMap<String, String>();
		for (Entry<String, PaymentGatewayProperty> entry : properties.entrySet()) {
					configurations.put(entry.getValue().getKey(), entry.getValue().getValue());
			}
				instance.setConfigurationValues(configurations);

		return (PaymentGatewayPluginInvoker) instance;
		}

	@Override
	public PaymentType getPaymentTypeForPlugin(final String pluginType) {
		return createConfiguredPaymentGatewayPluginInstance(pluginType, new HashMap<String, PaymentGatewayProperty>()).getPaymentType();
	}

	@Override
	public PaymentGatewayPluginManagement getPluginGatewayPluginManagement(final String pluginType) {
		if (getAvailableGatewayPlugins().get(pluginType) == null) {
			LOG.warn("No payment gateway plugin found for type: " + pluginType);
			return new UnresolvablePaymentGatewayPluginImpl(pluginType);
		} else {
			return createInstance(getAvailableGatewayPlugins().get(pluginType));
		}
	}
	
	private PaymentGatewayPluginManagement createInstance(final Class<? extends PaymentGatewayPluginManagement> clazz) {
		try {
			PaymentGatewayPluginManagement plugin = clazz.newInstance();
			if (plugin instanceof GiftCertificatePaymentGatewayPluginImpl) {
				((GiftCertificatePaymentGatewayPluginImpl) plugin).setGiftCertificateTransactionService(giftCertificateTransactionService);
			}
			return plugin;
		} catch (InstantiationException e) {
			throw new EpSystemException("Failed to create payment gateway plugin :" + clazz, e);
		} catch (IllegalAccessException e) {
			throw new EpSystemException("Failed to create payment gateway plugin :" + clazz, e);
		}
	}

	/**
	 * Setter for injecting the gateway classes through Spring.
	 * @param gatewayClasses set of gateway class names
	 */
	public void setGatewayClasses(final Set<Class<? extends PaymentGatewayPluginManagement>> gatewayClasses) {
		for (Class<? extends PaymentGatewayPluginManagement> gatewayClass : gatewayClasses) {
				PaymentGatewayPluginManagement gatewayInstance = createInstance(gatewayClass);
				gatewayClassesByType.put(gatewayInstance.getPluginType(), gatewayClass);
		}
	}

	/**
	 * Gets the path prefix for the payment gateway certificate files.
	 * 
	 * @return the path prefix
	 */
	protected String getCertificatePathPrefix() {
		SettingValue value = getSettingsReader().getSettingValue(CERT_PATH_SETTING);
		if (value == null) {
			return null;
		}
		String path = value.getValue();
		LOG.debug("Certificate path prefix: " + path);
		return path;
	}

	/**
	 * Get the settings reader.
	 * 
	 * @return the settings reader
	 */
	protected SettingsReader getSettingsReader() {
		return settingsReader;
	}

	public void setSettingsReader(final SettingsReader settingsReader) {
		this.settingsReader = settingsReader;
	}

	/**
	 * Get the gift certificate transaction service.
	 * 
	 * @return the gift certificate transaction service
	 */
	protected GiftCertificateTransactionService getGiftCertificateTransactionService() {
		return giftCertificateTransactionService;
	}

	public void setGiftCertificateTransactionService(final GiftCertificateTransactionService giftCertificateTransactionService) {
		this.giftCertificateTransactionService = giftCertificateTransactionService;
	}

	protected BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}
}
