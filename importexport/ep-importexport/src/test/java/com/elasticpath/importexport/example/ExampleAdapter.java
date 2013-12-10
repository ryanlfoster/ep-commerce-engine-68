package com.elasticpath.importexport.example;

import com.elasticpath.importexport.common.adapters.AbstractDomainAdapterImpl;

/**
 * Example {@link com.elasticpath.importexport.common.adapters.DomainAdapter DomainAdapter} for extensibility testing.
 */
public class ExampleAdapter extends AbstractDomainAdapterImpl<ExamplePersistence, ExampleDTO> {
	@Override
	public void populateDomain(final ExampleDTO source, final ExamplePersistence target) {
		target.setName(source.getName());
	}

	@Override
	public void populateDTO(final ExamplePersistence source, final ExampleDTO target) {
		target.setName(source.getName());
	}

	@Override
	public ExamplePersistence createDomainObject() {
		return new ExamplePersistence();
	}

	@Override
	public ExampleDTO createDtoObject() {
		return new ExampleDTO();
	}
}