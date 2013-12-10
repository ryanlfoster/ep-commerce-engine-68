package com.elasticpath.test.persister;

import java.io.File;
import java.io.IOException;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import com.elasticpath.test.common.exception.TestApplicationException;

/**
 * Factory for {@link TestConfig}. Loads configuration from classpath or file system.
 */
public class TestConfigurationFactory {
	private ResourceProvider resourceProvider;
	
	/**
	 * Returns a new {@link TestConfig}.
	 * @return new {@link TestConfig}
	 */
	public TestConfig createTestConfig() {
		return new TestConfig(resourceProvider);
	}

	/**
	 * Provides {@link Resource}s. 
	 */
	public static interface ResourceProvider {
		/**
		 * Gets a Resource based on the given relative path
		 * @param relativePath the relative path 
		 * @return the resource
		 */
		Resource getResource(final String relativePath);
	}

	/**
	 * A {@link TestConfigurationFactory#ResourceProvider} that uses classpath as the source. 
	 */
	public static class ClassPathResourceProvider implements ResourceProvider {
		@Override
		public Resource getResource(final String relativePath) {
			return new ClassPathResource(relativePath);
		}
	}

	/**
	 * A {@link TestConfigurationFactory#ResourceProvider} that uses file system as the source. 
	 */
	public static class FileSystemResourceProvider implements ResourceProvider {
		@Override
		public Resource getResource(final String relativePath) {
			return new FileSystemResource(getProjectRootPath() + File.separator + relativePath);
		}

		private static String getProjectRootPath() {
			try {
				final File dir = new File(".");
				return dir.getCanonicalPath();
			} catch (final IOException e) {
				throw new TestApplicationException("Failed to resolve project root path." + e);
			}
		}
	}

	public ResourceProvider getResourceProvider() {
		return resourceProvider;
	}

	public void setResourceProvider(ResourceProvider resourceProvider) {
		this.resourceProvider = resourceProvider;
	}
}