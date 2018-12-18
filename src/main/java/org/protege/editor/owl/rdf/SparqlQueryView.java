package org.protege.editor.owl.rdf;

import org.protege.editor.core.ui.error.ErrorLogPanel;
import org.protege.editor.owl.rdf.repository.BasicSparqlReasonerFactory;
import org.protege.editor.owl.ui.renderer.OWLCellRenderer;
import org.protege.editor.owl.ui.table.BasicOWLTable;
import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.List;

public class SparqlQueryView extends AbstractOWLViewComponent {

	private SparqlReasoner reasoner;

	private final JTextPane queryPane = new JTextPane();

	private final SwingResultModel resultModel = new SwingResultModel();

	@Override
	protected void initialiseOWLView() {
		initializeReasoner();
		setLayout(new BorderLayout());
		add(createCenterComponent(), BorderLayout.CENTER);
		add(createBottomComponent(), BorderLayout.SOUTH);
	}
	
	private void initializeReasoner() {
		try {
			List<SparqlInferenceFactory> plugins = Collections.singletonList((SparqlInferenceFactory) new BasicSparqlReasonerFactory());
			reasoner = plugins.iterator().next().createReasoner(getOWLModelManager().getOWLOntologyManager());
			reasoner.precalculate();
		}
		catch (SparqlReasonerException e) {
			ErrorLogPanel.showErrorDialog(e);
		}
	}
	
	private JComponent createCenterComponent() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0,1));
		queryPane.setText(reasoner.getSampleQuery());
		panel.add(new JScrollPane(queryPane, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER));
		BasicOWLTable results = new BasicOWLTable(resultModel) {
			@Override
			protected boolean isHeaderVisible() {
				return true;
			}
		};
		OWLCellRenderer renderer = new OWLCellRenderer(getOWLEditorKit());
		renderer.setWrap(false);
		results.setDefaultRenderer(Object.class, renderer);
		JScrollPane scrollableResults = new JScrollPane(results);
		panel.add(scrollableResults);
		return panel;
	}
	
	private JComponent createBottomComponent() {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.CENTER));
		JButton executeQuery = new JButton("Execute");
		executeQuery.addActionListener(e -> {
			try {
				String query = queryPane.getText();
				SparqlResultSet result = reasoner.executeQuery(query);
				resultModel.setResults(result);
			}
			catch (SparqlReasonerException ex) {
				ErrorLogPanel.showErrorDialog(ex);
				JOptionPane.showMessageDialog(getOWLWorkspace(), ex.getMessage() + "\nSee the logs for more information.");
			}
		});
		panel.add(executeQuery);
		return panel;
	}

	@Override
	protected void disposeOWLView() {
		if (reasoner != null) {
			reasoner.dispose();
			reasoner = null;
		}
	}
	
	

}
