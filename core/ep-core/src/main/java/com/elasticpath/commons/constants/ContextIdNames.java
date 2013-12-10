/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.commons.constants;

/**
 * <code>ContextIdNames</code> contains spring container bean id constants. Only core com.elasticpath.core classes should be added in here.
 */
@SuppressWarnings({ "PMD.ExcessiveClassLength" })
public final class ContextIdNames {
    /** bean id for implementation of com.elasticpath.commons.context.ElasticPath. */
    public static final String ELASTICPATH = "elasticPath";

    /** bean id for implementation of com.elasticpath.domain.persistence.PersistenceEngine. */
    public static final String PERSISTENCE_ENGINE = "persistenceEngine";

    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ //
    /** bean id for implementation of com.elasticpath.domain.CategoryType. */
    public static final String CATEGORY_TYPE = "categoryType";

    /** bean id for implementation of com.elasticpath.domain.Customer. */
    public static final String CUSTOMER = "customer";

    /** bean id for implementation of com.elasticpath.domain.CustomerDeleted. */
    public static final String CUSTOMER_DELETED = "customerDeleted";

    /** bean id for implementation of com.elasticpath.domain.customer.CustomerAuthentication. */
    public static final String CUSTOMER_AUTHENTICATION = "customerAuthentication";

    /** bean id for implementation of com.elasticpath.domain.CustomerGroup. */
    public static final String CUSTOMER_GROUP = "customerGroup";

    /** bean id for implementation of com.elasticpath.domain.CustomerSession. */
    public static final String CUSTOMER_SESSION = "customerSession";

    /** bean id for implementation of com.elasticpath.domain.customer.CustomerSessionMemento. */
    public static final String CUSTOMER_SESSION_MEMENTO = "customerSessionMemento";

    /** bean id for implementation of com.elasticpath.service.customer.CustomerSessionService. */
    public static final String CUSTOMER_SESSION_SERVICE = "customerSessionService";

    /** bean id for implementation of com.elasticpath.domain.customer.Address. */
    public static final String CUSTOMER_ADDRESS = "customerAddress";

    /** bean id for implementation of com.elasticpath.domain.customer.CustomerCreditCard. */
    public static final String CUSTOMER_CREDIT_CARD = "customerCreditCard";

    /** bean id for implementation of com.elasticpath.util.impl.RandomGuid. */
    public static final String RANDOM_GUID = "randomGuid";

    /** bean id for implementation of com.elasticpath.domain.CmUser. */
    public static final String CMUSER = "cmUser";

    /** bean id for implementation of com.elasticpath.domain.cmuser.UserPasswordHistoryItem. */
    public static final String USER_PASSWORD_HISTORY_ITEM = "userPasswordHistoryItem";

    /** bean id for implementation of com.elasticpath.domain.CmUserSession. */
    public static final String CM_USER_SESSION = "cmUserSession";

    /** bean id for implementation of com.elasticpath.domain.catalog.impl.AbstractCategoryImpl. */
    public static final String ABSTRACT_CATEGORY = "abstractCategory";

    /** bean id for implementation of com.elasticpath.domain.catalog.impl.CategoryImpl. */
    public static final String CATEGORY = "category";

    /** bean id for implementation of com.elasticpath.domain.catalog.impl.LinkedCategoryImpl. */
    public static final String LINKED_CATEGORY = "linkedCategory";

    /** bean id for implementation of com.elasticpath.domain.CategoryDeleted. */
    public static final String CATEGORY_DELETED = "categoryDeleted";

    /** bean id for implementation of com.elasticpath.domain.Product. */
    public static final String PRODUCT = "product";

    /** bean id for implementation of com.elasticpath.domain.ProductDeleted. */
    public static final String PRODUCT_DELETED = "productDeleted";

    /** bean id for implementation of com.elasticpath.domain.ProductSku. */
    public static final String PRODUCT_SKU = "productSku";

    /** bean id for implementation of com.elasticpath.domain.ProductType. */
    public static final String PRODUCT_TYPE = "productType";

    /** bean id for {@link com.elasticpath.domain.catalog.ProductBundle}. */
    public static final String PRODUCT_BUNDLE = "productBundle";

    /** bean id for {@link com.elasticpath.domain.catalog.BundleConstituent}. */
    public static final String BUNDLE_CONSTITUENT = "bundleConstituent";

    /** bean id for {@link com.elasticpath.service.catalog.BundleConstituentFactory}. */
    public static final String BUNDLE_CONSTITUENT_FACTORY = "bundleConstituentFactory";

    /** bean id for {@link com.elasticpath.service.catalog.ProductBundleService}. */
    public static final String PRODUCT_BUNDLE_SERVICE = "productBundleService";

    /** bean id for implementation of com.elasticpath.domain.ProductType. */
    public static final String PRODUCT_TYPE_PRODUCT_ATTRIBUTE = "productTypeProductAttribute";

    /** bean id for product type implementation of com.elasticpath.domain.attribute.AttributeGroup. */
    public static final String PRODUCT_TYPE_SKU_ATTRIBUTE = "productTypeSkuAttribute";

    /** bean id for product type implementation of com.elasticpath.domain.attribute.AttributeGroup. */
    public static final String CATEGORY_TYPE_ATTRIBUTE = "categoryTypeAttribute";

    /** bean id for implementation of com.elasticpath.domain.CustomerProfileValue. */
    public static final String CUSTOMER_PROFILE_VALUE = "customerProfileValue";

    /** bean id for implementation of com.elasticpath.domain.Attribute. */
    public static final String ATTRIBUTE = "attribute";

    /** bean id for implementation of com.elasticpath.domain.AttributeValue. */
    public static final String ATTRIBUTE_VALUE = "attributeValue";

    /** bean id for implementation of com.elasticpath.domain.ShoppingCart. */
    public static final String SHOPPING_CART = "shoppingCart";

    /** bean id for implementation of com.elasticpath.domain.misc.MoneyFormatter. */
    public static final String MONEY_FORMATTER = "moneyFormatter";

    /** bean id for implementation of com.elasticpath.domain.campaign.ContentSpace. */
    public static final String CONTENTSPACE = "contentspace";

    /** bean id for implementation of com.elasticpath.domain.campaign.DynamicContent. */
    public static final String DYNAMIC_CONTENT = "dynamicContent";

    /** bean id for implementation of com.elasticpath.domain.targetedselling.DynamicContentDelivery. */
    public static final String DYNAMIC_CONTENT_DELIVERY = "dynamicContentDelivery";

    /** bean id for implementation of com.elasticpath.service.targetedselling.DynamicContentDeliveryService. */
    public static final String DYNAMIC_CONTENT_DELIVERY_SERVICE = "dynamicContentDeliveryService";

    /** bean id for implementation of com.elasticpath.service.contentspace.impl.DynamicContentServiceImpl. */
    public static final String DYNAMIC_CONTENT_SERVICE = "dynamicContentService";

    /** bean id for implementation of com.elasticpath.service.contentspace.impl.ContentSpaceServiceImpl. */
    public static final String CONTENTSPACE_SERVICE = "contentspaceService";

    /** bean id for implementation of com.elasticpath.domain.contentspace.impl.ParameterValueImpl. */
    public static final String DYNAMIC_CONTENT_WRAPPER_PARAMETER_VALUE = "dynamicContentParameterValue";

    /** bean id for implementation of com.elasticpath.domain.contentspace.impl.ParameterLocaleDependantValueImpl. */
    public static final String DYNAMIC_PARAMETER_LDF_VALUE = "dynamicContentParameterLocaleDependantValue";

    /** bean id for implementation of com.elasticpath.domain.contentspace.impl.ParameterImpl. */
    public static final String DYNAMIC_CONTENT_WRAPPER_TEMPLATE_PARAMETER = "dynamicContentTemplateParameter";

    /** bean id for implementation of com.elasticpath.domain.contentspace.impl.ParameterImpl. */
    public static final String DYNAMIC_CONTENT_WRAPPER_USER_INPUT_PARAMETER = "dynamicContentUserInputParameter";



    /**
     * bean id for implementation of com.elasticpath.service.tax.TaxCalculationResult.
     */
    public static final String TAX_CALCULATION_RESULT = "taxCalculationResult";

    /** bean id for implementation of com.elasticpath.domain.rules.EpRuleEngine. */
    public static final String EP_RULE_ENGINE = "epRuleEngine";

    /** bean id for implementation of com.elasticpath.domain.rules.PromotionRuleDelegate. */
    public static final String PROMO_RULE_HELPER = "promotionRuleDelegate";

    /** bean id for implementation of com.elasticpath.domain.rules.RuleSet. */
    public static final String RULE_SET = "ruleSet";

    /** bean id for implementation of com.elasticpath.domain.rules.RuleParameter. */
    public static final String RULE_PARAMETER = "ruleParameter";

    /** bean id for implementation of com.elasticpath.domain.rules.PromotionRule. */
    public static final String PROMOTION_RULE = "promotionRule";

    /** bean id for implementation of com.elasticpath.domain.rules.RuleScenarios. */
    public static final String RULE_SCENARIOS = "scenarios";

    /** bean id for implementation of com.elasticpath.domain.shoppingcart.ShoppingItem. */
    public static final String SHOPPING_ITEM = "shoppingItem";

    /** bean id for implementation of com.elasticpath.domain.shoppingcart.wishlistItem. */
    public static final String WISH_LIST_ITEM = "wishListItem";

    /** bean id for implementation of ccom.elasticpath.domain.shoppingcart.WishListMessage. */
    public static final String WISH_LIST_MESSAGE = "wishListMessage";

    /** bean id for implementation of com.elasticpath.domain.UserRole. */
    public static final String USER_ROLE = "userRole";

    /** bean id for implementation of com.elasticpath.domain.order.OrderPayment. */
    public static final String ORDER_PAYMENT = "orderPayment";

    /** bean id for abstract order shipment - physical and electronic derive from this. */
    public static final String ABSTRACT_ORDER_SHIPMENT = "abstractOrderShipment";

    /** bean id for implementation of com.elasticpath.domain.order.PhysicalOrderShipment. */
    public static final String PHYSICAL_ORDER_SHIPMENT = "physicalOrderShipment";

    /** bean id for implementation of com.elasticpath.domain.order.ElectronicOrderShipment. */
    public static final String ELECTRONIC_ORDER_SHIPMENT = "electronicOrderShipment";

    /** bean id for implementation of com.elasticpath.domain.order.OrderEvent. */
    public static final String ORDER_EVENT = "orderEvent";

    /** bean id for implementation of com.elasticpath.domain.order.OrderSku. */
    public static final String ORDER_SKU = "orderSku";

    /** bean id for implementation of com.elasticpath.service.shoppingcart.OrderSkuFactory. */
    public static final String ORDER_SKU_FACTORY = "orderSkuFactory";

    /** bean id for implementation of com.elasticpath.domain.shoppingcart.ShoppingCartVisitor. */
    public static final String SHIPMENT_TYPE_SHOPPING_CART_VISITOR = "shipmentTypeShoppingCartVisitor";

    /** bean id for implementation of com.elasticpath.domain.order.Order. */
    public static final String ORDER = "order";

    /** bean id for implementation of com.elasticpath.domain.order.OrderAddress. */
    public static final String ORDER_ADDRESS = "orderAddress";

    /** bean id for implementation of com.elasticpath.domain.order.OrderTaxValue. */
    public static final String ORDER_TAX_VALUE = "orderTaxValue";

