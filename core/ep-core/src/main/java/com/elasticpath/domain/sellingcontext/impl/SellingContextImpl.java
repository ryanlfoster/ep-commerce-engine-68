package com.elasticpath.domain.sellingcontext.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.MapKey;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import org.apache.openjpa.persistence.DataCache;
import org.apache.openjpa.persistence.FetchAttribute;
import org.apache.openjpa.persistence.FetchGroup;
import org.apache.openjpa.persistence.jdbc.Unique;

import com.elasticpath.commons.constants.GlobalConstants;
import com.elasticpath.domain.sellingcontext.SellingContext;
import com.elasticpath.persistence.api.AbstractEntityImpl;
import com.elasticpath.persistence.support.FetchGroupConstants;
import com.elasticpath.tags.TagSet;
import com.elasticpath.tags.domain.ConditionalExpression;
import com.elasticpath.tags.domain.TagDictionary;
import com.elasticpath.tags.domain.impl.ConditionalExpressionImpl;
import com.elasticpath.tags.service.ConditionEvaluatorService;


/**
 * {@link com.elasticpath.domain.sellingcontext.SellingContext} implementation class.
 */
@Entity
@Table(name = SellingContextImpl.TABLE_NAME)
@DataCache(enabled = false)
@FetchGroup(name = FetchGroupConstants.PROMOTION_INDEX, attributes = { @FetchAttribute(name = "conditions") })
@DiscriminatorColumn(name = "TYPE", discriminatorType = DiscriminatorType.STRING)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorValue("Selling Context")
public class SellingContextImpl extends AbstractEntityImpl implements SellingContext {

	/**
     * Database Table.
     */
    public static final String TABLE_NAME = "TSELLINGCONTEXT";
    
    private static final String JOIN_TABLE_NAME_CONDITIONS = "TSELLINGCONTEXTCONDITION";
    private static final String JOIN_COLUMN_UID_SELLING_CONTEXT = "SELLING_CONTEXT_UID";
    private static final String JOIN_COLUMN_GUID_CONDITION = "CONDITION_GUID";
    private static final String JOIN_COLUMN_GUID_CONDITION_INVERSE = "GUID";
    private static final String MAP_BY_TAGDICTIONARY_GUID = "tagDictionaryGuid";

    /**
     * Serial version id.
     */
    private static final long serialVersionUID = 5000000001L;
    
    /*
     * TODO need investigation. ATM we have ambiguity between  
     * Dynamic Content Delivery Selling context  and Price Lists Assignment context.
     * DCD use DICTIONARY_SHOPPER_GUID,     DICTIONARY_TIME_GUID, DICTIONARY_STORES_GUID
     * PLA use DICTIONARY_PLA_SHOPPER_GUID, DICTIONARY_TIME_GUID, DICTIONARY_STORES_GUID
     * But DICTIONARY_PLA_SHOPPER_GUID is subset of DICTIONARY_SHOPPER_GUID
     *  
     * */
	/**
	 * Default set of tags used for evaluation.
	 */
	private static final String[] DEFAULT_EVALUATION_TAGS = {
    	TagDictionary.DICTIONARY_SHOPPER_GUID, 
    	TagDictionary.DICTIONARY_TIME_GUID, 
    	TagDictionary.DICTIONARY_STORES_GUID,
    	TagDictionary.DICTIONARY_PLA_SHOPPER_GUID,
		TagDictionary.DICTIONARY_PROMOTIONS_SHOPPER_GUID,
		TagDictionary.DICTIONARY_OFFER_SHOPPER_GUID
    	};

    private long uidPk;
    private String name;
    private String description;
    private int priority;
    
    private Map<String, ConditionalExpression> conditions = new HashMap<String, ConditionalExpression>();

	private String guid;

	/**
	 * Gets the list of default evaluation tags.
	 * 
	 * @return list of default evaluation tags
	 */
	@Transient
	protected List<String> getDefaultEvaluationTags() {
		// new list to allow modification of result without changing our default evaluation tags
		return new ArrayList<String>(Arrays.asList(DEFAULT_EVALUATION_TAGS));
	}

    @Override
    @Basic
    @Column(name = "NAME")
    public String getName() {
        return name;
    }

    @Override
    public void setName(final String name) {
        this.name = name;
    }

    @Override
    @Basic
    @Column(name = "DESCRIPTION", length = GlobalConstants.SHORT_TEXT_MAX_LENGTH)
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(final String description) {
        this.description = description;
    }

    @Override
    @Basic
    @Column(name = "PRIORITY")
    public int getPriority() {
        return priority;
    }

    @Override
    public void setPriority(final int priority) {
        this.priority = priority;
    }

    @Override
    @Transient
    public ConditionalExpression getShopperCondition() {
    	return getCondition(TagDictionary.DICTIONARY_SHOPPER_GUID);
    }

    @Override
    @Transient
    public ConditionalExpression getTimeCondition() {
    	return getCondition(TagDictionary.DICTIONARY_TIME_GUID);
    }

    @Override
    @Transient
    public ConditionalExpression getStoresCondition() {
    	return getCondition(TagDictionary.DICTIONARY_STORES_GUID);
    }

