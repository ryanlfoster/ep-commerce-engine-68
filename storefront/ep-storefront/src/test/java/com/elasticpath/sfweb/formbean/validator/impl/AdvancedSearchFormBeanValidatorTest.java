package com.elasticpath.sfweb.formbean.validator.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.internal.runners.JUnit4ClassRunner;
import org.junit.runner.RunWith;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import com.elasticpath.sfweb.formbean.GiftCertificateFormBean;
import com.elasticpath.sfweb.formbean.NonPreDefinedAttributeRangeFieldFormBean;
import com.elasticpath.sfweb.formbean.impl.AdvancedSearchControllerFormBeanImpl;
import com.elasticpath.sfweb.formbean.impl.NonPreDefinedAttributeRangeFieldFormBeanImpl;
import com.elasticpath.sfweb.view.helpers.ParameterMapper;

/**
 * Test for AdvancedSearchFormBeanValidator.
 *
 */
@RunWith(JUnit4ClassRunner.class)
@SuppressWarnings("PMD.TooManyMethods")
public class AdvancedSearchFormBeanValidatorTest {

	private static final String ENGLISH_LEGAL_DECIMAL = "1.5";

	private static final String FRENCH_DECIMAL = "1,5";
	
	private static final String ATTRIBUTE_KEY = "A00001";
	
	private AdvancedSearchFormBeanValidator validator;
	
	private AdvancedSearchControllerFormBeanImpl advancedSearchFormBean;
	
	private Errors errors;
	

	private static final String FROM_FIELD_IS_HIGHER_THAN_TO_FIELD = "errors.advancedSearch.upper.limit.less.than.lower.limit";

	private static final String FROM_FIELD_MALFORMED = "errors.advancedSearch.lower.limit.invalid.number";
	
	private static final String TO_FIELD_MALFORMED = "errors.advancedSearch.upper.limit.invalid.number";

	private static final String FROM_FIELD_REQUIRED = "errors.advancedSearch.lower.limit.required";
	
	private static final String TO_FIELD_REQUIRED = "errors.advancedSearch.upper.limit.required";
	
	private static final String NEGATIVE_NUMBER = "typeMismatch.quantity";
	
	/**
	 * Setup initial objects.
	 */
	@Before
	public void setUp() {

		validator = new AdvancedSearchFormBeanValidator();
		validator.setParameterMapper(new ParameterMapper());
		Map<String, String> errorCodeMap = new HashMap<String, String>();
				
		errorCodeMap.put("errorLowerLimitRequired", "errors.advancedSearch.lower.limit.required");
		errorCodeMap.put("errorUpperLimitRequired", "errors.advancedSearch.upper.limit.required");
		errorCodeMap.put("errorInvalidLowerLimit", "errors.advancedSearch.lower.limit.invalid.number");
		errorCodeMap.put("errorInvalidUpperLimit", "errors.advancedSearch.upper.limit.invalid.number");
		errorCodeMap.put("errorNegative", "typeMismatch.quantity");
		errorCodeMap.put("errorUpperLessThanLower", "errors.advancedSearch.upper.limit.less.than.lower.limit");
		validator.setErrorCodeMap(errorCodeMap);
		
		advancedSearchFormBean = new AdvancedSearchControllerFormBeanImpl();
		advancedSearchFormBean.setNonPreDefinedAttributeRangeFilterMap(getNonPreDefinedAttributeRangeFilterMap());
		

		errors = new BeanPropertyBindingResult(advancedSearchFormBean, "command");

	}
	

	
	/** 
	 * Test another bean other than AdvancedSearchControllerFormBean will not be supported by validator.
	 */
	@Test
	public void testSupportOnlyAdvancedSearchFormBean() {
		//No support for other classes
		assertFalse(validator.supports(GiftCertificateFormBean.class));
		assertFalse(validator.supports(Object.class));
		assertFalse(validator.supports(null));
		
		//..except for AdvancedSearchControllerFormBean
		assertTrue(validator.supports(advancedSearchFormBean.getClass()));
	}
	


	/**  
	 * Test that the values in the fields FROM and TO is the same should work.
	 */
	@Test
	public void validateSameFromToRange() {
		advancedSearchFormBean.setLocale(Locale.ENGLISH);
		NonPreDefinedAttributeRangeFieldFormBean formBean = 
			advancedSearchFormBean.getNonPreDefinedAttributeRangeFilterMap().get(ATTRIBUTE_KEY);
		
		formBean.setFromField(ENGLISH_LEGAL_DECIMAL);
		formBean.setToField(ENGLISH_LEGAL_DECIMAL);
		
		validator.validate(advancedSearchFormBean, errors);

		assertEquals(0, errors.getAllErrors().size());
	}	
	