    /** bean id for implementation of com.elasticpath.domain.order.OrderLock. */
    public static final String ORDER_LOCK = "orderLock";

    /** bean id for implementation of com.elasticpath.domain.UserPermission. */
    public static final String USER_PERMISSION = "userPermission";

    /** bean id for implementation of com.elasticpath.domain.Inventory. */
    public static final String INVENTORY = "inventory";

    /** bean id for implementation of com.elasticpath.domain.InventoryAudit. */
    public static final String INVENTORY_AUDIT = "inventoryAudit";

    /** bean id of anonymous customerRole (used for spring security integration). */
    public static final String ROLE_ANONYMOUS_CUSTOMER = "ROLE_ANONYMOUS_CUSTOMER";

    /** bean id of default customerRole (used for spring security integration). */
    public static final String ROLE_CUSTOMER = "ROLE_CUSTOMER";

    /** bean id for implementation of com.elasticpath.domain.shipping.ShippingRegion. */
    public static final String SHIPPING_REGION = "shippingRegion";

    /** bean id for implementation of com.elasticpath.domain.shipping.ShippingServiceLevel. */
    public static final String SHIPPING_SERVICE_LEVEL = "shippingServiceLevel";

    /** bean id for implementation of com.elasticpath.domain.shipping.ShippingServiceLevelDeleted. */
    public static final String SHIPPING_SERVICE_LEVEL_DELETED = "shippingServiceLevelDeleted";

    /** bean id for implementation of com.elasticpath.domain.Region. */
    public static final String REGION = "region";

    /** bean id for implementation of com.elasticpath.domain.shipping.ShippingCostCalculationMethod. */
    public static final String SHIPPING_COST_CALCULATION_METHOD = "shippingCostCalculationMethod";

    /** bean id for implementation of com.elasticpath.domain.shipping.ShippingCostCalculationParameter. */
    public static final String SHIPPING_COST_CALCULATION_PARAMETER = "shippingCostCalculationParameter";

    /** bean id for implementation of com.elasticpath.domain.shipping.impl.FixedPriceMethodImpl. */
    public static final String SHIPPING_FIXED_PRICE_METHOD = "fixedPriceMethod";

    /** bean id for implementation of com.elasticpath.domain.shipping.impl.OrderTotalPercentageMethodImpl. */
    public static final String SHIPPING_TOTAL_PERCENT_METHOD = "orderTotalPercentageMethod";

    /** bean id for implementation of com.elasticpath.domain.shipping.impl.FixedBaseAndOrderTotalPercentageMethodImpl. */
    public static final String SHIPPING_FIXED_BASE_TOTAL_PERCENT_METHOD = "fixedBaseAndOrderTotalPercentageMethod";

    /** bean id for implementation of com.elasticpath.domain.shipping.impl.CostPerUnitWeightMethodImpl. */
    public static final String SHIPPING_COST_PER_UNIT_WEIGHT_METHOD = "costPerUnitWeightMethod";

    /** bean id for implementation of com.elasticpath.domain.shipping.impl.FixedBaseAndCostPerUnitWeightMethodImpl. */
    public static final String SHIPPING_FIXED_BASE_COST_PER_UNIT_WEIGHT_METHOD = "fixedBaseAndCostPerUnitWeightMethod";

    /** bean id for implementation of com.elasticpath.domain.skuConfiguration.SkuOption. */
    public static final String SKU_OPTION = "skuOption";

    /** bean id for implementation of com.elasticpath.domain.skuConfiguration.SkuOptionValue. */
    public static final String SKU_OPTION_VALUE = "skuOptionValue";

    /** bean id for implementation of com.elasticpath.domain.skuConfiguration.impl.JpaAdaptorOfSkuOptionValueImpl. */
    public static final String SKU_OPTION_VALUE_JPA_ADAPTOR = "jpaAdaptorSkuOptionValue";

    /** bean id for implementation of com.elasticpath.domain.catalog.ProductAssociation. */
    public static final String PRODUCT_ASSOCIATION = "productAssociation";

    /** bean id for implementation of com.elasticpath.domain.tax.TaxCategory. */
    public static final String TAX_CATEGORY = "taxCategory";

    /** bean id for implementation of com.elasticpath.domain.tax.TaxCode. */
    public static final String TAX_CODE = "taxCode";

    /** bean id for implementation of com.elasticpath.domain.tax.TaxJurisdiction. */
    public static final String TAX_JURISDICTION = "taxJurisdiction";

    /** bean id for implementation of com.elasticpath.domain.tax.TaxRegion. */
    public static final String TAX_REGION = "taxRegion";

    /** bean id for implementation of com.elasticpath.domain.misc.Countries. */
    public static final String COUNTRIES = "countries";

    /** Bean id for implementation of {@link com.elasticpath.service.rules.PromotionRuleExceptions}. */
    public static final String PROMOTION_RULE_EXCEPTIONS = "promotionRuleExceptions";

    /** Bean id for implementation of {@link com.elasticpath.service.rules.PromotionRuleDelegate}. */
    public static final String PROMOTION_RULE_DELEGATE = "promotionRuleDelegate";

    /** bean id for implementation of com.elasticpath.domain.shoppingcart.ViewHistory. */
    public static final String VIEW_HISTORY = "viewHistory";

    /** bean id for implementation of com.elasticpath.domain.rules.AppliedRule. */
    public static final String APPLIED_RULE = "appliedRule";

    /** bean id for implementation of com.elasticpath.domain.rules.AppliedCoupon. */
    public static final String APPLIED_COUPON = "appliedCoupon";

    /** bean id for implementation of com.elasticpath.domain.payment.PayPalExpressPayment. */
    public static final String PAYPAL_EXPRESS_PAYMENT = "PAYPAL_EXPRESS_PAYMENT";

    /** bean id for implementation of com.elasticpath.service.catalogview.ProductCacheLoader. */
    public static final String PRODUCT_CACHE_LOADER = "productCacheLoader";

    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ //

    /** bean id for implementation of com.elasticpath.domain.rules.CustomerGroupCondition. */
    public static final String CUST_GROUP_COND = "customerGroupCondition";

    /** bean id for implementation of com.elasticpath.domain.rules.ProductCategoryCondition. */
    public static final String PRODUCT_CAT_COND = "productCategoryCondition";

    /** bean id for implementation of com.elasticpath.domain.rules.impl.ProductConditionImpl. */
    public static final String PRODUCT_COND = "productCondition";

    /** bean id for implementation of com.elasticpath.domain.rules.impl.BrandConditionImpl. */
    public static final String PRODUCT_BRAND_COND = "brandCondition";

    /** bean id for implementation of com.elasticpath.domain.rules.impl.CartCurrencyCondition. */
    public static final String CART_CURRENCY_COND = "cartCurrencyCondition";

    /** bean id for implementation of com.elasticpath.domain.rules.impl.LimitedUsagePromotionConditionImpl. */
    public static final String LIMITED_USAGE_PROMOTION_COND = "limitedUsagePromotionCondition";

    /** bean id for implementation of com.elasticpath.domain.rules.impl.LimitedUseCouponCodeConditionImpl. */
    public static final String LIMITED_USE_COUPON_CODE_COND = "limitedUseCouponCodeCondition";

    /** bean id for implementation of com.elasticpath.domain.rules.ProductAmountDiscountAction. */
    public static final String PRODUCT_AMOUNT_ACTION = "productAmountDiscountAction";

    /** bean id for implementation of com.elasticpath.domain.rules.CartSkuAmountDiscountAction. */
    public static final String CART_SKU_AMOUNT_ACTION = "cartSkuAmountDiscountAction";

    /** bean id for implementation of com.elasticpath.domain.rules.CartAnySkuAmountDiscountAction. */
    public static final String CART_ANY_SKU_AMOUNT_ACTION = "cartAnySkuAmountDiscountAction";

    /** bean id for implementation of com.elasticpath.domain.rules.CartSkuPercentDiscountAction. */
    public static final String CART_SKU_PERCENT_ACTION = "cartSkuPercentDiscountAction";

    /** bean id for implementation of com.elasticpath.domain.rules.CartAnySkuPercentDiscountAction. */
    public static final String CART_ANY_SKU_PERCENT_ACTION = "cartAnySkuPercentDiscountAction";

    /** bean id for implementation of com.elasticpath.domain.rules.SkuAmountDiscountAction. */
    public static final String SKU_AMOUNT_ACTION = "skuAmountDiscountAction";

    /** bean id for implementation of com.elasticpath.domain.rules.SkuPercentDiscountAction. */
    public static final String SKU_PERCENT_ACTION = "skuPercentDiscountAction";

    /** bean id for implementation of com.elasticpath.domain.rules.SkuException. */
    public static final String SKU_EXCEPTION = "skuException";

    /** bean id for implementation of com.elasticpath.domain.rules.CategoryException. */
    public static final String CATEGORY_EXCEPTION = "categoryException";

    /** bean id for implementation of com.elasticpath.domain.rules.impl.ProductExceptionImpl. */
    public static final String PRODUCT_EXCEPTION = "productException";

    /** bean id for implementation of com.elasticpath.domain.rules.CartProductPercentDiscountAction. */
    public static final String CART_PRODUCT_PERCENT_ACTION = "cartProductPercentDiscountAction";

    /** bean id for implementation of com.elasticpath.domain.rules.CartProductPercentDiscountAction. */
    public static final String CART_PRODUCT_AMOUNT_ACTION = "cartProductAmountDiscountAction";

    /** bean id for implementation of com.elasticpath.domain.rules.CartNthProductPercentDiscountAction. */
    public static final String CART_NTH_PRODUCT_ACTION = "cartNthProductPercentDiscountAction";

    /** bean id for implementation of com.elasticpath.domain.rules.CartNFreeSkusAction. */
    public static final String CART_N_FREE_ACTION = "cartNFreeSkusAction";

    /** bean id for implementation of com.elasticpath.domain.rules.CartContainsItemsOfCategoryCondition. */
    public static final String CART_CONTAINS_CATEGORY_COND = "cartContainsItemsOfCategoryCondition";

    /** bean id for implementation of com.elasticpath.domain.rules.CartCategoryPercentDiscountAction. */
    public static final String CART_CATEGORY_PERCENT_ACTION = "cartCategoryPercentDiscountAction";

    /** bean id for implementation of com.elasticpath.domain.rules.CartCategoryAmountDiscountAction. */
    public static final String CART_CATEGORY_AMOUNT_ACTION = "cartCategoryAmountDiscountAction";

    /** bean id for implementation of com.elasticpath.domain.rules.CartSubtotalCondition. */
    public static final String CART_SUBTOTAL_COND = "cartSubtotalCondition";

    /** bean id for implementation of com.elasticpath.domain.rules.CartSubtotalAmountDiscountAction. */
    public static final String CART_SUBTOTAL_AMT_DISCOUNT_ACTION = "cartSubtotalAmountDiscountAction";

    /** bean id for implementation of com.elasticpath.domain.rules.CartSubtotalPercentDiscountAction. */
    public static final String CART_SUBTOTAL_PERCENT_DISCOUNT_ACTION = "cartSubtotalPercentDiscountAction";

    /** bean id for implementation of com.elasticpath.domain.rules.ProductInCartCondition. */
    public static final String PRODUCT_IN_CART_COND = "productInCartCondition";

    /** bean id for implementation of com.elasticpath.domain.rules.ProductPercentDiscountAction. */
    public static final String PRODUCT_PERCENT_ACTION = "productPercentDiscountAction";

