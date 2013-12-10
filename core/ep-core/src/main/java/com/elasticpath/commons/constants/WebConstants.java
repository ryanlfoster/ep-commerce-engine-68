package com.elasticpath.commons.constants;

import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

/**
 * <code>WebConstants</code> contains constants for the web layer.
 */
public class WebConstants {

	/**
	 * SPACE constant.
	 */
	public static final String SPACE = " ";

	/**
	 * Variable indicates if the url is http or https.
	 */
	public static final String IS_SECURE = "isSecure";

	/**
	 * The WEB-INF directory name.
	 */
	public static final String WEB_INF_DIR_NAME = "WEB-INF";

	/** Maximum age for a cookie. */
	public static final int MAX_COOKIE_AGE = 60 * 60 * 24 * 365 * 10; // 10 years

	//======================================
	// Object names used for cookies
	//======================================
	/** Customer session guid. */
	public static final String CUSTOMER_SESSION_GUID = "customerSessionGuid";

	//======================================
	// Object names used in the web context
	//======================================
	/** <code>ElasticPath</code> object. */
	public static final String ELASTICPATH = "ctxEp";

	/** {@link com.elasticpath.commons.util.impl.VelocityGeographyHelperImpl} object. */
	public static final String GEOGRAPHY_HELPER = "ctxCountries";

	/**
	 * All top categories.
	 */
	public static final String TOPCATEGORIESHELPER = "ctxTopCategoriesHelper";

	/**
	 * The configured SeoUrlBuilder.
	 */
	public static final String SEOURLBUILDER = "ctxSeoUrlBuilder";

	/**
	 * The {@link com.elasticpath.domain.misc.MoneyFormatter} object.
	 */
	public static final String MONEY_FORMATTER = "moneyFormatter";

	//======================================
	// Object names used in the user session
	//======================================
	/**<code>ShoppingCart</code> object. */
	public static final String SHOPPING_CART = "sesShoppingCart";

	/**<code>CmUserSession</code> object. */
	public static final String CM_USER_SESSION = "cmUserSes";

	/** <code>PayPalExpressSession</code> object. */
	public static final String PAYPAL_EXPRESS_CHECKOUT_SESSION = "paypalExpressCheckoutSession";

	/** <code>OrderPaymentSession</code> object. */
	public static final String ORDER_PAYMENT_SESSION = "orderPaymentSession";

	/** <code>PayerAuthenticationSession</code> object. */
	public static final String PAYER_AUTHENTICATION_SESSION = "payerAuthenticationSession";

	/** <code>PayerAuthenticationEnrollmentSession</code> object. */
	public static final String PAYER_AUTHENTICATION_ENROLLMENT_SESSION = "payerAuthenticationEnrollmentSession";

	//======================================
	// State flags used in the session
	//======================================
	/**Indicates that the user is signing in as part of the checkout process. */
	public static final String IS_CHECKOUT_SIGN_IN = "sesCheckoutSignIn";


	//======================================
	// Dynamic content rewrite rule pattern
	//======================================
	/**
	 * Used for generating and resolving paths of assest for dynamic content.
	 * If you need to modify this please check web.xml, url-mapping.xml and
	 * DynamicContentAssetResourceRetrievalStrategy.
	 */
	public static final String DYNAMIC_CONTENT_REWRITE_PATTERN = "/content/dcassets/";


	//======================================
	// Object name used in the request
	//======================================

	/**
	 * This name is used to specify browsed product in the request.
	 */
	public static final String REQUEST_BROWSED_PRODUCT = "browsed_product";

	/**
	 * This name is used to specify order Uid in a request.
	 */
	public static final String REQUEST_OID = "oID";

	/**
	 * This name is used to specify category Uid in a request.
	 */
	public static final String REQUEST_CID = "cID";

	/**
	 * This name is used to specify a page number used to list
	 * products in a category in a request.
	 */
	public static final String REQUEST_PAGE_NUM = "pn";

