package com.elasticpath.sfweb.formbean.validator.impl;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.BigDecimalValidator;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.elasticpath.sfweb.formbean.AdvancedSearchControllerFormBean;
import com.elasticpath.sfweb.formbean.NonPreDefinedAttributeRangeFieldFormBean;
import com.elasticpath.sfweb.formbean.impl.AdvancedSearchControllerFormBeanImpl;
import com.elasticpath.sfweb.view.helpers.ParameterMapper;

/**
 * A validator for AdvancedSearchControllerFormBean bean.
 *
 */
public class AdvancedSearchFormBeanValidator implements Validator {
	
	/** The number pattern used for validating decimals. Note this gets overridden by locale specific formatting. */
	private static final String NUMBER_PATTERN = "#.##";

	private ParameterMapper parameterMapper;
	
	private Map<String, String> errorCodeMap;

	private static final String LEFT_BRACKET = "[\"";
	private static final String RIGHT_BRACKET_AND_DOT = "\"].";
	
	@Override
	public boolean supports(final Class< ? > clazz) {
		return AdvancedSearchControllerFormBeanImpl.class.equals(clazz);
	}

	@Override
	public void validate(final Object target, final Errors errors) {
		AdvancedSearchControllerFormBean advancedSearchFormBean = (AdvancedSearchControllerFormBean) target;
		validateNonPreDefinedAttributeRanges(advancedSearchFormBean.getNonPreDefinedAttributeRangeFilterMap(), errors);
	}
	
	private void validateNonPreDefinedAttributeRanges(
			final Map<String, NonPreDefinedAttributeRangeFieldFormBean> ranges, final Errors errors) {
		
		Set<String> keys = ranges.keySet();
		Iterator<String> iterator = keys.iterator();
		
		while (iterator.hasNext()) {
			
			String attributeKey = iterator.next();
			NonPreDefinedAttributeRangeFieldFormBean range = ranges.get(attributeKey);
			String toFieldAsString = range.getToField();
			String fromFieldAsString = range.getFromField();
			
			if (StringUtils.isBlank(toFieldAsString) && StringUtils.isBlank(fromFieldAsString)) {
				continue;
			} else if (StringUtils.isBlank(fromFieldAsString)) {
				errors.rejectValue(getParameterMapper().getNonPreDefinedAttributeRangeFilterMap() 
						+ LEFT_BRACKET + attributeKey + RIGHT_BRACKET_AND_DOT + getParameterMapper().getToField(), 
						errorCodeMap.get("errorLowerLimitRequired"));
				continue;
			} else if (StringUtils.isBlank(toFieldAsString)) {
				errors.rejectValue(getParameterMapper().getNonPreDefinedAttributeRangeFilterMap() 
						+ LEFT_BRACKET + attributeKey + RIGHT_BRACKET_AND_DOT + getParameterMapper().getToField(), 
						errorCodeMap.get("errorUpperLimitRequired"));
				continue;
			}
			
			BigDecimalValidator bigDecimalValidator = new BigDecimalValidator(true);
			
			if (!bigDecimalValidator.isValid(fromFieldAsString, NUMBER_PATTERN)) {
				errors.rejectValue(getParameterMapper().getNonPreDefinedAttributeRangeFilterMap() 
						+ LEFT_BRACKET + attributeKey + RIGHT_BRACKET_AND_DOT + getParameterMapper().getToField(), 
						errorCodeMap.get("errorInvalidLowerLimit"));
				continue;
			}
			
			if (!bigDecimalValidator.isValid(toFieldAsString, NUMBER_PATTERN)) {
				errors.rejectValue(getParameterMapper().getNonPreDefinedAttributeRangeFilterMap() + LEFT_BRACKET 
						+ attributeKey + RIGHT_BRACKET_AND_DOT + getParameterMapper().getToField(), 
						errorCodeMap.get("errorInvalidUpperLimit"));
				continue;
			}		
		
			BigDecimal fromNumber = bigDecimalValidator.validate(fromFieldAsString);
			BigDecimal toNumber = bigDecimalValidator.validate(toFieldAsString);
			
			if (fromNumber.doubleValue() < 0 || toNumber.doubleValue() < 0) {
				errors.rejectValue(getParameterMapper().getNonPreDefinedAttributeRangeFilterMap() + LEFT_BRACKET 
						+ attributeKey + RIGHT_BRACKET_AND_DOT + getParameterMapper().getToField(), 
						errorCodeMap.get("errorNegative"));
				continue;
			}
			
			if (fromNumber.doubleValue() > toNumber.doubleValue()) {
				errors.rejectValue(getParameterMapper().getNonPreDefinedAttributeRangeFilterMap() + LEFT_BRACKET 
						+ attributeKey + RIGHT_BRACKET_AND_DOT + getParameterMapper().getToField(), 
						errorCodeMap.get("errorUpperLessThanLower"));
			}
		}
	}
	
	/**
	 * @param parameterMapper the parameterMapper to set
	 */
	public void setParameterMapper(final ParameterMapper parameterMapper) {
		this.parameterMapper = parameterMapper;
		
	}
	
	private ParameterMapper getParameterMapper() {
		return parameterMapper;
	}

	/**
	 * @param errorCodeMap the errorCodeMap to set
	 */
	public void setErrorCodeMap(final Map<String, String> errorCodeMap) {
		this.errorCodeMap = errorCodeMap;
	}

	/**
	 * @return the errorCodeMap
	 */
	protected Map<String, String> getErrorCodeMap() {
		return errorCodeMap;
	}	
	
}
