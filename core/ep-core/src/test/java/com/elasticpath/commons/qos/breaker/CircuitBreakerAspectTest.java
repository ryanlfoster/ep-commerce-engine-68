package com.elasticpath.commons.qos.breaker;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.util.SimpleMethodInvocation;

import com.elasticpath.test.TestLog4jLoggingAppender;


/**
 * Test the circuit breaker works as expected - moving between states based on the 
 * responses from the delegate service.
 */
@SuppressWarnings({"PMD.AvoidDuplicateLiterals", "PMD.MoreThanOneLogger" })
public class CircuitBreakerAspectTest {
	
	private CircuitBreakerAspect breaker;

	/**
	 * Create the fixture for the test methods.
	 */
	@Before
	public void setUp() {
		breaker = new CircuitBreakerAspect();
	}
	
	/**
	 * Tests that normal calls in the closed state are delegated and returned.
	 * @throws Throwable if something goes wrong.
	 */
	@Test
	public void testClosedStateWithSucessfulCall() throws Throwable {
		assertDelegateCallSucceeds("Simple call in closed state should return delegate return"); 
	}
	
	/**
	 * Tests that failed calls cause the breaker to trip once the threshold is exceeded.
	 * @throws Throwable if something goes wrong.  Once tripped we should see 
	 * CircuitBreakerExceptions rather than delegate exceptions.
	 */
	@Test
	public void testClosedStateTripsToOpenWhenThresholdExceeded() throws Throwable {
		breaker.setFailureThreshold(1);
		assertDelegateExceptionThrown("Expected exception from delegate");
		assertCircuitBreakerFailsFast("CircuitBreakerException expected as breaker should have tripped.");
	}
	
	/**
	 * Test that a successful call will reset the failure count.
	 * @throws Throwable if something goes wrong.
	 */
	@Test
	public void testCountResetWhenSuccessIsSeen() throws Throwable {
		final int three = 3;
		breaker.setFailureThreshold(three);

		// 2 fails
		// 1 success
		// 1 fail
		// next call should be delegated fine (no CircuitBreakerException).
		assertDelegateExceptionThrown("Expected exception from delegate");
		assertDelegateExceptionThrown("Expected exception from delegate");
		assertDelegateCallSucceeds("Call should have succeeded without a problem");
		assertDelegateExceptionThrown("Expected exception from delegate");
		assertDelegateCallSucceeds("Call should have succeeded without a problem");
	}

	/**
	 * Test that only the appropriate number of consecutive fails causes the 
	 * breaker to trip.
	 * @throws Throwable if something goes wrong.
	 */
	@Test
	public void testConsecutiveFailuresCauseTrip() throws Throwable {
		final int three = 3;
		breaker.setFailureThreshold(three);

		// 3 successive fails
		assertDelegateExceptionThrown("Expected exception from delegate");
		assertDelegateExceptionThrown("Expected exception from delegate");
		assertDelegateExceptionThrown("Expected exception from delegate");
		// All future calls should fail-fast.
		assertCircuitBreakerFailsFast("Expected fail-fast exception");
		assertCircuitBreakerFailsFast("Expected fail-fast exception");
		assertCircuitBreakerFailsFast("Expected fail-fast exception");
	}
	
	/**
	 * Tests that once in the Open state that once the timeout expires a successful
	 * attempt will put us back into the closed state.
	 * 
	 * @throws Throwable if something goes wrong.
	 */
	@Test
	public void testAttemptResetGoesToClosedWithSuccessfulCall() throws Throwable {
		breaker.setAttemptResetTimeoutMillis(-1);
		breaker.trip();
		assertDelegateCallSucceeds("Should have reset back to closed state");
		
		// Prevent flip-flopping from half-open to open
		final int longTimeout = 1000;
		breaker.setAttemptResetTimeoutMillis(longTimeout);
		//Successive calls should all be good.
		assertDelegateCallSucceeds("Should have reset back to closed state");
		assertDelegateCallSucceeds("Should have reset back to closed state");
	}
	
	
	/**
	 * Tests that once in the Open state that once the timeout expires a successful
	 * attempt will put us back into the closed state.
	 * 
	 * @throws Throwable if something goes wrong.
	 */
	@Test
	public void testAttemptResetGoesBackToOpenWhenResetAttemptFails() throws Throwable {
		final int noTimeout = -1;
		breaker.setAttemptResetTimeoutMillis(noTimeout);
		breaker.trip();
		
		assertCircuitBreakerFailsFast("Half-open state should fail-fast");
		
		// Make sure we stay in the open-state for successive calls.
		final int longTimeout = 10000;
		breaker.setAttemptResetTimeoutMillis(longTimeout);
		
		assertCircuitBreakerFailsFast("Circuit breaker should fail fast");
		assertCircuitBreakerFailsFast("Circuit breaker should fail fast");
		assertCircuitBreakerFailsFast("Circuit breaker should fail fast");
	}
	
