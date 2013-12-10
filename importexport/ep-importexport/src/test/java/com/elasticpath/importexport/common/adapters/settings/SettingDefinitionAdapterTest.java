package com.elasticpath.importexport.common.adapters.settings;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.ElasticPath;
import com.elasticpath.importexport.common.dto.settings.DefaultValueDTO;
import com.elasticpath.importexport.common.dto.settings.MetadataDTO;
import com.elasticpath.importexport.common.dto.settings.SettingDTO;
import com.elasticpath.settings.domain.SettingDefinition;
import com.elasticpath.settings.domain.SettingMetadata;
import com.elasticpath.settings.domain.impl.SettingDefinitionImpl;
import com.elasticpath.settings.domain.impl.SettingMetadataImpl;

/**
 * Tests population of DTO and domain objects by <code>SettingDefinitionAdapter</code>.
 */
public class SettingDefinitionAdapterTest {

	private static final String DEFAULT_VALUE_VALUE = "/assets";

	private static final String DEFAULT_VALUE_TYPE = "String";

	private static final int MAXIMUM_OVERRIDES = 1;

	private static final String DESCRIPTION = "The path to the location of global assets.";

	private static final String NAMESPACE = "COMMERCE/SYSTEM/ASSETS/assetLocation";

	private static final String VALUE = "true";

	private static final String ADDITIONAL_METADATA = "additionalMetadata";

	private static final String AVAILABLE_TO_MARKETING = "availableToMarketing";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private final SettingDefinitionAdapter settingDefinitionAdapter = new SettingDefinitionAdapter();
	
	private final ElasticPath elasticpath = context.mock(ElasticPath.class);

	/**
	 * Sets Up Test Case.
	 */
	@Before
	public void setUp() {
		settingDefinitionAdapter.setElasticPath(elasticpath);
	}
	
	/**
	 * Tests creation and population of <code>DefaultValueDTO</code> object as a part of <code>SettingDTO</code> state.
	 */
	@Test
	public void testCreateDefaultValueDTO() {
		final SettingDefinition settingDefinition = context.mock(SettingDefinition.class);
		final String defaultValueType = DEFAULT_VALUE_TYPE;
		final String defaultValue = "storeassets";

		context.checking(new Expectations() { {
			oneOf(settingDefinition).getValueType(); will(returnValue(defaultValueType));
			oneOf(settingDefinition).getDefaultValue(); will(returnValue(defaultValue));
		} });

		DefaultValueDTO defaultValueDTO = settingDefinitionAdapter.createDefaultValueDTO(settingDefinition);

		assertEquals(defaultValue, defaultValueDTO.getValue());
		assertEquals(defaultValueType, defaultValueDTO.getType());
	}

	/**
	 * Tests creation and population of <code>MetadataDTO</code> object as a part of <code>SettingDTO</code> state.
	 */
	@Test
	public void testCreateMetadataDTO() {
		final SettingMetadata settingMetadata = context.mock(SettingMetadata.class);
		final String metadataKey = AVAILABLE_TO_MARKETING;
		final String metadataValue = VALUE;

		context.checking(new Expectations() { {
			oneOf(settingMetadata).getKey(); will(returnValue(metadataKey));
			oneOf(settingMetadata).getValue(); will(returnValue(metadataValue));
		} });

		MetadataDTO metadataDto = settingDefinitionAdapter.createMetadataDTO(settingMetadata);

		assertEquals(metadataKey, metadataDto.getKey());
		assertEquals(metadataValue, metadataDto.getValue());
	}

	/**
	 * Checks that list of <code>MetadataDTO</code> objects is objects is created properly.
	 */
	@Test
	public void testCreateMetadataDTOList() {
		final SettingMetadata settingMetadata1 = new SettingMetadataImpl();
		final SettingMetadata settingMetadata2 = new SettingMetadataImpl();
		final Map<String, SettingMetadata> metadata = new HashMap<String, SettingMetadata>();
		metadata.put(AVAILABLE_TO_MARKETING, settingMetadata1);
		metadata.put(ADDITIONAL_METADATA, settingMetadata2);

		final SettingDefinition settingDefinition = context.mock(SettingDefinition.class);

		context.checking(new Expectations() { {
			oneOf(settingDefinition).getMetadata(); will(returnValue(metadata));
		} });

		final List<MetadataDTO> metadataDtoList = settingDefinitionAdapter.createMetadataDTOList(settingDefinition);

		assertEquals(2, metadataDtoList.size());
	}

	/**
	 * Tests population of <code>SettingDTO</code> in the assumption that all other methods are implemented correctly.
	 */
	@Test
	public void testPopulateDTO() {
		final SettingDefinitionAdapter settingDefinitionAdapter = new SettingDefinitionAdapter() {
			@Override
			List<MetadataDTO> createMetadataDTOList(final SettingDefinition source) {
				return null;
			}
			@Override
			DefaultValueDTO createDefaultValueDTO(final SettingDefinition source) {
				return null;
			}
		};

		final SettingDefinition settingDefinition = context.mock(SettingDefinition.class);
		final String settingNameSpace = NAMESPACE;
		final String settingDescription = DESCRIPTION;
		final int maxOverrides = MAXIMUM_OVERRIDES;

		context.checking(new Expectations() { {
			oneOf(settingDefinition).getPath(); will(returnValue(settingNameSpace));
			oneOf(settingDefinition).getDescription(); will(returnValue(settingDescription));
			oneOf(settingDefinition).getMaxOverrideValues(); will(returnValue(maxOverrides));
		} });

		final SettingDTO settingDto = new SettingDTO();

		settingDefinitionAdapter.populateDTO(settingDefinition, settingDto);

		assertEquals(settingNameSpace, settingDto.getNameSpace());
		assertEquals(settingDescription, settingDto.getDescription());
		assertEquals(maxOverrides, settingDto.getMaximumOverrides());
	}
	
