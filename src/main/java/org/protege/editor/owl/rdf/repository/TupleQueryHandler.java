package org.protege.editor.owl.rdf.repository;

import java.util.ArrayList;
import java.util.List;

import org.openrdf.model.Value;
import org.openrdf.query.Binding;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryResultHandlerException;
import org.openrdf.query.TupleQueryResultHandler;
import org.openrdf.query.TupleQueryResultHandlerException;
import org.openrdf.repository.RepositoryException;
import org.protege.editor.owl.rdf.SparqlResultSet;
import org.protege.owl.rdf.api.OwlTripleStore;

public class TupleQueryHandler implements TupleQueryResultHandler {
	private OwlTripleStore triples;
	private SparqlResultSet queryResult;
	
	public TupleQueryHandler(OwlTripleStore triples) {
		this.triples = triples;
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
				Binding binding = bindingSet.getBinding(columnName);
				Value v = binding != null ? binding.getValue() : (Value) null;
				row.add(Util.convertValue(triples, v));
			}
			queryResult.addRow(row);
		}
		catch (RepositoryException re) {
			throw new TupleQueryResultHandlerException(re);
		}
	}
	
	@Override
	public void endQueryResult() throws TupleQueryResultHandlerException {

	}

	@Override
	public void handleBoolean(boolean arg0) throws QueryResultHandlerException {
		
	}

	@Override
	public void handleLinks(List<String> arg0) throws QueryResultHandlerException {
		
	}

}
