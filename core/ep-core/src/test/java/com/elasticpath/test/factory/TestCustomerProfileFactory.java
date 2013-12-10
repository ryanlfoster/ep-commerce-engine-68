package com.elasticpath.test.factory;

import java.util.HashMap;
import java.util.Map;

import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeType;
import com.elasticpath.domain.attribute.AttributeUsage;
import com.elasticpath.domain.attribute.impl.AttributeImpl;
import com.elasticpath.domain.attribute.impl.AttributeUsageImpl;
import com.elasticpath.domain.customer.impl.CustomerImpl;

/**
 * Creates a customer profile useful for unit testing.
 */
public class TestCustomerProfileFactory {
	private static final boolean REQUIRED = true;
	private static final boolean NOT_REQUIRED = false;

	/**
	 * Setup the customer profile attribute.
	 * @return the constructed customer profile
	 */
	public Map<String, Attribute> getProfile() {
		HashMap<String, Attribute> customerProfileMap = new HashMap<String, Attribute>();

		addAttribute(customerProfileMap, "CP_FIRST_NAME", REQUIRED, AttributeType.SHORT_TEXT);
		addAttribute(customerProfileMap, "CP_LAST_NAME", REQUIRED, AttributeType.SHORT_TEXT);
		addAttribute(customerProfileMap, "CP_EMAIL", REQUIRED, AttributeType.SHORT_TEXT);
		addAttribute(customerProfileMap, "CP_PREF_LOCALE", NOT_REQUIRED, AttributeType.SHORT_TEXT);
		addAttribute(customerProfileMap, "CP_PREF_CURR", NOT_REQUIRED, AttributeType.SHORT_TEXT);
		addAttribute(customerProfileMap, "CP_ANONYMOUS_CUST", REQUIRED, AttributeType.BOOLEAN);
		addAttribute(customerProfileMap, "CP_PHONE", NOT_REQUIRED, AttributeType.SHORT_TEXT);
		addAttribute(customerProfileMap, "CP_FAX", NOT_REQUIRED, AttributeType.SHORT_TEXT);
		addAttribute(customerProfileMap, "CP_GENDER", NOT_REQUIRED, AttributeType.SHORT_TEXT);
		addAttribute(customerProfileMap, "CP_COMPANY", NOT_REQUIRED, AttributeType.SHORT_TEXT);
		addAttribute(customerProfileMap, "CP_HTML_EMAIL", REQUIRED, AttributeType.BOOLEAN);
		addAttribute(customerProfileMap, "CP_DOB", NOT_REQUIRED, AttributeType.DATE);
		addAttribute(customerProfileMap, CustomerImpl.ATT_KEY_CP_BE_NOTIFIED, NOT_REQUIRED, AttributeType.BOOLEAN);

		return customerProfileMap;
	}


	private void addAttribute(final Map<String, Attribute> customerProfileMap, final String key, final boolean required, final  AttributeType type) {

		Attribute attribute = new AttributeImpl() {
			private static final long serialVersionUID = -5855153586334823605L;

			@Override
			public AttributeUsage getAttributeUsage() {
				return AttributeUsageImpl.CUSTOMERPROFILE_USAGE;
			}
			@Override
			public int getAttributeUsageId() {
				return AttributeUsageImpl.CUSTOMERPROFILE_USAGE.getValue();
			}
			@Override
			public void setAttributeUsage(final AttributeUsage attributeUsage) {
				throw new UnsupportedOperationException("You tried to set attributeUsage on a test AttributeImpl that was"
						+ " created by TestCustomerProfileFactory.  We are trying to avoid needing ATTRIBUTE_USAGE to be defined"
						+ " in the beanFactory.");
			}
			@Override
			public void setAttributeUsageId(final int attributeUsageId) {
				throw new UnsupportedOperationException("You tried to set attributeUsageId on a test AttributeImpl that was"
						+ " created by TestCustomerProfileFactory.  We are trying to avoid needing ATTRIBUTE_USAGE to be defined"
						+ " in the beanFactory.");
			}
		};
		attribute.setKey(key);
		attribute.setRequired(required);
		attribute.setAttributeType(type);

		customerProfileMap.put(key, attribute);

	}
}
