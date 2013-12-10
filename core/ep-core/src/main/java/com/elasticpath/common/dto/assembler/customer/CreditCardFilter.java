package com.elasticpath.common.dto.assembler.customer;

import com.elasticpath.common.dto.customer.CreditCardDTO;
import com.elasticpath.domain.customer.CustomerCreditCard;

/**
 * Used to filter a credit card during DtoAssembler processing.
 * See {@code BuiltinFilters} for two simple implementations.
 */
public interface CreditCardFilter {

	/**
	 * Given a credit card, filter it in some interesting way.
	 * 
	 * @param card The card to transform.
	 * @return The filtered credit card or null.
	 */
	CreditCardDTO filter(CustomerCreditCard card);
}
