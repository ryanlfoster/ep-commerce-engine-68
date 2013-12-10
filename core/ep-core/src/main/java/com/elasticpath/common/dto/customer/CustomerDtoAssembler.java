package com.elasticpath.common.dto.customer;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.common.dto.AddressDTO;
import com.elasticpath.common.dto.assembler.AbstractDtoAssembler;
import com.elasticpath.common.dto.assembler.customer.BuiltinFilters;
import com.elasticpath.common.dto.assembler.customer.CreditCardFilter;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.exception.EpInvalidValueBindException;
import com.elasticpath.commons.util.security.CreditCardEncrypter;
import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerAddress;
import com.elasticpath.domain.customer.CustomerCreditCard;
import com.elasticpath.domain.customer.CustomerGroup;
import com.elasticpath.service.customer.CustomerGroupService;

/**
 * <b>Does not copy credit card numbers.</b> <br>
 * This assembler is for Customers, but delegates to {@code CreditCardFilter} to assemble the credit card DTO.
 */
public class CustomerDtoAssembler extends AbstractDtoAssembler<CustomerDTO, Customer> {

	private BeanFactory beanFactory;

	private CustomerGroupService customerGroupService;

	private CreditCardFilter cardFilter;

	@Override
	public Customer getDomainInstance() {
		return getBeanFactory().getBean(ContextIdNames.CUSTOMER);
	}

	@Override
	public CustomerDTO getDtoInstance() {
		return new CustomerDTO();
	}

	@Override
	public void assembleDto(final Customer source, final CustomerDTO target) {
		target.setCreationDate(source.getCreationDate());
		target.setGuid(source.getGuid());
		target.setLastEditDate(source.getLastEditDate());

		target.setPassword(source.getPassword());
		target.setSalt(source.getCustomerAuthentication().getSalt());

		if (source.getPreferredBillingAddress() != null) {
			target.setPreferredBillingAddressGuid(source.getPreferredBillingAddress().getGuid());
		}
		if (source.getPreferredShippingAddress() != null) {
			target.setPreferredShippingAddressGuid(source.getPreferredShippingAddress().getGuid());
		}
		target.setStatus(source.getStatus());
		target.setStoreCode(source.getStoreCode());
		target.setUserId(source.getUserId());

		populateDtoAddresses(source, target);
		populateDtoCreditCards(source, target);

		for (CustomerGroup group : source.getCustomerGroups()) {
			target.getGroups().add(group.getGuid());
		}

		for (String key : source.getProfileValueMap().keySet()) {
			AttributeValue value = source.getProfileValueMap().get(key);

			AttributeValueDTO avDto = getAttributeValueDto();
			avDto.setKey(key);
			avDto.setType(value.getAttributeType().toString());
			avDto.setValue(value.getStringValue());
			target.getProfileValues().add(avDto);
		}

	}

	private void populateDtoAddresses(final Customer source, final CustomerDTO target) {
		for (CustomerAddress sourceAddress : source.getAddresses()) {
			AddressDTO targetAddress = getAddressDto();

			targetAddress.setCity(sourceAddress.getCity());
			targetAddress.setCommercialAddress(sourceAddress.isCommercialAddress());
			targetAddress.setCountry(sourceAddress.getCountry());
			targetAddress.setFaxNumber(sourceAddress.getFaxNumber());
			targetAddress.setFirstName(sourceAddress.getFirstName());
			targetAddress.setGuid(sourceAddress.getGuid());
			targetAddress.setLastName(sourceAddress.getLastName());
			targetAddress.setPhoneNumber(sourceAddress.getPhoneNumber());
			targetAddress.setStreet1(sourceAddress.getStreet1());
			targetAddress.setStreet2(sourceAddress.getStreet2());
			targetAddress.setSubCountry(sourceAddress.getSubCountry());
			targetAddress.setZipOrPostalCode(sourceAddress.getZipOrPostalCode());
			targetAddress.setOrganization(sourceAddress.getOrganization());

			target.getAddresses().add(targetAddress);
		}
	}

	private void populateDtoCreditCards(final Customer source, final CustomerDTO target) {
		for (CustomerCreditCard sourceCard : source.getCreditCards()) {
			CreditCardDTO targetCard = getCardFilter().filter(sourceCard);
			if (targetCard != null) {
				target.getCreditCards().add(targetCard);
			}
		}
	}

	@Override
	public void assembleDomain(final CustomerDTO source, final Customer target) {

		// Needed by credit card and preferred* below.
		populateDomainAddresses(source, target);

		target.setCreationDate(source.getCreationDate());
		target.setGuid(source.getGuid());
		target.setLastEditDate(source.getLastEditDate());

		target.setPassword(source.getPassword());
		target.getCustomerAuthentication().setSalt(source.getSalt());

		target.setPreferredBillingAddress(target.getAddressByGuid(source.getPreferredBillingAddressGuid()));
		target.setPreferredShippingAddress(target.getAddressByGuid(source.getPreferredShippingAddressGuid()));
		target.setStatus(source.getStatus());

		target.setStoreCode(source.getStoreCode());

		target.setUserId(source.getUserId());

		populateDomainCreditCards(source, target);

		populateDomainCustomerGroup(source, target);

		for (AttributeValueDTO avDto : source.getProfileValues()) {
			target.getCustomerProfile().setStringProfileValue(avDto.getKey(), avDto.getValue());
		}

	}

