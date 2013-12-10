/**
 * Copyright (c) 2009. ElasticPath Software Inc. All rights reserved.
 */
package com.elasticpath.tags.builder;

import com.elasticpath.tags.domain.TagDefinition;

/**
 * A custom string class that's metaclass will be changing in the run-time.
 */
public class BuilderString {
	private TagDefinition key;
	
	/**
	 * Default constructor which sets the active key name.
	 */
	public BuilderString(TagDefinition key) {
		this.key = key;
	}
	
	/**
	 * Delegates any method invocation to the LogicalTreeBuilder#addCondition.
	 */
	def methodMissing(String name, args) {
		LogicalTreeBuilder.getInstance().addCondition(this.key, args, name);
	}
}
