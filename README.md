# SPARQL Query Plugin

Version 4 targets Protégé 6, OWL API 5.5.1, Java 11, and RDF Library 4. It retains the existing Sesame query engine and public plugin API.

RDF Library 4.0.0 resolves from Maven Central. To test a paired local build, first build and install RDF Library 4.0.0 from `protegeproject/rdf-library`:

```sh
mvn --batch-mode clean install
```

Then build this repository:

```sh
mvn --batch-mode clean verify
```

The integration tests exercise SELECT, ASK, CONSTRUCT, literals, anonymous restriction results, ontology changes, imports, and malformed queries against the real RDF Library implementation.

For interactive verification, copy both bundle JARs into an assembled Protégé 6 distribution, enable Window > Tabs > SPARQL Query, and run queries against a loaded ontology. Check that results reflect subsequent ontology edits. The migration is tracked by protegeproject/protege#1358 and depends on #1357.
