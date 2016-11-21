import groovy.json.JsonSlurper
import groovy.json.JsonOutput
import groovy.json.JsonBuilder

import java.math.RoundingMode

@Grab('org.codehaus.groovy:groovy-xmlrpc:0.8')
import groovy.net.xmlrpc.*

def server8080 = new XMLRPCServer()

server8080.echo = { it }

def serverSocket8080 = new ServerSocket( 8080 )

server8080.startServer( serverSocket8080 )
println "demarrage 8080"            
             
server8080.microservice1 = { nomFichierIntrant ->  fileResultat1
                       //Lecture du fichier json
                       def jsonSlurper = new JsonSlurper()
                       def parametre = jsonSlurper.parse new File(nomFichierIntrant) 
                       
                       //calculer versement
                       def scenario = parametre.scenario
                       def datePret = parametre.datePret
                       def montant = parametre.montantInitial as Double
                       def nombrePeriodes = parametre.nombrePeriodes as int
                       def taux = parametre.tauxPeriodique as Double
                       def versement = montant * taux / (1 - (1 + taux) ** (- nombrePeriodes))
                       versement = versement.round(2) as String     
                       
                       //Creer fichier de sortie de microservice 1
                       def json = new JsonBuilder()
                       def fileResultat1 = json "scenario": scenario,
                                       "datePret": datePret,
                                       "montantInitial": montant,
                                       "nombrePeriodes": nombrePeriodes,
                                       "tauxPeriodique": taux,
                                       "versement": versement
                       return fileResultat1.toString()                 
             }        