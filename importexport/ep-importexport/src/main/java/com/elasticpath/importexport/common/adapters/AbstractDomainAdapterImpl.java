package com.elasticpath.importexport.common.adapters;

import com.elasticpath.common.dto.Dto;
import com.elasticpath.importexport.common.caching.CachingService;
import com.elasticpath.importexport.common.exception.runtime.PopulationRuntimeException;
import com.elasticpath.service.impl.AbstractEpServiceImpl;

/**
 * Abstract class for domain adapter with default behavior.
 * 
 * @param <DOMAIN> the domain interface
 * @param <DTO> the data transfer interface that extends <code>Dto</code> interface
 */
public abstract class AbstractDomainAdapterImpl<DOMAIN, DTO extends Dto> extends AbstractEpServiceImpl implements DomainAdapter<DOMAIN, DTO> {

	private CachingService cachingService;

	/**
	 * Throws <code>UnsupportedOperationException</code>.
	 * 
	 * @return nothing
	 */
	public DTO createDtoObject() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Throws <code>UnsupportedOperationException</code>.
	 * 
	 * @return nothing
	 */
	public DOMAIN createDomainObject() {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Gets the cachingService.
	 * 
	 * @return the cachingService
	 * @see CachingService
	 */
	public CachingService getCachingService() {
		return cachingService;
	}

	/**
	 * Sets the cachingService.
	 * 
	 * @param cachingService the cachingService to set
	 * @see CachingService
	 */
	public void setCachingService(final CachingService cachingService) {
		this.cachingService = cachingService;
	}
	
	@Override
	public DOMAIN buildDomain(final DTO source, final DOMAIN target) {
		if (target == null) { 
			throw new PopulationRuntimeException("IE-10800");
		}
		populateDomain(source, target);
		return target;
	}
}
