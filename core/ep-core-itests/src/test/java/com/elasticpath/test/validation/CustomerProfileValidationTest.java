package com.elasticpath.test.validation;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.validation.ConstraintViolation;

import org.drools.util.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.constants.GlobalConstants;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeType;
import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.attribute.CustomerProfileValue;
import com.elasticpath.domain.attribute.impl.AttributeUsageImpl;
import com.elasticpath.domain.attribute.impl.CustomerProfileValueImpl;
import com.elasticpath.domain.customer.CustomerProfile;
import com.elasticpath.service.attribute.AttributeService;
import com.elasticpath.test.integration.DirtiesDatabase;

/**
 * Validation tests for {@link CustomerProfile}.
 */
public class CustomerProfileValidationTest extends AbstractValidationTest {

	private static final String PROFILE_VALUE_MAP_FORMAT = "profileValueMap[%s]";

	private final AtomicInteger count = new AtomicInteger();

	@Autowired
	private AttributeService attributeService;

	private static final String MID_RANGE_ELEMENT = StringUtils.repeat("a", GlobalConstants.SHORT_TEXT_MAX_LENGTH -1);

	private static final String MAX_ELEMENT = StringUtils.repeat("a", GlobalConstants.SHORT_TEXT_MAX_LENGTH);

	private static final String GREATER_THAN_MAX_ELEMENT = StringUtils.repeat("a", GlobalConstants.SHORT_TEXT_MAX_LENGTH + 1);

	@Before
	public void populateServices() {
		// cleanup old attributes
		for (Attribute attribute : attributeService.getCustomerProfileAttributes()) {
			attributeService.remove(attribute);
		}
	}

	private Attribute createTextAttribute(final String key,
			final AttributeType attributeType,
			final boolean multiValueEnabled,
			final boolean required) {
		Attribute attribute = getBeanFactory().getBean(ContextIdNames.ATTRIBUTE);

		attribute.setName(String.format("attribute-%d", count.incrementAndGet()));
		attribute.setGlobal(true);
		attribute.setRequired(required);
		attribute.setKey(key);
		attribute.setAttributeUsage(AttributeUsageImpl.CUSTOMERPROFILE_USAGE);
		attribute.setAttributeType(attributeType);
		attribute.setMultiValueEnabled(multiValueEnabled);
		attributeService.add(attribute);

		return attribute;
	}

	private CustomerProfile createCustomerProfile() {
		CustomerProfile customerProfile = getBeanFactory().getBean(ContextIdNames.CUSTOMER_PROFILE);
		customerProfile.setProfileValueBeanId(ContextIdNames.CUSTOMER_PROFILE_VALUE);
		customerProfile.setProfileValueMap(new HashMap<String, AttributeValue>());
		return customerProfile;
	}

	/** Tests validation when there are no required attributes. */
	@DirtiesDatabase
	@Test
	public void testNonRequiredAttributes() {
		Attribute nonRequired = createTextAttribute("nonRequired", AttributeType.SHORT_TEXT, false, false);

		CustomerProfile profile = createCustomerProfile();
		profile.setProfileValue(nonRequired.getKey(), "value");

		Set<ConstraintViolation<CustomerProfile>> violations = getValidator().validate(profile);
		assertViolationsNotContains("No required attributes shouldn't cause violations", violations, "");
	}

	/** Tests validation when there are required attributes. */
	@DirtiesDatabase
	@Test
	public void testRequiredAttributes() {
		String attributeKey = "blastoff";
		Attribute required = createTextAttribute(attributeKey, AttributeType.SHORT_TEXT, false, true);

		CustomerProfile profile = createCustomerProfile();

		Set<ConstraintViolation<CustomerProfile>> violations = getValidator().validate(profile);
		assertViolationsContains("Unset required attributes should cause violations", violations, String.format(PROFILE_VALUE_MAP_FORMAT, attributeKey));

		profile.setProfileValue(required.getKey(), "attribute");
		violations = getValidator().validate(profile);
		assertViolationsNotContains("Violations after setting all required keys", violations, String.format(PROFILE_VALUE_MAP_FORMAT, attributeKey));
	}

