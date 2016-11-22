import groovy.json.JsonSlurper
import groovy.json.JsonOutput

import java.math.RoundingMode

@Grab('org.codehaus.groovy:groovy-xmlrpc:0.8')
import groovy.net.xmlrpc.*

def server8080 = new XMLRPCServer()

server8080.echo = { it }

def serverSocket8080 = new ServerSocket( 8080 )

server8080.startServer( serverSocket8080 )
println "demarrage 8080"                       
             
server8080.lireParametres = {nomFichierIntrant -> nomFichierIntrant
    def jsonSlurper = new JsonSlurper()
    jsonSlurper.parse new File(nomFichierIntrant)
}

server8080.calculerVersement = {calcule -> calcule
    def montant = calcule.montantInitial as Double
    def taux = calcule.tauxPeriodique as Double
    def nombrePeriodes = calcule.nombrePeriodes as int
    def versement = montant * taux / (1 - (1 + taux) ** (- nombrePeriodes))
    versement.round(2) as String
}
