package com.elasticpath.service.catalog.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Currency;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.util.Pair;
import com.elasticpath.commons.util.Utility;
import com.elasticpath.domain.catalog.GiftCertificate;
import com.elasticpath.domain.misc.Money;
import com.elasticpath.domain.misc.impl.MoneyFactory;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.payment.GiftCertificateTransaction;
import com.elasticpath.domain.store.Store;
import com.elasticpath.plugin.payment.PaymentType;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.catalog.GiftCertificateService;
import com.elasticpath.service.impl.AbstractEpPersistenceServiceImpl;
import com.elasticpath.service.payment.GiftCertificateTransactionService;

/**
 * Provides services related to Gift Certificates.
 * This implementation uses the configured Persistence Engine to perform most operations 
 */
public class GiftCertificateServiceImpl extends AbstractEpPersistenceServiceImpl implements GiftCertificateService {
	private static final String PLACEHOLDER_FOR_LIST = "list";

	private GiftCertificateTransactionService giftCertificateTransactionService;

	private Utility utility;

	/**
	 * Adds the given giftCertificate.
	 * 
	 * @param giftCertificate the Gift Certificate to add
	 * @return the persisted instance of Gift Certificate
	 * @throws EpServiceException in case of any errors
	 */
	public GiftCertificate add(final GiftCertificate giftCertificate) throws EpServiceException {
		sanityCheck();
		getPersistenceEngine().save(giftCertificate);
		return giftCertificate;
	}

	/**
	 * Load the giftCertificate with the given Uid.
	 * 
	 * @param giftCertificateUid the Gift Certificate Uid
	 * @return the giftCertificate if ID exists, otherwise null
	 * @throws EpServiceException in case of any errors
	 */
	public GiftCertificate load(final long giftCertificateUid) throws EpServiceException {
		sanityCheck();
		GiftCertificate giftCertificate = null;
		if (giftCertificateUid <= 0) {
			giftCertificate = getBean(ContextIdNames.GIFT_CERTIFICATE);
		} else {
			giftCertificate = getPersistentBeanFinder().load(ContextIdNames.GIFT_CERTIFICATE, giftCertificateUid);
		}
		return giftCertificate;
	}

	/**
	 * Get the Gift Certificate with the given UID. Return null if no matching record exists.
	 * 
	 * @param giftCertificateUid the Gift Certificate UID.
	 * @return the Gift Certificate if UID exists, otherwise null
	 * @throws EpServiceException in case of any errors
	 */
	public GiftCertificate get(final long giftCertificateUid) throws EpServiceException {
		return load(giftCertificateUid);
	}

	/**
	 * Generic get method for all persistable domain models.
	 * 
	 * @param uid the persisted instance uid
	 * @return the persisted instance if exists, otherwise null
	 * @throws EpServiceException in case of any errors
	 */
	public Object getObject(final long uid) throws EpServiceException {
		return load(uid);
	}

	/**
	 * Save or update the given giftCertificate.
	 * 
	 * @param giftCertificate the Gift Certificate to save or update
	 * @return the merged gift certificate
	 * @throws EpServiceException in case of any errors
	 */
	public GiftCertificate saveOrUpdate(final GiftCertificate giftCertificate) throws EpServiceException {
		return this.getPersistenceEngine().saveOrUpdate(giftCertificate);
	}

	/**
	 * Save or merge the given giftCertificate.
	 * 
	 * @param giftCertificate the Gift Certificate to merge
	 * @return giftCertificate
	 * @throws EpServiceException in case of any errors
	 */
	public GiftCertificate saveOrMerge(final GiftCertificate giftCertificate) throws EpServiceException {
		return saveOrUpdate(giftCertificate);
	}

	/**
	 * Load the Gift Certificate with the given UID.
	 * 
	 * @param giftCertificateUid the giftCertificate UID
	 * @return the Gift Certificate if UID exists, otherwise null
	 * @throws EpServiceException in case of any errors
	 */
	public GiftCertificate getByUid(final long giftCertificateUid) throws EpServiceException {
		return load(giftCertificateUid);
	}

	/**
	 * Deletes the Gift Certificate.
	 * 
	 * @param giftCertificateUid the Gift Certificate Uid to be removed
	 * @throws EpServiceException in case of any errors
	 */
	public void removeGiftCertificate(final long giftCertificateUid) throws EpServiceException {
		sanityCheck();

		GiftCertificate giftCertificate = getPersistentBeanFinder().get(ContextIdNames.GIFT_CERTIFICATE, giftCertificateUid);

		getPersistenceEngine().delete(giftCertificate);
	}

