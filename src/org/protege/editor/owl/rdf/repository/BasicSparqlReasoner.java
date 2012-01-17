package org.protege.editor.owl.rdf.repository;

import java.util.Map.Entry;

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
import org.semanticweb.owlapi.util.NamespaceUtil;

public class BasicSparqlReasoner implements SparqlReasoner {
	private OWLOntologyManager manager;
	private OwlTripleStore triples;
	
	public BasicSparqlReasoner(OWLOntologyManager manager) {
		this.manager = manager;
	}
	
	@Override
	public String getSampleQuery() {
		StringBuffer sb = new StringBuffer();
		NamespaceUtil nsUtil = new NamespaceUtil();
		for (Entry<String, String> entry : nsUtil.getNamespace2PrefixMap().entrySet()) {
			String ns = entry.getKey();
			String prefix = entry.getValue();
			sb.append("PREFIX ");
			sb.append(prefix);
			sb.append(": <");
			sb.append(ns);
			sb.append(">\n");
		}
		sb.append("SELECT ?subject ?object\n\tWHERE { ?subject rdfs:subClassOf ?object }");
		return sb.toString();
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
				QueryHandler handler = new QueryHandler(triples, manager.getOWLDataFactory());
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