	/** Tests validation when there are required attributes with blanks. */
	@DirtiesDatabase
	@Test
	public void testBlankRequiredAttributes() {
		String attributeKey = "hardy";
		Attribute required = createTextAttribute(attributeKey, AttributeType.SHORT_TEXT, false, true);

		CustomerProfile profile = createCustomerProfile();

		Set<ConstraintViolation<CustomerProfile>> violations = getValidator().validate(profile);
		assertViolationsContains("Unset required attributes should cause violations", violations, String.format(PROFILE_VALUE_MAP_FORMAT, attributeKey));

		profile.setProfileValue(required.getKey(), null);
		violations = getValidator().validate(profile);
		assertViolationsContains("null values are not allowed for required attributes",
				violations,
				String.format(PROFILE_VALUE_MAP_FORMAT, attributeKey));

		profile.setProfileValue(required.getKey(), "    	   ");
		violations = getValidator().validate(profile);
		assertViolationsContains("Blank values are not allowed for required attributes",
				violations,
				String.format(PROFILE_VALUE_MAP_FORMAT, attributeKey));
	}

	/** Tests validation when there are both required and non-required attributes. */
	@DirtiesDatabase
	@Test
	public void testBothNonAndRequiredAttributes() {
		String requiredAttributeKey = "foo";
		String nonRequiredAttributeKey = "bar";
		Attribute nonRequired = createTextAttribute(nonRequiredAttributeKey, AttributeType.SHORT_TEXT, false, false);
		Attribute required = createTextAttribute(requiredAttributeKey, AttributeType.SHORT_TEXT, false, true);

		CustomerProfile profile = createCustomerProfile();

		Set<ConstraintViolation<CustomerProfile>> violations = getValidator().validate(profile);
		assertViolationsContains("Unset required attributes should cause violations",
				violations,
				String.format(PROFILE_VALUE_MAP_FORMAT, requiredAttributeKey));
		assertViolationsNotContains("Non required attribute fails validation?",
				violations,
				String.format(PROFILE_VALUE_MAP_FORMAT, nonRequiredAttributeKey));

		profile.setProfileValue(nonRequired.getKey(), null);
		profile.setProfileValue(required.getKey(), "hardy");
		violations = getValidator().validate(profile);
		assertViolationsNotContains("Violations after setting all required keys",
				violations,
				String.format(PROFILE_VALUE_MAP_FORMAT, nonRequiredAttributeKey));
		assertViolationsNotContains("Violations after setting all required keys",
				violations,
				String.format(PROFILE_VALUE_MAP_FORMAT, requiredAttributeKey));

		profile.setProfileValue(nonRequired.getKey(), "ferentschik");
		profile.setProfileValue(required.getKey(), null);
		violations = getValidator().validate(profile);
		assertViolationsContains("Setting all non-required attribute but having a required attribute should fail",
				violations,
				String.format(PROFILE_VALUE_MAP_FORMAT, requiredAttributeKey));
	}

