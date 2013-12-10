package com.elasticpath.importexport.importer.controller.impl;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.elasticpath.common.dto.Dto;
import com.elasticpath.importexport.common.dto.catalogs.CatalogDTO;
import com.elasticpath.importexport.importer.context.ImportContext;
import com.elasticpath.importexport.importer.controller.ImportStageFailedException;
import com.elasticpath.importexport.importer.importers.Importer;
import com.elasticpath.persistence.api.Persistable;

/**
 * Tests the {@code ChangeSetImportStageImpl} class. 
 */
public class ChangeSetImportStageImplTest {
	
	/**
	 * Tests that exceptions raised during {@code addObjectToChangeSet} 
	 * result in an {@code ImportStageFailedException} with the changeSetGuid
	 * and DTO class type included. 
	 */
	@Test
	public void testAddObjectExceptionLogging() {
		ChangeSetImportStageImpl importStage = new ChangeSetImportStageImpl() {
			@Override
			protected void addObjectToChangeSet(final Dto dto, final Importer<? super Persistable, ? super Dto> importer,
					final ImportContext context) {
				throw new IllegalArgumentException(String.format(
						"Cannot add object to a change set with null or invalid GUID: %s and object descriptor", "abc"));
			}
			public String getChangeSetGuid() {
				return "abc";
			}
		};
		
		CatalogDTO dto = new CatalogDTO();
		
		boolean expectedExceptionCaught = false;
		try {
			importStage.addObjectToChangeSetWithErrorHandling(dto, null, null);
		} catch (ImportStageFailedException e) {
			expectedExceptionCaught = true;
			String[] expectedParams = {
					"abc",
					"com.elasticpath.importexport.common.dto.catalogs.CatalogDTO",
					"Cannot add object to a change set with null or invalid GUID: abc and object descriptor" };
			assertArrayEquals(expectedParams, e.getIEMessage().getParams());
			assertEquals("Expect the generic message code", "IE-30406", e.getIEMessage().getCode());
		}
		
		assertTrue("Expect the ImportStageFailedException", expectedExceptionCaught);
	}
}