    /** bean id for implementation of com.elasticpath.domain.rules.SkuInCartCondition. */
    public static final String CART_SKU_COND = "skuInCartCondition";

    /** bean id for implementation of com.elasticpath.domain.rules.AnySkuInCartCondition. */
    public static final String CART_ANY_SKU_COND = "anySkuInCartCondition";

    /** bean id for implementation of com.elasticpath.domain.rules.impl.ShippingAmountDiscountActionImpl. */
    public static final String SHIPPING_AMOUNT_DISCOUNT_ACTION = "shippingAmountDiscountAction";

    /** bean id for implementation of com.elasticpath.domain.rules.impl.ShippingPercentDiscountActionImpl. */
    public static final String SHIPPING_PERCENT_DISCOUNT_ACTION = "shippingPercentDiscountAction";

    /** bean id for implementation of com.elasticpath.domain.rules.impl.CatalogCurrencyPercentDiscountActionImpl. */
    public static final String CATALOG_CURRENCY_PERCENT_DISCOUNT_ACTION = "catalogCurrencyPercentDiscountAction";

    /** bean id for implementation of com.elasticpath.domain.rules.impl.CatalogCurrencyAmountDiscountActionImpl. */
    public static final String CATALOG_CURRENCY_AMOUNT_DISCOUNT_ACTION = "catalogCurrencyAmountDiscountAction";

    /** bean id for implementation of com.elasticpath.domain.discounts.impl.LimitedTotallingApplierImpl. */
    public static final String TOTALLING_APPLIER = "totallingApplier";

    /** bean id for implementation of com.elasticpath.domain.discounts.impl.ShoppingCartDiscountItemContainerImpl. */
    public static final String SHOPPING_CART_DISCOUNT_ITEM_CONTAINER = "shoppingCartDiscountItemContainer";

    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ //

    /** bean id for implementation of com.elasticpath.common.comparator.DisplayNameComparator. */
    public static final String DISPLAY_NAME_COMPARATOR = "displayNameComparator";

    /** bean id for implementation of com.elasticpath.domain.misc.impl.TopSellerComparatorImpl. */
    public static final String TOP_SELLER_COMPARATOR = "topSellerComparator";

    /** bean id for implementation of com.elasticpath.domain.misc.impl.RegionCodeComparatorImpl. */
    public static final String REGION_CODE_COMPARATOR = "regionCodeComparator";

    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ //
    /** bean id for implementation of com.elasticpath.sfweb.formBean.SignInFormBean. */
    public static final String SIGN_IN_FORM_BEAN = "signInFormBean";

    /** bean id for implementation of com.elasticpath.sfweb.formBean.AnonymousSignInFormBean. */
    public static final String ANONYMOUS_SIGN_IN_FORM_BEAN = "anonymousSignInFormBean";

    /** bean id for implementation of com.elasticpath.sfweb.formBean.CheckoutAddressFormBean. */
    public static final String CHECKOUT_ADDRESS_FORM_BEAN = "checkoutAddressFormBean";

    /** bean id for implementation of com.elasticpath.sfweb.formBean.CreditCardFormBean. */
    public static final String CREDIT_CARD_FORM_BEAN = "creditCardFormBean";

    /** bean id for implementation of com.elasticpath.sfweb.formBean.BillingAndReviewFormBean. */
    public static final String BILLING_AND_REVIEW_FORM_BEAN = "billingAndReviewFormBean";

    /** bean id for implementation of com.elasticpath.sfweb.formBean.CustomerAddressFormBean. */
    public static final String CUSTOMER_ADDRESS_FORM_BEAN = "customerAddressFormBean";

    /** bean id for implementation of com.elasticpath.sfweb.formBean.ManageAccountFormBeanImpl. */
    public static final String MANAGE_ACCOUNT_FORM_BEAN = "manageAccountFormBean";

    /** bean id for implementation of com.elasticpath.sfweb.formBean.EditAccountFormBean. */
    public static final String EDIT_ACCOUNT_FORM_BEAN = "editAccountFormBean";

    /** bean id for implementation of com.elasticpath.cmweb.formBean.ImportStep1FormBean. */
    public static final String IMPORT_STEP1_FORM_BEAN = "importStep1FormBean";

    /** bean id for implementation of com.elasticpath.cmweb.formBean.ImportStep2FormBean. */
    public static final String IMPORT_STEP2_FORM_BEAN = "importStep2FormBean";

    /** bean id for implementation of com.elasticpath.cmweb.formBean.ImportStep3FormBean. */
    public static final String IMPORT_STEP3_FORM_BEAN = "importStep3FormBean";

    /** bean id for implementation of com.elasticpath.cmweb.formBean.ImportStep5FormBean. */
    public static final String IMPORT_STEP5_FORM_BEAN = "importStep5FormBean";

    /** bean id for implementation of com.elasticpath.cmweb.formBean.ImportStep6FormBean. */
    public static final String IMPORT_STEP6_FORM_BEAN = "importStep6FormBean";

    /** bean id for implementation of com.elasticpath.sfweb.formBean.GiftCertificateFormBean. */
    public static final String GIFT_CERTIFICATE_FORM_BEAN = "giftCertificateFormBean";

    /** bean id for implementation of {@link com.elasticpath.sfweb.formbean.ShoppingItemFormBean}. */
    public static final String SHOPPING_ITEM_FORM_BEAN = "shoppingItemFormBean";

    /** bean id for implementation of {@link com.elasticpath.sfweb.formbean.ShoppingCartFormBean}. */
    public static final String SHOPPING_CART_FORM_BEAN = "shoppingCartFormBean";

    /** bean id for implementation of {@link com.elasticpath.sfweb.formbean.OrderItemFormBean}. */
    public static final String ORDER_ITEM_FORM_BEAN = "orderItemFormBean";

    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ //
    // SF AJAX Beans

    /** bean id for implementation of com.elasticpath.cmweb.formBean.ImportStep6FormBean. */
    public static final String SKU_SELECTION_BEAN = "skuSelectionBean";

    /** bean id for implementation of com.elasticpath.sfweb.ajax.bean.SkuConfigurationBean. */
    public static final String SKU_CONFIGURATION_BEAN = "skuConfigurationBean";

    /** bean id for json bundle, impl resides in com.elasticpath.sfweb.ajax.bean.impl.JsonBundleItemBeanImpl. */
    public static final String JSON_BUNDLE = "jsonBundle";

    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ //
    /** bean id for implementation of com.elasticpath.service.CustomerService. */
    public static final String CUSTOMER_SERVICE = "customerService";

    /** bean id for implementation of com.elasticpath.service.CustomerGroupService. */
    public static final String CUSTOMER_GROUP_SERVICE = "customerGroupService";

    /** bean id for implementation of com.elasticpath.service.ShoppingCartService. */
    public static final String SHOPPING_CART_SERVICE = "shoppingCartService";

    /** bean id for implementation of com.elasticpath.service.RuleSetService. */
    public static final String RULE_SET_SERVICE = "ruleSetService";

    /** bean id for implementation of com.elasticpath.service.RuleService. */
    public static final String RULE_SERVICE = "ruleService";

    /** bean id for implementation of com.elasticpath.service.RuleElementService. */
    public static final String RULE_ELEMENT_SERVICE = "ruleElementService";

    /** bean id for implementation of com.elasticpath.service.RuleParameterService. */
    public static final String RULE_PARAMETER_SERVICE = "ruleParameterService";

    /** bean id for implementation of com.elasticpath.service.rules.CouponrService. */
    public static final String COUPON_SERVICE = "couponService";

    /** bean id for implementation of com.elasticpath.service.rules.CouponUsageService. */
    public static final String COUPON_USAGE_SERVICE = "couponUsageService";

    /** bean id for implementation of com.elasticpath.service.rules.CouponConfigService. */
    public static final String COUPON_CONFIG_SERVICE = "couponConfigService";

    /** bean id for implementation of com.elasticpath.service.CmUserService. */
    public static final String CMUSER_SERVICE = "cmUserService";

    /** bean id for implementation of com.elasticpath.service.UserRoleService. */
    public static final String USER_ROLE_SERVICE = "userRoleService";

    /** bean id for implementation of com.elasticpath.service.UserRoleService. */
    public static final String SKU_CONFIGURATION_SERVICE = "skuConfigurationService";

    /** bean id for implementation of com.elasticpath.service.ShippingREgionService. */
    public static final String SHIPPING_REGION_SERVICE = "shippingRegionService";

    /** bean id for implementation of com.elasticpath.service.ShippingServiceLevelService. */
    public static final String SHIPPING_SERVICE_LEVEL_SERVICE = "shippingServiceLevelService";

    /** bean id for implementation of com.elasticpath.service.ProductService. */
    public static final String PRODUCT_SERVICE = "productService";

    /** bean id for implementation of com.elasticpath.service.ProductSkuService. */
    public static final String PRODUCT_SKU_SERVICE = "productSkuService";

    /** bean id for implementation of com.elasticpath.service.catalog.productInventoryManagementService. */
    public static final String PRODUCT_INVENTORY_MANAGEMENT_SERVICE = "productInventoryManagementService";

    /** bean id for implementation of com.elasticpath.service.ProductAssociationService. */
    public static final String PRODUCT_ASSOCIATION_SERVICE = "productAssociationService";

    /** bean id for implementation of com.elasticpath.service.catalog.CatalogService. */
    public static final String CATALOG_SERVICE = "catalogService";

    /** bean id for implementation of com.elasticpath.service.CategoryService. */
    public static final String CATEGORY_SERVICE = "categoryService";

    /** bean id for implementation of com.elasticpath.service.AttributeService. */
    public static final String ATTRIBUTE_SERVICE = "attributeService";

    /** bean id for implementation of com.elasticpath.service.CategoryTypeService. */
    public static final String CATEGORY_TYPE_SERVICE = "categoryTypeService";

    /** bean id for implementation of com.elasticpath.service.ProductTypeService. */
    public static final String PRODUCT_TYPE_SERVICE = "productTypeService";

    /** bean id for implementation of com.elasticpath.service.BrandService. */
    public static final String BRAND_SERVICE = "brandService";

    /** bean id for implementation of com.elasticpath.service.SkuOptionService. */
    public static final String SKU_OPTION_SERVICE = "skuOptionService";

    /** bean id for implementation of com.elasticpath.service.tax.TaxCodeService. */
    public static final String TAX_CODE_SERVICE = "taxCodeService";

    /** bean id for implementation of com.elasticpath.service.tax.TaxJurisdictionService. */
    public static final String TAX_JURISDICTION_SERVICE = "taxJurisdictionService";

    /** bean id for implementation of com.elasticpath.service.order.OrderService. */
    public static final String ORDER_SERVICE = "orderService";

    /** bean id for implementation of com.elasticpath.service.order.OrderService. */
    public static final String NOTIFICATION_SERVICE = "notificationService";

    /** bean id for implementation of com.elasticpath.service.order.OrderlockService. */
    public static final String ORDER_LOCK_SERVICE = "orderLockService";

    /** bean id for implementation of com.elasticpath.service.order.ReturnAndExchangeServiceImpl. */
    public static final String ORDER_RETURN_SERVICE = "returnAndExchangeService";

    /** bean id for implementation of com.elasticpath.service.order.OrderReturn. */
    public static final String ORDER_RETURN = "orderReturn";

    /** bean id for implementation of com.elasticpath.service.order.OrderReturnSku. */
    public static final String ORDER_RETURN_SKU = "orderReturnSku";

