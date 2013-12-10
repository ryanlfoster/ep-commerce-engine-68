package com.elasticpath.search.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.domain.search.IndexNotification;
import com.elasticpath.domain.search.UpdateType;
import com.elasticpath.service.search.IndexNotificationService;
import com.elasticpath.service.search.IndexType;

/**
 * Test case for {@link IndexNotificationProcessorImpl}.
 */
public class IndexNotificationProcessorImplTest {

	private static final String NEVER_BE_EMPTY = "This should never be empty";

	private IndexNotificationProcessorImpl indexNotificationProcessorImpl;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private IndexNotificationService mockIndexNotificationService;

	private final IndexType indexType = IndexType.PRODUCT;

	private IndexNotification mockUpdateNotification;

	private IndexNotification mockRebuildNotification;

	private IndexNotification mockDeleteAllNotification;

	private IndexNotification mockDeleteNotification;

	@Before
	public void setUp() throws Exception {
		mockUpdateNotification = context.mock(IndexNotification.class, "update notification");
		mockRebuildNotification = context.mock(IndexNotification.class, "rebuild notification");
		mockDeleteAllNotification = context.mock(IndexNotification.class, "delete all notification");
		mockDeleteNotification = context.mock(IndexNotification.class, "delete notification");
		context.checking(new Expectations() {
			{
				allowing(mockUpdateNotification).getUpdateType();
				will(returnValue(UpdateType.UPDATE));
				allowing(mockRebuildNotification).getUpdateType();
				will(returnValue(UpdateType.REBUILD));
				allowing(mockDeleteAllNotification).getUpdateType();
				will(returnValue(UpdateType.DELETE_ALL));
				allowing(mockDeleteNotification).getUpdateType();
				will(returnValue(UpdateType.DELETE));
			}
		});

		indexNotificationProcessorImpl = new IndexNotificationProcessorImpl();
		mockIndexNotificationService = context.mock(IndexNotificationService.class);
		indexNotificationProcessorImpl.setIndexNotificationService(mockIndexNotificationService);
	}

	/**
	 * Test that collapsing of notifications happens correctly.
	 */
	@Test
	public void testRebuildOverridesUpdate() {

		context.checking(new Expectations() {
			{
				oneOf(mockIndexNotificationService).findByIndexType(indexType);
				will(returnValue(Arrays.asList(mockUpdateNotification, mockRebuildNotification)));
			}
		});
		assertEquals(indexNotificationProcessorImpl.findAllNewNotifications(indexType), Arrays.asList(mockRebuildNotification));
	}

	@Test
	public void testDeleteAllOveridesUpdate() {
		context.checking(new Expectations() {
			{
				oneOf(mockIndexNotificationService).findByIndexType(indexType);
				will(returnValue(Arrays.asList(mockUpdateNotification, mockDeleteAllNotification)));
			}
		});
		assertEquals(indexNotificationProcessorImpl.findAllNewNotifications(indexType), Arrays.asList(mockDeleteAllNotification));
	}

	@Test
	public void testDeleteAllOverridesRebuildBecauseItWasRegisteredLater() {
		context.checking(new Expectations() {
			{
				oneOf(mockIndexNotificationService).findByIndexType(indexType);
				will(returnValue(Arrays.asList(mockRebuildNotification, mockDeleteAllNotification)));
			}
		});
		assertEquals(indexNotificationProcessorImpl.findAllNewNotifications(indexType), Arrays.asList(mockDeleteAllNotification));
	}

	@Test
	public void testRebuildOverridesDeleteAllBecauseItWasRegisteredLater() {
		context.checking(new Expectations() {
			{
				oneOf(mockIndexNotificationService).findByIndexType(indexType);
				will(returnValue(Arrays.asList(mockDeleteAllNotification, mockRebuildNotification)));
			}
		});
		assertEquals(indexNotificationProcessorImpl.findAllNewNotifications(indexType), Arrays.asList(mockRebuildNotification));
	}

