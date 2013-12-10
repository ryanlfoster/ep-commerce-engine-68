package com.elasticpath.importexport.exporter.exporters.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.elasticpath.persistence.api.Persistable;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.elasticpath.common.dto.Dto;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.common.summary.Summary;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.exporter.configuration.ExportConfiguration;
import com.elasticpath.importexport.exporter.configuration.search.SearchConfiguration;
import com.elasticpath.importexport.exporter.context.ExportContext;
import com.elasticpath.importexport.exporter.exporters.DependentExporter;

/**
 * Tests for {@link AbstractExporterImpl}s.
 * 
 * The processExport has a while loop. We test a exporter's isFinish is always set to true 
 * at the end of the method no matter what exceptions are and where they occur. 
 */
@RunWith(JMock.class)
@SuppressWarnings("PMD.NonStaticInitializer")
public class AbstractExporterImplTest {

	private AbstractExporterImpl<Persistable, Dto, Object> abstractExporter;
	private final Mockery context = new JUnit4Mockery();
	private static int mockCounter;
	private List<Object> exportableObjectIds = new ArrayList<Object>();
	private DomainAdapter<Persistable, Dto> domainAdapter;
	private final List<Persistable> exportableObjects = new ArrayList<Persistable>();

	private List<Persistable> getExportableObjects() {
		return exportableObjects;
	}

	private List<Object> getExportableObjectIds() {
		return exportableObjectIds;
	}

	/** {@link Dto} class for tests. */
	@XmlRootElement(name = "test")
	private static final class TestDto implements Dto {
		private static final long serialVersionUID = 1L;
	}

	/** Test initialization. */
	@Before
	public void setUp() {
		@SuppressWarnings("unchecked")
		final DomainAdapter<Persistable, Dto> domainAdapter = context.mock(DomainAdapter.class);
		this.domainAdapter = domainAdapter;

		abstractExporter = new AbstractExporterImpl<Persistable, Dto, Object>() {
			@Override
			public JobType getJobType() {
				return JobType.PRODUCT;
			}

			@Override
			public Class<?>[] getDependentClasses() {
				return null;
			}

			@Override
			protected void initializeExporter(final ExportContext context) throws ConfigurationException {
				// do nothing
			}

			@Override
			protected Class<? extends Dto> getDtoClass() {
				return TestDto.class;
			}

			@Override
			protected List<Object> getListExportableIDs() {
				return getExportableObjectIds(); // lazy expansion
			}

			@Override
			protected List<Persistable> findByIDs(final List<Object> subList) {
				return getExportableObjects(); // lazy expansion
			}

			@Override
			protected DomainAdapter<Persistable, Dto> getDomainAdapter() {
				return domainAdapter;
			}
		};
	}

	@SuppressWarnings("unchecked")
	private <T extends Persistable, J extends Dto, K extends Dto> DependentExporter<T, J, K> mockDependentExporter() {
		return context.mock(DependentExporter.class, "dependentExporter-" + ++mockCounter);
	}

	/**
	 * {@link DependentExporter}s should be initialized as part of
	 * {@link AbstractExporterImpl#initialize(ExportContext)} when they are setup with the provided setter.
	 * 
	 * @throws ConfigurationException in case of errors
	 */
	@Test
	public void testDependentExportersInitializedViaSet() throws ConfigurationException {
		final DependentExporter<Persistable, Dto, Dto> dependentExporter1 = mockDependentExporter();
		final DependentExporter<Persistable, Dto, Dto> dependentExporter2 = mockDependentExporter();

		List<DependentExporter<? extends Persistable, ? extends Dto, Dto>> dependentExporters =
				new ArrayList<DependentExporter<? extends Persistable, ? extends Dto, Dto>>();
		dependentExporters.add(dependentExporter1);
		dependentExporters.add(dependentExporter2);

		abstractExporter.setDependentExporters(dependentExporters);

		final ExportContext exportContext = new ExportContext(new ExportConfiguration(), new SearchConfiguration());
		context.checking(new Expectations() {
			{
				one(dependentExporter1).initialize(exportContext, abstractExporter);
				one(dependentExporter2).initialize(exportContext, abstractExporter);
			}
		});
		abstractExporter.initialize(exportContext);
	}

