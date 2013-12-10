package com.elasticpath.importexport.common.adapters.settings;

import static org.junit.Assert.assertEquals;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.ElasticPath;
import com.elasticpath.importexport.common.dto.settings.DefinedValueDTO;
import com.elasticpath.settings.domain.SettingValue;
import com.elasticpath.settings.domain.impl.SettingValueImpl;

/**
 * Tests domain and DTO population by <code>SettingValueAdapter</code>.
 */
public class SettingValueAdapterTest {

	private static final String CONTEXT = "SNAPITUP";
	
	private static final String VALUE = "kayaks,sunset";
	
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private final SettingValueAdapter settingValueAdapter = new SettingValueAdapter();
	
	private final ElasticPath elasticpath = context.mock(ElasticPath.class);
	
	private final SettingValue settingValue = context.mock(SettingValue.class);
	
	/**
	 * Sets Up Test Case.
	 */
	@Before
	public void setUp() {
		settingValueAdapter.setElasticPath(elasticpath);
	}
	
	/**
	 * Tests population of <code>DefinedValueDTO</code>.
	 */
	@Test
	public void testPopulateDTO() {
		context.checking(new Expectations() { {
			oneOf(settingValue).getContext(); will(returnValue(CONTEXT));
			oneOf(settingValue).getValue(); will(returnValue(VALUE));
		} });

		final DefinedValueDTO definedValueDTO = new DefinedValueDTO();
		settingValueAdapter.populateDTO(settingValue, definedValueDTO);

		assertEquals(CONTEXT, definedValueDTO.getContext());
		assertEquals(VALUE, definedValueDTO.getValue());
	}
	
	/**
	 * Tests population from <code>DefinedValueDTO</code>.
	 */
	@Test
	public void testPopulateDomain() {
		context.checking(new Expectations() { {
			oneOf(settingValue).setContext(CONTEXT);
			oneOf(settingValue).setValue(VALUE);
		} });

		final DefinedValueDTO definedValueDTO = new DefinedValueDTO();
		definedValueDTO.setContext(CONTEXT);
		definedValueDTO.setValue(VALUE);
		
		settingValueAdapter.populateDomain(definedValueDTO, settingValue);
	}

	/**
	 * Class of DTO object populated by <code>SettingValueAdapter</code> is <code>DefinedValueDTO</code>.
	 * Don't mix up with <code>DefaultValueDTO</code> because these two objects are populated differently
	 */
	@Test
	public void testCreateDtoObject() {
		assertEquals(DefinedValueDTO.class, settingValueAdapter.createDtoObject().getClass());
	}
	
	/**
	 * Tests createDomainObject.
	 */
	@Test
	public void testCreateDomainObjct() {
		context.checking(new Expectations() { {
			oneOf(elasticpath).getBean("settingValue"); will(returnValue(new SettingValueImpl()));
		} });
		assertEquals(SettingValueImpl.class, settingValueAdapter.createDomainObject().getClass());
	}
}
