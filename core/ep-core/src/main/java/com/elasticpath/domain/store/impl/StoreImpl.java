package com.elasticpath.domain.store.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicReference;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang.ObjectUtils;
import org.apache.openjpa.persistence.DataCache;
import org.apache.openjpa.persistence.ElementDependent;
import org.apache.openjpa.persistence.Externalizer;
import org.apache.openjpa.persistence.Factory;
import org.apache.openjpa.persistence.FetchAttribute;
import org.apache.openjpa.persistence.FetchGroup;
import org.apache.openjpa.persistence.FetchGroups;
import org.apache.openjpa.persistence.Persistent;
import org.apache.openjpa.persistence.PersistentCollection;
import org.apache.openjpa.persistence.jdbc.ElementForeignKey;
import org.apache.openjpa.persistence.jdbc.ElementJoinColumn;

import com.elasticpath.base.Initializable;
import com.elasticpath.commons.constants.GlobalConstants;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.DefaultValueRemovalForbiddenException;
import com.elasticpath.domain.catalog.impl.CatalogImpl;
import com.elasticpath.domain.misc.SupportedCurrency;
import com.elasticpath.domain.misc.SupportedLocale;
import com.elasticpath.domain.payment.PaymentGateway;
import com.elasticpath.domain.payment.impl.PaymentGatewayImpl;
import com.elasticpath.domain.store.CreditCardType;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.StoreState;
import com.elasticpath.domain.store.StoreType;
import com.elasticpath.domain.store.Warehouse;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.domain.tax.TaxJurisdiction;
import com.elasticpath.domain.tax.impl.TaxCodeImpl;
import com.elasticpath.domain.tax.impl.TaxJurisdictionImpl;
import com.elasticpath.persistence.api.AbstractPersistableImpl;
import com.elasticpath.persistence.support.FetchGroupConstants;
import com.elasticpath.plugin.payment.PaymentType;

/**
 * Implementation of Store.java that takes into account special persistence-layer restrictions.
 * Since we cannot specify or determine the order in which JPA will load fields from the database
 * it is necessary to specify internal protected methods for JPA to access instance variables without
 * being hampered by consistency logic that may access fields that are not yet loaded.
 */
@SuppressWarnings({ "PMD.AvoidDuplicateLiterals", "PMD.TooManyFields", "PMD.ExcessiveImports" })
@Entity
@Table(name = StoreImpl.TABLE_NAME, uniqueConstraints = @UniqueConstraint(columnNames = { "NAME", "STORECODE" }))
@DataCache(enabled = true)
@FetchGroups({
		@FetchGroup(name = FetchGroupConstants.PRODUCT_INDEX, attributes = {
				@FetchAttribute(name = "warehouses"),
				@FetchAttribute(name = "displayOutOfStock"),
				@FetchAttribute(name = "code"),
				@FetchAttribute(name = "storeState"),
				@FetchAttribute(name = "name") }),
		@FetchGroup(name = FetchGroupConstants.PROMOTION_INDEX, attributes = {
				@FetchAttribute(name = "code"),
				@FetchAttribute(name = "storeState"),
				@FetchAttribute(name = "name") }),
		@FetchGroup(name = FetchGroupConstants.ORDER_INDEX, attributes = {
				@FetchAttribute(name = "code"),
				@FetchAttribute(name = "storeState"),
				@FetchAttribute(name = "name") }),
		@FetchGroup(name = FetchGroupConstants.CATALOG, attributes = {
				@FetchAttribute(name = "catalog"),
				@FetchAttribute(name = "code"),
				@FetchAttribute(name = "storeState"),
				@FetchAttribute(name = "name") }),
		@FetchGroup(name = FetchGroupConstants.STORE_SHARING, attributes = {
				@FetchAttribute(name = "associatedStoreUids"),
				@FetchAttribute(name = "code"),
				@FetchAttribute(name = "storeState"),
				@FetchAttribute(name = "name"),
				@FetchAttribute(name = "supportedLocalesInternal"),
				@FetchAttribute(name = "supportedCurrenciesInternal") }),
		@FetchGroup(name = FetchGroupConstants.SHOPPING_ITEM_CHILD_ITEMS, attributes = {
				@FetchAttribute(name = "associatedStoreUids"),
				@FetchAttribute(name = "code"),
				@FetchAttribute(name = "storeState"),
				@FetchAttribute(name = "name"),
				@FetchAttribute(name = "supportedLocalesInternal"),
				@FetchAttribute(name = "supportedCurrenciesInternal") }),
		@FetchGroup(name = FetchGroupConstants.STORE_FOR_EDIT, attributes = {
				@FetchAttribute(name = "taxJurisdictions"),
				@FetchAttribute(name = "taxCodes"),
				@FetchAttribute(name = "associatedStoreUids"),
				@FetchAttribute(name = "code"),
				@FetchAttribute(name = "storeState"),
				@FetchAttribute(name = "name") },
				fetchGroups = { FetchGroupConstants.DEFAULT	}),
		@FetchGroup(name = FetchGroupConstants.ORDER_STORE_AND_WAREHOUSE, attributes = {
				@FetchAttribute(name = "warehouses"),
				@FetchAttribute(name = "code"),
				@FetchAttribute(name = "name") }),
		@FetchGroup(name = FetchGroupConstants.ORDER_SEARCH, attributes = {
				@FetchAttribute(name = "code"),
				@FetchAttribute(name = "storeState"),
				@FetchAttribute(name = "name") }),
		@FetchGroup(name = FetchGroupConstants.ORDER_LIST_BASIC, attributes = {
				@FetchAttribute(name = "code"),
				@FetchAttribute(name = "storeState"),
				@FetchAttribute(name = "name"),
				@FetchAttribute(name = "url") })
})
public class StoreImpl extends AbstractPersistableImpl implements Store, Initializable {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	private String contentEncoding;

