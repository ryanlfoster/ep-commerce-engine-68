package com.elasticpath.sfweb.formbean.impl;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.Map;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.shoppingcart.ShoppingItem;

/**
 * Tests the data conversion in {@code GiftCertificateFormBean2Impl}.
 */
public class GiftCertificateFormBeanImplTest {
	
	private static final String YOU_KNOW_WHY = "You know why ;->";
	private static final String MISS_MONEYPENNY = "Miss Moneypenny";
	private static final String JAMES_BOND = "James Bond";
	private static final String EMAIL_ADDRESS = "me@there.com";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	/**
	 * Tests that setting fields on the bean can be returned as itemFields.
	 */
	@Test 
	public void testGetAsItemFields() {
		GiftCertificateFormBeanImpl formBean = new GiftCertificateFormBeanImpl();
		
		formBean.setRecipientEmail(EMAIL_ADDRESS);
		formBean.setRecipientName(JAMES_BOND);
		formBean.setSenderName(MISS_MONEYPENNY);
		formBean.setPurchaseAmount(new BigDecimal("12.34"));
		formBean.setMessage(YOU_KNOW_WHY);
		
		Map<String, String> map = formBean.getAsItemFields();
		
		assertEquals(EMAIL_ADDRESS, map.get("giftCertificate.recipientEmail"));
		assertEquals(JAMES_BOND, map.get("giftCertificate.recipientName"));
		assertEquals(MISS_MONEYPENNY, map.get("giftCertificate.senderName"));
//		assertEquals("12.34", map.get("giftCertificate.purchaseAmount"));
		assertEquals(YOU_KNOW_WHY, map.get("giftCertificate.message"));
	}
	
	/**
	 * Tests that the bean can be initialized from fields.
	 */
	@Test 
	public void testInitFromItemFields() {

		GiftCertificateFormBeanImpl formBean = new GiftCertificateFormBeanImpl();
		
		final ShoppingItem shoppingItem = context.mock(ShoppingItem.class);
		
		context.checking(new Expectations() { { 
			exactly(2).of(shoppingItem).getFieldValue("giftCertificate.recipientEmail"); will(returnValue(EMAIL_ADDRESS));
			oneOf(shoppingItem).getFieldValue("giftCertificate.recipientName"); will(returnValue(JAMES_BOND));
			oneOf(shoppingItem).getFieldValue("giftCertificate.senderName"); will(returnValue(MISS_MONEYPENNY));
			oneOf(shoppingItem).getFieldValue("giftCertificate.message"); will(returnValue(YOU_KNOW_WHY));
		} });
		
		formBean.initFromShoppingItem(shoppingItem);
		
		assertEquals(EMAIL_ADDRESS, formBean.getRecipientEmail());
		assertEquals(EMAIL_ADDRESS, formBean.getConfirmEmail());
		assertEquals(JAMES_BOND, formBean.getRecipientName());
		assertEquals(MISS_MONEYPENNY, formBean.getSenderName());
		assertEquals(YOU_KNOW_WHY, formBean.getMessage());
	}
}
