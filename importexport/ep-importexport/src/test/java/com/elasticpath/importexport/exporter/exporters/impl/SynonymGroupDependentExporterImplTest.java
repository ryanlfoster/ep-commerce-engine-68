package com.elasticpath.importexport.exporter.exporters.impl;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.search.SynonymGroup;
import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.exporter.configuration.ExportConfiguration;
import com.elasticpath.importexport.exporter.configuration.search.SearchConfiguration;
import com.elasticpath.importexport.exporter.context.DependencyRegistry;
import com.elasticpath.importexport.exporter.context.ExportContext;
import com.elasticpath.importexport.exporter.exporters.DependentExporterFilter;
import com.elasticpath.service.search.SynonymGroupService;

/**
 * Test for {@link SynonymGroupDependentExporterImpl}.
 */
@RunWith(JMock.class)
@SuppressWarnings("PMD.NonStaticInitializer")
public class SynonymGroupDependentExporterImplTest {
	private final SynonymGroupDependentExporterImpl synonymGroupExporter = new SynonymGroupDependentExporterImpl();
	private SynonymGroupService synonymGroupService;
	private DependentExporterFilter dependentExporterFilter;
	private ExportContext exportContext;
	private final Mockery context = new JUnit4Mockery();
	private static final long CATALOG_UID = 14441;

	/**
	 * Test initialization.
	 * 
	 * @throws ConfigurationException in case of errors
	 */
	@Before
	public void setUp() throws ConfigurationException {
		synonymGroupService = context.mock(SynonymGroupService.class);
		synonymGroupExporter.setSynonymGroupService(synonymGroupService);

		dependentExporterFilter = context.mock(DependentExporterFilter.class);
		exportContext = new ExportContext(new ExportConfiguration(), new SearchConfiguration());
		synonymGroupExporter.initialize(exportContext, dependentExporterFilter);
	}

	/** Tests finding dependent objects when the dependent object should be filtered. */
	@Test
	public void testFindDependentObjectsFiltered() {
		SynonymGroup synonymGroup1 = context.mock(SynonymGroup.class, "synonymGroup-1");
		SynonymGroup synonymGroup2 = context.mock(SynonymGroup.class, "synonymGroup-2");
		final List<SynonymGroup> synonymGroupList = Arrays.asList(synonymGroup1, synonymGroup2);
		context.checking(new Expectations() {
			{
				one(dependentExporterFilter).isFiltered(CATALOG_UID);
				will(returnValue(true));

				one(synonymGroupService).findAllSynonymGroupForCatalog(CATALOG_UID);
				will(returnValue(synonymGroupList));
			}
		});

		assertEquals(synonymGroupList, synonymGroupExporter.findDependentObjects(CATALOG_UID));
	}

	/** Tests finding dependent objects when the dependent object should be filtered. */
	@Test
	public void testFindDependentObjectsNotFiltered() {
		DependencyRegistry registry = new DependencyRegistry(Arrays.<Class<?>> asList(SynonymGroup.class));
		exportContext.setDependencyRegistry(registry);

		final long synonymGroup1Uid = 91;
		final long synonymGroup2Uid = 454111;
		final long synonymGroup3Uid = 4;
		registry.addUidDependency(SynonymGroup.class, synonymGroup1Uid);
		registry.addUidDependencies(SynonymGroup.class, new HashSet<Long>(Arrays.asList(synonymGroup2Uid, synonymGroup3Uid)));

		final SynonymGroup synonymGroup1 = context.mock(SynonymGroup.class, "synonymGroup-1");
		final SynonymGroup synonymGroup2 = context.mock(SynonymGroup.class, "synonymGroup-2");
		final SynonymGroup synonymGroup3 = context.mock(SynonymGroup.class, "synonymGroup-3");
		context.checking(new Expectations() {
			{
				one(dependentExporterFilter).isFiltered(CATALOG_UID);
				will(returnValue(false));

				Catalog dependentCatalog = context.mock(Catalog.class, "dependentCatalog");
				Catalog otherCatalog = context.mock(Catalog.class, "otherCatalog");
				allowing(dependentCatalog).getUidPk();
				will(returnValue(CATALOG_UID));
				allowing(otherCatalog).getUidPk();
				will(returnValue(0L));

				allowing(synonymGroup1).getCatalog();
				will(returnValue(dependentCatalog));
				allowing(synonymGroup2).getCatalog();
				will(returnValue(otherCatalog));
				allowing(synonymGroup3).getCatalog();
				will(returnValue(dependentCatalog));

				one(synonymGroupService).getObject(synonymGroup1Uid);
				will(returnValue(synonymGroup1));
				one(synonymGroupService).getObject(synonymGroup2Uid);
				will(returnValue(synonymGroup2));
				one(synonymGroupService).getObject(synonymGroup3Uid);
				will(returnValue(synonymGroup3));
			}
		});

		List<SynonymGroup> result = synonymGroupExporter.findDependentObjects(CATALOG_UID);
		assertThat("Missing synonymGroup-1", result, hasItem(synonymGroup1));
		assertThat("Missing synonymGroup-3", result, hasItem(synonymGroup3));
		assertThat("synonymGroup-2 is not a part of this catalog", result, not(hasItem(synonymGroup2)));
		assertEquals("Other SynonymGroups returned?", 2, result.size());
	}
}