	/** Multiple failures should be reported as separate violations. */
	@DirtiesDatabase
	@Test
	public void testMultipleValidationFailures() {
		String attributeKey1 = "requiredAttributeKey3354";
		String attributeKey2 = "ferentchik09";
		Attribute attribute1 = createTextAttribute(attributeKey1, AttributeType.SHORT_TEXT, false, true);
		Attribute attribute2 = createTextAttribute(attributeKey2, AttributeType.SHORT_TEXT, false, true);

		CustomerProfile profile = createCustomerProfile();

		Set<ConstraintViolation<CustomerProfile>> violations = getValidator().validate(profile);
		assertEquals("Both required attributes are not set", 2, violations.size());
		assertViolationsContains("Unset required attributes should cause violations",
				violations,
				String.format(PROFILE_VALUE_MAP_FORMAT, attributeKey1),
				String.format(PROFILE_VALUE_MAP_FORMAT, attributeKey2));

		profile.setProfileValue(attribute1.getKey(), null);
		profile.setProfileValue(attribute2.getKey(), "hardy");
		violations = getValidator().validate(profile);
		assertViolationsContains("Missing 1 required attribute and not the other should fail",
				violations,
				String.format(PROFILE_VALUE_MAP_FORMAT, attributeKey1));

		profile.setProfileValue(attribute1.getKey(), "ferentschik");
		profile.setProfileValue(attribute2.getKey(), null);
		violations = getValidator().validate(profile);
		assertViolationsContains("Missing 1 required attribute and not the other should fail",
				violations,
				String.format(PROFILE_VALUE_MAP_FORMAT, attributeKey2));

		profile.setProfileValue(attribute1.getKey(), "value");
		profile.setProfileValue(attribute2.getKey(), "value2");
		violations = getValidator().validate(profile);
		assertViolationsNotContains("Both required attributes are set", violations, String.format(PROFILE_VALUE_MAP_FORMAT, attributeKey1));
		assertViolationsNotContains("Both required attributes are set", violations, String.format(PROFILE_VALUE_MAP_FORMAT, attributeKey2));
	}

	/** Tests validation failures when a short text attribute is too long. */
	@DirtiesDatabase
	@Test
	public void testRequiredShortTextAttributesValidationFailsOnTooLongText() {
		String attributeKey = "blastoff";
		Attribute required = createTextAttribute(attributeKey, AttributeType.SHORT_TEXT, false, true);

		CustomerProfile profile = createCustomerProfile();

		profile.setProfileValue(required.getKey(), StringUtils.repeat("A", GlobalConstants.SHORT_TEXT_MAX_LENGTH + 1));
		Set<ConstraintViolation<CustomerProfile>> violations = getValidator().validate(profile);
		assertViolationsContains("Violations after setting all required keys",
				violations,
				String.format(PROFILE_VALUE_MAP_FORMAT, attributeKey));
	}

	/** Tests validation failures when a long text attribute is too long. */
	@DirtiesDatabase
	@Test
	public void testRequiredLongTextAttributesValidationFailsOnTooLongText() {
		String attributeKey = "blastoff";
		Attribute required = createTextAttribute(attributeKey, AttributeType.LONG_TEXT, false, true);
		CustomerProfile profile = createCustomerProfile();

		profile.setProfileValue(required.getKey(), StringUtils.repeat("A", GlobalConstants.LONG_TEXT_MAX_LENGTH + 1));
		Set<ConstraintViolation<CustomerProfile>> violations = getValidator().validate(profile);
		assertViolationsContains("Violations after setting all required keys",
				violations,
				String.format(PROFILE_VALUE_MAP_FORMAT, attributeKey));
	}

	/**
	 * As it stands Customer profile doesn't incorporate short text multivalue attributes, <br>
	 * but this test is included for coverage if that was the case.
	 */
	@DirtiesDatabase
	@Test
	public void testShortTextMultiValueAttributesValidationFailsOnTooLongText() {
		CustomerProfileValue customerProfileValue = new CustomerProfileValueImpl();
		Attribute attribute = createTextAttribute("test1", AttributeType.SHORT_TEXT, false, true);
		attribute.setMultiValueEnabled(true);
		customerProfileValue.setAttribute(attribute);
		customerProfileValue.setAttributeType(AttributeType.SHORT_TEXT);

		customerProfileValue.setShortTextMultiValues(Arrays.asList(MID_RANGE_ELEMENT, MAX_ELEMENT, GREATER_THAN_MAX_ELEMENT));

		Set<ConstraintViolation<CustomerProfileValue>> violations = getValidator().validate(customerProfileValue);
		assertEquals(1, violations.size());
		assertViolationsMessageContainsToken(violations, "[2]", String.valueOf(GlobalConstants.SHORT_TEXT_MAX_LENGTH));
	}
}
