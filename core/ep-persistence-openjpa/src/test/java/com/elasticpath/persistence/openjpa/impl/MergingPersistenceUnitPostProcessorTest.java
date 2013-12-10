package com.elasticpath.persistence.openjpa.impl;

import static org.junit.Assert.assertEquals;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.orm.jpa.persistenceunit.MutablePersistenceUnitInfo;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitPostProcessor;

/** 
 * Test the merging and exclusion functionality of the MergingPersistenceUnitPostProcessor.
 */
public class MergingPersistenceUnitPostProcessorTest {

	private final MergingPersistenceUnitPostProcessor processor = new MergingPersistenceUnitPostProcessor();

	/**
	 * Test merging the list of mapping file names, after processing the last PersistenceUnitInfo 
	 * should contain the superset of all previous units.
	 */
	@Test
	public void testMergingMappingFileNames() {
		
		MutablePersistenceUnitInfo pui1 = persistenceUnitWithMappingFiles("a");
		MutablePersistenceUnitInfo pui2 = persistenceUnitWithMappingFiles("b");
		MutablePersistenceUnitInfo pui3 = persistenceUnitWithMappingFiles("c");
		
		simulateProcessing(processor, list(pui1, pui2, pui3));

		Assert.assertEquals(list("a", "b", "c"), pui3.getMappingFileNames());
	}

	/**
	 * Test merging the list of mapping file names with exclusions.
	 */
	@Test
	public void testMergingMappingFileNamesWithExclusions() {
		
		processor.setExcludedMappingFiles(list("b"));
		
		MutablePersistenceUnitInfo pui1 = persistenceUnitWithMappingFiles("a");
		MutablePersistenceUnitInfo pui2 = persistenceUnitWithMappingFiles("b", "c");
		MutablePersistenceUnitInfo pui3 = persistenceUnitWithMappingFiles("d");
		
		simulateProcessing(processor, list(pui1, pui2, pui3));

		Assert.assertEquals(list("a", "c", "d"), pui3.getMappingFileNames());
	}
	
	
	/**
	 * Test merging the list of class names, after processing the last PersistenceUnitInfo 
	 * should contain the superset of all previous units.
	 */
	@Test
	public void testMergingManagedClassNames() {
		
		MutablePersistenceUnitInfo pui1 = persistenceUnitWithManagedClassNames("a");
		MutablePersistenceUnitInfo pui2 = persistenceUnitWithManagedClassNames("b");
		MutablePersistenceUnitInfo pui3 = persistenceUnitWithManagedClassNames("c");
		
		simulateProcessing(processor, list(pui1, pui2, pui3));

		Assert.assertEquals(list("a", "b", "c"), pui3.getManagedClassNames());
	}

	/**
	 * Test merging the list of class names, after processing the last PersistenceUnitInfo 
	 * should contain the superset of all previous units.
	 */
	@Test
	public void testMergingManagedClassNamesWithExclusions() {
		
		processor.setExcludedManagedClassNames(list("c"));
		
		MutablePersistenceUnitInfo pui1 = persistenceUnitWithManagedClassNames("a");
		MutablePersistenceUnitInfo pui2 = persistenceUnitWithManagedClassNames("b", "c");
		MutablePersistenceUnitInfo pui3 = persistenceUnitWithManagedClassNames("d");
		
		simulateProcessing(processor, list(pui1, pui2, pui3));
		
		Assert.assertEquals(list("a", "b", "d"), pui3.getManagedClassNames());
	}
	
	
	/**
	 * Test merging the list of class names, after processing the last PersistenceUnitInfo 
	 * should contain the superset of all previous units.
	 * @throws MalformedURLException if there is a problem with the test urls.
	 */
	@SuppressWarnings("PMD.AvoidDuplicateLiterals")
	@Test
	public void testMergingJarFileUrls() throws MalformedURLException {
		
		MutablePersistenceUnitInfo pui1 = persistenceUnitWithJarFileUrls(new URL("file:///jar1"));
		MutablePersistenceUnitInfo pui2 = persistenceUnitWithJarFileUrls(new URL("file:///jar2"));
		MutablePersistenceUnitInfo pui3 = persistenceUnitWithJarFileUrls(new URL("file:///jar3"));
		
		simulateProcessing(processor, list(pui1, pui2, pui3));

		ArrayList<URL> jarFileUrls = new ArrayList<URL>(pui3.getJarFileUrls());
		assertEquals(list(new URL("file:///jar1"), new URL("file:///jar2"), new URL("file:///jar3")), jarFileUrls);
	}	

	
	/**
	 * Test merging the list of class names, after processing the last PersistenceUnitInfo 
	 * should contain the superset of all previous units.
	 * @throws MalformedURLException if there is a problem with the test urls.
	 */
	@Test
	public void testMergingJarFileUrlsWithExclusions() throws MalformedURLException {
		
		processor.setExcludedJarFileUrls(list(new URL("file:///jar3")));
		
		MutablePersistenceUnitInfo pui1 = persistenceUnitWithJarFileUrls(new URL("file:///jar1"));
		MutablePersistenceUnitInfo pui2 = persistenceUnitWithJarFileUrls(new URL("file:///jar2"));
		MutablePersistenceUnitInfo pui3 = persistenceUnitWithJarFileUrls(new URL("file:///jar3"));
		
		simulateProcessing(processor, list(pui1, pui2, pui3));
		
		assertEquals(list(new URL("file:///jar1"), new URL("file:///jar2")), new ArrayList<URL>(pui3.getJarFileUrls()));
	}	
	

	private MutablePersistenceUnitInfo persistenceUnitWithMappingFiles(final String ... mappingFiles) {
		MutablePersistenceUnitInfo pui = createPersistenceUnit();
		pui.getMappingFileNames().addAll(list(mappingFiles));
		return pui;
	}

	private MutablePersistenceUnitInfo persistenceUnitWithManagedClassNames(final String ... classNames) {
		MutablePersistenceUnitInfo pui = createPersistenceUnit();
		pui.getManagedClassNames().addAll(list(classNames));
		return pui;
	}

	private MutablePersistenceUnitInfo persistenceUnitWithJarFileUrls(final URL ... jarFileUrls) {
		MutablePersistenceUnitInfo pui = createPersistenceUnit();
		pui.getJarFileUrls().addAll(list(jarFileUrls));
		return pui;
	}

	private MutablePersistenceUnitInfo createPersistenceUnit() {
		MutablePersistenceUnitInfo pui = new MutablePersistenceUnitInfo();
		pui.setPersistenceUnitName("pu");
		return pui;
	}
	
	
	private void simulateProcessing(final PersistenceUnitPostProcessor processor, 
			final List<MutablePersistenceUnitInfo> puis) {
		for (MutablePersistenceUnitInfo pui : puis) {
			processor.postProcessPersistenceUnitInfo(pui);
		}
	}

	private <T> List<T> list(final T ... elems) {
		return Arrays.asList(elems);
	}	

}
