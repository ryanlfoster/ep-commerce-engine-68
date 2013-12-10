package com.elasticpath.test.util;

import static com.elasticpath.test.util.Assert.assertEqualsReflectively;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.AssertionFailedError;

import org.junit.Test;


/**
 * Test the Assert class.
 */
public class AssertTest {

    private static final String MESSAGE_IS_INCORRECT = "Message is incorrect";
    private static final String MESSAGE = "Somebody set up us the bomb.";
    private static final String NAME = "MyName";
    private static final int SIXTEEN = 16;
    private static final int EIGHT = 8;
    private static final int FIVE = 5;
    private static final int FOUR = 4;
    private static final int TWO = 2;
    private static final int ONE = 1;
    private static final int THREE = 3;

    /**
     * Equal objects should be reflectively equal.
     */
    @Test
    public void shouldPassWhenEqual() {
        Bar bar1 = new Bar(THREE, Arrays.asList(ONE, TWO, FOUR));
        Bar bar2 = new Bar(THREE, Arrays.asList(ONE, TWO, FOUR));
        Foo foo1 = new Foo(NAME, bar1);
        Foo foo2 = new Foo(NAME, bar2);
        assertEqualsReflectively(MESSAGE, foo1, foo2);
    }

    /**
     * Objects with different fields somewhere in the tree should fail.
     */
    @Test
    public void shouldFindDifferentFields() {
        Bar bar1 = new Bar(THREE, Arrays.asList(ONE, TWO, FOUR));
        Bar bar2 = new Bar(FIVE, Arrays.asList(ONE, ONE, TWO, THREE, FIVE));
        Foo foo1 = new Foo(NAME, bar1);
        Foo foo2 = new Foo(NAME, bar2);
        try {
            assertEqualsReflectively(MESSAGE, foo1, foo2);
        } catch (AssertionFailedError e) {
            assertEquals(MESSAGE_IS_INCORRECT, MESSAGE + " (Foo).bar.size expected: 3 but was 5", e.getMessage());
        }
    }
    
    /**
     * Objects with lists somewhere in the tree with different values should fail.
     */
    @Test
    public void shouldFindDifferentListItemsInFields() {
        Bar bar1 = new Bar(FIVE, Arrays.asList(ONE, TWO, FOUR, EIGHT, SIXTEEN));
        Bar bar2 = new Bar(FIVE, Arrays.asList(ONE, ONE, TWO, THREE, FIVE));
        Foo foo1 = new Foo(NAME, bar1);
        Foo foo2 = new Foo(NAME, bar2);
        try {
            assertEqualsReflectively(MESSAGE, foo1, foo2);
        } catch (AssertionFailedError e) {
            assertEquals(MESSAGE_IS_INCORRECT, MESSAGE + " (Foo).bar.values[1] expected: 2 but was 1", e.getMessage());
        }
    }
    
    /**
     * Should fail when the actual map is missing keys.
     */
    @Test
    public void shouldFindMapsWithMissingKeys() {
        Map<Integer, String> expected = new HashMap<Integer, String>();
        expected.put(ONE, NAME);
        expected.put(TWO, NAME);
        expected.put(THREE, NAME);
        
        Map<Integer, String> actual = new HashMap<Integer, String>();
        actual.put(ONE, NAME);
        actual.put(THREE, NAME);
        
        try {
            assertEqualsReflectively(MESSAGE, expected, actual);
        } catch (AssertionFailedError e) {
            assertEquals(MESSAGE_IS_INCORRECT, MESSAGE + " (HashMap)keySet() expected: 2 in set: [1, 3]", e.getMessage());
        }
    }
    
    /**
     * Should fail when the actual map has extra keys.
     */
    @Test
    public void shouldFindMapsWithExtraKeys() {
        Map<Integer, String> expected = new HashMap<Integer, String>();
        expected.put(ONE, NAME);
        expected.put(THREE, NAME);
        
        Map<Integer, String> actual = new HashMap<Integer, String>();
        actual.put(ONE, NAME);
        actual.put(TWO, NAME);
        actual.put(THREE, NAME);
        
        try {
            assertEqualsReflectively(MESSAGE, expected, actual);
        } catch (AssertionFailedError e) {
            assertEquals(MESSAGE_IS_INCORRECT, MESSAGE + " (HashMap)keySet() found unexpected: 2, was expecting: [1, 3]", e.getMessage());
        }
    }
    
    /**
     * Should fail when the keys are the same, but values differ.
     */
    @Test
    public void shouldFindMapsWithDifferentValues() {
        Map<Integer, String> expected = new HashMap<Integer, String>();
        expected.put(ONE, NAME);
        expected.put(TWO, NAME);
        expected.put(THREE, NAME);
        
        Map<Integer, String> actual = new HashMap<Integer, String>();
        actual.put(ONE, NAME);
        actual.put(TWO, NAME + NAME);
        actual.put(THREE, NAME);
        
        try {
            assertEqualsReflectively(MESSAGE, expected, actual);
        } catch (AssertionFailedError e) {
            assertEquals(MESSAGE_IS_INCORRECT, MESSAGE + " (HashMap)[2] expected: MyName but was MyNameMyName", e.getMessage());
        }
    }
    
    /**
     * Foo.
     */
    @SuppressWarnings("unused")
    private class Foo {
        private final String name;
        private final Bar bar;

        public Foo(final String name, final Bar bar) {
            this.name = name;
            this.bar = bar;
        }
    }

    /**
     * Bar.
     */
    @SuppressWarnings("unused")
    private class Bar {
        private final int size;
        private final List<Integer> values;

        public Bar(final int size, final List<Integer> values) {
            this.size = size;
            this.values = values;
        }
    }

}
