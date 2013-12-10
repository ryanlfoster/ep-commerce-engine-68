package com.elasticpath.service.payment.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.payment.PaymentGateway;
import com.elasticpath.domain.payment.PaymentGatewayFactory;
import com.elasticpath.domain.payment.PaymentGatewayProperty;
import com.elasticpath.plugin.payment.PaymentGatewayPluginManagement;
import com.elasticpath.plugin.payment.capabilities.CreditCardCapability;
import com.elasticpath.service.impl.AbstractEpPersistenceServiceImpl;
import com.elasticpath.service.payment.PaymentGatewayService;

/**
 * Default implementation of <code>PaymentGatewayService</code>.
 */
public class PaymentGatewayServiceImpl extends AbstractEpPersistenceServiceImpl implements PaymentGatewayService {
	private PaymentGatewayFactory paymentGatewayFactory;
	
	/**
	 * Saves or updates a given <code>PaymentGateway</code>.
	 * 
	 * @param paymentGateway the <code>PaymentGateway</code> to save or update
	 * @return the updated instance of the paymentGateway object
	 * @throws EpServiceException in case of any errors
	 * @see PaymentGateway
	 */
	@Override
	public PaymentGateway saveOrUpdate(final PaymentGateway paymentGateway) throws EpServiceException {
		sanityCheck();

		// people might forget about persisting the property key, lets add this check
		for (Entry<String, PaymentGatewayProperty> entry : paymentGateway.getPropertiesMap().entrySet()) {
			if (entry.getValue() != null && entry.getValue().getKey() == null) {
				entry.getValue().setKey(entry.getKey());
			}
		}
		return getPersistenceEngine().saveOrUpdate(paymentGateway);
	}

	/**
	 * Deletes a <code>PaymentGateway</code>.
	 * 
	 * @param paymentGateway the <code>PaymentGateway</code> to remove
	 * @throws EpServiceException in case of any errors
	 */
	@Override
	public void remove(final PaymentGateway paymentGateway) throws EpServiceException {
		sanityCheck();
		getPersistenceEngine().delete(paymentGateway);
	}

	/**
	 * Gets a <code>PaymentGateway</code> with the given UID. Return null if no matching records
	 * exist.
	 * 
	 * @param paymentGatewayUid the <code>PaymentGateway</code> UID
	 * @return the <code>PaymentGateway</code> if the UID exists, otherwise null
	 * @throws EpServiceException in case of any errors
	 */
	@Override
	public PaymentGateway getGateway(final long paymentGatewayUid) throws EpServiceException {
		sanityCheck();
		PaymentGateway paymentGateway = null;
		if (paymentGatewayUid > 0) {
			paymentGateway = getPersistentBeanFinder().get(ContextIdNames.ABSTRACT_PAYMENT_GATEWAY, paymentGatewayUid);
		}
		return paymentGateway;
	}

	/**
	 * Gets a list of all payment gateway UIDs.
	 * 
	 * @return a list of all payment gateway UIDs
	 * @throws EpServiceException in case of any errors
	 */
	@Override
	public List<Long> findAllPaymentGatewayUids() throws EpServiceException {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("FIND_ALL_PAYMENT_GATEWAY_UIDS");
	}

	/**
	 * Gets a list of all payment gateways.
	 * 
	 * @return a list of all payment gateways
	 * @throws EpServiceException in case of any errors
	 */
	@Override
	public List<PaymentGateway> findAllPaymentGateways() throws EpServiceException {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("FIND_ALL_PAYMENT_GATEWAYS");
	}

	/**
	 * Gets the set of supported payment gateways.
	 * 
	 * @return the set of supported payment gateways
	 */
	@Override
	public Set<String> getSupportedPaymentGateways() {
		// construct a new set using HashSet so that set returned is serializable
		return new HashSet<String>(paymentGatewayFactory.getAvailableGatewayPlugins().keySet());
	}
	
	/**
	 * Gets the payment gateway default properties.
	 *
	 * @param gatewayType the gateway type
	 * @return the payment gateway default properties
	 */
	@Override
	public Properties getPaymentGatewayDefaultProperties(final String gatewayType) {
		Properties prop = new Properties();
		PaymentGatewayPluginManagement plugin = paymentGatewayFactory.getPluginGatewayPluginManagement(gatewayType);
		if (plugin != null) {
			for (String parameterName : plugin.getConfigurationParameters()) {
				prop.put(parameterName, "");
			}
		}
		
		return prop;
	}

