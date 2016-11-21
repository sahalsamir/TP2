import groovy.json.JsonSlurper
import groovy.json.JsonOutput
import groovy.json.JsonBuilder


import java.math.RoundingMode

@Grab('org.codehaus.groovy:groovy-xmlrpc:0.8')
import groovy.net.xmlrpc.*

def server8082 = new XMLRPCServer()

server8082.echo = { it }

def serverSocket8082 = new ServerSocket( 8082 )

server8082.startServer( serverSocket8082 )
println "demarrage 8082"             
             