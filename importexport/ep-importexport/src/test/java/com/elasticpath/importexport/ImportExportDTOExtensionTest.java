package com.elasticpath.importexport;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.fail;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.jmock.Expectations;
import org.jmock.api.Invocation;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.lib.action.CustomAction;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.common.dto.Dto;
import com.elasticpath.commons.ThreadLocalMap;
import com.elasticpath.importexport.common.summary.Summary;
import com.elasticpath.importexport.example.ExampleDTO;
import com.elasticpath.importexport.example.ExampleDtoExporter;
import com.elasticpath.importexport.example.ExampleDtoImporter;
import com.elasticpath.importexport.example.ExampleExtDTO;
import com.elasticpath.importexport.example.ExampleExtDtoExporter;
import com.elasticpath.importexport.example.ExampleExtDtoImporter;
import com.elasticpath.importexport.example.ExamplePersistence;
import com.elasticpath.importexport.example.ExamplePersistenceExt;
import com.elasticpath.importexport.exporter.context.ExportContext;
import com.elasticpath.importexport.exporter.exportentry.ExportEntry;
import com.elasticpath.importexport.exporter.exporters.Exporter;
import com.elasticpath.importexport.importer.changesetsupport.BusinessObjectDescriptorLocator;
import com.elasticpath.importexport.importer.configuration.ImportConfiguration;
import com.elasticpath.importexport.importer.context.ImportContext;
import com.elasticpath.importexport.importer.controller.RelatedObjectsResolver;
import com.elasticpath.importexport.importer.controller.impl.ChangeSetImportStageImpl;
import com.elasticpath.importexport.importer.importers.ImporterFactory;
import com.elasticpath.importexport.importer.importers.SavingStrategy;
import com.elasticpath.importexport.importer.importers.impl.ImportProcessorImpl;
import com.elasticpath.importexport.importer.importers.impl.ImportStatusHolder;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.persistence.api.PersistenceSession;
import com.elasticpath.persistence.api.Transaction;
import com.elasticpath.service.changeset.ChangeSetService;
import com.elasticpath.service.misc.TimeService;

/**
 * Tests import/export of extension DTOs.
 */
public class ImportExportDTOExtensionTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private ExportContext exportContext;
	private ImportContext importContext;
	private PersistenceEngine persistenceEngine;

	/** Test initialization. */
	@Before
	public void before() {
		context.setImposteriser(ClassImposteriser.INSTANCE);

		exportContext = context.mock(ExportContext.class);
		persistenceEngine = context.mock(PersistenceEngine.class);

		final ImportConfiguration configuration = context.mock(ImportConfiguration.class);
		importContext = new ImportContext(configuration);

		final Summary summary = context.mock(Summary.class);
		importContext.setSummary(summary);

		context.checking(new Expectations() {
			{
				allowing(exportContext);
				allowing(configuration);
				allowing(summary);

				PersistenceSession session = context.mock(PersistenceSession.class);
				allowing(persistenceEngine).getPersistenceSession();
				will(returnValue(session));
				allowing(persistenceEngine).getSharedPersistenceSession();
				will(returnValue(session));

				Transaction transaction = context.mock(Transaction.class);
				allowing(session).beginTransaction();
				will(returnValue(transaction));
				allowing(session);

				allowing(transaction).rollback();
				will(new CustomAction("Failed due to rollback") {
					@Override
					public Object invoke(final Invocation invocation) throws Throwable {
						fail("Internal server error (rollback detected), check logs");
						return null;
					}
				});
				allowing(transaction);
			}
		});
	}

	/**
	 * Full round-trip with an original (unextended) object. This is merely to prove that we can round-trip with our
	 * test objects.
	 *
	 * @throws Exception in case of errors
	 */
	@Test
	public void testImportExportNoExtension() throws Exception {
		final ExamplePersistence persistence = new ExamplePersistence();
		persistence.setName("some name");

		final Exporter exporter = new ExampleDtoExporter(persistence);
		exporter.initialize(exportContext);
		ExportEntry entry = exporter.executeExport();

		@SuppressWarnings("unchecked")
		final SavingStrategy<ExamplePersistence, ExampleDTO> strategy = context.mock(SavingStrategy.class);
		context.checking(new Expectations() {
			{
				allowing(strategy).saveDomainObject(with(new TypeSafeMatcher<ExamplePersistence>() {
					@Override
					public void describeTo(final Description description) {
						description.appendText("ExampleDTO equal to ExamplePersistence");
					}

					@Override
					public boolean matchesSafely(final ExamplePersistence object) {
						if (object == null) {
							return false;
						}
						return persistence.getName().equals(object.getName());
					}
				}));
				allowing(strategy);
			}
		});

		final ExampleDtoImporter importer = new ExampleDtoImporter();
		importer.setSavingStrategy(strategy);
		importer.setStatusHolder(new ImportStatusHolder());
		importer.setProcessedObjectGuids(new HashSet<String>());

		final ImporterFactory importerFactory = context.mock(ImporterFactory.class);
		ImportProcessorImpl processor = new ImportProcessorImpl();
		processor.setImporterFactory(importerFactory);
		processor.setPersistenceEngine(persistenceEngine);

		context.checking(new Expectations() {
			{
				allowing(importerFactory).createImporter(exporter.getJobType(), importContext, null);
				will(returnValue(importer));
			}
		});

		processor.process(entry.getInputStream(), importContext);
	}

	/**
	 * Full round-trip with an extension object. {@link ChangeSetImportStageImpl} does things differently than
	 * the {@link ImportProcessorImpl}.
	 *
	 * @throws Exception in case of errors
	 */
	@Test
	public void testChangeSetExportExtension() throws Exception {
		final ExamplePersistenceExt extensionPersistence = new ExamplePersistenceExt();
		extensionPersistence.setName("another name");
		extensionPersistence.setCode("another code");


		final Exporter exporter = new ExampleExtDtoExporter(extensionPersistence);
		exporter.initialize(exportContext);
		ExportEntry entry = exporter.executeExport();

		@SuppressWarnings("unchecked")
		final SavingStrategy<ExamplePersistence, ExampleDTO> strategy = context.mock(SavingStrategy.class);
		context.checking(new Expectations() {
			{
				allowing(strategy).populateAndSaveObject(with(notNullValue(ExamplePersistence.class)),
						with(new TypeSafeMatcher<ExampleExtDTO>() {
							@Override
							public void describeTo(final Description description) {
								description.appendText("ExampleExtDTO equal to ExamplePersistenceExt");
							}

							@Override
							public boolean matchesSafely(final ExampleExtDTO dto) {
								if (dto == null) {
									return false;
								}
								boolean equals = true;
								equals &= extensionPersistence.getCode().equals(dto.getCode());
								equals &= extensionPersistence.getName().equals(dto.getName());
								return equals;
							}
						}));

				allowing(strategy).isImportRequired(with(any(ExamplePersistence.class)));
				will(returnValue(true));
			}
		});

		final BusinessObjectDescriptorLocator businessObjectDescriptorLocator = context.mock(BusinessObjectDescriptorLocator.class);
		final RelatedObjectsResolver<?, ?> relatedObjectsResolver = context.mock(RelatedObjectsResolver.class);
		final TimeService timeService = context.mock(TimeService.class);
		final ChangeSetService changeSetService = context.mock(ChangeSetService.class);
		context.checking(new Expectations() {
			{
				allowing(businessObjectDescriptorLocator);
				allowing(relatedObjectsResolver);
				allowing(timeService);
				allowing(changeSetService);
			}
		});

		final ExampleExtDtoImporter importer = new ExampleExtDtoImporter();
		importer.setSavingStrategy(strategy);
		importer.setStatusHolder(new ImportStatusHolder());
		importer.setProcessedObjectGuids(new HashSet<String>());

		final ImporterFactory importerFactory = context.mock(ImporterFactory.class);
		ChangeSetImportStageImpl processor = new ChangeSetImportStageImpl();
		processor.setImporterFactory(importerFactory);
		processor.setPersistenceEngine(persistenceEngine);
		processor.setMetadataMap(new ThreadLocalMap<String, Object>());
		processor.setBusinessObjectDescriptorLocator(businessObjectDescriptorLocator);
		processor.setTimeService(timeService);
		processor.setChangeSetService(changeSetService);

		Map<Class<? extends Dto>, RelatedObjectsResolver<? extends Persistable, ? extends Dto>> relatedObjectsResolversMap =
				Collections.<Class<? extends Dto>, RelatedObjectsResolver<? extends Persistable, ? extends Dto>> singletonMap(
						ExampleExtDTO.class,
						relatedObjectsResolver);
		processor.setRelatedObjectsResolvers(relatedObjectsResolversMap);

		context.checking(new Expectations() {
			{
				allowing(importerFactory).createImporter(exporter.getJobType(), importContext, null);
				will(returnValue(importer));
			}
		});

		processor.execute(entry.getInputStream(), importContext);
	}

	/**
	 * Full round-trip using an extension object. This case tests {@link ImportProcessorImpl}.
	 *
	 * @throws Exception in case of errors
	 */
	@Test
	public void testImportExportExtended() throws Exception {
		final ExamplePersistenceExt extensionPersistence = new ExamplePersistenceExt();
		extensionPersistence.setName("another name");
		extensionPersistence.setCode("another code");


		final Exporter exporter = new ExampleExtDtoExporter(extensionPersistence);
		exporter.initialize(exportContext);
		ExportEntry entry = exporter.executeExport();

		@SuppressWarnings("unchecked")
		final SavingStrategy<ExamplePersistence, ExampleDTO> strategy = context.mock(SavingStrategy.class);
		context.checking(new Expectations() {
			{
				allowing(strategy).populateAndSaveObject(with(notNullValue(ExamplePersistence.class)),
						with(new TypeSafeMatcher<ExampleExtDTO>() {
							@Override
							public void describeTo(final Description description) {
								description.appendText("ExampleExtDTO equal to ExamplePersistenceExt");
							}

							@Override
							public boolean matchesSafely(final ExampleExtDTO dto) {
								if (dto == null) {
									return false;
								}
								boolean equals = true;
								equals &= extensionPersistence.getCode().equals(dto.getCode());
								equals &= extensionPersistence.getName().equals(dto.getName());
								return equals;
							}
						}));

				allowing(strategy).isImportRequired(with(any(ExamplePersistence.class)));
				will(returnValue(true));
			}
		});

		final ExampleExtDtoImporter importer = new ExampleExtDtoImporter();
		importer.setSavingStrategy(strategy);
		importer.setStatusHolder(new ImportStatusHolder());
		importer.setProcessedObjectGuids(new HashSet<String>());

		final ImporterFactory importerFactory = context.mock(ImporterFactory.class);
		ImportProcessorImpl processor = new ImportProcessorImpl();
		processor.setImporterFactory(importerFactory);
		processor.setPersistenceEngine(persistenceEngine);

		context.checking(new Expectations() {
			{
				allowing(importerFactory).createImporter(exporter.getJobType(), importContext, null);
				will(returnValue(importer));
			}
		});

		processor.process(entry.getInputStream(), importContext);
	}
}
