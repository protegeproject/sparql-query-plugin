package org.protege.editor.owl.rdf.repository;

import org.protege.editor.owl.rdf.SparqlInferenceFactory;
import org.protege.editor.owl.rdf.SparqlReasoner;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class BasicSparqlReasonerFactory implements SparqlInferenceFactory {

	@Override
	public SparqlReasoner createReasoner(OWLOntologyManager manager) {
		return new BasicSparqlReasoner(manager);
	}

}
