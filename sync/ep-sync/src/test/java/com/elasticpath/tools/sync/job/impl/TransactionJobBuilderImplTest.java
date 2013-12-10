package com.elasticpath.tools.sync.job.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.rules.Coupon;
import com.elasticpath.domain.rules.CouponConfig;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.tools.sync.exception.SyncToolConfigurationException;
import com.elasticpath.tools.sync.job.Command;
import com.elasticpath.tools.sync.job.JobEntry;
import com.elasticpath.tools.sync.job.TransactionJobUnit;
import com.elasticpath.tools.sync.job.descriptor.TransactionJobDescriptor;
import com.elasticpath.tools.sync.job.descriptor.TransactionJobDescriptorEntry;
import com.elasticpath.tools.sync.job.descriptor.impl.TransactionJobDescriptorImpl;
import com.elasticpath.tools.sync.merge.configuration.EntityLocator;
import com.elasticpath.tools.sync.target.AssociatedDaoAdapter;
import com.elasticpath.tools.sync.target.DaoAdapter;
import com.elasticpath.tools.sync.target.DaoAdapterFactory;

/**
 * Tests that the TransactionJobBuilderImpl class behaves as expected.
 */
public class TransactionJobBuilderImplTest {

	@org.junit.Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private EntityLocator entityLocator;

	private TransactionJobBuilderImpl transactionJobBuilderImpl;

	private DaoAdapterFactory daoAdapterFactory;

	/**
	 * Setup test.
	 */
	@Before
	public void setUp() {
		entityLocator = context.mock(EntityLocator.class);
		daoAdapterFactory = new DaoAdapterFactory();
		transactionJobBuilderImpl = new TransactionJobBuilderImpl();
		transactionJobBuilderImpl.setEntityLocator(entityLocator);
		transactionJobBuilderImpl.setDaoAdapterFactory(daoAdapterFactory);
	}

	/**
	 * Tests retrieveObject() method.
	 */
	@Test
	public void testRetrieveObject() {

		final TransactionJobDescriptorEntry descriptorEntry = transactionJobBuilderImpl.createJobDescriptorEntry(Product.class, "dd", Command.REMOVE);

		final Product product = context.mock(Product.class);
		context.checking(new Expectations() {
			{
				try {
					oneOf(entityLocator).locatePersistence(descriptorEntry.getGuid(), descriptorEntry.getType());
					will(returnValue(product));
				} catch (Exception e) {
				    Assert.fail("Exception should not be thrown: " + e.getMessage());
				}
			}
		});

		try {
			Persistable result = transactionJobBuilderImpl.getObject(descriptorEntry);
			Assert.assertSame("The job builder object should be the expected product", product, result);
		} catch (SyncToolConfigurationException e) {
			Assert.fail("Exception should not be thrown: " + e.getMessage());
		}
	}

	/**
	 * Test that associated job entries get created when required.
	 */
	@Test
	public void testAssociatedJobEntries() {
		@SuppressWarnings("unchecked")
		final DaoAdapter<? super Persistable> ruleAdaptor = context.mock(DaoAdapter.class, "ruleAdaptor");

		@SuppressWarnings("unchecked")
		final AssociatedDaoAdapter<? super Persistable> couponConfigAdapter = context.mock(AssociatedDaoAdapter.class, "couponConfigAdaptor");

		@SuppressWarnings("unchecked")
		final AssociatedDaoAdapter<? super Persistable> couponAdapter = context.mock(AssociatedDaoAdapter.class, "couponAdaptor");

		final Map<Class< ? >, DaoAdapter< ? super Persistable>> syncAdapters = new HashMap<Class< ? >, DaoAdapter< ? super Persistable>>();
		syncAdapters.put(Rule.class, ruleAdaptor);
		syncAdapters.put(CouponConfig.class, couponConfigAdapter);
		syncAdapters.put(Coupon.class, couponAdapter);
		daoAdapterFactory.setSyncAdapters(syncAdapters);

		final TransactionJobDescriptorEntry entry = transactionJobBuilderImpl.createJobDescriptorEntry(Rule.class, "testrule", Command.UPDATE);
		final List<TransactionJobDescriptorEntry> jobDescriptorEntries = new ArrayList<TransactionJobDescriptorEntry>();
		jobDescriptorEntries.add(entry);

		final TransactionJobDescriptor transactionJobDescriptor = new TransactionJobDescriptorImpl();
		transactionJobDescriptor.setName("test");
		transactionJobDescriptor.setJobDescriptorEntries(jobDescriptorEntries);

		final Collection<Class<?>> assocaitedTypes = new ArrayList<Class<?>>();
		assocaitedTypes.add(CouponConfig.class);
		assocaitedTypes.add(Coupon.class);

		final CouponConfig couponConfig = context.mock(CouponConfig.class);
		final List<String> couponConfigGuids = new ArrayList<String>();
		couponConfigGuids.add("CONFIG1");

		final Coupon coupon1 = context.mock(Coupon.class, "coupon1");
		final Coupon coupon2 = context.mock(Coupon.class, "coupon2");
		final List<String> couponCodes = new ArrayList<String>();
		couponCodes.add("coupon1");
		couponCodes.add("coupon2");

		final Rule rule = context.mock(Rule.class);
		context.checking(new Expectations() {
			{
				oneOf(entityLocator).locatePersistence(entry.getGuid(), entry.getType()); will(returnValue(rule));
				oneOf(entityLocator).locatePersistence("CONFIG1", CouponConfig.class); will(returnValue(couponConfig));
				oneOf(entityLocator).locatePersistence("coupon1", Coupon.class); will(returnValue(coupon1));
				oneOf(entityLocator).locatePersistence("coupon2", Coupon.class); will(returnValue(coupon2));

				oneOf(ruleAdaptor).getAssociatedTypes(); will(returnValue(assocaitedTypes));

				oneOf(couponConfigAdapter).getAssociatedGuids(entry.getType(), entry.getGuid()); will(returnValue(couponConfigGuids));
				oneOf(couponConfigAdapter).getType(); will(returnValue(CouponConfig.class));

				oneOf(couponAdapter).getAssociatedGuids(entry.getType(), entry.getGuid()); will(returnValue(couponCodes));
				allowing(couponAdapter).getType(); will(returnValue(Coupon.class));
			}
		});
		TransactionJobUnit jobUnit = transactionJobBuilderImpl.createTransactionJobUnit(transactionJobDescriptor, true);

		final Collection<JobEntry> entries = new ArrayList<JobEntry>(jobUnit.createJobEntries());

		final int expectedEntryCount = 4;

		assertFalse("There should be job entries", entries.isEmpty());
		assertEquals("There should be a total of 4 entries - rule + couponConfig + 2 Coupons", expectedEntryCount, entries.size());

		// unordered at this point
		assertEntriesHasExactly(entries, Rule.class, 1);
		assertEntriesHasExactly(entries, Coupon.class, 2);
		assertEntriesHasExactly(entries, CouponConfig.class, 1);
	}

	private static <T extends JobEntry> void assertEntriesHasExactly(final Collection<T> actual, final Class<?> clazz, final int number) {
		int matches = 0;
		for (JobEntry entry : actual) {
			if (ObjectUtils.equals(clazz, entry.getType())) {
				matches += 1;
			}
		}

		assertSame("Expected <" + number + "> JobEntry with <" + clazz + ">, got: " + actual, number, matches);
	}
}