	/**
	 * Creates the payment gateway.
	 *
	 * @param name the name
	 * @param type the type
	 * @param prop the prop
	 * @return the payment gateway
	 */
	@Override
	public PaymentGateway addPaymentGateway(final String name, final String type, final Properties prop) {
		PaymentGateway gateway = paymentGatewayFactory.getPaymentGateway(type);
		if (null != gateway) {
			gateway.setName(name);
			gateway.setProperties(prop);
			gateway = saveOrUpdate(gateway);
		}
		
		return gateway;
	}
	
	/**
	 * Sets the payment gateway factory.
	 * 
	 * @param paymentGatewayFactory the payment gateway factory
	 */
	public void setPaymentGatewayFactory(final PaymentGatewayFactory paymentGatewayFactory) {
		this.paymentGatewayFactory = paymentGatewayFactory;
	}

	/**
	 * Generic get method for a paymentGateway.
	 * 
	 * @param uid the persisted paymentGateway uid
	 * @return the persisted instance of a <code>PaymentGateway</code> if it exists, otherwise
	 *         null
	 * @throws EpServiceException in case of any errors
	 */
	@Override
	public Object getObject(final long uid) throws EpServiceException {
		return getGateway(uid);
	}
	
	/**
	 * Gets the set of supported credit card types.
	 * 
	 * @return the set of supported credit card types.
	 */
	@Override
	public Set<String> getSupportedCreditCardTypes() {
		Set<String> supportedCreditCardTypes = new TreeSet<String>();
		final Collection<Class<? extends PaymentGatewayPluginManagement>> paymentGateways =
				paymentGatewayFactory.getAvailableGatewayPlugins().values();
		for (Class<? extends PaymentGatewayPluginManagement> paymentGateway : paymentGateways) {
			try {
				PaymentGatewayPluginManagement paymentGatewayObj = paymentGateway.newInstance();
				CreditCardCapability creditCardCapability = paymentGatewayObj.getCapability(CreditCardCapability.class);
				if (creditCardCapability != null) {
					supportedCreditCardTypes.addAll(creditCardCapability.getSupportedCardTypes());
				}
			} catch (IllegalAccessException illegalAccessException) {
				throw new EpSystemException("Failed to create Object", illegalAccessException);
			} catch (InstantiationException instantiationException) {
				throw new EpSystemException("Failed to create Object", instantiationException);
			}
		}
		return supportedCreditCardTypes;
	}
	
	/**
	 * Get the list of payment gateways (Long) uidPk in use.
	 * 
	 * @return the list of uids of <code>PaymentGateway</code>s in use.
	 * @throws EpServiceException - in case of any errors
	 */
	@Override
	public List<Long> getPaymentGatewaysInUse() throws EpServiceException {
		sanityCheck();		
		return getPersistenceEngine().retrieveByNamedQuery("PAYMENT_GATEWAYS_WITH_STORE");
	}

	@Override
	public List<PaymentGateway> findByUids(final Collection<Long> gatewayUids) {
		sanityCheck();
		if (gatewayUids == null || gatewayUids.isEmpty()) {
			return Collections.emptyList();
		}
		return getPersistenceEngine().<PaymentGateway, Long> retrieveByNamedQueryWithList("PAYMENT_GATEWAYS_BY_UIDS", "list", gatewayUids);
	}

	@Override
	public List<PaymentGateway> findByNames(final Collection<String> gatewayNames) {
		sanityCheck();
		if (gatewayNames == null || gatewayNames.isEmpty()) {
			return Collections.emptyList();
		}
		return getPersistenceEngine().<PaymentGateway, String> retrieveByNamedQueryWithList("PAYMENT_GATEWAYS_BY_NAMES", "list", gatewayNames);
	}

	
	/**
	 * Get the payment gateway by payment gateway type.
	 * 
	 * @param paymentGatewayName name of payment gateway
	 * @return the <code>PaymentGateway</code> in use, or null if no payment gateway found with that name.
	 */
	@Override
	public PaymentGateway getGatewayByName(final String paymentGatewayName) {
		final List<PaymentGateway> paymentGateway = getPersistenceEngine().retrieveByNamedQuery("FIND_PAYMENT_GATEWAY_NAME", paymentGatewayName);
		if (paymentGateway.size() == 1) {
			return paymentGateway.get(0);
		}
		return null;
	}
}
