/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.domain.contentspace.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.contentspace.ContentWrapper;
import com.elasticpath.domain.contentspace.ContentWrapperLoader;


/**
 * Tests the content wrapper repository to ensure that it functions as intended.
 */
public class ContentWrapperRepositoryImplTest {

	/** Constant for a template name #1. */
	private static final String TEMPLATE1 = "template1";

	/** Constant for a template name #2. */
	private static final String TEMPLATE2 = "template2";
	
	/** Constant for a template name #3. */
	protected static final String TEMPLATE3 = "template3";

	/** Constant for a wrapper Id #1. */
	private static final String CS1 = "cs1";

	/** Constant for a wrapper Id #2. */
	private static final String CS2 = "cs2";
	
	/** Constant for a wrapper Id #3. */
	protected static final String CS3 = "cs3";

	private ContentWrapperRepositoryImpl repository;
	
	/** Used in the conversion to milliseconds. **/
	private static final long MILLI = 1000;
	
	/** The interval that must elapse before a load is done in seconds. **/
	private static final int LOAD_INTERVAL = 10;

	/** Mock content wrapper loader. **/
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private ContentWrapperLoader mockLoader;
	
	/** Mock missing content wrappers cache. **/
	@SuppressWarnings("unchecked")
	private final Map<String, Long> mockMissingContentWrappers = context.mock(Map.class);

	/**
	 * The set up that is required before each test is run.
	 */
	@Before
	public void setUp() {
		
		//Override the getLoadInterval method to return the interval specified in this test clas
		repository = new ContentWrapperRepositoryImpl() {
			@Override
			public int getLoadInterval() {
				return LOAD_INTERVAL;
			}
		};
		
		//Create a mock missed content wrapper cache, and use a mock content wrapper loader for loading
		//of content wrappers
		mockLoader = context.mock(ContentWrapperLoader.class);
		
		//Set the content wrapper repository to use the mock missed cache, and the mock loader
		repository.setMissingContentWrappers(mockMissingContentWrappers);
		repository.setContentWrapperLoader(mockLoader);
	}
	
	/**
	 * Creates a content wrapper Id to content wrapper map for two content wrappers.
	 * @return mapping of content wrapper Id to content wrapper for two objects
	 */
	private Map<String, ContentWrapper> setupTwoContentWrappers() {
		
		//Create two content wrapper objects
		ContentWrapper wrapper1 = new ContentWrapperImpl();
		wrapper1.setWrapperId(CS1);
		wrapper1.setTemplateName(TEMPLATE1);
		ContentWrapper wrapper2 = new ContentWrapperImpl();
		wrapper2.setWrapperId(CS2);
		wrapper2.setTemplateName(TEMPLATE2);
		
		//Add the two content wrapper objects to the map
		Map<String, ContentWrapper> wrappers = new HashMap<String, ContentWrapper>();
		wrappers.put(wrapper1.getWrapperId(), wrapper1);
		wrappers.put(wrapper2.getWrapperId(), wrapper2);
		return wrappers;
	}
	
