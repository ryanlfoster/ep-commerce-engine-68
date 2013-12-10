package com.elasticpath.importexport.example;

import java.util.ArrayList;
import java.util.List;

/**
 * Example {@link com.elasticpath.importexport.importer.importers.Importer Importer} for {@link ExampleExtDTO}
 * (for extensibility tests).
 */
public class ExampleExtDtoImporter extends ExampleDtoImporter {
	@Override
	public Class<? extends ExampleDTO> getDtoClass() {
		return ExampleExtDTO.class;
	}

	@Override
	public List<Class<?>> getAuxiliaryJaxbClasses() {
		// The list returned here contains all classes (other than the one that is returned by getDtoClass() above)
		// that the JAXBContext needs to know about to import the extension dto class above.
		// Therefore add super's auxiliary class and super's dto class as these will be needed.
		// The dto class above is not added as retrieved separately.

		final List<Class<?>> result = new ArrayList<Class<?>>(super.getAuxiliaryJaxbClasses());
		result.add(super.getDtoClass());
		return result;
	}

	@Override
	public String getImportedObjectName() {
		return ExampleExtDTO.ROOT_ELEMENT;
	}

	@Override
	protected ExampleAdapter getDomainAdapter() {
		return new ExampleAdapterExt();
	}
}