	/**
	 * This name is used to specify product Uid in a request.
	 */
	public static final String REQUEST_PID = "pID";

	/**
	 * Product view page.
	 */
	public static final String PRODUCT_VIEW_PAGE = "product-view.ep";

	/**
	 * This name is used to specify product Guid in a request from PowerReviews.
	 */
	public static final String REQUEST_POWER_REVIEWS_PGUID = "pageId";

	/**
	 * This name is used to specify product Guid (code) in a request.
	 */
	public static final String REQUEST_PGUID = "pGUID";

	/**
	 * This name is used to specify product SKU Uid in a request.
	 */
	public static final String REQUEST_SKUGUID = "skuGuid";

	/**
	 * This name is used to specify address Uid in a request.
	 */
	public static final String REQUEST_ADDRESS_ID = "addressID";

	/**
	 * This name is used to specify credit card Uid in a request.
	 */
	public static final String REQUEST_CUSTOMER_CREDIT_CARD_ID = "creditCardID";

	/**
	 * This name is used to specify that a shipping address is being edited from the checkout.
	 */
	public static final String REQUEST_EDIT_SHIPPING_ADDRESS = "checkoutshipping";

	/**
	 * This name is used to specify that a shipping address is being edited from the checkout.
	 */
	public static final String REQUEST_EDIT_BILLING_ADDRESS = "checkoutbilling";

	/**
	 * This name is used to specify a product association Uid in a request.
	 */
	public static final String REQUEST_PRODUCT_ASSOCIATION_ID = "productAssociationID";

	/**
	 * This name is used to specify a product quantity in a request.
	 */
	public static final String REQUEST_QUANTITY = "quantity";

	/**
	 * This name is used to specify search key words in a request.
	 */
	public static final String REQUEST_KEYWORDS = "keyWords";

	/**
	 * This name is used to specify the category ID in a request.
	 */
	public static final String REQUEST_CATEGORY_ID = "categoryId";

	/**
	 * This name is used to specify search filter in a request.
	 */
	public static final String REQUEST_FILTERS = "filters";

	/**
	 * This name is used to specify search sorter in a request.
	 */
	public static final String REQUEST_SORTER = "sorter";

	/**
	 * This name is used to specify a cart item UID in a request.
	 */
	public static final String REQUEST_CART_ITEM_ID = "cartItemId";

	/**
	 * This name is used to specify a gift certificate code in a request.
	 */
	public static final String REQUEST_GIFT_CERTIFICATE_CODE = "giftCertificateCode";

	/**
	 * This name is used to specify the locale in a request.
	 */
	public static final String LOCALE_PARAMETER_NAME = "locale";

	/**
	 * This name is used to specify the tag cloud in a request.
	 */
	public static final String TAG_SET = "tagSet";

	/**
	 * This name is used to specify the additional parameters in request.
	 */
	public static final String RENDER_MEDIATOR_GLOBAL_PARAMETER_VALUES = "renderMediatorGlobalParameterValues";

	/**
	 * This name is used to specify the locale in a request.
	 */
	public static final String CURRENCY = "currency";

	/**
	 * This name is used to specify the alternative image selected by the user.
	 */
	public static final String SELECTED_IMAGE = "selectedImage";

	/**
	 * This name is used to specify the saved import jobs in a request.
	 */
	public static final String SAVED_IMPORT_JOBS = "savedImportJobs";

	/**
	 * This name is used to specify the saved batch jobs in a request.
	 */
	public static final String SAVED_BATCH_JOBS = "savedBatchJobs";

	/**
	 * This name is used to specify the import data types in a request.
	 */
	public static final String IMPORT_DATA_TYPE = "importDataType";

	/**
	 * This name is used to specify the import types in a request.
	 */
	public static final String IMPORT_TYPE = "importType";

	/**
	 * This name is used to specify the import preview data in a request.
	 */
	public static final String PREVIEW_DATA = "previewData";

