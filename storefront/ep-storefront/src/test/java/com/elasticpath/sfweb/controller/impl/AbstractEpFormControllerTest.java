/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.sfweb.controller.impl;


import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.springframework.web.bind.ServletRequestDataBinder;

/**
 * Tests the {@link AbstractEpFormController} form controller.
 */
public class AbstractEpFormControllerTest {

	private AbstractEpFormController abstractFormController;

	private String[] disallowedFieldsSet;
	
	/**
	 *
	 * @throws java.lang.Exception on error
	 */
	@Before
	public void setUp() throws Exception {
		abstractFormController = new AbstractEpFormController() {
		};
	}

	/**
	 * Tests that set disallowed fields work as expected.
	 * 
	 * @throws Exception if exception occurs
	 */
	@Test
	public void testSetDisallowedFormFields() throws Exception {
		String disallowedFields = " field1, field2 , field3,field4,field5 ";
		
		abstractFormController.setDisallowedFormFields(disallowedFields);
		ServletRequestDataBinder binder = new ServletRequestDataBinder(new Object()) {
			@Override
			public void setDisallowedFields(final String... disallowedFields) {
				disallowedFieldsSet = disallowedFields;
			}
		};
		abstractFormController.initBinder(null, binder);

		final int fieldsNumber = 5;
		assertEquals(fieldsNumber, disallowedFieldsSet.length);
		for (String field : disallowedFieldsSet) {
			final int fieldSizeCut = 5;
			// field1 => compare field only
			assertEquals("field", field.substring(0, fieldSizeCut));
		}
	}

}
