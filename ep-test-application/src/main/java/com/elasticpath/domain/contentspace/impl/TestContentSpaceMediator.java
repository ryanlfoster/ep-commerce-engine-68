package com.elasticpath.domain.contentspace.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.velocity.runtime.resource.util.StringResourceRepositoryImpl;

import com.elasticpath.domain.contentspace.ContentWrapper;
import com.elasticpath.domain.contentspace.ContentWrapperLoader;

public class TestContentSpaceMediator {
	private static TestContentSpaceMediator instance = new TestContentSpaceMediator();
	
	private TestContentSpaceMediator() {
		//Do nothing
	}
	
	/**
	 * For templates we dont use file system. 
	 */
	public static final Map<String, String> TEMPLATES = new HashMap<String, String>();
	/**
	 * For content wrappers we dont use file system. 
	 */
	public static final Map<String, ContentWrapper> CONTENT_WRAPPERS = new HashMap<String, ContentWrapper>();
	
	/**
	 * A customised implementation of a {@link ContentWrapperLoader} that gets all the
	 * content wrappers from the list of registered content wrappers managed by the 
	 * set-up fixture.
	 */
	public static class CustomContentWrapperLoaderImpl implements ContentWrapperLoader {
		@Override
		public Map<String, ContentWrapper> loadContentWrappers() {
			return getContentWrappers();
		}
	}

	/**
	 * A customised implementation of StringResourceRepository which adds 
	 * all the string templates to the repository to be used by the Velocity Engine. 
	 */
	public static class CustomStringResourceRepositoryImpl extends StringResourceRepositoryImpl {

		/**
		 * Constructs an instance of this class.
		 */
		public CustomStringResourceRepositoryImpl() {
			super();
			for (String templateName : getTemplates().keySet()) {
				putStringResource(templateName, getTemplates().get(templateName));
			}
		}
	}
	

	/**
	 * Gets all the registered content wrappers.
	 * 
	 * @return a map of content wrapper IDs to their corresponding content wrappers
	 */
	public static Map<String, ContentWrapper> getContentWrappers() {
		return CONTENT_WRAPPERS;
	}

	/**
	 * Gets all the registered templates.
	 * 
	 * @return a map of template names and their bodies
	 */
	public static Map<String, String> getTemplates() {
		return TEMPLATES;
	}

	public static TestContentSpaceMediator getInstance() {
		return instance;
	}
	
	/**
	 * Custom content wrapper repository to avoid database calls to get settings.
	 */
	public static class CustomContentWrapperRepository extends ContentWrapperRepositoryImpl {
		/**
		 * Gets a fixed load interval.
		 * 
		 * @return 1 second
		 */
		@Override
		protected int getLoadInterval() {
			return 1;
		}
	}
}
