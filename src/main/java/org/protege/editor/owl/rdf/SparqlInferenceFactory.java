package org.protege.editor.owl.rdf;

import org.semanticweb.owlapi.model.OWLOntologyManager;

public interface SparqlInferenceFactory {
	
	public SparqlReasoner createReasoner(OWLOntologyManager manager);

}
