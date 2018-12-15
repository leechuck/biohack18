import java.util.zip.GZIPInputStream


InputStream fileStream = new FileInputStream(new File("goa_uniprot_all.gaf.gz"))
InputStream gzipStream = new GZIPInputStream(fileStream)
Reader decoder = new InputStreamReader(gzipStream)
BufferedReader fin = new BufferedReader(decoder)

fin.splitEachLine("\t") { line ->
    if (line[0] == "UniProtKB" && line[6] != "ND" && line[6] != "IEA") {
	println line[6]
	def id = "https://uniprot.org/uniprot/"+line[1]
	def go = "<http://purl.obolibrary.org/obo/"+line[4].replaceAll(":","_")+">"
//	println "$id\t$go"
    }
}
