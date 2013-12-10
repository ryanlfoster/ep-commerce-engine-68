package com.elasticpath.importexport;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.After;
import org.junit.Test;

import com.sun.mail.util.LineInputStream;

import com.elasticpath.importexport.common.exception.runtime.ImportRuntimeException;
import com.elasticpath.importexport.common.manifest.Manifest;
import com.elasticpath.importexport.common.marshalling.XMLMarshaller;
import com.elasticpath.importexport.common.util.runner.AbstractPipedStreamRunner;
import com.elasticpath.importexport.exporter.delivery.impl.AbstractDeliveryMethodImpl;
import com.elasticpath.importexport.exporter.delivery.impl.FileDeliveryMethodImpl;
import com.elasticpath.importexport.exporter.packager.impl.AbstractPackagerImpl;
import com.elasticpath.importexport.exporter.packager.impl.NullPackagerImpl;
import com.elasticpath.importexport.exporter.packager.impl.ZipPackagerImpl;
import com.elasticpath.importexport.importer.retrieval.impl.AbstractRetrievalMethodImpl;
import com.elasticpath.importexport.importer.retrieval.impl.FileRetrievalMethodImpl;
import com.elasticpath.importexport.importer.unpackager.impl.AbstractUnpackagerImpl;
import com.elasticpath.importexport.importer.unpackager.impl.NullUnpackagerImpl;
import com.elasticpath.importexport.importer.unpackager.impl.ZipUnpackagerImpl;

/**
 * Tests Pack Deliver Retrieval Unpack Flow.
 */
public class PackDeliverRetrievalUnpackFlowTest {

	private static final String DELIVERY_TARGET = "./target/test";

	private static final String PACKAGE_NAME = "test.test.test.zip";

	private static final String FILE_NAME = "test.xml";

	private static final String RETRIEVAL_PACKAGE_SOURCE = DELIVERY_TARGET + File.separatorChar + PACKAGE_NAME;

	private static final String CONTENT = "Some Content";


	/**
	 * Tears Down.
	 */
	@After
	public void tearDown() {
		(new File(RETRIEVAL_PACKAGE_SOURCE)).delete();
		(new File(DELIVERY_TARGET + File.separatorChar + FILE_NAME)).delete();
		(new File(DELIVERY_TARGET + File.separatorChar + Manifest.MANIFEST_XML)).delete();
		(new File(DELIVERY_TARGET)).delete();
	}

	/**
	 * Tests File Deliver Retrieval.
	 * 
	 * @throws IOException for IO errors
	 */
	@Test
	public void testFileDeliverRetrieval() throws IOException {

		// Delivery:
		AbstractDeliveryMethodImpl deliveryMethod = new FileDeliveryMethodImpl();
		deliveryMethod.initialize(DELIVERY_TARGET);

		OutputStream out = deliveryMethod.deliver(PACKAGE_NAME);

		out.write(CONTENT.getBytes());
		out.close();

		// Retrieval:
		AbstractRetrievalMethodImpl retrievalMethod = new FileRetrievalMethodImpl();
		retrievalMethod.initialize(RETRIEVAL_PACKAGE_SOURCE);

		assertTrue(checkContent(retrievalMethod.retrieve(), CONTENT));
	}

	/**
	 * Tests ZIP Pack Deliver Retrieval Unpack.
	 * 
	 * @throws IOException for IO errors
	 */
	@Test
	public void testZipPackDeliverRetrievalUnpack() throws IOException {
		// Pack and Deliver:
		AbstractDeliveryMethodImpl deliveryMethod = new FileDeliveryMethodImpl();
		deliveryMethod.initialize(DELIVERY_TARGET);

		AbstractPackagerImpl packager = new ZipPackagerImpl();

		try {
			packager.initialize(deliveryMethod, PACKAGE_NAME);
			packager.addEntry(new ByteArrayInputStream(CONTENT.getBytes()), FILE_NAME);
			packager.addEntry(createManifest(FILE_NAME), Manifest.MANIFEST_XML);
		} finally {
			packager.finish();
		}

		// Retrieval And Unpack:
		AbstractRetrievalMethodImpl retrievalMethod = new FileRetrievalMethodImpl();
		retrievalMethod.initialize(RETRIEVAL_PACKAGE_SOURCE);

		AbstractUnpackagerImpl unpackager = new ZipUnpackagerImpl();
		unpackager.initialize(retrievalMethod);

		assertTrue(unpackager.hasNext());
		assertTrue(checkContent(unpackager.nextEntry(), CONTENT));

		assertFalse(unpackager.hasNext());
		try {
			unpackager.nextEntry();
			fail("ImportRuntimeException must be thrown.");
		} catch (ImportRuntimeException e) {
			assertNotNull(e);
		}
	}

	/**
	 * Tests NULL Pack Deliver Retrieval Unpack.
	 * 
	 * @throws IOException for IO errors
	 */
	@Test
	public void testNullPackDeliverRetrievalUnpack() throws IOException {
		// Pack and Deliver:
		AbstractDeliveryMethodImpl deliveryMethod = new FileDeliveryMethodImpl();
		deliveryMethod.initialize(DELIVERY_TARGET);

		AbstractPackagerImpl packager = new NullPackagerImpl();

		try {
			packager.initialize(deliveryMethod, null);
			packager.addEntry(new ByteArrayInputStream(CONTENT.getBytes()), FILE_NAME);
			packager.addEntry(createManifest(FILE_NAME), Manifest.MANIFEST_XML);
		} finally {
			packager.finish();
		}

		// Retrieval And Unpack:
		AbstractRetrievalMethodImpl retrievalMethod = new FileRetrievalMethodImpl();
		retrievalMethod.initialize(DELIVERY_TARGET);

		AbstractUnpackagerImpl unpackager = new NullUnpackagerImpl();
		unpackager.initialize(retrievalMethod);

		assertTrue(unpackager.hasNext());
		assertTrue(checkContent(unpackager.nextEntry(), CONTENT));

		assertFalse(unpackager.hasNext());
		try {
			unpackager.nextEntry();
			fail("ImportRuntimeException must be thrown.");
		} catch (ImportRuntimeException e) {
			assertNotNull(e);
		}
	}

	/*
	 * Checks the content using File
	 */
	private static boolean checkContent(final File fileForCheck, final String content) throws IOException {
		return checkContent(new FileInputStream(fileForCheck), content);
	}

	/*
	 * Checks the content using InputStream
	 */
	private static boolean checkContent(final InputStream inputStream, final String content) throws IOException {
		final LineInputStream stream = new LineInputStream(inputStream);
		try {
			return stream.readLine().equals(content) && (stream.read() == -1);
		} finally {
			stream.close();
		}
	}

	/*
	 * Util: creates a Manifest InputStream from entries.
	 * @param entries - resources for manifest
	 * @return InputStream
	 */
	private static InputStream createManifest(final String... entries) {
		final Manifest manifest = new Manifest();
		for (String resource : entries) {
			manifest.addResource(resource);
		}
		return new AbstractPipedStreamRunner() {
			@Override
			protected void runInternal(final OutputStream outputStream) {
				(new XMLMarshaller(Manifest.class)).marshal(manifest, outputStream);
			}
		} .createResultStream();
	}
}
