package com.elasticpath.service.catalog.impl;

import java.util.Locale;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.attribute.AttributeValueGroup;
import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalogview.SeoUrlBuilder;
import com.elasticpath.domain.catalogview.impl.StoreSeoUrlBuilderFactoryImpl;
import com.elasticpath.domain.misc.LocalizedProperties;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.catalog.ProductService;
import com.elasticpath.service.catalog.ProductXmlService;
import com.elasticpath.service.impl.AbstractEpServiceImpl;

/**
 * Generates Product data in XML format.
 */
public class ProductXmlServiceImpl extends AbstractEpServiceImpl implements ProductXmlService {

	private ProductService productService;

	private static final String ATTRIBUTE_KEY_DESCRIPTION = "description";
	private static final int CHAR = 0; // The index of the special xml character to be replaced
	private static final int ESCAPE = 1; // The index of the xml escape characters
	
	/**
	 * Returns the minimal product data required by PowerReviews in xml format.
	 * 
	 * @param catalog - the catalog that the product is should be in.
	 * @param baseUrl - the base url of the request.
	 * @param productGuid - the product uid of the product to get data from.
	 * @param isSeoEnabled - True if SEO is enabled for the store.
	 * @return the product data in xml format
	 * @throws EpServiceException - in case of errors
	 */
	public String getProductMinimalXml(final Catalog catalog, final String baseUrl, final String productGuid, final boolean isSeoEnabled)
		throws EpServiceException {

		// Get the product using the product guid
		Product product = null;
		boolean productUidValid = false;
		if (productGuid != null && !productGuid.equals("")) {
			long productUid = this.productService.findUidById(productGuid);
			product = productService.get(productUid);
			if (product != null && product.isInCatalog(catalog)) {
				productUidValid = true;
			}
		}
		
		// Generate the product xml if the product exists, otherwise generate error xml
		String productXml = "";
		if (productUidValid) {
			productXml = generateMinimalXml(baseUrl, product, isSeoEnabled);
		} else {
			productXml = getInvalidProductXml();
		}
			
		return productXml;
	}
	
	/**
	 * Formats minimal product data required by PowerReviews into XML.
	 * <p>
	 * Note: This method has been given default access for testing purposes only.
	 * It should not be called by any classes outside of this one.
	 * 
	 * @param baseUrl - the base url of the request
	 * @param product - The product to generate XML from
	 * @param isSeoEnabled - True if SEO is enabled for the store
	 * @return A string of XML containing the product details.
	 */
	String generateMinimalXml(final String baseUrl, final Product product, final boolean isSeoEnabled) {

		String productXml = "";		
		Locale defaultLocale = product.getMasterCatalog().getDefaultLocale();
		Category parentCategory = product.getDefaultCategory(product.getMasterCatalog());
		
		// Get product string data and escape special characters
		String productTagStart = " <product xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"";
		String productNameXml = formatAsXmlString(product.getDisplayName(defaultLocale));
		
		AttributeValueGroup attributeValues = product.getAttributeValueGroup();
		String descriptionXml = formatAsXmlString(attributeValues.getStringAttributeValue(ATTRIBUTE_KEY_DESCRIPTION, defaultLocale));
		
		LocalizedProperties brandProperties = product.getBrand().getLocalizedProperties();
		String brandXml = formatAsXmlString(brandProperties.getValue(Brand.LOCALIZED_PROPERTY_DISPLAY_NAME, defaultLocale));

		String productUri = "";
		if (isSeoEnabled) {
			StoreSeoUrlBuilderFactoryImpl storeSeoUrlBuilderFactory = getBean(ContextIdNames.STORE_SEO_URL_BUILDER_FACTORY);
			SeoUrlBuilder urlBuilder = storeSeoUrlBuilderFactory.getStoreSeoUrlBuilder();

			productUri = "/" + urlBuilder.productSeoUrl(product, defaultLocale);
		} else {
			productUri = "/product-view.ep?pID=" + product.getUidPk();
		}
		
		String productEndTag = " </product>";

		//Build the product xml tag string
		String productTag = productTagStart
			+ " productName=\"" + productNameXml + "\""
 			+ " description=\"" + descriptionXml + "\""
 			+ " brand=\"" + brandXml + "\""
 			+ " productBaseUrl=\"" + baseUrl + "\""
 			+ " productUri=\"" + productUri + "\""
 			+  ">";
		
		// Get parent category string data and escape special characters
		String categoryNameXml = formatAsXmlString(parentCategory.getDisplayName(defaultLocale));
		
		// Build the parent category xml string
		String categoryHierarchy = "";
		categoryHierarchy = "<category name=\"" + categoryNameXml
			+ "\" code=\"" + parentCategory.getUidPk() + "\">";
		
		// Build the rest of the category hierarchy xml string
		Category currentCategory = parentCategory.getParent();
		int categoryCount = 1;
		while (currentCategory != null) {
			categoryNameXml = formatAsXmlString(currentCategory.getDisplayName(defaultLocale));
			String categoryXml = "<category name=\"" + categoryNameXml
				+ "\" code=\"" + currentCategory.getUidPk() + "\"> ";
			categoryHierarchy = categoryXml + categoryHierarchy;
			categoryCount++;
			
			currentCategory = currentCategory.getParent();
		}
		
		// Add the closing category tags for each category in the hierarchy
		for (int i = 0; i < categoryCount; i++) {
			categoryHierarchy = categoryHierarchy + " </category>";
		}
		
		categoryHierarchy = " <categoryHierarchy> " + categoryHierarchy + " </categoryHierarchy>";
		
		String xmlHeader = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
		productXml = xmlHeader + productTag + categoryHierarchy + productEndTag;
		
		return productXml;
	}
	
	/**
	 * Generates the xml string to use in response to a productUid that is invalid.
	 *
	 * @return An xml string indicating that the productUid is invalid.
	 */
	private String getInvalidProductXml() {
		// Currently PowerReviews does not require any special message, so return empty string
		return "";
	}
	
	
	/**
	 * Escapes any xml special characters in the string.
	 *
	 * @param data - The string to escape the characters in
	 * @return A string suitable for xml with all special characters escaped.
	 */
	private String formatAsXmlString(final String data) {
		String[][] characterXmlEscapes = getCharacterXmlEscapes();
		String escapedData = data;
		
		// Search for and escape any xml special characters found in the string
		for (int i = 0; i < characterXmlEscapes.length; i++) {
			escapedData = escapedData.replaceAll(characterXmlEscapes[i][CHAR], characterXmlEscapes[i][ESCAPE]);
		}
		
		return escapedData;
	}
	
	/**
	 * Generates the array of xml special characters to be escaped along with their
	 * corresponding replacement escape characters.
	 *
	 * @return A two-dimensional array with all xml special characters and their escapes.
	 */
	private String[][] getCharacterXmlEscapes() {
		String[][] xmlCharactersToEscape1 = {{"&", "&amp;"},
											 {"<", "&lt;"},
											 {">", "&gt;"},
											 {"\"", "&quot;"},
											 {"\'", "&apos;"} };
		
		return xmlCharactersToEscape1;
	}

	/**
	 * Sets the product service.
	 * 
	 * @param productService the product service
	 */
	public void setProductService(final ProductService productService) {
		this.productService = productService;
	}
	
	
}
