/**
 * Copyright Siemens AG, 2018
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.siemens.productconfigurationshacl.triples_to_asp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.util.FileUtils;
import org.junit.Test;
import org.topbraid.jenax.util.JenaUtil;

/**
 * Tests {@link TriplesToASPTranslator}.
 *
 */
public class TriplesToASPTranslatorTest {

	private static final String PATH = "src/main/resources";
	private final TriplesToASPTranslator translator = new TriplesToASPTranslator();

	@Test
	public void testShaclConstraints() throws FileNotFoundException {
		translate("racks_shacl_constraints.ttl");
		// this test case has no assertions, it just demonstrates the use of
		// TriplesToASPTranslator
	}

	@Test
	public void testValidConfiguration() throws FileNotFoundException {
		translate("racks_shacl_ex1_valid.ttl");
		// this test case has no assertions, it just demonstrates the use of
		// TriplesToASPTranslator
	}

	@Test
	public void testPartialConfiguration() throws FileNotFoundException {
		translate("racks_shacl_ex1_partial.ttl");
		// this test case has no assertions, it just demonstrates the use of
		// TriplesToASPTranslator
	}

	void translate(String fileName) throws FileNotFoundException {
		Model dataModel = JenaUtil.createMemoryModel();
		InputStream constraints = new FileInputStream(new File(PATH, fileName));
		dataModel.read(constraints, "urn:dummy", FileUtils.langTurtle);
		translator.toASP(dataModel);
	}

}
