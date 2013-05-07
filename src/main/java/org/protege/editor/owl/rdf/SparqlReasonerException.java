package org.protege.editor.owl.rdf;

public class SparqlReasonerException extends Exception {
	private static final long serialVersionUID = -7533512483805738539L;

	public SparqlReasonerException() {
		
	}
	
	public SparqlReasonerException(Throwable t) {
		super(t);
	}
	
	public SparqlReasonerException(String message, Throwable t) {
		super(message, t);
	}
}
