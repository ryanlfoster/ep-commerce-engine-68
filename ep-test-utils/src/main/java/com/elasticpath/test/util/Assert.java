package com.elasticpath.test.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import junit.framework.AssertionFailedError;

/**
 * Test assertions.
 * 
 * @author dlewis
 */
@SuppressWarnings({"PMD.AvoidThrowingRawExceptionTypes", "PMD.UseSingleton", "PMD.CompareObjectsWithEquals" })
public class Assert extends org.junit.Assert {

    private static final List<String> REFLECTIVE_EQUALS_EXCLUDE_PACKAGES = new LinkedList<String>();

    static {
        REFLECTIVE_EQUALS_EXCLUDE_PACKAGES.add("java");
        REFLECTIVE_EQUALS_EXCLUDE_PACKAGES.add("javax");
        REFLECTIVE_EQUALS_EXCLUDE_PACKAGES.add("sun");
    }

    /**
     * Assert an object is equal by using reflection on its fields.
     * 
     * @param expected expected value
     * @param actual actual value
     */
    public static void assertEqualsReflectively(final Object expected, final Object actual) {
        assertEqualsReflectively(null, expected, actual);
    }

    /**
     * Assert an object is equal by using reflection on its fields.
     * 
     * @param message prefix of message for failure
     * @param expected expected value
     * @param actual actual value
     */
    public static void assertEqualsReflectively(final String message, final Object expected, final Object actual) {
        final Stack<String> stack = new Stack<String>();
        stack.push(getObjectName(expected));
        assertEqualsReflectively(new HashSet<Object>(), stack, message, expected, actual);
    }
    
    private static void assertEqualsReflectively(final Set<Object> alreadyChecked, final Stack<String> stack, final String message,
            final Object expected, final Object actual) {
        if (checkNulls(stack, message, expected, actual)) {
            final Class< ? > expectedClass = expected.getClass();
            if (isInReflectivelyEqualsPackage(expectedClass)) {
                compareReflectively(alreadyChecked, stack, message, expected, actual);
            } else if (expected instanceof Collection) {
                compareCollections(alreadyChecked, stack, message, expected, actual);
            } else if (expected instanceof Map) {
                compareMap(alreadyChecked, stack, message, expected, actual);
            } else {
                if ((expected == null && actual != null)
                        || (expected != null && !expected.equals(actual))) {
                    throw new AssertionFailedError(getMessagePrefix(message) + describeStack(stack)
                            + " expected: " + expected + " but was " + actual); 
                }
            }
        }
    }

    private static boolean checkNulls(final Stack<String> stack, final String message, final Object expected, final Object actual)
            throws AssertionFailedError {
        if (expected != null && actual == null) {
            throw new AssertionFailedError(getMessagePrefix(message) + describeStack(stack) + " Was null, expected " + expected);
        } else if (expected == null && actual != null) {
            throw new AssertionFailedError(getMessagePrefix(message) + describeStack(stack) + " Expected null, was " + actual);
        } else if (expected == null && actual == null) {
            return false;
        }
        return true;
    }

