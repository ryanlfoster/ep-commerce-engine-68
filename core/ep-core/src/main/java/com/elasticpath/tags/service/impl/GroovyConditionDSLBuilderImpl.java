/*
 * Copyright (c) 2009. ElasticPath Software Inc. All rights reserved.
 */

package com.elasticpath.tags.service.impl;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.codehaus.groovy.runtime.InvokerHelper;

import com.elasticpath.cache.SimpleTimeoutCache;
import com.elasticpath.tags.domain.Condition;
import com.elasticpath.tags.domain.LogicalOperator;
import com.elasticpath.tags.domain.TagDefinition;
import com.elasticpath.tags.domain.TagValueType;
import com.elasticpath.tags.service.ConditionDSLBuilder;
import com.elasticpath.tags.service.ConditionValidationFacade;
import com.elasticpath.tags.service.DSLValueDecorator;
import com.elasticpath.tags.service.InvalidConditionTreeException;
import com.elasticpath.tags.service.TagDefinitionService;
import com.elasticpath.validation.domain.ValidationResult;

/**
 * Builds Groovy implementation of {@link ConditionDSLBuilder}.
 */
public class GroovyConditionDSLBuilderImpl implements ConditionDSLBuilder {
	
	private static final String TAG_DEFINITIONS_MAP_VARIABLE_NAME = "tagDefinitionsMap";
    
    private TagDefinitionService tagDefinitionService;
    
    private ConditionValidationFacade validationFacade;
    
    private static final int DEFAULT_CACHE_TIMEOUT = 60000;
	
	private final SimpleTimeoutCache<String, Map<String, TagDefinition>> cache = 
		new SimpleTimeoutCache<String, Map<String, TagDefinition>>(DEFAULT_CACHE_TIMEOUT);
	
    
    /**
     * initializes groovy shell by providing map of tag definitions as a 
     * variable available to scripts.
     * @return the new groovy shell.
     */
	GroovyShell initializeGroovyShellWithTagDefinitions() {
    	
    	synchronized (this) {

    		loadTagDefinitionsIntoCache();
    		
    		final Map<String, Object> variables = new HashMap<String, Object>();
    		variables.put(TAG_DEFINITIONS_MAP_VARIABLE_NAME, cache.get(TAG_DEFINITIONS_MAP_VARIABLE_NAME));
    		
    		final Binding binding = new Binding(variables);
    		
    		return new GroovyShell(binding);
    	}
    	
    }
    
    /**
     * Load all tag definitions using tag service and put it to cache.
     */
    void loadTagDefinitionsIntoCache() {
		final List<TagDefinition> tagDefinitions = tagDefinitionService.getTagDefinitions();
		
		final Map<String, TagDefinition> tagDefinitionsMap = new HashMap<String, TagDefinition>();
		for (TagDefinition tagDefinition : tagDefinitions) {
			tagDefinitionsMap.put(tagDefinition.getGuid(), tagDefinition);
		}
		
		cache.put(TAG_DEFINITIONS_MAP_VARIABLE_NAME, tagDefinitionsMap);
    }
    
    /**
     * Builds logical operator tree. A {@link LogicalOperator} can contain other logical operators or conditions.
     *
     * @param dslString a DSL representation of logical operator
     * @return a logical operator which represents the given dslString, returns null 
     * if there is a service exception or dsl string is not valid
     */
    public LogicalOperator getLogicalOperationTree(final String dslString) {        
        if (StringUtils.isEmpty(dslString)) {
        	return null;
        }
        
        Script compiledScript = null;
        try {
        	compiledScript = (initializeGroovyShellWithTagDefinitions()).parse(
							              "import com.elasticpath.tags.builder.*\n"
	              						+ "import com.elasticpath.tags.domain.*\n"
	              						
		              				  	+ "def propertyMissing(String name) {\n"
		              				    + "	   new BuilderString(tagDefinitionsMap.get(name))\n"
		              				    + "}\n"

		              				    + "def methodMissing(String name, args) { \n"
		              				    + "    def LogicalOperatorType operatorType = Enum.valueOf(LogicalOperatorType.class, name)\n"
		              				    + "    LogicalTreeBuilder.getInstance().addLogicalOperator(operatorType, args)\n"
		              				    + "}\n"

	              				  		+ "def getCondition() {\n"
							            + "    def dslClosure = "
							            +      dslString
							            + "\n  dslClosure.call()"
								        + "\n}\n"
								        
								        + "getCondition()"
	        					);
        
        	return (LogicalOperator) compiledScript.run();
        } finally {
        	if (compiledScript != null) {
        		InvokerHelper.removeClass(compiledScript.getClass());
        	}
        }
    }

