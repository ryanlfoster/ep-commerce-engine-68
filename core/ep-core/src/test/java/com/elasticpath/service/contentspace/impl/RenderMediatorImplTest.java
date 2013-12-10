package com.elasticpath.service.contentspace.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.constants.WebConstants;
import com.elasticpath.domain.contentspace.ContentWrapper;
import com.elasticpath.domain.contentspace.ContentWrapperRepository;
import com.elasticpath.domain.contentspace.DynamicContent;
import com.elasticpath.domain.contentspace.ParameterValue;
import com.elasticpath.domain.contentspace.impl.DynamicContentImpl;
import com.elasticpath.service.contentspace.DynamicContentResolutionException;
import com.elasticpath.service.contentspace.DynamicContentRuntimeService;
import com.elasticpath.service.contentspace.ParameterValueResolver;
import com.elasticpath.service.contentspace.RenderContext;
import com.elasticpath.service.contentspace.RenderMediatorException;
import com.elasticpath.service.contentspace.Renderer;
import com.elasticpath.tags.TagSet;

/**
 * Tests the render mediator to ensure proper functionality. If the render mediator
 * is not able to function it should return a fall back value to the velocity template
 * that is calling it. Also it is the responsibility of this class to call the Renderer and
 * ensure that all parameters have been resolved.
 */
public class RenderMediatorImplTest  {

	private static final String CONTENT_SPACE_1 = "cs1";

	private RenderMediatorImpl mediator;
	
	private static final String CONTENT_WRAPPER_ID = "CS1";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private final ContentWrapperRepository mockContentWrapperRepository = context.mock(ContentWrapperRepository.class);
	private final ParameterValueResolver parameterValueResolver = context.mock(ParameterValueResolver.class);
	private final DynamicContentRuntimeService dynamicContentRuntimeService = context.mock(DynamicContentRuntimeService.class);
	private final Renderer renderer = context.mock(Renderer.class);
	private final ContentWrapper contentWrapper = context.mock(ContentWrapper.class);
	/**
	 * Sets up the renderer mediator with a string used for fall back and a string used 
	 * for a successful render values.
	 */
	@Before
	public void setUp() {
		mediator = new RenderMediatorImpl();
		mediator.setContentWrapperRepository(mockContentWrapperRepository);
		mediator.setParameterValueResolver(parameterValueResolver);
		mediator.setDynamicContentRuntimeService(dynamicContentRuntimeService);
		mediator.setRenderer(renderer);

		context.checking(new Expectations() {
			{
				allowing(mockContentWrapperRepository).findContentWrapperById(CONTENT_WRAPPER_ID);
				will(returnValue(contentWrapper));
			}
		});
	}

	/**
	 * tests that is provided with a null for content wrapper id the method raises exception.
	 *
	 * @throws RenderMediatorException should throw exception
	 */
	@Test(expected = RenderMediatorException.class)
	public void testGetContentWrapperFromRepositoryByIdNull() throws RenderMediatorException {
		context.checking(new Expectations() { { // performance check
			never(mockContentWrapperRepository).findContentWrapperById(null);
		} });
		mediator.getContentWrapperFromRepositoryById(CONTENT_SPACE_1, null);
	}
	
	/**
	 * tests that is provided with a valid content wrapper id the method and wrapper with that 
	 * id is not present in the repository then exception is raised.
	 *
	 * @throws RenderMediatorException should throw exception
	 */
	@Test(expected = RenderMediatorException.class)
	public void testGetContentWrapperFromRepositoryByIdNoWrapper() throws RenderMediatorException {
		final String nonExistentContentWrapperId = "notFound";
		context.checking(new Expectations() { { 
			oneOf(mockContentWrapperRepository).findContentWrapperById(nonExistentContentWrapperId); will(returnValue(null));
		} });

		mediator.getContentWrapperFromRepositoryById(CONTENT_SPACE_1, nonExistentContentWrapperId);
	}

	/**
	 * tests that is provided with a valid content wrapper id and if such wrapper exists in repository
	 * no exception is raised and DC is returned.
	 *
	 * @throws RenderMediatorException should throw exception
	 */
	@Test
	public void testGetContentWrapperFromRepositoryById() throws RenderMediatorException {
		final ContentWrapper mockCwResult = mediator.getContentWrapperFromRepositoryById(CONTENT_SPACE_1, CONTENT_WRAPPER_ID);
		
		assertNotNull(mockCwResult);
		assertEquals(contentWrapper, mockCwResult);
	}

	@Test
	public void ensureRenderPassesLocaleToParameterValueResolver() throws Exception {
		final Locale locale = Locale.CANADA_FRENCH;

		final List<ParameterValue> parameterValues = Collections.emptyList();
		final Map<String, Object> contextMap = Collections.singletonMap(WebConstants.CUSTOMER_SESSION, null);

		final String contentSpaceName = "foo";

		stubDynamicContent(contentSpaceName, parameterValues);

		context.checking(new Expectations() {
			{
				allowing(renderer).doRender(with(any(String.class)), with(any(RenderContext.class)));

				oneOf(parameterValueResolver).resolveValues(contentWrapper, parameterValues, contextMap, locale.toString());
			}
		});

		final String renderedOutput = mediator.render(contentSpaceName, locale);

		assertNotEquals("Should not result in <<error>>", RenderMediatorImpl.SIGNAL_ERROR, renderedOutput);
	}

	@Test
	public void ensureRenderReturnsErrorStringWhenLocaleNull() throws DynamicContentResolutionException {
		final String contentSpaceName = "foo";
		stubDynamicContent(contentSpaceName, Collections.<ParameterValue>emptyList());

		assertEquals(RenderMediatorImpl.SIGNAL_ERROR, mediator.render("foo", null));
	}

	private DynamicContent stubDynamicContent(final String contentSpaceName, final List<ParameterValue> parameterValues)
			throws DynamicContentResolutionException {
		final DynamicContent dynamicContent = new DynamicContentImpl();
		dynamicContent.setContentWrapperId(CONTENT_WRAPPER_ID);
		dynamicContent.setParameterValues(parameterValues);

		context.checking(new Expectations() {
			{
				final TagSet nonExistentTagSet = null;
				allowing(dynamicContentRuntimeService).resolve(nonExistentTagSet, contentSpaceName);
				will(returnValue(dynamicContent));
			}
		});

		return dynamicContent;
	}

}
