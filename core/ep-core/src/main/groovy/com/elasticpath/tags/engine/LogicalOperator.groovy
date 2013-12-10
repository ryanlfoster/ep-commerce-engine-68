package com.elasticpath.tags.engine

/**
 * Command class for Logical Operations. 
 */
public class LogicalOperator {
	 
    /**
     * AND operator. Iterates through any child conditions and invokes as closures.
     */
    def AND (Object [] params) {
        def result = true
        params.each {
            result = it() && result
        }
        return result
    }
    

    /**
     * OR operator. Iterates through any child conditions and invokes as closures.
     */
    def OR (Object [] params) {
        def result = false
        params.each {
            result = it() || result
        }
        return result
    }
              
}