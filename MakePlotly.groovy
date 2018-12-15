import groovy.json.*

JsonSlurper slurper = new JsonSlurper()

def json = slurper.parseText(new File("misc/flopo-labels.txt").getText())


new File(args[0]).splitEachLine(",") { line ->
    def obj = json[line[0]]
    def x = line[1]
    def y = line[2]
    if (obj) {
	obj.x = x
	obj.y = y
    }
}

def x = [:].withDefault { [] }

json.each { k, v ->
    def order = v.order
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

x.keySet().sort().each { k ->
    v = x[k]
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
x.keySet().sort().each { l << it }
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
