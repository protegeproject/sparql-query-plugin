package org.protege.editor.owl.rdf.repository;

import org.openrdf.model.BNode;
import org.openrdf.model.Value;
import org.openrdf.repository.RepositoryException;
import org.protege.owl.rdf.api.OwlTripleStore;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClassExpression;

public class Util {
	private Util() {
		
	}

	public static Object convertValue(OwlTripleStore triples, Value v) throws RepositoryException {
		Object converted = v;
		if (v instanceof BNode) {
			OWLClassExpression ce = triples.parseClassExpression((BNode) v);
			if (ce != null) {
				converted = ce;
			}
		}
		else if (v instanceof org.openrdf.model.URI) {
			converted = IRI.create(((org.openrdf.model.URI) v).stringValue());
		}
		return converted;
	}
	
	
}
