package com.elasticpath.domain.catalogview.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.domain.catalogview.EpCatalogViewRequestBindException;

/**
 * Test case for {@link AbstractRangeFilterImpl}.
 */
public class AbstractRangeFilterImplTest {
	
	private TestRangeFilter abstractRangeFilterImpl;

	@Before
	public void setUp() throws Exception {
		abstractRangeFilterImpl = new TestRangeFilter();
	}
	
	/**
	 * Test method for
	 * {@link AbstractRangeFilterImpl#contains(com.elasticpath.domain.catalogview.RangeFilter)}.
	 */
	@Test
	public void testContains() {
		TestRangeFilter other = new TestRangeFilter();
		
		final BigDecimal zero = BigDecimal.ZERO;
		final BigDecimal one = BigDecimal.ONE;
		final BigDecimal ten = BigDecimal.TEN;
		final BigDecimal eleven = new BigDecimal(11);
		
		// this one contains the other
		abstractRangeFilterImpl.setLowerValue(zero);
		other.setLowerValue(one);
		abstractRangeFilterImpl.setUpperValue(eleven);
		other.setUpperValue(ten);
		assertTrue(abstractRangeFilterImpl.contains(other));
		assertFalse(other.contains(abstractRangeFilterImpl));
		
		// they overlap - this one is bigger
		abstractRangeFilterImpl.setLowerValue(one);
		other.setLowerValue(zero);
		abstractRangeFilterImpl.setUpperValue(eleven);
		other.setUpperValue(ten);
		assertFalse(abstractRangeFilterImpl.contains(other));
		assertFalse(other.contains(abstractRangeFilterImpl));
		
		// they overlap - other one is bigger
		abstractRangeFilterImpl.setLowerValue(zero);
		other.setLowerValue(one);
		abstractRangeFilterImpl.setUpperValue(ten);
		other.setUpperValue(eleven);
		assertFalse(abstractRangeFilterImpl.contains(other));
		assertFalse(other.contains(abstractRangeFilterImpl));
		
		// same range
		abstractRangeFilterImpl.setLowerValue(zero);
		other.setLowerValue(zero);
		abstractRangeFilterImpl.setUpperValue(ten);
		other.setUpperValue(ten);
		assertTrue(abstractRangeFilterImpl.contains(other));
		assertTrue(other.contains(abstractRangeFilterImpl));
		
		// other has no lower bound
		abstractRangeFilterImpl.setLowerValue(zero);
		other.setLowerValue(zero);
		abstractRangeFilterImpl.setUpperValue(ten);
		other.setUpperValue(ten);
		assertTrue(abstractRangeFilterImpl.contains(other));
		assertTrue(other.contains(abstractRangeFilterImpl));
	}
	