    @Override
    public void setCondition(final String tagDictionaryGuid, final ConditionalExpression expression) {
    	if (expression == null) {
    		// if we are setting condition to null we need to remove it
    		if (getConditions().containsKey(tagDictionaryGuid)) {
    			getConditions().remove(tagDictionaryGuid);
    		}
    	} else {
    		// if condition is good to go we need to make sure that the dictionary guid is properly set
    		expression.setTagDictionaryGuid(tagDictionaryGuid);
    		getConditions().put(tagDictionaryGuid, expression);
    	} 
    }
    
    @Override
    @Transient
    public ConditionalExpression getCondition(final String tagDefinitionGuid) {
    	return getConditions().get(tagDefinitionGuid);
    }
    
    /**
     * Get all conditions for this selling context for all dictionaries.
     * <br><b>NOTE: Currently has ElementDependent annotation - to be revisited when the Named Conditions concept will be introduced</b>
     * @return map of conditions by tag dictionary guid's
     */
    @ManyToMany(targetEntity = ConditionalExpressionImpl.class, cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
    @MapKey(name = MAP_BY_TAGDICTIONARY_GUID)
    @JoinTable(name = JOIN_TABLE_NAME_CONDITIONS, 
    		joinColumns = { @JoinColumn(name = JOIN_COLUMN_UID_SELLING_CONTEXT) },
    		inverseJoinColumns = { @JoinColumn(name = JOIN_COLUMN_GUID_CONDITION, referencedColumnName = JOIN_COLUMN_GUID_CONDITION_INVERSE) })
    public Map<String, ConditionalExpression> getConditions() {
    	return conditions;
    }
    
    /**
     * set conditions (needed by JPA).
     *
     * @param conditions the conditions passed to selling context
     */
    public void setConditions(final Map<String, ConditionalExpression> conditions) {
    	this.conditions = conditions;
    }
    
    /**
     * @return the guid.
     */
    @Override
    @Basic
    @Column(name = "GUID")
    @Unique(name = "TSELLINGCONTEXT_UNIQUE")
    public String getGuid() {
    	return guid;
    }

    /**
     * Set the guid.
     *
     * @param guid the guid to set.
     */
    @Override
    public void setGuid(final String guid) {
		this.guid = guid;
    }

    @Override
    @Id
    @Column(name = "UIDPK")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
    @TableGenerator(name = TABLE_NAME, table = "JPA_GENERATED_KEYS", pkColumnName = "ID", valueColumnName = "LAST_VALUE", pkColumnValue = TABLE_NAME)
    public long getUidPk() {
        return this.uidPk;
    }

    @Override    
    public void setUidPk(final long uidPk) {
        this.uidPk = uidPk;
    }

    /**
     * Generate the hash code.
     *
     * @return the hash code.
     */
    @Override
    public int hashCode() {
        if (getGuid() == null) {
            return 0;
        }

        return getGuid().hashCode();
    }

    /**
     * Determines whether the given object is equal to this 'SellingContext'.
     *
     * @param obj to be compared for equality
     * @return true if the given object's GUID is equal to this one's GUID
     */
    @Override
    public boolean equals(final Object obj) {
        return obj instanceof SellingContext && this.getGuid() != null && this.getGuid().equals(((SellingContext) obj).getGuid());

    }
    
    /**
     * @return the string representation of the {@link com.elasticpath.domain.sellingcontext.SellingContext}
     */
    @Override
    public String toString() {
        return "[SellingContext:"
                + " GUID=" + this.getGuid()
                + ", Name=" + this.getName()
                + ", Description=" + this.getDescription()
                + ", Who=" + this.getShopperCondition()
                + ", When=" + this.getTimeCondition()
                + ", Where=" + this.getStoresCondition()
                + "]";
    }
    
    /**
     * uses conditions evaluation service in order to evaluate the conditions of this
     * selling context.
     *
     * @param conditionsEvaluationService the evaluation service
     * @param tagSet the tag set with condition values
     * @param tagDefinitonGuids the dictionaries to use for evaluation (may be null)
     * @return true if all conditions are satisfied, false otherwise
     */
    public boolean isSatisfied(final ConditionEvaluatorService conditionsEvaluationService, 
    		final TagSet tagSet, final String ... tagDefinitonGuids) {
		List<String> useDictionaries = getDefaultEvaluationTags();
    	
    	if (tagDefinitonGuids != null && tagDefinitonGuids.length > 0) {
		useDictionaries = Arrays.asList(tagDefinitonGuids);
    	}
    	
    	boolean satisfied = true;
    	for (String tag : useDictionaries) {
    		satisfied &= isConditionSatisfied(conditionsEvaluationService, getCondition(tag), tagSet);
    	}
    	return satisfied;
    }

    /**
     * evaluate a single condition of this selling context.
     *
     * @param conditionEvaluatorService the evaluation service
     * @param expression the expression to evaluate
     * @param tagSet the conditions prerequisites
     * @return true if this condition is satisfied, false otherwise
     */
    private boolean isConditionSatisfied(final ConditionEvaluatorService conditionEvaluatorService, 
    		final ConditionalExpression expression, final TagSet tagSet) {
    	if (expression == null) {
    		return true;
    	}
    	return conditionEvaluatorService.evaluateConditionOnTags(tagSet, expression);
    }
}
