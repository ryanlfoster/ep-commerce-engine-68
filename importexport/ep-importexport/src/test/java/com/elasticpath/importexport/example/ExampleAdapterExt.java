package com.elasticpath.importexport.example;

/**
 * Example {@link com.elasticpath.importexport.common.adapters.DomainAdapter DomainAdapter} for
 * {@link ExamplePersistenceExt} for extensibility testing.
 */
public class ExampleAdapterExt extends ExampleAdapter {
	@Override
	public void populateDomain(final ExampleDTO source, final ExamplePersistence target) {
		super.populateDomain(source, target);

		ExampleExtDTO sourceExt = (ExampleExtDTO) source;
		ExamplePersistenceExt targetExt = (ExamplePersistenceExt) target;
		targetExt.setCode(sourceExt.getCode());
	}

	@Override
	public void populateDTO(final ExamplePersistence source, final ExampleDTO target) {
		super.populateDTO(source, target);

		ExamplePersistenceExt sourceExt = (ExamplePersistenceExt) source;
		ExampleExtDTO targetExt = (ExampleExtDTO) target;
		targetExt.setCode(sourceExt.getCode());
	}

	@Override
	public ExamplePersistenceExt createDomainObject() {
		return new ExamplePersistenceExt();
	}

	@Override
	public ExampleExtDTO createDtoObject() {
		return new ExampleExtDTO();
	}
}