	private String country;

	private boolean creditCardCvv2Enabled;

	private boolean storeFullCreditCardsEnabled;

	private Set<CreditCardType> creditCardTypes = new HashSet<CreditCardType>();

	private Currency defaultCurrency;

	private Locale defaultLocale;

	private String description;

	private boolean displayOutOfStock;

	private boolean enabled;

	private String name;

	private StoreType storeType;

	private String subCountry;

	private TimeZone timeZone;

	private Set<TaxCode> taxCodes = new HashSet<TaxCode>();

	private Set<TaxJurisdiction> taxJurisdictions = new HashSet<TaxJurisdiction>();

	private String url;

	private List<Warehouse> warehouses = new ArrayList<Warehouse>();

	private String code;

	private Set<PaymentGateway> paymentGateways = new HashSet<PaymentGateway>();

	private Catalog catalog;

	private final AtomicReference<Map<PaymentType, PaymentGateway>> paymentGatewayMap = new AtomicReference<Map<PaymentType, PaymentGateway>>();

	private long uidPk;

	private String emailSenderName;

	private String emailSenderAddress;

	private String storeAdminEmailAddress;

	private Collection<Long> associatedStoreUids = new HashSet<Long>();

	private Set<SupportedCurrency> supportedStoreCurrencies = new HashSet<SupportedCurrency>();

	private Set<SupportedLocale> supportedStoreLocales = new HashSet<SupportedLocale>();

	private StoreState storeState;

	/** Defines 20 as a constant to use for maximum string lengths. */
	private static final int LENGTH_20 = 20;

	/** Maximum string length of currency. */
	private static final int DEFAULT_CURRENCY_LENGTH = 3;

	/** Maximum string length of country and sub-country. */
	private static final int COUNTRY_LENGTH = 200;

	/** Maximum string length of a time zone. */
	private static final int TIME_ZONE_LENGTH = 50;

	/** Maximum string length for a store code. */
	private static final int STORE_CODE_LENGTH = 64;

	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TSTORE";

	@Override
	public void initialize() {
		setStoreState(StoreState.UNDER_CONSTRUCTION);
	}

	/**
	 * Gets the content encoding for the <code>Store</code>.
	 *
	 * @return the content encoding for the <code>Store</code>
	 */
	@Override
	@Column(name = "CONTENT_ENCODING", length = LENGTH_20)
	public String getContentEncoding() {
		return contentEncoding;
	}

	/**
	 * Sets the content encoding for the <code>Store</code>.
	 *
	 * @param contentEncoding the content encoding for the <code>Store</code>
	 */
	@Override
	public void setContentEncoding(final String contentEncoding) {
		this.contentEncoding = contentEncoding;
	}

	/**
	 * Gets the country of this <code>Store</code>.
	 *
	 * @return the country of this <code>Store</code>
	 */
	@Override
	@Basic(optional = false)
	@Column(name = "COUNTRY", length = COUNTRY_LENGTH)
	public String getCountry() {
		return country;
	}

	/**
	 * Sets the country of this <code>Store</code>.
	 *
	 * @param country the country of this <code>Store</code>
	 */
	@Override
	public void setCountry(final String country) {
		this.country = country;
	}

