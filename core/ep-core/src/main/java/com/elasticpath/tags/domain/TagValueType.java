package com.elasticpath.tags.domain;

import java.util.Set;

import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.validation.domain.ValidationConstraint;

/**
 * An interface to Tag Value Type object that represents type of the {@link TagDefinition}.
 *  Depending on this type TagDefinition object will receive proper validation rules.
 * 
 */
public interface TagValueType extends Persistable {

	  /**
     * @return the GUID of the tag value type
     */
    String getGuid();
        
    /**
     * Returns set of {@link TagOperator}.
     *
     * @return a set of {@link TagOperator}
     */
    Set <TagOperator> getOperators();
        
    /**
     * Returns Java class name if this type is one of java wrapper type.
     *
     * @return  Java class name 
     */
    String getJavaType();
    
    /**
     * Sets the string representation of java type.
     *
     * @param javaType the string representation of java type
     */
    void setJavaType(String javaType);    
    
    /**
     * Returns the key used by client-side application to create proper value picker - such as category, product.
     *
     * @return  string key of UI picker
     */    
    String getUIPickerKey();
    

    /**
     * Set the string that represents symbolic name of UI picker.
     *
     * @param uiPickerKey the string represents symbolic name of UI picker.
     */
    void setUIPickerKey(String uiPickerKey);

    /**
     * @return set of declarative validation constraints to which the condition value should adhere to.
     */
    Set<ValidationConstraint> getValidationConstraints();

    /**
     * Sets validation constraints.
     * 
     * @param validationConstraints ValidationConstraints to set
     */
    void setValidationConstraints(Set<ValidationConstraint> validationConstraints);
    
    /**
     * 
     * Get allowed values for tag value type.
     * 
     * @return a list of allowed values or null if TagValue type not provide selectable values.
     */
    Set<TagAllowedValue> getAllowedValues();
    
    /**
     * Add {@link TagAllowedValue} to allowed values set.
     * @param tagAllowedValue value to add
     */
    void addAllowedValue(TagAllowedValue tagAllowedValue);
    
    

}
