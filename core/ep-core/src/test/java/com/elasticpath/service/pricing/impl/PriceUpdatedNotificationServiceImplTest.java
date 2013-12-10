package com.elasticpath.service.pricing.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.Currency;

import org.junit.Assert;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.pricing.BaseAmount;
import com.elasticpath.domain.pricing.BaseAmountObjectType;
import com.elasticpath.domain.pricing.PriceListAssignment;
import com.elasticpath.domain.pricing.PriceListDescriptor;
import com.elasticpath.domain.search.IndexNotification;
import com.elasticpath.domain.search.IndexNotification.AffectedEntityType;
import com.elasticpath.domain.search.UpdateType;
import com.elasticpath.domain.search.impl.IndexNotificationImpl;
import com.elasticpath.service.catalog.ProductService;
import com.elasticpath.service.pricing.dao.PriceListAssignmentDao;
import com.elasticpath.service.search.IndexNotificationService;
import com.elasticpath.service.search.IndexType;
import com.elasticpath.service.search.query.ProductSearchCriteria;

/** */
public class PriceUpdatedNotificationServiceImplTest {
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private PriceUpdatedNotificationServiceImpl notificationService;

	/** */
	@Before
	public void setUp() {
		notificationService = new PriceUpdatedNotificationServiceImpl() {
			@Override
			protected IndexNotification createEmptyNotification() {
				return new IndexNotificationImpl();
			}

			@Override
			protected ProductSearchCriteria createEmptyProductSearchCriteria() {
				return new ProductSearchCriteria();
			}
		};
	}

	/**	 */
	@Test
	public void testCreateUpdateNotification() {
		final Long productUidPk = 11L;
		final IndexNotification notification = notificationService.createUpdateNotification(productUidPk);

		Assert.assertEquals(productUidPk, notification.getAffectedUid());
		Assert.assertEquals(AffectedEntityType.SINGLE_UNIT, notification.getAffectedEntityType());
		Assert.assertEquals(IndexType.PRODUCT, notification.getIndexType());
		Assert.assertEquals(UpdateType.UPDATE, notification.getUpdateType());
	}

	/**	 */
	@Test
	public void testGetProductUidPkFromProduct() {
		final BaseAmountObjectType productType = BaseAmountObjectType.PRODUCT;

		final BaseAmount baseAmount = context.mock(BaseAmount.class);
		final String objectGuid = "ObjectGuid";

		final ProductService productService = context.mock(ProductService.class);
		notificationService.setProductService(productService);

		context.checking(new Expectations() {
			{
				oneOf(baseAmount).getObjectType();
				will(returnValue(productType.getName()));

				oneOf(baseAmount).getObjectGuid();
				will(returnValue(objectGuid));

				oneOf(productService).findUidById(objectGuid);
				will(returnValue(2L));
			}
		});

		notificationService.getProductUidPk(baseAmount.getObjectType(), baseAmount.getObjectGuid());
	}

	/**	 */
	@Test
	public void testGetProductUidPkFromProductSkuType() {
		final BaseAmountObjectType productType = BaseAmountObjectType.SKU;

		final BaseAmount baseAmount = context.mock(BaseAmount.class);
		final String objectGuid = "ObjectGuid";

		final ProductService productService = context.mock(ProductService.class);
		notificationService.setProductService(productService);

		context.checking(new Expectations() {
			{
				oneOf(baseAmount).getObjectType();
				will(returnValue(productType.getName()));

				oneOf(baseAmount).getObjectGuid();
				will(returnValue(objectGuid));

				oneOf(productService).findUidBySkuCode(objectGuid);
				will(returnValue(2L));
			}
		});

		notificationService.getProductUidPk(baseAmount.getObjectType(), baseAmount.getObjectGuid());
	}

	/** */
	@Test
	public void testPriceListIsAssignedToCatalog() {
		final String priceListGuid = "price_list_guid";
		final PriceListAssignment assignment = context.mock(PriceListAssignment.class);
		final PriceListAssignmentDao assignmentService = context.mock(PriceListAssignmentDao.class);
		notificationService.setPriceListAssignmentDao(assignmentService);

		context.checking(new Expectations() {
			{
				oneOf(assignmentService).listByPriceList(priceListGuid);
				will(returnValue(Arrays.asList(assignment)));
			}
		});

		final boolean isAssigned = notificationService.isPriceListAssignedToCatalog(priceListGuid);

		Assert.assertTrue(isAssigned);
	}