	/**
	 * Gets whether the <code>Store</code> is CVV2 verification is enabled.
	 *
	 * @return boolean designating whether CVV2 verification is enabled
	 */
	@Override
	@Basic(optional = false)
	@Column(name = "CREDIT_CARD_CVV2_ENABLED")
	public boolean isCreditCardCvv2Enabled() {
		return creditCardCvv2Enabled;
	}

	/**
	 * Sets whether the <code>Store</code> is CVV2 verification is enabled.
	 *
	 * @param creditCardCvv2Enabled boolean designating whether CVV2 verification is enabled
	 */
	@Override
	public void setCreditCardCvv2Enabled(final boolean creditCardCvv2Enabled) {
		this.creditCardCvv2Enabled = creditCardCvv2Enabled;
	}

	/**
	 * Returns <code>true</code> if this {@link Store} saves full/unmasked
	 * credit card numbers on orders placed; <code>false</code> if it saves
	 * masked credit card numbers.
	 *
	 * @return <code>true</code> if this {@link Store} saves full/unmasked
	 * credit card numbers on orders placed; <code>false</code> if it saves
	 * masked credit card numbers.d
	 */
	@Override
	@Basic(optional = false)
	@Column(name = "STORE_FULL_CREDIT_CARDS")
	public boolean isStoreFullCreditCardsEnabled() {
		return storeFullCreditCardsEnabled;
	}

	/**
	 * Sets whether this {@link Store} saves full/unmasked credit card numbers
	 * on orders placed.
	 *
	 * @param storeFullCreditCardsEnabled
	 *            <code>true</code> if this {@link Store} saves full/unmasked
	 *            credit card numbers on orders placed; <code>false</code> if
	 *            it saves masked credit card numbers.
	 */
	@Override
	public void setStoreFullCreditCardsEnabled(final boolean storeFullCreditCardsEnabled) {
		this.storeFullCreditCardsEnabled = storeFullCreditCardsEnabled;
	}

	/**
	 * Gets the default currency for this <code>Store</code>,
	 * from the persistence layer.
	 *
	 * @return the default currency for this <code>Store</code>
	 */
	@Persistent
	@Column(name = "DEFAULT_CURRENCY", length = DEFAULT_CURRENCY_LENGTH)
	@Externalizer("getCurrencyCode")
	@Factory("com.elasticpath.commons.util.impl.ConverterUtils.currencyFromString")
	protected Currency getDefaultCurrencyInternal() {
		return defaultCurrency;
	}

	/**
	 * Gets the default currency for this <code>Store</code>.
	 *
	 * @return the default currency for this <code>Store</code>
	 */
	@Override
	@Transient
	public Currency getDefaultCurrency() {
		return this.getDefaultCurrencyInternal();
	}

	/**
	 * Sets the default currency for this <code>Store</code>, for the
	 * persistence layer.
	 *
	 * @param defaultCurrency the default currency for this <code>Store</code>
	 */
	protected void setDefaultCurrencyInternal(final Currency defaultCurrency) {
		this.defaultCurrency = defaultCurrency;
	}

	/**
	 * Sets the default currency for this <code>Store</code>.
	 *
	 * @param defaultCurrency the default currency for this <code>Store</code>
	 */
	@Override
	public void setDefaultCurrency(final Currency defaultCurrency) {
		this.setDefaultCurrencyInternal(defaultCurrency);
	}

	/**
	 * Gets the default locale for this <code>Store</code>,
	 * for JPA only.
	 *
	 * @return the default locale for this <code>Store</code>
	 */
	@Basic
	@Externalizer("toString")
	@Factory("org.apache.commons.lang.LocaleUtils.toLocale")
	@Column(name = "DEFAULT_LOCALE", length = LENGTH_20)
	protected Locale getDefaultLocaleInternal() {
		return defaultLocale;
	}

	/**
	 * Gets the default locale for this <code>Store</code>.
	 * Calls getDefaultLocaleInternal()
	 *
	 * @return the default locale for this <code>Store</code>
	 */
	@Override
	@Transient
	public Locale getDefaultLocale() {
		return this.getDefaultLocaleInternal();
	}

	/**
	 * Sets the default locale for this <code>Store</code>,
	 * for JPA only.
	 *
	 * @param defaultLocale the default locale for this <code>Store</code>
	 */
	protected void setDefaultLocaleInternal(final Locale defaultLocale) {
		this.defaultLocale = defaultLocale;
	}

	/**
	 * Sets the default locale for this <code>Store</code>.
	 * Calls setDefaultLocaleInternal.
	 *
	 * @param defaultLocale the default locale for this <code>Store</code>
	 */
	@Override
	public void setDefaultLocale(final Locale defaultLocale) {
		this.setDefaultLocaleInternal(defaultLocale);
	}

