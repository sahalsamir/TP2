@Grab('org.codehaus.groovy:groovy-xmlrpc:0.8')
import groovy.net.xmlrpc.*

def server8081 = new XMLRPCServer()

server8081.echo = { it }

def serverSocket8081 = new ServerSocket( 8081 )

server8081.startServer( serverSocket8081 )
println "demarrage 8081"   
 
            
server8081.microservice2 = { parametre -> parametre
                       //Lecture du fichier json
                     //  def jsonSlurper = new JsonSlurper()
                     //  def parametre = jsonSlurper.parse new File(nomFichierIntrant) 
return parametre
}   
             
             