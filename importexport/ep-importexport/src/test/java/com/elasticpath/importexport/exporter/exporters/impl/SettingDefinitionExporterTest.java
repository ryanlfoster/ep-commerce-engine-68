package com.elasticpath.importexport.exporter.exporters.impl;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

import com.elasticpath.importexport.common.dto.settings.DefinedValueDTO;
import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.common.summary.Summary;
import com.elasticpath.importexport.exporter.configuration.ExportConfiguration;
import com.elasticpath.importexport.exporter.configuration.search.SearchConfiguration;
import com.elasticpath.importexport.exporter.context.ExportContext;
import com.elasticpath.importexport.exporter.search.ImportExportSearcher;
import com.elasticpath.ql.custom.setting.SettingResult;
import com.elasticpath.ql.parser.EPQueryType;
import com.elasticpath.settings.SettingsService;
import com.elasticpath.settings.domain.SettingValue;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.dto.settings.SettingDTO;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.settings.domain.SettingDefinition;

/**
 * Tests for SettingDefinitionExporter.
 */
public class SettingDefinitionExporterTest {
	
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private final SettingDefinitionExporter settingDefinitionExporter = new SettingDefinitionExporter();
	
	@SuppressWarnings("unchecked")
	private final DomainAdapter<SettingDefinition, SettingDTO> settingDefinitionAdapter = context.mock(DomainAdapter.class,
			"settingDefinitionAdapter");

	@SuppressWarnings("unchecked")
	private final DomainAdapter<SettingValue, DefinedValueDTO> settingValueAdapter = context.mock(DomainAdapter.class, "settingValueAdapter");

	private ImportExportSearcher importExportSearcher;
	private SettingsService settingsService;
	/**
	 * Sets up Test Case.
	 * 
	 * @throws Exception if any problem
	 */
	@Before
	public void setUp() throws Exception {
		settingDefinitionExporter.setSettingDefinitionAdapter(settingDefinitionAdapter);

		importExportSearcher = context.mock(ImportExportSearcher.class);
		settingDefinitionExporter.setImportExportSearcher(importExportSearcher);

		settingsService = context.mock(SettingsService.class);
		settingDefinitionExporter.setSettingsService(settingsService);

		settingDefinitionExporter.setSettingValueAdapter(settingValueAdapter);
	}

	/**
	 * Tests getDomainAdapter.
	 */
	@Test
	public void testGetDomainAdapter() {
		assertEquals(settingDefinitionAdapter, settingDefinitionExporter.getDomainAdapter());
	}

	/**
	 * Tests getDtoClass.
	 */
	@Test
	public void testGetDtoClass() {
		assertEquals(SettingDTO.class, settingDefinitionExporter.getDtoClass());
	}

	/**
	 * Tests getJobType.
	 */
	@Test
	public void testGetJobType() {
		assertEquals(JobType.SYSTEMCONFIGURATION, settingDefinitionExporter.getJobType());
	}

	/**
	 * Tests getDependentClasses.
	 */
	@Test
	public void testGetDependentClasses() {
		assertEquals(0, settingDefinitionExporter.getDependentClasses().length);
	}

	private SettingValue mockSettingValue(final String name, final String settingPath, final String settingContext, final boolean persistent) {
		final SettingValue settingValue = context.mock(SettingValue.class, name);

		context.checking(new Expectations() {
			{
				one(settingsService).getSettingValue(settingPath, settingContext);
				will(returnValue(settingValue));
				allowing(settingValue).isPersisted();
				will(returnValue(persistent));
			}
		});

		return settingValue;
	}

	/**
	 * Test an export of {@link SettingValue}s and {@link SettingDefinition}s.
	 * 
	 * @throws ConfigurationException in case of errors
	 */
	@Test
	public void testFullExport() throws ConfigurationException {
		final Summary summary = context.mock(Summary.class);
		final SearchConfiguration searchConfiguration = new SearchConfiguration();
		ExportContext exportContext = new ExportContext(new ExportConfiguration(), searchConfiguration);
		exportContext.setSummary(summary);

		final String settingPath1 = "1";
		final String settingPath2 = "2";
		final String settingContext1 = "b";
		final String settingContext2 = "c";
		final String settingContext3 = "d";
		final String settingContext4 = "e";
		final List<SettingResult> searchResults = new ArrayList<SettingResult>();
		searchResults.add(new SettingResult(settingPath1, settingContext1));
		searchResults.add(new SettingResult(settingPath2, settingContext2));
		searchResults.add(new SettingResult(settingPath2, settingContext3));
		searchResults.add(new SettingResult(settingPath2, settingContext4));

		final long settingDefinition1Uid = 551;
		final long settingDefinition2Uid = 8881;

		context.checking(new Expectations() {
			{
				allowing(importExportSearcher).searchCompoundGuids(searchConfiguration, EPQueryType.CONFIGURATION);
				will(returnValue(searchResults));

				SettingDefinition settingDefinition1 = context.mock(SettingDefinition.class, "settingDefinition-1");
				allowing(settingDefinition1).getUidPk();
				will(returnValue(settingDefinition1Uid));
				allowing(settingDefinition1).getPath();
				will(returnValue(settingPath1));

				SettingDefinition settingDefinition2 = context.mock(SettingDefinition.class, "settingDefinition-2");
				allowing(settingDefinition2).getUidPk();
				will(returnValue(settingDefinition2Uid));
				allowing(settingDefinition2).getPath();
				will(returnValue(settingPath2));

				one(settingsService).getSettingDefinition(settingPath1);
				will(returnValue(settingDefinition1));
				one(settingsService).getSettingDefinition(settingPath2);
				will(returnValue(settingDefinition2));

				SettingValue settingValue1 = mockSettingValue("settinValue-1", settingPath1, settingContext1, true);
				SettingValue settingValue2 = mockSettingValue("settinValue-2", settingPath2, settingContext2, true);
				mockSettingValue("settinValue-3", settingPath2, settingContext3, false);
				SettingValue settingValue4 = mockSettingValue("settinValue-4", settingPath2, settingContext4, true);

				SettingDTO settingDto = new SettingDTO();
				allowing(settingDefinitionAdapter).createDtoObject();
				will(returnValue(settingDto));
				one(settingDefinitionAdapter).populateDTO(settingDefinition1, settingDto);
				one(settingDefinitionAdapter).populateDTO(settingDefinition2, settingDto);

				DefinedValueDTO definedValueDto = new DefinedValueDTO();
				allowing(settingValueAdapter).createDtoObject();
				will(returnValue(definedValueDto));
				one(settingValueAdapter).populateDTO(settingValue1, definedValueDto);
				one(settingValueAdapter).populateDTO(settingValue2, definedValueDto);
				// settingValue3 is not persisted, hence not exported
				one(settingValueAdapter).populateDTO(settingValue4, definedValueDto);

				// if you're missing this one, the object failed to export (exceptions are silently caught)
				// 2 unique definitions (3 unique paths)
				exactly(2).of(summary).addToCounter(settingDefinitionExporter.getJobType(), 1);
			}
		});
		settingDefinitionExporter.initialize(exportContext);

		settingDefinitionExporter.processExport(System.out);
	}
}
