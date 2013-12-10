package com.elasticpath.sfweb.controller.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.impl.CategoryImpl;
import com.elasticpath.domain.catalogview.CategoryFilter;
import com.elasticpath.domain.catalogview.EpCatalogViewRequestBindException;
import com.elasticpath.domain.catalogview.Filter;
import com.elasticpath.domain.catalogview.browsing.BrowsingRequest;
import com.elasticpath.domain.catalogview.browsing.impl.BrowsingRequestImpl;
import com.elasticpath.domain.catalogview.impl.CategoryFilterImpl;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.customer.impl.CustomerSessionImpl;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.impl.ShoppingCartImpl;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.impl.StoreImpl;
import com.elasticpath.sellingchannel.ProductUnavailableException;
import com.elasticpath.sfweb.util.SeoUrlValidator;
import com.elasticpath.sfweb.util.SfRequestHelper;
import com.elasticpath.sfweb.util.impl.RequestHelperImpl;

/**
 * Test that the browsing controller works as expected.
 */
public class BrowsingControllerImplTest {

	private static final long TEST_CATEGORY_ID = 15;

	/**
	 * Test that the browsing request is correctly populated with the category id when a non-seo url is used.
	 * @throws IOException - checked exception
	 */
	@Test
	public void testPopulateBrowsingRequestWithCategoryNonSeoRequest() throws IOException {

		MockHttpServletRequest request = new MockHttpServletRequest();
		ShoppingCart shoppingCart = new ShoppingCartForTest();

		BrowsingControllerImpl controller = new BrowsingControllerWithTestOverrides() {

			@Override
			protected long getCategoryUid(final HttpServletRequest request) {
				return TEST_CATEGORY_ID;
			}
		};
		BrowsingRequest browsingRequest = controller.populateBrowsingRequest(request, null, shoppingCart);
		assertEquals("Category id not set to input id", TEST_CATEGORY_ID, browsingRequest.getCategoryUid());
	}

	/**
	 * Test that the browsing request is correctly populated with the category id when a seo url is used.
	 * @throws IOException - checked exception
	 */
	@Test
	public void testPopulateBrowsingRequestWithCategorySeoRequest() throws IOException {

		MockHttpServletRequest request = new MockHttpServletRequest();
		ShoppingCart shoppingCart = new ShoppingCartForTest();

		BrowsingControllerImpl controller = new BrowsingControllerWithTestOverridesWithCategoryFilter() {

			@Override
			protected long getCategoryUid(final HttpServletRequest request) {
				throw new ProductUnavailableException("Pretending that category id not specified in non-seo fashion");
			}

			@Override
			protected String getSeoRequestFilters(final HttpServletRequest request) {
				return "c15";
			}
		};

		controller.setSeoUrlValidator(new SameResponseSeoUrlValidator(true));

		BrowsingRequest browsingRequest = controller.populateBrowsingRequest(request, null, shoppingCart);
		assertEquals("Category id not set to input id", TEST_CATEGORY_ID, browsingRequest.getCategoryUid());
	}

	/**
	 * Test that the request is rejected if no category id can be found from the seo and non-seo request information.
	 * @throws IOException - checked exception
	 */
	@Test(expected = ProductUnavailableException.class)
	public void testPopulateBrowsingRequestWithoutCategoryId() throws IOException {

		MockHttpServletRequest request = new MockHttpServletRequest();
		ShoppingCart shoppingCart = new ShoppingCartForTest();

		BrowsingControllerImpl controller = new BrowsingControllerWithTestOverrides() {
			@Override
			protected long getCategoryUid(final HttpServletRequest request) {
				throw new ProductUnavailableException("Pretending that category id not specified in non-seo fashion");
			}

			@Override
			protected String getSeoRequestFilters(final HttpServletRequest request) {
				return null;
			}
		};

		controller.populateBrowsingRequest(request, null, shoppingCart);
		// should fail with exception
	}

