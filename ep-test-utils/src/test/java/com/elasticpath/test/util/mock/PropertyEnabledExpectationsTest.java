package com.elasticpath.test.util.mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

/** Test case for {@link PropertyEnabledExpectations}. */
@SuppressWarnings("PMD.NonStaticInitializer")
public class PropertyEnabledExpectationsTest {
	private static final String INITIAL = "initial";
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	/** Calling a property which we allow should be stateful when changed. */
	@Test
	public void testPropertyStateful() {
		final Base base = context.mock(Base.class);
		context.checking(new PropertyEnabledExpectations() {
			{
				allowingProperty(base).getId();
			}
		});

		final String key1 = "something";
		final String key2 = "another";

		base.setId(key1);
		assertEquals(key1, base.getId());
		base.setId(key2);
		assertEquals(key2, base.getId());
	}

	/** The initial value should be able to be set. */
	@Test
	public void testPropertyDefaultValue() {
		final Base base = context.mock(Base.class);
		final String value = "value";
		context.checking(new PropertyEnabledExpectations() {
			{
				allowingProperty(base).getId();
				will(returnValue(INITIAL));
			}
		});

		assertEquals("Initial value not set", INITIAL, base.getId());
		base.setId(value);
		assertEquals("Initial value should be able to be changed", value, base.getId());
	}

	/** A property defined in a super class should be able to be accessed. */
	@Test
	public void testPropertyInSuper() {
		final Valuable valuable = context.mock(Valuable.class);
		context.checking(new PropertyEnabledExpectations() {
			{
				allowingProperty(valuable).getId();
				will(returnValue(INITIAL));
			}
		});

		assertEquals("Initial value not set", INITIAL, valuable.getId());
	}

	/** Properties with integral types that require boxing should work as well. */
	@Test
	public void testPropertyIntegralType() {
		final Base base = context.mock(Base.class);
		context.checking(new PropertyEnabledExpectations() {
			{
				allowingProperty(base).isValid();
				will(returnValue(true));
			}
		});

		assertTrue("Initial value was not set", base.isValid());
		base.setValid(false);
		assertFalse("Value didn't change", base.isValid());
	}

	/** When allowing properties, you should be able to do so with the {@link ClassImposteriser}. */
	@Test
	@Ignore("We don't currently work with the class imposterizer")
	public void testPropertyWithClassImposterizer() {
		context.setImposteriser(ClassImposteriser.INSTANCE);
		final BaseImpl base = context.mock(BaseImpl.class);
		context.checking(new PropertyEnabledExpectations() {
			{
				allowingProperty(base).getId();
				will(returnValue(INITIAL));
			}
		});

		final String key = "mmm";
		assertEquals("Initial value was not set", INITIAL, base.getId());
		base.setId(key);
		assertEquals("Value didn't change", key, base.getId());
	}

	/** Test interface for mocks. */
	private interface Base {
		String getId();
		void setId(String identifier);
		boolean isValid();
		void setValid(boolean valid);
	}

	/** Test interface for mocks. */
	private interface Valuable extends Base {
		String getValue();
		void setValue(String value);
	}

	/** Test interface for mocks. */
	private class BaseImpl implements Base {
		@Override
		public String getId() {
			// these do nothing so we can test the class imposterizer
			return null;
		}

		@Override
		public void setId(final String identifier) {
			// these do nothing so we can test the class imposterizer
		}

		@Override
		public boolean isValid() {
			// these do nothing so we can test the class imposterizer
			return false;
		}

		@Override
		public void setValid(final boolean valid) {
			// these do nothing so we can test the class imposterizer
		}
	}
}
