import groovy.json.JsonSlurper
import groovy.json.JsonOutput

@Grab('org.codehaus.groovy:groovy-xmlrpc:0.8')
import groovy.net.xmlrpc.*

def calcul_versement_server = new XMLRPCServer()

def calcul_versement_socket = new ServerSocket( 8080 )

calcul_versement_server.startServer( calcul_versement_socket )
println "demarrage du serveice calcul versement sur le port 8080"


calcul_versement_server.calculer_versement = {json_intrant -> json_intrant
    def jsonSlurper = new JsonSlurper()
    def jsonMap = jsonSlurper.parseText new File(json_intrant).text

    def montant = jsonMap.montantInitial as Double
    def taux = jsonMap.tauxPeriodique as Double
    def nombrePeriodes = jsonMap.nombrePeriodes as int
    def versement = (montant * taux / (1 - (1 + taux) ** (- nombrePeriodes))).round(2)

    def json_extrant = [
            "scenario": jsonMap.scenario,
            "datePret": jsonMap.datePret,
            "montantInitial": jsonMap.montantInitial,
            "nombrePeriodes": jsonMap.nombrePeriodes,
            "tauxPeriodique": jsonMap.tauxPeriodique,
            "versement": versement as String
    ]
    JsonOutput.toJson(json_extrant)

}
