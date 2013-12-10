package com.elasticpath.web.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.util.ReflectionUtils;

import com.elasticpath.web.security.ParameterConfiguration.Policy;

/**
 * Test class for {@link EsapiServletRequestDataBinder}.
 */
@SuppressWarnings("PMD.TooManyStaticImports")
public class EsapiServletRequestDataBinderTest {
	private static final String STR_VALUE = "value";
	private static final String STRING_FIELD = "stringField";
	private static final String INT_FIELD = "intField";
	private static final String UPPER_CASE_RULE = "upperCase";

	/**
	 * When there are multiple configuration matches, the one which matches explicitly should be used first regardless
	 * of configuration order.
	 */
	@Test
	public void testMostSpecificConfig() {
		String field = "stringField";
		ParameterConfiguration explicitConfig = new ParameterConfiguration(field, "onlyA", 1, false, Policy.BLANK);
		ParameterConfiguration wildcardConfig = new ParameterConfiguration("*Field", "onlyB", 1, false, Policy.BLANK);

		TargetObject bindableObject;
		String explicitConfigValidValue = "A";

		bindableObject = dataBinderBindWithConfigs(TargetObject.class, Arrays.asList(explicitConfig, wildcardConfig), field,
				explicitConfigValidValue);
		assertEquals("Most specific configuration was first, but not used", explicitConfigValidValue, bindableObject.stringField);

		bindableObject = dataBinderBindWithConfigs(TargetObject.class, Arrays.asList(wildcardConfig, explicitConfig), field,
				explicitConfigValidValue);
		assertEquals("Most specific configuration was last, but not used", explicitConfigValidValue, bindableObject.stringField);
	}

	/**
	 * If there are multiple configuration matches when both are using wildcards, the best match should be the one which
	 * matches the most characters.
	 */
	@Test
	public void testMostSpecificConfigWithWildcards() {
		ParameterConfiguration wildcardConfig = new ParameterConfiguration("*Field", "onlyB", 1, false, Policy.BLANK);
		ParameterConfiguration allConfig = new ParameterConfiguration("*", "onlyC", 1, false, Policy.BLANK);

		TargetObject bindableObject;
		String wildcardConfigValidValue = "B";

		bindableObject = dataBinderBindWithConfigs(TargetObject.class, Arrays.asList(wildcardConfig, allConfig), STRING_FIELD,
				wildcardConfigValidValue);
		assertEquals("Most specific configuration was first, but not used", wildcardConfigValidValue, bindableObject.stringField);

		bindableObject = dataBinderBindWithConfigs(TargetObject.class, Arrays.asList(allConfig, wildcardConfig), STRING_FIELD,
				wildcardConfigValidValue);
		assertEquals("Most specific configuration was last, but not used", wildcardConfigValidValue, bindableObject.stringField);
	}

	private <T> T dataBinderBindWithConfigs(final Class<? extends T> clazz, final Object initialValue,
			final List<ParameterConfiguration> configs, final String field, final String value) {
		T target = null;
		try {
			Constructor<? extends T> ctor = clazz.getDeclaredConstructor();
			ReflectionUtils.makeAccessible(ctor);
			target = ctor.newInstance();
		} catch (Exception e) {
			throw new AssertionError(e);
		}

		if (initialValue != null) {
			Field reflectiveField = ReflectionUtils.findField(TargetObject.class, field);
			ReflectionUtils.makeAccessible(reflectiveField);
			ReflectionUtils.setField(reflectiveField, target, initialValue);
		}

		return dataBinderBindWithConfigs(target, configs, field, value);
	}

	private <T> T dataBinderBindWithConfigs(final T target, final List<ParameterConfiguration> configs, final String field,
			final String value) {
		EsapiServletRequestDataBinder dataBinder = new EsapiServletRequestDataBinder(target, "target", configs);
		dataBinder.initBeanPropertyAccess(); // we aren't able to call recursively with field access

		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setParameter(field, value);
		dataBinder.bind(request);

		return target;
	}

