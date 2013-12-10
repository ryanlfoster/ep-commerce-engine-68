package com.elasticpath.sfweb.listeners;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.springframework.mock.web.MockHttpServletRequest;

import com.elasticpath.commons.constants.WebConstants;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.sfweb.servlet.facade.HttpServletFacadeFactory;
import com.elasticpath.sfweb.servlet.facade.HttpServletRequestFacade;
import com.elasticpath.sfweb.servlet.facade.impl.HttpServletFacadeFactoryImpl;
import com.elasticpath.sfweb.util.impl.RequestHelperImpl;
import com.elasticpath.tags.Tag;
import com.elasticpath.tags.TagSet;

/**
 * Test for VisitedCategoryTagger.
 */
public class VisitedCategoryTaggerTest {

	private static final String CATEGORIES_VISITED = "CATEGORIES_VISITED";

	private static final String CATEGORY_CODE_1 = "categoryCode1";

	private static final String CATEGORY_CODE_2 = "categoryCode2";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery() {
		{
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};

	private final CustomerSession session = context.mock(CustomerSession.class);

	private TagSet tagSet;

	private MockHttpServletRequest request;

	private HttpServletRequestFacade requestFacade;
	
	private VisitedCategoryTagger listener;

	/**
	 * Setup test.
	 */
	@Before
	public void setUp() throws Exception {
		tagSet = new TagSet();
		request = new MockHttpServletRequest();
		final HttpServletFacadeFactory httpServletFacadeFactory = new HttpServletFacadeFactoryImpl(new RequestHelperImpl(), null, null);
		requestFacade = httpServletFacadeFactory.createRequestFacade(request);
		listener = new VisitedCategoryTagger();
	}

	/**
	 * Test that {@link VisitedCategoryTagger} add a value to tag set.
	 */
	@Test
	public void testVisitedCategoryListenerAddCategoryToTagSet() {

		context.checking(new Expectations() {
			{
				allowing(session).getCustomerTagSet();
				will(returnValue(tagSet));
			}
		});

		request.setParameter(WebConstants.REQUEST_CID, CATEGORY_CODE_1);
		listener.execute(session, requestFacade);
		assertEquals(CATEGORY_CODE_1, tagSet.getTagValue(CATEGORIES_VISITED).getValue());

		request.setParameter(WebConstants.REQUEST_CID, CATEGORY_CODE_2);
		listener.execute(session, requestFacade);

		String tagValue = (String) tagSet.getTagValue(CATEGORIES_VISITED).getValue();

		assertTrue(tagValue.indexOf(CATEGORY_CODE_1) > -1);

		assertTrue(tagValue.indexOf(CATEGORY_CODE_2) > -1);

		assertEquals(2, tagValue.split(",").length);
	}

	/**
	 * Test that {@link VisitedCategoryTagger} not add a value to tag set if request not contains {@link WebConstants}.REQUEST_CID.
	 */
	@Test
	public void testVisitedCategoryListenerNotAddCategoryToTagSet() {

		context.checking(new Expectations() {
			{
				allowing(session).getCustomerTagSet();
				will(returnValue(tagSet));
			}
		});

		request.setParameter("someOtherParam", CATEGORY_CODE_1);
		listener.execute(session, requestFacade);
		assertEquals(0, tagSet.getTags().size());

	}

	/**
	 * Test that {@link VisitedCategoryTagger} doesn't perform unnecessary call to addTag when visited category already in tag set.
	 */
	@Test
	public void testVisitedCategoryDoesNotTriggerEventIfAlreadyInTagSet() {

		final Tag visitedCategoriesTag = context.mock(Tag.class);
		final TagSet tagSet = context.mock(TagSet.class);

		context.checking(new Expectations() {
			{
				allowing(session).getCustomerTagSet();
				will(returnValue(tagSet));
				allowing(tagSet).getTagValue(with(CATEGORIES_VISITED));
				will(onConsecutiveCalls(returnValue(null), returnValue(visitedCategoriesTag)));
				allowing(visitedCategoriesTag).getValue();
				will(returnValue(CATEGORY_CODE_1));
				oneOf(tagSet).addTag(with(any(String.class)), with(any(Tag.class)));
			}
		});

		request.setParameter(WebConstants.REQUEST_CID, CATEGORY_CODE_1);
		listener.execute(session, requestFacade);

		request.setParameter(WebConstants.REQUEST_CID, CATEGORY_CODE_1);
		listener.execute(session, requestFacade);

	}
}
