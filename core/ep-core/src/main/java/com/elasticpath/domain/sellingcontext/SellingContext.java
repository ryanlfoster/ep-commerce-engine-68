package com.elasticpath.domain.sellingcontext;

import java.util.Map;

import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.tags.TagSet;
import com.elasticpath.tags.domain.ConditionalExpression;
import com.elasticpath.tags.service.ConditionEvaluatorService;

/**
 * Selling context defines the who, when and where conditions.
 */
public interface SellingContext extends Persistable {
	
    /**
     * @return the name of the 'Selling Context'
     */
    String getName();

    /**
     * Sets a name for the 'Selling Context'.
     *
     * @param name a name to be set
     */
    void setName(String name);

    /**
     * @return the description of the 'Selling Context'
     */
    String getDescription();

    /**
     * Sets the description of the 'Selling Context'.
     *
     * @param description a description to be set
     */
    void setDescription(String description);

    /**
     * @return the priority of the 'Selling Context'
     */
    int getPriority();

    /**
     * Sets the priority of the 'Selling Context'.
     * The lower the number set for priority the higher rank it has i.e. 
     * 0 - the highest, Integer.MAX_VALUE - the lowest 
     * @param priority a priority to be set
     */
    void setPriority(int priority);
    
    /**
     * @return the conditional expression for the SHOPPER condition
     */
    ConditionalExpression getShopperCondition();

    /**
     * @return the conditional expression for the TIME condition
     */
    ConditionalExpression getTimeCondition();

    /**
     * @return the conditional expression for the STORE condition
     */
    ConditionalExpression getStoresCondition();

    /**
     * @return the Guid.
     */
    String getGuid();

    /**
     * Set the Guid.
     *
     * @param guid the Guid to set.
     */
    void setGuid(String guid);
	
    /**
     * uses conditions evaluation service in order to evaluate the conditions of this
     * selling context.
     *
     * @param conditionsEvaluationService the evaluation service
     * @param tagSet the tag set with condition values
     * @param tagDefinitonGuids the dictionaries to use for evaluation (may be null)
     * @return true if all conditions are satisfied, false otherwise
     */
    boolean isSatisfied(ConditionEvaluatorService conditionsEvaluationService, 
    		TagSet tagSet, String ... tagDefinitonGuids);
	
    /**
     * set a condition for this selling context. each condition MUST relate to a
     * tag dictionary by providing tag dictionary GUID
     *
     * @param tagDictionaryGuid the tag dictionary guid
     * @param expression the condition
     */
    void setCondition(String tagDictionaryGuid, ConditionalExpression expression);
    
    /**
     * get a condition for this selling context.
     *
     * @param tagDefinitionGuid the tag dictionary guid
     * @return conditional expression
     */
    ConditionalExpression getCondition(String tagDefinitionGuid);
    
    /**
     * get all conditions for this selling context for all dictionaries.
     *
     * @return map of conditions by tag dictionary guid's
     */
    Map<String, ConditionalExpression> getConditions();
}
