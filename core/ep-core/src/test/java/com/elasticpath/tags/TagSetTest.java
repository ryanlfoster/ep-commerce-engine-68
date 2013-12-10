package com.elasticpath.tags;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.listeners.TagSetListener;

/**
 * Test for tag set listeners and contained set of tags.
 */
public class TagSetTest  {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	
	/**
	 * Test that the getTags method returns an unmodifiable collection.
	 */
	@Test
	public void testGetTagsUnmodifiable() {
		final TagSet tagSet = new TagSet();
		tagSet.addTag("tag1", new Tag("tag1"));
		
		try {
			tagSet.getTags().put("newTag", new Tag("newTag"));
			fail();
		} catch (UnsupportedOperationException uoe) { // NOPMD
			// pass
		}
	}
	
	/**
	 * Test that shows that listeners of tag set are notified when 
	 * tag set is changed.
	 */
	@Test
	public void testAddTagListenersNotification() {
		final TagSet tagSet = new TagSet();
		final TagSetListener listener1 = context.mock(TagSetListener.class, "l1"); 
		final TagSetListener listener2 = context.mock(TagSetListener.class, "l2"); 
		
		tagSet.addListener(listener1);
		tagSet.addListener(listener2);
		
		final String key = "check";
		final Tag tag = new Tag("listeners");
		
		context.checking(new Expectations() { { 
			oneOf(listener1).onEvent(key, tag);
			oneOf(listener2).onEvent(key, tag);
			oneOf(listener2).onEvent(key, tag);
		} });
		
		// check two
		tagSet.addTag(key, tag);
		
		// check one remaining
		tagSet.removeListener(listener1);
		tagSet.addTag(key, tag);
		
	}
	
	/**
	 * Test that addListener does not add duplicate listeners.
	 */
	@Test
	public void testAddListenerDoesNotAddDumplicates() {
		
		final TagSet tagSet = new TagSet();
		final TagSetListener listener1 = context.mock(TagSetListener.class, "l1"); 
		final TagSetListener listener2 = context.mock(TagSetListener.class, "l2"); 
		
		assertTrue("Listener 1 has to be added initially", tagSet.addListener(listener1));
		assertTrue("Listener 2 has to be added initially", tagSet.addListener(listener2));
		assertFalse("Listener 1 has to be disregarded second time", tagSet.addListener(listener1));
		assertFalse("Listener 2 has to be disregarded second time", tagSet.addListener(listener2));
		
	}
	
	/**
	 * Test that remove listener returns a correct value for the operation.
	 */
	@Test
	public void testRemoveListener() {
		
		final TagSet tagSet = new TagSet();
		final TagSetListener listener1 = context.mock(TagSetListener.class, "l1"); 
		final TagSetListener listener2 = context.mock(TagSetListener.class, "l2"); 
		
		assertTrue("Listener 1 has to be added initially", tagSet.addListener(listener1));
		assertFalse("Listener 2 has has not yet been added", tagSet.removeListener(listener2));
		assertTrue("Listener 1 must be removed", tagSet.removeListener(listener1));
		assertFalse("Listener 1 was already removed", tagSet.removeListener(listener1));
		
	}
	
}