    private static void compareReflectively(final Set<Object> alreadyChecked, final Stack<String> stack, final String message,
            final Object expected, final Object actual) {
        final Class< ? > expectedClass = expected.getClass();
        if (expectedClass != actual.getClass()) {
            throw new AssertionFailedError(getMessagePrefix(message) + describeStack(stack) + " Expected class " + expectedClass.getName()
                    + ", was " + actual.getClass().getName());
        }
        if (!alreadyChecked.contains(expected)) {
            alreadyChecked.add(expected);
            final List<Field> fields = ReflectionHelper.getFields(expectedClass);
            for (final Field field : fields) {
                try {
                    if ((field.getModifiers() & Modifier.TRANSIENT) == 0) {
                        final Object expectedValue = field.get(expected);
                        final Object actualValue = field.get(actual);
                        if (expectedValue != actualValue) {
                            stack.push("." + field.getName());
                            assertEqualsReflectively(alreadyChecked, stack, message, expectedValue, actualValue);
                            stack.pop();
                        }
                    }
                } catch (final IllegalArgumentException e) {
                    throw new RuntimeException(e);
                } catch (final IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private static void compareCollections(final Set<Object> alreadyChecked, final Stack<String> stack, final String message, final Object expected,
            final Object actual) throws AssertionFailedError {
        if (expected instanceof List) {
            compareLists(alreadyChecked, stack, message, (List<?>) expected, (List<?>) actual);
        } else if (expected instanceof Set) {
            compareSets(alreadyChecked, stack, message, (Set<?>) expected, (Set<?>) actual);
        } else {
            throw new UnsupportedOperationException(getMessagePrefix(message) + "Unsupported collection: " + expected.getClass());
        }
    }

    private static void compareLists(final Set<Object> alreadyChecked, final Stack<String> stack, final String message, final List<?> expectedList,
            final List<?> actualList) throws AssertionFailedError {
        final Iterator<?> iteratorActual = actualList.iterator();
        final Iterator<?> iteratorExpected = expectedList.iterator();
        int index = 0;
        while (iteratorActual.hasNext() && iteratorExpected.hasNext()) {
            final Object expectedItem = iteratorExpected.next();
            final Object actualItem = iteratorActual.next();
            stack.push("[" + index + "]");
            assertEqualsReflectively(alreadyChecked, stack, message, expectedItem, actualItem);
            stack.pop();
            ++index;
        }
        if (iteratorExpected.hasNext()) {
            throw new AssertionFailedError(getMessagePrefix(message) + "Missing item " + iteratorExpected.next()
                    + " from list : " + actualList + ", expected " + expectedList);
        }
        if (iteratorActual.hasNext()) {
            throw new AssertionFailedError(getMessagePrefix(message) + "Unexpected item " + iteratorActual.next()
                    + " in list : " + actualList + ", expected " + expectedList);
        }
    }

    private static void compareSets(final Set<Object> alreadyChecked, final Stack<String> stack, final String message, final Set<?> expectedSet,
            final Set<?> actualSet) throws AssertionFailedError {
        assertSetContainsAllReflectively(alreadyChecked, stack, message, expectedSet, actualSet, true);
        assertSetContainsAllReflectively(alreadyChecked, stack, message, actualSet, expectedSet, false);
    }

    private static void assertSetContainsAllReflectively(final Set<Object> alreadyChecked, final Stack<String> stack, final String message,
            final Set<?> setToCheck, final Set<?> subSet, final boolean invertMessage) {
        for (Object obj : subSet) {
            assertSetContainsReflectively(alreadyChecked, stack, message, setToCheck, obj, invertMessage);
        }
    }

    private static void assertSetContainsReflectively(final Set<Object> alreadyChecked, final Stack<String> stack, final String message,
            final Set<?> setToCheck, final Object expected, final boolean invertMessage) {
        for (Object objToCheck : setToCheck) {
            try {
                assertEqualsReflectively(alreadyChecked, stack, message, expected, objToCheck);
                return;
            } catch (AssertionFailedError e) { // NOPMD
                // this just means there was some object in setToCheck that wasn't reflectively equal to the expected object.
            }
        }
        if (invertMessage) {
            throw new AssertionFailedError(getMessagePrefix(message) + describeStack(stack)
                    + " found unexpected: " + expected + ", was expecting: " + setToCheck);
        } else {
            throw new AssertionFailedError(getMessagePrefix(message) + describeStack(stack) + " expected: " + expected + " in set: " + setToCheck);
        }
    }
    
    private static void compareMap(final Set<Object> alreadyChecked, final Stack<String> stack, final String message, final Object expected,
            final Object actual) throws AssertionFailedError {
        stack.push("keySet()");
        compareCollections(alreadyChecked, stack, message, ((Map<?, ?>) expected).keySet(), ((Map<?, ?>) actual).keySet());
        stack.pop();
        compareMapValues(alreadyChecked, stack, message, (Map<?, ?>) expected, (Map<?, ?>) actual);
    }

	private static void compareMapValues(final Set<Object> alreadyChecked, final Stack<String> stack, final String message,
			final Map<?, ?> expectedMap, final Map<?, ?> actualMap) throws AssertionFailedError {
        for (Object expectedMapKey : expectedMap.keySet()) {
            Object actualMapKey = findActualMapKey(actualMap, expectedMapKey);
            Object expectedValue = expectedMap.get(expectedMapKey);
            Object actualValue = actualMap.get(actualMapKey);
            stack.push("[" + expectedMapKey + "]");
            assertEqualsReflectively(alreadyChecked, stack, message, expectedValue, actualValue);
            stack.pop();
        }
    }

    private static Object findActualMapKey(final Map<?, ?> actualMap, final Object key) {
        for (Object potentialKey : actualMap.keySet()) {
            try {
                assertEqualsReflectively(potentialKey, key);
                return potentialKey;
            } catch (AssertionFailedError e) { // NOPMD
                // this just means there was some key in actualMap that wasn't reflectively equal to the expected key.
            }
        }
        throw new IllegalStateException("This shouldn't happen because we've already checked the keys");
    }

    private static String getObjectName(final Object object) {
        if (object == null) {
            return "(null)";
        }
        return "(" + object.getClass().getSimpleName() + ")";
    }

    private static String getMessagePrefix(final String message) {
      if (message == null) {
          return "";
      }
      return message + " ";
    }

    private static boolean isInReflectivelyEqualsPackage(final Class< ? > klass) {
        final String className = klass.getName();
        for (final String packagePrefix : REFLECTIVE_EQUALS_EXCLUDE_PACKAGES) {
            if (className.startsWith(packagePrefix + ".")) {
                return false;
            }
        }
        return true;
    }

    /**
     * Assert that a string starts with a certain substring.
     * @param message prefix of message for failure
     * @param beginning String that <code>stringToCheck</code> must begin with.
     * @param stringToCheck String to check beginning of.
     */
    public static void assertStartsWith(final String message, final String beginning, final String stringToCheck) {
        if (!stringToCheck.startsWith(beginning)) {
            throw new AssertionFailedError(getMessagePrefix(message) + "Expected String to start with <" + beginning + "> but was <"
                    + stringToCheck + ">");
        }
    }
    
    private static String describeStack(final Stack<String> stack) {
        final StringBuilder builder = new StringBuilder();
        final Iterator<String> iterator = stack.iterator();
        while (iterator.hasNext()) {
            final String item = iterator.next();
            builder.append(item);
        }
        
        return builder.toString();
    }
    
}
