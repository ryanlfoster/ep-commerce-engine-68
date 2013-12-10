package com.elasticpath.tools.sync.target.result;

/**
 * Encapsulates information about error occurred during transaction job unit synchronization.  
 */
public class SyncErrorResultItem extends SyncResultItem {

	private Exception cause;

	/**
	 * @return the cause
	 */
	public Exception getCause() {
		return cause;
	}
	
	/**
	 * @param cause the cause to set
	 */
	public void setCause(final Exception cause) {
		this.cause = cause;
	}
	
	/**
	 * @return human-readable error
	 */
	@Override
	public String toString() {
		StringBuffer stringBuffer = new StringBuffer(super.toString());
		if (getJobEntryGuid() != null && cause != null) {
			stringBuffer.append("\nTransaction job entry failure cause: ").append(cause.toString());
			if (cause.getCause() != null) { //NOPMD
				stringBuffer.append("\nRoot Cause: ").append(cause.getCause().toString());
			}					
		}
		return stringBuffer.toString();
	}
	
}

