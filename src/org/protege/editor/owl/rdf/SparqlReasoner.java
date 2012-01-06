package org.protege.editor.owl.rdf;

public interface SparqlReasoner {
	SparqlResultSet executeQuery(String query);
	
}
