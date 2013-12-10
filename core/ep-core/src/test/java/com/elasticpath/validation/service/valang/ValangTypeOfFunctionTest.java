package com.elasticpath.validation.service.valang;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Test that typeof function works correctly.
 */
public class ValangTypeOfFunctionTest {

	private ValangTypeOfFunction typeOf;
	
	/**
	 * Test null value argument returns false as result.
	 * @throws Exception if valang encounter an error
	 */
	@Test
	public void testDoGetResultForNullValue() throws Exception {
		
		typeOf = new ValangTypeOfFunction() {
			
			Object getFunctionArgument(final int argumentIndex, final Object target) {
				switch (argumentIndex) {
				case 0:
					return null;
				case 1:
					return "java.lang.Integer";
				default:
					return null;
				}
			}
			
		};
		
		assertFalse(typeOf.doGetResult(null));
		
	}
	
	/**
	 * Test null type argument returns false as result.
	 * @throws Exception if valang encounter an error
	 */
	@Test
	public void testDoGetResultForNullType() throws Exception {
		
		typeOf = new ValangTypeOfFunction() {
			
			Object getFunctionArgument(final int argumentIndex, final Object target) {
				switch (argumentIndex) {
				case 0:
					return Boolean.TRUE;
				case 1:
					return null;
				default:
					return null;
				}
			}
			
		};
		
		assertFalse(typeOf.doGetResult(null));
		
	}
	
	/**
	 * Test that if a boolean value is tested for having class "java.lang.Boolean" the result is true.
	 * @throws Exception if valang encounter an error
	 */
	@Test
	public void testDoGetResultForSuccess() throws Exception {
		
		typeOf = new ValangTypeOfFunction() {
			
			Object getFunctionArgument(final int argumentIndex, final Object target) {
				switch (argumentIndex) {
				case 0:
					return Boolean.TRUE;
				case 1:
					return "java.lang.Boolean";
				default:
					return null;
				}
			}
			
		};
		
		assertTrue(typeOf.doGetResult(null));
	}
	
	/**
	 * Test that if a boolean value is tested for having class "java.lang.Integer" the result is true.
	 * @throws Exception if valang encounter an error
	 */
	@Test
	public void testDoGetResultForFailure() throws Exception {
		
		typeOf = new ValangTypeOfFunction() {
			
			Object getFunctionArgument(final int argumentIndex, final Object target) {
				switch (argumentIndex) {
				case 0:
					return Boolean.TRUE;
				case 1:
					return "java.lang.Integer";
				default:
					return null;
				}
			}
			
		};
		
		assertFalse(typeOf.doGetResult(null));
	}
	
	/**
	 * Test that if a long value is tested for having class "java.lang.Integer" the result is true.
	 * @throws Exception if valang encounter an error
	 */
	@Test
	public void testDoGetResultForFailure2() throws Exception {
		
		typeOf = new ValangTypeOfFunction() {
			
			Object getFunctionArgument(final int argumentIndex, final Object target) {
				switch (argumentIndex) {
				case 0:
					return Long.valueOf(1L);
				case 1:
					return "java.lang.Integer";
				default:
					return null;
				}
			}
			
		};
		
		assertFalse(typeOf.doGetResult(null));
	}

}
