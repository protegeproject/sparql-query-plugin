package org.protege.editor.owl.rdf.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.openrdf.query.BooleanQuery;
import org.openrdf.query.GraphQuery;
import org.openrdf.query.Query;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResultHandlerException;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFHandlerException;
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
	public SparqlResultSet executeQuery(String queryString) throws SparqlReasonerException {
		precalculate();
		try {
			RepositoryConnection connection = null;
			try {
				connection = triples.getRepository().getConnection();
				Query query = connection.prepareQuery(QueryLanguage.SPARQL, queryString);
				if (query instanceof TupleQuery) {
					return handleTupleQuery((TupleQuery) query);
				}
				else if (query instanceof GraphQuery) {
					return handleGraphQuery((GraphQuery) query);
				}
				else if (query instanceof BooleanQuery) {
					return handleBooleanQuery((BooleanQuery) query);
				}
				else {
					throw new IllegalStateException("Can't handle queries of type " + query.getClass());
				}
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
	
	private SparqlResultSet handleTupleQuery(TupleQuery tupleQuery) throws QueryEvaluationException, TupleQueryResultHandlerException {
		TupleQueryHandler handler = new TupleQueryHandler(triples);
		tupleQuery.evaluate(handler);
		return handler.getQueryResult();
	}
	
	private SparqlResultSet handleGraphQuery(GraphQuery graph) throws QueryEvaluationException, RDFHandlerException {
		GraphQueryHandler handler = new GraphQueryHandler(triples);
		graph.evaluate(handler);
		return handler.getQueryResult();
	}
	
	private SparqlResultSet handleBooleanQuery(BooleanQuery booleanQuery) throws QueryEvaluationException {
		List<String> columnNames = new ArrayList<String>();
		columnNames.add("Result");
		SparqlResultSet result = new SparqlResultSet(columnNames);
		List<Object> row = new ArrayList<Object>();
		row.add(booleanQuery.evaluate() ? "True" : "False");
		result.addRow(row);
		return result;
	}

	@Override
	public void dispose() {
		
	}

}