    /**
     * Builds DSL string from given logical operator. Please refer to user documentation about DSL string syntax.
     * If a null reference given, it returns an empty string.
     *
     * @param rootNode a root node of logical operator tree to be represented as DSL string
     * @return a DSL string that represents the logical operator tree
     * @throws InvalidConditionTreeException is thrown when the condition tree syntax or values are invalid 
     *                                (exception message to contain a message for UI) 
     */
    public String getConditionalDSLString(final LogicalOperator rootNode) throws InvalidConditionTreeException {
    	if (rootNode == null) {
    		return StringUtils.EMPTY;
    	}
    	
    	final ValidationResult result = validationFacade.validateTree(rootNode);
    	
    	if (!result.isValid()) {
    		throw new InvalidConditionTreeException(result);
    	}
    	
        return this.traverseConditionTree(rootNode);
    }

    /**
     * Traverses the logical operator tree recursively and builds the condition string.
     * 
     * @param logicalOperator a logical operator to be traversed
     * @return a DSL string
     */
    private String traverseConditionTree(final LogicalOperator logicalOperator) {
        StringBuffer conditionString = new StringBuffer();

        conditionString.append(" { ");
        conditionString.append(logicalOperator.getOperatorType().toString());
        conditionString.append(addConditions(logicalOperator));

        for (LogicalOperator operator : logicalOperator.getLogicalOperators()) {
            conditionString.append(traverseConditionTree(operator));
        }

        conditionString.append(" } ");

        return conditionString.toString();
    }

    /**
     * Builds the condition strings for given logical operator node.
     * Right operand of the condition object has to be primitive type (or their wrappers)
     * or has to be String. If a new type is introduced, it has to be covered here too.
     *
     * @param logicalOperator a node from root logical operator, also might be root too.
     * @return a string that contains conditional DSL representation of the node
     */
    private String addConditions(final LogicalOperator logicalOperator) {
        StringBuffer conditionString = new StringBuffer();

        for (Condition condition : logicalOperator.getConditions()) {
            buildCondition(conditionString, condition);
        }

        return conditionString.toString();
    }

    /**
     * Complex condition string is built from several {@link Condition} instances. Each of them is 
     * being processed and appended to the condition string in this method.
     * 
     * @param conditionString condition string that is being built
     * @param condition {@link Condition} instance that will be parsed and appended to conditionString
     */
	protected void buildCondition(final StringBuffer conditionString,
			final Condition condition) {
		TagDefinition tagDefinition = condition.getTagDefinition();
		TagValueType tagValueType = tagDefinition.getValueType();
		conditionString.append(" { ");
		conditionString.append(tagDefinition.getGuid());
		conditionString.append('.');
		conditionString.append(condition.getOperator());
		conditionString.append(' ');
		final String dataType = tagValueType.getJavaType();
		final DSLValueDecorator decorator = getValueDecorator(dataType, condition.getTagValue());
		conditionString.append(decorator.decorate());
		conditionString.append(" } ");
	}
	
	private DSLValueDecorator getValueDecorator(final String dataType, final Object value) {
		return GroovyDSLValueDecoratorFactory.getValueDecorator(dataType, value);
	}
	    
	/**
	 * Sets tag definition service.
	 * 
	 * @param tagDefinitionService tag definition service
	 */
	public void setTagDefinitionService(final TagDefinitionService tagDefinitionService) {
		this.tagDefinitionService = tagDefinitionService;
	}
	
	/**
	 * @param validationFacade validation facade that will allow to validate logical
	 * operator trees before they get converted into conditional expression string.
	 */
	public void setValidationFacade(final ConditionValidationFacade validationFacade) {
		this.validationFacade = validationFacade;
	}
	
	/**
	 * Set the cache timeout for tag definitions.
	 * @param timeout time in milliseconds
	 */
	public void setTagDefinitionCacheTimeout(final long timeout) {
		cache.setTimeout(timeout);
	}

    
}