package com.elasticpath.importexport.common.adapters.targetedselling;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.runners.JUnit4ClassRunner;
import org.junit.runner.RunWith;

import com.elasticpath.domain.contentspace.ContentSpace;
import com.elasticpath.domain.contentspace.DynamicContent;
import com.elasticpath.domain.contentspace.impl.ContentSpaceImpl;
import com.elasticpath.domain.contentspace.impl.DynamicContentImpl;
import com.elasticpath.domain.sellingcontext.SellingContext;
import com.elasticpath.domain.sellingcontext.impl.SellingContextImpl;
import com.elasticpath.domain.targetedselling.DynamicContentDelivery;
import com.elasticpath.domain.targetedselling.impl.DynamicContentDeliveryImpl;
import com.elasticpath.importexport.common.adapters.tag.SellingContextAdapter;
import com.elasticpath.importexport.common.dto.tag.SellingContextDTO;
import com.elasticpath.importexport.common.dto.targetedselling.DynamicContentDeliveryDTO;

/**
 * Tests for DynamicContentDeliveryAssembler.
 */
@RunWith(JUnit4ClassRunner.class)
public class DynamicContentDeliveryAssemblerTest {

	private final Mockery context = new JUnit4Mockery() { { //NOPMD
	        setImposteriser(ClassImposteriser.INSTANCE);
	    } };
	   
	private final SellingContextAdapter mockSellingContextAdapter = context.mock(SellingContextAdapter.class);
	
	private SellingContext testSellingContext;
	private SellingContextDTO sellingContextDto;
	private DynamicContentDelivery testDynamicContentDelivery;
	private DynamicContentDeliveryDTO dynamicContentDeliveryDto;
	private ContentSpace contentSpace;
	private DynamicContent dynamicContent;
	
	
	/**
	 * @throws Exception upon error
	 */
	@Before
	public void setUp() throws Exception {
		
		/**
		 * Setup a Dynamic Content Delivery Domain object for testing assembling DTO
		 */
		
		testDynamicContentDelivery = new DynamicContentDeliveryImpl();
		testDynamicContentDelivery.setGuid("DCD123");
		testDynamicContentDelivery.setPriority(0);
		
		Set<ContentSpace> contentSpaces = new HashSet<ContentSpace>();
		contentSpace = new ContentSpaceImpl();
		contentSpace.setGuid("CS123");
		contentSpaces.add(contentSpace);
		testDynamicContentDelivery.setContentspaces(contentSpaces);
		
		testDynamicContentDelivery.setDescription("description");
		dynamicContent = new DynamicContentImpl();
		dynamicContent.setGuid("DC123");
		testDynamicContentDelivery.setDynamicContent(dynamicContent);
		
		testSellingContext = new SellingContextImpl();
		testSellingContext.setGuid("SC123");
		
		sellingContextDto = new SellingContextDTO();
		
		/**
		 * Setup a Dynamic Content Delivery DTO object for testing assembling Domain
		 */
		dynamicContentDeliveryDto = new DynamicContentDeliveryDTO();
		dynamicContentDeliveryDto.setGuid("DCD123");
		dynamicContentDeliveryDto.setPriority(0);
		
		List<String> contentSpaceGuids = new ArrayList<String>();
		contentSpaceGuids.add(contentSpace.getGuid());
		dynamicContentDeliveryDto.setContentSpaceGuids(contentSpaceGuids);
		dynamicContentDeliveryDto.setDynamicContentGuid(dynamicContent.getGuid());
		
	}
	/**
	 * Tests that assembleDto() creates a DTO out of the Dynamic Content Delivery domain entity 
	 * with a null Selling Context. 
	 */
	@Test
	public void testAssembleDtoNullSellingContext() {
		// Setup target DTO
		DynamicContentDeliveryDTO dynamicContentDeliveryDTO = new DynamicContentDeliveryDTO();
		
		DynamicContentDeliveryAssembler dynamicContentDeliveryAssembler = new DynamicContentDeliveryAssembler();
		dynamicContentDeliveryAssembler.assembleDto(testDynamicContentDelivery, dynamicContentDeliveryDTO);
		
		assertEquals(testDynamicContentDelivery.getDescription(), dynamicContentDeliveryDTO.getDescription());
		assertEquals(testDynamicContentDelivery.getGuid(), dynamicContentDeliveryDTO.getGuid());
		assertEquals(testDynamicContentDelivery.getName(), dynamicContentDeliveryDTO.getName());
		assertEquals(testDynamicContentDelivery.getPriority(), dynamicContentDeliveryDTO.getPriority());
		assertEquals(1, dynamicContentDeliveryDTO.getContentSpaceGuids().size());
		assertEquals(contentSpace.getGuid(), dynamicContentDeliveryDTO.getContentSpaceGuids().get(0));
		assertEquals(dynamicContent.getGuid(), dynamicContentDeliveryDTO.getDynamicContentGuid());
		
	}
	
	/**
	 * Tests that assembleDto() creates a DTO out of the Dynamic Content Delivery domain entity
	 * with the same Selling Context set into it. It does not test other fields set onto the DTO, since
	 * these are tested in testAssembleDtoNullSellingContext().
	 */
	@Test
	public void testAssembleDtoSellingContext() {
		
		// Setup source DynamicContentDelivery domain object and target DTO object to be populated
		testDynamicContentDelivery.setSellingContext(testSellingContext);
		DynamicContentDeliveryDTO dynamicContentDeliveryDTO = new DynamicContentDeliveryDTO();

		// Setup dynamic content delivery assembler with mock adapter
		DynamicContentDeliveryAssembler dynamicContentDeliveryAssembler = new DynamicContentDeliveryAssembler();
		dynamicContentDeliveryAssembler.setSellingContextAdapter(mockSellingContextAdapter);
		context.checking(new Expectations() { { //NOPMD
			allowing(mockSellingContextAdapter).createDtoObject(); will(returnValue(sellingContextDto));
			allowing(mockSellingContextAdapter).populateDTO(testSellingContext, sellingContextDto);
		} });
		
		// Execute and validate the method under test
		dynamicContentDeliveryAssembler.assembleDto(testDynamicContentDelivery, dynamicContentDeliveryDTO);
		assertEquals(sellingContextDto, dynamicContentDeliveryDTO.getSellingContext());
	}
}
