

def current = null
def map = [:]
def labelmap = [:]

// InputStream fileStream = new FileInputStream(new File(args[0]))
// InputStream gzipStream = new GZIPInputStream(fileStream)
// Reader decoder = new InputStreamReader(gzipStream)
// BufferedReader fin = new BufferedReader(decoder)

new File(args[0]).eachLine { line ->
    if (line.indexOf('[')>-1) { // new vector starts here
        toks = line.replaceAll("]","").replaceAll("\\[","")trim().split(" +")
        current = toks[0]
        map[current] = []
        toks[1..-1].each { map[current] << new Double(it) }
    } else {
        if (current != null) {
            toks = line.replaceAll("]","").replaceAll("\\[","")trim().split(" +")
            toks[0..-1].each { map[current] << new Double(it) }
        }
    }
}

PrintWriter fout = new PrintWriter(new BufferedWriter(new FileWriter(args[1])))
map.each { k, v -> 
    fout.print(k+"\t")
    fout.print(v[0])
    v[1..-1].each { fout.print("\t"+it) }
    fout.println("")
}
fout.flush()
fout.close()
