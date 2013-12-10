package com.elasticpath.service.notification.helper;

import java.util.ArrayList;
import java.util.List;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.GiftCertificate;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.misc.EmailProperties;
import com.elasticpath.domain.misc.GiftCertificateEmailPropertyHelper;
import com.elasticpath.service.misc.OrderEmailPropertyHelper;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.catalog.ProductService;
import com.elasticpath.service.order.OrderService;

/**
 * EmailNotificationHelper a notification helper class to encapsulate the
 * dependencies and functionality required to send an email.
 */
public class EmailNotificationHelperImpl implements EmailNotificationHelper {

	private BeanFactory beanFactory;

	private OrderService orderService;
	
	private ProductService productService;

	/**
	 * Send a confirmation email.
	 * 
	 * @param orderNumber
	 *            is the order number
	 * @return whether the email was sent successfully
	 */
	public EmailProperties getOrderEmailProperties(final String orderNumber) {
		final Order order = orderService.findOrderByOrderNumber(orderNumber);
		OrderEmailPropertyHelper orderEmailPropertyHelper = beanFactory.getBean(ContextIdNames.EMAIL_PROPERTY_HELPER_ORDER);
		return orderEmailPropertyHelper.getOrderConfirmationEmailProperties(order);
	}

	/**
	 * Set the order service.
	 * 
	 * @param orderService
	 *            is the order service
	 */
	public void setOrderService(final OrderService orderService) {
		this.orderService = orderService;
	}

	/**
	 * Set the bean factory.
	 * 
	 * @param beanFactory
	 *            is the bean factory
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * ${@inheritDoc}.
	 * @throws EpServiceException if any of the {@code Product}s in the given {@code Order}
	 * cannot be retrieved from persistent storage
	 */
	public List<EmailProperties> getGiftCertificateEmailProperties(final String orderNumber) {
		List<EmailProperties> retList = new ArrayList<EmailProperties>();

		GiftCertificateEmailPropertyHelper giftCertificateEmailPropertyHelper = beanFactory
				.getBean(ContextIdNames.EMAIL_PROPERTY_HELPER_GIFT_CERT);

		final Order order = orderService.findOrderByOrderNumber(orderNumber);

		for (final OrderSku currOrderSku : order.getOrderSkus()) {
			if (GiftCertificate.KEY_PRODUCT_TYPE.equals(getProductTypeNameFromOrderSku(currOrderSku))) {
				String giftCertificateImageFilename = currOrderSku.getProductSku().getImage();

				EmailProperties emailProperties = giftCertificateEmailPropertyHelper
					.getEmailProperties(order, currOrderSku, giftCertificateImageFilename);
				retList.add(emailProperties);
			}
		}
		return retList;
	}

	/**
	 * @param orderSku the {@code OrderSku} referencing a particular type of product
	 * @return the string representation of the product type
	 * @throws EpServiceException if the product cannot be found
	 */
	String getProductTypeNameFromOrderSku(final OrderSku orderSku) {
		Product product = getProductService().get(orderSku.getProductSku().getProduct().getUidPk());
		if (product == null) {
			throw new EpServiceException("Product uid=" + orderSku.getProductSku().getProduct().getUidPk() + " could not be found.");
		}
		return product.getProductType().getName();		
	}

	@Override
	public EmailProperties getShipmentConfirmationEmailProperties(
			final String orderNumber, final String shipmentNumber) {
		Order order = orderService.findOrderByOrderNumber(orderNumber);

		OrderShipment orderShipment = order.getShipment(shipmentNumber);

		OrderEmailPropertyHelper orderEmailPropertyHelper = beanFactory
				.getBean(ContextIdNames.EMAIL_PROPERTY_HELPER_ORDER);

		return orderEmailPropertyHelper.getShipmentConfirmationEmailProperties(
				order, orderShipment);
	}

	/**
	 * @param productService the productService to set
	 */
	public void setProductService(final ProductService productService) {
		this.productService = productService;
	}

	/**
	 * @return the productService
	 */
	public ProductService getProductService() {
		return productService;
	}	
}