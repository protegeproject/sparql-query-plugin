package org.protege.editor.owl.rdf.repository;

import java.util.ArrayList;
import java.util.List;

import org.openrdf.model.BNode;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.TupleQueryResultHandler;
import org.openrdf.query.TupleQueryResultHandlerException;
import org.openrdf.repository.RepositoryException;
import org.protege.editor.owl.rdf.SparqlResultSet;
import org.protege.owl.rdf.api.OwlTripleStore;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;

public class QueryHandler implements TupleQueryResultHandler {
	private OwlTripleStore triples;
	private OWLDataFactory factory;
	private SparqlResultSet queryResult;
	
	public QueryHandler(OwlTripleStore triples, OWLDataFactory factory) {
		this.triples = triples;
		this.factory = factory;
	}
	
	public SparqlResultSet getQueryResult() {
		return queryResult;
	}

	@Override
	public void startQueryResult(List<String> bindingNames) throws TupleQueryResultHandlerException {
		queryResult = new SparqlResultSet(bindingNames);
	}

	@Override
	public void handleSolution(BindingSet bindingSet) throws TupleQueryResultHandlerException {
		try {
		List<Object> row = new ArrayList<Object>();
		for (int i = 0; i < queryResult.getColumnCount(); i++) {
			String columnName = queryResult.getColumnName(i);
			Value v = bindingSet.getBinding(columnName).getValue();
			row.add(convertValue(v));
		}
		queryResult.addRow(row);
		}
		catch (RepositoryException re) {
			throw new TupleQueryResultHandlerException(re);
		}
	}
	
	private Object convertValue(Value v) throws RepositoryException {
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

	@Override
	public void endQueryResult() throws TupleQueryResultHandlerException {

	}

}