	/**  
	 * Test that the values in the fields FROM and TO is the same should work.
	 */
	@Test
	public void validateSameFromToRangeFrench() {
		advancedSearchFormBean.setLocale(Locale.FRENCH);
		NonPreDefinedAttributeRangeFieldFormBean formBean = 
			advancedSearchFormBean.getNonPreDefinedAttributeRangeFilterMap().get(ATTRIBUTE_KEY);
		
		formBean.setFromField(ENGLISH_LEGAL_DECIMAL);
		formBean.setToField(ENGLISH_LEGAL_DECIMAL);
		
		validator.validate(advancedSearchFormBean, errors);

		assertEquals(0, errors.getAllErrors().size());
	}	
	
	/**  
	 * Test two decmial places.
	 */
	@Test
	public void validateTwoDecimalPlaces() {
		advancedSearchFormBean.setLocale(Locale.ENGLISH);
		NonPreDefinedAttributeRangeFieldFormBean formBean = 
			advancedSearchFormBean.getNonPreDefinedAttributeRangeFilterMap().get(ATTRIBUTE_KEY);
		formBean.setFromField("1.50");
		formBean.setToField("110.59");
		validator.validate(advancedSearchFormBean, errors);
		assertEquals(0, errors.getAllErrors().size());
	}

	
	/**  
	 * Test three decimal places will pass. (precision is 4)
	 */
	@Test
	public void testThreeDecimalPlaces() {
		advancedSearchFormBean.setLocale(Locale.ENGLISH);
		NonPreDefinedAttributeRangeFieldFormBean formBean = 
			advancedSearchFormBean.getNonPreDefinedAttributeRangeFilterMap().get(ATTRIBUTE_KEY);
		
		formBean.setFromField(ENGLISH_LEGAL_DECIMAL);
		formBean.setToField("3.555");
		
		validator.validate(advancedSearchFormBean, errors);

		assertEquals(0, errors.getAllErrors().size());
	}

	/**  
	 * Test three decimal places will pass. (precision is 4)
	 */
	@Test
	public void testThreeDecimalPlacesFrench() {
		advancedSearchFormBean.setLocale(Locale.FRENCH);
		NonPreDefinedAttributeRangeFieldFormBean formBean = 
			advancedSearchFormBean.getNonPreDefinedAttributeRangeFilterMap().get(ATTRIBUTE_KEY);
		
		formBean.setFromField(FRENCH_DECIMAL);
		formBean.setToField("3.555");
		
		validator.validate(advancedSearchFormBean, errors);

		assertEquals(1, errors.getAllErrors().size());
	}	
	
	/**  
	 * Test that the field that have 2 commas will fail.
	 */
	@Test
	public void validateMalFormedCommas() {
		advancedSearchFormBean.setLocale(Locale.ENGLISH);
		NonPreDefinedAttributeRangeFieldFormBean formBean = 
			advancedSearchFormBean.getNonPreDefinedAttributeRangeFilterMap().get(ATTRIBUTE_KEY);
		
		formBean.setFromField("1,5,5");
		formBean.setToField("1,5,6");
		
		validator.validate(advancedSearchFormBean, errors);

		assertTrue(errors.hasFieldErrors());
		assertEquals(1, errors.getAllErrors().size());
		assertEquals(FROM_FIELD_MALFORMED, errors.getAllErrors().get(0).getCode());
	}
	
	
	private Map<String, NonPreDefinedAttributeRangeFieldFormBean> getNonPreDefinedAttributeRangeFilterMap() {
		Map<String, NonPreDefinedAttributeRangeFieldFormBean> map = new HashMap<String, NonPreDefinedAttributeRangeFieldFormBean>();
		map.put(ATTRIBUTE_KEY, new NonPreDefinedAttributeRangeFieldFormBeanImpl());
		return map;
	}
	
	/**  
	 * From range is not specified.
	 */
	@Test
	public void fromRangeNotSpecified() {
		advancedSearchFormBean.setLocale(Locale.ENGLISH);
		NonPreDefinedAttributeRangeFieldFormBean formBean = 
			advancedSearchFormBean.getNonPreDefinedAttributeRangeFilterMap().get(ATTRIBUTE_KEY);
		
		formBean.setFromField("");
		formBean.setToField(ENGLISH_LEGAL_DECIMAL);
		
		validator.validate(advancedSearchFormBean, errors);

		assertTrue(errors.hasFieldErrors());
		assertEquals(1, errors.getAllErrors().size());
		assertEquals(FROM_FIELD_REQUIRED, errors.getAllErrors().get(0).getCode());
		
		formBean.setFromField(null);
		formBean.setToField(ENGLISH_LEGAL_DECIMAL);
		
		validator.validate(advancedSearchFormBean, errors);

		assertTrue(errors.hasFieldErrors());
		assertEquals(2, errors.getAllErrors().size());
		assertEquals(FROM_FIELD_REQUIRED, errors.getAllErrors().get(1).getCode());
	}
	
