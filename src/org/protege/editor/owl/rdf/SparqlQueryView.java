package org.protege.editor.owl.rdf;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;

import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.owl.rdf.repository.BasicSparqlReasonerFactory;
import org.protege.editor.owl.ui.renderer.OWLCellRenderer;
import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;
import org.semanticweb.owlapi.model.OWLObject;

public class SparqlQueryView extends AbstractOWLViewComponent {
	private static final long serialVersionUID = -1370725700740073290L;
	
	private SparqlReasoner reasoner;
	private JTextPane queryPane;
	private JButton executeQuery;
	private SwingResultModel resultModel;

	@Override
	protected void initialiseOWLView() throws Exception {
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
			ProtegeApplication.getErrorLog().logError(e);
		}
	}
	
	private JComponent createCenterComponent() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0,1));
		queryPane = new JTextPane();
		queryPane.setText(reasoner.getSampleQuery());
		panel.add(queryPane);
		resultModel = new SwingResultModel();
		JTable results = new JTable(resultModel);
		OWLCellRenderer renderer = new OWLCellRenderer(getOWLEditorKit());
		results.setDefaultRenderer(Object.class, renderer);
		JScrollPane scrollableResults = new JScrollPane(results);
		panel.add(scrollableResults);
		return panel;
	}
	
	private JComponent createBottomComponent() {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.CENTER));
		executeQuery = new JButton("Execute");
		executeQuery.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
				String query = queryPane.getText();
				SparqlResultSet result = reasoner.executeQuery(query);
				resultModel.setResults(result);
				}
				catch (SparqlReasonerException ex) {
					ProtegeApplication.getErrorLog().logError(ex);
				}
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
