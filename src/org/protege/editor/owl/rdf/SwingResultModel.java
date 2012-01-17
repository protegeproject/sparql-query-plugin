package org.protege.editor.owl.rdf;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

public class SwingResultModel extends AbstractTableModel {
	private static final long serialVersionUID = 8138862450119034828L;
	private SparqlResultSet results = new SparqlResultSet(new ArrayList<String>());

	@Override
	public int getRowCount() {
		return results.getRowCount();
	}

	@Override
	public int getColumnCount() {
		return results.getColumnCount();
	}

	@Override
	public String getColumnName(int column) {
		return results.getColumnName(column);
	}
	
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return results.getResult(rowIndex, columnIndex);
	}

	public void setResults(SparqlResultSet results) {
		this.results = results;
		fireTableStructureChanged();
	}
}
