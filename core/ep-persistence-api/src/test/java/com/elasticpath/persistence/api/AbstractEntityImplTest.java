package com.elasticpath.persistence.api;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import org.junit.Before;
import org.junit.Test;

/**
 * Test class <code>AbstractEntityImplTest</code>.
 * 
 * @author jai
 */
public class AbstractEntityImplTest {

	private AbstractEntityImpl entityImpl;

	/**
	 * Prepare for tests.
	 */
	@Before
	public void setUp()  {
		this.entityImpl = new AbstractEntityImpl() {
			private static final long serialVersionUID = 7837311546842119516L;

			private String guid;
			
			public String getGuid() {
				return guid;
			};
			
			public void setGuid(final String guid) {
				this.guid = guid;
			};
			
			public long getUidPk() {
				return 0;
			}
			
			public void setUidPk(final long newUidPk) {
				// stub for test, not used
			}
		};
	}

	/**
	 * Test method for 'com.elasticpath.persistence.api.AbstractEntityImpl#initialize()'.
	 */
	@Test
	public void testInitialize() {
		
		entityImpl.initialize();
		assertNotNull(entityImpl.getGuid());
		String guid = entityImpl.getGuid();

		// call initialize() again, no value should be changed.
		entityImpl.initialize();
		assertSame(guid, entityImpl.getGuid());
	}

}
