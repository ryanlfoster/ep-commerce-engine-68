/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.persistence.support.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.domain.order.AdvancedOrderSearchCriteria;
import com.elasticpath.domain.order.OrderPaymentStatus;
import com.elasticpath.domain.order.OrderShipmentStatus;
import com.elasticpath.domain.order.OrderStatus;
import com.elasticpath.persistence.support.OrderCriterion;
import com.elasticpath.service.search.query.CustomerSearchCriteria;
import com.elasticpath.service.search.query.EpInvalidOrderCriterionResultTypeException;
import com.elasticpath.service.search.query.OrderReturnSearchCriteria;
import com.elasticpath.service.search.query.OrderSearchCriteria;
import com.elasticpath.service.search.query.SortBy;
import com.elasticpath.service.search.query.SortOrder;
import com.elasticpath.service.search.query.StandardSortBy;

/**
 * Implementation of OrderCriterion that constructs the appropriate criteria for order queries.
 */
public class OrderCriterionImpl implements OrderCriterion {

	private static final String ORDER_QUERY = "select o from OrderImpl as o";

	private static final String ORDER_COUNT = "select count (o.uidPk) from OrderImpl as o";

	private static final String ORDER_NUMBER = "select o.orderNumber from OrderImpl as o";

	private static final String ORDER_RETURN_QUERY = "select ort from OrderReturnImpl as ort";

	private static final String ORDER_RETURN_COUNT = "select count (ort.uidPk) from OrderReturnImpl as ort";

	private static final String ORDER_JOIN = ", in(ort.order) as o";

	private static final String ORDER_SHIPMENT_JOIN = ", in(o.shipments) as os";

	private static final String ORDER_RETURN_JOIN = ", in(o.returns) as ort";

	private static final String ORDER_PAYMENT_JOIN = ", in(o.orderPayments) as op";

	private static final String ORDER_SKU_JOIN = ", in(os.shipmentOrderSkusInternal) as sku";

	private static final String ORDER_WHERE_ORDER_NUMBER_EQUALS = " o.orderNumber = ";

	private static final String ORDER_WHERE_TO_DATE = " o.createdDate <= ";

	private static final String ORDER_WHERE_FROM_DATE = " o.createdDate >= ";

	private static final String ORDER_WHERE_ORDER_STATUS_EQUALS = " o.status = ";

	private static final String ORDER_WHERE_ORDER_STATUS_DOES_NOT_EQUALS = " o.status <> ";

	private static final String ORDER_WHERE_STORE_IN = " o.storeCode in (:storeList)";

	private static final String ORDER_WHERE_CUSTOMER_ID_LIKE = " o.customer.userId LIKE ";

	private static final String ORDER_WHERE_CUSTOMER_NUMBER_EQUALS = " o.customer.uidPk = ";

	private static final String ORDER_WHERE_CUSTOMER_GUID_EQUALS = " o.customer.guid = ";

	private static final String ORDER_WHERE_FIRST_NAME_EQUALS = " o.billingAddress.firstName = ";

	private static final String ORDER_WHERE_LAST_NAME_EQUALS = " o.billingAddress.lastName = ";

	private static final String ORDER_WHERE_POSTAL_CODE_LIKE = " o.billingAddress.zipOrPostalCode LIKE ";

	private static final String ORDER_WHERE_PHONE_NUMBER_EQUALS = " o.billingAddress.phoneNumber = ";

	private static final String ORDER_WHERE_SHIPMENT_STATUS_EQUALS = " os.status = ";

	private static final String ORDER_WHERE_ORDER_SKU_EQUALS = " sku.skuCode = ";

	private static final String ORDER_WHERE_RMA_EQUALS = " ort.rmaCode = ";

	private static final String WHERE_CLAUSE = " where";

	private static final String AND_CLAUSE = " and";

	@Override
	public String getStatusCriteria(final OrderStatus orderStatus, final OrderPaymentStatus paymentStatus, final OrderShipmentStatus shipmentStatus) {
		final StringBuffer query = new StringBuffer();
		query.append(ORDER_QUERY);
		query.append(getStatusInnerJoin(paymentStatus, shipmentStatus));
		query.append(" where o = o");
		query.append(getStatusWhereCondition(orderStatus, paymentStatus, shipmentStatus));
		return query.toString();
	}

