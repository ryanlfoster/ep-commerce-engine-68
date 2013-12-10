package com.elasticpath.test.factory;

import java.util.HashSet;
import java.util.Set;

import com.elasticpath.domain.catalog.impl.CatalogImpl;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.rules.RuleAction;
import com.elasticpath.domain.rules.RuleCondition;
import com.elasticpath.domain.rules.RuleParameter;
import com.elasticpath.domain.rules.RuleScenarios;
import com.elasticpath.domain.rules.RuleSet;
import com.elasticpath.domain.rules.impl.CartCategoryPercentDiscountActionImpl;
import com.elasticpath.domain.rules.impl.CartCurrencyConditionImpl;
import com.elasticpath.domain.rules.impl.CartNFreeSkusActionImpl;
import com.elasticpath.domain.rules.impl.CartNthProductPercentDiscountActionImpl;
import com.elasticpath.domain.rules.impl.ProductInCartConditionImpl;
import com.elasticpath.domain.rules.impl.PromotionRuleImpl;
import com.elasticpath.domain.rules.impl.RuleParameterImpl;
import com.elasticpath.domain.rules.impl.RuleSetImpl;
import com.elasticpath.domain.rules.impl.SkuInCartConditionImpl;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.impl.StoreImpl;

/**
 * Test utility for creating ruleset for testings.
 */
public final class RuleSetTestUtility {

    /**
     * Default name.
     */
    public static final String RULE_SET_NAME = "com.elasticpath.promotionrules";

   /**
    * Default rule_uid, use a real LONG number to check we can handle the long number.
    */
    public static final long RULE_UID = 2176843776L;
    
    /**
     * Default store.
     */
    public static final Store STORE = getStore();

    /**
     * Create a rule set containing the rule: When a cart contains SKU x, apply a discount to items of category y.
     * 
     * @return a <code>RuleSet</code>
     */
    public static RuleSet createCartCategoryDiscountRuleSet() {

        RuleSet ruleSet = new RuleSetImpl();
        ruleSet.setName(RULE_SET_NAME);
        ruleSet.setScenario(RuleScenarios.CART_SCENARIO);

        Rule promotionRule = new PromotionRuleImpl() {

			private static final long serialVersionUID = -4609628217125302055L;

			private final Set<RuleAction> actions = new HashSet<RuleAction>();

            @Override
            public void addAction(final RuleAction ruleAction) { // NOPMD
                this.actions.add(ruleAction);
            }

            @Override
            public void removeAction(final RuleAction ruleAction) {
                this.actions.remove(ruleAction);
            }

            @Override
            public Set<RuleAction> getActions() {
                return this.actions;
            }

        };
        promotionRule.setName("Order Sale");
        promotionRule.setStore(getStore());
        promotionRule.setUidPk(RULE_UID);

        // Create a condition that the cart contains at least 3 items with a given sku
        RuleCondition skuInCartCondition = new SkuInCartConditionImpl();
        RuleParameter skuCodeParam = new RuleParameterImpl();
        skuCodeParam.setKey(RuleParameter.SKU_CODE_KEY);
        skuCodeParam.setValue("I2004FR12C");
        skuInCartCondition.addParameter(skuCodeParam);
        RuleParameter numItemsQuantifierParam = new RuleParameterImpl();
        numItemsQuantifierParam.setKey(RuleParameter.NUM_ITEMS_QUANTIFIER_KEY);
        numItemsQuantifierParam.setValue("AT_LEAST");
        skuInCartCondition.addParameter(numItemsQuantifierParam);
        RuleParameter quantityParam = new RuleParameterImpl();
        quantityParam.setKey(RuleParameter.NUM_ITEMS_KEY);
        quantityParam.setValue("3");
        skuInCartCondition.addParameter(quantityParam);
        promotionRule.addCondition(skuInCartCondition);

        // Create a condition that the currency is CAD
        RuleCondition currencyCondition = new CartCurrencyConditionImpl();
        RuleParameter currencyParam = new RuleParameterImpl();
        currencyParam.setKey(RuleParameter.CURRENCY_KEY);
        currencyParam.setValue("CAD");
        currencyCondition.addParameter(currencyParam);
        promotionRule.addCondition(currencyCondition);

        // //Create an exception
        // RuleCondition skuException = new SkuExceptionImpl();
        // RuleParameter skuParam = new RuleParameterImpl();
        // skuParam.setKey(RuleParameter.SKU_CODE_KEY);
        // skuParam.setValue("APACHE001");
        // skuException.addParameter(skuParam);
        // promotionRule.addException(skuException);

        // Create an action
        RuleAction discountAction = new CartCategoryPercentDiscountActionImpl();
        RuleParameter discountParameter = new RuleParameterImpl();
        discountParameter.setKey(RuleParameter.DISCOUNT_PERCENT_KEY);
        discountParameter.setValue("50");
        discountAction.addParameter(discountParameter);
        RuleParameter categoryParameter = new RuleParameterImpl();
        categoryParameter.setKey(RuleParameter.CATEGORY_CODE_KEY);
        categoryParameter.setValue("10");
        discountAction.addParameter(categoryParameter);
        RuleParameter numItemsParameter = new RuleParameterImpl();
        numItemsParameter.setKey(RuleParameter.NUM_ITEMS_KEY);
        numItemsParameter.setValue("10");
        discountAction.addParameter(numItemsParameter);
        promotionRule.addAction(discountAction);

        promotionRule.setRuleSet(ruleSet);
        ruleSet.addRule(promotionRule);
        return ruleSet;
    }
    
