package com.elasticpath.service.asset.impl;

import java.util.Date;
import java.util.List;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.DigitalAsset;
import com.elasticpath.domain.catalog.DigitalAssetAudit;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.asset.DigitalAssetAuditService;
import com.elasticpath.service.impl.AbstractEpPersistenceServiceImpl;

/**
 * <code>DigitalAssetServiceImpl</code>.
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
public class DigitalAssetAuditServiceImpl extends AbstractEpPersistenceServiceImpl implements DigitalAssetAuditService {

	private static final long HOURS = 24L;
	private static final long MINUTES = 60L;
	private static final long SECONDS = 60L;
	private static final long MILLISECONDS = 1000L;
	private static final int VALID_REQUEST = 0;
	private static final int ORDERSKU_NOT_EXIST = 1;
	private static final int DIGITALASSET_NOT_EXIST = 2;
	private static final int EXPIRED = 3;
	private static final int EXCEED_MAX_DOWNLOAD_TIMES = 4;
	private static final int SKU_ASSET_NOT_MATCH = 5;
	private static final int UNAUTHORIZED_CUSTOMER = 6;
	private static final int NO_EXPIRY_LIMIT = 0;
	private static final int NO_MAX_DOWNLOAD_LIMIT = 0;

	/**
	 * Adds the given digitalAssetAudit.
	 *
	 * @param digitalAssetAudit the digitalAssetAudit to add
	 * @return the persisted instance of digitalAssetAudit
	 * @throws EpServiceException - if digitalAssetAudit with the specified name already exists
	 */
	public DigitalAssetAudit add(final DigitalAssetAudit digitalAssetAudit) throws EpServiceException {
		sanityCheck();
		getPersistenceEngine().save(digitalAssetAudit);
		return digitalAssetAudit;
	}

	/**
	 * Updates the given digitalAssetAudit.
	 *
	 * @param digitalAssetAudit the digitalAssetAudit to update
	 * @return the updated instance of digitalAssetAudit
	 * @throws EpServiceException - if digitalAssetAudit with the specified name already exists
	 */
	public DigitalAssetAudit update(final DigitalAssetAudit digitalAssetAudit) throws EpServiceException {
		sanityCheck();
		return getPersistenceEngine().update(digitalAssetAudit);
	}

	/**
	 * Delete the digitalAssetAudit.
	 *
	 * @param digitalAssetAudit the digitalAssetAudit to remove
	 *
	 * @throws EpServiceException - in case of any errors
	 */
	public void remove(final DigitalAssetAudit digitalAssetAudit) throws EpServiceException {
		sanityCheck();
		getPersistenceEngine().delete(digitalAssetAudit);
	}


	/**
	 * Load the digitalAssetAudit with the given UID.
	 * Throw an unrecoverable exception if there is no matching database row.
	 *
	 * @param digitalAssetAuditUid the digitalAssetAudit UID
	 *
	 * @return the digitalAssetAudit if UID exists, otherwise null
	 *
	 * @throws EpServiceException - in case of any errors
	 */
	public DigitalAssetAudit load(final long digitalAssetAuditUid) throws EpServiceException {
		sanityCheck();
		DigitalAssetAudit digitalAssetAudit = null;
		if (digitalAssetAuditUid <= 0) {
			digitalAssetAudit = getBean(ContextIdNames.DIGITAL_ASSET_AUDIT);
		} else {
			digitalAssetAudit = getPersistentBeanFinder().load(ContextIdNames.DIGITAL_ASSET_AUDIT, digitalAssetAuditUid);
		}
		return digitalAssetAudit;
	}
	
	/**
	 * Get the digitalAssetAudit with the given UID.
	 * Return null if no matching record exists.
	 *
	 * @param digitalAssetAuditUid the digitalAssetAudit UID
	 *
	 * @return the digitalAssetAudit if UID exists, otherwise null
	 *
	 * @throws EpServiceException - in case of any errors
	 */
	public DigitalAssetAudit get(final long digitalAssetAuditUid) throws EpServiceException {
		sanityCheck();
		DigitalAssetAudit digitalAssetAudit = null;
		if (digitalAssetAuditUid <= 0) {
			digitalAssetAudit = getBean(ContextIdNames.DIGITAL_ASSET_AUDIT);
		} else {			
			digitalAssetAudit = getPersistentBeanFinder().get(ContextIdNames.DIGITAL_ASSET_AUDIT, digitalAssetAuditUid);
		}
		return digitalAssetAudit;
	}

	/**
	 * Generic load method for all persistable domain models.
	 *
	 * @param uid
	 *            the persisted instance uid
	 * @return the persisted instance if exists, otherwise null
	 * @throws EpServiceException -
	 *             in case of any errors
	 */
	public Object getObject(final long uid) throws EpServiceException {
		return get(uid);
	}

	/**
	 * Lists all digitalAssets stored in the database.
	 *
	 * @return a list of digitalAssets
	 * @throws EpServiceException -
	 *             in case of any errors
	 */
	public List<DigitalAssetAudit> list() throws EpServiceException {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("DIGITAL_ASSET_AUDIT_SELECT_ALL");
	}

	/**
	 * Check if the asset uid and ordersku uid from download request is a valid download request.
	 *
	 * @param digitalAssetUid the digital asset uid
	 * @param orderSkuUid the order Sku uid
	 * @param customerUid the customer uid
	 * @return the error code if fail in validation, otherwise return 0.
	 *
	 */
	@SuppressWarnings("PMD.CyclomaticComplexity")
	public int isValidDADownloadRequest(final long digitalAssetUid, final long orderSkuUid, final long customerUid) {

		//Get orderSku
		OrderSku orderSku = getOrderSku(orderSkuUid);
		if (orderSku == null) {
			return ORDERSKU_NOT_EXIST;
		}
		
		if (orderSku.getShipment().getOrder().getCustomer().getUidPk() != customerUid) {
			return UNAUTHORIZED_CUSTOMER;
		}

		//Get digital Asset throught orderSku --> digital asset, they should have same product sku id
		DigitalAsset digitalAsset = null;
		digitalAsset = getDigitalAsset(orderSkuUid);
		if (digitalAsset == null) {
			return DIGITALASSET_NOT_EXIST;
		}

		//Check if the digitalAssetUid match the digital asset uidPk which attain from order sku
		if (digitalAssetUid != digitalAsset.getUidPk()) {
			return SKU_ASSET_NOT_MATCH;
		}

		//Check the expiry days
		int expiryDays = digitalAsset.getExpiryDays();
		if (expiryDays != NO_EXPIRY_LIMIT) {
			long expiryDaysInMillis = expiryDays * HOURS * MINUTES * SECONDS * MILLISECONDS;
			Date orderSkuCreateDate = orderSku.getCreatedDate();
			long expiryDateInMillis = orderSkuCreateDate.getTime() + expiryDaysInMillis;
			Date expiryDate = new Date(expiryDateInMillis);
			Date currentDate = new Date();
			if (currentDate.equals(expiryDate) || currentDate.after(expiryDate)) {
				return EXPIRED;
			}
		}

		// Check if exceed the maximum download times
		int maxDownloadTimes = digitalAsset.getMaxDownloadTimes();
		if (maxDownloadTimes != NO_MAX_DOWNLOAD_LIMIT) {
			List<DigitalAssetAudit> digitalAssetAuditList = this.findDAAuditListByDAOrderSku(
					orderSkuUid, digitalAssetUid);
			if ((digitalAssetAuditList != null)	&& digitalAssetAuditList.size() >= maxDownloadTimes) {
				return EXCEED_MAX_DOWNLOAD_TIMES;
			}
		}

		return VALID_REQUEST;
	}

	/**
	 * Add an audit record to for digital asset download.
	 *
	 * @param ipAddress the IP address
	 * @param digitalAssetUid the digital asset uid
	 * @param orderSkuUid the order sku id
	 * @return the added <code>DigitalAssetAudit</code>
	 *
	 */
	public DigitalAssetAudit addDigitalAssetAudit(final String ipAddress, final long digitalAssetUid, final long orderSkuUid) {

		//Create the download record
		DigitalAssetAudit digitalAssetAudit = getBean(ContextIdNames.DIGITAL_ASSET_AUDIT);
		
		digitalAssetAudit.setDownloadTime(new Date());
		digitalAssetAudit.setIpAddress(ipAddress);

		OrderSku orderSku = findOrderSkuByID(orderSkuUid).get(0);
		digitalAssetAudit.setOrderSku(orderSku);

		DigitalAsset digitalAsset = getDigitalAsset(orderSkuUid);
		digitalAssetAudit.setDigitalAsset(digitalAsset);

		return add(digitalAssetAudit);

	}

	/**
	 * Retrieves list of <code>OrderSku</code> found by the orderSku Uid. It should contain only one OrderSku.
	 *
	 * @param orderSkuUid the orderSku Uid
	 * @return list of <code>OrderSku</code> found by the orderSku Uid
	 */
	protected List<OrderSku> findOrderSkuByID(final long orderSkuUid) {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("ORDER_SKU_SELECT_BY_ID", new Long(orderSkuUid));
	}

	/**
	 * Retrieves list of <code>DigitalAssetAudit</code> found by the OrderSku Uid and DigitalAsset Uid.
	 *
	 * @param orderSkuUid the orderSku Uid
	 * @param digitalAssetUid the digitalAsset Uid
	 * @return list of <code>DigitalAssetAudit</code> searched by OrderSku Uid and DigitalAsset Uid
	 */
	protected List<DigitalAssetAudit> findDAAuditListByDAOrderSku(final long orderSkuUid, final long digitalAssetUid) {
		sanityCheck();

		return getPersistenceEngine().retrieveByNamedQuery("DA_AUDIT_BY_DAID_ORDERSKUID", new Long(orderSkuUid), new Long(digitalAssetUid));
	}

	/**
	 * Retrieves <code>DigitalAsset</code>.
	 *
	 * @param orderSkuUid the orderSku Uid
	 * @return <code>DigitalAsset</code>
	 */
	public DigitalAsset getDigitalAsset(final long orderSkuUid) {

		return getOrderSku(orderSkuUid).getDigitalAsset();

	}

	/**
	 * Retrieves <code>OrderSku</code> found by the orderSku Uid.
	 *
	 * @param orderSkuUid the orderSku Uid
	 * @return <code>OrderSku</code> found by the orderSku Uid
	 */
	public OrderSku getOrderSku(final long orderSkuUid) {

		OrderSku orderSku = null;

		List<OrderSku> orderSkuList = this.findOrderSkuByID(orderSkuUid);
		if ((orderSkuList != null) && orderSkuList.size() == 1) {
			orderSku = orderSkuList.get(0);
		}
		return orderSku;
	}



	/**
	 * Retrieves order number found by the orderSku Uid.
	 *
	 * @param orderSkuUid the orderSku Uid
	 * @return order number found by the orderSku Uid
	 */
	public String findOrderNumberByOrderSkuID(final long orderSkuUid) {
		sanityCheck();
		List<String> orderNumberList = getPersistenceEngine().retrieveByNamedQuery("ORDER_NUM_SELECT_BY_ORDERSKU_ID", new Long(orderSkuUid));
		return orderNumberList.get(0);
	}

}