	private String getStatusInnerJoin(final OrderPaymentStatus paymentStatus, final OrderShipmentStatus shipmentStatus) {
		final StringBuffer query = new StringBuffer();
		if (paymentStatus != null) {
			query.append(ORDER_PAYMENT_JOIN);
		}
		if (shipmentStatus != null) {
			query.append(ORDER_SHIPMENT_JOIN);
		}
		return query.toString();
	}

	private String getStatusWhereCondition(final OrderStatus orderStatus,
			final OrderPaymentStatus paymentStatus, final OrderShipmentStatus shipmentStatus) {
		final StringBuffer query = new StringBuffer();
		int paramPosition = 1;
		paramPosition = attachConditionInWhereCaluse(query, ORDER_WHERE_ORDER_STATUS_EQUALS, orderStatus, paramPosition);
		paramPosition = attachConditionInWhereCaluse(query, " op.status = ", paymentStatus, paramPosition);
		paramPosition = attachConditionInWhereCaluse(query,  ORDER_WHERE_SHIPMENT_STATUS_EQUALS, shipmentStatus, paramPosition);
		return query.toString();
	}

	private int attachConditionInWhereCaluse(final StringBuffer query, final String leftSideCondition, final Object rightSideCondition,
			final int paramPosition) {
		if (rightSideCondition == null) {
			return paramPosition;
		}

		query.append(AND_CLAUSE).append(leftSideCondition).append('?').append(paramPosition);

		return paramPosition + 1;
	}

	@Override
	public String getOrderCustomerProfileCriteria(final String attributeKey, final String attributeValue, final boolean isExactMatch) {
		final StringBuffer query = new StringBuffer();
		query.append(ORDER_QUERY);
		query.append(" inner join o.customer as c inner join c.profileValueMap as cp where cp.localizedAttributeKey = '").append(attributeKey).append(
		"' and cp.shortTextValue ");
		if (isExactMatch) {
			query.append(" = '").append(attributeValue).append('\'');
		} else {
			query.append(" like '%").append(attributeValue).append("%'"); // NOPMD
		}
		return query.toString();
	}

	@Override
	public String getOrderCustomerCriteria(final String propertyName, final String criteriaValue, final boolean isExactMatch) {
		final StringBuffer query = new StringBuffer();
		query.append(ORDER_QUERY);
		query.append(" where o.customer.").append(propertyName);
		if (isExactMatch) {
			query.append(" = '").append(criteriaValue).append('\'');
		} else {
			query.append(" like '%").append(criteriaValue).append("%'");
		}
		return query.toString();
	}

	@Override
	public String getOrderGiftCertificateCriteria(final String propertyName, final String criteriaValue, final boolean isExactMatch) {
		final StringBuffer query = new StringBuffer();
		query.append(ORDER_QUERY);
		query.append(" left outer join o.orderPayments as op where op.giftCertificate.").append(propertyName);
		if (isExactMatch) {
			query.append(" = '").append(criteriaValue).append('\'');
		} else {
			query.append(" like '%").append(criteriaValue).append("%'");
		}
		return query.toString();
	}

