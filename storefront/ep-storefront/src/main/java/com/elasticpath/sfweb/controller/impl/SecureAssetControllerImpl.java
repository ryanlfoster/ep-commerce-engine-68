/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.sfweb.controller.impl;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;

import com.elasticpath.commons.constants.WebConstants;
import com.elasticpath.commons.util.AssetRepository;
import com.elasticpath.commons.util.security.StringEncrypter;
import com.elasticpath.domain.catalog.DigitalAsset;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.service.asset.DigitalAssetAuditService;
import com.elasticpath.sfweb.util.IpAddressResolver;
import com.elasticpath.web.service.FileDownloadService;

/**
 * Spring MVC controller for beginning the checkout process.
 */
public class SecureAssetControllerImpl extends AbstractEpControllerImpl {

	private static final Logger LOG = Logger.getLogger(SecureAssetControllerImpl.class);

	private String successView;

	private String errorView;

	private FileDownloadService fileDownloadService;

	private DigitalAssetAuditService digitalAssetAuditService;

	private static final int VALID_REQUEST = 0;

	private static final int EXPIRED = 3;

	private static final int EXCEED_MAX_DOWNLOAD_TIMES = 4;

	private AssetRepository assetRepository;

	private IpAddressResolver ipAddressResolver;

	/**
	 * Return the ModelAndView for the configured static view page.
	 * 
	 * @param request -the request
	 * @param response -the response
	 * @return - the ModleAndView instance for the static page.
	 * @throws Exception if anything goes wrong.
	 */
	@Override
	protected ModelAndView handleRequestInternal(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
		LOG.debug("entering 'AssetControllerImpl.handleRequestInternal' method...");

		String assetID = getRequestHelper().getStringParameterOrAttribute(request, WebConstants.REQUEST_ASSEST_ID, "");
		String orderSKUID = getRequestHelper().getStringParameterOrAttribute(request, WebConstants.REQUEST_ORDERSKU_ID, "");

		// Replace the space back to '+' due to URL encode,
		// or you can use URLEncoder to encode the '+' to '%2B' when generate the download URL
		assetID = assetID.replace(' ', '+');
		orderSKUID = orderSKUID.replace(' ', '+');

		// Decrypt the assetID and orderSKUID using the same key we used to encrypt it
		final StringEncrypter digitalAssetStringEncrypter = getBean("digitalAssetStringEncrypter");
		final String decryptedAssetId = digitalAssetStringEncrypter.decrypt(assetID);
		final String decryptedOrderSKUID = digitalAssetStringEncrypter.decrypt(orderSKUID);

		final long assetUid = (new Long(decryptedAssetId)).longValue();
		final long orderSkuUid = (new Long(decryptedOrderSKUID)).longValue();

		ModelAndView resultView = null;
		final CustomerSession customerSession = getRequestHelper().getCustomerSession(request);
		final Customer customer = customerSession.getShopper().getCustomer();
		final int errorType = digitalAssetAuditService.isValidDADownloadRequest(assetUid, orderSkuUid, customer.getUidPk());
		
		if (errorType == VALID_REQUEST) {
			final DigitalAsset digitalAsset = digitalAssetAuditService.getDigitalAsset(orderSkuUid);
			final String fileName = digitalAsset.getFileName();
			final String fileFullPath = getDigitalAssetsPath() + File.separator + fileName;

			if (LOG.isDebugEnabled()) {
				LOG.debug("Going to download " + fileFullPath);
			}

			fileDownloadService.download(request, response, fileFullPath);

			digitalAssetAuditService.addDigitalAssetAudit(ipAddressResolver.getRemoteAddr(request), assetUid, orderSkuUid);

		} else {
			final Map<String, Serializable> secureAssetModel = new HashMap<String, Serializable>();
			if (errorType == EXCEED_MAX_DOWNLOAD_TIMES || errorType == EXPIRED) {
				// should be able to get digital asset for these error type
				final DigitalAsset digitalAsset = digitalAssetAuditService.getDigitalAsset(orderSkuUid);
				secureAssetModel.put("DigitalAsset", digitalAsset);
				secureAssetModel.put("OrderNumber", digitalAssetAuditService.findOrderNumberByOrderSkuID(orderSkuUid));
			}
			secureAssetModel.put("DownloadAssetErrorType", Integer.valueOf(errorType));
			secureAssetModel.put("DefaultDownloadMessage", "Fail to download file.");
			resultView = new ModelAndView(getErrorView(), "secureAssetModel", secureAssetModel);
		}

		// Retain in the same page if resultView is null
		return resultView;

	}

	/**
	 * Gets the full path prefix to digital assets.
	 * 
	 * @return the full path prefix to digital assets
	 */
	private String getDigitalAssetsPath() {
		return getAssetRepository().getCatalogDigitalGoodsPath();
	}

	/**
	 * Sets the success view name.
	 * 
	 * @param successView name of the success view
	 */
	public final void setSuccessView(final String successView) {
		this.successView = successView;
	}

	/**
	 * Gets the success view name.
	 * 
	 * @return name of the success view
	 */
	public String getSuccessView() {
		return this.successView;
	}

	/**
	 * Sets the error view name.
	 * 
	 * @param errorView name of the error view
	 */
	public final void setErrorView(final String errorView) {
		this.errorView = errorView;
	}

	/**
	 * Gets the error view name.
	 * 
	 * @return name of the error view
	 */
	public String getErrorView() {
		return this.errorView;
	}

	public void setFileDownloadService(final FileDownloadService fileDownloadService) {
		this.fileDownloadService = fileDownloadService;
	}

	/**
	 * Sets the digitalAssetAudit service.
	 * 
	 * @param digitalAssetAuditService the digitalAssetAudit service
	 */
	public void setDigitalAssetAuditService(final DigitalAssetAuditService digitalAssetAuditService) {
		this.digitalAssetAuditService = digitalAssetAuditService;
	}

	/**
	 * @return the assetRepository
	 */
	public AssetRepository getAssetRepository() {
		return assetRepository;
	}

	/**
	 * @param assetRepository the assetRepository to set
	 */
	public void setAssetRepository(final AssetRepository assetRepository) {
		this.assetRepository = assetRepository;
	}

	/**
	 * @param ipAddressResolver the {@link IpAddressResolver} to set.
	 */
	public void setIpAddressResolver(final IpAddressResolver ipAddressResolver) {
		this.ipAddressResolver = ipAddressResolver;
	}

}
