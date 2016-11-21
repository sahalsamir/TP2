import groovy.json.JsonSlurper
import groovy.json.JsonOutput
import groovy.json.JsonBuilder


import java.math.RoundingMode

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
println "demarrage 8080"

server8081.startServer( serverSocket8081 )
println "demarrage 8081"

server8082.startServer( serverSocket8082 )
println "demarrage 8082"

server8080.microservice1 = { nomFichierIntrant ->  nomFichierIntrant
                       def jsonSlurper = new JsonSlurper()
                       def parametre = jsonSlurper.parse new File(nomFichierIntrant) 
                       
                       def scenario = parametre.scenario
                       def datePret = parametre.datePret
                       def montant = parametre.montantInitial as Double
                       def nombrePeriodes = parametre.nombrePeriodes as int
                       def taux = parametre.tauxPeriodique as Double
                       def versement = montant * taux / (1 - (1 + taux) ** (- nombrePeriodes))
                       versement = versement.round(2) as String     
                       
                       def json = new JsonBuilder()
                       def micro1 = json "scenario": scenario,
                                       "datePret": datePret,
                                       "montantInitial": montant,
                                       "nombrePeriodes": nombrePeriodes,
                                       "tauxPeriodique": taux,
                                       "versement": versement
                       print micro1.toString()   
             }
                       