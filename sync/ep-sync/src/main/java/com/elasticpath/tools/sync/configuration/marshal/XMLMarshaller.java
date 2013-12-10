package com.elasticpath.tools.sync.configuration.marshal;

import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.elasticpath.tools.sync.exception.SyncToolRuntimeException;

/**
 * <code>XMLMarshaller</code> class is responsible for marshaling data transfer objects to given <code>OutputStream</code>.
 */
public class XMLMarshaller {

	private final Marshaller marshaller;

	/**
	 * Constructs <code>XMLMarshaller</code> for given classes.
	 * <p>
	 * Makes initialization of JAXBContext and Marshaller.
	 * 
	 * @param classesToBeBound class or classes for JAXBContext creation.
	 */
	public XMLMarshaller(final Class< ? >... classesToBeBound) {
		this(true, classesToBeBound);
	}
	
	/**
	 * Constructs <code>XMLMarshaller</code> for given classes.
	 * <p>
	 * Makes initialization of JAXBContext and Marshaller.
	 * 
	 * @param fragment - JAXB_FRAGMENT if true
	 * @param classesToBeBound class or classes for JAXBContext creation.
	 */
	public XMLMarshaller(final boolean fragment, final Class< ? >... classesToBeBound) {
		try {
			final JAXBContext jaxbContext = JAXBContext.newInstance(classesToBeBound);
			marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.valueOf(fragment));
		} catch (JAXBException e) {
			throw new SyncToolRuntimeException("Unable to populate xml file", e);
		}
	}

	/**
	 * Marshals the source object to <code>OutputStream</code> in xml format.  
	 * 
	 * @param source Object to marshal
	 * @param target stream for marshaling
	 */
	public void marshal(final Object source, final OutputStream target) {
		try {
			marshaller.marshal(source, target);
		} catch (JAXBException e) {
			throw new SyncToolRuntimeException("Unable to populate xml file", e);
		}
	}
}
