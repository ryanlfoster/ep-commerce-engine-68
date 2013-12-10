package com.elasticpath.service.search;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.junit.Test;

/**
 * Test case for {@link IndexType}.
 */
public class IndexTypeTest {

	/**
	 * Test method for {@link IndexType#findFromName(String)}.
	 */
	@Test
	public void testFindFromName() {
		for (IndexType type : IndexType.values()) {
			assertSame(type, IndexType.findFromName(type.getIndexName()));
		}
		assertNull(IndexType.findFromName("some name that doesn't exists"));
		assertNull(IndexType.findFromName(null));
	}
}
