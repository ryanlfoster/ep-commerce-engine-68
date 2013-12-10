package com.elasticpath.service.changeset.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.common.dto.pricing.PriceListDescriptorDTO;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.domain.misc.impl.RandomGuidImpl;
import com.elasticpath.domain.pricing.impl.PriceListDescriptorImpl;

/**
 * Test for {@link PriceListDescriptorDTOGuidResolver}.
 */
@SuppressWarnings("PMD.TooManyStaticImports")
public class PriceListDescriptorDTOGuidResolverTest {
	private PriceListDescriptorDTOGuidResolver resolver;

	/**
	 * Setup test.
	 */
	@Before
	public void setUp() {
		resolver = new PriceListDescriptorDTOGuidResolver();
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
		assertFalse(resolver.isSupportedObject(new PriceListDescriptorImpl()));
		assertTrue(resolver.isSupportedObject(new PriceListDescriptorDTO()));
		assertFalse(resolver.isSupportedObject(null));
		assertFalse(resolver.isSupportedObject(new Object()));
	}
	
	/**
	 * Test resolve guid.
	 */
	@SuppressWarnings("PMD.AvoidCatchingNPE")
	@Test
	public void testResolveGuid() {
		PriceListDescriptorDTO dto = new PriceListDescriptorDTO();
		dto.setGuid(null);
		assertNotNull("Should be resolved to a new random guid", resolver.resolveGuid(dto));
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
