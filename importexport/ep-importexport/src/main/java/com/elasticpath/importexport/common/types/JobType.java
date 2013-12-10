package com.elasticpath.importexport.common.types;

import java.util.HashMap;
import java.util.Map;

/**
 * Job Type determines type of export/import operations. Each Job Type reflects type of complex object to be processed: Product for example. So
 * appropriate classes can be used for execution of the job according to its type.
 */
public enum JobType {

	/** Job Type for Product. */
	PRODUCT("products"),

	/** Job Type for ProductBundle. */
	PRODUCTBUNDLE("bundles"),

	/** Job Type for Catalog. */
	CATALOG("catalogs"),

	/** Job Type for Category. */
	CATEGORY("categories"),

	/** Job Type for Promotion. */
	PROMOTION("promotions"),

	/** Job Type for Promotion. */
	COUPONSET("coupon_sets"),

	/** Job Type for Condition Rules. */
	CONDITIONRULE("condition_rules"),

	/** Job type for Assets. */
	ASSETS("assets"),

	/** Job type for Inventory. */
	INVENTORY("inventory"),

	/** Job type for Pricing. */
	PRICING("pricing"),

	/** Job type for ProductAssociations. */
	PRODUCTASSOCIATION("productassociations"),

	/** Job type for ProductCategoryAssociations. */
	PRODUCTCATEGORYASSOCIATION("productcategoryassociations"),

	/** Job Type for Price List Descriptor. */
	PRICELISTDESCRIPTOR("price_lists"),

	/** Job Type for Base Amount. */
	BASEAMOUNT("amounts"),

	/** Job type for SystemConfiguration. */
	SYSTEMCONFIGURATION("system_configuration"),

	/** Job type for Dynamic Content. */
	DYNAMICCONTENT("dynamic_contents"),

	/** Job type for Dynamic Content Delivery. */
	DYNAMICCONTENTDELIVERY("dynamic_content_deliveries"),

	/** Job type for PLAs. */
	PRICELISTASSIGNMENT("price_list_assignments"),

	/** Payment gateways. */
	PAYMENTGATEWAY("payment_gateways"),

	/** Warehouses. */
	WAREHOUSE("warehouses"),

	/** Job type for tax codes. */
	TAXCODE("tax_codes"),

	/** Job type for tax jurisdictions. */
	TAXJURISDICTION("tax_jurisdictions"), 

	/** Job type for customer profile attributes. */
	 CUSTOMERPROFILE_ATTRIBUTE("customerprofile_attributes"),
	
	/** Stores. */
	STORE("stores"), 

	/** Store Associations. */
	STORE_ASSOCIATION("store_associations"), 
	
	/** Customers. */
	CUSTOMER("customers"),
	
	/** User Roles. */
	USER_ROLE("user_roles"),

	/** Commerce Manager Users. */
	CMUSER("cmusers"),

	/** Gift certificates. */
	GIFT_CERTIFICATE("gift_certificates"),
	
	/** Job type for Content Spaces. */
	CONTENTSPACE("contentspaces"),
	
	/** Job type for Shipping Service Level. */
	SHIPPING_SERVICE_LEVEL("shipping_service_levels"),
	
	/** Job type for Shipping Regions. */
	SHIPPING_REGION("shipping_regions"),
	
	/** Job type for Saved Conditions. */
	SAVED_CONDITION("saved_conditions"),
	
	/** Job Type for ImportJob. */
	CM_IMPORT_JOB("cmimportjobs");
	
	/*
	 * The map of Job Types available in the system. XML tag name is the key and JobType is the value.
	 */
	private static Map<String, JobType> jobTypeMap = new HashMap<String, JobType>();

	/*
	 * Fill the map with Job Types registered in this enumeration.
	 */
	static {
		for (JobType jobType : JobType.values()) {
			jobTypeMap.put(jobType.getTagName(), jobType);
		}
	}

	/*
	 * String representation of job type name is used as tag name in XML files produced/processed by jobs.
	 */
	private final String tagName;

	/*
	 * Constructor is disabled to ensure that only standard job types can be used.
	 */
	private JobType(final String tagName) {
		this.tagName = tagName;
	}

	/**
	 * Gets the name of XML tag representing Job Type.
	 * 
	 * @return the tag name
	 */
	public String getTagName() {
		return tagName;
	}

	/**
	 * Finds JobType in the map by its XML tag name.
	 * 
	 * @param tagName XML tag name
	 * @return appropriate JobType if exists
	 */
	public static JobType getJobTypeByTag(final String tagName) {
		return jobTypeMap.get(tagName);
	}

}