	@Override
	@SuppressWarnings({ "PMD.NPathComplexity", "PMD.ExcessiveMethodLength" })
	public String getAdvancedOrderCriteria(final AdvancedOrderSearchCriteria orderSearchCriteria) {
		StringBuffer query = null;
		boolean hasFirstCondition = false;
		int numOfParameter = 0;

		Map<String, String> shipmentAddressCriteria = orderSearchCriteria.getShipmentAddressCriteria();

		if (((shipmentAddressCriteria == null)
				|| shipmentAddressCriteria.isEmpty())
				&& (orderSearchCriteria.getSkuCode() == null)) {
			query = new StringBuffer(ORDER_QUERY);
		} else {
			query = new StringBuffer(ORDER_QUERY);
			query.append(ORDER_SHIPMENT_JOIN);

			if ((shipmentAddressCriteria != null)
					&& !shipmentAddressCriteria.isEmpty()) {
				for (final String propertyName : shipmentAddressCriteria.keySet()) {
					if (hasFirstCondition) {
						query.append(AND_CLAUSE);
					} else {
						hasFirstCondition = true;
						query.append(WHERE_CLAUSE);
					}
					query.append(" os.shipmentAddress.").append(propertyName).append(" like '%").append(
							shipmentAddressCriteria.get(propertyName)).append("%'");
				}
			}
			if (orderSearchCriteria.getSkuCode() != null) {
				if (hasFirstCondition) {
					query.append(AND_CLAUSE);
				} else {
					hasFirstCondition = true;
					query.append(WHERE_CLAUSE);
				}
				query.append(" os.shipmentOrderSkusInternal.skuCode like '%").append(orderSearchCriteria.getSkuCode()).append("%\'");
			}
		}

		if (orderSearchCriteria.getStoreCode() != null) {
			if (hasFirstCondition) {
				query.append(AND_CLAUSE);
			} else {
				hasFirstCondition = true;
				query.append(WHERE_CLAUSE);
			}
			query.append(" o.storeCode = ").append(orderSearchCriteria.getStoreCode());
		}

		if (orderSearchCriteria.getOrderStatus() != null) {
			if (hasFirstCondition) {
				query.append(AND_CLAUSE);
			} else {
				hasFirstCondition = true;
				query.append(WHERE_CLAUSE);
			}
			query.append(" o.status = ").append(orderSearchCriteria.getOrderStatus());
		}

		if (orderSearchCriteria.getOrderFromDate() != null) {
			if (hasFirstCondition) {
				query.append(AND_CLAUSE);
			} else {
				hasFirstCondition = true;
				query.append(WHERE_CLAUSE);
			}
			numOfParameter++;
			query.append(" o.createdDate >= ?").append(numOfParameter);
		}

		if (orderSearchCriteria.getOrderToDate() != null) {
			if (hasFirstCondition) {
				query.append(AND_CLAUSE);
			} else {
				hasFirstCondition = true;
				query.append(WHERE_CLAUSE);
			}
			numOfParameter++;
			query.append(" o.createdDate <= ?").append(numOfParameter);
		}

		if (orderSearchCriteria.getCustomerCriteria() != null) {
			for (final String propertyName : orderSearchCriteria.getCustomerCriteria().keySet()) {
				if (hasFirstCondition) {
					query.append(AND_CLAUSE);
				} else {
					hasFirstCondition = true;
					query.append(WHERE_CLAUSE);
				}
				query.append(" o.customer.").append(propertyName).append(" like '%").append(
						orderSearchCriteria.getCustomerCriteria().get(propertyName)).append("%'");
			}
		}

		if (orderSearchCriteria.getShipmentStatus() != null) {
			if (hasFirstCondition) {
				query.append(AND_CLAUSE);
			} else {
				hasFirstCondition = true;
				query.append(WHERE_CLAUSE);
			}
			query.append(" o.shipments.status = ").append(orderSearchCriteria.getShipmentStatus());
		}
		return query.toString();
	}

