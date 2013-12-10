package com.elasticpath.domain.dataimport.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.elasticpath.domain.dataimport.ImportType;

/**
 * Test <code>AbstractImportTypeImpl</code>.
 */
public class AbstractImportTypeImplTest {


	/**
	 * Test method for 'com.elasticpath.domain.dataimport.impl.AbstractImportTypeImpl.hashCode()'.
	 */
	@Test
	public void testHashCode() {
		assertTrue(AbstractImportTypeImpl.UPDATE_TYPE.hashCode() != AbstractImportTypeImpl.INSERT_TYPE.hashCode()); // NOPMD
	}

	/**
	 * Test method for 'com.elasticpath.domain.dataimport.impl.AbstractImportTypeImpl.getTypeId()'.
	 */
	@Test
	public void testGetTypeId() {
		assertEquals(AbstractImportTypeImpl.INSERT_UPDATE_ID, AbstractImportTypeImpl.INSERT_UPDATE_TYPE.getTypeId());
	}

	/**
	 * Test method for 'com.elasticpath.domain.dataimport.impl.AbstractImportTypeImpl.getName()'.
	 */
	@Test
	public void testGetName() {
		final String name2 = AbstractImportTypeImpl.INSERT_UPDATE_TYPE.getNameMessageKey();
		final String name3 = AbstractImportTypeImpl.UPDATE_TYPE.getNameMessageKey();
		assertFalse(name2.equals(name3));
	}

	/**
	 * Test method for 'com.elasticpath.domain.dataimport.impl.AbstractImportTypeImpl.equals(ImportType)'.
	 */
	@Test
	public void testEqualsImportType() {
		assertFalse(AbstractImportTypeImpl.INSERT_UPDATE_TYPE.equals(AbstractImportTypeImpl.UPDATE_TYPE));
	}

	/**
	 * Test method for 'com.elasticpath.domain.dataimport.impl.AbstractImportTypeImpl.equals(Object)'.
	 */
	@Test
	public void testEqualsObject() {
		assertFalse(AbstractImportTypeImpl.INSERT_UPDATE_TYPE.equals(new Object()));
	}

	/**
	 * Test method for 'com.elasticpath.domain.dataimport.impl.AbstractImportTypeImpl.getInstance(int)'.
	 */
	@Test
	public void testGetInstance() {
		assertSame(AbstractImportTypeImpl.INSERT_UPDATE_TYPE, AbstractImportTypeImpl.getInstance(1));
		assertSame(AbstractImportTypeImpl.UPDATE_TYPE, AbstractImportTypeImpl.getInstance(2));
	}

	/**
	 * Test method for 'com.elasticpath.domain.dataimport.impl.AbstractImportTypeImpl.getAllImportTypes()'.
	 */
	@Test
	public void testGetAllImportTypes() {
		final List<ImportType> list = AbstractImportTypeImpl.getAllImportTypes();
		assertSame(AbstractImportTypeImpl.INSERT_UPDATE_TYPE, list.get(0));
		assertSame(AbstractImportTypeImpl.UPDATE_TYPE, list.get(1));
	}
}
