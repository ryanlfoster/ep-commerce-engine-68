package com.elasticpath.test.concurrent;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

/**
 * A test runner which may repeat certain tests.
 * <p>
 * In general you wouldn't want to repeat a tests unless it was non-deterministic. Usually, this would only be the case
 * for tests which exercised concurrency. Despite this, repeating non-deterministic test only decrease the probability
 * that errors will arise, they cannot eliminate it.
 * </p>
 */
public class RepeatableTestRunner extends BlockJUnit4ClassRunner {

	/**
	 * Default constructor.
	 * 
	 * @param klass class under test
	 * @throws InitializationError in case of errors
	 */
	public RepeatableTestRunner(final Class<?> klass) throws InitializationError {
		super(klass);
	}

	@Override
	protected List<FrameworkMethod> getChildren() {
		List<FrameworkMethod> tests = super.getChildren();
		List<FrameworkMethod> result = new ArrayList<FrameworkMethod>(tests.size());
		for (FrameworkMethod test : tests) {
			Repeats repeats = test.getAnnotation(Repeats.class);
			if (repeats == null) {
				result.add(test);
			} else {
				for (int i = 0; i < repeats.times(); ++i) {
					result.add(new RepeatedFrameworkMethod(test.getMethod(), i));
				}
			}
		}

		return result;
	}

	/** Test method that should be repeated. */
	private static class RepeatedFrameworkMethod extends FrameworkMethod {
		private final int number;

		public RepeatedFrameworkMethod(final Method method, final int testNumber) {
			super(method);
			this.number = testNumber;
		}

		@Override
		public String getName() {
			return String.format("%s[%d]", super.getName(), number);
		}
	}
}
