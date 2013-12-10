package com.elasticpath.smoketests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Properties;

import org.junit.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.TextPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.xml.XmlPage;

/**
 * Test that the applications start up without error.
 */
public class ApplicationStartupTest {
	
	private static final int HTTP_STATUS_OK = 200;

	/**
	 * Test storefront.
	 *
	 * @throws FailingHttpStatusCodeException the failing http status code exception
	 * @throws MalformedURLException the malformed url exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void testStorefront() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		final Properties settings = new Properties();
		settings.load(ApplicationStartupTest.class.getResourceAsStream("/test.properties"));
		final WebClient webClient = new WebClient();
		//use the store code to get to the storefront
		final Page page = webClient.getPage("http://localhost:"
				+ settings.getProperty("cargo.port") + settings.getProperty("storefront.context")
				+ "?storeCode=" + settings.getProperty("store.code"));
		assertEquals(HTTP_STATUS_OK, page.getWebResponse().getStatusCode());
		assertTrue(page instanceof HtmlPage);
		HtmlPage htmlPage = (HtmlPage) page;
		assertTrue(htmlPage.getTitleText().length() != 0);
		webClient.closeAllWindows();
	}

	/**
	 * Test search server.
	 *
	 * @throws FailingHttpStatusCodeException the failing http status code exception
	 * @throws MalformedURLException the malformed url exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void testSearchServer() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		final Properties settings = new Properties();
		settings.load(ApplicationStartupTest.class.getResourceAsStream("/test.properties"));
		final WebClient webClient = new WebClient();
		final Page page = webClient.getPage("http://localhost:"
				+ settings.getProperty("cargo.port") + settings.getProperty("search.context")
				+ "/product/select?q=*:*");
		assertEquals(HTTP_STATUS_OK, page.getWebResponse().getStatusCode());
		assertTrue(page instanceof XmlPage);
		XmlPage xmlPage = (XmlPage) page;
		assertTrue(xmlPage.hasChildNodes());
		webClient.closeAllWindows();
	}

	/**
	 * Test cm server.
	 *
	 * @throws FailingHttpStatusCodeException the failing http status code exception
	 * @throws MalformedURLException the malformed url exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void testCMServer() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		final Properties settings = new Properties();
		settings.load(ApplicationStartupTest.class.getResourceAsStream("/test.properties"));
		final WebClient webClient = new WebClient();
		final Page page = webClient.getPage("http://localhost:"
				+ settings.getProperty("cargo.port") + settings.getProperty("cmserver.context")
				+ "/diagnostics.ep");
		assertEquals(HTTP_STATUS_OK, page.getWebResponse().getStatusCode());
		assertTrue(page instanceof TextPage);
		TextPage textPage = (TextPage) page;
		assertEquals("I am alive!", textPage.getContent());
		webClient.closeAllWindows();
	}

}