	/**
	 * This name is used to specify the import validation preview data in a request.
	 */
	public static final String VALIDATION_PREVIEW_DATA = "validationPreviewData";

	/**
	 * This name is used to specify the import file in a request.
	 */
	public static final String IMPORT_FILE = "file";

	/**
	 * This name is used to specify the license key file in a request.
	 */
	public static final String LICENSE_KEY_FILE = "license_key_file";

	/**
	 * This name is used to specify an existing address in a request.
	 */
	public static final String REQUEST_EXISTING_ADDRESS = "existingAddress";

	/**
	 * This name is used in a request to specify that a cart item is being updated.
	 */
	public static final String REQUEST_UPDATE = "update";

	/**
	 * This name is used in a request to specify that a cart item is being updated
	 * in the view cart page.
	 */
	public static final String REQUEST_UPDATE_VIEW_CART = "viewCart";

	/**
	 * This name is used in a request to specify that a cart item is being updated
	 * in the billing and review page.
	 */
	public static final String REQUEST_UPDATE_BILLING_REVIEW = "billingAndReview";

	/**
	 * This name is used in a request to specify that a country/subcountries map for regions with shipping service configured.
	 */
	public static final String REQUEST_SHIPPING_COUNTRY_SUBCOUNTRY_MAP = "shippingCountrySubCountryMap";

	/**
	 * This name is used in a request to specify a localized countryCode/name map for regions with shipping service configured.
	 */
	public static final String REQUEST_SHIPPING_COUNTRY_NAME_MAP = "shippingCountryNameMap";

	/**
	 * This name is used in a request to specify that asset id is being download.
	 */
	public static final String REQUEST_ASSEST_ID = "assetID";

	/**
	 * This name is used in a request to specify that ordersku id with digital asset being download.
	 */
	public static final String REQUEST_ORDERSKU_ID = "orderSKUID";

	/**
	 * Request parameter name of country code.
	 */
	public static final String COUNTRY = "country";

	/**
	 * Request parameter name of subCountry code.
	 */
	public static final String SUB_COUNTRY = "subCountry_";
	/**
	 * Request parameter name of when gift certificate balance is zero.
	 */
	public static final String GC_ZERO_BALANCE = "gcZeroBalance";
	/**
	 * Request parameter name of gift certificate currency mismatch.
	 */
	public static final String GC_CURRENCY_MISMATCH = "gcCurrencyMismatch";

	//======================================
	// Command Object Names
	//======================================
	/** Customer command object. */
	public static final String COMMAND_NAME_CUSTOMER = "customer";

	/** CmUser command object. */
	public static final String COMMAND_NAME_CMUSER = "cmUser";

	//======================================
	// Query parameter names
	//======================================
	/** Login status flag used on sign in page. */
	public static final String LOGIN_FAILED = "login_failed";

	/**
	 * The character '+', which is used in query string.
	 */
	public static final char SYMBOL_PLUS = '+';

	/**
	 * The character '&', which is used in query string, note this is not html escaped,
	 * it is just the plain '&' charater.
	 */
	public static final String SYMBOL_AND = "&";

	/**
	 * The character '=', which is used in query string.
	 */
	public static final char SYMBOL_EQUAL = '=';

	//==================================================
	// Key for error message on error.vm
	//==================================================
	/** Error Key. */
	public static final String ERROR_KEY = "errorKey";

	/**
	 * the user id mode.
	 */
    public static final int USE_EMAIL_AS_USER_ID_MODE = 1;

    /**
	 * the user id mode.
	 */
	public static final int GENERATE_UNIQUE_PERMANENT_USER_ID_MODE = 2;

	/**
	 * the user id mode.
	 */
	public static final int INDEPENDANT_EMAIL_AND_USER_ID_MODE = 3;

	//==================================================
	// Key for cmclient csr login
	//==================================================

