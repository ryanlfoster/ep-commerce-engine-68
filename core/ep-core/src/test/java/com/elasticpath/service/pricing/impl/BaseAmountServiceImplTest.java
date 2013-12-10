package com.elasticpath.service.pricing.impl;

import java.math.BigDecimal;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.pricing.BaseAmount;
import com.elasticpath.domain.pricing.impl.BaseAmountImpl;
import com.elasticpath.domain.search.IndexNotification;
import com.elasticpath.service.pricing.dao.BaseAmountDao;
import com.elasticpath.service.search.IndexNotificationService;

/** */
public class BaseAmountServiceImplTest {
	private BaseAmountServiceImpl baService;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	/**	 */
	@Test
	public void testAddAndNotified() {
		baService = new BaseAmountServiceImpl() {
			@Override
			public boolean exists(final BaseAmount baseAmount) {
				return false;
			}
		};
		
		final BaseAmount amount = new BaseAmountImpl("guid", "OBJ_GUID", "SKU", new BigDecimal(12), new BigDecimal(13), new BigDecimal(14),
				"desp_guid");

		final BaseAmountDao baDao = context.mock(BaseAmountDao.class);
		baService.setBaseAmountDao(baDao);

		final IndexNotification indexNotificaiton = context.mock(IndexNotification.class);
		PriceUpdatedNotificationServiceImpl priceNotificationService = new PriceUpdatedNotificationServiceImpl() {
			@Override
			protected IndexNotification createUpdateNotification(final long productUidPk) {
				return indexNotificaiton;
			}

			@Override
			protected boolean isPriceListAssignedToCatalog(final String priceListDescriptorGuid) {
				return true;
			}
			
			@Override
			protected long getProductUidPk(final String objectType, final String objectGuid) {
				return 2;
			}
		};
		final IndexNotificationService indexNotificationService = context.mock(IndexNotificationService.class);
		priceNotificationService.setIndexNotificationService(indexNotificationService);

		baService.setPriceUpdatedNotificationService(priceNotificationService);

		context.checking(new Expectations() {
			{
				oneOf(baDao).add(amount);
				oneOf(indexNotificationService).add(indexNotificaiton);
			}
		});

		baService.add(amount);
	}

	/**	 */
	@Test
	public void testDeleteAndNotified() {
		baService = new BaseAmountServiceImpl();
		final BaseAmount amount = new BaseAmountImpl("guid", "OBJ_GUID", "SKU", new BigDecimal(12), new BigDecimal(13), new BigDecimal(14),
				"desp_guid");

		final BaseAmountDao baDao = context.mock(BaseAmountDao.class);
		baService.setBaseAmountDao(baDao);

		final IndexNotification indexNotificaiton = context.mock(IndexNotification.class);
		PriceUpdatedNotificationServiceImpl priceNotificationService = new PriceUpdatedNotificationServiceImpl() {
			@Override
			protected IndexNotification createUpdateNotification(final long productUidPk) {
				return indexNotificaiton;
			}

			@Override
			protected boolean isPriceListAssignedToCatalog(final String priceListDescriptorGuid) {
				return true;
			}
			
			@Override
			protected long getProductUidPk(final String objectType, final String objectGuid) {
				return 2;
			}
		};
		final IndexNotificationService indexNotificationService = context.mock(IndexNotificationService.class);
		priceNotificationService.setIndexNotificationService(indexNotificationService);

		baService.setPriceUpdatedNotificationService(priceNotificationService);

		context.checking(new Expectations() {
			{
				oneOf(baDao).delete(amount);
				oneOf(indexNotificationService).add(indexNotificaiton);
			}
		});

		baService.delete(amount);
	}
}