	/**  
	 * To range is not specified.
	 */
	@Test
	public void toRangeNotSpecified() {
		advancedSearchFormBean.setLocale(Locale.ENGLISH);
		NonPreDefinedAttributeRangeFieldFormBean formBean = 
			advancedSearchFormBean.getNonPreDefinedAttributeRangeFilterMap().get(ATTRIBUTE_KEY);
		
		formBean.setFromField(ENGLISH_LEGAL_DECIMAL);
		formBean.setToField("");
		
		validator.validate(advancedSearchFormBean, errors);

		assertTrue(errors.hasFieldErrors());
		assertEquals(1, errors.getAllErrors().size());
		assertEquals(TO_FIELD_REQUIRED, errors.getAllErrors().get(0).getCode());
		
		formBean.setFromField(ENGLISH_LEGAL_DECIMAL);
		formBean.setToField(null);
		
		validator.validate(advancedSearchFormBean, errors);

		assertTrue(errors.hasFieldErrors());
		assertEquals(2, errors.getAllErrors().size());
		assertEquals(TO_FIELD_REQUIRED, errors.getAllErrors().get(1).getCode());
	}
	
	/**  
	 * Test that the field FROM is higher than TO will fail.
	 */
	@Test
	public void validateFromIsHigerThanTo() {
		advancedSearchFormBean.setLocale(Locale.ENGLISH);
		NonPreDefinedAttributeRangeFieldFormBean formBean = 
			advancedSearchFormBean.getNonPreDefinedAttributeRangeFilterMap().get(ATTRIBUTE_KEY);
		
		formBean.setFromField("3.5");
		formBean.setToField(ENGLISH_LEGAL_DECIMAL);
		
		validator.validate(advancedSearchFormBean, errors);

		assertTrue(errors.hasFieldErrors());
		assertEquals(1, errors.getAllErrors().size());
		assertEquals(FROM_FIELD_IS_HIGHER_THAN_TO_FIELD, errors.getAllErrors().get(0).getCode());
	}
	
	/**  
	 * Test that the field that have 2 periods will fail.
	 */
	@Test
	public void validateMalFormedDecimals() {
		advancedSearchFormBean.setLocale(Locale.ENGLISH);
		NonPreDefinedAttributeRangeFieldFormBean formBean = 
			advancedSearchFormBean.getNonPreDefinedAttributeRangeFilterMap().get(ATTRIBUTE_KEY);
		
		formBean.setFromField("1.5.5");
		formBean.setToField("1.5.6");
		
		validator.validate(advancedSearchFormBean, errors);

		assertTrue(errors.hasFieldErrors());
		assertEquals(1, errors.getAllErrors().size());
		assertEquals(FROM_FIELD_MALFORMED, errors.getAllErrors().get(0).getCode());
	}
	

	/**  
	 * Test that having non decimal characters in search will fail.
	 */
	@Test
	public void validateMalFormedCharactersFrom() {
		advancedSearchFormBean.setLocale(Locale.ENGLISH);
		NonPreDefinedAttributeRangeFieldFormBean formBean = 
			advancedSearchFormBean.getNonPreDefinedAttributeRangeFilterMap().get(ATTRIBUTE_KEY);
		
		formBean.setFromField("1.5x");
		formBean.setToField(ENGLISH_LEGAL_DECIMAL);
		
		validator.validate(advancedSearchFormBean, errors);

		assertTrue(errors.hasFieldErrors());
		assertEquals(1, errors.getAllErrors().size());
		assertEquals(FROM_FIELD_MALFORMED, errors.getAllErrors().get(0).getCode());
	}
	
	/**  
	 * Test that having non decimal characters in search will fail.
	 */
	@Test
	public void validateMalFormedCharactersTo() {
		advancedSearchFormBean.setLocale(Locale.ENGLISH);
		NonPreDefinedAttributeRangeFieldFormBean formBean = 
			advancedSearchFormBean.getNonPreDefinedAttributeRangeFilterMap().get(ATTRIBUTE_KEY);
		
		formBean.setFromField(ENGLISH_LEGAL_DECIMAL);
		formBean.setToField("1.5x");
		
		validator.validate(advancedSearchFormBean, errors);

		assertTrue(errors.hasFieldErrors());
		assertEquals(1, errors.getAllErrors().size());
		assertEquals(TO_FIELD_MALFORMED, errors.getAllErrors().get(0).getCode());
	}
	
