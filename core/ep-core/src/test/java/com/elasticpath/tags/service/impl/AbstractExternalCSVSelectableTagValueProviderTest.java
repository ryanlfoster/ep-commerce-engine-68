package com.elasticpath.tags.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Locale;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.ResourceUtils;

import com.elasticpath.tags.domain.SelectableValue;

/**
 * 
 * Test for AbstractSelectableInternalTagValueServiceImpl.
 *
 */
@SuppressWarnings({ "PMD.TooManyStaticImports" })
public class AbstractExternalCSVSelectableTagValueProviderTest {
	
	
	private static final String ONE = "one";
	
	/**
	 * 
	 * Test provider class.
	 *
	 */
	class IntegerExternalCSVSelectableTagValueProvider extends AbstractExternalCSVSelectableTagValueProvider<Integer> {

		@Override
		protected Integer adaptString(final String stringValue) {
			return Integer.valueOf(stringValue);
		}
		
		/**
		 * Get input stream for specified locale.  
		 * @param locale specified locale.
		 * @return input stream for specified locale or null if can get input stream for specified or defalut. 
		 */
		InputStream getInputStream(final Locale locale) {
			if (enLocale.equals(locale)) {
				return new ByteArrayInputStream(enCSV.getBytes());
			} else if (frLocale.equals(locale)) {
				return new ByteArrayInputStream(frCSV.getBytes());
			}
			return new ByteArrayInputStream(enCSV.getBytes());
		}
		
		
	}
	
		
	private IntegerExternalCSVSelectableTagValueProvider provider;
	
	private final Locale enLocale = new Locale("en");
	
	private final Locale frLocale = new Locale("fr");
	
	private final String enCSV = "Integer,Description\n1,one\n2,two"; //NOPMD
	
	private final String frCSV = "frInteger,frDescription"; //NOPMD
	
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	
	/**
	 * Initializing test.
	 */
	@Before	
	public void setUp() {		
		provider = new IntegerExternalCSVSelectableTagValueProvider();
		
	}
	
	/**
	 * Test, row acceptance validation function.  
	 */
	@Test
	public void testRowValidation() {
		provider.setNameFieldIndex(1);
		provider.setValueFieldIndex(2);		
		String [] line = new String[] { // the same fashion as from CSVReader.
				"1,2,3",
				"1",
				"2",
				"3",
		};		
		assertTrue(provider.isValid(line));
		
		provider.setValueFieldIndex(2 + 1);
		assertTrue(provider.isValid(line));
		
		provider.setValueFieldIndex(2 + 2);
		assertFalse(provider.isValid(line));
		
	}
	
	/**
	 * Test, creation of single selectable value.  
	 */
	@Test
	public void testSelectableValueCreation() {
		provider.setNameFieldIndex(2);
		provider.setValueFieldIndex(1);		
		String [] line = new String[] { // the same fashion as from CSVReader.
				"1,2,3",
				"1",
				"2",
				"3",
		};
		
		SelectableValue<Integer> selectableValue = provider.createSelectableValue(line);
		
		assertEquals(1, selectableValue.getValue().intValue());
		
		assertEquals("2", selectableValue.getName());
		
	}
	
	/**
	 * Test, processing csv.  
	 */
	@Test
	public void testProcessCSV() {
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(provider.getInputStream(enLocale)));

		provider.setValueFieldIndex(1);
		provider.setNameFieldIndex(2);	
		provider.setSkipFirstLine(true);
		provider.setDelimiter(',');
		
		List<SelectableValue<Integer>> values = provider.processCSV(reader);
		
		assertNotNull(values);
		
		assertEquals(2, values.size());
		
		assertEquals(1, values.get(0).getValue().intValue());
		
		assertEquals(ONE, values.get(0).getName());
		
	}

	/**
	 * Test, createSelectableValues csv.  
	 */
	@Test
	public void testCreateSelectableValues() {

		provider.setValueFieldIndex(1);
		provider.setNameFieldIndex(2);	
		provider.setSkipFirstLine(true);
		provider.setDelimiter(',');
		
		List<SelectableValue<Integer>> values = provider.createSelectableValues(enLocale);
		
		assertNotNull(values);
		
		assertEquals(2, values.size());
		
		assertEquals(1, values.get(0).getValue().intValue());
		
		assertEquals(ONE, values.get(0).getName());

		//test , that method can work with null locale
		values = provider.createSelectableValues(null);
		
		assertNotNull(values);
		
		assertEquals(2, values.size());
		
		assertEquals(1, values.get(0).getValue().intValue());
		
		assertEquals(ONE, values.get(0).getName());
		
		
		// test, that null will be returned instead of empty list
		values = provider.createSelectableValues(frLocale);
		 
		assertNull(values);
		
	}
	
	/**
	 * Test, get localized resource name .  
	 */
	@Test
	public void testGetLocalizedResourceName() {
		
		provider.setResourceName("resource.csv");
		assertEquals("resource.csv.en", provider.getLocalizedResourceName(enLocale));
		
		// test for null locale
		assertEquals("resource.csv", provider.getLocalizedResourceName(null));
		
	}
	
	/**
	 * Test, GetSelectableValues.  
	 */
	@Test
	public void testGetSelectableValues() {

		provider.setValueFieldIndex(1);
		provider.setNameFieldIndex(2);	
		provider.setSkipFirstLine(true);
		provider.setDelimiter(',');
		
		List<SelectableValue<Integer>> values = provider.getSelectableValues(enLocale, null, null);
		assertNotNull(values);
		
		assertEquals(2, values.size());
		
		assertEquals(1, values.get(0).getValue().intValue());
		
		assertEquals(ONE, values.get(0).getName());
		
		//test for null locale
		values = provider.getSelectableValues(null, null, null);
		assertNotNull(values);
		
		assertEquals(2, values.size());
		
		assertEquals(1, values.get(0).getValue().intValue());
		
		assertEquals(ONE, values.get(0).getName());
		
		//test for locale, that return null instead of empty list
		values = provider.getSelectableValues(frLocale, null, null);
		assertNull(values);
		
	}

	/**
	 * Test that requesting an input stream uses the appropriate loader.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void testGetInputStream() throws IOException {
		AbstractExternalCSVSelectableTagValueProvider<Integer> provider = new AbstractExternalCSVSelectableTagValueProvider<Integer>() {
			@Override
			protected Integer adaptString(final String stringValue) {
				return Integer.valueOf(stringValue);
			}
		};
		final ResourceLoader resourceLoader = context.mock(ResourceLoader.class);
		provider.setResourceLoader(resourceLoader);
		provider.setResourceName("somefile.csv");
		
		final Resource nonExistentResource = context.mock(Resource.class, "nonExistent");
		final Resource resource = context.mock(Resource.class, "good");
		
		final ByteArrayInputStream inputStream = new ByteArrayInputStream(enCSV.getBytes());
		context.checking(new Expectations() {
			{
				oneOf(resourceLoader).getResource(ResourceUtils.CLASSPATH_URL_PREFIX + "somefile.csv.en"); will(returnValue(nonExistentResource));
				oneOf(resourceLoader).getResource(ResourceUtils.CLASSPATH_URL_PREFIX + "somefile.csv"); will(returnValue(resource));
				
				oneOf(nonExistentResource).exists(); will(returnValue(false));
				oneOf(resource).getInputStream(); will(returnValue(inputStream));
			}
		});
		
		InputStream result = provider.getInputStream(enLocale);
		assertEquals("The resultant input stream should match the one from the resource loader", inputStream, result);
		
	}
}

