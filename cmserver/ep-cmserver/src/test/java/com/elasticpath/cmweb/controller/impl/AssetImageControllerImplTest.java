package com.elasticpath.cmweb.controller.impl;

import static org.junit.Assert.assertEquals;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.commons.util.AssetRepository;
import com.elasticpath.service.misc.ImageService;

/**
 * The junit test class for AssetImageControllerImpl.
 */
public class AssetImageControllerImplTest {

	private AssetImageControllerImpl assetImageControllerImpl;
	
	private ImageService imageService;
	private AssetRepository assetRepository;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	/**
	 * Setup method.
	 */
	@Before
	public void setUp() {
		assetImageControllerImpl = new AssetImageControllerImpl();
	
		imageService = context.mock(ImageService.class);
		assetImageControllerImpl.setImageService(imageService);
		
		assetRepository = context.mock(AssetRepository.class);
		assetImageControllerImpl.setAssetRepository(assetRepository);
	}
	
	/**
	 * Test get path prefix for an empty sub-folder.
	 */
	@Test
	public void testGetPathPrefixEmptySubFolder() {
		context.checking(new Expectations() {
			{
				oneOf(imageService).getImagePath(); 
				will(returnValue("imageFolder"));
				
				never(assetRepository).getCatalogAssetPath();
			}
		});
		
		String filePathPrefix = assetImageControllerImpl.getPathPrefixForSubFolder(null);
		
		assertEquals("imageFolder", StringUtils.replace(filePathPrefix, "\\", "/"));
	}

	/**
	 * Test get path prefix for a non-empty sub-folder.
	 */
	@Test
	public void testGetPathPrefix() {
		context.checking(new Expectations() {
			{
				never(imageService).getImagePath(); 

				oneOf(assetRepository).getCatalogAssetPath();
				will(returnValue("assetFolder"));
			}
		});
		String filePathPrefix = assetImageControllerImpl.getPathPrefixForSubFolder("dynamiccontent");
		assertEquals("assetFolder/dynamiccontent", StringUtils.replace(filePathPrefix, "\\", "/"));
	}
	
}
