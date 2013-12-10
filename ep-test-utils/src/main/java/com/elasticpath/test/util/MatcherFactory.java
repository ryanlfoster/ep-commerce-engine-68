package com.elasticpath.test.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.AssertionFailedError;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 * Static utility (like Assert) for getting Matchers. 
 */
public final class MatcherFactory {
    
    private MatcherFactory() {
        // singleton
    }
    
    /** Matcher that checks objects for equality reflectively.
     * 
     * @param <T> type of object
     * @param obj object to check against
     * @return a matcher
     */
    public static <T> Matcher<T> reflectivelyEquals(final T obj) {
        return new TypeSafeMatcher<T>() {
            public boolean matchesSafely(final T item) {
                try {
                    Assert.assertEqualsReflectively(obj, item);
                } catch (AssertionFailedError e) {
                    e.printStackTrace(); // NOPMD
                    return false;
                }
                return true;
            }

            public void describeTo(final Description description) {
                description.appendText("Expected ").appendValue(obj);
            }
        };
    }

    /**
     * Matcher that checks that a list contains exactly the expected values, but in any order.
     * @param <T> type of list element
     * @param expectedValues values expected to be in the list
     * @return a matcher
     */
    public static <T> Matcher<List<T>> listContaining(final List<T> expectedValues) {
        final Set<T> expectedValuesSet = new HashSet<T>(expectedValues);
        return new TypeSafeMatcher<List<T>>() {

            public boolean matchesSafely(final List<T> actualValues) {
                return new HashSet<T>(actualValues).equals(expectedValuesSet);
            }

            public void describeTo(final Description description) {
                description.appendText("Expected ").appendValue(expectedValuesSet).appendText(" in any order");
            }
        };

    }
    

}
