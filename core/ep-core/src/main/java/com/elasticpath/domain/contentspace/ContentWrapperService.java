package com.elasticpath.domain.contentspace;

import java.io.Serializable;

/**
 * Service definition type for <code>ContentWrapper</code>.
 */
public interface ContentWrapperService extends Serializable {
	
	/**
	 * Service name, that will be used by script engine. 
	 * @return Name of service.
	 */
	String getName();

	/**
	 * Id of Spring bean. 
	 * @return Id of Spring bean.
	 */
	String getValue();
	

}
