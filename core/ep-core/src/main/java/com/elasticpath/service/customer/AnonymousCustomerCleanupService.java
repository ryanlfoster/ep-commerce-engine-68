/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.service.customer;

import java.util.Date;

/**
 * Service to clean up anonymous customer usage.
 */
public interface AnonymousCustomerCleanupService {

	/**
	 * Delete anonymous customers. <br>
	 * If the removal date is less than the anonymous customer creation date, the anonymous customer is considered as a candidate.<br>
	 *
	 * @param removalDate the last modified date before which anonymous customers are considered a candidate for removal
	 * @param maxResults limits the number of anonymous customers to remove
	 * @return the number of anonymous customers removed
	 */
	int deleteAnonymousCustomers(Date removalDate, int maxResults);

}