	/**
	 * Tests populateDomain.
	 */
	@Test
	public void testPopulateDomain() {
		// Expectation
		final SettingDefinitionAdapter settingDefinitionAdapter = new SettingDefinitionAdapter() {
			@Override
			Map<String, SettingMetadata> createSettingMetadataMap(final List<MetadataDTO> metadataValues) {
				return Collections.emptyMap();
			}
		};
		
		// Checking
		final SettingDefinition settingDefinition = context.mock(SettingDefinition.class);
		context.checking(new Expectations() { {
			oneOf(settingDefinition).setPath(NAMESPACE);
			oneOf(settingDefinition).setDescription(DESCRIPTION);
			oneOf(settingDefinition).setMaxOverrideValues(MAXIMUM_OVERRIDES);
			oneOf(settingDefinition).setValueType(DEFAULT_VALUE_TYPE);
			oneOf(settingDefinition).setDefaultValue(DEFAULT_VALUE_VALUE);
			atLeast(1).of(settingDefinition).getMetadata(); will(returnValue(Collections.emptyMap()));
			oneOf(settingDefinition).setMetadata(Collections.<String, SettingMetadata>emptyMap());
		} });
		
		// Test Data
		SettingDTO settingDTO = new SettingDTO();
		settingDTO.setNameSpace(NAMESPACE);
		settingDTO.setDescription(DESCRIPTION);
		settingDTO.setMaximumOverrides(MAXIMUM_OVERRIDES);
		settingDTO.setDefaultValue(createDefaultValue(DEFAULT_VALUE_TYPE, DEFAULT_VALUE_VALUE));
		settingDTO.setMetadataValues(Collections.<MetadataDTO>emptyList());
		
		// Result
		settingDefinitionAdapter.populateDomain(settingDTO, settingDefinition);
	}
	
	private DefaultValueDTO createDefaultValue(final String type,	final String value) {
		DefaultValueDTO defaultValueDTO = new DefaultValueDTO();
		defaultValueDTO.setType(type);
		defaultValueDTO.setValue(value);
		
		return defaultValueDTO;
	}

	/**
	 * Test createSettingMetadataMap.
	 */
	@Test
	public void testCreateSettingMetadataMap() {
		// Expectation
		final SettingMetadata settingMetadata = context.mock(SettingMetadata.class);
		final SettingDefinitionAdapter settingDefinitionAdapter = new SettingDefinitionAdapter() {
			@Override
			SettingMetadata createSettingMetaData(final MetadataDTO metadataDTO) {
				settingMetadata.setKey(metadataDTO.getKey());
				settingMetadata.setValue(metadataDTO.getValue());
				return settingMetadata;
			}
		};		

		// Checking
		context.checking(new Expectations() { {			
			oneOf(settingMetadata).setKey(AVAILABLE_TO_MARKETING);
			oneOf(settingMetadata).setValue(VALUE);
		} });

		// Test Data
		final List<MetadataDTO> metadataList = new ArrayList<MetadataDTO>();
		metadataList.add(createMetaDataDTO(AVAILABLE_TO_MARKETING, VALUE));
		
		// Result
		final Map<String, SettingMetadata> map = settingDefinitionAdapter.createSettingMetadataMap(metadataList);
		
		// Check Result
		assertEquals(1, map.size());
		assertEquals(settingMetadata, map.get(AVAILABLE_TO_MARKETING));
	}	

	/**
	 * Tests createSettingMetaData.
	 */
	@Test
	public void testCreateSettingMetaData() {
		MetadataDTO metadataDTO = createMetaDataDTO(AVAILABLE_TO_MARKETING, VALUE);
		
		final SettingMetadata settingMetadata = context.mock(SettingMetadata.class);
		context.checking(new Expectations() { {
			oneOf(elasticpath).getBean(ContextIdNames.SETTING_METADATA); will(returnValue(settingMetadata));
			oneOf(settingMetadata).setKey(AVAILABLE_TO_MARKETING);
			oneOf(settingMetadata).setValue(VALUE);
		} });
		
		assertEquals(settingMetadata, settingDefinitionAdapter.createSettingMetaData(metadataDTO));
	}

	private MetadataDTO createMetaDataDTO(final String key, final String value) {
		MetadataDTO metadataDTO = new MetadataDTO();
		
		metadataDTO.setKey(key);
		metadataDTO.setValue(value);
		
		return metadataDTO;
	}
	
	/**
	 * Tests createDtoObject.
	 */
	@Test
	public void testCreateDtoObject() {
		assertEquals(SettingDTO.class, settingDefinitionAdapter.createDtoObject().getClass());
	}
	
	/**
	 * Tests createDomainObject.
	 */
	@Test
	public void testCreateDomainObjct() {
		context.checking(new Expectations() { {
			oneOf(elasticpath).getBean(ContextIdNames.SETTING_DEFINITION); will(returnValue(new SettingDefinitionImpl()));
		} });
		assertEquals(SettingDefinitionImpl.class, settingDefinitionAdapter.createDomainObject().getClass());
	}
}
