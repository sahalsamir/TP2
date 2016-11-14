package tp1

@Grab('org.codehaus.groovy:groovy-xmlrpc:0.8')

import groovy.net.xmlrpc.*

def url8080 = "http://localhost:8080"
def url8081 = "http://localhost:8081"
def url8082 = "http://localhost:8082"

def serverProxy8080 = new XMLRPCServerProxy(url8080)
def serverProxy8081 = new XMLRPCServerProxy(url8081)
def serverProxy8082 = new XMLRPCServerProxy(url8082)

println serverProxy8080.echo("Bonjour 8080 ")
println serverProxy8081.echo("Bonjour 8081 ")
println serverProxy8082.echo("Bonjour 8082 ")