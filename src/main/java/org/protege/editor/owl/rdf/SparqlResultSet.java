package org.protege.editor.owl.rdf;

import java.util.ArrayList;
import java.util.List;

public class SparqlResultSet {
	private List<String> columnNames = new ArrayList<String>();
	private List<List<Object>> entries = new ArrayList<List<Object>>();
	
	public SparqlResultSet(List<String> colunmNames) {
		this.columnNames = new ArrayList<String>(colunmNames);
	}
	
	public void addRow(List<Object> row) {
		assert(row.size() == columnNames.size());
		entries.add(row);
	}
	
	public int getColumnCount() {
		return columnNames.size();
	}
	
	public int getRowCount() {
		return entries.size();
	}
	
	public String getColumnName(int col) {
		return columnNames.get(col);
	}
	
	public Object getResult(int row, int col) {
		return entries.get(row).get(col);
	}
	
	
}