	/**
	 * Gets the description of the <code>Store</code>.
	 *
	 * @return the description of the <code>Store</code>
	 */
	@Override
	@Lob
	@Column(name = "DESCRIPTION", length = GlobalConstants.LONG_TEXT_MAX_LENGTH)
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description of the <code>Store</code>.
	 *
	 * @param description the description of the <code>Store</code>
	 */
	@Override
	public void setDescription(final String description) {
		this.description = description;
	}

	/**
	 * Gets whether the <code>Store</code> should display out of stock items. If set to false, out of stock items will not be shown (will be
	 * "hidden").
	 *
	 * @return boolean designating whether out of stock items are shown
	 */
	@Override
	@Basic(optional = false)
	@Column(name = "DISPLAY_OUT_OF_STOCK")
	public boolean isDisplayOutOfStock() {
		return displayOutOfStock;
	}

	/**
	 * Sets whether the <code>Store</code> should display out of stock items. If set to false, out of stock items will not be shown (will be
	 * "hidden").
	 *
	 * @param displayOutOfStock boolean designating whether out of stock items are shown
	 */
	@Override
	public void setDisplayOutOfStock(final boolean displayOutOfStock) {
		this.displayOutOfStock = displayOutOfStock;
	}

	/**
	 * Gets whether this <code>Store</code> is currently enabled.
	 *
	 * @return boolean designating that a store is live (enabled)
	 */
	@Override
	@Basic(optional = false)
	@Column(name = "ENABLED")
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * Sets whether this <code>Store</code> is currently enabled.
	 *
	 * @param enabled boolean designating that a store is live (enabled)
	 */
	@Override
	public void setEnabled(final boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * Gets the name of the <code>Store</code>.
	 *
	 * @return the name of the <code>Store</code>
	 */
	@Override
	@Basic(optional = false)
	@Column(name = "NAME", unique = true)
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the <code>Store</code>.
	 *
	 * @param name the name of the <code>Store</code>
	 */
	@Override
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * Gets the <code>StoreType</code> of the <code>Store</code>.
	 *
	 * @return the <code>StoreType</code> of the <code>Store</code>
	 */
	@Override
	@Basic(optional = false)
	@Column(name = "STORE_TYPE")
	@Enumerated(EnumType.STRING)
	public StoreType getStoreType() {
		return storeType;
	}

	/**
	 * Sets the <code>StoreType</code> of the <code>Store</code>.
	 *
	 * @param storeType the <code>StoreType</code> of the <code>Store</code>
	 */
	@Override
	public void setStoreType(final StoreType storeType) {
		this.storeType = storeType;
	}

	/**
	 * Gets the sub-country of this <code>Store</code>.
	 *
	 * @return the sub-country of this <code>Store</code>
	 */
	@Override
	@Basic
	@Column(name = "SUB_COUNTRY", length = COUNTRY_LENGTH)
	public String getSubCountry() {
		return subCountry;
	}

	/**
	 * Sets the sub-country of this <code>Store</code>.
	 *
	 * @param subCountry the sub-country of this <code>Store</code>
	 */
	@Override
	public void setSubCountry(final String subCountry) {
		this.subCountry = subCountry;
	}

	/**
	 * Gets the url of the <code>Store</code>.
	 *
	 * @return the url of the <code>Store</code>
	 */
	@Override
	@Basic
	@Column(name = "URL")
	public String getUrl() {
		return url;
	}

	/**
	 * Sets the url of the <code>Store</code>.
	 *
	 * @param url the url of the <code>Store</code>
	 */
	@Override
	public void setUrl(final String url) {
		this.url = url;
	}

	/**
	 * Gets a list of <code>Warehouse</code>s for associated with the <code>Store</code>.
	 *
	 * @return the <code>Warehouse</code> for the <code>Store</code>, if one exists, otherwise null
	 */
	@Override
	@ManyToMany(targetEntity = WarehouseImpl.class, fetch = FetchType.EAGER)
	// might want this LAZY in the future
	@JoinTable(name = "TSTOREWAREHOUSE", joinColumns = @JoinColumn(name = "STORE_UID"), inverseJoinColumns = @JoinColumn(name = "WAREHOUSE_UID"))
	public List<Warehouse> getWarehouses() {
		return warehouses;
	}

	/**
	 * Gets the first <code>Warehouse</code> for associated with the <code>Store</code>. Note: current implementation only allows one warehouse
	 * to be associated with a store, though data model has one-to-many relationship between store and warehouse.
	 *
	 * @return the first <code>Warehouse</code> for the <code>Store</code>, if one exists, otherwise null
	 */
	@Override
	@Transient
	public Warehouse getWarehouse() {
		if (!getWarehouses().isEmpty()) {
			return getWarehouses().get(0);
		}
		return null;
	}

	/**
	 * Sets the list of <code>Warehouse</code>s for the <code>Store</code>.
	 *
	 * @param warehouse the <code>Warehouse</code> for the <code>Store</code>
	 */
	@Override
	public void setWarehouses(final List<Warehouse> warehouse) {
		this.warehouses = warehouse;
	}

	/**
	 * Gets the set of <code>CreditCardType</code>s for this <code>Store</code>.
	 *
	 * @return the set of <code>CreditCardType</code>s for this <code>Store</code>
	 */
	@Override
	@OneToMany(targetEntity = CreditCardTypeImpl.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@ElementJoinColumn(name = "STORE_UID", nullable = false)
	@ElementForeignKey
	@ElementDependent
	public Set<CreditCardType> getCreditCardTypes() {
		return creditCardTypes;
	}

	/**
	 * Sets the set of <code>CreditCardType</code>s for this <code>Store</code>.
	 *
	 * @param creditCardTypes the set of <code>CreditCardType</code>s for this <code>Store</code>
	 */
	@Override
	public void setCreditCardTypes(final Set<CreditCardType> creditCardTypes) {
		this.creditCardTypes = creditCardTypes;
	}

	/**
	 * Gets a set of tax codes for this <code>Store</code>.
	 *
	 * @return the set of tax codes for this <code>Store</code>
	 */
	@Override
	@ManyToMany(targetEntity = TaxCodeImpl.class, fetch = FetchType.EAGER)
	@JoinTable(name = "TSTORETAXCODE", joinColumns = @JoinColumn(name = "STORE_UID"), inverseJoinColumns = @JoinColumn(name = "TAXCODE_UID"))
	public Set<TaxCode> getTaxCodes() {
		return taxCodes;
	}

	/**
	 * Sets a set of tax codes for this <code>Store</code>.
	 *
	 * @param taxCodes the set of tax codes for this <code>Store</code>
	 */
	@Override
	public void setTaxCodes(final Set<TaxCode> taxCodes) {
		this.taxCodes = taxCodes;
	}

	/**
	 * Gets a set of tax jurisdictions for this <code>Store</code>.
	 *
	 * @return the tax jurisdictions for this <code>Store</code>
	 */
	@Override
	@ManyToMany(targetEntity = TaxJurisdictionImpl.class, fetch = FetchType.LAZY)
							//Set to lazy as in large store, there would a large amount of taxJuridiction --> taxRegion
	@JoinTable(name = "TSTORETAXJURISDICTION", joinColumns = @JoinColumn(name = "STORE_UID"),
			inverseJoinColumns = @JoinColumn(name = "TAXJURISDICTION_UID"))
	public Set<TaxJurisdiction> getTaxJurisdictions() {
		return taxJurisdictions;
	}

	/**
	 * Sets a set of tax jurisdictions for this <code>Store</code>.
	 *
	 * @param taxJurisdictions the tax jurisdictions for this <code>Store</code>
	 */
	@Override
	public void setTaxJurisdictions(final Set<TaxJurisdiction> taxJurisdictions) {
		this.taxJurisdictions = taxJurisdictions;
	}

	/**
	 * Gets time time zone of the <code>Store</code>.
	 *
	 * @return the time zone of the <code>Store</code>
	 */
	@Override
	@Persistent(optional = false)
	@Externalizer("getID")
	@Factory("TimeZone.getTimeZone")
	@Column(name = "TIMEZONE", length = TIME_ZONE_LENGTH)
	public TimeZone getTimeZone() {
		return timeZone;
	}

	/**
	 * Sets time time zone of the <code>Store</code>.
	 *
	 * @param timeZone the time zone of the <code>Store</code>
	 */
	@Override
	public void setTimeZone(final TimeZone timeZone) {
		this.timeZone = timeZone;
	}

	/**
	 * Gets a set of available payment gateways for this <code>Store</code>.
	 *
	 * @return a set of available payment gateways for this <code>Store</code>.
	 */
	@Override
	@ManyToMany(targetEntity = PaymentGatewayImpl.class, fetch = FetchType.EAGER, cascade = { CascadeType.REFRESH, CascadeType.MERGE })
	@JoinTable(name = "TSTOREPAYMENTGATEWAY", joinColumns = @JoinColumn(name = "STORE_UID"), inverseJoinColumns = @JoinColumn(name = "GATEWAY_UID"))
	public Set<PaymentGateway> getPaymentGateways() {
		return paymentGateways;
	}

	/**
	 * Sets a set of available payment gateways for this <code>Store</code>.
	 *
	 * @param paymentGateways a set of available payment gateways for this <code>Store</code>.
	 */
	@Override
	public void setPaymentGateways(final Set<PaymentGateway> paymentGateways) {
		this.paymentGateways = paymentGateways;
		// set the corresponding Map to null so it will be regenerated next time the getter is
		// called
		paymentGatewayMap.set(null);
	}

	/**
	 * Gets a map of available payment gateways for this <code>Store</code>.  This field is lazy-loaded, and can be
	 * accessed from a cached instance, so it must be thread safe.
	 *
	 * Note, however, that there still is a race condition with the {@link #setPaymentGateways(java.util.Set)} method, however
	 * we should not ever hit that condition if we have a shared instance of a Store, since shared instances should always
	 * be read-only in practice.
	 *
	 * @return a map of available payment gateways for this <code>Store</code>.
	 */
	@Override
	@Transient
	public Map<PaymentType, PaymentGateway> getPaymentGatewayMap() {
		Map<PaymentType, PaymentGateway> gateways = paymentGatewayMap.get();
		if (gateways == null) {
			gateways = new EnumMap<PaymentType, PaymentGateway>(PaymentType.class);

			for (PaymentGateway paymentGateway : getPaymentGateways()) {
				gateways.put(paymentGateway.getPaymentType(), paymentGateway);
			}

			gateways = Collections.unmodifiableMap(gateways);
			paymentGatewayMap.set(gateways);
		}
		return gateways;
	}

	/**
	 * Gets the unique code associated with the <code>Store</code>.
	 *
	 * @return the unique code associated with the <code>Store</code>
	 */
	@Override
	@Basic(optional = false)
	@Column(name = "STORECODE", length = STORE_CODE_LENGTH, unique = true)
	public String getCode() {
		return code;
	}

	/**
	 * Sets the unique code associated with the <code>Store</code>.
	 *
	 * @param code the unique code associated with the <code>Store</code>
	 */
	@Override
	public void setCode(final String code) {
		this.code = code;
	}

	/**
	 * Get the catalog used by this store.
	 *
	 * @return the catalog
	 */
	@Override
	@ManyToOne(optional = true, targetEntity = CatalogImpl.class, cascade = { CascadeType.REFRESH, CascadeType.MERGE })
	@JoinColumn(name = "CATALOG_UID")
	public Catalog getCatalog() {
		return catalog;
	}

	/**
	 * Set the catalog used by this store.
	 *
	 * @param catalog the catalog to set
	 */
	@Override
	public void setCatalog(final Catalog catalog) {
		this.catalog = catalog;
	}

	/**
	 * Gets the unique identifier for this domain model object.
	 *
	 * @return the unique identifier.
	 */
	@Override
	@Id
	@Column(name = "UIDPK")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
	@TableGenerator(name = TABLE_NAME, table = "JPA_GENERATED_KEYS", pkColumnName = "ID", valueColumnName = "LAST_VALUE", pkColumnValue = TABLE_NAME)
	public long getUidPk() {
		return this.uidPk;
	}

	/**
	 * Sets the unique identifier for this domain model object.
	 *
	 * @param uidPk the new unique identifier.
	 */
	@Override
	public void setUidPk(final long uidPk) {
		this.uidPk = uidPk;
	}

	/**
	 * Gets the friendly name displayed when sending an email from this <code>Store</code>.
	 *
	 * @return the friendly name displayed when sending an email from this <code>Store</code>
	 */
	@Override
	@Basic
	@Column(name = "EMAIL_SENDER_NAME", length = GlobalConstants.SHORT_TEXT_MAX_LENGTH)
	public String getEmailSenderName() {
		return emailSenderName;
	}

	/**
	 * Sets the friendly name displayed when sending an email from this <code>Store</code>.
	 *
	 * @param emailSenderName the friendly name displayed when sending an email from this <code>Store</code>
	 */
	@Override
	public void setEmailSenderName(final String emailSenderName) {
		this.emailSenderName = emailSenderName;
	}

	/**
	 * Gets the physical email address displayed when sending an email from this <code>Store</code>.
	 *
	 * @return Gets the physical email address displayed when sending an email from this <code>Store</code>
	 */
	@Override
	@Basic
	@Column(name = "EMAIL_SENDER_ADDRESS", length = GlobalConstants.SHORT_TEXT_MAX_LENGTH)
	public String getEmailSenderAddress() {
		return emailSenderAddress;
	}

	/**
	 * Sets the physical email address displayed when sending an email from this <code>Store</code>.
	 *
	 * @param emailSenderAddress Gets the physical email address displayed when sending an email from this <code>Store</code>
	 */
	@Override
	public void setEmailSenderAddress(final String emailSenderAddress) {
		this.emailSenderAddress = emailSenderAddress;
	}

	@Override
	@Basic
	@Column(name = "STORE_ADMIN_EMAIL", length = GlobalConstants.SHORT_TEXT_MAX_LENGTH)
	public String getStoreAdminEmailAddress() {
		return storeAdminEmailAddress;
	}

	@Override
	public void setStoreAdminEmailAddress(final String emailAddress) {
		this.storeAdminEmailAddress = emailAddress;
	}

	/**
	 * Compares this StoreCode with the StoreCode from the given Store.
	 *
	 * @param other the Store with which to compare this one
	 * @return negative integer, zero, or a positive integer as this store's code is less than, equal to, or greater than the given store's code
	 */
	@Override
	public int compareTo(final Store other) {
		// codes should always be defined
		return getCode().compareTo(other.getCode());
	}

	@Override
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}

		if (!(other instanceof StoreImpl)) {
			return false;
		}

		StoreImpl store = (StoreImpl) other;
		return ObjectUtils.equals(code, store.code);
	}