	@Override
	@SuppressWarnings({ "PMD.NPathComplexity", "PMD.ExcessiveMethodLength" })
	public String getOrderSearchCriteria(final OrderSearchCriteria orderSearchCriteria,
			final List<Object> parameters,
			final Collection<String> stores,
			final ResultType resultType) {
		parameters.clear();
		stores.clear();

		StringBuffer query = startOrderQuery(resultType);

		String filterClause = WHERE_CLAUSE;

		// set up the query joins
		String rmaCode = orderSearchCriteria.getRmaCode();
		if (!StringUtils.isEmpty(rmaCode)) {
			query.append(ORDER_RETURN_JOIN);
		}

		String shipmentStatus = null;
		if (orderSearchCriteria.getShipmentStatus() != null) {
			shipmentStatus = orderSearchCriteria.getShipmentStatus().toString();
		}
		String skuCode = orderSearchCriteria.getSkuCode();
		if (StringUtils.isNotEmpty(shipmentStatus)
				|| StringUtils.isNotEmpty(skuCode)) {
			query.append(ORDER_SHIPMENT_JOIN);
		}

		if (StringUtils.isNotEmpty(skuCode)) {
			query.append(ORDER_SKU_JOIN);
		}

		// add the filters
		filterClause = appendSubQuery(query, filterClause, ORDER_WHERE_ORDER_NUMBER_EQUALS, orderSearchCriteria.getOrderNumber(), parameters);

		filterClause = appendSubQuery(query, filterClause, ORDER_WHERE_ORDER_STATUS_EQUALS, orderSearchCriteria.getOrderStatus(), parameters);

		filterClause = appendSubQuery(query, filterClause, ORDER_WHERE_ORDER_STATUS_DOES_NOT_EQUALS,
				orderSearchCriteria.getExcludedOrderStatus(), parameters);

		filterClause = appendSubQuery(query, filterClause, ORDER_WHERE_SHIPMENT_STATUS_EQUALS, orderSearchCriteria.getShipmentStatus(), parameters);

		if (orderSearchCriteria.getCustomerSearchCriteria() != null) {
			String customerNumber = orderSearchCriteria.getCustomerSearchCriteria().getCustomerNumber();
			if (StringUtils.isNotEmpty(customerNumber)) {
				filterClause = appendSubQuery(query, filterClause, ORDER_WHERE_CUSTOMER_NUMBER_EQUALS, Long.parseLong(customerNumber));
			}

			filterClause = appendSubQuery(query, filterClause, ORDER_WHERE_CUSTOMER_GUID_EQUALS,
					orderSearchCriteria.getCustomerSearchCriteria().getGuid(), parameters);

			filterClause = appendSubLikeQuery(query, filterClause, ORDER_WHERE_CUSTOMER_ID_LIKE,
					orderSearchCriteria.getCustomerSearchCriteria().getUserId(), parameters);

			filterClause = appendSubQuery(query, filterClause, ORDER_WHERE_FIRST_NAME_EQUALS,
					orderSearchCriteria.getCustomerSearchCriteria().getFirstName(), parameters);

			filterClause = appendSubQuery(query, filterClause, ORDER_WHERE_LAST_NAME_EQUALS,
					orderSearchCriteria.getCustomerSearchCriteria().getLastName(), parameters);

			filterClause = appendSubQuery(query, filterClause, ORDER_WHERE_PHONE_NUMBER_EQUALS,
					orderSearchCriteria.getCustomerSearchCriteria().getPhoneNumber(), parameters);
		}

		// called shipment zip code, but use billing address zip code in query
		filterClause = appendSubLikeQuery(query, filterClause, ORDER_WHERE_POSTAL_CODE_LIKE, orderSearchCriteria.getShipmentZipcode(), parameters);

		filterClause = appendSubQuery(query, filterClause, ORDER_WHERE_ORDER_SKU_EQUALS, skuCode, parameters);

		filterClause = appendSubQuery(query, filterClause, ORDER_WHERE_RMA_EQUALS, rmaCode, parameters);

		filterClause = appendSubQuery(query, filterClause, ORDER_WHERE_TO_DATE, orderSearchCriteria.getOrderToDate(), parameters);

		filterClause = appendSubQuery(query, filterClause, ORDER_WHERE_FROM_DATE, orderSearchCriteria.getOrderFromDate(), parameters);

		Collection<String> codes = orderSearchCriteria.getStoreCodes();
		if ((codes != null) && !codes.isEmpty()) {
			query.append(filterClause);
			query.append(ORDER_WHERE_STORE_IN);
			for (String code : codes) {
				if (code != null) {
					stores.add(code);
				}
			}
			filterClause = AND_CLAUSE;
		}


		if (resultType != ResultType.COUNT) {
			SortOrder sortOrder = orderSearchCriteria.getSortingOrder();
			SortBy sortBy = orderSearchCriteria.getSortingType();
			boolean bAppendOrderNumber = true;
			query.append(" order by ");
			switch (sortBy.getOrdinal()) {
			case StandardSortBy.DATE_ORDINAL:
				query.append("o.createdDate");
				break;
			case StandardSortBy.STATUS_ORDINAL:
				query.append("o.status");
				break;
			case StandardSortBy.CUSTOMER_ID_ORDINAL:
				query.append("o.customer.userId");
				break;
			case StandardSortBy.CUSTOMER_NAME_ORDINAL:
				query.append("o.billingAddress.firstName ").append(sortOrder).append(", o.billingAddress.lastName");
				break;
			case StandardSortBy.PHONE_ORDINAL:
				query.append("o.billingAddress.phoneNumber");
				break;
			case StandardSortBy.STORE_CODE_ORDINAL:
				query.append("o.storeCode");
				break;
			case StandardSortBy.TOTAL_ORDINAL:
				query.append("o.currency desc, o.total");
				break;
			default:
				query.append("o.orderNumber");
				bAppendOrderNumber = false;
				break;
			}
			query.append(' ').append(sortOrder);
			if (bAppendOrderNumber) {
				query.append(", o.orderNumber ").append(sortOrder);
			}
		}

		return query.toString();
	}

	private StringBuffer startOrderQuery(final ResultType resultType) {
		StringBuffer query;
		if (resultType == ResultType.COUNT) {
			query = new StringBuffer(ORDER_COUNT);
		} else if (resultType == ResultType.ORDER_NUMBER) {
			query = new StringBuffer(ORDER_NUMBER);
		} else {
			query = new StringBuffer(ORDER_QUERY);
		}
		return query;
	}