	/**  
	 * Test that having non decimals in the FROM field in search will fail.
	 */
	@Test
	public void validateMalFormedCharactersInFromField() {
		advancedSearchFormBean.setLocale(Locale.ENGLISH);
		NonPreDefinedAttributeRangeFieldFormBean formBean = 
			advancedSearchFormBean.getNonPreDefinedAttributeRangeFilterMap().get(ATTRIBUTE_KEY);
		
		formBean.setFromField("%1.5");
		formBean.setToField(ENGLISH_LEGAL_DECIMAL);
		
		validator.validate(advancedSearchFormBean, errors);

		assertTrue(errors.hasFieldErrors());
		assertEquals(1, errors.getAllErrors().size());
		assertEquals(FROM_FIELD_MALFORMED, errors.getAllErrors().get(0).getCode());
	}
	
	

	/**  
	 * Test that having non decimals in the FROM field in search will fail.
	 */
	@Test
	public void validateMalFormedCharactersInToField() {
		advancedSearchFormBean.setLocale(Locale.ENGLISH);
		NonPreDefinedAttributeRangeFieldFormBean formBean = 
			advancedSearchFormBean.getNonPreDefinedAttributeRangeFilterMap().get(ATTRIBUTE_KEY);
		
		formBean.setFromField(ENGLISH_LEGAL_DECIMAL);
		formBean.setToField("%1.6");
		
		validator.validate(advancedSearchFormBean, errors);

		assertTrue(errors.hasFieldErrors());
		assertEquals(1, errors.getAllErrors().size());
		assertEquals(TO_FIELD_MALFORMED, errors.getAllErrors().get(0).getCode());
	}
	
	/**  
	 * Test that having negatives in the FROM field in search will fail.
	 */
	@Test
	public void validateNegativeNumbersFromField() {
		advancedSearchFormBean.setLocale(Locale.ENGLISH);
		NonPreDefinedAttributeRangeFieldFormBean formBean = 
			advancedSearchFormBean.getNonPreDefinedAttributeRangeFilterMap().get(ATTRIBUTE_KEY);
		
		formBean.setFromField("-1.5");
		formBean.setToField(ENGLISH_LEGAL_DECIMAL);
		
		validator.validate(advancedSearchFormBean, errors);

		assertTrue(errors.hasFieldErrors());
		assertEquals(1, errors.getAllErrors().size());
		assertEquals(NEGATIVE_NUMBER, errors.getAllErrors().get(0).getCode());
	}
	
	/**  
	 * Test that having negatives in the FROM field in search will fail.
	 */
	@Test
	public void validateNegativeNumbersToField() {
		advancedSearchFormBean.setLocale(Locale.ENGLISH);
		NonPreDefinedAttributeRangeFieldFormBean formBean = 
			advancedSearchFormBean.getNonPreDefinedAttributeRangeFilterMap().get(ATTRIBUTE_KEY);
		
		formBean.setFromField(ENGLISH_LEGAL_DECIMAL);
		formBean.setToField("-1.6");
		
		validator.validate(advancedSearchFormBean, errors);

		assertTrue(errors.hasFieldErrors());
		assertEquals(1, errors.getAllErrors().size());
		assertEquals(NEGATIVE_NUMBER, errors.getAllErrors().get(0).getCode());
	}
	
	/**  
	 * Test string values.
	 */
	@Test
	public void stringCharsFromFieldWillFail() {
		advancedSearchFormBean.setLocale(Locale.ENGLISH);
		NonPreDefinedAttributeRangeFieldFormBean formBean = 
			advancedSearchFormBean.getNonPreDefinedAttributeRangeFilterMap().get(ATTRIBUTE_KEY);
		
		formBean.setFromField("one");
		formBean.setToField(ENGLISH_LEGAL_DECIMAL);
		
		validator.validate(advancedSearchFormBean, errors);

		assertTrue(errors.hasFieldErrors());
		assertEquals(1, errors.getAllErrors().size());
		assertEquals(FROM_FIELD_MALFORMED, errors.getAllErrors().get(0).getCode());
	}

	/**  
	 * Test string values.
	 */
	@Test
	public void stringCharsToFieldWillFail() {
		advancedSearchFormBean.setLocale(Locale.ENGLISH);
		NonPreDefinedAttributeRangeFieldFormBean formBean = 
			advancedSearchFormBean.getNonPreDefinedAttributeRangeFilterMap().get(ATTRIBUTE_KEY);
		
		formBean.setFromField(ENGLISH_LEGAL_DECIMAL);
		formBean.setToField("àâäèéêëîïôœùûüÿÇç");
		
		validator.validate(advancedSearchFormBean, errors);

		assertTrue(errors.hasFieldErrors());
		assertEquals(1, errors.getAllErrors().size());
		assertEquals(TO_FIELD_MALFORMED, errors.getAllErrors().get(0).getCode());
	}
	
}