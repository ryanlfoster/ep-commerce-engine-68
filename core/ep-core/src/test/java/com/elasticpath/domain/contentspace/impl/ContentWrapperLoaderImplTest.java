/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.domain.contentspace.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ValueTypeEnum;
import com.elasticpath.domain.contentspace.ContentWrapper;
import com.elasticpath.domain.contentspace.ContentWrapperService;
import com.elasticpath.domain.contentspace.Parameter;


/**
 * Tests the implementation of the content wrapper loader, which is responsible for the
 * loading of content wrappers from .xml files.
 */
public class ContentWrapperLoaderImplTest {
	
	private static final int NUMBER_OF_DEFINITIONS_1 = 1;
	
	private static final int NUMBER_OF_DEFINITIONS_2 = 2;

	private static final int NUMBER_OF_DEFINITIONS_3 = 3;
	
	private static final int NUMBER_OF_DEFINITIONS_6 = 6;
	
	private static final int NUMBER_OF_DEFINITIONS_7 = 7;
	
	private static final String MSG = "Template parameter definitions not all loaded";
	
	/** The path to the directory that contains the configuration for tests. **/
	private static final String XML_DIRECTORY = "target/test-classes/conf/content-wrappers";

	/** The content wrapper loader to be tested when loading content wrappers. **/
	private ContentWrapperLoaderImpl wrapperLoader;
	
	/** Loaded wrappers. **/
	private Map<String, ContentWrapper> wrappers;
	
	/**
	 * The method that will set up the necessary data before each test is run.
	 */
	@Before
	public void setUp() {
		wrapperLoader = new ContentWrapperLoaderImpl() {
			protected String getContentWrapperDirectoryLocation() {
				File file = new File(XML_DIRECTORY);
				if (!file.exists()) {
					String newPath = "com.elasticpath.core/" + XML_DIRECTORY;
					file = new File(newPath);
					if (!file.exists()) {
						fail("Test configuration directory was not found.");
					}
					return newPath;
				}
				return XML_DIRECTORY;
			}
		};
		
		BeanFactory beanFactory = new BeanFactoryStubImpl();

		wrapperLoader.setBeanFactory(beanFactory);
		
		wrappers = wrapperLoader.loadContentWrappers();
		
	}
	
	/**
	 * Method for check quantity of loaded wrapper.
	 */
	@Test
	public void testLoadContentWrappersQuantity() {
		assertEquals("Only 7 of the content wrapper XML files are valid.", NUMBER_OF_DEFINITIONS_7, wrappers.size());
	}

	/**
	 * Method that checks if the content wrapper loader returns only well formed content
	 * wrappers that have been parsed from XML files.
	 */
	@Test
	public void testLoadContentWrappers() {
		
		//There are currently three content wrappers in the directory from which the
		//content wrappers are being loaded, two of them have well formed XML and therefore
		//are loaded while the third is not loaded because of problems with data.
		
		ContentWrapper returnedWrapper = wrappers.get("1001");
		assertNotNull("Wrapper Id not found from loaded content wrappers", returnedWrapper);
		assertEquals("Template name not loaded correctly", "cs2.cwt", returnedWrapper.getTemplateName());
		assertEquals("Wrapper Id not loaded correctly", "1001", returnedWrapper.getWrapperId());
		assertEquals("Template parameter definitions should be empty", 0, returnedWrapper.getTemplateParameters().size());
		assertEquals("Imput parameter definitions should be empty", 0, returnedWrapper.getUserInputSettings().size());
		
		returnedWrapper = wrappers.get("1000");
		assertNotNull("Wrapper Id not found from loaded content wrappers", returnedWrapper);
		assertEquals("Template name not loaded correctly", "productContentSpace.cwt", returnedWrapper.getTemplateName());
		assertEquals("Wrapper Id not loaded correctly", "1000", returnedWrapper.getWrapperId());

		assertEquals("Imput parameter definitions should be empty", 1, returnedWrapper.getUserInputSettings().size());

		
		//Check the parameters for this wrapper because it contained parameter definitions
		List<Parameter> definitions = returnedWrapper.getUserInputSettings();
		assertNotNull("Parameter definitions not loaded correctly", definitions);
		assertEquals("Parameter definitions not all loaded", NUMBER_OF_DEFINITIONS_1, definitions.size());
		
		List<Parameter> templateParameters = returnedWrapper.getTemplateParameters();
		assertEquals(MSG, NUMBER_OF_DEFINITIONS_3, templateParameters.size());
		
		List<ContentWrapperService> serviceDefinitions = returnedWrapper.getServiceDefinitions();
		
		assertEquals("Service definitions not all loaded", NUMBER_OF_DEFINITIONS_3, serviceDefinitions.size());
		
		
		assertNotNull("Init section must be not empty", returnedWrapper.getInitSection());
		assertNotNull("Human readable name must be not empty", returnedWrapper.getName());
		
		returnedWrapper = wrappers.get("2000");
		assertNotNull("Wrapper Id = 2000 not found from loaded content wrappers", returnedWrapper);
		assertEquals("No input parameters", NUMBER_OF_DEFINITIONS_2, returnedWrapper.getUserInputSettings().size());
		
		returnedWrapper = wrappers.get("2001");
		assertNotNull("Wrapper Id = 2001 not found from loaded content wrappers", returnedWrapper);

		
	}
	
