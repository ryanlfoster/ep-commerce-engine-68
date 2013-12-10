package com.elasticpath.tags.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.ResourceUtils;

import au.com.bytecode.opencsv.CSVReader;

import com.elasticpath.tags.domain.SelectableValue;
import com.elasticpath.tags.domain.TagValueType;
import com.elasticpath.tags.domain.impl.SelectableValueImpl;
import com.elasticpath.tags.service.SelectableTagValueProvider;

/**
 * 
 * Abstract external selectable values provider, that obtain data from cvs files.
 * Usually data in csv files can be differently formated, hence this class 
 * contain following configuration properties.
 * 
 * Column, that contains actual value and name shall be configured via 
 * <code>valueFieldIndex</code> and <code>nameFieldIndex</code> properties.
 * Warning, {@CSVReader} hold whole row at 0 element. I.e. csv row
 * firstCol,secondCol
 * will be present as
 * line[0] -> firstCol,secondCol
 * line[1] -> firstCol
 * line[2] -> secondCol  
 * 
 * Csv file can have a header at first line, to skip first line set 
 * <code>skipFirstLine</code>property to true.
 * 
 * Csv file can have a different columns delimiter, set <code>delimiter</code>
 * to specify column delimiter.
 * 
 * Csv can have localized version. Localized resource name will be constructed for
 * specified locale as <code>resourceName + "." + locale.getLanguage()</code> if locale not null, 
 * otherwise will be used specified <code>resourceName</code>
 * 
 * @param <VALUE> value type in {@link SelectableValue} value name pair   
 */
public abstract class AbstractExternalCSVSelectableTagValueProvider<VALUE> implements SelectableTagValueProvider<VALUE>, ResourceLoaderAware {
	
	private static final Logger LOG = Logger.getLogger(AbstractExternalCSVSelectableTagValueProvider.class);
	
	private static final String DOT = ".";
	
	private final Map<String, List<SelectableValue<VALUE>>> selectableValuesMap =  new HashMap<String, List<SelectableValue<VALUE>>>();
	
	private String resourceName;
	
	private boolean skipFirstLine;
	
	private char delimiter;
	
	private int valueFieldIndex;
	
	private int nameFieldIndex;
	
	private ResourceLoader resourceLoader;
	
	/**
	 * Adapt string value to VALUE type. 
	 * @param stringValue the value to adapt.
	 * @return VALUE
	 */
	protected abstract VALUE adaptString(String stringValue);
	
	
	/**
	 * Get the list of value-name pair for given locale and optional search criteria.
	 * @param tagValueType the tag value type, that request list of values
	 * @param locale specified locale
	 * @param searchCriteria - optional search criteria.
	 * @return list of {@SelectableTagValue).
	 */
	public List<SelectableValue<VALUE>> getSelectableValues(final Locale locale,
			final TagValueType tagValueType, final Map<?, ?> searchCriteria) {
		
		String localeLanguage = "default";
		if (locale != null) {
			localeLanguage = locale.getLanguage();
		}
		if (!selectableValuesMap.containsKey(localeLanguage)) {
			List<SelectableValue<VALUE>> selectableValues = createSelectableValues(locale);
			selectableValuesMap.put(localeLanguage, selectableValues);
		}
		
		return selectableValuesMap.get(localeLanguage);
	}

