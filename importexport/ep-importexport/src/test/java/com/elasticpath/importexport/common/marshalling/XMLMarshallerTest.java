package com.elasticpath.importexport.common.marshalling;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

/**
 * Tests XMLMarshaller's ability to marshal XML.
 */
public class XMLMarshallerTest {

	private final XMLMarshaller xmlMarshaller = new XMLMarshaller(FakeObjectDTO.class);
	

	/**
	 * Tests marshaling a fake adapter with no fields set.
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testMarshalSingleEmptyAdapter() throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		FakeObjectDTO adapter = new FakeObjectDTO();
		xmlMarshaller.marshal(adapter, out);
		String xml = out.toString();
		String expectedXml = "<fakeObject><relation/></fakeObject>";
		assertXmlEquals(expectedXml, xml);
	}
	
	/**
	 * Tests marshaling a fake adapter.
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testMarshalSingleAdapter() throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		FakeObjectDTO adapter = new FakeObjectDTO();
		adapter.setCode("123");
		adapter.setName("faker");
		List<FakeRelatedObjectDTO> fakeRelatedObjectList = new ArrayList<FakeRelatedObjectDTO>();
		fakeRelatedObjectList.add(new FakeRelatedObjectDTO());
		adapter.setFakeRelatedObjectList(fakeRelatedObjectList);
		xmlMarshaller.marshal(adapter, out);
		String xml = out.toString();
		String expectedXml = "<fakeObject name=\"faker\"><code>123</code><relation><relatedObject id=\"0\"/></relation>"
			+ "</fakeObject>";
		assertXmlEquals(expectedXml, xml);
	}

	/*
	 * Compares 2 XML strings with their whitespace and newlines removed from between elements.
	 */
	private void assertXmlEquals(final String expectedXml, final String xml) {
		assertEquals(expectedXml.replaceAll(">\\s+<", "><").trim(), xml.replaceAll(">\\s+<", "><").trim());
	}
}
