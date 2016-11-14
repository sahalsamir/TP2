package tp1

@Grab('org.codehaus.groovy:groovy-xmlrpc:0.8')
import groovy.net.xmlrpc.*

def server8080 = new XMLRPCServer()
def server8081 = new XMLRPCServer()
def server8082 = new XMLRPCServer()

server8080.echo = { it }
server8081.echo = { it }
server8082.echo = { it }

def serverSocket8080 = new ServerSocket( 8080 )
def serverSocket8081 = new ServerSocket( 8081 )
def serverSocket8082 = new ServerSocket( 8082 )

server8080.startServer( serverSocket8080 )
server8081.startServer( serverSocket8081 )
server8082.startServer( serverSocket8082 )