	/**
	 * List all gift certificates stored in the database.
	 * 
	 * @return a list of gift certificates
	 * @throws EpServiceException in case of any errors
	 */
	public List<GiftCertificate> list() throws EpServiceException {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("GIFT_CERTIFICATE_SELECT_ALL");
	}

	/**
	 * Returns a list of <code>GiftCertificate</code> based on the given uids.
	 * 
	 * @param giftCertificateUids a collection of giftCertificate uids
	 * @return a list of <code>GiftCertificate</code>s
	 */
	public List<GiftCertificate> findByUids(final Collection<Long> giftCertificateUids) {
		sanityCheck();

		if (giftCertificateUids == null || giftCertificateUids.isEmpty()) {
			return new ArrayList<GiftCertificate>(0);
		}
		final List<GiftCertificate> result = getPersistenceEngine().retrieveByNamedQueryWithList("GIFT_CERTIFICATE_BY" + "_UIDS",
				PLACEHOLDER_FOR_LIST, giftCertificateUids);
		return result;
	}

	/**
	 * Returns all gift certificate uids as a list.
	 * 
	 * @return all gift certficate uids as a list
	 */
	public List<Long> findAllUids() {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("GIFT_CERTIFICATE_UIDS_ALL");
	}

	/**
	 * Retrieves list of <code>GiftCertificate</code> uids where the last modified date is later than the specified date.
	 * 
	 * @param date date to compare with the last modified date
	 * @return list of <code>GiftCertificate</code> whose last modified date is later than the specified date
	 */
	public List<Long> findUidsByModifiedDate(final Date date) {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("GIFT_CERTIFICATE_UIDS_SELECT_BY_MODIFIED_DATE", date);
	}

	/**
	 * Retrieves list of <code>GiftCertificate</code> where the last modified date is later than the specified date.
	 * 
	 * @param date date to compare with the last modified date
	 * @return list of <code>GiftCertificate</code> whose last modified date is later than the specified date
	 */
	public List<GiftCertificate> findByModifiedDate(final Date date) {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("GIFT_CERTIFICATE_SELECT_BY_MODIFIED_DATE", date);
	}

	/**
	 * Retrieves <code>GiftCertificate</code> given the gift certificate code.
	 * 
	 * @param giftCertificateCode Gift Certificate Code to compare with
	 * @return GiftCertificate <code>GiftCertificate</code>
	 */
	public GiftCertificate findByGiftCertificateCode(final String giftCertificateCode) {

		if (giftCertificateCode == null) {
			throw new EpServiceException("Cannot retrieve null giftCertificateCode.");
		}
		
		String code = giftCertificateCode.trim();
		
		GiftCertificate giftCertificate = getSingleResultFromNamedQuery("GIFT_CERTIFICATE_FIND_BY_CODE", code);

		return giftCertificate;
	}

	/**
	 * Retrieves {@link GiftCertificate} given the gift certificate code. If that gift certificate is not within the given store, returns
	 * <code>null</code>.
	 * 
	 * @param giftCertificateCode Gift Certificate Code to compare with
	 * @param store the store to search
	 * @return a {@link GiftCertificate}
	 */
	public GiftCertificate findByGiftCertificateCode(final String giftCertificateCode, final Store store) {

		if (giftCertificateCode == null) {
			throw new IllegalArgumentException("Gift certificate code (" + giftCertificateCode + ") and store (" + store + ") can not be null");
		}
		
		String code = giftCertificateCode.trim();
		GiftCertificate giftCertificate = getSingleResultFromNamedQuery("GIFT_CERTIFICATE_FIND_BY_CODE_AND_STORE", code, store.getUidPk());
		
		return giftCertificate;
	}
	

	@Override
	public GiftCertificate findByGuid(final String guid) {

		if (guid == null) {
			throw new EpServiceException("Cannot retrieve null giftCertificate GUID.");
		}
		
		GiftCertificate giftCertificate = getSingleResultFromNamedQuery("GIFT_CERTIFICATE_FIND_BY_GUID", guid);
		
		return giftCertificate;
	}

	/**
	 * Gets a single {@link com.elasticpath.domain.catalog.GiftCertificate} from a named query.
	 * @param queryName the name of the named query.
	 * @param queryParameters the parameters for the query.
	 * @return the GiftCertificate found by the query, null otherwise.
	 */
	protected GiftCertificate getSingleResultFromNamedQuery(final String queryName, final Object ...queryParameters) {
		sanityCheck();
		final List<GiftCertificate> results = getPersistenceEngine().retrieveByNamedQuery(queryName, queryParameters);
		
		GiftCertificate giftCertificate = null;
		
		if (results.size() == 1) {
			giftCertificate = results.get(0);
		} else if (results.size() > 1) {
			String errorMessageText = "Inconsistent data. Duplicate gift certificate identifier(s) exist: ";
			String fullErrorMessage = assembleErrorMessageWithQueryParameters(errorMessageText, queryParameters); 
			throw new EpServiceException(fullErrorMessage);
		}
		
		return giftCertificate;
	}