	/**
	 * Found the content wrapper Id in the cache of previously loaded content wrapper.
	 * Should be removed from the missing content cache if for some reason it is present there.
	 */
	@Test
	public void testFindById() {
		//Create a map that will have two content wrappers within it (with mapped wrapper Id's)
		final Map<String, ContentWrapper> wrappers = setupTwoContentWrappers();
		
		//The mock loader expects to load the content wrappers, which will return the two wrappers
		//we have already set up, the missed cache will attempt to remove the content wrappers 
		//from within the cache as they are not "missing" (this is to ensure that if previously
		//they were recorded missing that they are not still considered missing).
		context.checking(new Expectations() {
			{
				oneOf(mockLoader).loadContentWrappers();
				will(returnValue(wrappers));

				oneOf(mockMissingContentWrappers).remove(CS1);
				oneOf(mockMissingContentWrappers).remove(CS2);
			}
		});

		//Invoke the find content wrapper by Id method to find the wrapper with Id "cs1"
		//and ensure that the returned wrapper contains the correct information
		ContentWrapper returnedWrapper = repository.findContentWrapperById(CS1);
		assertTrue(returnedWrapper.getTemplateName().equals(TEMPLATE1));
		
		//Invoke the find content wrapper by Id method to find the wrapper with Id "cs2"
		//and ensure that the returned wrapper contains the correct information
		returnedWrapper = repository.findContentWrapperById(CS2);
		assertTrue(returnedWrapper.getTemplateName().equals(TEMPLATE2));
		
	}

	
	/**
	 * The content wrapper with the Id is not found in the cache of previously loaded content wrappers.
	 * Should first check if the missing cache contains the Id and if it does then check to see if the
	 * entry is expired. If the both of these conditions are true, then will return a null value.
	 */
	@Test
	public void testFindByIdInMissedCacheNotExpired() {
		//Obtain a map of two content wrappers (cs1 and cs2)
		final Map<String, ContentWrapper> twoWrappers = setupTwoContentWrappers();
		//Ensure that when loading content wrappers, two are loaded
		context.checking(new Expectations() {
			{
				oneOf(mockLoader).loadContentWrappers();
				will(returnValue(twoWrappers));
			}
		});
		
		//The missing cache will contain the key for (cs3) and will be a valid entry, thus marking
		//the content wrapper as missing
		context.checking(new Expectations() {
			{
				oneOf(mockMissingContentWrappers).containsKey(CS3);
				will(returnValue(true));

				oneOf(mockMissingContentWrappers).get(CS3);
				will(returnValue(System.currentTimeMillis() + (LOAD_INTERVAL * MILLI) + 1));
			}
		});
		
		//If a content wrapper is in the missing cache, and the entry is valid then we should be returned
		//a null content wrapper 
		ContentWrapper returnedWrapper = repository.findContentWrapperById(CS3);
		assertNull(returnedWrapper);
	}
	
	/**
	 * The content wrapper with the Id is not found in the cache of previously loaded content wrappers.
	 * Should first check if the missing cache contains the Id and if it does then check to see if the
	 * entry is expired. In the case where the missing cache contains the wrapper Id but the entry has
	 * expired we must reload the content wrappers. If the freshly loaded content wrappers do contain the 
	 * content wrapper Id, then return this wrapper and ensure that if there is a reference to the wrapper 
	 * Id in the missing cache, remove it.
	 */
	@Test
	public void testFindByIdInMissedCacheExpiredFoundOnReload() {
		//Obtain a map of two content wrapper objects
		final Map<String, ContentWrapper> twoWrappers = setupTwoContentWrappers();
		
		//Obtain a map of two content wrapper objects and add a third
		//content wrapper to it (cs3 in addition to cs1 and cs2)
		final Map<String, ContentWrapper> threeWrappers = setupTwoContentWrappers();
		ContentWrapper wrapper3 = new ContentWrapperImpl();
		wrapper3.setWrapperId(CS3);
		wrapper3.setTemplateName(TEMPLATE3);
		threeWrappers.put(wrapper3.getWrapperId(), wrapper3);
		
		//The content wrapper will return two wrappers the first time loadContentWrappers method is called,
		//to simulate when a third content wrapper xml file was not present but on the subsequent call it will
		//return three content wrappers simulating a reload where another file was found
		context.checking(new Expectations() {
			{
		atLeast(1).of(mockLoader).loadContentWrappers();
				will(onConsecutiveCalls(returnValue(twoWrappers), returnValue(threeWrappers)));
			}
		});
		
		//The missed cache should contain the wrapper Id for cs3 but it will be an expired entry, this
		//means that a reload of the content wrappers will have to take place
		context.checking(new Expectations() {
			{
				oneOf(mockMissingContentWrappers).containsKey(CS3);
				will(returnValue(true));

				oneOf(mockMissingContentWrappers).get(CS3);
				will(returnValue(System.currentTimeMillis() - (LOAD_INTERVAL * MILLI) - 1));

				//Since the new content wrapper will be found, the method should be trying to remove the entry
				//if it exists for cs3 from the missed cache
				oneOf(mockMissingContentWrappers).remove(CS3);
			}
		});

		//Invoke the findContentWrapperById method to try to find the content wrapper with Id cs3, since
		//it will be found when the reload occurs it should not be null and contain all of the pertinent
		//information
		ContentWrapper returnedWrapper = repository.findContentWrapperById(CS3);
		assertNotNull(returnedWrapper);
		assertEquals(CS3, returnedWrapper.getWrapperId());
		assertEquals(TEMPLATE3, returnedWrapper.getTemplateName());
	}
	
