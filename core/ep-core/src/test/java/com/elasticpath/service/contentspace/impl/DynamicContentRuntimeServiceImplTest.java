package com.elasticpath.service.contentspace.impl;

import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.contentspace.DynamicContent;
import com.elasticpath.domain.sellingcontext.SellingContext;
import com.elasticpath.domain.targetedselling.DynamicContentDelivery;
import com.elasticpath.service.contentspace.DynamicContentResolutionException;
import com.elasticpath.service.sellingcontext.SellingContextRetrievalStrategy;
import com.elasticpath.service.targetedselling.DynamicContentDeliveryService;
import com.elasticpath.service.targetedselling.DynamicContentResolutionAlgorithm;
import com.elasticpath.tags.TagSet;
import com.elasticpath.tags.service.ConditionEvaluatorService;

/**
 * Tests the render mediator to ensure proper functionality. If the render mediator
 * is not able to function it should return a fall back value to the velocity template
 * that is calling it. Also it is the responsibility of this class to call the Renderer and
 * ensure that all parameters have been resolved.
 */
public class DynamicContentRuntimeServiceImplTest  {

	private static final String CONTENT_SPACE_1 = "cs1";

	private DynamicContentRuntimeServiceImpl mockRuntime;
		
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	
	private DynamicContentDeliveryService mockDynamicContentDeliveryService;
	
	private SellingContextRetrievalStrategy mockSellingContextRetrievalStrategy;
	
	private DynamicContentResolutionAlgorithm mockDynamicContentResolutionAlgorithm;
	
	private ConditionEvaluatorService mockConditionEvaluatorService;
	
	private static final TagSet TAG_CLOUD = new TagSet();
	
	/**
	 * Sets up the renderer mediator with a string used for fall back and a string used 
	 * for a successful render values.
	 */
	@Before
	public void setUp() {
		
		mockDynamicContentDeliveryService = context.mock(DynamicContentDeliveryService.class);
		mockSellingContextRetrievalStrategy = context.mock(SellingContextRetrievalStrategy.class);
		mockConditionEvaluatorService = context.mock(ConditionEvaluatorService.class);
		mockDynamicContentResolutionAlgorithm = context.mock(DynamicContentResolutionAlgorithm.class);
		
		mockRuntime = new DynamicContentRuntimeServiceImpl() {

			@Override
			DynamicContentResolutionAlgorithm getActionResolutionAlgorithm() {
				return mockDynamicContentResolutionAlgorithm;
			}

			@Override
			ConditionEvaluatorService getConditionEvaluatorService() {
				return mockConditionEvaluatorService;
			}

			@Override
			DynamicContentDeliveryService getDynamicContentDeliveryService() {
				return mockDynamicContentDeliveryService;
			}

			@Override
			SellingContextRetrievalStrategy getSellingContextRetrievalStrategy() {
				return mockSellingContextRetrievalStrategy;
			}

			
			
		};
		
	}

	/**
	 * tests that if <code>DynamicContentDeliveryService</code> returns a null an 
	 * <code>DynamicContentResolutionException</code> exception is raised.
	 * @throws DynamicContentResolutionException should be thrown
	 */
	@Test(expected = DynamicContentResolutionException.class)
	public void testResolveGetContentSpaceDeliveryNull() throws DynamicContentResolutionException {

		context.checking(new Expectations() { { 
			oneOf(mockDynamicContentDeliveryService).findByContentSpaceName(CONTENT_SPACE_1); will(returnValue(null));
		} });
		
		mockRuntime.resolve(TAG_CLOUD, CONTENT_SPACE_1);
		
	}
	
	/**
	 * tests that if <code>DynamicContentDeliveryService</code> returns an empty list a 
	 * <code>DynamicContentResolutionException</code> exception is raised.
	 * @throws DynamicContentResolutionException should be thrown
	 */
	@Test(expected = DynamicContentResolutionException.class)
	public void testResolveGetContentSpaceDeliveryEmpty() throws DynamicContentResolutionException {
		final List<DynamicContentDelivery> emptyList = new ArrayList<DynamicContentDelivery>();
		
		context.checking(new Expectations() { { 
			oneOf(mockDynamicContentDeliveryService).findByContentSpaceName(CONTENT_SPACE_1); will(returnValue(emptyList));
		} });
		
		mockRuntime.resolve(TAG_CLOUD, CONTENT_SPACE_1);
		
	}
	
