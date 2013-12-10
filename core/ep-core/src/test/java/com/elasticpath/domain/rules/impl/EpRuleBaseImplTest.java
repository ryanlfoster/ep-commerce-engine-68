package com.elasticpath.domain.rules.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.io.StringReader;

import org.junit.Test;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.common.DroolsObjectInputStream;
import org.drools.common.DroolsObjectOutputStream;
import org.drools.compiler.PackageBuilder;
import org.drools.rule.Package;

/**
 * Test case for {@link EpRuleBaseImpl}.
 */
public class EpRuleBaseImplTest {


	/**
	 * Test method for externalizers of {@link EpRuleBaseImpl} for the Reteoo {@link RuleBase}s
	 * it stores.
	 *
	 * @throws Exception in case of any errors
	 */
	@Test
	public void testRuleBaseExternalizersReteoo() throws Exception {
		final RuleBase ruleBase = RuleBaseFactory.newRuleBase(RuleBase.RETEOO);

		addDefaultRuleToRuleBase(ruleBase);
		testDefaultRule(ruleBase);

		final RuleBase serializedRuleBase = EpRuleBaseImpl.ruleBaseFactory(EpRuleBaseImpl.externalizeRuleBase(ruleBase));
		testDefaultRule(serializedRuleBase);
	}

	/**
	 * This test makes sure that an externally compiled rule base can be deserialized and
	 * reserialized whilst maintaining its ability to perform the rule.
	 *
	 * @throws Exception in case of errors
	 */
	@Test
	public void testWriteReadDifferentRuleBase() throws Exception {
		RuleBase reteooRuleBase1 = RuleBaseFactory.newRuleBase(RuleBase.RETEOO);

		addDefaultRuleToRuleBase(reteooRuleBase1);

		// Test to make sure the rule works with the leaps type rule base
		testDefaultRule(reteooRuleBase1);

		Externalizable leapsExternalRuleBase = reteooRuleBase1;
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		DroolsObjectOutputStream outputStream = new DroolsObjectOutputStream(output);
		leapsExternalRuleBase.writeExternal(outputStream);
		outputStream.close();

		RuleBase reteooRuleBase = RuleBaseFactory.newRuleBase(RuleBase.RETEOO);
		Externalizable reteooExternalRuleBase = reteooRuleBase;
		reteooExternalRuleBase.readExternal(new DroolsObjectInputStream(
			new ByteArrayInputStream(output.toByteArray()), EpRuleBaseImpl.class.getClassLoader()));

		testDefaultRule(reteooRuleBase);
	}

	private void testDefaultRule(final RuleBase reteooRuleBase1) {
		TestClass reteooTester1 = new TestClass();
		assertFalse(reteooTester1.isResult());
		reteooTester1.setResult(true);
		WorkingMemory leapsMemory = reteooRuleBase1.newStatefulSession();
		leapsMemory.insert(reteooTester1);
		leapsMemory.fireAllRules();
		assertTrue(reteooTester1.isResult());
	}

	@SuppressWarnings("PMD.ConsecutiveLiteralAppends")
	private void addDefaultRuleToRuleBase(final RuleBase reteooRuleBase1) throws Exception {
		final StringBuffer source = new StringBuffer("package test\n\n");
		source.append("import " + TestClass.class.getName().replace('$', '.') + ";\n");
		source.append("rule \"test rule\"\n");
		source.append("salience 0\n");
		source.append("when\n");
		source.append("tester : TestClass ( bool == true )\n");
		source.append("then\n");
		source.append("tester.setResult(true);\n");
		source.append("end");


		PackageBuilder builder = new PackageBuilder();
		builder.addPackageFromDrl(new StringReader(source.toString()));
		Package pkg = builder.getPackage();
		reteooRuleBase1.addPackage(pkg);
	}

	/**
	 * Test class for testing.
	 */
	public class TestClass {
		private boolean bool;

		private boolean result;

		public boolean isBool() {
			return bool;
		}

		public void setBool(final boolean bool) {
			this.bool = bool;
		}

		public boolean isResult() {
			return result;
		}

		public void setResult(final boolean result) {
			this.result = result;
		}
	}
}
