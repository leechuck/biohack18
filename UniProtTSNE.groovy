@Grab(group='org.semanticweb.elk', module='elk-owlapi', version='0.4.3')
@Grab(group='net.sourceforge.owlapi', module='owlapi-api', version='4.2.5')
@Grab(group='net.sourceforge.owlapi', module='owlapi-apibinding', version='4.2.5')
@Grab(group='net.sourceforge.owlapi', module='owlapi-impl', version='4.2.5')

import java.net.*
import groovy.json.*
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

String url = 'http://api.gbif.org/v1/species/'
URLEncoder enc = new URLEncoder()
def jsonSlurper = new JsonSlurper()


def current = null
def map = [:]
def labelmap = [:]
new File("../swissprot-vec.txt").eachLine { line ->
    if (line.indexOf('[')>-1) { // new vector starts here
	if (line.indexOf("uniprot")>-1) { // protein
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
PrintWriter fout = new PrintWriter(new BufferedWriter(new FileWriter("swissprot-tsne.txt")))
//PrintWriter lout = new PrintWriter(new BufferedWriter(new FileWriter("patho-tsne-labels.txt")))
map.each { k, v -> 
//    fout.print(labelmap[k].order+"\t$k\t"+labelmap[k].canonicalName?.replaceAll(","," "))
    v.each { fout.print("\t"+it) }
    fout.println("")
}
fout.flush()
fout.close()
