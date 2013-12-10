package com.elasticpath.importexport.common.adapters.paymentgateway;

import com.elasticpath.common.dto.paymentgateway.PaymentGatewayDTO;
import com.elasticpath.common.dto.paymentgateway.PaymentGatewayDtoAssembler;
import com.elasticpath.domain.payment.PaymentGateway;
import com.elasticpath.importexport.common.adapters.AbstractDomainAdapterImpl;

/**
 * Domain adapter for PaymentGateways. Similar to a DtoAssemblerDelegating one, however has some special handling as described in
 * {@link com.elasticpath.common.dto.paymentgateway.PaymentGatewayDtoAssembler}.
 */
public class PaymentGatewayAdapter extends AbstractDomainAdapterImpl<PaymentGateway, PaymentGatewayDTO> {

	private PaymentGatewayDtoAssembler assembler;

	@Override
	public void populateDTO(final PaymentGateway source, final PaymentGatewayDTO target) {
		assembler.assembleDto(source, target);
	}

	@Override
	public PaymentGatewayDTO createDtoObject() {
		return assembler.getDtoInstance();
	}

	@Override
	public PaymentGateway createDomainObject() {
		return assembler.getDomainInstance();
	}

	public void setAssembler(final PaymentGatewayDtoAssembler assembler) {
		this.assembler = assembler;
	}

	@Override
	public PaymentGateway buildDomain(final PaymentGatewayDTO source, final PaymentGateway target) {

		PaymentGateway gateway;

		if (target.isPersisted()) {
			assembler.assembleDomain(source, target);
			gateway = target;
		} else {
			gateway = assembler.assembleDomain(source);
		}

		return gateway;
	}

	/**
	 * This method is not supported for payment gateways. Use buildDomain above.
	 * 
	 * @param source ignored: this method throws {@code UnsupportedOperationException}
	 * @param target ignored: this method throws {@code UnsupportedOperationException}
	 */
	public void populateDomain(final PaymentGatewayDTO source, final PaymentGateway target) {
		/* This method is pass by reference, so we can't change the reference to 'target' without returning it */
		throw new UnsupportedOperationException("If we are populating a non persisted paymentgateway, we must create one through a factory.");
	}

}
