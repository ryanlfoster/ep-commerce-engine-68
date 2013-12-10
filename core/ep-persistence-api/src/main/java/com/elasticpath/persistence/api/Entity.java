/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.persistence.api;

import com.elasticpath.base.GloballyIdentifiable;
import com.elasticpath.base.Initializable;


/**
 * <code>Entity</code> represents a entity domain object that includes the EpDomain interface (bad).
 */
public interface Entity extends GloballyIdentifiable<String>, Initializable, Persistable {
	
}
