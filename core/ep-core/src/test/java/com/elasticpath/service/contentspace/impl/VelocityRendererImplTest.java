package com.elasticpath.service.contentspace.impl;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.junit.Test;

import com.elasticpath.domain.contentspace.ContentWrapper;
import com.elasticpath.domain.contentspace.Parameter;
import com.elasticpath.domain.contentspace.impl.ContentWrapperImpl;
import com.elasticpath.domain.contentspace.impl.TemplateParameterImpl;
import com.elasticpath.service.contentspace.RenderContext;
/**
 * Velocity renderer test. 
 */
public class VelocityRendererImplTest {
	
	private static final String TEMPLATE_NAME = "target/test-classes/conf/content-wrappers/simpleContentSpacetemplate.cwt";
	
	/**
	 * Try to get render result from dummy implementations.
	 * @throws Exception exception thrown on render failure
	 */
	@Test
	public void testRendererImpl() throws Exception {
		
		RenderContext renderContext = new RenderContext() {
			
			@Override
			public ContentWrapper getContentWrapper() {
				List<Parameter> paramDefs = new ArrayList<Parameter>();
				paramDefs.add(new TemplateParameterImpl("param1"));
				paramDefs.add(new TemplateParameterImpl("param2"));

				ContentWrapper contentWrapper = new ContentWrapperImpl();
				contentWrapper.setTemplateParameters(paramDefs);
				String fileName = TEMPLATE_NAME;
				File file = new File(fileName);
				if (!file.exists()) {
					fileName =  TEMPLATE_NAME; 
					
				}
				contentWrapper.setTemplateName(fileName);
				return contentWrapper;
			}
			
			@Override
			public Map<String, String> getParameters() {
				Map<String, String> resolvedParameters = new HashMap<String, String>();
				resolvedParameters.put("param1", "hello");
				resolvedParameters.put("param2", "bye");
				return resolvedParameters;
			}
		};
		
		VelocityRendererImpl renderer = new VelocityRendererImpl() {

			@Override
			String getTemplateFullPath(final String templateName) {
				return templateName;
			}
			
		};
		renderer.setVelocityProperties(new Properties());
		
		String rez = renderer.doRender("cs1", renderContext);

		assertTrue(rez.indexOf("hello") > -1);
		assertTrue(rez.indexOf("bye") > -1);
	}
	

}
