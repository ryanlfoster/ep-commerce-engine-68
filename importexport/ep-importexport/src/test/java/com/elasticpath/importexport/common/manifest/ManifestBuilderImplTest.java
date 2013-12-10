package com.elasticpath.importexport.common.manifest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.importexport.common.exception.runtime.EngineRuntimeException;
import com.elasticpath.importexport.common.manifest.impl.ManifestBuilderImpl;
import com.elasticpath.importexport.common.types.JobType;

/**
 * Tests ManifestBuilder Implementation. 
 */
public class ManifestBuilderImplTest {

	private static final String PRODUCT_RESOURCE_NAME = "product.xml";
	private static final String CATALOG_RESOURCE_NAME = "catalog.xml";
	private static final String ASSETS_RESOURCE_NAME = "assets.xml";
	private final ManifestBuilderImpl manifestBuilder = new ManifestBuilderImpl();
	
	/**
	 * Setup tests.
	 */
	@Before
	public void setUp() throws Exception {
		List<JobType> jobTypePriority = new ArrayList<JobType>();
		jobTypePriority.add(JobType.CATALOG);
		jobTypePriority.add(JobType.PRODUCT);
		jobTypePriority.add(JobType.ASSETS);
		manifestBuilder.setJobTypePriority(jobTypePriority);
		
		assertEquals(jobTypePriority, manifestBuilder.getJobTypePriority());
	}

	/**
	 * Tests Build method.
	 */	
	@Test
	public void testBuild() {
		manifestBuilder.addResource(JobType.ASSETS, ASSETS_RESOURCE_NAME);
		manifestBuilder.addResource(JobType.CATALOG, CATALOG_RESOURCE_NAME);
		manifestBuilder.addResource(JobType.PRODUCT, PRODUCT_RESOURCE_NAME);
		
		try {
			manifestBuilder.addResource(JobType.PRODUCTASSOCIATION, "it.does.not.matter.xml");
		} catch (EngineRuntimeException e) {
			assertNotNull(e);
		}
		
		Manifest manifest = manifestBuilder.build();
		
		List<String> resources = manifest.getResources();
		assertEquals(CATALOG_RESOURCE_NAME, resources.get(0));
		assertEquals(PRODUCT_RESOURCE_NAME, resources.get(1));
		assertEquals(ASSETS_RESOURCE_NAME, resources.get(2));
	}

}
