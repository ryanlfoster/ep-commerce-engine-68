package com.elasticpath.service.contentspace.impl;

import java.io.File;
import java.util.Map;
import java.util.Properties;

import com.elasticpath.settings.SettingsReader;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.ui.velocity.VelocityEngineUtils;

import com.elasticpath.service.contentspace.RenderContext;
import com.elasticpath.service.contentspace.Renderer;

/**
 * Velocity implementation of {@link Render}.
 */
public class VelocityRendererImpl implements Renderer {
	
	private Properties props = new Properties();
	private VelocityEngine engine = null;
	private SettingsReader settingsReader;
	
	/**
	 * Renders a velocity template for a specific content space using a rendering context.
	 * 
	 * @param contentSpaceName the content space name
	 * @param renderContext the context that is used when rendering a template
	 * @return the rendered velocity template as a string representation
	 * @throws Exception when template fails to render
	 */
	public String doRender(final String contentSpaceName, final RenderContext renderContext) throws Exception {
		final String templateName = renderContext.getContentWrapper().getTemplateName();
		final Map<String, String> resolvedParameters = renderContext.getParameters();
	
		final VelocityEngine velocityEngine = getVelocityEngine();
		
        return VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, 
        		getTemplateFullPath(templateName), resolvedParameters);
	}

	/**
	 * Gets the relative path to the template. 
	 * Based on the assets path and concatenates 'content-wrapper'.
	 * 
	 * @param templateName the template name
	 * @return a relative to the 'assets' folder path
	 */
	String getTemplateFullPath(final String templateName) {
		return getTemplatesLocation() + templateName;
	}

	/**
	 * Gets the templates location.
	 * 
	 * @return the templates location relative to assets root folder
	 */
	protected String getTemplatesLocation() {
		return withTrailingSlash(settingsReader.getSettingValue("COMMERCE/SYSTEM/ASSETS/contentWrappersLocation").getValue());
	}
	
	/**
	 * Creates a new velocity engine.
	 * 
	 * @return a new velocity engine
	 * @throws Exception if error occurs
	 */
	VelocityEngine getVelocityEngine() throws Exception {
		if (engine == null) {
			engine = new VelocityEngine();
			for (Object key : props.keySet()) {
				String keyStr = (String) key;
				engine.addProperty(keyStr, props.get(key));
			}
			engine.init();
		}
		return engine;
	}

	/**
	 *
	 * @param props the properties to set
	 */
	public void setVelocityProperties(final Properties props) {
		this.props = props;
	}

	/**
	 * Set the settings reader.
	 *
	 * @param settingsReader the settings reader instance
	 */
	public void setSettingsReader(final SettingsReader settingsReader) {
		this.settingsReader = settingsReader;
	}

	private String withTrailingSlash(final String input) {
		if (StringUtils.isEmpty(input) || input.endsWith("/") || input.endsWith("\\")) {
			return input;
		}
		return input + File.separator;
	}
}