	/**
	 *
	 * Create list of {@SelectableTagValue) from csv file.
	 * If csv file not found for specified locale, will be used default locale.  
	 * @param locale the locale.
	 * @return list of {@SelectableTagValue).
	 */
	List<SelectableValue<VALUE>> createSelectableValues(final Locale locale) {
		List<SelectableValue<VALUE>> selectableValues = null;
		InputStream inputStream = getInputStream(locale);
		if (inputStream != null) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));			
			selectableValues = processCSV(reader);
			if (selectableValues.isEmpty()) {
				selectableValues = null;
			}
			try {
				inputStream.close();
			} catch (IOException e) {
				LOG.warn("Cant close resource stream to " + resourceName + " file or localized derivant " + locale, e);
			}
		}
		return selectableValues;
	}

	/**
	 * Read data from cvs file and create list of selectable values.
	 * Only valid csv row will be converted to  {@link SelectableValue} and added to result.
	 * @param reader {@link BufferedReader}
	 * @return list of {@link SelectableValue}
	 */
	List<SelectableValue<VALUE>> processCSV(final BufferedReader reader) {
		String [] line;		
		boolean firstLine = true;
		List<SelectableValue<VALUE>> list = new ArrayList<SelectableValue<VALUE>>();
		CSVReader csvReader = new CSVReader(reader, delimiter);
		try {
			while ((line = csvReader.readNext()) != null) {
				if (firstLine && skipFirstLine) {
					firstLine = false;
					continue;
				}
				if (isValid(line)) {
					list.add(createSelectableValue(line));	
				}
			}
		} catch (IOException e) {
			LOG.error("Error while processing " + resourceName + " file or localized derivant", e);
		}
		return list; 
	}
	
	/**
	 * Check if array of strings is valid.
	 * @param line string array, represent the row from csv file 
	 * @return true if <code>line[valueFieldIndex]</code> 
	 * and <code>line[nameFieldIndex]</code> exists and contains not null data.  
	 */
	protected boolean isValid(final String [] line) {
		return (
				(line != null)
				&&
				(line.length > nameFieldIndex)
				&&
				(line.length > valueFieldIndex)
				&&
				(!StringUtils.isBlank(line[nameFieldIndex]))
				&&
				(!StringUtils.isBlank(line[valueFieldIndex]))				
				); 
	}
	
	/**
	 * Create {@link SelectableValue} from array of strings.
	 * @param line represent the row from csv file
	 * @return single {@link SelectableValue}
	 */
	protected SelectableValue<VALUE> createSelectableValue(final String [] line) {
		return new SelectableValueImpl<VALUE>(
				adaptString(line[valueFieldIndex]), 
				line[nameFieldIndex]);		
	}
	
	/**
	 * Get input stream for specified locale.  
	 * @param locale specified locale.
	 * @return input stream for specified locale or null if can get input stream for specified or defalut. 
	 */
	InputStream getInputStream(final Locale locale) {
		String localizedResourceName = getLocalizedResourceName(locale);
		Resource resource = getResource(localizedResourceName);
		if (!resource.exists()) {
			resource = getResource(resourceName);
		}
		try {
			return resource.getInputStream();
		} catch (IOException e) {
			LOG.warn("No CSV found with name " + localizedResourceName + " or " + resourceName);
		}
		return null;
	}
	
	/**
	 * Get localized resource name for specified locale.  
	 * @param locale specified locale.
	 * @return localized resource name
	 */
	protected String getLocalizedResourceName(final Locale locale) {
		if (locale != null) {
			return resourceName + DOT + locale.getLanguage();
		}
		return resourceName;
		
	}
	
	/**
	 * Set CSV resource name.
	 * @param resourceName CSV resource name to set.
	 */
	public void setResourceName(final String resourceName) {
		this.resourceName = resourceName;
	}

	/**
	 * First line will be skipped if skip first line flag is true.
	 * @param skipFirstLine flag to set.
	 */
	public void setSkipFirstLine(final boolean skipFirstLine) {
		this.skipFirstLine = skipFirstLine;
	}


	/**
	 * Set cvs delimiter character.
	 * @param delimiter delimiter char
	 */
	public void setDelimiter(final char delimiter) {
		this.delimiter = delimiter;
	}


	/**
	 * Column index used for value. 
	 * @param valueFieldIndex column index.
	 */
	public void setValueFieldIndex(final int valueFieldIndex) {
		this.valueFieldIndex = valueFieldIndex;
	}

	/**
	 * Column index used for name. 
	 * @param nameFieldIndex column index.
	 */
	public void setNameFieldIndex(final int nameFieldIndex) {
		this.nameFieldIndex = nameFieldIndex;
	}
	
	@Override
	public void setResourceLoader(final ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}
	
	/**
	 * Gets the resource.
	 *
	 * @param location the location
	 * @return the resource
	 */
	public Resource getResource(final String location) {
		return resourceLoader.getResource(ResourceUtils.CLASSPATH_URL_PREFIX + location);
	}
	
}