	private void populateDomainAddresses(final CustomerDTO source, final Customer target) {

		// Remove all addresses in the target which are also in the source.
		// We do it this way since AbstractAddressImpl overrides equals and doesn't use guid.
		for (AddressDTO sourceAddress : source.getAddresses()) {
			CustomerAddress targetAddress;

			targetAddress = target.getAddressByGuid(sourceAddress.getGuid());

			if (targetAddress == null) {
				targetAddress = getCustomerAddress();
				target.getAddresses().add(targetAddress);
			}

			targetAddress.setCity(sourceAddress.getCity());
			targetAddress.setCommercialAddress(sourceAddress.isCommercialAddress());
			targetAddress.setCountry(sourceAddress.getCountry());
			targetAddress.setFaxNumber(sourceAddress.getFaxNumber());
			targetAddress.setFirstName(sourceAddress.getFirstName());
			targetAddress.setGuid(sourceAddress.getGuid());
			targetAddress.setLastName(sourceAddress.getLastName());
			targetAddress.setPhoneNumber(sourceAddress.getPhoneNumber());
			targetAddress.setStreet1(sourceAddress.getStreet1());
			targetAddress.setStreet2(sourceAddress.getStreet2());
			targetAddress.setSubCountry(sourceAddress.getSubCountry());
			targetAddress.setZipOrPostalCode(sourceAddress.getZipOrPostalCode());
			targetAddress.setOrganization(sourceAddress.getOrganization());
		}
	}

	private void populateDomainCreditCards(final CustomerDTO source, final Customer target) {
		for (CreditCardDTO sourceCard : source.getCreditCards()) {
			CustomerCreditCard targetCard = null;

			for (CustomerCreditCard card : target.getCreditCards()) {
				if (sourceCard.getGuid().equals(card.getGuid())) {
					targetCard = card;
					break;
				}
			}

			if (targetCard == null) {
				targetCard = getCustomerCreditCard();
				target.getPaymentMethods().add(targetCard);
			}

			targetCard.setGuid(sourceCard.getGuid());
			targetCard.setBillingAddress(target.getAddressByGuid(sourceCard.getBillingAddressGuid()));
			targetCard.setCardHolderName(sourceCard.getCardHolderName());
			targetCard.setCardType(sourceCard.getCardType());

			if (StringUtils.isEmpty(sourceCard.getCardNumber())) {
				throw new EpInvalidValueBindException("Credit card number field is empty.");
			}

			String encryptedCardNumber = getCreditCardEncrypter().encrypt(sourceCard.getCardNumber());
			targetCard.setCardNumber(encryptedCardNumber);

			targetCard.setDefaultCard(sourceCard.isDefaultCard());
			targetCard.setExpiryMonth(sourceCard.getExpiryMonth());
			targetCard.setExpiryYear(sourceCard.getExpiryYear());
			targetCard.setIssueNumber(sourceCard.getIssueNumber());
			targetCard.setStartMonth(sourceCard.getStartMonth());
			targetCard.setStartYear(sourceCard.getStartYear());
		}
	}

	private void populateDomainCustomerGroup(final CustomerDTO source, final Customer target) {
		for (String guid : source.getGroups()) {

			CustomerGroup customerGroup = getCustomerGroupService().findByGuid(guid);

			if (!target.getCustomerGroups().contains(customerGroup)) {
				target.getCustomerGroups().add(customerGroup);
			}
		}
	}

	protected AddressDTO getAddressDto() {
		return new AddressDTO();
	}

	protected AttributeValueDTO getAttributeValueDto() {
		return new AttributeValueDTO();
	}

	protected CustomerAddress getCustomerAddress() {
		return getBeanFactory().getBean(ContextIdNames.CUSTOMER_ADDRESS);
	}

	protected CustomerCreditCard getCustomerCreditCard() {
		return getBeanFactory().getBean(ContextIdNames.CUSTOMER_CREDIT_CARD);
	}

	private CreditCardEncrypter getCreditCardEncrypter() {
		return getBeanFactory().getBean(ContextIdNames.CREDIT_CARD_ENCRYPTER);
	}

	public void setCardFilter(final CreditCardFilter filter) {
		cardFilter = filter;
	}

	/**
	 * Gets the card filter. <br>
	 * If a card filter not set then use the {@link BuiltinFilters.EMPTYING} filter.
	 *
	 * @return the card filter
	 */
	protected CreditCardFilter getCardFilter() {
		if (cardFilter == null) {
			cardFilter = BuiltinFilters.EMPTYING;
		}
		return cardFilter;
	}

	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	protected BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public void setCustomerGroupService(final CustomerGroupService customerGroupService) {
		this.customerGroupService = customerGroupService;
	}

	protected CustomerGroupService getCustomerGroupService() {
		return customerGroupService;
	}

}