    /**
     * Create a rule set containing the rule: When a cart contains product x, customer gets third item of x free.
     * 
     * @return a <code>RuleSet</code> specifying this rule.
     */
    public static RuleSet createBuyTwoGetOneFreeRuleSet() {

        RuleSet ruleSet = new RuleSetImpl();
        ruleSet.setName(RULE_SET_NAME);
        ruleSet.setScenario(RuleScenarios.CART_SCENARIO);

        Rule promotionRule = new PromotionRuleImpl() {

			private static final long serialVersionUID = -2161033927460564406L;

			private final Set<RuleAction> actions = new HashSet<RuleAction>();
            
            @Override
            public void addAction(final RuleAction ruleAction) { //NOPMD
                this.actions.add(ruleAction);
            }
            
            @Override
            public void removeAction(final RuleAction ruleAction) {
                this.actions.remove(ruleAction);
            }
            
            @Override
            public Set<RuleAction> getActions() {
                return this.actions;
            }
            
        };
        promotionRule.setName("Buy two get one free");
        promotionRule.setStore(getStore());
        promotionRule.setUidPk(RULE_UID);
        

        // Create a condition that the product is in the cart
        RuleCondition productCondition = new ProductInCartConditionImpl();
        productCondition.addParameter(new RuleParameterImpl(RuleParameter.PRODUCT_CODE_KEY, "10"));
        productCondition.addParameter(new RuleParameterImpl(RuleParameter.NUM_ITEMS_QUANTIFIER_KEY, "AT_LEAST"));
        productCondition.addParameter(new RuleParameterImpl(RuleParameter.NUM_ITEMS_KEY, "1"));
        promotionRule.addCondition(productCondition);

        // Create an action to discount the third item of the product
        RuleAction discountAction = new CartNthProductPercentDiscountActionImpl();
        discountAction.addParameter(new RuleParameterImpl(RuleParameter.DISCOUNT_PERCENT_KEY, "100"));
        discountAction.addParameter(new RuleParameterImpl(RuleParameter.PRODUCT_CODE_KEY, "10"));
        discountAction.addParameter(new RuleParameterImpl(RuleParameter.NUM_ITEMS_KEY, "3"));
        promotionRule.addAction(discountAction);
        promotionRule.setRuleSet(ruleSet);

        ruleSet.addRule(promotionRule);
        return ruleSet;

    }

    /**
     * Create a rule set containing the rule: When a cart contains product x, customer gets N free items of y.
     * 
     * @return a <code>RuleSet</code> specifying this rule.
     */
    public static RuleSet createBuyOneYGetZFreeItemsOfX() {

        RuleSet ruleSet = new RuleSetImpl();
        ruleSet.setName(RULE_SET_NAME);
        ruleSet.setScenario(RuleScenarios.CART_SCENARIO);

        Rule promotionRule = new PromotionRuleImpl() {
			private static final long serialVersionUID = 3091914846751183683L;

			private final Set<RuleAction> actions = new HashSet<RuleAction>();
            
            @Override
            public void addAction(final RuleAction ruleAction) { //NOPMD
                this.actions.add(ruleAction);
            }
            
            @Override
            public void removeAction(final RuleAction ruleAction) {
                this.actions.remove(ruleAction);
            }
            
            @Override
            public Set<RuleAction> getActions() {
                return this.actions;
            }
        };
        promotionRule.setName("Buy one get three free");
        
        promotionRule.setStore(new StoreImpl());
        
        // Create a condition that the product is in the cart
        RuleCondition productCondition = new ProductInCartConditionImpl();
        productCondition.addParameter(new RuleParameterImpl(RuleParameter.PRODUCT_CODE_KEY, "10"));
        productCondition.addParameter(new RuleParameterImpl(RuleParameter.NUM_ITEMS_QUANTIFIER_KEY, "AT_LEAST"));
        productCondition.addParameter(new RuleParameterImpl(RuleParameter.NUM_ITEMS_KEY, "1"));
        promotionRule.addCondition(productCondition);

        // Create an action to discount to give 3 free items of another product
        RuleAction discountAction = new CartNFreeSkusActionImpl();
        discountAction.addParameter(new RuleParameterImpl(RuleParameter.SKU_CODE_KEY, "17"));
        discountAction.addParameter(new RuleParameterImpl(RuleParameter.NUM_ITEMS_KEY, "3"));
        promotionRule.addAction(discountAction);
        promotionRule.setRuleSet(ruleSet);

        ruleSet.addRule(promotionRule);
        return ruleSet;

    }



    /**
     * @return
     */
    private static Store getStore() {
        Store store = new StoreImpl();
        store.setCatalog(new CatalogImpl());
        return store;
    }

    private RuleSetTestUtility() {
        
    }
}
