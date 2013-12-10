package com.elasticpath.importexport.exporter.exporters.impl;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.elasticpath.common.dto.Dto;
import com.elasticpath.commons.util.impl.JaxbUtils;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.common.marshalling.XMLMarshaller;
import com.elasticpath.importexport.common.util.Message;
import com.elasticpath.importexport.common.util.runner.AbstractPipedStreamRunner;
import com.elasticpath.importexport.exporter.context.DependencyRegistry;
import com.elasticpath.importexport.exporter.context.ExportContext;
import com.elasticpath.importexport.exporter.exportentry.ExportEntry;
import com.elasticpath.importexport.exporter.exportentry.impl.PipedStreamExportEntry;
import com.elasticpath.importexport.exporter.exporters.DependentExporter;
import com.elasticpath.importexport.exporter.exporters.DependentExporterFilter;
import com.elasticpath.importexport.exporter.exporters.Exporter;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.service.impl.AbstractEpServiceImpl;

/**
 * Abstract exporter implements common exporters methods.
 * 
 * @param <DOMAIN> the domain object that should be exported
 * @param <DTO> the DTO that is corresponded to <code>DomainObject</code>
 * @param <SEARCHID> either Long or String depending on the type of id being used for searching. UIDs are of type Long, whereas GUIDs are Strings.
 */
