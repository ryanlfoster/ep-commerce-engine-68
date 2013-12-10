/*
 * Copyright (c) Elastic Path Software Inc., 2011.
 */
package com.elasticpath.domain.shipping.evaluator.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.shipping.ShipmentType;
import com.elasticpath.domain.shipping.evaluator.ShipmentTypeEvaluatorStrategy;
import com.elasticpath.domain.shoppingcart.ShoppingItem;

/**
 * Test {@link ShoppingCartShipmentTypeEvaluator}.
 */
public class ShoppingCartShipmentTypeEvaluatorTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private ShoppingCartShipmentTypeEvaluator shoppingCartShipmentTypeEvaluator;

	private ShipmentTypeEvaluatorStrategy mockShipmentTypeEvaluatorStrategy;

	private ShoppingItem electronicShoppingItem;

	private ShoppingItem physicalShoppingItem;

	private ShoppingItem serviceShoppingItem;

	private Set<ShipmentType> electronicShipmentTypes;

	/**
	 * Setup instance variables for each test. This ensures the mockery is initialized each time and the tests are independent.
	 *
	 * @throws Exception If an exception occurs.
	 */
	@Before
	public void setUp() throws Exception {
		mockShipmentTypeEvaluatorStrategy = context.mock(ShipmentTypeEvaluatorStrategy.class);
		shoppingCartShipmentTypeEvaluator = new ShoppingCartShipmentTypeEvaluator(mockShipmentTypeEvaluatorStrategy);

		electronicShoppingItem = context.mock(ShoppingItem.class, "electronicShoppingItem");
		physicalShoppingItem = context.mock(ShoppingItem.class, "physicalShoppingItem");
		serviceShoppingItem = context.mock(ShoppingItem.class, "serviceShoppingItem");
		electronicShipmentTypes = createShipmentTypes(ShipmentType.ELECTRONIC);
	}

	/**
	 * Test no shipment types on empty cart.
	 */
	@Test
	public void testNoShipmentTypes() {
		assertTrue("No shipment types should be set prior to evaluation.", shoppingCartShipmentTypeEvaluator.getShipmentTypes().isEmpty());
	}

	/**
	 * Visit a single shopping item.
	 */
	@Test
	public void visitASingleShoppingItem() {

		context.checking(new Expectations() {
			{
				oneOf(mockShipmentTypeEvaluatorStrategy).evaluate(electronicShoppingItem);
				will(returnValue(ShipmentType.ELECTRONIC));
			}
		});
		shoppingCartShipmentTypeEvaluator.visit(electronicShoppingItem);
		Set<ShipmentType> shipmentTypes = shoppingCartShipmentTypeEvaluator.getShipmentTypes();
		assertTrue(shipmentTypes.containsAll(electronicShipmentTypes));
		assertShipmentTypesEquals(electronicShipmentTypes, shipmentTypes);
	}

	/**
	 * Visit multiple shopping item of the same shipping type.
	 */
	@Test
	public void visitMultipleShoppingItemsOfTheSameShippingType() {

		context.checking(new Expectations() {
			{
				exactly(2).of(mockShipmentTypeEvaluatorStrategy).evaluate(electronicShoppingItem);
				will(returnValue(ShipmentType.ELECTRONIC));
			}
		});
		shoppingCartShipmentTypeEvaluator.visit(electronicShoppingItem);
		shoppingCartShipmentTypeEvaluator.visit(electronicShoppingItem);
		Set<ShipmentType> shipmentTypes = shoppingCartShipmentTypeEvaluator.getShipmentTypes();
		assertShipmentTypesEquals(electronicShipmentTypes, shipmentTypes);
	}

	/**
	 * Visit multiple shopping items with different shipping type.
	 */
	@Test
	public void visitMultipleShoppingItemsWithDifferentShippingType() {

		context.checking(new Expectations() {
			{
				oneOf(mockShipmentTypeEvaluatorStrategy).evaluate(electronicShoppingItem);
				will(returnValue(ShipmentType.ELECTRONIC));
				oneOf(mockShipmentTypeEvaluatorStrategy).evaluate(physicalShoppingItem);
				will(returnValue(ShipmentType.PHYSICAL));
			}
		});
		shoppingCartShipmentTypeEvaluator.visit(electronicShoppingItem);
		shoppingCartShipmentTypeEvaluator.visit(physicalShoppingItem);
		Set<ShipmentType> shipmentTypes = shoppingCartShipmentTypeEvaluator.getShipmentTypes();
		Set<ShipmentType> expectedShipmentTypes = createShipmentTypes(ShipmentType.PHYSICAL, ShipmentType.ELECTRONIC);
		assertShipmentTypesEquals(expectedShipmentTypes, shipmentTypes);
	}

	/**
	 * Ensure fail fast when all shipping types found.
	 */
	@Test
	public void ensureFailFastWhenAllShippingTypesFound() {

		context.checking(new Expectations() {
			{
				oneOf(mockShipmentTypeEvaluatorStrategy).evaluate(electronicShoppingItem);
				will(returnValue(ShipmentType.ELECTRONIC));
				oneOf(mockShipmentTypeEvaluatorStrategy).evaluate(physicalShoppingItem);
				will(returnValue(ShipmentType.PHYSICAL));
				oneOf(mockShipmentTypeEvaluatorStrategy).evaluate(serviceShoppingItem);
				will(returnValue(ShipmentType.SERVICE));
			}
		});
		shoppingCartShipmentTypeEvaluator.visit(electronicShoppingItem);
		shoppingCartShipmentTypeEvaluator.visit(physicalShoppingItem);
		shoppingCartShipmentTypeEvaluator.visit(serviceShoppingItem);
		shoppingCartShipmentTypeEvaluator.visit(electronicShoppingItem);
		Set<ShipmentType> shipmentTypes = shoppingCartShipmentTypeEvaluator.getShipmentTypes();
		Set<ShipmentType> expectedShipmentTypes = createShipmentTypes(ShipmentType.PHYSICAL, ShipmentType.ELECTRONIC, ShipmentType.SERVICE);
		assertShipmentTypesEquals(expectedShipmentTypes, shipmentTypes);
	}

	private Set<ShipmentType> createShipmentTypes(final ShipmentType... types) {
		Set<ShipmentType> result = new HashSet<ShipmentType>(types.length);
		for (ShipmentType type : types) {
			result.add(type);
		}
		return result;
	}

	private void assertShipmentTypesEquals(final Set<ShipmentType> expected, final Set<ShipmentType> actual) {
		assertEquals("The number of shipment types should be equal.", expected.size(), actual.size());
		assertTrue(String.format("The expected shipment types: %s should equal the actual shipment types %s.",
				expected.toString(),
				actual.toString()),
				actual.containsAll(expected));
	}

}

