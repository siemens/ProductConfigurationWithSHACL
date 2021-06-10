/**
 * Copyright Siemens AG, 2018
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.siemens.productconfigurationshacl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.reasoner.ValidityReport;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.topbraid.jenax.util.JenaUtil;
import org.topbraid.shacl.rules.RuleUtil;

/**
 * Simple example, how to solve a configuration with SHACL rules
 * This works only, if the configuration can be found without backtracking
 * @author gs
 *
 */
public class ShaclRacksSolver {
	//private static Logger logger = LoggerFactory.getLogger(ShaclValidation.class);
	// Why This Failure marker
	//private static final Marker WTF_MARKER = MarkerFactory.getMarker("WTF");

	static Path path = Paths.get(".").toAbsolutePath().normalize();

	public static void main(String[] args) throws IOException {

		String constraints = "file:" + path.toFile().getAbsolutePath() + "/src/main/resources/racks_shacl_constraints.ttl";		
		String rules = "file:" + path.toFile().getAbsolutePath() + "/src/main/resources/racks_shacl_rules.ttl";
		String data = "file:" + path.toFile().getAbsolutePath() + "/src/main/resources/racks_shacl_rules_example.ttl";
		Model shapeModel = JenaUtil.createDefaultModel();
		shapeModel.read(constraints);
		shapeModel.read(rules);
		

		Model dataModel = JenaUtil.createDefaultModel();
		dataModel.read(data);
		int steps = 1;
		//while(executeRules(dataModel, shapeModel) && (++steps<1)) {	
		//}
		executeRules(dataModel, shapeModel);		
		executeRules(dataModel, shapeModel);
		executeRules(dataModel, shapeModel);
		executeRules(dataModel, shapeModel);		
		executeRules(dataModel, shapeModel);		
		executeRules(dataModel, shapeModel);			
		executeRules(dataModel, shapeModel);			
	}

	public static boolean executeRules(Model dataModel, Model shapeModel) throws IOException {
		Reasoner reasoner = ReasonerRegistry.getRDFSReasoner();
		InfModel infModel = ModelFactory.createInfModel(reasoner, dataModel);
		ValidityReport validity = infModel.validate();
		if (!validity.isValid()) {
			System.out.println("Conflicts");
			for (Iterator i = validity.getReports(); i.hasNext();) {
				System.out.println(" - " + i.next());
			}
		}
		Model inferenceModel = JenaUtil.createDefaultModel();
		inferenceModel = RuleUtil.executeRules(infModel, shapeModel, inferenceModel, null);
		String inferences = path.toFile().getAbsolutePath() + "/src/main/resources/racks_shacl_rules_inferences.ttl";
		File inferencesFile = new File(inferences);
		inferencesFile.createNewFile();
		OutputStream reportOutputStream = new FileOutputStream(inferencesFile);
		if (inferenceModel.isEmpty()) {
			System.out.println("No new inferences");
		}
 		
		dataModel.add(inferenceModel);
		RDFDataMgr.write(reportOutputStream, dataModel, RDFFormat.TTL);
		System.out.println("Result:");
		String content = new String(Files.readAllBytes(Paths.get(inferences)));
		System.out.println(content);
		return (!inferenceModel.isEmpty());
	}
}
