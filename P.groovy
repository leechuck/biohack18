import groovy.json.*

JsonSlurper slurper = new JsonSlurper()

def json = slurper.parseText(new File("misc/flopo-labels.txt").getText())

def n2id = [:]
json.each { k, v ->
    n2id[v.canonicalName] = v
}

new File("flope-image-vec.txt").splitEachLine("\t") { line ->
    def name = line[1]
    def vec = line[5].split(",").collect { new Double(it) }
    if (n2id[name] != null) {
	print n2id[name].order
    } else {
	print "unknown"
    }
    vec.each { print "\t" + it }
    println ""
}
