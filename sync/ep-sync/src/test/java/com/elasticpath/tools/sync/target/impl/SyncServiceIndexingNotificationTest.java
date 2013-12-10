package com.elasticpath.tools.sync.target.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Test;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.search.IndexNotification;
import com.elasticpath.domain.search.UpdateType;
import com.elasticpath.domain.search.IndexNotification.AffectedEntityType;
import com.elasticpath.domain.search.impl.IndexNotificationImpl;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.service.search.IndexNotificationService;
import com.elasticpath.service.search.IndexType;
import com.elasticpath.service.search.impl.IndexNotificationServiceImpl;
import com.elasticpath.tools.sync.job.JobEntry;
import com.elasticpath.tools.sync.job.impl.JobEntryImpl;

/**
 * Tests to verify that the Index Notification hooks for SyncServiceImpl successfully
 * add the notifications.
 */
public class SyncServiceIndexingNotificationTest {
	@org.junit.Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	
	private final long uidOfObjectToNotifyIndexAbout = 123456L;  //NOPMD
	private final Persistable objectToIndex = createObjectToIndex(uidOfObjectToNotifyIndexAbout);
	
	
	private IndexNotification notificationAdded;
	private final IndexNotificationService capturingIndexNotificationService = new IndexNotificationServiceImpl() {
		public IndexNotification add(final IndexNotification notification) {
			notificationAdded = notification;
			return null;
		}
	};

	private final IndexNotificationService indexNotificationServiceFailsOnAdd = new IndexNotificationServiceImpl() {
		public IndexNotification add(final IndexNotification notification) {
			fail("Notification should never be sent for an object with no mapped index.");
			return null;
		}
	};	
	
	
	/**
	 * Test to make sure update notification is set when expected.
	 */
	@Test
	public void testProductUpdateNotifications() {
		getNotificationCallbackUnderTest(capturingIndexNotificationService).postUpdateJobEntryHook(createJobEntry(Product.class), objectToIndex);
		assertNotificationAsExpected(UpdateType.UPDATE, AffectedEntityType.SINGLE_UNIT, IndexType.PRODUCT, uidOfObjectToNotifyIndexAbout, null);	
	}

	/**
	 * Test to make sure promotion remove notification is added. 
	 */
	@Test
	public void testPromotionRemoveNotifications() {
		getNotificationCallbackUnderTest(capturingIndexNotificationService).postRemoveJobEntryHook(createJobEntry(Rule.class), objectToIndex);
		assertNotificationAsExpected(UpdateType.DELETE, AffectedEntityType.SINGLE_UNIT, IndexType.PROMOTION, uidOfObjectToNotifyIndexAbout, null);	
	}
	
	/**
	 * Test that no update notification is added when an object not mapped to an index is removed.
	 */
	@Test
	public void testNoUpdateNotificationAddedForObjectWithoutIndex() {
		getNotificationCallbackUnderTest(indexNotificationServiceFailsOnAdd).postUpdateJobEntryHook(createJobEntry(Object.class), objectToIndex);
	}		
	
	/**
	 * Test that no remove notification is added when an object not mapped to an index is removed.
	 */
	@Test
	public void testNoRemoveNotificationAddedForObjectWithoutIndex() {
		getNotificationCallbackUnderTest(indexNotificationServiceFailsOnAdd).postRemoveJobEntryHook(createJobEntry(Object.class), objectToIndex);
	}	
	
	
	private JobEntry createJobEntry(final Class< ? > type) {
		final JobEntry jobEntry = new JobEntryImpl();
		jobEntry.setType(type);
		return jobEntry;
	}
	
	private Persistable createObjectToIndex(final long uidPk) {
		final Persistable objectToIndex = context.mock(Persistable.class);
		context.checking(new Expectations() { {
			allowing(objectToIndex).getUidPk(); 
			will(returnValue(uidPk));
		} });
		return objectToIndex;
	}
	
	
	private void assertNotificationAsExpected(
			final UpdateType update, final String singleUnit, final IndexType product, final Long uidOfObjectToNotifyIndexAbout, final String query) {
		assertEquals("Wrong update type in notification", update, notificationAdded.getUpdateType());
		assertEquals("Wrong entity type in notification", singleUnit, notificationAdded.getAffectedEntityType());
		assertEquals("Wrong index type in notification", product, notificationAdded.getIndexType());
		assertEquals("Wrong object id in notification", uidOfObjectToNotifyIndexAbout, notificationAdded.getAffectedUid());
		assertEquals("Wrong query in notification", query, notificationAdded.getQueryString());
	}	
	
	/**
	 * Create a callback instance with the specific notification service and registered index types.
	 */
	private IndexNotificationJobTransactionCallback getNotificationCallbackUnderTest(final IndexNotificationService indexNotificationService) {
		IndexNotificationJobTransactionCallback indexNotificationCallback = new IndexNotificationJobTransactionCallback() {
			@Override
			protected Object getBean(final String beanID) {
				return new IndexNotificationImpl();
			}
		};
		
		Map<String, IndexType> indexNameMap = new HashMap<String, IndexType>();
		indexNameMap.put("com.elasticpath.domain.catalog.Product", IndexType.PRODUCT);
		indexNameMap.put("com.elasticpath.domain.rules.Rule", IndexType.PROMOTION);
		
		indexNotificationCallback.setIndexNameMap(indexNameMap);
		indexNotificationCallback.setIndexNotificationService(indexNotificationService);
		
		return indexNotificationCallback;
	}
}