	@Override
	public int hashCode() {
		return ObjectUtils.hashCode(code);
	}

    /**
     * String representation of this object.
     *
     * @return the string representation
     */
    @Override
    public String toString() {
    	 final StringBuffer sbf = new StringBuffer();
         sbf.append("==> STORE FIELDS <==\n==> Code:").append(this.getCode());
         sbf.append("\n==> Tax Jurisdictions:").append(this.getTaxJurisdictions());
         sbf.append("\n==> Default Currency:").append(this.getDefaultCurrency());
         return sbf.toString();
    }

	/**
	 * Get the collection of store uids associated to this store.
	 * @return collection of uids
	 */
	@Override
	@PersistentCollection (elementCascade = {CascadeType.ALL }, fetch = FetchType.EAGER)
	@JoinTable(name = "TSTOREASSOCIATION", joinColumns = { @JoinColumn(name = "STORE_UID") },
		inverseJoinColumns = @JoinColumn(name = "ASSOCIATED_STORE_UID", nullable = false))
	public Collection<Long> getAssociatedStoreUids() {
		return associatedStoreUids;
	}

	/**
	 * Set the collection of associated store uids.
	 * @param associatedStoreUids the collection of uids.
	 */
	protected void setAssociatedStoreUids(final Collection<Long> associatedStoreUids) {
		this.associatedStoreUids = associatedStoreUids;
	}

