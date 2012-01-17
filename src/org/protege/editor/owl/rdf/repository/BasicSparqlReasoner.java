package org.protege.editor.owl.rdf.repository;

import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.protege.editor.owl.rdf.SparqlReasoner;
import org.protege.editor.owl.rdf.SparqlReasonerException;
import org.protege.editor.owl.rdf.SparqlResultSet;
import org.protege.owl.rdf.Utilities;
import org.protege.owl.rdf.api.OwlTripleStore;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class BasicSparqlReasoner implements SparqlReasoner {
	private OWLOntologyManager manager;
	private OwlTripleStore triples;
	
	public BasicSparqlReasoner(OWLOntologyManager manager) {
		this.manager = manager;
	}

	@Override
	public void precalculate() throws SparqlReasonerException {
		if (triples == null) {
			try {
				triples = Utilities.getOwlTripleStore(manager, true);
			}
			catch (RepositoryException e) {
				throw new SparqlReasonerException(e);
			}
		}
	}
	
	@Override
	public SparqlResultSet executeQuery(String query) throws SparqlReasonerException {
		precalculate();
		try {
			RepositoryConnection connection = null;
			try {
				connection = triples.getRepository().getConnection();
				TupleQuery tquery = connection.prepareTupleQuery(QueryLanguage.SPARQL, query);
				QueryHandler handler = new QueryHandler();
				tquery.evaluate(handler);
				return handler.getQueryResult();
			}
			finally {
				if (connection != null) {
					connection.close();
				}
			}
		}
		catch (Exception e) {
			throw new SparqlReasonerException(e);
		}
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

}