    /** bean id for implementation of com.elasticpath.service.GiftCertificateService. */
    public static final String GIFT_CERTIFICATE_SERVICE = "giftCertificateService";

    /** bean id for implementation of com.elasticpath.service.GiftCertificateTransactionService. */
    public static final String GIFT_CERTIFICATE_TRANSACTION_SERVICE = "giftCertificateTransactionService";

    /** bean id for implemenation of com.elasticpath.service.shoppingcart.CheckoutService. */
    public static final String CHECKOUT_SERVICE = "checkoutService";

    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ //
    // Content Wrapper Repository

    /** bean id for implementation of com.elasticpath.domain.contentspace.impl.ContentWrapperRepositoryImpl. */
    public static final String CONTENT_WRAPPER_REPOSITORY = "contentWrapperRepository";

    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ //
    // CM AJAX Services

    /** bean id for implementation of com.elasticpath.cmweb.ajaxbean.impl.CatalogAjaxServiceImpl. */
    public static final String CATALOG_AJAX_SERVICE = "catalogAjaxService";

    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ //
    // CM AJAX Beans

    /** bean id for implementation of com.elasticpath.cmweb.ajaxbean.impl.ProductAjaxBean. */
    public static final String PRODUCT_AJAX_BEAN = "productAjaxBean";

    /** bean id for implementation of com.elasticpath.cmweb.ajaxbean.impl.ProductSkuAjaxBean. */
    public static final String PRODUCT_SKU_AJAX_BEAN = "productSkuAjaxBean";

    /** bean id for implementation of com.elasticpath.cmweb.ajaxbean.impl.CategoryAjaxBean. */
    public static final String CATEGORY_AJAX_BEAN = "categoryAjaxBean";

    /** bean id for implementation of com.elasticpath.cmweb.ajaxbean.impl.CategoryInfoAjaxBean. */
    public static final String CATEGORY_INFO_AJAX_BEAN = "categoryInfoAjaxBean";

    /** bean id for implementation of com.elasticpath.cmweb.ajaxbean.impl.ProductInfoAjaxBean. */
    public static final String PRODUCT_INFO_AJAX_BEAN = "productInfoAjaxBean";

    /** bean id for implementation of com.elasticpath.cmweb.ajaxbean.impl.OrderInfoAjaxBean. */
    public static final String ORDER_INFO_AJAX_BEAN = "orderInfoAjaxBean";

    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ //

    /** bean id for implementation of com.elasticpath.persistence.support.impl.OrderCriterionImpl. */
    public static final String ORDER_CRITERION = "orderCriterion";

    /** bean id for implementation of com.elasticpath.persistence.support.impl.DistinctAttributeValueCriterionImpl. */
    public static final String DISTINCT_ATTRIBUTE_VALUE_CRITERION = "distinctAttributeValueCriterion";

    /** bean id for implementation of com.elasticpath.domain.dataimport.impl.ImportServiceImpl. */
    public static final String IMPORT_SERVICE = "importService";

    /** bean id for implementation of com.elasticpath.domain.dataimport.ImportJob. */
    public static final String IMPORT_JOB = "importJob";

    /** bean id for implementation of com.elasticpath.domain.pricing.csvimport.impl.ImportDataTypeBaseAmountImpl. */
    public static final String IMPORT_DATA_TYPE_BASEAMOUNT = "importDataTypeBaseAmount";

    /** bean id for implementation of com.elasticpath.domain.dataimport.impl.ImportDataTypeProductImpl. */
    public static final String IMPORT_DATA_TYPE_PRODUCT = "importDataTypeProduct";

    /** bean id for implementation of com.elasticpath.domain.dataimport.impl.ImportDataTypeProductSkuImpl. */
    public static final String IMPORT_DATA_TYPE_PRODUCT_SKU = "importDataTypeProductSku";;

    /** bean id for implementation of com.elasticpath.domain.dataimport.impl.ImportDataTypeCategoryImpl. */
    public static final String IMPORT_DATA_TYPE_CATEGORY = "importDataTypeCategory";

    /** bean id for implementation of com.elasticpath.domain.dataimport.impl.ImportDataTypeProductCategoryAssociationImpl. */
    public static final String IMPORT_DATA_TYPE_PRODUCT_CATEGORY_ASSOCIATION = "importDataTypeProductCategoryAssociation";

    /** bean id for implementation of com.elasticpath.domain.dataimport.impl.ImportDataTypeProductAssociationImpl. */
    public static final String IMPORT_DATA_TYPE_PRODUCT_ASSOCIATION = "importDataTypeProductAssociation";

    /** bean id for implementation of com.elasticpath.domain.dataimport.impl.ImportDataTypeDigitalAssetImpl. */
    public static final String IMPORT_DATA_TYPE_DIGITAL_ASSET = "importDataTypeDigitalAsset";

    /** bean id for implementation of com.elasticpath.domain.dataimport.impl.ImportDataTypeCustomerImpl. */
    public static final String IMPORT_DATA_TYPE_CUSTOMER = "importDataTypeCustomer";

    /** bean id for implementation of com.elasticpath.domain.dataimport.impl.ImportDataTypeustomerAddressImpl. */
    public static final String IMPORT_DATA_TYPE_CUSTOMER_ADDRESS = "importDataTypeCustomerAddress";

    /** bean id for implementation of com.elasticpath.domain.dataimport.impl.ImportDataTypeustomerAddressImpl. */
    public static final String IMPORT_DATA_TYPE_INVENTORY = "importDataTypeInventory";

    /** bean id for implementation of com.elasticpath.domain.dataimport.ImportBadRow. */
    public static final String IMPORT_BAD_ROW = "importBadRow";

    /** bean id for implementation of com.elasticpath.domain.dataimport.ImportFault. */
    public static final String IMPORT_FAULT = "importFault";

    /** bean id for implementation of com.elasticpath.domain.dataimport.ImportJobRunnerProductImpl. */
    public static final String IMPORT_JOB_RUNNER_PRODUCT = "importJobRunnerProduct";

    /** bean id for implementation of com.elasticpath.domain.dataimport.ImportJobRunnerCategoryImpl. */
    public static final String IMPORT_JOB_RUNNER_CATEGORY = "importJobRunnerCategory";

    /** bean id for implementation of com.elasticpath.domain.dataimport.ImportJobRunnerCustomerImpl. */
    public static final String IMPORT_JOB_RUNNER_CUSTOMER = "importJobRunnerCustomer";

    /** bean id for implementation of com.elasticpath.persistence.impl.CsvFileReaderImpl. */
    public static final String CSV_FILE_READER = "csvFileReader";

    /** bean id for implementation of com.elasticpath.persistence.impl.PrintWriterImpl. */
    public static final String PRINT_WRITER = "printWriter";

    /** bean id for implementation of com.elasticpath.domain.misc.LocalizedProperties. */
    public static final String LOCALIZED_PROPERTIES = "localizedProperties";

    /** bean id for implementation of com.elasticpath.commons.util.impl.UtilityImpl. */
    public static final String UTILITY = "utility";

    /** bean id for implementation of com.elasticpath.commons.util.PasswordGenerator. */
    public static final String PASSWORD_GENERATOR = "passwordGenerator";

    /** bean id for implementation of com.elasticpath.commons.util.security.CreditCardEncrypter. */
    public static final String CREDIT_CARD_ENCRYPTER = "creditCardEncrypter";

    /** bean id for implementation of com.elasticpath.commons.util.security.CardEncrypter. */
    public static final String CARD_ENCRYPTER = "cardEncrypter";

    /** bean id for implementation of org.springframework.security.authentication.encoding.PasswordEncoder. */
    public static final String PASSWORDENCODER = "passwordEncoder";

    /** bean id for CM User implementation of org.springframework.security.authentication.encoding.PasswordEncoder. */
    public static final String CM_PASSWORDENCODER = "cmPasswordEncoder";
    
    /** bean id for implementation of com.elasticpath.domain.catalogview.SearchRequest. */
    public static final String SEARCH_REQUEST = "searchRequest";

    /** bean id for implementation of com.elasticpath.domain.catalogview.SearchResult. */
    public static final String SEARCH_RESULT = "searchResult";

    /** bean id for implementation of com.elasticpath.domain.search.SfSearchLog. */
    public static final String SF_SEARCH_LOG = "sfSearchLog";

    /** bean id for implementation of com.elasticpath.domain.catalogview.SearchResultHistory. */
    public static final String CATALOG_VIEW_RESULT_HISTORY = "catalogViewResultHistory";

    /** bean id for implementation of com.elasticpath.domain.catalogview.CategoryFilter. */
    public static final String CATEGORY_FILTER = "categoryFilter";

    /** bean id for implementation of com.elasticpath.domain.catalogview.AttributeFilter. */
    public static final String ATTRIBUTE_FILTER = "attributeFilter";

    /** bean id for implementation of com.elasticpath.domain.catalogview.AttributeRangeFilter. */
    public static final String ATTRIBUTE_RANGE_FILTER = "attributeRangeFilter";

    /** bean id for implementation of com.elasticpath.domain.catalogview.impl.PriceFilterImpl. */
    public static final String PRICE_FILTER = "priceFilter";

    /** bean id for implementation of com.elasticpath.domain.catalogview.impl.BrandFilterImpl. */
    public static final String BRAND_FILTER = "brandFilter";

    /** bean id for implementation of com.elasticpath.domain.brand. */
    public static final String BRAND = "brand";

    /** bean id for implementation of com.elasticpath.domain.catalogview.FilterOption. */
    public static final String FILTER_OPTION = "filterOption";

    /** bean id for implementation of com.elasticpath.domain.catalogview.browsing.BrowsingFilterOption. */
    public static final String BROWSING_FILTER_OPTION = "browsingFilterOption";

    /** bean id for implementation of com.elasticpath.domain.browsing.BrowsingRequest. */
    public static final String BROWSING_REQUEST = "browsingRequest";

    /** bean id for implementation of com.elasticpath.domain.browsing.BrowsingResult. */
    public static final String BROWSING_RESULT = "browsingResult";

    /** bean id for implementation of com.elasticpath.domain.catalogview.sitemap.SitemapRequest. */
    public static final String SITEMAP_REQUEST = "sitemapRequest";

    /** bean id for implementation of com.elasticpath.domain.catalogview.sitemap.SitemapResult. */
    public static final String SITEMAP_RESULT = "sitemapResult";

    /** bean id for implementation of com.elasticpath.domain.catalog.PriceImpl. */
    public static final String PRICE = "Price";

    /** bean id for implementation of com.elasticpath.domain.catalog.ProductPriceTierImpl. */
    public static final String PRICE_TIER = "PriceTier";

    /** bean id for implementation of com.elasticpath.domain.pricing.PriceLookupService. */
    public static final String PRICE_LOOKUP_SERVICE = "priceLookupService";

    /** bean id for implementation of com.elasticpath.domain.pricing.PriceLookupFacade. */
    public static final String PRICE_LOOKUP_FACADE = "priceLookupFacade";

    /** bean id for implementation of com.elasticpath.domain.pricing.PriceListLookupService. */
    public static final String PRICE_LIST_LOOKUP_SERVICE = "priceListLookupService";

    /** bean id for implementation of com.elasticpath.domain.pricing.PriceListDescriptor. */
    public static final String PRICE_LIST_DESCRIPTOR = "priceListDescriptor";

    /** bean id for implementation of com.elasticpath.domain.pricing.PriceListStack. */
    public static final String PRICE_LIST_STACK = "priceListStack";

