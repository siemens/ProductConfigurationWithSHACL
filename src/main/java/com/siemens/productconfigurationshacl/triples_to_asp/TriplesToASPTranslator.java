/**
 * Copyright Siemens AG, 2018
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.siemens.productconfigurationshacl.triples_to_asp;

import java.math.BigInteger;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Node_Literal;
import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.util.iterator.ExtendedIterator;

public class TriplesToASPTranslator {

	public TriplesToASPTranslator() {
		// TODO Auto-generated constructor stub
	}

	public void toASP(Model model) {
		ExtendedIterator<Triple> tripleIterator = model.getGraph().find();
		while (tripleIterator.hasNext()) {
			Triple triple = tripleIterator.next();
			System.out.println(String.format("triple(%s, %s, %s).", toASP(triple.getSubject()),
					toASP(triple.getPredicate()), toASP(triple.getObject())));
		}
	}

	private String toASP(Node node) {
		if (node.isLiteral()) {
			if (isInteger((Node_Literal) node)) {
				return node.getLiteralLexicalForm();
			} else {
				return "\"" + node.getLiteralLexicalForm() + "\"";
			}
		} else {
			return "\"" + node.toString() + "\"";
		} // TODO: special treatment for other kinds of nodes?
	}

	private boolean isInteger(Node_Literal literal) {
		return literal.getLiteralDatatype().getJavaClass() == BigInteger.class
				|| canParseInteger(literal.getLiteralLexicalForm());
		// TODO: can this be improved?
	}

	private boolean canParseInteger(String literal) {
		try {
			new BigInteger(literal);
			return true;
		} catch (NumberFormatException nfe) {
			return false;
		}
	}

}
