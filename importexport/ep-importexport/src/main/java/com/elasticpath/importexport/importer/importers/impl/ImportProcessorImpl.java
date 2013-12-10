package com.elasticpath.importexport.importer.importers.impl;

import java.io.InputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionException;

import com.elasticpath.common.dto.Dto;
import com.elasticpath.commons.util.impl.JaxbUtils;
import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.common.exception.runtime.ImportDuplicateAssociatedEntityRuntimeException;
import com.elasticpath.importexport.common.exception.runtime.ImportDuplicateEntityRuntimeException;
import com.elasticpath.importexport.common.exception.runtime.ImportRuntimeException;
import com.elasticpath.importexport.common.exception.runtime.MarshallingRuntimeException;
import com.elasticpath.importexport.common.exception.runtime.PopulationRollbackException;
import com.elasticpath.importexport.common.exception.runtime.PopulationRuntimeException;
import com.elasticpath.importexport.common.marshalling.DefaultValidationEventHandler;
import com.elasticpath.importexport.common.marshalling.XMLUnmarshaller;
import com.elasticpath.importexport.common.summary.Summary;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.common.util.Message;
import com.elasticpath.importexport.importer.context.ImportContext;
import com.elasticpath.importexport.importer.importers.ImportProcessor;
import com.elasticpath.importexport.importer.importers.Importer;
import com.elasticpath.importexport.importer.importers.ImporterFactory;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.persistence.api.PersistenceSession;
import com.elasticpath.persistence.api.Transaction;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.impl.AbstractEpPersistenceServiceImpl;

