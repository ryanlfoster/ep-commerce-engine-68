package com.elasticpath.search.index.grouper.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.search.index.pipeline.IndexingStage;
import com.elasticpath.search.index.pipeline.stats.PipelinePerformance;

/**
 * Verifies that grouping search indices functionality. 
 */
public class IndexGrouperTest {
	private static final int DEFAULT_SIZE = 4;

	private static final int DEFAULT_GROUP_SIZE = 4;

	private static final int UNEVEN_GROUP_SIZE = 3;
	
    @Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	
    @SuppressWarnings("unchecked")
	private final IndexingStage<Set<Long>, ?> nextStage = context.mock(IndexingStage.class);
    
    private final PipelinePerformance performance = context.mock(PipelinePerformance.class);
    
	private UidGroupingTaskImpl indexGrouper;
	
	/**
	 * Initialize the {@link UidGroupingTaskImpl} for testing.
	 */
	@Before
	public void setUpIndexGrouper() {
		indexGrouper = new UidGroupingTaskImpl();
		indexGrouper.setPipelinePerformance(performance);
	}
	
	/**
	 * Test grouping when no next stage was set.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testGroupingWithNoNextStage() {
		indexGrouper.setUids(getIndexList(DEFAULT_SIZE));
		indexGrouper.setGroupSize(DEFAULT_GROUP_SIZE);
		indexGrouper.run();
	}
	
	/**
	 * Test grouping when group size less than one.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testGroupingWithNoGroupSize() {
		indexGrouper.setGroupSize(0);
	}

	/**
	 * Test grouping with batch size equal to index list size.
	 */
	@Test
	public void testGroupingWithEqualBatchAndListSize() {
		final Set<Long> uidsToLoad = getIndexList(DEFAULT_SIZE);

		context.checking(new Expectations() { {
			oneOf(nextStage).send(with(uidsToLoad));
			allowing(performance).addCount(with(any(String.class)), with(any(Long.class)));
			allowing(performance).addValue(with(any(String.class)), with(any(double.class)));
		} });

		indexGrouper.setUids(uidsToLoad);
		indexGrouper.setNextStage(nextStage);
		indexGrouper.setGroupSize(DEFAULT_GROUP_SIZE);
		indexGrouper.run();	
	}

	/**
	 * Test grouping with a different batch and index list size.
	 */
	@Test
	public void testGroupingWithUnequalGroupAndListSize() {
		final Set<Long> uidsToLoad = getIndexList(DEFAULT_SIZE);

		context.checking(new Expectations() { {
			exactly(2).of(nextStage).send(with(uidsSubSetMatcher(uidsToLoad)));
			allowing(performance).addCount(with(any(String.class)), with(any(Long.class)));
			allowing(performance).addValue(with(any(String.class)), with(any(double.class)));
		} });
		
		indexGrouper.setUids(uidsToLoad);
		indexGrouper.setNextStage(nextStage);
		indexGrouper.setGroupSize(UNEVEN_GROUP_SIZE);
		indexGrouper.run();	
	}

	/**
	 * Test grouping with a group size of one.
	 */
	@Test
	public void testGroupingWithGroupSizeOfOne() {
		final Set<Long> uidsToLoad = getIndexList(DEFAULT_SIZE);

		context.checking(new Expectations() { {
			for (Long uid : uidsToLoad) {
				oneOf(nextStage).send(with(Collections.<Long> singleton(uid)));
			}

			allowing(performance).addCount(with(any(String.class)), with(any(Long.class)));
			allowing(performance).addValue(with(any(String.class)), with(any(double.class)));
		} });
		
		indexGrouper.setUids(uidsToLoad);
		indexGrouper.setNextStage(nextStage);
		indexGrouper.setGroupSize(1);
		indexGrouper.run();
	}

	private Set<Long> getIndexList(final int size) {
		final Set<Long> indexList = new HashSet<Long>();
		for (long index = 0; index < size; ++index) {
			indexList.add(index);
		}
		return indexList;
	}

	/**
	 * A matcher that confirms that a given Set of UIDs contains only values from a Set of allowed values.
	 *
	 * @param allowedValues the master {@code Set} of allowed values
	 * @return a {@link Matcher} that can test a given Set to determine if it is made up exclusively of allowed values
	 */
	private Matcher<Set<Long>> uidsSubSetMatcher(final Set<Long> allowedValues) {
		return new TypeSafeMatcher<Set<Long>>() {

			@Override
			protected boolean matchesSafely(final Set<Long> items) {
				return allowedValues.containsAll(items);
			}

			@Override
			public void describeTo(final Description description) {
				description.appendText("Expected elements in the allowed values collection: [").appendValue(allowedValues).appendText("]");
			}
		};
	}

}