	/**
	 * {@link DependentExporter}s should be initialized as part of
	 * {@link AbstractExporterImpl#initialize(ExportContext)} when they are setup with the provided add method.
	 * 
	 * @throws ConfigurationException in case of errors
	 */
	@Test
	public void testDependentExportersInitializedViaAdd() throws ConfigurationException {
		final DependentExporter<Persistable, Dto, Dto> dependentExporter1 = mockDependentExporter();
		final DependentExporter<Persistable, Dto, Dto> dependentExporter2 = mockDependentExporter();

		abstractExporter.addDependentExporter(dependentExporter2);
		abstractExporter.addDependentExporter(dependentExporter1);

		final ExportContext exportContext = new ExportContext(new ExportConfiguration(), new SearchConfiguration());
		context.checking(new Expectations() {
			{
				one(dependentExporter1).initialize(exportContext, abstractExporter);
				one(dependentExporter2).initialize(exportContext, abstractExporter);
			}
		});
		abstractExporter.initialize(exportContext);
	}

	/**
	 * {@link DependentExporter}s should be initialized as part of
	 * {@link AbstractExporterImpl#initialize(com.elasticpath.importexport.exporter.context.ExportContext)} when they
	 * are setup with the provided via add and set.
	 * 
	 * @throws ConfigurationException in case of errors
	 */
	@Test
	public void testDependentExportersInitializedViaAddAndSet() throws ConfigurationException {
		final DependentExporter<Persistable, Dto, Dto> dependentExporter1 = mockDependentExporter();
		final DependentExporter<Persistable, Dto, Dto> dependentExporter2 = mockDependentExporter();

		List<DependentExporter<? extends Persistable, ? extends Dto, Dto>> dependentExporters =
				new ArrayList<DependentExporter<? extends Persistable, ? extends Dto, Dto>>();
		dependentExporters.add(dependentExporter2);
		abstractExporter.setDependentExporters(dependentExporters);
		abstractExporter.addDependentExporter(dependentExporter1);

		final ExportContext exportContext = new ExportContext(new ExportConfiguration(), new SearchConfiguration());
		context.checking(new Expectations() {
			{
				one(dependentExporter1).initialize(exportContext, abstractExporter);
				one(dependentExporter2).initialize(exportContext, abstractExporter);
			}
		});
		abstractExporter.initialize(exportContext);
	}


	/**
	 * {@link DependentExporter}s should be initialized as part of
	 * {@link AbstractExporterImpl#initialize(ExportContext)} when they are setup with the provided via add and set. If
	 * set is called after add, the added {@link DependentExporter}s should be ignored.
	 * 
	 * @throws ConfigurationException in case of errors
	 */
	@Test
	public void testDependentExportersInitializedSetOverridesAdd() throws ConfigurationException {
		final DependentExporter<Persistable, Dto, Dto> dependentExporter1 = mockDependentExporter();
		final DependentExporter<Persistable, Dto, Dto> dependentExporter2 = mockDependentExporter();

		List<DependentExporter<? extends Persistable, ? extends Dto, Dto>> dependentExporters =
				new ArrayList<DependentExporter<? extends Persistable, ? extends Dto, Dto>>();
		dependentExporters.add(dependentExporter2);
		abstractExporter.addDependentExporter(dependentExporter1);
		abstractExporter.setDependentExporters(dependentExporters);

		final ExportContext exportContext = new ExportContext(new ExportConfiguration(), new SearchConfiguration());
		context.checking(new Expectations() {
			{
				one(dependentExporter2).initialize(exportContext, abstractExporter);
			}
		});
		abstractExporter.initialize(exportContext);
	}

	/**
	 * Tests {@link AbstractExporterImpl#processExport(java.io.OutputStream)} will set
	 * {@link AbstractExporterImpl#isFinished()} to true with Exception on the while loop condition. The while loop is
	 * not executed and NullPointerException is thrown.
	 */
	@Test(expected = NullPointerException.class)
	public void testProcessExportWithExceptionOnWhileLoop() {
		exportableObjectIds = null;
		abstractExporter.processExport(System.out);
	}

