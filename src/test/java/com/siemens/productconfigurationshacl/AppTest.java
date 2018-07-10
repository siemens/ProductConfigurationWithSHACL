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
import java.util.HashSet;
import java.util.Set;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.FileUtils;
import org.topbraid.jenax.util.JenaUtil;
import org.topbraid.shacl.util.ModelPrinter;
import org.topbraid.shacl.validation.ValidationUtil;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
	String PATH = "src/main/resources";
	
	
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     * @throws FileNotFoundException 
     */
    public void testValidConfigurations() throws FileNotFoundException
    {
		// Load the main data model
		Model dataModel = JenaUtil.createMemoryModel();
		InputStream constraints = new FileInputStream(new File(PATH, "racks_shacl_constraints.ttl"));
		dataModel.read(constraints, "urn:dummy", FileUtils.langTurtle);
		InputStream instance = new FileInputStream(new File(PATH, "racks_shacl_ex1_valid.ttl"));
		dataModel.read(instance, "urn:dummy", FileUtils.langTurtle);
		
		// Perform the validation of everything, using the data model
		// also as the shapes model - you may have them separated
		Resource report = ValidationUtil.validateModel(dataModel, dataModel, true);

		// Print violations
		System.out.println(ModelPrinter.get().print(report.getModel()));
		Model m = report.getModel();
		Property type = m.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "type");
		assertTrue(type!=null);
		Property validationreport = m.createProperty("http://www.w3.org/ns/shacl#", "ValidationReport");
		assertTrue(validationreport!=null);
		Property conforms = m.createProperty("http://www.w3.org/ns/shacl#", "conforms");
		assertTrue(validationreport!=null);
		
		ResIterator rItor = m.listSubjectsWithProperty(conforms);
		assertTrue(rItor.hasNext());
		while(rItor.hasNext()) {
			Resource r = rItor.next();
			assertTrue(r.getProperty(conforms).getBoolean());
		}
	}
    
    public void testInvalidConfigurations() throws FileNotFoundException
    {
    	// either configuration is either incomplete or faulty
		// Load the main data model
		Model dataModel = JenaUtil.createMemoryModel();
		InputStream constraints = new FileInputStream(new File(PATH, "racks_shacl_constraints.ttl"));
		dataModel.read(constraints, "urn:dummy", FileUtils.langTurtle);
		InputStream instance = new FileInputStream(new File(PATH, "racks_shacl_ex1_partial.ttl"));
		dataModel.read(instance, "urn:dummy", FileUtils.langTurtle);
		
		// Perform the validation of everything, using the data model
		// also as the shapes model - you may have them separated
		Resource report = ValidationUtil.validateModel(dataModel, dataModel, true);
		 
		// Print violations
		System.out.println(ModelPrinter.get().print(report.getModel()));    
		
		Model m = report.getModel();
		Property ptype = m.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "type");
		assertTrue(ptype!=null);
		Property pvalidationreport = m.createProperty("http://www.w3.org/ns/shacl#", "ValidationReport");
		assertTrue(pvalidationreport!=null);
		Property validationresult = m.createProperty("http://www.w3.org/ns/shacl#", "ValidationResult");
		assertTrue(pvalidationreport!=null);
		Property pconforms = m.createProperty("http://www.w3.org/ns/shacl#", "conforms");
		assertTrue(pconforms!=null);
		
		ResIterator rItor = m.listSubjectsWithProperty(pconforms);
		assertTrue(rItor.hasNext());
		while(rItor.hasNext()) {
			Resource r = rItor.next();
			assertTrue(!r.getProperty(pconforms).getBoolean());
		}
		// count the validation results
		ResIterator resItor = m.listSubjectsWithProperty(ptype);
		Set<Resource> validationresults = new HashSet<Resource>(); 
		while(resItor.hasNext()) {
			Resource r = resItor.next();
			if (r.getProperty(ptype).getObject().toString().equals("http://www.w3.org/ns/shacl#ValidationResult")) {
				validationresults.add(r);
			}
		}
		assertTrue(validationresults.size()==10);
	}
}