	/**
	 * Test loaded content wrappers, which has definition of parameter type. 
	 */
	@Test
	public void testLoadedWrappersWithParameterType() {
		
		ContentWrapper returnedWrapper = wrappers.get("2002");
		assertNotNull("Wrapper Id = 2002 not found from loaded content wrappers", returnedWrapper);
		
		List<Parameter> inputParameters = returnedWrapper.getUserInputSettings();
		
		assertEquals(MSG, NUMBER_OF_DEFINITIONS_6, inputParameters.size());
		
		
		for (Parameter parameter : inputParameters) {
			if ("code1".equals(parameter.getParameterId()) || "code2".equals(parameter.getParameterId()) 
					|| "code3".equals(parameter.getParameterId())) {
				assertEquals(ValueTypeEnum.StringShort, parameter.getType());
			} else if ("code4".equals(parameter.getParameterId())) {
				assertEquals(ValueTypeEnum.Integer, parameter.getType());
			} else if ("code5".equals(parameter.getParameterId())) {
				assertEquals(ValueTypeEnum.Image, parameter.getType());
			} else if ("code6".equals(parameter.getParameterId())) {
				assertEquals(ValueTypeEnum.StringShort, parameter.getType());
			}
		}
		
		
	}

	/**
	 * Test loaded content wrappers, which has category and product types. 
	 */
	@Test
	public void testLoadedWrappersWithProductAndCategory() {
		
		ContentWrapper returnedWrapper = wrappers.get("20090216");
		assertNotNull("Wrapper Id = 20090216 not found from loaded content wrappers", returnedWrapper);
		
		List<Parameter> inputParameters = returnedWrapper.getUserInputSettings();
		
		assertEquals(MSG, NUMBER_OF_DEFINITIONS_2, inputParameters.size());
		
		
		for (Parameter parameter : inputParameters) {
			if ("prodCode".equals(parameter.getParameterId())) {
				assertEquals(ValueTypeEnum.Product, parameter.getType());
			} else if ("catCode".equals(parameter.getParameterId())) {
				assertEquals(ValueTypeEnum.Category, parameter.getType());
			}
		}
		
		
	}
	
	/**
	 * Support HTML type test. 
	 */
	@Test
	public void testLoadedWrappersWithHTMLParameterType() {
		
		ContentWrapper returnedWrapper = wrappers.get("20090318");
		assertNotNull("Wrapper Id = 20090318 not found from loaded content wrappers", returnedWrapper);
		
		List<Parameter> templateParameters = returnedWrapper.getTemplateParameters();
		
		assertEquals(MSG, NUMBER_OF_DEFINITIONS_1, templateParameters.size());
		
		
		for (Parameter parameter : templateParameters) {
			if ("html".equals(parameter.getParameterId())) {
				assertEquals(ValueTypeEnum.HTML, parameter.getType());
			}
		}
		
		
	}

	/**
	 * Separate class to work around a Checkstyle defect.
	 * 
	 * @see <a href="http://sourceforge.net/p/checkstyle/bugs/472/">the defect</a>
	 */
	private static final class BeanFactoryStubImpl implements BeanFactory {
		@Override
		@SuppressWarnings("unchecked")
		public <T> T getBean(final String beanName) {
			return (T) new ContentWrapperImpl();
		}

		@Override
		public <T> Class<T> getBeanImplClass(final String beanName) {
			return null;
		}
	}

}
