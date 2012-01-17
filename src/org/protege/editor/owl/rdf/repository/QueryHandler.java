package org.protege.editor.owl.rdf.repository;

import java.util.ArrayList;
import java.util.List;

import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.TupleQueryResultHandler;
import org.openrdf.query.TupleQueryResultHandlerException;
import org.protege.editor.owl.rdf.SparqlResultSet;

public class QueryHandler implements TupleQueryResultHandler {
	private SparqlResultSet queryResult;
	
	public SparqlResultSet getQueryResult() {
		return queryResult;
	}

	@Override
	public void startQueryResult(List<String> bindingNames) throws TupleQueryResultHandlerException {
		queryResult = new SparqlResultSet(bindingNames);
	}

	@Override
	public void handleSolution(BindingSet bindingSet) throws TupleQueryResultHandlerException {
		List<Object> row = new ArrayList<Object>();
		for (int i = 0; i < queryResult.getColumnCount(); i++) {
			String columnName = queryResult.getColumnName(i);
			Value v = bindingSet.getBinding(columnName).getValue();
			row.add(v);
		}
		queryResult.addRow(row);
	}

	@Override
	public void endQueryResult() throws TupleQueryResultHandlerException {

	}

}
