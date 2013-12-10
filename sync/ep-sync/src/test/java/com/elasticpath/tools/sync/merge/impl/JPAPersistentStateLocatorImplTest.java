package com.elasticpath.tools.sync.merge.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PostLoad;
import javax.persistence.Version;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.tools.sync.exception.SyncToolRuntimeException;
import com.elasticpath.tools.sync.merge.MergeFilter;
import com.elasticpath.tools.sync.utils.SyncUtils;

/**
 * Test for JPAPersistentStateLocatorImpl.
 */
public class JPAPersistentStateLocatorImplTest {

	private static final String BASIC_VERSION_METHOD = "getVersion";

	private static final String ONE_TO_ONE_METHOD = "getOneToOne";

	private static final String MANY_TO_ONE_METHOD = "getManyToOne";

	private static final String ONE_TO_MANY_METHOD = "getOneToMany";

	private static final String MANY_TO_MANY_METHOD = "getManyToMany";

	private static final String COLUMN_METHOD = "getColumn";

	private static final String BASIC_ID_METHOD = "getId";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private final MergeFilter filter = context.mock(MergeFilter.class);

	private final SyncUtils syncUtils = context.mock(SyncUtils.class);

	private JPAPersistentStateLocatorImpl persistentStateLocator;

	/**
	 * Test Interface for getting annotated methods.
	 */
	private interface TestInterface {
		@Column
		int getColumn();

		void setColumn(int index);

		@Column
		@Version
		int getVersion();

		@Column
		@Id
		int getId();

		@ManyToOne
		int getManyToOne();

		@OneToOne
		int getOneToOne();

		@OneToMany
		int getOneToMany();

		@ManyToMany
		int getManyToMany();
	}

	/**
	 * Test Class for getting annotated methods.
	 */
	@SuppressWarnings("unused")
	private static class TestClass {
		@Column
		int getBasic() {
			return 0;
		}

		void setBasic(final int index) {
			// empty
		}

		@OneToMany
		int getOneToMany() {
			return 0;
		}

		void setOneToMany() {
			// empty
		}

		@OneToOne
		int getOneToOne() {
			return 0;
		}

		void setOneToOne() {
			// empty
		}

		@PostLoad
		void doAfterLoad() {
			// empty
		}
	}

	/**
	 * Setup test.
	 */
	@Before
	public void setUp() {
		persistentStateLocator = new JPAPersistentStateLocatorImpl();
		persistentStateLocator.setFilter(filter);
		persistentStateLocator.setSyncUtils(syncUtils);
	}

	/**
	 * Tests extractPersistentStateAttributes.
	 */
	@Test
	public void testExtractPersistentStateAttributes() {
		persistentStateLocator = new JPAPersistentStateLocatorImpl() {
			@Override
			void addMethod(final Map<Method, Method> attributes, final Method method) throws SyncToolRuntimeException {
				attributes.put(method, null);
			}
		};

		Map<Method, Method> basicAttributes = new HashMap<Method, Method>();
		Map<Method, Method> singleValuedAssociations = new HashMap<Method, Method>();
		Map<Method, Method> collectionValuedAssociations = new HashMap<Method, Method>();
		Set<Method> postLoadMethods = new HashSet<Method>();
		persistentStateLocator.extractPersistentStateAttributes(TestClass.class, basicAttributes, singleValuedAssociations,
				collectionValuedAssociations, postLoadMethods);

		assertEquals(1, basicAttributes.size());
		assertEquals(1, singleValuedAssociations.size());
		assertEquals(1, collectionValuedAssociations.size());
		assertEquals(1, postLoadMethods.size());
	}

	/**
	 * Tests addMethod.
	 *
	 * @throws Exception if test data is wrong
	 */
	@Test
	public void testAddMethod() throws Exception {
		context.checking(new Expectations() {
			{
				oneOf(syncUtils).createSetterName(COLUMN_METHOD);
				will(returnValue("setColumn"));
				oneOf(syncUtils).findDeclaredMethodWithFallback(TestInterface.class, "setColumn", int.class);
				will(returnValue(getTestMethodByName("setColumn", int.class)));
			}
		});

		persistentStateLocator = new JPAPersistentStateLocatorImpl() {
			@Override
			boolean methodPermitted(final Method method) {
				return true;
			}
		};
		persistentStateLocator.setFilter(filter);
		persistentStateLocator.setSyncUtils(syncUtils);

		Map<Method, Method> attributes = new HashMap<Method, Method>();
		persistentStateLocator.addMethod(attributes, getTestMethodByName(COLUMN_METHOD));

	}

	/**
	 * Tests isCollectionField.
	 *
	 * @throws Exception if test data is wrong
	 */
	@Test
	public void testIsCollectionField() throws Exception {
		assertTrue(persistentStateLocator.isCollectionField(getTestMethodByName(ONE_TO_MANY_METHOD)));
		assertTrue(persistentStateLocator.isCollectionField(getTestMethodByName(MANY_TO_MANY_METHOD)));
		assertFalse(persistentStateLocator.isCollectionField(getTestMethodByName(COLUMN_METHOD)));
	}

	/**
	 * Tests isSingleField.
	 *
	 * @throws Exception if test data is wrong
	 */
	@Test
	public void testIsSingleField() throws Exception {
		assertTrue(persistentStateLocator.isSingleField(getTestMethodByName(MANY_TO_ONE_METHOD)));
		assertTrue(persistentStateLocator.isSingleField(getTestMethodByName(ONE_TO_ONE_METHOD)));
		assertFalse(persistentStateLocator.isSingleField(getTestMethodByName(COLUMN_METHOD)));
	}

	/**
	 * Tests isStateField.
	 *
	 * @throws Exception if test data is wrong
	 */
	@Test
	public void testIsStateField() throws Exception {
		assertTrue(persistentStateLocator.isStateField(getTestMethodByName(COLUMN_METHOD)));
		assertFalse(persistentStateLocator.isStateField(getTestMethodByName(BASIC_VERSION_METHOD)));
		assertFalse(persistentStateLocator.isStateField(getTestMethodByName(BASIC_ID_METHOD)));
	}

	/**
	 * Tests methodPermitted.
	 *
	 * @throws Exception if test data is wrong
	 */
	@Test
	public void testMethodPermitted() throws Exception {
		context.checking(new Expectations() {
			{
				oneOf(filter).isMergePermitted(TestInterface.class, COLUMN_METHOD);
				will(returnValue(false));
			}
		});

		assertFalse(persistentStateLocator.methodPermitted(getTestMethodByName(COLUMN_METHOD)));

		persistentStateLocator.setFilter(null);
		assertTrue(persistentStateLocator.methodPermitted(getTestMethodByName(COLUMN_METHOD)));
	}

	private Method getTestMethodByName(final String methodName, final Class< ? >... params) throws Exception {
		return TestInterface.class.getMethod(methodName, params);
	}

}
