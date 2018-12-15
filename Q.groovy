new File("swissprot-vec-clean.txt").splitEachLine("\t") { line ->
    def id = line[0].replaceAll("https://uniprot.org/uniprot/", "http://purl.uniprot.org/uniprot/")
    def i2 = id.substring(id.lastIndexOf("/")+1)
    println id + "\t$i2\t\t\t\t"+line[1]
}