	/**
	 * Prove that when a trip happens the circuit breaker logs a method appropriate for
	 * an Operations department to use.
	 */
	@Test
	public void testTripCausesUsefulLoggingWithExplicitName() {
		TestLog4jLoggingAppender appender = new TestLog4jLoggingAppender();

		breaker.setName("CircuitBreakerA100");
		
		// add expectations
		appender.addMessageToVerify(Level.ERROR, "CircuitBreaker:'CircuitBreakerA100' has tripped into Open state");
		appender.addMessageToVerify(Level.INFO, "CircuitBreaker:'CircuitBreakerA100' changing state from CLOSED to OPEN");

		
		// add the appender to those associated with the class under test
		Logger log = LogManager.getLogger(CircuitBreakerAspect.class);
		log.setLevel(Level.INFO);
		log.addAppender(appender);

		breaker.trip();

		// ensure all the expectations are met
		appender.verify();
	}
	
	/**
	 * Prove that when a trip happens the circuit breaker logs a method appropriate for
	 * an Operations department to use when no name has been set.
	 */
	@Test
	public void testTripCausesUsefulLoggingWithoutName() {
		TestLog4jLoggingAppender appender = new TestLog4jLoggingAppender();
		
		// add expectations
		appender.addMessageToVerify(Level.ERROR, "CircuitBreaker:'anonymous' has tripped into Open state");
		appender.addMessageToVerify(Level.INFO, "CircuitBreaker:'anonymous' changing state from CLOSED to OPEN");

		
		// add the appender to those associated with the class under test
		Logger log = LogManager.getLogger(CircuitBreakerAspect.class);
		log.setLevel(Level.INFO);
		log.addAppender(appender);

		breaker.trip();

		// ensure all the expectations are met
		appender.verify();
	}
	
	private void assertCircuitBreakerFailsFast(final String failureMessage) throws Throwable {
		assertExceptionThrown(breaker, CircuitBreakerException.class, failureMessage);
	}

	private void assertDelegateExceptionThrown(final String failureMessage) throws Throwable {
		assertExceptionThrown(breaker, IllegalStateException.class, failureMessage);
	}
	
	private void assertDelegateCallSucceeds(final String failureMessage) throws Throwable {
		final Object resultObject = new Object();
		assertSame(failureMessage, resultObject, breaker.invoke(new SimpleMethodInvocation(null, null) {
			@Override
			public Object proceed() throws Throwable {
				return resultObject;
			}
		}));
	}
	
	@SuppressWarnings("PMD.AvoidCatchingThrowable")
	private void assertExceptionThrown(
			final CircuitBreakerAspect breaker, 
			final Class< ? extends Throwable> expectedExceptionClass,
			final String failureMessage) throws Throwable {
		
		MethodInvocation failureInvocation = new SimpleMethodInvocation(null, null) {
			@Override
			public Object proceed() throws Throwable {
				throw new IllegalStateException("Faked up delegate exception");
			}
		};

		try {
			breaker.invoke(failureInvocation);
			fail(failureMessage);
		} catch (Throwable expected) {
			if (!expectedExceptionClass.isInstance(expected)) {
				fail(failureMessage + ", actually got " + expected.getClass().getName());
			}
		}
	}
}