/**
 * This class is responsible for import processing.
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
public class ImportProcessorImpl extends AbstractEpPersistenceServiceImpl implements ImportProcessor {

	private static final Logger LOG = Logger.getLogger(ImportProcessorImpl.class);

	private ImporterFactory importerFactory;

	private DefaultValidationEventHandler validationEventHandler;

	private SavingManager<? extends Persistable> savingManager;

	@Override
	public void process(final InputStream entryToImport, final ImportContext context) throws ConfigurationException {
		XMLStreamReader streamReader = null;
		try {
			final Summary summary = context.getSummary();
			streamReader = XMLInputFactory.newInstance().createXMLStreamReader(entryToImport);
			streamReader.nextTag();
			streamReader.require(XMLStreamConstants.START_ELEMENT, null, null);
			final JobType jobType = JobType.getJobTypeByTag(streamReader.getLocalName());

			LOG.info("Begin importing " + jobType);
			final Importer<? super Persistable, ? super Dto> importer = importerFactory.createImporter(jobType, context, savingManager);

			runImport(context, streamReader, summary, jobType, importer);

			importer.postProcessingImportHandling();

		} catch (XMLStreamException exception) {
			throw new ImportRuntimeException("IE-30403", exception);
		} finally {
			if (streamReader != null) {
				try {
					streamReader.close();
				} catch (XMLStreamException e) {
					LOG.error("Could not close xml stream reader", e);
				}
			}
		}
	}

	/**
	 * Runs an import of the given <code>jobType</code> from the given <code>streamReader</code>.
	 *
	 * @param context Import configuration settings
	 * @param streamReader XML stream reader setup with the file with data to import from
	 * @param summary Contains summary details of the import of <code>jobType</code>
	 * @param jobType Type of entity being imported
	 * @param importer The importer to use to do the import
	 * @throws XMLStreamException
	 */
	private void runImport(final ImportContext context,
			final XMLStreamReader streamReader,
			final Summary summary,
			final JobType jobType,
			final Importer<? super Persistable, ? super Dto> importer) throws XMLStreamException {
		final Class<?>[] jaxbClasses = JaxbUtils.createClassArray(importer.getDtoClass(), importer.getAuxiliaryJaxbClasses());

		final XMLUnmarshaller unmarshaller = createUnmarshaller(jaxbClasses,
				context.getImportConfiguration().isXmlValidation(),
				importer.getSchemaPath());

		streamReader.nextTag();

		PersistenceSession session = getPersistenceEngine().getSharedPersistenceSession();

		// Continue importing while there are still start tags left in the XML to process for import
		while (streamReader.getEventType() == XMLStreamConstants.START_ELEMENT) {
			final Transaction transaction = session.beginTransaction();
			int transactionVolume = 0;
			try {
				for (int idx = 0; idx < importer.getCommitUnit() && (streamReader.getEventType() == XMLStreamConstants.START_ELEMENT); idx++) {
					streamReader.require(XMLStreamConstants.START_ELEMENT, null, importer.getImportedObjectName());
					try {
						Dto unmarshalledObject = unmarshaller.unmarshall(streamReader);
						if (importer.executeImport(unmarshalledObject)) {
							transactionVolume++;
							summary.addToCounter(jobType, importer.getObjectsQty(unmarshalledObject));
						}
					} catch (MarshallingRuntimeException marshallingRuntimeException) {
						LOG.error(new Message("IE-30400", marshallingRuntimeException, jobType.toString(),
								validationEventHandler.getLastErrorStatus()));
						LOG.error(marshallingRuntimeException.getIEMessage());
					} catch (ImportDuplicateEntityRuntimeException duplicateEntityRuntimeException) {
						LOG.error((new Message("IE-30401", duplicateEntityRuntimeException, jobType.toString(),
								importer.getStatusHolder().getImportStatus(), jobType.toString())));
						LOG.error(duplicateEntityRuntimeException.getIEMessage());
					} catch (ImportDuplicateAssociatedEntityRuntimeException ex) {
						LOG.error((new Message("IE-30408", ex, jobType.toString(), importer.getStatusHolder().getImportStatus(),
								jobType.toString())));
						LOG.error(ex.getIEMessage());
					} catch (PopulationRuntimeException exception) {
						LOG.error(exception.getIEMessage());
					}

					skipNotStartElements(streamReader, importer.getImportedObjectName());
				}

				commitTransaction(summary, jobType, importer.getStatusHolder().getImportStatus(), transaction, transactionVolume);

			} catch (PopulationRollbackException exception) {
				LOG.error(exception.getIEMessage());
				summary.addToCounter(jobType, -transactionVolume);
				skipNotStartElements(streamReader, importer.getImportedObjectName());
				try {
					transaction.rollback();
					// Report that the transaction was rolled back.
					// transactionVolume is 0 based so need to increment by 1
					LOG.error((new Message("IE-30407", Integer.toString(transactionVolume + 1), jobType.toString())));
				} catch (TransactionException ex) {
					LOG.error((new Message("IE-30405", ex, Integer.toString(transactionVolume))));
				}
			} catch (Exception exception) {
				LOG.error(new Message("IE-30402", exception, jobType.toString(), importer.getStatusHolder().getImportStatus()));
				summary.addToCounter(jobType, -transactionVolume);
				skipNotStartElements(streamReader, importer.getImportedObjectName());
				try {
					transaction.rollback();
					// Report that the transaction was rolled back.
					// transactionVolume is 0 based so need to increment by 1
					LOG.error((new Message("IE-30407", Integer.toString(transactionVolume + 1), jobType.toString())));
				} catch (TransactionException ex) {
					LOG.error((new Message("IE-30405", ex, Integer.toString(transactionVolume))));
				}
			}
		}
		session.close();
	}

	/**
	 * Attempts to commit the <code>transaction</code> and logs error information if an exception occurs during the commit.
	 *
	 * @param summary Contains summary details of the import of <code>jobType</code>
	 * @param jobType Type of entity that is in the transaction
	 * @param importStatus Identifier of any failing entity in the transaction
	 * @param transaction The batch of entity changes to commit
	 * @param transactionVolume The size of the batch to be committed
	 */
	private void commitTransaction(final Summary summary,
			final JobType jobType,
			final String importStatus,
			final Transaction transaction,
			final int transactionVolume) {

		try {
			LOG.debug("Attempting Transaction Commit");
			transaction.commit();
			LOG.info("Transaction of type " + jobType + " and size " + (transactionVolume + 1) + " was committed.");
		} catch (TransactionException exception) {
			summary.addToCounter(jobType, -transactionVolume);
			LOG.error((new Message("IE-30404", exception, Integer.toString(transactionVolume))));
		} catch (Exception exc) {
			LOG.error(new Message("IE-30402", exc, jobType.toString(), importStatus));
		}

	}

	private XMLUnmarshaller createUnmarshaller(final Class<?>[] jaxbClasses, final boolean xmlValidation,
			final String schemaPath) {
		XMLUnmarshaller unmarshaller = new XMLUnmarshaller(jaxbClasses);
		if (xmlValidation) {
			unmarshaller.initValidationParameters(schemaPath, validationEventHandler);
		}
		return unmarshaller;
	}

	private void skipNotStartElements(final XMLStreamReader streamReader, final String expectedTagName) throws XMLStreamException {
		while ((streamReader.getEventType() != XMLStreamConstants.START_ELEMENT || !streamReader.getLocalName().equals(expectedTagName))
				&& streamReader.hasNext()) {
			streamReader.next();
		}
	}

	@Override
	public Object getObject(final long uid) throws EpServiceException {
		throw new UnsupportedOperationException("unsupported operation");
	}

	public ImporterFactory getImporterFactory() {
		return importerFactory;
	}

	public void setImporterFactory(final ImporterFactory importerFactory) {
		this.importerFactory = importerFactory;
	}

	public DefaultValidationEventHandler getValidationEventHandler() {
		return validationEventHandler;
	}

	public void setValidationEventHandler(final DefaultValidationEventHandler validationEventHandler) {
		this.validationEventHandler = validationEventHandler;
	}

	public SavingManager<? extends Persistable> getSavingManager() {
		return savingManager;
	}

	public void setSavingManager(final SavingManager<? extends Persistable> savingManager) {
		this.savingManager = savingManager;
	}
}
