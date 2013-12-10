package com.elasticpath.tags.engine

/**
 * Delegate class for resolving DSL properties to values within a map.
 * e.g. location.contains will call contains() on the value of "location" in the map.
 * 
 * New instances of this class should be created with a given map of string values, and run
 * by passing a condition DSL into run(). 
 */
 public class MapRunner {
	def myMap

	def MapRunner(map) {
		myMap = map
	}

	/**
	 * Property lookups through the DSL are routed to items in the map. If no item exist, a dummy object is used.
	 */
	def propertyMissing(String name) {
	    !myMap.get(name) ?new NullProperty(): new OperatorDelegate(myMap.get(name))
	}

	/**
	 * Execution method to be invoked with DSL closure.
	 */
	def run(Closure closure) {
		closure.delegate = this
		closure()
	}


}

/**
 * Non existent keys will cause conditions to evaluate to false.
 */
class NullProperty {
    /**
     * Intercept all operator calls and return false.
     */
    def methodMissing(String name, args) {
        false
    }
}