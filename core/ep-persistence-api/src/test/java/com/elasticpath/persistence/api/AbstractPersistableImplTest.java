/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.persistence.api;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

/**
 * Test of the public API of <code>PersistenceImpl</code>.
 */
public class AbstractPersistableImplTest {
	private Persistable persistable;

	/**
	 * Prepares for the next test.
	 * 
	 * @throws Exception
	 *             if something goes wrong.
	 */
	@Before
	public void setUp() throws Exception {
		persistable = new AbstractPersistableImpl() {
			private static final long serialVersionUID = -2442440233081733148L;
			private long uidPk;
			public long getUidPk() {
				return this.uidPk;
			}
			public void setUidPk(final long newUidPk) {
				this.uidPk = newUidPk;
			}
		};
	}

	/**
	 * Test method for
	 * 'com.elasticpath.domain.impl.PersistenceImpl.setUid(long)'.
	 */
	@Test
	public void testIsPersistent() {
		assertFalse(persistable.isPersisted());
		persistable.setUidPk(1);
		assertTrue(persistable.isPersisted());
	}

}
