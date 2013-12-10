package com.elasticpath.service.shoppingcart.actions.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.domain.order.Order;
import com.elasticpath.service.shoppingcart.actions.CheckoutActionContext;
import com.elasticpath.service.shoppingcart.actions.ReversibleCheckoutAction;

/**
 * <p>Checkout Action which populates the {@link com.elasticpath.domain.order.Order}'s {@link com.elasticpath.domain.order.OrderData} with data
 * from the {@link CheckoutActionContext}.</p>
 *
 * <p>The values that are populated by this action need to be configured via Spring, via the property {@link #orderDataProperties}.</p>
 *
 * <p>For instance:</p>
 *
 * <pre>
 *    &lt;bean id=&quot;populateOrderDataCheckoutAction&quot; class=&quot;com.elasticpath.service.shoppingcart.actions.impl&quot;&gt;
 *       &lt;property name=&quot;orderDataProperties&quot;&gt;
 *          &lt;map&gt;
 *             &lt;-- Vanilla Property Example --&gt;
 *             &lt;entry key=&quot;gender&quot; value=&quot;shoppingCart.shopper.customer.gender&quot;&gt;
 *
 *             &lt;-- Indexed (array or list) Property Example --&gt;
 *             &lt;entry key=&quot;firstSkuCode&quot; value=&quot;shoppingCart.cartItems[0].productSku.skuCode&quot;&gt;
 *
 *             &lt;-- Single Mapped Property Example --&gt;
 *             &lt;entry key=&quot;foo&quot; value=&quot;shoppingCart.shopper.cache.item(FOO)&quot;&gt;
 *
 *             &lt;-- Full Map Import Example
 *
 *                    This will copy all the elements in the given map to the OrderData map.  For instance, the entry
 *                    ["FREE-PHONE", 50] in the limitedUsagePromotionRuleCodes map would be copied to the OrderData map
 *                    as ["ruleCode.FREE-PHONE", 50].
 *             --&gt;
 *             &lt;entry key=&quot;ruleCode&quot; value=&quot;shoppingCart.limitedUsagePromotionRuleCodes&quot;&gt;

 *          &lt;/map&gt;
 *       &lt;/property&gt;
 *    &lt;/bean&gt;
 * </pre>
 *
 */
public class PopulateOrderDataCheckoutAction implements ReversibleCheckoutAction {
	private Map<String, String> orderDataProperties;

	@Override
	public void execute(final CheckoutActionContext context) throws EpSystemException {
		for (Map.Entry<String, String> orderDataProperty : getOrderDataProperties().entrySet()) {
			String orderDataPropertyKey = orderDataProperty.getKey();
			String contextPropertyName = orderDataProperty.getValue();

			try {
				Object propertyValue = PropertyUtils.getProperty(context, contextPropertyName);
				if (propertyValue == null) {
					continue;
				}

				if (propertyValue instanceof Map) {
					for (Map.Entry<?, ?> mapEntry : ((Map<?, ?>) propertyValue).entrySet()) {
						if (mapEntry.getValue() == null) {
							continue;
						}

						setOrderDataValue(context.getOrder(), getSubMapKey(orderDataPropertyKey, mapEntry.getKey()), mapEntry.getValue());
					}
				} else {
					setOrderDataValue(context.getOrder(), orderDataPropertyKey, propertyValue);
				}
			} catch (Exception ex) {
				throw new EpServiceException("Could not read CheckoutActionContext property: " + orderDataProperty.getKey(), ex);
			}
		}
	}

	/**
	 * Sets the given value for the given key in the OrderData.  This method is protected to allow extension projects
	 * to customize serialization.
	 *
	 * @param order the order
	 * @param propertyKey the property key
	 * @param propertyValue the value
	 */
	protected void setOrderDataValue(final Order order, final String propertyKey, final Object propertyValue) {
		final String orderDataValue = propertyValue.toString();
		order.setFieldValue(propertyKey, orderDataValue);
	}

	@Override
	public void rollback(final CheckoutActionContext context) throws EpSystemException {
		Map<String, String> orderData = new HashMap<String, String>(context.getOrder().getFieldValues());
		for (String dataKey : orderData.keySet()) {
			context.getOrder().removeFieldValue(dataKey);
		}
	}

	/**
	 * Creates the key that will be used to store the given input map element in the OrderData map.
	 * @param baseKey the base key for the input map
	 * @param mapElementKey the name of the key that the element is stored in in the input map
	 * @return the key that will be used to store the element in the OrderData map
	 */
	protected String getSubMapKey(final String baseKey, final Object mapElementKey) {
		StringBuilder stringBuilder = new StringBuilder();
		if (StringUtils.isNotEmpty(baseKey)) {
			stringBuilder.append(baseKey);
			stringBuilder.append('.');
		}

		stringBuilder.append(mapElementKey.toString());
		return stringBuilder.toString();
	}

	public void setOrderDataProperties(final Map<String, String> orderDataProperties) {
		this.orderDataProperties = orderDataProperties;
	}

	protected Map<String, String> getOrderDataProperties() {
		return orderDataProperties;
	}
}