	private <T> T dataBinderBindWithConfigs(final Class<? extends T> clazz, final List<ParameterConfiguration> configs, final String field,
			final String value) {
		return dataBinderBindWithConfigs(clazz, null, configs, field, value);
	}

	/** Parameters which are not configured should not be allowed to be bound. */
	@Test
	public void testBindParameterNotConfigured() {
		TargetObject bindableObject = dataBinderBindWithConfigs(TargetObject.class, Collections.<ParameterConfiguration> emptyList(),
				"boolField", Boolean.TRUE.toString());
		assertFalse("Field should not be set because of no config", bindableObject.boolField);
	}

	/** Parameters with an {@link Policy#IGNORE ignoring policy} should be bound even if they fail validation. */
	@Test
	public void testPolicyIgnore() {
		ParameterConfiguration config = new ParameterConfiguration(INT_FIELD, "onlyA", 1, false, Policy.IGNORE);
		TargetObject bindableObject = dataBinderBindWithConfigs(TargetObject.class, Collections.singletonList(config), INT_FIELD, "1");
		assertEquals("Field marked as ignore validation", 1, bindableObject.intField);

		config = new ParameterConfiguration(INT_FIELD, "HTTPParameterValue", 1, false, Policy.IGNORE);
		bindableObject = dataBinderBindWithConfigs(TargetObject.class, Collections.singletonList(config), INT_FIELD, "1");
		assertEquals("A good value passed with ignore policy, but not set?", 1, bindableObject.intField);
	}

	/** Parameters with a {@link Policy#BLANK blanking policy} should be blanked if they fail validation. */
	@Test
	public void testPolicyBlank() {
		ParameterConfiguration config = new ParameterConfiguration(INT_FIELD, "onlyA", 1, false, Policy.BLANK);
		TargetObject bindableObject = dataBinderBindWithConfigs(TargetObject.class, Collections.singletonList(config), INT_FIELD, "1");
		assertEquals("Field marked as blank on validation failure", 0, bindableObject.intField);

		config = new ParameterConfiguration(INT_FIELD, "HTTPParameterValue", 1, false, Policy.BLANK);
		bindableObject = dataBinderBindWithConfigs(TargetObject.class, Collections.singletonList(config), INT_FIELD, "1");
		assertEquals("A good value passed with blank policy, but not set?", 1, bindableObject.intField);
	}

	/** Parameters with a {@link Policy#FILTER filter policy} should be filtered if they fail validation. */
	@Test
	public void testPolicyFilter() {
		ParameterConfiguration config = new ParameterConfiguration(STRING_FIELD, UPPER_CASE_RULE, Integer.MAX_VALUE, false, Policy.FILTER);
		TargetObject bindableObject = dataBinderBindWithConfigs(TargetObject.class, Collections.singletonList(config), STRING_FIELD,
				"UPPERfield");
		assertEquals("Field not filtered correctly when incorrect values passed", "UPPER", bindableObject.stringField);

		config = new ParameterConfiguration(STRING_FIELD, UPPER_CASE_RULE, Integer.MAX_VALUE, false, Policy.FILTER);
		bindableObject = dataBinderBindWithConfigs(TargetObject.class, Collections.singletonList(config), STRING_FIELD, "UPPERFIELD");
		assertEquals("A good value passed with filter policy, but not set?", "UPPERFIELD", bindableObject.stringField);
	}

	/** Ensures that we are able to handle {@code null}s when our configuration allows us to do so. */
	@Test
	public void testPolicyFilterWithNulls() {
		ParameterConfiguration config = new ParameterConfiguration(STRING_FIELD, UPPER_CASE_RULE, Integer.MAX_VALUE, true, Policy.FILTER);
		TargetObject bindableObject = dataBinderBindWithConfigs(TargetObject.class, STR_VALUE, Collections.singletonList(config),
				STRING_FIELD, null);
		assertNull("Unable to set null value during filtering", bindableObject.stringField);
	}

