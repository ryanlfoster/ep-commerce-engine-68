package com.elasticpath.common.dto.assembler;

import java.util.TimeZone;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * A simple JAXB adapter that converts between TimeZone and String.
 */
public class TimeZoneXmlAdapter extends XmlAdapter<String, TimeZone> {

	@Override
	public TimeZone unmarshal(final String timezone) throws Exception {
		return TimeZone.getTimeZone(timezone);
	}

	@Override
	public String marshal(final TimeZone timezone) throws Exception {
		return timezone.getID();
	}

}
