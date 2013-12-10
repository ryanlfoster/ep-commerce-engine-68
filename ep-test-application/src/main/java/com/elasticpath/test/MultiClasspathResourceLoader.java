package com.elasticpath.test;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.ExtendedProperties;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

/**
 * {@link ClassPathResourceLoader} which allows loading resources from multiple different classpath locations. They are
 * specified with an order list of base paths to check for resources under. Use the velocity property {@code basepaths}
 * to specify a comma-separated list of base paths to check.
 */
public class MultiClasspathResourceLoader extends ClasspathResourceLoader {

	private List<String> basepaths = new ArrayList<String>();

	@Override
	@SuppressWarnings("unchecked")
	public void init(final ExtendedProperties configuration) {
		super.init(configuration);

		basepaths.addAll(configuration.getVector("basepaths"));
	}

	@Override
	public InputStream getResourceStream(final String name) throws ResourceNotFoundException {
		if (StringUtils.isEmpty(name)) {
			return super.getResourceStream(name);
		}

		String trimmedName = name.trim();
		if (!trimmedName.startsWith("/")) {
			trimmedName = '/' + trimmedName;
		}

		for (String basepath : basepaths) {
			try {
				return super.getResourceStream(basepath + trimmedName);
			} catch (ResourceNotFoundException e) {
				// ignore, location doesn't exist
			}
		}

		// can't find with base paths, so use existing logic
		return super.getResourceStream(name);
	}
}