	@Test
	public void testNoOverridesHere() {
		context.checking(new Expectations() {
			{
				oneOf(mockIndexNotificationService).findByIndexType(indexType);
				will(returnValue(Arrays.asList(mockDeleteNotification, mockUpdateNotification)));
			}
		});
		assertEquals(indexNotificationProcessorImpl.findAllNewNotifications(indexType),
				Arrays.asList(mockDeleteNotification, mockUpdateNotification));
	}

	@Test
	public void testREbuildOverrideBothDeleteAndUpdate() {
		context.checking(new Expectations() {
			{
				oneOf(mockIndexNotificationService).findByIndexType(indexType);
				will(returnValue(Arrays.asList(mockDeleteNotification, mockUpdateNotification, mockRebuildNotification)));
			}
		});
		assertEquals(indexNotificationProcessorImpl.findAllNewNotifications(indexType), Arrays.asList(mockRebuildNotification));
	}

	@Test
	public void testRebuildOverridesDeleteButNotUpdateAsItWasRegisteredAfter() {
		context.checking(new Expectations() {
			{
				oneOf(mockIndexNotificationService).findByIndexType(indexType);
				will(returnValue(Arrays.asList(mockDeleteNotification, mockRebuildNotification, mockUpdateNotification)));
			}
		});
		assertEquals(indexNotificationProcessorImpl.findAllNewNotifications(indexType),
				Arrays.asList(mockRebuildNotification, mockUpdateNotification));
	}

	/**
	 * Test method for {@link IndexNotificationProcessorImpl#findAllNewNotifications(IndexType)}
	 * with a {@code null}.
	 */
	@Test(expected = EpSystemException.class)
	public void testFindAllNewNotificationsWithNull() {
		indexNotificationProcessorImpl.findAllNewNotifications(null);
	}

	/**
	 * Test method for {@link IndexNotificationProcessorImpl#findAllNewNotifications(IndexType)}.
	 */
	@Test
	public void testFindAllNewNotifications() {
		context.checking(new Expectations() {
			{
				oneOf(mockIndexNotificationService).findByIndexType(indexType);
				will(returnValue(Arrays.asList(mockUpdateNotification, mockRebuildNotification)));
			}
		});

		assertEquals(indexNotificationProcessorImpl.findAllNewNotifications(indexType), Arrays.asList(mockRebuildNotification));
	}

	/**
	 * Test method for {@link IndexNotificationProcessorImpl#getNotifications()} when
	 * {@link IndexNotificationProcessorImpl#findAllNewNotifications(IndexType)} is called.
	 */
	@Test
	public void testGetNotificationsWithFindAllNewNotifications() {
		// first this should be empty
		assertNotNull(NEVER_BE_EMPTY, indexNotificationProcessorImpl.getNotifications());
		assertTrue(indexNotificationProcessorImpl.getNotifications().isEmpty());

		context.checking(new Expectations() {
			{
				oneOf(mockIndexNotificationService).findByIndexType(indexType);
				will(returnValue(Arrays.asList(mockUpdateNotification, mockRebuildNotification)));
			}
		});

		// now after we run through, this should different
		final List<IndexNotification> notificationList = Arrays.asList(mockRebuildNotification);
		assertEquals(indexNotificationProcessorImpl.findAllNewNotifications(indexType), notificationList);
		assertEquals(indexNotificationProcessorImpl.getNotifications(), notificationList);
	}

