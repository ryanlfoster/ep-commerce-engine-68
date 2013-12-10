package com.elasticpath.tags.engine

 /**
  * Delegate for handling logical operations.
  * New instances should be created when trying to execute new conditions.
  * 
  * Possible future refactoring may inject this functionality onto
  * the Closure object's metaclass. 
  */
public class OperatorDelegate {
	Object key;

	OperatorDelegate(Object key) {
        this.key = key
	}

    /**
     * Missing operators are treated as false.
     */
    def methodMissing(String name, args) {
        return false
    }
    

	/**
	 * Less than operator.
	 */
	def lessThan(value) {
        key?.value < value
	}

	/**
	 * Greater than operator.
	 */
	def greaterThan(value) {
        key?.value > value
    }

	/**
	 * Less than or equal to.
	 */
	def lessThanOrEqualTo(value) {
        key?.value <= value
    }

	/**
	 * Greater than or equal to.
	 */
	def greaterThanOrEqualTo(value) {
		key?.value >= value
	}

	/**
	 * Equal to. Groovy calls equals() for '==' operator
	 */
	def equalTo(value) {
        key?.value.equals(value)
	}

	/**
	 * Not equal to.
	 */
	def notEqualTo(value) {
		!equalTo(value)
	}

	/**
	 * Includes.
	 */
	def includes(value) {
		key?.value.contains(value)
	}

	/**
	 * Not include.
	 */
	def notIncludes(value) {
		!key?.value.contains(value)
	}

    /**
	 * String contains.
	 */
	def contains(value) {
        includes(value)
	}
    
    /**
     * String equalsIgnoreCase.
     */
    def equalsIgnoreCase(value) {
    	key?.value.equalsIgnoreCase(value)
    }
    
    /**
     * String notEqualsIgnoreCase.
     */
    def notEqualsIgnoreCase(value) {
    	!equalsIgnoreCase(value)
    }
    
    /**
     * String includesIgnoreCase.
     *                             
     */
    def includesIgnoreCase(value) {
    	key?.value.toLowerCase().contains(value.toLowerCase())
    }
    
    /**
     * String includesIgnoreCase.
     *                             
     */
    def notIncludesIgnoreCase(value) {
    	!includesIgnoreCase(value)
    }
    
}