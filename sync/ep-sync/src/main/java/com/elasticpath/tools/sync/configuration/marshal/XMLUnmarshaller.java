package com.elasticpath.tools.sync.configuration.marshal;

import java.io.InputStream;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.stream.XMLStreamReader;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import com.elasticpath.tools.sync.exception.SyncToolRuntimeException;

/**
 * <code>XMLUnmarshaller</code> class is responsible for unmarshaling data transfer objects from given Stream.
 */
public class XMLUnmarshaller {

	private static final String CONFIGURATION_ERROR = "Unable to read configuration";
	
	private final Unmarshaller unmarshaller;

	/**
	 * Constructs <code>XMLMarshaller</code> for given classes. 
	 * <p>
	 * Makes initialization of JAXBContext and Marshaller.
	 * 
	 * @param classesToBeBound class or classes for JAXBContext creation.
	 */
	public XMLUnmarshaller(final Class< ? >... classesToBeBound) {
		try {
			final JAXBContext jaxbContext = JAXBContext.newInstance(classesToBeBound);
			unmarshaller = jaxbContext.createUnmarshaller();
		} catch (JAXBException e) {
			throw new SyncToolRuntimeException(CONFIGURATION_ERROR, e);
		}
	}

	/**
	 * Initializes validation xml unmarshaller parameters in order to enables xml validation using appropriate xml schema.  
	 * 
	 * @param schemaUrl string representation of the {@link java.net.URL} of the schema location
	 * @param validationEventHandler the appropriate validation event handler
	 */
	public void initValidationParameters(final String schemaUrl, final ValidationEventHandler validationEventHandler) {
		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		try {
			Schema schema = schemaFactory.newSchema(getClass().getClassLoader().getResource(schemaUrl));
			unmarshaller.setSchema(schema);
			unmarshaller.setEventHandler(validationEventHandler);
		} catch (SAXException e) {
			throw new SyncToolRuntimeException(CONFIGURATION_ERROR, e);
		} catch (JAXBException e) {
			throw new SyncToolRuntimeException(CONFIGURATION_ERROR, e);
		}			
	}

	/**
	 * Unmarshals the object from <code>InputStream</code>.
	 * 
	 * @param source stream for unmarshaling
	 * @param <T> return object type
	 * @return an unmarshalled object.
	 */
	@SuppressWarnings("unchecked")
	public <T> T unmarshall(final InputStream source) {
		try {
			return (T) unmarshaller.unmarshal(source);
		} catch (JAXBException e) {
			throw new SyncToolRuntimeException(CONFIGURATION_ERROR, e);
		}
	}

	/**
	 * Unmarshals the object from <code>XMLStreamReader</code>.
	 * 
	 * @param source XMLStreamReader for unmarshaling
	 * @param <T> return object type	 * 
	 * @return an unmarshalled object.
	 */
	@SuppressWarnings("unchecked")
	public <T> T unmarshall(final XMLStreamReader source) {
		try {
			return (T) unmarshaller.unmarshal(source);
		} catch (JAXBException e) {
			throw new SyncToolRuntimeException(CONFIGURATION_ERROR, e);
		}
	}
}
