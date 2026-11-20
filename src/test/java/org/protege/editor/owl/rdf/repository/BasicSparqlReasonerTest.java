package org.protege.editor.owl.rdf.repository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.Literal;
import org.protege.editor.owl.rdf.SparqlReasonerException;
import org.protege.editor.owl.rdf.SparqlResultSet;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import static org.junit.Assert.*;

public class BasicSparqlReasonerTest {
    private static final String NS = "urn:sparql-test#";
    private static final String PREFIXES =
            "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
            "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n";
    private OWLOntologyManager manager;
    private OWLOntology ontology;
    private OWLDataFactory df;
    private OWLClass a, b;
    private OWLObjectProperty p;
    private BasicSparqlReasoner reasoner;

    @Before
    public void setUp() throws Exception {
        manager = OWLManager.createOWLOntologyManager();
        ontology = manager.createOntology(IRI.create("urn:sparql-test"));
        df = manager.getOWLDataFactory();
        a = df.getOWLClass(IRI.create(NS + "A"));
        b = df.getOWLClass(IRI.create(NS + "B"));
        p = df.getOWLObjectProperty(IRI.create(NS + "p"));
        manager.addAxiom(ontology, df.getOWLSubClassOfAxiom(a, b));
        reasoner = new BasicSparqlReasoner(manager);
    }

    @After
    public void tearDown() {
        reasoner.dispose();
    }

    @Test
    public void selectsAssertedAxiomsAndPreservesColumnOrder() throws Exception {
        SparqlResultSet result = query("SELECT ?object ?subject WHERE { ?subject rdfs:subClassOf ?object }");
        assertEquals(1, result.getRowCount());
        assertEquals("object", result.getColumnName(0));
        assertEquals("subject", result.getColumnName(1));
        assertEquals(b.getIRI(), result.getResult(0, 0));
        assertEquals(a.getIRI(), result.getResult(0, 1));
    }

    @Test
    public void answersTrueAndFalseAskQueries() throws Exception {
        assertEquals("True", query("ASK { <" + a.getIRI() + "> rdfs:subClassOf <" + b.getIRI() + "> }").getResult(0, 0));
        assertEquals("False", query("ASK { <" + b.getIRI() + "> rdfs:subClassOf <" + a.getIRI() + "> }").getResult(0, 0));
    }

    @Test
    public void constructsGraphResults() throws Exception {
        SparqlResultSet result = query("CONSTRUCT { ?s rdfs:subClassOf ?o } WHERE { ?s rdfs:subClassOf ?o }");
        assertEquals(1, result.getRowCount());
        assertEquals(3, result.getColumnCount());
        assertEquals(a.getIRI(), result.getResult(0, 0));
        assertEquals(IRI.create("http://www.w3.org/2000/01/rdf-schema#subClassOf"), result.getResult(0, 1));
        assertEquals(b.getIRI(), result.getResult(0, 2));
    }

    @Test
    public void preservesLanguageTagsAndTypedLiterals() throws Exception {
        manager.addAxiom(ontology, df.getOWLAnnotationAssertionAxiom(df.getRDFSLabel(), a.getIRI(), df.getOWLLiteral("bonjour", "fr")));
        manager.addAxiom(ontology, df.getOWLAnnotationAssertionAxiom(df.getRDFSComment(), a.getIRI(), df.getOWLLiteral(42)));
        Literal label = (Literal) query("SELECT ?value WHERE { <" + a.getIRI() + "> rdfs:label ?value }").getResult(0, 0);
        assertEquals("bonjour", label.getLabel());
        assertEquals("fr", label.getLanguage());
        Literal number = (Literal) query("SELECT ?value WHERE { <" + a.getIRI() + "> rdfs:comment ?value }").getResult(0, 0);
        assertEquals(42, number.intValue());
        assertEquals("http://www.w3.org/2001/XMLSchema#integer", number.getDatatype().stringValue());
    }

    @Test
    public void convertsRestrictionBlankNodesToClassExpressions() throws Exception {
        OWLClassExpression restriction = df.getOWLObjectSomeValuesFrom(p, b);
        manager.addAxiom(ontology, df.getOWLSubClassOfAxiom(a, restriction));
        SparqlResultSet result = query("SELECT ?restriction WHERE { <" + a.getIRI() + "> rdfs:subClassOf ?restriction . ?restriction a owl:Restriction }");
        assertEquals(1, result.getRowCount());
        assertEquals(restriction, result.getResult(0, 0));
    }

    @Test
    public void reflectsAxiomAdditionsAndRemovals() throws Exception {
        assertEquals(1, query("SELECT ?s ?o WHERE { ?s rdfs:subClassOf ?o }").getRowCount());
        OWLAxiom addition = df.getOWLSubClassOfAxiom(b, a);
        manager.addAxiom(ontology, addition);
        assertEquals(2, query("SELECT ?s ?o WHERE { ?s rdfs:subClassOf ?o }").getRowCount());
        manager.removeAxiom(ontology, addition);
        assertEquals(1, query("SELECT ?s ?o WHERE { ?s rdfs:subClassOf ?o }").getRowCount());
    }

    @Test
    public void includesLoadedImportedOntologyData() throws Exception {
        IRI importedIri = IRI.create("urn:imported-sparql-test");
        OWLOntology imported = manager.createOntology(importedIri);
        manager.addAxiom(imported, df.getOWLSubClassOfAxiom(b, df.getOWLThing()));
        manager.applyChange(new AddImport(ontology, df.getOWLImportsDeclaration(importedIri)));
        assertEquals(2, query("SELECT ?s ?o WHERE { ?s rdfs:subClassOf ?o }").getRowCount());
    }

    @Test(expected = SparqlReasonerException.class)
    public void reportsInvalidQueries() throws Exception {
        query("SELECT WHERE {");
    }

    private SparqlResultSet query(String query) throws SparqlReasonerException {
        return reasoner.executeQuery(PREFIXES + query);
    }
}
