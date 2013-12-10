package com.elasticpath.commons.exception;

import static org.junit.Assert.assertNotSame;

import org.junit.Test;

/**
 * Test that the EpRequiredDependentFieldsMissingBindException reports
 * the data appropriately.
 */
public class EpRequiredDependentFieldsMissingBindExceptionTest {

	/**
	 * Test that we get a defensivly copied version of the required fields.
	 */
	@Test
	public void testReceiveCopyOfArgsArray() {
		String [] requiredFields = new String [] {"required1", "required2"};
		EpRequiredDependentFieldsMissingBindException exception = 
			new EpRequiredDependentFieldsMissingBindException("field", "value", requiredFields);
		assertNotSame(requiredFields, exception.getRequiredFields());
	}
}
