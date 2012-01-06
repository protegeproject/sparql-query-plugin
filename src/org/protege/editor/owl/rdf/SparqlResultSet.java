package org.protege.editor.owl.rdf;

import java.util.ArrayList;
import java.util.List;

public class SparqlResultSet {
	private List<String> columnNames = new ArrayList<String>();
	private List<List<Object>> entries = new ArrayList<List<Object>>();
	
	
	public String getColumnName(int col) {
		return columnNames.get(col);
	}
	
	
	
	
}
