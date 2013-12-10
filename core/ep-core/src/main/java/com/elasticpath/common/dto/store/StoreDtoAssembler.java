package com.elasticpath.common.dto.store;

import java.util.Currency;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.common.dto.assembler.AbstractDtoAssembler;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.DefaultValueRemovalForbiddenException;
import com.elasticpath.domain.payment.PaymentGateway;
import com.elasticpath.domain.store.CreditCardType;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.StoreState;
import com.elasticpath.domain.store.Warehouse;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.domain.tax.TaxJurisdiction;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.catalog.CatalogService;
import com.elasticpath.service.payment.PaymentGatewayService;
import com.elasticpath.service.store.StoreService;
import com.elasticpath.service.store.WarehouseService;
import com.elasticpath.service.tax.TaxCodeService;
import com.elasticpath.service.tax.TaxJurisdictionService;

/**
 * Assembler for {@link Store} domain object and {@link StoreDTO}.
 */
public class StoreDtoAssembler extends AbstractDtoAssembler<StoreDTO, Store> {

	private BeanFactory beanFactory;

	private CatalogService catalogService;

	private WarehouseService warehouseService;

	private TaxCodeService taxCodeService;

	private TaxJurisdictionService taxJurisdictionService;

	private PaymentGatewayService paymentGatewayService;

	private StoreService storeService;

	@Override
	public Store getDomainInstance() {
		return beanFactory.getBean(ContextIdNames.STORE);
	}

	@Override
	public StoreDTO getDtoInstance() {
		return new StoreDTO();
	}

	/**
	 * Factory method for {@link StoreGlobalizationDTO}.
	 * 
	 * @return a new, uninitialized {@link StoreGlobalizationDTO}.
	 */
	protected StoreGlobalizationDTO storeGlobalizationDtoFactory() {
		return new StoreGlobalizationDTO();
	}

	/**
	 * Factory method for {@link creditCardType}.
	 * 
	 * @return a new, uninitialized {@link creditCardType}.
	 */
	protected CreditCardType creditCardTypeDomainFactory() {
		return beanFactory.getBean(ContextIdNames.CREDIT_CARD_TYPE);
	}

	@Override
	public void assembleDto(final Store source, final StoreDTO target) {
		target.setCode(source.getCode());
		target.setEnabled(source.isEnabled());

		StoreGlobalizationDTO globalization = storeGlobalizationDtoFactory();

		globalization.setContentEncoding(source.getContentEncoding());
		globalization.setCountry(source.getCountry());
		globalization.setDefaultCurrency(source.getDefaultCurrency());
		globalization.setDefaultLocale(source.getDefaultLocale());
		globalization.setSubCountry(source.getSubCountry());
		globalization.setTimeZone(source.getTimeZone());

		target.setGlobalization(globalization);

		target.setUrl(source.getUrl());
		target.setName(source.getName());
		target.setStoreState(source.getStoreState().getValue());
		target.setStoreType(source.getStoreType());

		target.setDescription(source.getDescription());
		target.setCatalogCode(source.getCatalog().getCode());

		target.setDisplayOutOfStock(source.isDisplayOutOfStock());

		target.setEmailSenderName(source.getEmailSenderName());
		target.setEmailSenderAddress(source.getEmailSenderAddress());
		target.setStoreAdminEmail(source.getStoreAdminEmailAddress());
		
		target.setCvv2Enabled(source.isCreditCardCvv2Enabled());
		target.setStoreFullCreditCards(source.isStoreFullCreditCardsEnabled());

		target.getSupportedLocales().addAll(source.getSupportedLocales());
		target.getSupportedCurrencies().addAll(source.getSupportedCurrencies());

		for (Warehouse w : source.getWarehouses()) {
			target.getWarehouses().add(w.getCode());
		}

		for (TaxCode t : source.getTaxCodes()) {
			target.getTaxCodeGuids().add(t.getGuid());
		}

		for (TaxJurisdiction tj : source.getTaxJurisdictions()) {
			target.getTaxJurisdictions().add(tj.getGuid());
		}

		for (PaymentGateway pg : source.getPaymentGateways()) {
			target.getPaymentGateways().add(pg.getName());
		}

		for (CreditCardType cct : source.getCreditCardTypes()) {
			target.getCreditCardTypes().add(cct.getCreditCardType());
		}
	}

	@Override
	public void assembleDomain(final StoreDTO source, final Store target) {
		target.setCode(source.getCode());
		target.setEnabled(source.getEnabled());

		target.setTimeZone(source.getGlobalization().getTimeZone());
		target.setCountry(source.getGlobalization().getCountry());
		target.setSubCountry(source.getGlobalization().getSubCountry());
		target.setContentEncoding(source.getGlobalization().getContentEncoding());
		target.setDefaultCurrency(source.getGlobalization().getDefaultCurrency());
		target.setDefaultLocale(source.getGlobalization().getDefaultLocale());
		target.setUrl(source.getUrl());
		target.setName(source.getName());
		target.setStoreState(StoreState.valueOf(source.getStoreState()));
		target.setStoreType(source.getStoreType());
		target.setDescription(source.getDescription());

		Catalog catalog = catalogService.findByCode(source.getCatalogCode());

		target.setCatalog(catalog);

		target.setDisplayOutOfStock(source.getDisplayOutOfStock());
		target.setEmailSenderName(source.getEmailSenderName());
		target.setEmailSenderAddress(source.getEmailSenderAddress());
		target.setStoreAdminEmailAddress(source.getStoreAdminEmail());

		target.setCreditCardCvv2Enabled(source.getCvv2Enabled());
		target.setStoreFullCreditCardsEnabled(source.getStoreFullCreditCards());

		populateSupportedLocales(source, target);
		populateSupportedCurrencies(source, target);

		populateWarehousesForDomain(source, target);
		populateTaxCodesForDomain(source, target);
		populateTaxJurisdictionsForDomain(source, target);
		populatePaymentGatewaysForDomain(source, target);
		populateCreditCardTypesForDomain(source, target);
	}