@SuppressWarnings("unchecked")
public abstract class AbstractExporterImpl<DOMAIN extends Persistable, DTO extends Dto, SEARCHID> extends AbstractEpServiceImpl implements
	Exporter, DependentExporterFilter {

	private static final String XML_ENCODING_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>";

	private static final Logger LOG = Logger.getLogger(AbstractExporterImpl.class);

	private List<DependentExporter<? extends Persistable, ? extends Dto, DTO>> dependentExporterList =
			new ArrayList<DependentExporter<? extends Persistable, ? extends Dto, DTO>>();

	private XMLMarshaller xmlMarshaller;

	private ExportContext context;

	private boolean exportFinished;

	private static final String EXTENSION = ".xml";

	private static final int DEFAULT_CHUNK_SIZE = 10;

	private int defaultChunkSize = DEFAULT_CHUNK_SIZE;

	/**
	 * Initialize exporter with services, search criteria and other using export context.
	 * 
	 * @param context export context to get bean factory, export configuration etc.
	 * @throws ConfigurationException if exporter couldn't be initialized
	 */
	@Override
	public void initialize(final ExportContext context) throws ConfigurationException {
		this.context = context;
		this.exportFinished = false;
		this.xmlMarshaller = new XMLMarshaller(JaxbUtils.createClassArray(getDtoClass(), getAuxiliaryJaxbClasses()));
		initializeExporter(context);
		for (DependentExporter<?, ?, ?> dependentExporter : dependentExporterList) {
			dependentExporter.initialize(context, this);
		}
	}

	/**
	 * Initializes the exporter. For example, finds the object uids for export and so on.
	 * <p>
	 * Note: Dependency registry is empty in this moment.
	 * 
	 * @param context the context that contains data regarding search criteria, dependency registry and so on.
	 * @throws ConfigurationException if exporter couldn't be initialized
	 */
	protected abstract void initializeExporter(final ExportContext context) throws ConfigurationException;

	/**
	 * Gets the dto class that is corresponded the export domain object.
	 * 
	 * @return the dto class
	 */
	protected abstract Class<? extends DTO> getDtoClass();

	/**
	 * Returns a list of JAXB classes that should be passed to the {@link XMLMarshaller} for use when marshalling the DTO to XML.
	 *
	 * @return a list of JAXB classes (not including the DTO class returned from {@link #getDtoClass()}) to pass to the {@link XMLMarshaller}.
	 */
	protected List<Class<?>> getAuxiliaryJaxbClasses() {
		return Collections.emptyList();
	}

	/**
	 * @return the Entry Name (JobType.TagName+Extension)
	 */
	protected String getEntryName() {
		return getJobType().getTagName() + EXTENSION;
	}

	@Override
	public boolean isFinished() {
		return exportFinished;
	}

	/**
	 * Gets the IDs list of objects that will be exported. It can be a list of uids or guids
	 * <p>
	 * Note: The dependency registry contains in this moment information from another exporters that executed earlier
	 * 
	 * @return the list of uids
	 */
	protected abstract List<SEARCHID> getListExportableIDs();

	/**
	 * Gets the chunk size for export operation.
	 * 
	 * @return the chunk size
	 */
	public int getChunkSize() {
		return defaultChunkSize;
	}

	/**
	 * Sets the chunk size for export operation.
	 * 
	 * @param chunkSize the chunk size
	 */
	public void setChunkSize(final int chunkSize) {
		this.defaultChunkSize = chunkSize;
	}

	/**
	 * Finds the list of domain objects by IDs (uids or guids).
	 * 
	 * @param subList the list of IDs
	 * @return the list of domain objects
	 */
	protected abstract List<DOMAIN> findByIDs(final List<SEARCHID> subList);

	/**
	 * Adds dependencies for objects that also should be exported. The default implementation of this method is empty so exporter that needs to add
	 * dependencies to dependency registry should override this method.
	 * 
	 * @param objects the list of domain objects that will be exported
	 * @param dependencyRegistry the dependency registry
	 */
	protected void addDependencies(final List<DOMAIN> objects, final DependencyRegistry dependencyRegistry) {
		// do nothing
	}

	/**
	 * Gets the domain adapter to transform data from domain object to DTO.
	 * 
	 * @return the appropriate domain adapter
	 */
	protected abstract DomainAdapter<DOMAIN, DTO> getDomainAdapter();

	/**
	 * Adds dependent exporter to the list of dependent exporters which will be executed during export operation.
	 * 
	 * @param dependentExporter the dependentExporter to add
	 */
	protected void addDependentExporter(final DependentExporter<? extends Persistable, ? extends Dto, DTO> dependentExporter) {
		dependentExporterList.add(dependentExporter);
	}

	public void setDependentExporters(final List<DependentExporter<? extends Persistable, ? extends Dto, DTO>> dependentExporters) {
		dependentExporterList = dependentExporters;
	}

	/**
	 * Removes all duplicates from the passed in List whilst preserving the order.
	 * 
	 * @param exportedIds the list to remove duplicates from
	 * @see java.util.LinkedHashSet
	 */
	protected void removeDuplicatesFromExportableIDs(final List<SEARCHID> exportedIds) {
		Set<SEARCHID> noDupes = new LinkedHashSet<SEARCHID>(exportedIds);
		if (noDupes.size() < exportedIds.size()) {
			exportedIds.clear();
			exportedIds.addAll(noDupes);
		}
	}

	/**
	 * Actual export processing.
	 * 
	 * @param output output stream to serialize objects into
	 */
	protected void processExport(final OutputStream output) {
		PrintStream printer = null;
		String tagName = null;
		try {
			printer = new PrintStream(output);

			int count = 0;
			tagName = getJobType().getTagName();
			printer.println(XML_ENCODING_HEADER);
			printer.print("<" + tagName + ">");

			List<SEARCHID> listExportedUids = getListExportableIDs();
			removeDuplicatesFromExportableIDs(listExportedUids);

			int index = 0;
			while (index < listExportedUids.size()) {
				List<SEARCHID> subList = new ArrayList<SEARCHID>(getSubList(listExportedUids, index, getChunkSize()));

				index += Math.min(getChunkSize(), listExportedUids.size() - index);

				// extract objects
				List<DOMAIN> exportObjects = Collections.emptyList();
				try {
					exportObjects = findByIDs(subList);
					addDependencies(exportObjects, context.getDependencyRegistry());
				} catch (RuntimeException e) {
					LOG.error(new Message("IE-20500", e, getJobType().toString(), subList.toString()));
				}

				// adapt and marshal objects
				for (DOMAIN object : exportObjects) {
					if (exportObject(object, output)) {
						count++;
					}
				}
				LOG.info("Export " + count + " " + tagName);
			}
		} finally {
			if (printer != null && tagName != null) {
				printer.print("</" + tagName + ">");
				LOG.info("Finish export objects");
			}
			exportFinished = true;
		}
	}

	private <T extends Persistable, K extends Dto, J extends Dto> void exportDependentObject(
			final DependentExporter<T, K, J> dependentExporter,
			final DOMAIN object, final J objectDto) {
		List<T> dependentObjects = dependentExporter.findDependentObjects(object.getUidPk());
		List<K> dependentDtoList = new ArrayList<K>();
		DomainAdapter<T, K> domainAdapter = dependentExporter.getDomainAdapter();

		for (T dependentObject : dependentObjects) {
			K dependentDtoObject = domainAdapter.createDtoObject();
			domainAdapter.populateDTO(dependentObject, dependentDtoObject);
			dependentDtoList.add(dependentDtoObject);
		}

		if (!dependentDtoList.isEmpty()) {
			dependentExporter.bindWithPrimaryObject(dependentDtoList, objectDto);
		}
	}

	/*
	 * Populate product DTO from product to be exported and marshall it.
	 */
	private boolean exportObject(final DOMAIN object, final OutputStream output) {
		try {
			DTO primaryDtoObject = getDomainAdapter().createDtoObject();
			getDomainAdapter().populateDTO(object, primaryDtoObject);

			for (DependentExporter<? extends Persistable, ? extends Dto, DTO> dependentExporter : dependentExporterList) {
				exportDependentObject(dependentExporter, object, primaryDtoObject);
			}

			xmlMarshaller.marshal(primaryDtoObject, output);
			context.getSummary().addToCounter(getJobType(), getObjectsQty(object));
			return true;
		} catch (Exception e) {
			exportFailureHandler(object);
			return false;
		}
	}

	/**
	 * Gets the quantity of objects. If export of value objects occurs, appropriate exporter can override this method to count exported objects
	 * correctly.
	 * 
	 * @param domain the domain object.
	 * @return 1 in default implementation
	 */
	protected int getObjectsQty(final DOMAIN domain) {
		return 1;
	}

	/**
	 * This method calls if error happens during export one object, it can be overriden for log purpose and so on.
	 * 
	 * @param object the object that can not be exported
	 */
	protected void exportFailureHandler(final DOMAIN object) {
		LOG.error(new Message("IE-20501", getJobType().toString(), String.valueOf(object.getUidPk())));
	}

	/*
	 * Gets sublist of UIDs.
	 */
	private List<SEARCHID> getSubList(final List<SEARCHID> list, final int startIndex, final int size) {
		int toIndex = startIndex + size;
		if (list.size() < toIndex) {
			toIndex = list.size();
		}
		return list.subList(startIndex, toIndex);
	}

	@Override
	public ExportEntry executeExport() {
		return new PipedStreamExportEntry(getEntryName(), new AbstractPipedStreamRunner() {
			@Override
			protected void runInternal(final OutputStream outputStream) {
				processExport(outputStream);
			}
		});
	}

	/**
	 * Gets the context.
	 * 
	 * @return the context
	 */
	public ExportContext getContext() {
		return context;
	}

	/**
	 * This implementation filters nothing, subclasses should override to provide filtering logic.
	 * 
	 * @param primaryObjectUid {@inerheritDoc}
	 * @return {@inheritDoc}
	 */
	@Override
	public boolean isFiltered(final long primaryObjectUid) {
		return false;
	}
}
