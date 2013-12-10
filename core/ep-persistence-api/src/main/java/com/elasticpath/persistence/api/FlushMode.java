package com.elasticpath.persistence.api;

/**
 * Exact, but non-JPA copy of the JPA enum {@link javax.persistence.FlushModeType}.
 */
public enum FlushMode {
	/** Same meaning as {@link javax.persistence.FlushModeType#COMMIT}. */
	COMMIT,

	/** Same function as {@link javax.persistence.FlushModelType#AUTO}. */
	AUTO
}