    /** bean id for implementation of com.elasticpath.domain.pricing.PriceListDescriptorService. */
    public static final String PRICE_LIST_DESCRIPTOR_SERVICE = "priceListDescriptorService";

    /** bean id for implementation of com.elasticpath.domain.pricing.BaseAmountService. */
    public static final String BASE_AMOUNT_SERVICE = "baseAmountService";

    /** bean id for implementation of com.elasticpath.common.pricing.service.BaseAmountUpdateStrategy. */
    public static final String BASE_AMOUNT_UPDATE_STRATEGY = "baseAmountUpdateAllOrNothing";

    /** bean id for implementation of com.elasticpath.common.pricing.service.PriceListService. */
    public static final String PRICE_LIST_CLIENT_SERVICE = "priceListService";

    /** bean id for implementation of com.elasticpath.common.pricing.service.PriceListHelperService. */
    public static final String PRICE_LIST_HELPER_SERVICE = "priceListHelperService";

    /** bean id for implementation of com.elasticpath.common.pricing.service.PriceListAssignmentHelperService. */
    public static final String PRICE_LIST_ASSIGNMENT_HELPER_SERVICE = "priceListAssignmentHelperService";

    /** bean id for {@link com.elasticpath.domain.pricing.impl.PriceListAssignment}. */
    public static final String PRICE_LIST_ASSIGNMENT = "priceListAssignment";

    /** bean id for {@link com.elasticpath.service.pricing.impl.PriceListAssignmentService}. */
    public static final String PRICE_LIST_ASSIGNMENT_SERVICE = "priceListAssignmentService";

    /** bean id for implementation of com.elasticpath.domain.pricing.BaseAmountFactory. */
    public static final String BASE_AMOUNT_FACTORY = "baseAmountFactory";

    /** bean id for implementation of com.elasticpath.domain.pricing.BaseAmountFilter. */
    public static final String BASE_AMOUNT_FILTER = "baseAmountFilter";

    /** bean id for implementation of com.elasticpath.domain.pricing.BaseAmountFilterExt. */
    public static final String BASE_AMOUNT_FILTER_EXT = "baseAmountFilterExt";

    /** bean id for implementation of com.elasticpath.domain.pricing.PriceAdjustment. */
    public static final String PRICE_ADJUSTMENT = "priceAdjustment";

    /** bean id for implementation of com.elasticpath.service.pricing.PriceAdjustmentService. */
    public static final String PRICE_ADJUSTMENT_SERVICE = "priceAdjustmentService";

    /** bean id for implementation of com.elasticpath.common.pricing.service.PriceAdjustmentDtoService. */
    public static final String PRICE_ADJUSTMENT_DTO_SERVICE = "priceAdjustmentDtoService";

    /** bean id for implementation of com.elasticpath.domain.customer.CustomerProfile. */
    public static final String CUSTOMER_PROFILE = "customerProfile";

    /** bean id for implementation of com.elasticpath.domain.AttributeGroup. */
    public static final String ATTRIBUTE_GROUP = "attributeGroup";

    /** bean id for implementation of com.elasticpath.domain.attribute.AttributeUsage. */
    public static final String ATTRIBUTE_USAGE = "attributeUsage";

    /** bean id for implementation of com.elasticpath.domain.AttributeGroupAttribute. */
    public static final String ATTRIBUTE_GROUP_ATTRIBUTE = "attributeGroupAttribute";

    /** bean id for implementation of com.elasticpath.domain.misc.ImageRenderResponse. */
    public static final String IMAGE_RENDER_RESPONSE = "imageRenderResponse";

    /** bean id for implementation of com.elasticpath.domain.misc.ImageRenderRequest. */
    public static final String IMAGE_RENDER_REQUEST = "imageRenderRequest";

    /** bean id for implementation of com.elasticpath.domain.catalog.DigitalAsset. */
    public static final String DIGITAL_ASSET = "digitalAsset";

    /** bean id for implementation of com.elasticpath.domain.catalog.DigitalAssetAudit. */
    public static final String DIGITAL_ASSET_AUDIT = "digitalAssetAudit";

    /** bean id for implementation of com.elasticpath.persistence.PropertiesDao. */
    public static final String PROPERTIES_DAO = "propertiesDao";

    /** bean id for implementation of com.elasticpath.domain.catalog.ProductTypeLoadTuner. */
    public static final String PRODUCT_TYPE_LOAD_TUNER = "productTypeLoadTuner";

    /** bean id for implementation of com.elasticpath.domain.catalog.ProductSkuLoadTuner. */
    public static final String PRODUCT_SKU_LOAD_TUNER = "productSkuLoadTuner";

    /** bean id for implementation of com.elasticpath.domain.catalog.ProductLoadTuner. */
    public static final String PRODUCT_LOAD_TUNER = "productLoadTuner";

    /** bean id for implementation of com.elasticpath.domain.catalog.CategoryLoadTuner. */
    public static final String CATEGORY_LOAD_TUNER = "categoryLoadTuner";

    /** bean id for implementation of com.elasticpath.domain.catalog.CategoryTypeLoadTuner. */
    public static final String CATEGORY_TYPE_LOAD_TUNER = "categoryTypeLoadTuner";

    /** bean id for implementation of com.elasticpath.domain.catalog.TopSeller. */
    public static final String TOP_SELLER = "topSeller";

    /** bean id for implementation of com.elasticpath.domain.catalog.TopSellerProduct. */
    public static final String TOP_SELLER_PRODUCT = "topSellerProduct";

    /** bean id for implementation of com.elasticpath.domain.misc.Geography. */
    public static final String GEOGRAPHY = "geography";

    /** bean id for implementation of com.elasticpath.domain.order.OrderReturnReceivedState. */
    public static final String ORDER_RETURN_RECEIVED_STATE = "orderReturnReceivedState";

    /** bean id for implementation of com.elasticpath.domain.order.OrderReturnSkuReason. */
    public static final String ORDER_RETURN_SKU_REASON = "orderReturnSkuReason";

    /** bean id for implementation of com.elasticpath.service.payment.PaymentHandlerFactory. */
    public static final String PAYMENT_HANDLER_FACTORY = "paymentHandlerFactory";

    /** bean id for implementation of com.elasticpath.domain.search.ProductSearchCriteria. */
    public static final String PRODUCT_SEARCH_CRITERIA = "productSearchCriteria";

    /** bean id for implementation of com.elasticpath.domain.search.CategorySearchCriteria. */
    public static final String CATEGORY_SEARCH_CRITERIA = "categorySearchCriteria";

    /** bean id for implementation of com.elasticpath.domain.search.CustomerSearchCriteria. */
    public static final String CUSTOMER_SEARCH_CRITERIA = "customerSearchCriteria";

    /** bean id for implementation of com.elasticpath.domain.search.impl.OrderSearchCriteriaImpl. */
    public static final String ORDER_SEARCH_CRITERIA = "orderSearchCriteria";

    /** bean id for implementation of com.elasticpath.domain.search.impl.OrderReturnSearchCriteriaImpl. */
    public static final String ORDER_RETURN_SEARCH_CRITERIA = "orderReturnSearchCriteria";

    /** bean id for implementation of com.elasticpath.domain.search.PromotionSearchCriteria. */
    public static final String PROMOTION_SEARCH_CRITERIA = "promotionSearchCriteria";

    /** bean id for implementation of com.elasticpath.domain.search.UserSearchCriteria. */
    public static final String USER_SEARCH_CRITERIA = "userSearchCriteria";

    /** bean id for implementation of com.elasticpath.service.query.KeywordSearchCriteria. */
    public static final String KEYWORD_SEARCH_CRITERIA = "keywordSearchCriteria";

    /** bean id for implementation of com.elasticpath.service.query.FilteredSearchCriteria. */
    public static final String FILTERED_SEARCH_CRITERIA = "filteredSearchCriteria";

    /** bean id for implementation of {@link com.elasticpath.service.query.LuceneRawSearchCriteria}. */
    public static final String LUCENE_RAW_SEARCH_CRITERIA = "luceneRawSearchCriteria";

    /** bean id for implementation of com.elasticpath.domain.search.ProductAutocompleteSearchCriteria. */
    public static final String PRODUCT_AUTOCOMPLETE_SEARCH_CRITERIA = "productAutocompleteSearchCriteria";

    /** bean id for implementation of com.elasticpath.domain.misc.impl.FilterBucketComparatorImpl. */
    public static final String FILTER_BUCKET_COMPARATOR = "filterBucketComparator";

    /** bean id for implementation of com.elasticpath.domain.catalogview.FilterOptionCompareToComparator. */
    public static final String FILTER_OPTION_COMPARETO_COMPARATOR = "filterOptionComparetoComparator";

    /** bean id for implementation of com.elasticpath.service.tax.TaxCalculationService. */
    public static final String TAX_CALCULATION_SERVICE = "taxCalculationService";

    /** bean id for implementation of com.elasticpath.domain.catalog.impl.GiftCertificateImpl. */
    public static final String GIFT_CERTIFICATE = "giftCertificate";

    /** bean id for implementation of com.elasticpath.domain.catalog.impl.GiftCertificateTransactionImpl. */
    public static final String GIFT_CERTIFICATE_TRANSACTION = "giftCertificateTransaction";

    /** bean id for implementation of com.elasticpath.domain.tax.TaxValue. */
    public static final String TAX_VALUE = "taxValue";

    /** bean id for implementation of com.elasticpath.service.misc.GeneralJpaLoaderService. */
    public static final String JPA_GENRAL_LOADER_SERVICE = "generalJpaLoaderService";

    /** bean id for com.elasticpath.service.index.impl.indexSearchServiceImpl. */
    public static final String INDEX_SEARCH_SERVICE = "indexSearchService";

    /** bean id for implementation of com.elasticpath.service.store.WarehouseService. */
    public static final String WAREHOUSE_SERVICE = "warehouseService";

    /** bean id for implementation of com.elasticpath.domain.store.Warehouse. */
    public static final String WAREHOUSE = "warehouse";

    /** bean id for implementation of com.elasticpath.domain.store.WarehouseAddress. */
    public static final String WAREHOUSE_ADDRESS = "warehouseAddress";

    /** bean id for implementation of com.elasticpath.service.store.StoreService. */
    public static final String STORE_SERVICE = "storeService";

    /** bean id for implementation of com.elasticpath.domain.store.Store. */
    public static final String STORE = "store";

    /** bean id for implementation of com.elasticpath.domain.store.CreditCardType. */
    public static final String CREDIT_CARD_TYPE = "creditCardType";

    /** bean id for implementation of com.elasticpath.service.payment.PaymentGatewayService. */
    public static final String PAYMENT_GATEWAY_SERVICE = "paymentGatewayService";

    /** bean id for implementation of com.elasticpath.domain.payment.PaymentGatewayFactory. */
    public static final String PAYMENT_GATEWAY_FACTORY = "paymentGatewayFactory";

    /** bean id for the implementation of com.elasticpath.plugin.payment.service.PaymentGatewayTransactionService. */
    public static final String PAYMENT_GATEWAY_TRANSACTION_SERVICE = "paymentGatewayTransactionService";
    
    /** bean id for implementation of com.elasticpath.domain.payment.PaymentGatewayProperty. */
    public static final String PAYMENT_GATEWAY_PROPERTY = "paymentGatewayProperty";

    /** bean id for implementation of com.elasticpath.domain.misc.EmailProperties. */
    public static final String EMAIL_PROPERTIES = "emailProperties";

