/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.web.controller.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import javax.servlet.http.HttpServletResponse;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.owasp.esapi.ESAPI;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.util.impl.ClassLoaderUtils;
import com.elasticpath.domain.misc.ImageRenderRequest;
import com.elasticpath.domain.misc.ImageRenderResponse;
import com.elasticpath.domain.misc.impl.ImageRenderRequestImpl;
import com.elasticpath.service.misc.ImageService;
import com.elasticpath.sfweb.util.SfRequestHelper;
import com.elasticpath.sfweb.util.impl.RequestHelperImpl;

/**
 * Test that the image controller handles requests correctly and securely.
 */
@SuppressWarnings({"serial" })
public class ImageControllerImplTest {
	
	private static final String INVALID_FILE_NAME = "images" + File.separator + "invalid-file.jpg";

	private static final String JPEG = "jpeg";

	private static final String IMAGE_JPEG = "image/jpeg";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private ImageControllerHelper imageControllerHelper;
	private MockHttpServletRequest request;
	private MockHttpServletResponse response;
	private ImageService imageService;
	private BeanFactory beanFactory;
	private ImageRenderRequest imageRenderRequest;
	private SfRequestHelper requestHelper;
	private ImageRenderResponse imageRenderResponse;
	private RenderedImage renderedImage;

	/**
	 * Reset ESAPI before running tests so that it finds correct ESAPI.properties file.
	 * 
	 * @throws MalformedURLException in case of error forming URL of security path
	 */
	@BeforeClass
	public static void resetESAPI() throws MalformedURLException {
		File securityFolder = new File("WEB-INF/security");
		if (!securityFolder.exists()) {
			securityFolder = new File("com.elasticpath.sf/WEB-INF/security");
		}
		ClassLoaderUtils.addURL(securityFolder.toURI().toURL());
		ESAPI.securityConfiguration().setResourceDirectory(securityFolder.getAbsolutePath());
	}
	
	/**
	 * Setup required for every test.
	 * 
	 * @throws java.lang.Exception in case of errors during setup
	 */
	@Before
	public void setUp() throws Exception {
		context.setImposteriser(ClassImposteriser.INSTANCE);
		// Objects required by the tests
		imageService = context.mock(ImageService.class);
		imageRenderResponse = context.mock(ImageRenderResponse.class);
		renderedImage = context.mock(RenderedImage.class);
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		imageRenderRequest = new ImageRenderRequestImpl();
		requestHelper = new RequestHelperImpl();
		beanFactory = context.mock(BeanFactory.class);

		// Set up the class under test
		imageControllerHelper = new ImageControllerHelper() {
			@Override
			protected void encodeImage(final HttpServletResponse response, final RenderedImage scaledImage, 
					final String imageType) throws IOException {
				
				// Do nothing, we don't want JAI involved in a unit test
			}
		};
		imageControllerHelper.setImageService(imageService);
		imageControllerHelper.setBeanFactory(beanFactory);

		// Common expectations
		context.checking(new Expectations() {
			{
				allowing(imageService).getImagePath(); will(returnValue("/"));
				allowing(beanFactory).getBean(ContextIdNames.IMAGE_RENDER_REQUEST); will(returnValue(imageRenderRequest));
			}
		});

	}

	/**
	 * Test a valid request gives the correct response.
	 */
	@Test
	public final void testValidImageRequest() {
		final String validFileName = File.separator + "images" + File.separator + "validImage.jpg";
		context.checking(new Expectations() {
			{
				oneOf(imageService).render(imageRenderRequest); will(returnValue(imageRenderResponse));
				oneOf(imageRenderResponse).getScaledImage(); will(returnValue(renderedImage));
				oneOf(imageRenderResponse).getMimeType(); will(returnValue(IMAGE_JPEG));
				oneOf(imageRenderResponse).getImageType(); will(returnValue(JPEG));
				atLeast(1).of(imageRenderResponse).getFileName(); will(returnValue(validFileName));
			}
		});
		request.setParameter("imageName", validFileName);
		try {
			imageControllerHelper.handleRequest(request, response, requestHelper, "");
		} catch (Exception e) {
			fail("Unexpected exception: " + e);
		}

		// We do not want the path in the headers because this exposes internal information about the file layout of the server.
		assertEquals("Content-disposition header should contain the file name only", "inline; filename=validImage.jpg",
				response.getHeader("Content-disposition"));
		assertEquals("Response content type should be as expected", IMAGE_JPEG, response.getContentType());
	}

	/**
	 * Test that a request with a blank name acts accordingly.
	 */
	@Test
	public final void testMissingImageName() {
		context.checking(new Expectations() {
			{
				oneOf(imageService).render(imageRenderRequest); will(returnValue(imageRenderResponse));
				oneOf(imageRenderResponse).getScaledImage(); will(returnValue(renderedImage));
				oneOf(imageRenderResponse).getMimeType(); will(returnValue(IMAGE_JPEG));
				oneOf(imageRenderResponse).getImageType(); will(returnValue(JPEG));
				atLeast(1).of(imageRenderResponse).getFileName(); will(returnValue(INVALID_FILE_NAME));
			}
		});
		request.setParameter("imageName", "");
		try {
			imageControllerHelper.handleRequest(request, response, requestHelper, "");
		} catch (Exception e) {
			fail("Unexpected exception: " + e);
		}
		assertEquals("Content-disposition header should contain the invalid file name only", "inline; filename=invalid-file.jpg",
				response.getHeader("Content-disposition"));
		assertEquals("Response content type should be as expected", IMAGE_JPEG, response.getContentType());
	}

	/**
	 * Test whether a response header splitting exploit will end up on the header.
	 */
	@Test
	public final void testHeaderSplittingImageName() {
		final String headerSplittingFileName = "0D%0AContent-Type%3A%20text%2Fhtml%0D%0AContent-Length%3A%2029%"
						+	"0D%0A%3Cscript%3Ealert%28%22XSS%22%29%3C%2Fscript%3E%0D%0A%0D%0AHTTP%2F1.x%20200%20OK%0D%0A"
						+ "Server%3A%20Apache-Coyote%2F1.1%0D%0AContent-Type%3A%20image%2Fjpg%0D%0ATransfer-Encoding%3A%20chunked%0D%0A"
						+ "Date%3A%20Thu%2C%2023%20Jul%202009%2022%3A36%3A17%20GMT%0D%0A%0D%0A";
		context.checking(new Expectations() {
			{
				oneOf(imageService).render(imageRenderRequest); will(returnValue(imageRenderResponse));
				oneOf(imageRenderResponse).getScaledImage(); will(returnValue(renderedImage));
				oneOf(imageRenderResponse).getMimeType(); will(returnValue(IMAGE_JPEG));
				oneOf(imageRenderResponse).getImageType(); will(returnValue(JPEG));
				atLeast(1).of(imageRenderResponse).getFileName(); will(returnValue(INVALID_FILE_NAME));
			}
		});
		request.setParameter("imageName", headerSplittingFileName);
		try {
			imageControllerHelper.handleRequest(request, response, requestHelper, "");
		} catch (Exception e) {
			fail("Unexpected exception: " + e);
		}
		assertEquals("Content-disposition header should not contain the invalid file name", "inline; filename=invalid-file.jpg",
				response.getHeader("Content-disposition"));
	}

}