	/**
	 * Test method for {@link AbstractRangeFilterImpl#compareTo(AbstractRangeFilterImpl)}.
	 */
	@Test
	public void testCompareTo() {
		TestRangeFilter other = new TestRangeFilter();
		
		final BigDecimal zero = BigDecimal.ZERO;
		final BigDecimal one = BigDecimal.ONE;
		final BigDecimal ten = BigDecimal.TEN;
		final BigDecimal eleven = new BigDecimal(11);
		
		// compare the same range
		abstractRangeFilterImpl.setLowerValue(zero);
		other.setLowerValue(zero);
		abstractRangeFilterImpl.setUpperValue(ten);
		other.setUpperValue(ten);
		assertEquals(0, abstractRangeFilterImpl.compareTo(other));
		assertEquals(0, other.compareTo(abstractRangeFilterImpl));
		
		// overlap - this filter less than other
		abstractRangeFilterImpl.setLowerValue(zero);
		other.setLowerValue(one);
		abstractRangeFilterImpl.setUpperValue(ten);
		other.setUpperValue(eleven);
		assertTrue(abstractRangeFilterImpl.compareTo(other) < 0);
		assertTrue(other.compareTo(abstractRangeFilterImpl) > 0);
		
		// overlap - this filter greater than other
		abstractRangeFilterImpl.setLowerValue(one);
		other.setLowerValue(zero);
		abstractRangeFilterImpl.setUpperValue(eleven);
		other.setUpperValue(ten);
		assertTrue(abstractRangeFilterImpl.compareTo(other) > 0);
		assertTrue(other.compareTo(abstractRangeFilterImpl) < 0);
		
		// this contained within the other, priceFilter is the bigger range
		abstractRangeFilterImpl.setLowerValue(zero);
		other.setLowerValue(one);
		abstractRangeFilterImpl.setUpperValue(eleven);
		other.setUpperValue(ten);
		assertTrue(abstractRangeFilterImpl.compareTo(other) > 0);
		assertTrue(other.compareTo(abstractRangeFilterImpl) < 0);
		
		// this contained within the other, other is the bigger range
		abstractRangeFilterImpl.setLowerValue(one);
		other.setLowerValue(zero);
		abstractRangeFilterImpl.setUpperValue(ten);
		other.setUpperValue(eleven);
		assertTrue(abstractRangeFilterImpl.compareTo(other) < 0);
		assertTrue(other.compareTo(abstractRangeFilterImpl) > 0);
		
		// test same lower bound - this filter smaller than other
		abstractRangeFilterImpl.setLowerValue(zero);
		other.setLowerValue(zero);
		abstractRangeFilterImpl.setUpperValue(ten);
		other.setUpperValue(eleven);
		assertTrue(abstractRangeFilterImpl.compareTo(other) < 0);
		assertTrue(other.compareTo(abstractRangeFilterImpl) > 0);
		
		// test same lower bound - this filter bigger than other
		abstractRangeFilterImpl.setLowerValue(zero);
		other.setLowerValue(zero);
		abstractRangeFilterImpl.setUpperValue(eleven);
		other.setUpperValue(ten);
		assertTrue(abstractRangeFilterImpl.compareTo(other) > 0);
		assertTrue(other.compareTo(abstractRangeFilterImpl) < 0);
		
		// other has no lower bound
		abstractRangeFilterImpl.setLowerValue(zero);
		other.setLowerValue(null);
		abstractRangeFilterImpl.setUpperValue(ten);
		other.setUpperValue(ten);
		assertTrue(abstractRangeFilterImpl.compareTo(other) < 0);
		assertTrue(other.compareTo(abstractRangeFilterImpl) > 0);
		
		// other has no upper bound
		abstractRangeFilterImpl.setLowerValue(zero);
		other.setLowerValue(zero);
		abstractRangeFilterImpl.setUpperValue(ten);
		other.setUpperValue(null);
		assertTrue(abstractRangeFilterImpl.compareTo(other) < 0);
		assertTrue(other.compareTo(abstractRangeFilterImpl) > 0);
		
		// other has no bound
		abstractRangeFilterImpl.setLowerValue(zero);
		other.setLowerValue(null);
		abstractRangeFilterImpl.setUpperValue(ten);
		other.setUpperValue(null);
		assertTrue(abstractRangeFilterImpl.compareTo(other) < 0);
		assertTrue(other.compareTo(abstractRangeFilterImpl) > 0);
	}
	
	/**
	 * Test bench.
	 */
	private class TestRangeFilter extends AbstractRangeFilterImpl<TestRangeFilter, BigDecimal> {

		private static final long serialVersionUID = -775046766216492351L;

		@Override
		public void initialize(final String filterId) throws EpCatalogViewRequestBindException {
			// stub
		}

		public String getSeoId() {
			// stub
			return StringUtils.EMPTY;
		}

		public void initialize(final Map<String, Object> properties) {
			// stub
		}

		public Map<String, Object> parseFilterString(final String filterIdStr) {
			// stub
			return Collections.emptyMap();
		}
	}
}
