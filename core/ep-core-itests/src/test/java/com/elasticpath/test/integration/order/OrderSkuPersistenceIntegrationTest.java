/**
 * 
 */
package com.elasticpath.test.integration.order;

import org.junit.Test;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.test.db.DbTestCase;
import com.elasticpath.test.integration.DirtiesDatabase;

/**
 * Tests for persisting OrderSkus.
 */
public class OrderSkuPersistenceIntegrationTest extends DbTestCase {
	
	/**
	 * Test persisting an OrderSku and retrieving it again.
	 */
	@DirtiesDatabase
	@Test
	public void testPersistOrderSku() {
		OrderSku orderSku = createOrderSku();
		persistOrderSku(orderSku);
	}

	private OrderSku persistOrderSku(final OrderSku orderSku) throws TransactionException {
		final OrderSku savedOrderSku = getTxTemplate().execute(new TransactionCallback<OrderSku>() {
			public OrderSku doInTransaction(final TransactionStatus arg0) {
				getPersistenceEngine().save(orderSku);
				return orderSku;
			}
		});
		return savedOrderSku;
	}
	
	protected PersistenceEngine getPersistenceEngine() {
		return getBeanFactory().getBean(ContextIdNames.PERSISTENCE_ENGINE);
	}

	private OrderSku createOrderSku() {
		OrderSku orderSku = getBeanFactory().getBean(ContextIdNames.ORDER_SKU);
		orderSku.setDisplayName("");
		orderSku.setSkuCode("testsku");
		orderSku.setTaxCode("GOODS");
		orderSku.setOrdering(0);
		return orderSku;
	}
}