	/** Ensures that if we are given a {@code null}, but we aren't allowing {@code null}s, we filter it out. */
	@Test
	public void testPolicyFilterWithNullsNotAllowed() {
		ParameterConfiguration config = new ParameterConfiguration(STRING_FIELD, UPPER_CASE_RULE, Integer.MAX_VALUE, false, Policy.FILTER);
		TargetObject bindableObject = dataBinderBindWithConfigs(TargetObject.class, STR_VALUE, Collections.singletonList(config),
				STRING_FIELD, null);
		assertNotNull("Null value was not filtered out", bindableObject.stringField);
	}

	/** If we exceed the line length when a {@link Policy#FILTER filter policy} is used, we should trim the value. */
	@Test
	public void testPolicyFilteredWithExceededLength() {
		final int maxLength = 3;
		ParameterConfiguration config = new ParameterConfiguration(STRING_FIELD, UPPER_CASE_RULE, maxLength, false, Policy.FILTER);
		TargetObject bindableObject = dataBinderBindWithConfigs(TargetObject.class, Collections.singletonList(config), STRING_FIELD,
				"LONGVALUE");
		assertEquals("Fields that exceed max length, should be trimmed", "LON", bindableObject.stringField);

		// ensure correct number of characters are removed if we exceed by 1
		bindableObject = dataBinderBindWithConfigs(TargetObject.class, Collections.singletonList(config), STRING_FIELD, "LONG");
		assertEquals("Fields that exceed max length, should be trimmed", "LON", bindableObject.stringField);

		bindableObject = dataBinderBindWithConfigs(TargetObject.class, Collections.singletonList(config), STRING_FIELD, "invalidLONGVALUE");
		assertEquals("Fields that exceed max length, should be trimmed after its filtered", "LON", bindableObject.stringField);
	}

	/** Tests the pattern matching ability of the binder when using wild cards and array matching. */
	@Test
	public void testPatternMatchingWildCardsArrays() {
		ParameterConfiguration configWithArray = new ParameterConfiguration("objects[*]*.stringField");

		final String value = STR_VALUE;
		final String initialValue = "initial";

		NestedObject bindableObject = new NestedObject();
		bindableObject.objectsField = new NestedObject();
		bindableObject.objects = Collections.singletonList(new NestedObject());
		bindableObject.getObjects().get(0).objectsField = new NestedObject();
		bindableObject.getObjects().get(0).objects = Collections.singletonList(new NestedObject());
		bindableObject.getObjects().get(0).getObjects().get(0).objects = Collections.singletonList(new NestedObject());

		bindableObject.stringField = initialValue;
		dataBinderBindWithConfigs(bindableObject, Collections.singletonList(configWithArray), STRING_FIELD, value);
		assertEquals("Field without similar name not configured, but changed", initialValue, bindableObject.stringField);

		bindableObject.getObjects().get(0).getObjectsField().stringField = initialValue;
		dataBinderBindWithConfigs(bindableObject, Collections.singletonList(configWithArray), "objects[0].objectsField.stringField", value);
		assertEquals("Closely named sub-field not changed, but configured", value,
				bindableObject.getObjects().get(0).getObjectsField().stringField);

		bindableObject.getObjects().get(0).getObjects().get(0).getObjects().get(0).stringField = initialValue;
		dataBinderBindWithConfigs(bindableObject, Collections.singletonList(configWithArray),
				"objects[0].objects[0].objects[0].stringField", value);
		assertEquals("Deeply nested field was not changed, but should match", value, bindableObject.getObjects().get(0).getObjects().get(0)
				.getObjects().get(0).stringField);

		bindableObject.getObjectsField().stringField = initialValue;
		dataBinderBindWithConfigs(bindableObject, Collections.singletonList(configWithArray), "objectsField.stringField", value);
		assertEquals("Closely named root-field was changed, but not configured", initialValue, bindableObject.getObjectsField().stringField);
	}

