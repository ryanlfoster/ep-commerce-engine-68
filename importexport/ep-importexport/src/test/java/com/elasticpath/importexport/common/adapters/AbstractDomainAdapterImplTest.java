package com.elasticpath.importexport.common.adapters;

import org.junit.Test;

import com.elasticpath.common.dto.Dto;

/**
 * Tests AbstractDomainAdapterImpl.
 */
public class AbstractDomainAdapterImplTest {

	private final AbstractDomainAdapterImpl<Object, Dto> abstractDomainAdapterImpl = new AbstractDomainAdapterImpl<Object, Dto>() {

		@Override
		public void populateDTO(final Object source, final Dto target) {
			// empty
		}
		
		@Override
		public void populateDomain(final Dto source, final Object target) {
			// empty
		}
	
	};
	
	/**
	 * Tests CreateDtoObject.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void testCreateDtoObject() {
		abstractDomainAdapterImpl.createDtoObject();
	}

	/**
	 * Tests CreateDomainObject.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void testCreateDomainObject() {
		abstractDomainAdapterImpl.createDomainObject();
	}
}