	/**
	 * Test processExport will set exporter.isFinished to true with Exception inside the while loop.
	 * The while loop is executed, and there are several Exceptions could be thrown inside the while loop
	 * during exporting domain entities.
	 */
	@Test
	public void testProcessExportWithExceptionInsideWhileLoop() {
		final Persistable persistable = context.mock(Persistable.class);
		context.checking(new Expectations() {
			{
				allowing(persistable);
			}
		});

		exportableObjectIds.add(1L);
		exportableObjects.add(persistable);

		context.checking(new Expectations() {
			{
				TestDto dto = new TestDto();
				allowing(domainAdapter).createDtoObject();
				will(returnValue(dto));

				allowing(domainAdapter).populateDTO(persistable, dto);
			}
		});

		abstractExporter.processExport(System.out);
		assertEquals(abstractExporter.getListExportableIDs().size(), 1);
		assertTrue(abstractExporter.isFinished());
	}
	
	/**
	 * Test that {@link AbstractExporterImpl#removeDuplicatesFromExportableIDs(List)} will remove all duplicates from
	 * the passed in {@link List} with passed a list of {@link Long}s.
	 */
	@Test
	public void testRemoveDuplicatesLongFromExportableIDs() {
		//CHECKSTYLE:OFF
		List<Object> exportedIds = new ArrayList<Object>(Arrays.asList(1L, 1L, 2L, 1L, 3L, 4L, 3L));
		
		List<Object> beforeRemoveDups = exportedIds;
		abstractExporter.removeDuplicatesFromExportableIDs(exportedIds);
	
		assertEquals(4, exportedIds.size());
		assertSame(beforeRemoveDups, exportedIds);
		//CHECKSTYLE:ON
	}

	/**
	 * Test that {@link AbstractExporterImpl#removeDuplicatesFromExportableIDs(List)} will remove all duplicates from
	 * the passed in {@link List} with passed a list of {@link String}s.
	 */
	@Test
	public void testRemoveDuplicatesStringFromExportableIDs() {
		//CHECKSTYLE:OFF
		List<Object> exportedIds = new ArrayList<Object> ();
		exportedIds.add("adam"); exportedIds.add("jing"); //NOPMD
		exportedIds.add("adam"); exportedIds.add("jing"); //NOPMD
		exportedIds.add("greg"); exportedIds.add("greg"); //NOPMD
		exportedIds.add("yuri"); exportedIds.add("karl"); //NOPMD
		exportedIds.add("karl"); exportedIds.add("dave"); //NOPMD
		
		List<Object> beforeRemoveDups = exportedIds;
		abstractExporter.removeDuplicatesFromExportableIDs(exportedIds);
		
		assertEquals(6, exportedIds.size());
		assertSame(beforeRemoveDups, exportedIds);
		//CHECKSTYLE:ON
	}

	/**
	 * Test that {@link AbstractExporterImpl#removeDuplicatesFromExportableIDs(List)} will not remove anything when
	 * there are no duplicates. 
	 */
	@Test
	public void testRemoveDuplicatesStringFromExportableIDsHasNoDuplicates() {
		//CHECKSTYLE:OFF
		List<Object> exportedIds = new ArrayList<Object> ();
		exportedIds.add("adam"); exportedIds.add("jing"); //NOPMD
		exportedIds.add("karl"); exportedIds.add("dave"); //NOPMD

		List<Object> beforeRemoveDups = exportedIds;
		abstractExporter.removeDuplicatesFromExportableIDs(exportedIds);

		assertEquals(4, exportedIds.size());
		assertSame(beforeRemoveDups, exportedIds);
		//CHECKSTYLE:ON
	}
	
	/**
	 * Test that {@link AbstractExporterImpl#removeDuplicatesFromExportableIDs(List)} will remove all duplicates from
	 * the passed in List and that original insertion ordering will be preserved.
	 */
	@Test
	public void testRemoveDuplicatesStringFromExportableIDsPreservesOrder() {
		//CHECKSTYLE:OFF
		List<Object> exportedIds = new ArrayList<Object> ();
		exportedIds.add("adam"); exportedIds.add("jing"); //NOPMD
		exportedIds.add("adam"); exportedIds.add("jing"); //NOPMD
		exportedIds.add("greg"); exportedIds.add("greg"); //NOPMD
		exportedIds.add("yuri"); exportedIds.add("karl"); //NOPMD
		exportedIds.add("karl"); exportedIds.add("dave"); //NOPMD

		List<Object> beforeRemoveDups = exportedIds;
		abstractExporter.removeDuplicatesFromExportableIDs(exportedIds);
		
		assertEquals(6, exportedIds.size());
		assertSame(beforeRemoveDups, exportedIds);
		assertEquals("adam" , exportedIds.get(0));
		assertEquals("jing" , exportedIds.get(1));
		assertEquals("greg" , exportedIds.get(2));
		assertEquals("yuri" , exportedIds.get(3));
		assertEquals("karl" , exportedIds.get(4));
		assertEquals("dave" , exportedIds.get(5));
		//CHECKSTYLE:ON
	}
	