	/**
	 * Assembles an error message from the message and the query parameters.
	 * @param messsage Error message test
	 * @param queryParameters the query parameters that generated the error.
	 * @return a String containing the final error message.
	 */
	protected String assembleErrorMessageWithQueryParameters(final String messsage, final Object ...queryParameters) {
		StringBuilder errorStringBuilder = new StringBuilder();
		errorStringBuilder.append("Inconsistent data. Duplicate gift certificate identifier(s) exist: ");

		for (Object queryParameter : queryParameters) {
			errorStringBuilder.append(queryParameter + " ");
		}

		return errorStringBuilder.toString();
	}
	
	/**
	 * return true if gift certificate code exist.
	 * 
	 * @param giftCertificateCode Gift Certificate Code to compare with
	 * @param storeUid the store UID
	 * @return true if gift certificate code exist
	 */
	public boolean isGiftCertificateCodeExist(final String giftCertificateCode, final long storeUid) {
		sanityCheck();
		if (giftCertificateCode == null) {
			throw new EpServiceException("Cannot retrieve null giftCertificateCode.");
		}
		final List<GiftCertificate> results = getPersistenceEngine().retrieveByNamedQuery("GIFT_CERTIFICATE_FIND_BY_CODE_AND_STORE",
				giftCertificateCode, storeUid);
		if (results.size() >= 1) {
			return true;
		}
		return false;
	}

	/**
	 * Obtains the balance of the gift certificate.
	 * 
	 * @param giftCertificate the gift certificate
	 * @return the balance amount
	 */
	public BigDecimal getBalance(final GiftCertificate giftCertificate) {
		return getGiftCertificateTransactionService().getBalance(giftCertificate);
	}

	/**
	 * @return the utility
	 */
	public Utility getUtility() {
		return utility;
	}

	/**
	 * @param utility the utility to set
	 */
	public void setUtility(final Utility utility) {
		this.utility = utility;
	}

	/**
	 * Get the gift certificate transaction service.
	 * 
	 * @return giftCertificateTransactionService
	 */
	public GiftCertificateTransactionService getGiftCertificateTransactionService() {
		return giftCertificateTransactionService;
	}

	/**
	 * Set the gift certificate transaction service.
	 * 
	 * @param giftCertificateTransactionService the giftCertificateTransactionService to set
	 */
	public void setGiftCertificateTransactionService(final GiftCertificateTransactionService giftCertificateTransactionService) {
		this.giftCertificateTransactionService = giftCertificateTransactionService;
	}
	
	/**
	 * 
	 * Get detail data for gift certificate summary report. 
	 * Report data can not be obtained in one query, because 
	 * Gift Certificate entity, not contain information about
	 * his transaction(s), hence GC transaction can not be left
	 * joined to GC.  
	 * 
	 * @param storeUidPks the stores UIDs
	 * @param startDate optional begin of report period
	 * @param endDate optional end  of report period
	 * @param currencyCode optional currency code
	 * @return list of reporting rows as array 
	 */
	public List<Object[]> getGiftCertificateSummaryData(
			final List<Long> storeUidPks, 
			final String currencyCode, 
			final Date startDate, 
			final Date endDate) {
		
		final Set<Pair<String, String>> storeNameCurrencySet = getStoreNameSupportedCurrencySet(storeUidPks, currencyCode);

		
		final List<Object[]> purchased = getPersistenceEngine().retrievePartByNamedQueryWithList(
				"GIFT_CERTIFICATE_SUMMARY_PURCHASED_REPORT", PLACEHOLDER_FOR_LIST,	storeUidPks,
				currencyCode, startDate, endDate);

		final List<Object[]> spend = getPersistenceEngine().retrievePartByNamedQueryWithList(
				"GIFT_CERTIFICATE_SUMMARY_SPEND_REPORT", PLACEHOLDER_FOR_LIST,	storeUidPks,
				currencyCode, startDate, endDate);
		
		final List<Object[]> result = new ArrayList<Object[]>(storeNameCurrencySet.size()); 
		
		for (Pair<String, String> storeCurrency : storeNameCurrencySet) {
			BigDecimal totalPurchased = getMoneyByStoreCurrency(storeCurrency, purchased);
			BigDecimal totalSpend = getMoneyByStoreCurrency(storeCurrency, spend);
			result.add(
					new Object [] {
							storeCurrency.getFirst(),
							storeCurrency.getSecond(),
							totalPurchased,
							totalPurchased.subtract(totalSpend) 
					});
		}
		Collections.sort(result, new Comparator<Object[]>() {
					public int compare(final Object[] dataRow1, final Object[] dataRow2) {						
						int result = ((String) dataRow1[0]).compareTo((String) dataRow2[0]); //store name  
						if (result == 0) {
							result = ((String) dataRow1[1]).compareTo((String) dataRow2[1]); //currency							
						}
						return result;
					} });
		return result;
	}

