package com.elasticpath.test.integration;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.elasticpath.test.integration.junit.DatabaseHandlingTestExecutionListener;

/**
 * The DirtiesDatabase annotation is used to identify scenarios where
 * the test database has been mutated and should be reset.
 * When placed on class level it will be active for all methods as well.
 * 
 * See {@link DatabaseHandlingTestExecutionListener}.
 * @author eheath
 */
@Target( { ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DirtiesDatabase {

}