	/**
	 * Test that {@link AbstractExporterImpl#removeDuplicatesFromExportableIDs(List)} will remove all duplicates from
	 * the passed in List and that original insertion ordering will be preserved.
	 */
	@Test
	public void testRemoveDuplicatesLongFromExportableIDsPreservesOrder() {
		//CHECKSTYLE:OFF
		List<Object> exportedIds = new ArrayList<Object> ();
		exportedIds.add(new Long(230)); exportedIds.add(new Long(9921)); //NOPMD
		exportedIds.add(new Long(1)); exportedIds.add(new Long(66)); //NOPMD
		exportedIds.add(new Long(3301)); exportedIds.add(new Long(9921)); //NOPMD
		exportedIds.add(new Long(231)); exportedIds.add(new Long(230)); //NOPMD
		exportedIds.add(new Long(66)); exportedIds.add(new Long(1)); //NOPMD
		exportedIds.add(new Long(9921)); exportedIds.add(new Long(100000001)); //NOPMD

		List <Object> beforeRemoveDups = exportedIds;
		abstractExporter.removeDuplicatesFromExportableIDs(exportedIds);
		
		assertEquals(7, exportedIds.size());
		assertSame(beforeRemoveDups, exportedIds);
		assertEquals(new Long(230) , exportedIds.get(0));
		assertEquals(new Long(9921) , exportedIds.get(1));
		assertEquals(new Long(1) , exportedIds.get(2));
		assertEquals(new Long(66) , exportedIds.get(3));
		assertEquals(new Long(3301) , exportedIds.get(4));
		assertEquals(new Long(231) , exportedIds.get(5));
		assertEquals(new Long(100000001) , exportedIds.get(6));
		//CHECKSTYLE:ON
	}

	/**
	 * When there are {@link DependentExporter}s available, they should be called as part of a regular
	 * {@link AbstractExporterImpl#processExport(java.io.OutputStream)}.
	 * 
	 * @throws ConfigurationException in case of errors
	 */
	@Test
	public void testExportWithDependentExporters() throws ConfigurationException {
		final Persistable persistable = context.mock(Persistable.class);
		context.checking(new Expectations() {
			{
				allowing(persistable);
			}
		});

		exportableObjectIds.add(1L);
		exportableObjects.add(persistable);

		@SuppressWarnings("unchecked")
		final DomainAdapter<Persistable, Dto> dependentAdapter = context.mock(DomainAdapter.class, "dependentAdapter");
		final DependentExporter<Persistable, Dto, Dto> dependentExporter = mockDependentExporter();
		abstractExporter.addDependentExporter(dependentExporter);

		final Summary summary = context.mock(Summary.class);
		final ExportContext exportContext = new ExportContext(new ExportConfiguration(), new SearchConfiguration());
		exportContext.setSummary(summary);

		context.checking(new Expectations() {
			{
				allowing(dependentExporter).initialize(exportContext, abstractExporter);
				allowing(dependentExporter).getDomainAdapter();
				will(returnValue(dependentAdapter));
			}
		});
		abstractExporter.initialize(exportContext);

		context.checking(new Expectations() {
			{
				TestDto dependentDto = new TestDto();
				TestDto dto = dependentDto;
				allowing(domainAdapter).createDtoObject();
				will(returnValue(dto));

				allowing(domainAdapter).populateDTO(persistable, dto);

				Persistable dependentObject = context.mock(Persistable.class, "dependentObject");
				one(dependentExporter).findDependentObjects(with(any(Long.class)));
				will(returnValue(Collections.singletonList(dependentObject)));

				allowing(dependentAdapter).createDtoObject();
				will(returnValue(dependentDto));
				allowing(dependentAdapter).populateDTO(dependentObject, dependentDto);
				one(dependentExporter).bindWithPrimaryObject(Collections.<Dto> singletonList(dependentDto), dto);

				// if you're missing this one, the object failed to export (exceptions are silently caught)
				one(summary).addToCounter(abstractExporter.getJobType(), 1);
			}
		});

		abstractExporter.processExport(System.out);
	}
}
