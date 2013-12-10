package com.elasticpath.tools.sync.exception;

/**
 * Should be thrown when required <code>ChangeSet</code> couldn't be found.
 */
public class ChangeSetNotFoundException extends SyncToolRuntimeException {

	private static final long serialVersionUID = 6388579784885275513L;

	private final String changeSetName;
	
	/**
	 * @param message message describing error
	 * @param changeSetName name of the undefined change set.
	 */
	public ChangeSetNotFoundException(final String message, final String changeSetName) {
		super(message);
		this.changeSetName = changeSetName;
	}

	/**
	 * @param message message describing error
	 * @param cause exception causing this error
	 * @param changeSetName name of the undefined change set.
	 */
	public ChangeSetNotFoundException(final String message, final Throwable cause, final String changeSetName) {
		super(message, cause);
		this.changeSetName = changeSetName;
	}

	/**
	 * Returns change set name that cause an exception.
	 * 
	 * @return change set name.
	 */
	public String getChangeSetName() {
		return changeSetName;
	}
}
