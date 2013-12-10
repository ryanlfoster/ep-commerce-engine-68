package com.elasticpath.tools.sync.job.impl;

import static org.junit.Assert.assertEquals;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.tools.sync.job.Command;
import com.elasticpath.tools.sync.job.JobEntry;
import com.elasticpath.tools.sync.job.TransactionJobUnit;
import com.elasticpath.tools.sync.job.descriptor.TransactionJobDescriptorEntry;


/**
*
* Tests for the cache aware job builder impl.
*/
public class CacheAwareTransactionJobBuilderImplTest {

    @Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

    private final CacheAwareTransactionJobBuilderImpl builder = new CacheAwareTransactionJobBuilderImpl();


    /**
     * Tests the happy path for creating a job entry.
     */
    @Test
    public void testHappyPath() {

        final TransactionJobUnit transactionJobUnit = context.mock(TransactionJobUnit.class);
        final TransactionJobDescriptorEntry descriptorEntry = context.mock(TransactionJobDescriptorEntry.class);
        final String name = "aJobEntryName";
        final String guid = "aGuid";
        final Class <?> clazz = ProductImpl.class;
        final Command command = Command.UPDATE;


        context.checking(new Expectations() {
            {
                oneOf(transactionJobUnit).getName(); will(returnValue(name));
                oneOf(descriptorEntry).getGuid(); will(returnValue(guid));
                oneOf(descriptorEntry).getType(); will(returnValue(clazz));
                oneOf(descriptorEntry).getCommand(); will(returnValue(command));

            }
        });

        final JobEntry createdJobEntry = builder.createJobEntry(transactionJobUnit, descriptorEntry);
        assertEquals("job entry name not equal", name, createdJobEntry.getTransactionJobUnitName());
        assertEquals("job entry guid not equal", guid, createdJobEntry.getGuid());
        assertEquals("job entry class type not equal", clazz, createdJobEntry.getType());
        assertEquals("job entry name command equal", command, createdJobEntry.getCommand());

    }

}