	private void populateSupportedCurrencies(final StoreDTO source, final Store target) {

		Set<Currency> currencies = new HashSet<Currency>(source.getSupportedCurrencies());

		// As this is a set, existing currencies are not replaced.
		currencies.addAll(target.getSupportedCurrencies());

		try {
			target.setSupportedCurrencies(currencies);
		} catch (DefaultValueRemovalForbiddenException e) {
			throw new EpSystemException("While setting supported currencies on store " + source.getCode(), e);
		}
	}

	private void populateSupportedLocales(final StoreDTO source, final Store target) {
		Set<Locale> locales = new HashSet<Locale>(source.getSupportedLocales());

		// As this is a set, existing locales are not replaced.
		locales.addAll(target.getSupportedLocales());

		try {
			target.setSupportedLocales(locales);
		} catch (DefaultValueRemovalForbiddenException e) {
			throw new EpSystemException("While setting supported locales on store " + source.getCode(), e);
		}
	}

	private void populatePaymentGatewaysForDomain(final StoreDTO source, final Store target) {
		PaymentGateway existingGateway;

		for (String gatewayName : source.getPaymentGateways()) {

			existingGateway = null;

			try {
				existingGateway = paymentGatewayService.getGatewayByName(gatewayName);
			} catch (EpServiceException e) {
				throw new EpSystemException("Store with the code " + source.getCode() + " references payment gateway named " + gatewayName
						+ " which we had trouble fetching. Maybe run an export/import on payment gateways first.", e);
			}

			for (PaymentGateway gateway : target.getPaymentGateways()) {
				if (gateway.getName().equals(gatewayName)) {
					target.getPaymentGateways().remove(gateway);
					break;
				}
			}

			target.getPaymentGateways().add(existingGateway);
		}
	}

	private void populateCreditCardTypesForDomain(final StoreDTO source, final Store target) {

		Set<CreditCardType> cards = new HashSet<CreditCardType>();

		for (String cardTypeName : source.getCreditCardTypes()) {

			CreditCardType cardType = creditCardTypeDomainFactory();
			cardType.setCreditCardType(cardTypeName);
			cards.add(cardType);
		}

		// As this is a set, existing card types are not replaced.
		cards.addAll(target.getCreditCardTypes());

		target.setCreditCardTypes(cards);
	}

	private void populateTaxJurisdictionsForDomain(final StoreDTO source, final Store target) {

		for (String guid : source.getTaxJurisdictions()) {

			TaxJurisdiction jurisdiction = taxJurisdictionService.findByGuid(guid);

			if (jurisdiction == null) {
				throw new EpSystemException("Store " + source.getCode() + " references tax jurisdiction guid " + guid
						+ " which is not in the target system. Maybe run an export/import on tax jurisdictions first.");
			}

			// Jurisdiction comparison done with .equals which uses guid
			target.getTaxJurisdictions().remove(jurisdiction);
			target.getTaxJurisdictions().add(jurisdiction);
		}
	}

	private void populateTaxCodesForDomain(final StoreDTO source, final Store target) {
		for (String guid : source.getTaxCodeGuids()) {

			TaxCode taxCode = taxCodeService.findByGuid(guid);

			if (taxCode == null) {
				throw new EpSystemException("Store " + source.getCode() + " references tax code guid " + guid
						+ " which is not in the target system. Maybe run an export/import on tax code (and tax jurisdictions) first.");
			}

			// tax code comparison done with .equals which uses guid
			target.getTaxCodes().remove(taxCode);
			target.getTaxCodes().add(taxCode);
		}
	}

	private void populateWarehousesForDomain(final StoreDTO source, final Store target) {

		for (String code : source.getWarehouses()) {

			Warehouse warehouse = warehouseService.findByCode(code);

			if (warehouse == null) {
				throw new EpSystemException("Store " + source.getCode() + " references warehouse " + code
						+ " which is not in the target system. Maybe run an export/import on warehouses first.");
			}

			// Warehouse comparison done with .equals which uses code
			target.getWarehouses().remove(warehouse);
			target.getWarehouses().add(warehouse);
		}
	}

	/**
	 * @param beanFactory the factory used for creating beans.
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	public void setCatalogService(final CatalogService catalogService) {
		this.catalogService = catalogService;
	}

	public void setWarehouseService(final WarehouseService warehouseService) {
		this.warehouseService = warehouseService;
	}

	public void setTaxCodeService(final TaxCodeService taxCodeService) {
		this.taxCodeService = taxCodeService;
	}

	public void setTaxJurisdictionService(final TaxJurisdictionService taxJurisdictionService) {
		this.taxJurisdictionService = taxJurisdictionService;
	}

	public void setPaymentGatewayService(final PaymentGatewayService paymentGatewayService) {
		this.paymentGatewayService = paymentGatewayService;
	}

	/**
	 * @return the storeService
	 */
	public StoreService getStoreService() {
		return storeService;
	}

	/**
	 * @param storeService the storeService to set
	 */
	public void setStoreService(final StoreService storeService) {
		this.storeService = storeService;
	}

}