	/**
	 * The content wrapper with the Id is not found in the cache of previously loaded content wrappers.
	 * Should first check if the missing cache contains the Id and if it does then check to see if the
	 * entry is expired. In the case where the missing cache contains the wrapper Id but the entry has
	 * expired we must reload the content wrappers. If the freshly loaded content wrappers does NOT 
	 * contain the content wrapper Id, then add the wrapper Id to the cache of missing wrappers and return 
	 * a null content wrapper.
	 */
	@Test
	public void testFindByIdInMissedCacheExpiredNotFoundOnReload() {
		//Obtain a map of two content wrapper objects
		final Map<String, ContentWrapper> twoWrappers = setupTwoContentWrappers();
		
		//The mock content wrapper loader should on consecutive calls to the loaderContentWrappers method
		//return both times a map of two content wrappers, simulating a case where no additional content
		//wrapper xml files were found
		context.checking(new Expectations() {
			{
				atLeast(1).of(mockLoader).loadContentWrappers();
				will(onConsecutiveCalls(returnValue(twoWrappers), returnValue(twoWrappers)));
			}
		});
		
		//The missing cache should contain the wrapper Id for the desired content wrapper (cs3) and the 
		//entry should be expired meaning that a reload of the content wrappers should occur
		context.checking(new Expectations() {
			{
				oneOf(mockMissingContentWrappers).containsKey(CS3);
				will(returnValue(true));

				oneOf(mockMissingContentWrappers).get(CS3);
				will(returnValue(System.currentTimeMillis() - (LOAD_INTERVAL * MILLI) - 1));

				//Since the new content wrapper will not be found, the missed cache should insert itself with
				//the content wrapper Id, overriding any old one since it is a map along with a current time stamp (not expired now)
				oneOf(mockMissingContentWrappers).put(with(CS3), with(any(Long.class)));
			}
		});

		//Invoking the findContentWrapperId to find the cs3 content wrapper will return null since the content wrapper was not found
		ContentWrapper returnedWrapper = repository.findContentWrapperById(CS3);
		assertNull(returnedWrapper);
	}
	
	/**
	 * The content wrapper with the Id is not found in the cache of previously loaded content wrappers.
	 * Should first check if the missing cache contains the Id, and if it does then check to see if the
	 * entry is expired. In the case where the missing cache does not contain the content wrapper Id, the
	 * content wrappers are reloaded. If the freshly loaded content wrappers do contain the content wrapper Id,
	 * then return this wrapper and ensure that if there is a reference to the wrapper Id in the missing cache, remove it.
	 */
	@Test
	public void testFindByIdNotInMissedCacheFoundOnReload() {
		//Obtain a map of two content wrapper objects
		final Map<String, ContentWrapper> twoWrappers = setupTwoContentWrappers();
		
		//Obtain a map of two content wrapper objects and add a third
		//content wrapper to it (cs3 in addition to cs1 and cs2)
		final Map<String, ContentWrapper> wrappers = setupTwoContentWrappers();
		ContentWrapper wrapper3 = new ContentWrapperImpl();
		wrapper3.setWrapperId(CS3);
		wrapper3.setTemplateName(TEMPLATE3);
		wrappers.put(wrapper3.getWrapperId(), wrapper3);
		
		//The content wrapper will return two wrappers the first time loadContentWrappers method is called,
		//to simulate when a third content wrapper xml file was not present but on the subsequent call it will
		//return three content wrappers simulating a reload where another file was found
		context.checking(new Expectations() {
			{
		atLeast(1).of(mockLoader).loadContentWrappers();
				will(onConsecutiveCalls(returnValue(twoWrappers), returnValue(wrappers)));
			}
		});
		
		//The missing cache should NOT contain the wrapper Id for the desired content wrapper (cs3), and
		//since the new content wrapper file will be found we try to try to remove any stale references to
		//the content wrapper Id in the missing cache if they exist
		context.checking(new Expectations() {
			{
				oneOf(mockMissingContentWrappers).containsKey(CS3);
				will(returnValue(false));

				oneOf(mockMissingContentWrappers).remove(CS3);
			}
		});

		//Invoke the findContentWrapperById to find the content wrapper Id cs3, since it will be found it
		//should not be null and the information should be the same as what we had put in.
		ContentWrapper returnedWrapper = repository.findContentWrapperById(CS3);
		assertNotNull(returnedWrapper);
		assertEquals(wrapper3.getWrapperId(), returnedWrapper.getWrapperId());
		assertEquals(wrapper3.getTemplateName(), returnedWrapper.getTemplateName());
	}
	
