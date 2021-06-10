/**
 * Copyright Siemens AG, 2018
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.siemens.productconfigurationshacl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.FileUtils;
import org.topbraid.jenax.util.JenaUtil;
import org.topbraid.shacl.util.ModelPrinter;
import org.topbraid.shacl.validation.ValidationUtil;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws FileNotFoundException
    {
		// Load the main data model
		Model dataModel = JenaUtil.createMemoryModel();
		InputStream constraints = new FileInputStream(new File("src/main/resources/racks_shacl_constraints.ttl"));
		dataModel.read(constraints, "urn:dummy", FileUtils.langTurtle);
		InputStream instance = new FileInputStream(new File("src/main/resources/racks_shacl_ex1_partial.ttl"));
		dataModel.read(instance, "urn:dummy", FileUtils.langTurtle);
		
		// Perform the validation of everything, using the data model
		// also as the shapes model - you may have them separated
		Resource report = ValidationUtil.validateModel(dataModel, dataModel, true);
		 
		// Print violations
		System.out.println(ModelPrinter.get().print(report.getModel()));
    }
}
