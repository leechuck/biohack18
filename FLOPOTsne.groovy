import groovy.json.*

String url = 'http://api.gbif.org/v1/species/'
URLEncoder enc = new URLEncoder()
def slurper = new JsonSlurper()

def json = slurper.parseText(new File("flopo-labels.txt").getText())

def count = 0
PrintWriter fout = new PrintWriter(new BufferedWriter(new FileWriter("flopo-tsne-input.txt")))
new File("../gbif-again.txt").splitEachLine("\t") { line ->
    def gbif = line[0]
//    fout.print(gbif + "\t" + json[gbif].order + "\t" + json[gbif].family + "\t" + json[gbif].canonicalName)
//    fout.print(json[gbif].order)
    fout.print(gbif)
    line[1..-1].each { fout.print("\t" + it) }
    fout.println("")
}
fout.flush()
fout.close()