	/**
	 * The content wrapper with the Id is not found in the cache of previously loaded content wrappers.
	 * Should first check if the missing cache contains the Id, and if it does then check to see if the
	 * entry is expired. In the case where the missing cache does not contain the content wrapper Id, the
	 * content wrappers are reloaded. If the freshly loaded content wrappers does NOT contain the content wrapper Id,
	 * then add the wrapper Id to the cache of missing wrappers and return a null content wrapper.
	 */
	@Test
	public void testFindByIdNotInMissedCacheNotFoundOnReload() {
		//Obtain a map of two content wrapper objects
		final Map<String, ContentWrapper> wrappers = setupTwoContentWrappers();

		//The content wrapper will on consecutive calls return two wrappers
		context.checking(new Expectations() {
			{
				atLeast(1).of(mockLoader).loadContentWrappers();
				will(returnValue(wrappers));
			}
		});
		
		//The missing cache will not contain the wrapper Id for cs4 but since that content wrapper will not be found
		//(only cs1 and cs2) will be found, then we must put the cs4 wrapper Id into the missed cache along
		//with the time the load was attempted 
		context.checking(new Expectations() {
			{
				oneOf(mockMissingContentWrappers).containsKey("cs4");
				will(returnValue(false));

				oneOf(mockMissingContentWrappers).put(with("cs4"), with(any(Long.class)));
			}
		});
		//Invoke the findContentWrapperById method that will attempt to find the cs4 wrapper but since
		//this will not be found it will return null
		ContentWrapper returnedWrapper = repository.findContentWrapperById("cs4");
		assertNull(returnedWrapper);
	}

	/**
	 * Tests to ensure that the get method for the content wrappers will return
	 * an empty map of wrapper Ids mapped to content wrappers if there are no content wrappers/null.
	 */
	/*
	 * This test is incorrect since we are dynamicly loading content wrappers
	 * using loaded they are never set manually.
	 * 
	 * public void testGetContentWrappersEmptyMap() {
		
		repository.setContentWrappers(null);
		Map<String, ContentWrapper> wrappers = repository.getContentWrappers();
		assertNull(wrappers);
		
		Map<String, ContentWrapper> newMap = new HashMap<String, ContentWrapper>();
		repository.setContentWrappers(newMap);
		wrappers = repository.getContentWrappers();
		assertEquals(wrappers.size(), 0);
	}*/
	
	/**
	 * Test method for hasLoadingIntervalElapsed, which should only return true if the spell 
	 * checking interval has elapsed since the previous rebuild.
	 */
	@Test
	public void testHasLoadingIntervalElapsed() {
		
		// First call to load/reload, lastLoadTime is 0
		assertTrue("Loading interval should have elapsed.", 
				repository.hasLoadingIntervalElapsed(repository.getLastLoadTime()));
		
		// Second, and immediate, call should not trigger reload since interval has not elapsed
		repository.setLastLoadTime(System.currentTimeMillis());
		assertFalse("Loading interval should not have elapsed.", 
				repository.hasLoadingIntervalElapsed(repository.getLastLoadTime()));
		
		// third delayed call should trigger spell checking rebuild
		long delayedTime = System.currentTimeMillis() - (LOAD_INTERVAL * MILLI) - 1;
		repository.setLastLoadTime(delayedTime);
		assertTrue("Loading interval should have elasped.", 
				repository.hasLoadingIntervalElapsed(repository.getLastLoadTime()));
	}
}
