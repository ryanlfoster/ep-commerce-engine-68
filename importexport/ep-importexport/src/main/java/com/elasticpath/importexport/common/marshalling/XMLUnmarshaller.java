package com.elasticpath.importexport.common.marshalling;

import java.io.File;
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

import com.elasticpath.importexport.common.exception.runtime.MarshallingRuntimeException;

/**
 * <code>XMLUnmarshaller</code> class is responsible for unmarshaling data transfer objects from given Stream.
 */
public class XMLUnmarshaller {

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
			throw new MarshallingRuntimeException("IE-40200", e);
		}
	}

	/**
	 * Initializes validation xml unmarshaller parameters in order to enables xml validation using appropriate xml schema.  
	 * 
	 * @param schemaFileName the schema file name for validation
	 * @param validationEventHandler the appropriate validation event handler
	 */
		
	public void initValidationParameters(final String schemaFileName, final ValidationEventHandler validationEventHandler) {
		
		unmarshaller.setSchema(findSchema(schemaFileName));
		
		try {
			unmarshaller.setEventHandler(validationEventHandler);
		} catch (JAXBException e) {
			throw new MarshallingRuntimeException("IE-40204", e);
		}
	}

	/**
	 * Unmarshals the object from <code>InputStream</code>.
	 * 
	 * @param source stream for unmarshaling
	 * @param <T> the Java type of the unmarshalled object
	 * @return an unmarshalled object.
	 */
	@SuppressWarnings("unchecked")
	public <T> T unmarshall(final InputStream source) {
		try {
			return (T) unmarshaller.unmarshal(source);
		} catch (JAXBException e) {
			throw new MarshallingRuntimeException("IE-40202", e);
		}
	}

	/**
	 * Unmarshals the object from <code>XMLStreamReader</code>.
	 *
	 * @param source XMLStreamReader for unmarshaling
	 * @param <T> the Java type of the unmarshalled object
	 * @return an unmarshalled object.
	 */
	@SuppressWarnings("unchecked")
	public <T> T unmarshall(final XMLStreamReader source) {
		try {
			return (T) unmarshaller.unmarshal(source);
		} catch (JAXBException e) {
			throw new MarshallingRuntimeException("IE-40202", e);
		} catch (NumberFormatException e) {
			throw new MarshallingRuntimeException("IE-40205", e, e.getMessage());
		}
	}

	/**
	 * Attempts to load schemaFileName from the filesystem, and if that fails, attempts to load it from a classloader.
	 * 
	 * @param schemaFileName the name (and optional relative path) of the XSD to load
	 * 
	 * @return a {@code Schema} instance
	 * @throws MarshallingRuntimeException (IE-40203)
	 */
	private Schema findSchema(final String schemaFileName) {
		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

		Schema schema;

		try {
			schema = schemaFactory.newSchema(new File(schemaFileName));
		} catch (SAXException e) {
			try {
				schema = schemaFactory.newSchema(getClass().getClassLoader()
						.getResource(schemaFileName));
			} catch (SAXException e2) {
				throw new MarshallingRuntimeException("IE-40203", e2, schemaFileName); // NOPMD
			}
		}
		return schema;
	}

}