	/**
	 * the cm client sign in flag.
	 */
	public static final String CREATE_USER = "createUser";

	/**
	 * CMUSER ID.
	 */
	public static final String CMUSER_ID = "username";

	/**
	 * CMUSER PASSWORD.
	 */
	public static final String CMUSER_PASSWORD = "password";

	/**
	 * CUSTOMER UID.
	 */
	public static final String CUSTOMER_UID = "custusername";

	/**
	 * Constant for checkout results.
	 */
	public static final String CHECKOUT_RESULTS = "checkoutResults";


	/**
	 * Session attribute to be removed when CSR wants to open
	 * an internal browser to create customer account.
	 */
	public static final String SECURITY_CONTEXT = HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

	/**
	 * Session attribute to be removed when CSR wants to open
	 * an internal browser to create customer account.
	 */
	public static final String LOCALE_CONTEXT = "org.springframework.web.servlet.i18n.SessionLocaleResolver.LOCALE";

	/**
	 * Request attribute for the locale in the URL.
	 */
	public static final String URL_REQUEST_LOCALE = "urlRequestLocale";

	/**
	 * Request attribute for the destination.
	 */
	public static final String DESTINATION_URL = "destination";

	/**
	 * Name of the reference to the class that is used for getting the URL to change locale.
	 */
	public static final String LOCALE_URL_TOOL = "localeUrlTool";

	/**
	 * Name of the reference to the (request-scoped) class that is used for getting the URL to change locale.
	 */
	public static final String REQUEST_LOCALE_URL_TOOL = "requestLocaleUrlTool";

	/**
	 * A request attribute for storing the requested SEO URL.
	 */
	public static final String SEO_URL_STRING = "seoURL";

	/**
	 * Name of the reference to the class that is used for getting the Auto complete search  price enabled property.
	 */
	public static final String AUTO_COMPLETE_SEARCH_PRICE_ENABLED = "autoCompleteSearchPriceEnabledHelper";

	/**
	 * Name of the reference to the class that is used for getting the Auto complete search thumbnail enabled property.
	 */
	public static final String AUTO_COMPLETE_SEARCH_THUMB_ENABLED = "autoCompleteSearchThumbnailEnabledHelper";

	/**
	 * This setting will turn on/off the search autocomplete for storefronts.
	 */
	public static final String AUTO_COMPLETE_SEARCH_ENABLED = "autoCompleteSearchEnabledHelper";

	/**
	 * This setting defines the maximum number of results to resturn from the search autocomplete for storefronts.
	 */
	public static final String AUTO_COMPLETE_SEARCH_MAX_RESULTS = "autoCompleteSearchNumberOfResultsHelper";

	/**
	 * Seo Enabled.
	 */
	public static final String SEOENABLEDHELPER = "ctxSeoEnabledHelper";

	/**
	 * Render Mediator id.
	 */
	public static final String RENDER_MEDIATOR = "renderMediator";

	/**
	 * Power reviews enabled.
	 */
	public static final String POWERREVIEWSENABLEDHELPER = "pwrEnabledHelper";

	/** Gift Certificate Themes reference. */
	public static final String GC_THEMES = "gcThemes";

	/** Default pagination value. */
	public static final int DEFAULT_PAGINATION_VALUE = 20;

	/**
	 * A constant for holding the content space context data.
	 * This is used as a holder of context specific parameters passed
	 * between the campaign service and the other participants in
	 * the campaign fulfilment.
	 */
	public static final String CONTENT_SPACE_HOLDER = "contentSpaceHolder";

	/** Customer session.*/
	public static final String CUSTOMER_SESSION = "CS";

	/** Error Code.*/
	public static final String ERROR_CODE = "errorCode";
	
	/** 
	 * Used for successful authentication and temporarily store the customer within the 
	 * session to be picked up later in the filter chain. 
	 */
	public static final String AUTHENTICATED_CUSTOMER = "AUTHENTICATED_CUSTOMER";
}
