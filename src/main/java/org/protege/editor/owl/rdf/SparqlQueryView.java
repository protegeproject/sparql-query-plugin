package org.protege.editor.owl.rdf;

import java.awt.GridLayout;
import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/*
@Author Daniele Francesco Santamaria - University of Catania
*/
public class SparqlQueryView extends AbstractOWLViewComponent {

    private static final Logger log = LoggerFactory.getLogger(SparqlQueryView.class);

    private SparqlQueryViewPanel yaspppanel;

    @Override
    protected void initialiseOWLView() throws Exception {
        setLayout(new GridLayout(1,1));        
        yaspppanel = new SparqlQueryViewPanel(getOWLEditorKit());        
        add(yaspppanel);        
        //log.info("Example View Component initialized");
        
    }

	@Override
	protected void disposeOWLView() {
		yaspppanel.dispose();
	}
}