	/**
	 * Test method for {@link IndexNotificationProcessorImpl#getNotifications()} when
	 * {@link IndexNotificationProcessorImpl#findAllNewRawNotifications(IndexType)} is called.
	 */
	@Test
	public void testGetNotificationsWithFindAllNewRawNotifications() {
		// first this should be empty
		assertNotNull(NEVER_BE_EMPTY, indexNotificationProcessorImpl.getNotifications());
		assertTrue(indexNotificationProcessorImpl.getNotifications().isEmpty());

		final List<IndexNotification> rawList = Arrays.asList(mockUpdateNotification, mockRebuildNotification);
		final List<IndexNotification> actualList = Arrays.asList(mockRebuildNotification);

		context.checking(new Expectations() {
			{
				oneOf(mockIndexNotificationService).findByIndexType(indexType);
				will(returnValue(Arrays.asList(mockUpdateNotification, mockRebuildNotification)));
			}
		});

		// now after we run through, this should different
		assertEquals(indexNotificationProcessorImpl.findAllNewRawNotifications(indexType), rawList);
		assertEquals(indexNotificationProcessorImpl.getNotifications(), actualList);
	}

	/**
	 * Test method for {@link IndexNotificationProcessorImpl#findAllNewRawNotifications(IndexType)}
	 * with a {@code null}.
	 */
	@Test(expected = EpSystemException.class)
	public void testFindAllNewRawNotificationsWithNull() {
		indexNotificationProcessorImpl.findAllNewRawNotifications(null);
	}

	/**
	 * Test method for {@link IndexNotificationProcessorImpl#findAllNewRawNotifications(IndexType)}.
	 */
	@Test
	public void testFindAllNewRawNotifications() {
		final List<IndexNotification> notList = Arrays.asList(mockUpdateNotification, mockRebuildNotification);
		context.checking(new Expectations() {
			{
				oneOf(mockIndexNotificationService).findByIndexType(indexType);
				will(returnValue(notList));
			}
		});
		assertEquals(indexNotificationProcessorImpl.findAllNewRawNotifications(indexType), notList);
	}

	/**
	 * Test method for {@link IndexNotificationProcessorImpl#getRawNotifications()} when
	 * {@link IndexNotificationProcessorImpl#findAllNewRawNotifications(IndexType)} is called.
	 */
	@Test
	public void testGetRawNotificationsWithFindAllNewRawNotifications() {
		// first this should be empty
		assertNotNull(NEVER_BE_EMPTY, indexNotificationProcessorImpl.getRawNotifications());
		assertTrue(indexNotificationProcessorImpl.getRawNotifications().isEmpty());

		final List<IndexNotification> notList = Arrays.asList(mockUpdateNotification, mockRebuildNotification);
		context.checking(new Expectations() {
			{
				oneOf(mockIndexNotificationService).findByIndexType(indexType);
				will(returnValue(notList));
			}
		});

		// now after we run through, this should different
		assertEquals(indexNotificationProcessorImpl.findAllNewRawNotifications(indexType), notList);
		assertEquals(indexNotificationProcessorImpl.getRawNotifications(), notList);
	}

	/**
	 * Test method for {@link IndexNotificationProcessorImpl#getRawNotifications()} when
	 * {@link IndexNotificationProcessorImpl#findAllNewNotifications(IndexType)} is called.
	 */
	@Test
	public void testGetRawNotificationsWithFindAllNewNotifications() {
		// first this should be empty
		assertNotNull(NEVER_BE_EMPTY, indexNotificationProcessorImpl.getRawNotifications());
		assertTrue(indexNotificationProcessorImpl.getRawNotifications().isEmpty());

		final List<IndexNotification> rawList = Arrays.asList(mockUpdateNotification, mockRebuildNotification);
		final List<IndexNotification> actualList = Arrays.asList(mockRebuildNotification);

		context.checking(new Expectations() {
			{
				oneOf(mockIndexNotificationService).findByIndexType(indexType);
				will(returnValue(Arrays.asList(mockUpdateNotification, mockRebuildNotification)));
			}
		});

		// now after we run through, this should different
		assertEquals(indexNotificationProcessorImpl.findAllNewNotifications(indexType), actualList);
		assertEquals(indexNotificationProcessorImpl.getRawNotifications(), rawList);
	}
}
