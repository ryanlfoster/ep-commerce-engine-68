/**
 * 
 */
package com.elasticpath.sfweb.formbean.validator.impl;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.owasp.esapi.ESAPI;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import com.elasticpath.commons.util.impl.ClassLoaderUtils;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.domain.catalog.impl.ProductTypeImpl;
import com.elasticpath.sfweb.formbean.ShoppingItemFormBean;
import com.elasticpath.sfweb.formbean.impl.CartFormBeanImpl;
import com.elasticpath.sfweb.formbean.impl.ShoppingItemFormBeanImpl;

/**
 * A test for CartFormBean validator. 
 *
 */

public class GiftCertificateFormBeanValidatorTest {

	private static final String EXPECTED_ERROR_CODE = "Error code should be the one provided for string validation";
	private static final String ERROR_XSS = "errorXSS";
	private static final String BASIC_XSS_CODE = "<IMG SRC=\"javascript:alert('XSS');\">";
	private static final String WE_DON_T_EXPECT_ANY_ERRORS_HERE = "We don't expect any errors here";
	private static final String WE_EXPECT_ERROR_HERE = "We expect error here";
	private static final String ADVANCED_XSS_CODE = "%3C%53%43%52%49%50%54%20%53%52%43%3D%68%74%74%70" 
		+ "%3A%2F%2F%68%61%2E%63%6B%65%72%73%2E%6F%72%67%2F%78%73%73%2E%6A%73%3E%3C%2F%53%43%52%49%50%54%3E";
	private GiftCertificateFormBeanValidator validator;
	private CartFormBeanImpl cartFormBean;
	private ShoppingItemFormBean shoppingItemFormBean;

	private Errors errors;
	
	private ProductType productType;

	/**
	 * Reset ESAPI before running tests so that it finds correct ESAPI.properties file.
	 * 
	 * @throws MalformedURLException in case of error forming URL of security path
	 */
	@BeforeClass
	public static void resetESAPI() throws MalformedURLException {
		File securityFolder = new File("target/classes/WEB-INF/security");
		if (!securityFolder.exists()) {
			securityFolder = new File("com.elasticpath.sf/WEB-INF/security");
		}
		ClassLoaderUtils.addURL(securityFolder.toURL());
		ESAPI.securityConfiguration().setResourceDirectory(securityFolder.getAbsolutePath());
	}
	
	/**
	 * Setup initial objects.
	 */
	@Before
	public void setUp() {
		
		productType = new ProductTypeImpl();
		productType.setName("Gift Certificates");
		
		Product product = new ProductImpl();
		product.setProductType(productType);

		final ProductSku productSku = new ProductSkuImpl();
		productSku.setProduct(product);
		
		validator = new GiftCertificateFormBeanValidator();
		Map<String, String> errorCodeMap = new HashMap<String, String>();
		errorCodeMap.put("errorInvalidConfirmEmail", "error1");
		errorCodeMap.put("errorInvalidRecipientEmail", "error2");
		errorCodeMap.put("errorInvalidRecipientEmail", "error2");
		errorCodeMap.put("errorInvalidEmailsAreNotEqual", "error3");
		errorCodeMap.put("errorInvalidStringValue", ERROR_XSS);
		validator.setErrorCodeMap(errorCodeMap);
		validator.setErrorInvalidQuantity("error4");
		
		shoppingItemFormBean = new ShoppingItemFormBeanImpl();
		shoppingItemFormBean.setSkuCode("sku_code");
		shoppingItemFormBean.setSelected(true);
		shoppingItemFormBean.getGiftCertificateFields().setRecipientEmail("jon@elasticpath.com");
		shoppingItemFormBean.getGiftCertificateFields().setConfirmEmail("jon@elasticpath.com");
		shoppingItemFormBean.setQuantity(1);
		
		cartFormBean = new CartFormBeanImpl();
		cartFormBean.addShoppingItemFormBean(shoppingItemFormBean);

		errors = new BeanPropertyBindingResult(cartFormBean, "command");

	}
	
	/** Test if product type eq gift certificate. */
	@Test
	public void validateTrue() {
		validator.validate(shoppingItemFormBean, errors, 0);
		assertFalse(WE_DON_T_EXPECT_ANY_ERRORS_HERE, errors.hasFieldErrors());
	}

	/** Test if product is other type. */
	@Test
	public void validateTrueForAnyOtherType() {
		productType.setName("Any Name");
		validator.validate(shoppingItemFormBean, errors, 0);
		assertFalse(WE_DON_T_EXPECT_ANY_ERRORS_HERE, errors.hasFieldErrors());
	}

	/** Test if quantity is wrong. */
	@Test
	public void validateFalseQuantity() {
		shoppingItemFormBean.setQuantity(0);
		validator.validate(shoppingItemFormBean, errors, 0);
		assertTrue(WE_EXPECT_ERROR_HERE, errors.hasFieldErrors());
		FieldError error = (FieldError) errors.getAllErrors().get(0);
		assertEquals("cartItems[0].quantity", error.getField());
	}


	/** Test if recipient email is failed. */
	@Test
	public void validateFalseRecipientEmail() {
		shoppingItemFormBean.getGiftCertificateFields().setRecipientEmail("");
		validator.validate(shoppingItemFormBean, errors, 0);
		assertTrue(WE_EXPECT_ERROR_HERE, errors.hasFieldErrors());
		FieldError error = (FieldError) errors.getAllErrors().get(0);
		assertEquals("cartItems[0].giftCertificateFields.recipientEmail", error.getField());
	}

