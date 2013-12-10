package com.elasticpath.service;

import com.elasticpath.domain.order.OrderReturn;
import com.elasticpath.domain.order.OrderReturnSku;
import com.elasticpath.domain.order.OrderReturnStatus;

/**
 * Provide Returns-related business operations.
 */
public class ReturnsExchangeService {
	
	/**
	 * 
	 * @param orderReturn order return.
	 * @param orderReturnSku order return sku.
	 * @param recQty received quantity.
	 */
	public void receiveQuantity(final OrderReturn orderReturn, final OrderReturnSku orderReturnSku, final int recQty) {
		if (recQty <= orderReturnSku.getQuantity()) {
			orderReturnSku.setReceivedQuantity(recQty);
			for (OrderReturnSku sku : orderReturn.getOrderReturnSkus()) {
				if (sku.getReceivedQuantity() != orderReturnSku.getQuantity()) {
					orderReturn.setReturnStatus(OrderReturnStatus.AWAITING_STOCK_RETURN);
					return;
				}
			}
			orderReturn.setReturnStatus(OrderReturnStatus.AWAITING_COMPLETION);
			return;
		}
	}
}