    /** bean id for implementation of com.elasticpath.service.misc.EmailService. */
    public static final String EMAIL_SERVICE = "emailService";

    /** bean id for implementation of com.elasticpath.service.misc.OrderReturnEmailService. */
    public static final String ORDER_RETURN_EMAIL_SERVICE = "orderReturnEmailService";

    /** bean id for implementation of com.elasticpath.domain.catalog.Catalog. */
    public static final String CATALOG = "catalog";

    /** bean id for implementation of com.elasticpath.domain.catalog.CatalogLocale. */
    public static final String CATALOG_LOCALE = "catalogLocale";

    /** bean id for implementation of com.elasticpath.domain.misc.SearchConfig. */
    public static final String SEARCH_CONFIG = "searchConfig";

    /** bean id for implementation of com.elasticpath.service.index.SolrIndexSearchResult. */
    public static final String SOLR_SEARCH_RESULT = "solrIndexSearchResult";

    /** bean id for implmentation of com.elasticpath.domain.catalogview.SeoUrlBuilder. */
    public static final String SEO_URL_BUILDER = "coreSeoUrlBuilder";

    /** bean id for implementation of com.elasticpath.domain.catalogview.FeaturedProductFilter. */
    public static final String FEATURED_PRODUCT_FILTER = "featuredProductFilter";

    /** bean id for implementation of com.elasticpath.domain.catalogview.DisplayableFilter. */
    public static final String DISPLAYABLE_FILTER = "displayableFilter";

    /** bean id for implementation of com.elasticpath.domain.misc.CheckoutResults. */
    public static final String CHECKOUT_RESULTS = "checkoutResults";

    /** bean id for implementation of com.elasticpath.domain.InventoryCommand. */
    public static final String INVENTORY_COMMAND = "inventoryCommand";

    /** bean id for implementation of com.elasticpath.domain.ExecutionResult. */
    public static final String INVENTORY_EXECUTION_RESULT = "inventoryExecutionResult";

    /** bean id for implementation of com.elasticpath.domain.inventory.InventoryJournal. */
    public static final String INVENTORY_JOURNAL = "inventoryJournal";

    /** bean id for implementation of com.elasticpath.domain.event.EventOriginator. */
    public static final String EVENT_ORIGINATOR = "eventOriginator";

    /** bean id for implementation of com.elasticpath.domain.event.OrderEventHelper. */
    public static final String ORDER_EVENT_HELPER = "orderEventHelper";

    /** bean id for implementation of com.elasticpath.domain.event.EventOriginatorHelper. */
    public static final String EVENT_ORIGINATOR_HELPER = "eventOriginatorHelper";

    /** bean id for implementation of com.elasticpath.service.misc.PropertyService. */
    public static final String PROPERTIES_SERVICE = "propertyService";

    /** bean id for implementation of com.elasticpath.service.misc.PayerAuthenticationEnrollmentResult. */
    public static final String PAYER_AUTHENTICATION_ENROLLMENT_RESULT = "payerAuthenticationEnrollmentResult";

    /** bean id for implementation of com.elasticpath.service.misc.PayerAuthValidationValue. */
    public static final String PAYER_AUTH_VALIDATION_VALUE = "payerAuthValidationValue";

    /** bean id for implementation of com.elasticpath.domain.store.AdjustmentQuantityOnHandReason. */
    public static final String ADJUSTMENT_QUANTITY_ON_HAND_REASON = "adjustmentQuantityOnHandReason";

    /** bean id for implementation of com.elasticpath.service.search.SynonymGroupService. */
    public static final String SYNONYM_GROUP_SERVICE = "synonymGroupService";

    /** bean id for implementation of com.elasticpath.domain.search.SynonymGroup. */
    public static final String SYNONYM_GROUP = "synonymGroup";

    /** bean id for implementation of com.elasticpath.domain.search.Synonym. */
    public static final String SYNONYM = "synonym";

    /** bean id for implementation of com.elasticpath.service.payment.PaymentResult. */
    public static final String PAYMENT_RESULT = "paymentResult";

    /** bean id for implementation of com.elasticpath.service.payment.PaymentService. */
    public static final String PAYMENT_SERVICE = "paymentService";

    /** bean id for implementation of com.elasticpath.domain.misc.OrderingComparator. */
    public static final String ORDERING_COMPARATOR = "orderingComparator";

    /** bean id for implementation of com.elasticpath.domain.misc.CurrencyCodeComparator. */
    public static final String CURRENCYCODE_COMPARATOR = "currencyCodeComparator";

    /** bean id for implementation of com.elasticpath.domain.misc.CouponUsageByCouponCodeComparator. */
    public static final String COUPON_USAGE_CODE_COMPARATOR = "couponUsageByCouponCodeComparator";

    /** bean id for implementation of org.apache.lucene.analysis.Analyzer. */
    public static final String SYNONYM_ANALYZER = "synonymAnalyzer";

    /** bean id for implementation of com.elasticpath.domain.payment.CreditCardPaymentHandler. */
    public static final String PAYMENT_HANDLER_CREDITCARD = "paymentHandlerCreditCard";

    /** bean id for implementation of com.elasticpath.domain.payment.TokenPaymentHandler. */
    public static final String PAYMENT_HANDLER_TOKEN = "paymentHandlerToken";
    
    /** bean id for implementation of com.elasticpath.domain.payment.PayPalExpressPaymentHandler. */
    public static final String PAYMENT_HANDLER_PAYPAL = "paymentHandlerPayPal";

    /** bean id for implementation of com.elasticpath.domain.payment.GooglePaymentHandler. */
    public static final String PAYMENT_HANDLER_GOOGLE = "paymentHandlerGoogleCheckout";

    /** bean id for implementation of com.elasticpath.domain.payment.ExchangePaymentHandler. */
    public static final String PAYMENT_HANDLER_EXCHANGE = "paymentHandlerReturnAndExchange";

    /** bean id for implementation of com.elasticpath.domain.payment.GiftCertificatePaymentHandler. */
    public static final String PAYMENT_HANDLER_GIFTCERTIFICATE = "paymentHandlerGiftCertificate";

    /** bean id for implementation of com.elasticpath.domain.misc.CmUserEmailPropertyHelper. */
    public static final String EMAIL_PROPERTY_HELPER_CMUSER = "emailPropertyHelperCmUser";

    /** bean id for implementation of com.elasticpath.domain.misc.CustomerEmailPropertyHelper. */
    public static final String EMAIL_PROPERTY_HELPER_CUSTOMER = "emailPropertyHelperCustomer";

    /** bean id for implementation of com.elasticpath.domain.misc.GiftCertificateEmailPropertyHelper. */
    public static final String EMAIL_PROPERTY_HELPER_GIFT_CERT = "emailPropertyHelperGiftCertificate";

    /** bean id for implementation of com.elasticpath.domain.misc.ImportEmailPropertyHelper. */
    public static final String EMAIL_PROPERTY_HELPER_IMPORT = "emailPropertyHelperImport";

    /** bean id for implementation of com.elasticpath.service.misc.OrderEmailPropertyHelper. */
    public static final String EMAIL_PROPERTY_HELPER_ORDER = "emailPropertyHelperOrder";

    /** bean id for implementation of com.elasticpath.domain.misc.WishListEmailPropertyHelper. */
    public static final String EMAIL_PROPERTY_HELPER_WISHLIST = "emailPropertyHelperWishList";

    /** bean id for implementation of {@link com.elasticpath.persistence.api.FetchGroupLoadTuner}. */
    public static final String FETCH_GROUP_LOAD_TUNER = "fetchGroupLoadTuner";

    /** bean id for implementation of {@link com.elasticpath.domain.search.IndexNotification}. */
    public static final String INDEX_NOTIFICATION = "indexNotification";

    /** bean id for implementation of {@link com.elasticpath.service.search.IndexNotificationService}. */
    public static final String INDEX_NOTIFICATION_SERVICE = "indexNotificationService";

    /** bean id for implementation of com.elasticpath.service.order.AllocationService. */
    public static final String ALLOCATION_SERVICE = "allocationService";

    /** bean id for implementation of com.elasticpath.service.catalog.AllocationResult. */
    public static final String ALLOCATION_RESULT = "allocationResult";

    /** bean id for implementation of com.elasticpath.service.order.OrderAllocationProcessor. */
    public static final String ORDER_ALLOCATION_PROCESSOR = "orderAllocationProcessor";

    /** bean id for implementation of com.elasticpath.commons.util.MailMessage. */
    public static final String MAILER_BEAN = "mailerBean";

    /** bean id for abstract payment gateway implementation. */
    public static final String ABSTRACT_PAYMENT_GATEWAY = "abstractPaymentGateway";

    /** Bean id for implementation of {@link com.elasticpath.domain.rules.EpRuleBase}. */
    public static final String EP_RULE_BASE = "epRuleBase";

    /** Bean id for implementation of {@link com.elasticpath.domain.rules.Coupon}. */
    public static final String COUPON = "coupon";

    /** Bean id for implementation of {@link com.elasticpath.domain.rules.CouponUsage}. */
    public static final String COUPON_USAGE = "couponUsage";

    /** Bean id for implementation of {@link com.elasticpath.domain.rules.CouponConfig}. */
    public static final String COUPON_CONFIG = "couponConfig";

    /** bean id for abstract rule element. */
    public static final String ABSTRACT_RULE_ELEMENT = "abstractRuleElement";

    /** bean id for com.elasticpath.domain.shoppingcart.ShoppingCartMemento. */
    public static final String SHOPPING_CART_MEMENTO = "shoppingCartMemento";

    /** bean id for com.elasticpath.settings.domain.SettingDefinition. */
    public static final String SETTING_DEFINITION = "settingDefinition";

    /** bean id for com.elasticpath.settings.domain.SettingMetadata. */
    public static final String SETTING_METADATA = "settingMetadata";

    /** bean id for com.elasticpath.com.elasticpath.settings. */
    public static final String SETTINGS_SERVICE = "settingsService";

    /** bean id for implementation of com.elasticpath.domain.advancedsearch.AdvancedSearchQuery. */
    public static final String ADVANCED_SEARCH_QUERY = "advancedSearchQuery";

    /** bean id for implementation of com.elasticpath.persistence.dao.impl.AdvancedSearchQueryDaoImpl. */
    public static final String ADVANCED_SEARCH_QUERY_DAO = "advancedSearchQueryDao";

    /** bean id for implementation of com.elasticpath.service.command.impl.UpdateStoreCommandImpl. */
    public static final String UPDATE_STORE_COMMAND = "updateStoreCommand";

    /** bean id for implementation of com.elasticpath.service.command.impl.UpdateStoreCommandResultImpl. */
    public static final String UPDATE_STORE_COMMAND_RESULT = "updateStoreCommandResult";

    /** bean id for implementation of com.elasticpath.service.command.impl.CommandServiceImpl. */
    public static final String COMMAND_SERVICE = "commandService";

    /** bean id for implementation of com.elasticpath.service.reporting.impl.ReportServiceImpl. */
    public static final String REPORT_SERVICE = "reportService";

    /** bean id for com.elasticpath.domain.search.impl.IndexBuildStatusImpl. */
    public static final String INDEX_BUILD_STATUS = "indexBuildStatus";

    /** bean id for implementation of {@link com.elasticpath.domain.FileSystemConnectionInfo}. */
    public static final String CONNECTION_INFO = "connectionInfo";

    /** bean id for implementation of {@link com.elasticpath.domain.catalogview.impl.StoreSeoUrlBuilderFactoryImpl}. */
    public static final String STORE_SEO_URL_BUILDER_FACTORY = "storeSeoUrlBuilderFactory";

