package com.elasticpath.service.pricing.impl;

import java.util.Currency;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.pricing.BaseAmountObjectType;
import com.elasticpath.domain.pricing.PriceListAssignment;
import com.elasticpath.domain.search.IndexNotification;
import com.elasticpath.domain.search.IndexNotification.AffectedEntityType;
import com.elasticpath.domain.search.UpdateType;
import com.elasticpath.service.catalog.ProductService;
import com.elasticpath.service.catalog.ProductSkuService;
import com.elasticpath.service.impl.AbstractEpServiceImpl;
import com.elasticpath.service.pricing.PriceUpdatedNotificationService;
import com.elasticpath.service.pricing.dao.PriceListAssignmentDao;
import com.elasticpath.service.search.IndexNotificationService;
import com.elasticpath.service.search.IndexType;
import com.elasticpath.service.search.query.ProductSearchCriteria;

/**
 * Default implementation of {@link PriceUpdatedNotificationService}.
 */
public class PriceUpdatedNotificationServiceImpl extends AbstractEpServiceImpl implements PriceUpdatedNotificationService {

	private static final Logger LOG = Logger.getLogger(PriceUpdatedNotificationServiceImpl.class);

	private IndexNotificationService indexNotificationService;

	private ProductService productService;

	private ProductSkuService productSkuService;

	private PriceListAssignmentDao priceListAssignmentDao;

	/**
	 * Creates an empty {@link IndexNotification}.
	 *
	 * @return {@link IndexNotification}.
	 */
	protected IndexNotification createEmptyNotification() {
		return getBean(ContextIdNames.INDEX_NOTIFICATION);
	}

	/**
	 * Creates an empty {@link ProductSearchCriteria}.
	 *
	 * @return {@link ProductSearchCriteria}.
	 */
	protected ProductSearchCriteria createEmptyProductSearchCriteria() {
		return getBean(ContextIdNames.PRODUCT_SEARCH_CRITERIA);
	}

	/**
	 * Creates a {@link ProductSearchCriteria} based on catalog code and {@link Currency}.
	 *
	 * @param catalogCode the catalog code.
	 * @param currency the {@link Currency}.
	 * @return {@link ProductSearchCriteria}.
	 */
	protected ProductSearchCriteria createProductSearchCriteria(final String catalogCode, final Currency currency) {
		final ProductSearchCriteria searchCriteria = createEmptyProductSearchCriteria();
		searchCriteria.setCurrency(currency);
		searchCriteria.setCatalogCode(catalogCode);
		// we just need a locale here, it isn't going to be used
		searchCriteria.setLocale(Locale.US);

		return searchCriteria;
	}

	/**
	 * Creates an update {@link IndexNotification} based on a product UidPk.
	 *
	 * @param productUidPk product UidPk.
	 * @return {@link IndexNotification}.
	 */
	protected IndexNotification createUpdateNotification(final long productUidPk) {
		final IndexNotification notification = createEmptyNotification();

		notification.setAffectedEntityType(AffectedEntityType.SINGLE_UNIT);
		notification.setAffectedUid(productUidPk);
		notification.setIndexType(IndexType.PRODUCT);
		notification.setUpdateType(UpdateType.UPDATE);

		return notification;
	}

	/**
	 * Gets the {@link IndexNotificationService}.
	 *
	 * @return the {@link IndexNotificationService}.
	 */
	public IndexNotificationService getIndexNotificationService() {
		return indexNotificationService;
	}

	/**
	 * Gets the {@link PriceListAssignmentDao}.
	 *
	 * @return the {@link PriceListAssignmentDao}.
	 */
	public PriceListAssignmentDao getPriceListAssignmentDao() {
		return priceListAssignmentDao;
	}

	/**
	 * Gets the {@link ProductService}.
	 *
	 * @return the {@link ProductService}.
	 */
	public ProductService getProductService() {
		return productService;
	}

	/**
	 * Gets the {@link ProductSkuService}.
	 *
	 * @return the {@link ProductSkuService}.
	 */
	public ProductSkuService getProductSkuService() {
		return productSkuService;
	}

	/**
	 * Gets the product UidPk based on Base Amount.
	 *
	 * @param objectType - the type of the object associated with the base amount
	 * @param objectGuid - the guid of the object associated with this baseAmount
	 * @return the product UidPk if there is any, returns 0 if
	 */
	protected long getProductUidPk(final String objectType, final String  objectGuid) {

	    if (BaseAmountObjectType.PRODUCT.getName().equals(objectType)) {
	        return getProductService().findUidById(objectGuid);
	    } else if (BaseAmountObjectType.SKU.getName().equals(objectType)) {
	        return getProductService().findUidBySkuCode(objectGuid);
	    }
	    return 0;
	}

	/**
	 * Check whether a price list is assigned to any catalog.
	 *
	 * @param priceListDescriptorGuid the price list descriptor guid.
	 * @return whether a price list is assigned to any catalog.
	 */
	protected boolean isPriceListAssignedToCatalog(final String priceListDescriptorGuid) {
		final List<PriceListAssignment> assignments = getPriceListAssignmentDao().listByPriceList(priceListDescriptorGuid);
		if (assignments.isEmpty()) {
			return false;
		}

		return true;
	}

	@Override
	public void notifyPriceUpdated(final String priceListDescriptorGuid, final String objectType, final String  objectGuid) {
		if (!isPriceListAssignedToCatalog(priceListDescriptorGuid)) {
			return;
		}

		final long productUidPk = getProductUidPk(objectType, objectGuid);
		if (productUidPk == 0) {
			LOG.error("No product was found for base amount with objectType: " + objectType + " and objectGuid:" + objectGuid);
			return;
		}

		final IndexNotification notification = createUpdateNotification(productUidPk);
		getIndexNotificationService().add(notification);
	}

	@Override
	public void notifyPriceUpdated(final PriceListAssignment assignment) {
		final String catalogCode = assignment.getCatalog().getCode();
		final Currency currency = Currency.getInstance(assignment.getPriceListDescriptor().getCurrencyCode());
		final ProductSearchCriteria searchCriteria = createProductSearchCriteria(catalogCode, currency);

		getIndexNotificationService().addViaQuery(UpdateType.UPDATE, searchCriteria, false);
	}

	/**
	 * Sets the {@link IndexNotificationService}.
	 *
	 * @param indexNotificationService {@link IndexNotificationService}.
	 */
	public void setIndexNotificationService(final IndexNotificationService indexNotificationService) {
		this.indexNotificationService = indexNotificationService;
	}

	/**
	 * Sets the {@link PriceListAssignmentDao}.
	 *
	 * @param priceListAssignmentDao {@link PriceListAssignmentDao}.
	 */
	public void setPriceListAssignmentDao(final PriceListAssignmentDao priceListAssignmentDao) {
		this.priceListAssignmentDao = priceListAssignmentDao;
	}

	/**
	 * Sets the {@link ProductService}.
	 *
	 * @param productService {@link ProductService}.
	 */
	public void setProductService(final ProductService productService) {
		this.productService = productService;
	}

	/**
	 * Sets the {@link ProductSkuService}.
	 *
	 * @param productSkuService {@link ProductSkuService}.
	 */
	public void setProductSkuService(final ProductSkuService productSkuService) {
		this.productSkuService = productSkuService;
	}
}
