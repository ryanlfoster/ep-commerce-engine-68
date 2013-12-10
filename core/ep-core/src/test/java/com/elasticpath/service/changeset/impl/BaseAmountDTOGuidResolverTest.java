package com.elasticpath.service.changeset.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.common.dto.pricing.BaseAmountDTO;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.domain.misc.impl.RandomGuidImpl;
import com.elasticpath.domain.pricing.impl.BaseAmountImpl;

/**
 * Test for {@link BaseAmountDTOGuidResolver}.
 */
@SuppressWarnings("PMD.TooManyStaticImports")
public class BaseAmountDTOGuidResolverTest {

	private BaseAmountDTOGuidResolver resolver;

	/**
	 * Setup test.
	 */
	@Before
	public void setUp() {
		resolver = new BaseAmountDTOGuidResolver();
		resolver.setBeanFactory(new BeanFactory() {
			@SuppressWarnings("unchecked")
			public <T> T getBean(final String name) {
				return (T) new RandomGuidImpl();
			}
			
			public <T> Class<T> getBeanImplClass(final String beanName) {
				return null;
			}
		});
	}

	/**
	 * Test isSupportObject.
	 */
	@Test
	public void testIsSupportedObject() {
		assertFalse(resolver.isSupportedObject(new BaseAmountImpl()));
		assertTrue(resolver.isSupportedObject(new BaseAmountDTO()));
		assertFalse(resolver.isSupportedObject(null));
	}
	
	/**
	 * Test resolve guid.
	 */
	@SuppressWarnings("PMD.AvoidCatchingNPE")
	@Test
	public void testResolveGuid() {
		BaseAmountDTO dto = new BaseAmountDTO();
		dto.setGuid(null);
		assertNotNull(resolver.resolveGuid(dto));
		dto.setGuid("HI");
		assertEquals("HI", resolver.resolveGuid(dto));
		
		try {
			resolver.resolveGuid(null);
			fail("Should have NPEd on null dto");
		} catch (NullPointerException e) {
			return;
		}
	}
	
}
