new File(args[0]).splitEachLine("\t") { line ->
    if (line[0].indexOf("gbif")>-1) {
	def id = line[0].substring(line[0].lastIndexOf("/")+1)
	print id
	line[1..-1].each { print "\t$it" }
	println ""
    }
}