	/**
	 * Get the set <code>StoreCurrency</code>s representing those <code>Currency</code>s that are supported by the
	 * Store. Necessary because the persistence layer cannot persist java.util.Currency objects directly.
	 *
	 * @return the supported currencies
	 */
	@OneToMany(targetEntity = StoreCurrencyImpl.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@ElementJoinColumn(name = "STORE_UID", nullable = false)
	@ElementForeignKey(name = "TSTORESUPPORTEDCURRENCY_IBFK_1")
	@ElementDependent
	protected Set<SupportedCurrency> getSupportedCurrenciesInternal() {
		return supportedStoreCurrencies;
	}

	/**
	 * Set the supported currencies.
	 *
	 * @param supportedCurrencies the supportedCurrencies to set
	 */
	protected void setSupportedCurrenciesInternal(final Set<SupportedCurrency> supportedCurrencies) {
		this.supportedStoreCurrencies = supportedCurrencies;
	}

	/**
	 * Gets the unmodifiable collection of this store's supported currencies.
	 *
	 * @return unmodifiable collection of this store's supported currencies, which is empty if no currencies are supported
	 */
	@Override
	@Transient
	public Set<Currency> getSupportedCurrencies() {
		final Set<Currency> supportedCurrencies = new HashSet<Currency>();
		for (SupportedCurrency sCurrency : getSupportedCurrenciesInternal()) {
			supportedCurrencies.add(sCurrency.getCurrency());
		}
		return Collections.unmodifiableSet(supportedCurrencies);
	}

	/**
	 * Set the collection of <code>Currency</code>s that are supported by this Store.
	 *
	 * @param supportedCurrencies the supportedCurrencies to set
	 * @throws DefaultValueRemovalForbiddenException if the given Currencies do not contain the default currency, or if a Store
	 *             that is using this Store has a default Currency that is missing from the given collection
	 */
	@Override
	public void setSupportedCurrencies(final Collection<Currency> supportedCurrencies) throws DefaultValueRemovalForbiddenException {
		if ((supportedCurrencies.contains(this.getDefaultCurrency()) || this.getDefaultCurrency() == null)) {
			final Set<SupportedCurrency> supportedStoreCurrencies = new HashSet<SupportedCurrency>();
			for (Currency currency : supportedCurrencies) {
				SupportedCurrency cCurrency = createSupportedCurrencyInstance();
				cCurrency.setCurrency(currency);
				supportedStoreCurrencies.add(cCurrency);
			}
			setSupportedCurrenciesInternal(supportedStoreCurrencies);
		} else {
			throw new DefaultValueRemovalForbiddenException(
					"Cannot remove default: + " + this.getDefaultCurrency() + " from collection of supported currencies");
		}
	}

	/**
	 * Factory method which creates a new instance of a SupportedCurrency.  Extension projects should override this
	 * method if they need to supply a new implementation class for StoreCurrencyImpl.
	 *
	 * @return a SupportedCurrency instance.
	 */
	protected SupportedCurrency createSupportedCurrencyInstance() {
		return new StoreCurrencyImpl();
	}

	/**
	 * Get the set of supported <code>Storelocale</code>s. Necessary because the persistence layer cannot persist
	 * java.util.Locale objects directly.
	 *
	 * @return the supported StoreLocales
	 */
	@OneToMany(targetEntity = StoreLocaleImpl.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@ElementJoinColumn(name = "STORE_UID", nullable = false)
	@ElementForeignKey(name = "TSTORESUPPORTEDLOCALE_IBFK_1")
	@ElementDependent
	protected Set<SupportedLocale> getSupportedLocalesInternal() {
		return supportedStoreLocales;
	}

	/**
	 * Set the supported locales in the form of SupportedLocale objects, which can be persisted.
	 *
	 * @param supportedLocales the supportedLocales to set
	 */
	protected void setSupportedLocalesInternal(final Set<SupportedLocale> supportedLocales) {
		this.supportedStoreLocales = supportedLocales;
	}

	/**
	 * Gets the unmodifiable collection of this store's supported locales.
	 *
	 * @return unmodifiable collection of this store's supported locales, which is empty if no locales are supported
	 */
	@Override
	@Transient
	public Set<Locale> getSupportedLocales() {
		final Set<Locale> supportedLocales = new HashSet<Locale>();
		for (SupportedLocale sLocale : getSupportedLocalesInternal()) {
			supportedLocales.add(sLocale.getLocale());
		}
		return Collections.unmodifiableSet(supportedLocales);
	}

	/**
	 * Set the collection of locales that are supported by this store.
	 *
	 * @param supportedLocales the supportedLocales to set
	 * @throws DefaultValueRemovalForbiddenException if the new locales do not contain the default locale
	 */
	@Override
	public void setSupportedLocales(final Collection<Locale> supportedLocales) throws DefaultValueRemovalForbiddenException {
		if ((supportedLocales.contains(this.getDefaultLocale()) || this.getDefaultLocale() == null)) {
			Set<SupportedLocale> supportedStoreLocales = new HashSet<SupportedLocale>();
			for (Locale locale : supportedLocales) {
				SupportedLocale sLocale = createSupportedLocaleInstance();
				sLocale.setLocale(locale);
				supportedStoreLocales.add(sLocale);
			}
			setSupportedLocalesInternal(supportedStoreLocales);
		} else {
			throw new DefaultValueRemovalForbiddenException(
					"Cannot remove default: + " + this.getDefaultLocale() + " from collection of supported locales");
		}
	}

	/**
	 * Factory method which creates a new instance of a SupportedLocale.  Extension projects should override this
	 * method if they need to supply a new implementation class for StoreLocaleImpl.
	 *
	 * @return a SupportedLocale instance.
	 */
	protected SupportedLocale createSupportedLocaleInstance() {
		return new StoreLocaleImpl();
	}

	/**
	 * Gets the value of storeState.
	 *
	 * @return the value of storeState.
	 */
	@Override
	@Persistent(optional = false)
	@Column(name = "STORE_STATE")
	@Externalizer("getValue")
	@Factory("valueOf")
	public StoreState getStoreState() {
		return storeState;
	}

	/**
	 * Sets the value of storeState.
	 *
	 * @param storeState the storeState to set
	 */
	@Override
	public void setStoreState(final StoreState storeState) {
		this.storeState = storeState;
	}

	@Override
	public boolean supportsPaymentType(final PaymentType paymentType) {
		return getPaymentGatewayMap().containsKey(paymentType); 
	}
}
