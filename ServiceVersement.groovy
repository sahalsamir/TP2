import groovy.json.JsonSlurper
import groovy.json.JsonOutput

@Grab('org.codehaus.groovy:groovy-xmlrpc:0.8')
import groovy.net.xmlrpc.*

def versementServer = new XMLRPCServer()
def versementSocket = new ServerSocket( 8080 )
versementServer.startServer( versementSocket )


versementServer.calculerVersement = {json -> json
    def jsonSlurper = new JsonSlurper()
    def jsonMap = jsonSlurper.parseText new File(json).text

    def montant = jsonMap.montantInitial as Double
    def taux = jsonMap.tauxPeriodique as Double
    def nombrePeriodes = jsonMap.nombrePeriodes as int
    def versement = (montant * taux / (1 - (1 + taux) ** (- nombrePeriodes))).round(2)

    def jsonExtrant = [
            "scenario": jsonMap.scenario,
            "datePret": jsonMap.datePret,
            "montantInitial": jsonMap.montantInitial,
            "nombrePeriodes": jsonMap.nombrePeriodes,
            "tauxPeriodique": jsonMap.tauxPeriodique,
            "versement": versement as String
    ]
    JsonOutput.toJson(jsonExtrant)

}
