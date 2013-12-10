package com.elasticpath.commons.listeners;

import com.elasticpath.tags.Tag;

/**
 * Interface for listeners of tag set events.
 */
public interface TagSetListener {

	/**
	 * generic event handler.
	 * @param key the Tag guid
	 * @param tag the tag
	 */
	void onEvent(String key, Tag tag);
	
}