	/** */
	@Test
	public void testPriceListIsNotAssignedToCatalog() {
		final String priceListGuid = "price_list_guid";
		final PriceListAssignmentDao assignmentService = context.mock(PriceListAssignmentDao.class);
		notificationService.setPriceListAssignmentDao(assignmentService);

		context.checking(new Expectations() {
			{
				oneOf(assignmentService).listByPriceList(priceListGuid);
				will(returnValue(Collections.emptyList()));
			}
		});

		final boolean isAssigned = notificationService.isPriceListAssignedToCatalog(priceListGuid);

		Assert.assertFalse(isAssigned);
	}

	/**
	 *
	 */
	@Test
	public void testCreateProductSearchCriteria() {
		final String catalogCode = "catalog_code";
		final Currency currency = Currency.getInstance("CAD");

		final ProductSearchCriteria criteria = notificationService.createProductSearchCriteria(catalogCode, currency);

		Assert.assertEquals(catalogCode, criteria.getCatalogCode());
		Assert.assertEquals(currency, criteria.getCurrency());
	}

	/**
	 *
	 */
	@Test
	public void testNotifyPriceUpdatedByBaseAmount() {
		final BaseAmount baseAmount = context.mock(BaseAmount.class);

		final String plDescriptorGuid = "pl_des_guid";

		final IndexNotification notification = context.mock(IndexNotification.class);
		notificationService = new PriceUpdatedNotificationServiceImpl() {
			@Override
			protected boolean isPriceListAssignedToCatalog(final String priceListDescriptorGuid) {
				return true;
			}

			@Override
			protected long getProductUidPk(final String objectType, final String objectGuid) {
				return 2;
			}

			@Override
			protected IndexNotification createUpdateNotification(final long productUidPk) {
				return notification;
			}
		};

		final IndexNotificationService indexNotificationService = context.mock(IndexNotificationService.class);
		notificationService.setIndexNotificationService(indexNotificationService);

		context.checking(new Expectations() {
			{
				oneOf(baseAmount).getPriceListDescriptorGuid();
				will(returnValue(plDescriptorGuid));
				oneOf(baseAmount).getObjectType();
				will(returnValue(BaseAmountObjectType.PRODUCT.getName()));
				oneOf(baseAmount).getObjectGuid();
				will(returnValue("some_GUID"));

				oneOf(indexNotificationService).add(notification);
			}
		});

		notificationService.notifyPriceUpdated(baseAmount.getPriceListDescriptorGuid(), baseAmount.getObjectType(), baseAmount.getObjectGuid());
	}

	/**
	 *
	 */
	@Test
	public void tsetNotifyPriceChangedByPriceListAssignment() {
		final ProductSearchCriteria searchCriteria = new ProductSearchCriteria();
		notificationService = new PriceUpdatedNotificationServiceImpl() {
			@Override
			protected ProductSearchCriteria createProductSearchCriteria(final String catalogCode, final Currency currency) {
				return searchCriteria;
			}
		};

		final String catalogCode = "catalog_code";
		final Catalog catalog = context.mock(Catalog.class);

		final String currencyCode = "CAD";
		final PriceListDescriptor plDescriptor = context.mock(PriceListDescriptor.class);

		final IndexNotificationService indexNotificationService = context.mock(IndexNotificationService.class);
		notificationService.setIndexNotificationService(indexNotificationService);

		final PriceListAssignment assignment = context.mock(PriceListAssignment.class);

		context.checking(new Expectations() {
			{
				oneOf(assignment).getCatalog();
				will(returnValue(catalog));

				oneOf(catalog).getCode();
				will(returnValue(catalogCode));

				oneOf(assignment).getPriceListDescriptor();
				will(returnValue(plDescriptor));

				oneOf(plDescriptor).getCurrencyCode();
				will(returnValue(currencyCode));

				allowing(indexNotificationService).addViaQuery(UpdateType.UPDATE, searchCriteria, false);
			}
		});

		notificationService.notifyPriceUpdated(assignment);
	}
}