	/**
	 * tests that if <code>DynamicContentDeliveryService</code> returns a list of valid items and 
	 * <code>ConditionEvaluatorService</code> evaluates to empty list of satisfactory deliveries 
	 * <code>DynamicContentResolutionException</code> exception is raised.
	 * @throws DynamicContentResolutionException should be thrown
	 */
	@Test(expected = DynamicContentResolutionException.class)
	public void testResolveFindSatisfiedDeliveriesEmpty() throws DynamicContentResolutionException {

		final String sellingContextGuid = "GUID";
		final List<DynamicContentDelivery> validList = new ArrayList<DynamicContentDelivery>();
		final DynamicContentDelivery validDelivery1 = context.mock(DynamicContentDelivery.class, "validDelivery1");
		final SellingContext falseContext1 = context.mock(SellingContext.class, "falseContext1");
		validList.add(validDelivery1);
		
		
		context.checking(new Expectations() { { 
			oneOf(mockDynamicContentDeliveryService).findByContentSpaceName(CONTENT_SPACE_1); will(returnValue(validList));
			allowing(validDelivery1).getSellingContextGuid(); will(returnValue(sellingContextGuid));
			oneOf(mockSellingContextRetrievalStrategy).getByGuid(sellingContextGuid); will(returnValue(falseContext1));
			oneOf(falseContext1).isSatisfied(mockConditionEvaluatorService, TAG_CLOUD); will(returnValue(false));
		} });
		
		mockRuntime.resolve(TAG_CLOUD, CONTENT_SPACE_1);
		
	}
	
	/**
	 * tests that if <code>DynamicContentDeliveryService</code> returns a list of valid items and 
	 * they do not hold a guid to a selling context (i.e. GUID is null) then this will evaluate to true 
	 * and resolve method will return the dynamic content that is held within the delivery.
	 * @throws DynamicContentResolutionException should not be thrown
	 */
	@Test
	public void testResolveFindSatisfiedDeliveriesNullGuid() throws DynamicContentResolutionException {

		final String sellingContextGuid = null;
		final String contentGuid = "GUID";
		final List<DynamicContentDelivery> validList = new ArrayList<DynamicContentDelivery>();
		final DynamicContentDelivery validDelivery1 = context.mock(DynamicContentDelivery.class, "validDelivery1");
		final DynamicContent validContent1 = context.mock(DynamicContent.class, "validContent1");
		validList.add(validDelivery1);
		
		
		context.checking(new Expectations() { { 
			oneOf(mockDynamicContentDeliveryService).findByContentSpaceName(CONTENT_SPACE_1); will(returnValue(validList));
			allowing(validDelivery1).getSellingContextGuid(); will(returnValue(sellingContextGuid));
			allowing(validDelivery1).getPriority(); will(returnValue(0));
			never(mockSellingContextRetrievalStrategy).getByGuid(sellingContextGuid);
			oneOf(mockDynamicContentResolutionAlgorithm).resolveDynamicContent(validList); will(returnValue(validContent1));
			allowing(validContent1).getGuid(); will(returnValue(contentGuid));
		} });
		
		final DynamicContent resultContent = mockRuntime.resolve(TAG_CLOUD, CONTENT_SPACE_1);
		assertSame(validContent1, resultContent);
		
	}
	
	/**
	 * tests that if <code>DynamicContentDeliveryService</code> returns a list of valid items and 
	 * their selling contexts evaluate to true but the resolution algorithms returns null 
	 * an exception is thrown.
	 * @throws DynamicContentResolutionException should be thrown
	 */
	@Test(expected = DynamicContentResolutionException.class)
	public void testResolveFindSatisfiedDeliveriesNotResolved() throws DynamicContentResolutionException {

		final String sellingContextGuid = null;
		final String contentGuid = "GUID";
		final List<DynamicContentDelivery> validList = new ArrayList<DynamicContentDelivery>();
		final DynamicContentDelivery validDelivery1 = context.mock(DynamicContentDelivery.class, "validDelivery1");
		final DynamicContent validContent1 = context.mock(DynamicContent.class, "validContent1");
		validList.add(validDelivery1);
		
		
		context.checking(new Expectations() { { 
			oneOf(mockDynamicContentDeliveryService).findByContentSpaceName(CONTENT_SPACE_1); will(returnValue(validList));
			allowing(validDelivery1).getSellingContextGuid(); will(returnValue(sellingContextGuid));
			allowing(validDelivery1).getPriority(); will(returnValue(0));
			never(mockSellingContextRetrievalStrategy).getByGuid(sellingContextGuid);
			oneOf(mockDynamicContentResolutionAlgorithm).resolveDynamicContent(validList); will(returnValue(null));
			allowing(validContent1).getGuid(); will(returnValue(contentGuid));
		} });
		
		mockRuntime.resolve(TAG_CLOUD, CONTENT_SPACE_1);
		
	}
	
}
