import groovy.json.*

JsonSlurper slurper = new JsonSlurper()

def json = slurper.parseText(new File("misc/flopo-labels.txt").getText())


def count = 0
def gbif2count = [:]
new File("misc/flopo-tsne.txt").splitEachLine("\t") { line ->
    def cl = line[0]
    def gbif = line[1]
    def name = line[2]
    def vec = line[3..-1]
    json[gbif].vec = vec
    gbif2count[count] = gbif
    count += 1
}

count = 0
new File("flopo-tsne-plot.txt").splitEachLine(",") { line ->
    def obj = json[gbif2count[count]]
    def x = line[1]
    def y = line[2]
    if (obj) {
	obj.x = x
	obj.y = y
    }
    count += 1
}

def x = [:].withDefault { [] }

json.each { k, v ->
    def order = v.family
    x[order] << v
}


println """<html>
  <head>
    <script src="https://cdn.plot.ly/plotly-latest.min.js"></script>
  </head>

  <body>

    <div align="center" id="plot"><!-- Plotly chart will be drawn inside this DIV --></div>
    
  </body>
  <script>
"""

x.each { k, v ->
    println """
var trace$k = {
  mode: 'markers',
  type: 'scatter',
  name: '$k',
  marker: { size: 7 },"""
    print "x: ["
    print v[0].x
    if (v.size()>1) {
	v[1..-1].each {
	    print ", "+it.x
	}
    }
    println "],"
    print "y: ["
    print v[0].y
    if (v.size()>1) {
	v[1..-1].each {
	    print ", "+it.y
	}
    }
    println "],"
    print "text: ["
    print "\""+v[0].canonicalName+"\""
    if (v.size()>1) {
	v[1..-1].each {
	    print ", "+"\""+it.canonicalName+"\""
	}
    }
    println "]"
println "};"

}
    println "var data = [ "
    def l = []
    x.keySet().each { l << it }
    print "trace"+l[0]
    l[1..-1].each { kk ->
	print ", trace"+kk
    }
    println "];"

    

    println """
var layout = { 
  xaxis: {
    range: [ -50, 50 ] 
  },
  yaxis: {
    range: [-55, 50]
},
hovermode: 'closest',
title:'Plant phenotype distribution (t-SNE)',
autosize: true,
  width: 1500,
  height: 1000,
};

Plotly.newPlot('plot', data, layout);

    </script>

</html>

"""
