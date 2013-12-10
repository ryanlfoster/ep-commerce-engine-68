package com.elasticpath.service.contentspace;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import org.junit.Assert;

import org.junit.Test;

import com.elasticpath.service.contentspace.impl.VelocityRendererImpl;

/**
 * Test case for {@link RendererFactory}.
 */
public class RendererFactoryTest {

	/**
	 * Tests that we get one and the same instance when getInstance() is invoked.
	 */
	@Test
	public void testGetInstance() {
		RendererFactory instance = RendererFactory.getInstance();
		assertNotNull(instance);
		assertSame(instance, RendererFactory.getInstance());
	}

	/**
	 * Tests that createRenderer() creates new instance every time it is invoked.
	 */
	@Test
	public void testCreateRenderer() {
		RendererFactory instance = RendererFactory.getInstance();
		instance.setRendererClass(VelocityRendererImpl.class);
		
		Renderer instance1 = instance.createRenderer();
		assertNotNull(instance1);
		Renderer instance2 = instance.createRenderer();
		assertNotNull(instance2);
		
		Assert.assertNotSame(instance1, instance2);
	}

}
