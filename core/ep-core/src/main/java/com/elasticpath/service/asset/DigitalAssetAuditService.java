package com.elasticpath.service.asset;

import java.util.List;

import com.elasticpath.domain.catalog.DigitalAsset;
import com.elasticpath.domain.catalog.DigitalAssetAudit;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.service.EpPersistenceService;
import com.elasticpath.base.exception.EpServiceException;

/**
 * Provide digitalAssetAudit-related business service.
 */
public interface DigitalAssetAuditService extends EpPersistenceService {
	/**
	 * Adds the given digitalAssetAudit.
	 *
	 * @param digitalAssetAudit the digitalAssetAudit to add
	 * @return the persisted instance of digitalAssetAudit
	 * @throws EpServiceException - if digitalAssetAudit with the specified name already exists
	 */
	DigitalAssetAudit add(final DigitalAssetAudit digitalAssetAudit) throws EpServiceException;

	/**
	 * Updates the given digitalAssetAudit.
	 *
	 * @param digitalAssetAudit the digitalAssetAudit to update
	 * @return the updated instance of digitalAssetAudit
	 * @throws EpServiceException - if digitalAssetAudit with the specified name already exists
	 */
	DigitalAssetAudit update(final DigitalAssetAudit digitalAssetAudit) throws EpServiceException;

	/**
	 * Delete the digitalAssetAudit.
	 *
	 * @param digitalAssetAudit the digitalAssetAudit to remove
	 *
	 * @throws EpServiceException - in case of any errors
	 */
	void remove(final DigitalAssetAudit digitalAssetAudit) throws EpServiceException;


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
	DigitalAssetAudit load(final long digitalAssetAuditUid) throws EpServiceException;

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
	DigitalAssetAudit get(final long digitalAssetAuditUid) throws EpServiceException;


	/**
	 * Lists all digitalAssetAudits stored in the database.
	 *
	 * @return a list of digitalAssetAudits
	 * @throws EpServiceException - in case of any errors
	 */
	List<DigitalAssetAudit> list() throws EpServiceException;

	/**
	 * Check if the asset uid and ordersku uid from download request is a valid download request.
	 *
	 * @param digitalAssetUid the digital asset uid
	 * @param orderSkuUid the order Sku uid
	 * @param customerUid the customer uid
	 * @return the error code if fail in validation, otherwise return 0.
	 *
	 */
	int isValidDADownloadRequest(long digitalAssetUid, long orderSkuUid, long customerUid);

	/**
	 * Add an audit record to for digital asset download.
	 *
	 * @param ipAddress the IP address
	 * @param digitalAssetUid the digital asset uid
	 * @param orderSkuUid the order sku id
	 * @return the added <code>DigitalAssetAudit</code>
	 *
	 */
	DigitalAssetAudit addDigitalAssetAudit(final String ipAddress, long digitalAssetUid, long orderSkuUid);

	/**
	 * Retrieves <code>DigitalAsset</code>.
	 *
	 * @param orderSkuUid the orderSku Uid
	 * @return <code>DigitalAsset</code>
	 */
	DigitalAsset getDigitalAsset(final long orderSkuUid);

	/**
	 * Retrieves <code>OrderSku</code> found by the orderSku Uid.
	 *
	 * @param orderSkuUid the orderSku Uid
	 * @return <code>OrderSku</code> found by the orderSku Uid
	 */
	OrderSku getOrderSku(final long orderSkuUid);

	/**
	 * Retrieves order number found by the orderSku Uid.
	 *
	 * @param orderSkuUid the orderSku Uid
	 * @return order number found by the orderSku Uid
	 */
	String findOrderNumberByOrderSkuID(final long orderSkuUid);



}