	/**
	 * Test that the response is correct set as "page not found" when the seo url validator determines that the requested url is invalid.
	 * @throws IOException - checked exception
	 */
	@Test
	public void testPopulateBrowsingRequestHonoursSeoUrlValidation() throws IOException {

		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();

		ShoppingCart shoppingCart = new ShoppingCartForTest();

		BrowsingControllerImpl controller = new BrowsingControllerWithTestOverridesWithCategoryFilter() {

			@Override
			protected long getCategoryUid(final HttpServletRequest request) {
				throw new ProductUnavailableException("Pretending that category id not specified in non-seo fashion");
			}

			@Override
			protected String getSeoRequestFilters(final HttpServletRequest request) {
				// Force controller into processing an seo url.
				return "c15";
			}
		};

		controller.setSeoUrlValidator(new SameResponseSeoUrlValidator(false));

		assertNull("No BrowsingRequest should be returned", controller.populateBrowsingRequest(request, response, shoppingCart));
		assertEquals("Response should be 'page not found'", HttpServletResponse.SC_NOT_FOUND, response.getStatus());
	}

	/**
	 * Mocked seo url validator.
	 */
	private static class SameResponseSeoUrlValidator implements SeoUrlValidator {
		private final boolean response;

		public SameResponseSeoUrlValidator(final boolean response) {
			this.response = response;
		}

		public boolean validateCategoryUrl(final Category category, final List<Filter< ? >> filters, final Locale locale,
				final HttpServletRequest request) {
			return response;
		}

		public boolean validateProductUrl(final Product product, final Locale locale, final HttpServletRequest request) {
			return response;
		}

		public boolean validateSitemapUrl(final Category category, final Brand brand, final Locale locale, final HttpServletRequest request) {
			return response;
		}
	}

	/**
	 * Simple shopping cart extension to reduce coupling during testing.
	 */
	private static class ShoppingCartForTest extends ShoppingCartImpl {
		private static final long serialVersionUID = -7631232033417529641L;

		@Override
		public CustomerSession getCustomerSession() {
			return new CustomerSessionImpl() {
				private static final long serialVersionUID = 3743062074728921730L;

				public java.util.Currency getCurrency() {
					return Currency.getInstance("USD");
				}
			};
		}

		@Override
		public Locale getLocale() {
			return Locale.getDefault();
		}
	}

	/**
	 * Overridden browsing controller that returns browsing request, request helper and store.
	 */
	private static class BrowsingControllerWithTestOverrides extends BrowsingControllerImpl {

		@SuppressWarnings("unchecked")
		@Override
		protected <T> T getBean(final String beanName) {
			return (T) new BrowsingRequestImpl();
		}

		@Override
		public SfRequestHelper getRequestHelper() {
			return new RequestHelperImpl();
		}

		@Override
		Store getStoreFromRequest() {
			return new StoreImpl();
		}
	}

	/**
	 * Overridden browsing controller that returns browsing request, request helper, store and category filter stub.
	 */
	private static class BrowsingControllerWithTestOverridesWithCategoryFilter extends BrowsingControllerWithTestOverrides {

		@SuppressWarnings("unchecked")
		@Override
		protected <T> T getBean(final String beanName) {
			return (T) new BrowsingRequestImplTest();
		}

	}

	/**
	 * Browsing request with overridden category filter return.
	 */
	private static class BrowsingRequestImplTest extends BrowsingRequestImpl {
		private static final long serialVersionUID = 6414662858194658160L;

		@Override
		public void setFiltersIdStr(final String filtersIdStr, final Store store) throws EpCatalogViewRequestBindException {
			// do nothing;
		}

		@Override
		public List<Filter< ? >> getFilters() {
			Category category = new CategoryImpl();
			category.setUidPk(TEST_CATEGORY_ID);
			CategoryFilter filter = new CategoryFilterImpl();
			filter.setCategory(category);
			return Arrays.<Filter< ? >> asList(filter);
		}
	}
}