	private StringBuffer startOrderReturnQuery(final ResultType resultType) {
		StringBuffer query;
		if (resultType == ResultType.COUNT) {
			query = new StringBuffer(ORDER_RETURN_COUNT);
		} else if (resultType == ResultType.ENTITY) {
			query = new StringBuffer(ORDER_RETURN_QUERY);
		} else {
			throw new EpInvalidOrderCriterionResultTypeException("Invalid result type for order return.");
		}
		return query;
	}

	private String appendSubQuery(final StringBuffer query, final String filterClause, final String compareClause, final long value) {
		query.append(filterClause);
		query.append(compareClause);
		query.append(value);
		return AND_CLAUSE;
	}

	private String appendSubQuery(final StringBuffer query,
			final String filterClause,
			final String compareClause,
			final String value,
			final List<Object> parameters) {
		if (StringUtils.isNotEmpty(value)) {
			query.append(filterClause);
			query.append(compareClause);
			parameters.add(value);
			query.append('?').append(parameters.size());
			return AND_CLAUSE;
		}
		return filterClause;
	}

	private String appendSubLikeQuery(final StringBuffer query,
			final String filterClause, final String compareClause,
			final String value, final List<Object> parameters) {
		if (StringUtils.isNotEmpty(value)) {
			query.append(filterClause);
			query.append(compareClause);
			parameters.add(value + "%");
			query.append('?').append(parameters.size());
			return AND_CLAUSE;
		}
		return filterClause;
	}

	private String appendSubQuery(final StringBuffer query,
			final String filterClause,
			final String compareClause,
			final Object value,
			final List<Object> parameters) {
		if (value != null) {
			query.append(filterClause);
			query.append(compareClause);
			parameters.add(value);
			query.append('?').append(parameters.size());
			return AND_CLAUSE;
		}
		return filterClause;
	}

	@Override
	public String getOrderReturnSearchCriteria(
			final OrderReturnSearchCriteria orderReturnSearchCriteria,
			final List<Object> parameters, final ResultType resultType) {
		parameters.clear();

		StringBuffer query = startOrderReturnQuery(resultType);

		String filterClause = WHERE_CLAUSE;

		// set up the query joins
		String orderNumber = orderReturnSearchCriteria.getOrderNumber();

		CustomerSearchCriteria customerSearchCriteria = orderReturnSearchCriteria.getCustomerSearchCriteria();
		String firstName = null;
		String lastName = null;
		if (customerSearchCriteria != null) {
			firstName = customerSearchCriteria.getFirstName();
			lastName = customerSearchCriteria.getLastName();
		}

		if (StringUtils.isNotBlank(orderNumber) || StringUtils.isNotBlank(firstName) || StringUtils.isNotBlank(lastName)) {
			query.append(ORDER_JOIN);
		}

		// add the filters
		String rmaCode = orderReturnSearchCriteria.getRmaCode();
		filterClause = appendSubQuery(query, filterClause, ORDER_WHERE_RMA_EQUALS, rmaCode, parameters);

		filterClause = appendSubQuery(query, filterClause, ORDER_WHERE_ORDER_NUMBER_EQUALS, orderReturnSearchCriteria.getOrderNumber(), parameters);

		if (orderReturnSearchCriteria.getCustomerSearchCriteria() != null) {
			filterClause = appendSubQuery(query, filterClause, ORDER_WHERE_FIRST_NAME_EQUALS,
					orderReturnSearchCriteria.getCustomerSearchCriteria().getFirstName(), parameters);

			filterClause = appendSubQuery(query, filterClause, ORDER_WHERE_LAST_NAME_EQUALS,
					orderReturnSearchCriteria.getCustomerSearchCriteria().getLastName(), parameters);
		}

		if (resultType != ResultType.COUNT) {
			SortOrder sortOrder = orderReturnSearchCriteria.getSortingOrder();
			SortBy sortBy = orderReturnSearchCriteria.getSortingType();
			query.append(" order by ");
			switch (sortBy.getOrdinal()) {
			case StandardSortBy.DATE_ORDINAL:
				query.append("ort.createdDate");
				break;
			case StandardSortBy.STATUS_ORDINAL:
				query.append("ort.returnStatus");
				break;
			case StandardSortBy.ORDER_NUMBER_ORDINAL:
				query.append("o.orderNumber");
				break;
			default:
				query.append("ort.rmaCode");
				break;
			}
			query.append(' ').append(sortOrder);
		}

		return query.toString();
	}


}
