package com.elasticpath.service.contentspace;


/**
 * Renderer interface. Responsible for retrieve the result for content space.
 */
public interface Renderer {
	
	/**
	 * The method that will render the content space.
	 * @param contentSpaceName - Name for content space.
	 * @param renderContext - The content space information required for rendering.
	 * @return <code>RenderResult</code>
	 * @throws Exception if error occurs when rendering the contents
	 */
	String doRender(final String contentSpaceName, final RenderContext renderContext) throws Exception;
	

}
