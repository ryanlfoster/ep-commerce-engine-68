package com.elasticpath.tools.sync.job.transaction;

import java.util.Collections;
import java.util.List;


/**
 * This class contains transaction settings.
 */
public class TransactionSettings {
	
	private TransactionAttribute transactionAttribute = TransactionAttribute.JOBENTRY;
	
	private List<String> parameters = Collections.emptyList();

	/**
	 * @return the transactionAttribute
	 */
	public TransactionAttribute getTransactionAttribute() {
		return transactionAttribute;
	}

	/**
	 * @param transactionAttribute the transactionAttribute to set
	 */
	public void setTransactionAttribute(final TransactionAttribute transactionAttribute) {
		this.transactionAttribute = transactionAttribute;
	}

	/**
	 * @return the parameters
	 */
	public List<String> getParameters() {
		return parameters;
	}

	/**
	 * @param parameters the parameters to set
	 */
	public void setParameters(final List<String> parameters) {
		this.parameters = parameters;
	}
}