    /** bean id for implementation of {@link com.elasticpath.domain.contentspace.impl.ContentWrapperImpl}. */
    public static final String CONTENT_WRAPPER = "contentWrapper";

    /** bean id for {@link com.elasticpath.domain.misc.LocalizedPropertyValue}. */
    public static final String BRAND_LOCALIZED_PROPERTY_VALUE = "brandLocalizedPropertyValue";

    /** bean id for {@link com.elasticpath.domain.misc.LocalizedPropertyValue}. */
    public static final String SHIPPING_SERVICE_LEVEL_LOCALIZED_PROPERTY_VALUE = "shippingServiceLevelLocalizedPropertyValue";

    /** bean id for {@link com.elasticpath.domain.misc.LocalizedPropertyValue}. */
    public static final String SKU_OPTION_LOCALIZED_PROPERTY_VALUE = "skuOptionLocalizedPropertyValue";

    /** bean id for {@link com.elasticpath.domain.misc.LocalizedPropertyValue}. */
    public static final String SKU_OPTION_VALUE_LOCALIZED_PROPERTY_VALUE = "skuOptionValueLocalizedPropertyValue";

    /** bean id for {@link com.elasticpath.domain.misc.LocalizedPropertyValue}. */
    public static final String TAX_CATEGORY_LOCALIZED_PROPERTY_VALUE = "taxCategoryLocalizedPropertyValue";

    /** bean id for {@link com.elasticpath.csvimport.CsvReadResult}. */
    public static final String CSV_READ_RESULT = "csvReadResult";

    /** bean id for {@link com.elasticpath.csvimport.CsvReaderConfiguration}. */
    public static final String CSV_READER_CONFIGURATION = "csvReaderConfiguration";

    /** bean id for {@link com.elasticpath.csvimport.ImportValidRow}. */
    public static final String IMPORT_VALID_ROW = "importValidRow";

    /** bean id for {@link com.elasticpath.domain.objectgroup.BusinessObjectGroupMember}. */
    public static final String BUSINESS_OBJECT_GROUP_MEMBER = "businessObjectGroupMember";

    /** bean id for {@link com.elasticpath.domain.changeset.ChangeSet}. */
    public static final String CHANGE_SET = "changeSet";

    /** bean id for {@link com.elasticpath.domain.objectgroup.BusinessObjectDescriptor}. */
    public static final String BUSINESS_OBJECT_DESCRIPTOR = "businessObjectDescriptor";

    /** bean id for {@link com.elasticpath.service.changeset.ChangeSetService}. */
    public static final String CHANGESET_SERVICE = "changeSetService";

    /** bean id for {@link com.elasticpath.service.changeset.ChangeSetManagementService}. */
    public static final String CHANGESET_MANAGEMENT_SERVICE = "changeSetManagementService";

    /** bean id for {@link com.elasticpath.domain.changeset.ChangeSetObjectStatus}. */
    public static final String CHANGESET_OBJECT_STATUS = "changeSetObjectStatus";

    /** bean id for {@link com.elasticpath.domain.objectgroup.BusinessObjectMetadata}. */
    public static final String BUSINESS_OBJECT_METADATA = "businessObjectMetadata";

    /** bean id for {@link com.elasticpath.domain.changeset.ChangeSetMember}. */
    public static final String CHANGESET_MEMBER = "changeSetMember";

    /** bean id for {@link com.elasticpath.service.misc.TimeService}. */
    public static final String TIME_SERVICE = "timeService";

    /** bean id for {@link com.elasticpath.domain.misc.LocalizedPropertyValue}. */
    public static final String TAG_DEFINITION_LOCALIZED_PROPERTY_VALUE = "tagDefinitionLocalizedPropertyValue";

    /** bean id for {@link com.elasticpath.domain.misc.LocalizedPropertyValue}. */
    public static final String TAG_GROUP_LOCALIZED_PROPERTY_VALUE = "tagGroupLocalizedPropertyValue";

    /** bean id for {@link com.elasticpath.domain.misc.LocalizedPropertyValue}. */
    public static final String TAG_ALLOWED_VALUE_LOCALIZED_PROPERTY_VALUE = "tagAllowedValueDescriptionLocalizedPropertyValue";

    /** bean id for {@link com.elasticpath.domain.misc.LocalizedPropertyValue}. */
    public static final String TAG_OPERATOR_LOCALIZED_PROPERTY_VALUE = "tagOperatorLocalizedPropertyValue";

    /** bean id for {@link com.elasticpath.tags.TagSet}. */
    public static final String TAG_SET = "tagSet";

    /** bean id for {@link com.elasticpath.tags.domain.TagDefinition}. */
    public static final String TAG_DEFINITION = "tagDefinition";

    /** bean id for {@link com.elasticpath.tags.domain.TagGroup}. */
    public static final String TAG_GROUP = "tagGroup";

    /** bean id for {@link com.elasticpath.tags.domain.TagDictionary}. */
    public static final String TAG_DICTIONARY = "tagDictionary";

    /** bean id for {@link com.elasticpath.tags.domain.TagValueType}. */
    public static final String TAG_VALUE_TYPE = "tagValueType";

    /** bean id for {@link com.elasticpath.tags.domain.TagAllowedValue}. */
    public static final String TAG_ALLOWED_VALUE = "tagAllowedValue";

    /** bean id for {@link com.elasticpath.tags.domain.TagOperator}. */
    public static final String TAG_OPERATOR = "tagOperator";

    /** bean id for {@link com.elasticpath.tags.domain.ConditionalExpression}. */
    public static final String TAG_CONDITION = "tagCondition";

    /** bean id for {@link com.elasticpath.tags.dao.TagDefinitionDao}. */
    public static final String TAG_DEFINITION_DAO = "tagDefinitionDao";

    /** bean id for {@link com.elasticpath.tags.dao.TagValueTypeDao}. */
    public static final String TAG_VALUE_TYPE_DAO = "tagValueTypeDao";

    /** bean id for {@link com.elasticpath.tags.dao.TagDictionaryDao}. */
    public static final String TAG_DICTIONARY_DAO = "tagDictionaryDao";

    /** bean id for {@link com.elasticpath.tags.dao.ConditionalExpressionDao}. */
    public static final String TAG_CONDITION_DAO = "tagConditionDao";

    /** bean id for {@link com.elasticpath.tags.service.TagDefinitionService}. */
    public static final String TAG_DEFINITION_SERVICE = "tagDefinitionService";

    /** bean id for {@link com.elasticpath.tags.service.TagOperatorService}. */
    public static final String TAG_OPERATOR_SERVICE = "tagOperatorService";

    /** bean id for {@link com.elasticpath.tags.service.TagValueTypeService}. */
    public static final String TAG_VALUE_TYPE_SERVICE = "tagValueTypeService";

    /** bean id for {@link com.elasticpath.tags.service.TagDictionaryService}. */
    public static final String TAG_DICTIONARY_SERVICE = "tagDictionaryService";

    /** bean id for {@link com.elasticpath.tags.service.TagGroupService}. */
    public static final String TAG_GROUP_SERVICE = "tagGroupService";

    /** bean id for {@link com.elasticpath.tags.service.ConditionService}. */
    public static final String TAG_CONDITION_SERVICE = "tagConditionService";

    /** bean id for {@link com.elasticpath.tags.service.ConditionEvaluatorService}. */
    public static final String TAG_CONDITION_EVALUATOR_SERVICE = "conditionEvaluationService";

    /** bean id for {@link com.elasticpath.tags.service.ConditionDSLBuilder}. */
    public static final String TAG_CONDITION_DSL_BUILDER = "tagConditionDSLBuilder";

    /** bean id for {@link com.elasticpath.tags.service.SelectableTagValueServiceFacade}. */
    public static final String TAG_SELECTABLE_VALUES_SERVICE = "selectableTagValuesService";


    /** bean id for {@link com.elasticpath.tags.service.ConditionValidationFacade}. */
    public static final String TAG_CONDITION_VALIDATION_FACADE = "conditionValidationFacade";


    /** bean id for {@link com.elasticpath.domain.sellingcontext.SellingContext}. */
    public static final String SELLING_CONTEXT = "sellingContext";

    /** bean id for {@link com.elasticpath.service.sellingcontext.SellingContextService}. */
    public static final String SELLING_CONTEXT_SERVICE = "sellingContextService";

    /** bean id for {@link com.elasticpath.tags.domain.impl.ConditionalExpressionImpl}. */
    public static final String CONDITIONAL_EXPRESSION = "conditionalExpression";

    /** bean id for {@link com.elasticpath.tags.domain.Condition}. */
    public static final String CONDITION = "condition";

    /** bean id for {@link com.elasticpath.tags.domain.LogicalOperator}. */
    public static final String LOGICAL_OPERATOR = "logicalOperator";

    /** bean id for {@link com.elasticpath.service.changeset.ChangeSetLoadTuner}. */
    public static final String CHANGESET_LOAD_TUNER = "changeSetLoadTuner";

    /** bean id for {@link com.elasticpath.commons.pagination.PaginatorFactory}. */
    public static final String PAGINATOR_FACTORY = "paginatorFactory";

    /** bean id for {@link com.elasticpath.commons.pagination.PaginationConfig}. */
    public static final String PAGINATION_CONFIG = "paginationConfig";

    /** bean id for {@link com.elasticpath.tags.service.ConditionBuilder}. */
    public static final String TAG_CONDITION_BUILDER = "tagConditionBuilder";

    /** bean id for {@link com.elasticpath.service.search.query.ShippingServiceLevelSearchCriteria}. */
    public static final String SHIPPING_SERVICE_LEVEL_SEACRH_CRITERIA = "shippingServiceLevelSearchCriteria";

    /** bean id for {@link com.elasticpath.sfweb.controller.impl.DefaultProductConfigController}. */
    public static final String DEFAULT_PRODUCT_CONFIG_CONTROLLER = "defaultProductConfigController";

    /** bean id for {@link com.elasticpath.domain.dataimport.ImportNotification}. */
    public static final String IMPORT_NOTIFICATION = "importNotification";

    /** bean id for {@link com.elasticpath.domain.dataimport.ImportJobStatus}. */
    public static final String IMPORT_JOB_STATUS = "importJobStatus";

    /** bean id for {@link com.elasticpath.domain.dataimport.ImportJobRequest}. */
    public static final String IMPORT_JOB_REQUEST = "importJobRequest";

    /** bean id for {@link com.elasticpath.domain.catalog.SelectionRule}. */
    public static final String BUNDLE_SELECTION_RULE = "bundleSelectionRule";

    /** bean id for {@link com.elasticpath.common.dto.InventoryDetails}. */
    public static final String INVENTORY_DETAILS = "inventoryDetails";

    /** bean id for {@link com.elasticpath.domain.catalog.impl.InventoryCalculatorImpl}. */
    public static final String INVENTORY_CALCULATOR = "inventoryCalculator";

    /** bean id for {@link com.elasticpath.service.changeset.dao.impl.ChangeSetMemberDaoImpl}. */
    public static final String CHANGE_SET_MEMBER_DAO = "changeSetMemberDao";

    /** bean id for {@link com.elasticpath.service.objectgroup.dao.impl.BusinessObjectGroupImpl}. */
    public static final String BUSINESS_OBJECT_GROUP_DAO = "businessObjectGroupDao";

