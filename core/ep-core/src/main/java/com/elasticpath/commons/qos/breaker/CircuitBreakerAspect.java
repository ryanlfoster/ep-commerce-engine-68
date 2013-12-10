package com.elasticpath.commons.qos.breaker;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.log4j.Logger;

/**
 * Helps ensure quality of service and prevent the failure of
 * one service component cascading throughout the system by wrapping the
 * calls to a delegate and responding appropriately based on historical failure/success
 * information.  
 * 
 * This component has three states:
 * <ul>
 * <li>Closed: the "normal" state where all calls are simply delegated, a successful call
 * resets a failure count a failure increments the failure count.  Once the failure 
 * threshold is exceeded the we trip into the Open state.
 * </li>
 * <li>Open: all calls will now fail-fast and not delegated: a CircuitBreakerException will be
 * thrown for fail-fast.  Once a timeout expires we will attempt to recover by moving 
 * into the Half Open state.
 * </li>
 * <li>Half Open: allow a single delegate call, if this fails then trip back to the Open
 * state again.  If the call succeeds then resume normal service by moving back to the 
 * Closed state.
 * </li>
 * </ul>
 * 
 * <p>
 * When failing fast this implementation will throw a {@link CircuitBreakerException} which is a RuntimeException
 * which may have an effect on a client that is not expecting this failure mode.  You should ensure that
 * your client will behave appropriately in this case.
 * </p>
 * <p>
 * Consecutive failures is the strategy used to determine if we should trip the breaker while
 * in the Closed state.  This value can be set and defaults to 10, see {@link #setFailureThreshold(int)}.
 * Alternate implementations could use different strategies such as the Leaky Bucket pattern.
 * </p>
 * <p>
 * The timeout in the Open state can be set and defaults to 30000 milliseconds (30 seconds), 
 * see {@link #setAttemptResetTimeoutMillis(int)}.
 * </p>
 * <p>
 * This implementation is written as a Spring AOP aspect and may be wired as shown below.  
 * You should use a single breaker instance per service - it makes no sense to combine the 
 * success/failure information from disparate services into a single breaker instance.
 * </p>
 * <p>
 * Each breaker can be given a name that will be used in logging - which helps an Operations
 * department identify the underlying service that has caused state changes within the breaker, see
 * {@link #getName()} and {@link #setName(String)}.
 * </p>
 * <p>
 * <pre>
 *  &lt;bean name="aServiceCircuitBreaker" class="com.elasticpath.commons.qos.breaker.CircuitBreakerAspect"&gt;
 *      &lt;property name="name" value="A Service CircuitBreaker" /&gt;
 *  &lt/bean&gt;
 *
 *  &lt;bean class="org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator"&gt;
 *      &lt;property name="beanNames" value="aServiceThatMayFail"&gt;&lt;/property&gt;
 *      &lt;property name="interceptorNames"&gt;
 *          &lt;list&gt;
 *              &lt;value&gt;aServiceCircuitBreaker&lt;/value&gt;
 *          &lt;/list&gt;
 *      &lt;/property&gt;
 * &lt;/bean&gt;
 * 
 * </pre>
 * </p>
 * <p> 
 * This component implementation is inspired by the Release It! book by Michael Nygard.
 * </p>
 */
public class CircuitBreakerAspect implements MethodInterceptor {

	private static final Logger LOG = Logger.getLogger(CircuitBreakerAspect.class);
	
	private final OpenState openState = new OpenState();

	private final ClosedState closedState = new ClosedState();

	private final HalfOpenState halfOpenState = new HalfOpenState();

	private final AtomicReference<AbstractBreakerState> state = new AtomicReference<AbstractBreakerState>(closedState);

	private String name = "anonymous";
	
	private boolean setState(final AbstractBreakerState currentState, final AbstractBreakerState state) {
		if (currentState.equals(state)) {
			return false;
		}
		boolean success = this.state.compareAndSet(currentState, state);
		if (success && LOG.isInfoEnabled()) {
			LOG.info("CircuitBreaker:'" + getName() + "' changing state from " + currentState + " to " + state);
		}
		return success;
	}

	/**
	 * Trip the breaker into the Open state.
	 */
	public void trip() {
		openState.enterState(state.get());
	}


	/**
	 * Attempt a call to the delegate wrapped by the current breaker state.
	 * 
	 * @param invocation the description of the method to invoke.
	 * @return the result of the delegate call.
	 * @throws Throwable if there is a failure with the delegate call or if we
	 * 			         are in a fail-fast state.
	 */
	@SuppressWarnings("PMD.AvoidCatchingThrowable")
	public Object invoke(final MethodInvocation invocation) throws Throwable {
		Object result = null;
		try {
			state.get().preInvoke();
			result = invocation.proceed();
			state.get().postInvoke();
		} catch (Throwable throwable) {
			state.get().onError(throwable);
			throw throwable;
		}
		
		return result;
	}

