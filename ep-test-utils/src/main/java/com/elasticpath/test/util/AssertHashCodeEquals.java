/**
 * Copyright (c) Elastic Path Software Inc., 2012
 */
package com.elasticpath.test.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Common methods to help determine basic hash code / equals contracts of objects.
 */
public final class AssertHashCodeEquals {

	private static final String BOTH_OBJECTS_SHOULD_HAVE_THE_SAME_HASH_CODE = "Both objects should have the same hashCode.";

	private static final String BOTH_OBJECTS_SHOULD_BE_EQUAL = "Both objects should be equal.";

	private static final String OBJECTS_SHOULD_NOT_BE_EQUAL = "Objects should not be equal.";

	private AssertHashCodeEquals() {
		// singleton - static methods only
	}

	/**
	 * Assert hash code / equals contract with regards to reflexivity.
	 *
	 * @param obj the obj
	 */
	public static void assertReflexivity(final Object obj) {
		assertEquals(BOTH_OBJECTS_SHOULD_BE_EQUAL, obj, obj);
		assertEquals(BOTH_OBJECTS_SHOULD_HAVE_THE_SAME_HASH_CODE, obj.hashCode(), obj.hashCode());
	}

	/**
	 * Assert hash code / equals contract with regards to symmetry.
	 *
	 * @param obj1 the obj1
	 * @param obj2 the obj2
	 */
	public static void assertSymmetry(final Object obj1, final Object obj2) {
		assertEquals(BOTH_OBJECTS_SHOULD_BE_EQUAL, obj1, obj2);
		assertEquals(BOTH_OBJECTS_SHOULD_BE_EQUAL, obj2, obj1);
		assertEquals(BOTH_OBJECTS_SHOULD_HAVE_THE_SAME_HASH_CODE, obj1.hashCode(), obj2.hashCode());
	}

	/**
	 * Assert hash code / equals contract with regards to non-symmetry with Objects that are not equivalent.
	 *
	 * @param obj1 the obj1
	 * @param obj2 the obj2
	 */
	public static void assertNonEquivalence(final Object obj1, final Object obj2) {
		assertFalse(OBJECTS_SHOULD_NOT_BE_EQUAL, obj1.equals(obj2));
		assertFalse(OBJECTS_SHOULD_NOT_BE_EQUAL, obj2.equals(obj1));
	}

	/**
	 * Assert hash code / equals contract with regards to transitivity.
	 *
	 * @param obj1 the obj1
	 * @param obj2 the obj2
	 * @param obj3 the obj3
	 */
	public static void assertTransitivity(final Object obj1, final Object obj2, final Object obj3) {
		assertEquals(BOTH_OBJECTS_SHOULD_BE_EQUAL, obj1, obj2);
		assertEquals(BOTH_OBJECTS_SHOULD_BE_EQUAL, obj2, obj3);
		assertEquals(BOTH_OBJECTS_SHOULD_BE_EQUAL, obj1, obj3);
		assertEquals(BOTH_OBJECTS_SHOULD_HAVE_THE_SAME_HASH_CODE, obj1.hashCode(), obj2.hashCode());
		assertEquals(BOTH_OBJECTS_SHOULD_HAVE_THE_SAME_HASH_CODE, obj2.hashCode(), obj3.hashCode());
	}

	/**
	 * Assert hash code / equals contract with regards to nullity.<br>
	 * <code>x.equals(null)</code> should return <code>false</code>
	 *
	 * @param obj the obj
	 */
	@SuppressWarnings({ "PMD.EqualsNull", "PMD.PositionLiteralsFirstInComparisons" })
	public static void assertNullity(final Object obj) {
		assertFalse(obj.equals(null));
	}

}
