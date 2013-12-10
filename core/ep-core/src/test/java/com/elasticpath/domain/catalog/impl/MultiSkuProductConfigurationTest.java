package com.elasticpath.domain.catalog.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.skuconfiguration.SkuOptionValue;

/**
 * Tests {@link MultiSkuProductConfiguration}.
 */
public class MultiSkuProductConfigurationTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private SkuConfiguration config1;

	private SkuConfiguration config2;

	private SkuConfiguration config3;

	private MultiSkuProductConfiguration product;

	/**
	 * Instantiates a new multi sku product configuration test.
	 */
	public MultiSkuProductConfigurationTest() {
		context.setImposteriser(ClassImposteriser.INSTANCE);
	}

	/**
	 * Runs before every test case.
	 */
	@Before
	public void setUp() {
		config1 = context.mock(SkuConfiguration.class, "var1");
		config2 = context.mock(SkuConfiguration.class, "var2");
		config3 = context.mock(SkuConfiguration.class, "var3");
		product = new MultiSkuProductConfiguration(Arrays.asList(config1, config2, config3));
	}

	/**
	 * Tests {@link ConfigurableProduct#getAvailableOptions(Collection, String)}.
	 */
	@Test
	public void testGetMatchingConfigurations() {
		final SkuOptionValue selectedValue = context.mock(SkuOptionValue.class, "selected-sov");

		final Collection<SkuOptionValue> options = Arrays.asList(selectedValue);

		context.checking(new Expectations() {
			{
				allowing(config1).isCompatibleWithSelection(options);
					will(returnValue(true));
				allowing(config2).isCompatibleWithSelection(options);
					will(returnValue(false));
				allowing(config3).isCompatibleWithSelection(options);
					will(returnValue(true));
			}
		});

		Collection<SkuConfiguration> availableConfigurations = product.getMatchingSkuConfigurations(options);
		assertEquals("should return two compatible skus", 2, availableConfigurations.size());
		assertTrue("should contain the compatible sku", availableConfigurations.contains(config1));
		assertFalse("shouldn't contain the incompatible sku", availableConfigurations.contains(config2));
		assertTrue("should contain the compatible sku", availableConfigurations.contains(config3));
	}

	/**
	 * Tests {@link ConfigurableProduct#getAvailableOptions(Collection, String)}.
	 */
	@Test
	public void testGetAvailableValuesForOptionKey() {

		final SkuOptionValue selectedValue = context.mock(SkuOptionValue.class, "selected-sov");

		final SkuOptionValue sov1 = context.mock(SkuOptionValue.class, "sov1");
		final SkuOptionValue sov2 = context.mock(SkuOptionValue.class, "sov2");

		final Collection<SkuOptionValue> options = Arrays.asList(selectedValue);
		final String optionKey = "key";

		context.checking(new Expectations() {
			{
				allowing(config1).isCompatibleWithSelection(options);
					will(returnValue(true));
				allowing(config1).getOptionValueForOptionKey(optionKey);
					will(returnValue(sov1));

				allowing(config2).isCompatibleWithSelection(options);
					will(returnValue(false));
				never(config2).getOptionValueForOptionKey(optionKey);

				allowing(config3).isCompatibleWithSelection(options);
					will(returnValue(true));
				allowing(config3).getOptionValueForOptionKey(optionKey);
					will(returnValue(sov2));
			}
		});

		Collection<SkuOptionValue> availableValues = product.getAvailableValuesForOptionKey(optionKey, options);
		assertEquals("should return two available values", 2, availableValues.size());
		assertTrue("should contain the available value", availableValues.contains(sov1));
		assertTrue("should contain the available value", availableValues.contains(sov2));
	}

}