	/**
	 * All internal breaker states should extend this to wrap around the
	 * calls to the delegate. 
	 */
	private abstract class AbstractBreakerState {

		public void preInvoke() {
			// No-op by default
		}

		public void postInvoke() {
			// No-op by default
		}

		public void onError(final Throwable throwable) {
			// No-op by default
		}

		public abstract void enterState(final AbstractBreakerState currentState);
	}
	

	/**
	 * This is the "normal" state of the breaker, requests are passed through to the
	 * delegate until a certain threshold of failures occurs.  Once exceeded we trip
	 * the breaker into the Open state.
	 */
	private class ClosedState extends AbstractBreakerState {

		private static final int DEFAULT_FAILURE_THRESHOLD = 10;
		
		private final AtomicInteger currentFailureCount = new AtomicInteger();

		private final AtomicInteger failureThreshold = new AtomicInteger(DEFAULT_FAILURE_THRESHOLD);

		@Override
		public void preInvoke() {
			// NO OP
		}

		@Override
		public void postInvoke() {
			resetFailureCount();
		}

		private void resetFailureCount() {
			currentFailureCount.set(0);
		}

		@Override
		public void onError(final Throwable throwable) {
			int currentCount = currentFailureCount.incrementAndGet();
			int threshold = failureThreshold.get();
			if (currentCount >= threshold) {
				openState.enterState(this);
			}
		}

		public void setFailureThreshold(final int threshold) {
			this.failureThreshold.set(threshold);
		}
		
		@Override
		public void enterState(final AbstractBreakerState currentState) {
			if (setState(currentState, this)) {
				resetFailureCount();
			}
		}
		
		@Override
		public String toString() {
			return "CLOSED";
		}
	};

	/**
	 * In this state all requests will fail-fast until a timeout expires at which
	 * point we will attempt to reset the breaker.
	 */
	private class OpenState extends AbstractBreakerState {

		private static final long DEFAULT_TIMEOUT_MILLIS = 30000;
		
		private final AtomicLong tripTime = new AtomicLong();

		private final AtomicLong timeout = new AtomicLong(DEFAULT_TIMEOUT_MILLIS);

		@Override
		public void preInvoke() {

			long elapsed = System.currentTimeMillis() - tripTime.get();
			if (elapsed > timeout.get()) {
				halfOpenState.enterState(this);
			} else {
				throw new CircuitBreakerException("Circuit Breaker is open; calls are failing fast");
			}
		}

		@Override
		public void enterState(final AbstractBreakerState currentState) {
			tripTime.set(System.currentTimeMillis());
			if (setState(currentState, openState)) {
				LOG.error("CircuitBreaker:'" + getName() + "' has tripped into Open state");
			}
		}
		
		public void setAttemptResetTimeoutMillis(final int timeoutMillis) {
			timeout.set(timeoutMillis);
		}
		
		@Override
		public String toString() {
			return "OPEN";
		}
	};

	/**
	 * The state of the circuit breaker as it attempts to recover from
	 * Open to Closed.  If the attempt fails then we trip the breaker to Open,
	 * otherwise we reset back to Closed.
	 */
	private class HalfOpenState extends AbstractBreakerState {

		@Override
		public void postInvoke() {
			closedState.enterState(this);
		}

		@Override
		public void enterState(final AbstractBreakerState currentState) {
			setState(currentState, this);
		}

		@Override
		public void onError(final Throwable throwable) {
			openState.enterState(this);
			throw new CircuitBreakerException(throwable);
		}
		
		@Override
		public String toString() {
			return "HALF_OPEN";
		}
	}

	/**
	 * Sets the number of failed calls that will cause a trip from closed to open state.
	 * @param threshold the number of delegate calls that must fail consecutively to cause
	 *                  the breaker to trip. 
	 */
	public void setFailureThreshold(final int threshold) {
		closedState.setFailureThreshold(threshold);
	}

	/**
	 * Set the number of milliseconds to stay in the open state until switching
	 * into the half-open state and attempting to reset.
	 * @param timeoutMillis the number of milliseconds to wait in before attempting reset.
	 */
	public void setAttemptResetTimeoutMillis(final int timeoutMillis) {
		openState.setAttemptResetTimeoutMillis(timeoutMillis);
	};
	
	/**
	 * Returns the name of this circuit breaker - each breaker should be named individually
	 * to help an Operations department identify the delegate service that is having problems.
	 * @return the name of this circuit breaker
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the name of this circuit breaker - helps an Operations department which breaker
	 * has tripped in log files and such.
	 * @param name the name for this circuit breaker.
	 */
	public void setName(final String name) {
		this.name = name;		
	}
	
}