	private Set<Pair<String, String>> getStoreNameSupportedCurrencySet(final List<Long> storeUidPks,
			final String currencyCode) {
		final Set<Pair<String, String>> storeNameCurrencySet = new HashSet<Pair<String, String>>();
		
		final List<Store> stores = getPersistenceEngine().retrieveByNamedQueryWithList(
				"STORE_WITH_UIDS", 
				PLACEHOLDER_FOR_LIST, 
				storeUidPks);
		
		for (Store store : stores) {
			for (Currency currency : store.getSupportedCurrencies()) {
				if (currencyCode == null) {
					storeNameCurrencySet.add(new Pair<String, String>(store.getName(), currency.getCurrencyCode()));					
				} else {
					if (currency.getCurrencyCode().equalsIgnoreCase(currencyCode)) {
						storeNameCurrencySet.add(new Pair<String, String>(store.getName(), currency.getCurrencyCode()));						
					}					
				}
								
			}			
		}
		return storeNameCurrencySet;
	}
	
	private BigDecimal getMoneyByStoreCurrency(final Pair<String, String> storeCurrency, final List<Object[]> list) {
		for (Object[] obj : list) {
			Pair<String, String> candidate = new Pair<String, String>((String) obj[0], (String) obj[1]);
			if (storeCurrency.equals(candidate)) {
				return (BigDecimal) obj[2];			 
			}
		}
		return BigDecimal.ZERO;
		
	}
	
	
	/**
	 * 
	 * Get detail data for gift certificate detail report. 
	 * 
	 * @param storeUid the store UID
	 * @param startDate optional begin of report period
	 * @param endDate optional end  of report period
	 * @param currencies list of currency codes.
	 * @return list of reporting rows as array 
	 */
	public List<Object[]> getGiftCertificateDetailData(final long storeUid, final Date startDate, final Date endDate, final List<String> currencies) {

		final List<GiftCertificate> list = getPersistenceEngine().retrieveByNamedQueryWithList("GIFT_CERTIFICATE_DETAIL_REPORT",
				PLACEHOLDER_FOR_LIST,
				currencies,
				storeUid,
				startDate,
				endDate);

		final List<Object[]> result = new ArrayList<Object[]>();
		for (GiftCertificate giftCertificate : list) {
			String purchaserName = giftCertificate.getSenderName();
			if (giftCertificate.getPurchaser() != null) {
				purchaserName = giftCertificate.getPurchaser().getUserId();				
			}
			result.add(
					new Object [] {
							giftCertificate.getCreationDate(),
							giftCertificate.displayMaskedGiftCertificateCode(),
							giftCertificate.getRecipientEmail(),
							purchaserName,
							giftCertificate.getCurrencyCode(),
							giftCertificate.getPurchaseAmount(),
							giftCertificate.retrieveBalanceMoney().getAmount()
					}
					);		
		}
		
		return result;
		
	}

	/**
	 * Retrieves list of <code>OrderPayment</code>s associated with the specified gift certificate grouped by their shipments.
	 * 
	 * @param uidPk GiftCertificate uid.
	 * @return map of shipments to their payments.
	 */
	public Map<Order, Money> retrieveOrdersBalances(final long uidPk) {
		sanityCheck();
		
		final Map<Order, Money> ordersBalance = new TreeMap<Order, Money>(new Comparator<Order>() {
			public int compare(final Order order1, final Order order2) {
				return order1.getCreatedDate().compareTo(order2.getCreatedDate());
			}
		});

		List<Order> orders = getPersistenceEngine().retrieveByNamedQuery("ORDERS_BY_GIFT_CERTIFICATE", uidPk);
		for (Order order : orders) {
			List<GiftCertificateTransaction> transactions = getPersistenceEngine().retrieveByNamedQuery(
					"GIFT_CERTIFICATE_TRANSACTIONS_BY_ORDER_AND_GIFT_CERTIFICATE", uidPk, PaymentType.GIFT_CERTIFICATE, order.getUidPk());
			BigDecimal amount = getGiftCertificateTransactionService().calcTransactionBalance(transactions);
			Money money = MoneyFactory.createMoney(amount, order.getCurrency());
			ordersBalance.put(order, money);
		}

		return ordersBalance;
	}
}
