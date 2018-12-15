@Grab(group='org.semanticweb.elk', module='elk-owlapi', version='0.4.3')
@Grab(group='net.sourceforge.owlapi', module='owlapi-api', version='4.2.5')
@Grab(group='net.sourceforge.owlapi', module='owlapi-apibinding', version='4.2.5')
@Grab(group='net.sourceforge.owlapi', module='owlapi-impl', version='4.2.5')

import java.net.*
import org.semanticweb.owlapi.model.parameters.*
import org.semanticweb.elk.owlapi.ElkReasonerFactory;
import org.semanticweb.elk.owlapi.ElkReasonerConfiguration
import org.semanticweb.elk.reasoner.config.*
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.reasoner.*
import org.semanticweb.owlapi.reasoner.structural.StructuralReasoner
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.io.*;
import org.semanticweb.owlapi.owllink.*;
import org.semanticweb.owlapi.util.*;
import org.semanticweb.owlapi.search.*;
import org.semanticweb.owlapi.manchestersyntax.renderer.*;
import org.semanticweb.owlapi.reasoner.structural.*
import org.semanticweb.owlapi.model.parameters.*
import org.semanticweb.elk.owlapi.ElkReasonerFactory;
import org.semanticweb.elk.owlapi.ElkReasonerConfiguration
import org.semanticweb.elk.reasoner.config.*
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.reasoner.*
import org.semanticweb.owlapi.reasoner.structural.StructuralReasoner
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.io.*;
import org.semanticweb.owlapi.owllink.*;
import org.semanticweb.owlapi.util.*;
import org.semanticweb.owlapi.search.*;
import org.semanticweb.owlapi.manchestersyntax.renderer.*;
import org.semanticweb.owlapi.reasoner.structural.*

// Let's load an ontology and output the number of classes
// Create the ontology manager
manager = OWLManager.createOWLOntologyManager()
// create data factory (to create axioms, classes, etc.)
fac = manager.getOWLDataFactory()
// Load the latest version of the PhenomeNET Ontology; for more information about this ontology, see http://journals.plos.org/ploscompbiol/article?id=10.1371/journal.pcbi.1005500

ont = manager.loadOntologyFromOntologyDocument(new IRI("file:ncbitaxon.owl"))

ConsoleProgressMonitor progressMonitor = new ConsoleProgressMonitor()
OWLReasonerConfiguration config = new SimpleConfiguration(progressMonitor)
ElkReasonerFactory f1 = new ElkReasonerFactory()
reasoner = f1.createReasoner(ont,config)

def viruses = reasoner.getSubClasses(fac.getOWLClass(IRI.create("http://purl.obolibrary.org/obo/NCBITaxon_10239")), false).getFlattened().collect { it.toString() }
def bacteria = reasoner.getSubClasses(fac.getOWLClass(IRI.create("http://purl.obolibrary.org/obo/NCBITaxon_2")), false).getFlattened().collect { it.toString() }

def labelmap = [:]
reasoner.getSubClasses(fac.getOWLThing(), false).getFlattened().each { cl ->
    def ciri = cl.toString()
    EntitySearcher.getAnnotations(cl, ont, fac.getRDFSLabel()).each { a ->
	OWLAnnotationValue val = a.getValue()
	if(val instanceof OWLLiteral) {
	    def label = ((OWLLiteral)val).getLiteral()
	    labelmap[ciri.toString()] = label
	}
    }
}

// Use the next line instead if you have memory problems loading the ontology above (comment out the line above and uncomment this line):
//ont = manager.loadOntologyFromOntologyDocument(new IRI("file:phenomenet-inferred.owl"))

ont.getClassesInSignature(true).size()


def current = null
def map = [:]
new File("patho-vec.txt").eachLine { line ->
    if (line.indexOf('[')>-1) { // new vector starts here
	if (line.indexOf("NCBI")>-1) { // pathogen
            toks = line.replaceAll("]","").replaceAll("\\[","")trim().split(" +")
            current = toks[0]
            map[current] = []
            toks[1..-1].each { map[current] << new Double(it) }
	} else {
	    current = null
	}
    } else {
        if (current != null) {
            toks = line.replaceAll("]","").replaceAll("\\[","")trim().split(" +")
            toks[0..-1].each { map[current] << new Double(it) }
        }
    }
}

Set s = new LinkedHashSet()
def count = 0
PrintWriter fout = new PrintWriter(new BufferedWriter(new FileWriter("patho-tsne.txt")))
//PrintWriter lout = new PrintWriter(new BufferedWriter(new FileWriter("patho-tsne-labels.txt")))
map.each { k, v -> 
    if ((k.indexOf("NCBITaxon")>-1) && !(k in s) && (count < 50000)) {
	if (k in viruses) {
	    fout.print("0\t$k\t"+labelmap[k])
	} else if (k in bacteria) {
	    fout.print("1\t$k\t"+labelmap[k])
	} else {
	    fout.print("2\t$k\t"+labelmap[k])
	}
        s.add(k)
        count += 1
        v.each { fout.print("\t"+it) }
        fout.println("")
    }
}
fout.flush()
fout.close()
