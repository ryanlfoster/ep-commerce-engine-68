package com.elasticpath.importexport.common.marshalling;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

/**
 * Tests XMLUnmarshaller's ability to unmarshal XML.
 */
public class XMLUnmarshallerTest {

	private final XMLUnmarshaller xmlUnmarshaller = new XMLUnmarshaller(FakeObjectDTO.class);

	/**
	 * Tests unmarshaling a fake adapter with no fields set.
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testUnmarshalSingleEmptyAdapter() throws Exception {
		String sourceXml = "<fakeObject><relation/></fakeObject>";
		byte[] byteXml = sourceXml.getBytes("UTF-8");
		ByteArrayInputStream input = new ByteArrayInputStream(byteXml);
		Object out = xmlUnmarshaller.unmarshall(input);
		assertTrue(out instanceof FakeObjectDTO);
		FakeObjectDTO fakeObjectDTO = (FakeObjectDTO) out;
		assertNotNull(fakeObjectDTO.getFakeRelatedObjectList());
		assertEquals(0, fakeObjectDTO.getFakeRelatedObjectList().size());
	}
	
	/**
	 * Tests unmarshaling a fake adapter.
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testUnmarshalSingleAdapter() throws Exception {
		String sourceXml = "<fakeObject name=\"faker\">"
								+ "<code>123</code>"
								+ "<relation>"
									+ "<relatedObject>"
									+ "</relatedObject>"
								+ "</relation>"
							+ "</fakeObject>";
		byte[] byteXml = sourceXml.getBytes("UTF-8");
		ByteArrayInputStream input = new ByteArrayInputStream(byteXml);
		Object out = xmlUnmarshaller.unmarshall(input);
		assertTrue(out instanceof FakeObjectDTO);
		FakeObjectDTO actualObjectDTO = (FakeObjectDTO) out;
		FakeObjectDTO expectedObjectDTO = new FakeObjectDTO();
		expectedObjectDTO.setCode("123");
		expectedObjectDTO.setName("faker");
		List<FakeRelatedObjectDTO> expectedRelatedObjectList = new ArrayList<FakeRelatedObjectDTO>();
		expectedRelatedObjectList.add(new FakeRelatedObjectDTO());
		expectedObjectDTO.setFakeRelatedObjectList(expectedRelatedObjectList);
		assertTrue(expectedObjectDTO.equals(actualObjectDTO)); // NOPMD 
	}

}
