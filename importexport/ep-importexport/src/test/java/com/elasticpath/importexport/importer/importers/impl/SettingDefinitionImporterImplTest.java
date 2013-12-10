package com.elasticpath.importexport.importer.importers.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.dto.settings.SettingDTO;
import com.elasticpath.settings.SettingsService;
import com.elasticpath.settings.domain.SettingDefinition;

/**
 * Tests SettingDefinitionImporterImpl.
 */
public class SettingDefinitionImporterImplTest {

	private static final String NAME_SPACE = "/NAME/SPACE";
	
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	
	private final SettingDefinitionImporterImpl settingDefinitionImporter = new SettingDefinitionImporterImpl();

	@SuppressWarnings("unchecked")
	private final DomainAdapter<SettingDefinition, SettingDTO> settingDefinitionAdapter = context.mock(DomainAdapter.class);
	
	private final SettingsService settingsService = context.mock(SettingsService.class);

	private SettingDTO createSettingDTO(final String nameSpace) {
		SettingDTO settingDto = new SettingDTO();
		settingDto.setNameSpace(nameSpace);
		
		return settingDto;
	}
	
	/**
	 * Sets Up Test Case.
	 */
	@Before
	public void setUp() {
		settingDefinitionImporter.setSettingDefinitionAdapter(settingDefinitionAdapter);
		settingDefinitionImporter.setSettingsService(settingsService);
	}

	/**
	 * Tests findPersistentObject.
	 */
	@Test
	public void testFindPersistentObjectSettingDTO() {
		context.checking(new Expectations() { {
			oneOf(settingsService).getSettingDefinition(NAME_SPACE);
		} });
		
		settingDefinitionImporter.findPersistentObject(createSettingDTO(NAME_SPACE));
		
	}

	/**
	 * Tests getDomainAdapter.
	 */
	@Test
	public void testGetDomainAdapter() {
		assertEquals(settingDefinitionAdapter, settingDefinitionImporter.getDomainAdapter());
	}

	/**
	 * Tests getDtoGuid.
	 */
	@Test
	public void testGetDtoGuidSettingDTO() {
		assertEquals(NAME_SPACE, settingDefinitionImporter.getDtoGuid(createSettingDTO(NAME_SPACE)));
	}

	/**
	 * Tests getImportedObjectName.
	 */
	@Test
	public void testGetImportedObjectName() {
		assertEquals(SettingDTO.ROOT_ELEMENT, settingDefinitionImporter.getImportedObjectName());
	}

	/** The import classes should at least contain the DTO class we are operating on. */
	@Test
	public void testDtoClass() {
		assertEquals("Incorrect DTO class", SettingDTO.class, settingDefinitionImporter.getDtoClass());
	}

	/** The auxiliary JAXB class list must not be null (can be empty). */
	@Test
	public void testAuxiliaryJaxbClasses() {
		assertNotNull(settingDefinitionImporter.getAuxiliaryJaxbClasses());
	}
}