    /** bean id for {@link com.elasticpath.domain.rules.impl.CouponAssignmentActionImpl}. */
    public static final String COUPON_ASSIGNMENT_ACTION = "couponAssignmentAction";

    /** bean id for {@link com.elasticpath.domain.rules.impl.RuleLocalizedPropertyValueImpl}. */
    public static final String RULE_LOCALIZED_PROPERTY_VALUE = "ruleLocalizedPropertyValue";

    /** bean id for {@link com.elasticpath.domain.rules.csvimport.impl.ImportDataTypeCouponCodeImpl}. */
    public static final String IMPORT_DATA_TYPE_COUPONCODE = "importDataTypeCouponCode";

    /** bean id for {@link com.elasticpath.domain.rules.csvimport.impl.ImportDataTypeCouponCodeEmailImpl}. */
    public static final String IMPORT_DATA_TYPE_COUPONCODE_EMAIL = "importDataTypeCouponCodeAndEmail";

    /** bean id for {@link com.elasticpath.common.dto.CouponDtoMediatorImpl}. */
    public static final String COUPON_DTO_MEDIATOR = "couponDtoMediator";

    /** bean id for {@link com.elasticpath.common.dto.CouponUsageDtoMediatorImpl}. */
    public static final String COUPON_USAGE_DTO_MEDIATOR = "couponUsageDtoMediator";

    /** bean id for {@link com.elasticpath.service.search.query.SkuSearchCriteria}. */
    public static final String SKU_SEARCH_CRITERIA = "skuSearchCriteria";

    /** bean id for {@link com.elasticpath.domain.shoppingcart.impl.WishListImpl}. */
    public static final String WISH_LIST = "wishList";

    /** bean id for {@link com.elasticpath.service.shoppingcart.impl.WishListServiceImpl}. */
    public static final String WISH_LIST_SERVICE = "wishListService";

    /** bean id for {@link com.elasticpath.domain.shopper.impl.ShopperImpl}. */
    public static final String SHOPPER = "shopper";

    /** bean id for {@link com.elasticpath.domain.shopper.impl.ShopperMementoImpl}. */
    public static final String SHOPPER_MEMENTO = "shopperMemento";

    /** bean id for implementation of com.elasticpath.service.shopper.ShopperCleanupService. */
    public static final String SHOPPER_CLEANUP_SERVICE = "shopperCleanupService";

    /** bean id for implementation of com.elasticpath.service.shopper.ShopperService. */
    public static final String SHOPPER_SERVICE = "shopperService";

    /** bean id for {@link com.elasticpath.domain.catalogview.impl.SkuOptionValueFilterImpl}. */
    public static final String SKU_OPTION_VALUE_FILTER = "skuOptionValueFilter";

    /** bean id for {@link com.elasticpath.domain.catalogview.search.impl.AdvancedSearchRequestImpl}. */
    public static final String ADVANCED_SEARCH_REQUEST = "advancedSearchRequest";

    /** bean id for {@link com.elasticpath.service.catalogview.FilterFactory}. */
    public static final String FILTER_FACTORY = "filterFactory";

    /** bean id for {@link com.elasticpath.domain.catalogview.search.AdvancedSearchConfigurationProvider}. */
    public static final String ADVANCED_SEARCH_CONFIGURATION_PROVIDER = "advancedSearchConfigurationProvider";

    /** bean id for {@link com.elasticpath.sfweb.formbean.AdvancedSearchControllerFormBean}. */
    public static final String ADVANCED_SEARCH_CONTROLLER_FORM_BEAN = "advancedSearchControllerFormBean";

    /** bean id for {@link com.elasticpath.domain.catalogview.impl.AdvancedSearchFilteredNavSeparatorFilter}. */
    public static final String ADVANCED_SEARCH_FILTERED_NAV_SEPARATOR_FILTER = "advancedSearchFilteredNavSeparatorFilter";

    /** bean id for {@link com.elasticpath.service.misc.impl.CurrencyServiceImpl}. */
    public static final String CURRENCY_SERVICE = "currencyService";

    /** bean id for {@link com.elasticpath.domain.subscriptions.PaymentSchedule}. */
    public static final String PAYMENT_SCHEDULE = "paymentSchedule";

    /** bean id for {@link com.elasticpath.domain.PricingScheme}. */
    public static final String PRICING_SCHEME = "pricingScheme";

    /** bean id for {@link com.elasticpath.domain.PriceSchedule}. */
    public static final String PRICE_SCHEDULE = "priceSchedule";

    /** bean id for (@link com.elasticpath.domain.shoppingcart.ShoppingItemRecurringPrice}. */
    public static final String SHOPPING_ITEM_RECURRING_PRICE = "shoppingItemRecurringPrice";

    /** bean id for {@link com.elasticpath.domain.shoppingcart.impl.ShoppingItemSimplePrice}. */
    public static final String SHOPPING_ITEM_SIMPLE_PRICE = "shoppingItemSimplePrice";

    /** bean id for {@link com.elasticpath.domain.order.ServiceOrderShipment}. */
    public static final String SERVICE_ORDER_SHIPMENT = "serviceOrderShipment";

    /** bean id for {@link com.elasticpath.sellingchannel.impl.ShoppingItemRecurringPriceAssemblerImpl}. */
    public static final String SHOPPING_ITEM_RECURRING_PRICE_ASSEMBLER = "shoppingItemRecurringPriceAssembler";

    /** bean id for {@link com.elasticpath.service.pricing.datasource.BaseAmountDataSourceFactoryBuilder}. */
    public static final String BASE_AMOUNT_DATA_SOURCE_FACTORY_BUILDER = "baseAmountDataSourceFactoryBuilder";

    /**
     * bean id for {@link com.elasticpath.inventory.impl.InventoryDtoImpl}.
     */
    public static final String INVENTORYDTO = "inventoryDto";

    /** bean id for {@link com.elasticpath.inventory.impl.InventoryFacadeImpl}. */
    public static final String INVENTORY_FACADE = "inventoryFacade";

    /** bean id for {@link com.elasticpath.inventory.domain.impl.InventoryJournalLockImpl}. */
    public static final String INVENTORY_JOURNAL_LOCK = "inventoryJournalLock";

    /** bean id for {@link com.elasticpath.inventory.dao.impl.InventoryDaoImpl}. */
    public static final String INVENTORY_DAO = "inventoryDao";

    /** bean id for {@link com.elasticpath.inventory.dao.impl.InventoryJournalDaoImpl}. */
    public static final String INVENTORY_JOURNAL_DAO = "inventoryJournalDao";

    /** bean id for {@link com.elasticpath.inventory.dao.impl.InventoryJournalLockDaoImpl}. */
    public static final String INVENTORY_JOURNAL_LOCK_DAO = "inventoryJournalLockDao";

    /** bean id for {@link com.elasticpath.service.cartorder.dao.impl.CartOrderDaoImpl}. */
    public static final String CART_ORDER_DAO = "cartOrderDao";

    /** bean id for {@link com.elasticpath.service.cartorder.impl.CartOrderServiceImpl}. */
    public static final String CART_ORDER_SERVICE = "cartOrderService";

    /** bean id for {@link com.elasticpath.service.customer.dao.impl.CustomerAddressDaoImpl}. */
    public static final String CUSTOMER_ADDRESS_DAO = "customerAddressDao";

    /** bean id for {@link com.elasticpath.domain.catalog.StoreProductLoadTuner}. */
    public static final String STORE_PRODUCT_LOAD_TUNER_FOR_ADD_TO_CART = "productLoadTunerForSFAddToCart";

    /** bean id for {@link com.elasticpath.service.auth.OAuth2AccessTokenService}. */
    public static final String OAUTH2_ACCESS_TOKEN_SERVICE = "oAuth2AccessTokenService";

    /** bean id for {@link com.elasticpath.domain.auth.OAuth2AccessTokenMemento}. */
    public static final String OAUTH2_ACCESS_TOKEN_MEMENTO = "oAuth2AccessTokenMemento";

	/** bean id for {@link com.elasticpath.domain.cartorder.CartOrder}. */
    public static final String CART_ORDER = "cartOrder";

    /** bean id for {@link com.elasticpath.service.changeset.helper.impl.ChangeSetHelperImpl}. */
	public static final String CHANGESET_HELPER = "changeSetHelper";

	/** bean id for {@link com.elasticpath.domain.catalog.ItemConfigurationMemento}. */
	public static final String ITEM_CONFIGURATION_MEMENTO = "itemConfigurationMemento";

	/** bean id for {@link com.elasticpath.service.catalog.ItemConfigurationService}. */
	public static final String ITEM_CONFIGURATION_SERVICE = "itemConfigurationService";

	/** bean id for {@link com.elasticpath.service.catalog.ItemCharacteristicsService}. */
	public static final String ITEM_CHARACTERISTICS_SERVICE = "itemCharacteristicsService";

	/** bean id for {@link com.elasticpath.service.catalog.ProductCharacteristicsService}. */
	public static final String PRODUCT_CHARACTERISTICS_SERVICE = "productCharacteristicsService";

	/** bean id for {@link com.elasticpath.domain.search.query.SearchTermsMemento}. */
	public static final String SEARCH_TERMS_MEMENTO = "searchTermsMemento";

	/** bean id for {@link com.elasticpath.domain.search.query.SearchTerms}. */
	public static final String SEARCH_TERMS = "searchTerms";

	/** Bean id for {@link com.elasticpath.domain.search.query.SearchTermsActivity}. */
	public static final String SEARCH_TERMS_ACTIVITY = "searchTermsActivity";

	/** bean id for {@link com.elasticpath.service.targetedselling.impl.ContentSpaceQueryService}. */
	public static final String CONTENTSPACE_QUERY_SERVICE = "contentSpaceQueryService";

	/** bean id for {@link com.elasticpath.service.contentspace.impl.ParameterValueQueryService}. */
	public static final String PARAMETER_VALUE_QUERY_SERVICE = "parameterValueQueryService";

	/** bean id for {@link com.elasticpath.domain.asset.ImageMap}. */
	public static final String IMAGE_MAP = "imageMap";

	/** bean id for {@link com.elasticpath.domain.asset.ImageMapWithAbsolutePath}. */
	public static final String IMAGE_MAP_WITH_ABSOLUTE_PATH = "imageMapWithAbsolutePath";

	/** bean id for {@link com.elasticpath.service.asset.impl.ProductImageDiscoveryServiceImpl}. */
	public static final String PRODUCT_IMAGE_DISCOVERY_SERVICE = "productImageDiscoveryService";

	/** bean id for {@link com.elasticpath.service.asset.impl.SkuImageDiscoveryServiceImpl}. */
	public static final String SKU_IMAGE_DISCOVERY_SERVICE = "skuImageDiscoveryService";

	/** bean id for {@link com.com.elasticpath.validation.service.impl.ValidatorUtilsImpl}. */
	public static final String VALIDATOR_UTILS = "validatorUtils";

	/** bean id for {@link com.elasticpath.domain.shipping.evaluator.impl.ShoppingCartShipmentTypeEvaluator}. */
	public static final String SHOPPING_CART_SHIPMENT_TYPE_EVALUATOR = "shoppingCartShipmentTypeEvaluator";

	/** bean id for {@link com.elasticpath.service.security.SaltFactory}. */
	public static final String SALT_FACTORY = "saltFactory";

	/** bean id for {@link com.elasticpath.domain.catalog.impl.LocaleFallbackPolicyFactory. **/
	public static final String LOCALE_FALLBACK_POLICY_FACTORY = "localeFallbackPolicyFactory";
}