	/** Test if confirm email is failed. */
	@Test
	public void validateFalseConfirmEmail() {
		shoppingItemFormBean.getGiftCertificateFields().setConfirmEmail("");
		validator.validate(shoppingItemFormBean, errors, 0);
		assertTrue(WE_EXPECT_ERROR_HERE, errors.hasFieldErrors());
		FieldError error = (FieldError) errors.getAllErrors().get(0);
		assertEquals("cartItems[0].giftCertificateFields.confirmEmail", error.getField());
	}
	
	/** Test if recipient email is not eq confirm email. */
	@Test
	public void validateFalseRecipientAndConfirmEmailsAreNotEqual() {
		shoppingItemFormBean.getGiftCertificateFields().setConfirmEmail("roo@xyz.com");
		validator.validate(shoppingItemFormBean, errors, 0);
		assertTrue(WE_EXPECT_ERROR_HERE, errors.hasFieldErrors());
		FieldError error = (FieldError) errors.getAllErrors().get(0);
		assertEquals("cartItems[0].giftCertificateFields.confirmEmail", error.getField());
	}
	
	/**
	 * Test validation fails if XSS elements are put in the sender name field.
	 */
	@Test
	public void validateXSSSenderValue() {
		shoppingItemFormBean.getGiftCertificateFields().setSenderName(BASIC_XSS_CODE);
		validator.validate(shoppingItemFormBean, errors, 0);
		assertTrue("XSS in sender field should fail validation", errors.hasFieldErrors());
		FieldError error = (FieldError) errors.getAllErrors().get(0);
		assertEquals("Field in error should be the sender field", "cartItems[0].giftCertificateFields.senderName", error.getField());
		assertEquals(EXPECTED_ERROR_CODE, ERROR_XSS, error.getCode());
	}

	/**
	 * Test validation fails if XSS elements are put in the recipient name field.
	 */
	@Test
	public void validateXSSRecipientValue() {
		shoppingItemFormBean.getGiftCertificateFields().setRecipientName(BASIC_XSS_CODE);
		validator.validate(shoppingItemFormBean, errors, 0);
		assertTrue("XSS in recipient field should fail validation", errors.hasFieldErrors());
		FieldError error = (FieldError) errors.getAllErrors().get(0);
		assertEquals("Field in error should be the recipient field", "cartItems[0].giftCertificateFields.recipientName", error.getField());
		assertEquals(EXPECTED_ERROR_CODE, ERROR_XSS, error.getCode());
	}
	
	/**
	 * Test validation fails if XSS elements are put in the message field.
	 */
	@Test
	public void validateXSSMessageValue() {
		shoppingItemFormBean.getGiftCertificateFields().setMessage(BASIC_XSS_CODE);
		validator.validate(shoppingItemFormBean, errors, 0);
		assertTrue("XSS in message field should fail validation", errors.hasFieldErrors());
		FieldError error = (FieldError) errors.getAllErrors().get(0);
		assertEquals("Field in error should be the message field", "cartItems[0].giftCertificateFields.message", error.getField());
		assertEquals(EXPECTED_ERROR_CODE, ERROR_XSS, error.getCode());
	}
	
	/**
	 * Test validation fails if Advanced XSS elements are put in the message field.
	 */
	@Test
	public void validateAdvancedXSSMessageValue() {
		shoppingItemFormBean.getGiftCertificateFields().setMessage(ADVANCED_XSS_CODE);
		validator.validate(shoppingItemFormBean, errors, 0);
		assertTrue("XSS in message field should fail validation", errors.hasFieldErrors());
		FieldError error = (FieldError) errors.getAllErrors().get(0);
		assertEquals("Field in error should be the message field", "cartItems[0].giftCertificateFields.message", error.getField());
		assertEquals(EXPECTED_ERROR_CODE, ERROR_XSS, error.getCode());
	}
	
	/**
	 * Test validation passes if message field contains whitespace.
	 */
	@Test
	public void validateMessageWhitespace() {
		shoppingItemFormBean.getGiftCertificateFields().setMessage("Message containing whitespace");
		validator.validate(shoppingItemFormBean, errors, 0);
		assertFalse("Whitespace in message field should pass validation", errors.hasFieldErrors());
	}
	
	/**
	 * Test validation passes if message field contains non-English letters (e.g. Japanese).
	 */
	@Test
	public void validateMessageNonEnglish() {
		shoppingItemFormBean.getGiftCertificateFields()
			.setMessage("\u0030e1\u0030c3\u0030bb\u0030fc\u0030b8\u0065e5\u00672c\u003092\u00542b\u003080");
		validator.validate(shoppingItemFormBean, errors, 0);
		assertFalse("Message in other languages should pass validation", errors.hasFieldErrors());
	}
	
	/**
	 * Test validation passes if message field contains basic punctuation.
	 */
	@Test
	public void validateMessageWithPunctuation() {
		shoppingItemFormBean.getGiftCertificateFields().setMessage("Happy Birthday, Dude! Have fun (not too much fun), ok?? Be Cool.");
		validator.validate(shoppingItemFormBean, errors, 0);
		assertFalse("Message with punctuation should pass validation", errors.hasFieldErrors());
	}
	


}