	/**
	 * Tests the pattern matching ability of the binder when there are 2 configurations with patterns which are closely
	 * related. In this case, they overlap, but this is not necessary.
	 */
	@Test
	public void testPatternMatchingWildCardsCloselyNamed() {
		ParameterConfiguration configWithArray = new ParameterConfiguration("objects[*]*.stringField");
		ParameterConfiguration configNoArray = new ParameterConfiguration("objects*.stringField");

		final String value = STR_VALUE;
		final String initialValue = "initial";

		NestedObject bindableObject = new NestedObject();
		bindableObject.objectsField = new NestedObject();
		bindableObject.objects = Collections.singletonList(new NestedObject());
		bindableObject.getObjects().get(0).objectsField = new NestedObject();
		bindableObject.getObjects().get(0).objects = Collections.singletonList(new NestedObject());
		bindableObject.getObjects().get(0).getObjects().get(0).objects = Collections.singletonList(new NestedObject());

		bindableObject.getObjectsField().stringField = initialValue;
		dataBinderBindWithConfigs(bindableObject, Collections.singletonList(configNoArray), "objectsField.stringField", value);
		assertEquals("Configured to match both similarly named fields, but not changed", value,
				bindableObject.getObjectsField().stringField);

		bindableObject.getObjects().get(0).getObjectsField().stringField = initialValue;
		dataBinderBindWithConfigs(bindableObject, Collections.singletonList(configNoArray), "objects[0].objectsField.stringField", value);
		assertEquals("Subfield configured to match, but not changed", value,
				bindableObject.getObjects().get(0).getObjectsField().stringField);

		bindableObject.stringField = initialValue;
		dataBinderBindWithConfigs(bindableObject, Collections.singletonList(configWithArray), STRING_FIELD, value);
		assertEquals("Field without similar name not configured, but changed", initialValue, bindableObject.stringField);
	}

	/** Setting the allowed fields happens internally. You should not be allowed to change them. */
	@Test(expected = UnsupportedOperationException.class)
	public void testAllowedFields() {
		new EsapiServletRequestDataBinder(new TargetObject(), "target", Collections.<ParameterConfiguration> emptyList()).setAllowedFields(
				"some", "thing");
	}

	/** Setting the disallowed fields happens internally. You should not be allowed to change them. */
	@Test(expected = UnsupportedOperationException.class)
	public void testDisallowedFields() {
		new EsapiServletRequestDataBinder(new TargetObject(), "target", Collections.<ParameterConfiguration> emptyList())
				.setDisallowedFields("some", "thing");
	}

	/** An example object which we bind to for tests. */
	@SuppressWarnings("unused") // getters/setters required for binding
	private static final class TargetObject {
		private boolean boolField;
		private String stringField;
		private int intField;

		public boolean isBoolField() {
			return boolField;
		}

		public void setBoolField(final boolean boolField) {
			this.boolField = boolField;
		}

		public String getStringField() {
			return stringField;
		}

		public void setStringField(final String stringField) {
			this.stringField = stringField;
		}

		public int getIntField() {
			return intField;
		}

		public void setIntField(final int intField) {
			this.intField = intField;
		}
	}

	/**
	 * An example object which we bind to for tests which fakes nested fields. {@link NestedObject#objects} and
	 * {@link NestedObject#objectsField} are similarily named in order to test configuration; their associated getters
	 * and setters must match.
	 */
	@SuppressWarnings("unused") // getters/setters required for binding
	private static final class NestedObject {
		private List<NestedObject> objects;
		private NestedObject objectsField; // depends on name to be similar to above
		private String stringField;

		public List<NestedObject> getObjects() {
			return objects;
		}

		public void setObjects(final List<NestedObject> objects) {
			this.objects = objects;
		}

		public NestedObject getObjectsField() {
			return objectsField;
		}

		public void setObjectsField(final NestedObject objectsField) {
			this.objectsField = objectsField;
		}

		public String getStringField() {
			return stringField;
		}

		public void setStringField(final String stringField) {
			this.stringField = stringField;
		}
	}
}
