/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain;

import java.io.Serializable;

import com.elasticpath.base.Initializable;
import com.elasticpath.commons.util.Utility;

/**
 * Represents a general domain object.
 */
public interface EpDomain extends Serializable, Initializable {

	/**
	 * Returns the <code>Utility</code> singleton.
	 * @return the <code>Utility</code> singleton.
	 * @deprecated If the implementation class needs the Utility object it should be retrieved inside that class.
	 */
	@Deprecated
	Utility getUtility();
}
