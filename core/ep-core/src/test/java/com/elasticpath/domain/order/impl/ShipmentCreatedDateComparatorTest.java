package com.elasticpath.domain.order.impl;

import static org.junit.Assert.assertThat;

import java.util.Date;

import org.junit.Test;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import com.elasticpath.domain.order.OrderShipment;

/**
 * Tests the contract of the ShipmentCreatedDateComparator.
 */
@SuppressWarnings("PMD.TestClassWithoutTestCases")
public class ShipmentCreatedDateComparatorTest {
	/** Test. */
	@Test
	public void ensureTwoNullShipmentsAreEqual() {
		assertThat(null, equalTo(null));
	}

	/** Test. */
	@Test
	public void ensureSameShipmentsAreEqual() {
		PhysicalOrderShipmentImpl shipment = new PhysicalOrderShipmentImpl();
		assertThat(shipment, equalTo(shipment));
	}

	/** Test. */
	@Test
	public void ensureTwoOrdersWithNullCreatedDateAreEqual() {
		OrderShipment shipmentWithNullDate1 = new PhysicalOrderShipmentImpl();
		OrderShipment shipmentWithNullDate2 = new ElectronicOrderShipmentImpl();
		assertThat(shipmentWithNullDate1, equalTo(shipmentWithNullDate2));
	}

	/** Test. */
	@Test
	public void ensureShipmentsWithEqualDatesAreEqual() {
		PhysicalOrderShipmentImpl shipmentWithDateOf1 = new PhysicalOrderShipmentImpl();
		PhysicalOrderShipmentImpl otherShipmentWithDateOf1 = new PhysicalOrderShipmentImpl();

		shipmentWithDateOf1.setCreatedDate(new Date(1));
		otherShipmentWithDateOf1.setCreatedDate(new Date(1));
		assertThat(shipmentWithDateOf1, equalTo(otherShipmentWithDateOf1));
	}

	/** Test. */
	@Test
	public void ensureShipmentsWithDifferentDatesAreCorrectlyOrdered() {
		PhysicalOrderShipmentImpl shipmentWithDateOf1 = new PhysicalOrderShipmentImpl();
		PhysicalOrderShipmentImpl shipmentWithDateOf2 = new PhysicalOrderShipmentImpl();

		shipmentWithDateOf1.setCreatedDate(new Date(1));
		shipmentWithDateOf2.setCreatedDate(new Date(2));
		assertThat(shipmentWithDateOf1, lessThan(shipmentWithDateOf2));
		assertThat(shipmentWithDateOf2, greaterThan(shipmentWithDateOf1));
	}

	/** Test. */
	@Test
	public void ensureNullIsAlwaysGreaterThanNonNull() {
		PhysicalOrderShipmentImpl shipmentWithNullDate = new PhysicalOrderShipmentImpl();
		PhysicalOrderShipmentImpl shipmentWithDate = new PhysicalOrderShipmentImpl();
		shipmentWithDate.setCreatedDate(new Date());

		assertThat(null, lessThan(shipmentWithNullDate));
		assertThat(shipmentWithNullDate, greaterThan(null));

		assertThat(null, lessThan(shipmentWithDate));
		assertThat(shipmentWithDate, greaterThan(null));

		assertThat(shipmentWithNullDate, lessThan(shipmentWithDate));
	}

	/**
	 * Abstract base Matcher that can compare two OrderShipments.
	 */
	private abstract static class AbstractComparingMatcher extends BaseMatcher<OrderShipment> {
		private final OrderShipment shipment;

		private AbstractComparingMatcher(final OrderShipment shipment) {
			this.shipment = shipment;
		}

		/**
		 * Tests whether or not the relationship between the two objects is correct.
		 *
		 * @param shipment the first shipment
		 * @param otherShipment the second shipment
		 * @return true if the relationship is correct, false otherwise
		 */
		abstract boolean matches(OrderShipment shipment, OrderShipment otherShipment);

		/**
		 * Returns a description of the relationship being tested.
		 * @return the description
		 */
		abstract String describeRelationship();

		@Override
		public boolean matches(final Object item) {
			OrderShipment otherShipment = (OrderShipment) item;
			return matches(shipment, otherShipment);
		}

		@Override
		public void describeTo(final Description description) {
			description.appendText(describeRelationship());
			describeShipment(description, shipment);
		}

		private void describeShipment(final Description description, final OrderShipment shipment) {
			if (null == shipment) {
				description.appendValue(null);
			} else {
				description.appendText("OrderShipment with created date of ")
						.appendValue(shipment.getCreatedDate());
			}
		}

		@Override
		public void describeMismatch(final Object item, final Description description) {
			OrderShipment otherShipment = (OrderShipment) item;

			description.appendText("was ");
			describeShipment(description, otherShipment);
		}

		int compare(final OrderShipment shipment1, final OrderShipment shipment2) {
			return new ShipmentCreatedDateComparator().compare(shipment1, shipment2);
		}
	}

	/**
	 * AbstractComparingMatcher that implements a less than relationship test.
	 */
	private static final class ShipmentIsLessThan extends AbstractComparingMatcher {
		ShipmentIsLessThan(final OrderShipment shipment) {
			super(shipment);
		}

		boolean matches(final OrderShipment shipment, final OrderShipment otherShipment) {
			return -1 == compare(otherShipment, shipment);
		}

		String describeRelationship() {
			return "less than ";
		}
	}

	/**
	 * AbstractComparingMatcher that implements a greater than relationship test.
	 */
	private static final class ShipmentIsGreaterThan extends AbstractComparingMatcher {
		ShipmentIsGreaterThan(final OrderShipment shipment) {
			super(shipment);
		}

		boolean matches(final OrderShipment shipment, final OrderShipment otherShipment) {
			return 1 == compare(otherShipment, shipment);
		}

		String describeRelationship() {
			return "greater than ";
		}
	}

	/**
	 * AbstractComparingMatcher that implements an equals relationship test.
	 */
	private static final class ShipmentIsEqualTo extends AbstractComparingMatcher {
		ShipmentIsEqualTo(final OrderShipment shipment) {
			super(shipment);
		}

		boolean matches(final OrderShipment shipment, final OrderShipment otherShipment) {
			return 0 == compare(otherShipment, shipment);
		}

		String describeRelationship() {
			return "equal to ";
		}
	}

	private static Matcher<OrderShipment> lessThan(final OrderShipment shipment) {
		return new ShipmentIsLessThan(shipment);
	}

	private static Matcher<OrderShipment> greaterThan(final OrderShipment shipment) {
		return new ShipmentIsGreaterThan(shipment);
	}

	private static Matcher<OrderShipment> equalTo(final OrderShipment shipment) {
		return new ShipmentIsEqualTo(shipment);
	}
}
